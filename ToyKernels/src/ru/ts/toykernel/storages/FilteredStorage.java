package ru.ts.toykernel.storages;

import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.IKeyFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class FilteredStorage implements IBaseStorage
{
	private IBaseFilter filter;
	private IBaseStorage storage;


	public IAttrs getObjAttrs(String curveid) throws Exception
	{
		return getBaseGisByCurveId(curveid).getObjAttrs();
	}
	

	public FilteredStorage(IBaseFilter filter,IBaseStorage storage)
	{
		this.filter = filter;
		this.storage = storage;
	}

	public int getObjectsCount()
	{
		try
		{
			return getListCurvesIds().size();
		}
		catch (Exception e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public long getLastModified()
	{
		return storage.getLastModified();
	}

	public IBaseStorage filter(IBaseFilter filter) throws Exception
	{
		return new FilteredStorage(filter,this);
	}

	public IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception
	{
		IBaseGisObject robj = storage.getBaseGisByCurveId(curvId);
		if (filter.acceptObject(robj))
			return robj;
		else
			return null;
	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter) throws Exception
	{
		Iterator<IBaseGisObject> itfl=storage.filterObjs(filter);

		LinkedList<IBaseGisObject> ll = new LinkedList<IBaseGisObject>();
		while (itfl.hasNext())
		{
			IBaseGisObject iBaseGisObject = itfl.next();
			if (this.filter.acceptObject(iBaseGisObject))
				ll.add(iBaseGisObject);
		}
		return ll.iterator();
	}

	public Iterator<IBaseGisObject> getAllObjects() throws Exception
	{
		Iterator<IBaseGisObject> itall=storage.getAllObjects();
		LinkedList<IBaseGisObject> ll = new LinkedList<IBaseGisObject>();
		while (itall.hasNext())
		{
			IBaseGisObject iBaseGisObject = itall.next();
			if (filter.acceptObject(iBaseGisObject))
				ll.add(iBaseGisObject);
		}
		return ll.iterator();
	}

	public Iterator<String> getCurvesIds() throws Exception
	{
		List<String> ll = getListCurvesIds();

		return ll.iterator();
	}

	private List<String> getListCurvesIds()
			throws Exception
	{
		Iterator<String> itoid=storage.getCurvesIds();
		List<String> ll = new LinkedList<String>();

		IKeyFilter kfilter=null;
		if (filter instanceof IKeyFilter)
			kfilter=(IKeyFilter)filter;

		while (itoid.hasNext())
		{
			String oid = itoid.next();
			if (kfilter!=null)
			{
				if (kfilter.acceptObject(oid))
					ll.add(oid);
			}
			else if (getBaseGisByCurveId(oid)!=null)
				ll.add(oid);
		}
		return ll;
	}
}
