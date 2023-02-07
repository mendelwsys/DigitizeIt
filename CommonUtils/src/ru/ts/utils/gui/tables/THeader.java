package ru.ts.utils.gui.tables;

import java.util.LinkedList;

public abstract class THeader
{
	private TNode root;
	public TNode getRoot()
	{
		return root;
	}

	public THeader(TNode root)
	{
		this.root = root;
	}

	protected LinkedList<TNode> bypassfromroot(TNode root)
	{
		TNode[] chnodes = root.getChildNodes();
		LinkedList<TNode> retVal=new LinkedList<TNode>();
		if (chnodes !=null)
			for (TNode chnode : chnodes)
				retVal.addAll(bypassfromroot(chnode));

		retVal.add(root);
		return retVal;
	}


	public int getCountField()
	{
		return bypassfromroot(getRoot()).size();
	}


	public String getNameField(int col)
	{
		LinkedList<TNode> rv=bypassfromroot(getRoot());
		return rv.get(col).toString();
	}

	/**
	 * get Value object at column and row
	 * @param col - column
	 * @param row - row
	 * @param data -
	 * @return
	 */
	public abstract Object getValueAt(int col,int row,Object data);
	public abstract boolean setValueAt(Object val,int col,int row,Object data);
	public abstract Class getClassValue();
	public abstract boolean isEditable(int col,int row, Object data);
}
