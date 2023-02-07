package ru.ts.gisutils.legend;

/**
 * Created 29.07.2008 13:15:43 by Syg for the "MapRuler" project
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Ruler is part of a map legend, showing length of the standard unit on the
 * map.
 * 
 * <pre>
 * The ruler itself is drawn from 20 smaller rectangles (cells) in width 
 * and 2 in height:
 * 
 * 0|                        NNN  km   |
 *  |------.------.------.------.------|------.---
 *  |BBBBBB|      |BBBBBB|      |BBBBBB|      |BBBBBB|
 *  |------|------|------|------|------|------|------|--
 *  |      |BBBBBB|      |BBBBBB|      |BBBBBB|      |BBBBBB
 *  |------.------.------.------.------.------.------.-----
 *  
 *  where rectangles with BBBBBB are black and with spaces are white
 *  Each small rectangle is a so called &quot;cell&quot;. 5x2 block 
 *  of cells forms so called &quot;segment&quot;. 4 in width segments forms the ruler itself.
 *  Each segment is separated from follow one with a &quot;touch&quot; 
 *  (straight vertical short lines on the segment right edge). 
 *  Each touch has height of the &quot;ruler unit&quot;. 
 * </pre>
 * 
 * @author Syg
 * 
 */
public interface IMapRuler
{
	/**
	 * builds ruler in the raster according to unit size and scale factor
	 * 
	 * @param unit_size
	 *            size of units used in world rectangle in meters. So if units
	 *            of world are centimetres it will be 0.01, if meters - 1.0, if
	 *            kilometres 1000.0, for feet it will be 0.3048, for naval miles
	 *            1852.0.
	 * @param gra
	 *            graphics to draw
	 * @param dev_rect
	 *            device rectangle for drawing (for example onto the screen)
	 * @param world_rect
	 *            world rectangle projected to the screen by the dev_rect
	 *            rectangle
	 * @return scale between world rectangle and device one, that is the ratio
	 *         of division of world rectangle widths (or height) to a device one
	 */
	float buildRuler( float unit_size, Graphics gra, Rectangle dev_rect,
	        Rectangle world_rect );

	/**
	 * builds ruler in the raster according to world coordinate system unit size
	 * in meters and ratio factor. Bottom and right indentation is default one.
	 * 
	 * @param unit_size
	 *            size of units used in world rectangle in meters. So if units
	 *            of world are centimetres it will be 0.01, if meters - 1.0, if
	 *            kilometres 1000.0, for feet it will be 0.3048, for naval miles
	 *            1852.0.
	 * @param gra
	 *            graphics to draw
	 * @param dev_rect
	 *            device rectangle for drawing (for example onto the screen)
	 * @param ratio
	 *            ratio between world and graphics coordinates, that is a float
	 *            value of the result of a world rectangle width divided to a
	 *            graphics rect width
	 * @return <code>true</code> if ruler was build else <code>false</code>
	 */
	boolean buildRuler( float world_unit_size, Graphics gra,
	        Rectangle dev_rect, float ratio );

	/**
	 * builds ruler in the raster according to the world coordinate system unit
	 * size and ration factor
	 * 
	 * @param unit_size
	 *            size of units used in world rectangle in meters. So if units
	 *            of world are centimetres it will be 0.01, if meters - 1.0, if
	 *            kilometres 1000.0, for feet it will be 0.3048, for naval miles
	 *            1852.0.
	 * @param gra
	 *            graphics to draw
	 * @param dev_rect
	 *            device rectangle for drawing (for example onto the screen)
	 * @param ratio
	 *            ratio between world and graphics coordinates, that is a float
	 *            value of the result of a world rectangle width divided to a
	 *            graphics rect width
	 * @param right
	 *            int offset to the right border of the ruler (default value is 6 )
	 *            from a right border of draw rectangle
	 * @param bottom
	 *            int offset to the bottom border of the ruler (default value is 6)
	 *            from a bottom border of a draw rectangle
	 * @return <code>true</code> if ruler was build else <code>false</code>
	 */
	boolean buildRuler( float world_unit_size, Graphics gra,
	        Rectangle dev_rect, float ratio, int right, int bottom );

	/**
	 * Detects if ruler was built and can be rebuild if the same scale is
	 * presented. Scale is the ration between world rectangle side and device
	 * rectangle one. Ration should be isotropic that is be the same in X and Y
	 * directions
	 * 
	 * @param scale
	 *            the scale between world space and device space
	 * @return <code>true</code> if ruler was already built as can be rebuild
	 *         to other context with same scale, and <code>false</code> if
	 *         ruler was not build for the designated scale
	 * @deprecated
	 */
	boolean canRebuild( float scale );

	/**
	 * Rebuilds last builded ruler in a new graphics context. Of course, ruler
	 * should be build before by call to a method
	 * {@link IMapRuler#buildRuler(float, Graphics2D, Rectangle, Rectangle)} to
	 * use this method successfully. If not, nothing occur
	 * 
	 * @param gra
	 *            graphics to build
	 * @param dev_rect
	 *            device rectangle
	 * @deprecated
	 */
	void rebuildRuler( float scale, Graphics gra, Rectangle dev_rect );

	/**
	 * Gets the current ratio to create map ruler. Ratio is set only after call
	 * to {@link IMapRuler#buildRuler(float, Graphics2D, Rectangle, Rectangle)}.
	 * So if you call it before the mentioned call, it will return the 0.0F
	 * value.
	 * 
	 * @return value of the currently used ration, may be 0.0F if ratio still
	 *         was not calculated
	 */
	float getRatio();

	/**
	 * @return the _right_indent
	 */
	public abstract int get_right_indent();

	/**
	 * @param _right_indent
	 *            the _right_indent to set
	 */
	public abstract void set_right_indent( int right_indent );

	/**
	 * @return the _bottom_indent
	 */
	public abstract int get_bottom_indent();

	/**
	 * @param _bottom_indent
	 *            the _bottom_indent to set
	 */
	public abstract void set_bottom_indent( int bottom_indent );

	/**
	 * Defines if text on a ruler will be drawn on the white background (<code>true</code>)
	 * or not (<code>false</code>)
	 * 
	 * @param on
	 *            if <code>true</code> the text will be drawn on white
	 *            background, if <code>false</code> it will be drawn
	 *            transparently
	 * @return previous setting for this properties
	 */
	public abstract boolean set_text_background( boolean on );

	/**
	 * Sets ruler cell width
	 * 
	 * @param w
	 *            new width of the ruler cell. The whole ruler width consists of
	 *            20 (twenty) of such cells
	 */
	public abstract void set_cell_width( int w );

	/**
	 * Gets current cell width, default value is 10.
	 * 
	 * @return integer width of the smallest ruler cell. The whole ruler width
	 *         consists of 20 (twenty) of such cells
	 */
	public abstract int get_cell_width();

	/**
	 * Sets ruler cell height
	 * 
	 * @param h
	 *            new height of the ruler cell. The whole ruler height consists
	 *            of 2 (two) of such cells
	 */
	public abstract void set_cell_height( int h );

	/**
	 * Gets current smallest cell height, default value is 3.
	 * 
	 * @return integer height of the smallest ruler cell. The whole ruler height
	 *         consists of 2 (two) of such cells
	 */
	public abstract int get_cell_height();

}
