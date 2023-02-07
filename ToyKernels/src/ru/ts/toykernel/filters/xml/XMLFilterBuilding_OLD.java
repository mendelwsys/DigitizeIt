package ru.ts.toykernel.filters.xml;

import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
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
 * Builder filter from XML
 */
public class XMLFilterBuilding_OLD
{
	public static Map<String, IBaseFilter> filters= new HashMap<String, IBaseFilter>();

	static Map<String, FilterParamDescriptor> filtersdesc = new HashMap<String, FilterParamDescriptor>();
	static class FilterParamDescriptor
	{
		String filtername ="";
		String classname="";
		List<IDefAttr> params=new LinkedList<IDefAttr>();
	}

	public static IBaseFilter initByDesctriptor(FilterParamDescriptor desc) throws Exception
	{
		Class metaclass = ClassLoader.getSystemClassLoader().loadClass(desc.classname);
		Constructor[] constr=metaclass.getConstructors();

		Object[] pars=new Object[desc.params.size()+1];
		for (int i = 1; i < pars.length; i++)
			pars[i]=desc.params.get(i-1);
		pars[0]=new DefAttrImpl("ObjName",desc.filtername);

		for (Constructor constructor : constr)
		{
			if (constructor.getParameterTypes().length==0)
			{
				IBaseFilter rv= (IBaseFilter) constructor.newInstance();
				rv.init(pars);
				return rv;
			}
		}
		throw new UnsupportedOperationException("Can't find constructor with no params");
	}

	public static class CurrentHandler extends HandlerEx
	{
		FilterParamDescriptor desc=null;
		String qName;
		public CurrentHandler(XMLReader reader)
		{
			super(reader);
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			this.qName=qName;
			if (qName.equalsIgnoreCase("filter-name"))
				desc=new FilterParamDescriptor();
			else if (qName.equalsIgnoreCase("param"))
			{
				String nm=attributes.getValue("Nm");
				if (nm==null)
					nm="";
				String val=attributes.getValue("Val");
				desc.params.add(new DefAttrImpl(nm,val));
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (qName.equalsIgnoreCase("filter"))
			{
				//Размещение хранилища по полученному дескритору
				filtersdesc.put(desc.filtername,desc);
			}
			else
				if (qName.equalsIgnoreCase("filters"))
				{
					for (String filtename : filtersdesc.keySet())
					{
						try
						{
							filters.put(filtename,initByDesctriptor(filtersdesc.get(filtename)));
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
				else if (qName.equalsIgnoreCase("filter-name"))
					desc.filtername +=s;
			}
		}


	}

}
