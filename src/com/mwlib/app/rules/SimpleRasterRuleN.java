package com.mwlib.app.rules;

import com.mwlib.app.painters.RasterPainterN;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.IPainter;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.geom.IBaseGisObject;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 05.04.14
 * Time: 19:04
 * Правило рисования (!!!SINGLE THREAD!!!) com.mwlib.app.rules.SimpleRasterRuleN
 */
public class SimpleRasterRuleN extends BaseInitAble implements IDrawObjRule
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

    private RasterPainterN rasterPainterN = new RasterPainterN();
	public IPainter cretatePainter(Graphics g, ILayer layer, IBaseGisObject obj) throws Exception
	{
        return rasterPainterN;
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
