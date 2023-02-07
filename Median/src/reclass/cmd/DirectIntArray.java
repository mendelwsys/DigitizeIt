/**
 *</pre>
 * Created on 28.08.2009 15:17:38<br> 
 * by Syg<br> 
 * for project in 'ru.ts.common.arrays'
 *</pre>
 */
/*
 * Copyright (c) 2000-2001 Sosnoski Software Solutions, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package reclass.cmd;

/**
 * Growable <code>int</code> array providing the same functionality as Vector
 * provides for Object subclasses. The underlying array used for storage of
 * values doubles in size each time more space is required, up to an optional
 * maximum growth increment specified by the user.<p>
 * <p/>
 * This test class differs from the
 * <code>com.sosnoski.util.array.IntArray</code> class in that the subset of
 * methods it implements are coded inline, rather than using base classes. This
 * lets us measure the performance difference caused by the use of a base class
 * hierarchy. It does not include all public methods available from
 * <code>IntArray</code>.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class DirectIntArray
{
	public String toString()
	{
		if ( this.size() == 0 )
			return "";
    int[] arr = this.baseArray;
    return Arrs.toString( arr, 0, this.size() );
	}

  public static int DEFAULT_ARRAY_SIZE = 16;

  /**
	 * The number of values currently present in the array.
	 */
	protected int countPresent;

	/**
	 * Maximum size increment for growing array.
	 */
	protected int maximumGrowth;

	/**
	 * The underlying array used for storing the data.
	 */
	protected int[] baseArray;

	/**
	 * Constructor with full specification.
	 *
	 * @param size   number of int values initially allowed in array
	 * @param growth maximum size increment for growing array
	 */
	public DirectIntArray( int size, int growth )
	{
		maximumGrowth = growth;
		baseArray = new int[size];
	}

	/**
	 * Constructor with only initial size specified.
	 *
	 * @param size number of int values initially allowed in array
	 */

	public DirectIntArray( int size )
	{
		this( size, Integer.MAX_VALUE );
	}


	/**
	 * Copy (clone) constructor.
	 *
	 * @param base instance being copied
	 */

	public DirectIntArray( DirectIntArray base )
	{
		this( base.size(), base.maximumGrowth );
		System.arraycopy( base.baseArray, 0, baseArray, 0, base.size() );
		countPresent = base.size();
	}

	/**
	 * Constructor with default size of buffer
	 */
	public DirectIntArray()
	{
		this( DEFAULT_ARRAY_SIZE );
	}

  /**
   * Constructor with default size of buffer
   */
  public DirectIntArray(int[] src )
  {
    this( src.length );
    System.arraycopy( src, 0, baseArray, 0, src.length );
    countPresent = src.length;
  }

  /**
   * Increase the size of the array to at least a specified size. The array
   * will normally be at least doubled in size, but if a maximum size
   * increment was specified in the constructor and the value is less than
   * the current size of the array, the maximum increment will be used
   * instead. If the requested size requires more than the default growth,
   * the requested size overrides the normal growth and determines the size
   * of the replacement array.
   *
   * @param required new minimum size required
   */
  protected byte[] newArray( int required )
  {
    final int size = Math.max( required,
//			baseArray.length + Math.min(baseArray.length, maximumGrowth));
      baseArray.length + Math.min( baseArray.length >>> 1, maximumGrowth ) );
    return new byte[size];
  }


  /**
	 * Increase the size of the array to at least a specified size. The array
	 * will normally be at least doubled in size, but if a maximum size
	 * increment was specified in the constructor and the value is less than
	 * the current size of the array, the maximum increment will be used
	 * instead. If the requested size requires more than the default growth,
	 * the requested size overrides the normal growth and determines the size
	 * of the replacement array.
	 *
	 * @param required new minimum size required
	 */

	protected void growArray( int required )
	{
		final int size = Math.max( required,
//			baseArray.length + Math.min(baseArray.length, maximumGrowth));
						baseArray.length + Math.min( baseArray.length >>> 1, maximumGrowth ) );
		final int[] grown = new int[size];
		System.arraycopy( baseArray, 0, grown, 0, baseArray.length );
		baseArray = grown;
	}

	/**
	 * Add a value to the array, appending it after the current values.
	 *
	 * @param value value to be added
	 * @return index number of added element
	 */

	public final int add( int value )
	{
		int index = countPresent++;
		if ( countPresent > baseArray.length )
		{
			growArray( countPresent );
		}
		baseArray[ index ] = value;
		return index;
	}

	public final int add( int[] arr )
	{
		if ( arr == null || arr.length == 0 )
			return -1;
		int ret = countPresent;
		countPresent += arr.length;
		if ( countPresent > baseArray.length )
			growArray( countPresent );
		System.arraycopy( arr, 0, baseArray, ret, arr.length );
		return ret;
	}

	/**
	 * Add a value at a specified index in the array.
	 *
	 * @param index index position at which to insert element
	 * @param value value to be inserted into array
	 */

	public void add( int index, int value )
	{
		if ( index >= 0 && index <= countPresent )
		{
			if ( ++countPresent > baseArray.length )
			{
				growArray( countPresent );
			}
			if ( index < countPresent )
			{
				System.arraycopy( baseArray, index, baseArray, index + 1,
								countPresent - index - 1 );
			}
			baseArray[ index ] = value;
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException( "Invalid index value" );
		}
	}

	/**
	 * Remove a value from the array. All values above the index removed
	 * are moved down one index position.
	 *
	 * @param index index number of value to be removed
	 */

	public void remove( int index )
	{
		if ( index >= 0 && index < countPresent )
		{
			if ( index < --countPresent )
			{
				System.arraycopy( baseArray, index + 1, baseArray, index,
								countPresent - index );
				baseArray[ countPresent ] = 0;
			}
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException( "Invalid index value" );
		}
	}

	/**
	 * Remove a sequence of values from the array. All values above the start index removed
	 * are moved down by (stop - start + 1) index position.
	 *
	 * @param start int with a start index number of value to be removed, including
	 * @param stop  int with a stop index number of value to be removed, including
	 */

	public void remove( int start, int stop )
	{
		if ( ( start >= 0 ) && ( start <= stop ) && ( stop >= start ) && ( stop < countPresent ) )
		{
			int cnt = stop - start + 1;
			if ( stop == ( countPresent - 1 ) ) // last element also removed, no need to cut it out
			{
				countPresent = start;
			}
			else
			{
				System.arraycopy( baseArray, stop + 1, baseArray, start, cnt );
				baseArray[countPresent -= cnt] = 0; // mark last to be zero
			}
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException( "Invalid indexes value" );
		}
	}

	/**
	 * Ensure that the array has the capacity for at least the specified
	 * number of values.
	 *
	 * @param min minimum capacity to be guaranteed
	 */

	public final void ensureCapacity( int min )
	{
		if ( min > baseArray.length )
		{
			growArray( min );
		}
	}

	/**
	 * Set the array to the empty state.
	 */

	public final void clear()
	{
		countPresent = 0;
	}

	/**
	 * Get the number of values currently present in the array.
	 *
	 * @return count of values present
	 */

	public final int size()
	{
		return countPresent;
	}

	/**
	 * Sets the number of values currently present in the array. If the new
	 * size is greater than the current size, the added values are initialized
	 * to 0.
	 *
	 * @param count number of values to be set
	 */

	public void setSize( int count )
	{
		if ( count > baseArray.length )
		{
			growArray( count );
		}
		else if ( count < countPresent )
		{
			for ( int i = count; i < countPresent; i++ )
			{
				baseArray[ i ] = 0;
			}
		}
		countPresent = count;
	}

	/**
	 * Trims internal array to the its actual size
	 */
	public void trimToSize()
	{
		final int[] arr = new int[countPresent];
		System.arraycopy( baseArray, 0, arr, 0, countPresent );
		baseArray = arr;
	}

	/**
	 * Retrieve the value present at an index position in the array.
	 *
	 * @param index index position for value to be retrieved
	 * @return value from position in the array
	 */

	public int get( int index )
	{
		if ( (index < countPresent) && (index >= 0) )
		{
			return baseArray[ index ];
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException( "Invalid index value" );
		}
	}

	/**
	 * Set the value at an index position in the array.
	 *
	 * @param index index position to be set
	 * @param value value to be set
	 */

	public final void set( int index, int value )
	{
		if ( (index < countPresent) && (index >= 0) )
		{
			baseArray[ index ] = value;
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException( "Invalid index value" );
		}
	}

  /**
   * Sets the array to the designated int[]
   * @param src int[] to set as an internal value
   */
  public void set( int[] src )
  {
    final int len = src.length;
    this.baseArray = new int[  len ];
    System.arraycopy( src, 0, baseArray, 0, len);
    countPresent = src.length;

  }

  /**
	 * Constructs and returns a simple array containing the same data as held
	 * in this growable array.
	 *
	 * @return array containing a copy of the data
	 */

	public int[] toArray()
	{
		int[] copy = new int[countPresent];
		System.arraycopy( baseArray, 0, copy, 0, countPresent );
		return copy;
	}

	/**
	 * Duplicates the object with the generic call.
	 *
	 * @return a copy of the object
	 */

	public Object clone()
	{
		return new DirectIntArray( this );
	}

	public int[] baseArray()
	{
		return this.baseArray;
	}

	public boolean equals( final Object o )
	{
		if ( this == o )
			return true;
		if ( !(o instanceof DirectIntArray ) )
			return false;

		final DirectIntArray that = (DirectIntArray) o;

		if ( countPresent != that.countPresent )
			return false;
		return Arrs.equals( baseArray, that.baseArray, that.countPresent );

	}

	public int hashCode()
	{
		return 31 * countPresent + Arrs.hashCode(baseArray, countPresent );
	}

	/**
	 * Finds for first occurence of 'val' int he array
	 * @param val value to find
	 * @return index of first element found, or -1 if no such element at all
	 */
	public int find( int val )
	{
		for ( int i = 0; i < baseArray.length; i++ )
		{
			if ( baseArray[i] == val )
				return i;
		}
		return -1;
	}

	/**
	 * Finds for last occurence of 'val' in the array
	 *
	 * @param val value to find
	 * @return back index of first element found, or -1 if no such element at all
	 */
	public int findBack( int val )
	{
		for ( int i = baseArray.length - 1; i >= 0; i-- )
		{
			if ( baseArray[i] == val )
				return i;
		}
		return -1;
	}

}