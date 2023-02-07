/**
 * Created on 31.01.2008 15:05:08 2008 by Syg for project in
 * 'ru.ts.gisutils.potentialmap' of 'test'
 */
package ru.ts.gisutils.potentialmap;

/**
 * @author Sygky ++++++++++++++++++++++++++++++++++++++++++++++++++
 * 
 * IMPLEMENTATION of the class to keep the shape of the potential field surface
 * line vertical shear it is done by keeping the array of vertical values for
 * each horisontal equal step up to but excluding last,where value goes to zero,
 * that is the line of the function meets with abscciss axe
 * 
 * +++++++++++++++++++++++++++++++++++++++++++++++++++
 * 
 */
public class GeomProfile
{

	private static final double[]	_curveGauss	= new double[] { 0.95, 0.9,
	        0.45, 0.3, 0.2	                    };

	private static final double[]	_curveConic	= new double[] {};

	/**
	 * length of a step distance along X
	 */
	private double	              _x_step;

	private double	              _max_x;

	/**
	 * follow array contains the values of Y for each of step along 
	 * Y axis
	 * ^ 
	 * |
	 * * Y0 - the top value in the point itself (always 1.0) not stored
	 * |  
	 * | 
	 * | 
	 * | 
	 * |       * Y1 for step 1 (value of potential on the distance = XStep 
	 * | 	   | 
	 * |       | 
	 * |       | 
	 * |       |       * Y2 for step 2 (distance = XStep * 2)
	 * |       |       |        Y last for step 3. Last Y is always 0.0 and not stored. 
	 * 0-------1-------2-------*-------> X axis 
	 * | XStep | XStep | XStep |
	 * 
	 * So Count now will be = 4, while YValues array length = 2 as Max Y always
	 * is the 1.0 and last one is always 0.0
	 * 
	 * Please NOTE: Max Y ALWAYS = 1.0!!! So if you want to see near natural
	 * shape of the curve, use all consequent Yi value <= Yi+1 value
	 * 
	 * For example, the simples possible shape for this approach is the cone
	 * which can be specified by no values of Y and only XStep, which stands for
	 * the half of the whole cone base line width
	 */
	private double[]	          _y_values;

	/**
	 * means that we use relative values from 0.0 to 1.0 (_abs_value = NAN) or
	 * absolute as loaded in this item ((_abs_value != NAN) && (_abs_value > 0.0))
	 */
	private float	              _abs_value;

	/**
	 * main constructor with absolute value set to NaN to allow relative mode
	 * usage
	 * 
	 * @param profile_points
	 *            values of profile points
	 * @param max_x
	 *            double value for the radious of the phenomenon with a maximum
	 *            value
	 */
	public GeomProfile( double[] profile_points, double max_x )
	{
		_y_values = (double[]) profile_points.clone();
		setMaxX( max_x );
		_abs_value = Float.NaN;
	}

	/**
	 * main constructor
	 * 
	 * @param profile_points
	 *            values of profile points
	 * @param max_x
	 *            double value for the radious of the phenomenon with a maximum
	 *            value
	 * @param abs_value
	 *            double value for the absolute value of the phenomenon
	 */
	public GeomProfile( double[] profile_points, double max_x, float abs_value )
	{
		_y_values = (double[]) profile_points.clone();
		setMaxX( max_x );
		_abs_value = abs_value;
	}

	/**
	 * assignes new values for profile
	 * 
	 * @param steps
	 *            new Y values for each step along X
	 * @return <code>true</code> if success else <code>false</code>
	 */
	public boolean assign( double[] steps )
	{
		if ( steps == null )
			return false;
		_y_values = (double[]) steps.clone();
		return true;
	}

	public int size()
	{
		return _y_values.length + 2;
	}

	private void checkIndex( int index )
	{
		if ( ( index < 0 ) || ( index >= size() ) )
			throw new IndexOutOfBoundsException( "index = " + index
			        + ", max. allowed is " + ( size() - 1 ) );
	}

	/**
	 * returns maximum X (radius) of the phenomenon to work with if relative
	 * value = 1.0
	 * 
	 * @return the value of maximum phenomenon radius to be
	 */
	public double getMaxX()
	{
		return _max_x;
	}

	/**
	 * returns radius of the phenomenon to work with if relative value = 1.0
	 * 
	 * @return the value of maximum phenomenon radius to be
	 */
	public double getRadius()
	{
		return _max_x;
	}

	public void setMaxX( double max_x )
	{
		_max_x = Math.abs( max_x );
		_x_step = _max_x / ( _y_values.length + 1 );
	}

	/**
	 * gets Y values set on the step points
	 * 
	 * @param index
	 *            index of step ( 0 stands fo the center of phenomenon)
	 * @return Y value of the step index
	 * @throws IndexOutOfBoundsException
	 *             if index out of bounds
	 */
	public double getY( int index )
	{
		index = Math.abs( index );
		// checkIndex( index );
		if ( index == 0 )
			return 1.0;
		if ( index > _y_values.length )
			return 0.0;
		return _y_values[ index - 1 ];
	}

	/**
	 * gets value of a phenomenon by the distance from the centre
	 * 
	 * @param distance
	 *            distance from the centre of a phenomenon
	 * @return value of phenomenon on the designated distance from the centre.
	 *         At centre it is always 1.0
	 */
	public double getY( double distance )
	{
		distance = Math.abs( distance );
		if ( distance >= getMaxX() )
			return 0.0;
		if ( distance == 0.0 )
			return 1.0;
		/* Result should be interpolated */
		int index = (int) ( distance / _x_step ); /* index of array (0 stands for the zero item) */
		double x0 = index * _x_step; // X for the left known Y
		double y1 = getY( index ); // left known Y
		double y2 = getY( index + 1 ); // right known Y
		return y1 + ( ( distance - x0 ) / _x_step ) * ( y2 - y1 );
	}

	/**
	 * sets a new value for the step between Y values of the array
	 * 
	 * @param step
	 *            new step value
	 */
	public void set_x_step( double step )
	{
		_x_step = Math.abs( step );
		_max_x = _x_step * ( _y_values.length + 1 );
	}

	public double get_x_step()
	{
		return _x_step;
	}

	public void add( double val )
	{
		final int _size = _y_values.length;
		final double[] arr = new double[_size + 1];
		System.arraycopy( _y_values, 0, arr, 0, _size );
		arr[ _size ] = val;
		_x_step = _max_x / ( _size + 1 );
		_y_values = arr;
	}

	/**
	 * defines if we should use absolute mode for the center of phenomenon, not
	 * 1.0 as for relative mode
	 * 
	 * @return true if yes, we uses absolute value
	 */
	public boolean useAbsValue()
	{
		return !Double.isNaN( _abs_value ) && ( _abs_value > 0.0 );
	}

	public float getAbsValue()
	{
		return _abs_value;
	}

	public void setAbsValue( float val )
	{
		_abs_value = Math.abs( val );
	}

	/**
	 * gets Gauss profile with an user radious of the 1.0 phenomenon
	 * 
	 * @param max_x
	 *            user radious of phenomenon for the max item
	 * @return profile for the Gauss curve to draw
	 */
	public static GeomProfile getGaussProfile( double max_x )
	{
		return new GeomProfile( _curveGauss, max_x );
	}

	/**
	 * gets Gauss profile with an user radious ofr the 1.0 phenomenon
	 * 
	 * @param max_x
	 *            user radious of phenomenon for the max item
	 * @return profile for the Gauss curve to draw
	 */
	public static GeomProfile getConicProfile( double max_x )
	{
		return new GeomProfile( _curveConic, max_x );
	}

}
