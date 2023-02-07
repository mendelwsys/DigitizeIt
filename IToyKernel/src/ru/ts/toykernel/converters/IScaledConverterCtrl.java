package ru.ts.toykernel.converters;

import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

/**
 * Контроллер масштаба
 */
public interface IScaledConverterCtrl
{
	/**
	 * Вычисление масштаба так что бы окно в начальных координатах умещалось в окне рисования
	 * @param proj_rect - окно в начальных координатах для которого вычисляется масштаб
	 * @param szXY - размер окна рисования в пикселях по x и по y
	 * @throws Exception - если не возможно вычислить масштаб
	 */
	void recalcScale(MRect proj_rect, int[] szXY) throws Exception;

	/**
	 * Увеличить карту на указанную величину
	 * @param dS - величина на которую меняется масштаб
	 * @return - результирующий масштаб
	 * @throws Exception -
	 */
	MPoint increaseMap(double dS) throws Exception;


	/**
	 * Увеличить карту на указанную величину
	 * @param dS - величина на которую меняется масштаб отдельно по x и по y
	 * @param evently
	 * @return - результирующий масштаб
	 * @throws Exception -
	 */
	MPoint increaseMap(MPoint dS, boolean evently) throws Exception;

	/**
	 * Уменьшить карту на указанную величину
	 * @param dS - величина на которую меняется масштаб\
 	 * @return - результирующий масштаб
	 */
	MPoint decreaseMap(double dS) throws Exception;

	/**
	 * Отдать мастшаб единицы/ пиксели
	 * @return масштаб  единицы/ пиксели
	 * @throws Exception -
	 */
	MPoint getUnitsOnPixel() throws Exception;

	void reSetInitScale();

	/**
	 * Установить масштаб
	 * @param newscale- новый масштаб
	 */
	void setScale(MPoint newscale)  throws Exception;

	/**
	 * @return отдать текущий масштаб
	 */
	MPoint getScale() throws Exception;

	public void setScaleBean(IScaleDescBean scalebean) throws Exception;

}
