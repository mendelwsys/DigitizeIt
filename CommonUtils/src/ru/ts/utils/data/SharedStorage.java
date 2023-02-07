package ru.ts.utils.data;

import javax.naming.OperationNotSupportedException;
import java.util.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 26.01.2007
 * Time: 11:45:12
 * Changes: yugl, 04.05.2008,
 * Для того что бы вектор не содержал копии одной и той же информации для разных клиентов
 * однако клиент теперь не может менять содержимое вектора, оно должно меняться через статические методы
 */
public class SharedStorage<T extends Serializable>
		implements IReadStorage<T>
{
	static private Vector vc= new Vector();
	private int curpos=-1;

	public static void initStorage(Set objset)
	{
		vc.addAll(objset);
	}

	public static Vector getVc()
	{
		Vector retVal = new Vector();
		retVal.addAll(vc);
		return retVal;
	}

	public Vector<T> getCopyVc()
	{
		Vector<T> retVal = new Vector<T>();
		retVal.addAll(vc);
		return retVal;
	}

	public LinkedList<T> getLil()
	{
		LinkedList<T> retVal = new LinkedList<T>();
		retVal.addAll(vc);
		return retVal;
	}

	public int getCurpos()
	{
		return curpos;
	}

	public T get(int i)
	{
		if (i>=0 && i<vc.size())
			return (T)vc.get(i);
		throw new ArrayIndexOutOfBoundsException();
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
			return (T)vc.get(++curpos);
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
			return (T)vc.get(--curpos);
		}
		throw new ArrayIndexOutOfBoundsException();
	}


	public int size()
	{
		return vc.size();
	}

	public T remove(int i)
	{
		return null;
	}


	public void saveStorageToStream(DataOutputStream dos) throws IOException
	{
		ObjectOutputStream oos= new ObjectOutputStream(dos);
		dos.writeInt(vc.size());
		for (Object t : vc)
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
}