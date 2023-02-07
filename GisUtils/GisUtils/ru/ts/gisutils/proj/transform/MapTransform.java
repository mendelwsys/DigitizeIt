package ru.ts.gisutils.proj.transform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.units.SI;

import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.datum.AbstractDatum;
import org.geotools.referencing.datum.BursaWolfParameters;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.geotools.referencing.datum.DefaultGeodeticDatum;
import org.geotools.referencing.datum.DefaultPrimeMeridian;
import org.geotools.referencing.factory.FactoryGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.NoSuchIdentifierException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.Operation;
import org.opengis.referencing.operation.OperationNotFoundException;

import ru.ts.utils.Text;

public class MapTransform
{
	private static HashMap _ellipsoid_map = new HashMap/*<String, DefaultEllipsoid>*/(); 
	private static HashMap _datum_map = new HashMap/*<String, DefaultGeodeticDatum>*/ ();
//	private static Map<String, ParameterValueGroup> _param_map = Collections.emptyMap();
	
	/**
	 * Constructs a new ellipsoid using the specified axis length and inverse flattening value.
	 *
	 * @param name              The ellipsoid name which will be assigned to it. If such name already exists,
	 * its assotiated value is returned without regarding to all other parameters
	 * @param semiMajorAxis     The equatorial radius (in meters).
	 * @param inverseFlattening The inverse flattening value (relative).
	 * @return new ellipsoid object for your pleasure
	 */
	synchronized public static DefaultEllipsoid createEllipsoid(final String name,
	                                                     final double semiMajorAxis,
	                                                     final double inverseFlattening)
	{
		if ( _ellipsoid_map.containsKey(name))
			return (DefaultEllipsoid)_ellipsoid_map.get(name);
		else
		{
			final DefaultEllipsoid elli = DefaultEllipsoid.createFlattenedSphere( name, semiMajorAxis, inverseFlattening, SI.METER);
			_ellipsoid_map.put(name, elli);
			return elli;
		}
	}

	private final static String KRASSOWSKY = "Krassowsky_1942";
	static
	{
		_ellipsoid_map.put(KRASSOWSKY, DefaultEllipsoid.createFlattenedSphere(KRASSOWSKY, 6378245.0, 298.3, SI.METER));
	}

	/**
	 * 
	 * @return ellipsoid with Krassovsky(1942) geometry parameters
	 */
	public static DefaultEllipsoid createKrassowskyEllipsoid()
	{
		return (DefaultEllipsoid)_ellipsoid_map.get(KRASSOWSKY);
	}
	
	/**
	 * 
	 * @return ellipsoid of WGS84 parameters
	 */
	public static Ellipsoid createWGS84Ellipsoid()
	{
		return DefaultEllipsoid.WGS84;
	}

	/**
     * Constructs a geodetic datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     * Additionally, the following properties are understood by this construtor:
     * <p>
     * <tables border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;"name"</td>
     *     <td nowrap>&nbsp;name for the new datum<br>should be unique for each special<br>set of parameters</td>
     *     <td nowrap>&nbsp;for internal usage only</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@link #BURSA_WOLF_KEY "bursaWolf"}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link BursaWolfParameters} or an array of those&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getBursaWolfParameters}</td>
     *   </tr>
     * </tables>
     *
     * @param properties    Set of properties. Should contains at least <code>"name"</code>.
     * Also can contains Bursa-Wolf parameters block
     * @param ellipsoid     The ellipsoid (should exists).
     * @param primeMeridian The prime meridian(should be valid).
     */
	synchronized public static DefaultGeodeticDatum createGeodeticDatum(Map properties, Ellipsoid ellipsoid, PrimeMeridian primeMeridian)
    {
		String name = (String)properties.get("name");
		if( _datum_map.containsKey(name) )
		{
			return (DefaultGeodeticDatum)_datum_map.get(name);
		}
		else
		{
			DefaultGeodeticDatum dat = new DefaultGeodeticDatum(properties, ellipsoid, primeMeridian);
			_datum_map.put(name, dat);
			return dat;
		}
    }

	public static CRSFactory get_crsFactory()
	{
		return _crsFactory;
	}

	protected static final CRSFactory _crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
	protected static final CSFactory _csFactory = ReferencingFactoryFinder.getCSFactory(null);
	protected static final MathTransformFactory _mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
	protected static final FactoryGroup _factories = new FactoryGroup(null);
	protected static final CoordinateSystemAxis _latAxis = org.geotools.referencing.cs.DefaultCoordinateSystemAxis.GEODETIC_LATITUDE;
	protected static final CoordinateSystemAxis _longAxis = org.geotools.referencing.cs.DefaultCoordinateSystemAxis.GEODETIC_LONGITUDE;
//	private static final CartesianCS _cartCS = org.geotools.referencing.cs.DefaultCartesianCS.GENERIC_2D;
	protected static final CartesianCS _cartCS = org.geotools.referencing.cs.DefaultCartesianCS.PROJECTED;
	protected static EllipsoidalCS _ellipseCS;
	
	static
	{ 
		try 
		{
			_ellipseCS= _csFactory.createEllipsoidalCS(Collections
		        .singletonMap("name", "Lat/Long"), _latAxis, _longAxis);
		}
		catch(Exception e)
		{
			_ellipseCS = null;
		}
	}

	/**
	 * creates Geographic Coordinates Reference System(units are in degrees). 
	 * First goes latitudes and second - longitudes. 
	 * @author Sygsky
	 * 
	 * @param name String name of a new GeographicCRS
	 * @param ellipsoidName Ellipsoid string Name (should exists in database)
	 * @param datum GeodeticDatum
	 * @return newly created GCRS
	 */
	public static GeographicCRS createGeographicCRS( String name, DefaultGeodeticDatum datum ) throws FactoryException
    {
		return _crsFactory.createGeographicCRS(Collections.singletonMap("name", name), datum, _ellipseCS);
    }

	/**
	 * here are all possible field values for a string identifiers from a nameId 
	 * of any projection registered
	 */
	private static final HashMap/*<String, Integer>*/ _map = new HashMap/*<String, Integer>*/();
    static {
        _map.put("Name", new Object[] {new Integer ( 1)});
        _map.put("Projection", new Object[] {new Integer (  2)});
        _map.put("Ellipsoid",  new Object[] {new Integer( 3)});
        _map.put("Datum",  new Object[] {new Integer( 4)});
        _map.put("EPSG", new Object[] {new Integer( 5)});
    }

    /**
     * gets all available item names for the c
     * @return
     */
    synchronized public static String[] getItemNames()
    {
    	return (String[])_map.keySet().toArray(new String[0]);
    }
    
	/**
	 * creates a new projected coordinate system through its name. Name should be
	 * known to the system
	 * 
	 * @param definitionString consists of pairs of items separated by semicolons (';'). 
	 * Each item  consists of key separated from value by equal char ('='). Now there are
	 * two forms for the string - first with multiple items (first example) and second - 
	 * with a single item (second example).
	 * 
	 * Example of some projectionString: 
	 * 
	 * 		"Projection=Mercator_1SP;Ellipsoid=Krassowsky,6378245.0,298.3;Datum=Pulkovo_1990;" 
	 * or
	 * 		"EPSG=4326;" 
	 * 
	 * Now there are 3 (three) items which are recognised in the string:
	 * 1. Name  - this item is used to find already existing projectionString after 
	 * 			  its registration
	 * 
	 * 2. Projection - the projection name. It is strongly typed name and should exist in a list
	 *   of projections, supported in GeoTools.
	 *    
	 * 3. Ellipsoid - ellipsoid for this CRS. Should consists of 3 elements, namely:
	 *   3.1. Ellipsoid name
	 *   3.2. The equatorial radius.
     * 	 3.3. The inverse of the flattening value.
     *   So the Ellipsoid item can be as follow: "Ellipsoid=Krassovsky,6378245.0,298.3"
     *   
	 * 4. Datum - datum for the ellipsoid. Still the string is used as a dumb string. 
	 * In future the the Molodensky or Bursa-Wolf parameters will be parsed.
	 *  
	 * 5. EPSG - is detected, all other items are skipped and code tries to create
	 * the CRS from the EPSG database. At this moment I don't know how tTo add new projection to EPSG 
	 * 
	 * @return newly created Projected Coordinates System, or null if no projected CRS
	 * was found
	 * 
	 * @throws FactoryException
	 */
	public static ProjectedCRS createProjectedCRS( String definitionString ) 
		throws FactoryException, IllegalArgumentException
    {
		String[] items = definitionString.split(";");
		String namestr = null, projstr = null, ellistr = null, datumstr = null;
		DefaultEllipsoid ellipsoid = null;
		DefaultGeodeticDatum datum = null;

		for(int i = 0; i < items.length; i++)
		{
			String item = items[i].trim();
			String[] pair = item.split("=");
			if (pair.length != 2)
				throw new IllegalArgumentException("projectionString \"item=value\" is \"" + item + "\"");
			String key = pair[0].trim();
			String value = pair[1].trim();
			
			if (!_map.containsKey(key))
				throw new IllegalArgumentException( "\"" + key + "\"" );
			switch(((Integer)_map.get(key)).intValue())
			{
				case 1:	
					// Name
					namestr = value;
					break;
				case 2:
					// Projection
					projstr = value;
					// 
					break;
				case 3:
					// Ellipsoid
					ellistr = value;
					break;
				case 4:
					// Datum
					datumstr = value;
					break;
				case 5:
					// EPSG
					return CRS.getProjectedCRS(CRS.decode("EPSG:"+value));
				default:
					throw new IllegalArgumentException("(\""+ key +"\") �");
			}
		}
		
		// check if all parameters present
		if( ellistr == null || projstr == null || datumstr == null )
			throw new IllegalArgumentException("Null as parameter");
		
		ParameterValueGroup parameters = _mtFactory.getDefaultParameters(projstr);
		
		/*
		 *  process ellipsoid info
		 */
		items = Text.splitItems( ellistr, ',', false);
		ellipsoid = createEllipsoid(items[0],Double.parseDouble(items[1]),Double.parseDouble(items[1]));
		
		/* 
		 * process datum info
		 */
		Map properties = Collections.singletonMap("name", "Krassowsky����");

		datum = createGeodeticDatum(properties, ellipsoid, DefaultPrimeMeridian.GREENWICH); 
		properties.put("name", projstr);
		final GeographicCRS gCRS = createGeographicCRS("���������",datum);
		return _factories.createProjectedCRS(properties, gCRS, null, parameters, _cartCS);
    }

	/**
	 * creates ProjectedCRS for caller usage
	 * 
	 * @param name of resulting CRS 
	 * @param parameters ParameterValueGroup for projection 
	 * @param ellipsoid DefaultEllipsoid for this projection
	 * @param datum DefaultGeodeticDatum for this projection
	 * @return ProjectedCRS or exception if any error
	 * @throws FactoryException
	 */
	public static ProjectedCRS createProjectedCRS(String name, 
			ParameterValueGroup parameters, DefaultEllipsoid ellipsoid, 
			DefaultGeodeticDatum datum)
		throws FactoryException
    {
/*		if (false)
		{
			// Set optional parameters here. This example set the false
			// easting and northing just for demonstration purpose.
			parameters.parameter("false_easting").setValue(1000.0);
			parameters.parameter("false_northing").setValue(1000.0);
		
		}
*/		// first create a GRS
		final GeographicCRS gCRS = createGeographicCRS( "Geographic", datum);
		final Map properties = Collections.singletonMap("name", name);
		return _factories.createProjectedCRS(properties,
				gCRS, null, parameters, _cartCS);
    }

	/**
	 * @param srcCRS source coordinate reference system. For example,
	 * it can be a result of the {@link #createGeographicCRS} call.
	 * 
	 * @param dstCRS destination coordinate reference system. For example,
	 * it can be a result of the {@link #createProjectedCRS} call.
	 * 
	 * @return new IMapTransformer object to transform coordinates to and back
	 */
	public static IMapTransformer createMapTransformer(SingleCRS srcCRS,
	        SingleCRS dstCRS) throws OperationNotFoundException,
	        FactoryException
	{
		return new MapTransformer(srcCRS, dstCRS);
	}

	
	/**
	 * creates IMapTransformer object to transform coordinates
	 * @param sourceWKT - source projection string of WKT 
	 * @param targetWKT - target projection string of WKT
	 * @return new {@link IMapTransformer#IMapTransformer} interface object
	 * @throws OperationNotFoundException
	 * @throws FactoryException
	 */
	public static IMapTransformer createMapTransformer(String sourceWKT, String targetWKT) throws OperationNotFoundException, FactoryException
	{
		return new MapTransformer(sourceWKT, targetWKT);
	} 
	
	/**
	 * @param sourceCRS source coordinate reference system. For example,
	 * it can be a result of the {@link #createGeographicCRS} call.
	 * 
	 * @param targetCRS destination coordinate reference system. For example,
	 * it can be a result of the {@link #createProjectedCRS} call.
	 * 
	 * @return new IMapTransformer object to transform coordinates to and back
	 * @throws OperationNotFoundException
	 * @throws FactoryException
	 */
	public static IMapTransformer createMapTransformer(CoordinateReferenceSystem sourceCRS,
			CoordinateReferenceSystem targetCRS) throws OperationNotFoundException,
	        FactoryException
	{
		return new MapTransformer(sourceCRS, targetCRS);
	}
	
	/**
	 * use it to get already prescribed definitions from EPSG
	 * 
	 * @param epsgCode - see file epsg.properties - you will understand very much things :o)
	 * @return CoordinateReferenceSystem assigned for this code or exception if no such code
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 */
	public static CoordinateReferenceSystem getCRSFromEPSGName(String epsgCode)
		throws NoSuchAuthorityCodeException, FactoryException
	{
		return CRS.decode(epsgCode);
	}

    /**
     * Returns the default parameter values for a math transform using the given method.
     * The {@code method} argument is the name of any operation method returned by
     * <code>{@link #getAvailableMethods getAvailableMethods}({@linkplain Operation}.class)</code>.
     * A typical example is
     * <code>"<A HREF="http://www.remotesensing.org/geotiff/proj_list/transverse_mercator.html">Transverse_Mercator</A>"</code>).
     *
     * <P>The {@linkplain ParameterDescriptorGroup#getName parameter group name} shall be the
     * method name, or an alias to be understood by <code>{@linkplain #createParameterizedTransform
     * createParameterizedTransform}(parameters)</code>. This method creates new parameter instances
     * at every call. Parameters are intended to be modified by the user before to be given to the
     * above-cited {@code createParameterizedTransform} method.</P>
     *
     * @param  method The case insensitive name of the method to search for.
     * @return The default parameter values.
     * @throws NoSuchIdentifierException if there is no transform registered for the specified method.
     */

	public static ParameterValueGroup getDefaultParameters(String method)
		throws NoSuchIdentifierException
	{
		return _mtFactory.getDefaultParameters(method);
	};
	
}
