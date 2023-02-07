/**
 * Created 28.07.2008 16:48:33 by Syg for the "GisUtils" project
 */
package ru.ts.gisutils.common.CSV;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Extension for a fast CSV reader
 * @author Syg
 * 
 */
public class FastCSVFileReader extends FastCsvReader
{

	/**
	 * @param fname  file name to read
	 * @param comment character to detect comment at first position in the read line
	 * @param delimiter delimiter to separate different fields
	 * @throws FileNotFoundException if no file found
	 */
	public FastCSVFileReader( String fname, char comment, char delimiter )
	        throws FileNotFoundException
	{
		super( fname, comment, delimiter );
	}

	public FastCSVFileReader( String fileName, char comment, char delimiter,
	        Charset charset ) throws FileNotFoundException
	{
		super( fileName, comment, delimiter );
	}

	public FastCSVFileReader( String fileName, char delimiter )
	        throws FileNotFoundException
	{
		super( fileName, '\0', delimiter );
	}

	public FastCSVFileReader( String fileName ) throws FileNotFoundException
	{
		super( fileName, '\0', ',' );
	}

	/**
	 * gets <code>int</code> value from a CSV string
	 * 
	 * @param columnIndex
	 *            index of the column to get
	 * @return int value of the column
	 * @throws IOException
	 *             if string of the column can't be converted into an integer
	 * @throws NumberFormatException
	 *             if the string does not contain a parseable integer
	 */
	public int getInt( int columnIndex ) throws IOException,
	        NumberFormatException
	{
		return Integer.parseInt( super.get( columnIndex ) );
	}

	public Integer getInteger( int columnIndex ) throws IOException,
	        NumberFormatException
	{
		return Integer.valueOf( super.get( columnIndex ) );
	}

	/**
	 * gets <code>int</code> value from a CSV string
	 * 
	 * @param headerName
	 *            name of the column to get
	 * @return int value of the column
	 * @throws IOException
	 *             if string of the column can't be converted into an integer
	 * @throws NumberFormatException
	 *             if the string does not contain a parseable integer
	 */
	public int getInt( String headerName ) throws IOException,
	        NumberFormatException
	{
		return Integer.parseInt( super.get( headerName ) );
	}

	public int getInteger( String headerName ) throws IOException,
	        NumberFormatException
	{
		return Integer.parseInt( super.get( headerName ) );
	}

	/**
	 * gets double value from a CSV string
	 * 
	 * @param columnIndex
	 *            index of the column to get
	 * @return double value of the column
	 * @throws IOException
	 *             if string of the column can't be converted into an double
	 * @throws NumberFormatException
	 *             if the string does not contain a parseable double
	 */
	public double getDbl( int columnIndex ) throws IOException,
	        NumberFormatException
	{
		return Double.parseDouble( super.get( columnIndex ) );
	}

	public Double getDouble( int columnIndex ) throws IOException,
	        NumberFormatException
	{
		return Double.valueOf( super.get( columnIndex ) );
	}

	/**
	 * gets double value from a CSV string
	 * 
	 * @param headerName
	 *            name of the column to get
	 * @return double value of the column
	 * @throws IOException
	 *             if string of the column can't be converted into an double
	 * @throws NumberFormatException
	 *             if the string does not contain a parseable double
	 */
	public double getDbl( String headerName ) throws IOException,
	        NumberFormatException
	{
		return Double.parseDouble( super.get( headerName ) );
	}

	public Double getDouble( String headerName ) throws IOException,
	        NumberFormatException
	{
		return Double.valueOf( super.get( headerName ) );
	}

	public float getFlt( String fieldName ) throws IOException, NumberFormatException
	{
		return Float.parseFloat( super.get( fieldName) );
	}

	public float getFlt( int fldInd ) throws IOException, NumberFormatException
	{
		return Float.parseFloat( super.get( fldInd) );
	}

	public Float getFloat( String fieldName ) throws IOException, NumberFormatException
	{
		return Float.valueOf( super.get( fieldName) );
	}

	public Float getFloat( int fldInd ) throws IOException, NumberFormatException
	{
		return Float.valueOf( super.get( fldInd) );
	}
}
