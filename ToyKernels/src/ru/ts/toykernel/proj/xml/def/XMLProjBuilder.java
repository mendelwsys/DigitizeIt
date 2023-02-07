package ru.ts.toykernel.proj.xml.def;

import ru.ts.xml.HandlerEx;
import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.toykernel.xml.def.ParamDescriptor;
import ru.ts.toykernel.proj.xml.IXMLProjBuilder;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.factory.BaseInitAble;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;

/**
 * MetaInformation of project builder from XML descriptor
 * ru.ts.toykernel.proj.xml.def.XMLProjBuilder
 */
public class XMLProjBuilder  extends BaseInitAble implements IXMLProjBuilder
{
	protected IXMLBuilderContext builderContext;

	/**
	 * Конструктор по умолчанию, УСТАНАВЛИВАЕТ инстанцирование классов во время парсинга xml
	 */
	public XMLProjBuilder()
	{
		this(false);
	}

	/**
	 * Конструктор с индиктором инстанцирования классов во время парсинга xml
	 * @param ispostpone -индикатор того что надо отложить инстанцирование классов, во время их построения
	 */
	public XMLProjBuilder(boolean ispostpone)
	{
		builderContext = new XMLBuilderContext(ispostpone);

		IXMLBuilder bld = builderContext.getBuilderByTagName(KernelConst.APPBUILDER_TAGNAME);

		//Заполним контекст системными параметрами
		ObjName=KernelConst.SYSTEMNAME;
		desc = new ParamDescriptor(KernelConst.APPBUILDER_TAGNAME, ObjName, "ru.ts.toykernel.proj.xml.def.XMLProjBuilder", null, null, builderContext.getNextObjectId());


		bld.getParamDescs().add(desc);
		bld.getInitables().put(ObjName,this);

	}

	public IXMLBuilderContext getBuilderContext()
	{
		return builderContext;
	}

	public HandlerEx getProjBuilderHandler(XMLReader reader)
	{
		return new ProjHandler(reader);
	}

	public Object init(Object obj) throws Exception
	{
		return null;
	}

	public class ProjHandler extends HandlerEx
	{

		public ProjHandler(XMLReader reader)
		{
			super(reader);
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			IXMLBuilder builder = null;
			if ((builder = builderContext.getBuilderByTagSName(qName)) != null)
				builder.getGroupHandler(reader);
		}
	}

}