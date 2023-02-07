package ru.ts.toykernel.storages.simpl;

import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.IEditableStorageOper;
import ru.ts.toykernel.storages.mem.SimpleMemStorage;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.IEditableGisObject;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.utils.gui.elems.IViewProgress;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.io.DataOutputStream;
import java.io.DataInputStream;

/**
 * Тестовое хранилище объектов
 * ru.ts.toykernel.storages.simpl.TtSimpleInitable
 */
public class TtSimpleInitable extends BaseInitAble
		implements INodeStorage, IEditableStorageOper

{

	public IAttrs getObjAttrs(String curveid) throws Exception
	{
		return getBaseGisByCurveId(curveid).getObjAttrs();
	}
	
	protected SimpleMemStorage memstor;
	protected INodeStorage parent;

	public TtSimpleInitable()
	{
	}

	public int getObjectsCount()
	{
		return memstor.getObjectsCount();
	}

	public long getLastModified()
	{
		return memstor.getLastModified();
	}

	public IBaseStorage filter(IBaseFilter filter) throws Exception
	{
		return memstor.filter(filter);
	}

	public IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception
	{
		return memstor.getBaseGisByCurveId(curvId);
	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter) throws Exception
	{
		return memstor.filterObjs(filter);
	}

	public Iterator<IBaseGisObject> getAllObjects() throws Exception
	{
		return memstor.getAllObjects();
	}

	public Iterator<String> getCurvesIds() throws Exception
	{
		return memstor.getCurvesIds();
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public IBaseStorage getStorageByCurveId(String curveId) throws Exception
	{
		Iterator<String> it = memstor.getCurvesIds();
		while (it.hasNext())
		{
			if (it.next().equals(curveId))
				return this;
		}
		return null;
	}

	public String getNodeId()
	{
		return getObjName();
	}

	public INodeStorage getParentStorage()
	{
		return parent;
	}

	public void setParentStorage(INodeStorage parent)
	{
		this.parent=parent;
	}

	public Collection<INodeStorage> getChildStorages()
	{
		return new LinkedList<INodeStorage>();
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
		throw new UnsupportedOperationException();
	}

	public void setObjAttrsFactory(AObjAttrsFactory attrsfactory)
	{
		throw new UnsupportedOperationException();
	}

	public AObjAttrsFactory getObjAttrsFactory()
	{
		throw new UnsupportedOperationException();
	}

	public void rebindByObjAttrsFactory(AObjAttrsFactory attrsfactory) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void setStoragesfactory(IFactory<INodeStorage> storagesfactory)
	{
	}

	public void setViewProgress(IViewProgress viewProgress)
	{
	}


	private List<IBaseGisObject> gisobjs=new LinkedList<IBaseGisObject>();
	public Object[] init(Object ... objs) throws Exception
	{
		super.init(objs);
		memstor= new SimpleMemStorage(gisobjs);
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.GISO_TAGNAME))
			gisobjs.add((IBaseGisObject)attr.getValue());
		return null;
	}

	public void addObject(IBaseGisObject object) throws Exception
	{
		memstor.addObject(object);
	}

	public void clearAll() throws Exception
	{
		memstor.clearAll();
	}

	public IEditableGisObject createObject(String geotype) throws Exception
	{
		return memstor.createObject(geotype);
	}

	public IBaseGisObject removeObject(String curveId) throws Exception
	{
		return memstor.removeObject(curveId);
	}

	public IEditableGisObject getEditableObject(String curveId) throws Exception
	{
		return memstor.getEditableObject(curveId);
	}
}
