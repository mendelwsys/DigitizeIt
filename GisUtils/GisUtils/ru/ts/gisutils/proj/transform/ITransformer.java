/**
 * Created on 05.06.2008 20:20:11 2008 by Syg for project in
 * 'ru.ts.gisutils.pcntxt.transform' of 'test'
 */
package ru.ts.gisutils.proj.transform;


import org.opengis.referencing.operation.TransformException;
import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;

/**
 * Interface to transform any date by any means, polynomial or cartographic
 * projection
 *
 * @author Syg
 */
public interface ITransformer
{
	/**
	 * Transforms input coordinates to output one
	 *
	 * @param pntSrc is a source point to transform of type
	 *               java.awt.geom.Point2D.Double. Units are custom.
	 * @param pntDst is a destination point to receive result of type
	 *               java.awt.geom.Point2D.Double. Units are custom.
	 */
	void TransformDirect(IGetXY pntSrc, IXY pntDst) throws TransformException;

	/**
	 * Transforms a list of coordinate point ordinal values. This method is
	 * provided for efficiently transforming many points. The supplied array of
	 * ordinal values will contain packed ordinal values. For example, as the
	 * source dimension is 2, then the ordinals will be packed in this order: (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var></var>,
	 * <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var></var> ...).
	 * <p/>
	 * Coordinate units are custom!
	 *
	 * @param srcPnts the array containing the source point coordinates.
	 * @param srcOff  the offset to the first (double) coordinate to be transformed
	 *                in the source array.
	 * @param dstPnts the array into which the transformed (double) coordinates are
	 *                returned. May be the same than {@code srcPts}.
	 * @param dstOff  the offset to the location of the first transformed (double)
	 *                coordinate that is stored in the destination array ( may be
	 *                the same as source one.
	 * @param numPnt  the number of point objects to be transformed.
	 */
	void TransformDirect(double[] srcPnts, int srcOff, double[] dstPnts,
						 int dstOff, int numPnt) throws TransformException;


	/**
	 * Transforms input coordinates (x,y) to output ones.
	 *
	 * @param pnts	the array containing the point coordinates. Units are custom.
	 *                Result is stored in the same array. Old values will be
	 *                overwritten.
	 * @param srcOff  the offset to the first (double) coordinate to be transformed
	 *                in the source array.
	 * @param numPnts the number of point objects to be transformed.
	 */
	void TransformDirect(double[] pnts, int srcOff, int dsrOff, int numPnts) throws TransformException;

	/**
	 * Transforms inversely x,y coordinates
	 *
	 * @param pntSrc is a source point to transform of type
	 *               java.awt.geom.Point2D.Double. Units are custom.
	 * @param pntDst is a destination point to receive result
	 *               java.awt.geom.Point2D.Double. Units are custom.
	 */
	void TransformInverse(IGetXY pntSrc, IXY pntDst) throws TransformException;

	/**
	 * Transforms a list of coordinate point ordinal values in inverse
	 * direction, i.e. from destination projection to the source one. This
	 * method is provided for efficiently transforming many points. The supplied
	 * array of ordinal values will contain packed ordinal values. As the source
	 * dimension is 2, then the ordinals will be packed in this order: (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var></var>,
	 * <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var></var> ...).
	 *
	 * @param srcPnts the array containing the source point coordinates.
	 * @param srcOff  the offset to the first (double) coordinate to be transformed
	 *                in the source array.
	 * @param dstPnts the array into which the transformed point coordinates are
	 *                returned. May be the same than {@code srcPts}.
	 * @param dstOff  the offset to the location of the first transformed (double)
	 *                coordinate that is stored in the destination array.
	 * @param numPnt  the number of point objects to be transformed.
	 */
	void TransformInverse(double[] srcPnts, int srcOff, double[] dstPnts,
						  int dstOff, int numPnt) throws TransformException;

	/**
	 * Transforms a list of coordinate point ordinal values in inverse
	 * direction, i.e. from destination projection to the source one. This
	 * method is provided for efficiently transforming many points. The supplied
	 * array of ordinal values will contain packed ordinal values. For example,
	 * if the source dimension is 3, then the ordinals will be packed in this
	 * order: (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var>, <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>
	 * ...).
	 *
	 * @param pnts	the array containing the source point coordinates.
	 * @param srcOff  the offset to the first (double) coordinate to be transformed
	 *                in the source array.
	 * @param dstOff  the offset to the location of the first transformed (double)
	 *                coordinate that is stored in the same array.
	 * @param numPnts the number of point objects to be transformed.
	 */
	void TransformInverse(double[] pnts, int srcOff, int dstOff, int numPnts) throws TransformException;
}
