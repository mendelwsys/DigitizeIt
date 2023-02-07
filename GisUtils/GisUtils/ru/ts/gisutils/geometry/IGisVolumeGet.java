package ru.ts.gisutils.geometry;

public interface IGisVolumeGet extends IGetXY
{

	/**
	 * gets depth - Z dimension extent
	 * 
	 * @return value for the box Z dimension extent
	 */
	double getDepth();
	
	double getWidth();
	
	double getHeight();

	/**
	 * gets center of Z dimension
	 * 
	 * @return double value of Z dimension extent center
	 */
	double getCenterZ();

	/**
	 * gets minimum of the pointed dimension
	 * 
	 * @param dim_index
	 *            index of dimension to get min value
	 * @return min value for the indicated dimension
	 * @throws IndexOutOfBoundsException
	 *             if you indicated some]illegal dimension index (not 0, 1, 2)
	 */
	double getMinDimension( int dim_index ) throws IndexOutOfBoundsException;

	/**
	 * gets maximum of the pointed dimension
	 * 
	 * @param dim_index
	 *            index of dimension to get min value
	 * @return min value for the indicated dimension
	 * @throws IndexOutOfBoundsException
	 *             if you indicated some]illegal dimension index (not 0, 1, 2)
	 */
	double getMaxDimension( int dim_index ) throws IndexOutOfBoundsException;

	/**
	 * gets minimum for Z dimension
	 * 
	 * @return value of the Z dimension minimum
	 */
	double getMinZ();
	
	double getMinX();
	double getMinY();

	/**
	 * Returns the Z coordinate of the nearest to the coordinate system corner
	 * of the framing volume in <code>double</code> precision.
	 * 
	 * @return the Z coordinate of the nearest to the coordinate system corner
	 *  of the framing volume.
	 */
	double getZ();

	/**
	 * gets maximum for Z dimension
	 * 
	 * @return value of the Z dimension maximum
	 */
	double getMaxZ();
	
	double getMaxX();
	double getMaxY();

	/**
	 * returns result of intersection with other IGisVolumeGet object
	 * 
	 * @param vol
	 *            a volume to check intersection with
	 * @return <code>true</code> if intesection found, else false
	 */
	boolean intersects( IGisVolumeGet vol );
	
	/**
	 * primitive intersect testing
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @return <code>true</code> if point IN voulme, else <code>false</code>
	 */
	boolean intersects( double x, double y, double z);
	
	/**
	 * finds intersection
	 * @param x X
	 * @param y Y
	 * @param w Width
	 * @param h Height
	 * @return <code>true</code> if intersected, else <code>false</code> 
	 */
	boolean intersects( double x, double y, double w, double h);

	/**
	 * finds intersection
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @param w Width
	 * @param h Height
	 * @param d Depth
	 * @return <code>true</code> if intersected, else <code>false</code> 
	 */
	boolean intersects( double x, double y, double z, double w, double h, double d);

	/**
	 * checks if 2D point is on volume boundary
	 * @param x X of point
	 * @param y Y of point
	 * @return <code>true</code> if point is on boundary, else <code>false</code>
	 */
	boolean isOnBoundary(double x, double y);

	/**
	 * checks if 3D point is on volume boundary
	 * @param x X of point
	 * @param y Y of point
	 * @param z Z of point
	 * @return <code>true</code> if point is on boundary, else <code>false</code>
	 */
	boolean isOnBoundary(double x, double y, double z);
	
	/**
	 * checks if a 3D volume is totally in the filter volume, but may touch
	 * boundaries internally
	 * @param x X of volume
	 * @param y Y of volume
	 * @param z Z of volume
	 * @param w width
	 * @param h height
	 * @param d depth
	 * @return <code>true</code> if checked volume totally in this one
	 */
	boolean contains(double x, double y, double z, double w, double h, double d);
	/**
	 * checks if a 3D volume is totally in the filter volume, but may touch
	 * boundaries internally
	 * @param x X of volume
	 * @param y Y of volume
	 * @param z Z of volume
	 * @param w width
	 * @param h height
	 * @param d depth
	 * @return <code>true</code> if checked volume totally in this one
	 */
	boolean contains(double x, double y, double w, double h);
	
	/**
	 * checks if a 2D rectangle is totally in the filter volume, but may touch
	 * boundaries internally
	 * @param x X of volume
	 * @param y Y of volume
	 * @param w width
	 * @param h height
	 * @return <code>true</code> if checked rectangle totally in this one
	 */
	boolean contains(double x, double y, double z);
	
	/**
	 * checks if a a point is totally in the filter volume, but may touch
	 * boundaries internally
	 * @param x X of point
	 * @param y Y of point
	 * @return <code>true</code> if checked point totally in this one
	 */
	boolean contains(double x, double y);
	
	/**
	 * checks if designated volume totally fit into this one ( may touch on boundaries)
	 * @param vol testing volume
	 * @return <code>true</code> if fit in, else <code>false</code>
	 */
	boolean contains(IGisVolumeGet vol);
	
	/**
	 * checks if rectangle is empty.
	 * @return <code>true</code> if volume not empty else <code>false</code>
	 */
	boolean isEmpty();
}
