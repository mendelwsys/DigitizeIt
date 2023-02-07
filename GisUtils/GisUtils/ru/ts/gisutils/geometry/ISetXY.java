/*
 * Created on 18.07.2007
 *
 * TC is a shortening of Topology Cleaning
 */
package ru.ts.gisutils.geometry;

/**
 * @author yugl
 *
 * Interface to set 2D point's coordinates
 */
public interface ISetXY {

	/**
	 * X coordinate setter
	 * 
	 * @param x
	 *            new double value for X
	 */
	public void setX(double x);
	
	/**
	 * Y coordinate setter
	 * 
	 * @param y
	 *            new double value for Y
	 */
	public void setY(double y);
	
	/**
	 * set X and Y values at one time
	 * @param x value of X axis
	 * @param y value of Y axis
	 */
	public void setXY(double x, double y);
	
}
