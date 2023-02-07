/**
 * Created on 26.03.2008 13:08:47 2008 by Syg
 * for project in 'ru.ts.gisutils.geometry' of 'test' 
 */
package ru.ts.gisutils.geometry;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * GisVolume ReadOnly interface
 * @author Syg
 */
public interface IGisVolumeSet extends ISetXY
{
	/**
	 * sets minimum of the pointed dimension
	 * 
	 * @param dim_index
	 *            index of dimension to set
	 * @param new_val
	 *            new value for the dimension minimum
	 */
	void setMinDimension( int dim_index, double new_val )
	        throws IndexOutOfBoundsException;

	/**
	 * sets maximum of the pointed dimension
	 * 
	 * @param dim_index
	 *            index of dimension to set
	 * @param new_val
	 *            new value for the dimension maximum
	 */
	void setMaxDimension( int dim_index, double new_val )
	        throws IndexOutOfBoundsException;

	/**
	 * sets minimum of the Z dimension
	 * @param new_val new value for minimum Z 
	 */
	void setMinZ(double new_val);
	
	void setMinX(double new_val);
	void setMinY(double new_val);
	
	/**
	 * sets maximum of the Z dimension
	 * @param new_val new value for maximum Z 
	 */
	void setMaxZ(double new_val);
	void setMaxX(double new_val);
	void setMaxY(double new_val);
	
	/**
	 * set Z extension
	 * @param new_val new value for Z dimension extension
	 */
	void setDepth(double new_val);
	void setWidth(double new_val);
	void setHeight(double new_val);
	
	/**
	 * gets read-only copy of this object
	 * @return IGisVolumeGet object with the same data as for current one
	 */
	IGisVolumeGet getReadOnlyGisVolume();
	
    /**
     * Adds a 3D point, specified by the double precision arguments
     * <code>newx</code>,<code>newy</code> and <code>newz</code>, to this 
     * <code>IGisVolumeGet</code>.  The resulting <code>IGisVolumeGet</code> 
     * is the smallest <code>IGisVolumeGet</code> that
     * contains both the original <code>IGisVolumeGet</code> and the
     * specified point.
     * <p>
     * After adding a point, a call to <code>contains</code> with the 
     * added point as an argument does not necessarily return 
     * <code>true</code>. The <code>contains</code> method does not 
     * return <code>true</code> for points on the right or bottom 
     * edges of a rectangle. Therefore, if the added point falls on 
     * the left or bottom edge of the enlarged rectangle, 
     * <code>contains</code> returns <code>false</code> for that point.
     * @param newx X 
     * @param newy Y 
     * @param newz Z - you can set it to any value as it is dummy now
     */
    public void add(double newx, double newy, double newz);
    
    /**
     * adds 2d point to the volume
     * @param newx new x
     * @param newy new y
     */
    public void add( double newx, double newy );
    
    /**
     * adds single coordinate
     * @param co Icoordinate object to add to the volume
     */
    public void add( ICoordinate co );
    
    /**
     * use to add coordinates array 
     * @param co_arr array of ICoordinate
     */
    public void add( ICoordinate[] co_arr );
    
    public void add(Point2D.Double point);
    
    /** 
     * adds rectangle to the current volume described by this object
     * @param rect rectangle to add to the currently available
     */
    public void add( Rectangle2D rect);
    
    /**
     * adds volume еще he exising one
     * @param vol IGisVolumeGet to add to the current one
     */
    public void add(IGisVolumeGet vol);
    
    /**
     * sets current volume to the parameter 
     * @param vol nIGisVolumeSet to copy its data into a current one
     */
    public void set(IGisVolumeGet vol);
    
    /**
     * sets volume to the empty state
     *
     */
    public void setEmpty();
    
    /** sets X Y dimensions
     * 
     * @param x start X
     * @param y start Y
     * @param width rectangle width
     * @param height rectangle height
     */
    public void setFrame(double x, double y, double width, double height);
    
    /**
     * sets frame from a rect
     * @param rect rectangle to copy to this one
     */
    public void setFrame( Rectangle2D.Double rect);
    
    /**
     * sets all dimensions
     * @param x start X
     * @param y start Y
     * @param z start Z
     * @param width volume width
     * @param height volume height
     * @param depth volume depth
     */
    public void setFrame(double x, double y, double z, double width, double height, double depth);
    
    /**
     * sets the volume from another one
     * @param vol volume to copy to this
     */
    public void setFrame(IGisVolumeGet vol);
}
