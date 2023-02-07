package ru.ts.toykernel.geom;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.attrs.IDefAttr;

import java.util.Set;
import java.util.Map;

/**
 * GisObject with changing support
 */
public interface IEditableGisObject extends IGisObject
{
	//Индекс точки сквозной от 0 до кол-ва точек входящих в объект

	/**
	 * Явно перестроить объем объекта
	 *
	 */
	void rebuildGisValume();


	/**
	 * Удалить точку по переданному индексу
	 * @param index - точка по индексу
	 * @throws Exception -
	 */
	void removePoint(int index) throws Exception;

	/**
	 * Добавить ко всем координатам переданные значения x и y
	 * @param addpnt - переданные значения x и y
	 * @throws Exception-
	 */
	void add2AllCoordinates(MPoint addpnt)  throws Exception;

	/**
	 * Добавить всем точкам сегмента
	 * @param segindex - индекс сегмента
	 * @param addpnt - точка которая добавляется
	 * @throws Exception -
	 */
	void add2SegCoordinates(int segindex,MPoint addpnt)  throws Exception;
	/**
	 * Добавить к координатам указанной точки переданные значения x и y
	 * @param index - индекс редактируемой точки
	 * @param addpnt -
	 * @throws Exception -
	 */
	void add2Point(int index, MPoint addpnt)  throws Exception;

	/**
	 * Добавить току к объекту (точка добавляется по переданному индексу)
	 * @param index - индекс объекта
	 * @param pntnew - точка для добавления
	 * @throws Exception -
	 */
	void addPoint(int index,MPoint pntnew)  throws Exception;


	/**
	 * Разделить криву по точке с переданным индексом
	 * @param index - идекс точки по которой разделяется кривая, при этом точка отходит к новому сегменту
	 */
	void splitCurveByPoint(int index);

	/**
	 * Слить сегменты кривой
	 * @param segindex1 - индекс сегмента в который происходит слияние
	 * @param segindex2 - индекс сегмента еоторый сливается с сегментом с индексом segindex1, при этом сегмент с индексом
	 * segindex2 удаляется
	 */
	void mergeSegments(int segindex1, int segindex2);

	/**
	 * Слить сегменты кривой
	 * @param segindex1 - индекс сегмента в который происходит слияние
	 * @param segindex2 - индекс сегмента еоторый сливается с сегментом с индексом segindex1, при этом сегмент с индексом
	 * segindex2 удаляется
	 * @param pntnew - точка добавляется в конец сегмента с индексом segindex1 перед слиянием сегмента
	 * @return - индекс вставленной точки
	 */
	int mergeSegments(int segindex1, int segindex2,
							  MPoint pntnew);

	/**
	 * Добавить сегмент
	 * @param segindex - индекс сегмента в который производится добавление
	 * @param index - индекс в сегменте для добавления
	 * @param points - точки для добавления
	 */
	void add2Segment(int segindex,int index,MPoint[] points);
	void add2Segment(int segindex, int index, double[] pX, double[] pY);


	/**
	 * Добавить сегмент в мултиточку, мултилинию, или мулти полигон
	 * @param segindex - индекс сегмента
	 * @param points - точки для добавления
	 */
	void addSegment(int segindex,MPoint[] points);
	void addSegment(int segindex, double[] pX, double[] pY);

	/**
	 * Удалить сегмент с указанным индексом
	 * @param segindex - индекс сегмента
	 */
	void removeSegment(int segindex);

	/**
	 * Установить идентифкатор объекта
	 * @param m_sCurveID - идентификатор объекта
	 */
	void setCurveId(String m_sCurveID);

	/**
	 * Установить аттрибут
	 * @param value - значение аттрибута
	 * @throws Exception -
	 */
	void setCurveAttr(IDefAttr value)
			throws Exception;

	/**
	 * Установить аттрибуты
	 * @param sattrs - аттрибуты объекта
	 * @throws Exception -
	 */
	public void setCurveAttrs(Map<String,IDefAttr> sattrs) throws Exception;

	/**
	 * Добавить аттрибуты из переданного объект
	 * @param curve- переданный объект
	 * @throws Exception -
	 */
	void addCurveAttrs(IBaseGisObject curve)
			throws Exception;

	/**
	 * @return Отдать идентифкатор родительского объекта используется для ссылки на редактируемый объект
	 * @throws Exception -
	 */
	String getParentId() throws Exception;

	/**
	 * Установить идентифкатор родительского объекта используется для ссылки на редактируемый объект
	 * @param parentId - родительский объект
	 * @throws Exception -
	 */
	void setParentId(String parentId) throws Exception;

	/**
	 * Установить тип объекта
	 * @param geotype - тип объекта в KernelConsts
	 */
	void setGeotype(String geotype);

	/**
	 * Установить точку с индексом index
	 * @param index - индекс точки (0 ..numberOfPoints()-1)
	 * @param pnt - координаты точки для установки
	 * @throws Exception -
	 */
	void setPoint(int index, MPoint pnt) throws Exception;

	/**
	 * @return изменилась ли геометрия
	 */
	boolean isChangeGeom();

	/**
	 * @return изменились ли аттрибуты
	 */
	boolean isChangeAttrs();
}
