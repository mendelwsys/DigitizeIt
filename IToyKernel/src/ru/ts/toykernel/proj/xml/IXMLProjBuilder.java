package ru.ts.toykernel.proj.xml;

import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.xml.HandlerEx;
import org.xml.sax.XMLReader;

/**
 * Project XML builder
 */
public interface IXMLProjBuilder
{
	IXMLBuilderContext getBuilderContext();

	HandlerEx getProjBuilderHandler(XMLReader reader);
}
