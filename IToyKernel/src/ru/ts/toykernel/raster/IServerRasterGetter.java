package ru.ts.toykernel.raster;

import ru.ts.utils.data.Pair;
import ru.ts.gisutils.algs.common.MPoint;

import java.awt.image.BufferedImage;

/**
 * Raseter provider
 * <p/>
 * Растровый провайдер предназначен для обеспечения проекта растровой подложкой
 * <p/>
 * <p/>
 * Растровая подложка может сосотоять из одного или серии упорядоченных растров,
 * для разных масштабах разные серии (или нарезкии)
 */
public interface IServerRasterGetter
{
	/**
	 * Запрос индексов растров попадающих в область рисования окна
	 *
	 * @param bindstruct - структура привязки окна
	 * @param ddXddY	- растяжение/сжатие растра по координатам X,Y
	 * @return список индексов растров  (началоX, конецX,началоY, конецY)
	 * @throws Exception -
	 */
	int[] getImagesIndices(BindStructure bindstruct, double ddXddY[]) throws Exception;


	/**
	 * Запрос куска изображения растра
	 *
	 * @param nXnY - номер сегмента растра
	 * @return пара <Изображение растра,название растра>
	 * @throws Exception -
	 */
	Pair<BufferedImage, String> getImageRequest(int[] nXnY) throws Exception;

	/**
	 * Запросить данные  по привязке растра.
	 *
	 * @param bindstruct - структура привязки окна
	 * @param scalemult - умножитель масштаба
	 * @param dXdY	  - размер ячейки растра (возврат)
	 * @param szXszY	- размер всего растра (возврат)
	 * @param nXnY	  - количество объектов растров (возврат)
	 * @throws Exception - ошибка запроса параметров растровой информации
	 */
	void getRasterParamters(BindStructure bindstruct,double scalemult,
							double[] dXdY, double[] szXszY, int[] nXnY) throws Exception;

	/**
	 * Получить точку рисования по точек растра
	 *
	 * @param point	 - Растровая точка
	 * @param bindstruct - структура привязки окна
	 * @param ddXddY	- растяжение или сжатие растра по координатам X и Y
	 * @return - точка прорисовки (экранная точка)
	 * @throws Exception -
	 */
	MPoint getDrawPointByRasterPoint(MPoint point, BindStructure bindstruct, double ddXddY[]) throws Exception;

	/**
	 * Получить точку растра по точке рисования
	 *
	 * @param point	 - точка рисования
	 * @param bindstruct - структура привязки окна
	 * @param ddXddY	- растяжение или сжатие растра по координатам X и Y
	 * @return точка растра
	 * @throws Exception -
	 */
	MPoint getRasterPointByDrawPoint(MPoint point, BindStructure bindstruct,
									 double ddXddY[]) throws Exception;

	/**
	 * Получить строковое представление урла  указывающего на изображение
	 *
	 * @param nXnY - количество объектов растров
	 * @return Строковое представление URL(a) указывающего на изображение
	 * @throws Exception -
	 */
	String getUrlImageRequest(int[] nXnY) throws Exception;
}