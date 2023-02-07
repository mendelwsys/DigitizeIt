/**
 * Created on 25.03.2008 13:27:47 2008 by Syg for project in
 * 'ru.ts.gisutils.common.geometry' of 'test'
 */
package ru.ts.gisutils.geometry;

import com.vividsolutions.jts.geom.Envelope;
import ru.ts.gisutils.common.Sys;

import java.awt.geom.Rectangle2D;

/**
 * implements 3D bounding box (volume) for a spatial index processing
 * 
 * @author Syg
 */
public class GisVolume extends Rectangle2D.Double implements IGisVolume
{
	/**
	 * mininum z for this box
	 */
	//public double	z;
	
	/**
	 * the depth in z axis, max z stands for z + depth 
	 */
//	public double	depth;
	
	public GisVolume()
	{
		setEmpty();
	}
	
	public GisVolume(IGisVolumeGet volume)
	{
		x = volume.getX();
		y = volume.getY();
		//z = volume.getZ();
		width = volume.getWidth();
		height = volume.getHeight();
		//depth = volume.getDepth();
	}

/*	public GisVolume( Rectangle2D rect )
	{
		super( rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight() );
	}
*/	
	public GisVolume(double x0, double y0, double width0, double height0 )
	{
		super( x0, y0, width0, height0 );
	}

	public GisVolume( Envelope env )
	{
		super( env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.geometry.IDimension#setMinDimension(int, double)
	 */
	public void setMinDimension( int dim_index, double new_val )
	        throws IndexOutOfBoundsException
	{
		switch ( dim_index )
		{
		case 0:
			((Rectangle2D.Double)this).x = new_val;
			break;
		case 1:
			((Rectangle2D.Double)this).y = new_val;
			break;
		case 2:
//			z = new_val;
			break;
		default:
			throw new IndexOutOfBoundsException( "dimension " + dim_index
			        + " was pointed. 0 for X, 1 for Y or 2 for Z was expected" );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.geometry.IDimension#setMaxDimension(int, double)
	 */
	public void setMaxDimension( int dim_index, double new_val )
	        throws IndexOutOfBoundsException
	{
		switch ( dim_index )
		{
		case 0:
			double x1 = Math.min( getMinX(), new_val );
			double x2 = Math.max( getMaxX(), new_val );
			super.setRect( x1, super.getY(), x2 - x1, super.getHeight() );
			break;
		case 1:
			double y1 = Math.min( getMinY(), new_val );
			double y2 = Math.max( getMaxY(), new_val );
			super.setRect( super.getX(), y1, super.getWidth(), y2 - y1 );
			break;
		case 2:
/*			double z1 = Math.min(getMinZ(), new_val );
			double z2 = Math.max(getMaxZ(), new_val );
			z = z1;
			depth = z2 - z1;
*/			break;
		default:
			throw new IndexOutOfBoundsException( "dimension " + dim_index
			        + " was pointed. 0 for X, 1 for Y or 2 for Z was expected" );
		}
	}

    /**
     * compares 2 GisVolumeGet object on X axis by minimum dimension values
     * @param v1 first GisVolumeGet to compare
     * @param v2 second GisVolumeGet to compare
     * @return 1 if first X > second X, 0 if they are equal, -1 if first X < second X
     */
    static public int compareToX( GisVolume v1, GisVolume v2 )
    {
		return Sys.signum( v1.x - v2.x ); 
    }
    
    /**
     * compares 2 GisVolumeGet object on Y axis by minimum dimension values
     * @param v1 first GisVolumeGet to compare
     * @param v2 second GisVolumeGet to compare
     * @return 1 if first Y > second Y, 0 if they are equal, -1 if first Y < second Y
     */
	static public int compareToY( GisVolume v1, GisVolume v2 )
    {
		return Sys.signum( v1.y - v2.y );
    }

    /**
     * compares 2 GisVolumeGet object on Z axis by minimum dimension values
     * @param v1 first GisVolumeGet to compare
     * @param v2 second GisVolumeGet to compare
     * @return 1 if first Z > second Z, 0 if they are equal, -1 if first Z < second Z
     */
	static public int compareToZ(  GisVolume v1, GisVolume v2  )
    {
		return 0; 
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setDepth(double)
     */
    public void setDepth( double new_val )
    {
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setMaxZ(double)
     */
    public void setMaxZ( double new_val )
    {
    }


	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.ISetXY#setXY(double, double)
     */
    public void setXY( double new_x, double new_y )
    {
	    x = new_x; y = new_y;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setHeight(double)
     */
    public void setHeight( double new_val )
    {
    	if ( Sys.signum( new_val ) < 0 )
    		y -= ( height = Math.abs( new_val ) );
    	else
    		height = new_val;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setMaxX(double)
     */
    public void setMaxX( double new_val )
    {
    	setMaxDimension(0, new_val);
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setMaxY(double)
     */
    public void setMaxY( double new_val )
    {
    	setMaxDimension( 1, new_val );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setMinX(double)
     */
    public void setMinX( double new_val )
    {
    	x = new_val;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setMinY(double)
     */
    public void setMinY( double new_val )
    {
    	y = new_val;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setMinZ(double)
     */
    public void setMinZ( double new_val )
    {
    	setMinDimension( 2, new_val );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setWidth(double)
     */
    public void setWidth( double new_val )
    {
    	if ( Sys.signum( new_val ) < 0 )
    		x -= ( width = Math.abs(new_val) );
    	else
    		width = new_val;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.ISetXY#setX(double)
     */
    public void setX( double new_val )
    {
    	this.x = new_val;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.ISetXY#setY(double)
     */
    public void setY( double new_val )
    {
    	this.y = new_val;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#getReadOnlyGisVolume()
     */
    public IGisVolumeGet getReadOnlyGisVolume()
    {
	    return new GisVolume(this);
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.GisVolumeGet#add(double, double)
     */
    //@Override
    public void add( double newx, double newy )
    {
    	double x1 = Math.min(getMinX(), newx);
    	double x2 = Math.max(getMaxX(), newx);
    	double y1 = Math.min(getMinY(), newy);
    	double y2 = Math.max(getMaxY(), newy);
    	_setRect(x1, y1, x2 - x1, y2 - y1);
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#add(double, double, double)
     */
    public void add( double newx, double newy, double newz )
    {
    	this.add( newx, newy );
    }

	/**
	 * Sets the location and size of this <code>Rectangle2D</code>
     * to the specified double values.
     * @param x X to set
     * @param y Y to set
     * @param w the value to use to set the width of this
     * <code>GisVolume</code>
     * @param h the value to use to set the height of this
     * <code>GisVolume</code>
	 */
	private void _setRect(double x, double y, double w, double h) {
	    this.x = x;
	    this.y = y;
	    this.width = w;
	    this.height = h;
	}

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#add(java.awt.geom.Point2D.Double)
     */
    public void add( java.awt.geom.Point2D.Double point )
    {
    	add( point.x, point.y, 0.0 );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#add(ru.ts.gisutils.geometry.IGisVolumeGet)
     */
    public void add( IGisVolumeGet vol )
    {
    	double x1 = Math.min(getMinX(), vol.getMinX());
    	double x2 = Math.max(getMaxX(), vol.getMaxX());
    	double y1 = Math.min(getMinY(), vol.getMinY());
    	double y2 = Math.max(getMaxY(), vol.getMaxY());
    	_setRect(x1, y1, x2 - x1, y2 - y1);
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#add(ru.ts.gisutils.datamine.ICoordinate)
     */
    public void add( ICoordinate co )
    {
    	this.add( co.getX(), co.getY(), co.getZ() );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#add(ru.ts.gisutils.datamine.ICoordinate[])
     */
    public void add( ICoordinate[] co_arr )
    {
    	for ( int i = 0; i < co_arr.length; i++ )
        {
    		ICoordinate co = co_arr[i];
	        add(co.getX(), co.getY(), co.getZ() );
        }
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#set(ru.ts.gisutils.geometry.IGisVolumeGet)
     */
    public void set( IGisVolumeGet vol )
    {
    	_setRect(vol.getMinX(), vol.getMinY(), vol.getWidth(), vol.getHeight() );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setEmpty()
     */
    public void setEmpty()
    {
    	x = java.lang.Double.MAX_VALUE / 2;
    	width = -java.lang.Double.MAX_VALUE;
    	y = java.lang.Double.MAX_VALUE / 2;
    	height = -java.lang.Double.MAX_VALUE;
    	/* XXX add Z processing
    	z = java.lang.Double.MAX_VALUE;
    	depth = -java.lang.Double.MAX_VALUE;
    	 */
    }
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGisVolumeGet#getCenterZ()
	 */
	public double getCenterZ()
	{
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGisVolumeGet#getDepth()
	 */
	public double getDepth()
	{
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGisVolumeGet#getMaxDimension(int)
	 */
	public double getMaxDimension( int dim_index )
	        throws IndexOutOfBoundsException
	{
		switch( dim_index )
		{
		case 0:
			return super.getMaxX();
		case 1:
			return super.getMaxY();
		case 2:
			return 0.0;
		default:
			throw new IndexOutOfBoundsException("dimension index " + dim_index);
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGisVolumeGet#getMaxZ()
	 */
	public double getMaxZ()
	{
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGisVolumeGet#getMinDimension(int)
	 */
	public double getMinDimension( int dim_index )
	        throws IndexOutOfBoundsException
	{
		switch( dim_index )
		{
		case 0:
			return super.getMinX();
		case 1:
			return super.getMinY();
		case 2:
			return 0.0;
		default:
			throw new IndexOutOfBoundsException("dimension index " + dim_index);
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGisVolumeGet#getMinZ()
	 */
	public double getMinZ()
	{
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGisVolumeGet#getZ()
	 */
	public double getZ()
	{
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGisVolumeGet#intersects(ru.ts.gisutils.geometry.IGisVolumeGet)
	 */
	public boolean intersects( IGisVolumeGet vol )
	{
		return this.intersects( vol.getX(), vol.getY(), vol.getWidth(), vol.getHeight() );
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.geometry.IGetXY#copyTo(ru.ts.gisutils.geometry.IXY)
	 */
	public void copyTo( IXY xy )
	{
		xy.setX( getX() );
		xy.setY( getY() );
	}

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeGet#intersects(double, double, double)
     */
    public boolean intersects( double x, double y, double z )
    {
	    /* XXX add Z usage if needed */
    	/* now Z is not used in our model*/
    	return this.contains( x, y );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeGet#intersects(double, double, double, double, double, double)
     */
    public boolean intersects( double x, double y, double z, double w, double h, double d )
    {
	    /* XXX add Z usage if needed */
    	/* now Z dimension is not used in our model*/
	    return this.intersects( x, y, w, h );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeGet#isOnBoundary(double, double, double)
     */
    public boolean isOnBoundary( double x, double y, double z )
    {
    	return isOnBoundary( x, y );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeGet#isOnBoundary(double, double)
     */
    public boolean isOnBoundary( double x1, double y1 )
    {
    	return (     this.x == x1) 
		|| ( (this.x + width) == x1)
		|| ( this.y == y1 )
		|| ( (this.y + height) == y1 );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setFrame(double, double, double, double, double, double)
     */
    public void setFrame( double x1, double y1, double z1, double width1, double height1, double depth1 )
    {
    	x = x1; y = y1; width = width1; height = height1;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setFrame(java.awt.geom.Rectangle2D.Double)
     */
    public void setFrame( Double rect )
    {
    	x = rect.x; y = rect.y; width = rect.width; height = rect.height;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeSet#setFrame(ru.ts.gisutils.geometry.IGisVolumeGet)
     */
    public void setFrame( IGisVolumeGet vol )
    {
    	x = vol.getX(); y= vol.getY(); width = vol.getWidth(); height = vol.getHeight();
    }
    
	/**
	 * Determines whether or not this <code>Rectangle2D</code> 
         * is empty.
         * @return <code>true</code> if this <code>Rectangle2D</code>
         * is empty; <code>false</code> otherwise.
	 * @since 1.2
	 */
    //@Override
	public boolean isEmpty() {
	    return (width < 0.0) || (height < 0.0) /* || (depth < 0.0) */;
	}

    /**
     * Tests if the interior of this <code>Rectangle2D</code> 
     * intersects the interior of a specified set of rectangular 
     * coordinates.
     * @param x,y the coordinates of the upper left corner
     * of the specified set of rectangular coordinates
     * @param w the width of the specified set of rectangular
     * coordinates
     * @param h the height of the specified set of rectangular
     * coordinates
     * @return <code>true</code> if this <code>Rectangle2D</code>
     * intersects the interior of a specified set of rectangular
     * coordinates; <code>false</code> otherwise.
     * @since 1.2
     */
    //@Override
    public boolean intersects(double x, double y, double w, double h) 
    {
		if ( isEmpty() || w < 0.0 || h < 0.0 )
		    return false;
		double x0 = getX();
		if ( x > ( x0 + getWidth() ) )
			return false;
		double y0 = getY();
		if ( y > ( y0 + getHeight() ) )
			return false;
		if ( ( x + w ) < x0 )
			return false;
		if ( ( y + h ) < y0 )
			return false;
		return true;
    }
    
    /**
     * Tests if the interior of this rectangle entirely
     * contains the specified set of rectangular coordinates.
     * @param x,y the coordinates of the upper left corner
     * of the specified set of rectangular coordinates
     * @param w the width of the specified set of rectangular
     * coordinates
     * @param h the height of the specified set of rectangular
     * coordinates
     * @return <code>true</code> if this <code>Rectangle2D</code>
     * entirely contains specified set of rectangular
     * coordinates; <code>false</code> otherwise.
     * @since 1.2
     */
    //@Override
    public boolean contains(double x, double y, double w, double h) 
    {
		if ( isEmpty() || (w < 0.0) || (h <= 0.0) )
		    return false;
		double x0 = getX();
		if ( x < x0 )
			return false;
		double y0 = getY();
		if ( y < y0 )
			return false;
		if ( ( x + w ) > ( x0 + width ) )
			return false;
		if ( ( y + h ) > ( y0 + height ) )
			return false;
		return true;
    }

	/* (non-Javadoc)
     * @see java.awt.geom.Rectangle2D#contains(double, double)
     */
    //@Override
    public boolean contains( double x, double y )
    {
    	double x0;
    	if ( x < ( x0  = getX()) )
    		return false;
    	double y0;
    	if ( y < ( y0  = getY() ) )
    		return false;
    	if ( x > (x0 + width) )
    		return false;
    	if ( y > (y0 + height) )
    		return false;
    	return true;
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeGet#contains(double, double, double, double, double, double)
     */
    public boolean contains( double x, double y, double z, double w, double h, double d )
    {
	    return contains( x, y, w, h );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeGet#contains(double, double, double)
     */
    public boolean contains( double x, double y, double z )
    {
	    return contains( x, y );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.geometry.IGisVolumeGet#contains(ru.ts.gisutils.geometry.IGisVolumeGet)
     */
    public boolean contains( IGisVolumeGet vol )
    {
    	return contains( vol.getX(), vol.getY(), vol.getWidth(), vol.getHeight() );
    }

    

}
