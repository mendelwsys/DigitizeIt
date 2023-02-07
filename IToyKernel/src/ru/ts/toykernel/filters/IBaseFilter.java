package ru.ts.toykernel.filters;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.factory.IInitAble;

/**
 * Base object filter
 */
public interface IBaseFilter extends IInitAble
{
	/**
	 * check given object
	 * @param obj - gis object
	 * @return true if accepted false otherwise
	 * @throws Exception -
	 */
	boolean acceptObject(IBaseGisObject obj) throws Exception;

	/**
	 * @return get filter type name
	 */
	String getTypeName();
}
