/*
 * Created on 24.08.2007
 *
 */
package ru.ts.gisutils.tcstore;

import java.util.Comparator;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.XY;

/**
 * @author yugl
 *
 * Для сортировки по ху 
 */
public class ComparatorXY implements Comparator {

	public int compare(Object o1, Object o2) {
		IGetXY xy1 = (IGetXY) o1;
		IGetXY xy2 = (IGetXY) o2;
		return XY.geometry().compareYthenX(xy1, xy2);
	}

//	public int compare(IGetXY xy1, IGetXY xy2) {
//		return xy1.compareTo(xy2);
//	}

}
