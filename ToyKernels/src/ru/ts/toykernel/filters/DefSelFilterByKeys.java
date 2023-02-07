package ru.ts.toykernel.filters;

import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.factory.IParam;

import java.util.Set;
import java.util.HashSet;

/**
 *  Фильтр для отбора выделенныйх объектов по идентификаторам
 *  ru.ts.toykernel.filters.DefSelFilterByKeys 
 */
public class DefSelFilterByKeys extends BaseInitAble implements IKeySelFilter
{
	public static final String TYPENAME = "DefSelFilterByKeys";
	private Set<String> keyset= new HashSet<String>();//Множество ключей по для отбора объектов

	private boolean negate=false;//Флаг показывающий отрицание критерия фильтрации по филтру выбора

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase("ISNEGATE"))
			negate = Boolean.valueOf((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase("ID"))
			 addKey2Set((String)attr.getValue());
		return null;
	}

	public void addKey2Set(String key)
	{
		this.keyset.add(key);
	}

	public void setKeySet(Set<String> keyset)
	{
		this.keyset = keyset;
	}

	public Set<String> getKeySet()
	{
		return this.keyset;
	}

	public void clearKeySet()
	{
		this.keyset.clear();
	}

	public boolean acceptObject(IBaseGisObject obj)
	{
		return acceptObject(obj.getCurveId());
	}

	public String getTypeName()
	{
		return TYPENAME;
	}

	public boolean acceptObject(String key)
	{
		return
				(negate?(!(keyset!=null && keyset.contains(key))):(keyset!=null && keyset.contains(key)));
	}
}
