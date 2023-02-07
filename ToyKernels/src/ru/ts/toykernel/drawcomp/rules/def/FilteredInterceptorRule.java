package ru.ts.toykernel.drawcomp.rules.def;

import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.IPainter;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.painters.def.DefEmptyPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;

import java.awt.*;

/**
 * Перехватчик рисования согласно установленому фильтру
 * Если объект не удовлетворяет фильтру тогда возвращаем пустой рисователь
 * Если удовлетоворяет дает рисовать правилу по умолчанию
 * ru.ts.toykernel.drawcomp.rules.def.FilteredInterceptorRule 
 */
public class FilteredInterceptorRule extends BaseInitAble implements IDrawObjRule
{
	private IDrawObjRule interceptor;
	private IBaseFilter filter;

	private IDrawObjRule defaultRule;

	public FilteredInterceptorRule(IBaseFilter filter, IDrawObjRule defaultRule)
	{
		this.filter = filter;
		this.defaultRule = defaultRule;
	}

	public FilteredInterceptorRule()
	{
		
	}

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

	public void setFilter(IBaseFilter filter)
	{
		this.filter = filter;
	}

	public void getFilter(IBaseFilter filter)
	{
		this.filter = filter;
	}

	public IPainter cretatePainter(Graphics g, ILayer layer, IBaseGisObject obj) throws Exception
	{
		if (interceptor!=null)
		{
			IPainter rv=interceptor.cretatePainter(g, layer, obj);
			if (rv!=null)
				return rv;
		}

		if (filter==null || !filter.acceptObject(obj))
		{
			if (defaultRule==null)
				return new DefEmptyPainter();
			return defaultRule.cretatePainter(g, layer, obj);
		}
		return null;
	}

	public boolean isVisibleLayer(ILayer lr, ILinearConverter converter)
	{
		return true;
	}

	public static final String RULETYPENAME ="FILTER_RL";

	public String getRuleType()
	{
		return RULETYPENAME;
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.FILTER_TAGNAME))
			filter= (IBaseFilter) attr.getValue();
		else
			if (attr.getName().equalsIgnoreCase(KernelConst.RULE_TAGNAME))
			defaultRule= (IDrawObjRule) attr.getValue();
		return null;
	}
}
