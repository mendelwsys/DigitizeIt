package ru.ts.toykernel.converters;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.converters.ILinearConverter;

/**
 * Shift Converter
 */
public interface IShiftConverter extends ILinearConverter
{

	public final String SHIFTCONVERTER="SHIFTCONVERTER";

	MPoint getBindP0()  throws Exception;
	MPoint setBindP0(MPoint currentP0) throws Exception;

	MPoint getViewSize() throws Exception;

	MPoint setViewSize(MPoint sz) throws Exception;
	/**
	 * Скоректировать точку привязки при перемешении картинки из точки drawpnt1 в точку drawpnt2
	 * @param dXdY - изменения в координатах по X и по Y
	 */
	void recalcBindPointByDrawDxDy(double[] dXdY)  throws Exception;
}
