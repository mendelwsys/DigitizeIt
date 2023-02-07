package ru.ts.utils.data;

import java.util.*;
import java.io.*;

/**
 * Memmory storage of different types
 */
public class Storage<T extends Serializable>
		implements IReadStorage<T>
{
	private Vector<T> vc= new Vector<T>();
	private int curpos=-1;



	public Storage()
	{
	}

	public Storage (Collection<T> list)
	{
	    addToStorage(list);
	}

	public Vector<T> getCopyVc()
	{
		Vector<T> retVal = new Vector<T>();
		retVal.addAll(vc);
		return retVal;
	}

	public Vector<T> getVc()
	{
		return vc;
	}


	public LinkedList<T> getLil()
	{
		LinkedList<T> retVal = new LinkedList<T>();
		retVal.addAll(vc);
		return retVal;
	}


	public void clear()
	{
		vc.clear();
		curpos=-1;
	}

	public int getCurpos()
	{
		return curpos;
	}

	public void addToStorage(T obj)
	{
		vc.add(obj);
		if (curpos<0)
			curpos=0;
	}

	public void addToStorage(T obj,int i)
	{
		vc.add(i,obj);
		if (curpos<0)
			curpos=0;
	}


	public T get(int i)
	{
		if (i>=0 && i<vc.size())
			return vc.get(i);
		throw new ArrayIndexOutOfBoundsException();
	}


	public void addToStorage(Collection<T> list)
	{
		vc.addAll(list);
	}

	public boolean hasMoreElements()
	{
		return vc.size()>0 && curpos<vc.size()-1 && curpos>=-1;
	}

	public boolean hasPrevElements()
	{
		return vc.size()>0 && curpos<vc.size() && curpos>0;
	}


	public boolean setNearestElemet()
	{
		if (vc.size()==0)
			return false;

		if (curpos>=vc.size())
			curpos=vc.size()-1;
		else if (curpos<0)
			curpos=0;
		return true;
	}

	public T nextElement()
	{
		if (vc.size()>0 && curpos<vc.size()-1)
		{
			if (curpos<0)
				curpos=-1;
			return vc.get(++curpos);
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	public void setposonAfterLast()
	{
		curpos=vc.size()-1;
	}

	public void setposon(int pos)
	{
		if (pos<0 || pos>=vc.size())
			throw new ArrayIndexOutOfBoundsException();
		curpos=pos;
	}


	public void setposonBeforeFirst()
	{
			curpos=-1;
	}

	public T prevElement()
	{
		if (vc.size()>0 && curpos>0)
		{
			if (curpos>vc.size())
				curpos=vc.size();
			return vc.get(--curpos);
		}
		throw new ArrayIndexOutOfBoundsException();
	}


	public T remove(int i)
	{
		if (i>=vc.size())
			return null;

		T retVal = vc.remove(i);
		if (vc.size()<curpos)
			curpos=vc.size()-1;

		return retVal;
	}


	public int size()
	{
		return vc.size();
	}


	public void saveStorageToStream(DataOutputStream dos) throws IOException
	{
		ObjectOutputStream oos= new ObjectOutputStream(dos);
		dos.writeInt(vc.size());
		for (T t : vc)
			oos.writeObject(t);
		oos.flush();
	}

	public static <T extends Serializable>  Storage<T> loadTemplatesFromStream(DataInputStream dis) throws IOException, ClassNotFoundException
	{
		ObjectInputStream ois= new ObjectInputStream(dis);
		int sz=dis.readInt();
		Storage<T> retVal=new Storage<T>();
		for (int i = 0; i < sz; i++)
			  retVal.addToStorage((T) ois.readObject());
		return retVal;
	}


	public static void main(String[] args) throws Exception
	{
//		class ATest implements Serializable
//		{
//			int i1;
//			int i2;
//
//			public ATest(int i1, int i2)
//			{
//				this.i1 = i1;
//				this.i2 = i2;
//			}
//		}
//		Storage<ATest> tstorage= new Storage<ATest>();


//		byte[] bt= "ФЫВА".getBytes("UTF-8");

		Storage<int[][]> tstorage2= new Storage<int[][]>();

		for (int i=0;i<10;i++)
		{
			int[][] content=new int[2][2];
			content[0][1]=i;
			content[1][1]=i+1;
			tstorage2.addToStorage(content);
		}


		File file = new File("D://super.txt");
		FileOutputStream fos = new FileOutputStream(file);
		tstorage2.saveStorageToStream(new DataOutputStream(fos));
		fos.flush();
		fos.close();

		FileInputStream fis = new FileInputStream(file);
		Storage<int[][]> tst=Storage.loadTemplatesFromStream(new DataInputStream(fis));
		fis.close();

		tst.setposonAfterLast();
		while (tst.hasMoreElements())
		{
			int[][] a = tst.prevElement();
			System.out.println("a.i1 = " + a[0][1]);
			System.out.println("a.i2 = " + a[1][1]);
		}
	}
}