package ru.ts.toykernel.raster;

import ru.ts.utils.data.Pair;
import ru.ts.toykernel.converters.ILinearConverter;
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
public interface IRasterProvider
{
	/**
	 * Запрос индексов растров попадающих в область рисования окна
	 *
	 * @param converter - конвертор проекта (содержит в том числе точку привязки к окну и масштаб), поэтому что
	 *                  бы вычислить размеры попавших в окно растров необходимо занать только размеры окна
	 * @param drwWindos - размеры окна рисования по X и по Y
	 * @param ddXddY	- растяжение/сжатие растра по координатам X,Y
	 * @return список индексов растров  (началоX, конецX,началоY, конецY)
	 * @throws Exception -
	 */
	int[] getImagesIndices(ILinearConverter converter, int[] drwWindos, double ddXddY[]) throws Exception;


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
	 * @param converter -  конвертер проекта
	 * @param mp12	  - точки привязки растра в линейной области
	 * @param pt12	  - точки растра
	 * @param dXdY	  - размер ячейки растра
	 * @param szXszY	- размер всего растра
	 * @param nXnY	  - количество объектов растров
	 * @throws Exception - ошибка запроса параметров растровой информации
	 */
	void getRasterParamters(ILinearConverter converter,
							MPoint[] mp12, MPoint[] pt12,
							double[] dXdY, double[] szXszY, int[] nXnY) throws Exception;

	/**
	 * Привязать растр
	 *
	 * @param converter -  конвертер проекта
	 * @param mp12	  - точки привязки растра в линейной области
	 * @param pt12	  - точки растра
	 * @throws Exception -
	 */
	void bindRasterProvider(ILinearConverter converter, MPoint[] mp12, MPoint[] pt12) throws Exception;


	/**
	 * Получить точку рисования по точек растра
	 *
	 * @param point	 - Растровая точка
	 * @param converter - конвертер
	 * @return - точка прорисовки (экранная точка)
	 * @throws Exception -
	 */
	MPoint getDrawPointByRasterPoint(MPoint point, ILinearConverter converter) throws Exception;

	/**
	 * Получить точку растра по точке рисования
	 *
	 * @param point	 - точка рисования
	 * @param converter - конвертер
	 * @return точка растра
	 * @throws Exception -
	 */
	MPoint getRasterPointByDrawPoint(MPoint point, ILinearConverter converter
	) throws Exception;

	/**
	 * Получить строковое представление урла  указывающего на изображение
	 *
	 * @param nXnY - количество объектов растров
	 * @return Строковое представление URL(a) указывающего на изображение
	 * @throws Exception -
	 */
	String getUrlImageRequest(int[] nXnY) throws Exception;
}
