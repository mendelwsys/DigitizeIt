package ru.ts.toykernel.storages.mem;

import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.geom.IEditableGisObject;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.EditableCurve;
import ru.ts.toykernel.geom.def.stream.StreamEditableCurve;
import ru.ts.toykernel.storages.IEditableStorage;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.IMBBFilter;
import ru.ts.utils.data.Pair;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;

import java.io.*;
import java.util.*;

/**
 * ru.ts.toykernel.storages.mem.MemEditableStorageLr
 * Редактируемый слой фильтруется с правилами включ в себя объекты редактирования
 *
 * 1-й фильтр - содержит только идентифкаторы новых объектов и применяется для рисования новых объектов
 * в слое редактирования
 *
 * 2-й фильтр так же содержит только новые объекты и предназанчен для того что бы не допустить их рисования на исходном слое
 *
 * 3-й фильтр содержит только родительские объекты новых объектов и используется в правиле рисования для того что бы
 * специально прорисовывать отфильтрованные объекты
 *
 */
public class MemEditableStorageLr extends MemBaseStorageLr implements IEditableStorage
{
	protected IXMLObjectDesc desc;

	protected class MemBaseStorageIterator2 extends MemBaseStorageIterator
	{
		String currentId = null;

		MemBaseStorageIterator2(Iterator<String> keys)
		{
			super(keys);
		}

		public IBaseGisObject next()
		{
			currentId = keys.next();
			try
			{
				IBaseGisObject rv = id2Editable.get(currentId);
				if (rv == null)
					rv = incarnateByIndex(graphobkects.get(currentId).first);
				return rv;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public void remove()
		{
			try
			{
				String remId = currentId;
				currentId = keys.next();
				removeObject(remId);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new UnsupportedOperationException(e);
			}
		}
	}

	protected Iterator<IBaseGisObject> getMemStorageIterator(Iterator<String> keys)
	{
		return new MemBaseStorageIterator2(keys);
	}

	//класс для редактирования объектов
	protected class InEditableCurve extends EditableCurve
	{

		protected InEditableCurve(String geotype, String curveId, IAttrsPool attrsPool, INameConverter storNm2CodeNm, String parentId)
		{
			super(geotype, curveId, attrsPool, storNm2CodeNm, parentId);
		}

		protected InEditableCurve(IBaseGisObject obj, String parentId) throws Exception
		{
			super(obj, parentId);
		}

		protected InEditableCurve(INameConverter storNm2CodeNm)
		{
			super(storNm2CodeNm);
		}
	}


	public static final String TYPENAME = "MEM_STORG4";//name of the object

	protected MemEditableStorageLr(AObjAttrsFactory attrsfactory, IAttrs defAttrs, INameConverter storNm2CodeNm, String folderlayers, String nodeId)
	{
		super(attrsfactory, defAttrs, storNm2CodeNm);
		this.folderlayers = folderlayers;
		this.nodeId = nodeId;
		releaseStorage();
		flnm = folderlayers + "/" + nodeId + ".gm";//Сохранить на файловую систему
	}

	public MemEditableStorageLr()
	{
		super(null,null,null);
	}

	public void truncate()  throws Exception
	{
		releaseStorage();
		MemEditableStorageLr.truncate(folderlayers,nodeId);
		loadIxDat();
	}

	static public void truncate(String folderlayers,String nodeId)
	{
		new File(folderlayers + "/" + nodeId + ".gm").delete();
		new File(folderlayers + "/" + nodeId + ".ix").delete();
		new File(folderlayers + "/" + nodeId + ".dat").delete();
	}

	public MemEditableStorageLr(String folderlayers, String nodeId) throws Exception
	{
		super(null, null, null);
		this.folderlayers = folderlayers;
		this.nodeId = nodeId;
		releaseStorage();
		flnm = getGeomFileName();//Сохранить на файловую систему
		loadIxDat();
	}

	public void loadFromStream(DataInputStream dis)
			throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	protected void saveDats() throws Exception
	{
		DataOutputStream dos_dat = null;

		try
		{
			dos_dat = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(getDatFileName())));
			//Сначала сохраним атрибуты по умолчанию
			dos_dat.writeInt(defAttrs.size());

			for (String attrname : defAttrs.keySet())
			{
				ObjectOutputStream obos = new ObjectOutputStream(dos_dat);//TODO перенести выше
				IDefAttr iDefAttr = defAttrs.get(attrname);
				obos.writeUTF(iDefAttr.getName());
				obos.writeObject(iDefAttr.getValue());
			}

			for (String curveid : curveorder)
			{
				IAttrs attrs = pool.get(curveid);

				int size;
				if (attrs != null)
					size = attrs.keySet().size();
				else
					size=0;
				dos_dat.writeInt(size);
				if (size > 0)
				{
					dos_dat.writeInt(graphobkects.get(curveid).first);
					for (String attrname : attrs.keySet())
					{
						ObjectOutputStream obos = new ObjectOutputStream(dos_dat);//TODO перенести выше
						IDefAttr iDefAttr = attrs.get(attrname);
						obos.writeUTF(iDefAttr.getName());
						obos.writeObject(iDefAttr.getValue());
					}
				}
			}
		}
		finally
		{
			if (dos_dat != null)
			{
				dos_dat.flush();
				dos_dat.close();
			}
		}
	}

	protected void saveIds() throws Exception
	{
		DataOutputStream dos_ix = null;
		try
		{
			dos_ix = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(getIXFileName())));
			dos_ix.writeInt(curveorder.size());
			for (String curveid : curveorder)
			{
				String[] lrId2curveId = curveid.split("[" + INodeStorage.GROUP_SEPARATOR + "]");
				dos_ix.writeUTF(lrId2curveId[2]);
				Pair<Integer, MRect> params = graphobkects.get(curveid);

				dos_ix.writeInt(params.first); //Запись смещения в файде геометрии

				dos_ix.writeDouble(params.second.p1.x); //Запись объема объекта
				dos_ix.writeDouble(params.second.p1.y);
				dos_ix.writeDouble(params.second.p4.x);
				dos_ix.writeDouble(params.second.p4.y);
			}
		}
		finally
		{
			if (dos_ix != null)
			{
				dos_ix.flush();
				dos_ix.close();
			}
		}
	}

	//Мы считаем что аттрибуты объектов которые сгенерированы этим хранилищем
	//Уже включены в систему хранения  атрибутов
	protected void addGeom() throws Exception
	{
		for (String curveId : removedIds)
		{
			graphobkects.remove(curveId);
			curveorder.remove(curveId);
			pool.remove(curveId);
		}

		if (id2Editable.size() > 0)
		{
			DataOutputStream dos = null;
			try
			{
				File file = new File(flnm);

				long totallength = file.length();
				dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, true)));

				Set<String> l_curveorder=new HashSet<String>(curveorder);//Для ускорения процесса поиска в списке

				Set<String> keys = new TreeSet<String>(id2Editable.keySet());


				//for (IEditableGisObject iEditableGisObject : id2Editable.values())
				for (String key : keys)
				{
					IEditableGisObject iEditableGisObject=id2Editable.get(key);
					if (!iEditableGisObject.isChangeGeom())
						continue;

					int initsz=dos.size();
					StreamEditableCurve sec = new StreamEditableCurve(nameconverter);
					iEditableGisObject.setInstance(sec);
					if (sec.getParentId() != null)
						sec.setCurveId(sec.getParentId());

					sec.rebuildGisValume();
					sec.getSerializer(pool).saveToStream(dos);
					{
						//Включить текущий объект в инфраструктуру хранилища вместо родительского
						String curveId = sec.getCurveId();
						Pair<Integer, MRect> params = graphobkects.get(curveId);
						if (params == null)
							graphobkects.put(curveId, params = new Pair<Integer, MRect>(-1, new MRect()));

						params.first = (int) totallength;
						params.second = sec.getMBB(null);

						if (l_curveorder.contains(curveId))
							while (curveorder.remove(curveId))
								System.out.println("remove curveId:"+ curveId);
						l_curveorder.add(curveId);
						curveorder.add(curveId);
					}
					totallength += (dos.size()-initsz);
				}
				id2Editable.clear();
				parentIds.clear();
				removedIds.clear();
			}
			finally
			{
				if (dos != null)
				{
					dos.flush();
					dos.close();
				}
			}
		}
	}

	//Сохранить геометрию
	protected void saveGeom() throws Exception
	{
		DataOutputStream dos_gm = null;
		try
		{
			dos_gm = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(flnm + ".tmp")));
			for (String curveId : curveorder)
			{
				Pair<Integer, MRect> ix2rect = graphobkects.get(curveId);
				IBaseGisObject curve = incarnateByIndex(ix2rect.first);
				if (curve instanceof IStreamStorageable)
				{
					ix2rect.first = dos_gm.size();
					((IStreamStorageable) curve).getSerializer(pool).saveToStream(dos_gm);
				}
				else
					throw new UnsupportedOperationException("Can't save not streamable object");
			}
			dos_gm.flush();
			dos_gm.close();

			File ifl = new File(flnm + ".tmp");
			releaseStorage();
			new File(flnm).delete();
			System.out.println("ifl = " + ifl.renameTo(new File(flnm)));
			ifl.delete();
		}
		finally
		{
			if (dos_gm != null)
			{
				dos_gm.flush();
				dos_gm.close();
			}
		}
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

		loadIxDat();
		return null;
	}

	protected void loadIxDat()
			throws Exception
	{
		curveorder.clear();
		graphobkects.clear();
		pool.clear();

		DataInputStream dis_dat = null;
		DataInputStream dis_ix = null;
		long tm = System.currentTimeMillis();
		try
		{
//Читаем индекс
			File fileix = new File(getIXFileName());
			int size = 0;
			Map<Integer, String> index2curveId = null;
			if (fileix.exists())
			{
				dis_ix = new DataInputStream(new BufferedInputStream(new FileInputStream(fileix)));
				size = dis_ix.readInt();
				curveorder = new ArrayList<String>(size);
				index2curveId = new HashMap<Integer, String>();
				while (size > 0)
				{
					String curveid = dis_ix.readUTF();
					int index = dis_ix.readInt();

					double minX = dis_ix.readDouble();
					double minY = dis_ix.readDouble();
					double maxX = dis_ix.readDouble();
					double maxY = dis_ix.readDouble();

					MRect rect = new MRect(new MPoint(minX, minY), new MPoint(maxX, maxY));
					graphobkects.put(nodeId + GROUP_SEPARATOR + curveid, new Pair<Integer, MRect>(index, rect));
					curveorder.add(nodeId + GROUP_SEPARATOR + curveid);

					index2curveId.put(index, nodeId + GROUP_SEPARATOR + curveid);

					size--;
				}
				dis_ix.close();
			}
//Читаем аттрибуты слоев  (Даже если ix не существует тогда файл dat может быть для хранения аттрибутов по умолчанию)
			File filedat = new File(getDatFileName());
			if (!filedat.exists())
				fileix.delete();
			else
			{
				dis_dat = new DataInputStream(new BufferedInputStream(new FileInputStream(filedat)));
				int cntattrs = dis_dat.readInt();
				defAttrs = new DefaultAttrsImpl();
				while (cntattrs > 0)
				{
					ObjectInputStream obis = new ObjectInputStream(dis_dat);
					String attrname = obis.readUTF();
					Object val = obis.readObject();
					defAttrs.put(attrname, new DefAttrImpl(attrname, val));
					cntattrs--;
				}

				size = curveorder.size();
				while (size > 0)
				{
					int attrsz = dis_dat.readInt();

					if (attrsz > 0)
					{
						String curveId = index2curveId.get(dis_dat.readInt());
						if (curveId == null)
							throw new Exception("Error init of MemStorgeLr:" + this.getClass().getCanonicalName());
						IAttrs attrs = attrsfactory.createByGisObjId(curveId, parentstor, defAttrs);
						pool.put(curveId, attrs);

						while (attrsz > 0)
						{
							ObjectInputStream objis = new ObjectInputStream(dis_dat);
							String key = objis.readUTF();
							Object obj = objis.readObject();
							attrs.put(key, new DefAttrImpl(key, obj));
							attrsz--;
						}

					}
					size--;
				}
			}
		}
		finally
		{
			if (dis_ix != null)
				dis_ix.close();
			if (dis_dat != null)
				dis_dat.close();

			System.out.println(nodeId + " time init:" + (System.currentTimeMillis() - tm));
		}
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

	public IEditableGisObject createObject(String geotype) throws Exception
	{
		int ii = curveorder.size()+id2Editable.size();
		String newcurveId;
		do
		{
			newcurveId = "auto" + (ii++);
		}
		while (graphobkects.get(newcurveId) != null || id2Editable.get(newcurveId)!=null);

		InEditableCurve editCurve = new InEditableCurve(geotype, newcurveId, pool, nameconverter, null);
		editCurve.setCurveIdPrefix(nodeId + INodeStorage.GROUP_SEPARATOR);
		newcurveId = editCurve.getCurveId();
		//Включаем кривую в инфраструктуру хранилища
		pool.put(newcurveId, attrsfactory.createByGisObjId(newcurveId, parentstor, defAttrs));
		id2Editable.put(newcurveId, editCurve);
		return editCurve;
	}

	//Удалить объект
	public IBaseGisObject removeObject(String curveId) throws Exception
	{
		if (id2Editable.keySet().contains(curveId))
		{
			IEditableGisObject rv=id2Editable.remove(curveId);
			parentIds.remove(rv.getParentId());
			removedIds.add(rv.getParentId());
			pool.remove(curveId);
			return incarnateByIndex(graphobkects.get(rv.getParentId()).first);
		}
		else if (graphobkects.get(curveId)!=null)
		{
			removedIds.add(curveId);
			return incarnateByIndex(graphobkects.get(curveId).first);
		}
		return null;

	}


	//Дополнительные объекты с ссылкой на родительский объект и без таковой (новые объекты)
	protected Map<String, IEditableGisObject> id2Editable = new HashMap<String, IEditableGisObject>();
	protected Set<String> parentIds = new HashSet<String>();//Родительские ключи
	protected Set<String> removedIds = new HashSet<String>();//Ключи родителей для удаления


	//Взять объект на редактирование
	public IEditableGisObject getEditableObject(String curveId) throws Exception
	{
		if (removedIds.contains(curveId))
			return null;
		if (id2Editable.keySet().contains(curveId))
			return id2Editable.get(curveId);
		else if (parentIds.contains(curveId))
			return id2Editable.get(getChildId(curveId));
		
		IBaseGisObject base = getBaseGisByCurveId(curveId);
		String baseCurveId = base.getCurveId();
		IEditableGisObject rv = new InEditableCurve(base, baseCurveId);
		rv.setCurveId(getChildId(baseCurveId));
		id2Editable.put(rv.getCurveId(), rv);
		parentIds.add(baseCurveId);
		return rv;
	}

	protected String getChildId(String baseCurveId)
	{
		return baseCurveId + INodeStorage.GROUP_SEPARATOR + "CHILD";
	}

	//Дописать новые объекты в файловое хранилище
	public boolean notcommited() throws Exception
	{
		return id2Editable.size()>0 || removedIds.size()>0 || parentIds.size()>0;
	}

	public Set<String> getNotCommited()
	{
		final Set<String> rv = new HashSet<String>(id2Editable.keySet());
		rv.addAll(removedIds);
		return rv;
	}

	public int getSizeNotCommited()
	{
		return id2Editable.size();
	}

	//Дописать новые объекты в файловое хранилище
	public void commit() throws Exception
	{
		releaseStorage();
		addGeom();
		saveIds();
		saveDats();
	}

	public void rollback() throws Exception
	{
		if (notcommited())
		{
			id2Editable.clear();
			parentIds.clear();
			removedIds.clear();
			loadIxDat();//rollback for delete data
		}
	}

	//Удалить из файла пространство которые занимали удаленные объекты
	public void rearange() throws Exception
	{
		releaseStorage();
		saveGeom();
		saveIds();
		saveDats();
	}

	public void setViewProgress(IViewProgress viewProgress)
	{

	}

	protected List<String> keysFilterObject(IBaseFilter filter) throws Exception
	{
		List<String> keys = super.keysFilterObject(filter);
		if (filter instanceof IMBBFilter)
		{
			for (IEditableGisObject iEditableGisObject : id2Editable.values())
			{
				if (((IMBBFilter) filter).acceptObject(iEditableGisObject.getMBB(null)))
					keys.add(iEditableGisObject.getCurveId());
			}
		}
		return keys;
	}

	public IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception
	{
		IBaseGisObject rv = id2Editable.get(curvId);
		if (rv == null)
			rv = super.getBaseGisByCurveId(curvId);
		return rv;
	}

	/**
	 * @return all object Ids in storage
	 */
	public Iterator<String> getCurvesIds()
	{
		List<String> keys = keysCurveIds();
		keys.addAll(id2Editable.keySet());
		return getKeyIterator(keys.iterator());
	}
}