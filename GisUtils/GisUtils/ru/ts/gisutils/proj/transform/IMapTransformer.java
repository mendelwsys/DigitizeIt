/**
 * 
 */
package ru.ts.gisutils.proj.transform;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.opengis.referencing.operation.TransformException;
import ru.ts.utils.data.Pair;

import javax.units.Converter;

/**
 * Works to help mass point transformation without excessive repeatable
 * parameters pointing for each call.
 * 
 * @author sygsky
 * 
 */
public interface IMapTransformer  extends ITransformer
{

	String getTransformerFactoryType();

  /**
   * yg: need to get the transformer parameters from server delivering them to client
   * @return transformer parameters
   */
  public String[] getParams();


	/**
	 * Transforms input Lambda,Fi (geographical coordinates) to X,Y (on map
	 * list)
	 * 
	 * @param pnts
	 *            the array containing the point coordinates. Units are degrees.
	 *            Result is stored in the same array. Old values will be
	 *            overwritten.
	 * @param srcOff
	 *            the offset to the first (double) coordinate to be transformed
	 *            in the source array.
	 * @param numPnts
	 *            the number of point objects to be transformed.
	 * @throws TransformException
	 *             if a point can't be transformed.
	 */
	void TransformDirect( double[] pnts, int srcOff, int numPnts )
	        throws TransformException;

	/**
	 * Returns a <cite>Well Known Text</cite> (WKT) for this object. Well know
	 * text are <A HREF="../doc-files/WKT.html">defined in extended Backus Naur
	 * form</A>. This operation may fails if an object is too complex for the
	 * WKT format capability.
	 * 
	 * @return The <A HREF="../doc-files/WKT.html"><cite>Well Known Text</cite>
	 *         (WKT)</A> for this object.
	 * @throws UnsupportedOperationException
	 *             If this object can't be formatted as WKT.
	 */
	String getWKT();


	/**
	 * @return type of transformer
	 */
	public Pair<String,String> getTransformerType();

	/**
	 * Unknown direction, for example, you asked for X direction but
	 * for geographic CRS with Lat/Lon parameters, not X/Y  
	 */
	public static int DIRECTION_UNKNOWN = -1;
	/**
	 * North direction
	 */
	public static int DIRECTION_NORTH = 0;
	/**
	 * East direction
	 */
	public static int DIRECTION_EAST  = 1;
	/**
	 * South direction
	 */
	public static int DIRECTION_SOUTH = 2;
	/**
	 * West direction
	 */
	public static int DIRECTION_WEST  = 3;
	
	/**
	 * returns direction of X axis of the destination coordinate system
	 * @return int enumeration, e.g. DIRECTION_EAST or any other
	 */
	int getXDstDirection();

	/**
	 * returns direction of Y axis of the destination coordinate system
	 * @return int enumeration, e.g. DIRECTION_NORTH or any other
	 */
	int getYDstDirection();

	void saveTransformer(DataOutputStream dos) throws Exception;

	IMapTransformer loadTransformer(DataInputStream dis) throws Exception;

	IMapTransformer loadTranformerByTextFile(BufferedReader isr, String charsetName) throws Exception;

	String getSrcWKT();

	String getDstWKT();

	Converter getUnitConverter(String unitname,boolean fromsrc);
}
