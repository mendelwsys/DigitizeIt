/**
 * 
 */
package ru.ts.gisutils.common.specials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Simplest class to read text files by lines or in the whole peace.
 * 
 * @author Syg
 * 
 */
public class TextReader
{

	BufferedReader	_reader;

	long	       _length;

	String	       _path;

	/**
	 * @param path
	 *            file path to open for text read
	 * @param charset
	 *            character set used to read text from a file. Can be
	 *            <code>null</code> or empty string to use default character
	 *            set
	 */
	public TextReader( String path, String charset )
	        throws FileNotFoundException, UnsupportedEncodingException
	{
		final File file = new File( path );
		if ( ( charset == null ) || ( charset.length() == 0 ) )
			_reader = new BufferedReader( new InputStreamReader(
			        new FileInputStream( file ) ) );
		else
			_reader = new BufferedReader( new InputStreamReader(
			        new FileInputStream( file ), charset ) );
		_length = file.length();
		_path = file.getPath();
	}

	/**
	 * reads the next line from a file opened in constructor
	 * 
	 * @return next string as a line of the file or null if end of file is
	 *         reached
	 * @throws IOException
	 *             if error during read occur
	 */
	public String readLine() throws IOException
	{
		return _reader.readLine();
	}

	/**
	 * returns opened text file path
	 * 
	 * @return String as a file path
	 */
	public String getPath()
	{
		return _path;
	}

	/**
	 * return the length of the file in bytes
	 * 
	 * @return long value for a file length in bytes
	 */
	public long getLength()
	{
		return _length;
	}

	/**
	 * Reads the whole remaining file part into a buffer. If you didn't read any
	 * chars from this file, the whole file will be read. End of line symbols
	 * will be removed from the resulting String
	 * 
	 * @return Resulting String with all the chars read
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public String readWholeFile() throws IOException
	{
		StringBuffer sb = new StringBuffer();
		String buffer = "";
		while ( ( buffer = readLine() ) != null )
			sb.append( buffer );
		return sb.toString();
	}
}
