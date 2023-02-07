package ru.ts.toykernel.storages.mem.share;

import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.storages.IBaseStorage;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 07.07.2011
 * Time: 18:08:50
 * Класс для хранения данных загруженных хранилищь и доступа к ним  
 */
public class IAccessorImpl
{

	private static IAccessorImpl ourInstance = new IAccessorImpl();

	public static IAccessorImpl getInstance()
	{
		return ourInstance;
	}

	private IAccessorImpl()
	{
	}

	public Map<String,AccessorContainer> nodeId2container =new HashMap<String,AccessorContainer>();

	public AccessorContainer getContainerByName(String nodeId)
	{
		return nodeId2container.get(nodeId);
	}

	public AccessorContainer initStorName
	(
			String ixfnname,String datfname,String nodeId,
			AObjAttrsFactory attrsfactory,
	        IBaseStorage parentstor
	)
	throws Exception
	{
		AccessorContainer accontainer = new AccessorContainer();
		accontainer.initIt(ixfnname,datfname,nodeId,attrsfactory,parentstor);
		nodeId2container.put(nodeId,accontainer);
		return accontainer;
	}

//	public long getLastModified()
//	{
//		long ixlm=new File(getIXFileName()).lastModified();
//		long dtlm=new File(getDatFileName()).lastModified();
//		long gmlm=new File(getGeomFileName()).lastModified();
//		return Math.max(Math.max(ixlm,dtlm),gmlm);
//	}
}
