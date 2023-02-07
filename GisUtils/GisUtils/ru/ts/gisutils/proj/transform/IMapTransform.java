/**
 * 
 */
package ru.ts.gisutils.proj.transform;

import java.util.Map;

import org.geotools.referencing.datum.AbstractDatum;
import org.geotools.referencing.datum.BursaWolfParameters;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.PrimeMeridian;

/**
 * @author sygsky
 *
 */
public interface IMapTransform
{
/*	*//**
	 * returns list of strings which are connected with
	 * corresponding map projection. Each string is 
	 * connected only with one projection. You can 
	 * take in these strings as an unique string identifier
	 * for a projection.
	 * 
	 * @return array of strings for all supported map projections
	 *//*
	String[] GetSupportedProjections(); 
	
	*//**
	 * adds new projection to the list of supported ones.
	 * 
	 * @param newprojname adding projection string identifier. Normally 
	 * it consists of parameter=value pairs concatenated by semicolons.
	 * You can put empty string (null) as an argument. In this case
	 * internal logic will construct the string by his own means. And
	 * you can read it as a next step of your program :o)
	 * 
	 * @param projection class to support newly adding projection
	 *//*
	void addProjection(String newprojname, MapProjection projection);
*/
    /**
     * Constructs a geodetic datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     * Additionally, the following properties are understood by this constructor:
     * <p>
     * <tables border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@link #BURSA_WOLF_KEY "bursaWolf"}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link BursaWolfParameters} or an array of those&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getBursaWolfParameters}</td>
     *   </tr>
     * </tables>
     *
     * @param properties    Set of properties. Should contains at least <code>"name"</code>.
     * @param ellipsoid     The ellipsoid (should exists).
     * @param primeMeridian The prime meridian(should exists).
     */
	GeodeticDatum createGeodeticDatum(final Map properties,
            final Ellipsoid     ellipsoid,
            final PrimeMeridian primeMeridian);
	
	/**
	 * builds a new geographical CRS for an user goals
	 * 
	 * @param datum datum needed is the only parameter to build a standard 
	 * geographic reference system. The default values used to create it (in
	 * interiors of this method), include follow:
	 * 1. 1st axe is a latitude {@link org.geotools.referencing.cs.DefaultCoordinateSystemAxis#GEODETIC_LATITUDE}
	 * 2. 2nd is a longitude {@link org.geotools.referencing.cs.DefaultCoordinateSystemAxis#GEODETIC_LONGITUDE}
	 * 3. Ellipsoidal coordinate system without ellipsoidal height is used.
	 */
	GeographicCRS createGeographicCRS(GeodeticDatum datum);

	/**
	 * builds a new projected CRS for an user goals
	 * 
	 * @param ellipsoidName name for the used ellipsoid. It can be present in the
	 * list of standard ellipsoids or should be in the follow free sequence of names, 
	 * predefined ones are:
	 * "semiMajorAxis=6378245; inverseFlattening=298.3; name=Krassovsky"
	 * 
	 * @param datum is the only parameter needed to build a standard 
	 * geographic reference system. The default values used to create it in
	 * interiors of this method, include follow:
	 * 1. 1st axis is a latitude {@link org.geotools.referencing.cs.DefaultCoordinateSystemAxis#GEODETIC_LATITUDE}
	 * 2. 2nd one is a longitude {@link org.geotools.referencing.cs.DefaultCoordinateSystemAxis#GEODETIC_LONGITUDE}
	 * 3. Ellipsoidal coordinate system without ellipsoidal height is used.
	 */
	ProjectedCRS createProjectedCRS(String ellipsoidName, GeodeticDatum datum);
	
	/**
	 * @param srcCRS source coordinate reference system. For example,
	 * it can be a result of the {@link #createGeographicCRS} call.
	 * @param dstCRS destination coordinate reference system. For example,
	 * it can be a result of the {@link #createProjectedCRS} call.
	 * @return new IMapTransformer object to transform coordinates to and back
	 */
	/*IMapTransformer createMapTransformer( SingleCRS srcCRS, SingleCRS dstCRS)
	throws OperationNotFoundException,
    FactoryException;*/
}
