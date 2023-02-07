package ru.ts.factory;

/**
 * IInitAble interface for initalization convenience by java reflections
 */
public interface IInitAble
{
	/**
	 * @return object name for late binding
	 */
	String getObjName();

	/**
	 * Init class wtih objs arguments
	 * @param objs - objects argument
	 * @throws Exception - error initalization
	 * @return Array of old objects values (if supported)
	 */
	Object[] init(Object ...objs)
			throws Exception;

	/**
	 * init object by parameter
	 * @param obj - paramter object
	 * @return - old paramter that was set (if supported)
	 * @throws Exception -
	 */
	Object init(Object obj) throws Exception;

	/**
	 * @return get descriptor of initable object
	 */
	IObjectDesc getObjectDescriptor();
}
