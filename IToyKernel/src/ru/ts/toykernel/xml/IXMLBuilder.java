package ru.ts.toykernel.xml;

import ru.ts.factory.IInitAble;
import ru.ts.factory.IParam;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.xml.HandlerEx;

import java.util.Map;
import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

/**
 * XML builder interface
 */
public interface IXMLBuilder<T extends IInitAble>
{
	/**
	 * @return map of built types (objectName -> Object)
	 */
	Map<String, T> getT();

	/**
	 * @return list of built types
	 */
	List<T> getLT();

	/**
	 * @return tag name builder
	 */
	String getTagname();

	/**
	 * @return map of built types (objectName -> Object)
	 */
	Map<String, IInitAble> getInitables();

	/**
	 * clear of all initAbles objects
	 */
	void clearInitAbles();
	/**
	 * @return list pf parameters descriptors
	 */
	List<IXMLObjectDesc> getParamDescs();

	/**
	 * Allocate and init object by descriptor
	 * @param desc - descriptor for allocate
	 * @param attrs
	 * @return - allocated and inited object
	 * @throws Exception -
	 */
	IInitAble initByDescriptor(IXMLObjectDesc desc, List<IParam> attrs) throws Exception;

	/**
	 * get parse handler of section
	 * @param reader
	 * @param paramdesc
	 * @return
	 */
	HandlerEx getSectionHandler(XMLReader reader, IXMLObjectDesc paramdesc);

	HandlerEx getGroupHandler(XMLReader reader);

	/**
	 * Start init all object by parameters descriptors
	 * @throws SAXException -
	 */
	void initByDescriptors() throws SAXException;

	/**
	 * Получить дескриптор в виде xml (параметр xmls)
	 * @param desc - дескриптор по которому строится фрагмент xml
	 * @param xmls - tagname-><object_name>->xml
	 * @param tag2prior- tagname->приоритет
	 * @param prior - текущий приоритет (чем выше тем выше он в xml должен появится таг)
	 * @return - есть перекрестные ссылки или нет (есди есть тогда надо парсить с режимом postpone, т.е. с генерацией объектов после полного парсинга описателя)
	 * @throws Exception -
	 */
	boolean getXMLByDescriptor(IXMLObjectDesc desc, Map<String, Map<String, String>> xmls, Map<String, Integer> tag2prior, int prior) throws Exception;
}
