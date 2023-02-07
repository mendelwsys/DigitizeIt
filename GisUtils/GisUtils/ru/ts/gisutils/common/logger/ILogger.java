/**
 * 
 */
package ru.ts.gisutils.common.logger;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * @author sygsky
 * 
 * Interface for logging info to any output PrintStream
 */
public abstract interface ILogger
{
	/**
	 * log empty string
	 */
	void log();
	
	/**
	 * log empty with LF+CR
	 *
	 */
	void logLine();
	
	/**
	 * log empty lines num times 
	 * @param num number of empty lines to insert to the output stream
	 */
	void logLine(int num);

	/**
	 * log object as string without LF+CR
	 * @param obj
	 */
	void log(Object obj);
	
	/**
	 * logs the whole string to the output stream
	 * 
	 * @param text -
	 *            text to output
	 */
	void log(String text);

	/**
	 * logs the string to the output stream and append to it LF+CR sequence
	 * 
	 * @param text -
	 *            text to output
	 */
	void logLine(String text);

	/**
	 * prints arguments as printf of C language does it to the output stream
	 * 
	 * @param format
	 *            A format string as described in <a
	 *            href="../util//Formatter.html#syntax">Format string syntax</a>
	 * 
	 * @param args
	 *            Arguments referenced by the format specifiers in the format
	 *            string. If there are more arguments than format specifiers,
	 *            the extra arguments are ignored. The number of arguments is
	 *            variable and may be zero. The maximum number of arguments is
	 *            limited by the maximum dimension of a Java array as defined by
	 *            the <a href="http://java.sun.com/docs/books/vmspec/">Java
	 *            Virtual Machine Specification</a>. The behaviour on a
	 *            <tt>null</tt> argument depends on the <a
	 *            href="../util/Formatter.html#syntax">conversion</a>.
	 */
	void logf(String format, Object[] args);

	/**
	 * info about first stream used in this log object
	 * 
	 * @return first stream used for logging 
	 */
	PrintStream getLogStream();
	
	/**
	 * get number of streams used to log
	 * @return number in range from 1 to n of log streams used
	 */
	int size();

	/**
	 * info about first stream used in this log object
	 * @param index of stream in the list 
	 * @return stream at this position. May return null if index out of range
	 */
	PrintStream getLogStream(int index);
	
	/**
	 * tries to append stream to the output stream list
	 * 
	 * @param stream
	 *            new output stream
	 * @return <code>true</code> if stream was appended.
	 *         <code>false</code> if the stream already exists in the pool
	 */
	boolean appendStream(PrintStream stream);
	
	/**
	 * appends new log stream as file
	 * 
	 * @param fPath
	 *            file path for a log
	 * @param append
	 *            defines append to content (<code>true</code>) or not (<code>false</code>)
	 * @return <code>true</code> if new output successfully added to the log
	 *         service or <code>false</code> if not
	 * @throws FileNotFoundException
	 */
	public boolean appendFile( String fPath, boolean append )
	        throws FileNotFoundException;
	
	/**
	 * tries to remove stream from the output stream list
	 * 
	 * @param stream
	 *            output stream to remove from a list
	 * @return <code>true</code> if stream was removed successfully. It is
	 *         your problem to close this stream after.
	 *         <code>false</code> if such stream not found in the pool of output
	 *         streams or it is a last stream in the pool. Such stream can't be 
	 *         removed at any case.
	 */
	public boolean removeStream(PrintStream stream);

	/**
	 * gets internal name of the logger
	 * @return String with logger name
	 */
	public String getName();

	
	/**
	 * logs exception to the output stream[s]
	 * @param ex Exception to log
	 */
	public void logException(Exception ex);

}
