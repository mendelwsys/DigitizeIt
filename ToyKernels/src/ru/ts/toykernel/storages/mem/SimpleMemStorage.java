package ru.ts.toykernel.storages.mem;

import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.IEditableGisObject;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.IEditableStorageOper;
import ru.ts.toykernel.attrs.IAttrs;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

/**
 * Simple memmory storage
 */
public class SimpleMemStorage implements IBaseStorage, IEditableStorageOper
{


	public IAttrs getObjAttrs(String curveid) throws Exception
	{
		return getBaseGisByCurveId(curveid).getObjAttrs();
	}


	protected Collection<IBaseGisObject> gisobjs;

	public SimpleMemStorage()
	{
		gisobjs=new LinkedList<IBaseGisObject>();
	}


	public SimpleMemStorage(IBaseGisObject gisobj)
	{
		gisobjs=new LinkedList<IBaseGisObject>();
		gisobjs.add(gisobj);
	}	
	public SimpleMemStorage(Collection<IBaseGisObject> gisobjs)
	{
		this.gisobjs = gisobjs;
	}

	public int getObjectsCount()
	{
		return gisobjs.size();
	}

	public long getLastModified()
	{
		return -1;
	}

	public IBaseStorage filter(IBaseFilter filter) throws Exception
	{
		if (filter==null)
			return this;
		throw new UnsupportedOperationException("Unsupport operation filter for simple storage");
	}

	public IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception
	{
		for (IBaseGisObject iBaseGisObject : gisobjs)
		{
			if (iBaseGisObject.getCurveId().equals(curvId))
				return iBaseGisObject;
		}
		return null;
	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter)      throws Exception
	{
		List<IBaseGisObject> keys = new LinkedList<IBaseGisObject>();
		Iterator<IBaseGisObject> allkeys = getAllObjects();
		while (allkeys.hasNext())
		{
				IBaseGisObject iGisObject = allkeys.next();
				if (filter.acceptObject(iGisObject))
					keys.add(iGisObject);
		}
		return keys.iterator();
	}

	public Iterator<IBaseGisObject> getAllObjects()
	{
		return gisobjs.iterator();
	}

	public Iterator<String> getCurvesIds()
	{
		List<String> rv= new LinkedList<String>();
		for (IBaseGisObject iBaseGisObject : gisobjs)
			rv.add(iBaseGisObject.getCurveId());
		return rv.iterator();
	}

	public void addObject(IBaseGisObject object) throws Exception
	{
		gisobjs.add(object);
	}

	public void clearAll() throws Exception
	{
		gisobjs.clear();
	}

	public IEditableGisObject createObject(String geotype) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public IBaseGisObject removeObject(String curveId) throws Exception
	{
		IBaseGisObject obj4rem=null;
		for (IBaseGisObject gisobj : gisobjs)
		{
			if (gisobj.getCurveId().equals(curveId))
			{
				obj4rem=gisobj;
				break;
			}
		}
		if (obj4rem!=null)
			gisobjs.remove(obj4rem);
		return obj4rem;
	}

	public IEditableGisObject getEditableObject(String curveId) throws Exception
	{
		IBaseGisObject obj4rem=null;
		for (IBaseGisObject gisobj : gisobjs)
		{
			if (gisobj.getCurveId().equals(curveId))
			{
				obj4rem=gisobj;
				break;
			}
		}
		if (obj4rem instanceof IEditableGisObject)
			return (IEditableGisObject)obj4rem;
		else
			throw new UnsupportedOperationException("Can't find editbale object with curveID"+curveId);
	}
}
