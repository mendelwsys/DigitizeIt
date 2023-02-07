package ru.ts.factory;

import java.util.List;

/**
 * Parameter has name and value
 * type of parameter defines by java type of value
 *
 */
public interface IParam 
{

	/**
	 * get Attributes of the parameter
	 * @return list of attributes
	 */
	List<IParam> getAttributes();

	/**
	 * set attributes
	 * @param attrs - list pf attributes for setting
	 */
	void setAttributes(List<IParam> attrs);

	/**
	 * set value of attribute
	 * @param obj - attribute value
	 */
	void setValue(Object obj);

	/**
	 * get value of attribute
	 * @return attribute value
	 */
	Object getValue();

	/**
	 * get name of attribute
	 * @return name of attribute
	 */
	String getName();

	IParam getCopy();

}