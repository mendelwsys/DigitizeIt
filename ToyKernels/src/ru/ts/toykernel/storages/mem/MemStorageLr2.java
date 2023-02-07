package ru.ts.toykernel.storages.mem;

import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;
import ru.ts.utils.logger.SimpleLogger;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.toykernel.attrs.*;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.attrs.def.DefaultGisObjectAttrs;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.InitStorageException;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.FilteredStorage;
import ru.ts.toykernel.geom.IEditableGisObject;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.stream.StreamEditableCurve;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.IMBBFilter;
import ru.ts.toykernel.filters.IKeyFilter;
import ru.ts.factory.IFactory;
import ru.ts.factory.IObjectDesc;
import ru.ts.stream.ISerializer;

import java.util.*;
import java.io.*;

import su.web.WebUtils;

/**
 * Гибридное хранилище, постоянно держит в памяти только аттрибуты и индексы
 * объектов, остальное сбрасывает на диск
 */
public class MemStorageLr2 implements INodeStorage, ISerializer
{

	public static final String TYPENAME ="MEM_STORG2";//name of object
	protected IAttrsPool pool = new IAttrsPoolImpl();//Implementation of pool storage
	protected IAttrs defAttrs;//Default attributes for all objects of this storage
	protected INameConverter nameconverter;
	protected String nodeId;//node identifyer
	protected AObjAttrsFactory attrsfactory;//attribute factory
	protected INodeStorage parentstor;
	private IViewProgress viewProgress;



	public IAttrs getObjAttrs(String curveid) throws Exception
	{
		IAttrs attrs = pool.get(curveid);
		if (attrs != null)
			return new DefaultAttrsImpl(attrs, true);
		else
			return attrs;
	}
	
	private class MemStorageIterator implements Iterator<IBaseGisObject>
	{
		private Iterator<String> keys;

		MemStorageIterator(Iterator<String> keys)
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
				return incarnateByIndex(graphobkects.get(currentId).first);
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

	public IBaseStorage getStorageByCurveId(String curveId)
	{
		if (this.graphobkects.keySet().contains(curveId))
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
		this.defAttrs=defAttrs;
	}

	public IAttrs getDefAttrs()
	{
		return defAttrs;
	}

	protected String folderlayers;

	public void setNameConverter(INameConverter nmconverter)
	{
		if (nmconverter == null)
			nmconverter = new DefNameConverter();
		this.nameconverter = nmconverter;
	}

	public MemStorageLr2(String folderlayers) throws InitStorageException
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
		for (String objId : pool.keySet())
			pool.put(objId,attrsfactory.createByGisObjId(objId,parentstor,pool.get(objId)));
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
		this.viewProgress=viewProgress;
	}

	public String getObjName()
	{
		return nodeId;
	}

	public Object[] init(Object... objs) throws Exception
	{
		if (objs[0] instanceof DataInputStream)
			loadFromStream((DataInputStream)objs[0]);
		else
			throw new UnsupportedOperationException();
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		throw new UnsupportedOperationException();
	}

	protected MemStorageLr2(AObjAttrsFactory attrsfactory,IAttrs defAttrs, INameConverter nameconverter)
	{
		setDefAttrs(defAttrs);
		setObjAttrsFactory(attrsfactory);
		setNameConverter(nameconverter);
	}
	public MemStorageLr2(AObjAttrsFactory attrsfactory,IAttrs defAttrs, INameConverter nameconverter, String folderlayers) throws InitStorageException
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

	protected Map<String, Pair<Integer, MRect>> graphobkects = new HashMap<String, Pair<Integer, MRect>>();//Набор объектов входящих в индекс curveId-><curve_array_index,project_rect>
	protected List<String> curveorder = new ArrayList<String>();//Сохраняется на внешний носитель согласно порядку в этом списке

	public int getObjectsCount()
	{
		return graphobkects.size();
	}
	
	public long getLastModified()
	{
		return -1;
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
		return getEditableByCurveId(curvId);
	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter)     throws Exception
	{
		List<String> keys = new LinkedList<String>();
		if (filter instanceof IMBBFilter)
		{
			for (String key : curveorder)
			{
				Pair<Integer, MRect> pr = graphobkects.get(key);
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
		return new MemStorageIterator(keys.iterator());
	}

	private void orderKeys(List<String> keys)
	{
		Map<Integer, String> orderedkeys = new TreeMap<Integer, String>();
		for (String key : keys)
			orderedkeys.put(graphobkects.get(key).first, key);

		keys.clear();
		for (Integer index : orderedkeys.keySet())
			keys.add(orderedkeys.get(index));
	}

	protected IEditableGisObject getEditableByCurveId(String curvId) throws Exception
	{
		Pair<Integer, MRect> curveindex = graphobkects.get(curvId);
		if (curveindex != null)
			return incarnateByIndex(curveindex.first);
		throw new IOException("Can't find object by curveId: " + curvId);
	}


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

	protected synchronized StreamEditableCurve incarnateByIndex(int index) throws Exception
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
		StreamEditableCurve rv_curev = new StreamEditableCurve(nameconverter);
		rv_curev.getSerializer(pool).loadFromStream(dis);
		rv_curev.setCurveIdPrefix(nodeId + INodeStorage.GROUP_SEPARATOR);
		lastindexacc += cnt;
		return rv_curev;
	}


	private class KeyIterator implements Iterator<String>
	{
		private Iterator<String> keys;

		KeyIterator(Iterator<String> keys)
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

	/**
	 * Отдать индексы кривых которые входят в слой
	 *
	 * @return массив индексов
	 */
	public Iterator<String> getCurvesIds()
	{
		List<String> keys=new LinkedList<String>();
		keys.addAll(curveorder);
		orderKeys(keys);
		return new KeyIterator(keys.iterator());
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
		Iterator<String> kiter = getCurvesIds();
		return new MemStorageIterator(kiter);
	}

	/**
	 * @param dis - входной поток содержащий слой
	 * @throws Exception - исключения
	 */
	public void loadFromStream(DataInputStream dis)
			throws Exception
	{
		fis=null;
		lastindexacc=0;
		//Загружаем байтовый массив
		int arraysize = dis.readInt();
		nodeId = dis.readUTF();
		{
			flnm = folderlayers + "/" + nodeId;//Сохранить на файловую систему
			if (!new File(flnm).exists())
			{
				byte[] curveArray = new byte[arraysize];
				int sz = 0;
				int count = 0;
				while ((count = dis.read(curveArray, sz, arraysize - sz)) >= 0 && (sz += count) < arraysize)
					SimpleLogger.Singleton.getLoger().getLog().println("sz:" + sz);
				if (count < 0)
				{
					SimpleLogger.Singleton.getLoger().getLog().println("Error reading map form file");
					throw new EOFException("Error reading map form file");
				}

				FileOutputStream fout = new FileOutputStream(flnm);
				fout.write(curveArray);
				fout.flush();
				fout.close();
			}
			else
			{
				int sz = 0;
				long count = 0;
				while ((count=dis.skip(arraysize-sz))>=0 && (sz += count) < arraysize){}
					//SimpleLogger.Singleton.getLoger().getLog().println("skiped sz:" + sz);
			}
		}
//Загружаем графические объекты
		int size = dis.readInt();
		curveorder = new ArrayList<String>(size);
		while (size > 0)
		{
			String curveid = dis.readUTF();
			int index = dis.readInt();

			double minX = dis.readDouble();
			double minY = dis.readDouble();
			double maxX = dis.readDouble();
			double maxY = dis.readDouble();

			MRect rect = new MRect(new MPoint(minX, minY), new MPoint(maxX, maxY));
			graphobkects.put(nodeId + GROUP_SEPARATOR + curveid, new Pair<Integer, MRect>(index, rect));
			curveorder.add(nodeId + GROUP_SEPARATOR + curveid);
			size--;
		}

		size = curveorder.size();


		while (size > 0)
		{
			int attrsz = dis.readInt();
			if (attrsz > 0)
			{
				StreamEditableCurve curve = incarnateByIndex(dis.readInt());
				String id=curve.getCurveId();
				IAttrs attrs = attrsfactory.createByGisObjId(id,parentstor,defAttrs);
				while (attrsz > 0)
				{
					ObjectInputStream objis = new ObjectInputStream(dis);
					String key = objis.readUTF();
					Object obj = objis.readObject();
					attrs.put(key, new DefAttrImpl(key, obj));
					attrsz--;
				}
				pool.put(id, attrs);
			}
			size--;
		}

//		for (String curveId : curveorder) //TODO проверять сгенерирован ли объем и если нет генерировать его
//		{
//			StreamEditableCurve curve = incarnateByIndex(graphobkects.get(curveId).first);
//			if (!graphobkects.get(curveId).second.equals(curve.getMBB(null)))
//			{
//				graphobkects.get(curveId).second=curve.getMBB(null);
//				System.out.println("Not Eq");
//			}
////			else
////			    System.out.println("Eq");
//		}
		releaseStorage();
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		File fl = new File(flnm);
		long ln= fl.length();
		//Сохраняем байтовый массив
		dos.writeInt((int) ln);
		dos.writeUTF(nodeId);

		BufferedInputStream fin = new BufferedInputStream(new FileInputStream(fl));
		byte[] curveArray=new byte[8*1024];
		int sz = 0;
		int count = 0;
		while ((sz += count) < ln && (count = fin.read(curveArray)) >= 0)
			dos.write(curveArray, 0, count);
		if (count < 0)
		{
			SimpleLogger.Singleton.getLoger().getLog().println("Error reading map form file");
			throw new EOFException("Error reading map form file");
		}
		fin.close();


//Сохраняем графические объекты
		dos.writeInt(graphobkects.size());
		for (String key : curveorder)//Кривые Сохраняются в порядке рисования
		{
			String[] lrId2curveId = key.split("[" + INodeStorage.GROUP_SEPARATOR + "]");
			dos.writeUTF(lrId2curveId[2]);
			Pair<Integer, MRect> index2mbb = graphobkects.get(key);
			dos.writeInt(index2mbb.first);
			dos.writeDouble(index2mbb.second.p1.x);
			dos.writeDouble(index2mbb.second.p1.y);
			dos.writeDouble(index2mbb.second.p4.x);
			dos.writeDouble(index2mbb.second.p4.y);
		}

//допишем атрибуты объектов
		for (String curveId : curveorder)
		{
			int objsizeattrs = 0;
			IAttrs objattrs = pool.get(curveId);

			Set<String> objkeyattrs = null; //формируем те ключи объекты по которым отличаются от объектов по умолчанию
			if (objattrs!=null)
			{
				objkeyattrs = new HashSet<String>();
				for (String keyattr : objattrs.keySet())
				{
					IDefAttr iDefAttr = defAttrs.get(keyattr);
					if (iDefAttr == null || !iDefAttr.equals(objattrs.get(keyattr)))
						objkeyattrs.add(keyattr);
				}
				objsizeattrs = objkeyattrs.size();
			}
			dos.writeInt(objsizeattrs);//кол-во атрибутов
			if (objsizeattrs > 0)
			{
				dos.writeInt(graphobkects.get(curveId).first);//индекс кривой которой принадлежат аттрибуты
				for (String keyattr : objkeyattrs)
				{
					IDefAttr objAttr = objattrs.get(keyattr);
					ObjectOutputStream objos = new ObjectOutputStream(dos);
					objos.writeUTF(objAttr.getName());
					objos.writeObject(objAttr.getValue());
					objos.flush();
				}
			}
		}

	}

}