/**
 * Created on 06.02.2008 13:14:36 2008 by Syg
 * for project in 'ru.ts.gisutils.potentialmap' of 'test' 
 */
package ru.ts.gisutils.potentialmap;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * superposes two coordinate systems and translates coordinates
 * from each to another 
 * 
 * translates coordinates from world (double precision) Rectangle2D 
 * to pixels (integer precision) Rectangle
 * 
 * @author Syg
 * 
 */
public class CoordSystemTranslator
{
	Rectangle2D _world;
	Rectangle _pix;
	
	/**
	 * scale coefficient along X axis
	 */
	double _scaleX;
	/**
	 * scale coefficient along Y axis
	 */
	double _scaleY;
	
	/**
	 *  main constructor for superposing two spaces in different precision
	 *  
	 * @param worldrect rectangle in doubles (world coordinates)
	 * @param pixrect rectangle in integers (pixels)
	 */
	public CoordSystemTranslator(Rectangle2D worldrect, Rectangle pixrect)
	{
		_world = worldrect;
		_pix = pixrect;
		_scaleX = _world.getWidth() / _pix.getWidth();
		_scaleY = _world.getHeight() / _pix.getHeight();
	}
	
	public boolean pointInRect2D( Point2D pnt)
	{
		return _world.contains( pnt );
	}
	
	public boolean pointInRect( Point pnt)
	{
		return _pix.contains( pnt );
	}
	
	public double getWorldX( int x )
	{
		return ( _pix.getMinX() - x ) * _scaleX + _world.getMinX(); 
	}

	public double getWorldY( int y )
	{
		return ( _pix.getMinY() - y ) * _scaleY + _world.getMinY(); 
	}

	public int getPixX( double x )
	{
		return (int)Math.round( ( x - _world.getMinX() ) / _scaleX + _pix.x ); 
	}

	public int getPixY( double y )
	{
		return (int)Math.round(( y - _world.getMinY() ) / _scaleY + _pix.y ); 
	}
	
}
