/**
 * 
 */
package ru.ts.gisutils.common.logger;

import ru.ts.utils.Text;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class to add a little to the standard Logger functionality
 * 
 * @author Syg
 * 
 */
public class SysLogger implements IHasLogger
{

	/**
	 * level of messages, default value is {@link Level#INFO}
	 */
	private Level	_level;

	private Logger	_logger;

	/**
	 * Main constructor
	 * @param name
	 */
	public SysLogger( String name )
	{
		_logger = Logger.getLogger( name );
		_level = Level.INFO;
	}

	/**
	 * @return the level of messaging
	 */
	public final Level get_current_level()
	{
		return _level;
	}

	/**
	 * sets a new default level for all follow messaging
	 * 
	 * @param level is the {@link Level} instance to set. If value is <code>null</code> nothing occur.
	 */
	public final void set_current_level( Level level )
	{
		if ( level == null )
			return;
		_level = level;
	}

	public SysLogger getLogger()
	{
		return this;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see Logger#log( Level, String )
	 */
	public void log( Level level, String msg )
	{
		_logger.log( level, msg );
	}

	public void logWarn( String msg )
	{
		log( Level.WARNING, msg );
	}

	public void logErr( String msg )
	{
		log( Level.SEVERE, msg );
	}

	public Level get_level()
	{
		return _level;
	}

	public void set_level( Level level )
	{
		setLogLevel( level );
	}

	/**
	 * logs the message at preset level (from previous call with level
	 * parameter) or with Level.INFO by default for the first call
	 * 
	 * @param msg
	 *            message to output
	 */
	public void log( String msg )
	{
		_logger.log( get_current_level(), msg );
	}

	public void logInfo( String msg )
	{
		log( msg );
	}

	/**
	 * gets the internal Logger instance for your purposes
	 * 
	 * @return the Logger really logging your messages
	 */
	public final Logger get_logger()
	{
		return _logger;
	}

	/**
	 * Sets level of messages allowed for output. There are many levels e.g.
	 * {@see Level#ALL} or {@see Level#OFF}
	 * 
	 * @param level
	 */
	public void setLogLevel( Level level )
	{
		_logger.setLevel( level );
	}

	/**
	 * Add one more handler to the logger
	 * 
	 * @param hnd
	 *            Handler to add
	 */
	public void addHandler( Handler hnd )
	{
		if ( hnd == null )
			return;
		_logger.addHandler( hnd );
	}

	/**
	 * Removes some handler. Returns silently in any case.
	 * 
	 * @param hnd
	 *            Handler to remove
	 */
	public void remove( Handler hnd )
	{
		if ( hnd == null )
			return;
		try
		{
			_logger.removeHandler( hnd );
		}
		catch ( Exception ex )
		{
			log( "" );
		}
	}

	public static void main(String[] args)
	{
		Logger log = Logger.getLogger( "test" );
		log.logp( Level.INFO, Text.class.getCanonicalName(), "indexOf()", "test message" );
	}
}
