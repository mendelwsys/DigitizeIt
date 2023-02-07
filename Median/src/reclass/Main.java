package reclass;

import reclass.cmd.ICmdArgs;
import reclass.bitmatrix.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;

/**
 * <pre>
 * Created by IntelliJ IDEA.
 * User: sigolaev_va
 * Date: 17.03.2014
 * Time: 15:03:14
 * Original package: reclass
 * *
 * To change this template use File | Settings | File Templates.
 * *
 * <pre>
 */
public class Main
{

	public static void main( String[] args ) throws IOException
	{
		if ( args.length == 0 )
		{
			System.out.println( "Usage:" );
			System.out.println( ">medianfilter -i=<input_raster_path> -o=<output_path> -s=N -c=0x00FF0000 -r=0x00FF0000 -d=<0..255> -b=<0..3>" );
			System.out.println( "\twhere -b value may be: 0 for BORDER_ZERO (default), 1 for BORDER_COPY, 2 for BORDER_WRAP, 2 for BORDER_REFLECT" );
			return;
		}
		ICmdArgs cmd = new ICmdArgs.Impl( args );
		String inPath;
		File file;
		if ( cmd.contains( 'i' ) )// input file
		{
			inPath = cmd.value( 'i' );
			file = new File( inPath );
			if ( !file.isFile() )
			{
				System.err.println( String.format( "Expected input raster \"%s\" not exist", inPath ) );
				return;
			}
		}
		else
		{
			System.err.println( "Expected -i <input_raster_path> key not found" );
			return;
		}
		System.out.println( "Input raster path \"" + inPath + "\"" );
		int mSize = 3;
		if ( cmd.contains( 's' ) )
		{
			try
			{
				mSize = Integer.parseInt( cmd.value( 's' ) );
				if ( (mSize & 1) == 0 )
				{
					System.err.println( "Expected median size ("+mSize+") MUST be odd and >= 3" );
					return;
				}
				System.out.println( "Median filter apperture set to value " + mSize );
			}
			catch ( NumberFormatException e )
			{
				System.err.println( String.format( "Expected key -s contains illegal value '%s'", cmd.value( 's' ) ) );
				return;
			}
		}
		else
		{
			System.out.println( String.format( "Expected -s key not found, default value %d be used instead", mSize ) );
		}

		Color selColor = Color.RED;
		char[] keyColor = { 'c', 'ñ' };
		if ( cmd.contains( keyColor ) )
		{
			String val = cmd.value( keyColor );
			if ( val.startsWith( "0x" ) )
			{
				val = val.substring( 2 );
			}
			int rgb = Integer.parseInt( val, 16 );
			selColor = new Color( rgb );
			System.out.println( String.format( "selectedColor set to 0x%08X", rgb ) );
		}
		else
		{
			System.out.println( String.format( "Expected key -c <input_color_in_hex> not found, default value 0x%X be used instead", selColor.getRGB() ) );
		}

		Color outColor = Color.RED;
		if ( cmd.contains( 'r' ) )
		{
			String val = cmd.value( 'r' );
			if ( val.startsWith( "0x" ) )
			{
				val = val.substring( 2 );
			}
			int rgb = Integer.parseInt( val, 16 );
			outColor = new Color( rgb );
			System.out.println( String.format( "outputColor set to 0x%08X", rgb ) );
		}
		else
		{
			System.out.println( String.format( "Expected key -r <output_color_in_hex> not found, default value 0x%X be used instead", outColor.getRGB() ) );
		}

		double colorDist = 25.0d;
		if ( cmd.hasKey( 'd' ) )
		{
			final String val = cmd.value( 'd' );
			try
			{
				colorDist = Double.parseDouble( val );
				if ( colorDist < 0 )
					colorDist = 0.0d;
				else if (colorDist > 255.0d )
					colorDist = 255.0d;
				System.out.println( "Color distance set to " + colorDist );
			}
			catch ( NumberFormatException e )
			{
				System.err.println( String.format( Locale.ENGLISH,  "Expected key -d is illegal (%s)", val ) );
				return;
			}
		}
		else
		{
			System.out.println( String.format( "Expected key -d <color_dist> not detected, default value %f be used", colorDist ) );
		}
		int borderType = BitMatrix.BORDER_ZERO;
		if ( cmd.contains( 'b' ) )
		{
			String value = cmd.value( 'b' );
			if ( Character.isDigit( value.charAt( 0 ) ) )
			{
				// user put int value
				try
				{
					borderType = Integer.parseInt( value );
					if ( ( borderType < BitMatrix.BORDER_MIN_VALUE ) || ( borderType > BitMatrix.BORDER_MAX_VALUE ) )
					{
						System.err.println( String.format( "Border type (%d) must be integer in range (%d..%d)", borderType, BitMatrix.BORDER_MIN_VALUE, BitMatrix.BORDER_MAX_VALUE ) );
						return;
					}
					String name = "BORDER_UNKNOWN";
					switch( borderType )
					{
						case BitMatrix.BORDER_ZERO:
							name = "BORDER_ZERO";
							break;
						case BitMatrix.BORDER_REFLECT:
							name = "BORDER_REFLECT";
							break;
						case BitMatrix.BORDER_WRAP:
							name = "BORDER_WRAP";
							break;
						case BitMatrix.BORDER_COPY:
							name = "BORDER_COPY";
							break;
					}
					System.out.println( "Boder type is " + name );
				}
				catch ( NumberFormatException e )
				{
					System.err.println( String.format( "Expected kay -b contains illegal value \"%s\"", value ) );
					return;
				}
			}
		}
		else
		{
			System.out.println( "Expected key -b <BORDER_TYPE> not detected, default value BORDER_ZERO be used" );
		}

		String outPath;
		char[] outKeys = { 'o', 'î' };
		if ( cmd.contains( outKeys ) )
		{
			outPath = cmd.value( outKeys );
			System.out.println( String.format( "Output path \"%s\" designated", outPath ) );
		}
		else
		{
			FileSystem fs = FileSystems.getDefault();
			Path fp = fs.getPath( inPath );
			String fileName = fp.getFileName().toString();
			String ext = "";
			String name = "";
			int pos = fileName.lastIndexOf( '.' );
			if ( pos >= 0 )
			{
				ext = fileName.substring( pos );
				name = fileName.substring( 0, pos );
			}
			String fileDir = fp.getParent().toString();
			fp = fs.getPath( fileDir, String.format( "%s_mflt_%dx%d%s", name, mSize, mSize, ext ) );
			outPath = fp.toString();
			System.out.println( String.format( "Expected key -o not detected, input dir/name used instead: \"%s\"", outPath ) );
		}

		BufferedImage inBi = ImageIO.read( file );
		System.out.println( String.format("Image width %d, height %d", inBi.getWidth(), inBi.getHeight()) );
		BufferedImage outBi;
		try
		{
			Date start = new Date();
			System.out.println( String.format( "Start at %tT", start  ) );
			outBi = processMedian( inBi, mSize, selColor, colorDist, outColor, borderType, null );
			Date end = new Date();
			long secs = (end.getTime() - start.getTime()) / 1000L;
			System.out.println( "+++ Processed in " + secs + " secs");

		}
		catch ( Exception e )
		{
			System.err.println( "Median filtering error: " + e.getMessage() );
			return;
		}
		if ( outBi == null )
		{
			System.err.println( "Failured to apply median filter, sorry o(" );
			return;
		}

		System.out.println( "Resulting image created, try to save it tp PNG format" );
		try
		{
			ImageIO.write( outBi, "png", new File( outPath ) );
			System.out.println( "+++ Successfully wrote result as \"" + outPath + "\"");
		}
		catch ( IOException e )
		{
			System.err.println( "Faillure to write output file:" + e.getMessage() );
		}
	}

	/**
	 * Processes full image with median filter of designated size
	 *
	 * @return {@link BufferedImage}
	 */
	private static BufferedImage processMedian( BufferedImage bi, int mSize, Color selColor, double colorDist, Color outColor, int borderType, IProgressObserver prog )
	{
		if ( bi == null )
		{
			throw new NullPointerException( "Expected BufferedImage == null" );
		}

		if ( mSize < 3 || ( ( mSize & 1 ) == 0 ) )
		{
			throw new IllegalArgumentException( "Expected median apperture is < 3 or even (must be odd)" );
		}
		bi = processMedianFilter( bi, mSize, selColor, colorDist, outColor, borderType, prog );
		return bi;
	}


	/**
	 * Apply median filter onto designated {@link BufferedImage}
	 *
	 * @param src
	 * @param medianSize
	 * @return newly created 1-bit {@link BufferedImage} with bits set in1 (ones) as result and 0 (xero) as filtered out
	 */
	public static BufferedImage processMedianFilter( BufferedImage src, int medianSize, Color selColor, double colorDist, Color outColor, int borderType, IProgressObserver prog )
	{
		MedianFilter mflt = new MedianFilter( medianSize );
		return mflt.filterSelectedColors( src, selColor, colorDist, outColor, borderType/*IConsts.BORDER_ZERO*/, prog );
	}

}
