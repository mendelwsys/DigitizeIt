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
import java.util.logging.Level;

import shp.exception.ShpDbfException;

/**
 * Represents a point shapefile.
 * Refer to the <a href = "http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile technical description</a> for more details ().
 * 
 * @author salaj
 * @version 1.0
 * */
public class PointShpFile
{
    
    /* ------------------------------------------------------------------------ */
    /* CLASS MEMBERS                                                            */
    /* ------------------------------------------------------------------------ */
    
    /**
     * The shapefile base.
     * */
    private ShpFileBase shpBase;
    
    /**
     * The record number.
     * */
    private int recordNumber;
    
    /**
     * The length of the file in bytes.
     * */
    private long fileLength;
    
    /**
     * The length of a point record in bytes.
     * */
    private static int POINT_RECORD_LENGTH = 20;
    
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
     * Creates and initializes the point shapefile with the given name.
     * If the shapefile with the given name exists, it will be tried to delete.
     * 
     * @param shpfileName          The name of the shapefile to create (without any extension).
     * @param numberColumnNames    The name of the number columns.
     * @param strColumnNames       The name of the string column names.
     * 
     * */
    public PointShpFile(String shpfileName, String[] numberColumnNames,String[] doubleColumnNames, String[] strColumnNames)
    {
        shpBase = new ShpFileBase(shpfileName, ShpFileHeader.SHPTYPE_POINT, numberColumnNames,doubleColumnNames, strColumnNames);
        
        recordNumber = 1;
        fileLength   = ShpFileHeader.INITIAL_FILE_LENGTH;
        
        /* Init default values. */
        defaultNumberValues = new HashMap<String, Integer>();
        defaultDoubleValues = new HashMap<String, Double>();
        defaultStringValues = new HashMap<String, String>();
        
        if(numberColumnNames != null)
        {
            for(String colName: numberColumnNames)
            {
                defaultNumberValues.put(colName, 0);
            }
        }
        
        if(strColumnNames != null)
        {
            for(String colName: strColumnNames)
            {
                defaultStringValues.put(colName, "");
            }
        }
    }
    
    /**
     * Adds a point to the shapefile.
     * 
     * @param point            The point to add.
     * @param numberValues     The number values.
     * @param stringValues     The string values.
     * 
     * @throws IOException 
     * @throws ShpDbfException 
     * */
    public void addPoint(ShpPoint point, Map<String, Integer> numberValues, Map<String, Double> doubleValues,Map<String, String> stringValues) throws IOException, ShpDbfException
    {   
        byte[] indexRecordByteArray = generateIndexFileRecordAsByteArray((int)fileLength / 2);
        byte[] mainRecordByteArray  = generateMainFileRecordAsByteArray(point);
        
        shpBase.getIndexOutputStream().write(indexRecordByteArray);
        shpBase.getMainOutputStream().write(mainRecordByteArray);
        shpBase.getDbfFile().addValues(numberValues,doubleValues,stringValues);
        
        shpBase.getMainBoundingBox2D().update(point.x, point.y);
        
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
     * This version is used to add a point to the shapefile, which doesn't have any additional (dbf) data
     * attached to it.
     * 
     * @param point    The point to add.
     * 
     * @throws ShpDbfException 
     * @throws IOException 
     * */
    public void addPoint(ShpPoint point) throws IOException, ShpDbfException
    {
        addPoint(point, defaultNumberValues,defaultDoubleValues,defaultStringValues);
    }
    
    /**
     * Closes the shapefile. After that, no writing should be commenced.
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
     * Generates a point record (with the record header) as a byte array for the main shapefile.
     * 
     * @return The point record as a byte array.
     * */
    private byte[] generateMainFileRecordAsByteArray(ShpPoint point)
    {
        ByteBuffer retVal = ByteBuffer.allocate(ShpFileBase.RECORD_HEADER_LENGTH + POINT_RECORD_LENGTH);
        
        /* -- Adding record header ------- */
        retVal.putInt(recordNumber);
        retVal.putInt(POINT_RECORD_LENGTH / 2);
        
        /* -- Adding content ------------- */
        retVal.putInt(ShpUtil.convertToLittleEndian(ShpFileHeader.SHPTYPE_POINT));
        retVal.putLong(ShpUtil.convertToLittleEndian(point.x));
        retVal.putLong(ShpUtil.convertToLittleEndian(point.y));
        
        return retVal.array();
    }
    
    /**
     * Generates a record for the index file as a byte array.
     * 
     * @return An index record as a byte array.
     * */
    private byte[] generateIndexFileRecordAsByteArray(int offset)
    {
        ByteBuffer retVal = ByteBuffer.allocate(ShpFileBase.INDEX_RECORD_LENGTH);
        
        retVal.putInt(offset);
        retVal.putInt(POINT_RECORD_LENGTH / 2);
        
        return retVal.array();
    }
}
