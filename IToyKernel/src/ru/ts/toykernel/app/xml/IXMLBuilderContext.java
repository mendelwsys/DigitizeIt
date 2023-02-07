package ru.ts.toykernel.app.xml;

import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.xml.HandlerEx;
import ru.ts.utils.data.Pair;
import org.xml.sax.XMLReader;

import java.util.Collection;

/**
 * Builder context
 */
public interface IXMLBuilderContext
{

	Collection<String> getTagNames();

	IXMLBuilder getBuilderByTagSName(String tagSName);

	IXMLBuilder getBuilderByTagName(String tagName);

	HandlerEx getHandlerByTagName(String tagName, XMLReader reader, IXMLObjectDesc descriptor);

	/**
	 *
	 * @param desc - описатель дескриптора
	 * @return - <флаг- цикл присутствует, XML описание переданного дескриптора, что бы его можно было собрать на клиенте>
	 * @throws Exception -
	 */
	Pair<Boolean,String> getFullXML(IXMLObjectDesc desc) throws Exception;

	/**
	 * Вернуть XML описатель в виде строки
	 * @param encoding - какое кодирование вписать в заголовок
	 * @param byDescriptors
	 * @return - XML описатель
	 * @throws Exception -
	 */
	String getFullXML(String encoding, boolean byDescriptors)  throws Exception;

	/**
	 * @return next object Id in the context
	 */
	int  getNextObjectId();

	void getXML(IXMLObjectDesc desc, Pair<Boolean, String> iscycle2rv)
			throws Exception;

	String getXML(String prefix, boolean byDescriptors)
					throws Exception;
}
