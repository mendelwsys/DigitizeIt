/**
 * 
 */
package ru.ts.gisutils.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author sygsky
 * 
 */
public class Sys
{
	/**
	 * Returns 1 if the number is positive, -1 if the number is negative, and 0
	 * otherwise
	 * 
	 * @param i
	 *            The integer to examine.
	 * @return The integer's sign.
	 */
	public static int signum( int i )
	{
		// HD, Section 2-7
		return ( i >> 31 ) | ( -i >>> 31 );
	}

	/**
	 * Returns 1 if the number is positive, -1 if the number is negative, and 0
	 * otherwise
	 * 
	 * @param d
	 *            double value to examine.
	 * @return The integer's sign.
	 */
	public static int signum( double d )
	{
		if ( d < 0.0 )
			return -1;
		else if ( d > 0.0 )
			return 1;
		return 0;
	}

	public static int signum( long l )
	{
		// HD, Section 2-7
		return (int) ((l >> 63) | (-l >>> 63));
	}

	/**
	 * method detects if dValue have only integer part
	 * 
	 * @param dValue -
	 *            tested value
	 * @return true if only integer part presented, else false
	 */
	public static boolean isrounded( double dValue )
	{
		return isaligned( dValue );
	}

	/**
	 * method detects if the d is equal to mathematic integer
	 * 
	 * @param d - tested value
	 * @return <code>true</code> if в is equal to mathematical integer, else
	 *         <code>false</code> if В has fractional floating part
	 */
	public static boolean isaligned( double d )
	{
		return d == Math.floor( d );
	}

	/**
	 * gets host name of the current computer
	 * 
	 * @return String name of this computer name
	 */
	public static String getHostName()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch ( UnknownHostException ex )
		{
			return "";
		}
	}

	/**
	 * gets host address (IP) in the string form. E.g "10.20.3.120"
	 * 
	 * @return IP address in text form. E.g. "10.20.3.120"
	 */
	public static String getHostAddress()
	{
		try
		{
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch ( UnknownHostException ex )
		{
			return "";
		}
	}

	/**
	 * Detects if value is odd one
	 * @param value int value to test
	 * @return {@code true} if value is odd, else {@code false}
	 */
	public static boolean isOdd( int value )
	{
		return (value & 0x1) != 0;
	}

	/**
	 * Detects if value is even one
	 * @param value int value to test
	 * @return {@code true} if value is even, else {@code false}
	 */
	public static boolean isEven( int value )
	{
		return (value & 0x1) == 0;
	}

	/**
	 * Detects if value is odd one
	 * @param value long value to test
	 * @return {@code true} if value is odd, else {@code false}
	 */
	public static boolean isOdd( long value )
	{
		return (value & 0x1L) != 0;
	}

	/**
	 * Detects if value is even one
	 * @param value long value to test
	 * @return {@code true} if value is even, else {@code false}
	 */
	public static boolean isEven( long value )
	{
		return (value & 0x1L) == 0;
	}

	/**
	 * Rounds a number to the specified number of decimal places. This is
	 * particularly useful for simple display formatting. If you want to round an
	 * number to the nearest integer, it is better to use {@link Math#round}, as
	 * that will return an {@link Integer} rather than a {@link Double}.
	 * 
	 * @param decimals
	 *            the number of decimal places (may be negative, zero or
	 *            positive
	 * @param num
	 *            the number to round
	 * @return the value rounded to the specified number of decimal places to
	 *         left or right side. If decimals == 0, the
	 *         {@link java.lang.Math#floor(double)} is returned
	 */
	public static double roundTo( int decimals, double num )
	{
		if ( decimals == 0 )
			return Math.floor( decimals );
		double delta = 10;
		for ( int j = 1; j < Math.abs( decimals ); j++ )
			delta *= 10.0;
		if ( decimals < 0 )
			return Math.round( num / delta ) * delta;
		else
			return Math.round( num * delta ) / delta;
	}

	/**
	 * gets the Java VM version
	 * @return String with a Java VM version, for example, "1.1.2" or "1.5.0_05"
	 */
	public static String javaVersion()
	{
		return System.getProperty("java.version");
	}

}
