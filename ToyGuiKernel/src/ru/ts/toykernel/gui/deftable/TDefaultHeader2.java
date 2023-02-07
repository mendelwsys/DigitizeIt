package ru.ts.toykernel.gui.deftable;

import ru.ts.utils.gui.tables.TNode;

import java.util.List;
import java.util.Map;

/**
 * Table header 2
 */
public class TDefaultHeader2
		extends TDefaultHeader
{
	public TDefaultHeader2(TNode root, final String paramname,boolean isEditable, Class attrclass)
	{
		super(root, paramname,isEditable,attrclass);
	}

	public Object getValueAt(int col, int row, Object data)
	{
		Map<String, List<String>> lrattr = (Map<String, List<String>>) data;
		return lrattr.get(paramname).get(row);
	}

	public boolean setValueAt(Object val, int col, int row, Object data)
	{
		Map<String, List<String>> lrattr = (Map<String, List<String>>) data;
		lrattr.get(paramname).set(row, (String) val);
		return true;
	}
}

