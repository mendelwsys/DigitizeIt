package ru.ts.gisutils.proj.calculate;

import java.awt.geom.Point2D;

import org.geotools.referencing.datum.DefaultEllipsoid;

import ru.ts.gisutils.proj.transform.MapTransform;

public class MerkatorCalculator
{

	private DefaultEllipsoid _ellipsoid;
	
	public MerkatorCalculator()
	{
		this( MapTransform.createKrassowskyEllipsoid() );
	}
	
	public MerkatorCalculator( DefaultEllipsoid ellipsoid ) throws IllegalArgumentException
	{
		_ellipsoid = ellipsoid;
	}
	
	/**
	 * gets meeter coordinates for point defined by user designated parallel and meridian
	 * @param meridian geographical meridian in degrees
	 * @param parallel geographical parallel in degrees
	 * @param result resulting Point2D instance
	 * @return <code>true</code> if well else <code>false</code>
	 */
	public  boolean getMeters( double meridian, double parallel, Point2D result )
	{
		/* For ellipsoid */
		double m = Math.toRadians( meridian );
		double p = Math.toRadians( parallel );
		double a = _ellipsoid.getSemiMajorAxis();
		double e = _ellipsoid.getEccentricity();
		double tmp =  Math.pow( ((1.0 - e * Math.sin( p )) / ( 1.0 + e * Math.sin( p ))) , e / 2.0);
		double x = a * Math.log( Math.tan( Math.toRadians( 45.0 ) + p / 2.0 ) ) * tmp;
		double y = a * m;
/*		Для шара: 
			x = R ln tg (45° + φ / 2); 
			y = Rλ 
			m = n = sec φ 
			ω = 0.
*/		result.setLocation( x, y );
		return true;
	}
}
