package ru.ts.toykernel.converters.providers;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.factory.IInitAble;
import ru.ts.toykernel.converters.IScaleDescBean;

/**
 * Конвертер провайдер
 */
public interface IConvProvider extends IInitAble
{
	/**
	 * Изменить масштаб конвертера
	 * @param mscale -умножитель масштаба
	 * @return - текущий масштаб
	 * @throws Exception -
	 */
	MPoint multScale(double mscale) throws Exception;


	MPoint multScale(MPoint mscale, boolean evently) throws Exception;

	/**
	 * Установка диапазаона масштабов
	 * @param scalebean
	 * @throws Exception -
	 */
	void setScaleRange(IScaleDescBean scalebean) throws Exception;

}
