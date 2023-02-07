package ru.ts.toykernel.converters;

import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.stream.ISerializer;

import java.util.List;
import java.awt.*;

/**
 * Linear converter
 * PointDst - целевая точка преобразования (Например точка экрана)
 * Point - точка проекта
 */
public interface ILinearConverter extends ISerializer
{
	/**
	 * @param converterchain - установить цепочку конвертеров
	 */
	public  void setConverterChain(List<ILinearConverter> converterchain);

	/**
	 * Ковертер может состоять из списка конвертеров более простого вида
	 * @return Список конвертеров которые лежат в основе данного
	 */
	public List<ILinearConverter> getConverterChain() throws Exception;

	/**
	 * Создать копию текущего конвертера
	 * @return converter copy
	 */
	ILinearConverter createCopyConverter();

	/**
	 * Converter type (name)
	 *
	 * @return converter type
	 */
	String getTypeConverter();

	/**
	 * Получить размеры сторон прямоугольника экрана исходя из прямоуголькника в начальных координатах
	 *
	 * @param rect - прямоугольник в начальной системе координат
	 * @return - размеры прямоугольника в координатах экрана охватывающего в данной проекции rect
	 */
	double[] getDstSzBySz(MRect rect);

	/**
	 * @param pnt - точка в целевых координатах
	 * @return тока в начальной системе координат
	 */
	MPoint getPointByDstPoint(MPoint pnt);

	MPoint getPointByDstPoint(Point pnt);

	MPoint getPointByDstPoint(Point.Double pnt);

	/**
	 * @param pt - точка в начальной системе координат
	 * @return точка в координатах рисования
	 */
	Point.Double getDstPointByPointD(MPoint pt);

	Point getDstPointByPoint(MPoint pt);

	/**
	 * Получить охватывающий прямоугольник в координатах рисования по прямоугольнику в начальной системе координат
	 *
	 * @param rect - прямоугольник в начальной системе координат
	 * @return прямоугольник рисования
	 */
	MRect getDstRectByRect(MRect rect);

	/**
	 * Перевести прямоугольником в координатах рисования -> охватывающий прямоугольник в начальной системе координат
	 * Т.е. все точки охваченые прямоугольником в системе рисования должны быть охвачены полученым прямоугольником в
	 * в начальной системе координат, понятно, что в полученая область может содержать не только указанные точки
	 *
	 * @param drawrect  - прямоугольник рисования
	 * @param wholeRect - весь прямоугольник в начальной системе координат, охватывающий все точки карты
	 * @return прямоугольник в начальной системе координат
	 */
	MRect getRectByDstRect(MRect drawrect, MRect wholeRect);

	/**
	 * @return Перегнать конвертер в Base64
	 * @throws Exception - ошибка при преобразовании
	 */
	byte[] getBase64Converter() throws Exception;

	/**
	 * Восстановить конвертер из Base64
	 * @param bbase64 - исходная строка
	 * @throws Exception - ошибка при преобразовании
	 */
	void initByBase64Point(byte[] bbase64) throws Exception;


}
