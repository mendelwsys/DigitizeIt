/**
 * Created on 07.02.2008 12:49:10 2008 by Syg for project in
 * 'ru.ts.gisutils.potentialmap' of 'test'
 */
package ru.ts.gisutils.potentialmap;

import ru.ts.gisutils.common.SpectralLibrary;

/**
 * works for colors generating for any of corresponding values.
 * 
 * Note: order of colour component storage in this class is as follow: Low
 * byte->Red, Middle byte->Green, Most Byte->Blue. It is so in my native
 * languages including C, C++, Pascal. <br>
 * <br>
 * In Java language the order is opposite, that is low byte for Blue, middle for
 * Green and high for Red. <strong>Remember this difference!!</strong>
 * 
 * @author Syg
 */
public class ColorScheme
{
	private static final int DEFAULT_ZERO_COLOR = 0xFFFFFFFF;
	
	private int	                  _palTemplate;

	/**
	 * lowest value colour
	 */
	public int	                  bottomColor;

	/**
	 * highest value colour
	 */
	public int	                  topColor;

	/**
	 * colour for the zero level, default is 0xFFFFFFFF and means absence of zero
	 * colour which is replace by bottomColor. If value is not 0xFFFFFFFF, this
	 * colour is used for any values on the map equal to 0.0
	 */
	int	                          _zeroColor;

	/**
	 * value for the maximum potential allowed. All values above will be
	 * truncated to this value. Set it to 0.0 or Float.NaN to mark not to use
	 * this maximum and calculate maximum dynamically from the potential matrix
	 * before each processing step
	 */
	double	                      _maxAllowedPotential;
	
	/**
	 * a level of potential to start its merging
	 * so that potential figure looks to merge with background at its edges
	 * gradually.
	 * If it is equal to 0.0F (zero) it means no merging allowed  
	 */
	double						  _mergeLevel;

	/**
	 * custom means nothing, so you should to set all items manually
	 */
	public static final int	      palCustom	    = 0;

	/**
	 * from blue to red
	 */
	public static final int	      palBlueRed	= 1;

	/**
	 * from red to blue
	 */
	public static final int	      palRedBlue	= 2;

	/**
	 * from dark green to light one
	 */
	public static final int	      palGreen	    = 3;

	/**
	 * from dark blue to light one
	 */
	public static final int	      palBlue	    = 4;

	/**
	 * from dark red to light one
	 */
	public static final int	      palRed	    = 5;

	/**
	 * from dark yellow to light one
	 */
	public static final int	      palYellow	    = 6;

	/**
	 * from dark to gray
	 */
	public static final int	      palGrey	    = 7;

	/**
	 * most natural palette but time consuming on compute
	 */
	public static final int	      palRainbow	= 8;

	/**
	 * default palette
	 */
	public static final int	      palRainbow2	= 9;

	private static final int	  PAL_MAX_VALUE	= palRainbow2;

	private static final String[]	palNames	= new String[] { "Custom",
	        "BlueRed", "RedBlue", "Green", "Blue", "Red", "Yellow", "Gray",
	        "Rainbow", "Rainbow2"	            };

	private static final int[][]	_colors	         = new int[][] {
	        { 0x00000000, 0x00FFFFFF }, /* palCustom */
	        { 0x00FF0000, 0x000000FF }, // palBlueRed
	        { 0x000000FF, 0x00FF0000 }, // palRedBlue
	        { 0x00004000, 0x00E0FFE0 }, // palGreen
	        { 0x00800000, 0x00FFFF00 }, // palBlue
	        { 0x00000080, 0x008080FF }, // palRed
	        { 0x00008080, 0x0080FFFF }, // palYellow
	        { 0x00404040, 0x00E0E0E0 }	             /* palGrey */};

	/**
	 * detailed constructor
	 * 
	 * @param template
	 *            template for the palette
	 * @param zeroColor
	 *            zero colour value. 0xFFFFFFFF means absence of zero colour
	 * @param maxPotential
	 *            maximum value allowed to use for potential calculations use it
	 *            with care or legend could change unpredictably for an average
	 *            minded man :o)
	 */
	public ColorScheme( int template, int zeroColor, double maxPotential )
	{
		this( template, zeroColor );
		if ( maxPotential > 0.0F )
			this._maxAllowedPotential = maxPotential;
	}

	/**
	 * most detailed constructor
	 * 
	 * @param template
	 *            template for the palette
	 * @param zeroColor
	 *            zero colour value. 0xFFFFFFFF means absence of zero colour
	 * @param maxPotential
	 *            maximum value allowed to use for potential calculations use it
	 *            with care or legend could change unpredictably for an average
	 *            minded man :o)
	 * @param mergeLevel
	 *            a level of potential to start its merging so that potential
	 *            figure looks to merge with background at its edges gradually.
	 *            If mergeLevel is set to 0.0F (zero) it means no merging allowed
	 * 
	 */
	public ColorScheme( int template, int zeroColor, double maxPotential, double mergeLevel )
	{
		this(template,zeroColor, maxPotential);
		this.set_mergeLevel( mergeLevel );
	}
	
	/**
	 * average detailed constructor
	 * 
	 * @param template
	 *            template for the palette
	 * @param zeroColor
	 *            zero colour value. 0xFFFFFFFF means absence of zero colour
	 */
	public ColorScheme( int template, int zeroColor )
	{
		/**
		 * remember the user colour scheme
		 */
		setScheme( template, zeroColor );

		/**
		 * set max potential value not to use it when building raster image with
		 * potential above the background
		 */
		_maxAllowedPotential = Float.NaN;
	}

	/**
	 * default constructor means palRainbow2 and no zero colour
	 * 
	 */
	public ColorScheme()
	{
		this( palRainbow2, DEFAULT_ZERO_COLOR );
	}

	/**
	 * constructor with default zeroColor == 0xFFFFFFFF
	 * 
	 * @param template
	 */
	public ColorScheme( int template )
	{
		this( template, DEFAULT_ZERO_COLOR );
	}

	/**
	 * average detailed constructor
	 * 
	 * @param template
	 *            template for the palette
	 * @param maxPotential
	 *            maximum value allowed to use for potential calculations use it
	 *            with care or legend could change unpredictably for an average
	 *            minded man :o) To switch off maximum potential usage, set its
	 *            value to 0.0F
	 */
	public ColorScheme( int template,  double maxPotential )
	{
		this( template, DEFAULT_ZERO_COLOR, maxPotential );
	}
	
	/**
	 * gets name of the predefined palettes. I should be palCustom, palBlueRed,
	 * palRedBlue, palGreen, palBlue, palRed, palYellow, palGray, palRainbow,
	 * palRainbow2
	 * 
	 * @param paletteId
	 *            integer predefined palette identifier.
	 * 
	 * @return String with palette name or "Unknown" for unknown Id
	 */
	public static String getStdPaletteName( int paletteId )
	{
		if ( ( paletteId < 0 ) || ( paletteId > palRainbow2 ) )
			return "Unknown";
		return palNames[ paletteId ];
	}

	public static boolean isLegalPalette( int paletteId )
	{
		return ( paletteId >= 0 ) && ( paletteId <= PAL_MAX_VALUE );
	}

	public String getPaletteName()
	{
		return getStdPaletteName( _palTemplate );
	}

	/**
	 * sets the colour scheme to the predefined values.
	 * 
	 * There is follow specific in that: after you sets palette template, you
	 * can change bottom and top colors, but it influence coloring differently
	 * for different palettes, namely your change doesn't influence palettes of
	 * type palRainbow and palRainbow2, but influence all other palettes
	 * 
	 * @param palTemplate
	 * @param zeroColor
	 * @throws IndexOutOfBoundsException
	 */
	public void setScheme( int palTemplate, int zeroColor )
	        throws IndexOutOfBoundsException
	{
		switch ( palTemplate )
		{
		case palCustom:
		case palBlueRed:
		case palRedBlue:
		case palGreen:
		case palBlue:
		case palRed:
		case palYellow:
		case palGrey:
			bottomColor = _colors[ palTemplate ][ 0 ];
			topColor = _colors[ palTemplate ][ 1 ];
			break;
		case palRainbow:
			bottomColor = SpectralLibrary.rainbow( 0.0 );
			topColor = SpectralLibrary.rainbow( 1.0 );
			break;
		case palRainbow2:
			bottomColor = SpectralLibrary.rainbow2( 0.0 );
			topColor = SpectralLibrary.rainbow2( 1.0 );
			break;
		default:
			throw new IndexOutOfBoundsException(
			        "schemeId should be in bounds (" + palCustom + ".."
			                + palRainbow2 + ")" );
		}
		_zeroColor = zeroColor;
		_palTemplate = palTemplate;
	}

	/**
	 * @return the special zeroColor for values 0.0 on the spectral axis
	 */
	public final int get_zeroColor()
	{
		if ( _zeroColor == DEFAULT_ZERO_COLOR )
			return bottomColor;
		return _zeroColor;
	}

	/**
	 * @param zeroColor
	 *            sets special colour for values 0.0 on the spectral axis
	 */
	public final void set_zeroColor( int zeroColor )
	{
		this._zeroColor = zeroColor;
	}

	/**
	 * gets maximum potential allowed.
	 * 
	 * @return the _maxAllowedPotential
	 */
	public final double get_maxAllowedPotential()
	{
		return _maxAllowedPotential;
	}

	/**
	 * sets maximum potential allowed. Must be strictly > 0.0F or usage of this
	 * parameter is prohibited by such setting
	 * 
	 * @param potential
	 *            the _maxAllowedPotential to set
	 */
	public final void set_maxAllowedPotential( float potential )
	{
		_maxAllowedPotential = potential;
	}

	/**
	 * detects is potential is limited from above or not
	 * 
	 * @return <code>true</code> if potential is limited or <code>false</code>
	 *         if not
	 */
	public boolean useMaximumPotential()
	{
		return _maxAllowedPotential > 0.0F;
	}

	/**
	 * gets mode of merging. Merging means gradually increasing transparency up
	 * to the 100% beginning from a predefined potential value. Merging may be
	 * disallowed if _mergeLevel is set to 0.0F.
	 * 
	 * @return the current merge level
	 */
    public final double get_mergeLevel()
    {
    	if ( Double.isNaN( _mergeLevel ) )
    			return 0.0F;
    	return _mergeLevel;
    }

	/**
	 * sets a merge level. If level is set to .LE. 0.0F (zero) it means merging
	 * disallowing and level is set to 0.0F. The level could not be .GT. 1.0F and is
	 * forced to this value by this setter
	 * 
	 * @param level
	 *            the new merge level to set. Use 0.0F value to disallow merging
	 */
    public final void set_mergeLevel( double level )
    {
    	if ( Double.isNaN( level ) || ( level < 0.0F ) )
			level = 0.0F;
    	else if ( level > 1.0F )
    		level = 1.0F;
    	_mergeLevel = level;
    }

	public boolean useMerging()
	{
		return _mergeLevel > 0.0F;
	}
	
	/**
	 * translates value from a range [0.0..1.0] to a colour from the designated
	 * palette
	 * 
	 * @param fraction
	 *            value in range 0.0..1.0. If value is out of such range, it is
	 *            forced to it.
	 * @return new colour in Delphi and C++ form, that is in 0x00BBGGRR mask
	 */
	public int legendColor( double fraction )
	{
		switch ( _palTemplate )
		{
		case palRainbow:
			return SpectralLibrary.rainbow( fraction );
		case palRainbow2:
			return SpectralLibrary.rainbow2( fraction );
		default:
			return SpectralLibrary.ColorInterpolate( fraction, bottomColor,
			        topColor );
		}
	}

	/**
	 * converts C++ colour RGB to Java colour BGR as it will be stored in the
	 * processor register
	 * 
	 * @param CColorRGB
	 *            C++ (Delphi) colour
	 * @return JavaColorBGR
	 */
	public static int colorC2Java( int CColorRGB )
	{
		int R = CColorRGB & 0x00FF;
		int B = ( CColorRGB & 0x00FF0000 ) >>> 16;
		return ( CColorRGB & 0x0000FF00 ) | ( ( R << 16 ) & 0x00FF0000 )
		        | ( B & 0x00FF );
	}

	/**
	 * @return the _palTemplate
	 */
	public final int get_palTemplate()
	{
		return _palTemplate;
	}

}
