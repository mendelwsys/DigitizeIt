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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import shp.exception.ShpDbfException;

import nl.knaw.dans.common.dbflib.CorruptedTableException;
import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.IfNonExistent;
import nl.knaw.dans.common.dbflib.InvalidFieldLengthException;
import nl.knaw.dans.common.dbflib.InvalidFieldTypeException;
import nl.knaw.dans.common.dbflib.NumberValue;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Table;
import nl.knaw.dans.common.dbflib.Type;
import nl.knaw.dans.common.dbflib.Value;
import nl.knaw.dans.common.dbflib.Version;
import nl.knaw.dans.common.dbflib.StringValue;

public class ShpDbfFile
{
    //public static final String CHARSET_NAME = "UTF-8";
    public static final String CHARSET_NAME = "WINDOWS-1251";

    /* ------------------------------------------------------------------------ */
    /* CLASS MEMBERS                                                            */
    /* ------------------------------------------------------------------------ */
    
    /**
     * The dbf table.
     * */
    private Table dbfTable;
    
    /**
     * Stores the record map. This store values for records.
     * */
    private Map<String, Value> recordMap;
    
    /**
     * Stores the number column names.
     * */
    private String[] numberColNames;
    
    /**
     * Stores the string column names.
     * */
    private String[] strColNames;
    
    
    /* ------------------------------------------------------------------------ */
    /* PUBLIC FUNCTIONS                                                         */
    /* ------------------------------------------------------------------------ */
    
    /**
     * Creates the dbase file with the given file name. If the file exists,
     * it will be overwritten.
     * 
     * @param fileName             The name of the dbase file.
     * @param numberColumnNames    The column names for number type columns. Can be null. If null, then 
     *                             strColumnNames should not be null.
     * @param strColumnNames       The column names for string type columns. Can be null. If null, then 
     *                             numberColumnNames should not be null.
     * */
    public ShpDbfFile(String fileName, String[] numberColumnNames, String[] doubleColumnNames,String[] strColumnNames)
    {
        File dbfFile       = new File(fileName + ".dbf");
        File dbtFile       = new File(fileName + ".dbt");
        List<Field> fields = new Vector<Field>();
        
        try
        {
            if(dbfFile.exists())
            {
                try
                {
                    dbfFile.delete();
                }
                catch(SecurityException e)
                {
                    System.err.println("Could not delete the dbf file!");
                    throw e;
                }
            }
            
            if(dbtFile.exists())
            {
                try
                {
                    dbtFile.delete();
                }
                catch(SecurityException e)
                {
                    System.err.println("Could not delete the dbt (memo) file!");
                    throw e;
                }
            }
            
            if(numberColumnNames != null)
                for (String numberColumnName : numberColumnNames)
                    fields.add(new Field(numberColumnName, Type.NUMBER, 18, 0));

            if (doubleColumnNames!=null)
                for (String doubleColumnName : doubleColumnNames)
                    fields.add(new Field(doubleColumnName, Type.NUMBER, 18, 4));

            if(strColumnNames != null)
                for (String strColumnName : strColumnNames)
                    if (strColumnName!=null)
                        fields.add(new Field(strColumnName, Type.CHARACTER, 40));

            dbfTable = new Table(dbfFile, Version.DBASE_3, fields, CHARSET_NAME);
            
            recordMap = new HashMap<String, Value>();
            
            numberColNames = numberColumnNames;
            strColNames    = strColumnNames;
        }
        catch(InvalidFieldTypeException e)
        {
            // Should never happen.
            e.printStackTrace();
        }
        catch(InvalidFieldLengthException e)
        {
            // Should never happen.
            e.printStackTrace();
        }
    }
    
    /**
     * Adds values to columns to the shapefile.
     * 
     * @param numberValues    The number values (a map "pairing" column names with their values). Can be null.
     * @param stringValues    The string values (a map "pairing" column names with their values). Can be null.
     * @throws ShpDbfException 
     * */
    public void addValues(Map<String, Integer> numberValues,
                          Map<String, Double> doubleValues,
                          Map<String, String> stringValues) throws ShpDbfException
    {
        /* Clear old values */
        recordMap.clear();
        
        if(numberValues != null)
        {
            for(String colName: numberValues.keySet())
            {
                Integer value = numberValues.get(colName);
                
                recordMap.put(colName, new NumberValue(value));
            }
        }
        
        if(doubleValues != null)
        {
            for(String colName: doubleValues.keySet())
            {
                Double value = doubleValues.get(colName);
                recordMap.put(colName, new NumberValue(value));
            }
        }


        if(stringValues != null)
        {
            for(String colName: stringValues.keySet())
            {
                String value = stringValues.get(colName);
                
                recordMap.put(colName, new StringValue(value, CHARSET_NAME));
            }
        }
        
        try
        {
            dbfTable.addRecord(new Record(recordMap));
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new ShpDbfException("Could not add record due to IOException.");
        }
        catch(DbfLibException e)
        {
            e.printStackTrace();
            throw new ShpDbfException("Could not add record due to DbfLibException.");
        }
    }
    
    /**
     * Opens the DBF file
     * 
     * @throws ShpDbfException
     * @throws IOException 
     * */
    public void open() throws ShpDbfException, IOException
    {
        try
        {
            dbfTable.open(IfNonExistent.CREATE);
        }
        catch (CorruptedTableException e)
        {
            /* Rethrow it as a ShpDbfException. */
            throw new ShpDbfException("CorruptedTableException.");
        }
    }
    
    /**
     * Closes the DBF file.
     * 
     * @throws IOException 
     * */
    public void close() throws IOException
    {
        dbfTable.close();
    }
    
    /**
     * Gets the number column names array.
     * 
     * @return The number columnNames.  Can return null.
     * */
    public String[] getNumberColumnNames()
    {
        return numberColNames;
    }
    
    /**
     * Gets the string column names.
     * 
     * @return The string column names. Can return null.
     * */
    public String[] getStrColumnNames()
    {
        return strColNames;
    }
    
    
    /* ------------------------------------------------------------------------ */
    /* PROTECTED FUNCTIONS                                                      */
    /* ------------------------------------------------------------------------ */
    
    
    /* ------------------------------------------------------------------------ */
    /* PRIVATE FUNCTIONS                                                        */
    /* ------------------------------------------------------------------------ */
}
