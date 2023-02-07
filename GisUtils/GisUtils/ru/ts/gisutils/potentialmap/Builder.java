/**
 * Created on 30.01.2008 14:03:41 2008 by Syg for project in
 * 'ru.ts.gisutils.potentialmap' of 'test'
 */
package ru.ts.gisutils.potentialmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;

import ru.ts.gisutils.common.Geom;
import ru.ts.gisutils.common.SpectralLibrary;
import ru.ts.gisutils.algs.common.IMCoordinate;

/**
 * Main class to build the potential matrix above the points with potential
 * values
 * 
 * @author Syg
 */
/**
 * @author Syg
 */
public class Builder
{

	/**
	 * source image with a some background
	 */
	BufferedImage	   _srcimg;

	/**
	 * source image rectangle in pixels.
	 */
	Rectangle	       _pixrect;

	/**
	 * rectangle of drawing area in world coordinates. Data points should be in
	 * the same coordinate space.
	 */
	Rectangle2D.Double	_world_rect;

	/**
	 * drawing phenomenon profile
	 */
	GeomProfile	       _profile;

	/**
	 * points array
	 */
	IMCoordinate[]	_points;

	/**
	 * matrix of values
	 */
	DoubleMatrix	       _matr;

	/**
	 * max radius for the item in pixels
	 */
	int	               _max_radious;

	/**
	 * maximum value for the points
	 */
	double	           _max_value;

	/**
	 * minimum value for all points
	 */
	// double _min_value;
	double	           _val_delta;

	/**
	 * matrix width
	 */
	int	               _w;

	/**
	 * matrix height
	 */
	int	               _h;

	/**
	 * What is the potential transparency mode. 0.0 means opaque, 1.0 mean
	 * transparent totally
	 */
	double	           _transparency;

	double	           _opacity;

	/**
	 * center of the current point
	 */
	Point	           _cpnt;

	/**
	 * X coordiante for a current output pixel
	 */
	int	               _xc;

	/**
	 * X coordiante for a current output pixel
	 */
	int	               _yc;

	/**
	 * value of the pixel to set
	 */
	double	           _valC;

	/**
	 * relative value of current point phenomenon
	 */
	double	           _relVal;

	/**
	 * the radius for a current point in integer form
	 */
	int	               _radious;

	/**
	 * current processing input point index
	 */
	short	           _index;

	/**
	 * accumulation mode
	 */
	int	               _mode;

	ColorScheme	       _colors;

	int	               _zeroColor;

	Legend	           _legend;

	Translator	       _translator;

	/* +++++++++++++++++++++++++++S+T+A+R+T++O+F++C+O+D+E++++++++++++++++++++++++++++ */

	private void _init( Legend legend )
	{
		_transparency = 0.5F;
		_opacity = 0.5F;
		if ( legend == null )
			legend = new Legend();
		_legend = legend;
		_translator = new Translator();
	}

	public Builder()
	{
		_init( null );
	}

	public Builder( Legend legend )
	{
		_init( legend );
	}

	/**
	 * creates a potential map image above background one
	 * 
	 * @param worldrect
	 *            world coordinate rectangle of the 'bgimage'
	 * @param bgimage
	 *            BufferedImage to be a background for a map
	 * @param profile
	 *            GeomProfile for the phenomenon
	 * @param points
	 *            PotentialPoint[] with points to draw them onto a map
	 * @param colors
	 *            ColorScheme to build the potential map
	 * @param legendheight
	 *            if < 0 no legend is built, if 0 legend will be build
	 *            automatically, if > 0 legend will be build with this value as
	 *            its height ( in pixels)
	 * @param transparency
	 *            for the potential in range of 0.0 to 1.0. 0.0 means total
	 *            opacity for potential, 1.0 means total transparency.
	 * @param mode
	 *            integer to select potential build mode, may be of 4 types, namely:
	 *            doubleMatrix.PM_SUM, doubleMatrix.PM_MIN, doubleMatrix.PM_MAX,
	 *            doubleMatrix.PM_AVG
	 * @return new BufferedImage with a map above 'bgimage' or <code>null</code>
	 *         on any error. Format of bitmap is a BufferedImage.TYPE_INT_RGB
	 */
	public BufferedImage makeImage( Rectangle2D worldrect,
	        BufferedImage bgimage, GeomProfile profile,
	        IMCoordinate[] points, ColorScheme colors, int legendheight,
	        double transparency, int mode )
	{
		final Legend leg = getLegend();
		if ( legendheight == 0 )
			leg.set_height( Legend.DEFAULT_HEIGHT );
		else
		{
			if ( legendheight < 0 )
				leg.drawLegend = false;
			else
				leg.set_height( legendheight );
		}
		return this.makeImage( worldrect, bgimage, profile, points, colors,
		        transparency, mode );
	}

	/**
	 * creates correct copy of the image
	 * @param src BufferedImage 
	 * @return a copy of the input image but with not less than 24 colour bits, or <code>null</code>
	 * if any error or it is not possible to create such image.  
	 */
	public static BufferedImage makeImageCopy( BufferedImage src )
	{
		// check colour mode correctness 
		ColorModel cm = src.getColorModel();
		int[] colorBits = cm.getComponentSize();
		int component_cnt = colorBits.length;
		if ( (component_cnt > 4) || (component_cnt < 3) )
		{
			System.err.println("Number of colour components per pixel should be 3 or 4, but is " + component_cnt);
			return null;
		}

		SampleModel sm = src.getSampleModel();
		int nb = sm.getNumBands();
		int dt = sm.getDataType();
		int[] ss = sm.getSampleSize();

		int transferType = src.getColorModel().getTransferType();
		int colorType = BufferedImage.TYPE_3BYTE_BGR;
		switch ( transferType )
		{
		case DataBuffer.TYPE_BYTE:
			switch ( component_cnt )
			{
			case 3:
				colorType = BufferedImage.TYPE_3BYTE_BGR;
				break;
			case 4:
				colorType = BufferedImage.TYPE_4BYTE_ABGR;
				break;
			}
			break;
		case DataBuffer.TYPE_INT:
			switch ( component_cnt )
			{
			case 3:
				colorType = BufferedImage.TYPE_INT_RGB;
				break;
			case 4:
				colorType = BufferedImage.TYPE_INT_ARGB;
				break;
			}
			break;
		default:
			System.err.println( "transferType should be 0 or 3 but is " + transferType );
			return null;
		}
		BufferedImage dst;
		if ( cm instanceof IndexColorModel )
			return ((IndexColorModel)cm).convertToIntDiscrete( src.getRaster(), component_cnt == 4 );
		else
			dst = new BufferedImage( src.getWidth(), src.getHeight(), colorType );
		src.copyData( dst.getRaster() );
		return dst;
	}
	
	/**
	 * creates a potential map image above background one
	 * 
	 * @param worldrect
	 *            world coordinate rectangle of the 'bgimage'
	 * @param bgimage
	 *            BufferedImage to be a background for a map. Image type should
	 *            be from a follow set: TYPE_INT_RGB, TYPE_3BYTE_BGR,
	 *            TYPE_4BYTE_ABGR, TYPE_4BYTE_ABGR_PRE, TYPE_INT_ARGB
	 * @param profile
	 *            GeomProfile for the phenomenon
	 * @param points
	 *            PotentialPoint[] with points to draw them onto a map
	 * @param colors
	 *            ColorScheme to build the potential map
	 * @param transparency
	 *            for the potential in range of 0.0 to 1.0. 0.0 means total
	 *            opacity for potential, 1.0 means total transparency.
	 * @param mode
	 *            int to select potential build mode, may be of 4 types, namely:
	 *            doubleMatrix.PM_SUM, doubleMatrix.PM_MIN, doubleMatrix.PM_MAX,
	 *            doubleMatrix.PM_AVG
	 * @return new BufferedImage with a map above 'bgimage' or <code>null</code>
	 *         on any error. Format of bitmap is a BufferedImage.TYPE_INT_RGB
	 */
	public BufferedImage makeImage( Rectangle2D worldrect,
	        BufferedImage bgimage, GeomProfile profile,
	        IMCoordinate[] points, ColorScheme colors, double transparency,
	        int mode )
	{
		if ( ( bgimage == null ) || ( !( bgimage instanceof BufferedImage ) ) )
			return null;
		if ( ( points == null ) || ( points.length == 0 ) )
			return null;
		
		_srcimg = bgimage;
		_w = _srcimg.getWidth();
		_h = _srcimg.getHeight();
		_world_rect = new Rectangle2D.Double( worldrect.getX(), worldrect
		        .getY(), worldrect.getWidth(), worldrect.getHeight() );
		_points = points;

		init( mode, profile, colors, transparency );

		/* ====================== DO IT ====================== */

		calculatePotentialA();

		BufferedImage outimg = makeImageCopy( bgimage );
		if ( outimg == null )
			return null;
		try
		{
			potentials2ColorsA( outimg, colors );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
		}

		/* returns a beautiful image above some background */

		_srcimg = null;
		return outimg;
	}

	/**
	 * sets initial values for the internal variables
	 * 
	 * @param mode
	 * @param profile
	 * @param colors
	 * @param transparency
	 */
	private void init( int mode, GeomProfile profile, ColorScheme colors,
	        double transparency )
	{
		_mode = mode;
		_profile = profile;
		_colors = colors;
		if ( transparency < 0.0F )
			transparency = 0.0F;
		else if ( transparency > 1.0F )
			transparency = 1.0F;
		_transparency = transparency;
		_opacity = 1.0F - transparency;
		_zeroColor = _colors.get_zeroColor();
	}

	/**
	 * rebuild colour image from existing potential matrix but with different
	 * background and colour scheme. Potential should exists so you first should
	 * call the method makeImage before this one
	 * 
	 * @param bgimage
	 *            new background image. Should have the same size as previously
	 *            processed.
	 * @param colors
	 *            ColorScheme for a new image
	 * @param legendheight
	 *            legend height in pixels
	 * @param transparency
	 *            double value of transparency for the potential. Should be in
	 *            range from 0.0 (opaque) to 1.0 (totally transparent, so not
	 *            drawn at all)
	 * @param mode
	 *            int mode of potential field accumulation
	 * @throws IllegalArgumentException
	 *             is thrown is Width or Height of new bg image are different
	 *             from previous image sizes.
	 */
	public BufferedImage remakeImage( BufferedImage bgimage,
	        ColorScheme colors, int legendheight, double transparency, int mode )
	        throws IllegalArgumentException
	{
		if ( legendheight == 0 )
			getLegend().drawLegend = false;
		else
			getLegend().set_height( legendheight );
		return remakeImage( bgimage, colors, transparency );
	}

	/**
     * rebuild colour image from existing potential matrix but with different
     * background and colour scheme. Potential should exists so you first should
     * call the method <code>makeImage(...)</code> before this one.
     * 
     * @param bgimage
     *            new background image. Should have the same size as previously
     *            processed. May be <code>null</code> in that case original background
     *            image will be used
     * @param colors
     *            ColorScheme for a new image. May be <code>null</code> in that
     *            case original colour scheme will be used
     * @param transparency
     *            double value of transparency for the potential. Should be in
     *            range from 0.0 (opaque) to 1.0 (totally transparent, so not
     *            drawn at all)
     * @param mode
     *            integer mode of potential field accumulation. Really it is a
     *            dummy parameter declaration as mode was used during potential
     *            creation BEFORE this call and can't be changed without regeneration
     *            of an potential itself
     * @throws IllegalArgumentException
     *             is thrown is Width or Height of new background image are different
     *             from previous image sizes.
     * @deprecated Use {@link #remakeImage(BufferedImage,ColorScheme,double)} instead
     */
    public BufferedImage remakeImage( BufferedImage bgimage,
            ColorScheme colors, double transparency, int mode )
            throws IllegalArgumentException
    {
        return remakeImage( bgimage, colors, transparency );
    }

	/**
	 * rebuild colour image from existing potential matrix but with different
	 * background and colour scheme. Potential should exists so you first should
	 * call the method <code>makeImage(...)</code> before this one.
	 * 
	 * @param bgimage
	 *            new background image. Should have the same size as previously
	 *            processed. May be <code>null</code> in that case original background
	 *            image will be used
	 * @param colors
	 *            ColorScheme for a new image. May be <code>null</code> in that
	 *            case original colour scheme will be used
	 * @param transparency
	 *            double value of transparency for the potential. Should be in
	 *            range from 0.0 (opaque) to 1.0 (totally transparent, so not
	 *            drawn at all)
	 * @throws IllegalArgumentException
	 *             is thrown is Width or Height of new background image are different
	 *             from previous image sizes.
	 */
	public BufferedImage remakeImage( BufferedImage bgimage,
	        ColorScheme colors, double transparency )
	        throws IllegalArgumentException
	{
		if ( ( bgimage == null ) || ( bgimage.getWidth() != _w ) || ( bgimage.getHeight() != _h ) )
			throw new IllegalArgumentException(
				        "New image is null or have different dimensions" );
		_srcimg = bgimage;
		/* create output image now */
		BufferedImage outimg = makeImageCopy( bgimage );

		/* do nothing except setting a new transparency */
		init( _mode, _profile, _colors, transparency );
		try
		{
			potentials2ColorsA( outimg, colors != null ? colors : _colors );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
		}

		_srcimg = null;
		/* returns a beautiful image drawn on a some background */
		return outimg;
	}


	
	/**
	 * translates potential to colours on bitmap and build the legend
	 * 
	 * @param image
	 *            BufferedImage to draw potential on it
	 * @param colors
	 *            ColorScheme to generate colours from potential
	 * @throws Exception
	 *             any exception during execution
	 */
	private void potentials2Colors( BufferedImage image, ColorScheme colors )
	        throws Exception

	{
		if ( image == null )
			throw new IllegalArgumentException( "Image is null" );

		/* play with transparency */
		if ( _transparency == 1.0 )
			return;
		boolean transparency = _transparency > 0.0F;

		/* play with potential maximum */
		if ( _matr != null )
		{
			/* find maximum value in the resulting matrix */
			if ( colors.useMaximumPotential() )
				_max_value = colors.get_maxAllowedPotential();
			else
				_max_value = _matr.findMax();
			for ( int i = 0; i < image.getWidth(); i++ )
			{
				for ( int j = 0; j < image.getHeight(); j++ )
				{
					double val = _matr.get_item( j, i );
					if ( val == DoubleMatrix.DEFAULT_VALUE )
					{
						if ( !transparency )
							image.setRGB( i, j, ColorScheme.colorC2Java( colors
							        .get_zeroColor() ) );
						/* else not change the picture at all */
					}
					else
					{
						val = Math.min( val, _max_value ) / _max_value;
						/* clear alpha channel */
						int in = image.getRGB( i, j ) & 0x00FFFFFF;
						int out = overlayPotential2Pixel( in, colors.legendColor( val ) );
						if ( out != in )
							image.setRGB( i, j, out );
					}
				}
			}
		}
		else
			_max_value = colors.get_maxAllowedPotential();

		/***********************************************************************
		 * play with a legend *
		 **********************************************************************/

		Legend leg = getLegend();
		if ( !leg.drawLegend )
			return;
		int lh = leg.get_height( image ); /* legend height */
		int lw = leg.get_width( image ); /* legend width */
		lh = Math.min( lh, _h - 80 );
		if ( lh < 16 )
			lh = _h / 3;
		/* check if image is too low to draw legend at all */
		if ( lh < 40 )
			return;
		Rectangle rect = new Rectangle(); /* rect for a legend */
		int legROff = leg.get_right( image ); /* legend offset from a right */
		int legBOff = leg.get_bottom( image ); /* legend offset from a _bottom */
		rect.setBounds( _w - lw - legROff, _h - lh - legBOff, lw, lh );

		/* draw mono coloured horizontal strips */
		for ( int i = 0; i < lh; i++ )
		{
			int colour = colors
			        .legendColor( (double) ( lh - 1 - i ) / ( lh - 1 ) );
			for ( int j = rect.x; j < rect.x + lw; j++ )
				image.setRGB( j, rect.y + i, ColorScheme.colorC2Java( colour ) );
		}

		/* draw legend bounding rect */
		Graphics gr = image.getGraphics();
		gr.setColor( Color.BLACK );
		gr.setPaintMode();
		gr.drawRect( rect.x, rect.y, lw, lh );

		/* draw texts for upper values above */
		String txt = String.valueOf( ru.ts.gisutils.common.Sys.roundTo( 2,
		        _translator.translatevalue( _max_value ) ) );
		/* prepare font */
		Font font = gr.getFont();
		font = new Font( font.getFontName(), Font.BOLD, Math.min( 12, font
		        .getSize() ) );
		gr.setFont( font );
		FontMetrics fm = gr.getFontMetrics();
		Rectangle2D rect2 = fm.getStringBounds( txt, gr );
		Rectangle txtrect = Geom.offsetRect2D( rect2, -rect2.getX(),
		        -rect2.getY() ).getBounds();
		/* calculate centre of legend along Y axis */
		int xc = rect.x + rect.width / 2;
		int tx = xc - txtrect.width / 2;
		if ( ( tx + txtrect.width + 6 ) >= _w )
			tx -= _w - tx - txtrect.width - 6;
		txtrect.x = tx;
		txtrect.y = rect.y - txtrect.height - 3;
		gr.setXORMode( Color.WHITE );
		gr.drawString( txt, txtrect.x, txtrect.y + txtrect.height );

		/* and under legend bar */
		txt = "0";
		rect2 = fm.getStringBounds( txt, gr );
		txtrect = Geom.offsetRect2D( rect2, -rect2.getX(), -rect2.getY() )
		        .getBounds();
		gr.drawString( txt, xc - txtrect.width / 2, rect.y + rect.height
		        + txtrect.height );
	}

	/**
	 * translates potential to colours on bitmap and build the legend
	 * 
	 * @param image
	 *            BufferedImage to draw potential on it
	 * @param colours
	 *            ColorScheme to generate colours from potential
	 * @throws Exception
	 *             any exception during execution
	 */
	private void potentials2ColorsA( BufferedImage image, ColorScheme colours )
	        throws Exception

	{
		if ( image == null )
			throw new IllegalArgumentException( "Image is null" );

		/* play with transparency */
		if ( _transparency == 1.0 )
			return;
		boolean transparency = _transparency > 0.0F;
		double mergelevel = colours.get_mergeLevel();
		double opacity = _opacity;

		/* play with potential maximum */
		if ( _matr != null )
		{
			/* find maximum value in the resulting matrix */
			if ( colours.useMaximumPotential() )
				_max_value = colours.get_maxAllowedPotential();
			else
				_max_value = _matr.findMax();

			if ( true ) /* use a new technology */
			{
				DoubleMatrix.MultiFinder finder = new DoubleMatrix.MultiFinder( _matr );
				while ( _matr.find_values( finder ) )
				{
					for ( int k = 0; k < finder.count; k++ )
					{
						int i = finder.get_row( k );
						int j = finder.get_column( k );
						double val = finder.values[ k ];
						val = Math.min( val, _max_value ) / _max_value;

						if ( transparency )
						{
							/* clear alpha channel */
							int in = image.getRGB( i, j ) & 0x00FFFFFF;
							int out;
							if ( val < mergelevel )
							{
								double mrg_transparency = 1.0F - val / mergelevel * opacity;
								out = mergePotential2Pixel( in, colours.legendColor( val ), mrg_transparency );
							}
							else
								out = overlayPotential2Pixel( in, colours.legendColor( val ) );
							if ( out != in ) 
								image.setRGB( i, j, out | 0xFF000000 ); /* add alpha channel */
						}
						else
						/* direct setting of a new pixel */
						{
							int out = ColorScheme.colorC2Java( colours
							        .legendColor( val ) );
							image.setRGB( i, j, out );
						}

					}
				}
			}		
			else
				/* use old technology */
				for ( int i = 0; i < image.getWidth(); i++ )
				{
					for ( int j = 0; j < image.getHeight(); j++ )
					{
						double val = _matr.get_item( j, i );
						if ( val == DoubleMatrix.DEFAULT_VALUE )
						{
							if ( !transparency )
								image.setRGB( i, j, ColorScheme
								        .colorC2Java( colours.get_zeroColor() ) );
							/* else not change the picture at all */
						}
						else
						{
							val = Math.min( val, _max_value ) / _max_value;
							/* clear alpha channel */
							int in = image.getRGB( i, j ) & 0x00FFFFFF;
							int out = overlayPotential2Pixel( in, colours.legendColor( val ) );
							if ( out != in ) 
								image.setRGB( i, j, out | 0xFF000000 ); /* add alpha channel */
						}
					}
				}
		}
		else
			_max_value = colours.get_maxAllowedPotential();

		/***********************************************************************
		 * play with a legend *
		 **********************************************************************/
		_drawLegend( image, colours, _max_value );
	}

	public void drawLegend( BufferedImage image, ColorScheme colors,
	        double max_value )
	{
		_srcimg = image;
		_w = _srcimg.getWidth();
		_h = _srcimg.getHeight();
		_world_rect = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight());
		_drawLegend(image, colors,max_value);
	}
	/**
	 * draws the legend on the image if needed
	 * 
	 * @param image
	 *            BufferedImage to draw potential on it
	 * @param colors
	 *            ColorScheme to generate colours from potential
	 * @throws Exception
	 *             any exception during execution
	 */
	private void _drawLegend( BufferedImage image, ColorScheme colors,
	        double max_value )
	{

		Legend leg = getLegend();
		if ( !leg.drawLegend )
			return;
		int lh = leg.get_height( image ); /* legend height */
		int lw = leg.get_width( image ); /* legend width */
		lh = Math.min( lh, _h - 80 );
		if ( lh < 16 )
			lh = _h / 3;
		/* check if image is too low to draw legend at all */
		if ( lh < 40 )
			return;
		Rectangle rect = new Rectangle(); /* rect for a legend */
		int legROff = leg.get_right( image ); /* legend offset from a right */
		int legBOff = leg.get_bottom( image ); /* legend offset from a _bottom */
		rect.setBounds( _w - lw - legROff, _h - lh - legBOff, lw, lh );

		/* draw mono coloured horizontal strips */
		for ( int i = 0; i < lh; i++ )
		{
			int colour = colors
			        .legendColor( (double) ( lh - 1 - i ) / ( lh - 1 ) );
			for ( int j = rect.x; j < rect.x + lw; j++ )
				image.setRGB( j, rect.y + i, 0xFF000000|ColorScheme.colorC2Java( colour ) );
		}

		/* draw legend bounding rect */
		Graphics gr = image.getGraphics();
		gr.setColor(new Color(Color.BLACK.getRGB()|0xFF000000));
		gr.setPaintMode();
		gr.drawRect( rect.x, rect.y, lw, lh );

		/* draw texts for upper values above */
		String txt = String.valueOf( ru.ts.gisutils.common.Sys.roundTo( 2,
		        _translator.translatevalue( max_value ) ) );
		/* prepare font */
		Font font = gr.getFont();
		font = new Font( font.getFontName(), Font.BOLD, Math.min( 12, font
		        .getSize() ) );
		gr.setFont( font );
		FontMetrics fm = gr.getFontMetrics();
		Rectangle2D rect2 = fm.getStringBounds( txt, gr );
		Rectangle txtrect = Geom.offsetRect2D( rect2, -rect2.getX(),
		        -rect2.getY() ).getBounds();
		/* calculate centre of legend along Y axis */
		int xc = rect.x + rect.width / 2;
		int tx = xc - txtrect.width / 2;
		if ( ( tx + txtrect.width + 6 ) >= _w )
			tx -= _w - tx - txtrect.width - 6;
		txtrect.x = tx;
		txtrect.y = rect.y - txtrect.height - 3;
//		gr.setXORMode(Color.WHITE);
//		gr.setColor(new Color(gr.getColor().getRGB()|0xFF000000));
		gr.setColor(new Color(Color.BLACK.getRGB()|0xFF000000));
		gr.drawString( txt, txtrect.x, txtrect.y + txtrect.height );

		/* and under legend bar */
		txt = "0";
		rect2 = fm.getStringBounds( txt, gr );
		txtrect = Geom.offsetRect2D( rect2, -rect2.getX(), -rect2.getY() )
		        .getBounds();
		gr.drawString( txt, xc - txtrect.width / 2, rect.y + rect.height
		        + txtrect.height );
	}

	/**
	 * overlays pixel value with a potential one
	 * 
	 * @param pix
	 *            pixel colour in Java BGR form ( Ouch !!!)
	 * @param pot
	 *            potential colour in RGB ( as in normal languages ) order
	 * @return new pixel in BGR form ( Java style )
	 */
	private int overlayPotential2Pixel( int pix, int pot )
	{
		int pixB = pix & 0x00FF;
		int pixG = ( pix >>> 8 ) & 0x00FF;
		int pixR = ( pix >>> 16 ) & 0x00FF;
		int potR = pot & 0x00FF;
		int potG = ( pot >>> 8 ) & 0x00FF;
		int potB = ( pot >>> 16 ) & 0x00FF;
		int outR =(int) Math.round( pixR * _transparency + potR * _opacity );
		int outG =(int) Math.round( pixG * _transparency + potG * _opacity );
		byte outB = (byte) Math.round( pixB * _transparency + potB * _opacity );

		/*
		 * exchange B <-> R to convert from Pascal to Java colour component
		 * ordering
		 */
		return SpectralLibrary.RGB2Int( outB, outG, outR );
	}

	/**
	 * overlays pixel value with a potential one
	 * 
	 * @param pix
	 *            pixel colour in Java BGR form ( Ouch !!!)
	 * @param pot
	 *            potential colour in RGB ( as in normal languages ) order
	 * @param merge_transp
	 * 			  merging transparency for this potential value           
	 * @return new pixel in BGR form ( Java style )
	 */
	private int mergePotential2Pixel( int pix, int pot, double merge_transp )
	{
		int pixB = pix & 0x00FF;
		int pixG = ( pix >>> 8 ) & 0x00FF;
		int pixR = ( pix >>> 16 ) & 0x00FF;
		int potR = pot & 0x00FF;
		int potG = ( pot >>> 8 ) & 0x00FF;
		int potB = ( pot >>> 16 ) & 0x00FF;
		double opacity = 1.0F - merge_transp;
		int outR = (int)Math.round( pixR * merge_transp + potR * opacity);
		int outG = (int)Math.round( pixG * merge_transp + potG * opacity );
		byte outB = (byte) Math.round( pixB * merge_transp + potB * opacity );

		/*
		 * exchange B <-> R to convert from Pascal to Java colour component
		 * ordering
		 */
		return SpectralLibrary.RGB2Int( outB, outG, outR );
	}

	public int size()
	{
		return _points.length;
	}

	/**
	 * MAIN method to run processor fast
	 * 
	 * @return <code>doubleMatrix</code> filled with pre-map values if success
	 *         or <code>null</code> if any failure
	 */
	private DoubleMatrix calculatePotential()
	{
		/* check radius value */
		if ( _profile.getMaxX() == 0.0 )
			return null;

		_pixrect = new Rectangle( 0, 0, _w, _h );

		/* prepare to filter points to be influencing a bitmap */
		IMCoordinate[] points = new IMCoordinate[_points.length];
		_matr = new DoubleMatrix( _w, _h, _mode );

		/* create translator between world and pixels */
		CoordSystemTranslator CRS = new CoordSystemTranslator( _world_rect,
		        _pixrect );

		try
		{
			/*
			 * Prepare output rectangle where all point should be found. it will
			 * contain rectangle for the drawing inflated by radios of the
			 * maximum phenomenon value
			 */

			/* make bounding rect for all points */
			Rectangle2D.Double outrect = new Rectangle2D.Double();
			outrect.setRect( _world_rect.x, _world_rect.y, _world_rect.width,
			        _world_rect.height );
			double r = Math.round( _profile.getRadius() / 2.0 );
			/*
			 * build the bounding rect for all the points as if they all are
			 * equal to the maximum
			 */
			Geom.inflateRect2D( outrect, r );

			/*
			 * filter points to be in the rectangle of interest
			 */
			int outsize = 0;
			_max_value = -Double.MAX_VALUE;
			// _min_value = double.MAX_VALUE;
			for ( int index = 0; index < size(); index++ )
			{
				IMCoordinate pnt = _points[ index ];
				if ( outrect.contains( new Point2D.Double(pnt.getX(),pnt.getY())))
					points[ outsize++ ] = pnt;
				if ( pnt.getM() > _max_value )
					_max_value = pnt.getM();
			}

			/* check what to use - data maximum or profile maximum */
			if ( _profile.useAbsValue() )
			{
				/* then use value from profile, not one calculated now */
				_max_value = _profile.getAbsValue();
			}

			/*
			 * From now works with a 'points' array, not '_points'. Prepare some
			 * constant values. Note: radious is in meters (world coordinates),
			 * not pixels.
			 */
			double maxX = _profile.getRadius();
			_max_radious = CRS.getPixX( maxX ); /* get it in pixels */

			/* MAIN loop starts HERE */
			for ( int index = 0; index < outsize; index++ ) /*
															 * for EACH of the
															 * potential points
															 */
			{
				/* get current point value fraction of the max value */
				IMCoordinate pnt = points[ index ];

				if ( pnt.getM() >= _max_value )
					_relVal = 1.0;
				else
				{
					/* calculate relative value for a current point */
					if ( pnt.getM() < 0.0F )
						_relVal = 0.0;
					else
						_relVal = pnt.getM() / _max_value;
				}

				/* get real radius for the current point */
				_radious = (int) Math.round( _max_radious * _relVal );
				/* square of the radius (for lower calculations) */
				int intRP2 = _radious * _radious;
				/**
				 * <pre>
				 *       now we are ready to process central value to show its
				 *       potential spreading. Start from the centre point
				 *       
				 *             +
				 * 
				 */
				_xc = CRS.getPixX( pnt.getX());
				_yc = CRS.getPixY( pnt.getY());

				_cpnt = new Point( _xc, _yc );

				_valC = _profile.getY( 0.0 );
				putPix();

				/**
				 * <pre>
				 *        DON'T FORMAT this comment or it will be reformatted incorrectly...
				 *       
				 *       now loop along central rows from zero to the right 
				 *       (to X positive direction). Note that center is already filled
				 *       
				 *              &circ;
				 *        1     +     0
				 *              +
				 *              +
				 *        + + + + + + + &gt;
				 *              +
				 *        2     +     3
				 *              +      
				 * </pre>
				 */

				for ( int j = 1; j <= _radious; j++ )
				{
					/*
					 * first build pixels along X, Y axis
					 */
					double dist = j * maxX / _radious;
					_valC = _profile.getY( dist );
					/* current value set */
					_xc = _cpnt.x + j;
					_yc = _cpnt.y;
					putPix(); // axis 0
					_xc = _cpnt.x - j;
					putPix(); // axis 2
					_xc = _cpnt.x;
					_yc = _cpnt.y - j;
					putPix(); // axis 3
					_yc = _cpnt.y + j;
					putPix(); // axis 1
				}

				/**
				 * <pre>
				 *       now calculate one of 8 symmetrical parts between rays, based by
				 *       axes themselves and bisector of quadrants
				 *               ++.++
				 *            +++++.++*oo
				 *          +++++++.*oooooo
				 *          ...............
				 *          +++++++.+++++++
				 *            +++++.+++++
				 *               ++.++
				 *         '*' stands for symmetry axis (bisector of quadrant)
				 *         'o' stands for currently generating pixels from the main 
				 *         	  sector to mirror over axis,
				 *         '+' stands for other pixels, which are copied from '*' and 'o'  ones
				 *         '.' stands for axes pixels with already generated potential
				 * </pre>
				 */
				for ( int j = 1; j <= _radious; j++ )
				{
					/* calculate the square of the current pixel radius */
					int jSq; // square of 'j'
					int intRC2 = ( jSq = j * j ) * 2;
					if ( intRC2 > intRP2 ) // then sector building is completed
						break;
					double dist = Math.sqrt( intRC2 ) / _radious * maxX;
					_valC = _profile.getY( dist );

					/*
					 * don't leave if value is equal to zero, as user can invert
					 * very strange profile line
					 */
					if ( _valC == 0.0 )
						continue;

					/* goes along bisectors */
					reflectPixel( j, j );

					/*
					 * now emit horizontal ray to the right up to the circle
					 * bound
					 */

					for ( int k = j + 1; k <= _radious; k++ )
					{
						intRC2 = k * k + jSq;
						if ( intRC2 > intRP2 ) // then already out of the point
							// drawing circle
							break;
						dist = Math.sqrt( intRC2 ) / _radious * maxX;
						_valC = _profile.getY( dist );
						if ( _valC == 0.0 )
							continue;
						reflectPixel( k, j );
						reflectPixel( j, k );
					}
				}
			}
			/*
			 * multiply all relative values by max value to return them into a
			 * real space
			 */
			_matr.multiplyBy( (double) _max_value );
			return _matr;
		}
		catch ( Exception ex )
		{
			return null;
		}
	}

	/**
	 * MAIN method to run processor fast
	 * 
	 * @return <code>doubleMatrix</code> filled with pre-map values if success
	 *         or <code>null</code> if any failure
	 */
	private DoubleMatrix calculatePotentialA()
	{
		/* check radius value */
		if ( _profile.getMaxX() == 0.0 )
			return null;

		_pixrect = new Rectangle( 0, 0, _w, _h );

		/* prepare to filter points to be influencing a bitmap */
		short[] points = new short[_points.length];
		_matr = new DoubleMatrix( _w, _h, _mode );

		/* create translator between world and pixels */
		CoordSystemTranslator CRS = new CoordSystemTranslator( _world_rect,
		        _pixrect );

		try
		{
			/*
			 * Prepare output rectangle where all point should be found. it will
			 * contain rectangle for the drawing inflated by radius of the
			 * maximum phenomenon value
			 */

			/* make bounding rectangle for all points */
			Rectangle2D.Double outrect = new Rectangle2D.Double();
			outrect.setRect( _world_rect.x, _world_rect.y, _world_rect.width,
			        _world_rect.height );
			double r = Math.round( _profile.getRadius() / 2.0 );
			/*
			 * build the bounding rectangle for all the points as if they all
			 * are equal to the maximum
			 */
			Geom.inflateRect2D( outrect, r );

			/*
			 * filter points to be in the rectangle of interest
			 */
			int outsize = 0;
			_max_value = -Double.MAX_VALUE;
			// _min_value = double.MAX_VALUE;
			/* select points for the building rectangle */
			for ( int index = 0; index < size(); index++ )
			{
				IMCoordinate pnt = _points[ index ];
				if ( pnt == null )
					continue;

				if (outrect.contains(new Point2D.Double(pnt.getX(),pnt.getY())))/* then in rectangle */
					if ( pnt.getM() <= 0.0F ) /*
												 * check if value is correct,
												 * that is .GT. 0.0F
												 */
						continue;
				points[ outsize++ ] = (short) index;
				/* simultaneously find maximum value */
				if ( pnt.getM() > _max_value )
					_max_value = pnt.getM();
			}

			/* check what to use - data maximum or profile maximum */
			if ( _profile.useAbsValue() )
			{
				/* then use value from profile, not one calculated now */
				_max_value = _profile.getAbsValue();
			}

			/*
			 * From now works with a 'points' array, not '_points'. Prepare some
			 * constant values. Note: radius is in meters (world coordinates),
			 * not pixels.
			 */
			double maxX = _profile.getRadius();
			_max_radious = CRS.getPixX( maxX ); /* get it in pixels */

			/* MAIN loop starts HERE */

			/* for EACH of the potential points DO */
			for ( int index = 0; index < outsize; index++ )
			{
				/*
				 * gets point and its index in input array (needed for modes
				 * PM_MIN and PM_MAX)
				 */
				IMCoordinate pnt = _points[ _index = points[ index ] ];
				if ( pnt == null )
					continue;

				/* get current point value fraction of the max value */
				if ( pnt.getM() >= _max_value )
					_relVal = 1.0;
				else
				{
					/* calculate relative value for a current point */
					if ( pnt.getM() < 0.0F )
						_relVal = 0.0;
					else
						_relVal = pnt.getM() / _max_value;
				}

				/* get real radius for the current point */
				_radious = (int) Math.round( _max_radious * _relVal );
				/* square of the radius (for lower calculations) */
				int intRP2 = _radious * _radious;
				/**
				 * <pre>
				 *       now we are ready to process central value to show its
				 *       potential spreading. Start from the centre point
				 *       
				 *             +
				 * 
				 */
				_xc = CRS.getPixX( pnt.getX() );
				_yc = CRS.getPixY( pnt.getY() );

				_cpnt = new Point( _xc, _yc );

				_valC = _profile.getY( 0.0 );
				putPix();

				/**
				 * <pre>
				 *        DON'T FORMAT this comment or it will be reformatted incorrectly...
				 *       
				 *       now loop along central rows from zero to the right 
				 *       (to X positive direction). Note that centre is already filled
				 *       
				 *              &circ;
				 *        1     +     0
				 *              +
				 *              +
				 *        + + + + + + + &gt;
				 *              +
				 *        2     +     3
				 *              +      
				 * </pre>
				 */

				for ( int j = 1; j <= _radious; j++ )
				{
					/*
					 * first build pixels along X, Y axis
					 */
					double dist = j * maxX / _radious;
					_valC = _profile.getY( dist );
					/* current value set */
					_xc = _cpnt.x + j;
					_yc = _cpnt.y;
					putPix(); // axis 0
					_xc = _cpnt.x - j;
					putPix(); // axis 2
					_xc = _cpnt.x;
					_yc = _cpnt.y - j;
					putPix(); // axis 3
					_yc = _cpnt.y + j;
					putPix(); // axis 1
				}

				/**
				 * <pre>
				 *       now calculate one of 8 symmetrical parts between rays, based by
				 *       axes themselves and bisectors of quadrants
				 *               ++.++
				 *            +++++.++*oo
				 *          +++++++.*oooooo
				 *          ...............
				 *          +++++++.+++++++
				 *            +++++.+++++
				 *               ++.++
				 *         '*' stands for symmetry axis (bisector of quadrant)
				 *         'o' stands for currently generating pixels from the main 
				 *         	  sector to mirror over axis,
				 *         '+' stands for other pixels, which are copied from '*' and 'o'  ones
				 *         '.' stands for axes pixels with already generated potential
				 * </pre>
				 */
				for ( int j = 1; j <= _radious; j++ )
				{
					/* calculate the square of the current pixel radius */
					int jSq; // square of 'j'
					int intRC2 = ( jSq = j * j ) * 2;
					if ( intRC2 > intRP2 ) // then sector building is completed
						break;
					double dist = Math.sqrt( intRC2 ) / _radious * maxX;
					_valC = _profile.getY( dist );

					/*
					 * don't leave if value is equal to zero, as user can invert
					 * very strange profile line
					 */
					if ( _valC == 0.0 )
						continue;

					/* goes along bisector */
					reflectPixel( j, j );

					/*
					 * now emit horizontal ray to the right up to the circle
					 * bound
					 */

					for ( int k = j + 1; k <= _radious; k++ )
					{
						intRC2 = k * k + jSq;
						if ( intRC2 > intRP2 ) // then already out of the point
							// drawing circle
							break;
						dist = Math.sqrt( intRC2 ) / _radious * maxX;
						_valC = _profile.getY( dist );
						if ( _valC == 0.0 )
							continue;
						reflectPixel( k, j );
						reflectPixel( j, k );
					}
				}
			}
			/*
			 * multiply all relative values by max value to return them into a
			 * real space
			 */
			_matr.multiplyBy( (double) _max_value );
			return _matr;
		}
		catch ( Exception ex )
		{
			return null;
		}
	}

	/**
	 * puts set value to the pixel with x,y coordinates
	 */
	private void putPix()
	{
		if ( ( _xc < 0 ) || ( _yc < 0 ) || ( _xc >= _w ) || ( _yc >= _h ) )
			return;
		/* scale according to the relative value */
		_matr.set_item( _yc, _xc, _valC * _relVal, _index ); 	}

	/**
	 * <pre>
	 *      reflects current pixel value along 4 quadrants circle
	 *      
	 *              &circ; Y axis
	 *              |
	 *              | 
	 *           Q1 | Q0 
	 *       -------+---------&gt; X axis 
	 *           Q2 | Q3 
	 *              |
	 *              |
	 *              
	 * </pre>
	 * 
	 * Note: this method doesn't change _xc and _yc properties of this class
	 * 
	 * @param xoff
	 *            X offset from the central point
	 * @param yoff
	 *            Y offset from the central point
	 */
	private void reflectPixel( int xoff, int yoff )
	{
		_xc = _cpnt.x + xoff;
		_yc = _cpnt.y + yoff;
		putPix(); // Q0
		_xc = _cpnt.x - xoff;
		putPix(); // Q1
		_yc = _cpnt.y - yoff;
		putPix(); // Q2
		_xc = _cpnt.x + xoff;
		putPix(); // Q3
		_yc = _cpnt.y + yoff; // return coordinates to Q0
	}

	/**
	 * gets potential value for the designated pixel of the image
	 * 
	 * @param row
	 *            Y coordinate of the pixel
	 * @param column
	 *            X coordinate of the pixel
	 * @return double value for the potential if potential exists at the point or
	 *         0.0 if no potential or pixel coordinates are out of the matrix
	 *         space or any other error occurrence
	 */
	public double getPotential( int row, int column )
	{
		try
		{
			double ret = _matr.get_item( row, column );
			if ( ret == DoubleMatrix.DEFAULT_VALUE )
				return DoubleMatrix.DEFAULT_VALUE;
			return _translator.translatevalue( ret );
		}
		catch ( Exception ex )
		{
			System.err.println( "getPotential error: " + ex.getMessage() );
			return DoubleMatrix.DEFAULT_VALUE;
		}
	}

	/**
	 * gets index of potential point for the designated pixel of the image.
	 * Potential point is one of points used to generate potential values
	 * 
	 * @param row
	 *            Y coordinate of the pixel
	 * @param column
	 *            X coordinate of the pixel
	 * 
	 * @return short index for the point used to build this potential at
	 *         designated pixel. If potential is absent, -1 is returned. Else
	 *         some exception thrown on any error.
	 * 
	 * @throws IllegalAccessError
	 *             is thrown if there isn't such info at all. E.g. mode is
	 *             PM_AVR or PM_SUM as only for PM_MIN and PM_MAX such info is
	 *             available
	 * @throws IndexOutOfBoundsException
	 *             thrown if no such pixel in matrix
	 */
	public short getIndex( int row, int column ) throws IllegalAccessError,
	        IndexOutOfBoundsException
	{
		return _matr.get_index( row, column );
	}

	/**
	 * sets a new translator. Translator is the class with the only method,
	 * called only twice, first on legend max value printing and second on
	 * method getPotential(...) call to return some value.
	 * 
	 * @param trans
	 *            class inherited from Translator class
	 */
	public void settranslator( Translator trans )
	{
		_translator = trans;
	}
	
	/**
	 * gets current translator. @see Builder#settranslator(Translator)
	 * @return current Translator instance of this object
	 */
	public Translator gettranslator()
	{
		return _translator;
	}

	/**
	 * @return the Legend instance of this builder
	 */
	public Legend getLegend()
	{
		return _legend;
	}

}
