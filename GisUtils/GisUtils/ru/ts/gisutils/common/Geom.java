/**
 * Created on 05.02.2008 10:44:06 2008 by Syg
 * for project in 'ru.ts.gisutils.common' of 'test' 
 */
package ru.ts.gisutils.common;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * contains useful geometry utils
 * @author Syg
 */
public class Geom
{
	/**
	 * inflates the rect to all sides for the same value
	 * @param rect Rectangle2D to inflate
	 * @param val value to inflate for. Absolute value will be used
	 */
	public static void inflateRect2D(Rectangle2D rect, double val)
	{
		inflateRect2D(rect, val, val);
	}

	/**
	 * inflates the rect in X and Y direction differently
	 * @param rect Rectangle2D to inflate
	 * @param xval value to inflate for X axis. Absolute value will be used.
	 * @param yval value to inflate for Y axis. Absolute value will be used.
	 */
	public static void inflateRect2D(Rectangle2D rect, double xval, double yval)
	{
		xval = Math.abs( xval );
		yval = Math.abs( yval );
		double x = rect.getMaxX() + xval, y = rect.getMaxY() + yval;
		rect.add( x, y );
		x = rect.getMinX() - xval;
		y = rect.getMinY() - yval;
		rect.add( x, y );
	}

	/**
	 * inflates the rect to all sides for the same value
	 * @param rect Rectangle to inflate
	 * @param val value to inflate for. Absolute value will be used
	 */
	public static void inflateRect(Rectangle rect, double val)
	{
		inflateRect(rect, val, val);
	}

	/**
	 * inflates the rect to all sides for the same value
	 * @param rect Rectangle to inflate
	 * @param xval value to inflate for X axis. Absolute value will be used.
	 * @param yval value to inflate for Y axis. Absolute value will be used.
	 */
	public static void inflateRect(Rectangle rect, double xval, double yval)
	{
		xval = Math.abs( xval );
		yval = Math.abs( yval );
		double x = rect.getMaxX() + xval, y = rect.getMaxY() + yval;
		rect.add( x, y );
		x = rect.getMinX() - xval;
		y = rect.getMinY() - yval;
		rect.add( x, y );
	}

	/**
	 * offsets rectangle by some values on both axes
	 * @param rect Rectangle2D to offset
	 * @param offsetx X axis offset value
	 * @param offsety Y axis offset value
	 */
	public static Rectangle2D offsetRect2D( Rectangle2D rect, double offsetx, double offsety )
	{
		rect.setRect( rect.getMinX() + offsetx, rect.getMinY() + offsety, 
					  rect.getMaxX() + offsetx, rect.getMaxY() + offsety );
		return rect;
	}
	
	public static Rectangle2D.Double setEmpty( Rectangle2D.Double rect )
	{
		rect.setFrame( -Double.MAX_VALUE / 2, -Double.MAX_VALUE / 2, 
				Double.MAX_VALUE, Double.MAX_VALUE );
		return rect;
	}

	/**
	 * offsets rectangle by some values on both axes
	 * @param rect Rectangle to offset
	 * @param offsetx X axis offset value
	 * @param offsety Y axis offset value
	 */
	public static void offsetRect( Rectangle rect, double offsetx, double offsety )
	{
		rect.setRect( rect.getMinX() + offsetx, rect.getMinY() + offsety, 
					  rect.getMaxX() + offsetx, rect.getMaxY() + offsety );
	}

	/**
	 * Euclide distance betwee two points as integer
	 * @param x1 X of 1st point
	 * @param y1 Y of 2st point
	 * @param x2 X of 2nd point
	 * @param y2 X of 2nd point
	 * @return distance int value for the distance between two points
	 */
	public static int distance(int x1, int y1, int x2, int y2)
	{
		final int dx = x1 - x2;
		final int dy = y1 - y2;
		return (int)Math.round( Math.sqrt( (double)(dx * dx + dy * dy) ) );
	}

	/**
	 * Euclide distance betwee two points as double
	 * @param x1 X of 1st point
	 * @param y1 Y of 2st point
	 * @param x2 X of 2nd point
	 * @param y2 X of 2nd point
	 * @return double value for the distance between two points
	 */
	public static double distance(double x1, double y1, double x2, double y2)
	{
		final double dx = x1 - x2;
		final double dy = y1 - y2;
		return Math.sqrt( dx * dx + dy * dy );
	}

}
