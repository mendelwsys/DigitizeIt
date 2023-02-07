/**
 * 
 */
package ru.ts.gisutils.proj.transform;

// import ru.ts.utils.Text;
import ru.ts.utils.Text;

/**
 * @author Syg
 * 
 * @category works for WKT handing to help creation CRS
 */
public class WKTHandler
{

	private static final String	axis	               = "AXIS[";

	private static final String	strKrassowskyElli	   = "SPHEROID[\"Krassowsky_1942\", 6378245.0, 298.3]";

	private static final String	WKT_geographic	       = "GEOGCS[\"Geographic_No_Projection\","
	                                                           + "DATUM[\"Krassowsky\","
	                                                           + "SPHEROID[\"Krassowsky_1942\", 6378245.0, 298.3]],"
	                                                           + "PRIMEM[\"Greenwich\", 0.0],"
	                                                           + "UNIT[\"degree\", 0.017453292519943295],"
	                                                           + "AXIS[\"Geodetic latitude\", NORTH],"
	                                                           + "AXIS[\"Geodetic longitude\", EAST]];";

	private static final int	PROJ_GEOGRAPHICS	   = 0;

	private static final String	WKT_Mercator_1SP	   = "PROJCS[\"Mercator_1_SP\","
	                                                           + "GEOGCS[\"Geographic\","
	                                                           + "DATUM[\"Krassowsky\","
	                                                           + "SPHEROID[\"Krassowsky_1942\", 6378245.0, 298.3]],"
	                                                           + "PRIMEM[\"Greenwich\", 0.0],"
	                                                           + "UNIT[\"degree\", 0.017453292519943295],"
	                                                           + "AXIS[\"Geodetic latitude\", NORTH],"
	                                                           + "AXIS[\"Geodetic longitude\", EAST]],"
	                                                           + "PROJECTION[\"Mercator_1SP\"],"
	                                                           + "PARAMETER[\"latitude_of_origin\", 50.0]," // latOfOriginValTag
	                                                           + "PARAMETER[\"central_meridian\", 40.0]," // cMeridianValTag
	                                                           + "PARAMETER[\"scale_factor\", 1.0],"
	                                                           + "PARAMETER[\"false_easting\", 0.0],"
	                                                           + "PARAMETER[\"false_northing\", 0.0],"
	                                                           + "UNIT[\"m\", 1.0],"
	                                                           + "AXIS[\"x\", EAST],"
	                                                           + "AXIS[\"y\", NORTH]]";

	private static final int	PROJ_MERCATOR	       = 1;

	private static String	    WKT_Lambert_2SP	       = "PROJCS[\"Lambert_Conformal_Conic_2SP\","
        + "GEOGCS[\"Geographic\","
        + "DATUM[\"Krassowsky\","
        + "SPHEROID[\"Krassowsky_1942\", 6378245.0, 298.3]],"
        + "PRIMEM[\"Greenwich\", 0.0],"
        + "UNIT[\"degree\", 0.017453292519943295],"
        + "AXIS[\"Geodetic latitude\", NORTH],"
        + "AXIS[\"Geodetic longitude\", EAST]],"
        + "PROJECTION[\"Lambert_Conformal_Conic_2SP\"],"
        + "PARAMETER[\"latitude_of_origin\", 50.0],"
        + "PARAMETER[\"STANDARD_PARALLEL_1\", 40.0],"
        + "PARAMETER[\"STANDARD_PARALLEL_2\", 60.0],"
        + "PARAMETER[\"central_meridian\", 100.0],"
        + "PARAMETER[\"false_easting\", 0.0],"
        + "PARAMETER[\"false_northing\", 0.0],"
        + "UNIT[\"m\", 1.0],"
        + "AXIS[\"x\", EAST],"
        + "AXIS[\"y\", NORTH]]";

	private static final int	PROJ_LAMBERT_CONIC	   = 2;

	private static String	    WKT_AlbersEqualArea	   = "PROJCS[\"Albers_Conic_Equal_Area\","
        + "GEOGCS[\"Geographic\","
        + "DATUM[\"Krassowsky\","
        + "SPHEROID[\"Krassowsky_1942\", 6378245.0, 298.3]],"
        + "PRIMEM[\"Greenwich\", 0.0],"
        + "UNIT[\"degree\", 0.017453292519943295],"
        + "AXIS[\"Geodetic latitude\", NORTH],"
        + "AXIS[\"Geodetic longitude\", EAST]],"
        + "PROJECTION[\"Albers_Conic_Equal_Area\"],"
        + "PARAMETER[\"latitude_of_origin\", 50.0],"
        + "PARAMETER[\"STANDARD_PARALLEL_1\", 40.0],"
        + "PARAMETER[\"STANDARD_PARALLEL_2\", 60.0],"
        + "PARAMETER[\"central_meridian\", 100.0],"
        + "PARAMETER[\"false_easting\", 0.0],"
        + "PARAMETER[\"false_northing\", 0.0],"
        + "UNIT[\"m\", 1.0],"
        + "AXIS[\"x\", EAST],"
        + "AXIS[\"y\", NORTH]]";

	private static final int	PROJ_ALBERS_CONIC	   = 3;

	private static String	    WKT_LambertAzimuthal	= "PROJCS[\"�Lambert_Azimuthal_Equal_Area\","
	                                                           + "GEOGCS[\"Geographic\","
	                                                           + "DATUM[\"Krassowsky\","
	                                                           + "SPHEROID[\"Krassowsky_1942\", 6378245.0, 298.3]],"
	                                                           + "PRIMEM[\"Greenwich\", 0.0],"
	                                                           + "UNIT[\"degree\", 0.017453292519943295],"
	                                                           + "AXIS[\"Geodetic latitude\", NORTH],"
	                                                           + "AXIS[\"Geodetic longitude\", EAST]],"
	                                                           + "PROJECTION[\"Lambert_Azimuthal_Equal_Area\"],"
	                                                           + "PARAMETER[\"LATITUDE_OF_CENTER\", 60.0],"
	                                                           + "PARAMETER[\"LONGITUDE_OF_CENTER\", 100.0],"
	                                                           + "PARAMETER[\"false_easting\", 0.0],"
	                                                           + "PARAMETER[\"false_northing\", 0.0],"
	                                                           + "UNIT[\"m\", 1.0],"
	                                                           + "AXIS[\"x\", EAST],"
	                                                           + "AXIS[\"y\", NORTH]]";

	private static final int	PROJ_LAMBERT_AZIMUTHAL	= 4;

	private static final String	WKT_Projections[]	   = { WKT_geographic,
	        WKT_Mercator_1SP, WKT_Lambert_2SP, WKT_AlbersEqualArea,
	        WKT_LambertAzimuthal	                   };

	private static final int	PROJ_LAST	           = PROJ_LAMBERT_AZIMUTHAL;

	/**
	 * Gets number of projections registered in this module
	 * 
	 * @return int value for projection size
	 */
	public static int size()
	{
		return PROJ_LAST;
	}

	/**
	 * Gets indicated projection title
	 * 
	 * @param index
	 *            index of projection in the list, see size()
	 * @return String with projection title or <code>null</code> if index out
	 *         of bounds
	 */
	public static String getProjTitle( int index )
	{
		if ( index < 0 || index >= PROJ_LAST )
			return null;
		if ( index == PROJ_GEOGRAPHICS )
			return getParameterTitle( WKT_Projections[ PROJ_GEOGRAPHICS ],
			        "GEOGCS" );
		return getParameterTitle( WKT_Projections[ index ], "PROJCS" );
	}

	/**
	 * returns geographic coordinate system description in WKT form
	 * 
	 * @return String with system in WKT form
	 */
	public static String getGeographicWKT()
	{
		return WKT_geographic;
	}

	/**
	 * returns WKT for Lambert conical conformal with 2 standard parallels
	 * projection for Krassowsky ellipsoid.
	 * 
	 * Latitude goes to North, Longitudes goes to East, False Easting 0.0, False
	 * Northing 0.0.
	 * 
	 * @param cMer
	 *            central meridian of this projection
	 * @param latOfOrig
	 *            latitude of origin
	 * @param stP1
	 *            standard parallel one
	 * @param stP2
	 *            standard parallel two
	 * @return String with a WKT text generated
	 */
	public static String getLambert2SP_WKT( int cMer, int latOfOrig, int stP1,
	        int stP2 )
	{
		String WKT = WKT_Lambert_2SP;
		WKT = replaceItem( WKT, "central_meridian", String.valueOf( cMer ) );
		WKT = replaceItem( WKT, "latitude_of_origin", String
		        .valueOf( latOfOrig ) );
		WKT = replaceItem( WKT, "STANDARD_PARALLEL_1", String.valueOf( stP1 ) );
		WKT = replaceItem( WKT, "STANDARD_PARALLEL_2", String.valueOf( stP2 ) );
		return WKT;
	}

	/**
	 * returns WKT for Albers equal area conical with 2 standard parallels
	 * projection for Krassowsky ellipsoid.
	 * 
	 * Latitude goes to North, Longitudes goes to East, False Easting 0.0, False
	 * Northing 0.0.
	 * 
	 * @param cMer
	 *            central meridian of this projection
	 * @param latOfOrig
	 *            latitude of origin
	 * @param stP1
	 *            standard parallel one
	 * @param stP2
	 *            standard parallel two
	 * @return String with a WKT text generated
	 */
	public static String getAlbersEqualArea_WKT( int cMer, int latOfOrig,
	        int stP1, int stP2 )
	{
		String WKT = WKT_AlbersEqualArea;
		WKT = replaceItem( WKT, "central_meridian", String.valueOf( cMer ) );
		WKT = replaceItem( WKT, "latitude_of_origin", String
		        .valueOf( latOfOrig ) );
		WKT = replaceItem( WKT, "STANDARD_PARALLEL_1", String.valueOf( stP1 ) );
		WKT = replaceItem( WKT, "STANDARD_PARALLEL_2", String.valueOf( stP2 ) );
		return WKT;
	}

	/**
	 * returns WKT for Lambert asimuthal for Krassowsky ellipsoid.
	 * 
	 * Latitude goes to North, Longitudes goes to East, False Easting 0.0, False
	 * Northing 0.0.
	 * 
	 * @param latCenter
	 *            latitude of projection centre
	 * @param lonCenter
	 *            longitude of projection centre
	 * @return String with a WKT text generated
	 */
	public static String getLambertAsimuthal_WKT( int latCenter, int lonCenter )
	{
		String WKT = WKT_LambertAzimuthal;
		WKT = replaceItem( WKT, "LATITUDE_OF_CENTER", String
		        .valueOf( latCenter ) );
		WKT = replaceItem( WKT, "LONGITUDE_OF_CENTER", String
		        .valueOf( lonCenter ) );
		return WKT;
	}

	/**
	 * returns WKT for Merkator with 1 standard parallel projection for
	 * Krassowsky ellipsoid.
	 * 
	 * Latitude goes to North, Longitudes goes to East, False Easting 0.0, False
	 * Northing 0.0.
	 * 
	 * @param cMer
	 *            central meridian of this projection
	 * @param latOfOrig
	 *            latitude of origin
	 * @return String with a WKT text generated
	 */
	public static String getMercator1SP_WKT( int cMer, int latOfOrig )
	{
		String WKT = WKT_Mercator_1SP;
		WKT = replaceItem( WKT, "central_meridian", String.valueOf( cMer ) );
		WKT = replaceItem( WKT, "latitude_of_origin", String
		        .valueOf( latOfOrig ) );
		return WKT;
	}

	private static String	PARAMETER	= "PARAMETER[";

	/**
	 * replaces parameter of known name and index of item with other value
	 * 
	 * @param param
	 *            parameter name to replace
	 * @param value2Set
	 *            String to replace designated item
	 * @return String modified or null if something was wrong
	 */
	private static String replaceItem( String WKT, String param,
	        String value2Set )
	{
		String str;
		// get first pos in parameter body
		int pos = 0, end = pos, comma_pos;
		/*
		 * find body of parameter of type "PARAMETER[Name,Value]"
		 */
		/* find the parameter name first */
		pos = WKT.indexOf( param ) - 1; // start of parameter name including
		// quotation mark '"'
		end = WKT.indexOf( ']', pos + param.length() ); // end of parameter body
		if ( pos < 0 )
			return null;
		int ppos = Text.getLastPos( WKT, 0, pos, PARAMETER );
		// check if it is parameter
		str = WKT.substring( ppos + PARAMETER.length(), pos );
		if ( str.trim().length() != 0 )
			return null;
		// well, find the comma and set parameter
		comma_pos = WKT.indexOf( ',', pos + param.length() );
		if ( comma_pos >= end )
			return null;
		return WKT.substring( 0, comma_pos + 1 ) + ' ' + value2Set
		        + WKT.substring( end );
	}

	/**
	 * finds parameter type (e.g. "AXIS") with a designated name (e.g. "x") and
	 * returns its string value. E.g. find for ( "AXIS", "x") in the parameter
	 * string "AXIS[\"x\", EAST]" will return "EAST"
	 * 
	 * @param WKT
	 *            Well Known Text with CRS description
	 * @param typeName
	 *            name of a parameter, e.g. "AXIS"
	 * @param paramName
	 *            name of an item, e.g. "x"
	 * @return found trimmed value or <code>null</code> if no such parameter
	 *         or item
	 */
	public static String getParameterValue( String WKT, String typeName,
	        String paramName )
	{
		int pos1 = 0;
		typeName = typeName.trim();
		if ( !paramName.startsWith( "\"" ) )
			paramName = "\"" + paramName.trim() + "\"";
		while ( pos1 >= 0 )
		{
			pos1 = WKT.indexOf( typeName + "[", pos1 ) + 1;
			if ( pos1 <= 0 )
				return null;
			int pos2 = WKT.indexOf( ']', pos1 );
			int pos = WKT.indexOf( paramName, pos1 );
			if ( ( pos < 0 ) || ( pos > pos2 ) )
				continue;
			pos = WKT.indexOf( ',', pos + 1 );
			if ( ( pos < 0 ) || ( pos > pos2 ) )
				return null;
			return WKT.substring( pos + 1, pos2 ).trim();
		}
		return null;
	}

	/**
	 * finds parameter type (e.g. "AXIS") with a designated name (e.g. "x") and
	 * returns its string value. E.g. find for ( "AXIS", "x") in the parameter
	 * string "AXIS[\"x\", EAST]" will return "EAST"
	 * 
	 * @param WKT
	 *            Well Known Text with CRS description
	 * @param typeName
	 *            name of a parameter, e.g. "AXIS"
	 * @param paramName
	 *            name of an item, e.g. "x"
	 * @return found trimmed value or <code>null</code> if no such parameter
	 *         or item
	 */
	public static String getParameterTitle( String WKT, String typeName )
	{
		int pos1 = 0;
		typeName = typeName.trim();
		while ( pos1 >= 0 )
		{
			String stype = typeName + "[";
			pos1 = WKT.indexOf( stype, pos1 ) + 1;
			if ( pos1 <= 0 )
				return null;

			pos1 += stype.length();
			int pos2 = WKT.indexOf( '"', pos1 );
			if ( pos2 < 0 )
				return null;
			return WKT.substring( pos1, pos2 ).trim();
		}
		return null;
	}
}
