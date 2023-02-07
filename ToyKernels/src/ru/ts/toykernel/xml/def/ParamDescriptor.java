package ru.ts.toykernel.xml.def;

import ru.ts.xml.IXMLObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.factory.IInitAble;

import java.util.List;
import java.util.LinkedList;

/**
 * Object descriptor
 * Contains information for allocate object
 */
public class ParamDescriptor implements IXMLObjectDesc
{
	protected String tagname;
	public String objname = "";//Object name
	public String classname;//Object class name
	protected IXMLObjectDesc classloader;//Loader of class (if not set load class using class loader of current thread)
	public List<IParam> params = new LinkedList<IParam>();//В качестве Value может выступать ParamDescriptor
	protected int initOrder;//init order of param descriptor

	public ParamDescriptor(IXMLObjectDesc desc)
	{
		this.tagname = desc.getTagname();
		this.objname = desc.getObjname();
		this.classname = desc.getClassname();
		this.classloader = desc.getClassloader();
		this.params =new LinkedList<IParam>();
		for (IParam l_param : desc.getParams())
			this.params.add(l_param.getCopy());
		this.initOrder = desc.getInitOrder();
	}

	public ParamDescriptor(String tagname, String objname, String classname, IXMLObjectDesc classloader, List<IParam> params, int initOrder)
	{
		this.tagname = tagname;
		this.objname = objname;
		this.classname = classname;
		this.classloader = classloader;
		this.params = params;
		this.initOrder = initOrder;
	}

	public ParamDescriptor(String tagname, int initOrder)
	{
		this.tagname = tagname;
		this.initOrder = initOrder;
	}

	public String getXMLDescriptor(String prefix) throws Exception
	{
		if (prefix==null)
			prefix="";
		
		IXMLObjectDesc classloaderdesc = this.getClassloader();
		String xmlstr = prefix+"<" + this.getTagname() + ">\n" +

				prefix+"\t<" + this.getTagname() + "-name>" + this.getObjname() + "</" + this.getTagname() + "-name>\n" +
				prefix+"\t<class-name>\n" +
				prefix+"\t\t" + this.getClassname() + "\n" +
				prefix+"\t</class-name>\n" +
				((classloaderdesc == null) ? "" : prefix+"\t<" + classloaderdesc.getTagname() + ">" + classloaderdesc.getObjname() + "</" + classloaderdesc.getTagname() + ">\n");
		if (this.getParams() != null && this.getParams().size()!=0)
		{
			xmlstr += prefix+"\t<params>\n";

			for (int i = 0; i < this.getParams().size(); i++)
			{
				IParam iDefAttr = this.getParams().get(i);
				Object val = iDefAttr.getValue();
				List<IParam> attrs = iDefAttr.getAttributes();
				if (val instanceof IXMLObjectDesc)
					xmlstr = desc2XMLFrag(prefix, xmlstr, val, attrs);
				else if (val instanceof IInitAble)
					xmlstr = initAble2XMLFrag(prefix, xmlstr, val, attrs);
				else
				{
					String nm = iDefAttr.getName();
					String buffer = getXMLByParamList(attrs);

					if (nm!=null && nm.length()>0 && !nm.contains(" ") && val!=null && buffer.length()==0)
						xmlstr += prefix+"\t\t<param "+nm+"=\'" + val + "\'/>\n";
					else
					{   xmlstr +=prefix+"\t\t<param";
						if (nm != null && nm.length() > 0)
							 xmlstr +=" Nm=\'" + nm + "\'";
						if (val!=null  && val.toString().length()>0)
							xmlstr += " Val=\'" + val + "\'";
						xmlstr+=buffer+"/>\n";
					}

				}
			}
			xmlstr += prefix+"\t</params>\n";
		}
		xmlstr += prefix+"</" + this.getTagname() + ">\n";
		return xmlstr;
	}

	private String getXMLByParamList(List<IParam> lst)
	{
		StringBuffer buffer=new StringBuffer();
		if (lst!=null && lst.size()>0)
			for (IParam iParam : lst)
				buffer.append(" ").append(iParam.getName()).append("=\'").append(iParam.getValue()).append("\'");
		return buffer.toString();
	}

	private String desc2XMLFrag(String prefix, String xmlstr, Object val, List<IParam> paramCtx)
	{
		ParamDescriptor descriptor = (ParamDescriptor) val;
		String tagname = descriptor.getTagname();
		String params=getXMLByParamList(paramCtx);
		xmlstr += prefix+"\t\t<" + tagname + params+">" + descriptor.getObjname() + "</" + tagname + ">\n";
		return xmlstr;
	}

	private String initAble2XMLFrag(String prefix, String xmlstr, Object val, List<IParam> paramCtx)
			throws Exception
	{
		IXMLObjectDesc desc = (IXMLObjectDesc) ((IInitAble) val).getObjectDescriptor();
		String params=getXMLByParamList(paramCtx);
		if (desc != null)
			xmlstr += prefix+"\t\t<" + desc.getTagname() + params+ ">" + desc.getObjname() + "</" + desc.getTagname() + ">\n";
		else
			throw new Exception("Object descriptor is null");
		return xmlstr;
	}


	public IParam getParamByName(String parname)
	{
		if (parname!=null)
			for (IParam param : params)
				if(parname.equals(param.getName()))
					return param;
		return null;
	}
	public String getTagname()
	{
		return tagname;
	}



	public String getObjname()
	{
		return objname;
	}

	public void setObjname(String objname)
	{
		this.objname = objname;
	}

	public List<IParam> getParams()
	{
		return params;
	}

	public void setParams(List<IParam> params)
	{
		this.params = params;
	}

	public String getClassname()
	{
		return classname;
	}

	public void setClassname(String classname)
	{
		this.classname = classname;
	}


	public IXMLObjectDesc getClassloader()
	{
		return classloader;
	}

	public void setClassloader(IXMLObjectDesc classloader)
	{
		this.classloader = classloader;
	}

	public int getInitOrder()
	{
		return initOrder;
	}

    public void setInitOrder(int initOrder)
    {
        this.initOrder=initOrder;
    }

}
