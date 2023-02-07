package ru.ts.toykernel.factory;

import ru.ts.factory.IInitAble;
import ru.ts.factory.IParam;
import ru.ts.factory.IObjectDesc;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.toykernel.consts.KernelConst;

import java.util.List;
import java.util.LinkedList;

/**
 * Standart initializator
 */
abstract public class BaseInitAble implements IInitAble
{
	protected String ObjName;
	protected IXMLObjectDesc desc;

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}

	public String getObjName()
	{
		return ObjName;
	}
	public Object[] init(Object... objs) throws Exception
	{
		List olds= new LinkedList();
		for (Object obj : objs)
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				ObjName = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else
			{
				Object oobj=init(obj);
				if (oobj!=null)
					olds.add(oobj);
			}
		}
		return olds.toArray(new Object[olds.size()]);
	}

}
