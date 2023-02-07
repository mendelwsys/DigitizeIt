package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.drawcomp.IPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MRect;

import java.awt.*;

/**
 * Пустой кульман для блокирования рисования некоторых объектов
 * во время перехвата в правиле фильтрованном правиле рисования
 */
public class DefEmptyPainter implements IPainter
{
	public int[] paint(Graphics g, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
	{
		return new int[]{0,0,0,0,0};
	}

	public MRect getRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		return new MRect();
	}

	public MRect getDrawRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		return new MRect();
	}

	public Shape createShape(IBaseGisObject drawMe, ILinearConverter converter) throws Exception
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
