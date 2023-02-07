package ru.ts.toykernel.consts;

import ru.ts.toykernel.factory.BaseInitAble;

import java.util.List;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 14.03.2009
 * Time: 19:32:47
 * Base abstract implementations
 */
public abstract class DefBaseNameConverter extends BaseInitAble implements INameConverter
{
	protected List<INameConverter> ff = new LinkedList<INameConverter>();

	public void addNameConverter(INameConverter nmconverter)
	{
		ff.add(nmconverter);
	}
	public Object init(Object obj) throws Exception
	{
		return null;
	}
}
