package ru.ts.toykernel.storages.mem.share;

import ru.ts.gisutils.algs.common.MRect;
import ru.ts.utils.data.Pair;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.toykernel.attrs.*;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.attrs.def.DefaultGisObjectAttrs;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.InitStorageException;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.FilteredStorage;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.stream.StreamROCurve;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.IMBBFilter;
import ru.ts.toykernel.filters.IKeyFilter;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.factory.IObjectDesc;
import ru.ts.xml.IXMLObjectDesc;

import java.util.*;
import java.io.*;

import su.web.WebUtils;

/**
 * Гибридное хранилище, постоянно держит в памяти только аттрибуты и индексы
 * объектов, остальное сбрасывает на диск
 * ru.ts.toykernel.storages.mem.share.MemShareBaseStorageLr
 */
public class MemShareBaseStorageLr implements INodeStorage
{

	public static final String TYPENAME ="MEM_SHARE_BASE_STORAGE";//name of object
	protected INameConverter nameconverter;
	protected String nodeId;//node identifyer
	protected AObjAttrsFactory attrsfactory;//attribute factory
	protected INodeStorage parentstor;

	protected IXMLObjectDesc desc;


	protected AccessorContainer container;


	public int getObjectsCount()
	{
		return container.graphobkects.size();
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	protected class MemBaseStorageIterator implements Iterator<IBaseGisObject>
	{
		protected Iterator<String> keys;

		MemBaseStorageIterator(Iterator<String> keys)
		{
			this.keys = keys;
		}

		public boolean hasNext()
		{
			boolean hasNext = keys.hasNext();
			if (!hasNext)
				releaseStorage();
			return hasNext;
		}

		public IBaseGisObject next()
		{
			String currentId = keys.next();
			try
			{
				return incarnateByIndex(container.graphobkects.get(currentId).first);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public void remove()
		{
			throw new UnsupportedOperationException("Operation remove is not supported for this Strorage");
		}
	}

	protected class KeyIterator implements Iterator<String>
	{
		private Iterator<String> keys;

		protected KeyIterator(Iterator<String> keys)
		{
			this.keys = keys;
		}

		public boolean hasNext()
		{
			boolean hasNext = keys.hasNext();
			if (!hasNext)
				releaseStorage();
			return hasNext;
		}

		public String next()
		{
			return keys.next();
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	protected  Iterator<IBaseGisObject> getMemStorageIterator(Iterator<String> keys)
	{
		return new MemBaseStorageIterator(keys);
	}

	protected   Iterator<String> getKeyIterator(Iterator<String> keys)
	{
		return new KeyIterator(keys);
	}

	public IBaseStorage getStorageByCurveId(String curveId)
	{
		if (this.container.graphobkects.keySet().contains(curveId))
			return this;
		return null;
	}

	public String getNodeId()
	{
		return nodeId;
	}

	public INodeStorage getParentStorage()
	{
		return parentstor;
	}

	public void setParentStorage(INodeStorage parent)
	{
		this.parentstor=parent;
	}

	public Collection<INodeStorage> getChildStorages()
	{
		return new LinkedList<INodeStorage>();
	}

	public  void setDefAttrs(IAttrs defAttrs)
	{
		if (defAttrs == null)
			defAttrs = new DefaultAttrsImpl();
		this.container.defAttrs =defAttrs;
	}

	public IAttrs getDefAttrs()
	{
		return container.defAttrs;
	}


	public void setNameConverter(INameConverter nmconverter)
	{
		if (nmconverter == null)
			nmconverter = new DefNameConverter();
		this.nameconverter = nmconverter;
	}

	public MemShareBaseStorageLr()
	{
			this(null,null);
	}


	public MemShareBaseStorageLr(String folderlayers) throws InitStorageException
	{
		this(null,null,null,folderlayers);
	}

	public void setObjAttrsFactory(AObjAttrsFactory attrsfactory)
	{
		if (attrsfactory == null)
			attrsfactory = new AObjAttrsFactory()
			{
				public IAttrs createLocaleByGisObjId(String objId,IBaseStorage storage,IAttrs boundAttrs) throws Exception
				{
					return new DefaultGisObjectAttrs(boundAttrs,objId);
				}
			};
		this.attrsfactory = attrsfactory;
	}

	public void rebindByObjAttrsFactory(AObjAttrsFactory attrsfactory) throws Exception
	{
		for (String objId : container.pool.keySet())
			container.pool.put(objId,attrsfactory.createByGisObjId(objId,parentstor,container.pool.get(objId)));
	}

	public AObjAttrsFactory getObjAttrsFactory()
	{
		return attrsfactory;
	}

	public void setStoragesfactory(IFactory<INodeStorage> storagesfactory)
	{
	}

	public void setViewProgress(IViewProgress viewProgress)
	{
	}

	public String getObjName()
	{
		return nodeId;
	}

	protected MemShareBaseStorageLr(AObjAttrsFactory attrsfactory,IAttrs defAttrs, INameConverter nameconverter)
	{
		setDefAttrs(defAttrs);
		setObjAttrsFactory(attrsfactory);
		setNameConverter(nameconverter);
	}

	protected MemShareBaseStorageLr(AObjAttrsFactory attrsfactory,INameConverter nameconverter)
	{
		setObjAttrsFactory(attrsfactory);
		setNameConverter(nameconverter);
	}

	public MemShareBaseStorageLr(AObjAttrsFactory attrsfactory,IAttrs defAttrs, INameConverter nameconverter, String folderlayers) throws InitStorageException
	{
		this(attrsfactory,defAttrs, nameconverter);
		if (folderlayers == null)
			folderlayers = getTmpFolder();
		else
		{
			folderlayers+="_DIR";
			File file = new File(folderlayers);
			if (file.exists() && !file.isDirectory())
				folderlayers=getTmpFolder();
		}
		this.folderlayers = folderlayers;

		File file = new File(folderlayers);
		if (!file.exists() && !file.mkdir())
				throw new InitStorageException("Can't make dir for temporary files:"+folderlayers);
	}

	private String getTmpFolder()
	{
		String folderlayers;
		do folderlayers = WebUtils.getRandomString();
		while (new File(folderlayers).exists());
		return folderlayers;
	}


	public long getLastModified()
	{
		long ixlm=new File(getIXFileName()).lastModified();
		long dtlm=new File(getDatFileName()).lastModified();
		long gmlm=new File(getGeomFileName()).lastModified();
		return Math.max(Math.max(ixlm,dtlm),gmlm);
	}

	public IBaseStorage filter(IBaseFilter filter) throws Exception
	{
		if (filter == null)
			return this;
		else
			return new FilteredStorage(filter,this);
	}

	public IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception
	{
		Pair<Integer, MRect> curveindex = container.graphobkects.get(curvId);
		if (curveindex != null)
			return incarnateByIndex(curveindex.first);
		throw new IOException("Can't find object by curveId: " + curvId);
	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter) throws Exception
	{
		return getMemStorageIterator(keysFilterObject(filter).iterator());
	}

	protected List<String> keysFilterObject(IBaseFilter filter) throws Exception
	{
		List<String> keys = new LinkedList<String>();
		if (filter instanceof IMBBFilter)
		{
			for (String key : container.curveorder)
			{
				Pair<Integer, MRect> pr = container.graphobkects.get(key);
				if (((IMBBFilter) filter).acceptObject(pr.second))
						keys.add(key);
			}
			orderKeys(keys);
		}
		else if (filter instanceof IKeyFilter)
		{
			Iterator<String> allkeys = getCurvesIds();
			while (allkeys.hasNext())
			{
				String key;
				if (((IKeyFilter) filter).acceptObject( key=allkeys.next()))
					keys.add(key);
			}
		}
		else
		{
			Iterator<IBaseGisObject> allobj = getAllObjects();
			while (allobj.hasNext())
			{
				IBaseGisObject iGisObject = allobj.next();
				if (filter.acceptObject(iGisObject))
					keys.add(iGisObject.getCurveId());
			}
		}
		return keys;
	}

	protected void orderKeys(List<String> keys)
	{
		Map<Integer, String> orderedkeys = new TreeMap<Integer, String>();
		for (String key : keys)
			orderedkeys.put(container.graphobkects.get(key).first, key);

		keys.clear();
		for (Integer index : orderedkeys.keySet())
			keys.add(orderedkeys.get(index));
	}

	protected String folderlayers;

	protected String flnm;//Имя файла для доступа к файловой системе
	protected  InputStream fis;//входной поток файла
	protected  int lastindexacc = 0;//индекс последнего достпа к файловой системе

	private long cnt;
	private class BufferedInputStreamExt extends BufferedInputStream
	{
		public BufferedInputStreamExt(InputStream in)
		{
			super(in);
		}

		public BufferedInputStreamExt(InputStream in, int size)
		{
			super(in, size);
		}


		public int read() throws IOException
		{
			cnt++;
			return super.read();
		}

		public int read(byte b[], int off, int len)
				throws IOException
		{
			int rv = super.read(b, off, len);
			if (rv >= 0)
				cnt += rv;
			return rv;
		}
		public void close() throws IOException
		{
			super.close();
		}
	}

	protected synchronized IBaseGisObject incarnateByIndex(int index) throws Exception
	{

		if (fis == null)
		{
			fis = new BufferedInputStreamExt(new FileInputStream(flnm));
			lastindexacc = 0;
		}
		if (index < lastindexacc)
		{
			fis.close();
			fis = new BufferedInputStreamExt(new FileInputStream(flnm));
			lastindexacc = 0;
		}

		if (index >= lastindexacc)
		{
			int n = index - lastindexacc;
			long scpt = 0;
			while (scpt != n)
			{
				long lsc = fis.skip(n - scpt);
				if (lsc < 0)
					throw new IOException();
				scpt += lsc;
			}
			lastindexacc = index;
		}

		cnt = 0;
		DataInputStream dis = new DataInputStream(fis);
		StreamROCurve rv_curev = new StreamROCurve(nameconverter);
		rv_curev.getSerializer(container.pool).loadFromStream(dis);
		rv_curev.setCurveIdPrefix(nodeId + GROUP_SEPARATOR);

		String id = rv_curev.getCurveId();
		if (container.pool.get(id)==null)
			container.pool.put(id, attrsfactory.createByGisObjId(id, parentstor, container.defAttrs));

		lastindexacc += cnt;
		return rv_curev;
	}



	/**
	 * Отдать индексы кривых которые входят в хранилище
	 * @return массив индексов входящих в хранилище
	 */
	public Iterator<String> getCurvesIds()
	{
		return getKeyIterator(keysCurveIds().iterator());
	}

	public IAttrs getObjAttrs(String curveid) throws Exception
	{
		IAttrs attrs = container.pool.get(curveid);
		if (attrs != null)
			return new DefaultAttrsImpl(attrs, true);
		else
			return attrs;
	}

	protected List<String> keysCurveIds()
	{
		List<String> keys=new LinkedList<String>();
		keys.addAll(container.curveorder);
		orderKeys(keys);
		return keys;
	}

	public synchronized void releaseStorage()
	{
		try
		{
			if (fis != null)
				fis.close();
			lastindexacc = 0;
			fis = null;
		} catch (IOException e)
		{//
		}
	}

	public Iterator<IBaseGisObject> getAllObjects()
	{
		return getMemStorageIterator(getCurvesIds());
	}

	protected String getDatFileName()
	{
		return folderlayers+ "/" + nodeId+".dat";
	}

	protected  String getGeomFileName()
	{
		return folderlayers + "/" + nodeId+".gm";
	}

	protected  String getIXFileName()
	{
		return folderlayers+ "/" + nodeId+".ix";
	}


	public Object[] init(Object... objs)
			throws Exception
	{
		for (Object obj : objs)
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.nodeId = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else
				this.folderlayers = ((String) attr.getValue());
		}
		releaseStorage();

		flnm = folderlayers + "/" + nodeId + ".gm";//Сохранить на файловую систему
		IAccessorImpl accesor = IAccessorImpl.getInstance();

		container=accesor.getContainerByName(nodeId);
		if (container==null)
			container=accesor.initStorName(getIXFileName(),getDatFileName(),nodeId,attrsfactory,parentstor);
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

	public void addObject(IBaseGisObject object) throws Exception
	{
		throw  new UnsupportedOperationException();
	}

	public void clearAll() throws Exception
	{
		throw  new UnsupportedOperationException();
	}//Создать новый редактируемый объект

}