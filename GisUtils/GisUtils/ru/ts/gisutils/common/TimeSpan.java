/**
 * 
 */
package ru.ts.gisutils.common;

import ru.ts.utils.DateTime;

import java.util.Date;
import java.util.Calendar;

/*
 * TimeSpan.java
 * 
 * Created on January 28, 2003, 11:09 AM
 * ====================================================================
 * 
 * The JavaRanch Software License, Version 1.0
 * 
 * Copyright (c) 2003 JavaRanch. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The name JavaRanch must not be used to endorse or promote products derived
 * from this software without prior written permission.
 * 
 * 4. Products derived from this software may not be called "JavaRanch" nor may
 * "JavaRanch" appear in their names without prior written permission of
 * JavaRanch.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JAVARANCH
 * OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 */

/**
 * The value of an instance of TimeSpan represents a period of time. TimeSpan
 * can be used in several ways. To calculate the difference in time between two
 * dates:
 * 
 * <PRE>
 * TimeSpan timespan = TimeSpan.subtract( date1, date2 );
 * 
 * </PRE>
 * 
 * To subtract another TimeSpan object from this one:
 * 
 * <PRE>
 * 
 * timespan.subtract( timespan2 );
 * 
 * </PRE>
 * 
 * @author Thomas Paul, modified by Sygsky
 */
public class TimeSpan implements Comparable, java.io.Serializable, Cloneable
{
	/*
	 * Just for fun :o)
	 */
	private static final long	 serialVersionUID	= 1L;	                // Let
																			// it
																			// be
																			// 1st
																			// version

	/*
	 * private static final Log LOG = LogFactory.getLog(TimeSpan.class);
	 */
	/** Represents the Maximum TimeSpan value */
	public static final TimeSpan	MAX_VALUE	  = new TimeSpan(
	                                                      Long.MAX_VALUE );

	/** Represents the Minimum TimeSpan value */
	public static final TimeSpan	MIN_VALUE	  = new TimeSpan(
	                                                      Long.MIN_VALUE );

	/** Represents the TimeSpan with a value of zero */
	public static final TimeSpan	ZERO	      = new TimeSpan( 0L );

	private long	             time	          = 0;

	/**
	 * Creates a new instance of TimeSpan based on the number of milliseconds
	 * entered.
	 * 
	 * @param time
	 *            the number of milliseconds for this TimeSpan.
	 * 
	 */
	public TimeSpan( long time )
	{
		this.time = time;
	}

	/**
	 * Initialise time span with a current date time
	 */
	public TimeSpan()
	{
		this.time = DateTime.nowMillis();
	}

	/**
	 * Creates a new instance of TimeSpan based on the difference between two
	 * Dates entered.
	 * 
	 * @param date1
	 *            the first (later) date from which the second one will be
	 *            subtracted.
	 * @param date2
	 *            the second (earlier) date which will be subtracted from first
	 *            one.
	 */
	public TimeSpan( Date date1, Date date2 )
	{
		this.time = date1.getTime() - date2.getTime();
	}

	/**
	 * creates new instance of a TimeSpan based on difference between current
	 * Date and indicated one
	 * 
	 * @param dt
	 *            Date to subtract from current one
	 */
	public TimeSpan( Date dt )
	{
		this.time = DateTime.nowMillis() - dt.getTime();
	}

	/**
	 * Compares two TimeSpan objects.
	 * 
	 * @param first
	 *            first TimeSpan to use in the compare.
	 * @param second
	 *            second TimeSpan to use in the compare.
	 * 
	 * @return a negative integer, zero, or a positive integer as the first
	 *         TimeSpan is less than, equal to, or greater than the second
	 *         TimeSpan.
	 * 
	 */
	public static int compare( TimeSpan first, TimeSpan second )
	{
		return Sys.signum( first.time - second.time );
	}

	/**
	 * Subtracts two Date objects creating a new TimeSpan object.
	 * 
	 * @param date1
	 *            Date to use as the base value.
	 * @param date2
	 *            Date to subtract from the base value.
	 * @return a TimeSpan object representing the difference bewteen the 122 *
	 *         two Date objects.
	 */
	public static TimeSpan subtract( Date date1, Date date2 )
	{
		return new TimeSpan( date1.getTime() - date2.getTime() );
	}

	/**
	 * Gets the number of days (truncated).
	 * 
	 * @return the number of days.
	 */
	public long getDays()
	{
		return this.time / TimeSpanConstants.DAYS;
	}

	/**
	 * Gets the number of hours (truncated).
	 * 
	 * @return the number of hours.
	 */
	public long getHours()
	{
		return this.time / TimeSpanConstants.HOURS;
	}

	/**
	 * Gets the number of milliseconds.
	 * 
	 * @return the number of milliseconds.
	 */
	public long getMilliseconds()
	{
		return this.time;
	}

	/**
	 * Gets the number of minutes (truncated).
	 * 
	 * @return the number of minutes.
	 */
	public long getMinutes()
	{
		return this.time / TimeSpanConstants.MINUTES;
	}

	/**
	 * Gets the number of seconds (truncated).
	 * 
	 * @return the number of seconds.
	 */
	public long getSeconds()
	{
		return this.time / TimeSpanConstants.SECONDS;
	}

	/**
	 * Gets the number of days including fractional days.
	 * 
	 * @return the number of days. 208
	 */
	public double getTotalDays()
	{
		return ( ( ( this.time / 1000.0d ) / 60.0d ) / 60.0d ) / 24.0d;
	}

	/**
	 * Gets the number of hours including fractional hours.
	 * 
	 * @return the number of hours.
	 */
	public double getTotalHours()
	{
		return ( ( this.time / 1000.0d ) / 60.0d ) / 60.0d;
	}

	/**
	 * Gets the number of minutes including fractional minutes.
	 * 
	 * @return the number of minutes.
	 */
	public double getTotalMinutes()
	{
		return ( this.time / 1000.0d ) / 60.0d;
	}

	/**
	 * Gets the number of seconds including fractional seconds.
	 * 
	 * @return the number of seconds.
	 */
	public double getTotalSeconds()
	{
		return this.time / 1000.0d;
	}

	public long getOnlyDays()
	{
		return getDays();
	}

	public int getOnlyHours()
	{
		return (int) ( ( this.time - getDays() * TimeSpanConstants.DAYS ) / TimeSpanConstants.HOURS );
	}

	public int getOnlyMinutes()
	{
		long res = this.time - getDays() * TimeSpanConstants.DAYS;
		res -= ( res / TimeSpanConstants.HOURS ) * TimeSpanConstants.HOURS;
		return (int) ( res / TimeSpanConstants.MINUTES );
	}

	public int getOnlySeconds()
	{
		long res = this.time - getDays() * TimeSpanConstants.DAYS;
		res -= ( res / TimeSpanConstants.HOURS ) * TimeSpanConstants.HOURS;
		res -= ( res / TimeSpanConstants.MINUTES ) * TimeSpanConstants.MINUTES;
		return (int) ( res / TimeSpanConstants.SECONDS );
	}

	public int getOnlyMilliseconds()
	{
		long res = this.time - getDays() * TimeSpanConstants.DAYS;
		res -= ( res / TimeSpanConstants.HOURS ) * TimeSpanConstants.HOURS;
		res -= ( res / TimeSpanConstants.MINUTES ) * TimeSpanConstants.MINUTES;
		res -= ( res / TimeSpanConstants.SECONDS ) * TimeSpanConstants.SECONDS;
		return (int) res;
	}

	/**
	 * converts TimeSpan to Date structure. Of course, it returns very special
	 * Date object as it contains maximum only days of month, not of year and
	 * years itself
	 * 
	 * @return new Date object with all the elements set according to the
	 *         TimeSpan values
	 */
	public Date convert2Date()
	{
		Calendar cal = Calendar.getInstance();
		cal.clear();
		TimeSpan span = (TimeSpan) this.clone();
		long days = span.getDays();
		cal.set( Calendar.DAY_OF_MONTH, (int) days );
		span.subtract( days * TimeSpanConstants.DAYS );

		long hours = span.getHours();
		cal.set( Calendar.HOUR_OF_DAY, (int) hours );
		span.subtract( hours * TimeSpanConstants.HOURS );

		long minutes = span.getMinutes();
		cal.set( Calendar.MINUTE, (int) minutes );
		span.subtract( minutes * TimeSpanConstants.MINUTES );

		long seconds = span.getSeconds();
		cal.set( Calendar.SECOND, (int) seconds );
		span.subtract( seconds * TimeSpanConstants.SECONDS );
		long mils = span.getMilliseconds();
		cal.set( Calendar.MILLISECOND, (int) mils );

		return cal.getTime();
	}

	/**
	 * Indicates whether the value of the TimeSpan is negative.
	 * 
	 * @return <code>true</code> if the value of the TimeSpan is less than
	 *         zero. <code>false</code> otherwise.
	 */
	public boolean isNegative()
	{
		return this.time < 0;
	}

	/**
	 * Indicates whether the value of the TimeSpan is positive.
	 * 
	 * @return <code>true</code> if the value of the TimeSpan is greater than
	 *         zero. <code>false</code> otherwise.
	 */
	public boolean isPositive()
	{
		return this.time >= 0;
	}

	/**
	 * Indicates whether the value of the TimeSpan is zero.
	 * 
	 * @return <code>true</code> if the value of the TimeSpan is equal to
	 *         zero. <code>false</code> otherwise.
	 */
	public boolean isZero()
	{
		return this.time == 0;
	}

	/**
	 * Adds a TimeSpan to this TimeSpan.
	 * 
	 * @param timespan
	 *            the TimeSpan to add to this TimeSpan.
	 */
	public void add( TimeSpan timespan )
	{
		add( timespan.time );
	}

	/**
	 * Adds a number of milliseconds to this TimeSpan.
	 * 
	 * @param value
	 *            the number of milliseconds to add to this TimeSpan.
	 */
	public void add( long value )
	{
		this.time += value;
	}

	/**
	 * Returns a clone of this TimeSpan.
	 * 
	 * @return a clone of this TimeSpan. Returns null i� clone is not supported
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch ( CloneNotSupportedException ex )
		{
			return null;
		}
	}

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object. Comparison is based
	 * on the number of milliseconds in this TimeSpan.
	 * 
	 * @param o
	 *            the Object to be compared.
	 * 
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 * 
	 * @throws ClassCastException
	 *             if the specified object's type prevents it from being
	 *             compared to this Object.
	 * 
	 */
	public int compareTo( Object o )
	{
		long diff = this.time - ( (TimeSpan) o ).time;
		if ( diff > 0 )
			return +1;
		if ( diff < 0 )
			return -1;
		return 0;
	}

	/**
	 * Returns a TimeSpan whose value is the absolute value of this TimeSpan.
	 * 
	 * @return a TimeSpan whose value is the absolute value of this TimeSpan.
	 * 
	 */
	public TimeSpan duration()
	{
		return new TimeSpan( Math.abs( this.time ) );
	}

	/**
	 * Indicates whether some other object is "equal to" this one. Comparison is
	 * based on the number of milliseconds in this TimeSpan.
	 * 
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> if the obj argument is a TimeSpan object with
	 *         the exact same number of milliseconds. <code>false</code>
	 *         otherwise.
	 */
	public boolean equals( Object obj )
	{
		if ( obj instanceof TimeSpan )
		{
			TimeSpan compare = (TimeSpan) obj;
			if ( this.time == compare.time )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a hash code value for the object. This method is supported for
	 * the benefit of hashtables such as those provided by
	 * <code>java.util.Hashtable</code>. The method uses the same algorithm
	 * as found in the Long class.
	 * 
	 * @return a hash code value for this object.
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @see java.util.Hashtable
	 */
	public int hashCode()
	{
		return new Long( this.time ).hashCode();
	}

	/**
	 * Returns a TimeSpan whose value is the negated value of this TimeSpan.
	 * 
	 * @return a TimeSpan whose value is the negated value of this TimeSpan.
	 */
	public TimeSpan negate()
	{
		return new TimeSpan( -this.time );
	}

	/**
	 * Subtracts a TimeSpan from this TimeSpan.
	 * 
	 * @param timespan
	 *            the TimeSpan to subtract from this TimeSpan.
	 */
	public void subtract( TimeSpan timespan )
	{
		subtract( timespan.time );
	}

	/**
	 * Subtracts a number of units from this TimeSpan.
	 * 
	 * @param value
	 *            the number of units to subtract from this TimeSpan.
	 */
	public void subtract( long value )
	{
		add( -value );
	}

	/**
	 * Returns a string representation of the object in the format
	 * "[-]d hh:mm:ss.ff" where "-" is an optional sign for negative TimeSpan
	 * values, the "d" component is days, "hh" is hours, "mm" is minutes, "ss"
	 * is seconds, and "ff" is milliseconds
	 * 
	 * @return a string containing the number of milliseconds.
	 * 
	 */
	public String toString()
	{
		return toString( Calendar.MILLISECOND );
	}

	/**
	 * the same as {@link TimeSpan#toString()} except of parameter
	 * 
	 * @param stop_at
	 *            as a constants use {@link Calendar#MINUTE}, or SECOND to stop
	 *            text generation after minutes or seconds output.
	 * @return
	 */
	public String toString( int stop_at )
	{
		StringBuffer sb = new StringBuffer();
		long millis = this.time;

		// set sign if any
		if ( millis < 0 )
		{
			sb.append( '-' );
			millis = -millis;
		}
		// set days if any (no padding)
		long day = millis / TimeSpanConstants.DAYS;
		if ( day != 0 )
		{
			sb.append( Long.toString( day ) );
			sb.append( "d " );
			millis = millis % TimeSpanConstants.DAYS;
		}

		// set hours (always) - left pad with zero
		String str = Long.toString( millis / TimeSpanConstants.HOURS );
		int strlen = str.length();
		for ( ; strlen < 2; strlen++ )
			sb.append( "0" );
		sb.append( str );
		millis = millis % TimeSpanConstants.HOURS;
		sb.append( ":" );

		// set minutes (always)- left pad with zero
		str = Long.toString( millis / TimeSpanConstants.MINUTES );
		strlen = str.length();
		for ( ; strlen < 2; strlen++ )
			sb.append( "0" );
		sb.append( str );
		if ( stop_at == Calendar.MINUTE ) // stop after minutes
			return sb.toString();
		millis = millis % TimeSpanConstants.MINUTES;
		sb.append( ":" );

		// set seconds (always)- left pad with zero
		str = Long.toString( millis / TimeSpanConstants.SECONDS );
		strlen = str.length();
		for ( ; strlen < 2; strlen++ )
			sb.append( "0" );
		sb.append( str );

		if ( stop_at == Calendar.SECOND ) // stop after minutes
			return sb.toString();

		// set milliseconds - no padding
		millis = millis % TimeSpanConstants.SECONDS;
		if ( millis != 0 )
		{
			sb.append( '.' );
			sb.append( Long.toString( millis ) );
		}
		return sb.toString();
	}

	/**
	 * Gets the time.
	 * 
	 * @return Returns the time.
	 */
	public long getTime()
	{
		return this.time;
	}

	/**
	 * Re-initialise the internal value of the start time to zero
	 */
	public void reset()
	{
		time = 0L;
	}

	/**
	 * Sets time span between previously set start time and current time
	 */
	public void setTimeSpan()
	{
		time = DateTime.nowMillis() - time;
	}

	/**
	 * Sets a new time span.
	 * 
	 * @param aTime
	 *            The time to set.
	 */
	public void setTime( long aTime )
	{
		this.time = aTime;
	}

	interface TimeSpanConstants
	{

		/** Constant for milliseconds unit and conversion */
		long	MILLISECONDS	= 1;

		/** Constant for seconds unit and conversion */
		long	SECONDS		 = MILLISECONDS * 1000;

		/** Constant for minutes unit and conversion */
		long	MINUTES		 = SECONDS * 60;

		/** Constant for hours unit and conversion */
		long	HOURS		 = MINUTES * 60;

		/** Constant for days unit and conversion */
		long	DAYS		 = HOURS * 24;
	}
}
