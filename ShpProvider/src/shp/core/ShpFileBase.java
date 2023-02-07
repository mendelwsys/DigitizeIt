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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;

import shp.exception.ShpDbfException;

/**
 * Represents a base for shapefiles.
 * 
 * @author salaj
 * @version 1.0
 * */
public class ShpFileBase
{
    
    /* ------------------------------------------------------------------------ */
    /* CLASS MEMBERS                                                            */
    /* ------------------------------------------------------------------------ */
    
    /**
     * Stores the output stream for the main shapefile.
     * */
    private BufferedOutputStream mainOstream;
    
    /**
     * Stores the random access file for the main shapefile.
     * */
    private RandomAccessFile mainRacFile;
    
    /**
     * Stores the header for the main shapefile.
     * */
    private ShpFileHeader mainHeader;
    
    /**
     * Stores the output stream for the index shapefile.
     * */
    private BufferedOutputStream indexOstream;
    
    /**
     * Stores the random access file for the index shapefile.
     * */
    private RandomAccessFile indexRacFile;
    
    /**
     * Stores the header for the index shapefile.
     * */
    private ShpFileHeader indexHeader;
    
    /**
     * The dbase file.
     * */
    private ShpDbfFile dbfFile;
    
    /**
     * The file object for the shp file.
     * */
    private File shpFile;
    
    /**
     * The file oject for the shp file.
     * */
    private File shxFile;
    
    /**
     * The file name.
     * */
    private String fileName;
    
    /**
     * The shape type.
     * */
    private int shapeType;
    
    /**
     * Denotes if the file is created successfully, or not.
     * */
    private boolean isCreated;
    
    /**
     * Stores the names of the number columns.
     * */
    private String[] numberColumnNames;
    
    /**
     * Stores the names of the number columns.
     * */
    private String[] doubleColumNames;
    /**
     * Stores the names of the string columns.
     * */
    private String[] strColumnNames;
    
    /**
     * The length of a record header measured in bytes.
     * */
    public static final int RECORD_HEADER_LENGTH = 8;
    
    /**
     * The length of an index file record.
     * */
    public static final int INDEX_RECORD_LENGTH = 8;



    /* ------------------------------------------------------------------------ */
    /* PUBLIC FUNCTIONS                                                         */
    /* ------------------------------------------------------------------------ */
    
    /**
     * Intializes the shapefile base.
     * 
     * @param fileName            The output file name (without the extension).
     * @param shapeType           The type of the shapefile.
     * @param numberColumNames    The number column names for the dbase file.
     * @param strColumnNames      The string column names for the dbase file.
     * */
    public ShpFileBase(String fileName, int shapeType, String[] numberColumNames,String[] doubleColumNames, String[] strColumnNames)
    {
        this.fileName          = fileName;
        this.shapeType         = shapeType;
        this.numberColumnNames = numberColumNames;
        this.doubleColumNames=doubleColumNames;
        this.strColumnNames    = strColumnNames;
        
        isCreated = false;
    }
    
    /**
     * Creates the shapefile. If it was existing, it will be overwritten.
     * @throws IOException 
     * @throws ShpDbfException 
     * */
    public void create() throws IOException, ShpDbfException
    {
        shpFile = new File(fileName + ".shp");
        shxFile = new File(fileName + ".shx");
        
        /* Delete the files, if they're existing. */
        if(shpFile.exists())
        {
            try
            {
                shpFile.delete();
            }
            catch(SecurityException e)
            {
                System.err.println("Could not delete the shp file! file name = \"" + fileName + "\"");
                throw e;
            }
        }
        
        if(shxFile.exists())
        {
            try
            {
                shxFile.delete();
            }
            catch(SecurityException e)
            {
                System.err.println("Could not delete the shx file! file name = \"" + fileName + "\"");
                throw e;
            }
        }
        
        shpFile.getParentFile().mkdirs();
        shxFile.getParentFile().mkdirs();
        
        shpFile.createNewFile();
        shxFile.createNewFile();
        
        mainOstream  = new BufferedOutputStream(new FileOutputStream(shpFile));
        indexOstream = new BufferedOutputStream(new FileOutputStream(shxFile));
        dbfFile      = new ShpDbfFile(fileName, numberColumnNames,doubleColumNames, strColumnNames);
        
        mainHeader  = new ShpFileHeader(shapeType);
        indexHeader = new ShpFileHeader(shapeType);
        
        mainOstream.write(mainHeader.asByteArray());
        indexOstream.write(indexHeader.asByteArray());
        dbfFile.open();
        
        isCreated = true;
    }

    /**
     * Gets the main file's output stream object.
     * 
     * @return The main file's output stream.
     * */
    public BufferedOutputStream getMainOutputStream()
    {
        return mainOstream;
    }
    
    /**
     * Gets the index file's output stream object.
     * 
     * @return The main file's output stream.
     * */
    public BufferedOutputStream getIndexOutputStream()
    {
        return indexOstream;
    }
    
    /**
     * Gets the dbase file.
     * 
     * @return The dbase file.
     * */
    public ShpDbfFile getDbfFile()
    {
        return dbfFile;
    }
    
    /**
     * Gets the bounding box of the main shapefile.
     * 
     * @return The bounding box of the main shapefile.
     * */
    public BoundingBox2D getMainBoundingBox2D()
    {
        return mainHeader.getBbox2D();
    }
    
    /**
     * Closes the shapefile. After that, no further writing should be commenced.
     * 
     * @throws IOException 
     * */
    public void close() throws IOException
    {
        String logMessage = "";
        
        mainOstream.close();
        indexOstream.close();
        
        /* -- Update index file's bounding box based on main's ------- */
        indexHeader.getBbox2D().update(mainHeader.getBbox2D().getXMax(), mainHeader.getBbox2D().getYMax());
        indexHeader.getBbox2D().update(mainHeader.getBbox2D().getXMin(), mainHeader.getBbox2D().getYMin());
        
        /* -- Updating shapefile header structure. ------------------- */
        mainRacFile  = new RandomAccessFile(shpFile, "rws");
        indexRacFile = new RandomAccessFile(shxFile, "rws");
        
        if(mainRacFile.length() % 2 != 0)
        {
            logMessage += "Main SHP file's length is not appropriate! length = " + mainRacFile.length() + "\n";
        }
        
        if(indexRacFile.length() % 2 != 0)
        {
            logMessage += "Index SHP file's length is not appropriate! length = " + indexRacFile.length() + "\n";
        }
        
        mainHeader.setFileLength((int)mainRacFile.length() / 2);
        indexHeader.setFileLength((int)indexRacFile.length() / 2);
        
        mainRacFile.seek(0);
        indexRacFile.seek(0);
        
        mainRacFile.write(mainHeader.asByteArray());
        indexRacFile.write(indexHeader.asByteArray());
        
        mainRacFile.close();
        indexRacFile.close();

        dbfFile.close();

        /* ---------------- logging ---------------- */
        if(ShpCoreLogger.getLogger() != null && !logMessage.equalsIgnoreCase(""))
        {
            ShpCoreLogger.getLogger().log(Level.SEVERE, logMessage);

        }
        /* -------------- logging end -------------- */
    }
    
    /**
     * Checks if the file base is created successfully, or not.
     * 
     * @return true, if the file base is created successfully.
     * */
    public boolean isCreated()
    {
        return isCreated;
    }
    
    
    /* ------------------------------------------------------------------------ */
    /* PROTECTED FUNCTIONS                                                      */
    /* ------------------------------------------------------------------------ */
    
    
    /* ------------------------------------------------------------------------ */
    /* PRIVATE FUNCTIONS                                                        */
    /* ------------------------------------------------------------------------ */
}
