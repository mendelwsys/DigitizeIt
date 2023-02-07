/**
 *
 */
package reclass.cmd;

import java.util.Arrays;

/**
 * @author sygsky
 */
public class Arrs
{
	public static byte[] createRandomArr( int len )
	{
		return new byte[len];
	}

	/**
	 * Compares two sorted String[] with case sensivity.
	 * If any of arrays is unsorted, result is unpredictable
	 * @param arr1 first sorted String[] to compare
	 * @param arr2 second sorted String[] to compare
	 * @return as usual -1, 0, +1 depending on compare result
	 * @see java.util.Arrays#sort(Object[])
	 */
	public static int compare( String[] arr1, String[] arr2 )
	{
		if ( arr2 == null )
		{
			if ( arr1 != null)
				return 1;
			return 0;
		}
		else
			if ( arr1 == null)
				return -1;
		int diff = arr1.length - arr2.length;
		if ( diff != 0 )
			return diff;
		for ( int i = 0; i < arr1.length; i++ )
		{
			diff = arr1[i].compareTo(arr2[i ] );
				if ( diff != 0 )
					return diff;
		}
		return 0;
	}

	/**
	 * Compares two sorted String[] with no case sensivity.
	 * If any of arrays is unsorted, result is unpredictable
	 *
	 * @param arr1 first sorted String[] to compare
	 * @param arr2 second sorted String[] to compare
	 * @return as usual -1, 0, +1 depending on compare result
	 * @see java.util.Arrays#sort(Object[])
	 */
	public static int compareIgnoreCase( String[] arr1, String[] arr2 )
	{
		if ( arr2 == null )
		{
			if ( arr1 != null )
				return 1;
			return 0;
		}
		else if ( arr1 == null )
			return -1;
		int diff = arr1.length - arr2.length;
		if ( diff != 0 )
			return diff;
		for ( int i = 0; i < arr1.length; i++ )
		{
			diff = arr1[i].compareToIgnoreCase( arr2[i] );
			if ( diff != 0 )
				return diff;
		}
		return 0;	// Todo: change body of created methods use File | Settings | File Templates.
	}

  /**
   * Checks if sorted arrAy is unique (returns {@code true}) or not ({@code false})
   * @param arr int[] to check for uniquety
   */
  public static boolean isUnique( int[] arr)
  {
    for (int i = 0; i < (arr.length - 1); i++)
    {
      if ( arr[i] == arr[i+1] )
        return false;
    }
    return true;
  }

  /**
   * Interface for double array of free size
   */
  public interface IDoubleBuf
  {
    int size();

    void add( double val );

    boolean insert( int ind, double val );

    boolean insert( double[] arr, int ind, int num );

    boolean remove( int ind );

    /**
     * Removes range of items
     *
     * @param start start index to remove
     * @param end   end index of remove exclusively. Number of doubles removed
     *              will be (end - start)
     *
     * @return {@code true} if sucessfully removed else {@code false} if some
     *         arguments are illegal
     */
    boolean remove( int start, int end );

    double get( int ind );

    boolean get( double[] arr, int start, int end );

    /**
     * Gets internal array of doubles, valid values are in the first {@link
     * IDoubleBuf#size()} doubles
     *
     * @return double[] internally used
     */
    double[] getRef();
  }

  public interface IIntGrpBuf extends IGrpBuf
  {

    /**
     * Adds next group
     *
     * @param grp array with item type group
     * @param off offset int array, from this index the group is assumed to start
     *
     * @return {@code true} if grp have had a correct length
     */
    boolean addGroup( int[] grp, int off );

    boolean insertGroup( int ind, int[] grp );

    /**
     * gets group from the list
     *
     * @param ind int with index of the double group
     * @param arr double array at least group length
     * @param off offset in array
     *
     * @return {@code true} if index in range, else {@code false}
     */
    boolean getGroup( int ind, int[] arr, int off );


    /**
     * Gets all values from internal buffer
     *
     * @param arr user array to accept
     *
     * @return true if user arr have had enough length to accept all values
     */
    boolean getAll( int[] arr );

    /**
     * return reference to the internal array. Note that its length may be larger
     * that needed to keep all values, that is only first {@link
     * IGrpBuf#itemsNumber()} are valid
     *
     * @return int[] internally used
     */
    int[] getRef();

    public class Base extends IGrpBuf.Base implements IIntGrpBuf
    {
      int[] _arr;

      protected void _ensureSize( int size )
      {
        if ( ( _arr.length / _groupSize ) < size )
        {
          int[] arr1 = new int[( size * _groupSize * 3 ) / 2];
          System.arraycopy( _arr, 0, arr1, 0, itemsNumber() );
          _arr = arr1;
        }
      }

      public Base( int groupSize )
      {
        super( groupSize );
        _arr = new int[16];
      }

      public void trimToSize()
      {
        final int len = itemsNumber();
        if ( len != _arr.length )
        {
          int[] arr1 = new int[itemsNumber()];
          System.arraycopy( _arr, 0, arr1, 0, len );
          _arr = arr1;
        }
      }

      public boolean removeGroups( int start, int end )
      {
        int len = end - start;
        if ( len <= 0 )
          return false;
        if ( end > groupNumber() )
          return false;
        if ( ( end + len ) > groupNumber() )
          return false;
        System.arraycopy( _arr, end * groupSize(), _arr, start, len );
        _size -= len;
        return true;
      }

      public boolean addGroup( int[] grp, int off )
      {
        if ( ( grp == null ) || ( ( off + _groupSize ) > grp.length ) )
          return false;
        _ensureSize( _size + 1 );
        System
          .arraycopy( grp, off, _arr, _size * _groupSize, this.groupSize() );
        _size++;
        return true;
      }

      public boolean insertGroup( int ind, int[] grp )
      {
        if ( ( grp == null ) || ( grp.length < this._groupSize ) || ( ind < 0 ) || ( ind > _size ) )
          return false;
        _ensureSize( _size + 1 );
        int len = _size - ind;
        final int pos = ind * _groupSize;
        if ( ind < _size )
          System.arraycopy( _arr, pos, _arr, pos + _groupSize, len );
        System.arraycopy( grp, 0, _arr, pos, _groupSize );
        _size++;
        return true;
      }

      public boolean getGroup( int ind, int[] arr, int off )
      {
        if ( ( arr == null ) || ( arr.length < _groupSize ) || ( ind < 0 ) || ( ind >= _size ) )
          return false;
        final int pos = ind * _groupSize;
        System.arraycopy( _arr, pos, arr, 0, _groupSize );
        return true;
      }

      public boolean getAll( int[] arr )
      {
        if ( ( arr == null ) || ( arr.length < itemsNumber() ) )
          return false;
        System.arraycopy( _arr, 0, arr, 0, itemsNumber() );
        return true;
      }

      public int[] getRef()
      {
        return _arr;
      }
    }

  }

  /**
   * Interface for keeping double groups of arbitrary size (1,2,3 etc)
   */
  public interface IDoubleGrpBuf extends IGrpBuf
  {

    /**
     * Adds next group
     *
     * @param grp arra with double group
     * @param off offset int array, from this index the group is assumed to start
     *
     * @return {@code true} if grp have had a correct length
     */
    boolean addGroup( double[] grp, int off );

    boolean insertGroup( int ind, double[] grp );

    /**
     * gets group from the list
     *
     * @param ind int with index of the double group
     * @param arr double array at least group length
     * @param off offset in array
     *
     * @return {@code true} on success else {@code false}
     */
    boolean getGroup( int ind, double[] arr, int off );

    /**
     * Gets all values from internal buffer
     *
     * @param arr user array to accept
     *
     * @return true if user arr have had enough length to accept all values
     */
    boolean getAll( double[] arr );

    /**
     * return reference to the internal array. Note that its length may be larger
     * that needed to keep all values, that is only first {@link
     * IGrpBuf#itemsNumber()} are valid
     *
     * @return reference to the internal buffer
     */
    double[] getRef();

    public class Base extends IGrpBuf.Base implements IDoubleGrpBuf
    {
      double[] _arr;

      protected void _ensureSize( int size )
      {
        if ( ( _arr.length / _groupSize ) < size )
        {
          double[] arr1 = new double[( size * _groupSize * 3 ) / 2];
          System.arraycopy( _arr, 0, arr1, 0, itemsNumber() );
          _arr = arr1;
        }
      }

      public Base( int groupSize )
      {
        super( groupSize );
        _arr = new double[16];
      }

      public void trimToSize()
      {
        final int len = itemsNumber();
        if ( len != _arr.length )
        {
          double[] arr1 = new double[itemsNumber()];
          System.arraycopy( _arr, 0, arr1, 0, len );
          _arr = arr1;
        }
      }

      public boolean removeGroups( int start, int end )
      {
        int len = end - start;
        if ( len <= 0 )
          return false;
        if ( end > groupNumber() )
          return false;
        if ( ( end + len ) > groupNumber() )
          return false;
        System.arraycopy( _arr, end * groupSize(), _arr, start, len );
        _size -= len;
        return true;
      }

      public boolean addGroup( double[] grp, int off )
      {
        if ( ( grp == null ) || ( ( off + _groupSize ) > grp.length ) )
          return false;
        _ensureSize( _size + 1 );
        System
          .arraycopy( grp, off, _arr, _size * _groupSize, this.groupSize() );
        _size++;
        return true;
      }

      public boolean insertGroup( int ind, double[] grp )
      {
        if ( ( grp == null ) || ( grp.length < this._groupSize ) || ( ind < 0 ) || ( ind > _size ) )
          return false;
        _ensureSize( _size + 1 );
        int len = _size - ind;
        final int pos = ind * _groupSize;
        if ( ind < _size )
          System.arraycopy( _arr, pos, _arr, pos + _groupSize, len );
        System.arraycopy( grp, 0, _arr, pos, _groupSize );
        _size++;
        return true;
      }

      public boolean getGroup( int ind, double[] arr, int off )
      {
        if ( ( arr == null ) || ( arr.length < _groupSize ) || ( ind < 0 ) || ( ind >= _size ) )
          return false;
        final int pos = ind * _groupSize;
        System.arraycopy( _arr, pos, arr, 0, _groupSize );
        return true;
      }

      public boolean getAll( double[] arr )
      {
        if ( ( arr == null ) || ( arr.length < itemsNumber() ) )
          return false;
        System.arraycopy( _arr, 0, arr, 0, itemsNumber() );
        return true;
      }

      public double[] getRef()
      {
        return _arr;
      }

    }
  }

  /**
   * reverses input double array
   *
   * @param arr array of double to be reversed
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
   * removes all duplicates from sorted array. If array is not sorted, result is
   * unpredictable. No additional memory is allocated during this operation,
   * except of resulting array.
   *
   * @param arr array of int[], sorted in any order (!)
   *
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
   * removes all duplicates from sorted array. If array is not sorted, result is
   * unpredictable. No additional memory is allocated during this operation,
   * except of resulting array.
   *
   * @param arr array of char[], sorted in any order (!)
   *
   * @return new char[] of unique sorted values in the same order as before. If
   *         input array was already unique, the same array is returned.
   */
  public static char[] unique( char arr[] )
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
    char[] ret = new char[outind];
    System.arraycopy( arr, 0, ret, 0, outind );
    return ret;
  }

  /**
   * Detects if array in not <code>null</code> and its length > 0
   *
   * @param arr int[] to check
   *
   * @return <code>true</code> if array is not <code>null</code> and has items
   */
  public static boolean isEmpty( int arr[] )
  {
    return ( arr == null ) || ( arr.length == 0 );
  }

  /**
   * Detects if array in not <code>null</code> and its length > 0
   *
   * @param arr double[] to check
   *
   * @return <code>true</code> if array is not <code>null</code> and has items
   */
  public static boolean isEmpty( double arr[] )
  {
    return ( arr == null ) || ( arr.length == 0 );
  }

  /**
   * Checks if array is sorted ascending
   *
   * @param arr not empty array
   *
   * @return <code>true</code> if array is not empty and is sorted in ascending
   *         order, else <code>false</code>
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
   * @param arr not empty array
   *
   * @return <code>true</code> if array is not empty and is sorted in descending
   *         order, else <code>false</code>
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
   * Moves array elements to the end of array, extracting some ones, shifting all
   * upper element to free space and append extraction to the end
   *
   * @param src int[] source array
   * @param off source offset of array part to move to the end
   * @param len length of part to move
   *
   * @return new array with the same elements but in a new orders
   *
   * @exception IndexOutOfBoundsException if any parameter is out of range
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
    Object[] dst = ( Object[] ) src.clone();
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
   * @param arr    array to set items to the same value
   * @param newval value to set to all items
   */
  public static void arraySet( float[] arr, float newval )
  {
    Arrays.fill( arr, newval );
  }

  /**
   * sets array to some value
   *
   * @param arr    array to set items to the same value
   * @param newval value to set to all items
   */
  public static void arraySet( byte[] arr, byte newval )
  {
    Arrays.fill( arr, newval );
  }

  /**
   * sets array to some value
   *
   * @param arr    array to set items to the same value
   * @param newval value to set to all items
   */
  public static void arraySet( short[] arr, short newval )
  {
    Arrays.fill( arr, newval );
  }

  /**
   * sets array to some value
   *
   * @param arr    array to set items to the same value
   * @param newval value to set to all items
   */
  public static void arraySet( int[] arr, int newval )
  {
    Arrays.fill( arr, newval );
  }


  /**
   * adds all array items to a some value
   *
   * @param arr array to adds items to the same value
   * @param add value to adds to all items
   */
  public static void arrayAdd( float[] arr, float add )
  {
    for ( int index = 0; index < arr.length; index++ )
      arr[ index ] += add;
  }

  /**
   * multiply all array items by a some value
   *
   * @param arr  array to multiply its items by a same value
   * @param mult value to multiply to all items
   */
  public static void arrayMultiply( float[] arr, float mult )
  {
    for ( int index = 0; index < arr.length; index++ )
      arr[ index ] *= mult;
  }

  /**
   * finds maximum value in the array. Array shouldn't contain Float.NaN values
   *
   * @param arr array to search
   *
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

  /**
   * finds minimum value in the array. Array shouldn't contain Float.NaN values
   *
   * @param arr array to search
   *
   * @return minimum value in the array found
   */
  public static float arrayFindMin( float[] arr )
  {
    float min = Float.MAX_VALUE;
    for ( int index = 0; index < arr.length; index++ )
      if ( arr[ index ] < min )
        min = arr[ index ];
    return min;
  }

  /**
   * Universal algorithm to find min and max values in data array containing complex logic, e.g. containing X, Y sequences etc.
   * Finds minimum and maximum using offset, step size and step number
   * @param arr double[] to find throw
   * @param off start offset (e.g. first X offset) of checkig element in the structure
   * @param step step (structure size) to skip for next value
   * @param stepNum number of steps or number of structures
   * @return double[2] with {min, max} found
   */
  public static double[] arrayFindMinMax( double[] arr, int off, int step, int stepNum )
  {
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    double v;
    for ( int index = 0; index < stepNum; index++ )
    {
      int i = off + index * step;
      v = arr[ i ];
      if ( v < min )
        min = arr[ index ];
      if ( v > max )
        max = arr[ index ];

    }
    return new double[]{min, max};
  }

  /**
   * sets array to some value
   *
   * @param arr    array to set items to the same value
   * @param newval value to set to all items
   */
  public static void arraySet( double[] arr, double newval )
  {
    for ( int index = 0; index < arr.length; index++ )
      arr[ index ] = newval;
  }

  /**
   * Makes copy of an int[]
   *
   * @param src Source int[] to make its copy. If <code>null</code>,
   *            <code>null</code> will be returned
   *
   * @return new int[] array with all elements copied or <code>null</code> if
   *         input also was <code>null</code>
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
   * @param src Source byte[] to make its copy. If <code>null</code>,
   *            <code>null</code> will be returned
   *
   * @return new byte[] array with all elements copied or <code>null</code> if
   *         input also was <code>null</code>
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
   * @param src Source long[] to make its copy. If <code>null</code>,
   *            <code>null</code> will be returned
   *
   * @return new long[] array with all elements copied or <code>null</code> if
   *         input also was <code>null</code>
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
   * Makes copy of an String[]
   *
   * @param src Source String[] to make its copy. If <code>null</code>,
   *            <code>null</code> will be returned
   *
   * @return new String[] array with all elements copied or <code>null</code> if
   *         input also was <code>null</code>
   */
  public static String[] getCopy( String[] src )
  {
    if ( src == null )
      return null;
    final String[] arr = new String[src.length];
    System.arraycopy( src, 0, arr, 0, arr.length );
    return arr;
  }

  /**
   * Creates int[] sub-array from source one
   *
   * @param src   source int[]
   * @param start start index of sub array including
   * @param end   end index of the sub array including
   *
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
   *
   * @param src   source long[]
   * @param start start index of sub array including
   * @param end   end index of the sub array including
   *
   * @return new long[] with length end - start + 1
   */
  public static long[] getSubArray( long[] src, int start, int end )
  {
    if ( src == null )
      return null;
    if ( ( src.length == 0 ) || ( ( start == 0 ) && ( end == src.length - 1 ) ) )
      return getCopy( src );
    int len;
    final long[] arr = new long[len = end - start + 1];
    System.arraycopy( src, start, arr, 0, len );
    return arr;
  }

  /**
   * Creates String[] sub-array from a source one
   *
   * @param src   source String[]
   * @param start start index of sub array including
   * @param end   end index of the sub array including
   *
   * @return new String[] with length end - start + 1
   */
  public static String[] getSubArray( String[] src, int start, int end )
  {
    if ( src == null )
      return null;
    if ( ( src.length == 0 ) || ( ( start == 0 ) && ( end == src.length - 1 ) ) )
      return getCopy( src );
    int len;
    final String[] arr = new String[len = end - start + 1];
    System.arraycopy( src, start, arr, 0, len );
    return arr;
  }

  /**
   * Creates byte[] sub-array from source one
   *
   * @param src   source byte[]
   * @param start start index of sub array including
   * @param end   end index of the sub array including
   *
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

  /**
   * Returns array with elements from 'cutStart' with length cutlen to be cut out from the
   * 'src'
   *
   * @param src      source String[]
   * @param cutStart start index to cut out, if < 0, set to 0, if > src.length,
   *                 copy of src ios returned
   * @param cutlen   length of the cut items, if cutStart + cutlen > src.length,
   *                 src is truncated from cutStart
   *
   * @return new String[].length == src.length - cutlen ojn average
   */
  public static String[] cutOutArray( String[] src, int cutStart, int cutlen )
  {
    if ( cutlen < 0 )
      return src;
    if ( cutStart < 0 )
      cutStart = 0;
    if ( cutStart >= src.length )
      return getCopy( src );
    if ( cutStart + cutlen > src.length )
      cutlen = src.length - cutStart;
    String[] arr = new String[src.length - cutlen];
    System.arraycopy( src, 0, arr, 0, cutStart );
    if ( ( cutStart + cutlen ) < src.length )
      System.arraycopy( src, cutStart + cutlen, arr, cutStart,
        src.length - cutStart - cutlen );
    return arr;
  }

  public static boolean equals( byte[] arr1, byte[] arr2 )
  {
    return Arrays.equals( arr1, arr2 );
  }

	public static boolean equals( double[] arr1, double[] arr2 )
	{
		return Arrays.equals( arr1, arr2 );
	}

	/**
   * Gets the summary of all elements in the array
   *
   * @param vals int[] to summarize its elements
   *
   * @return summary of all elements in the input int[]
   */
  public static int summary( final int[] vals )
  {
    int sum = 0;
    for ( int i = 0; i < vals.length; i++ )
      sum += vals[ i ];
    return sum;
  }

  /**
   * Returns <tt>true</tt> if the two specified arrays of ints are <i>equal</i>
   * to one another.  Two arrays are considered equal if both arrays contain the
   * same number of elements, and all corresponding pairs of elements in the two
   * arrays are equal.  In other words, two arrays are equal if they contain the
   * same elements in the same order.  Also, two array references are considered
   * equal if both are <tt>null</tt>.<p>
   *
   * @param a1  one array to be tested for equality.
   * @param a2  the other array to be tested for equality.
   * @param len check length to be involved in the equals compare. Must be >= 0!
   *            May be > length of both arrays, in that case their length should
   *            be equal!)
   *
   * @return <tt>true</tt> if the two arrays are equal.
   */
  public static boolean equals( int[] a1, int[] a2, int len )
  {
    if ( a1 == a2 )
      return true;
    if ( a1 == null || a2 == null )
      return false;
    if ( len < 0 )
      return false;
    if ( ( len > a2.length ) || ( len > a1.length ) )
      if ( a1.length != ( len = a2.length ) )
        return false;

    for ( int i = 0; i < len; i++ )
      if ( a1[ i ] != a2[ i ] )
        return false;
    return true;
  }

	/**
	 * Returns <tt>true</tt> if the two specified arrays of doubles are <i>equal</i>
	 * to one another.  Two arrays are considered equal if both arrays contain the
	 * same number of elements, and all corresponding pairs of elements in the two
	 * arrays are equal.  In other words, two arrays are equal if they contain the
	 * same elements in the same order.  Also, two array references are considered
	 * equal if both are <tt>null</tt>.<p>
	 *
	 * @param a1	one array to be tested for equality.
	 * @param a2	the other array to be tested for equality.
	 * @param len check length to be involved in the equals compare. Must be >= 0!
	 *            May be > length of both arrays, in that case their length should
	 *            be equal!)
	 * @return <tt>true</tt> if the two arrays are equal.
	 */
	public static boolean equals( double[] a1, double[] a2, int len )
	{
		if ( a1 == a2 )
			return true;
		if ( a1 == null || a2 == null )
			return false;
		if ( len < 0 )
			return false;
		if ( ( len > a2.length ) || ( len > a1.length ) )
			if ( a1.length != ( len = a2.length ) )
				return false;

		for ( int i = 0; i < len; i++ )
			if ( a1[i] != a2[i] )
				return false;
		return true;
	}

	public static boolean equals( char[] a1, char[] a2, int len )
  {
    if ( a1 == a2 )
      return true;
    if ( a1 == null || a2 == null )
      return false;
    if ( len < 0 )
      return false;
    if ( ( len > a2.length ) || ( len > a1.length ) )
      if ( a1.length != ( len = a2.length ) )
        return false;

    for ( int i = 0; i < len; i++ )
      if ( a1[ i ] != a2[ i ] )
        return false;
    return true;
  }

  /**
   * Returns a hash code based on the contents of the specified array. For any
   * two non-null <tt>int</tt> arrays <tt>a</tt> and <tt>b</tt> such that
   * <tt>Arrays.equals(a, b)</tt>, it is also the case that
   * <tt>Arrays.hashCode(a) == Arrays.hashCode(b)</tt>.
   * <p/>
   * <p>The value returned by this method is the same value that would be
   * obtained by invoking the {@link java.util.List#hashCode() <tt>hashCode</tt>}
   * method on a {@link java.util.List} containing a sequence of {@link Integer}
   * instances representing the elements of <tt>a</tt> in the same order. If
   * <tt>a</tt> is <tt>null</tt>, this method returns 0.
   *
   * @param a   the array whose hash value to compute
   * @param len length of array to be involved to hashing, should be <= a.length
   *
   * @return a content-based hash code for <tt>a</tt>
   *
   * @since 1.5
   */
  public static int hashCode( int a[], int len )
  {
    if ( ( a == null ) || ( len <= 0 ) )
      return 0;
    int result = 1;
    if ( len > a.length )
      len = a.length;
    for ( int i = 0; i < len; i++ )
      result = 31 * result + a[ i ];
    return result;
  }

  /**
   * Returns a hash code based on the contents of the specified array. For any
   * two non-null <tt>int</tt> arrays <tt>a</tt> and <tt>b</tt> such that
   * <tt>Arrays.equals(a, b)</tt>, it is also the case that
   * <tt>Arrays.hashCode(a) == Arrays.hashCode(b)</tt>.
   * <p/>
   * <p>The value returned by this method is the same value that would be
   * obtained by invoking the {@link java.util.List#hashCode() <tt>hashCode</tt>}
   * method on a {@link java.util.List} containing a sequence of {@link Integer}
   * instances representing the elements of <tt>a</tt> in the same order. If
   * <tt>a</tt> is <tt>null</tt>, this method returns 0.
   *
   * @param a   the array whose hash value to compute
   * @param len length of array to be involved to hashing, should be <= a.length
   *
   * @return a content-based hash code for <tt>a</tt>
   *
   * @since 1.5
   */
  public static int hashCode( char a[], int len )
  {
    if ( ( a == null ) || ( len <= 0 ) )
      return 0;
    int result = 1;
    if ( len > a.length )
      len = a.length;
    for ( int i = 0; i < len; i++ )
      result = 31 * result + a[ i ];
    return result;
  }

	/**
	 * Returns a hash code for this <code>Double array</code> object. The
	 * result is the exclusive OR of the two halves of the
	 * <code>long</code> integer bit representation, exactly as
	 * produced by the method {@link Double#doubleToLongBits(double)}, of
	 * the primitive <code>double</code> value represented by this
	 * <code>Double</code> object. That is, the hash code is the value
	 * of the expression:
	 * <blockquote><pre>
	 * (int)(v^(v&gt;&gt;&gt;32))
	 * </pre></blockquote>
	 * where <code>v</code> is defined by:
	 * <blockquote><pre>
	 * long v = Double.doubleToLongBits(this.doubleValue());
	 * </pre></blockquote>
	 *
	 * @param a  double[]
	 * @param len int with number of doubles to use for hashinh
	 * @return a <code>hash code</code> value for double array.
	 */
	public static int hashCode( double a[], int len)
	{
		if ( ( a == null ) || ( len <= 0 ) )
			return 0;
		int result = 1;
		if ( len > a.length )
			len = a.length;
		for ( int i = 0; i < len; i++ )
		{
			long bits = Double.doubleToLongBits( a[i] );
			result = 31 * result + ( int ) ( bits ^ ( bits >>> 32 ) );
		}
		return result;
	}


	/**
   * Finds the first occurance of the value in the int[] array
   *
   * @param arr int[] to find in
   * @param val int value to find in array
   *
   * @return index of first position of val in the array or -1 if no such value
   *         was found
   */
  public static int indexOf( final int[] arr, final int val )
  {
    return indexOf( arr, 0, arr.length, val );
  }

  /**
   * Finds the first occurance of the value in the int[] array
   *
   * @param arr int[] to find in
   * @param off offset in the array
   * @param len length of search
   * @param val int value to find in array
   *
   * @return index of first position of val in the array or -1 if no such value
   *         was found
   */
  public static int indexOf( final int[] arr, final int off, final int len,
                             final int val )
  {
    for ( int i = off, off1 = off + len; i < off1; i++ )
      if ( val == arr[ i ] )
        return i;
    return -1;
  }

  /**
   * Finds the first occurance of the value in the byte[] array
   *
   * @param arr int[] to find in
   * @param val int value to find in array
   *
   * @return index of first position of val in the array or -1 if no such value
   *         was found
   */
  public static int indexOf( final byte[] arr, final byte val )
  {
    return indexOf( arr, 0, arr.length, val );
  }

  /**
   * Finds the first occurance of the value in the byte[] array
   *
   * @param arr int[] to find in
   * @param off offset to start search
   * @param len length of searching (number of steps to do in the worst case)
   * @param val int value to find in array
   *
   * @return index of first position of val in the array or -1 if no such value
   *         was found
   */
  public static int indexOf( final byte[] arr, final int off, final int len,
                             final byte val )
  {
    for ( int i = off, off1 = off + len; i < off1; i++ )
      if ( val == arr[ i ] )
        return i;
    return -1;
  }

  /**
   * Find the first occurance of the byte in the array not equal to the user
   * designated value (argument val). No check for correctness argument validity
   * is produced in the method
   *
   * @param arr byte[] to seek through
   * @param off offset to start seeking, if 1st byte at offset already is not
   *            equal to the designated value, this off position is returned
   *            immediately
   * @param len length of maximum skipping
   * @param val byte value to skip along the search
   *
   * @return int position to the first byte in array not equal to the val to skip
   *         or off + len value if no such byte found at the sequence at all
   */
  public static int skipOf( final byte[] arr, final int off, final int len,
                            final byte val )
  {
    for ( int i = off, off1 = off + len; i < off1; i++ )
      if ( val != arr[ i ] )
        return i;
    return off + len;
  }

  /**
   * Finds first byte in forward direction with value not in an user designated
   * array vals
   *
   * @param arr  byte[] to search in it
   * @param off  offset to start search
   * @param len  length of search
   * @param vals byte[] of searched values
   *
   * @return int position of the first byte with value not in the designated
   *         array vals
   */
  public static int skipForward( final byte[] arr, final int off, final int len,
                                 final byte[] vals )
  {
    boolean found = false;
    for ( int i = off, off1 = off + len; i < off1; i++ )
    {
      byte b = arr[ i ];
      for ( int j = 0; j < vals.length; j++ )
      {
        if ( vals[ j ] == b )
        {
          found = true;
          break;
        }
      }
      if ( found )
        continue;
      return i;
    }
    return off + len;
  }

  /**
   * Finds first byte in backward direction with value not in an user designated
   * array vals
   *
   * @param arr  byte[] to search in it
   * @param off  offset to start search
   * @param len  length of search
   * @param vals byte[] of searched values
   *
   * @return int position of the first byte with value not in the designated
   *         array vals
   */
  public static int skipBackward( final byte[] arr, final int off,
                                  final int len, final byte[] vals )
  {
    boolean found = false;
    for ( int i = off, off1 = off - len; i >= off1; i-- )
    {
      byte b = arr[ i ];
      for ( int j = 0; j < vals.length; j++ )
      {
        if ( vals[ j ] == b )
        {
          found = true;
          break;
        }
      }
      if ( found )
        continue;
      return i;
    }
    return off + len;
  }

  /**
   * Returns <tt>true</tt> if the two specified arrays of bytes are <i>equal</i>
   * to one another.  Two arrays are considered equal if both arrays contain the
   * same number of elements, and all corresponding pairs of elements in the two
   * arrays are equal.  In other words, two arrays are equal if they contain the
   * same elements in the same order.  Also, two array references are considered
   * equal if both are <tt>null</tt>.<p>
   *
   * @param a      one array to be tested for equality.
   * @param off1   1st array offset to start compare
   * @param a2     the other array to be tested for equality.
   * @param off2   2nd array offset to start compare
   * @param length number of bytes to compare
   *
   * @return <tt>true</tt> if the two arrays are equal.
   */
  public static boolean equals( byte[] a, int off1, byte[] a2, int off2,
                                int length )
  {
    if ( a == null || a2 == null || off1 < 0 || off2 < 0 || length < 0 ||
      ( off1 + length ) < a.length || ( off2 + length ) < a2.length )
      return false;
    length += off1;
    for (; off1 < length; off1++, off2++ )
      if ( a[ off1 ] != a2[ off2 ] )
        return false;
    return true;
  }

  /**
   * Rotate array for designated offset. Operation is done on the original array
   * @param arr int[] to rotate
   * @param distance value to rotate the designated array. E.g. if rotValue = 1
   * array will be rotated 1 position right, if -1 one position to left etc.
   */

  /**
   * Rotates the elements in the specified array by the specified distance.
   * After calling this method, the element at index <tt>i</tt> will be the
   * element previously at index <tt>(i - distance)</tt> mod
   * <tt>array.length</tt>, for all values of <tt>i</tt> between <tt>0</tt> and
   * <tt>array.length-1</tt>, inclusive.  (This method has no effect on the size
   * of the array.)
   * <p/>
   * <p>For example, suppose <tt>array</tt> comprises<tt> [t, a, n, k, s]</tt>.
   * After invoking <tt>Arrs.rotate(array, 1)</tt> (or <tt>array.rotate(array,
   * -4)</tt>), <tt>array</tt> will comprise <tt>[s, t, a, n, k]</tt>.
   * <p/>
   * <p>To move more than one element forward, increase the absolute value of
   * the rotation distance.  To move elements backward, use a positive shift
   * distance.
   * <p/>
   * <p>This implementation exchanges the first element into the location it
   * should go, and then repeatedly exchanges the displaced element into the
   * location it should go until a displaced element is swapped into the first
   * element.  If necessary, the process is repeated on the second and
   * successive elements, until the rotation is complete. For a more complete
   * description of such algorithms, see Section 2.3 of Jon Bentley's
   * <i>Programming Pearls</i> (Addison-Wesley, 1986).
   *
   * @param arr      the array to be rotated.
   * @param distance the distance to rotate the array.  There are no constraints
   *                 on this value; it may be zero, negative, or greater than
   *                 <tt>array.length</tt>.
   */
  public static void rotate( int[] arr, int distance )
  {
    if ( ( arr == null ) || ( distance == 0 ) )
      return;
    int size = arr.length;
    if ( size == 0 )
      return;
    distance = distance % size;
    if ( distance < 0 )
      distance += size;

    for ( int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++ )
    {
      int i;
      int displaced = arr[ i = cycleStart ];
      int tmp;
      do
      {
        i += distance;
        if ( i >= size )
          i -= size;
        tmp = arr[ i ];
        arr[ i ] = displaced;
        displaced = tmp;
        nMoved++;
      }
      while ( i != cycleStart );
    }
  }

  /**
   * Much simpler to understand method but longer to execute
   * @param arr array of int[] to rotate
   * @param distance to rotate
   */
  public static void rotate2( int[] arr, int distance )
  {
    int size = arr.length;
    if (size == 0)
        return;
    int mid = -distance % size;
    if (mid < 0)
        mid += size;
    if (mid == 0)
        return;

    reverse( arr, 0, mid );
    reverse( arr, mid, size - mid );
    reverse( arr );
  }

  /**
   * TODO check this method before usage
   * @param arr
   * @param off
   * @param len
   * @return
   */
  public static boolean reverse( int[] arr, int off, int len )
  {
    if ( ( arr == null ) || ( len <= 0 ) || ( off < 0 ) || ( (off + len) > arr.length ) )
      return false;
    int cnt = len >> 1; // number of exchanges
    int off1 = off + len -1; // right offset
    for ( int i = 0; i < cnt; i++ )
    {
      int val= arr[ off ];
      arr[ off++ ] = arr[ off1];
      arr[off1--] = val;
    }
    return true;
  }

  public static boolean reverse ( int[] arr )
  {
    return reverse( arr, 0, arr.length);
  }

  public static String toString( int[] arr, int off, int len )
  {
    final StringBuffer sb = new StringBuffer( );
    sb.append( arr[ off ] );
    int off1 = off + len;
    for ( int i = off + 1 ; i < off1; i++ )
    {
      sb.append( ',' ).append( arr[ i ] );
//			int len = baseArray[ i ];     // used for debugging purposes only
    }
    return sb.toString();
  }

  public static String toString( int[] arr )
  {
    return toString( arr, 0, arr.length );
  }


}