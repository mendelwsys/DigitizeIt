package ru.ts.toykernel.raster.providers;

import ru.ts.utils.data.Pair;
import ru.ts.factory.IInitAble;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


/**
 * Еще одна попытка построить провайдер растров
 *
 * Инициализация производится передачей конвертора в случае если имеется
 * несколько провайдеров
 */
public interface IRPRovider extends IInitAble
{
	public static final String URL_BASE = "urlBase";
	/**
	 * Получить параметры растра
	 *
	 * @param dXdY	  - размер ячейки растра (возврат) (
	 * для динамичски сгенерированных растров возврат -1,-1, для статических растров размер прямоугольника куда
	 * надо вписывать каждый кусок растра)
	 * @param szXszY  - размер всего растра (возврат, смысл одинаков для любой политики растров, размер растрового поля по
	 * которому перемещается ViewPort а именно координаты начальной точки растра x,y и конечной точки растра x,y (в т.ч и отридцательная))
	 * @param nXnY	  - количество объектов растров (возврат,-1,-1 для динамически сгенерированных растров)
	 * @throws Exception - ошибка запроса параметров растровой информации
	 */
	void getRasterParameters(double[] dXdY, double[] szXszY, int[] nXnY) throws Exception;

	Pair<BufferedImage, String> getRawRasterByImgIndex(int[] imgindex, Map<String, String[]> args) throws Exception;

	/**
	 * Запрос куска изображения растра
	 *
	 * @param iXiY  - индекс растрового изображения
	 * @param args
	 * @return пара <Изображение растра,название растра>
	 * @throws Exception -
	 */
	Pair<BufferedImage, String> getRawRaster(int[] iXiY, Map<String, String[]> args) throws Exception;


	Pair<InputStream,String> getStreamRawRaster(int[] iXiY, Map<String, String[]> args) throws Exception;

	/**
	 * поместить растр в переданный поток
	 * @param iXiY  - индекс растрового изображения
	 * @param os - выходной поток
	 * @param args
	 * @return - пара <Boolean,String> - (было ли загружено чего-то в поток, строка имя файла)
	 * @throws Exception -
	 */
	Pair<Boolean,String> getStreamRawRaster(int[] iXiY, OutputStream os, Map<String, String[]> args) throws Exception;

	/**
	 *
	 * @param iXiY - индекс изображения (для динамически сгенерированых растров передаются координаты и размер ViewPort(a))
	 * @return отдать индекс растра по индексу изображения (для динамически сгенерированых растров номер мозаики,передаются координаты и размер ViewPort(a)))
	 * @throws Exception -
	 */
	int[] getObjIndexByIndexImage(int[] iXiY) throws Exception;
	/**
	 * Отдать скрипт инициализации (для браузера или толстого клиента)
	 * @param dXdY	  - размер ячейки растра (
	 * для динамичски сгенерированных растров возврат -1,-1, для статических растров размер прямоугольника куда
	 * надо вписывать каждый кусок растра)
	 * @param szXszY  - размер всего растра (смысл одинаков для любой политики растров, размер растрового поля по
	 * которому перемещается ViewPort а именно координаты начальной точки растра x,y и конечной точки растра x,y (в т.ч и отридцательная))
	 * @param nXnY	  - количество объектов растров (-1,-1 для динамически сгенерированных растров)
	 * @param lrindex - номер слоя
	 * @param rCenter-
	 * @param rSize -
	 * @return - скрипт инициализации
	 * @throws Exception -
	 */
	 String getInitScript(double[] dXdY, double[] szXszY, int[] nXnY, int lrindex, Point rCenter, Point rSize) throws Exception;

	/**
	 * Отдать диапазон масштабов
	 * @return -
	 */
	public double[] getScaleRange();

	/**
	 * Получить строковое представление урла  указывающего на изображение
	 *
	 * @param iXiY - индекс растрового изображения (для динамически сгенерированых растров передаются координаты и размер ViewPort(a))
	 * @return Строковое представление URL(a) указывающего на изображение
	 * @throws Exception -
	 */
//	String getUrlImageRequest(int[] iXiY) throws Exception;



}
