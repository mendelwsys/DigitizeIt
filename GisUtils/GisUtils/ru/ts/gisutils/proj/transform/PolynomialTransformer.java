/**
 * Created on 06.06.2008 11:02:21 2008 by Syg for project in
 * 'ru.ts.gisutils.pcntxt.transform' of 'test'
 */
package ru.ts.gisutils.proj.transform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import ru.ts.utils.Files;
import ru.ts.gisutils.common.CSV.CSVFileReader;
import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;
import ru.ts.utils.DateTime;

/**
 * @author Syg
 */
public class PolynomialTransformer implements IPolyTransformer
{

	private static String	_Geom3Name	    = "GEOM3.EXE";

	private static String	_directName	    = "DIRECT.BP";

	private static String	_inverseName	= "INVERSE.BP";

	private static String	_polNameDirect	= "DIRECT.POL";

	private static String	_polNameInverse	= "INVERSE.POL";

	private double	      _pol_direct[];

	private double	      _pol_inverse[];

	/**
	 * 
	 */
	private PolynomialTransformer( double[] direct, double[] inverse )
	{
		_pol_direct = direct;
		_pol_inverse = inverse;
	}

	/**
	 * builds polynomial around base point set
	 * 
	 * @param points
	 *            points for polynomial building, should be grouped by 4 items,
	 *            where elements are as follow: XSrc, YSrc, XDst, YDst.
	 * @param grp_cnt
	 *            groups by 4 counter, so the whole number of points should be
	 *            not less than group_cnt * 4
	 * @return ITransformer instance, able to convert custom points with X,Y
	 *         forth and back
	 * @throws IllegalArgumentException
	 *             in the case of any difficulties with point or group counters
	 *             designated
	 * @throws FileNotFoundException
	 *             when a file "Geom3.exe" wasn't found on the system
	 * @throws IOException
	 *             if an I/O exception of some sort has occurred
	 */
	public static IPolyTransformer getPolynomialTransformer( double[] points,
	        int grp_cnt ) throws IllegalArgumentException,
	        FileNotFoundException, IOException, InterruptedException
	{
		if ( points.length < ( grp_cnt * 4 ) )
			throw new IllegalArgumentException( "Points count is "
			        + points.length + ", group count is " + grp_cnt
			        + ". That is illegal" );
		/* Prepare input files for forth and back polynomial calculations */
		File file = new File( _Geom3Name );
		if ( !file.exists() )
			throw new FileNotFoundException( "\"" + _Geom3Name + "\" not found" );
		if ( !file.isFile() )
			throw new FileNotFoundException( "\"" + _Geom3Name + "\" not found" );
		String geom3Name = file.getAbsolutePath();
		String dir = Files.getDirectory( geom3Name );

		/* remove temp files */
		String directName = "direct.bp";
		// ru.ts.utils.Files.deleteFile( directName );
		String inverseName = "inverse.bp";
		// ru.ts.utils.Files.deleteFile( inverseName );
		// String polName = "result.pol";
		// ru.ts.utils.Files.deleteFile( polName );

		/* create temp files */
		createDirectFile( directName, points, grp_cnt );
		createInverseFile( inverseName, points, grp_cnt );

		double pol_dir[] = new double[20];
		if ( !createPolynomial( directName, _polNameDirect, pol_dir ) )
		{
			System.err.println( "Error create direct polynomial" );
			return null;
		}
		double pol_inv[] = new double[20];
		if ( !createPolynomial( inverseName, _polNameInverse, pol_inv ) )
		{
			System.err.println( "Error create inverse polynomial" );
			return null;
		}
		return new PolynomialTransformer( pol_dir, pol_inv );
	}

	/**
	 * create polynomial from a polynomial coefficients text file
	 * 
	 * @param pol_path
	 *            path of a file with a polynomial coefficient in predefined
	 *            text form
	 * @return ITransformer instance, able to convert custom points with X,Y
	 *         forth and back
	 * @throws IllegalArgumentException
	 *             in the case of any difficulties with point or group counters
	 *             designated
	 * @throws FileNotFoundException
	 *             when a file "Geom3.exe" wasn't found on the system
	 * @throws IOException
	 *             if an I/O exception of some sort has occurred
	 */
	public static IPolyTransformer getPolynomialTransformerFromPolFile(
	        String pol_path ) throws IllegalArgumentException,
	        FileNotFoundException, IOException, InterruptedException
	{
		/* Prepare input files for forth and back polynomial calculations */
		File file = new File( pol_path );
		if ( !file.exists() )
			throw new FileNotFoundException( "\"" + pol_path + "\" not found" );
		if ( !file.isFile() )
			throw new FileNotFoundException( "\"" + pol_path + "\" not found" );

		double pol_dir[] = new double[20];
		double pol_inv[] = new double[20];
		if ( !readPolFile( pol_path, pol_dir, 0, pol_inv, 0 ) )
		{
			System.err.println( "Error create polynomial from a file" );
			return null;
		}
		return new PolynomialTransformer( pol_dir, pol_inv );
	}

	private static void createDirectFile( String fname, double[] points,
	        int grp_cnt ) throws IOException
	{
		Writer output = new BufferedWriter( new FileWriter( fname ) );
		output
		        .write( "; Temp file for direct polynomial generation. Created by Sygsky at "
		                + DateTime.gettimestr() + "\n" );
		output.write( "; Remove as soon as posible (ASAP)" + "\n" );
		for ( int i = 0, index = 0; i < grp_cnt; i++ )
		{
			output.write( "# " + ( i + 1 ) + "\n" );
			output.write( "\t" + points[ index++ ] + "\t" + points[ index++ ]
			        + "\t" + points[ index++ ] + "\t" + points[ index++ ]
			        + "\n" );
		}
		output.close();
	}

	private static void createInverseFile( String fname, double[] points,
	        int grp_cnt ) throws IOException
	{
		Writer output = new BufferedWriter( new FileWriter( fname ) );
		output
		        .write( "; Temp file for inverse polynomial generation. Created by Sygsky at "
		                + DateTime.gettimestr() + "\n" );
		output.write( "; Remove as soon as posible (ASAP)\n" );
		for ( int i = 0; i < grp_cnt; i++ )
		{
			output.write( "# " + ( i + 1 ) + "\n" );
			output.write( "\t" + points[ i * 4 + 2 ] + "\t"
			        + points[ i * 4 + 3 ] + "\t" + points[ i * 4 ] + "\t"
			        + points[ i * 4 + 1 ] + "\n" );
		}
		output.close();
	}

	/**
	 * creates coefficients of polynomial and read them from an result file
	 * 
	 * @param bp_name
	 *            base points file name to use
	 * @param array
	 *            output array to fit coefficients
	 * @return <code>true</code> if success, else <code>false</code>
	 */
	private static boolean createPolynomial( String bp_name, String pol_name,
	        double[] array ) throws IOException, InterruptedException
	{
		/* run polynomial generator to produce coefficients */

		Runtime rt = Runtime.getRuntime();
		String[] cmdarray = new String[3];
		cmdarray[ 0 ] = _Geom3Name;
		cmdarray[ 1 ] = "-i" + bp_name;
		cmdarray[ 2 ] = "-o" + pol_name;
		Process proc = rt.exec( cmdarray );
		proc.waitFor();
		if ( proc.exitValue() != 0 )
			throw new IllegalArgumentException(
			        "Polynomial generator completed with error" );

		/* read result polynomial file */
		return readPolFile( pol_name, array, 0, array, 10 );
	}

	/**
	 * reads polynomial from a file with predefined structure
	 * 
	 * @param pol_name
	 *            file path with polynomial
	 * @param pol_dir
	 *            array with length at least 20 doubles to fit polynomial
	 *            coefficients
	 * @return <code>true</code> if success else <code>false</code> if any
	 *         error occur
	 */
	private static boolean readPolFile( String pol_name, double[] pol_dir,
	        int pos_dir, double[] pol_inv, int pos_inv ) throws IOException
	{
		/* use buffering, reading one line at a time */
		/* FileReader always assumes default encoding is OK! */
		BufferedReader input = new BufferedReader( new FileReader( pol_name ) );
		try
		{
			String line = null; // not declared within while loop
			/*
			 * readLine is a bit quirky : it returns the content of a line MINUS
			 * the newline. it returns null only for the END of the stream. it
			 * returns an empty String if two newlines appear in a row.
			 */
			/* skip 1st comment line */
			if ( ( line = input.readLine() ) == null )
			{
				System.err.println( "Polynomial file \"" + pol_name
				        + "\": EOF after 1st line read detected" );
				return false;
			}
			if ( ( line = input.readLine() ) == null )
			{
				System.err.println( "Polynomial file \"" + pol_name
				        + "\": EOF after 2nd line read detected" );
				return false;
			}
			/* process 2st line with A coefficients */
			int cnt;
			if ( ( cnt = readPC( line, pol_dir, pos_dir ) ) != 10 )
			{
				System.err.println( "Polynomial file \"" + pol_name
				        + "\": 2nd line contains only " + cnt
				        + " A coefficients" );
				return false;
			}
			/* process 3rd line with B coefficients */
			if ( ( line = input.readLine() ) == null )
			{
				System.err.println( "Polynomial file \"" + pol_name
				        + "\": EOF after 3rd line read detected" );
				return false;
			}
			if ( ( cnt = readPC( line, pol_inv, pos_inv ) ) != 10 )
			{
				System.err.println( "Polynomial file \"" + pol_name
				        + "\": 3rd line contains only " + cnt
				        + " B coefficients" );
				return false;
			}
			return true;
		}
		finally
		{
			input.close();
		}
	}

	/**
	 * parse coefficients line
	 * 
	 * @param line
	 *            text to parse
	 * @param array
	 *            double to fit
	 * @param pos
	 *            start position for fitting (offset to begin fit)
	 * @return number of coefficients parsed, always <= 10
	 */
	private static int readPC( String line, double[] array, int pos )
	{
		String[] items = line.split( "[=,]" );
		int cnt;
		for ( cnt = 0; cnt < 10; cnt++ )
			try
			{
				double dbl = Double.parseDouble( items[ cnt + 1 ] );
				array[ pos + cnt ] = dbl;
			}
			catch ( NumberFormatException nfe )
			{
				System.err.println( "Coefficient #" + ( cnt + 1 ) + "\""
				        + items[ cnt + 1 ]
				        + "\" can't be parsed as a double value" );
				break;
			}
		return cnt;
	}

	/**
	 * tries to create polynomial transformer from base point in CSV file. CSV
	 * shouldn't have header as a 1st line
	 * 
	 * @param csvPath
	 *            path to a CSV file without header line
	 * @param delimiter
	 *            delimiter for an items, usually ',' (comma) is used whence CSV
	 *            term is created itself
	 * @return ITransformer instance if success, else <code>null</code> or
	 *         some exceptions
	 * @throws FileNotFoundException,
	 *             IOException, NumberFormatException if any error occur
	 */
	public static IPolyTransformer getPolynomialTransformerFromCSV(
	        String csvPath, char delimiter ) throws FileNotFoundException,
	        IOException, NumberFormatException, InterruptedException
	{
		ArrayList list = new ArrayList();
		int cnt = readBpFromCSV(list, csvPath, delimiter);
		double[] arr = new double[cnt * 4];
		for ( int i = 0, j = 0; i < cnt; i++ )
		{
			double[] darr = (double[]) list.get( i );
			arr[ j++ ] = darr[ 0 ];
			arr[ j++ ] = darr[ 1 ];
			arr[ j++ ] = darr[ 2 ];
			arr[ j++ ] = darr[ 3 ];
		}
		IPolyTransformer trm = getPolynomialTransformer( arr, cnt );
		return trm;
	}

	/**
	 * reads CSV file with 4 coordinates in a line. CSV should not have a header
	 * line
	 * 
	 * @param list
	 *            List to fit groups by 4. Resulting List contains N Object,
	 *            where each Object is double[4] array and N is number of lines
	 *            in the CSV. List is cleared in the beginning before the method 
	 *            begins to work. 
	 * @param CSV
	 *            path to CSV
	 * @param delimiter
	 *            delimiter of CSV file
	 * @return number of lines read and processed from a CSV
	 * @throws FileNotFoundException,
	 *             IOException, NumberFormatException if any error occur
	 */
	public static int readBpFromCSV( List list, String CSV, char delimiter )
	        throws FileNotFoundException, IOException, NumberFormatException
	{
		CSVFileReader reader = new CSVFileReader( CSV );
		try
		{
			list.clear();
			reader.setDelimiter( delimiter );
			double[] arr;
			int ln = 1; // line number
			int index = 0;
			while ( reader.readRecord() )
				try
				{
					arr = new double[4];
					for ( index = 0; index < 4; index++ )
						arr[ index ] = Double.parseDouble( reader.get( index ) );
					list.add( arr );
					ln++;
				}
				catch ( NumberFormatException nfe )
				{
					System.err.println( " CSV file \"" + CSV + "\", line " + ln
					        + ", illegal item value at index " + index );
					throw nfe;

				}
			return list.size();
		}
		finally
		{
			reader.close();
		}
	}

	/**
	 * tries to understand, what the delimiter is used in the designated CSV
	 * @param CSVpath path to the text file with a CSV data
	 * @return char used as delimiter of Exception if delimiter was not detected
	 */
	public static char findDelimiter(String CSVpath)
	{
		return (char)0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.pcntxt.transform.ITransformer#TransformDirect(java.awt.geom.Point2D,
	 *      java.awt.geom.Point2D)
	 */
	public void TransformDirect( IGetXY pntSrc, IXY pntDst )
	{
		double[] arr = new double[] { pntSrc.getX(), pntSrc.getY() };
		_transform( this._pol_direct, arr, 0, arr, 0, 1 );
		pntDst.setXY( arr[ 0 ], arr[ 1 ] );
	}

	public void getPolynomial( double[] coeffs, boolean direct )
	{
		if ( coeffs == null )
			return;
		if ( direct )
			System.arraycopy( _pol_direct, 0, coeffs, 0, Math.min( 20,
			        coeffs.length ) );
		else
			System.arraycopy( _pol_inverse, 0, coeffs, 0, Math.min( 20,
			        coeffs.length ) );
	}

	/**
	 * returns size of whole polynomial for A and B at one time, so if there are
	 * 10 coefficients, size will return 20. First part is coefficients for A
	 * part, second for B
	 */
	public int size()
	{
		return 20;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.pcntxt.transform.ITransformer#TransformDirect(double[],
	 *      int, double[], int, int)
	 */
	public void TransformDirect( double[] srcPnts, int srcOff,
	        double[] dstPnts, int dstOff, int numPnt )
	{
		_transform( _pol_direct, srcPnts, srcOff, dstPnts, dstOff, numPnt );
	}

	/**
	 * internal method
	 * 
	 * @param coeffs
	 * @param srcPnts
	 * @param srcOff
	 * @param dstPnts
	 * @param dstOfft
	 * @param numPnt
	 */
	private void _transform( double[] coeffs, double[] srcPnts, int srcOff,
	        double[] dstPnts, int dstOfft, int numPnt )
	{
		for ( int i = 0, j = srcOff, k = dstOfft; i < numPnt; i++ )
		{
			double x1 = srcPnts[ j++ ], y1 = srcPnts[ j++ ];
			double x1sqr, y1sqr, x1y1, x1x1y1, y1y1x1, x1cub, y1cub;
			double x = coeffs[ 0 ] + coeffs[ 1 ] * x1 + coeffs[ 2 ] * y1
			        + coeffs[ 3 ] * ( x1sqr = x1 * x1 ) + coeffs[ 4 ]
			        * ( x1y1 = x1 * y1 ) + coeffs[ 5 ] * ( y1sqr = y1 * y1 )
			        + coeffs[ 6 ] * ( x1x1y1 = x1sqr * y1 ) + coeffs[ 7 ]
			        * ( y1y1x1 = y1sqr * x1 ) + coeffs[ 8 ]
			        * ( x1cub = x1 * x1 * x1 ) + coeffs[ 9 ]
			        * ( y1cub = y1 * y1 * y1 );
			dstPnts[ k++ ] = x;
			double y = coeffs[ 10 ] + coeffs[ 11 ] * x1 + coeffs[ 12 ] * y1
			        + coeffs[ 13 ] * x1sqr + coeffs[ 14 ] * x1y1 + coeffs[ 15 ]
			        * y1sqr + coeffs[ 16 ] * x1x1y1 + coeffs[ 17 ] * y1y1x1
			        + coeffs[ 18 ] * x1cub + coeffs[ 19 ] * y1cub;
			dstPnts[ k++ ] = y;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.pcntxt.transform.ITransformer#TransformDirect(double[],
	 *      int, int, int)
	 */
	public void TransformDirect( double[] pnts, int srcOff, int dstOff,
	        int numPnts )
	{
		_transform( _pol_direct, pnts, srcOff, pnts, dstOff, numPnts );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.pcntxt.transform.ITransformer#TransformInverse(IGetXY pntSrc,
	 * IXY pntDst)
	 */
	public void TransformInverse( IGetXY pntSrc, IXY pntDst  )
	{
		double[] arr = new double[] { pntSrc.getX(), pntSrc.getY() };
		_transform( this._pol_direct, arr, 0, arr, 0, 1 );
		pntDst.setXY( arr[ 0 ], arr[ 1 ] );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.pcntxt.transform.ITransformer#TransformInverse(double[],
	 *      int, double[], int, int)
	 */
	public void TransformInverse( double[] srcPnts, int srcOff,
	        double[] dstPnts, int dstOff, int numPnt )
	{
		_transform( _pol_inverse, srcPnts, srcOff, dstPnts, dstOff, numPnt );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.pcntxt.transform.ITransformer#TransformInverse(double[],
	 *      int, int, int)
	 */
	public void TransformInverse( double[] pnts, int srcOff, int dstOff,
	        int numPnts )
	{
		_transform( _pol_inverse, pnts, srcOff, pnts, dstOff, numPnts );
	}

}
