/**
 *
 */
package ru.ts.utils;

import com.mwlib.utils.Enc;
import ru.ts.utils.Text;

import java.util.Calendar;
import java.util.Date;

/**
 * @author sygsky
 */
public class DateTime
{

	/**
	 * a small extractions from AbstractCalendar#
	 */
	// The constants assume no leap seconds support.
	static final int SECOND_IN_MILLIS = 1000;

	static final int MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;

	static final int HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;

	static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24L;

	// The number of days between January 1, 1 and January 1, 1970 (Gregorian)
	static final int EPOCH_OFFSET = 719163;

	private static final Calendar _zeroCalendar = Calendar.getInstance();

	static
	{
		_zeroCalendar.setTimeInMillis( 0L );
	}

	/**
	 * English names of month in a shortened form
	 */
	static final String[] ENG_MONTH_NAMES = new String[] { "JAN",
	                                                       "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT",
	                                                       "NOV", "DEC" };

	/**
	 * Russian names of month in a shortened form
	 */
	static final String[] RUS_MONTH_NAMES = new String[] { Enc.get("$2"),
	                                                       Enc.get("$3"), Enc.get("$4"), Enc.get("$5"), Enc.get("$6"), Enc.get("$7"), Enc.get("$8"), Enc.get("$9"), Enc.get("$10"), Enc.get("$11"),
	                                                       Enc.get("$12"), Enc.get("$13") };

	public static Calendar zeroCalendar()
	{
		return (Calendar) _zeroCalendar.clone();
	}

	/**
	 * generates current date and time string representation in a standard form
	 *
	 * @return string of format like "21-SEP-2007 11:50:22.123". value after
	 *         decimal point stands for milliseconds
	 */
	public static String gettimestr()
	{
		return gettimestr( Calendar.getInstance() );
	}

	/**
	 * generates date and time string representation in a standard form
	 *
	 * @param cal -
	 *            Calendar object
	 * @return string of Calendar exemplar
	 */
	public static String gettimestr( final Calendar cal )
	{
		if ( false )
		{
			return String.format( "%1$Td-%1$Tb-%1$Ty %1$TT.%1$TL",
					new Object[] { cal } );
		}
		else
		{
			final StringBuffer sb = new StringBuffer();
			// prepare day-month-year

			int val = cal.get( Calendar.DAY_OF_MONTH );

			String str = Integer.toString( val );
			if ( str.length() < 2 )
				sb.append( '0' );
			sb.append( str );

			val = cal.get( Calendar.MONTH );
			sb.append( "-" + ENG_MONTH_NAMES[ val ] + "-" );
			val = cal.get( Calendar.YEAR );
			str = Integer.toString( val );
			for ( int i = str.length(); i < 4; i++ )
				sb.append( '0' );
			sb.append( str );
			sb.append( ' ' ); // string now is something like "21-SEP-2007 "

			// prepare hh:mm:ss.ms

			val = cal.get( Calendar.HOUR_OF_DAY );
			str = Integer.toString( val );
			if ( str.length() < 2 )
				sb.append( '0' );
			sb.append( str + ":" );

			val = cal.get( Calendar.MINUTE );
			str = Integer.toString( val );
			if ( str.length() < 2 )
				sb.append( '0' );
			sb.append( str + ":" );

			val = cal.get( Calendar.SECOND );
			str = Integer.toString( val );
			if ( str.length() < 2 )
				sb.append( '0' );
			sb.append( str );
			sb.append( '.' );

			val = cal.get( Calendar.MILLISECOND );
			str = Integer.toString( val );
			for ( int i = str.length(); i < 3; i++ )
				sb.append( '0' );
			sb.append( str );
			return sb.toString();
		}
	}

	/**
	 * returns string of time in format suitable to add to the file name
	 *
	 * @param cal Calendar class object
	 * @return String with a time in form DD_MM_YYYY_HH_MM_SS_MLS
	 */
	public static String gettimestrA( final Calendar cal )
	{
		final String str = gettimestr( cal );
		return str.replaceAll( "[-: .]", "_" );
	}

	/**
	 * returns string of the current time in format suitable to add to the file
	 * name
	 *
	 * @return String with a time in form DD_MM_YYYY_HH_MM_SS_MLS
	 */
	public static String gettimestrA()
	{
		return gettimestrA( Calendar.getInstance() );
	}

	/**
	 * gets Date object from Calendar
	 *
	 * @param cal Calendar to get Date from
	 * @return Date object
	 */
	public static Date getDate( Calendar cal )
	{
		return new Date( cal.getTimeInMillis() );
	}

	/**
	 * See description for the method
	 * {@link DateTime#parseDate(String, boolean)}. This is the redirect call
	 * to it as follow (datetimestr, false)
	 */
	public static Date parseDate( String datetimestr )
			throws NumberFormatException
	{
		return parseDate( datetimestr, false );
	}

	/**
	 * Parses date as a text of format "YYYY-MM-DD HH:MM:SS.MS"
	 *
	 * @param date_time_str String in format "YYYY-MM-DD HH:MM:SS.MS". You can indicate
	 *                      not complete time string e.g. "1917-11-07 05" - it will be
	 *                      parsed as 5 hours at 7 November of 1917 year. But you should
	 *                      or set the integral date string (including year, month, date)
	 *                      or skip it totally. E.g. "2008-06-23 5" ,"5:4:3.210" are
	 *                      correct but "2008-06 5", "06-23 5:4:3:2.1" ares incorrect.
	 *                      <p/>
	 *                      <pre>
	 *                      Where:
	 *                      <p/>
	 *                      	YYYY - year, e.g.  1917
	 *                       MM   - month, e.g.   11
	 *                       DD   - day,   e.g.   07
	 *                      <p/>
	 *                       HH:MM:SS:MS - hours:minutes:seconds:milliseconds
	 *                      <p/>
	 *                      </pre>
	 * @param no_err        If set <code>true</code> no exception will be thrown, if
	 *                      <code>false</code>, exception will throw
	 * @return Date instance for the parsed string, <code>null</code> if
	 *         no_err is set to <code>true</code> and some error occur
	 * @throws NumberFormatException if time/date string has illegal form,
	 * @throws NullPointerException  if date_time_str is set to <code>null</code>
	 */
	public static Date parseDate( String date_time_str, boolean no_err )
			throws NumberFormatException, NullPointerException
	{
		if ( true )
		{
			String dts = date_time_str.trim();
			int len = dts.length();
			/* Is there date set? */
			int index = 0, index1 = dts.indexOf( '-' );
			int year = 0, month = 0, day = 0;
			if ( index1 >= 0 )
			{
				/*
				 * Date is set, parse all three parts of date, year, month, date
				 */
				year = Integer.parseInt( dts.substring( index, index1 ) ) - 1900;
				index1 = dts.indexOf( '-', index = index1 + 1 );
				if ( index1 <= 0 )
					throw new IllegalArgumentException( "Month expected" );
				month = Integer.parseInt( dts.substring( index, index1 ) ) - 1;
				/* find any not digit */
				if ( index1 == len )
					throw new IllegalArgumentException( "Day number expected" );
				for ( index = ++index1; index1 < len; index1++ )
					if ( !Character.isDigit( dts.charAt( index1 ) ) )
						break;
				day = Integer.parseInt( dts.substring( index, index1 ) );
				if ( index1 == len ) /* Only Date part is present */
					return new Date( year, month, day );
				/*
				 * skip white spaces between date and time portions of the Date
				 */
				for ( index = index1 + 1; index < len; index++ )
					if ( !Character.isWhitespace( dts.charAt( index ) ) )
						break;
			}
			else
				day = 1;

			/*
			 * String can't be completed with a white spaces as it was trimmed
			 * before processing, so we don't check for that case
			 */

			int hours = 0, mins = 0, secs = 0;
			long millis = 0L;
			/* Check if minutes are present */
			index1 = dts.indexOf( ':', index );
			if ( index1 < 0 )
				index1 = len;
			hours = Integer.parseInt( dts.substring( index, index1 ) );
			if ( index1 < len )
			{
				/* then minutes are set, check for seconds */
				index = index1 + 1;
				index1 = dts.indexOf( ':', index );
				if ( index1 < 0 )
					index1 = len;
				mins = Integer.parseInt( dts.substring( index, index1 ) );
				if ( index1 < len )
				{
					/* then seconds are set, check for milliseconds */
					index = index1 + 1;
					index1 = dts.indexOf( '.', index );
					if ( index1 < 0 )
						index1 = len;
					secs = Integer.parseInt( dts.substring( index, index1 ) );
					Date dt = new Date( year, month, day, hours, mins, secs );
					if ( index1 < len )
					{
						/* then read milliseconds and finish this hard work */
						millis = Integer.parseInt( dts
								.substring( index, index1 ) );
						dt.setTime( dt.getTime() + millis );
						return dt;
					}
				}
				else
					/* up to the minutes all is set, no seconds and smaller */
					return new Date( year, month, day, hours, mins );
			}
			return new Date( year, month, day, hours, mins, secs );
		}
		else
		{
			final String[] parts = Text.splitItems( date_time_str, "-: \t.",
					false );
			boolean date_presence = date_time_str.indexOf( '-' ) >= 0;
			int cnt = parts.length;
			if ( cnt == 0 )
				return new Date( 0L );
			final int vals[] = new int[7];
			for ( int i = 0; i < cnt; i++ )
				vals[ i ] = Integer.parseInt( parts[ i ] );
			if ( ( cnt == 3 ) && date_presence )
				return new Date( vals[ 0 ] - 1900, vals[ 1 ] - 1, vals[ 2 ] );
			if ( cnt == 5 )
				return new Date( vals[ 0 ] - 1900, vals[ 1 ] - 1, vals[ 2 ],
						vals[ 3 ], vals[ 4 ] );
			final Date dt = new Date( vals[ 0 ] - 1900, vals[ 1 ] - 1,
					vals[ 2 ], vals[ 3 ], vals[ 4 ], vals[ 5 ] );
			if ( cnt <= 6 ) // no need to add milliseconds
				return dt;
			/* Milliseconds found, add them now */
			dt.setTime( dt.getTime() + (long) vals[ 6 ] );
			return dt;
		}
	}

	/**
	 * convert Date into string of form:
	 * <p/>
	 * <pre>
	 * &quot;YYYY:MM:DD HH:MM:SS.MLS&quot;, where MLS are milliseconds all other you know
	 * </pre>
	 *
	 * @param start Date instance to convert
	 * @param rv    StringBuffer to use for conversion
	 */
	// public static String date2StdString( Date date )
	// {
	// Calendar cal = Calendar.getInstance();
	// cal.setTimeInMillis( date.getTime() );
	// StringBuffer sb = new StringBuffer( "%04d-%02d-%02d" ); // only date
	// if ( cal.get( Calendar.MILLISECOND ) != 0 )
	// sb.append( " %02d:%02d:%02d.%03d" ); // all time
	// else if ( cal.get( Calendar.SECOND ) != 0 )
	// sb.append( " %02d:%02d:%02d" ); // seconds
	// else if ( cal.get( Calendar.MINUTE ) != 0 )
	// sb.append( " %02d:%02d" ); // minutes
	// else if ( cal.get( Calendar.HOUR_OF_DAY ) != 0 )
	// sb.append( " %02d" ); // hours
	// return String.format( sb.toString(), new Object[] {
	// Integer.valueOf( cal.get( Calendar.YEAR ) ),
	// Integer.valueOf( cal.get( Calendar.MONTH ) + 1 ),
	// Integer.valueOf( cal.get( Calendar.DAY_OF_MONTH ) ),
	// Integer.valueOf( cal.get( Calendar.HOUR_OF_DAY ) ),
	// Integer.valueOf( cal.get( Calendar.MINUTE ) ),
	// Integer.valueOf( cal.get( Calendar.SECOND ) ),
	// Integer.valueOf( cal.get( Calendar.MILLISECOND ) ) } );
	// }
	private static void setDateString( Date start, StringBuffer rv )
	{
		int m = start.getMonth() + 1;
		String sm = String.valueOf( m );
		if ( m < 10 )
			sm = "0" + sm;

		int d = start.getDate();
		String sd = String.valueOf( d );
		if ( d < 10 )
			sd = "0" + sd;
		rv.append( start.getYear() + 1900 ).append( "-" ).append( sm ).append(
				"-" ).append( sd );
	}

	private static void setTimeString( Date start, StringBuffer rv )
	{
		int h = start.getHours();
		String sh = String.valueOf( h );
		if ( h < 10 )
			sh = "0" + sh;

		int min = start.getMinutes();
		String smin = String.valueOf( min );
		if ( min < 10 )
			smin = "0" + smin;

		int sec = start.getSeconds();
		String ssec = String.valueOf( sec );
		if ( sec < 10 )
			ssec = "0" + ssec;

		rv.append( sh ).append( ":" ).append( smin ).append( ":" )
				.append( ssec );
	}

	public static String date2StdString( Date date )
	{
/*
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( date.getTime() );

		StringBuffer sb = new StringBuffer( "%04d-%02d-%02d" ); // only date
		if ( cal.get( Calendar.MILLISECOND ) != 0 )
			sb.append( " %02d:%02d:%02d.%03d" ); // all time
		else if ( cal.get( Calendar.SECOND ) != 0 )
			sb.append( " %02d:%02d:%02d" ); // seconds
		else if ( cal.get( Calendar.MINUTE ) != 0 )
			sb.append( " %02d:%02d" ); // minutes
		else if ( cal.get( Calendar.HOUR_OF_DAY ) != 0 )
			sb.append( " %02d" ); // hours
*/

		StringBuffer rv = getDateString( date );
		rv.append( " " );
		setTimeString( date, rv );

		// return String.format( sb.toString(), new Object[] {
		// Integer.valueOf( cal.get( Calendar.YEAR ) ),
		// Integer.valueOf( cal.get( Calendar.MONTH ) + 1 ),
		// Integer.valueOf( cal.get( Calendar.DAY_OF_MONTH ) ),
		// Integer.valueOf( cal.get( Calendar.HOUR_OF_DAY ) ),
		// Integer.valueOf( cal.get( Calendar.MINUTE ) ),
		// Integer.valueOf( cal.get( Calendar.SECOND ) ),
		// Integer.valueOf( cal.get( Calendar.MILLISECOND ) ) } );
		return rv.toString();
	}

	public static StringBuffer getCurrentDateString()
	{
		return getDateString( new Date( System.currentTimeMillis() ) );
	}

	public static StringBuffer getDateString( Date date )
	{
		StringBuffer rv = new StringBuffer();
		setDateString( date, rv );
		return rv;
	}

	/*
	 * public static String date2StdString0( Date date ) { Calendar cal =
	 * Calendar.getInstance(); cal.setTimeInMillis( date.getTime() ); String str =
	 * String.format( "%04d-%02d-%02d %02d:%02d:%02d.%03d", new Object[] {
	 * Integer.valueOf( cal.get( Calendar.YEAR ) ), Integer.valueOf( cal.get(
	 * Calendar.MONTH ) + 1 ), Integer.valueOf( cal.get( Calendar.DAY_OF_MONTH ) ),
	 * Integer.valueOf( cal.get( Calendar.HOUR_OF_DAY ) ), Integer.valueOf(
	 * cal.get( Calendar.MINUTE ) ), Integer.valueOf( cal.get( Calendar.SECOND ) ),
	 * Integer.valueOf( cal.get( Calendar.MILLISECOND ) ) } ); return str; }
	 */
	/**
	 * TODO
	 *
	 * @param millis TODO
	 * @return TODO
	 */
	public static String millis2StdString( long millis )
	{
		return date2StdString( new Date( millis ) );
	}

	/**
	 * adds time from a one date to another. Only time is taking into
	 * consideration. Date (Year, month, day) is ignored totally
	 *
	 * @param date result Date instance. The time of this item will be replaced
	 *             while date components YEAR, MONTH and DAY remain unchanged
	 * @param time Date instance which time components HOUR, MINUTE, SECOND and
	 *             MILLISECOND will be added to a date
	 * @return <code>true</code> if both instances are real and addition occur
	 *         else <code>false</code> e.g. result or add are
	 *         <code>null</code>
	 */
	public static boolean appendTime( Date date, Date time )
	{
		// TODO test this method more
		if ( ( date == null ) || ( time == null ) )
			return false;
		Calendar cal = Calendar.getInstance();
		cal.setTime( date );
		Calendar tmo = Calendar.getInstance();
		tmo.setTime( time );
		cal.set( Calendar.HOUR_OF_DAY, tmo.get( Calendar.HOUR_OF_DAY ) );
		cal.set( Calendar.MINUTE, tmo.get( Calendar.MINUTE ) );
		cal.set( Calendar.SECOND, tmo.get( Calendar.SECOND ) );
		cal.set( Calendar.MILLISECOND, tmo.get( Calendar.MILLISECOND ) );
		date.setTime( cal.getTimeInMillis() );
		return true;
	}

	/**
	 * the same as new Date()
	 *
	 * @return current time in a Date format
	 */
	public static Date now()
	{
		return new Date();
	}

	/**
	 * Returns current milliseconds after epoch
	 *
	 * @return long value with milliseconds after epoch
	 */
	public static long nowMillis()
	{
		return System.currentTimeMillis();
	}

	/**
	 * Clear the time, leaving only Date
	 *
	 * @param dt Date to clear time components (hours, minutes, seconds,
	 *           milliseconds). This instance is changed!!!
	 * @return Date resulting (all time components are zero (0)
	 */
	public static Date clearTime( Date dt )
	{
		dt.setTime( clearTime( dt.getTime() ) );
		return dt;
	}

	/**
	 * removes all parts lower than DAY from a date
	 *
	 * @param date long value since January 1, 1970 (Gregorian)
	 * @return the same date but without HOUR, MINUTE, SECOND , MILLISECOND
	 *         fields
	 */
	public static long clearTime( long date )
	{
		if ( false )
		{
			return (date / DAY_IN_MILLIS) * DAY_IN_MILLIS;
		}
		else
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis( date );
			int year = cal.get( Calendar.YEAR );
			int mon = cal.get( Calendar.MONTH );
			int day = cal.get( Calendar.DAY_OF_MONTH );
			cal.clear();
			cal.set( year, mon, day );
			return cal.getTimeInMillis();
		}
	}

	/**
	 * Detects if time is set in the designated milliseconds
	 *
	 * @param date milliseconds from the epoch
	 * @return <code>true</code> if time portion (HOUR,MINUTE,SECOND,MILLIS)
	 *         is set in the milliseconds or <code>false</code> if not
	 */
	public static boolean isTimeSet( long date )
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( date );
		return ( cal.get( Calendar.HOUR_OF_DAY ) + cal.get( Calendar.MINUTE )
				+ cal.get( Calendar.SECOND ) + cal.get( Calendar.MILLISECOND ) ) != 0;
	}

	/**
	 * Returns time portion of milliseconds designated
	 *
	 * @param date long with milliseconds from epoch
	 * @return long value for a time portion (HOUR,MINUTE,SECOND,MILLIS) of the
	 *         milliseconds designated
	 */
	public static long timeOnly( long date )
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( date );
		int hour = cal.get( Calendar.HOUR_OF_DAY );
		int min = cal.get( Calendar.MINUTE );
		int sec = cal.get( Calendar.SECOND );
		int mls = cal.get( Calendar.MILLISECOND );
		cal.clear();
		cal.set( 0, 0, 0, hour, min, sec );
		cal.set( Calendar.MILLISECOND, mls );
		return cal.getTimeInMillis();
	}

	public static Date timeOnly( Date dt )
	{
		dt.setTime( timeOnly( dt.getTime() ) );
		return dt;
	}

	/**
	 * Detects if date is set in the designated milliseconds
	 *
	 * @param date milliseconds from the epoch
	 * @return <code>true</code> if date(YEAR,MONTH and DAY) portion is set in
	 *         the milliseconds or <code>false</code> if not
	 */
	public static boolean isDateSet( long date )
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( date );
		return ( cal.get( Calendar.YEAR ) + cal.get( Calendar.MONTH ) + cal
				.get( Calendar.DAY_OF_MONTH ) ) != 0;
	}

	/**
	 * Returns only the today Date, no time components, beginning from HOUR and ending
	 * by MILLISECOND
	 *
	 * @return Date without time components (HOUR,MINUTE,SECOND,MILLISECOND)
	 */
	public static Date dateOnly()
	{
		return clearTime( new Date() );
	}

	/**
	 * Returns today date only, no time is present
	 * @return today date value
	 */
	public static Date today()
	{
		return dateOnly();
	}

	public static long todayMillis()
	{
		return dateOnly().getTime();
	}

	/**
	 * Returns the yesterday date, no time, that is 00:00:00 of yesterday
	 *
	 * @return Date instance for the start of yesterday
	 */
	public static Date yesterday()
	{
		return add( dateOnly(), Calendar.DAY_OF_MONTH, -1 );
	}

	/**
	 * Returns Date with a first day of the designated month
	 *
	 * @param year  year for the month
	 * @param month the month between 0-11
	 * @return Date with a beginning of the designated month
	 */
	public static Date firstDay( int year, int month )
	{
		return new Date( year, month, 1 );
	}

	/**
	 * returns the Date instance with a date of a last day of designated month
	 *
	 * @param year  the year for the month
	 * @param month the month between 0-11
	 * @return Date with an end of the designated month
	 */
	public static Date lastDay( int year, int month )
	{
		if ( month == 11 )
		{
			year++;
			month = 0;
		}
		Date date = new Date( year, month, 1 );
		// add month
		add( date, Calendar.MONTH, 1 );
		// and subtract one day
		return add( date, Calendar.DAY_OF_MONTH, -1 );
	}

	public static long dateOnly( long date )
	{
		return clearTime( date );
	}

	public static Date dateOnly( Date dt )
	{
		dt.setTime( dateOnly( dt.getTime() ) );
		return dt;
	}

	/**
	 * Calculates the difference in days between two dates
	 *
	 * @param earlier Earlier date
	 * @param older   Older date
	 * @return int integer difference in days between this two dates: Earlier -
	 *         Older
	 */
	public static int diffInDays( Date earlier, Date older )
	{
		return days( earlier ) - days( older );
	}

	/**
	 * Calculates the difference in days between two dates represented in
	 * milliseconds
	 *
	 * @param earlier Earlier date in milliseconds
	 * @param older   Older date in milliseconds
	 * @return int integer difference in days between this two dates: Earlier -
	 *         Older
	 */
	public static int diffInDays( long earlier, long older )
	{
		return days( earlier ) - days( older );
	}

	/**
	 * Detects if designated date is a today one
	 *
	 * @param dt Date to test be a today one
	 * @return <code>true</code> if the designated Date is a today one
	 */
	public static boolean isToday( Date dt )
	{
		return diffInDays( dt, now() ) == 0;
	}

	/**
	 * Detects if designated time is a today one
	 *
	 * @param time time to test be a today one
	 * @return <code>true</code> if the designated time is a today one
	 */
	public static boolean isToday( long time )
	{
		return diffInDays( time, now().getTime() ) == 0;
	}

	/**
	 * Detects if designated date is a yesterday one
	 *
	 * @param dt Date to test be a today one
	 * @return <code>true</code> if the designated Date is a today one
	 */
	public static boolean isYesterday( Date dt )
	{
		return diffInDays( dt, now() ) == -1;
	}

	/**
	 * Detects if designated time is a yesterday one
	 *
	 * @param time time to test be a today one. You can get time by call to a
	 *             {@link Date#getTime()}
	 * @return <code>true</code> if the designated time is a today one
	 */
	public static boolean isYesterday( long time )
	{
		return diffInDays( time, now().getTime() ) == -1;
	}

	/**
	 * Gets the days number after January 1, 1970 (Gregorian)
	 *
	 * @param dt date to get days
	 * @return days count for the date after January 1, 1970 (Gregorian)
	 */
	public static int days( Date dt )
	{
		return (int) ( clearTime(dt.getTime()) / DAY_IN_MILLIS );
	}

	/**
	 * Gets the days number after January 1, 1970 (Gregorian)
	 *
	 * @param millis long number of milliseconds to get days
	 * @return days count for the date after January 1, 1970 (Gregorian)
	 */
	public static int days( long millis )
	{
		return (int) ( millis / DAY_IN_MILLIS );
	}

	/**
	 * adds some value to any Date instance field
	 *
	 * @param dt    Date instance or add
	 * @param field <pre>
	 *              <p/>
	 *              {@link Calendar#DAY_OF_YEAR},
	 *              {@link Calendar#HOUR},
	 *              {@link Calendar#MINUTE},
	 *              {@link Calendar#SECOND}
	 *              </pre>
	 *              <p/>
	 *              If field is not in range nothing occur
	 * @param value value to add to the field.
	 * @return Date with value added or unchanged Date if illegal field was
	 *         designated
	 */
	public static Date add( Date dt, int field, int value )
	{
		dt.setTime( add( dt.getTime(), field, value ) );
		return dt;
	}

	/**
	 * Bumps the date for designated number of days
	 *
	 * @param dt    Date to bump by days. The input instance is changed by
	 *              designated day counter
	 * @param count number of days to bump (may be negative
	 * @return the Date with new value for days
	 */
	public static Date bumpTheDay( Date dt, int count )
	{
		return add( dt, Calendar.DAY_OF_MONTH, count );
	}

	/**
	 * Bumps the date for the designated number of days
	 *
	 * @param time  long time value to bump by days.
	 * @param count number of days to bump (may be negative for reducing time)
	 * @return the long value with new time
	 */
	public static long bumpTheDay( long time, int count )
	{
		return add( time, Calendar.DAY_OF_MONTH, count );
	}

	/**
	 * Increments the day by one
	 * @param time long value with time to increment it by day
	 * @return new value for the incremented day in millis
	 */
	public static long incTheDay( long time )
	{
		return time + DAY_IN_MILLIS;
	}

	/**
	 * Decrements the day by one
	 * @param time long value with time to decrement it by day
	 * @return new value for the decremented day in millis
	 */
	public static long decTheDay( long time )
	{
		return time - DAY_IN_MILLIS;
	}

	/**
	 * adds some value to any Date in the designated units after epoch
	 *
	 * @param time  time in milliseconds after epoch
	 * @param field <pre>
	 *              { all are same
	 *              {@link Calendar#DAY_OF_YEAR},
	 *              {@link Calendar#DAY_OF_MONTH},
	 *              {@link Calendar#DAY_OF_WEEK},
	 *              }
	 *              {@link Calendar#HOUR},
	 *              {@link Calendar#MINUTE},
	 *              {@link Calendar#SECOND}
	 *              </pre>
	 *              <p/>
	 *              If field is not in range nothing occur
	 * @param value value in milliseconds to add to the field of the time.
	 * @return new milliseconds with value added or unchanged time if illegal
	 *         field was designated
	 */
	public static long add( long time, int field, int value )
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( time );
		cal.add( field, value );
		return cal.getTimeInMillis();
	}

	public static void main( String[] args )
	{
/*		if ( false )
		{
			Date dt = parseDate( "2008-6-23 10:11:12.134" );
			System.out.println( "whole " + date2StdString( dt ) );

			Date dt1 = parseDate( "2008-6-23 10:11" );
			System.out.println( "only minutes " + date2StdString( dt1 ) );

			Date dt2 = parseDate( "2008-6-23 10" );
			System.out.println( "only hours " + date2StdString( dt2 ) );

			Date dt3 = parseDate( " 2008-6-23 " );
			System.out.println( "only date, no time " + date2StdString( dt2 ) );

			try
			{
				Date dt4 = parseDate( " 2008-23 8:7:6.5 " );
				System.out.println( "only date, no time " + date2StdString( dt2 ) );
			}
			catch ( Exception ex )
			{
				System.out.println( "Error:" + ex.getMessage() );
			}

			try
			{
				Date dt5 = parseDate( " -23-24 8:7:6.5 " );
				System.err.println( "only date, no time " + date2StdString( dt2 ) );
			}
			catch ( Exception ex )
			{
				System.err.println( "Error:" + ex.getMessage() );
			}
		}*/
		if ( true )
		{
			Date dtoday = new Date();
			long ltoday = dtoday.getTime();
			int today1 = DateTime.days( dtoday );
			int today2 = DateTime.days( ltoday );
			Date dt = new Date( ltoday );
			Date only_date_today = DateTime.clearTime( dt );
			long lonly_date_today = DateTime.clearTime( ltoday );
			int days1 = DateTime.days( dtoday );
			int days2 = DateTime.days( only_date_today );
			long diff = ltoday - lonly_date_today;
			long test = lonly_date_today + diff;
			int days3 = DateTime.days( test );

			Calendar cal= Calendar.getInstance();
			cal.setTimeInMillis( ltoday );
			Date dcal = cal.getTime();


			long time = new Date().getTime();

		}

	}
}
