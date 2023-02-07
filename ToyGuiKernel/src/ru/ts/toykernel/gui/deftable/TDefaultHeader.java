package ru.ts.toykernel.gui.deftable;

import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.utils.gui.tables.THeader;
import ru.ts.utils.gui.tables.TNode;

import java.util.Map;

/**
 * Table header
 */
public class TDefaultHeader
		extends THeader
{
	protected String paramname;
	private boolean editable;
	protected  Class attrclass;

	public TDefaultHeader(TNode root,final String paramname,boolean isEditable,Class attrclass)
	{
		super(root);
		this.paramname = paramname;
		editable = isEditable;
		this.attrclass = attrclass;
	}

	public Object getValueAt(int col,int row, Object data)
	{
		Map<String,IDefAttr> lrattr = (Map<String, IDefAttr>) data;
		return lrattr.get(paramname).getValue();
	}

	public boolean setValueAt(Object val, int col,int row, Object data)
	{
		Map<String,IDefAttr> lrattr = (Map<String,IDefAttr>) data;
		lrattr.get(paramname).setValue(val);
		return true;
	}

	public Class getClassValue()
	{
		return attrclass;
	}

	public boolean isEditable(int col,int row, Object data)
	{
		return editable;
	}
}
