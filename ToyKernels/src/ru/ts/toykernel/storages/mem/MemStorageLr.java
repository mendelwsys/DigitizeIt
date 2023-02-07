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
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.stream.StreamEditableCurve;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.IMBBFilter;
import ru.ts.factory.IFactory;
import ru.ts.factory.IObjectDesc;


import java.util.*;
import java.io.*;

/**
 * Memmory Storage
 */
public class MemStorageLr implements INodeStorage
{
	private INameConverter storNm2CodeNm;
	public static final String TYPENAME ="MEM_STORG";//name of object
	protected AObjAttrsFactory attrsfactory;
	protected INodeStorage parentstor;//parent storage
	private IViewProgress viewProgress;//Создать новый редактируемый объект

	//Взять объект на редактирование
//	public IEditableGisObject getEditableObject(String curveId) throws Exception
//	{
//		Pair<Integer, MRect> curveindex = graphobkects.get(curveId);
//		if (curveindex != null)
//			return incarnateByIndex(curveindex.first);
//		throw new IOException("Can't find object by curveId: " + curveId);
//	}

//	public void test(IBaseGisObject obj) throws Exception
//	{
//
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		DataOutputStream dos = new DataOutputStream(bos);
//		StreamEditableCurve curve = (StreamEditableCurve) obj;
//		ISerializer serializer = curve.getSerializer(pool);
//		serializer.saveToStream(dos);
//		dos.flush();
//		DataInputStream dis=new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
//		serializer.loadFromStream(dis);
//	}

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
		private String currentId;

		MemStorageIterator(Iterator<String> keys)
		{
			this.keys = keys;
		}

		public boolean hasNext()
		{
			return keys.hasNext();
		}

		public IBaseGisObject next()
		{
			currentId = keys.next();
			try
			{
				return incarnateByIndex(graphobkects.get(currentId).first);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public void remove()
		{
				throw new UnsupportedOperationException();
		}
	}


	protected byte[] curveArray = new byte[0];//массив который содержит объекты слоя
	protected int busysize = 0;  //кол-во байтов занятых в массиве информацией об объектах

	protected IAttrsPool pool = new IAttrsPoolImpl();//Implementation of pool storage
	protected IAttrs defAttrs;//Default attributes for all objects of this storage

	public IAttrs getDefAttrs()
	{
		return defAttrs;
	}

	public void setDefAttrs(IAttrs defAttrs)
	{
		if (defAttrs == null)
			defAttrs = new DefaultAttrsImpl();
		this.defAttrs=defAttrs;
	}

	public void setNameConverter(INameConverter nmconverter)
	{
		if (nmconverter == null)
			nmconverter = new DefNameConverter();
		this.storNm2CodeNm = nmconverter;
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
			pool.put(objId,attrsfactory.createByGisObjId(objId, parentstor,pool.get(objId)));
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

	public MemStorageLr()
	{
		this(null,null,null);
	}

	public MemStorageLr(AObjAttrsFactory attrsfactory,IAttrs defAttrs, INameConverter storNm2CodeNm)
	{
		setDefAttrs(defAttrs);
		setObjAttrsFactory(attrsfactory);
		setNameConverter(storNm2CodeNm);
	}

	protected Map<String, Pair<Integer, MRect>> graphobkects = new HashMap<String, Pair<Integer, MRect>>();//Набор объектов входящих в индекс curveId-><curve_array_index,project_rect>
	protected List<String> curveorder = new ArrayList<String>();//Сохраняется на внешний носитель согласно порядку в этом списке

//	protected void tstincarnateAllLayer(IProjConverter converter, MPoint currentP0) throws IOException
//	{
//		long startinc = System.currentTimeMillis();
//		Set<String> curvids = graphobkects.keySet();
//
//		for (String curvid : curvids)
//		{
//			MPoint[][] arrpoint = getEditableByCurveId(curvid).getRawGeometry();
//			LinkedList<Pair<int[], int[]>> llcrd = new LinkedList<Pair<int[], int[]>>();
//			for (MPoint[] points : arrpoint)
//			{
//				int[] x = new int[points.length];
//				int[] y = new int[points.length];
//				for (int i = 0; i < points.length; i++)
//				{
//					Point pi = converter.getDstPointByPoint(points[i], currentP0);
//					x[i] = pi.x;
//					y[i] = pi.y;
//				}
//				llcrd.add(new Pair<int[], int[]>(x, y));
//			}
//		}
//
//
//		long resinc = System.currentTimeMillis() - startinc;
//		System.out.println("resinc = " + resinc + " for lrid:" + getNodeId());
//
//	}




	/**
	 * Полностью заменить объекты в слое
	 *
	 * @param curveArray   - массив в формате слоя
	 * @param graphobkects - графические объекты
	 * @throws Exception -
	 */
	public void initLayerByCurves(byte[] curveArray,
								  Map<String, Pair<Integer, MRect>> graphobkects,
								  List<String> curveorder) throws Exception
	{
		this.curveArray = curveArray;
		this.busysize = this.curveArray.length;
		this.graphobkects = graphobkects;
		this.curveorder.clear();
		this.curveorder.addAll(curveorder);
	}


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
		throw new UnsupportedOperationException();
	}

	public IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception
	{
		Pair<Integer, MRect> curveindex = graphobkects.get(curvId);
		if (curveindex != null)
			return incarnateByIndex(curveindex.first);
		throw new IOException("Can't find object by curveId: " + curvId);
	}


	protected StreamEditableCurve incarnateByIndex(int index) throws Exception
	{

		DataInputStream ba = new DataInputStream(new ByteArrayInputStream(curveArray, index, curveArray.length - index));
		StreamEditableCurve rv_curev = new StreamEditableCurve(storNm2CodeNm);
		rv_curev.getSerializer(pool).loadFromStream(ba);
		rv_curev.setCurveIdPrefix(nodeId + INodeStorage.GROUP_SEPARATOR);
		return rv_curev;
	}

	/**
	 * Отдать индексы кривых которые входят в слой
	 *
	 * @return массив индексов
	 */
	public Iterator<String> getCurvesIds()
	{
		return new LinkedList<String>(curveorder).iterator();
	}


	public Iterator<IBaseGisObject> filterObjs(final IBaseFilter filter)    throws Exception
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

	public Iterator<IBaseGisObject> getAllObjects()
	{
		Iterator<String> kiter = getCurvesIds();
		return new MemStorageIterator(kiter);
	}

	private int lobjid;//

	public synchronized String getnextObjId(String Prefix)
	{
		if (lobjid < graphobkects.size())
			lobjid = graphobkects.size();

		while (graphobkects.get(Prefix + "_" + lobjid) != null)
			lobjid++;
		return Prefix + "_" + lobjid;
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		//Сохраняем байтовый массив
		dos.writeInt(busysize);
		dos.writeUTF(nodeId);

		dos.write(curveArray, 0, busysize);

//Сохраняем индексы объектов
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

	/**
	 * Разделение произведено для того что бы можно вклинится в процесс загрузки слоя
	 *
	 * @param dis - входной поток содержащий слой
	 * @throws Exception - исключения
	 */

	public void loadFromStream(DataInputStream dis)
			throws Exception
	{
		//Загружаем байтовый массив
		busysize = dis.readInt();
		nodeId = dis.readUTF();
		curveArray = new byte[busysize];
		int sz = 0;
		int count = 0;
		while ((count = dis.read(curveArray, sz, busysize - sz)) >= 0 && (sz += count) < busysize)
			SimpleLogger.Singleton.getLoger().getLog().println("sz:" + sz);
		if (count < 0)
			throw new EOFException("Error reading map form file");
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
				String id = curve.getCurveId();
				IAttrs attrsDefault = attrsfactory.createByGisObjId(id, parentstor,defAttrs);
				pool.put(id, attrsDefault);
				while (attrsz > 0)
				{
					ObjectInputStream objis = new ObjectInputStream(dis);
					String key = objis.readUTF();
					Object obj = objis.readObject();
					attrsDefault.put(key, new DefAttrImpl(key, obj));
					attrsz--;
				}
			}
			size--;
		}

//		for (String curveId : curveorder) TODO проверять сгенерирован ли объем и если нет генерировать его
//		{
//			StreamEditableCurve curve = incarnateByIndex(graphobkects.get(curveId).first);
//			graphobkects.get(curveId).second = curve.getMBB(null);
//		}
	}

	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}

	private String nodeId;

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
		this.parentstor =parent;
	}

	public Collection<INodeStorage> getChildStorages()
	{
		return new LinkedList<INodeStorage>();
	}


}
