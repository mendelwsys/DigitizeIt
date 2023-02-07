package ru.ts.toykernel.raster;

import ru.ts.utils.data.Pair;
import ru.ts.gisutils.algs.common.MPoint;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.List;

/**
 * Интерфейс обеспечивает получение растров с их координатами
 * для прорисовки на клиентской стороне
 */
public interface IClientRasterGetter
{
	/**
	 * @return вернуть размеры растровой подложки над которой расположено окно
	 * @throws Exception -
	 */
	MPoint getRasterSize()  throws Exception;

	/**
	 * Получить список изображений для прорисовки
	 * @param struct - структура привзяки окна к растру
	 * @return - список изображений и их положение
	 * @throws Exception -
	 */
	List<Pair<BufferedImage,MPoint>> getRasters(BindStructure struct) throws Exception;

	/**
	 * Увеличить масштаб в dscale раз
	 * @param struct - структура привязки
	 * @param dscale - коэффицент увеличения масштаба
	 * @throws Exception -
	 */
	void multScale(BindStructure struct,double dscale) throws Exception;
}
