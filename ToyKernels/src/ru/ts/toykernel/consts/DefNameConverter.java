package ru.ts.toykernel.consts;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 06.03.2009
 * Time: 12:09:50
 * Trivial default implemetation  of name converter
 */
public class DefNameConverter extends DefBaseNameConverter
{

	public String codeAttrNm2ViewNm(String attrName)
	{
		String rv=null;
		for (INameConverter conv : ff)
			if ((rv = conv.codeAttrNm2ViewNm(attrName))!=null)
				break;
		if (rv==null)
			return attrName;
		return rv;
	}

	public String codeAttrNm2StorAttrNm(String attrName)
	{
		String rv=null;
		for (INameConverter conv : ff)
			if ((rv = conv.codeAttrNm2StorAttrNm(attrName))!=null)
				break;
		if (rv==null)
			return attrName;
		return rv;
	}

	public String storAttrNm2codeAttrNm(String attrName)
	{
		String rv=null;
		for (INameConverter conv : ff)
			if ((rv = conv.storAttrNm2codeAttrNm(attrName))!=null)
				break;
		if (rv==null)
			return attrName;
		return rv;
	}
}
