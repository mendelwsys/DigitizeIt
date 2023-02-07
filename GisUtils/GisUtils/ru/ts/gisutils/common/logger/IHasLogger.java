/**
 * 
 */
package ru.ts.gisutils.common.logger;

import java.util.logging.Level;

/**
 * @author Syg
 *
 */
public interface IHasLogger
{
		SysLogger getLogger();
		
		void log( Level level, String msg);
		void log(  String msg);
		void logInfo( String msg );
		void logWarn( String msg );
		void logErr( String msg );
		Level get_level();
		void set_level( Level level );
}
