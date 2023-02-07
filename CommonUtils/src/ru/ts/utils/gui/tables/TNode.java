package ru.ts.utils.gui.tables;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 04.10.2007
 * Time: 19:52:02
 * To change this template use File | Settings | File Templates.
 */
public class TNode
{
	private Object nodeval;
	private TNode[] childNodes;

	public TNode(Object nodeval)
	{
		this.nodeval = nodeval;
		childNodes=null;
	}

	public TNode(Object nodeval, String[] childNodes)
	{
		this.nodeval = nodeval;
		if (childNodes!=null && childNodes.length>0)
		{
			this.childNodes= new TNode[childNodes.length];
			for (int i = 0; i < childNodes.length; i++)
				this.childNodes[i]= new TNode(childNodes[i]);
		}
	}

	public TNode(Object nodeval, TNode[] childNodes)
	{
		this.nodeval = nodeval;
		this.childNodes = childNodes;
	}

	public Object getNodeval()
	{
		return nodeval;
	}

	public TNode[] getChildNodes()
	{
		return childNodes;
	}


	public String toString()
	{
		return nodeval.toString();
	}
}
