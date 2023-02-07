/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package reclass.bitmatrix;

import java.awt.Rectangle;

/**
 * Fixed sized (non resizable) n*m bit matrix.
 * A bit matrix has a number of columns and rows, which are assigned upon instance construction - The matrix's size is then <tt>columns()*rows()</tt>.
 * Bits are accessed via <tt>(column,row)</tt> coordinates.
 * <p/>
 * Individual bits can be examined, set, or cleared.
 * Rectangular parts (boxes) can quickly be extracted, copied and replaced.
 * Quick iteration over boxes is provided by optimized internal iterators (<tt>forEach()</tt> methods).
 * One <code>BitMatrix</code> may be used to modify the contents of another
 * <code>BitMatrix</code> through logical AND, OR, XOR and other similar operations.
 * <p/>
 * Legal coordinates range from <tt>[0,0]</tt> to <tt>[columns()-1,rows()-1]</tt>.
 * Any attempt to access a bit at a coordinate <tt>column&lt;0 || column&gt;=columns() || row&lt;0 || row&gt;=rows()</tt> will throw an <tt>IndexOutOfBoundsException</tt>.
 * Operations involving two bit matrices (like AND, OR, XOR, etc.) will throw an <tt>IllegalArgumentException</tt> if both bit matrices do not have the same number of columns and rows.
 * <p/>
 * If you need extremely quick access to individual bits: Although getting and setting individual bits with methods <tt>get(...)</tt> and <tt>put(...)</tt> is quick, it is even quicker (<b>but not safe</b>) to use <tt>getQuick(...)</tt> and <tt>putQuick(...)</tt>.
 * <p/>
 * <b>Note</b> that this implementation is not synchronized.
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 * @see BitVector
 * @see QuickBitVector
 * @see java.util.BitSet
 */
public class BitMatrix extends PersistentObject
{
	protected int columns;
	protected int rows;

	/*
	 * The bits of this matrix.
	 * bits are stored in row major, i.e.
	 * bitIndex==row*columns + column
	 * columnOf(bitIndex)==bitIndex%columns
	 * rowOf(bitIndex)==bitIndex/columns
	 */
	protected long bits[];
	/**
	 * Border extension by filling all pixels outside of the image
	 * bounds with zeros.  For example, the image:
	 * <p/>
	 * <p><center>
	 * <table border=1>
	 * <tr align=center><td>A</td><td>B</td><td>C</td> </tr>
	 * <tr align=center><td>D</td><td>E</td><td>F</td> </tr>
	 * <tr align=center><td>G</td><td>H</td><td>I</td> </tr>
	 * </table></center>
	 * <p/>
	 * <p/>
	 * <br>if extended by adding two extra rows to the top and bottom and
	 * two extra columns on the left and right sides, would become:
	 * <p/>
	 * <p><center>
	 * <table border=1>
	 * <tr align=center>
	 * <td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td> </tr>
	 * <td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td> </tr>
	 * <td>0</td><td>0</td><td>A</td><td>B</td><td>C</td><td>0</td><td>0</td> </tr>
	 * <td>0</td><td>0</td><td>D</td><td>E</td><td>F</td><td>0</td><td>0</td> </tr>
	 * <td>0</td><td>0</td><td>G</td><td>H</td><td>I</td><td>0</td><td>0</td> </tr>
	 * <td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td> </tr>
	 * <td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td> </tr>
	 * </table></center>
	 * <p/>
	 * This is default and most unexpensive to process mode
	 */
	public static final int BORDER_ZERO = 0;
	public static final int BORDER_MIN_VALUE = BORDER_ZERO;
	/**
	 * Implementsborder extension by filling all pixels outside of the image
	 * bounds with copies of the edge pixels.  For example, the image:
	 * <p/>
	 * <p><center>
	 * <table border=1>
	 * <tr align=center><td>A</td><td>B</td><td>C</td> </tr>
	 * <tr align=center><td>D</td><td>E</td><td>F</td> </tr>
	 * <tr align=center><td>G</td><td>H</td><td>I</td> </tr>
	 * </table></center>
	 * <p/>
	 * <br>if extended by adding two extra rows to the top and bottom and
	 * two extra columns on the left and right sides, would become:
	 * <p/>
	 * <p><center>
	 * <table border=1>
	 * <tr align=center>
	 * <td>A</td><td>A</td><td>A</td><td>B</td><td>C</td><td>C</td><td>C</td> </tr>
	 * <td>A</td><td>A</td><td>A</td><td>B</td><td>C</td><td>C</td><td>C</td> </tr>
	 * <td>A</td><td>A</td><td>A</td><td>B</td><td>C</td><td>C</td><td>C</td> </tr>
	 * <td>D</td><td>D</td><td>D</td><td>E</td><td>F</td><td>F</td><td>F</td> </tr>
	 * <td>G</td><td>G</td><td>G</td><td>H</td><td>I</td><td>I</td><td>I</td> </tr>
	 * <td>G</td><td>G</td><td>G</td><td>H</td><td>I</td><td>I</td><td>I</td> </tr>
	 * <td>G</td><td>G</td><td>G</td><td>H</td><td>I</td><td>I</td><td>I</td> </tr>
	 * </table></center>
	 * <p/>
	 * <p> Although this type of extension is not particularly
	 * visually appealing, it is very useful as a way of padding
	 * source images prior to area or geometric operations, such as
	 * convolution, scaling, or rotation.
	 */
	public static final int BORDER_COPY = 1;
	/**
	 * Implements border extension by filling all pixels outside of the image
	 * bounds with copies of the whole image.  For example, the image:
	 * <p/>
	 * <p><center>
	 * <table border=1>
	 * <tr align=center><td>A</td><td>B</td><td>C</td> </tr>
	 * <tr align=center><td>D</td><td>E</td><td>F</td> </tr>
	 * <tr align=center><td>G</td><td>H</td><td>I</td> </tr>
	 * </table></center>
	 * <p/>
	 * <br>if extended by adding two extra rows to the top and bottom and
	 * two extra columns on the left and right sides, would become:
	 * <p/>
	 * <p><center>
	 * <table border=1>
	 * <tr align=center>
	 * <td>E</td><td>F</td><td>D</td><td>E</td><td>F</td><td>D</td><td>E</td> </tr>
	 * <td>H</td><td>I</td><td>G</td><td>H</td><td>I</td><td>G</td><td>H</td> </tr>
	 * <td>B</td><td>C</td><td>A</td><td>B</td><td>C</td><td>A</td><td>B</td> </tr>
	 * <td>E</td><td>F</td><td>D</td><td>E</td><td>F</td><td>D</td><td>E</td> </tr>
	 * <td>H</td><td>I</td><td>G</td><td>H</td><td>I</td><td>G</td><td>H</td> </tr>
	 * <td>B</td><td>C</td><td>A</td><td>B</td><td>C</td><td>A</td><td>B</td> </tr>
	 * <td>E</td><td>F</td><td>D</td><td>E</td><td>F</td><td>D</td><td>E</td> </tr>
	 * </table></center>
	 * <p/>
	 * <p> This form of extension is appropriate for data that is inherently
	 * periodic, such as the Fourier transform of an image, or a wallpaper
	 * pattern.
	 */
	public static final int BORDER_WRAP = 2;

	/**
	 * Implements border extension by filling all pixels outside of the image
	 * bounds with copies of the whole image.  For example, the image:
	 * <p/>
	 * <p><center>
	 * <table width="10%" border=1>
	 * <tr align=center><td><tt>|&gt;<br>|\</td> </tr>
	 * </table></center>
	 * <p/>
	 * <br>if extended by adding two extra rows to the top and bottom and
	 * one extra column on the left and right sides, would become:
	 * <p/>
	 * <p><center>
	 * <table width="30%" border=1>
	 * <tr align=center><td><tt>&lt;|<br>/|</tt></td> <td><tt>|&gt;<br>|\</tt></td> <td><tt>&lt;|<br>/|</tt></td> </tr>
	 * <tr align=center><td><tt>\|<br>&lt;|</tt></td> <td><tt>|/<br>|></tt></td> <td><tt>\|<br>|</tt></td> </tr>
	 * <tr align=center><td><tt>&lt;|<br>/|</tt></td> <td><tt>|><br>|\</tt></td> <td><tt>&lt;|<br>/|</tt></td> </tr>
	 * <tr align=center><td><tt>\|<br><|</tt></td> <td><tt>|/<br>|&gt;</tt></td> <td><tt>\|<br>&lt;|</tt></td> </tr>
	 * <tr align=center><td><tt>&lt;|<br>/|</tt></td> <td><tt>|&gt;<br>|\</tt></td> <td><tt>&lt;|<br>/|</tt></td> </tr>
	 * </table></center>
	 * <p/>
	 * <p> This form of extension avoids discontinuities around the edges
	 * of the image.
	 */
	public static final int BORDER_REFLECT = 3;
	public static final int BORDER_MAX_VALUE = BORDER_REFLECT;

	/**
	 * Constructs a bit matrix with a given number of columns and rows. All bits are initially <tt>false</tt>.
	 *
	 * @param columns the number of columns the matrix shall have.
	 * @param rows    the number of rows the matrix shall have.
	 * @throws IllegalArgumentException if <tt>columns &lt; 0 || rows &lt; 0</tt>.
	 */
	public BitMatrix( int columns, int rows )
	{
		elements( QuickBitVector.makeBitVector( columns * rows, 1 ), columns, rows );
	}

	/**
	 * Performs a logical <b>AND</b> of the receiver with another bit matrix.
	 * The receiver is modified so that a bit in it has the
	 * value <code>true</code> if and only if it already had the
	 * value <code>true</code> and the corresponding bit in the other bit matrix
	 * argument has the value <code>true</code>.
	 *
	 * @param other a bit matrix.
	 * @throws IllegalArgumentException if <tt>columns() != other.columns() || rows() != other.rows()</tt>.
	 */
	public void and( BitMatrix other )
	{
		checkDimensionCompatibility( other );
		toBitVector().and( other.toBitVector() );
	}

	public static String borderType2String(int borderType)
	{
		switch ( borderType )
		{
			default:
				return "UNKNOWN_TYPE";
			case BORDER_ZERO:
					return "BORDER_ZERO";
			case BORDER_COPY:
					return "BORDER_COPY";
			case BORDER_WRAP:
					return "BORDER_WRAP";
			case BORDER_REFLECT:
					return "BORDER_REFLECT";
		}
	}
	/**
	 * Clears all of the bits in receiver whose corresponding
	 * bit is set in the other bit matrix.
	 * In other words, determines the difference (A\B) between two bit matrices.
	 *
	 * @param other a bit matrix with which to mask the receiver.
	 * @throws IllegalArgumentException if <tt>columns() != other.columns() || rows() != other.rows()</tt>.
	 */
	public void andNot( BitMatrix other )
	{
		checkDimensionCompatibility( other );
		toBitVector().andNot( other.toBitVector() );
	}

	/**
	 * Returns the number of bits currently in the <tt>true</tt> state.
	 * Optimized for speed. Particularly quick if the receiver is either sparse or dense.
	 */
	public int cardinality()
	{
		return toBitVector().cardinality();
	}

	/**
	 * Sanity check for operations requiring matrices with the same number of columns and rows.
	 */
	protected void checkDimensionCompatibility( BitMatrix other )
	{
		if ( columns != other.columns() || rows != other.rows() )
		{
			throw new IllegalArgumentException( "Incompatible dimensions: (columns,rows)=(" + columns + "," + rows + "), (other.columns,other.rows)=(" + other.columns() + "," + other.rows() + ")" );
		}
	}

	/**
	 * Clears all bits of the receiver.
	 */
	public void clear()
	{
		toBitVector().clear();
	}

	/**
	 * Sets all bits to one (1)
	 */
	public void fill()
	{
		final long[] elems = toBitVector().elements();
		for ( int i = 0; i < elems.length; )
		{
			elems[ i++ ] = 0xFFFFFFFFFFFFFFFFL;
		}
	}


	/**
	 * Cloning this <code>BitMatrix</code> produces a new <code>BitMatrix</code>
	 * that is equal to it.
	 * The clone of the bit matrix is another bit matrix that has exactly the
	 * same bits set to <code>true</code> as this bit matrix and the same
	 * number of columns and rows.
	 *
	 * @return a clone of this bit matrix.
	 */
	public Object clone()
	{
		BitMatrix clone = (BitMatrix) super.clone();
		if ( this.bits != null )
		{
			clone.bits = (long[]) this.bits.clone();
		}
		return clone;
	}

	/**
	 * Returns the number of columns of the receiver.
	 */
	public int columns()
	{
		return columns;
	}

	/**
	 * Checks whether the receiver contains the given box.
	 */
	protected void containsBox( int column, int row, int width, int height )
	{
		if ( column < 0 || column + width > columns || row < 0 || row + height > rows )
		{
			throw new IndexOutOfBoundsException( "column:" + column + ", row:" + row + " ,width:" + width + ", height:" + height );
		}
	}

	/**
	 * Returns a shallow clone of the receiver; calls <code>clone()</code> and casts the result.
	 *
	 * @return a shallow clone of the receiver.
	 */
	public BitMatrix copy()
	{
		return (BitMatrix) clone();
	}

	protected long[] elements()
	{
		return bits;
	}

	/**
	 * You normally need not use this method. Use this method only if performance is critical.
	 * Sets the bit matrix's backing bits, columns and rows.
	 * <b>WARNING:</b> For efficiency reasons and to keep memory usage low, <b>the array is not copied</b>.
	 * So if subsequently you modify the specified array directly via the [] operator, be sure you know what you're doing.
	 *
	 * @throws IllegalArgumentException if <tt>columns &lt; 0 || rows &lt; 0 || columns*rows &gt; bits.length*64</tt>
	 */
	protected void elements( long[] bits, int columns, int rows )
	{
		if ( columns < 0 || columns < 0 || columns * rows > bits.length * QuickBitVector.BITS_PER_UNIT )
		{
			throw new IllegalArgumentException();
		}
		this.bits = bits;
		this.columns = columns;
		this.rows = rows;
	}

	/**
	 * Compares this object against the specified object.
	 * The result is <code>true</code> if and only if the argument is
	 * not <code>null</code> and is a <code>BitMatrix</code> object
	 * that has the same number of columns and rows as the receiver and
	 * that has exactly the same bits set to <code>true</code> as the receiver.
	 *
	 * @param obj the object to compare with.
	 * @return <code>true</code> if the objects are the same;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals( Object obj )
	{
		if ( obj == null || !( obj instanceof BitMatrix ) )
		{
			return false;
		}
		if ( this == obj )
		{
			return true;
		}

		BitMatrix other = (BitMatrix) obj;
		if ( columns != other.columns() || rows != other.rows() )
		{
			return false;
		}

		return toBitVector().equals( other.toBitVector() );
	}

	/**
	 * Applies a procedure to each coordinate that holds a bit in the given state.
	 * Iterates rowwise downwards from [columns()-1,rows()-1] to [0,0].
	 * Useful, for example, if you want to copy bits into an image or somewhere else.
	 * Optimized for speed. Particularly quick if one of the following conditions holds
	 * <ul>
	 * <li><tt>state==true</tt> and the receiver is sparse (<tt>cardinality()</tt> is small compared to <tt>size()</tt>).
	 * <li><tt>state==false</tt> and the receiver is dense (<tt>cardinality()</tt> is large compared to <tt>size()</tt>).
	 * </ul>
	 *
	 * @param state     element to search for.
	 * @param procedure a procedure object taking as first argument the current column and as second argument the current row. Stops iteration if the procedure returns <tt>false</tt>, otherwise continues.
	 * @return <tt>false</tt> if the procedure stopped before all elements where iterated over, <tt>true</tt> otherwise.
	 */
	public boolean forEachCoordinateInState( boolean state, final IntIntProcedure procedure )
	{
		/*
	 this is equivalent to the low level version below, apart from that it iterates in the reverse oder and is slower.
	 if (size()==0) return true;
	 BitVector vector = toBitVector();
	 return vector.forEachIndexFromToInState(0,size()-1,state,
		 new cern.colt.function.IntFunction() {
			 public boolean apply(int index) {
				 return function.apply(index%columns, index/columns);
			 }
		 }
	 );
	 */
		//low level implementation for speed.
		if ( size() == 0 )
		{
			return true;
		}
		BitVector vector = new BitVector( bits, size() );

		long[] theBits = bits;

		int column = columns - 1;
		int row = rows - 1;

		// for each coordinate of bits of partial unit
		long val = theBits[ bits.length - 1 ];
		for ( int j = vector.numberOfBitsInPartialUnit(); --j >= 0; )
		{
			long mask = val & ( 1L << j );
			if ( ( state && ( mask != 0L ) ) || ( ( !state ) && ( mask == 0L ) ) )
			{
				if ( !procedure.apply( column, row ) )
				{
					return false;
				}
			}
			if ( --column < 0 )
			{
				column = columns - 1;
				--row;
			}
		}


		// for each coordinate of bits of full units
		final int bitsPerUnit = QuickBitVector.BITS_PER_UNIT;
		long comparator;
		if ( state )
		{
			comparator = 0L;
		}
		else
		{
			comparator = ~0L;// all 64 bits set
		}

		for ( int i = vector.numberOfFullUnits(); --i >= 0; )
		{
			val = theBits[ i ];
			if ( val != comparator )
			{
				// at least one element within current unit matches.
				// iterate over all bits within current unit.
				if ( state )
				{
					for ( int j = bitsPerUnit; --j >= 0; )
					{
						if ( ( ( val & ( 1L << j ) ) ) != 0L )
						{
							if ( !procedure.apply( column, row ) )
							{
								return false;
							}
						}
						if ( --column < 0 )
						{
							column = columns - 1;
							--row;
						}
					}
				}
				else
				{// unrolled comparison for speed.
					for ( int j = bitsPerUnit; --j >= 0; )
					{
						if ( ( ( val & ( 1L << j ) ) ) == 0L )
						{
							if ( !procedure.apply( column, row ) )
							{
								return false;
							}
						}
						if ( --column < 0 )
						{
							column = columns - 1;
							--row;
						}
					}
				}

			}
			else
			{// no element within current unit matches --> skip unit
				column -= bitsPerUnit;
				if ( column < 0 )
				{
					// avoid implementation with *, /, %
					column += bitsPerUnit;
					for ( int j = bitsPerUnit; --j >= 0; )
					{
						if ( --column < 0 )
						{
							column = columns - 1;
							--row;
						}
					}
				}
			}

		}

		return true;

	}

	/**
	 * Returns from the receiver the value of the bit at the specified coordinate.
	 * The value is <tt>true</tt> if this bit is currently set; otherwise, returns <tt>false</tt>.
	 *
	 * @param column the index of the column-coordinate.
	 * @param row    the index of the row-coordinate.
	 * @return the value of the bit at the specified coordinate.
	 * @throws IndexOutOfBoundsException if <tt>column&lt;0 || column&gt;=columns() || row&lt;0 || row&gt;=rows()</tt>
	 */
	public boolean get( int column, int row )
	{
		if ( column < 0 || column >= columns || row < 0 || row >= rows )
		{
			throw new IndexOutOfBoundsException( "column:" + column + ", row:" + row );
		}
		return QuickBitVector.get( bits, row * columns + column );
	}

	/**
	 * Returns from the receiver the value of the bit at the specified coordinate; <b>WARNING:</b> Does not check preconditions.
	 * The value is <tt>true</tt> if this bit is currently set; otherwise, returns <tt>false</tt>.
	 * <p/>
	 * <p>Provided with invalid parameters this method may return invalid values without throwing any exception.
	 * <b>You should only use this method when you are absolutely sure that the coordinate is within bounds.</b>
	 * Precondition (unchecked): <tt>column&gt;=0 && column&lt;columns() && row&gt;=0 && row&lt;rows()</tt>.
	 *
	 * @param column the index of the column-coordinate.
	 * @param row    the index of the row-coordinate.
	 * @return the value of the bit at the specified coordinate.
	 */
	public boolean getQuick( int column, int row )
	{
		return QuickBitVector.get( bits, row * columns + column );
	}

	/**
	 * Returns a hash code value for the receiver.
	 */
	public int hashCode()
	{
		return toBitVector().hashCode();
	}

	/**
	 * Performs a logical <b>NOT</b> on the bits of the receiver.
	 */
	public void not()
	{
		toBitVector().not();
	}

	/**
	 * Performs a logical <b>OR</b> of the receiver with another bit matrix.
	 * The receiver is modified so that a bit in it has the
	 * value <code>true</code> if and only if it either already had the
	 * value <code>true</code> or the corresponding bit in the other bit matrix
	 * argument has the value <code>true</code>.
	 *
	 * @param other a bit matrix.
	 * @throws IllegalArgumentException if <tt>columns() != other.columns() || rows() != other.rows()</tt>.
	 */
	public void or( BitMatrix other )
	{
		checkDimensionCompatibility( other );
		toBitVector().or( other.toBitVector() );
	}

	/**
	 * Constructs and returns a new matrix with <tt>width</tt> columns and <tt>height</tt> rows which is a copy of the contents of the given box.
	 * The box ranges from <tt>[column,row]</tt> to <tt>[column+width-1,row+height-1]</tt>, all inclusive.
	 *
	 * @param column the index of the column-coordinate.
	 * @param row    the index of the row-coordinate.
	 * @param width  the width of the box.
	 * @param height the height of the box.
	 * @throws IndexOutOfBoundsException if <tt>column&lt;0 || column+width&gt;columns() || row&lt;0 || row+height&gt;rows()</tt>
	 */
	public BitMatrix part( int column, int row, int width, int height )
	{
		if ( column < 0 || column + width > columns || row < 0 || row + height > rows )
		{
			throw new IndexOutOfBoundsException( "column:" + column + ", row:" + row + " ,width:" + width + ", height:" + height );
		}
		if ( width <= 0 || height <= 0 )
		{
			return new BitMatrix( 0, 0 );
		}

		BitMatrix subMatrix = new BitMatrix( width, height );
		subMatrix.replaceBoxWith( 0, 0, width, height, this, column, row );
		return subMatrix;
	}

	/**
	 * Sets the bit at the specified coordinate to the state specified by <tt>value</tt>.
	 *
	 * @param column the index of the column-coordinate.
	 * @param row    the index of the row-coordinate.
	 * @param value  the value of the bit to be copied into the specified coordinate.
	 * @throws IndexOutOfBoundsException if <tt>column&lt;0 || column&gt;=columns() || row&lt;0 || row&gt;=rows()</tt>
	 */
	public void put( int column, int row, boolean value )
	{
		if ( column < 0 || column >= columns || row < 0 || row >= rows )
		{
			throw new IndexOutOfBoundsException( "column:" + column + ", row:" + row );
		}
		QuickBitVector.put( bits, row * columns + column, value );
	}

	/**
	 * Sets the bit at the specified coordinate to the state specified by <tt>value</tt>; <b>WARNING:</b> Does not check preconditions.
	 * <p/>
	 * <p>Provided with invalid parameters this method may return invalid values without throwing any exception.
	 * <b>You should only use this method when you are absolutely sure that the coordinate is within bounds.</b>
	 * Precondition (unchecked): <tt>column&gt;=0 && column&lt;columns() && row&gt;=0 && row&lt;rows()</tt>.
	 *
	 * @param column the index of the column-coordinate.
	 * @param row    the index of the row-coordinate.
	 * @param value  the value of the bit to be copied into the specified coordinate.
	 */
	public void putQuick( int column, int row, boolean value )
	{
		QuickBitVector.put( bits, row * columns + column, value );
	}

	/**
	 * Replaces a box of the receiver with the contents of another matrix's box.
	 * The source box ranges from <tt>[sourceColumn,sourceRow]</tt> to <tt>[sourceColumn+width-1,sourceRow+height-1]</tt>, all inclusive.
	 * The destination box ranges from <tt>[column,row]</tt> to <tt>[column+width-1,row+height-1]</tt>, all inclusive.
	 * Does nothing if <tt>width &lt;= 0 || height &lt;= 0</tt>.
	 * If <tt>source==this</tt> and the source and destination box intersect in an ambiguous way, then replaces as if using an intermediate auxiliary copy of the receiver.
	 *
	 * @param column       the index of the column-coordinate.
	 * @param row          the index of the row-coordinate.
	 * @param width        the width of the box.
	 * @param height       the height of the box.
	 * @param source       the source matrix to copy from(may be identical to the receiver).
	 * @param sourceColumn the index of the source column-coordinate.
	 * @param sourceRow    the index of the source row-coordinate.
	 * @throws IndexOutOfBoundsException if <tt>column&lt;0 || column+width&gt;columns() || row&lt;0 || row+height&gt;rows()</tt>
	 * @throws IndexOutOfBoundsException if <tt>sourceColumn&lt;0 || sourceColumn+width&gt;source.columns() || sourceRow&lt;0 || sourceRow+height&gt;source.rows()</tt>
	 */
	public void replaceBoxWith( int column, int row, int width, int height, BitMatrix source, int sourceColumn, int sourceRow )
	{
		this.containsBox( column, row, width, height );
		source.containsBox( sourceColumn, sourceRow, width, height );
		if ( width <= 0 || height <= 0 )
		{
			return;
		}

		if ( source == this )
		{
			Rectangle destRect = new Rectangle( column, row, width, height );
			Rectangle sourceRect = new Rectangle( sourceColumn, sourceRow, width, height );
			if ( destRect.intersects( sourceRect ) )
			{// dangerous intersection
				source = source.copy();
			}
		}

		BitVector sourceVector = source.toBitVector();
		BitVector destVector = this.toBitVector();
		int sourceColumns = source.columns();
		for ( ; --height >= 0; row++, sourceRow++ )
		{
			int offset = row * columns + column;
			int sourceOffset = sourceRow * sourceColumns + sourceColumn;
			destVector.replaceFromToWith( offset, offset + width - 1, sourceVector, sourceOffset );
		}
	}

	/**
	 * Sets the bits in the given box to the state specified by <tt>value</tt>.
	 * The box ranges from <tt>[column,row]</tt> to <tt>[column+width-1,row+height-1]</tt>, all inclusive.
	 * (Does nothing if <tt>width &lt;= 0 || height &lt;= 0</tt>).
	 *
	 * @param column the index of the column-coordinate.
	 * @param row    the index of the row-coordinate.
	 * @param width  the width of the box.
	 * @param height the height of the box.
	 * @param value  the value of the bit to be copied into the bits of the specified box.
	 * @throws IndexOutOfBoundsException if <tt>column&lt;0 || column+width&gt;columns() || row&lt;0 || row+height&gt;rows()</tt>
	 */
	public void replaceBoxWith( int column, int row, int width, int height, boolean value )
	{
		containsBox( column, row, width, height );
		if ( width <= 0 || height <= 0 )
		{
			return;
		}

		BitVector destVector = this.toBitVector();
		for ( ; --height >= 0; row++ )
		{
			int offset = row * columns + column;
			destVector.replaceFromToWith( offset, offset + width - 1, value );
		}
	}

	/**
	 * Returns the number of rows of the receiver.
	 */
	public int rows()
	{
		return rows;
	}

	/**
	 * Returns the size of the receiver which is <tt>columns()*rows()</tt>.
	 */
	public int size()
	{
		return columns * rows;
	}

	public int memorySize()
	{
		return bits.length * Long.SIZE / Byte.SIZE;
	}

	/**
	 * Converts the receiver to a bitvector.
	 * In many cases this method only makes sense on one-dimensional matrices.
	 * <b>WARNING:</b> The returned bitvector and the receiver share the <b>same</b> backing bits.
	 * Modifying either of them will affect the other.
	 * If this behaviour is not what you want, you should first use <tt>copy()</tt> to make sure both objects use separate internal storage.
	 */
	public BitVector toBitVector()
	{
		return new BitVector( bits, size() );
	}

	/**
	 * Returns a (very crude) string representation of the receiver.
	 */
	public String toString()
	{
		return toBitVector().toString();
	}

	/**
	 * Performs a logical <b>XOR</b> of the receiver with another bit matrix.
	 * The receiver is modified so that a bit in it has the
	 * value <code>true</code> if and only if one of the following statements holds:
	 * <ul>
	 * <li>The bit initially has the value <code>true</code>, and the
	 * corresponding bit in the argument has the value <code>false</code>.
	 * <li>The bit initially has the value <code>false</code>, and the
	 * corresponding bit in the argument has the value <code>true</code>.
	 * </ul>
	 *
	 * @param other a bit matrix.
	 * @throws IllegalArgumentException if <tt>columns() != other.columns() || rows() != other.rows()</tt>.
	 */
	public void xor( BitMatrix other )
	{
		checkDimensionCompatibility( other );
		toBitVector().xor( other.toBitVector() );
	}

	/**
	 * Expands designated matrix in X to left and right with expandX size and in Y to top nad bottom with expandY size
	 *
	 * @param bm      {@link BitMatrix} instance to accept result. Is cleared before usage.
	 *                If width and height of matrix not coincide with input BufferedImage dimensions or matrix is {@code null},
	 *                new BitMatrix is returned, original one remain unchanged
	 * @param expandX expand size to left and right directions
	 * @param expandY expand size to top and bottom directions
	 * @return new {@link BitMatrix} created from designated one or <b>null<b> if input matrix is <b>null</b> too
	 */
	public static BitMatrix expandBitMatrix( BitMatrix bm, int expandX, int expandY )
	{
		return expandBitMatrix( bm, expandX, expandY, BORDER_ZERO );
	}

	/**
	 * Expands matrix with same extends to all 4 directions (E, W, S, N)
	 *
	 * @param bm     {@link BitMatrix} to expand
	 * @param expand int value to expand matrix with it in all directions
	 * @return new expanded {@link BitMatrix}
	 */
	public static BitMatrix expandBitMatrix( BitMatrix bm, int expand )
	{
		return expandBitMatrix( bm, expand, expand );
	}

	/**
	 * Expands designated matrix in X to left and right with expandX size and in Y to top nad bottom with expandY size
	 *
	 * @param bm         {@link BitMatrix} instance to accept result. Is cleared before usage.
	 *                   If width and height of matrix not coincide with input BufferedImage dimensions or matrix is {@code null},
	 *                   new BitMatrix is returned, original one remain unchanged
	 * @param expandX    expand size to left and right directions
	 * @param expandY    expand size to top and bottom directions
	 * @param borderType type of wanted border, may be {@link #BORDER_ZERO}, {@link #BORDER_COPY}, {@link #BORDER_WRAP}
	 *                   or {@link #BORDER_REFLECT}
	 * @return new {@link BitMatrix}
	 */
	public static BitMatrix expandBitMatrix( BitMatrix bm, int expandX, int expandY, int borderType )
	{
		if ( borderType < BORDER_MIN_VALUE || borderType > BORDER_MAX_VALUE )
		{
			borderType = BORDER_ZERO;// set border type to least excessive value (nothing to do)
		}
		int w = bm.columns();
		int h = bm.rows();
		if ( bm == null )
		{
			return null;
		}
		BitMatrix res = new BitMatrix( w + expandX * 2, h + expandY * 2 );
		res.replaceBoxWith( expandX, expandY, w, h, bm, 0, 0 );
		int w2 = res.columns();
		int h2 = res.rows();
		switch ( borderType )
		{
			/**
			 * Most simple case, simplest possible with nothig to do
			 */
			case BORDER_ZERO:
				// do nothing as border is zeroed on creation
				break;
				/**
				 * Most complicated case, but really not so complicated as BORDER_COPY case
				 */
			case BORDER_REFLECT:
				for( int i = 1; i <= expandX; i++ )
				{
					// reflect left vertical side
					res.replaceBoxWith( expandX - i, expandY, 1, h, res, expandX - 1 + i,  expandY );
					// reflect right vertical side
					res.replaceBoxWith( w + expandX + i - 1, expandY, 1, h, res, w + expandX - i,  expandY );
				}

				for( int i = 1; i <= expandY; i++ )
				{
					// reflect top horizontal side
					res.replaceBoxWith( 0, expandY - i, w2, 1, res, 0,  expandY - 1 + i );
					// reflect bottom horizontal side
					res.replaceBoxWith( 0, h + expandY + i - 1, w2, 1, res, 0,  h + expandY - i);
				}

				break;
			case BORDER_WRAP:
				// process horizontal sides first
				res.replaceBoxWith( expandX, 0, w, expandY, res, expandX, h );// fill top side
				res.replaceBoxWith( expandX, h + expandY, w, expandY, res, expandX, expandY );// fill bottom side
				// process vertical sides second
				// fill remaining h sides, as center is filled now from top to bottom
				res.replaceBoxWith( 0, 0, expandX, h2, res, w, 0 );// fill left side
				res.replaceBoxWith( w + expandX, 0, expandX, h2, res, expandX, 0 );// fill right side
				break;
			case BORDER_COPY:
				// simple but reaaly rather stupid operation

				// first fill vertical sides
				// Left side filling
				int width2Fill = expandX;
				int height2Fill = expandY;
				int copyWidth = 1;
				int copyHeight = 1;
				int rightReadX = w2 - expandX - 1;
				int bottomReadY = h2 - expandY - 1;
				do
				{
					if ( copyWidth > 0 )
					{
						// Left side
						res.replaceBoxWith( width2Fill - copyWidth, expandY, copyWidth, h, res, width2Fill, expandY );

						// Right side
						res.replaceBoxWith( w2 - width2Fill, expandY, copyWidth, h, res, rightReadX, expandY );
					}

					if ( copyHeight > 0 )
					{
						// top side
						res.replaceBoxWith( expandX, height2Fill - copyHeight, w, copyHeight, res, expandX, height2Fill );

						// bottom side
						res.replaceBoxWith( expandX, h2 - height2Fill, w, copyHeight, res, expandX, bottomReadY );
					}

					width2Fill -= copyWidth;
					height2Fill -= copyHeight;

					copyWidth += copyWidth;
					copyHeight += copyHeight;
					if ( width2Fill < copyWidth )
						copyWidth = width2Fill;
					if ( height2Fill < copyHeight)
						copyHeight = height2Fill;
				}
				while ( ( width2Fill > 0 ) || ( height2Fill > 0 ) );
				// fill corners now
				int filledCnt = 0;// at finish count must be 4,  as 4 corners we have
				// create corner size matrix with zero content
				BitMatrix cornerMat = new BitMatrix( expandX, expandY );

				// fill all 4 corners in follow order UL, UR, LR, LL

				res.replaceBoxWith( 0, 0, expandX, expandY, bm.get( 0, 0 ) );// UL
				res.replaceBoxWith( w + expandX, 0, expandX, expandY, bm.get( w - 1, 0 ) );// UR
				res.replaceBoxWith( w + expandX, h + expandY, expandX, expandY, bm.get( w - 1, h - 1 ) );// LR
				res.replaceBoxWith( 0, h + expandY, expandX, expandY, bm.get( 0, h - 1 ) );// LL
				break;
		}
		return res;
	}

}