package su.utils.tab;

import ru.ts.common.misc.Text;
import ru.ts.common.arrays.DirectPointArray;
import ru.ts.common.arrays.DirectIntArray;
import ru.ts.utils.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Locale;

/**
 * <pre>
 * Created by IntelliJ IDEA.
 * User: Syg
 * Date: 02.07.2012
 * Time: 12:35:33
 * MapInfo TAB reader for RASTER object types <b>ONLY</b>. Example text of TAB file is as follow:
 * <b>
 * <p/>
 * !table
 * !version 300
 * !charset WindowsCyrillic
 * <p/>
 * Definition Table
 *   File "proba.bmp"
 *   Type "RASTER"
 *   (99.0616302754092,56.0421971276744) (0,0) Label "Point 1",
 *   (99.1259358300973,56.0421971276744) (800,0) Label "Point 2",
 *   (99.1259358300973,55.9995015002965) (800,948) Label "Point 3",
 *   (99.0616302754092,55.9995015002965) (0,948) Label "Point 4"
 * CoordSys Earth Projection 1,104
 * Units "degree"
 * RasterStyle 4 1
 * RasterStyle 7 0
 * RasterStyle 8 255
 * </b>
 * <p/>
 * Use as follow:
 * <p/>
 * <p/>
 * try
 * {
 *   TabReader trd = new TabReader( path );
 *   trd.isTABInGeoprojection();
 *   System.out.println( trd.toString() );
 *   ...
 * }
 * catch ( IOException e )
 * {
 *   System.err.println( "Bad TAB file" );
 * }
 * </pre>
 */
public class TabReader
{
	private final String m_path;
	private final String m_cp;
	private String m_imgName;
	private String m_projection;
	private String m_units;

	private static final int STATE_AT_START = 0;
	private static final int STATE_DEF_TABLE = 1;
	private static final int STATE_AFTER_DEF = 2;
	private DirectIntArray m_refArrayPix;
	private DirectPointArray m_refArrayDeg;

	public static final String UNITS_DEGREE = "degree";
	public static final String UNITS_METERS = "m";
	public static final String UNITS = "Units";
	public static final String COORD_SYS = "CoordSys";
	public static final String TYPE = "Type";
	public static final String EARTH_PROJECTION = "Earth Projection";

	/**
	 * Main constructor
	 *
	 * @param path String with a TAB file path to open and read
	 * @param cp   String with a code page ("Windows-1251" for example)
	 * @throws java.io.IOException if some eroros, inluding not known TAB type (not RASTER).
	 */
	public TabReader( String path, String cp ) throws IOException
	{
		m_path = path;
		m_cp = cp;
		parseAll();
	}

	/**
	 * Default constructor with system default code page. For Russian Windows of any version it is "Windows-1251"
	 *
	 * @param path String with a TAB file path in default code page to open and read
	 */
	public TabReader( String path ) throws IOException, IllegalArgumentException
	{
		this( path, Text.defaultCharSet() );
	}

	public String getCodePage()
	{
		return m_cp;
	}

	public String getTabPath()
	{
		return m_path;
	}

	public String getImagePath()
	{
		// prepare path for image, using image name and tab path
		return Files.changeFileExt(m_path, Files.getExtension(m_imgName));
	}

	private void parseAll() throws IOException, IllegalArgumentException
	{
		// read TAB line by line and find all the info we needed
		final BufferedReader rd = Files.getBufferedReader( m_path, m_cp,0);
		String line;
		int state = STATE_AT_START;
		for ( int cnt = 1; ( line = rd.readLine() ) != null; cnt++ )
		{
			line = line.trim();
			if ( Text.isEmpty( line ) )
				continue;// comment line detected
			if ( line.startsWith( "!" ) )
			{
				// some of the first lines
				if ( line.startsWith( "charset", 1 ) )
				{
					// todo check charset value
				}
				else if ( line.startsWith( "version", 1 ) )
				{
					// todo check version
				}
				else if ( !line.equals( "!table" ) )
				{
					// todo some unknown tags
				}
				continue;
			}
			if ( Text.orderOfWords( line, new String[] { "Definition", "Table" } ) )
			{
				state = STATE_DEF_TABLE;
				continue;
			}
			switch ( state )
			{
				case STATE_AT_START:
				default:
					break;
				case STATE_DEF_TABLE:
					if ( Text.startsWithNoCase(line, "File") )
					{
						// get image file name
						m_imgName = Text.findWord(line, 4, "\"", "\"");
						break;
					}
					if ( line.startsWith( "(" ) )// parse reference point data
					{
						parseNextPoint( line );
						break;
					}
					if ( Text.startsWithNoCase(line, TYPE) )
					{
						// TAB type parsing
						if ( !Text.orderOfWords(line, new String[]{TYPE, "\"RASTER\""}) )
							throw new IllegalArgumentException( "Expected Type not \"RASTER\". Line #" + cnt );
						break;
					}
					state = STATE_AFTER_DEF;
					// we are out of Definition Table
				case STATE_AFTER_DEF:
					// todo parse any lines after Definition Table
					if ( Text.startsWithNoCase(line, COORD_SYS) )
					{
						// Coordinate system parsing
						int pos = Text.skipWord( line, 0 );
						int wPtr = Text.skipSpaces( line, pos );
						if ( wPtr < line.length() )
						{
							m_projection = line.substring( wPtr );
						}
						break;
					}

					if ( Text.startsWithNoCase(line, UNITS) )
					{
						m_units = Text.findWord(line, UNITS.length(), "\"", "\"");
						break;
					}

					break;
			}
		}
		// check units value
		if ( m_units == null )
		{
			// try to get units from projection string
			m_units = Text.findWord( m_projection, 0, "\"", "\"" );
		}
	}

	/**
	 * Add one more reference Point to the array
	 *
	 * @param line String with text of point defintion to parse
	 */
	private void parseNextPoint( String line )
	{
		if ( m_refArrayPix == null )
		{
			m_refArrayPix = new DirectIntArray( 8 );
			m_refArrayDeg = new DirectPointArray( 4 );
		}
		String[] items = Text.splitItems(line, "()", false);// 3 pairs should be returned
		if ( items.length != 3 )
		{
			throw new IllegalArgumentException(
				MessageFormat.format( "Expected ref. point definition line don't contain 3 parts: \"{0}\"", line ) );
		}

		double[] degPoint = Text.splitDoubles( items[ 0 ], ',', true );// 2 numbers expected
		if ( degPoint.length != 2 )
		{
			throw new IllegalArgumentException(
				MessageFormat.format( "Expected 2 degree coordinates per ref. point not found: \"{0}\"", line ) );
		}
		m_refArrayDeg.append( degPoint[ 0 ], degPoint[ 1 ] );

		int[] imgPoint = Text.splitInts( items[ 1 ], ',', true );// 2 numbers expected
		if ( imgPoint.length != 2 )
		{
			throw new IllegalArgumentException( MessageFormat.format( "Expected 2 pixel positions per ref. point not found: \"{0}\"", line ) );
		}
		m_refArrayPix.add( imgPoint[ 0 ] );
		m_refArrayPix.add( imgPoint[ 1 ] );
	}

	/**
	 * Number of reference points
	 *
	 * @return int with number of reference points found in the Definition Ta��� of the TAB file
	 */
	public int pntCount()
	{
		if ( m_refArrayDeg == null )
		{
			return 0;
		}
		return m_refArrayDeg.size();
	}

	private boolean checkPointIndex( int index )
	{
		return ( index >= 0 ) && ( index < pntCount());
	}

	public Point2D getProjectedPoint( int index )
	{
		if ( checkPointIndex( index ) )
		{
			return new Point2D.Double( m_refArrayDeg.getX( index ), m_refArrayDeg.getY( index ) );
		}
		return null;
	}

	public Point getImagePoint( int index )
	{
		if ( checkPointIndex( index ) )
		{
			index = index * 2;
			return new Point( m_refArrayPix.get( index ), m_refArrayPix.get( index + 1 ) );
		}
		return null;
	}

	public Point getMinPixXY()
	{
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		for ( int i = 0, pos = 0; i < m_refArrayPix.size() / 2; i++, pos += 2 )
		{
			if ( minx > m_refArrayPix.get( pos ) )
			{
				minx = m_refArrayPix.get( pos );
			}
			if ( miny > m_refArrayPix.get( pos + 1 ) )
			{
				miny = m_refArrayPix.get( pos + 1 );
			}
		}
		return new Point( minx, miny );
	}

	public Point getMaxPixXY()
	{
		int maxx = -Integer.MAX_VALUE;
		int maxy = -Integer.MAX_VALUE;
		for ( int i = 0, pos = 0; i < m_refArrayPix.size() / 2; i++, pos += 2 )
		{
			if ( maxx < m_refArrayPix.get( pos ) )
			{
				maxx = m_refArrayPix.get( pos );
			}
			if ( maxy < m_refArrayPix.get( pos + 1 ) )
			{
				maxy = m_refArrayPix.get( pos + 1 );
			}
		}
		return new Point( maxx, maxy );
	}

	public Point2D.Double getMinDegXY()
	{
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		for ( int i = 0; i < m_refArrayDeg.size(); i++ )
		{
			if ( minx > m_refArrayDeg.getX( i ) )
			{
				minx = m_refArrayDeg.getX( i );
			}
			if ( miny > m_refArrayDeg.getY( i ) )
			{
				miny = m_refArrayDeg.getY( i );
			}
		}
		return new Point2D.Double( minx, miny );
	}

	public Point2D.Double getMaxDegXY()
	{
		double maxx = -Double.MAX_VALUE;
		double maxy = -Double.MAX_VALUE;
		for ( int i = 0; i < m_refArrayDeg.size(); i++ )
		{
			if ( maxx < m_refArrayDeg.getX( i ) )
			{
				maxx = m_refArrayDeg.getX( i );
			}
			if ( maxy < m_refArrayDeg.getY( i ) )
			{
				maxy = m_refArrayDeg.getY( i );
			}
		}
		return new Point2D.Double( maxx, maxy );
	}

	/**
	 * Image name designated in the TAB Definition Table
	 *
	 * @return String with a raster fiel name only, no other path elements
	 */
	public String getImgName()
	{
		return m_imgName;
	}

	public String getProjection()
	{
		return m_projection;
	}

	public boolean projectionIsGeographical()
	{
		String proj = getProjection();
		if ( Text.isEmpty( proj ) )
			return false;
		return Text.startsWithNoCase( proj, EARTH_PROJECTION );
	}

	public String getUnits()
	{
		return m_units;
	}

	public boolean unitsAreDegree()
	{
		final String units = getUnits();
		if ( Text.isEmpty( units ) )
			return false;
		return units.equalsIgnoreCase( UNITS_DEGREE );
	}

	public boolean unitsAreMeters()
	{
		final String units = getUnits();
		if ( Text.isEmpty( units ) )
			return false;
		return units.toLowerCase().startsWith( UNITS_METERS );
	}

	/**
	 * Returns {@code true} if this raster TAB has coordinates in degrees and is in some of world geographical system with
	 * some ellipsoid (Krassosky, WGS84 etc), else return {@code false}
	 *
	 * @return
	 */
	public boolean isTABInGeoprojection()
	{
		return projectionIsGeographical() && unitsAreDegree();
	}

	public String toString()
	{
		return String.format( "Image name \"%s\", point number %d, proj \"%s\", units \"%s\"",
			this.getImgName(), this.pntCount(), this.getProjection(), this.getUnits() );
	}

	/**
	 * Returns TAB file path for corrresponding image, designated in parameter 'imagePath'
	 *
	 * @param imagePath path to image with TAB to check  existance
	 * @return {@link String} instance with path to existing TAB, or {@code null} if TAB for designated image not exists
	 */
	public static String TABPath4Image( String imagePath )
	{
		final String TABPath = Files.changeFileExt( imagePath, ".tab" );
		return Files.fileExists( TABPath ) ? TABPath : null;
	}

	public static void main( String[] args ) throws IOException
	{
		if ( args.length == 0 )
		{
			Text.sout( "Enter paths to TAB files to test functionality of " + TabReader.class.getName() );
			return;
		}

		for ( int i = 0; i < args.length; i++ )
		{
			String path = args[ i ];
			Text.sout("+++ Start TAB file " + new File( path ).getName() + " reading" );
			TabReader trd = null;
			try
			{
				// check reading from file
				//String[] tabs = Text.loadFromFile( path, "Cp1251" );
				trd = new TabReader( path );
				Text.sout( "\n+++++++++++++\nTAB file read from \"" + trd.getTabPath() + "\"" );
				Text.sout( "TAB info:" + trd.toString() );
				if ( Files.fileExists( trd.getImagePath() ) )
				{
					Text.sout( "File with designated raster image exists. Not checked to be raster really" );
				}
				else
				{
					Text.serr( "Failure to detect image file" );
				}
				if ( !trd.isTABInGeoprojection() )
				{
					Text.serr( "TAB can't be used for translations" );
				}
				// check for reverse affines coefficients
				Text.sout( "Test reverse affine coefficients receiving" );
				final int equationCnt = trd.pntCount();
				double[][] matA_x = new double[equationCnt][3];
				double[][] matA_y = new double[equationCnt][3];
				double[] vecB_x = new double[equationCnt];
				double[] vecB_y = new double[equationCnt];
				double[] vecX_x = new double[3];
				double[] vecX_y = new double[3];
				for ( int m = 0; m < equationCnt; m++ )
				{
					final Point2D projPnt = trd.getProjectedPoint( m );
					final Point imgPnt = trd.getImagePoint( m );
					matA_x[ m ][ 0 ] = matA_y[ m ][ 0 ] = imgPnt.x;
					matA_x[ m ][ 1 ] = matA_y[ m ][ 1 ] = imgPnt.y;
					matA_x[ m ][ 2 ] = matA_y[ m ][ 2 ] = 1.0d;
					vecB_x[ m ] = projPnt.getX();
					vecB_y[ m ] = projPnt.getY();
					//Text.sout( String.format( "TAB: img point %s, prj point %s ", imgPnt.toString(), projPnt.toString()  ) );
				}
				printMat( matA_x, "Matrix A(x)");
				printMat( vecB_x, "Vector B(x)");
				printMat( matA_y, "Matrix A(y)");
				printMat( vecB_y, "Vector B(y)");

				TabSolverJAMA ts = new TabSolverJAMA();
				double[] atX = ts.solve( matA_x, vecB_x, vecX_x );
				double[] atY = ts.solve( matA_y, vecB_y, vecX_y );
				if ( atX == null || atY == null)
					Text.serr("Can't solve 1st or 2nd matrix: singularity found");
				else
					Text.sout( String.format( Locale.ENGLISH, "AT:\nX= %14.10f * x + %14.10f * y + %14.10f\nY= %14.10f * x + %14.10f * y + %14.10f", atX[ 0 ], atX[ 1 ], atX[ 2 ], atY[ 0], atY[ 1 ], atY[ 2 ] ) );
			}
			catch ( Exception e )
			{
				e.printStackTrace(  );
				System.err.println( "Bad TAB file: "  + e.getMessage() );
			}
		}
	}

	public static void printMat( double[][] mat, String title )
	{
		StringBuffer sb = new StringBuffer( );
		for(int i = 0; i < mat.length; i++ )
		{
			for(int j = 0; j < mat[0].length; j++ )
			{
				sb.append( String.format( Locale.ENGLISH,"%14.10f ",mat[i][j] ));
			}
			sb.append("\n");
		}
		Text.sout( title+" :\n" + sb.toString());
	}

	public static void printMat( double[] vec, String title )
	{
		StringBuffer sb = new StringBuffer( );
		for(int i = 0; i < vec.length; i++ )
		{
			sb.append( String.format(Locale.ENGLISH, "%14.10f ",vec[i] ));
		}
		Text.sout( title+" :\n" + sb.toString() + "\n");
	}


}
