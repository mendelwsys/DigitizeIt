/**
 * 
 */
package ru.ts.gisutils.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author sygsky, created on 10.09.2007 12:46:57, (@)copyright by JHC,
 *         1987-2007
 * 
 */
//@SuppressWarnings("unchecked")
public class Colls
{
	/**
	 * @param list -
	 *            some list with Comparable objects
	 */
    public static boolean unique(List list)
	{
		final ArrayList set = new ArrayList(list);
		final int orig_size = list.size();
		Collections.sort(set);
		int count;
		final ArrayList ulist = new ArrayList(count = set.size()); // unique
		// list
		Object lst_obj;
		ulist.add(lst_obj = set.get(0));
		Object cur_obj;
		// make 'set' unique list
		for (int index = 1; index < count - 1; index++)
			if (((Comparable) (cur_obj = set.get(index))).compareTo(lst_obj) != 0)
				ulist.add(lst_obj = cur_obj);
		set.clear(); // free recources
		int found;
		// now check for each entry in an original list to be unique
		for (int index = count - 1; index >= 0; index--)
			if ((found = Collections.binarySearch(ulist, cur_obj = list
			        .get(index))) < 0)
				list.remove(index);
			else
				ulist.remove(found);
		ulist.clear(); // free recources
		return list.size() != orig_size;
	}
    
    public static boolean uniqueSorted(List sortedList)
    {
    	final int size = sortedList.size();
    	for(int i = size -1; i > 0; i-- )
    		if ( ((Comparable)sortedList.get(i)).compareTo(sortedList.get(i-1)) == 0  )
    			sortedList.remove( i );
    	return sortedList.size() != size;
    }
    
	/**
	 * @param list
	 *            consists of object to check unique. Result is returned in this
	 *            list.
	 * @param cmp
	 *            Comparator to compare objects from a list
	 * @return size of resulted list
	 */
    public static boolean unique(List list, Comparator cmp)
	{
		final ArrayList set = new ArrayList(list);
		final int orig_size = list.size();
		Collections.sort(set, cmp);
		int count;
		final ArrayList ulist = new ArrayList(count = set.size()); // unique
		// list
		Object lst_obj; // last object
		ulist.add(lst_obj = set.get(0));
		Object cur_obj;
		// make 'set' unique list
		for (int index = 1; index < count - 1; index++)
			if (cmp.compare(((Comparable) (cur_obj = set.get(index))), lst_obj) != 0)
				ulist.add(lst_obj = cur_obj);
		set.clear(); // free resources
		int found;
		// now check for each entry in an original list to be unique
		for (int index = count - 1; index >= 0; index--)
			if ((found = Collections.binarySearch(ulist, cur_obj = list
			        .get(index), cmp)) < 0)
				list.remove(index);
			else
				ulist.remove(found);
		ulist.clear(); // free resources
		return list.size() != orig_size;
	}

	/**
	 * removes from the orig 'list' all items that are presented in the 'sub'
	 * list
	 * 
	 * @param orig
	 *            original List to remove sub items from it
	 * @param set
	 *            List sorted list with elements to remove from orig
	 * @return new orig list size
	 */
    public static int retainSorted(List orig, List set)
	{
		int count;
		final ArrayList _list = new ArrayList(count = set.size());
		Object cur_obj;
		for (int index = 0; index < count - 1; index++)
			if (Collections.binarySearch(set, cur_obj = orig.get(index)) >= 0)
				_list.add(cur_obj);
		orig.clear();
		orig.addAll(_list);
		_list.clear();
		return orig.size();
	}

	/**
	 * @param orig
	 *            is a list to remove all non coincident entries for 'set' list
	 * @param set
	 *            is a collection of entries to remain unchanged in 'orig' list
	 * @return true if List was changed as a result of operation
	 */
	public static boolean retainAll(List orig, Collection set)
	{
		return retainAll(orig, set.toArray());
	}

	/**
	 * @param orig
	 *            is a list to remove all non coincident entries for 'set' list
	 * @param arr
	 *            is an array of entries to remain unchanged in 'orig' list
	 * @return true if List was changed as a result of operation
	 */
    public static boolean retainAll(List orig, Object[] arr)
	{
		int count = orig.size();
		final ArrayList res = new ArrayList(arr.length);
		Object cur_obj;
		Arrays.sort(arr);
		for (int index = 0; index < count - 1; index++)
			if (Arrays.binarySearch(arr, cur_obj = orig.get(index)) >= 0)
				res.add(cur_obj);
		boolean ret;
		if (ret = (res.size() != count)) // then update original values list
		{
			orig.clear(); // remove previous entries
			orig.addAll(res); // add new ones
		}
		res.clear();
		return ret;
	}

	/**
	 * @param orig
	 *            is a list to remove all coincident entries for 'removeset'
	 *            list
	 * @param removeset
	 *            is a collection of entries to remove from 'orig' list
	 * @return new size of 'orig' list
	 */
	public static boolean removeAll(List orig, Collection removeset)
	{
		return removeAll(orig, removeset.toArray());
	}

	/**
	 * @param orig
	 *            is a list to remove all coincident entries for 'removeset'
	 *            list
	 * @param arr
	 *            is an array of entries to remove from 'orig' list
	 * @return new size of 'orig' list
	 */
    public static boolean removeAll(List orig, Object[] arr)
	{
		final int count = orig.size();
		final ArrayList res = new ArrayList(arr.length);
		Object cur_obj;
		Arrays.sort(arr);
		for (int index = 0; index < count - 1; index++)
			if (Arrays.binarySearch(arr, cur_obj = orig.get(index)) < 0)
				res.add(cur_obj);
		boolean ret;
		if (ret = (res.size() != count)) // then update original values list
		{
			orig.clear(); // remove previous entries
			orig.addAll(res); // add new ones
		}
		res.clear();
		return ret;
	}

}
