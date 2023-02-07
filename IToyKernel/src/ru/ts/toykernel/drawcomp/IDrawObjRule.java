package ru.ts.toykernel.drawcomp;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.factory.IInitAble;
import ru.ts.toykernel.converters.ILinearConverter;

import java.awt.*;

/**
 * Правила рисования ответственны за генерацию рисователей IPainter для передаваемого объекта, планируется что правила рисования
 * могут быть сгенерированы исходя из описания на каком либо языке (в том числе sld) для возможности их динамического (в run-time)
 * изменения, для достижения более подвижной картинки
 * Rule of layer drawing - layers can share same draw rule it can be useful for depend-drawing of layers
 */
public interface IDrawObjRule extends IInitAble
{

	/**
	 * reset internal state of painters
	 */
	void resetPainters();

	/**
	 * set draw rule interceptor
	 *
	 * @param interseptor - new interseptor if null, interseptor will be removed
	 * @return - old interceptor
	 */
	IDrawObjRule setInterceptor(IDrawObjRule interseptor);

	/**
	 * @return interceptor in the rule
	 */
	IDrawObjRule getInterceptor();

	/**
	 * Create painter for gis object
	 *
	 * @param g	 - graphical context
	 * @param layer - draw layer
	 * @param obj   - geometry with attributes @return - painter for object @throws Exception - if error occures while generating painter
	 * @return - painter rect
	 * @throws Exception -
	 */
	IPainter cretatePainter(Graphics g, ILayer layer, IBaseGisObject obj) throws Exception;

	/**
	 * check if the layer is visible
	 *
	 * @param lr		- layer for which the draw rule will apply
	 * @param converter - coordinates conveter @return - true if layer visible @return - true if layer is visible
	 * @return true id layer is visible
	 */
	boolean isVisibleLayer(ILayer lr, ILinearConverter converter);

	/**
	 * @return rule type
	 */
	String getRuleType();

}
