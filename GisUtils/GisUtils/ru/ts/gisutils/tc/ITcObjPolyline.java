/*
 * Created on 11 Oct 2007
 *
 */
package ru.ts.gisutils.tc;

/**
 * @author yugl
 *
 * Представляет линию, заданную последовательностью вершин, которая после топологической 
 * чистки будет использована для построения дуги в сетевом графе.
 */
public interface ITcObjPolyline extends ITcObjBase {

	/**
	 * Доступ к координатам вершин. 
	 */ 
	public IGetVertices verts();

}