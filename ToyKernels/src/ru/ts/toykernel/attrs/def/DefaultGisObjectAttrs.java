package ru.ts.toykernel.attrs.def;

import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;

import java.util.*;

/**
 * Implementation attributes of GisObject
 */
public class DefaultGisObjectAttrs extends DefaultAttrsImpl
{
	protected String objectId;
	/**
	 * default attribute values
	 */
	protected IAttrs defAttrs;

	/**
	 * get default attributes of object
	 * @return default attributes of object
	 */
	public IAttrs getDefAttrs()
	{
		return defAttrs;
	}

	/**
	 * set default attributes of object
	 * @param defAttrs - default attributes of object
	 */
	public void setDefAttrs(IAttrs defAttrs)
	{
		this.defAttrs = defAttrs;
	}
	/**
	 * create attribute of gis object
	 * @param defAttrs - default attribute values
	 * @param objectId - object idenifier to which attributes belongs to
	 */
    public DefaultGisObjectAttrs(IAttrs defAttrs,String objectId)
    {
        this.defAttrs=defAttrs;
		this.objectId = objectId;
	}

    public IDefAttr get(Object key)
    {
        IDefAttr rv=super.get(key);
        if (rv==null)
            rv=defAttrs.get(key);
        return rv;
    }

	public  IDefAttr put(String key,IDefAttr attr)
	{
		IDefAttr rv=defAttrs.get(key);
		if (rv!=null)
		{
			if (rv.equals(attr))
			{
				this.remove(key);
				return rv;
			}
		}
		return super.put(key,attr);
	}

	public Collection<IDefAttr> values()
    {
        Collection<IDefAttr> rv=new LinkedList<IDefAttr>(super.values());
        rv.addAll(defAttrs.values());
        return rv;
    }


	public Set<String> keySet()
    {
        Set<String> rv=new HashSet<String>(super.keySet());
        rv.addAll(defAttrs.keySet());
        return rv;
    }

	public String getObjectId()
	{
		return objectId;
	}
}