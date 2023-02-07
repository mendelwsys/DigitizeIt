/*
 * 10-JUL-2007, Sygsky
 * 
 * �������� ������ ��� ������ � MapTransform � IMapTransformer. MapTransform -
 * ����������� ����� � �������� ��� ���������� ������ �����������������,
 * IMapTransformer - ��������� ��� ���������������� ������������� ������ � �����
 * ������. �������� � ��������� double, ��� ���������� ����������� � �������
 * x1,y1,x2,y2... xn,yn. ������ ��������� ��� ����������.
 * 
 */

package ru.ts.gisutils.proj.transform.test;

// J2SE and JAI dependencies
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.geotools.referencing.CRS;
import org.geotools.referencing.NamedIdentifier;
import org.geotools.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotools.referencing.datum.BursaWolfParameters;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.geotools.referencing.datum.DefaultGeodeticDatum;
import org.geotools.referencing.datum.DefaultPrimeMeridian;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

import ru.ts.utils.Files;
import ru.ts.gisutils.common.Geography;
import ru.ts.gisutils.common.TextFileReader;
import ru.ts.gisutils.common.TimeSpan;
import ru.ts.gisutils.common.logger.BaseLogger;
import ru.ts.gisutils.proj.transform.IMapTransformer;
import ru.ts.gisutils.proj.transform.MapTransform;
import ru.ts.gisutils.proj.transform.MapTransformer;
import ru.ts.gisutils.proj.transform.WKTHandler;

/**
 * An example of application reading points from the standard input,
 * transforming them and writing the result to the standard output. This class
 * can be run from the command-line using the following syntax:
 * 
 * <blockquote>
 * 
 * <pre>
 *   java TransformationConsole
 * </pre>
 * 
 * </blockquote>
 * 
 * Where [classification] is the the classification name of the projection to
 * perform. The default value is "Mercator_1SP". The list of supported
 * classification name is available here:
 * 
 * http://docs.codehaus.org/display/GEOTOOLS/Coordinate+Transformation+Parameters
 * 
 * To exit from the application, enter "exit".
 * 
 * @author Sygsky
 */
public class TestProjTransformer
{
	/**
	 * The program main entry point.
	 * 
	 * @param args
	 *            Array of command-line arguments. This small demo accept only
	 *            one argument: the classification name of the projection to
	 *            perform.
	 * 
	 * @throws IOException
	 *             if an error occured while reading the input stream.
	 * @throws FactoryException
	 *             if a coordinate system can't be constructed.
	 * @throws TransformException
	 *             if a transform failed.
	 */
	public static void main( String[] args ) throws IOException,
	        FactoryException, TransformException
	{
		java.util.Date start = new java.util.Date();
		String test = String.format( "%s\n", new Object[] { start
		        .toLocaleString() } );
		BaseLogger.Console.logLine( test );

		/*
		 * Check command-line arguments.
		 */
		String projection_name;
		switch ( args.length )
		{
		case 0:
			projection_name = "Mercator_1SP";
			break;
		case 1:
			projection_name = args[ 0 ];
			break;
		default:
			System.err.println( "Expected 0 or 1 argument" );
			return;
		}

		if ( true )
		{
			test = String.format( "%s\n",
			        new Object[] { "Lambert_Azimuthal_Equal_Area" } );
			BaseLogger.Console.logLine( test );
			SimplestExample( projection_name );
			return;
		}
		if ( false )
		{
			test = String.format( "Test projection: %s'\n",
			        new Object[] { "Lambert_Azimuthal_Equal_Area" } );
			BaseLogger.Console.logLine( test );
			SimplestExample( projection_name );
			return;
		}
		if ( false )
		{
			convertGEN2Projection(
			        "E:\\Temp\\Gen\\World Export files\\lat long verts.POL",
			        WKTHandler.getLambertAsimuthal_WKT( 60, 100 ) );
			return;
		}

		BaseLogger.Console.logf( "Projection:%s\n",
		        new Object[] { projection_name } );

		/*
		 * Construct the source CoordinateReferenceSystem. We will use a
		 * geographic coordinate system, i.e. one that use (latitude,longitude)
		 * coordinates. Latitude values are increasing north and longitude
		 * values area increasing east. Angular units are degrees and prime
		 * meridian is Greenwich. Datum is Krassowsky (a commonly used one for
		 * the USSR since 1942 in cartography and geodesy).
		 */

		if ( true )
		{
			DefaultCoordinateSystemAxis sa1 = DefaultCoordinateSystemAxis.EASTING;
			HashMap map = new HashMap();
			map.put( "code", "NO_CODE" );
			map.put( "name", "NO_NAME" );
			NamedIdentifier ni = new NamedIdentifier( map );
			String code = ni.getCode();
			ni = (NamedIdentifier) ( (CoordinateSystemAxis) sa1 ).getName();
			String tmp = ni.getCode();
			DefaultCoordinateSystemAxis sa2 = DefaultCoordinateSystemAxis.NORTHING;
		}
		DefaultEllipsoid krassowsky = MapTransform.createKrassowskyEllipsoid();
		DefaultGeodeticDatum datum = MapTransform.createGeodeticDatum(
		        Collections.singletonMap( "name", "Krassowsky" ), krassowsky,
		        DefaultPrimeMeridian.GREENWICH );
		GeographicCRS sourceCRS = MapTransform.createGeographicCRS(
		        "Geographics", datum );

		/*
		 * Construct the target CoordinateReferenceSystem. I will use a
		 * projected coordinate system, i.e. one that use linear (in meters)
		 * coordinates. I will use the same ellipsoid than the source geographic
		 * coordinate system (i.e. Krassowsky). Default parameters will be used
		 * for this projection.
		 */

		ParameterValueGroup parameters = MapTransform
		        .getDefaultParameters( projection_name );

		/*
		 * You can change any parameters in this group as follow:
		 * 
		 */
		parameters.parameter( "central_meridian" ).setValue( 40.0 );
		parameters.parameter( "latitude_of_origin" ).setValue( 50.0 );

		/*
		 * Output parameters of transformation
		 */

		java.util.Iterator it = parameters.values().iterator();
		BaseLogger.Console.logLine( "test projected CRS" );
		while ( it.hasNext() )
			BaseLogger.Console.log( it.next().toString() );

		final ProjectedCRS targetCRS = MapTransform.createProjectedCRS(
		        "Projected CRS", parameters, krassowsky, datum );

		// now print target projection information

		GeographicBoundingBox gbb = CRS.getGeographicBoundingBox( targetCRS );
		if ( gbb != null )
		{
			double dbl = gbb.getEastBoundLongitude();
			String str = ru.ts.gisutils.common.Geography
			        .DecimalDegreeToString( dbl );
			BaseLogger.Console.logLine( "East Bounding Longitude = " + str );

			dbl = gbb.getWestBoundLongitude();
			str = ru.ts.gisutils.common.Geography.DecimalDegreeToString( dbl );
			BaseLogger.Console.logLine( "West Bounding Longitude = " + str );

			dbl = gbb.getNorthBoundLatitude();
			str = ru.ts.gisutils.common.Geography.DecimalDegreeToString( dbl );
			BaseLogger.Console.logLine( "North  Bound   Latitude = " + str );

			dbl = gbb.getSouthBoundLatitude();
			str = Geography.DecimalDegreeToString( dbl );
			BaseLogger.Console.logLine( "South  Bound   Latitude = " + str );
		}

		/*
		 * check for rectangle2d handling
		 */
		int c_mer = 100, lat_orig = 60, sp_1 = 50, sp_2 = 70;
		String str = WKTHandler.getLambert2SP_WKT( c_mer, lat_orig, sp_1, sp_2 );
		BaseLogger.Console.logLine( "Create Lambert with central mer. " + c_mer
		        + ", lat. orig. " + lat_orig + ", std. parall. " + sp_1
		        + ", std. parall. " + sp_2 );

		CoordinateReferenceSystem crs = MapTransformer.getCRSFromWKT( str );
		/*
		 * Rectangle2D r2d = new Rectangle2D.Double(); double x = 1000000.0, y =
		 * x, w = 2000000.0, h = 1500000.0; r2d.setFrame(1000000.0, 1000000.0,
		 * 2000000.0, 1000000.0); BaseLogger.Console.logLine( "Create
		 * Rectangle2D with x " + x + ", y " + y + ", w " + w + ", h " + h );
		 * 
		 * Envelope2D e2d = new Envelope2D(targetCRS, r2d); double maxx =
		 * e2d.getMaxX(); ConsoleHndl.logLine("Max. X ", maxx); double maxy =
		 * e2d.getMaxY(); ConsoleHndl.logLine("Max. Y ", maxy); double minx =
		 * e2d.getMinX(); ConsoleHndl.logLine("Min. X ", minx); double miny =
		 * e2d.getMinY(); ConsoleHndl.logLine("Min. Y ", miny);
		 * 
		 */

		final String dir = Files.getCurrentDir();

		String sourceWKT = "GEOGCS[\"Geographic\","
		        + "DATUM[\"�����������Krassowsky\","
		        + "SPHEROID[\"Krassowsky_1942\", 6378245.0, 298.3]],"
		        + "PRIMEM[\"Greenwich\", 0.0],"
		        + "UNIT[\"degree\", 0.017453292519943295],"
		        + "AXIS[\"Geodetic latitude\", NORTH],"
		        + "AXIS[\"Geodetic longitude\", EAST]];";

		if ( sourceWKT == null )
		{
			final String inname = "InWKT.txt";
			BaseLogger.Console
			        .logLine( "������� �������������� �������� � ���� \""
			                + inname + "\"" );
			sourceWKT = sourceCRS.toWKT();
			String fname = dir + Files.FileSeparator + inname;
			java.io.FileWriter writer = new java.io.FileWriter( fname );
			writer.write( sourceWKT );
			writer.close();
		}

		String targetWKT = "PROJCS[\"�������� � ����� ����������� ����������\","
		        + "GEOGCS[\"���������\","
		        + "DATUM[\"�����������\","
		        + "SPHEROID[\"Krassowsky_1942\", 6378245.0, 298.3]],"
		        + "PRIMEM[\"Greenwich\", 0.0],"
		        + "UNIT[\"degree\", 0.017453292519943295],"
		        + "AXIS[\"Geodetic latitude\", NORTH],"
		        + "AXIS[\"Geodetic longitude\", EAST]],"
		        + "PROJECTION[\"Mercator_1SP\"],"
		        + "PARAMETER[\"latitude_of_origin\", 50.0],"
		        + "PARAMETER[\"central_meridian\", 40.0],"
		        + "PARAMETER[\"scale_factor\", 1.0],"
		        + "PARAMETER[\"false_easting\", 0.0],"
		        + "PARAMETER[\"false_northing\", 0.0],"
		        + "UNIT[\"m\", 1.0],"
		        + "AXIS[\"x\", EAST]," + "AXIS[\"y\", NORTH]]";

		if ( targetWKT == null )
		{
			final String outname = "OutWKT.txt";
			BaseLogger.Console.logLine( "������� �������� �������� � ���� \""
			        + outname + "\"" );
			targetWKT = targetCRS.toWKT();
			final String fname = dir + Files.FileSeparator + outname;
			final java.io.FileWriter writer = new java.io.FileWriter( fname );
			writer.write( targetWKT );
			writer.close();
		}

		/*
		 * Some output for parameters. May be empty if no real changes between
		 * ellipsoids occur
		 */

		final BursaWolfParameters[] bwparams = datum.getBursaWolfParameters();
		BaseLogger.Console.logf( "BursaWolf parameters of size %d\n",
		        new Object[] { new Integer( bwparams.length ) } );
		for ( int i = 0; i < bwparams.length; i++ )
			BaseLogger.Console.logLine( bwparams[ i ].toWKT() );

		/*
		 * MAIN call to create transformation object. It is the only thing
		 * needed to transform to and back.
		 */
		IMapTransformer trm = null;
		if ( ( sourceWKT != null ) && ( targetWKT != null ) )
		{
			BaseLogger.Console.logLine( "������ �� �������" );
			trm = MapTransform.createMapTransformer( sourceWKT, targetWKT );
		}
		else
		{
			BaseLogger.Console.logLine( "������ �� ��������" );
			trm = MapTransform.createMapTransformer( sourceCRS, targetCRS );
		}

		/*
		 * Now, read lines from the standard input, transform them, and write
		 * the result to the standard output.
		 */

		BaseLogger.Console.log( "��� ��������      " + projection_name );
		BaseLogger.Console.logLine( "Source CRS is:    " + sourceCRS.toWKT() );
		BaseLogger.Console.logLine( "Target CRS is:    " + targetCRS.toWKT() );
		final BufferedReader in = new BufferedReader( new InputStreamReader(
		        System.in ) );

		String line;
		while ( true )
		{
			BaseLogger.Console
			        .logLine( "������� ������ � ������� ��������� ��������." );
			BaseLogger.Console.log( "������� \"exit\" ��� ���������� ������:" );

			if ( ( line = in.readLine() ) == null )
				break;
			line = line.trim();
			if ( line.equalsIgnoreCase( "exit" ) )
				break;

			int split = line.indexOf( ' ' );
			if ( split >= 0 )
			{
				double latitude = Double
				        .parseDouble( line.substring( 0, split ) );
				double longitude = Double.parseDouble( line.substring( split ) );
				double[] coords = new double[] { latitude, longitude };
				double[] backs = new double[2];
				double[] res = new double[2];
				// transform from degrees to meters
				trm.TransformDirect( coords, 0, res, 0, 1 );
				// and transform back to the latitude/longitude
				trm.TransformInverse( res, 0, backs, 0, 1 );
				BaseLogger.Console.logf(
				        "X=%f, Y=%f -> X1=%f, Y1=%f -> X=%f, Y=%f\n",
				        new Object[] { new Double( latitude ),
				                new Double( longitude ),
				                new Double( res[ 0 ] ), new Double( res[ 1 ] ),
				                new Double( backs[ 0 ] ),
				                new Double( backs[ 1 ] ) } );
			}
		}
		TimeSpan ts = new TimeSpan( new Date(), start );
		BaseLogger.Console.logLine( "������ ���������, ������������ "
		        + ts.duration().toString() );
	}

	/**
	 * ���������� ������ �������� �������������� �� ��������� �������� ������� �
	 * �������� ��������
	 */
	static void SimplestExample( String method ) throws IOException,
	        FactoryException, TransformException
	{
		String sourceWKT = WKTHandler.getGeographicWKT();
		String targetWKT = WKTHandler.getMercator1SP_WKT( 40, 50 );
		String targetLambertConical = WKTHandler.getLambert2SP_WKT( 100, 50, 40, 60 );
		String targetLambertAzimuthal = WKTHandler.getLambertAsimuthal_WKT( 60, 100 );
		String targetAlbersEqualArea = WKTHandler.getAlbersEqualArea_WKT( 100, 50, 40, 60 );


		//DefaultCartesianCS generic = new DefaultCartesianCS( "Cartesian 2D",
		//        DefaultCoordinateSystemAxis.X, DefaultCoordinateSystemAxis.Y );
		String str = WKTHandler.getLambert2SP_WKT( 36, 60, 53, 67 );
		sourceWKT = WKTHandler.getGeographicWKT();
		IMapTransformer trm = null;
		System.out.println( str );
		trm = MapTransform.createMapTransformer( sourceWKT, str );
		final BufferedReader in = new BufferedReader( new InputStreamReader(
		        System.in ) );
		String line;
		while ( true )
		{
			String tmp = "Enter Lat Long separated by space:";
			System.out.print( tmp );

			if ( ( line = in.readLine() ) == null )
				break;
			line = line.trim();
			if ( line.equalsIgnoreCase( "exit" ) )
				break;

			int split = line.indexOf( ' ' );
			if ( split >= 0 )
			{
				double latitude = Double
				        .parseDouble( line.substring( 0, split ) );
				double longitude = Double.parseDouble( line.substring( split ).trim() );
				double[] coords = new double[] { latitude, longitude };
				double[] backs = new double[2];
				double[] res = new double[2];
				try
				{
					trm.TransformDirect( coords, 0, res, 0, 1 );
				}
				catch ( Exception ex )
				{
					ex.printStackTrace();
				}
				// and transform back to the latitude/longitude
				trm.TransformInverse( res, 0, backs, 0, 1 );
				BaseLogger.Console.logf(
				        "Lat=%f, Lon=%f -> X1=%f, Y1=%f -> Lat=%f, Lon=%f\n",
				        new Object[] { new Double( latitude ),
				                new Double( longitude ),
				                new Double( res[ 0 ] ), new Double( res[ 1 ] ),
				                new Double( backs[ 0 ] ),
				                new Double( backs[ 1 ] ) } );
			}
		}

	}

	/**
	 * opens GEN, read it, convert into a projection and write to other file
	 * with ext 'GEN0'
	 * 
	 * @param GENPath -
	 *            path to the original GEN file containing coordinates in
	 *            geographical coordinates system in degree form. GEN shoud
	 *            contain LINEs or POLYGONs, not POINT
	 * @param WKTProjection
	 */
	static void convertGEN2Projection( String GENPath, String WKTProjection )
	{
		IMapTransformer trm = null;
		try
		{
			trm = MapTransform.createMapTransformer( WKTHandler
			        .getGeographicWKT(), WKTProjection );
		}
		catch ( Exception ex )
		{
			System.err.println( "--- Error creation MapTransform ---" );
			ex.printStackTrace();
			return;
		}

		File gen = new File( GENPath );
		if ( !gen.exists() )
		{
			System.err.println( "\"" + GENPath + "\" - no such file" );
			return;
		}

		TextFileReader reader = null;
		try
		{
			reader = new TextFileReader( gen );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		String dir = Files.getDirectory( GENPath );
		String name = Files.getNameNoExt( GENPath );
		String ext = Files.getExtension( GENPath );
		String outpath = dir + name + ext + '0';
		File wfile = new File( outpath );

		BufferedWriter writer;
		try
		{
			writer = new BufferedWriter( new FileWriter( outpath, false ) );
			System.out.println( "Output file \"" + outpath + "\"  generated" );
		}
		catch ( Exception ex )
		{
			System.err.println( "\"" + outpath + "\" output file open error" );
			ex.printStackTrace();
			return;
		}

		double x = 0.0, y = 0.0;
		try
		{
			String line;
			boolean obj_hdr = true;
			while ( true )
			{
				try
				{
					line = reader.nextLine();
					if ( reader.EOF() )
						break;

					if ( obj_hdr ) // simply copy it
					{
						writer.write( line + "\r\n" );
						obj_hdr = false;
						continue;
					}
					if ( line.equals( "END" ) )
					{
						writer.write( line + "\r\n" );
						obj_hdr = true;
						continue;
					}
					/* now read coordinates */
					String[] coords = line.split( ", " );
					double lonLat[] = new double[2];
					int index = 0;
					for ( int i = 0; i < coords.length; i++ )
					{
						String val = coords[ i ].trim();
						if ( val.length() == 0 ) // empty string skipping
							continue;
						try
						{
							if ( index == 0 )
							{
								// longitude
								x = Double.valueOf( val ).doubleValue();
								index++;
								if ( x <= -180.0 )
									x = -180.0 + 1E-3;
								else if ( x >= 180.0 )
									x = 180.0 - 1E-3;
								lonLat[ 1 ] = x;
							}
							else
							{
								// latitude
								y = Double.valueOf( val ).doubleValue();
								if ( y <= -90.0 )
									y = -90.0 + 1E-3;
								else if ( y >= 90.0 )
									y = 90.0 - 1E-3;
								lonLat[ 0 ] = y;
								break;
							}
						}
						catch ( Exception ex )
						{
							ex.printStackTrace();
							System.err.println( "err data, line "
							        + ( reader.readLineCount() + 1 )
							        + ", text \"" + line + "\"" );
						}
					}
					try
					{
						trm.TransformDirect( lonLat, 0, 1 );
					}
					catch ( Exception ex )
					{
						System.err.println( "Can't transform x=" + x + ", y="
						        + y );
						lonLat[ 0 ] = lonLat[ 1 ] = 0.0;
					}
					writer.write( Double.toString( lonLat[ 0 ] ) + ", "
					        + Double.toString( lonLat[ 1 ] ) + "\r\n" );
				}
				catch ( Exception ex )
				{
					System.err.println( "Error read line "
					        + ( reader.readLineCount() + 1 ) );
					ex.printStackTrace();
					return;
				}
			}
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch ( Exception ex )
			{
			}
		}
		System.out.println( "Convertation completed, output file closed!" );

	}

}