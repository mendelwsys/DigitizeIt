package ru.ts.toykernel.filters;

import ru.ts.factory.DefIFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 09.03.2009
 * Time: 15:07:20
 * Default MMB factory
 */
public class DefMBBFactory  extends DefIFactory<IBaseFilter>
{
	public IBaseFilter createByTypeName(String filterType) throws Exception
	{
		if (filterType.equalsIgnoreCase(DefMBBFilter.TYPENAME))
			return new DefMBBFilter();
		return super.createByTypeName(filterType);
	}
}
