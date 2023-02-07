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

import java.nio.ByteBuffer;

/**
 * Represents a header used in the main, and in the index shapefile.
 * 
 * @author salaj
 * @version 1.0
 * */
public class ShpFileHeader
{
    
    /* ------------------------------------------------------------------------ */
    /* CLASS MEMBERS                                                            */
    /* ------------------------------------------------------------------------ */
    
    public static final int SHPTYPE_POINT    = 1;
    public static final int SHPTYPE_POLYLINE = 3;
    public static final int SHPTYPE_POLYGON  = 5;
    
    public static final int INITIAL_FILE_LENGTH = 100;
    
    /**
     * The bounding box.
     * */
    private BoundingBox2D bbox2d;
    
    /**
     * The file length measured in 16 bits words.
     * */
    private int fileLength;
    
    /**
     * The shapetype.
     * */
    private int shapeType;
    
    /* ------------------------------------------------------------------------ */
    /* PUBLIC FUNCTIONS                                                         */
    /* ------------------------------------------------------------------------ */
    
    /**
     * Initializes the shapefile header using the given shapetype.
     * 
     * @param shpType    The shape type. Must be a valid value. Use the static constants.
     * */
    public ShpFileHeader(int shpType)
    {
        fileLength = INITIAL_FILE_LENGTH / 2;
        bbox2d     = new BoundingBox2D();
        shapeType  = shpType;
    }
    
    /**
     * Sets the file length (measured in 16 bits words).
     * 
     * @param length    The file length.
     * */
    public void setFileLength(int length)
    {
        fileLength = length;
    }
    
    /**
     * Gets the file length.
     * 
     * @return The file length.
     * */
    public int getFileLength()
    {
        return fileLength;
    }
    
    /**
     * Gets the bounding box.
     * 
     * @return The bounding box.
     * */
    public BoundingBox2D getBbox2D()
    {
        return bbox2d;
    }
    
    /**
     * Converts this object to a shapefile file header bytestream
     * (for more see the ESRI Shapefile Technical Description).
     * 
     * @return The byte array representation of this object.
     * */
    public byte[] asByteArray()
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(INITIAL_FILE_LENGTH);
        
        /* File code. */
        byteBuffer.putInt(9994);
        
        /* 5 unused ints */
        for(int i = 0; i < 5; i++)
        {
            byteBuffer.putInt(0);
        }
        
        byteBuffer.putInt(fileLength);
        
        /* Version */
        byteBuffer.putInt(ShpUtil.convertToLittleEndian(1000));
        
        /* Shape type */
        byteBuffer.putInt(ShpUtil.convertToLittleEndian(shapeType));
        
        /* Bounding box */
        byteBuffer.putLong(ShpUtil.convertToLittleEndian(bbox2d.getXMin()));
        byteBuffer.putLong(ShpUtil.convertToLittleEndian(bbox2d.getYMin()));
        byteBuffer.putLong(ShpUtil.convertToLittleEndian(bbox2d.getXMax()));
        byteBuffer.putLong(ShpUtil.convertToLittleEndian(bbox2d.getYMax()));
        
        /* Unused M and Z values. */
        byteBuffer.putLong(ShpUtil.convertToLittleEndian(0.0));
        byteBuffer.putLong(ShpUtil.convertToLittleEndian(0.0));
        byteBuffer.putLong(ShpUtil.convertToLittleEndian(0.0));
        byteBuffer.putLong(ShpUtil.convertToLittleEndian(0.0));
        
        return byteBuffer.array().clone();
    }
    
    
    /* ------------------------------------------------------------------------ */
    /* PROTECTED FUNCTIONS                                                      */
    /* ------------------------------------------------------------------------ */
    
    
    /* ------------------------------------------------------------------------ */
    /* PRIVATE FUNCTIONS                                                        */
    /* ------------------------------------------------------------------------ */
}
