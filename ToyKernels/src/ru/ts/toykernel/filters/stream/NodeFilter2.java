package ru.ts.toykernel.filters.stream;

import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.stream.ISerializer;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.factory.BaseInitAble;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.List;
import java.util.LinkedList;

/**
 * Фильтр по идентификаторам узлов
 */
public class NodeFilter2 extends BaseInitAble implements IBaseFilter, ISerializer
{
	private List<String> nodesId;
	public static final String TYPENAME ="F_GR2";//layered filter

	public String getTypeName()
	{
		return TYPENAME;
	}

	public List<String> getNodesId()
	{
		return nodesId;
	}

	public NodeFilter2()
	{
	}

	public NodeFilter2(List<String> nodesId)
	{
		this.nodesId = nodesId;
	}
	public boolean acceptObject(IBaseGisObject obj)
	{
		String objid=obj.getCurveId();
		for (String groupId : nodesId)
		{
			if (objid.startsWith(groupId+"_"))
				return true;
		}
		return false;
	}

	public void saveToStream(DataOutputStream dos) throws IOException
	{
		dos.writeUTF(getTypeName());
		dos.writeInt(nodesId.size());
		for (String groupId : nodesId)
			dos.writeUTF(groupId);
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		int sz=dis.readInt();
		nodesId = new LinkedList<String>();
		while(sz>0)
		{
			nodesId.add(dis.readUTF());
			sz--;
		}
	}

	public Object[] init(Object... objs) throws Exception
	{
		nodesId=new LinkedList<String>();
		return super.init(objs);
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		nodesId.add((String)attr.getValue());
		return null;
	}
}
