package ru.ts.toykernel.filters;


/**
 * Spesial filter for objects key
 */
public interface IKeyFilter extends IBaseFilter
{
	/**
	 * check given object
	 * @param key - gis object id
	 * @return true if accepted false otherwise
	 */
	boolean acceptObject(String key);


}
