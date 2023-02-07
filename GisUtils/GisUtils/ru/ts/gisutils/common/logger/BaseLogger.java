/**
 * 
 */
package ru.ts.gisutils.common.logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import ru.ts.utils.DateTime;

/**
 * @author sygsky
 * 
 */
// @SuppressWarnings("unchecked")
public class BaseLogger implements ILogger
{

	public  static final ILogger Console = new BaseLogger();
	
	private final ArrayList	streams;

	/**
	 * logger text name
	 */
	private String	        _name;

	private static int	    _id	= 1;

	/**
	 * MAIN constructor - always is called from all other ones
	 */
	public BaseLogger( PrintStream out )
	{
		streams = new ArrayList();
		streams.add( out );
		_name = "Logger #" + _id++;
		logLine( " +++ " + _name + ": LOGGING started +++" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.logger.ILogger#getName()
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * constructor by default, use {@link java.lang.System.out} stream
	 * 
	 */
	public BaseLogger()
	{
		this( System.out );
	}

	/**
	 * prepare output to the the file, if file exists, output will be appended.
	 * The output buffer will be flushed whenever a byte array is written, one
	 * of the <code>println</code> methods is invoked, or a newline character
	 * or byte (<code>'\n'</code>) is written. Output are appended to the
	 * file contents if any.
	 * 
	 * @param fileName
	 *            the system-dependent file name
	 * @throws FileNotFoundException
	 *             if the file exists but is a directory rather than a regular
	 *             file, does not exist but cannot be created, or cannot be
	 *             opened for any other reason.
	 */
	public BaseLogger( String fileName ) throws FileNotFoundException
	{
		this( fileName, true );
	}

	/**
	 * prepare output to the the file, if file exists, output will be appended.
	 * The output buffer will be flushed whenever a byte array is written, one
	 * of the <code>println</code> methods is invoked, or a newline character
	 * or byte (<code>'\n'</code>) is written
	 * 
	 * @param fileName
	 *            the system-dependent file name
	 * @param append
	 *            if <code>true</code>, then bytes will be written to the end
	 *            of the file rather than the beginning
	 * @throws FileNotFoundException
	 *             if the file exists but is a directory rather than a regular
	 *             file, does not exist but cannot be created, or cannot be
	 *             opened for any other reason.
	 */
	public BaseLogger( String fileName, boolean append )
	        throws FileNotFoundException
	{
		this( new PrintStream( new FileOutputStream( fileName, append ), true ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.logger.ILogger#appendStream(java.io.PrintStream)
	 */
	public boolean appendStream( PrintStream stream )
	{
		if ( streams.indexOf( stream ) >= 0 )
			return false;
		streams.add( stream );
		return true;
	}

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
	        throws FileNotFoundException
	{
		return appendStream( new PrintStream( new FileOutputStream( fPath,
		        append ), true ) );
	}

	/**
	 * tries to remove stream from the output stream list
	 * 
	 * @param stream
	 *            output stream to remove from a list
	 * @return <code>true</code> if stream was removed successfully. It is
	 *         your problem to close this stream after. <code>false</code> if
	 *         such stream not found in the pool of output streams
	 */
	public boolean removeStream( PrintStream stream )
	{
		if ( streams.size() == 1 )
			return false;
		int index;
		if ( ( index = streams.indexOf( stream ) ) < 0 )
			return false;
		streams.remove( index );
		return true;
	}

	/**
	 * returns first output stream from an internal pool of the class
	 * PrintStream - the output stream first in the list
	 */
	public PrintStream getLogStream()
	{
		return (PrintStream) streams.get( 0 );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.ILogger#log()
	 */
	public void log()
	{
		log( "" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.ILogger#log(java.lang.Object)
	 */
	public void log( Object obj )
	{
		log( String.valueOf( obj ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.ILogger#log(java.lang.String)
	 */
	public void log( String text )
	{
		for ( int i = 0; i < streams.size(); i++ )
		{
			PrintStream stream = (PrintStream) streams.get( i );
			stream.print( ( timebefore ? DateTime.gettimestr() + " " : "" )
			        + text );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.ILogger#logLine()
	 */
	public void logLine()
	{
		boolean tmb = timebefore;
		timebefore = false;
		log( "\n" );
		timebefore = tmb;
	}

	public void logLine( int num )
	{
		boolean tmb = timebefore;
		timebefore = false;
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < num; i++ )
			sb.append( "\n" );
		log( sb.toString() );
		timebefore = tmb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.ILogger#logLine(java.lang.String)
	 */
	public void logLine( String text )
	{
		log( text + "\n" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.ILogger#logf(java.lang.String,
	 *      java.lang.Object[])
	 */
	public void logf( String format, Object[] args )
	{
		log( String.format( format, args ) );
	}

	/**
	 * shows to insert date time string before any other argument in the output
	 * to the goal stream. If true - date time is inserted, if false - not
	 * inserted
	 */
	public boolean	timebefore	= true;

	protected void finalize() throws Throwable
	{
		timebefore = true;
		logLine( "+++ " + _name + ": LOGGING stopped +++" );
		close();
		streams.clear();
		super.finalize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.logger.ILogger#getLogStream(int)
	 */
	public PrintStream getLogStream( int index )
	{
		if ( index < streams.size() )
			return (PrintStream) streams.get( index );
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.logger.ILogger#getStreamCount()
	 */
	public int size()
	{
		return streams.size();
	}

	public void close()
	{
		for ( int i = streams.size() - 1; i >= 0; i-- )
		{
			PrintStream stream = ( (PrintStream) streams.get( i ) );
			if ( stream.equals( System.out ) || stream.equals( System.err )
			        || stream.equals( System.in ) )
				continue;
			stream.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.common.logger.ILogger#logException(java.lang.Exception)
	 */
	public void logException( Exception ex )
	{
		for ( int i = 0; i < streams.size(); i++ )
		{
			PrintStream stream = ( (PrintStream) streams.get( i ) );
			ex.printStackTrace( stream );
		}
	}
}
