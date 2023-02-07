/**
 * Created on 13.05.2008 12:57:50 2008 by Syg for project in
 * 'ru.ts.gisutils.potentialmap' of 'test'
 */
package ru.ts.gisutils.potentialmap;

import java.awt.image.BufferedImage;

/**
 * Simplу class to help customization in a legend drawing. Some notes: legend is
 * considered to be better drawn in _bottom _right corner of the raster
 * 
 * @author Syg
 */
public class Legend
{

	public static final int	DEFAULT_WIDTH	= 16;

	public static final int	DEFAULT_HEIGHT	= -33;

	public static final int	DEFAULT_BOTTOM	= 64;

	public static final int	DEFAULT_RIGHT	= 10;

	/**
	 * defines drawing of legend. If set to <code>true</code> legend is now
	 * drawn on the resulting image, if set to <code>false</code> it is not
	 * drawn
	 */
	public boolean	        drawLegend;

	/**
	 * distance from the _bottom of the raster. Zero means absence of the legend
	 */
	public int	            _bottom;

	/**
	 * distance from the _right border of the raster. Zero means absence of the
	 * legend
	 */
	public int	            _right;

	/**
	 * _height of the legend in pixels. Zero means absence of the legend
	 */
	public int	            _height;

	/**
	 * _width of the legend in pixels. Zero means absence of the legend
	 */
	public int	            _width;

	/**
	 * initializatiojn of internal variables to the wanted values
	 * 
	 * @param draw
	 *            defines to draw legend if set to <code>true</code> else not
	 *            to draw
	 * 
	 * @param legendHeight
	 *            legend _height in pixels. If it is lower than zero, it is
	 *            considered to be in percents for raster _height
	 * @param legendWidth
	 *            legend _width in pixels. If it is lower than zero, it is
	 *            considered to be in percents for raster _width.
	 * @param legendBottom
	 *            distance in pixels from a _bottom of the raster. If it is
	 *            lower than zero, it is considered to be in percents for raster
	 *            _height
	 * @param legendRight
	 *            distance in pixels from a _right of the raster. If it is lower
	 *            than zero, it is considered to be in percents for raster
	 *            _width.
	 */
	private void _init( boolean draw, int legendHeight, int legendWidth,
	        int legendBottom, int legendRight )
	{
		drawLegend = draw;
		set_height( legendHeight );
		set_width( legendWidth );
		set_bottom( legendBottom );
		set_right( legendRight );
	}

	/**
	 * set all values default ones
	 */
	private void _init()
	{
		_init( true, DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_BOTTOM,
		        DEFAULT_RIGHT );
	}

	public Legend()
	{
		_init();
	}

	/**
	 * Most detailed constructor
	 * 
	 * @param draw
	 *            defines to draw legend if set to <code>true</code> else not
	 *            to draw
	 * 
	 * @param legendHeight
	 *            legend _height in pixels. If it is lower than zero, it is
	 *            considered to be in percents for raster _height
	 * @param legendWidth
	 *            legend _width in pixels. If it is lower than zero, it is
	 *            considered to be in percents for raster _width.
	 * @param legendBottom
	 *            distance in pixels from a _bottom of the raster. If it is
	 *            lower than zero, it is considered to be in percents for raster
	 *            _height
	 * @param legendRight
	 *            distance in pixels from a _right of the raster. If it is lower
	 *            than zero, it is considered to be in percents for raster
	 *            _width.
	 * @throws IllegalArgumentException
	 *             if any argument is out of bounds
	 */
	public Legend( boolean draw, int legendHeight, int legendWidth,
	        int legendBottom, int legendRight ) throws IllegalArgumentException
	{
		_init( draw, legendHeight, legendWidth, legendBottom, legendRight );
	}

	/**
	 * gets width according to the image
	 * 
	 * @param image
	 *            image to build legend. May be <code>null</code> if value is
	 *            not in percents but in pixels
	 * @return width value in pixels
	 */
	public int get_width( BufferedImage image )
	{
		if ( _width == 0 )
			return DEFAULT_WIDTH;
		return _width > 0 ? _width : ( image.getWidth() * (-_width) ) / 100;
	}

	/**
	 * gets height according to the image
	 * 
	 * @param image
	 *            image to build legend. May be <code>null</code> if value is
	 *            not in percents but in pixels
	 * @return height value in pixels
	 */
	public int get_height( BufferedImage image )
	{
		if ( _height == 0 )
			return DEFAULT_HEIGHT;
		return _height >= 0 ? _height : ( image.getHeight() * (-_height) ) / 100;
	}

	/**
	 * gets right according to the image
	 * 
	 * @param image
	 *            image to build legend. May be <code>null</code> if value is
	 *            not in percents but in pixels
	 * @return right value in pixels
	 */
	public int get_right( BufferedImage image )
	{
		if ( _right == 0)
			return DEFAULT_RIGHT;
		return _right >= 0 ? _right : ( image.getWidth() * (-_right) ) / 100;
	}

	/**
	 * gets bottom according to the image
	 * 
	 * @param image
	 *            image to build legend. May be <code>null</code> if value is
	 *            not in percents but in pixels
	 * @return bottom value in pixels
	 */
	public int get_bottom( BufferedImage image )
	{
		if ( _bottom == 0 )
			return DEFAULT_BOTTOM;
		return _bottom >= 0 ? _bottom : ( image.getHeight() * (-_bottom) ) / 100;
	}

	/**
	 * @param bottom
	 *            the bottom to set
	 * @throws IllegalArgumentException
	 *             if argument value is zero or smaller than 99
	 */
	public void set_bottom( int bottom ) throws IllegalArgumentException
	{
		if ( ( bottom == 0 ) || ( bottom < -99 ) )
			throw new IllegalArgumentException(
			        "bottom should be .NE. 0 and be .GT. -100" );
		_bottom = bottom;
	}

	/**
	 * @param height
	 *            the height to set
	 * @throws IllegalArgumentException
	 *             if argument value is zero or smaller than 99
	 */
	public final void set_height( int height ) throws IllegalArgumentException
	{
		if ( ( height == 0 ) || ( height < -99 ) )
			throw new IllegalArgumentException(
			        "height should be .NE. 0 and be .GT. -100" );
		this._height = height;
	}

	/**
	 * @param right
	 *            the right to set
	 * @throws IllegalArgumentException
	 *             if argument value is zero or smaller than 99
	 */
	public final void set_right( int right ) throws IllegalArgumentException
	{
		if ( ( right == 0 ) || ( right < -99 ) )
			throw new IllegalArgumentException(
			        "right should be .NE. 0 and be .GT. -100" );
		_right = right;
	}

	/**
	 * @param width
	 *            the width to set
	 * @throws IllegalArgumentException
	 *             if argument value is zero or smaller than 99
	 */
	public final void set_width( int width ) throws IllegalArgumentException
	{
		if ( ( width == 0 ) || ( width < -99 ) )
			throw new IllegalArgumentException(
			        "width should be .NE. 0 and be .GT. -100" );
		this._width = _width;
	}

}
