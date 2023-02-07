package ru.ts.factory;

import ru.ts.xml.IXMLObjectDesc;

import java.util.List;

/**
 * Object Descriptor
 */
public interface IObjectDesc
{
	List<IParam> getParams();

	void setParams(List<IParam> params);

	String getObjname();

	void setObjname(String objname);

	String getClassname();

	void setClassname(String classname);

	IXMLObjectDesc getClassloader();

	void setClassloader(IXMLObjectDesc classloader);

	IParam getParamByName(String parname);
}
