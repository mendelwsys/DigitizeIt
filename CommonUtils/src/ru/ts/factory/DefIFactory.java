package ru.ts.factory;

import ru.ts.factory.IFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation cascading of template factory
 */
public class DefIFactory <T> implements IFactory<T>
{
	protected List<IFactory<T>> ff = new LinkedList<IFactory<T>>();

	/* (non-Javadoc)
     * @see ru.ts.factory.IFactory#addFactory(IFactory<T> tiFactory)
     */
	public void addFactory(IFactory<T> tiFactory)
	{
		ff.add(tiFactory);
	}

	/* (non-Javadoc)
     * @see ru.ts.factory.IFactory#createByTypeName(String typeObj)
     */
	public T createByTypeName(String typeObj) throws Exception
	{
		T rv = null;
		for (IFactory<T> iFactory : ff)
			if ((rv = iFactory.createByTypeName(typeObj))!=null)
				break;
		return rv;
	}
}
