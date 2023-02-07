package ru.ts.toykernel.attrs;

import ru.ts.factory.DefIFactory;
import ru.ts.factory.IFactory;
import ru.ts.toykernel.storages.IBaseStorage;

/**
 * Factory of object attributes
 */
abstract public class AObjAttrsFactory extends DefIFactory<IAttrs>
{

	/**
	 * create object attributes by object identifier (not use chain of factory)
	 * @param objId - gis object identifier
	 * @param storage - storage of gis object
	 * @param boundAttrs - default attributes of object
	 * @return - instance of object
	 * @throws Exception - error of allocate object
	 */
	abstract public IAttrs createLocaleByGisObjId(String objId, IBaseStorage storage,IAttrs boundAttrs) throws Exception;

	/**
	 * create object attributes by object identifier with default using discipline of factory chain
	 * @param objId - gis object identifier
	 * @param storage - storage of gis object
	 * @param boundAttrs - default attributes of object
	 * @return - instance of object
	 * @throws Exception - error of allocate object
	 */
	public IAttrs createByGisObjId(String objId,IBaseStorage storage,IAttrs boundAttrs) throws Exception
	{
		IAttrs rv = createLocaleByGisObjId(objId,storage, boundAttrs);
		if (rv==null)
			for (IFactory<IAttrs> iFactory : ff)
				if (iFactory instanceof AObjAttrsFactory && (rv = ((AObjAttrsFactory)iFactory).createByGisObjId(objId,storage,boundAttrs))!=null)
					break;
		return rv;
	}
}
