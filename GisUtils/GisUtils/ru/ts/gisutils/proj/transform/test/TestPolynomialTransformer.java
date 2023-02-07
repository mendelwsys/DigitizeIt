/**
 * Created on 06.06.2008 15:44:49 2008 by Syg for project in
 * 'ru.ts.gisutils.pcntxt.transform' of 'test'
 */
package ru.ts.gisutils.proj.transform.test;

import java.util.ArrayList;

import com.mwlib.utils.Enc;
import ru.ts.gisutils.common.CSV.CSVFileReader;
import ru.ts.gisutils.common.CSV.CSVFileWriter;
import ru.ts.gisutils.proj.transform.IPolyTransformer;
import ru.ts.gisutils.proj.transform.PolynomialTransformer;

/**
 * @author Syg
 */
public class TestPolynomialTransformer
{

	/**
	 * converts points in radians to points in degrees
	 * 
	 * @param CSVRads
	 *            path to points in radians
	 * @param CSVRads
	 *            path for resulting file
	 */
	public static void convertRadian2Degree( String CSVRads, String CSVDegs )
	{
		CSVFileReader reader;
		CSVFileWriter writer;
	}

	/**
	 * @param args
	 */
	public static void main( String[] args )
	{
		try
		{
			String CSV;
			char delim;
			if ( true )
			{
				CSV = "msk2.txt";
				delim = '|';
			}
			else
			{
				CSV = Enc.get("$33");
				delim = ',';
			}
			IPolyTransformer trm = PolynomialTransformer
			        .getPolynomialTransformerFromCSV( CSV, delim );
			double[] arr = new double[20];
			System.out.println( "Polynomials are calculated" );
			System.out.println( "\nDirect one is:" );
			trm.getPolynomial( arr, true );
			print_pol( arr );
			System.out.println( "\nInverse one is:" );
			trm.getPolynomial( arr, false );
			print_pol( arr );
			ArrayList list = new ArrayList(); 
			// get point from a CSV
			int cnt = PolynomialTransformer.readBpFromCSV( list, CSV, delim );
			arr = new double[ cnt * 4 ];
			for ( int i = 0, j = 0; i < cnt; i++ )
			{
				double[] darr = (double[]) list.get( i );
				arr[ j++ ] = darr[ 0 ];
				arr[ j++ ] = darr[ 1 ];
				arr[ j++ ] = darr[ 2 ];
				arr[ j++ ] = darr[ 3 ];
			}

			/* find base points by polynomial itself */
			if ( true )
			{
				System.out.println( "+++ POLYNOM USAGE TEST +++" );
				System.out.println( "+++ Print inverse differences +++" );
				double[] tst = new double[ cnt * 2 ]; // test buffer
				double[] res = new double[ cnt * 2 ]; // result buffer
				for ( int i = 0, j = 0; i < cnt; i++ ) // copy pixels
				{
					tst[ j++ ] = arr[ i * 4 ];
					tst[ j++ ] = arr[ i * 4 + 1 ];
				}
				// transform from pixels to radians
				System.out.println( "transform from 1st set to a 2nd one" );
				trm.TransformInverse( tst, 0, res, 0, cnt );
				for ( int i = 0; i < cnt; i++ )
				{
					double x = arr[ i * 4 + 2 ]; // get original
					// radians
					double y = arr[ i * 4 + 3 ];
					double dx = res[ i * 2 ] - x; // get
					// difference
					double dy = res[ i * 2 + 1 ] - y;
					double dist = Math.sqrt( dx * dx + dy * dy );
					System.out.println( "Point " + i + ". X=" + x + ", Y=" + y
					        + ", Dist = " + dist );
				}

				System.out.println( "+++ Print direct differences +++" );
				for ( int i = 0, j = 0; i < cnt; i++ ) // copy radians
				{
					tst[ j++ ] = arr[ i * 4 + 2 ];
					tst[ j++ ] = arr[ i * 4 + 3 ];
				}
				// transform from pixels to radians
				System.out.println( "transform from 2nd set to a 1st one" );
				trm.TransformDirect( tst, 0, res, 0, cnt );
				for ( int i = 0; i < cnt; i++ )
				{
					double x = arr[ i * 4 ]; // get original pixel values
					double y = arr[ i * 4 + 1 ];
					double dx = res[ i * 2 ] - x; // compare with generated
													// ones
					double dy = res[ i * 2 + 1 ] - y;
					double dist = Math.sqrt( dx * dx + dy * dy );
					System.out.println( "Point " + i + ". X=" + x + ", Y=" + y
					        + ", Dist = " + dist );
				}

				System.out.println( "--- END OF POLYNOM USAGE TEST ---" );

			}
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	private static String str( String item, double value )
	{
		return ( value >= 0.0 ? "+" : "" )
		        + value
		        + ( ( ( item == null ) || ( item.length() == 0 ) ) ? "" : "*"
		                + item );
	}

	private static void print_pol( double[] arr )
	{
		System.out.println( "X=" + str( "", arr[ 0 ] ) + str( "x", arr[ 1 ] )
		        + str( "y", arr[ 2 ] ) + str( "x*x", arr[ 3 ] )
		        + str( "x*y", arr[ 4 ] ) + str( "y*y", arr[ 5 ] )
		        + str( "x*x*y", arr[ 6 ] ) + str( "y*y*x", arr[ 7 ] )
		        + str( "x*x*x", arr[ 8 ] ) + str( "y*y*y", arr[ 9 ] ) );
		System.out.println( "Y=" + str( "", arr[ 10 ] ) + str( "x", arr[ 11 ] )
		        + str( "y", arr[ 12 ] ) + str( "x*x", arr[ 13 ] )
		        + str( "x*y", arr[ 14 ] ) + str( "y*y", arr[ 15 ] )
		        + str( "x*x*y", arr[ 16 ] ) + str( "y*y*x", arr[ 17 ] )
		        + str( "x*x*x", arr[ 18 ] ) + str( "y*y*y", arr[ 19 ] ) );
	}
}
