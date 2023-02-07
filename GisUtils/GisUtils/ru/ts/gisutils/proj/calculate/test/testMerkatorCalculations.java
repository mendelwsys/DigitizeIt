/**
 * 
 */
package ru.ts.gisutils.proj.calculate.test;

import java.awt.geom.Point2D;

import ru.ts.gisutils.proj.calculate.MerkatorCalculator;

/**
 * @author Syg
 *
 */
public final class testMerkatorCalculations
{

	/**
	 * @param args
	 */
	public static void main( String[] args )
	{
		Point2D point = new Point2D.Double();
		MerkatorCalculator merk = new MerkatorCalculator();
		for( double p = -80.0; p <= 85.0; p += 10.0 )
		{
			merk.getMeters( 30.0, p, point );
			System.out.println( "Parallel=" + p + "\tX=" + point.getX() + "\tY=" + point.getY() ); 
		}
	}

}
