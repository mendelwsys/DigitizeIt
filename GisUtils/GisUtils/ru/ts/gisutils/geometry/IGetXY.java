/*
 * Created on 18.07.2007
 *
 * TC is a shortening of Topology Cleaning
 */
package ru.ts.gisutils.geometry;


/**
 * @author yugl
 *
 * Interface to get 2D point's coordinates
 */
public interface IGetXY {

	/**
	 * X coordinate getter
	 * 
	 * @return X coordinate value
	 */
	public double getX();

	/**
	 * Y coordinate getter
	 * 
	 * @return Y coordinate value
	 */
	public double getY();

	/**
	 * copy coordinates TO parameter
	 * @param xy - IXY interface instance to copy current coordinates TO it
	 */
	public void copyTo (IXY xy);
	
}

