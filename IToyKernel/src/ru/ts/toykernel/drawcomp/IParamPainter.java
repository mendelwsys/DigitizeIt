package ru.ts.toykernel.drawcomp;

import ru.ts.factory.IInitAble;
import ru.ts.toykernel.consts.INameConverter;

import java.awt.*;

/**
 * Интерфейс конкретизирует параметры рисования
 * Painter with common paramters
 */
public interface IParamPainter extends IPainter,
		IInitAble
{
	/**
	 * @return color of fill closed area
	 */
	Paint getPaintFill();

	/**
	 * set colo fill
	 * @param colorfill - color fill
	 */
	void setPaintFill(Paint colorfill);

	/**
	 * @return color line (bound color of closed area)
	 */
	Color getColorLine();

	/**
	 * set color line
	 * @param colorLine line color (bound color of closed area)
	 */
    void setColorLine(Color colorLine);

	/**
	 * @return get style of objects
	 */
	Stroke getStroke();
	/**
	 * set style  of objects
	 * @param stroke - stroke for drawing object
	 */
	void setStroke(Stroke stroke);

	/**
	 * @return  size of point object on map
	 */
	int getSizePnt();

	/**
	 * set size of point objects
	 * @param sizePnt -size of point objects
	 */
	void setSizePnt(int sizePnt);

	/**
	 * @return Является ли масштабиремой толщина линии
	 */
	boolean isScaledThickness();

	/**
	 * Установить что толщина линиии масштабируема
	 * @param scaledThickness - флаг показывающий что толшина линии масштабируема
	 */
	void setScaledThickness(boolean scaledThickness);


	void setComposite(Integer composite);

	Integer getComposite();

	INameConverter getNameConverter();

	void setNameConverter(INameConverter nameConverter);


}