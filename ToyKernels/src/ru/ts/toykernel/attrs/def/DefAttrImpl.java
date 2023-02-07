package ru.ts.toykernel.attrs.def;

import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.factory.IParam;

import java.util.List;
import java.util.LinkedList;

/**
 * Deafault implemetation of attribute
 *
 */
public class DefAttrImpl implements IDefAttr
{
    protected String name;
    protected Object obj;
	protected List<IParam> attrs;

	/**
	 * equals for comparing of attribute
	 * @param attr - attribute of comparing
	 * @return true if eaquls
	 */
    public boolean equals(Object attr)
    {
        if (attr instanceof DefAttrImpl)
        {
            DefAttrImpl defAttr = (DefAttrImpl) attr;
			boolean fstcheck = defAttr.name.equals(name) && defAttr.obj.equals(obj);
			if (this.attrs==null)
				fstcheck = fstcheck && (defAttr.attrs==null);
			if (fstcheck && (this.attrs!=null))
			{
				fstcheck = fstcheck && (this.attrs.size()==defAttr.attrs.size());
				if (fstcheck)
					for (int i = 0; i < attrs.size(); i++)
					{
						IParam iParam = attrs.get(i);
						if (!iParam.equals(defAttr.attrs.get(i)))
							 return false;
					}
			}
			return fstcheck;
        }
        return false;
    }

	/**
	 * Contructor
	 * @param name - name of attribute
	 * @param obj - value of attribute
	 */
	public DefAttrImpl(String name, Object obj)
    {
		this(name,obj,null);
	}

	/**
	 * Contructor
	 * @param name - name of attribute
	 * @param obj - value of attribute
	 * @param attrs - attributes of paramtes
	 */
	public DefAttrImpl(String name, Object obj,List<IParam> attrs)
    {
        this.name = name;
        this.obj = obj;
		this.attrs=attrs;
	}

	/**
	 * (non-Javadoc)
     * @see ru.ts.toykernel.attrs.IDefAttr#getValue()
     */
	public Object getValue()
    {
        return obj;
    }


	public List<IParam> getAttributes()
	{
		return this.attrs;
	}

	public void setAttributes(List<IParam> attrs)
	{
		this.attrs=attrs;
	}

	/**
	 * (non-Javadoc)
     * @see ru.ts.toykernel.attrs.IDefAttr#setValue(Object obj)
     */
	public void setValue(Object obj)
	{
		this.obj = obj;
	}

	/**
	 * (non-Javadoc)
     * @see ru.ts.toykernel.attrs.IDefAttr#getName()
     */
	public String getName()
    {
        return name;
    }

	public IParam getCopy()
	{
		List<IParam> attrs=null;
		if (this.attrs!=null)
		{
			attrs=new LinkedList<IParam>();
			for (IParam attr : this.attrs)
				attrs.add(attr.getCopy());
		}
		return new DefAttrImpl(getName(),getValue(),attrs);
	}
}
