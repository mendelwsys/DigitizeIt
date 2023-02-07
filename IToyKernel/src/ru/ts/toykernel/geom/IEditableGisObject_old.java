package ru.ts.toykernel.geom;

import ru.ts.utils.data.Pair;
import ru.ts.gisutils.geometry.ICoordinate;
import ru.ts.gisutils.algs.common.MPoint;

import java.util.List;
import java.util.Map;

/**
 * Editable gis object (UNDER construction)
 */
public interface IEditableGisObject_old extends IGisObject
{
 	boolean isEndPoint(Pair<Integer, Integer> prindex);//TODO Убрать эту функцию

	void rebuildGisValume();

	List<MPoint> replacePoints(List<Pair<Integer, Integer>> indices,
												  MPoint pntnew);

	void moveByPoints(Pair<Integer, Integer> index, MPoint pntnew);

	void splitCurveByPoint(Pair<Integer, Integer> index);

	boolean removeSegment(int segindex);

	boolean removePoint(Pair<Integer, Integer> index);

	void addSegmentWithGeoPoint(List<Pair<Integer, Integer>> indices, MPoint pntnew);

	void insertSegmentWithGeoPoint(Pair<Integer, Integer> index, MPoint pntnew);

	void mergeSegments(Pair<Integer, Integer> index1, Pair<Integer, Integer> index2,
							  MPoint pntnew);

	void mergeCurves(IBaseGisObject curve) throws Exception;

	void setCurveAttrs(Map<String, Object> hattrs)
			throws Exception;

	void mergeCurvesWithSegs(Pair<Integer, Integer> index1, IEditableGisObject_old curve, Pair<Integer, Integer> index2,
											MPoint pntnew) throws Exception;

	void addSegment(MPoint[] points);

	boolean setCoordinates(double[] coords, int pntNum);

	void addCoordinate(int index, ICoordinate co);

	void appendCoordinate(ICoordinate co);

	boolean removeCoordinate(int index);

	boolean setDimension(int index, int dimension, double val);

	boolean setCoordinate(int index, ICoordinate co);
}
