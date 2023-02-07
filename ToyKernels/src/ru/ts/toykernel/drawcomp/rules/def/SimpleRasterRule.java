package ru.ts.toykernel.drawcomp.rules.def;

import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.IPainter;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.painters.def.DefRasterPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.converters.ILinearConverter;

import java.awt.*;

/**
 * Simple raster rule
 */
public class SimpleRasterRule extends BaseInitAble implements IDrawObjRule
{
	protected IDrawObjRule interceptor;

	public static final String RULETYPENAME ="R_RL";

	public void resetPainters()
	{
	}

	public IDrawObjRule setInterceptor(IDrawObjRule interseptor)
	{
		IDrawObjRule rv = this.interceptor;
		this.interceptor=interseptor;
		return rv;
	}

	public IDrawObjRule getInterceptor()
	{
		return interceptor;
	}

	public IPainter cretatePainter(Graphics g, ILayer layer, IBaseGisObject obj) throws Exception
	{
		return new DefRasterPainter();
	}

	public boolean isVisibleLayer(ILayer lr, ILinearConverter converter)
	{
		return true;
	}

	public String getRuleType()
	{
		return RULETYPENAME;

	}

	public Object init(Object obj) throws Exception
	{
		return null;
	}
}
