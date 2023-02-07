package ru.ts.toykernel.filters.stream;

import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.factory.DefIFactory;

/**
 * factory of filter by node Id
 */
public class NodeFilterFactory extends DefIFactory<IBaseFilter>
{
	//Filter factory constanst
	public IBaseFilter createByTypeName(String filterType) throws Exception
	{
		if (filterType.equalsIgnoreCase(NodeFilter.TYPENAME))
			return new NodeFilter();
		else if (filterType.equalsIgnoreCase(NodeFilter2.TYPENAME))
			return new NodeFilter2();
		return super.createByTypeName(filterType);
	}

}
