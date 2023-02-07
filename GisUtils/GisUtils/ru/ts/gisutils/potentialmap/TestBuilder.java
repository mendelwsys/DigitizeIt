/**
 * Created on 13.02.2008 10:49:58 2008 by Syg for project in
 * 'ru.ts.gisutils.potentialmap' of 'test'
 */
package ru.ts.gisutils.potentialmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import com.mwlib.utils.Enc;
import ru.ts.utils.Files;
import ru.ts.gisutils.common.SpectralLibrary;
import ru.ts.gisutils.common.TimeSpan;
import ru.ts.gisutils.algs.common.IMCoordinate;
import ru.ts.gisutils.algs.common.MPointZM;

/**
 * @author Syg
 */
public class TestBuilder
{

	private static  Date _dt;
	/**
	 * @param args
	 */
	public static void main( String[] args )
	{
		_dt = new Date();
		System.out.println( Enc.get("$31") );

		try
		{
			String str = Files.getCurrentDir();

			str = Files.appendFileSeparator( str );
			String imgname = str + "MAP of DVD size.BMP";
			if ( args.length > 0 )
				imgname = args[ 0 ];

			File file = new File( imgname );
			if ( !file.exists() )
			{
				System.out.println( "File not found :\"" + imgname + "\"" );
				return;
			}
			BufferedImage mapimg = ImageIO.read( file );
			System.out.println( "Image file opened :\"" + imgname + "\"" );
			String dir = Files.appendFileSeparator( Files
			        .getDirectory( imgname ) );

			if ( false )
			{
				buildSpectrum( ColorScheme.palRainbow, dir );
				buildSpectrum( ColorScheme.palRainbow2, dir );
				buildSpectrum( ColorScheme.palCustom, dir );
				return;
			}

			Builder mapper = new Builder();

			int IW = mapimg.getWidth();
			int IH = mapimg.getHeight();
			System.out.println( "Width = " + IW + ", Height = " + IH );
			System.out.println( "Central pixel value = 0x"
			        + Integer.toHexString( mapimg.getRGB( IW / 2, IH / 2 ) ) );
			Rectangle2D worldrect = new Rectangle2D.Double( 0, 0, IW * 2,
			        IH * 2 );
			GeomProfile profile = GeomProfile.getGaussProfile( 300.0 );

			Random rnd = new Random();

			int cnt = 8 + rnd.nextInt( 32 );
			IMCoordinate[] arr = new IMCoordinate[cnt];
			System.out.println( "Point number generated = " + cnt );
			for ( int i = 0; i < cnt; i++ )
			{
				double x = rnd.nextDouble() * IW * 2;
				double y = rnd.nextDouble() * IH * 2;
				arr[ i ] = new MPointZM( x, y,0, 1.0 + (float) Math
				        .abs( rnd.nextGaussian() ) );
			}
			if ( true )
			{
				buildImageSequence( arr, mapimg, "E:\\Temp\\seq\\", 300, 3 );
				return;
			}

			float mergelevel = 0.05F;
			float maxpotential = 0.0F;
			float transparency  = 0.3F;
			
			ColorScheme colors = new ColorScheme( ColorScheme.palRainbow2, maxpotential );
				colors.set_mergeLevel( mergelevel );
			System.out.println( "Palette \"" + colors.getPaletteName() + "\"" );

			_dt = new Date();
			/* create potential map */
			BufferedImage output = mapper.makeImage( worldrect, mapimg,
			        profile, arr, colors, transparency, DoubleMatrix.PM_SUM );
			/* write it to the HDD */
			save2img( output, dir, colors.getPaletteName() + ".bmp" );

			if ( true )
			{
				colors = new ColorScheme( ColorScheme.palRainbow, maxpotential );
					colors.set_mergeLevel( mergelevel );
				output = mapper.remakeImage( mapimg, colors, transparency );
				save2img( output, dir, colors.getPaletteName() + ".bmp" );

				colors = new ColorScheme( ColorScheme.palBlueRed, maxpotential );
					colors.set_mergeLevel( mergelevel );
				output = mapper.remakeImage( mapimg, colors, transparency );
				save2img( output, dir, colors.getPaletteName() + ".bmp" );

				colors = new ColorScheme( ColorScheme.palRedBlue, maxpotential );
					colors.set_mergeLevel( mergelevel );
				output = mapper.remakeImage( mapimg, colors, transparency );
				save2img( output, dir, colors.getPaletteName() + ".bmp" );
				
				colors = new ColorScheme( ColorScheme.palGreen, maxpotential );
					colors.set_mergeLevel( mergelevel );
				output = mapper.remakeImage( mapimg, colors, transparency );
				save2img( output, dir, colors.getPaletteName() + ".bmp" );

				colors = new ColorScheme( ColorScheme.palGrey, maxpotential );
					colors.set_mergeLevel( mergelevel );
				output = mapper.remakeImage( mapimg, colors, transparency );
				save2img( output, dir, colors.getPaletteName() + ".bmp" );

				colors = new ColorScheme( ColorScheme.palRed, maxpotential );
					colors.set_mergeLevel( mergelevel );
				/* change legend position */
				Legend leg = mapper.getLegend();
				leg.set_right( -50 );
				output = mapper.remakeImage( mapimg, colors, transparency );
				save2img( output, dir, colors.getPaletteName() + ".bmp" );
			}
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
		}

		System.out.println( Enc.get("$32") );
	}

	private static void save2img( BufferedImage bi, String dir, String fname )
	{
		String ext = Files.getExtension( fname ).toLowerCase().substring( 1 );
		File file = new File( dir, fname );
		try
		{
			ImageIO.write( bi, ext, file );
			System.out.println( "Image created: " + fname + ", done in " + (new TimeSpan(_dt)).toString() );
		}
		catch ( Exception ex )
		{
			System.out.println( "Image \"" + fname + "\" creation error" );
		}
		_dt = new Date();
	}

	/**
	 * creates image with spectral line of preselected palette
	 * @param paletteType type of palette to use for this image
	 * @param dir directory to build the image in it
	 */
	private static void buildSpectrum( int paletteType, String dir )
	{
		System.out.println( "Build image with spectrum \""
		        + ColorScheme.getStdPaletteName( paletteType ) + "\"" );

		try
		{
			if ( !ColorScheme.isLegalPalette( paletteType ) )
				System.err.println( "palette not legal" );

			int min = SpectralLibrary.WavelengthMinimum;
			int max = SpectralLibrary.WavelengthMaximum;
			int len = max - min + 1;

			/*
			 * Create output image
			 */
			BufferedImage img = new BufferedImage( len, 256,
			        BufferedImage.TYPE_INT_RGB );
			int ymin = 0;
			int ymax = img.getHeight();

			/*
			 * fill image with WHITE
			 */
			Graphics gr = img.getGraphics();
			gr.setColor( Color.WHITE );
			gr.fillRect( 0, 0, img.getWidth(), img.getHeight() );
			
			
			ColorScheme colors = new ColorScheme( paletteType );
			double step = 1.0 / len;

			double fraction = (double) min / len;
			int color = colors.legendColor( fraction );
			int prevR = SpectralLibrary.getR( color ), prevG = SpectralLibrary
			        .getG( color ), prevB = SpectralLibrary.getB( color );
			for ( int i = min + 1; i <= max; i++ )
			{
				fraction = (double) ( i - min ) / len;
				color = colors.legendColor( fraction );
				int B = SpectralLibrary.getB( color );
				if ( prevB >= 0 )
				{
					gr.setColor( Color.BLUE );
					gr.drawLine( i - min - 1, 255 - prevB, i - min, 255 - B );
				}
				prevB = B;

				int G = SpectralLibrary.getG( color );
				if ( prevG >= 0 )
				{
					gr.setColor( Color.GREEN );
					gr.drawLine( i - min - 1, 255 - prevG, i - min, 255 - G );
				}
				prevG = G;

				int R = SpectralLibrary.getR( color );
				if ( prevR >= 0 )
				{
					gr.setColor( Color.RED );
					gr.drawLine( i - min - 1, 255 - prevR, i - min, 255 - R );
				}
				prevR = R;
				if ( ( i % 5 ) == 0 )
				{
					int x = i - min;
					gr.setColor( Color.BLACK );
					if ( ( i % 100 ) == 0 )
						gr.drawLine( x, ymax - 65, x, ymax );
					if ( ( i % 50 ) == 0 )
						gr.drawLine( x, ymax - 40, x, ymax );
					else if ( ( i % 25 ) == 0 )
						gr.drawLine( x, ymax - 25, x, ymax );
					else if ( ( i % 10 ) == 0 )
						gr.drawLine( x, ymax - 10, x, ymax );
					else
						gr.drawLine( x, ymax - 5, x, ymax );
				}

			}
			dir = Files.appendFileSeparator( dir );
			File output = new File( dir, "Spectrum_"
			        + ColorScheme.getStdPaletteName( paletteType ) + ".png" );
			ImageIO.write( img, "png", output );
			System.out.println( "Spectrum built" );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
			System.out.println( "Spectrum NOT built" );
		}

	}
	
	/**
	 * creates sequence of images to emulate point movements
	 * @param arr array of points to display their movement
	 * @param mapimg source image as background
	 * @param dir - directory to store sequence
	 * @param seq_length how many separate frames to create
	 * @param step average step for points movement in world coordinate system (meters for example)
	 */
	private static void buildImageSequence( IMCoordinate[] arr, BufferedImage mapimg, String dir, int seq_length, int step )
	{
		float mergelevel = 0.1F;
		float maxpotential = 0.0F;
		float transparency  = 0.3F;
		/* probability sector of a point movement direction */
		double movesector = Math.toRadians( 15.0 );
		step = 10; 
		
		ColorScheme colors = new ColorScheme( ColorScheme.palRainbow2, maxpotential );
			colors.set_mergeLevel( mergelevel );
		System.out.println( "Palette \"" + colors.getPaletteName() + "\"" );
		int IW = mapimg.getWidth();
		int IH = mapimg.getHeight();
		Rectangle2D worldrect = new Rectangle2D.Double( 0, 0, IW * 2,
		        IH * 2 );
		GeomProfile profile = GeomProfile.getGaussProfile( 200.0 );

		Random rnd = new Random();

		/* create 1st potential map */
		Builder mapper = new Builder();
		_dt = new Date();
		BufferedImage output = mapper.makeImage( worldrect, mapimg,
		        profile, arr, colors, transparency, DoubleMatrix.PM_SUM );
		/* write it to the HDD */
		save2img( output, dir, "Sequence0"  + ".jpg" );
		
		/* create roaming copies of points */
		double max = -Double.MAX_VALUE;
		double min = +Double.MAX_VALUE;
		for( int i = 0; i < arr.length; i++ )
			if ( arr[ i ].getM() > max )
				max = arr[ i ].getM();
			else if ( arr[ i ].getM() < min )
				min = arr[ i ].getM();
		ArrayList pnts;
		pnts = new ArrayList();
		for( int i = 0; i < arr.length; i++)
			pnts.add( new RoamingPotentialPoint( 
					arr[ i ], 
					movesector / 2 + rnd.nextDouble() * movesector, 
					(int)(step * (1.0 - arr[ i ].getM() / max ) + 2 )
					)
			);
		/*
		 * add more frames. Do for each sequence step
		 */
		int cnt =  arr.length;
		for(int i = 1; i < seq_length; i++ )
		{
			for ( int index = 0; index < cnt; index++ )
			{
				/* move current point */
				RoamingPotentialPoint pnt = (RoamingPotentialPoint)pnts.get( index );
				double value_orig = pnt.getM();
				if ( value_orig > max ) // this is too old point
					pnt.inverseGrow();
				else
					if ( value_orig < 0.0 )
					{
						/* remove this point and insert a new one */
						pnt.x = worldrect.getMinX() + rnd.nextDouble() * worldrect.getWidth();
						pnt.y = worldrect.getMinY() + rnd.nextDouble() * worldrect.getHeight();
						pnt.randomize( min );
					}

				double newdir = pnt.getDirection();
				double newsec = pnt.getSector();
				int newstep = pnt.getStep(); 
				
				if ( rnd.nextBoolean() ) // turn right
					newdir += rnd.nextDouble() *  newsec / 2.0;
				else
					// turn left
					newdir -= rnd.nextDouble() * newsec / 2.0;

				double pntx = pnt.x + Math.sin( newdir ) * newstep;
				double pnty = pnt.y + Math.cos( newdir ) * newstep;

				/* try to return it to the image */
				while ( !worldrect.contains( pntx, pnty ) ) 
				{
					newdir = Math.toRadians( rnd.nextInt( 360 ) );
					pntx = pnt.x + Math.sin( newdir ) * newstep;
					pnty = pnt.y + Math.cos( newdir ) * newstep;
				}

				pnt.setDirectionDegrees( Math.toDegrees( newdir ) );
				pnt.growUp();
				pnt.x = pntx;
				pnt.y = pnty;
			}
			_dt = new Date();
			
			/* build the map */
			RoamingPotentialPoint[] new_arr = new RoamingPotentialPoint[ pnts.size() ];
			for( int j = 0; j < pnts.size() ; j++ )
				new_arr[ j ] = (RoamingPotentialPoint)pnts.get( j );
			output = mapper.makeImage( worldrect, mapimg,
			        profile, new_arr, colors, transparency, DoubleMatrix.PM_SUM );
			
			/* write it to the HDD */
			save2img( output, dir, "Sequence" + i + ".jpg" );
		}
		
	}

}
