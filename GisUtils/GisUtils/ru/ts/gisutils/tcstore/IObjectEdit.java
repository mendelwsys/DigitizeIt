/**
 * created on 31-AUG-2007 by Syg
 */
package ru.ts.gisutils.tcstore;

import java.util.List;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.tc.IGetVertices;

/**
 * defines all methods to change data in all ways ( create, edit, delete ). It
 * is implied that Data are accessed through ITcDataStore interface
 * 
 * @author sygsky
 * 
 */

public interface IObjectEdit
{

	/**
	 * defines a new dangling node to existing store
	 * 
	 * @param node
	 *            TcNode to be known as dangling from now. Only node, known the
	 *            store engine could be set as dangling. If pointed node is
	 *            already marked as dangling, nothing is changed and
	 *            {@code true} is returned
	 * @param state -
	 *            defines what to do with a node. If {@code true} - node is set
	 *            as dangling, else is set as not dangling one
	 * 
	 * @return {@code true} if node was dangling before call of this method,
	 *         {@code false} if not
	 * 
	 */
	boolean setDanglingNode(IGetXY node, boolean state);

	/**
	 * defines a new dangling vertext to existing store
	 * 
	 * @param vertex -
	 *            IGetXY interface to change its state
	 * @param state - -
	 *            defines what to do with this vertex. If {@code true} - vertex
	 *            is set as dangling, else is set as not dangling one
	 * @return {@code true} if vertex is set as dangling {@code false} if not -
	 *         i.e. no such vertex in the storage or it is not and ending vertext
	 * 
	 */
	boolean setDanglingVertex(IGetXY vertex, boolean state);

	/**
	 * deletes selected node
	 * 
	 * @param node
	 *            IGetXY to delete
	 * @return {@code true} if node exists and was deleted else {@code false}
	 */
	boolean deleteNode(IGetXY node);

	/**
	 * deletes selected vertex. If this vertext is part of other group object,
	 * it will be removed from this group also.
	 * 
	 * @param node
	 *            IGetXY to delete
	 * @return {@code true} if node exists and was deleted else {@code false}
	 */
	boolean deleteVertex(IGetXY node);

	/**
	 * deletes selected polyline
	 * 
	 * @param pline -
	 *            interface to handle with vertices list
	 * @return {@code true} if pline existed and was deleted, else {@code false}
	 */
	boolean deleteVerticesList(IGetVertices pline);

	/**
	 * creates a new node with selected coordinates
	 * 
	 * @param x -
	 *            X coordinates
	 * @param y -
	 *            Y coordinates
	 * @return a newly created TcNode object with selected coordinates
	 */
	IGetXY createNode(double x, double y);

	/**
	 * creates a new polyline with user preset coordinates
	 * 
	 * @param coordinates
	 *            array with 2 coordinates per one vertex in the X,Y order. If
	 *            coords is set null, empty polyline will be created and you can
	 *            add more points to it later.
	 * @return a newly created IGetVertices object with selected coordinates
	 */
	IGetVertices createVerticesList(double[] coords);

	/**
	 * creates a new vertex
	 * 
	 * @param x -
	 *            X coordinates
	 * @param y -
	 *            Y coordinates
	 * @return a newly created vertext object with indicated coordinates
	 */
	IGetXY createVertex(double x, double y);

	/**
	 * insert a new vertex into existing polyline
	 * 
	 * @param pline
	 *            polyline to be expanded with selected vertex
	 * @param indexAt
	 *            index where to insert a new vertex
	 * @param vertex
	 *            vertex to insert at indicated position. If this position is >=
	 *            (length -1), vertex is appended to the list
	 */
	void insertVertexAt(IGetVertices pline, int indexAt, IGetXY vertex);

	/**
	 * insert a new vertex into existing polyline
	 * 
	 * @param pline
	 *            polyline to be expanded with selected polyline
	 * @param indexAt
	 *            index where to insert a new vertex
	 * @param vertex
	 *            vertex to insert at indicated position. If this position is >=
	 *            (length -1), vertex is appended to the list
	 */
	void insertVertexListAt(IGetVertices pline, int indexAt, IGetVertices insertion);

	/**
	 * splits pline at point indexes enumerated in indexAt array of int
	 * 
	 * @param vertex
	 *            IGetXY interface to split on it. If it belongs to some parent
	 *            group object (polyline)
	 * @return List of new IGetVertices objects which are children of the pline.
	 */
	boolean splitAtVertex(IGetXY vertex);

	/**
	 * splits pline at point indexes enumerated in indexAt array of int
	 * 
	 * @param vertices
	 *            IGetXY interfaces to split on it. if it belongs to some parent
	 *            group object (polyline)
	 * @return List of new IGetVertices objects which are children of the pline.
	 */
	boolean splitAtVertices(List vertices);

	/**
	 * combine two separate polylines to a single one
	 * 
	 * @param pline1
	 *            polyline 1
	 * @param pline2
	 *            polyline 2
	 * @return {@code true} - they were combined successfully, else
	 *         {@code false}
	 */
	IGetVertices joinTwoVerticesList(IGetVertices pline1, IGetVertices pline2);

	/**
	 * combine many separate polylines to a single one
	 * 
	 * @param plines
	 *            list of IGetVertices objects to combine. They are combines in 
	 *            the indicated sequence, so 2nd to 1st, 3rd to 2nd etc. Of course,
	 *            neighbouring plines shoud have the same end points to be
	 *            combinable.
	 * @return {@code true} - they were combined, else {@code false}
	 */
	IGetVertices joinManyVerticesList(List plines);

}
