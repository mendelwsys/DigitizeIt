package ru.ts.toykernel.geom;

import ru.ts.gisutils.proj.transform.ITransformer;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;

/**
 * Type represent GisObject
 *
 * Этот тип поддерживает более высокоуровневые операции работы с мультиобъектами,
 * такие как получении сегментов, геометрии объектов и сегментов, разбиение индекса точки на сегмент и индекс в сегменте и пр...
 *
 */
public interface IGisObject extends IBaseGisObject
{
	int getNearestIndexByPoint(MPoint pnt)   throws Exception;

	/**
	 * Получить индекс точки объекта ближайшей к переданному
	 * @param pnt - точка объекта
	 * @return - индекс точки
	 * @throws Exception -
	 */
	int getIndexByPoint(MPoint pnt)  throws Exception;

	/**
	 * @return  кол-во сегментов в объекте
	 */
	int getSegsNumbers();

	int getSegLength(int segindex);
	/**
	 * Отдать целиком сегмент объекта
	 * @param segindex - индекс сегмента
	 * @return - массив точек представляющий сегмент
	 */
	MPoint[] getSegmentById(int segindex);

	/**
	 * Transform object to other coordinate system
	 * @param transformer - coordinate transformer
	 * @param isdirect - direction of transformation
	 * @throws Exception - when error transform
	 */
	void transformIt(ITransformer transformer,boolean isdirect) throws Exception;

	/**
	 * @return Midle point of object
	 */
	MPoint getMidlePoint();
	/**
	 * @return get segments points
	 */
	MPoint[][] getGeometry();

	/**
	 * @param index - index of point
	 * @return coordinate of point
	 */
	MPoint getPoint(int index);

	/**
	 * @return number of points in object
	 */
	int numberOfPoints();

	/**
	 * Разбить индекс точки на два, первый из которых это индекс сегмента
	 * второй индекс в сегменте
	 * @param index - пордяковый номер точки начинающийся с 0
	 * @return пара <индекс сегмента, индекс внутри сегмента>
	 * @throws ArrayIndexOutOfBoundsException - в случае если кол-во точек < входного индекса
	 */
	Pair<Integer,Integer> splitIndex(int index);

	/**
	 * Получить по индексу представленным парой индекс точки
	 * @param index - пара <индекс сегмента, индекс внутри сегмента>
	 * @return - пордяковый номер точки начинающийся с 0
	 */
	int mergeIndex(Pair<Integer,Integer> index);

	/**
	 * Получить точку границы объекта ближайшую к переданной точке
	 * @param pnt - точка относительно которой определяются ближайшие точки границы
	 * @return - точка границы ближайшего к объекту
	 */
	MPoint getNearestBoundPointByPoint(MPoint pnt);
}
