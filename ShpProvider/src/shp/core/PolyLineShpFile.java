/* 
 *  Copyright 2012 Oliver Dozsa
 *      
 *  This program is free software: you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package shp.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import shp.exception.ShpDbfException;

/**
 * Represent a polyline shapefile.
 * Refer to the <a href = "http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile technical description</a> for more details ().
 * 
 * @author salaj
 * @version 1.0
 * */
public class PolyLineShpFile
{
    
    /* ------------------------------------------------------------------------ */
    /* CLASS MEMBERS                                                            */
    /* ------------------------------------------------------------------------ */
    
    /**
     * The shp file base.
     * */
    private ShpFileBase shpBase;
    
    /**
     * The record number.
     * */
    private int recordNumber;
    
    /**
     * The file length measured in bytes.
     * */
    private long fileLength;
    
    /**
     * The initial content length. This include the following sizes:
     *   - The shapetype (int, 4 bytes).
     *   - The local bounding box (4 doubles, 4 * 8 bytes).
     *   - Number of parts (int, 4 bytes).
     *   - Number of points (int, 4 bytes).
     * */
    private static final int MAIN_RECORD_INITIAL_CONTENT_LENGTH = 44;
    
    /**
     * The beginning byte position of the bounding box measured from the beginning of the record
     * (including the record header)
     * */
    private static final int BOUNDING_BOX_POSITION = 12;
    
    /**
     * The default number values.
     * */
    private final Map<String, Integer> defaultNumberValues;
    
    /**
     * The default number values.
     * */
    private final Map<String, Double> defaultDoubleValues;
    /**
     * The default string values.
     * */
    private final Map<String, String> defaultStringValues;
    
    
    /* ------------------------------------------------------------------------ */
    /* PUBLIC FUNCTIONS                                                         */
    /* ------------------------------------------------------------------------ */
    
    /**
     * Initializes the polyline shapefile.
     * 
     * @param fileName    The name of the shapefile to create (without extension).
     * @param numberColumnNames    The name of the number columns.
     * @param strColumnNames       The name of the string column names.
     * */
    public PolyLineShpFile(String fileName, String[] numberColumnNames,String[] doubleColumnNames, String[] strColumnNames)
    {
        shpBase = new ShpFileBase(fileName, ShpFileHeader.SHPTYPE_POLYLINE, numberColumnNames,doubleColumnNames, strColumnNames);
        
        recordNumber = 1;
        fileLength   = ShpFileHeader.INITIAL_FILE_LENGTH;
        
        /* Init default values. */
        defaultNumberValues = new HashMap<String, Integer>();

        defaultDoubleValues = new HashMap<String, Double>();

        defaultStringValues = new HashMap<String, String>();
        
        for(String colName: numberColumnNames)
        {
            defaultNumberValues.put(colName, 0);
        }
        
        for(String colName: strColumnNames)
        {
            defaultStringValues.put(colName, "");
        }
    }
    
    /**
     * Adds a polyline to the point shapefile.
     * 
     * @param polyline    The polyline to add. A polyline consists of parts. A part consists of points.
     * @param numberValues     The number values.
     * @param stringValues     The string values.
     * 
     * @throws IOException 
     * @throws ShpDbfException 
     * */
    public void addPolyLine(Vector<Vector<ShpPoint>> polyline, Map<String, Integer> numberValues,
                            Map<String, Double> doubleValues,
                            Map<String, String> stringValues
    ) throws IOException, ShpDbfException
    {
        byte[] mainRecordByteArray  = generateMainFileRecordAsByteArray(polyline);
        byte[] indexRecordByteArray = generateIndexFileRecordAsByteArray(polyline, (int)fileLength / 2);
        
        shpBase.getMainOutputStream().write(mainRecordByteArray);
        shpBase.getIndexOutputStream().write(indexRecordByteArray);
        shpBase.getDbfFile().addValues(numberValues,doubleValues, stringValues);
        
        for(Vector<ShpPoint> part: polyline)
        {
            for(ShpPoint point: part)
            {
                shpBase.getMainBoundingBox2D().update(point.x, point.y);
            }
        }
        
        recordNumber++;
        fileLength += mainRecordByteArray.length;
        
        /* ---------------- logging ---------------- */
        if(ShpCoreLogger.isLoggingEnabled() && ShpCoreLogger.getLogger() != null)
        {
            String logMessage = "record number = " + recordNumber               + "\n" +
                                "fileLength    = " + fileLength                 + "\n" +
                                "index record hex dump\n"                       +
                                ShpCoreLogger.toHexString(indexRecordByteArray) + "\n" +
                                "main record hex dump (array length = " + mainRecordByteArray.length + ")\n" + 
                                ShpCoreLogger.toHexString(mainRecordByteArray);
            
            ShpCoreLogger.getLogger().log(Level.INFO, logMessage);
        }
        /* -------------- logging end -------------- */
    }
    
    /**
     * Adds a polyline to the shapefile with no info attached.
     * 
     * @param polyline    The polyline to add.
     * 
     * @throws ShpDbfException 
     * @throws IOException 
     * */
    public void addPolyLine(Vector<Vector<ShpPoint>> polyline) throws IOException, ShpDbfException
    {
        addPolyLine(polyline, defaultNumberValues,defaultDoubleValues,defaultStringValues);
    }
    
    /**
     * Closes the polyline shapefile. After that, no further additions should be commenced.
     * 
     * @throws IOException 
     * */
    public void close() throws IOException
    {
        shpBase.close();
    }
    
    /**
     * Creates the point shapefile.
     * @throws ShpDbfException 
     * @throws IOException 
     * */
    public void create() throws IOException, ShpDbfException
    {
        shpBase.create();
    }
    
    /**
     * Checks if the file is created successfully, or not.
     * 
     * @return true, if the file base is created successfully.
     * */
    public boolean isCreated()
    {
        return shpBase.isCreated();
    }
    
    /* ------------------------------------------------------------------------ */
    /* PROTECTED FUNCTIONS                                                      */
    /* ------------------------------------------------------------------------ */
    
    
    /* ------------------------------------------------------------------------ */
    /* PRIVATE FUNCTIONS                                                        */
    /* ------------------------------------------------------------------------ */
    
    /**
     * Generates a main file record as a byte array.
     * 
     * @param polyline    The polyline from which the main file record should be generated.
     * 
     * @return The generated byte array.
     * */
    private byte[] generateMainFileRecordAsByteArray(Vector<Vector<ShpPoint>> polyline)
    {   
        /* 
         * The initial buffer length without the points is 44, plus the header length. 
         * See the ESRI shapefile description for more.
         */
        int bufferLength                 = MAIN_RECORD_INITIAL_CONTENT_LENGTH +
                                           ShpFileBase.RECORD_HEADER_LENGTH   +
                                           calculatePolyLineSize(polyline);
        int numOfParts                   = polyline.size();
        int numOfPoints                  = 0;
        BoundingBox2D localBoundingBox2D = new BoundingBox2D();
        
        /* Calculate number of points. */
        for(Vector<ShpPoint> part: polyline)
        {   
            numOfPoints += part.size();
        }
        
        ByteBuffer retVal = ByteBuffer.allocate(bufferLength);
        
        /* -- Adding record header ------- */
        retVal.putInt(recordNumber);
        retVal.putInt((bufferLength - ShpFileBase.RECORD_HEADER_LENGTH) / 2);
        
        /* -- Adding content ------------- */
        /* Writing shape type */
        retVal.putInt(ShpUtil.convertToLittleEndian(ShpFileHeader.SHPTYPE_POLYLINE));
        
        /* Write initial local bounding box. */
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMin()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMin()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMax()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMax()));
        
        /* Number of parts. */
        retVal.putInt(ShpUtil.convertToLittleEndian(numOfParts));
        
        /* Number of points. */
        retVal.putInt(ShpUtil.convertToLittleEndian(numOfPoints));
        
        
        /* Parts array */
        int partIndex = 0;
        
        for(Vector<ShpPoint> part: polyline)
        {
            retVal.putInt(ShpUtil.convertToLittleEndian(partIndex));
            partIndex += part.size();
        }
        
        /* Points */
        for(Vector<ShpPoint> part: polyline)
        {
            for(ShpPoint point: part)
            {
                localBoundingBox2D.update(point.x, point.y);
                
                retVal.putLong(ShpUtil.convertToLittleEndian(point.x));
                retVal.putLong(ShpUtil.convertToLittleEndian(point.y));
            }
        }
        
        /* Re-update the local bounding box. */
        retVal.position(BOUNDING_BOX_POSITION);
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMin()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMin()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMax()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMax()));
        
        return retVal.array();
    }
    
    /**
     * Generates an index file record as a byte array.
     * 
     * @param polyline    The polyline on which the index record content should be based.
     * 
     * @return The generated byte array.
     * */
    private byte[] generateIndexFileRecordAsByteArray(Vector<Vector<ShpPoint>> polyline, int offset)
    {   
        ByteBuffer retVal = ByteBuffer.allocate(ShpFileBase.INDEX_RECORD_LENGTH);
        
        retVal.putInt(offset);
        retVal.putInt((MAIN_RECORD_INITIAL_CONTENT_LENGTH + calculatePolyLineSize(polyline)) / 2);
        
        return retVal.array();
    }
    
    /**
     * Calculates the size of the polyline (including the parts array) in bytes.
     * 
     * @return The size of the polyline in bytes.
     * */
    private int calculatePolyLineSize(Vector<Vector<ShpPoint>> polyline)
    {
        int retVal = 0;
        
        for(Vector<ShpPoint> part: polyline)
        {
            /*
             * The parts array stores indexes to points in the points array.
             * It stores integers, which are 4 bytes long.
             */
            retVal += 4;
            
            /* 
             * Each point need 16 bytes (two doubles), therefore each polyline part 
             * contributes numOfPoints*16 to the buffer length.
             */
            retVal += part.size() * 16;
        }
        
        return retVal;
    }
}
