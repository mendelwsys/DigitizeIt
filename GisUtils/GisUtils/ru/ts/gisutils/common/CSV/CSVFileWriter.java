/**
 * Created on 07.06.2008 15:18:28 2008 by Syg for project in
 * 'ru.ts.gisutils.common.CSV' of 'test'
 */
package ru.ts.gisutils.common.CSV;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import com.csvreader.CsvWriter;

/**
 * @author Syg
 */
public class CSVFileWriter extends CsvWriter
{

	/**
	 * @param fileName
	 * @param delimiter
	 * @param charset
	 */
	public CSVFileWriter( String fileName, char delimiter, Charset charset )
	{
		super( fileName, delimiter, (charset == null ? Charset.forName("UTF8"): charset ));
	}

	/**
	 * @param fileName
	 */
	public CSVFileWriter( String fileName )
	{
		super( fileName );
	}

	/**
	 * @param outputStream
	 * @param delimiter
	 */
	public CSVFileWriter( Writer outputStream, char delimiter )
	{
		super( outputStream, delimiter );
	}

	/**
	 * @param outputStream
	 * @param delimiter
	 * @param charset
	 */
	public CSVFileWriter( OutputStream outputStream, char delimiter,
	        Charset charset )
	{
		super( outputStream, delimiter, charset );
	}

	/**
	 * Writes another column of data to this record.&nbsp;Does not preserve
	 * leading and trailing whitespace in this column of data.
	 * 
	 * @param value
	 *            The data for the new column.
	 * @exception IOException
	 *                Thrown if an error occurs while writing data to the
	 *                destination stream.
	 */
	public void writeInteger( int value ) throws IOException
	{
		write( value );
	}

	public void write( int value ) throws IOException
	{
		super.write( Integer.toString( value ) );
	}
	/**
	 * Writes another column of data to this record.&nbsp;Does not preserve
	 * leading and trailing whitespace in this column of data.
	 * 
	 * @param value
	 *            The data for the new column.
	 * @exception IOException
	 *                Thrown if an error occurs while writing data to the
	 *                destination stream.
	 */
	public void writeDouble( double value ) throws IOException
	{
		write( value );
	}
	
	public void write( double value ) throws IOException
	{
		super.write( Double.toString( value ) );
	}
	
	public void write( long value ) throws IOException
	{
		super.write( Long.toString( value ) );
	}
	

	/**
	 * Writes a new record using the passed in array of integer values.
	 * 
	 * @param values
	 *            Values to be written.
	 * 
	 * @param preserveSpaces
	 *            Whether to preserver leading and trailing spaces in columns
	 *            while writing out to the record or not.
	 * 
	 * @throws IOException
	 *             Thrown if an error occurs while writing data to the
	 *             destination stream.
	 */
	public void writeRecord( int[] values ) throws IOException
	{
		if ( values != null && values.length > 0 )
			for ( int i = 0; i < values.length; i++ )
				writeInteger( values[ i ] );
		endRecord();
	}

	/**
	 * Writes a new record using the passed in array of double values.
	 * 
	 * @param values
	 *            Values to be written.
	 * 
	 * @param preserveSpaces
	 *            Whether to preserver leading and trailing spaces in columns
	 *            while writing out to the record or not.
	 * 
	 * @throws IOException
	 *             Thrown if an error occurs while writing data to the
	 *             destination stream.
	 */
	public void writeRecord( double[] values ) throws IOException
	{
		if ( values != null && values.length > 0 )
			for ( int i = 0; i < values.length; i++ )
				writeDouble( values[ i ] );
		endRecord();
	}

	public void write( String[] values ) throws IOException
	{
		if ( values != null && values.length > 0 )
			for ( int i = 0; i < values.length; i++ )
				super.write( values[ i ] );
	}
}
