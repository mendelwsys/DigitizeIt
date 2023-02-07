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

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import shp.exception.ShpDbfException;

/**
 * Represents a polygon shapefile.
 * Refer to the <a href = "http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile technical description</a> for more details ().
 * 
 * @author salaj
 * @version 1.0
 * */
public class PolygonShpFile
{
    
    /* ------------------------------------------------------------------------ */
    /* CLASS MEMBERS                                                            */
    /* ------------------------------------------------------------------------ */

    public ShpFileBase getShpBase() {
        return shpBase;
    }

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
    public static final int MAIN_RECORD_INITIAL_CONTENT_LENGTH = 44;
    
    /**
     * The beginning byte position of the bounding box measured from the beginning of the record
     * (including the record header)
     * */
    public static final int BOUNDING_BOX_POSITION = 12;
    
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

  /**
   * Added polygon counter
   */
  private int m_polyCnt = 0;

  /* ------------------------------------------------------------------------ */
    /* PUBLIC FUNCTIONS                                                         */
    /* ------------------------------------------------------------------------ */
    
    /**
     * Creates, and initializes the polygon shapefile with given name.
     * If the shapefile with the given name exists, it will be tried to delete.
     * 
     * @param fileName             The name of the file to create (without extension).
     * @param numberColumnNames    The name of the number columns.
     * @param strColumnNames       The name of the string column names. 
     * */
    public PolygonShpFile(String fileName, String[] numberColumnNames,String[] doubleColumnNames, String[] strColumnNames)
    {
        shpBase = new ShpFileBase(fileName, ShpFileHeader.SHPTYPE_POLYGON, numberColumnNames,doubleColumnNames, strColumnNames);
        
        recordNumber = 1;
        fileLength   = ShpFileHeader.INITIAL_FILE_LENGTH;
        
        /* Init default values. */
        defaultNumberValues = new HashMap<String, Integer>();
        defaultDoubleValues = new HashMap<String, Double>();
        defaultStringValues = new HashMap<String, String>();


      if ( numberColumnNames != null )
        for(String colName: numberColumnNames)
        {
            defaultNumberValues.put(colName, 0);
        }
        
      if ( strColumnNames != null )
        for(String colName: strColumnNames)
        {
            defaultStringValues.put(colName, "");
        }
    }
    
    /**
     * Adds a polygon to the shapefile.
     * 
     * @param polygon          The polygon to add. A polygon consists of rings. A ring consists of poinst.
     *                         It is expected, that the first and the last point of a ring are the same.
     * @param numberValues     The number values. Can be an empty map.
     * @param stringValues     The string values. Can be an empty map.
     * 
     * @throws IOException 
     * @throws ShpDbfException 
     * */
    public void addPolygon(Vector<Vector<ShpPoint>> polygon, Map<String, Integer> numberValues,Map<String, Double> doubleValues, Map<String, String> stringValues) throws IOException, ShpDbfException
    {
        byte[] mainFileRecord  = generateMainFileRecordAsByteArray(polygon);
        byte[] indexFileRecord = generateIndexFileRecordAsByteArray(polygon, (int)fileLength / 2);
        
        shpBase.getMainOutputStream().write(mainFileRecord);
        shpBase.getIndexOutputStream().write(indexFileRecord);
        shpBase.getDbfFile().addValues(numberValues,doubleValues,stringValues);
        
        for(Vector<ShpPoint> part: polygon)
        {
            for(ShpPoint point: part)
            {
                shpBase.getMainBoundingBox2D().update(point.x, point.y);
            }
        }
        
        recordNumber++;
        fileLength += mainFileRecord.length;
        
        /* ---------------- logging ---------------- */
        if(ShpCoreLogger.isLoggingEnabled() && ShpCoreLogger.getLogger() != null)
        {
            String logMessage = "record number = " + recordNumber               + "\n" +
                                "fileLength    = " + fileLength                 + "\n" +
                                "index record hex dump\n"                       +
                                ShpCoreLogger.toHexString(indexFileRecord)      + "\n" +
                                "main record hex dump (array length = " + mainFileRecord.length + ")\n"                        + 
                                ShpCoreLogger.toHexString(mainFileRecord);
            
            ShpCoreLogger.getLogger().log(Level.INFO, logMessage);
        }
      m_polyCnt++;
        /* -------------- logging end -------------- */
    }
    

    public void addPolygon2(Vector<Vector<ShpPoint>> polygon, Map<String, Integer> numberValues,
                            Map<String, Double> doubleValues,
                            Map<String, String> stringValues) throws IOException, ShpDbfException
    {
        DataOutputStream dos = new DataOutputStream(shpBase.getMainOutputStream());
        long ln=generateMainFileRecordAsByteArray2(polygon, dos);

        byte[] indexFileRecord = generateIndexFileRecordAsByteArray(polygon, (int)fileLength / 2);

        shpBase.getIndexOutputStream().write(indexFileRecord);
        shpBase.getDbfFile().addValues(numberValues,doubleValues,stringValues);

        for(Vector<ShpPoint> part: polygon)
            for(ShpPoint point: part)
                shpBase.getMainBoundingBox2D().update(point.x, point.y);

        recordNumber++;
        fileLength += ln;

        /* ---------------- logging ---------------- */
        if(ShpCoreLogger.isLoggingEnabled() && ShpCoreLogger.getLogger() != null)
        {
            String logMessage = "record number = " + recordNumber               + "\n" +
                                "fileLength    = " + fileLength                 + "\n" +
                                "index record hex dump\n"                       +
                                ShpCoreLogger.toHexString(indexFileRecord)      + "\n" +
                                "main record hex dump (array length = " + ln + ")\n";
//                    +
//                                ShpCoreLogger.toHexString(mainFileRecord);

            ShpCoreLogger.getLogger().log(Level.INFO, logMessage);
        }
      m_polyCnt++;
        /* -------------- logging end -------------- */
    }

    /**
     * Adds a polygon to the shapfile without any info attached.
     * 
     * @param polygon    The polygon to add.
     * 
     * @throws ShpDbfException 
     * @throws IOException 
     * */
    public void addPolygon(Vector<Vector<ShpPoint>> polygon) throws IOException, ShpDbfException
    {   
        addPolygon(polygon, defaultNumberValues,defaultDoubleValues,defaultStringValues);
    }
    
    /**
     * Closes the shapefile. After that, no further writings should be commenced.
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
     * Generates a byte array representation of a mainfile record based on the given polygon.
     * 
     * @param polygon    The polygon, that should be placed in the generated main file record.
     * 
     * @return A main file record as a byte array.
     * */
    private byte[] generateMainFileRecordAsByteArray(Vector<Vector<ShpPoint>> polygon)
    {
        int bufferLength                 = ShpFileBase.RECORD_HEADER_LENGTH   +
                                           MAIN_RECORD_INITIAL_CONTENT_LENGTH +
                                           calculatePolygonSize(polygon);
        int numOfPoints                  = 0;
        int numOfParts                   = polygon.size();
        BoundingBox2D localBoundingBox2D = new BoundingBox2D();
        
        /* Calculate number of points */
        for(Vector<ShpPoint> part: polygon)
        {
            numOfPoints += part.size();
        }

        ByteBuffer retVal;
        try {
            retVal = ByteBuffer.allocate(bufferLength);
        } catch (OutOfMemoryError e) {
            System.err.println("error allocate bufferLength while shape converter in mBytes= " + bufferLength / (1024 * 1024));
            throw e;
        }


        /*-- Adding record header ---------------- */
        retVal.putInt(recordNumber);
        retVal.putInt((bufferLength - ShpFileBase.RECORD_HEADER_LENGTH) / 2);
        
        /*-- Adding content ---------------------- */
        /* Write shape type */
        retVal.putInt(ShpUtil.convertToLittleEndian(ShpFileHeader.SHPTYPE_POLYGON));

        /* Write initial local bounding box. */
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMin()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMin()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMax()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMax()));
        
        /* Write number of parts. */
        retVal.putInt(ShpUtil.convertToLittleEndian(numOfParts));
        
        /* Write number of points. */
        retVal.putInt(ShpUtil.convertToLittleEndian(numOfPoints));
        
        /* Write parts array. */
        int partIndex = 0;
        for(Vector<ShpPoint> part: polygon)
        {
            retVal.putInt(ShpUtil.convertToLittleEndian(partIndex));
            partIndex += part.size();
        }
        
        /* Write points. */
        for(Vector<ShpPoint> part: polygon)
        {
            for(ShpPoint point: part)
            {
                localBoundingBox2D.update(point.x, point.y);
                
                retVal.putLong(ShpUtil.convertToLittleEndian(point.x));
                retVal.putLong(ShpUtil.convertToLittleEndian(point.y));
            }
        }
        
        /* Re-update local bounding box. */
        retVal.position(BOUNDING_BOX_POSITION);
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMin()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMin()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMax()));
        retVal.putLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMax()));
        
        return retVal.array();
    }
    


    /* ------------------------------------------------------------------------ */
    /* PRIVATE FUNCTIONS                                                        */
    /* ------------------------------------------------------------------------ */

    private long generateMainFileRecordAsByteArray2(Vector<Vector<ShpPoint>> polygon,DataOutputStream retVal) throws IOException
    {

        BoundingBox2D localBoundingBox2D = new BoundingBox2D();
        long bufferLength                 = ShpFileBase.RECORD_HEADER_LENGTH   +
                                           MAIN_RECORD_INITIAL_CONTENT_LENGTH +
                                           calculatePolygonSize2(polygon, localBoundingBox2D);
        int numOfPoints                  = 0;
        int numOfParts                   = polygon.size();


        /* Calculate number of points */
        for(Vector<ShpPoint> part: polygon)
        {
            numOfPoints += part.size();
        }

        /*-- Adding record header ---------------- */
        retVal.writeInt(recordNumber);
        retVal.writeInt(((int)bufferLength - ShpFileBase.RECORD_HEADER_LENGTH) / 2);

        /*-- Adding content ---------------------- */
        /* Write shape type */
        retVal.writeInt(ShpUtil.convertToLittleEndian(ShpFileHeader.SHPTYPE_POLYGON));

        /* Write initial local bounding box. */
        retVal.writeLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMin()));
        retVal.writeLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMin()));
        retVal.writeLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMax()));
        retVal.writeLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMax()));

        /* Write number of parts. */
        retVal.writeInt(ShpUtil.convertToLittleEndian(numOfParts));

        /* Write number of points. */
        retVal.writeInt(ShpUtil.convertToLittleEndian(numOfPoints));

        /* Write parts array. */
        int partIndex = 0;
        for(Vector<ShpPoint> part: polygon)
        {
            retVal.writeInt(ShpUtil.convertToLittleEndian(partIndex));
            partIndex += part.size();
        }

        /* Write points. */
        for(Vector<ShpPoint> part: polygon)
        {
            for(ShpPoint point: part)
            {
                localBoundingBox2D.update(point.x, point.y);

                retVal.writeLong(ShpUtil.convertToLittleEndian(point.x));
                retVal.writeLong(ShpUtil.convertToLittleEndian(point.y));
            }
        }

//        /* Re-update local bounding box. */
//        retVal.position(BOUNDING_BOX_POSITION);
//        retVal.putLong(ShpUtil.convertToLittleEndian(boundingBox2D.getXMin()));
//        retVal.putLong(ShpUtil.convertToLittleEndian(boundingBox2D.getYMin()));
//        retVal.putLong(ShpUtil.convertToLittleEndian(boundingBox2D.getXMax()));
//        retVal.putLong(ShpUtil.convertToLittleEndian(boundingBox2D.getYMax()));
//        return retVal.array();
        return bufferLength;
    }

    /**
     * Generates an index file record as a byte array based on the given polygon, and offset.
     * 
     * @param polygon    The polygon.
     * @param offset     The offset.
     * */
    private byte[] generateIndexFileRecordAsByteArray(Vector<Vector<ShpPoint>> polygon, int offset)
    {
        ByteBuffer retVal = ByteBuffer.allocate(ShpFileBase.INDEX_RECORD_LENGTH);
        
        retVal.putInt(offset);
        retVal.putInt(calculatePolygonSize(polygon));
        
        return retVal.array();
    }
    
    /**
     * Calculates the size of the polygon (including the parts array) in bytes.
     * 
     * @param polygon    The polygon whose size should be calculated.
     * 
     * @return The size of the polygon in bytes.
     * */
    private int calculatePolygonSize(Vector<Vector<ShpPoint>> polygon)
    {
        int retVal = 0;
        
        for(Vector<ShpPoint> part: polygon)
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

    /**
     * Calculates the size of the polygon (including the parts array) in bytes.
     *
     * @param polygon    The polygon whose size should be calculated.
     *
     * @return The size of the polygon in bytes.
     * */
    private long calculatePolygonSize2(Vector<Vector<ShpPoint>> polygon,BoundingBox2D localBoundingBox2D)
    {
        long retVal = 0;

        for(Vector<ShpPoint> part: polygon)
        {
            for(ShpPoint point: part)
                localBoundingBox2D.update(point.x, point.y);

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


  /**
   * Return added polygon counter
   * @return int with added polygon counter
   */
  public int getPolyCnt()
  {
    return m_polyCnt;
  }
}
