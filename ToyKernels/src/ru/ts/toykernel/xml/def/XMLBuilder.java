package ru.ts.toykernel.xml.def;

import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.xml.HandlerEx;
import ru.ts.factory.IInitAble;
import ru.ts.factory.IParam;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * class builder
 */
public class XMLBuilder<T extends IInitAble> implements IXMLBuilder<T>
{
	/**
	 * @return map of initables
	 */
	public Map<String, T> getT()
	{
		Map<String, T> rv = new HashMap<String, T>();
		Map<String, IInitAble> initables = getInitables();
		for (String nodename : initables.keySet())
			rv.put(nodename, (T) initables.get(nodename));
		return rv;
	}

	/**
	 * @return inables as list in description order
	 */
	public List<T> getLT()
	{
		List<T> rv = new LinkedList<T>();

		List<IXMLObjectDesc> descriptor = getParamDescs();
		Map<String, IInitAble> initables = getInitables();
		for (IXMLObjectDesc initable : descriptor)
			rv.add((T) initables.get(initable.getObjname()));
		return rv;
	}


	public String getTagname()
	{
		return tagname;
	}

	protected String tagname;
	protected IXMLBuilderContext buildercontext;
	protected boolean postopone;//flag shows if postpone initialization of descriptor

	protected List<IXMLObjectDesc> paramsdesc = new LinkedList<IXMLObjectDesc>();
	protected Map<String, IInitAble> initables = new HashMap<String, IInitAble>();//Initables objects


	public XMLBuilder(String tagname, IXMLBuilderContext buildercontext, boolean postopone)
	{
		this.tagname = tagname;
		this.buildercontext = buildercontext;
		this.postopone = postopone;
	}


	public Map<String, IInitAble> getInitables()
	{
		return initables;
	}

	public void clearInitAbles()
	{
		initables.clear();
	}

	public List<IXMLObjectDesc> getParamDescs()
	{
		return paramsdesc;
	}


	protected boolean ischain = false;//Cycle indicator

	public boolean getXMLByDescriptor(IXMLObjectDesc desc, Map<String, Map<String, String>> xmls, Map<String, Integer> tag2prior, int prior) throws Exception
	{
		boolean rv = ischain;
		if (desc.getClassname() == null)
			for (IXMLObjectDesc localdesc : paramsdesc)//Попытаемя найти дескриптор в местных описателях
				if (localdesc.getObjname().equals(desc.getObjname()))
				{
					desc = localdesc;
					break;
				}


		Map<String, String> lxmls = xmls.get(desc.getTagname());
		if (lxmls == null)
			xmls.put(desc.getTagname(), lxmls = new HashMap<String, String>());
		if (lxmls.get(desc.getObjname()) != null)
			return rv;

		Integer l_prior = tag2prior.get(desc.getTagname());
		if (l_prior == null || l_prior < prior)
			tag2prior.put(desc.getTagname(), prior);

		String classname = null;
		if ((classname = desc.getClassname()) != null && classname.length() > 0)
		{
			IXMLObjectDesc classloaderdesc = desc.getClassloader();
			if (classloaderdesc != null)
			{
				IXMLBuilder builder = buildercontext.getBuilderByTagName(classloaderdesc.getTagname());
				if (builder != null)
				{
					ischain = !(classloaderdesc.getTagname().equals(desc.getTagname()));
					rv = rv || builder.getXMLByDescriptor(classloaderdesc, xmls, tag2prior, prior + 1);
					ischain = false;
				}
				else
					throw new Exception("Error of tag name:" + classloaderdesc.getTagname());
			}

			for (int i = 0; i < desc.getParams().size(); i++)
			{
				IParam iDefAttr = desc.getParams().get(i);
				if (iDefAttr.getValue() instanceof IXMLObjectDesc)
				{
					ParamDescriptor descriptor = (ParamDescriptor) iDefAttr.getValue();
					IXMLBuilder builder = buildercontext.getBuilderByTagName(descriptor.getTagname());
					if (builder != null)
					{
						ischain = !(descriptor.getTagname().equals(desc.getTagname()));
						rv = rv || builder.getXMLByDescriptor(descriptor, xmls, tag2prior, prior + 1);
						ischain = false;
					}
					else
						throw new Exception("Error of tag name:" + descriptor.getTagname());
				}
			}

			String xmlstr = desc.getXMLDescriptor("");
			lxmls.put(desc.getObjname(), xmlstr);
			return rv;
		}
		else
			throw new Exception("Can't find class for tag name:" + desc.getTagname());
	}

//	public String getXMLByDescriptor(IXMLObjectDesc desc)
//	{
//		IXMLObjectDesc classloaderdesc = desc.getClassloader();
//		String xmlstr = "<" + desc.getTagname() + ">\n" +
//
//				"<" + desc.getTagname() + "-name>" + desc.getObjname() + "</" + desc.getTagname() + "-name>\n" +
//				"<class-name>\n" +
//				"\t" + desc.getClassname() + "\n" +
//				"</class-name>\n" +
//				((classloaderdesc == null) ? "" : "<" + classloaderdesc.getTagname() + ">" + classloaderdesc.getObjname() + "</" + classloaderdesc.getTagname() + ">") +
//				"<params>\n";
//		for (int i = 0; i < desc.getParams().size(); i++)
//		{
//			IDefAttr iDefAttr = desc.getParams().get(i);
//			if (iDefAttr.getValue() instanceof IXMLObjectDesc)
//			{
//				ParamDescriptor descriptor = (ParamDescriptor) iDefAttr.getValue();
//				String tagname = descriptor.getTagname();
//				xmlstr += "<" + tagname + ">" + descriptor.getObjname() + "</" + tagname + ">\n";
//			}
//			else
//				xmlstr += "<param Nm=\'" + iDefAttr.getName() + "\' Val=\'" + iDefAttr.getValue() + "\'/>\n";
//		}
//		xmlstr += "</params>\n";
//		xmlstr += "</" + desc.getTagname() + ">\n";
//		return xmlstr;
//	}

	public IInitAble initByDescriptor(IXMLObjectDesc desc, List<IParam> attrs) throws Exception
	{
		IInitAble rv = initables.get(desc.getObjname());
		if (rv == null && desc.getClassname() == null)
			for (IXMLObjectDesc localdesc : paramsdesc)//Попытаемя найти дескриптор в местных описателях
				if (localdesc.getObjname().equals(desc.getObjname()))
				{
					desc = localdesc;
					break;
				}

		String classname = null;
		if (rv == null && (classname = desc.getClassname()) != null && classname.length() > 0)
		{
			ClassLoader loader = null;


			IXMLObjectDesc loaderdesc = desc.getClassloader();
			if (loaderdesc != null)
			{
				IXMLBuilder builder = buildercontext.getBuilderByTagName(loaderdesc.getTagname());
				IInitAble initable = null;
				if (builder == null || (initable = builder.initByDescriptor(loaderdesc, null)) == null)
				{
					System.out.println("Error of tag name:" + loaderdesc.getTagname());
					return null;
				}
				if (tagname.equalsIgnoreCase(loaderdesc.getTagname()))
					initables.put(initable.getObjName(), initable);
				loader = (ClassLoader) initable;
			}
			else
				loader = Thread.currentThread().getContextClassLoader();


			Class metaclass = loader.loadClass(desc.getClassname());
			Constructor[] constructors = metaclass.getConstructors();

			List<IParam> paramList = desc.getParams();
			if (attrs !=null && attrs.size()>0)
			{
				paramList = new LinkedList<IParam>(paramList);
				paramList.addAll(attrs);
			}

			Object[] params = new Object[paramList.size() + 2];
			for (int i = 2; i < params.length; i++)
			{
				IParam iDefAttr = paramList.get(i - 2);
				params[i] = iDefAttr;
				if (iDefAttr.getValue() instanceof IXMLObjectDesc && initByDescriptorParam(iDefAttr))
						return null;
			}
			params[0] = new DefAttrImpl(KernelConst.OBJNAME, desc.getObjname());
			params[1] = new DefAttrImpl(KernelConst.DESCRIPTOR, desc);

			for (Constructor constructor : constructors)
				if (constructor.getParameterTypes().length == 0)
				{
					Object o = constructor.newInstance();
					if (o instanceof IInitAble)
						rv = (IInitAble) o;
					else
					{
						System.out.println("Error of initable:" + o.getClass().getCanonicalName());
						rv = null;
						break;
					}
				}
			if (rv != null)
			{
				rv.init(params);
				initables.put(rv.getObjName(), rv);
			}
			else
				throw new Exception("Can't find constructor with no parameters:" + desc.getClassname());

		}
		return rv;
	}

	private boolean initByDescriptorParam(IParam iDefAttr)
			throws Exception
	{
		ParamDescriptor descriptor = (ParamDescriptor) iDefAttr.getValue();
		IXMLBuilder builder = buildercontext.getBuilderByTagName(descriptor.getTagname());
		IInitAble initable = null;

		if (builder!=null)
		{
			String oname = descriptor.getObjname();
			System.out.println("oname = " + oname);
		}

		if (builder == null || (initable = builder.initByDescriptor(descriptor, iDefAttr.getAttributes())) == null)
		{
			System.out.println("Error of tag name:" + descriptor.getTagname());
			return true;
		}
		iDefAttr.setValue(initable);
		if (tagname.equalsIgnoreCase(descriptor.getTagname()))
			initables.put(initable.getObjName(), initable);
		return false;
	}

	public class GroupHandler extends HandlerEx
	{
		public GroupHandler(XMLReader reader)
		{
			super(reader);
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			if (qName.equalsIgnoreCase(tagname))
			{
				ParamDescriptor storforbuild = new ParamDescriptor(tagname,buildercontext.getNextObjectId());
				paramsdesc.add(storforbuild);
				new SectionHandler(reader, storforbuild);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (qName.equalsIgnoreCase(tagname + "s"))
			{
				if (!postopone)
					initByDescriptors();
				ret2callerHadnler();
			}
		}


	}

	public void initByDescriptors()
			throws SAXException
	{
		long tm = System.currentTimeMillis();
		List<IXMLObjectDesc> l_storagesdesc = new LinkedList<IXMLObjectDesc>(paramsdesc);
		int old_size = l_storagesdesc.size();
		while (old_size > 0)
		{
			for (int j = 0; j < l_storagesdesc.size();)
			{
				IXMLObjectDesc descriptor = l_storagesdesc.get(j);
				try
				{
					IInitAble value = buildercontext.getBuilderByTagName(descriptor.getTagname()).initByDescriptor(descriptor, null);
					if (value != null)
					{
						if (!initables.containsKey(value.getObjName()))
							initables.put(value.getObjName(), value);
						l_storagesdesc.remove(j);
					}
					else
						j++;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					j++;
				}
			}
			if (old_size == l_storagesdesc.size())
				throw new SAXException("Error init of " + tagname);
			else
				old_size = l_storagesdesc.size();
		}
		System.out.println("init " + tagname + "s tm = " + (System.currentTimeMillis() - tm));
	}

	class SectionHandler extends HandlerEx
	{
		IXMLObjectDesc paramdesc;
		String qName;

		public SectionHandler(XMLReader reader, IXMLObjectDesc paramdesc)
		{
			super(reader);
			this.paramdesc = paramdesc;
			this.qName = tagname;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			this.qName = qName;
			int ln=attributes.getLength();

			if (qName.equalsIgnoreCase("param"))
			{
				DefAttrImpl attr=new DefAttrImpl("","");
				if (ln>0)
				{
					String nm = attributes.getValue("Nm");
					String val = attributes.getValue("Val");
					if (ln==1)
					{
						if (nm==null && val==null)
						{
							nm=attributes.getLocalName(0);
							val=attributes.getValue(0);
							attr=new DefAttrImpl(nm,val);
						}
						else
						{ //Конструкции вида <param Nm="Name"/> или <param Val="Value"/>
							if (nm==null) nm="";
							if (val==null) val="";
							attr=new DefAttrImpl(nm,val);
						}
					}
					else
					{ 
						if (ln==2 && nm!=null && val!=null)
							attr=new DefAttrImpl(nm,val);
						else
						{ //Формируем список из параметров
							if (nm==null) nm="";
							if (val==null) val="";

							List<IParam> attrs= new LinkedList<IParam>();
							for (int i=0;i<ln;i++)
							{
								String parname=attributes.getLocalName(i);
								if (!parname.equalsIgnoreCase("Nm") && !parname.equalsIgnoreCase("Val"))
									attrs.add(new DefAttrImpl(parname,attributes.getValue(i)));
							}
							attr=new DefAttrImpl(nm,val,attrs);
						}
					}
				}
				paramdesc.getParams().add(attr);
			}
			else if (
					qName.equalsIgnoreCase(KernelConst.CLASSLOADER_TAGNAME)
					)
			{
				IXMLObjectDesc descriptor = new ParamDescriptor(qName,buildercontext.getNextObjectId());  //Добвление описателя загрузчика класса
				paramdesc.setClassloader(descriptor);//установка дескриптора
				buildercontext.getHandlerByTagName(qName, reader, descriptor);//инициализация дескриптора
			}
			else if (
					!qName.equalsIgnoreCase("class-name")
							&& !qName.equalsIgnoreCase("params")
							&& !qName.equalsIgnoreCase(tagname + "-name")

					)
			{
				IXMLObjectDesc descriptor = new ParamDescriptor(qName,buildercontext.getNextObjectId());
				DefAttrImpl defAttr = new DefAttrImpl(qName, descriptor);
				if (ln>0)
				{
					List<IParam> attrs= new LinkedList<IParam>();
					for (int i=0;i<ln;i++)
						attrs.add(new DefAttrImpl(attributes.getLocalName(i),attributes.getValue(i)));
					defAttr = new DefAttrImpl(qName,descriptor,attrs);
				}

				paramdesc.getParams().add(defAttr);
				buildercontext.getHandlerByTagName(qName, reader, descriptor);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (qName.equalsIgnoreCase(tagname))
			{
				ret2callerHadnler();//Выходим из вызова
			}
		}

		public void characters(char[] ch, int start, int length)
		{
			int llength = 0;
			char lch[] = new char[length];

			for (int i = 0; i < length; i++)
			{
				char cc = ch[i + start];
				if (cc != '\n' && cc != '\t')
				{
					lch[llength] = cc;
					llength++;
				}
//				else
//					System.out.println("Skiped char");
			}

			String s = new String(lch, 0, llength);

			if (qName != null && s != null && s.length() > 0)
			{
				if (qName.equalsIgnoreCase("class-name"))
				{
					if (paramdesc.getClassname() == null)
						paramdesc.setClassname(s);
					else
						paramdesc.setClassname(paramdesc.getClassname() + s);
				}
				else if (qName.equalsIgnoreCase(tagname + "-name"))
				{
					if (paramdesc.getObjname() == null)
						paramdesc.setObjname(s);
					else
						paramdesc.setObjname(paramdesc.getObjname() + s);
				}
				else if (qName.equalsIgnoreCase(tagname))
				{
					if (paramdesc.getObjname() == null)
						paramdesc.setObjname(s);
					else
						paramdesc.setObjname(paramdesc.getObjname() + s);
				}
			}
		}
	}

	public HandlerEx getSectionHandler(XMLReader reader, IXMLObjectDesc paramdesc)
	{
		return new SectionHandler(reader, paramdesc);
	}

	public HandlerEx getGroupHandler(XMLReader reader)
	{
		return new GroupHandler(reader);
	}

}
