package ru.ts.toykernel.geom;

import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.gisutils.algs.common.MRect;

/**
 * Базовый интерфейс для Гис объектов
 * гис объект - геометрия с аттрибутами
 * вообще говоря деление несколько условно, можно было бы его не проводить,
 * т.е. ввести одни аттрибуты или фичи (как в гео-tools) и потом определять
 * по типам что это геометрия или атрибутивная информация, но мне (vladm) показалось это удобно
 * Base gis object
 */
public interface IBaseGisObject
{
	/**
	 * get Attrs of objects
	 * @return Attrs of object
	 */
	IAttrs getObjAttrs();

	/**
	 * get maximal bounding box of object and input mbb
	 * @param boundrect - input mbb if null return mbb of object
	 * @return mbb
	 */
	MRect getMBB(MRect boundrect);

	/**
	 * @return geo type of obbject
	 */
	String getGeotype();

	/**
	 * [x,segmentNumber,Number in the Segment]
	 * @return get object geometry, size of array equals dimention {x,y,...}
	 */
	double[][][] getRawGeometry();

	/**
	 * @return id of object
	 */
	String getCurveId();

	/**
	 * @return object dimention
	 */
	int getDimensions();

	/**
	 * copy current object to parameter
	 * @param _curve - paramerter for copy current object
	 * @throws Exception -
	 */
	void setInstance(IBaseGisObject _curve) throws Exception;

}
