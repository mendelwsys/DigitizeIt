/**
 * 
 */
package ru.ts.gisutils.proj.transform;

import java.awt.geom.Point2D;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

/**
 * Works to help mass point transformation without excessive repeatable
 * parameters pointing for each call.
 * 
 * @author sygsky
 * 
 */
public interface IMapTransformerBase
{
  /**
   * yg: need to get the transformer parameters from server delivering them to client
   * @return transformer parameters
   */
  public String[] getParams();

  /**
	 * Transforms input Lambda,Fi (geographical coordinates) to X,Y (on map
	 * list)
	 * 
	 * @param pntSrc
	 *            is a source point to transform of type
	 *            java.awt.geom.Point2D.Double Units are degrees.
	 * @param pntDst
	 *            is a destination point to receive result of type
	 *            java.awt.geom.Point2D.Double Units are meters.
	 * @throws TransformException
	 *             if a point can't be transformed.
	 * @deprecated Use {@link #TransformDirect(Point2D,Point2D)} instead
	 *///TODO DO NОT UNCOMENT IT USE NEW FUNCTION (VLAD)
//	void TransformDirect( Point2D.Double pntSrc, Point2D.Double pntDst )
//	        throws TransformException;

	/**
	 * Transforms input Lambda,Fi (geographical coordinates) to X,Y (on map
	 * list)
	 * 
	 * @param pntSrc
	 *            is a source point to transform of type
	 *            java.awt.geom.Point2D.Double Units are degrees.
	 * @param pntDst
	 *            is a destination point to receive result of type
	 *            java.awt.geom.Point2D.Double Units are meters.
	 * @throws TransformException
	 *             if a point can't be transformed.
	 */
	void TransformDirect( Point2D pntSrc, Point2D pntDst )
	        throws TransformException;

	/**
	 * Transforms a list of coordinate point ordinal values. This method is
	 * provided for efficiently transforming many points. The supplied array of
	 * ordinal values will contain packed ordinal values. For example, if the
	 * source dimension is 3, then the ordinals will be packed in this order:
	 *  (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var>,<var>z<sub>0</sub></var>,
	 * <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>,<var>z<sub>1</sub></var>
	 * ...).
	 * 
	 * Coordinate units are degrees!
	 * 
	 * @param srcPnts
	 *            the array containing the source point coordinates.
	 * @param srcOff
	 *            the offset to the first (double) coordinate to be transformed
	 *            in the source array.
	 * @param dstPnts
	 *            the array into which the transformed (double) coordinates are
	 *            returned. May be the same than {@code srcPts}.
	 * @param dstOff
	 *            the offset to the location of the first transformed (double)
	 *            coordinate that is stored in the destination array ( may be
	 *            the same as source one.
	 * @param numPnt
	 *            the number of point objects to be transformed.
	 * @throws TransformException
	 *             if a point can't be transformed.
	 */
	void TransformDirect( double[] srcPnts, int srcOff, double[] dstPnts,
	        int dstOfft, int numPnt ) throws TransformException;

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
	 * Transforms input X,Y (on map list) to Lambda,Fi (geographical
	 * coordinates)
	 * 
	 * @param pntSrc
	 *            is a source point to transform of type
	 *            java.awt.geom.Point2D.Double Units are meters.
	 * @param pntDst
	 *            is a destination point on the ellipsoid to receive result
	 *            java.awt.geom.Point2D.Double Units are radianes.
	 * @throws TransformException
	 *             if a point can't be transformed.
	 * @deprecated Use {@link #TransformInverse(Point2D,Point2D)} instead
	 *///TODO DO NОT UNCOMENT IT USE NEW FUNCTION (VLAD)
//	void TransformInverse( Point2D.Double pntSrc, Point2D.Double pntDst )
//	        throws TransformException;

	/**
	 * Transforms input X,Y (on map list) to Lambda,Fi (geographical
	 * coordinates)
	 * 
	 * @param pntSrc
	 *            is a source point to transform of type
	 *            java.awt.geom.Point2D.Double Units are meters.
	 * @param pntDst
	 *            is a destination point on the ellipsoid to receive result
	 *            java.awt.geom.Point2D.Double Units are radianes.
	 * @throws TransformException
	 *             if a point can't be transformed.
	 */
	void TransformInverse( Point2D pntSrc, Point2D pntDst )
	        throws TransformException;

	/**
	 * Transforms a list of coordinate point ordinal values in inverse
	 * direction, i.e. from destination projection to the source one. This
	 * method is provided for efficiently transforming many points. The supplied
	 * array of ordinal values will contain packed ordinal values. For example,
	 * if the source dimension is 3, then the ordinals will be packed in this
	 * order:
	 *  (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var>,<var>z<sub>0</sub></var>,
	 * <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>,<var>z<sub>1</sub></var>
	 * ...).
	 * 
	 * @param srcPnts
	 *            the array containing the source point coordinates.
	 * @param srcOff
	 *            the offset to the first (double) coordinate to be transformed
	 *            in the source array.
	 * @param dstPnts
	 *            the array into which the transformed point coordinates are
	 *            returned. May be the same than {@code srcPts}.
	 * @param dstOff
	 *            the offset to the location of the first transformed (double)
	 *            coordinate that is stored in the destination array.
	 * @param numPnt
	 *            the number of point objects to be transformed.
	 * @throws TransformException
	 *             if a point can't be transformed.
	 */
	void TransformInverse( double[] srcPnts, int srcOff, double[] dstPnts,
	        int dstOff, int numPnt ) throws TransformException;

	/**
	 * Transforms a list of coordinate point ordinal values in inverse
	 * direction, i.e. from destination projection to the source one. This
	 * method is provided for efficiently transforming many points. The supplied
	 * array of ordinal values will contain packed ordinal values. For example,
	 * if the source dimension is 3, then the ordinals will be packed in this
	 * order:
	 *  (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var>,<var>z<sub>0</sub></var>,
	 * <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>,<var>z<sub>1</sub></var>
	 * ...).
	 * 
	 * @param pnts
	 *            the array containing the source point coordinates.
	 * @param srcOff
	 *            the offset to the first (double) coordinate to be transformed
	 *            in the source array.
	 * @param dstOff
	 *            the offset to the location of the first transformed (double)
	 *            coordinate that is stored in the array.
	 * @param numPnts
	 *            the number of point objects to be transformed.
	 * @throws TransformException
	 *             if a point can't be transformed.
	 */
	void TransformInverse( double[] pnts, int srcOff, int dstOff, int numPnts )
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

}

