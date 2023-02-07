/**
 * Created on 30.01.2008 18:02:03 2008 by Syg
 * for project in 'ru.ts.gisutils.common' of 'test' 
 */
package ru.ts.gisutils.common;


/**
 * REMEBMER: all manipulations are done in C++/Pascal mode, that is
 * 
 * R bits stands for 0x000000FF bit mask
 * G bits stands for 0x0000FF00 bit mask and
 * B bits stands for 0x00FF0000 bit mask
 * 
 * @author Syg
 */
public class SpectralLibrary
{
	public static final int   WavelengthMinimum = 380; // Nanometers
	public static final int   WavelengthMaximum = 780;	// Nanometers
	private static final double Gamma = 0.80;
	private static final double IntensityMax = 255.0;
	
	/**
	 * constructs C++/Pascal style 24 bits colour from its R,G,B components
	 * @param R int Red component
	 * @param G int Green component
	 * @param B int Blue component
	 * @return integer colour representation where 
	 * R has mask 0x000000FF
	 * G has mask 0x0000FF00 and 
	 * B has mask 0x00FF0000 
	 */
	public static int RGB2Int(int R, int G, int B)
	{
		return (R & 0x00FF ) | ( (G << 8 ) & 0x00FF00 ) | ((B << 16 ) & 0x00FF0000 );
	}
	
	/**
	 * gets R component of colour
	 * @param CColor in C++/Pascal style colour 24 bits
	 * @return R part of colour
	 */
	public static int getR( int CColor )
	{
		return CColor & 0x00FF;
	}

	/**
	 * gets G component of colour
	 * @param CColor in C++/Pascal style colour 24 bits
	 * @return G part of colour
	 */
	public static int getG( int CColor )
	{
		return (CColor >>> 8) & 0x00FF;
	}

	/**
	 * gets B component of colour
	 * @param CColor in C++/Pascal style colour 24 bits
	 * @return B part of colour
	 */
	public static int getB(int CColor)
	{
		return  (CColor  >>> 16) & 0x00FF;
	}
	
	
	private static int adjust( double color, double factor )
	{
		if ( color == 0.0)
			return 0;
		return (int)Math.round( IntensityMax * Math.pow( color * factor, Gamma ));
	}
	
	// Overload for later convenience
	public static int WavelengthToRGB( double wavelength )
	{
		int ww = (int)wavelength;
		if ( (ww < WavelengthMinimum) || (ww > WavelengthMaximum) )
			return 0;
		double R, G, B;
		
		if ( ww <= 439 )
		{
			
			R = -( ww - 440.0 ) / ( 440.0 - 380.0 );
			G = 0.0;
			B = 1.0;
		}
		else if ( ww <= 489 )
		{
			R = 0.0;
			G = ( ww - 440.0 ) / ( 490.0 - 440.0 );
			B = 1.0;
		}
		else if ( ww <= 509 )
		{
			R = 0.0;
			G = 1.0;
			B = - ( ww - 510.0 ) / ( 510.0 - 490.0 );
		} 
		else if ( ww <= 579 )
		{
			R = ( ww - 510.0 ) / ( 580.0 - 510.0 );
			G = 1.0;
			B = 0.0;
		} 
		else if ( ww <= 644 )
		{
			R = 1.0;
			G = -( ww - 645.0 ) / ( 645.0 - 580.0 );
			B = 0.0;
		} 
		else if ( ww <= 780 )
		{
			R = 1.0;
			G = 0.0;
			B = 0.0;
		} 
		else
		{
			R = G = B = 0.0;
		}

	  // Let the intensity fall off near the vision limits
		double factor;
		if ( ww <= 419 )
			factor = 0.3 + 0.7 * ( ww - 380.0 ) / ( 420.0 - 380.0 );
		else if ( ww <= 700 )
			factor = 1.0;
		else if ( ww <= 780 )
			factor = 0.3 + 0.7 * ( 780.0 - ww ) / ( 780.0 - 700.0 );
		else
			factor = 0.0;

		return RGB2Int( adjust( R, factor ), adjust( G, factor ), adjust( B, factor ) );
	}
	
	/**
	 * Scientific version of the rainbow palette realization. 
	 * Is rather slow due to using method Mat.Pow(...) that means 
	 * exponential functionality calculation 
	 * 
	 * @param fraction ranges from 0.0 (WavelengthMinimum) to 1.0 (WavelengthMaximum)
	 * @return colour for the fraction as in Rainbow
	 */
	public static int rainbow( final double fraction )
	{
		
		if ( ( fraction < 0.0 ) || ( fraction > 1.0 ) )
			return 0;
		return WavelengthToRGB( WavelengthMinimum +
			      fraction * ( WavelengthMaximum - WavelengthMinimum ) );
	}
	/**
	 * it is a fast alternative but not scientifically accurate method 
	 * of a color calculation
	 * @param fraction
	 * @return color in integer form
	 */
	public static int rainbow2( final double fraction)
	{
		if ( fraction < 0.0 )
			return 0;
		if ( fraction > 1.0 )
			return 0x00FFFFFF;	// R255, G255, B255
		int wv = (int)(WavelengthMinimum + fraction * ( WavelengthMaximum - WavelengthMinimum ) );


		int R = 0, G = 0, B = 0;
		if ( wv <= 401 )
		{
			R = (int)((wv - 380.0) * (131.0 - 97.0) / (401.0 - 380.0) + 97.0);
			B = (int)((wv - 380.0) * (255.0 - 97.0) / (422.0 - 380.0) + 97.0);
		}
		else if ( wv <= 422 )
		{
			R = (int)((422.0 - wv) * (131.0 - 97.0) / (422.0 - 401.0) + 97.0);
			B = (int)((wv - 380.0) * (255.0 - 97.0) / (422.0 - 380.0) + 97.0);
		}
		else if ( wv <= 439 )
		{
			R = (int)((439.0 - wv) * (97.0 - 0.0) / (439.0 - 422.0));
			B = 255;
		}
		else if ( wv <= 489 )
		{
			G = (int)(( wv - 439.0 ) * (255.0 - 0.0) / (489.0 - 439.0));
			B = 255;
		}
		else if( wv <= 509 )
		{
			G = 255;
			B = (int)(( 509.0 - wv) * ( 255.0 - 0.0 ) / ( 509.0 - 489.0 ));
		}
		else if ( wv <= 579 )
		{
			R = (int)(( wv - 509.0 ) * ( 255.0 - 0.0 ) / ( 579.0 - 509.0 ));
			G = 255;
		}
		else if ( wv <= 644 )
		{
			R = 255;
			G = (int)(( 644.0 - wv ) * ( 255.0 - 0.0 ) / (644.0 - 579.0));
		}
		else if ( wv <= 700)
		{
			R = 255;
		}
		else
		{
			R = (int)(255.0 - (wv - 700.0) * (255.0 - 97.0) / (780.0 - 700.0 ));
		}
/*		
		int I = (int)Math.round( 1000 * fraction );
		if ( I <= 249 )
		{
			R = 255.0;
			G = 0;
			B = (int)Math.round( ( fraction * 2.0 + 0.5 ) * 255.0 );
		}
		else if ( I <= 499)
		{
			R = 0;
			G = (int)Math.round( ( fraction - 0.25 ) * 4.0 * 255.0 );
		    B = (int)Math.round( ( 0.5 - ( fraction - 0.25 ) ) * 2.0 * 255.0 );
		}
		else if ( I <= 749 )
		{
	        R = (int)Math.round( ( fraction - 0.5 ) * 4.0 * 255.0 );
	        G = 255;
	        B = (int)Math.round( ( 0.5 - ( fraction - 0.25 ) ) * 2.0 * 255.0 );
		}
		else if ( I <= 1000 )
		{
			R = -1;
        	G = (int)Math.round( ( 1.0 - ( fraction - 0.75 ) * 4.0 ) * 255.0 );
        	B = 0;
		}
		else
			R = G = B = 0;
*/		return RGB2Int(R, G, B);
	}
	
	/**
	 * calculates colour between two boundary colours ( low and high ) by the fraction
	 * from low to high. Fraction is 0 if set to color1 and is equal 1 when set to color2.
	 * Fraction 0.5 means some,e colour between color1 and color2 :o)
	 *   
	 * While a mathematical "linear interpolation" is used here, this is a
	 * non-linear interpolation in colour perception space.  Fraction is assumed
	 * to be from 0.0 to 1.0, but this is not enforced.  Returns color1 for
	 * fraction = 0.0 and color2 for fraction = 1.0.
	 * 
	 * @param fraction how new colour is different from color1. 1-fraction stands 
	 * for differences from color2.
	 * 
	 * @param color1 low boundary colour ( left on a spectral line)
	 * @param color2 high boundary colour ( right on a spectral line )
	 * @return intermediate colour between color1 and colour 2
	 */
	public static int ColorInterpolate( double fraction, int color1, int color2)
	{
		if ( fraction <= 0.0 )
			return color1;
		if ( fraction >= 1.0 )
			return color2;
		
		int R1 = color1 & 0x00FF;
		int G1 = (color1  >>> 8) & 0x00FF;
		int B1 = (color1  >>> 16) & 0x00FF;
		
		int R2 = color2 & 0x00FF;
		int G2 = (color2  >>> 8) & 0x00FF;
		int B2 = (color2  >>> 16) & 0x00FF;
		
		double complement = 1.0 - fraction;
		return RGB2Int( (int)Math.round( complement * R1 + fraction * R2 ),
				(int)Math.round( complement * G1 + fraction * G2 ),
				(int)Math.round( complement * B1 + fraction * B2 ) );
	}
}
