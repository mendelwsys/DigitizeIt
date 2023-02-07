package ru.ts.toykernel.shared;

import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.attrs.IDefAttr;

/**
 * PathObject
 * ru.ts.toykernel.shared.PathObject
 */
public class PathObject extends BaseInitAble
{
	protected String folderlayers;

	public String getFolderlayers()
	{
		return folderlayers;
	}
	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		this.folderlayers=((String)attr.getValue());
		return null;
	}
}
