package ru.ts.utils;

/**
 * Created by IntelliJ IDEA. User: vladm Date: 14.08.2008 Time: 15:29:50 To
 * change this template use File | Settings | File Templates.
 */
public class FunctEmul
{
	public static int dsignum( double i )
	{
		if ( i < 0 )
			return -1;
		else if ( i > 0 )
			return 1;
		return 0;
	}

	/**
	 * 
	 * @param i
	 *            an integer value to detect its signum function
	 * @return the signum function of the specified <tt>int</tt> value. (The
	 *         return value is -1 if the specified value is negative; 0 if the
	 *         specified value is zero; and 1 if the specified value is
	 *         positive.)
	 */
	public static int isignum( int i )
	{
		// HD, Section 2-7
		return ( i >> 31 ) | ( -i >>> 31 );
	}
	/**
	 * 
	 * @param i
	 *            a long value to detect its signum function
	 * @return the signum function of the specified <tt>int</tt> value. (The
	 *         return value is -1 if the specified value is negative; 0 if the
	 *         specified value is zero; and 1 if the specified value is
	 *         positive.)
	 */
	public static int isignum( long i )
	{
		// HD, Section 2-7
		return (int) ((i >> 63) | (-i >>> 63));
	}

	/**
	 * 
	 * @param i
	 *            a byte value to detect its signum function
	 * @return the signum function of the specified <tt>int</tt> value. (The
	 *         return value is -1 if the specified value is negative; 0 if the
	 *         specified value is zero; and 1 if the specified value is
	 *         positive.)
	 */
	public static int isignum( byte i )
	{
		// HD, Section 2-7
		return (int)(( i >> 7 ) | ( -i >>> 7 ));
	}
	
	/**
	 * 
	 * @param i
	 *            a short value to detect its signum function
	 * @return the signum function of the specified <tt>int</tt> value. (The
	 *         return value is -1 if the specified value is negative; 0 if the
	 *         specified value is zero; and 1 if the specified value is
	 *         positive.)
	 */
	public static int isignum( short i )
	{
		// HD, Section 2-7
		return (int)(( i >> 15 ) | ( -i >>> 15 ));
	}

	/**
	 * 
	 * @param i
	 *            a short value to detect its signum function
	 * @return the signum function of the specified <tt>int</tt> value. (The
	 *         return value is -1 if the specified value is negative; 0 if the
	 *         specified value is zero; and 1 if the specified value is
	 *         positive.)
	 */
	public static int isignum( char i )
	{
		// HD, Section 2-7
		return (int)(( i >> 15 ) | ( -i >>> 15 ));
	}
	
	/**
	 * 
	 * @param i
	 *            the Integer class instance to detect its value signum
	 * @return the signum function for the specified Integer value
	 */
	public static int signum( Integer i )
	{
		return isignum( i.intValue() );
	}
}
