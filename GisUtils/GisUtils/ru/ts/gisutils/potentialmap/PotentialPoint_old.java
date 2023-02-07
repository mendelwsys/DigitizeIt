/**
 * Created on 01.02.2008 17:39:35 2008 by Syg
 * for project in 'ru.ts.gisutils.potentialmap' of 'test' 
 */
package ru.ts.gisutils.potentialmap;

import java.awt.geom.Point2D;

/**
 * @author Syg
 */
public class PotentialPoint_old extends Point2D.Double
{

	/**
	 * potential value in this point
	 */
	public float value;

	/**
	 * @param x X value for the point 
	 * @param y YX value for the point 
	 * @param initval initial value for the phenomenon 
	 */
	public PotentialPoint_old(double x, double y, float initval)
	{
		super( x, y );
		value = initval;
	}

	/**
	 * @param x X value for the point 
	 * @param y YX value for the point 
	 * @param initval initial value for the phenomenon 
	 */
	public PotentialPoint_old(double x, double y, double initval)
	{
		super( x, y );
		value = (float)initval;
	}
	
	public PotentialPoint_old( PotentialPoint_old point )
	{
		super ( point.x, point.y ); 
	}

}
