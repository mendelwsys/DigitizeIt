/**
 * Created on 12.03.2008 19:17:18 2008 by Syg for project in
 * 'ru.ts.gisutils.common' of 'test'
 */
package ru.ts.gisutils.common.CSV;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * helps to read and process comma separated values file
 * 
 * @author Syg
 */
public class CSVFileReader extends com.csvreader.CsvReader
{

	public CSVFileReader( Reader inputStream )
	{
		super( inputStream );
	}

	public CSVFileReader( Reader inputStream, char delimiter )
	{
		super( inputStream, delimiter );
	}

	public CSVFileReader( InputStream inputStream, char delimiter,
	        Charset charset )
	{
		super( inputStream, delimiter, charset );
	}

	public CSVFileReader( InputStream inputStream, Charset charset )
	{
		super( inputStream, charset );
	}

	public CSVFileReader( String fileName, char delimiter, Charset charset )
	        throws FileNotFoundException
	{
		super( fileName, delimiter, charset );
	}

	public CSVFileReader( String fileName, char delimiter, String charsetname )
	        throws FileNotFoundException
	{
		super( fileName, delimiter, Charset.forName( charsetname ) );
	}

	public CSVFileReader( String fileName, char delimiter )
	        throws FileNotFoundException
	{
		super( fileName, delimiter, Charset.forName( "windows-1251" ) );
	}

	public CSVFileReader( String fileName ) throws FileNotFoundException
	{
		super( fileName, ',', Charset.forName( "windows-1251" ) );
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

	/**
	 * tries to detect from CSV line at item delimiter
	 * 
	 * @param line
	 *            CSV file data line
	 * @return char representation of detected delimiter
	 * @throws UnknownDelimiterException
	 *             if delimiter was not detected
	 */
	public static char detectCSVDelimiter( String line )
	        throws CSVDelimiterException
	{
		String str = line.trim();
		for ( int i = 0; i < str.length(); i++ )
		{
			char c = str.charAt( i );
			if ( Character.isLetterOrDigit( c ) || Character.isWhitespace( c ) )
				continue;
			switch ( str.charAt( i ) )
			{
			case '.':
			case '+':
			case '-':
			case 'e':
			case 'E':
				break;
			default:
				/* First char out of real number will be delimiter */
				return c;
			}
		}
		throw new CSVDelimiterException( line );
	}

}
