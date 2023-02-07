package ru.ts.toykernel.drawcomp;

import ru.ts.gisutils.algs.common.MRect;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.factory.IInitAble;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.IProjConverter;

import java.util.List;
import java.util.Iterator;
import java.awt.*;


/**
 * Слой объектов представляет собой группу обьектов запрошенных у хранилища через интерфейс IBaseStorage с помощью фильтра IBaseFilter
 * эта группа рисуется по определенным правилам рисования IDrawObjRule
 * ILayer interface
 */
public interface ILayer extends IInitAble
{
	/**
	 * @return draw rule of layer
	 */
	IDrawObjRule getDrawRule();

	/**
	 * set draw rule of layer
	 * @param drawRule draw rule of layer
	 */
	void setDrawRule(IDrawObjRule drawRule);

	/**
	 * get layer attributes
	 * @return return layer attributes
	 */
	IAttrs getLrAttrs();

	/**
	 * paint layer
	 * @param graphics - graphical context
	 * @param viewPort - view port for drawing
	 * @return - triplet of drawing time (test proposal)
	 * @throws Exception - exception when error of painting
	 */
	int[] paintLayer(Graphics graphics, IViewPort viewPort) throws Exception;

	/**
	 * get bound box of objects in layer
	 * @param proj_rect - external project rect
	 * @return - bound box which includes external bound box (if not null) and all objects in layer
	 * @throws Exception -
	 */
	MRect getMBBLayer(MRect proj_rect) throws Exception;

	/**
	 * @return - return filtered layer storage
	 */
	IBaseStorage getStorage();

	/**
	 * set storage of the layer
	 * @param storage - storage of layer
	 */
	void setBaseStorage(IBaseStorage storage);

	/**
	 * @return storage filter of the layer
	 */
	List<IBaseFilter> getFilters();

	public void addFilters(List<IBaseFilter> filters) throws Exception;

	/**
	 * set filter of layer
	 * @param filter -filter of layer
	 * @throws Exception - error setting filter
	 */
	void setFilters(List<IBaseFilter> filter) throws Exception;

	void setLrAttrs(IAttrs attrs);

	void initLayer(IBaseStorage basestorage, List<IBaseFilter> filters, IAttrs attrs, IDrawObjRule drawObjRule)
			throws Exception;

	/**
	 * Прорисовать один объект слоя
	 * @param drawMe - объект слоя
	 * @param graphics - куда рисовать
	 * @param viewPort - порт видимости
	 * @return мно-во параметров для профайлинга
	 * @throws Exception -
	 */
	int[] paintLayerObject(IBaseGisObject drawMe,
			Graphics graphics, IViewPort viewPort) throws Exception;

	Iterator<IBaseGisObject> getVisibleObjects(Graphics graphics,IViewPort viewPort)
			throws Exception;

	/**
	 * Получить обрамляющий прямоугольник объекта в области рисования
	 * @param drawMe - объект слоя
	 * @param graphics - куда рисовать
	 * @param viewPort - порт видимости
	 * @return - обрамляющий прямоугольник объекта в области рисования
	 * @throws Exception -
	 */
	MRect getObjectDrawRect(IBaseGisObject drawMe,Graphics graphics, IViewPort viewPort) throws Exception;

	/**
	 * @return отдать аттрибут видимости слоя
	 */
	boolean isVisible();

	void setVisible(Boolean visible);

	Shape setBounds(Graphics graphics, IProjConverter iProjConverter)
			throws Exception;
}
