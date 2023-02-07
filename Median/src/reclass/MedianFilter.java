package reclass;

//Problem Set 3 : Problem 2

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import reclass.bitmatrix.BitMatrix;

public class MedianFilter
{
	private int m_radius;// size of the square filter side without central pixel
	private int m_size;// size of the square filter
	private static final double SQRT_3 = Math.sqrt( 3.0d );

	/**
	 * Size of median filter side. Must be odd (e.g. 3,5,7,9 ...) and >= 3
	 *
	 * @param size designated median filter side size. <b>Important note:</b> If &lt; 3, is set to 3. If even also set to 3
	 */
	public MedianFilter( int size )
	{
		if ( ( size % 2 == 0 ) || ( size < 3 ) )
		{//check if the filter m_size is an odd number > = 3
			System.err.println( size + "is not a valid filter size." );
			System.err.println( "Filter size is now set to 3" );
			size = 3;
		}
		m_size = size;
		m_radius = size / 2;
	}

	public int getFilterSize()
	{
		return m_size;
	}

	//sort the array, and return the median
	public int median( int[] a )
	{
		int temp;
		int asize = a.length;
		//sort the array in increasing order
		for ( int i = 0; i < asize; i++ )
		{
			for ( int j = i + 1; j < asize; j++ )
			{
				if ( a[ i ] > a[ j ] )
				{
					temp = a[ i ];
					a[ i ] = a[ j ];
					a[ j ] = temp;
				}
			}
		}
		//if it's odd
		if ( asize % 2 == 1 )
		{
			return a[ asize / 2 ];
		}
		else
		{
			return ( ( a[ asize / 2 ] + a[ asize / 2 - 1 ] ) / 2 );
		}
	}

	private int[] getArray( BufferedImage image, int x, int y, int[] n )
	{
		//store the pixel values of position(x, y) and its neighbors
		int h = image.getHeight();
		int w = image.getWidth();
		int xmin, xmax, ymin, ymax;//the limits of the part of the image on which the filter operate on
		xmin = x - m_radius;
		xmax = x + m_radius;
		ymin = y - m_radius;
		ymax = y + m_radius;

		//special edge cases
		if ( xmin < 0 )
		{
			xmin = 0;
		}
		if ( xmax > ( w - 1 ) )
		{
			xmax = w - 1;
		}
		if ( ymin < 0 )
		{
			ymin = 0;
		}
		if ( ymax > ( h - 1 ) )
		{
			ymax = h - 1;
		}
		//the actual number of pixels to be considered
		int nsize = ( xmax - xmin + 1 ) * ( ymax - ymin + 1 );
		if ( n == null || n.length < nsize )
		{
			n = new int[nsize];
		}
		int k = 0;
		for ( int i = xmin; i <= xmax; i++ )
		{
			for ( int j = ymin; j <= ymax; j++ )
			{
				n[ k ] = image.getRGB( i, j );//get pixel value
				k++;
			}
		}
		return n;
	}

	/**
	 * Filter in raw mediana mode (very-very slow but universal for any image type)
	 *
	 * @param srcImage
	 * @param dstImage
	 */
	public void filter( BufferedImage srcImage, BufferedImage dstImage )
	{
		int height = srcImage.getHeight();
		int width = srcImage.getWidth();

		int[] a = null;//the array that gets the pixel value at (x, y) and its neightbors

		for ( int k = 0; k < height; k++ )
		{
			for ( int j = 0; j < width; j++ )
			{
				a = getArray( srcImage, j, k, a );
				int[] red, green, blue;
				red = new int[a.length];
				green = new int[a.length];
				blue = new int[a.length];
				//get the red,green,blue value from the pixel
				for ( int i = 0; i < a.length; i++ )
				{
					Color Pixel = new Color( a[ 0 ] );
					red[ i ] = a[ i ] >> 16 & 0x000000FF;
					green[ i ] = a[ i ] >> 8 & 0x000000FF;
					blue[ i ] = a[ i ] & 0x000000FF;
				}
				//find the median for each color
				int R = median( red );
				int G = median( green );
				int B = median( blue );
				//set the new pixel value using the median just found
				int spixel = ( R << 16 ) | ( G << 8 ) | B;// todo: check correctness of operation
				dstImage.setRGB( j, k, spixel );
			}
		}
	}

	/**
	 * Filter image by converting it to 1-bit type and filtering this as source
	 *
	 * @param src        source {@link java.awt.image.BufferedImage} instance
	 * @param srcCol     color to use as value
	 * @param dist       color distance to select near colors too
	 * @param dstCol     destination raster value color
	 * @param borderType type of border (see {@link BitMatrix#BORDER_ZERO} etc)
	 * @return newly created {@link java.awt.image.BufferedImage} created with filtered image of source image
	 */
	public BufferedImage filterSelectedColors( BufferedImage src, Color srcCol, double dist, Color dstCol, int borderType )
	{
		return filterSelectedColors( src, srcCol, dist, dstCol, borderType, null );
	}

	/**
	 * Filters designated image by converting it to 1-bit type and process with median filter as source one
	 *
	 * @param src        source {@link java.awt.image.BufferedImage} instance
	 * @param srcCol     color to use as value
	 * @param dist       color distance to select near colors too
	 * @param dstCol     destination raster value color
	 * @param borderType type of border (see {@link BitMatrix#BORDER_ZERO} etc)
	 * @param prog       {@link IProgressObserver} instance to use. May be {@code null}
	 * @return newly created {@link java.awt.image.BufferedImage} created with filtered image of source image
	 */
	public BufferedImage filterSelectedColors( BufferedImage src, Color srcCol, double dist, Color dstCol, int borderType, IProgressObserver prog )
	{
		final int subSize = m_size / 2;
		int middle = m_size * m_size / 2;

		if ( prog != null )
		{
			prog.setProgress( 0.0f );
		}

		Date start = new Date();
		final BitMatrix bm = makeBitMatrixFromImage( src, srcCol, dist, null, prog );
		Date end = new Date();
		long secs = (end.getTime() - start.getTime()) / 1000L;
		System.out.println( "+++ Color separation done in " + secs + " secs");

		int cardinalityOrig = bm.cardinality();
		if ( cardinalityOrig == 0)
		{
			System.err.println( "Input matrix is empty! May be selected color is wrong or color diatance too low" );
			return null;
		}
		final BitMatrix bme = BitMatrix.expandBitMatrix( bm, subSize, subSize, borderType );

		// now we have expanded bitmatrix . Filter it with median of designated size
		// do it now directly in not optimal way (todo - make it optimally in future)
		int card, filterCard = 0;
		//BitMatrix medianBM = new BitMatrix( m_size, m_size );
		final int i1 = bme.columns() - subSize;
		bm.clear();
		int colCards[] = new int[m_size];// filter matrix each column cardinalities
		BitMatrix rowBM = new BitMatrix( 1, m_size );
		final int bmRows = bm.rows();
		if ( prog != null )
		{
			prog.setProgress( 0.0f );
		}

		for ( int y = 0; y < bmRows; y++ )
		{
			filterCard = 0;
			// calc initial filter matrix column cardinalities for each scan along X axis
			int x;
			for ( x = 0; x < m_size; x++ )
			{
				rowBM.replaceBoxWith( 0, 0, 1, m_size, bme, x, y );
				filterCard += ( colCards[ x ] = rowBM.cardinality() );
			}
			bm.putQuick( 0, y, filterCard > middle );

			for ( ; x < bme.columns(); x++ )
			{
				rowBM.replaceBoxWith( 0, 0, 1, m_size, bme, x, y );
				card = rowBM.cardinality();
				int newColPos = x % m_size;
				filterCard -= colCards[ newColPos ];
				filterCard += ( colCards[ newColPos ] = card );
				bm.putQuick( x - m_size + 1, y, filterCard > middle );
			}
			if ( prog != null )
			{
				prog.setProgress( (float) ( y + 1 ) / (float) bmRows );
			}
		}
		int cardinalityFinal = bm.cardinality();
		System.out.println( String.format("Cardinality: for input is %d, for output is %d, difference %d", cardinalityOrig, cardinalityFinal, cardinalityOrig - cardinalityFinal) );
		BufferedImage ret = makeImageFromBitMatrix( bm, dstCol, Color.WHITE );
		return ret;
	}
	/**
	 * Returns 1-bit {@link BufferedImage} with copy of designated {@link BitMatrix}. Bits set to 1 are colored as valueColor (default RED),
	 * other colored as emptyColor (default WHITE)
	 *
	 * @param bm         {@link BitMatrix} instance to convert to image.
	 * @param valueColor how to color bit set to 1
	 * @param emptyColor how to color bit set to 0
	 * @return resulted {@link BufferedImage} instance. Or null if input matrix is null too
	 */
	public static BufferedImage makeImageFromBitMatrix( BitMatrix bm, Color valueColor, Color emptyColor )
	{
		if ( bm == null )
		{
			return null;
		}
		int w = bm.columns();
		int h = bm.rows();
		MultiPixelPackedSampleModel sm = new MultiPixelPackedSampleModel( DataBuffer.TYPE_BYTE, w, h, 1 );
		/*
			Color valueColor = Color.RED;
			Color emptyColor = Color.WHITE;
	*/
		byte[] r = { (byte) emptyColor.getRed(), (byte) valueColor.getRed() };
		byte[] g = { (byte) emptyColor.getRed(), (byte) valueColor.getGreen() };
		byte[] b = { (byte) emptyColor.getRed(), (byte) valueColor.getBlue() };
		ColorModel cm = new IndexColorModel( 1, 2, r, g, b, 0 );

		Raster raster = Raster.createWritableRaster( sm, new Point() );
		//Text.sout("Image created with size = " + getMemorySize( raster ));
		byte[] bytes = ( (DataBufferByte) raster.getDataBuffer() ).getData();
		int lineStride = ( w + 7 ) / 8;
		BufferedImage bi2 = new BufferedImage( cm, (WritableRaster) raster, false, null );
		//		int valueRGB = valueColor.getRGB();
		//		int cardinality = 0;
		for ( int y = 0, ypos = 0; y < h; y++, ypos += lineStride )
		{
			for ( int x = 0; x < w; x++ )
			{
				int pos = ypos + x / 8;
				if ( bm.get( x, y ) )
				{
					int off = 0x80 >>> ( x % 8 );
					bytes[ pos ] |= off;
					//					cardinality++;
				}
			}
		}
		//Text.sout(String.format("DEBUG:makeImageFromBitMatrix  w %d, h %d, cardinality %d, lineStride %d", w, h, cardinality, lineStride ));
		return bi2;
	}

	/**
	 * Selects designated color and returns 1-bit matrix {@link BitMatrix} instance,
	 * zero bit means non-data value and one valued bit means data value
	 * and be colored in image with user designated color
	 *
	 * @param bi        {@link java.awt.image.BufferedImage} instance to select data color from it
	 * @param dataColor {@link java.awt.Color} instance to find in image
	 * @param dist      color range to select from designated dataColor. If dist == 0.0, only namely designated dataColor
	 *                  will be selected. See {@link #colorDistance(java.awt.Color, java.awt.Color)} description
	 *                  to understand about color space distance
	 * @param bm        {@link BitMatrix} instance to accept result. Is cleared before usage.
	 *                  If width and height of matrix not coincide with input BufferedImage dimensions or matrix is {@code null},
	 *                  new BitMatrix is returned, original one remain unchanged
	 * @return {@link BitMatrix} instance. May be new one if designated input matrix is {@code null} or not correspond
	 *         to designated image size. If designated matrix is allright, itself is returned
	 */
	public static BitMatrix makeBitMatrixFromImage( BufferedImage bi, Color dataColor, double dist, BitMatrix bm, IProgressObserver prog )
	{
		int w = bi.getWidth();
		int h = bi.getHeight();
		final float fH = (float) h;
		if ( bm == null || bm.columns() != w || bm.rows() != h )
		{
			bm = new BitMatrix( w, h );
		}
		else
		{
			bm.clear();
		}
		for ( int y = 0; y < h; y++ )
		{
			for ( int x = 0; x < w; x++ )
			{
				int pixCol = bi.getRGB( x, y );
				double colDist = colorDistance( new Color( pixCol ), dataColor );
				if ( colDist <= dist )
				{
					bm.putQuick( x, y, true );
				}
			}
			if ( prog != null )
			{
				prog.setProgress( ( (float) ( y + 1 ) ) / fH );
			}
		}
		return bm;
	}

	/**
	 * Calculates distance in RGB color space with Euklidian distance between each component
	 *
	 * @param c1 {@link Color} one
	 * @param c2 {@link Color} two
	 * @return double result of comparison. Max possible distance is 225.0. Minimum 0.0
	 */
	public static double colorDistance( Color c1, Color c2 )
	{
		double rDist = c1.getRed() - c2.getRed();
		double gDist = c1.getGreen() - c2.getGreen();
		double bDist = c1.getBlue() - c2.getBlue();
		double distance = Math.sqrt( rDist * rDist + gDist * gDist + bDist * bDist ) / SQRT_3;
		return distance;
	}//end color distance method

}