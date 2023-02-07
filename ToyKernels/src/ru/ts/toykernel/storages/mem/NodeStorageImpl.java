package ru.ts.toykernel.storages.mem;

import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.FilteredStorage;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.stream.NodeFilter;
import ru.ts.toykernel.filters.stream.NodeFilter2;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.factory.IFactory;
import ru.ts.factory.IObjectDesc;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.utils.gui.elems.EmptyProgress;
import ru.ts.xml.IXMLObjectDesc;

import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Group Storage for in memmory map
 * ru.ts.toykernel.storages.mem.NodeStorageImpl
 */
public class NodeStorageImpl implements INodeStorage
{

	public static final String TYPENAME ="GRP_STORG";//name of object
	protected String nodeId="MAIN_STORAGE";
	protected Map<String, INodeStorage> storages;


	private IFactory<INodeStorage> storagesfactory;
	private AObjAttrsFactory objectAttrsFactory;

	protected IViewProgress viewProgress;
	protected INameConverter nmconverter;
	protected IXMLObjectDesc desc;

	public IAttrs getObjAttrs(String curveid) throws Exception
	{
		IBaseStorage stor = getStorageByCurveId(curveid);
		if (stor!=null)
			return stor.getObjAttrs(curveid);
		else
			return null;
	}

	public Map<String, INodeStorage> getStorages()
	{
		return storages;
	}


	public void setStoragesfactory(IFactory<INodeStorage> storagesfactory)
	{
		this.storagesfactory = storagesfactory;
	}


	public void setViewProgress(IViewProgress viewProgress)
	{
		this.viewProgress = viewProgress;
		this.viewProgress.setMaxProgress(100);
		this.viewProgress.setProgress(0);
	}

	public String getObjName()
	{
		return nodeId;
	}

	public Object[] init(Object... objs) throws Exception
	{
		if (storages==null)
            storages=new HashMap<String, INodeStorage>();
		for (Object obj : objs){
			IDefAttr attr=(IDefAttr)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.nodeId= (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else
			{
				Object value = attr.getValue();
				if (value instanceof INodeStorage)
				{
					String objName = ((INodeStorage) value).getObjName();
					storages.put(objName,((INodeStorage)value));
				}
			}
		}

//TODO Ввести фабрики как параметры для инициализации
//		setObjAttrsFactory(null);
//		setNameConverter(nmconverter);
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}


	public NodeStorageImpl()
	{}

	public NodeStorageImpl(String nodeId,Map<String, INodeStorage> storages,AObjAttrsFactory objectAttrsFactory,INameConverter nmconverter)
	{
		this.nodeId=nodeId;
		this.objectAttrsFactory = objectAttrsFactory;
		if (viewProgress == null)
			viewProgress = new EmptyProgress();

		if (nmconverter == null)
			nmconverter = new DefNameConverter();
		this.nmconverter = nmconverter;

		if (storages==null)
			storages=new HashMap<String, INodeStorage>();
		this.storages=storages;
	}

	private NodeStorageImpl
			(

			IFactory<INodeStorage> storagesfactory,
			AObjAttrsFactory objectAttrsFactory,
			IViewProgress viewProgress,
			INameConverter nmconverter
	)
	{
		this.storagesfactory = storagesfactory;
		this.objectAttrsFactory = objectAttrsFactory;

		if (viewProgress == null)
			viewProgress = new EmptyProgress();
		this.viewProgress=viewProgress;

		if (nmconverter == null)
			nmconverter = new DefNameConverter();
		this.nmconverter = nmconverter;

		this.viewProgress.setMaxProgress(100);
		this.viewProgress.setProgress(0);
	}

	public int getObjectsCount()
	{
		int rcount=0;
		for (INodeStorage iNodeStorage : storages.values())
			rcount+=iNodeStorage.getObjectsCount();
		return rcount;
	}

	public long getLastModified()
	{
		long lm=-1;
		for (INodeStorage iNodeStorage : storages.values())
			if (lm<0 || lm<iNodeStorage.getLastModified())
				lm=iNodeStorage.getLastModified();
		return lm;
	}

	public IBaseStorage filter(IBaseFilter filter) throws Exception
	{
		if (filter==null)
			return this;
		if (filter instanceof NodeFilter)
			return storages.get(((NodeFilter) filter).getNodeId());
		else if(filter instanceof NodeFilter2)
		{
			NodeStorageImpl rv=new NodeStorageImpl(storagesfactory,objectAttrsFactory,viewProgress,nmconverter);
			rv.storages=new HashMap<String, INodeStorage>();
			List<String> groupsId=((NodeFilter2) filter).getNodesId();
			for (String groupId : groupsId)
				rv.storages.put(groupId,storages.get(groupId));
			return rv;
		}
		else
			return new FilteredStorage(filter,this);
		//throw new UnsupportedOperationException("Can't filter with type:"+filter.getClass().getName());
	}

	/**
	 *
	 * @param curvId - object id in format layerId_curveId in layer
	 * @return - gisobject
	 * @throws Exception
	 */
	public IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception
	{
		String[] lrId2curveId=curvId.split("["+ INodeStorage.GROUP_SEPARATOR+"]");
		INodeStorage iNodeStorage = storages.get(lrId2curveId[0]);
		IBaseGisObject rv = null;
		if (iNodeStorage !=null)
			rv= iNodeStorage.getBaseGisByCurveId(curvId);
		return rv;
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		viewProgress.setCurrentOperation("Save default objects Attributes");
		viewProgress.setProgress(0);
		List<String> l_set=new LinkedList<String>(storages.keySet());
		dos.writeInt(l_set.size());
		for (String lr_id : l_set)
		{
			INodeStorage stor=storages.get(lr_id);
			IAttrs attrs=stor.getDefAttrs();
			dos.writeInt(attrs.size());
			for (String attrkey : attrs.keySet())
			{
				ObjectOutputStream obis = new ObjectOutputStream(dos);
				obis.writeUTF(attrs.get(attrkey).getName());
				obis.writeObject(attrs.get(attrkey).getValue());
				obis.flush();
			}
		}
		viewProgress.setCurrentOperation("Save Objectstorages");
		viewProgress.setProgress(0.1*viewProgress.getMaxProgress());
		for (int i = 0; i < l_set.size(); i++)
		{
			String lr_id = l_set.get(i);
			storages.get(lr_id).saveToStream(dos);
			viewProgress.setProgress(((0.8 *(i+1))/l_set.size())*viewProgress.getMaxProgress());
		}
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		viewProgress.setCurrentOperation("Loading default objects Attributes");
		viewProgress.setProgress(0);
		List<IAttrs> attrobjlist = new LinkedList<IAttrs>();
		int cnt = dis.readInt();
		while (cnt > 0)
		{
			int cntattrs = dis.readInt();
			IAttrs attr = new DefaultAttrsImpl();
			while (cntattrs > 0)
			{
				ObjectInputStream obis = new ObjectInputStream(dis);
				String attrname = obis.readUTF();
				Object val = obis.readObject();

//				if (
//						attrname.equalsIgnoreCase(KernelConst.ATTR_COLOR_LINE) ||
//						attrname.equalsIgnoreCase(KernelConst.ATTR_COLOR_FILL) ||
//						attrname.equalsIgnoreCase(KernelConst.ATTR_LINE_THICKNESS) ||
//						attrname.equalsIgnoreCase(KernelConst.ATTR_SCALE_THICKNESS)
//					)
//						System.out.println("Stop loading");
//				else
				attr.put(attrname, new DefAttrImpl(attrname, val));
				cntattrs--;
			}
			attrobjlist.add(attr);
			cnt--;
		}

		viewProgress.setCurrentOperation("Loading Objectstorages");
		storages = new HashMap<String, INodeStorage>();
		int sz=cnt = attrobjlist.size();
		while (cnt > 0)
		{
			INodeStorage storage= storagesfactory.createByTypeName("");
			storage.setDefAttrs(attrobjlist.get(attrobjlist.size() - cnt));
			storage.setObjAttrsFactory(objectAttrsFactory);
			storage.setNameConverter(nmconverter);
			storage.setParentStorage(this);
			storage.loadFromStream(dis);
			storages.put(storage.getNodeId(), storage);
			viewProgress.setProgress(((0.8- (cnt/sz*0.8))*viewProgress.getMaxProgress()));
			cnt--;
		}
	}

	public IBaseStorage getStorageByCurveId(String curveId) throws Exception
	{
		IBaseStorage rv=null;
		for (INodeStorage iNodeStorage : storages.values())
			if ((rv=iNodeStorage.getStorageByCurveId(curveId))!=null)
				break;
		return rv;
	}

	public String getNodeId()
	{
		throw new UnsupportedOperationException();
	}

	public INodeStorage getParentStorage()
	{
		return null;
	}

	public void setParentStorage(INodeStorage parent)
	{
		throw new UnsupportedOperationException();
	}

	public Collection<INodeStorage> getChildStorages()
	{
		return storages.values();
	}

	public IAttrs getDefAttrs()
	{
		throw new UnsupportedOperationException();
	}

	public void setDefAttrs(IAttrs defAttrs)
	{
		throw new UnsupportedOperationException();
	}

	public void setNameConverter(INameConverter nmconverter)
	{
		this.nmconverter = nmconverter;
	}

	public void setObjAttrsFactory(AObjAttrsFactory attrsfactory)
	{
		objectAttrsFactory=attrsfactory;
	}

	public void rebindByObjAttrsFactory(AObjAttrsFactory attrsfactory) throws Exception
	{
		for (INodeStorage iNodeStorage : storages.values())
			iNodeStorage.rebindByObjAttrsFactory(attrsfactory);
	}

	public AObjAttrsFactory getObjAttrsFactory()
	{
		return objectAttrsFactory;
	}

	private class GrpStorageIterator<T> implements Iterator<T>
	{
		private List<Iterator<T>> keys;
		private int curindex=0;

		GrpStorageIterator(List<Iterator<T>> keys)
		{
			this.keys = keys;
		}

		public boolean hasNext()
		{
			while (!keys.get(curindex).hasNext() && curindex<keys.size()-1)
				curindex++;
			return keys.get(curindex).hasNext();
		}

		public T next()
		{
			hasNext();
			try
			{
				return keys.get(curindex).next();
			} catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException();
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException("Can't remove by iterator:"+this.getClass().getCanonicalName());
		}
	}

	
	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter) throws Exception
	{
		List<Iterator<IBaseGisObject>> keys= new LinkedList<Iterator<IBaseGisObject>>();
		for (INodeStorage iNodeStorage : storages.values())
			keys.add(iNodeStorage.filterObjs(filter));
		return new GrpStorageIterator<IBaseGisObject>(keys);
	}

	public Iterator<IBaseGisObject> getAllObjects() throws Exception
	{
		List<Iterator<IBaseGisObject>> keys= new LinkedList<Iterator<IBaseGisObject>>();
		for (INodeStorage iNodeStorage : storages.values())
			keys.add(iNodeStorage.getAllObjects());
		return new GrpStorageIterator<IBaseGisObject>(keys);
	}

	public Iterator<String> getCurvesIds() throws Exception
	{
		List<Iterator<String>> keys= new LinkedList<Iterator<String>>();
		for (INodeStorage iNodeStorage : storages.values())
			keys.add(iNodeStorage.getCurvesIds());
		return new GrpStorageIterator<String>(keys);
	}
}
