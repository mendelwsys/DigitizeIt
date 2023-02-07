package ru.ts.toykernel.attrs.def;

import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.IAttrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Default attribute implementation
 */
public class DefaultAttrsImpl extends HashMap<String, IDefAttr>
        implements IAttrs
{
	private boolean ro=false;

	public DefaultAttrsImpl()
	{
	}

	public DefaultAttrsImpl(IAttrs attrs)
	{
			Set<String> ks = attrs.keySet();
			for (String k : ks)
				super.put(k,attrs.get(k));
		//super(attrs);Не работатет из скрытой карты внутри
	}

	public DefaultAttrsImpl(IAttrs attrs,boolean isRo)
	{
		this(attrs);
		ro = isRo;
	}

	public IDefAttr put(String key,IDefAttr attr)
	{
		if (ro)
			throw new UnsupportedOperationException("Can't change Read only attributes");
		return  super.put(key,attr);
	}

	public void clear()
	{
		if (ro)
			throw new UnsupportedOperationException("Can't change Read only attributes");
		super.clear();
	}

	public IDefAttr remove(Object key)
	{
		if (ro)
			throw new UnsupportedOperationException("Can't change Read only attributes");
		return  super.remove(key);
	}

	public void putAll(Map attrs)
	{
		if (ro)
			throw new UnsupportedOperationException("Can't change Read only attributes");
		super.putAll(attrs);
	}

}
