package ru.ts.toykernel.drawcomp.layers.def.xml;

import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.xml.HandlerEx;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.lang.reflect.Constructor;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Layer builder from xml
 */
public class XMLLayerBuilder_OLD
{
	public static List<ILayer> getLLayers()
	{
		List<ILayer> rv=new LinkedList<ILayer>();
		rv.addAll(layers.values());
		return rv;
	}

	public static Map<String, ILayer> getLayers()
	{
		return layers;
	}

	static protected Map<String, ILayer> layers= new HashMap<String, ILayer>();

	static protected Map<String, INodeStorage> storages;
	static protected Map<String, IBaseFilter> filters;
	static protected Map<String, IDrawObjRule> rules;

	static Map<String, LayersParamDescriptor> layersdesc = new HashMap<String, LayersParamDescriptor>();
	static class LayersParamDescriptor
	{
		String layername ="";
		String classname="";
		Map<String,IDefAttr> params=new HashMap<String,IDefAttr>();
	}

	public static ILayer initByDesctriptor(LayersParamDescriptor desc) throws Exception
	{
		Class metaclass = ClassLoader.getSystemClassLoader().loadClass(desc.classname);
		Constructor[] constr=metaclass.getConstructors();

		Object[] pars=new Object[desc.params.size()+1];
		int i = 1;
		for (IDefAttr attr : desc.params.values())
			pars[i++]=attr;
		pars[0]=new DefAttrImpl(KernelConst.LAYER_NAME,desc.layername);
		for (Constructor constructor : constr)
		{
			if (constructor.getParameterTypes().length==0)
			{
				ILayer rv= (ILayer) constructor.newInstance();
				rv.init(pars);
				return rv;
			}
		}
		throw new UnsupportedOperationException("Can't find constructor with no params");
	}

	public static class CurrentHandler extends HandlerEx
	{
		LayersParamDescriptor desc=null;
		String qName;
		int indexparam=0;
		public CurrentHandler(XMLReader reader,Map<String, INodeStorage> storages,Map<String, IBaseFilter> filters,Map<String, IDrawObjRule> rules)
		{
			super(reader);
			XMLLayerBuilder_OLD.storages = storages;
			XMLLayerBuilder_OLD.filters = filters;
			XMLLayerBuilder_OLD.rules = rules;
		}
		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			this.qName=qName;
			if (qName.equalsIgnoreCase("layer"))
				desc=new LayersParamDescriptor();
			else if (qName.equalsIgnoreCase("layer-visible"))
				desc.params.put(KernelConst.LAYER_VISIBLE,new DefAttrImpl(KernelConst.LAYER_VISIBLE,""));
			else if (qName.equalsIgnoreCase("storage") || qName.equalsIgnoreCase("filter") || qName.equalsIgnoreCase("rule"))
				desc.params.put(qName,new DefAttrImpl(qName,""));
			else if (qName.equalsIgnoreCase("param"))
			{
				String nm=attributes.getValue("Nm");
				if (nm==null)
					nm=String.valueOf(indexparam++);
				String val=attributes.getValue("Val");
				desc.params.put(nm,new DefAttrImpl(nm,val));
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (qName.equalsIgnoreCase("layer"))
			{
				//Размещение хранилища по полученному дескритору
				layersdesc.put(desc.layername,desc);
			}
			else if (qName.equalsIgnoreCase("storage"))
			{
				IDefAttr iDefAttr = desc.params.get(qName);
				String storname = (String) (iDefAttr.getValue());
				INodeStorage stor = storages.get(storname);
				if (stor==null)
					throw new SAXException("Storage not defined :"+storname);
				iDefAttr.setValue(stor); //Заменяем имя объекта его инстанцем если находим его в соответсвующем массиве
			}
			else if (qName.equalsIgnoreCase("filter"))
			{
				IDefAttr iDefAttr = desc.params.get(qName);
				String filtername = (String) (iDefAttr.getValue());
				IBaseFilter filter = filters.get(filtername);
				if (filter==null)
					throw new SAXException("Filter not defined :"+filtername);
				iDefAttr.setValue(filter); //Заменяем имя объекта его инстанцем если находим его в соответсвующем массиве
			}
			else if (qName.equalsIgnoreCase("rule"))
			{
				IDefAttr iDefAttr = desc.params.get(qName);
				String rulename = (String) (iDefAttr.getValue());
				IDrawObjRule rule = rules.get(rulename);
				if (rule==null)
					throw new SAXException("Rule not defined :"+rule);
				iDefAttr.setValue(rule); //Заменяем имя объекта его инстанцем если находим его в соответсвующем массиве
			}
			else
				if (qName.equalsIgnoreCase("layers"))
				{
					for (String storname : layersdesc.keySet())
					{
						try
						{
							layers.put(storname,initByDesctriptor(layersdesc.get(storname)));
						} catch (Exception e)
						{
							e.printStackTrace();
							throw new SAXException("Error init of storage");
						}
					}
					ret2callerHadnler();
				}

		}

		public void characters(char[] ch, int start, int length)
		{
			String s = new String(ch, start, length).trim();
			if (qName!=null && s!=null && s.length()>0)
			{
				if (qName.equalsIgnoreCase("class-name"))
					desc.classname+= s;
				else if (qName.equalsIgnoreCase("layer-visible"))
				{
					IDefAttr iDefAttr = desc.params.get(KernelConst.LAYER_VISIBLE);
					String s1 = (String) (iDefAttr.getValue());
					s1+=s;
					iDefAttr.setValue(s1);
				}
				else if (qName.equalsIgnoreCase("storage") || qName.equalsIgnoreCase("filter") || qName.equalsIgnoreCase("rule"))
				{
					IDefAttr iDefAttr = desc.params.get(qName);
					String s1 = (String) (iDefAttr.getValue());
					s1+=s;
					iDefAttr.setValue(s1);
				}
				else if (qName.equalsIgnoreCase("layer-name"))
					desc.layername +=s;
			}
		}


	}

}
