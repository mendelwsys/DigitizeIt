/*
 * Created on 24.08.2007
 *
 */
package ru.ts.gisutils.tcstore;

import java.util.*;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXYGeometry;
import ru.ts.gisutils.geometry.Rect;
import ru.ts.gisutils.geometry.XYGeometry;
import ru.ts.gisutils.tc.*;

/**
 * @author yugl
 *
 * Абстрактный базовый класс для хранилища данных.
 * Все реализации методов примитивны и могут переопределяться.
 */
abstract public class TcDataStoreBase implements ITcDataStore {

	//----------------------
	// static
	//----------------------
	static public Comparator comparatorXY;
	static {
		comparatorXY = new ComparatorXY();
	}
	
	//----------------------
	// fields
	//----------------------
	
	// объект, где реализована вся геометрия
	protected IXYGeometry _geometry;
	
	//----------------------
	// constructors
	//----------------------
	
	// uses XYGeometry by default 
	public TcDataStoreBase () {
		_geometry = new XYGeometry();
	}
	// uses the given geometry  
	public TcDataStoreBase (IXYGeometry geometry) {
		_geometry = geometry;
	}
	
	//----------------------
	// ITcDataStore
	//----------------------
	
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getGeometry()
	 */
	public IXYGeometry getGeometry () {
		return _geometry; 
	}
	
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getNodes()
	 */
	abstract public List getNodes ();

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getNodes(ru.ts.gisutils.tc.Rect)
	 */
	public List getNodes (Rect rect) {
		List objs = getNodes();
		List res = filter(objs, rect);
		return res;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getSortedNodes()
	 */
	public List getSortedNodes () {
		List objs = getNodes();
		Collections.sort(objs, comparatorXY);
		return objs;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getSortedNodes(ru.ts.gisutils.tc.Rect)
	 */
	public List getSortedNodes (Rect rect) {
		List objs = getNodes(rect);
		Collections.sort(objs, comparatorXY);
		return objs;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getEndVertices()
	 */
	abstract public List getEndVertices ();

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getEndVertices(ru.ts.gisutils.tc.Rect)
	 */
	public List getEndVertices (Rect rect) {
		List objs = getEndVertices();
		List res = filter(objs, rect);
		return res;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getSortedEndVertices()
	 */
	public List getSortedEndVertices () {
		List objs = getEndVertices();
		Collections.sort(objs, comparatorXY);
		return objs;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getSortedEndVertices(ru.ts.gisutils.tc.Rect)
	 */
	public List getSortedEndVertices (Rect rect) {
		List objs = getEndVertices(rect);
		Collections.sort(objs, comparatorXY);
		return objs;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getDanglingNodes()
	 */
	abstract public List getDanglingNodes();

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getDanglingNodes(ru.ts.gisutils.tc.Rect)
	 */
	public List getDanglingNodes(Rect rect) {
		List objs = getDanglingNodes();
		List res = filter(objs, rect);
		return res;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getSortedDanglingNodes()
	 */
	public List getSortedDanglingNodes() {
		List objs = getDanglingNodes();
		Collections.sort(objs, comparatorXY);
		return objs;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getSortedDanglingNodes(ru.ts.gisutils.tc.Rect)
	 */
	public List getSortedDanglingNodes(Rect rect) {
		List objs = getDanglingNodes(rect);
		Collections.sort(objs, comparatorXY);
		return objs;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getDanglingVertices()
	 */
	abstract public List getDanglingVertices();

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getDanglingVertices(ru.ts.gisutils.tc.Rect)
	 */
	public List getDanglingVertices(Rect rect) {
		List objs = getDanglingVertices();
		List res = filter(objs, rect);
		return res;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getSortedDanglingVertices()
	 */
	public List getSortedDanglingVertices() {
		List objs = getDanglingVertices();
		Collections.sort(objs, comparatorXY);
		return objs;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getSortedDanglingVertices(ru.ts.gisutils.tc.Rect)
	 */
	public List getSortedDanglingVertices(Rect rect) {
		List objs = getDanglingVertices(rect);
		Collections.sort(objs, comparatorXY);
		return objs;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getPolylines()
	 */
	abstract public List getPolylines();

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#getPolylines(ru.ts.gisutils.tc.Rect)
	 */
	public List getPolylines(Rect rect) {
		List objs = getPolylines();
		List res = filter(objs, rect);
		return res;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#filter(java.util.List, ru.ts.gisutils.tc.Rect)
	 */
	public List filter (List objs, Rect rect) {
		// подготовим прямоугольник на случай проверок по прямоугольнику
		Rect objRect = new Rect();
		// подготовим пустой список для результата
		ArrayList res = new ArrayList();
		
		Iterator objsItr = objs.iterator();			
		while (objsItr.hasNext()) {
			// берем очередной узел			
			ITcObjBase obj = (ITcObjBase) objsItr.next();
			if (obj instanceof IGetXY) {
				IGetXY xy = (IGetXY) obj;
				if (rect.has(xy)) res.add(obj);
			}
			else if (obj instanceof ITcObjPolyline) {
				ITcObjPolyline pline = (ITcObjPolyline) obj;
				pline.verts().copyRect(objRect);
				objRect.cutBy(rect);
				if (!objRect.isEmpty()) res.add(obj);
			}
		}		
		return res;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcDataStore#doActions(java.util.List, ru.ts.gisutils.tc.Rect)
	 */
	public void doActions (TcIteration iteration, List actions) {
	}
	
}
