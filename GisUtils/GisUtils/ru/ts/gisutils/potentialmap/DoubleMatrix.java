/**
 * Created on 30.01.2008 14:03:04 2008 by Syg for project in
 * 'ru.ts.gisutils.potentialmap' of 'test'
 */
package ru.ts.gisutils.potentialmap;

import ru.ts.gisutils.common.Arrs;

/**
 * @author Syg
 */
public class DoubleMatrix
{

	/**
	 * potential map building mode Maximum
	 */
	public static final int	  PM_MAX	      = 0;

	/**
	 * potential map building mode Additive
	 */
	public static final int	  PM_SUM	      = 1;

	/**
	 * potential map building mode Minimum
	 */
	public static final int	  PM_MIN	      = 2;

	/**
	 * potential map building mode Average
	 */
	public static final int	  PM_AVR	      = 3;

	private static final int	PM_LAST	      = PM_AVR;

	/**
	 * Default value for the matrix. If the matrix cell contains this value, it
	 * means that it was unassigned.
	 */
	public static final double	DEFAULT_VALUE	= 0.0;

	/**
	 * matrix width
	 */
	int	                      _w;

	/**
	 * matrix Height
	 */
	int	                      _h;

	/**
	 * building mode (0..3)
	 */
	int	                      _mode;

	/**
	 * cell number
	 */
	private int	              _size;

	/**
	 * potential matrix
	 */
	double[]	                  _matrix;

	/**
	 * array for counts if PM_AVR or point index for PM_MIN and PM_MAX
	 */
	short[]	                  _counts;

	/**
	 * main constructor
	 * 
	 * @param w
	 *            matrix width
	 * @param h
	 *            matrix height
	 * @param mode
	 *            mode of map building
	 * @throws IllegalArgumentException
	 *             if any of argument has invalid value
	 */
	public DoubleMatrix( int w, int h, int mode )
	        throws IllegalArgumentException
	{
		init( w, h, mode );
	}

	/**
	 * constructor to make a new matrix of the same size and more as this one.
	 * Values are not copied
	 * 
	 * @param matr
	 *            to make copy from it
	 */
	public DoubleMatrix( DoubleMatrix matr )
	{
		this( matr.width(), matr.height(), matr.mode() );
	}

	/**
	 * creates copy of this matrix
	 * 
	 * @return a new doubleMatrix with copy of this matrix
	 */
	public DoubleMatrix copy()
	{
		DoubleMatrix mat = new DoubleMatrix( this );
		System.arraycopy( this._matrix, 0, mat._matrix, 0, size() );
		if ( mat._counts != null )
			System.arraycopy( this._counts, 0, mat._counts, 0, size() );
		return mat;
	}

	private void init( int w, int h, int mode )
	{
		if ( w < 1 )
			throw new IllegalArgumentException( "invalid width value " + w );
		_w = w;

		if ( h < 1 )
			throw new IllegalArgumentException( "invalid height value " + w );
		_h = h;
		if ( ( mode < 0 ) || ( mode > PM_LAST ) )
			throw new IllegalArgumentException( "invalid mode value " + mode );
		_size = w * h;
		_mode = mode;
		switch ( mode )
		{
		case PM_AVR:
		case PM_MIN:
		case PM_MAX:
			if ( _counts != null )
				_counts = null;
			_counts = new short[_size];
			break;
		default:
			_counts = null;
			break;
		}
		_matrix = new double[_size];

		// set default values for all [unchanged] pixels
		if ( DEFAULT_VALUE != 0.0F )
			setAll( DEFAULT_VALUE );
	}

	/**
	 * @return the _h
	 */
	public final int height()
	{
		return _h;
	}

	/**
	 * @return the _mode
	 */
	public final int mode()
	{
		return _mode;
	}

	/**
	 * @return the _w
	 */
	public final int width()
	{
		return _w;
	}

	/**
	 * matrix cells number
	 * 
	 * @return cells number in this matrix
	 */
	public final int size()
	{
		return _size;
	}

	public void zeroAll()
	{
		setAll( 0.0F );
	}

	public void setAll( double val )
	{
		Arrs.arraySet( _matrix, val );
	}

	public boolean assign( DoubleMatrix fm )
	{
		if ( fm == null )
			return false;
		if ( !( fm instanceof DoubleMatrix ) )
			return false;
		init( fm._w, fm._h, fm._mode );
		System.arraycopy( fm._matrix, 0, _matrix, 0, _size );
		if ( _mode == PM_AVR )
			System.arraycopy( fm._counts, 0, _counts, 0, _size );
		return true;
	}

	public void addBy( float val )
	{
		Arrs.arrayAdd( _matrix, val );
	}

	public void multiplyBy( double val )
	{
		Arrs.arrayMultiply( _matrix, val );
	}

	public double findMax()
	{
		if ( mode() == PM_AVR )
		{
			double max = 0.0F;
			int count;
			for ( int index = 0; index < size(); index++ )
			{
				if ( ( count = _counts[ index ] ) > 0 )
				{
					double val = _matrix[ index ] / count;
					if ( val > max )
						max = val;
				}
			}
			return max;
		}
		else
			return Arrs.arrayFindMax( _matrix );
	}

	/**
	 * <pre>
	 *   creates a new matrix by averaging parent one by window 3x3 size. That is
	 *   every value of a new matrix is a avergae of 9 surrounding values of a
	 *   parent matrix.
	 *   
	 *   new matrix parent matrix
	 *    + + +        v1 v 2v3 
	 *    + v + 		  v4 v5 v6 
	 *    + + + 		  v7 v8 v9
	 *   
	 *   v = (v1+v2+v3+v4+v5+v6+v7+v8+v9) / 9
	 *   
	 *   Values on both 4 boundaries are remining the same with parent values
	 * </pre>
	 * 
	 * @return a new matrix with values averaged by 3x3 windows above this
	 *         matrix
	 */
	public DoubleMatrix aver3x3()
	{
		DoubleMatrix fm = new DoubleMatrix( _w, _h, _mode );
		double sum = 0.0F;
		for ( int i = 1; i < ( _h - 1 ); i++ )
		{
			int index1 = ( i - 1 ) * _w;
			int index2 = index1 + _w;
			int index3 = index2 + _w;
			for ( int j = 1; j < ( _w - 1 ); j++ )
			{
				for ( int k = 0; k < 3; k++ )
				{
					sum += _matrix[ index1 + k ];
					sum += _matrix[ index2 + k ];
					sum += _matrix[ index3 + k ];
				}
				index1++;
				index2++;
				index3++;
				fm.set_item( i, j, sum / 9.0 );
			}
		}
		return fm;
	}

	/**
	 * gets item value
	 * 
	 * @param row
	 *            item row
	 * @param column
	 *            item column
	 * @return value of item at designated row and column
	 * @throws IndexOutOfBoundsException
	 *             if row and/or column are out of matrix bounds
	 */
	public double get_item( int row, int column )
	        throws IndexOutOfBoundsException
	{
		return get_item( row * _w + column );
	}

	/**
	 * gets index of point under this potential
	 * 
	 * @param row
	 *            item row
	 * @param column
	 *            item column
	 * @return index of point that generated item designated by row and column.
	 *         If no potential at this pixel, -1 is returned.
	 * 
	 * @throws IllegalAccessError
	 *             is thrown if there isn't such info at all
	 * @throws IndexOutOfBoundsException
	 *             thrown if no such pixel in matrix
	 */
	public short get_index( int row, int column ) throws IllegalAccessError, IndexOutOfBoundsException 
	{
		switch ( _mode )
		{
		case PM_AVR:
		case PM_SUM:
			throw new IllegalAccessError(
			        "Illegal mode for this class to get point index, set PM_MIN or PM_MAX to have it" );
		default:
			return (short)(_counts[ row * _w + column ] - 1);
		}
	}

	private double get_item( int index )
	{
		if ( _mode == PM_AVR )
		{
			short cnt;
			if ( ( cnt = _counts[ index ] ) > 1 )
				return _matrix[ index ] / (double) cnt;
		}
		return _matrix[ index ];
	}

	/**
	 * finds next value different from default value
	 * 
	 * @param finder
	 * @return <code>true</code> if value found, position in matrix and value
	 *         are returned in the Finder object used as parameter. If no such value
	 *         found, <code>false</code> is returned
	 */
	public boolean find_value( Finder finder )
	{
		for ( int index = finder.position; index < _size; index++ )
			if ( _matrix[ index ] != DEFAULT_VALUE )
			{
				finder.position = index + 1; /* set next search index */
				finder.value = get_item( index ); /* set value found */
				return true;
			}
		finder.position = _size;
		finder.value = DEFAULT_VALUE;
		return false;
	}

	/**
	 * finds string of values not equal to default value
	 * @param mf MultiFinder internal class instance
	 * @return <code>true</code> if found or <code>false</code> if not found 
	 */
	public boolean find_values( MultiFinder mf )
	{
		for ( int index = mf.position + mf.count; index < _size; index++ )
			if ( _matrix[ index ] != DEFAULT_VALUE )
			{
				int end = index + 1;
				for( ; end < _size; end++ )
					if ( _matrix[ end ] == DEFAULT_VALUE )
						break;
				mf.position = index; /* set next search index */
				mf.count = end - index; /* set count of cells detected */
				if ( (mf.values == null) || (mf.values.length < mf.count) )
					mf.values = new double[ mf.count ];
				System.arraycopy( this._matrix, index, mf.values, 0, mf.count );
				return true;
			}
		mf.position = _size;
		mf.count = 0;
		return false;
	}
	
	public void set_item( int row, int column, double val )
	        throws IndexOutOfBoundsException
	{
		int index = row * _w + column;
		switch ( _mode )
		{
		case PM_MAX:
			if ( val > _matrix[ index ] )
				_matrix[ index ] = val;
			break;
		case PM_SUM:
			_matrix[ index ] += val;
			break;
		case PM_AVR:
			_matrix[ index ] += val;
			_counts[ index ]++;
			break;
		case PM_MIN:
			if ( _matrix[ index ] > val )
				_matrix[ index ] = val;
		}
	}

	public void set_item( int row, int column, double val, short pntind )
	        throws IndexOutOfBoundsException
	{
		int index = row * _w + column;
		switch ( _mode )
		{
		case PM_MAX:
			if ( val > _matrix[ index ] )
			{
				_matrix[ index ] = val;
				_counts[ index ] = ++pntind;
			}
			break;
		case PM_SUM:
			_matrix[ index ] += val;
			break;
		case PM_AVR:
			_matrix[ index ] += val;
			_counts[ index ]++;
			break;
		case PM_MIN:
			if ( _matrix[ index ] > val )
			{
				_matrix[ index ] = val;
				_counts[ index ] = ++pntind;
			}
		}
	}

	/**
	 * <pre>
	 *   ******************************************************** Finder class definitions *****
	 * </pre>
	 */

	/**
	 * @author Syg
	 */
	static class Finder
	{
		/* the value found */
		double		value;

		/* position of the value found*/
		int		    position;

		private int	_w;

		private int	_h;

		Finder( DoubleMatrix mat )
		{
			value = DoubleMatrix.DEFAULT_VALUE;
			_w = mat.width();
			_h = mat.height();
		}

		/**
		 * no checking for index out of bounds
		 * 
		 * @param x
		 *            column of matrix (begin from 0)
		 * @param y
		 *            row of matrix (begin from 0)
		 */
		public void set_position( int x, int y )
		{
			position = y * _w + x;
		}

		/**
		 * gets x position of last found value (column)
		 * 
		 * @return x position for the value different from default level
		 */
		public int get_row()
		{
			return position % _w;
		}

		/**
		 * gets y position of last found value (row)
		 * 
		 * @return y position for the value different from default level
		 */
		public int get_column()
		{
			return position / _w;
		}

		/**
		 * detects if value was found at all
		 * 
		 * @return <code>true</code> if value was found on last call or
		 *         <code>false</code> if there was no call or last call was
		 *         unsuccessful.
		 */
		public boolean was_found()
		{
			return value != DEFAULT_VALUE;
		}
	}

	static class MultiFinder
	{
		int position;
		int count;
		double[] values;
		private int	_w;
		private int	_h;
		
		MultiFinder( DoubleMatrix mat )
		{
			_w = mat.width();
			_h = mat.height();
		}

		/**
		 * no checking for index out of bounds
		 * 
		 * @param x
		 *            column of matrix (begin from 0)
		 * @param y
		 *            row of matrix (begin from 0)
		 */
		public void set_position( int x, int y )
		{
			position = y * _w + x;
		}

		/**
		 * gets row for position in found array
		 * 
		 * @param pos_off offset into position of this class instance
		 * @return row position for the value different from default level
		 */
		public int get_row( int pos_off )
		{
			return ( position + pos_off )% _w;
		}

		/**
		 * gets column for position in found array
		 * 
		 * @return y position for the value different from default level
		 */
		public int get_column( int pos_off )
		{
			return ( position + pos_off ) / _w;
		}

		/**
		 * detects if value was found at all
		 * 
		 * @return <code>true</code> if value was found on last call or
		 *         <code>false</code> if there was no call or last call was
		 *         unsuccessful.
		 */
		public boolean was_found()
		{
			return count != 0;
		}

	}
}
