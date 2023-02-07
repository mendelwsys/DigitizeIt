/**
 * 
 */
package ru.ts.gisutils.common;

import java.util.Arrays;

/**
 * @author sygsky
 * 
 */
public class Arrs
{
	/**
	 * reverses input double array
	 * 
	 * @param arr
	 */
	public static void reverse( final double[] arr )
	{
		final int count = arr.length / 2;
		double d;
		for ( int i = 0, j = arr.length - 1; i < count; i++, j-- )
		{
			d = arr[ i ];
			arr[ i ] = arr[ j ];
			arr[ j ] = d;
		}
	}

	/**
	 * removes all duplicates from sorted array. If array is not sorted, result
	 * is unpredictable. No additional memory is allocated during this
	 * operation, except of resulting array.
	 * 
	 * @param arr
	 *            array of int[], sorted in any order (!)
	 * @return new int[] of unique sorted values in the same order as before. If
	 *         input array was already unique, the same array is returned.
	 */
	public static int[] unique( int arr[] )
	{
		if ( arr == null )
			return null;
		if ( arr.length == 0 )
			return arr;
		int outind = 1;
		int outval = arr[ 0 ];
		for ( int index = 1; index < arr.length; index++ )
			if ( outval != arr[ index ] )
				outval = arr[ outind++ ] = arr[ index ];

		if ( outind == arr.length )
			return arr;
		int[] ret = new int[outind];
		System.arraycopy( arr, 0, ret, 0, outind );
		return ret;
	}

	/**
	 * Detects if array in not <code>null</code> and its length > 0
	 * 
	 * @param arr
	 *            in[] to check
	 * @return <code>true</code> if array is not <code>null</code> and has
	 *         items
	 */
	public static boolean isEmpty( int arr[] )
	{
		return ( arr == null ) || ( arr.length == 0 );
	}

	/**
	 * Checks if array is sorted ascending
	 * 
	 * @param arr
	 *            not empty array
	 * @return <code>true</code> if array is not empty and is sorted in
	 *         ascending order, else <code>false</code>
	 */
	public static boolean isSortedAsc( int arr[] )
	{
		if ( isEmpty( arr ) )
			return false;
		int prev = arr[ 0 ];
		int curr;
		for ( int i = 1; i < arr.length; i++ )
		{
			if ( prev > ( curr = arr[ i ] ) )
				return false;
			prev = curr;
		}
		return true;
	}

	/**
	 * Checks if array is sorted descending
	 * 
	 * @param arr
	 *            not empty array
	 * @return <code>true</code> if array is not empty and is sorted in
	 *         descending order, else <code>false</code>
	 */
	public static boolean isSortedDesc( int arr[] )
	{
		if ( isEmpty( arr ) )
			return false;
		int prev = arr[ 0 ];
		int curr;
		for ( int i = 1; i < arr.length; i++ )
		{
			if ( prev < ( curr = arr[ i ] ) )
				return false;
			prev = curr;
		}
		return true;
	}

	/**
	 * Moves array elements to the end of array, extracting some ones, shifting
	 * all upper element to free space and append extraction to the end
	 * 
	 * @param src
	 *            int[] source array
	 * @param off
	 *            source offset of array part to move to the end
	 * @param len
	 *            length of part to move
	 * @return new array with the same elements but in a new orders
	 * @throws IndexOutOfBoundsException
	 *             if any parameter is out of range
	 */
	public static Object[] moveUp( Object[] src, int off, int len )
	        throws IndexOutOfBoundsException
	{
		if ( src == null )
			return null;
		if ( len == 0 )
			return src;
		int arrlen = src.length;
		if ( ( off >= arrlen ) || ( off < 0 ) )
			throw new IndexOutOfBoundsException( "offset > array length" );
		if ( off + len > arrlen )
			throw new IndexOutOfBoundsException(
			        "(offset + length) > array length" );
		if ( ( off + len ) == arrlen )
			return src;
		Object[] dst = (Object[]) src.clone();
		int outind = off;
		for ( int index = off + len; index < arrlen; index++ )
			dst[ outind++ ] = src[ index ];
		for ( int index = off; index < off + len; index++ )
			dst[ outind++ ] = src[ index ];
		return dst;
	}

	/**
	 * sets array to some value
	 * 
	 * @param arr
	 *            array to set items to the same value
	 * @param newval
	 *            value to set to all items
	 */
	public static void arraySet( float[] arr, float newval )
	{
		Arrays.fill( arr, newval );
	}

	/**
	 * adds all array items to a some value
	 * 
	 * @param arr
	 *            array to adds items to the same value
	 * @param add
	 *            value to adds to all items
	 */
	public static void arrayAdd( float[] arr, float add )
	{
		for ( int index = 0; index < arr.length; index++ )
			arr[ index ] += add;
	}

	public static void arrayAdd( double[] arr, double add )
	{
		for ( int index = 0; index < arr.length; index++ )
			arr[ index ] += add;
	}

	/**
	 * multiply all array items by a some value
	 * 
	 * @param arr
	 *            array to multiply its items by a same value
	 * @param mult
	 *            value to multiply to all items
	 */
	public static void arrayMultiply( float[] arr, float mult )
	{
		for ( int index = 0; index < arr.length; index++ )
			arr[ index ] *= mult;
	}

	public static void arrayMultiply( double[] arr, double mult )
	{
		for ( int index = 0; index < arr.length; index++ )
			arr[ index ] *= mult;
	}
	/**
	 * finds maximum value in the array. Array shouldn't contain Float.NaN
	 * values
	 * 
	 * @param arr
	 *            array to search
	 * @return max value in the array found
	 */
	public static float arrayFindMax( float[] arr )
	{
		float max = -Float.MAX_VALUE;
		for ( int index = 0; index < arr.length; index++ )
			if ( arr[ index ] > max )
				max = arr[ index ];
		return max;
	}

	public static double arrayFindMax( double[] arr )
	{
		double max = -Double.MAX_VALUE;
		for ( int index = 0; index < arr.length; index++ )
			if ( arr[ index ] > max )
				max = arr[ index ];
		return max;
	}

	/**
	 * finds minimum value in the array. Array shouldn't contain Float.NaN
	 * values
	 * 
	 * @param arr
	 *            array to search
	 * @return minimum value in the array found
	 */
	public static float arrayFindMin( float[] arr )
	{
		float max = Float.MAX_VALUE;
		for ( int index = 0; index < arr.length; index++ )
			if ( arr[ index ] < max )
				max = arr[ index ];
		return max;
	}

	public static double arrayFindMin( double[] arr )
	{
		double max = Double.MAX_VALUE;
		for ( int index = 0; index < arr.length; index++ )
			if ( arr[ index ] < max )
				max = arr[ index ];
		return max;
	}
	/**
	 * sets array to some value
	 * 
	 * @param arr
	 *            array to set items to the same value
	 * @param newval
	 *            value to set to all items
	 */
	public static void arraySet( double[] arr, double newval )
	{
		for ( int index = 0; index < arr.length; index++ )
			arr[ index ] = newval;
	}

	/**
	 * Makes copy of an int[]
	 * 
	 * @param src
	 *            Source int[] to make its copy. If <code>null</code>,
	 *            <code>null</code> will be returned
	 * @return new int[] array with all elements copied or <code>null</code>
	 *         if input also was <code>null</code>
	 */
	public static int[] getCopy( int[] src )
	{
		if ( src == null )
			return null;
		final int[] arr = new int[src.length];
		System.arraycopy( src, 0, arr, 0, arr.length );
		return arr;
	}


	/**
	 * Makes copy of an byte[]
	 * 
	 * @param src
	 *            Source byte[] to make its copy. If <code>null</code>,
	 *            <code>null</code> will be returned
	 * @return new byte[] array with all elements copied or <code>null</code>
	 *         if input also was <code>null</code>
	 */
	public static byte[] getCopy( byte[] src )
	{
		if ( src == null )
			return null;
		final byte[] arr = new byte[src.length];
		System.arraycopy( src, 0, arr, 0, arr.length );
		return arr;
	}


	/**
	 * Makes copy of an long[]
	 * 
	 * @param src
	 *            Source long[] to make its copy. If <code>null</code>,
	 *            <code>null</code> will be returned
	 * @return new long[] array with all elements copied or <code>null</code>
	 *         if input also was <code>null</code>
	 */
	public static long[] getCopy( long[] src )
	{
		if ( src == null )
			return null;
		final long[] arr = new long[src.length];
		System.arraycopy( src, 0, arr, 0, arr.length );
		return arr;
	}

	/**
	 * Creates int[] sub-array from source one
	 * @param src source int[]
	 * @param start start index of sub array including 
	 * @param end end index of the sub array including
	 * @return new int[] with length end - start + 1
	 */
	public static int[] getSubArray( int[] src, int start, int end )
	{
		if ( src == null )
			return null;
		if ( ( start == 0 ) && ( end == src.length - 1 ) )
			return getCopy( src );
		int len;
		final int[] arr = new int[len = end - start + 1];
		System.arraycopy( src, start, arr, 0, len );
		return arr;
	}

	/**
	 * Creates long[] sub-array from source one
	 * @param src source long[]
	 * @param start start index of sub array including 
	 * @param end end index of the sub array including
	 * @return new long[] with length end - start + 1
	 */
	public static long[] getSubArray( long[] src, int start, int end )
	{
		if ( src == null )
			return null;
		if ((src.length == 0) || (( start == 0 ) && ( end == src.length - 1 )) )
			return getCopy( src );
		int len;
		final long[] arr = new long[len = end - start + 1];
		System.arraycopy( src, start, arr, 0, len );
		return arr;
	}

	/**
	 * Creates byte[] sub-array from source one
	 * @param src source byte[]
	 * @param start start index of sub array including 
	 * @param end end index of the sub array including
	 * @return new byte[] with length end - start + 1
	 */
	public static byte[] getSubArray( byte[] src, int start, int end )
	{
		if ( src == null )
			return null;
		if ( ( start == 0 ) && ( end == src.length - 1 ) )
			return getCopy( src );
		int len;
		final byte[] arr = new byte[len = end - start + 1];
		System.arraycopy( src, start, arr, 0, len );
		return arr;
	}
}
