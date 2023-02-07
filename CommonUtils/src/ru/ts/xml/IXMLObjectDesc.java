package ru.ts.xml;

import ru.ts.factory.IObjectDesc;


/**
 * XML обоусе
 */
public interface IXMLObjectDesc extends IObjectDesc
{

	String getTagname();

	String getXMLDescriptor(String prefix) throws Exception;

	int getInitOrder();

    void setInitOrder(int initOrder);
}
