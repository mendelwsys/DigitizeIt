/**
 *
 */
package reclass.cmd;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Random;
import java.util.UUID;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.DigestException;
import java.lang.reflect.Field;
import java.awt.*;

/**
 * @author sygsky
 */
public class Sys
{
	private static final double M = 1.0 / Math.log( 10.0 );

	static Random m_rnd;

	static
	{
		m_rnd = new Random();
	}

	/**
	 * Returns  internally used {@link java.util.Random} instance
	 *
	 * @return
	 */
	public static Random getStaticRandom()
	{
		return m_rnd;
	}

	/**
	 * Returns 1 if the number is positive, -1 if the number is negative, and 0
	 * otherwise
	 *
	 * @param i The integer to examine.
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
	 * @param i The byte to examine.
	 * @return The byte's sign.
	 */
	public static int signum( byte i )
	{
		// HD, Section 2-7
		return ( i >> 7 ) | ( -i >>> 7 );
	}

	/**
	 * Returns 1 if the number is positive, -1 if the number is negative, and 0
	 * otherwise
	 *
	 * @param d double value to examine.
	 * @return The integer's sign.
	 */
	public static int signum( double d )
	{
		if ( d < 0.0 )
		{
			return -1;
		}
		else if ( d > 0.0 )
		{
			return 1;
		}
		return 0;
	}

	public static int signum( long l )
	{
		// HD, Section 2-7
		return (int) ( ( l >> 63 ) | ( -l >>> 63 ) );
	}

	/**
	 * method detects if dValue have only integer part
	 *
	 * @param dValue - tested value
	 * @return true if only integer part presented, else false
	 */
	public static boolean isrounded( double dValue )
	{
		return isaligned( dValue );
	}

	/**
	 * method detects if the d is equal to mathematical integer
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
	 *
	 * @param value int value to test
	 * @return {@code true} if value is odd, else {@code false}
	 */
	public static boolean isOdd( int value )
	{
		return ( value & 0x1 ) != 0;
	}

	/**
	 * Detects if value is even one
	 *
	 * @param value int value to test
	 * @return {@code true} if value is even, else {@code false}
	 */
	public static boolean isEven( int value )
	{
		return ( value & 0x1 ) == 0;
	}

	/**
	 * Detects if value is odd one
	 *
	 * @param value long value to test
	 * @return {@code true} if value is odd, else {@code false}
	 */
	public static boolean isOdd( long value )
	{
		return ( value & 0x1L ) != 0;
	}

	/**
	 * Detects if value is even one
	 *
	 * @param value long value to test
	 * @return {@code true} if value is even, else {@code false}
	 */
	public static boolean isEven( long value )
	{
		return ( value & 0x1L ) == 0;
	}

	/**
	 * Rounds a number to the specified number of decimal places. This is
	 * particularly useful for simple display formatting. If you want to round an
	 * number to the nearest integer, it is better to use {@link Math#round}, as
	 * that will return an {@link Integer} rather than a {@link Double}. Works only
	 * for number > 0.0
	 *
	 * @param decimals the number of decimal places (may be negative for after
	 *                 point rounding, zero or positive for before point rounding)
	 * @param num      the number to round
	 * @return the value rounded to the specified number of decimal places to left
	 *         or right side. If decimals == 0, the {@link Math#floor(double)}
	 *         is returned
	 */
	public static double roundTo( int decimals, double num )
	{
		if ( decimals == 0 )
		{
			return Math.floor( num );
		}
		double delta = 10;
		delta = Math.pow( 10, decimals );
		return Math.round( num / delta ) * delta;
	}

	/**
	 * Rounds value to the nearest designated step
	 *
	 * @param value double value to round to. For example:<pre>
	 * roundTo(3.5, 2.5) == 2.5;<br>
	 * roundTo(4.5, 2.5) == 5.0;<br>
	 * roundTo(5.5, 2.5) == 5.0;<br>
	 * roundTo(6.5, 2.5) == 7.5; etc</pre>
	 * @param step  double step of rounding
	 * @return double with rounded result
	 */
	public static double roundTo( double value, double step )
	{
		return Math.floor( ( value + step / 2.0 ) / step ) * step;
	}

	public static double roundSignificants( double val, int cnt )
	{
		String fmt = ( new StringBuffer().append( "%." ).append( cnt ).append( 'f' ) ).toString();
		double dpow = Math.floor( lg( val ) + 1.0 );
		double tmp = Math.pow( 10.0, -dpow );
		String res = MessageFormat.format( fmt, new Object[] { new Double( val * tmp ) } );
		res = res.replace( ',', '.' );
		double dres = Double.parseDouble( res );
		return dres / tmp;
	}

	/**
	 * gets the Java VM version
	 *
	 * @return String with a Java VM version, for example, "1.1.2" or "1.5.0_05"
	 */
	public static String javaVersion()
	{
		return System.getProperty( "java.version" );
	}

	/**
	 * gets the current OS version
	 *
	 * @return String with a OS name, e.g. "Mac OS", "Windows XP", "Windows 7",
	 *         "LINUX" etc. Or "not specified" on some inconsistency
	 */
	public static String javaOSName()
	{
		return System.getProperty( "os.name", "not specified" );
	}

	/**
	 * Returns {@code true} if the VM running on Windows :o(
	 *
	 * @return {@code true} if if the VM running on Windows :o( or {@code false}
	 *         if on other system, mostly probable some Unix clone (Linux)
	 */
	public static boolean isOnWindows()
	{
		return javaOSName().toUpperCase().startsWith( "WIN" );
	}

	/**
	 * Returns {@code true} if {@code val} is power of two
	 *
	 * @param val int to be tested for power of two
	 * @return {@code true} if {@code val} is power of two, that is 1,2,4,8...
	 */
	public static boolean isPowerOf2( int val )
	{
		return ( val & -val ) == val;
	}

	/**
	 * Returns {@code true} if {@code val} is power of two
	 *
	 * @param val short to be tested for power of two
	 * @return {@code true} if {@code val} is power of two, that is 1,2,4,8...
	 */
	public static boolean isPowerOf2( short val )
	{
		return ( val & -val ) == val;
	}

	/**
	 * Returns {@code true} if {@code val} is power of two
	 *
	 * @param val long to be tested for power of two
	 * @return {@code true} if {@code val} is power of two, that is 1,2,4,8...
	 */
	public static boolean isPowerOf2( long val )
	{
		return ( val & -val ) == val;
	}

	/**
	 * Common (десятичный) logarithm of the user value
	 *
	 * @param val double with user value to get logarithm for it
	 * @return double value of the found common (десятичный) logarithm
	 */
	public static double lg( double val )
	{
		return Math.log( val ) * M;
	}

	/**
	 * Reverses the array contents
	 *
	 * @param vals int[] to reverse totaly, so to change elements order
	 */
	public static void reverse( final int[] vals )
	{
		int tmp;
		for ( int i = 0, j = vals.length - 1; i < vals.length / 2; i++ )
		{
			tmp = vals[ j ];
			vals[ j-- ] = vals[ i ];
			vals[ i ] = tmp;
		}
	}

	/**
	 * Reverses the array contents
	 *
	 * @param vals double[] to reverse totaly, so to change elements order
	 */
	public static void reverse( final double[] vals )
	{
		double tmp;
		for ( int i = 0, j = vals.length - 1; i < vals.length / 2; i++ )
		{
			tmp = vals[ j ];
			vals[ j-- ] = vals[ i ];
			vals[ i ] = tmp;
		}
	}

	public static int compare( final Comparable obj1, final Comparable obj2 )
	{
		if ( obj1 == null )
		{
			return ( obj2 == null ) ? 0 : -1;
		}
		return ( obj2 == null ) ? +1 : obj1.compareTo( obj2 );
	}

	public static boolean equals( final Object obj1, final Object obj2 )
	{
		return obj1 == null ? obj2 == null : obj1.equals( obj2 );
	}


	/**
	 * Checks if input value is integer value text representation
	 *
	 * @param value String with a value to check to be integer
	 * @return {@code true} if value is correct or {@code false} if not
	 */
	public static boolean isInt( String value )
	{
		try
		{
			Integer.parseInt( value );
			return true;
		}
		catch ( NumberFormatException e )
		{
			return false;
		}
	}

	/**
	 * Not sure that is will faster than use of temp variable
	 *
	 * @param x firts value to exchange
	 * @param y secod value to exchange
	 */
	public static void xorSwap( int x, int y )
	{
		x ^= y;
		y ^= x;
		x ^= y;
	}

	public static UUID randomUUID()
	{
		return UUID.randomUUID();
	}

	public static UUID nameUUID( byte[] name )
	{
		return UUID.nameUUIDFromBytes( name );
	}

	public static String UUIDString( byte[] name )
	{
		return UUID.nameUUIDFromBytes( name ).toString();
	}

	/**
	 * get SHA digest
	 *
	 * @param bytes byte[] to digest
	 * @return byte[] with a designad digest
	 * @throws java.security.NoSuchAlgorithmException
	 *          on SHA algoritm absence
	 */
	public static byte[] MD5( byte[] bytes ) throws NoSuchAlgorithmException, DigestException
	{
		return getDigest( bytes, "MD5", bytes.length );
	}

	/**
	 * get user designated digest
	 *
	 * @param bytes    byte[] to digest
	 * @param algoName algorithm name, see {@ref http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html}
	 * @param len
	 * @return byte[] with a designad digest
	 * @throws java.security.NoSuchAlgorithmException
	 *          on bad algoritm name
	 */
	public static byte[] getDigest( byte[] bytes, String algoName, int len ) throws NoSuchAlgorithmException, DigestException
	{
		final MessageDigest inst = MessageDigest.getInstance( algoName );
		inst.digest( bytes, 0, len );
		return inst.digest();
	}

	/**
	 * get SHA digest
	 *
	 * @param bytes byte[] to digest
	 * @return byte[] with a designad digest
	 * @throws java.security.NoSuchAlgorithmException
	 *          on SHA algoritm absence
	 */
	public static byte[] SHA( byte[] bytes ) throws NoSuchAlgorithmException, DigestException
	{
		return getDigest( bytes, "SHA", bytes.length );
	}

	/**
	 * Adds the specified path to the java library path
	 *
	 * @param pathToAdd the path to add
	 * @throws Exception
	 */
	public static boolean addLibraryPath( String pathToAdd )
	{
		try
		{
			final Field usrPathsField = ClassLoader.class.getDeclaredField( "usr_paths" );
			usrPathsField.setAccessible( true );

			//get array of paths
			final String[] paths = (String[]) usrPathsField.get( null );

			//check if the path to add is already present
			for ( String path : paths )
			{
				if ( path.equals( pathToAdd ) )
				{
					return true;
				}
			}
			//add the new path
			final String[] newPaths = Arrays.copyOf( paths, paths.length + 1 );
			newPaths[ newPaths.length - 1 ] = pathToAdd;
			usrPathsField.set( null, newPaths );
			return true;
		}
		catch ( Exception e )
		{
			return false;
		}

	}

	public static boolean addJavaLibraryPath( String path )
	{
		//
		// This enables the java.library.path to be modified at runtime
		// From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
		//
		Field field = null;
		try
		{
			field = ClassLoader.class.getDeclaredField( "usr_paths" );
		}
		catch ( NoSuchFieldException e )
		{
			e.printStackTrace();
			return false;
		}
		field.setAccessible( true );
		String[] paths;
		try
		{
			paths = (String[]) field.get( null );
		}
		catch ( IllegalAccessException e )
		{
			e.printStackTrace();
			return false;
		}
		for ( int i = 0; i < paths.length; i++ )
		{
			if ( path.equalsIgnoreCase( paths[ i ] ) )
			{
				return true;
			}
		}

		final String propName = "java.library.path";
		String syspath = System.getProperty( propName );
		if ( ( syspath != null && syspath.length() > 0) )
		{
			System.setProperty( propName, syspath + ";" + path );
		}
		else
		{
			System.setProperty( propName, path );
		}
		System.out.println( propName + " = " + syspath );
		System.setProperty( propName, syspath + ";" + path );
		Field fieldSysPath = null;
		try
		{
			fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
		}
		catch ( NoSuchFieldException e )
		{
			System.err.println( "Error to add new path to property java.library.pat: " + e.getMessage() );
			return false;
		}
		try
		{
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		}
		catch ( IllegalAccessException e )
		{
			System.err.println( "Can't access/set system field" );
			return false;
		}
		System.out.println( path + " is added" );
		return true;
	}

	public static boolean createShortcut()
	{
		return false;// todo
	}

	public static String rect2str( Rectangle rect )
	{
		return "[x=" + rect.x + ",y=" + rect.y + ",w=" + rect.width + ",h=" + rect.height + "]";
	}

	public static String JVMBitSizeStr()
	{
		return System.getProperty( "os.arch" );
	}

	public static String JVMBitSizeStrV2()
	{
		String str = System.getProperty( "sun.arch.data.model" );
		if ( str == null || str.length() == 0 )
		{
			str = System.getProperty( "com.ibm.vm.bitmode" );
		}
		return str;
	}


}