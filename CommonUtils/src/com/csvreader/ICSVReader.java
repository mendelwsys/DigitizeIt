package com.csvreader;

import java.io.IOException;

public interface ICSVReader
{

	public abstract String getRawRecord();

	/**
	 * Gets the character being used as the column delimiter. Default is comma,
	 * ','.
	 * 
	 * @return The character being used as the column delimiter.
	 */
	public abstract char getDelimiter();

	/**
	 * Sets the character to use as the column delimiter. Default is comma, ','.
	 * 
	 * @param delimiter
	 *            The character to use as the column delimiter.
	 */
	public abstract void setDelimiter( char delimiter );

	/**
	 * Gets the character being used as a comment signal.
	 * 
	 * @return The character being used as a comment signal.
	 */
	public abstract char getComment();

	/**
	 * Sets the character to use as a comment signal.
	 * 
	 * @param comment
	 *            The character to use as a comment signal.
	 */
	public abstract void setComment( char comment );

	/**
	 * Gets whether comments are being looked for while parsing or not.
	 * 
	 * @return Whether comments are being looked for while parsing or not.
	 */
	public abstract boolean getUseComments();

	/**
	 * Sets whether comments are being looked for while parsing or not.
	 * 
	 * @param useComments
	 *            Whether comments are being looked for while parsing or not.
	 */
	public abstract void setUseComments( boolean useComments );

	/**
	 * Gets the count of columns found in this record.
	 * 
	 * @return The count of columns found in this record.
	 */
	public abstract int getColumnCount();

	/**
	 * Gets the index of the current record.
	 * 
	 * @return The index of the current record.
	 */
	public abstract long getCurrentRecord();

	/**
	 * Gets the count of headers read in by a previous call to
	 * {@link com.csvreader.CsvReader#readHeaders readHeaders()}.
	 * 
	 * @return The count of headers read in by a previous call to
	 *         {@link com.csvreader.CsvReader#readHeaders readHeaders()}.
	 */
	public abstract int getHeaderCount();

	/**
	 * Returns the header values as a string array.
	 * 
	 * @return The header values as a String array.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public abstract String[] getHeaders() throws IOException;

	public abstract void setHeaders( String[] headers );

	public abstract String[] getValues() throws IOException;

	/**
	 * Returns the current column value for a given column index.
	 * 
	 * @param columnIndex
	 *            The index of the column.
	 * @return The current column value.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public abstract String get( int columnIndex ) throws IOException;

	/**
	 * Returns the current column value for a given column header name.
	 * 
	 * @param headerName
	 *            The header name of the column.
	 * @return The current column value.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public abstract String get( String headerName ) throws IOException;

	/**
	 * Reads another record.
	 * 
	 * @return Whether another record was successfully read or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	public abstract boolean readRecord() throws IOException;

	/**
	 * Read the first record of data as column headers.
	 * 
	 * @return Whether the header record was successfully read or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	public abstract boolean readHeaders() throws IOException;

	/**
	 * Returns the column header value for a given column index.
	 * 
	 * @param columnIndex
	 *            The index of the header column being requested.
	 * @return The value of the column header at the given column index.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public abstract String getHeader( int columnIndex ) throws IOException;

	/**
	 * Gets the corresponding column index for a given column header name.
	 * 
	 * @param headerName
	 *            The header name of the column.
	 * @return The column index for the given column header name.&nbsp;Returns
	 *         -1 if not found.
	 * @exception IOException
	 *                Thrown if this object has already been closed.
	 */
	public abstract int getIndex( String headerName ) throws IOException;

	/**
	 * Skips the next line of data using the standard end of line characters and
	 * does not do any column delimited parsing.
	 * 
	 * @return Whether a line was successfully skipped or not.
	 * @exception IOException
	 *                Thrown if an error occurs while reading data from the
	 *                source stream.
	 */
	public abstract boolean skipLine() throws IOException;

	/**
	 * Closes and releases all related resources.
	 */
	public abstract void close();
	
	/**
	 * Gets full CSV file path
	 * @return String with a CSV file path
	 */
	public String getFilePath();

}