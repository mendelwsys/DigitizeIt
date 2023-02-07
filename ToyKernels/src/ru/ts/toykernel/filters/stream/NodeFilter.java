package ru.ts.toykernel.filters.stream;

import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.stream.ISerializer;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.factory.BaseInitAble;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * Фильтр по идентификатору узла
 * ru.ts.toykernel.filters.stream.NodeFilter
 */
public class NodeFilter extends BaseInitAble implements IBaseFilter, ISerializer
{
	private String nodeId;
	public static final String TYPENAME ="F_GR";//layered filter

	public String getTypeName()
	{
		return TYPENAME;
	}

	public String getNodeId()
	{
		return nodeId;
	}
	public NodeFilter()
	{

	}
	public NodeFilter(String nodeId)
	{
		this.nodeId = nodeId;
	}
	public boolean acceptObject(IBaseGisObject obj)
	{
		String objid=obj.getCurveId();
		return objid.startsWith(nodeId +"_");
	}

	public void saveToStream(DataOutputStream dos) throws IOException
	{
		dos.writeUTF(getTypeName());
		dos.writeUTF(nodeId);
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		nodeId =dis.readUTF();
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
        String name = attr.getName();
        if (name==null || name.length()==0)
		    nodeId=((String)attr.getValue());
		return null;
	}
}
