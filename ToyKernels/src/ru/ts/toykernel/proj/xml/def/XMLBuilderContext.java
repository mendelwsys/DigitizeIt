package ru.ts.toykernel.proj.xml.def;

import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.xml.def.XMLBuilder;
import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.IMetaInfoBean;
import ru.ts.toykernel.trans.IInitAbleTransformer;
import ru.ts.xml.HandlerEx;
import ru.ts.factory.IInitAble;
import ru.ts.toykernel.converters.IIntableConverter;
import ru.ts.toykernel.converters.providers.IConvProvider;
import ru.ts.toykernel.servapp.IServProject;
import ru.ts.toykernel.proj.IConfigProvider;
import ru.ts.toykernel.raster.providers.IRPRovider;
import ru.ts.toykernel.loaders.UrlLoader;
import ru.ts.toykernel.plugins.IModule;
import ru.ts.utils.data.Pair;
import org.xml.sax.XMLReader;

import java.util.*;

/**
 * Контекст построителя XML,
 * содержит построители для различных объектов проекта из описателя на XML
 * процесс непосредственного строительства может быть отложен, и вызван явно позже
 * это может быть использовано для того что бы в рамках одного описателя можно было бы описывать
 * несколько проектов без их явного создания. Например: в одном описателе мы можем иметь как серверную
 * так и клиентскую часть, серверную часть мы строим сразу, клиентскую передаем удаленному клиенту для
 * последующего создания
 */
public class XMLBuilderContext implements IXMLBuilderContext
{
	private int counter = 0;
	private boolean ispostpone;

	public synchronized int getNextObjectId()
	{
		return ++counter;
	}

	Map<String, XMLBuilder<IInitAble>> mapbuilder = new HashMap<String, XMLBuilder<IInitAble>>();

	public XMLBuilderContext(boolean ispostpone)
	{
		this.ispostpone = ispostpone;
		XMLBuilder[] objarr =
				{
						new XMLBuilder<INodeStorage>(KernelConst.STORAGE_TAGNAME, this, ispostpone),
						new XMLBuilder<IIntableConverter>(KernelConst.CONVERTER_TAGNAME, this, ispostpone),
						new XMLBuilder<IBaseFilter>(KernelConst.FILTER_TAGNAME, this, ispostpone),
						new XMLBuilder<IDrawObjRule>(KernelConst.RULE_TAGNAME, this, ispostpone),
						new XMLBuilder<ILayer>(KernelConst.LAYER_TAGNAME, this, ispostpone),
						new XMLBuilder<IProjContext>(KernelConst.PROJCTXT_TAGNAME, this, ispostpone),
						new XMLBuilder<IViewControl>(KernelConst.VIEWCNTRL_TAGNAME, this, ispostpone),
						new XMLBuilder<IApplication>(KernelConst.APPLICATION_TAGNAME, this, ispostpone),
						new XMLBuilder<IMetaInfoBean>(KernelConst.META_TAGNAME, this, ispostpone),
						new XMLBuilder<IInitAbleTransformer>(KernelConst.TRANSFORMER_TAGNAME, this, ispostpone),
						new XMLBuilder<INameConverter>(KernelConst.NAMECONVERTER_TAGNAME, this, ispostpone),

						new XMLBuilder<IConvProvider>(KernelConst.CONVPROVIDER_TAGNAME, this, ispostpone),
						new XMLBuilder<IRPRovider>(KernelConst.STORPROVIDER_TAGNAME, this, ispostpone),
						new XMLBuilder<IServProject>(KernelConst.SERVAPP_TAGNAME, this, ispostpone),
						new XMLBuilder<IConfigProvider>(KernelConst.CONFPROVIDERS_TAGNAME, this, ispostpone),
						new XMLBuilder<UrlLoader>(KernelConst.CLASSLOADER_TAGNAME, this, ispostpone),
						new XMLBuilder<IModule>(KernelConst.PLUGIN_TAGNAME, this, ispostpone),
						new XMLBuilder<IModule>(KernelConst.CMDPROVIDER_TAGNAME, this, ispostpone),

						new XMLBuilder<IInitAble>(KernelConst.APPBUILDER_TAGNAME, this, ispostpone),
						new XMLBuilder<IInitAble>(KernelConst.DIR_TAGNAME, this, ispostpone),

				};

		for (XMLBuilder<IInitAble> builder : objarr)
			mapbuilder.put(builder.getTagname().toLowerCase(), builder);
	}


	public Collection<String> getTagNames()
	{
		return mapbuilder.keySet();
	}

	public IXMLBuilder getBuilderByTagSName(String tagSName)
	{
		if (tagSName.toLowerCase().endsWith("s"))
		{
			//return mapbuilder.get(tagSName.toLowerCase().substring(0,tagSName.length()-1));
			return getBuilderByTagName(tagSName.toLowerCase().substring(0, tagSName.length() - 1));
		}
		return null;
	}

	public IXMLBuilder getBuilderByTagName(String tagName)
	{
		XMLBuilder<IInitAble> builder = mapbuilder.get(tagName.toLowerCase());
		if (builder == null)
		{
			mapbuilder.put(tagName, builder = new XMLBuilder<IInitAble>(tagName, this, ispostpone));
			System.out.println("Allocate builder for tagName = " + tagName);
		}
		return builder;
	}


	public Pair<Boolean, String> getFullXML(IXMLObjectDesc desc) throws Exception
	{
		Pair<Boolean, String> iscycle2rv = new Pair<Boolean, String>(false, "<?xml version=\"1.0\" encoding=\"WINDOWS-1251\" ?>\n" +
				"<project>\n");

		getXML(desc, iscycle2rv);

		iscycle2rv.second += "</project>\n";

		return iscycle2rv;
	}

	public void getXML(IXMLObjectDesc desc, Pair<Boolean, String> iscycle2rv)
			throws Exception
	{
		Map<String, Map<String, String>> xmls = new HashMap<String, Map<String, String>>();
		Map<String, Integer> tag2prior = new HashMap<String, Integer>();

		IXMLBuilder builder = getBuilderByTagName(desc.getTagname());

		iscycle2rv.first = builder.getXMLByDescriptor(desc, xmls, tag2prior, 0);

		Map<Integer, List<String>> prior2tag = new TreeMap<Integer, List<String>>();

		for (String tagname : tag2prior.keySet())
		{
			int prior = tag2prior.get(tagname);
			List<String> taglst = prior2tag.get(-prior);
			if (taglst == null)
				prior2tag.put(-prior, taglst = new LinkedList<String>());
			taglst.add(tagname);
		}

		for (Integer prior : prior2tag.keySet())
		{
			List<String> tagnames = prior2tag.get(prior);
			for (String tagname : tagnames)
			{
				iscycle2rv.second += "<" + tagname + "s>\n";
				Map<String, String> val = xmls.get(tagname);
				for (String xml : val.values())
					iscycle2rv.second += xml;
				iscycle2rv.second += "</" + tagname + "s>\n";
			}
		}
	}

	public String getFullXML(String encoding, boolean byDescriptors) throws Exception
	{
		String rv = "<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>\n" +
				"<project>\n";

		rv = getXML(rv, byDescriptors);

		rv += "</project>\n";
		return rv;
	}

	public String getXML(String prefix, boolean byDescriptors)
			throws Exception
	{
		Map<Integer, IXMLObjectDesc> treemap = new TreeMap<Integer, IXMLObjectDesc>();


		for (XMLBuilder<IInitAble> builder : mapbuilder.values())
		{
			if (!byDescriptors)
			{

				Map<String, IInitAble> ibls = builder.getInitables();
				for (IInitAble initAble : ibls.values())
				{
					IXMLObjectDesc desc = (IXMLObjectDesc) initAble.getObjectDescriptor();
					if (desc != null)
					{
						if (!desc.getObjname().equals(KernelConst.SYSTEMNAME))
                        {
                            int key = desc.getInitOrder() * 1000;
                            while (treemap.get(key)!=null)
                                key++;
//                            if (key!=null)
//                                throw new Exception("can't generate init order key");
                            treemap.put(key, desc);//Системные дескрипторы не описываем явно
                        }
					}
					else
						throw new Exception("Object descriptor is null");
				}
			}
			else
			{
				int ix = 0;
				List<IXMLObjectDesc> descs = builder.getParamDescs();
				for (IXMLObjectDesc desc : descs)
				{
					if (!desc.getObjname().equals(KernelConst.SYSTEMNAME))
					{
						int order = desc.getInitOrder();
						if (ix < order)
							ix = 10 * order;
						if (treemap.containsKey(order))
						{
							while(treemap.containsKey(ix))
								ix++;
							treemap.put(ix, desc);
							ix++;
						}
						else
							treemap.put(order, desc);
					}
				}
			}
		}

		String currenttag = null;

		String _prefix = "\t";

		for (Integer initoder : treemap.keySet())
		{
			IXMLObjectDesc desc = treemap.get(initoder);
			if (!desc.getTagname().equals(currenttag))
			{
				if (currenttag != null)
					prefix += "</" + currenttag + "s>\n";
				prefix += "<" + (currenttag = desc.getTagname()) + "s>\n";
			}
			prefix += desc.getXMLDescriptor(_prefix);
		}

		if (currenttag != null)
			prefix += "</" + currenttag + "s>\n";
		return prefix;
	}

	public HandlerEx getHandlerByTagName(String tagName, XMLReader reader, IXMLObjectDesc descriptor)
	{
//		IXMLBuilder builder=mapbuilder.get(tagName.toLowerCase());

		IXMLBuilder builder = getBuilderByTagName(tagName.toLowerCase());
		if (builder != null)
			return builder.getSectionHandler(reader, descriptor);
		return null;
	}
}

