package ru.ts.toykernel.filters;

import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.attrs.IDefAttr;

import ru.ts.factory.IParam;

/**
 * Фильтр по аттритам объектов атрибутов
 * ru.ts.toykernel.filters.DefAttrDefineFilter
 */
public class DefAttrDefineFilter extends BaseInitAble implements IBaseFilter
{
	private boolean negate = false;//Флаг показывающий отрицание критерия фильтрации по филтру выбора
	private String attrname;//Имя аттрибта
	private String attrval;//Занчение аттрибута


	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase("ISNEGATE"))
			negate = Boolean.valueOf((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase("ATTRNAME"))
			attrname = (String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase("ATTRVALUE"))
			attrval = (String) attr.getValue();
		return null;
	}

	public DefAttrDefineFilter(boolean negate, String attrname,String attrval)
	{
		this.negate = negate;
		this.attrname = attrname;
		this.attrval = attrval;
	}

	public DefAttrDefineFilter()
	{

	}

	public boolean acceptObject(IBaseGisObject obj)
	{

		IDefAttr iDefAttr = obj.getObjAttrs().get(attrname);
		return negate^
				( iDefAttr !=null && ((attrval == null) || (attrval.equals(iDefAttr.getValue())))
		);
	}

	public static final String TYPENAME = "F_DEFATTRFILTER";

	public String getTypeName()
	{
		return TYPENAME;
	}
}
