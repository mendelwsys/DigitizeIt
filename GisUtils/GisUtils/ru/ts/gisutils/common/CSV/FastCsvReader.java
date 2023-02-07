/**
 * Created 28.07.2008 15:31:25 by Syg for the "GisUtils" project
 */
package ru.ts.gisutils.common.CSV;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import ru.ts.utils.Text;
import ru.ts.gisutils.common.TextFileReader;

import com.csvreader.ICSVReader;

/**
 * Main goal it attempt to build a fast CSV reader, faster than original
 * CsvReader. All records are trimmed before follow processing. All empty and
 * comment lines are skipped.
 * 
 * @author Syg
 * 
 */
public class FastCsvReader implements ICSVReader
{
	private boolean	_skip_empty;

	private boolean	_closed;

	private char	_comment;

	private char	_delimiter;

	String	        _line;

	String[]	    _headers;

	HashMap	        _hmap;

	String[]	    _items;

	TextFileReader	_reader;

	/**
	 * @param fname
	 *            file name to open
	 * @param comment
	 *            comment character. All lines beginning with this char are 
	 *            skipped. Set it to '\0' to process comment lines in a common
	 *            way
	 * @param delimiter
	 *            delimiter between columns
	 */
	public FastCsvReader( String fname, char comment,
	        char delimiter ) throws FileNotFoundException
	{
		_reader = new TextFileReader( fname );
		this._comment = comment;
		this._delimiter = delimiter;
		_items = new String[0];
		_closed = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#close()
	 */
	public void close()
	{
		try
		{
			_reader.close();
			_closed = true;
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#get(int)
	 */
	public String get( int columnIndex ) throws IOException
	{
		if ( columnIndex < 0 || columnIndex >= _items.length )
			throw new IndexOutOfBoundsException( "No column with index = "
			        + columnIndex );
		return _items[ columnIndex ];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#get(java.lang.String)
	 */
	public String get( String headerName ) throws IOException
	{
		_check_closed();
		final Object obj = _hmap.get( headerName );
		if ( obj == null )
			return "";
		final int index = ((Integer)obj).intValue();
		return get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getColumnCount()
	 */
	public int getColumnCount()
	{
		return _items.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getComment()
	 */
	public char getComment()
	{
		return _comment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getCurrentRecord()
	 */
	public long getCurrentRecord()
	{
		return _reader.readLineCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getDelimiter()
	 */
	public char getDelimiter()
	{
		return _delimiter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getHeader(int)
	 */
	public String getHeader( int columnIndex ) throws IOException,
	        IndexOutOfBoundsException
	{
		if ( columnIndex < 0 || _headers == null
		        || columnIndex >= _headers.length )
			throw new IndexOutOfBoundsException( "No header at index = "
			        + columnIndex );
		return _headers[ columnIndex ];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getHeaderCount()
	 */
	public int getHeaderCount()
	{
		return _headers == null ? 0 : _headers.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getHeaders()
	 */
	public String[] getHeaders() throws IOException
	{
		return _headers == null ? null : (String[])_headers.clone();
	}

	private void _check_closed() throws IOException
	{
		if ( _closed )
			throw new IOException( "File is closed" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getIndex(java.lang.String)
	 */
	public int getIndex( String headerName ) throws IOException
	{
		_check_closed();
		Object obj = _hmap.get( headerName );
		if ( obj == null )
			return -1;
		return ( (Integer) obj ).intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getRawRecord()
	 */
	public String getRawRecord()
	{
		return _line;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getSkipEmptyRecords()
	 */
	public boolean getSkipEmptyRecords()
	{
		return _skip_empty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getUseComments()
	 */
	public boolean getUseComments()
	{
		return _comment != '\0';
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#getValues()
	 */
	public String[] getValues() throws IOException
	{
		return _items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#readHeaders()
	 */
	public boolean readHeaders() throws IOException
	{
		if ( !readRecord() )
			return false;
		_headers = Text.splitItems( _line, _delimiter, true );
		setHeaders( _headers );
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#readRecord()
	 */
	public boolean readRecord() throws IOException
	{
		while ( true )
		{
			if ( _reader.EOF() )
				return false;
			_line = _reader.nextLine();
			if ( _line == null )
				return false;
			if ( ( _line = _line.trim() ).length() == 0 )
				continue;
			if ( this.getUseComments() )
				if ( _line.charAt( 0 ) == _comment )
					continue;
			break;
		}
		_items = Text.splitItems( _line, _delimiter, true );
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#setComment(char)
	 */
	public void setComment( char comment )
	{
		_comment = comment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#setDelimiter(char)
	 */
	public void setDelimiter( char delimiter )
	{
		_delimiter = delimiter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#setHeaders(java.lang.String[])
	 */
	public void setHeaders( String[] headers )
	{
		if ( _hmap == null )
			_hmap = new HashMap();
		_hmap.clear();
		_headers = headers;
		if ( headers == null )
			return;
		for ( int i = 0; i < headers.length; i++ )
			_hmap.put( headers[ i ], new Integer( i ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#setSkipEmptyRecords(boolean)
	 */
	public void setSkipEmptyRecords( boolean skipEmptyRecords )
	{
		_skip_empty = skipEmptyRecords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#setUseComments(boolean)
	 */
	public void setUseComments( boolean useComments )
	{
		if ( getUseComments() && ( !useComments ) )
			_comment = '\0';
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csvreader.ICSVReader#skipLine()
	 */
	public boolean skipLine() throws IOException
	{
		if ( _reader.EOF() )
			return false;
		return _reader.nextLine() != null;
	}

	public String getFilePath()
    {
	    return _reader.getFilePath();
    }

}
