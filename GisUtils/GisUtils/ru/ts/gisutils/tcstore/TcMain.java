/*
 * Created on 29.08.2007
 *
 */
package ru.ts.gisutils.tcstore;

import java.util.*;

import ru.ts.gisutils.geometry.Rect;
import ru.ts.gisutils.tc.*;

/**
 * @author yugl
 *
 * Запуск демо и отладки
 */
public class TcMain {

	static public void main (String args[]) {
		ITcDataStore store = new TcDataStoreDemo("log.txt", true);
		TcProcess process = new TcProcess(store);
		doIteration(process, TcIteration.PROC_DUPE_NODES, 0, null);
		doIteration(process, TcIteration.PROC_NEAR_NODES, 4.5, null);
		doIteration(process, TcIteration.MATCH_NODES_WITH_VERTS, 4.5, null);
		doIteration(process, TcIteration.PROC_VERTS_NEAR_NODES, 4.5, null);
		doIteration(process, TcIteration.PROC_NEAR_VERTS, 4.5, null);
		doIteration(process, TcIteration.PROC_NODES_NEAR_LINES, 4.5, null);
		doIteration(process, TcIteration.PROC_SMALL_LOOP_LINES, 4.5, null);
		doIteration(process, TcIteration.PROC_LINE_INTERSECTIONS, 4.5, null);
	}
	
	static public void doIteration (TcProcess process, int iterationTask, double delta, Rect rect) {
		List defects = null;
		if (rect == null)
			defects = process.newIteration(iterationTask, delta);
		else
			defects = process.newIteration(iterationTask, delta, rect);
		Iterator itr = defects.iterator();
		while (itr.hasNext()) {
			TcLocInfo info = (TcLocInfo)itr.next();
			System.out.println(info);
		}
		process.autoprocessDefects(defects);
		process.endIteration(); 
		//process.cancelIteration(); 
	}

}
