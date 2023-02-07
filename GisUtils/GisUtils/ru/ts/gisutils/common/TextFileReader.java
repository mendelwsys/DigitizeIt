/**
 * Created on 18.01.2008 15:56:57 2008 by Syg for project in
 * 'ru.ts.gisutils.common' of 'test'
 */
package ru.ts.gisutils.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class reads text file line by line
 * 
 * @author Syg
 */
public class TextFileReader
{

	private File	       _file;

	private BufferedReader	_br;

	private int	           _line_number;

	private boolean	       _EOF;

	/**
	 * checks if not EOF reached
	 */
	public boolean EOF()
	{
		return _EOF;
	}

	/**
	 * reads next line from the file.
	 * 
	 * @return String as a next line of the file without terminating LF CR If
	 *         EOF is detected at the beginning of reading, <code>null</code>
	 *         is returned as the result
	 */
	public String nextLine() throws IOException
	{
		if ( _EOF )
			return null;
		String ret = _br.readLine();
		if ( _EOF = ( ret == null ) )
			_br.close();
		else
			_line_number++;
		return ret;
	}

	/**
	 * main constructor. Opens file for reading
	 * 
	 * @param filePath
	 *            file path to open
	 * @throws NullPointerException
	 *             if fielPath is null
	 * @throws FileNotFoundException
	 *             if bad file name pointed
	 */
	public TextFileReader( String filePath ) throws FileNotFoundException,
	        NullPointerException
	{
		this( new File( filePath ) );
	}

	/**
	 * Constructor with ability to set read buffer size. May be useful for large
	 * text files to read them fast
	 */
	public TextFileReader( String fPath, int buf_size )
	        throws FileNotFoundException, NullPointerException
	{
		File file = new File( fPath );
		if ( ( !file.exists() ) || ( !file.isFile() ) )
			throw new FileNotFoundException( file.getPath() );
		_br = new BufferedReader( new FileReader( _file = file ), buf_size );
		_line_number = 0;
		_EOF = false;
	}

	/**
	 * additional constructor. Opens file for reading
	 * 
	 * @param file
	 *            File object to work through
	 * @throws FileNotFoundException
	 *             if file is absent
	 */
	public TextFileReader( File file ) throws FileNotFoundException
	{
		if ( ( !file.exists() ) || ( !file.isFile() ) )
			throw new FileNotFoundException( file.getPath() );
		_br = new BufferedReader( new FileReader( _file = file ) );
		_line_number = 0;
		_EOF = false;
	}

	public int readLineCount()
	{
		return _line_number;
	}

	/**
	 * @return the opened text file path as it is
	 */
	public final String get_filePath()
	{
		return _file.getPath();
	}

	/**
	 * tries to free all internally used resources .
	 * 
	 * @throws IOException
	 */
	public final void close() throws IOException
	{
		_br.close();
		_br = null;
		_file = null;
	}

	/**
	 * Returns the name of the file or directory denoted by this abstract
	 * pathname. This is just the last name in the pathname's name sequence. If
	 * the pathname's name sequence is empty, then the empty string is returned.
	 * 
	 * @return The name of the file or directory denoted by this abstract
	 *         pathname, or the empty string if this pathname's name sequence is
	 *         empty
	 */
	public final String getFileName()
	{
		return _file.getName();
	}

	public final String getFilePath()
	{
		return _file.getPath();
	}
}
