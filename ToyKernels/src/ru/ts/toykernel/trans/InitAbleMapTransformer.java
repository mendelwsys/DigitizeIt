package ru.ts.toykernel.trans;

import ru.ts.gisutils.proj.transform.MapTransformer;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.factory.IObjectDesc;
import ru.ts.utils.data.Pair;
import org.opengis.referencing.FactoryException;

import java.util.Collection;

/**
 * Initable transformer
 *
 */
public class InitAbleMapTransformer extends MapTransformer implements IInitAbleTransformer
{
	protected IXMLObjectDesc desc;

	public String replaceParameter(String targetWKT, Pair<String,String> param2val) throws FactoryException
	{
		final char[] closingBrackets = {']', ')'};
		char separator = ',';

		int ix=targetWKT.indexOf(param2val.first);
		if (ix<0)
			throw new FactoryException("Can't find replace paramter"+param2val.first);
		ix=targetWKT.indexOf(separator,ix+param2val.first.length());
		if (ix<0)
			throw new FactoryException("Can't find separator"+separator+ "after paramter " + param2val.first);
		
		for (int i=ix;i<targetWKT.length();i++)
			if (targetWKT.charAt(i)==closingBrackets[0] || targetWKT.charAt(i)==closingBrackets[1])
				return  targetWKT.substring(0,ix+1)+param2val.second+targetWKT.substring(i);

		throw new FactoryException("Can't find close bracket"+closingBrackets[0]+" or "+ closingBrackets[1]);
	}



	public void reinitMapTransformer(Collection<Pair<String,String>> replacelistinDst) throws Exception
	{
		for (Pair<String, String> stringStringPair : replacelistinDst)
			replaceParameter(_dstWKT,stringStringPair);
		initTransformer(_srcWKT,_dstWKT);
	}

	public InitAbleMapTransformer()
	{
		super();
	}

	public InitAbleMapTransformer(String ObjName,IXMLObjectDesc desc,String srcWKT,String dstWKT) throws Exception
	{
		super(srcWKT,dstWKT);
		this.ObjName=ObjName;
		this.desc=desc;
	}


	protected String ObjName;
	public String getObjName()
	{
		return ObjName;
	}
	public Object[] init(Object... objs) throws Exception
	{
		for (Object obj : objs)
		{
			IDefAttr attr=(IDefAttr)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				ObjName = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else
				init(obj);
		}
		initTransformer(_srcWKT,_dstWKT);
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase("wktdst"))
				_dstWKT= (String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase("wktsrc"))
				_srcWKT= (String) attr.getValue();
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}
}