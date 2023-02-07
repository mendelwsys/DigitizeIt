/*
 * Created on 15.08.2007
 *
 */
package ru.ts.gisutils.tc;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;
import ru.ts.gisutils.geometry.Rect;

/**
 * @author yugl
 *
 * Interface to get vertices coordinates by their indices
 */
public interface IGetVertices {
	
	/**
	 * возвращает количество вершин
	 */
	public int size ();

	/**
	 * возвращает прямоугольник, содержащий полилинию.
	 */
	public Rect rect ();
	/**
	 * копирует прямоугольник, содержащий полилинию.
	 */
	public void copyRect (Rect rect);

	/**
	 * возвращает координаты вершины
	 * @param index - индекс вершины
	 */
	public IGetXY getXY (int index);
	/**
	 * копирует координаты вершины
	 * @param index - индекс вершины
	 */
	public void copyXY (int index, IXY xy);

}
