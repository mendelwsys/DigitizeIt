/**
 * Created on 28-AUG-2007
 */
package ru.ts.gisutils.tcstore;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.mwlib.utils.Enc;
import ru.ts.gisutils.common.Colls;
import ru.ts.gisutils.common.logger.BaseLogger;
import ru.ts.gisutils.common.logger.ILogger;
import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.Rect;
import ru.ts.gisutils.geometry.XY;
import ru.ts.gisutils.tc.*;
import ru.ts.utils.FunctEmul;

/**
 * Defines the static general data storage only useful for the debugging goal of
 * Yugl. This class not designed to be used in multi-threaded environment
 * 
 * @author sygsky
 * 
 */
// @SuppressWarnings(value="unchecked")
public class TcDataStoreDemo extends TcDataStoreBase
{

	/**
	 * class to store whole data for the polyline split at the point
	 * 
	 * @author sygsky
	 */
	class Split implements Comparable
	{
		// local data
		TcObjPolyline	_pline;

		double		  _split, _x, _y;

		Split(TcObjPolyline pline, double split, double x, double y)
		{
			_pline = pline;
			_split = split;
			_x = x;
			_y = y;
		}

		/**
		 * sort on parent polyline and split value
		 * 
		 * @param arg0 -
		 *            split to compare
		 * @return as usual for this object
		 */
		public int compareTo(Object arg0)
		{
			int ret;
			if ((ret = _pline.compareTo(((Split) arg0)._pline)) != 0)
				return ret;
			return Double.compare(_split, ((Split) arg0)._split);
		}
	}

	/**
	 * creates and stores polylines chain
	 * 
	 * @author sygsky
	 * 
	 */
	public class PlineChain
	{
		PlinePair	_pair;

		/**
		 * create object with an initial pair
		 */
		PlineChain(PlinePair pair)
		{
			_pair = pair;
		}

		ArrayList	_plines	= new ArrayList(); // polylines in correct order

		ArrayList	_orders	= new ArrayList(); // order of points in each pline

		void addLeft(ITcObjPolyline pline, boolean dir)
		{
			_plines.add(0, pline);
			_orders.add(0, Boolean.valueOf(dir));
		}

		void addRight(ITcObjPolyline pline, boolean dir)
		{
			_plines.add(pline);
			_orders.add(Boolean.valueOf(dir));
		}

		/**
		 * fills the whole chain from the pool, removing all new found links
		 * from it.
		 * 
		 * @param pairPool -
		 *            sorted pool of all pairs participating in a joining
		 *            process
		 * @param iteration
		 *            current task iteration
		 * @return new polyline created
		 */
		ITcObjPolyline fillChain(List pairPool, TcIteration iteration)
		{
			/*
			 * check if base pair in not in the pool!!!
			 */
			int index = Collections.binarySearch(pairPool, _pair);
			if (index >= 0)
				throw new IllegalArgumentException(
				        "PlineChain.fillChain: base pair " + this.toString()
				                + "not removed  from a pool");

			_plines = new ArrayList(); // polylines of chain in correct order
			_orders = new ArrayList(); // order of points in each of polylines
			boolean leftfound, rightfound;
			TcObjPolyline left = _pair._pline1;
			TcObjPolyline right = _pair._pline2;
			addLeft(left, _pair._pline1direct);
			addRight(right, _pair._pline2direct);
			ListIterator it;
			int pcount = left.verts().size() + right.verts().size() - 1; // point
			// count
			// select all chain link
			do
			{
				leftfound = rightfound = false;
				it = pairPool.listIterator(pairPool.size());
				left = (TcObjPolyline) _plines.get(0);
				right = (TcObjPolyline) _plines.get(_plines.size() - 1);
				// seek through all the remaining pool for links to the current
				// chain
				while (it.hasPrevious())
				{
					PlinePair curpair = (PlinePair) it.previous();
					// check for a left link
					if (!leftfound)
						if (curpair.hasConnection(left))
						{ // how is this link oriented , that is connected
							// from itself left? or right?
							if (curpair._pline1._tcid.compareTo(left._tcid) == 0)
								addLeft(left = curpair._pline2,
								        curpair._pline2direct);
							else
								addLeft(left = curpair._pline1,
								        curpair._pline1direct);
							pcount += left.verts().size() - 1;
							it.remove();
							// both links are changed, so we restart searching
							if ((leftfound = true) && rightfound)
								break; // yes, both changed
							continue; // no, get next and try rightmost link
							// search
						}
					// check right link
					if (!rightfound)
						if (curpair.hasConnection(right))
						{
							if (curpair._pline1._tcid.compareTo(right._tcid) == 0)
								addRight(right = curpair._pline2,
								        curpair._pline2direct);
							else
								addRight(right = curpair._pline1,
								        curpair._pline1direct);
							pcount += right.verts().size() - 1;
							it.remove();
							// both links are changed, so we restart searching
							if ((rightfound = true) && leftfound)
								break;
						}
				}
				// check while not all possible pairs are connected to the base
				// one
			} while (leftfound || rightfound);

			/*
			 * check for possible circular links. To do it, compare first and
			 * last plines
			 */
			index = _plines.size() - 1; // last index
			if (_cmpTcId.compare(_plines.get(0), _plines.get(index)) == 0)
			{ // yes, circular links, remove last as it is more effective
				right = (TcObjPolyline) _plines.get(index);
				_plines.remove(index);
				_orders.remove(index);
				pcount -= right.verts().size() - 1;
			}

			/*
			 * Well - now we have the chain of links. Lets do the MAIN logic -
			 * collect all coordinates from all pairs, deleting all parents of
			 * this new polyline
			 */
			final double[] pcoords = new double[pcount * 2];
			index = 0;
			for (int i = 0; i < _plines.size() - 1; i++)
			{
				left = (TcObjPolyline) _plines.get(i);
				Boolean order = (Boolean) _orders.get(i);
				int num = left.verts().size(); // number of points in the
				VertexList vl = (VertexList) left.verts();
				if (order.booleanValue()) // then copy from the start offset
					index += vl.getPoints(pcoords, index, 0, (num - 1) * 2);
				else
					// copy from the end offset
					index += vl.getPoints(pcoords, index, (num - 1) * 2,
					        (-num + 1) * 2);
			}

			/*
			 * remove all base polylines
			 */
			wipePolylines(_plines);
			/*
			 * Colls.removeAll( _lines, _plines); wipeVertices(_plines);
			 */
			/*
			 * THAT is ALL. It is time to create a new synthetic polyline from
			 * many (min. 2) base ones.
			 */
			return createPolyline(pcoords, 0, pcoords.length);
		}

		ITcObjPolyline getLeftPline()
		{
			final PlinePair pair = (PlinePair) _plines.get(0);
			return pair._pline1;
		}

		ITcObjPolyline getRightPline()
		{
			final PlinePair pair = (PlinePair) _plines.get(_plines.size() - 1);
			return pair._pline2;
		}
	}

	/**
	 * internal object storage for this class
	 */

	final ILogger	                 _log;

	// all lines
	private final TcObjectList	     _lines;

	// only dangling vertices (should has a copy in one of a polyline from
	// _lines list)
	private final TcObjectList	     _dangling_verts;

	// only connected vertices (should has a copy in one of a polyline from
	// _lines list)
	private final TcObjectList	     _connected_verts;

	// all nodes
	private final TcObjectListSorted	_nodes;

	// only dangling nodes (should has a copy in a _nodes list)
	private final TcObjectListSorted	_dangling_nodes;

	// only connected nodes (should has a copy in a _nodes list)
	private final TcObjectListSorted	_connected_nodes;

	// iteration to do with
	private TcIteration	             _iter;

	/**
	 * constructs object with known iteration
	 * 
	 * @param iter
	 *            TcIteration to handle with
	 */
	public TcDataStoreDemo(TcIteration iter)
	{
		this();
		_iter = iter;
	}

	/**
	 * main constructor - does only internal data intialization, nothing more.
	 * 
	 * If you want to change data initialization, do it HERE!
	 */
	public TcDataStoreDemo()
	{
		_iter = null;

		// initialize internal storage arrays
		_lines = new TcObjectList();
		_dangling_verts = new TcObjectList();
		_connected_verts = new TcObjectList();

		_nodes = new TcObjectListSorted();
		_dangling_nodes = new TcObjectListSorted();
		_connected_nodes = new TcObjectListSorted();

		_log = new BaseLogger(System.out);

		// +++
		// initialize geometry objecs also by adding them to the arrays
		// ---

		double[] arr;
		// line #1
		addPline(createPolyline(arr = new double[] { 1.5, 5.0, 5.0, 8.0, 6.5,
		        4.5, 5.5, 1.5 }, 0, arr.length));
		// line #2
		addPline(createPolyline(arr = new double[] { 1.4, 5.0, 2.5, 9.0, 6.0,
		        9.0, 9.5, 4.5, 5.5, 1.4 }, 0, arr.length));
		// line #3
		addPline(createPolyline(arr = new double[] { 4.0, 3.0, 7.0, 1.5 }, 0,
		        arr.length));

		// TODO: add more polylines HERE

		// node #1
		addNode(createNode(null, 5.6, 0.4));
		// node #2
		addNode(createNode(null, 1.4, 1.5));
		// node #3
		addNode(createNode(null, 5.1, 8.1));

		// TODO: add more nodes HERE
	}

	/**
	 * constructor with replacement of std log stream (System.out) 
	 * to the specified file 
	 * 
	 * @param logFileName  file name to switch output stream to
	 * @param append if <code>true</code> will append text to the existing one, 
	 * if <code>false</code> will overwrite existing file. 
	 * If file not exists, it will be created. 
	 */
	public TcDataStoreDemo(String logFileName, boolean append)
	{
		this();
		try
		{
			_log.appendStream(
					new PrintStream(new FileOutputStream(logFileName, append), true));
			((BaseLogger)_log).removeStream( System.out );
		}
		catch(Exception e)
		{
			_log.logLine("File \""+ logFileName + "\" open error:" + e.toString());
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.TcDataStoreBase#getDanglingNodes()
	 */
	// @Override
	public List getDanglingNodes()
	{
		return (List) _dangling_nodes.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.TcDataStoreBase#getDanglingVertices()
	 */
	// @Override
	public List getDanglingVertices()
	{
		return (List) _dangling_verts.clone();
	}

	private void setDanglingVertex(final TcActSetAttr attr)
	{
		final TcObjVertex vertex = (TcObjVertex) attr.obj;
		ITcObjPolyline pline = getPolyline(vertex);
		if (pline == null)
			throw new IllegalArgumentException(
			        Enc.get("$34")
			                + vertex.toString());
		int index;
		if ((index = _connected_verts.indexOf(vertex)) >= 0)
		{
			_log
			        .logf(
			                " Vertex %s to be set as dangling is marked as connected before\n",
			                new Object[] { vertex.toString() });
			_connected_verts.remove(index);
		}
		if ((index = _dangling_verts.indexOf(vertex)) >= 0)
		{
			_log
			        .logf(
			                "Vertex %s to be set as dangling already is marked as dangling\n",
			                new Object[] { vertex.toString() });
		}
		else
			_dangling_verts.add(vertex);

	}

	/**
	 * process all polyline actions
	 * 
	 * @param del -
	 *            TcActDelObject list
	 * @param join -
	 *            TcActJoin2Plines list
	 * @param split -
	 *            TcActBreakPline list
	 * @return number of actions processed
	 */
	private int processAllPolylines(List del, List join, List split)
	{
		int processed = 0;

		if (del.size() > 0) // so delete all indicated from all lists
		{
			// wipe all join and split operations on deleted polylines
			ArrayList dels = new ArrayList(); // plines to delete
			for (int i = 0; i < del.size(); i++)
				dels.add(((TcActDelObject) (del.get(i))).obj);
			Colls.unique(dels);
			Collections.sort(dels);

			// temp pline to search for
			TcObjPolyline pline = new TcObjPolyline(null, null);

			/*
			 * remove all exccessive join actions remember that join action has
			 * 2 polylines concerned
			 */

			for (int index = join.size() - 1; index >= 0; index--)
			{
				TcActJoin2Plines jact = (TcActJoin2Plines) join.get(index);
				pline._tcid = jact.vert1.tcparent();
				if (Collections.binarySearch(dels, pline) >= 0)
				{
					// remove this action
					_log
					        .logLine(Enc.get("$35")
					                + jact.vert1.tcparent().toString()
					                + Enc.get("$36")
					                + jact.vert2.tcparent().toString()
					                + Enc.get("$37"));
					join.remove(index);
					continue;
				}
				pline._tcid = jact.vert2.tcparent();
				if (Collections.binarySearch(dels, pline) >= 0)
				{
					// remove this action
					_log
					        .logLine(Enc.get("$38")
					                + jact.vert1.tcparent().toString()
					                + Enc.get("$39")
					                + jact.vert2.tcparent().toString()
					                + Enc.get("$40"));
					join.remove(index);
					continue;
				}
			}

			/*
			 * remove all exccessive split actions
			 * 
			 */

			for (int index = split.size() - 1; index >= 0; index--)
			{
				TcActBreakPline sact = (TcActBreakPline) split.get(index);
				pline = (TcObjPolyline) sact.pline;

				// check if in delete list

				if (Collections.binarySearch(dels, pline) >= 0)
				{
					// remove this action
					split.remove(index);
					continue;
				}
			}

			/*
			 * now do delete actions
			 */
			int orig_size = _lines.size();
			wipePolylines(dels);
			processed += orig_size - _lines.size();
		}
		/*
		 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ well -
		 * well - well - it is time to do not so primitive actions
		 * 
		 * lets begin from a JOIN actions
		 * ----------------------------------------------------------
		 */
		processed += processJoins(join);

		processed += processSplits(split);

		return processed;
	}

	static ComparatorTcId	_cmpTcId;
	static
	{
		_cmpTcId = new ComparatorTcId();
	}

	/**
	 * class to store pair of polylines, which will connect each to other
	 */
	class PlinePair implements Comparable
	{
		final ITcObjVertex	_vert1;

		final ITcObjVertex	_vert2;

		final TcObjPolyline	_pline1;

		final TcObjPolyline	_pline2;

		final boolean		_pline1direct; // vertex order of first pline

		final boolean		_pline2direct; // vertex order of second pline

		/**
		 * creates new polyline pair from two joining vertext belonging to them
		 * 
		 * @param vert1 -
		 *            first vertex to join
		 * @param vert2 -
		 *            second vertex to join
		 */
		PlinePair(ITcObjVertex vert1, ITcObjVertex vert2)
		{
			_vert1 = vert1;
			_vert2 = vert2;
			if ((_pline1 = getPolyline(vert1)) == null)
				throw new IllegalArgumentException(
				        Enc.get("$41")
				                + _vert1.toString());

			if ((_pline2 = getPolyline(vert1)) == null)
				throw new IllegalArgumentException(
				        Enc.get("$42")
				                + _vert2.toString());

			/*
			 * direct order of first polyline is provided if this pline is
			 * connected with its last vertext
			 */
			VertexList vl = (VertexList) _pline1.verts();
			_pline1direct = _cmpTcId.compare(_vert1, vl.getLastVertex()) == 0;
			vl = (VertexList) _pline2.verts();
			_pline2direct = _cmpTcId.compare(_vert2, vl.get(0)) == 0;
		}

		/**
		 * work constructor for internal puposes
		 * 
		 * @param pline1 -
		 *            first pline to join
		 * @param pline2 -
		 *            second pline to join
		 */
		PlinePair(TcObjPolyline pline1, TcObjPolyline pline2)
		{
			_pline1 = pline1;
			_pline2 = pline2;
			_vert1 = _vert2 = null;
			_pline1direct = _pline2direct = true;
		}

		/**
		 * detects can this pair to connect with indicated one
		 * 
		 * @param pair -
		 *            PlinePair to test connectivity
		 * @return {@code true} - if can, {@code false} - if cannot
		 */
		boolean hasConnection(PlinePair pair)
		{
			return hasConnection(pair._pline1) || hasConnection(pair._pline2);
		}

		/**
		 * checks if pline can be connected with plines of this pair
		 * 
		 * @param pline
		 *            TcObjPolyline to compare with
		 * @return true if indicated pline has the same TcId as one of polylines
		 *         of this pair
		 */
		boolean hasConnection(ITcObjPolyline pline)
		{
			return (_cmpTcId.compare(_pline1, pline) == 0)
			        || (_cmpTcId.compare(_pline2, pline) == 0);
		}

		/**
		 * returns all vertices in correct order, beginning from a first
		 * polyline
		 */
		List getVertices() throws CloneNotSupportedException
		{
			final VertexList vl1 = (VertexList) _pline1.verts();
			final VertexList vl2 = (VertexList) _pline2.verts();
			final VertexList res = new VertexList();
			if (_pline1direct)
			{
				for (int i = 0; i < vl1.size(); i++)
					res.add(vl1.get(i));
			}
			else
			{
				for (int i = vl1.size() - 1; i >= 0; i--)
					res.add(vl1.get(i));
			}

			/*
			 * don't include first vertex as it is already inserted into the
			 * list from a first pline in the pair
			 */
			if (_pline2direct)
				for (int i = 1; i < vl2.size(); i++)
					res.add(vl2.get(i));
			else
				for (int i = vl2.size() - 2; i >= 0; i--)
					res.add(vl2.get(i));
			return res;
		}

		/**
		 * @return the _pline1
		 */
		ITcObjPolyline get_pline1()
		{
			return _pline1;
		}

		/**
		 * @return the _pline2
		 */
		ITcObjPolyline get_pline2()
		{
			return _pline2;
		}

		/**
		 * returns set of points consisting pair in the predefined order when
		 * new polyline start from first polyline vertex opposite to vertex1 and
		 * ends with opposite vertex of polyline2
		 * 
		 * @return double array with points in X,Y,... sequence for each vertex
		 *         of pair in correct order
		 */
		double[] getPointsOfPair()
		{
			final VertexList vl1 = (VertexList) _pline1.verts();
			final VertexList vl2 = (VertexList) _pline2.verts();
			final double[] res = new double[(vl1.size() + vl2.size() - 1) * 2];
			int index = 0;
			if (_pline1direct)
				for (int i = 0; i < vl1.size(); i++)
				{
					IGetXY xy = (IGetXY) vl1.get(i);
					res[ index++ ] = xy.getX();
					res[ index++ ] = xy.getY();
				}
			else
				for (int i = vl1.size() - 1; i >= 0; i--)
				{
					IGetXY xy = (IGetXY) vl1.get(i);
					res[ index++ ] = xy.getX();
					res[ index++ ] = xy.getY();
				}

			//
			// don't include first vertex as it is already inserted into the
			// list
			// from a first pline in the pair
			//
			if (_pline2direct)
				for (int i = 1; i < vl2.size(); i++)
				{
					IGetXY xy = (IGetXY) vl2.get(i);
					res[ index++ ] = xy.getX();
					res[ index++ ] = xy.getY();
				}
			else
				for (int i = vl2.size() - 2; i >= 0; i--)
				{
					IGetXY xy = (IGetXY) vl2.get(i);
					res[ index++ ] = xy.getX();
					res[ index++ ] = xy.getY();
				}
			return res;
		}

		/*
		 * to compare two pair is not a trivial thing. Should be 2 conditions on
		 * it: 1. 2pairs are equal if both its vertices are equal symmetrically,
		 * that is ((1.1 == 2.1) OR (1.1 == 2.2)) AND ((1.2 == 2.2) OR (1.2 ==
		 * 2.1)) 2. they should be comparable (be lesser or be bigger)
		 * correctly.
		 * 
		 * I decided to compare them in a sorted order of their vertices, that
		 * is first to sort vertices of each pair, and only as second step
		 * compare them.
		 */
		public int compareTo(Object arg0)
		{
			final PlinePair pair2 = (PlinePair) arg0;
			final Object p11, p12, p21, p22;

			// detect orders of first pair
			if (_cmpTcId.compare(_pline1, _pline2) < 0)
			{
				p11 = _pline1;
				p12 = _pline2;
			}
			else
			{
				p11 = _pline2;
				p12 = _pline1;
			}

			// detect orders of second pair
			if (_cmpTcId.compare(pair2._pline1, pair2._pline2) < 0)
			{
				p21 = pair2._pline1;
				p22 = pair2._pline2;
			}
			else
			{
				p21 = pair2._pline2;
				p22 = pair2._pline1;
			}
			int res;
			if ((res = _cmpTcId.compare(p11, p21)) != 0)
				return res;
			return _cmpTcId.compare(p12, p22);
		}

		public String toString()
		{
			return this._pline1.toString() + ":" + this._pline2.toString();
		}

	}

	private int processJoins(List joins)
	{
		if (joins.size() == 0)
			return 0;
		_log.logLine("+++ JOIN +++");
		try
		{
			final ArrayList plines = new ArrayList();
			final ArrayList pairs = new ArrayList();

			// create and fill all pairs used list
			for (int i = 0; i < joins.size() - 1; i++)
			{
				TcActJoin2Plines act = (TcActJoin2Plines) joins.get(i);
				pairs.add(new PlinePair(act.vert1, act.vert2));
			}
			// sort pairs to provide for fast search. May be it is not needed
			// :o)
			Collections.sort(pairs);

			// start processing all the chains for joining polylines
			while (pairs.size() > 0)
			{
				// always works with a last item in the list
				int index = pairs.size() - 1;
				PlinePair pair = (PlinePair) pairs.get(index);
				pairs.remove(index); // remove it from the list
				PlineChain chain = new PlineChain(pair); // prepare a new
				// chain
				plines.add(chain.fillChain(pairs, _iter)); // get new polyline
				// and
				// store it
			}
			_lines.addAll(plines);
			int ret = plines.size();
			plines.clear();
			pairs.clear();
			return ret; // counter of new polylines
		}
		finally
		{
			_log.logLine("--- end of JOIN ---");
		}
	}

	private int processSplits(List splits)
	{
		int size;
		if ((size = splits.size()) == 0)
			return 0;
		_log.logLine("+++ SPLIT +++");
		try
		{
			ArrayList slist = new ArrayList();
			// Collections.sort(slist);

			// create and fill all splits used list
			for (int i = 0; i < size; i++)
			{
				IGetXY xy;
				TcActBreakPline act = (TcActBreakPline) splits.get(i);
				if (act.correction == TcLocInfo.MOVE_NODE_TO_NEAREST_LINE)
				{ // then set coordinates to be totally equal to the point of
					// interest
					int index = (int) act.index;
					xy = act.pline.verts().getXY(index);
				}
				else
				{ // simply copy X,Y of the node inserted to pline
					xy = act.xy;
				}
				slist
				        .add(new Split((TcObjPolyline)act.pline, act.index, xy.getX(), xy.getY()));
			}
			// sort on plines/split index to find multiple splits for a single
			// pline
			Collections.sort(slist);
			int num = 1; // number of splits for a single polyline
			ITcObjPolyline pline = ((Split) slist.get(size - 1))._pline;
			for (int i = size - 2; i >= 0; i--)
			{
				Split split = (Split) slist.get(i);
				if (split._pline.compareTo(pline) != 0)
				{
					flushSplits(slist, i + 1, num);
					num = 1;
					pline = split._pline;
				}
				else
					num++;
			}
			flushSplits(slist, 0, num);
			return size;
		}
		finally
		{
			_log.logLine("--- end of SPLIT ---");
		}
	}

	private void flushSplits(ArrayList list, int index, int num)
	{
		if (num <= 0)
		{
			_log.logLine("flushSplits call with illegal number of splits = "
			        + num);
			return;
		}
		final TcObjPolyline pline = ((Split) list.get(index))._pline;
		final double[] sxy = new double[pline.verts().size() * 2];
		final Iterator itrpoly = ((VertexList) pline.verts()).iterator(); // pline
		// vertices
		// iterator
		// final Iterator its = list.iterator(); // splits iterator
		final int finish = index + num - 1; // last split object index
		int off = 0;
		Split split = (Split) list.get(index); // current split
		int sindex = (int) split._split; // split index
		while (itrpoly.hasNext())
		{
			TcObjVertex vertex = (TcObjVertex) itrpoly.next();
			int diff = FunctEmul.isignum(vertex._index - sindex);
			switch ( diff )
			{
			case -1: // vertex index is smaller than split
			case 1: // vertex index is greater than split
				sxy[ off++ ] = vertex.getX();
				sxy[ off++ ] = vertex.getY();
				break;
			case 0: // vertex index is equal
				sxy[ off++ ] = vertex.getX();
				sxy[ off++ ] = vertex.getY();
				// check if split is exactly on the vertex itself

				if (!XY.geometry().nearBoth(vertex.getX(), vertex.getY(), split._x,
				        split._y))
				{ // append only if not the same point
					sxy[ off++ ] = split._x;
					sxy[ off++ ] = split._y;
				}
				addPline(createPolyline(sxy, 0, off));
				// complete the new line creation
				// and of course, start a new polyline from the same split
				// point
				off = 0;
				sxy[ off++ ] = split._x;
				sxy[ off++ ] = split._y;
				if (index < finish)
				{
					index++;
					split = (Split) list.get(index); // current split
					sindex = (int) split._split; // point index
				}
				break;
			}
		}
		addPline(createPolyline(sxy, 0, off));
		// now remove old polyline
		wipePolyline(pline);
	}

	private void moveVertex(final TcActMoveXY move)
	{
		final TcObjVertex vertex1 = (TcObjVertex) move.obj;
		final TcObjVertex vertex2 = (TcObjVertex) getVertex(vertex1);
		final XY xy = (XY) vertex2._xy;
		xy.set(vertex1);
	}

	private void setConnectedVertex(final TcActSetAttr attr)
	{
		final TcObjVertex vertex = (TcObjVertex) attr.obj;
		int index;
		ITcObjPolyline pline = getPolyline(vertex);
		if (pline == null)
			throw new IllegalArgumentException(
			        Enc.get("$43")
			                + vertex._tcid.toString() + Enc.get("$44"));
		if ((index = _dangling_verts.indexOf(vertex)) >= 0)
		{
			_log
			        .log("Vertex "
			                + vertex.toString()
			                + " was marked as dangling and simultaneously is going to be set as connected\nIt is set to be connected from now.");
			_dangling_verts.remove(index);
		}
		if ((index = _connected_verts.indexOf(vertex)) >= 0)
		{
			_log.log("Vertex " + vertex.toString()
			        + " was already marked as connected");
		}
		else
			_connected_verts.add(vertex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.TcDataStoreBase#getEndVertices()
	 */
	// @Override
	public List getEndVertices()
	{
		final ArrayList _vertices = new ArrayList(_lines.size() * 2);
		TcObjPolyline line;
		TcObjVertex vert;
		for (int index = 0; index < _lines.size(); index++)
		{
			line = (TcObjPolyline) _lines.get(index);
			vert = (TcObjVertex) line.verts().getXY(0);
			_vertices.add((new TcObjVertex(vert._tcid, 0, vert, line._tcid)));
			int lastind = line.verts().size() - 1;
			vert = (TcObjVertex) line.verts().getXY(lastind);
			_vertices
			        .add(new TcObjVertex(vert._tcid, lastind, vert, line._tcid));
		}
		return (List) _vertices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.TcDataStoreBase#getNodes()
	 */
	// @Override
	public List getNodes()
	{
		return (List) _nodes.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.TcDataStoreBase#getPolylines()
	 */
	// @Override
	public List getPolylines()
	{
		return (List) _lines.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.ITcDataStore#getPolylines(ru.ts.gisutils.tc.Rect)
	 */
	// @Override
	public List getPolylines(Rect rect)
	{
		return filter(_lines, rect);
	}

	/**
	 * creates polyline from the array of coordinates pairs
	 * 
	 * @param coords
	 *            contains coordinates X,Y for {coords.length/2} points for the
	 *            new polyline to create
	 * @return TcPolyline with wanted points
	 */
	private ITcObjPolyline createPolyline(double[] coords)
	{
		return createPolyline(coords, 0, coords.length);
	}

	/**
	 * creates polyline from the array of coordinates pairs
	 * 
	 * @param coords
	 *            contains coordinates X,Y for {coords.length/2} points for the
	 *            new polyline to create
	 * @param start
	 *            is the index from which to begin read points from 'coords'
	 * @param length
	 *            is number of points to insert to polyline from 'coords' array
	 * @return TcPolyline with wanted points
	 */
	private TcObjPolyline createPolyline(double[] coords, int start, int length)
	{
		if ((length % 2) != 0)
			throw new IllegalArgumentException(
			        Enc.get("$45"));

		final VertexList vl = new VertexList();
		int index = start, pindex = 0, lastindex = index + length;
		XY xy;
		TcId pl_tcid;
		final TcObjPolyline pline = new TcObjPolyline(pl_tcid = getNextTcId(),
		        null);
		while (index < lastindex)
		{
			xy = new XY(coords[ index++ ], coords[ index++ ]);
			if (_iter == null)
				vl.add(new TcObjVertex(getNextTcId(), pindex++, xy, pl_tcid));
			else
				vl.add(new TcObjVertex(_iter.newTcId(), pindex++, xy, pl_tcid));
		}
		pline._verts = vl;
		return pline;
	}

	/**
	 * creates node from the coordinates pair
	 * 
	 * @param coords
	 *            contains coordinates X,Y for the node to create
	 * @return TcNode with wanted coordinates
	 */
	private TcObjNode createNode(double x, double y)
	{
		return createNode(_iter, x, y);
	}

	/**
	 * creates node from the coordinates pair
	 * 
	 * @param iter
	 *            TcIteration object for this call
	 * 
	 * @param coords
	 *            contains coordinates X,Y for the node to create
	 * @return TcNode with wanted coordinates
	 */
	private static TcObjNode createNode(TcIteration iter, double x, double y)
	{
		if (iter == null)
			return new TcObjNode(new TcId(0, getNextGlobalId()), new XY(x, y));
		return new TcObjNode(iter.newTcId(), new XY(x, y));
	}

	/**
	 * item to provide thread-safe call sequential unique integer values
	 */
	private static AtomicInteger	_poly_id	= new AtomicInteger();

	/**
	 * returns new unique integer id on each call
	 * 
	 * @return new integer value. It is guarantied that any thread number could
	 *         call this methoв simultaneously and any of them will get unique
	 *         values, but may be not in the strict serial sequence.
	 */
	public static int getNextGlobalId()
	{
		return (int) _poly_id.incrementAndGet();
	}

	/**
	 * @return new integer id from an iteration existed or simply some unique
	 *         integer value
	 */
	public int getNextId()
	{
		if (_iter == null)
			return getNextGlobalId();
		return _iter.nextNumber();

	}

	public TcId getNextTcId()
	{
		if (_iter == null)
			return new TcId(0, getNextId());
		return _iter.newTcId();
	}

	/**
	 * Executes the set of operaitons, which should be done on a current data
	 * set (ala transaction)
	 * 
	 * @param iteration
	 *            TcIteration of indicated actions
	 * @param actions -
	 *            list of objects of TcAction type
	 */
	public void doActions(TcIteration iteration, List actions)
	{
		_log.logLine(Enc.get("$46") + iteration.id()
		        + Enc.get("$47") + actions.size());
		_iter = iteration; // set iteration
		Iterator it = actions.iterator();
		int type, index;
		int processed = 0;
		XY xy;
		TcObjNode node;
		// TcObjVertex vertex;
		TcActSetAttr attr;
		TcActMoveXY move;
		List _plines_delete = new ArrayList(), _plines_join = new ArrayList(), _plines_split = new ArrayList();
		while (it.hasNext())
		{
			TcAction act = (TcAction) it.next();
			if (act.state == TcAction.STATE_DONE)
				continue;
			// TcObjNode obj;
			type = act.type();
			switch ( type )
			{
			case TcAction.NONE: // On NONE and judgment none
				break;

			case TcAction.NODE_MAKE_NEW: // create a new node
				xy = (XY) ((TcActNewNode) act).xy;
				node = createNode(xy.getX(), xy.getY());
				node._tcid = iteration.newTcId();
				_nodes.add(node);
				processed++;
				break;

			case TcAction.NODE_DELETE:
				node = (TcObjNode) ((TcActDelObject) act).obj;
				index = this.getNodeIndex(node);
				if (index < 0)
					throw new IllegalArgumentException(
					        Enc.get("$48"));
				node = (TcObjNode) _nodes.get(index);
				removeNode(node);
				processed++;
				break;

			case TcAction.NODE_MARK_AS_CONNECTED:
				attr = (TcActSetAttr) act;
				node = (TcObjNode) attr.obj;
				index = this.getNodeIndex(node);
				if (index < 0)
					throw new IllegalArgumentException(
					        Enc.get("$49") + node
					                + Enc.get("$50"));
				if ((index = _dangling_nodes.indexOf(node)) >= 0)
				{
					_log.logLine(Enc.get("$51") + node
					        + Enc.get("$52"));
					_dangling_nodes.remove(index);
				}
				if ((index = _connected_nodes.indexOf(node)) >= 0)
				{
					_log.logLine(Enc.get("$53") + node
					        + Enc.get("$54"));
				}
				else
				{
					_connected_nodes.add(node);
					processed++;
				}
				break;

			case TcAction.NODE_MARK_AS_DANGLING:
				attr = (TcActSetAttr) act;
				node = (TcObjNode) attr.obj;
				index = this.getNodeIndex(node);
				if (index < 0)
					throw new IllegalArgumentException(
					        Enc.get("$55") + node
					                + Enc.get("$56"));
				if ((index = _connected_nodes.indexOf(node)) >= 0)
				{
					_log.logLine(Enc.get("$57") + node
					        + Enc.get("$58"));
					_connected_nodes.remove(index);
				}
				if ((index = _dangling_nodes.indexOf(node)) >= 0)
				{
					_log.logLine(Enc.get("$59") + node
					        + Enc.get("$60"));
				}
				else
				{
					_dangling_nodes.add(node);
					processed++;
				}
				break;

			case TcAction.NODE_MOVE_TO: // move node position
				move = (TcActMoveXY) act;
				IGetXY getxy = move.xy;
				moveNode((TcObjNode) move.obj, getxy.getX(), getxy.getY());
				processed++;
				break;

			/**
			 * all other actions on polylines collect to process in groups
			 */
			/*
			 * case TcAction.PLINE_BREAK_AND_SHIFT:
			 */case TcAction.PLINE_BREAK_AT:
				_plines_split.add(act);
				break;
			case TcAction.PLINE_DELETE:
				_plines_delete.add(act);
				break;
			case TcAction.PLINE_JOIN_TWO:
				_plines_join.add(act);
				break;

			case TcAction.VERT_MARK_AS_CONNECTED:
				setConnectedVertex((TcActSetAttr) act);
				break;

			case TcAction.VERT_MARK_AS_DANDLING:
				setDanglingVertex((TcActSetAttr) act);
				break;

			case TcAction.VERT_MOVE_TO:
				moveVertex((TcActMoveXY) act);
				break;

			case TcAction.OBJ_DELETE:
				// break;
			case TcAction.OBJ_MAKE_NEW:
				// break;
			default:
				System.out.println("TcAction.type()=" + act.type()
				        + Enc.get("$61"));
			} // end of switch
		} // end if while
		processed += processAllPolylines(_plines_delete, _plines_join,
		        _plines_split);
		System.out.println("TcDataStoreDemo.doAction: " + processed
		        + " action[s] processed");

	}

	/**
	 * finds for the node in internal list
	 * 
	 * @param node
	 *            object to find for
	 * @return index of node found, -1 if no such node exists in internal array
	 */
	private int getNodeIndex(ITcObjNode node)
	{
		return _nodes.indexOf(node);
	}

	/**
	 * finds vertex by searching its polyline and get correct vertex from
	 * internal polyline collection
	 */
	private ITcObjVertex getVertex(final TcObjVertex vertex)
	{
		// find the polyline this vertex belongs
		ITcObjPolyline pline;
		if ((pline = getPolyline(vertex)) == null)
			return null;
		// get correct vertex object
		int index = ((VertexList) pline.verts()).indexOfTcObject(vertex);
		if (index < 0)
			return null;
		return (ITcObjVertex) pline.verts().getXY(index);
	}

	private TcObjPolyline getPolyline(final ITcObjVertex vertex)
	{
		// find the polyline this vertex belongs to
		final ITcObjPolyline pline = new TcObjPolyline(vertex.tcparent(), null);
		int index;
		if ((index = _lines.indexOf(pline)) < 0)
			return null;
		return (TcObjPolyline) _lines.get(index);
	}

	/*
	 * remove indicated node object from all lists
	 */
	private void removeNode(TcObjNode node)
	{
		// remove from the main list
		_nodes.remove(node);
		// remove from additional lists also (just in case)
		_dangling_nodes.remove(node);
		_connected_nodes.remove(node);
		_log.logLine("Node removed:" + node.toString());
		// TODO: add special logic here if it is needed for removing
	}

	/*
	 * move indicated node object in all known lists. Note: this method assumes
	 * that all lists are sorted by TcId
	 */
	private void moveNode(TcObjNode node, double x, double y)
	{
		// move in the main list
		int index = getNodeIndex(node);
		if (index < 0)
		{
			_log.logLine(Enc.get("$62") + node.toString()
			        + Enc.get("$63"));
			return;
		}
		node = (TcObjNode) _nodes.get(index);
		((XY) node._xy).set(x, y);
		// move in additional lists also (just in case)
		index = Collections.binarySearch(_connected_nodes, node);
		if (index >= 0)
		{
			node = (TcObjNode) _connected_nodes.get(index);
			((XY) node._xy).set(x, y);
		}
		index = Collections.binarySearch(_dangling_nodes, node);
		if (index >= 0)
		{
			node = (TcObjNode) _dangling_nodes.get(index);
			((XY) node._xy).set(x, y);
		}
		_dangling_nodes.remove(node);
		// TODO: add special logic here if it is needed for removing
	}

	private void wipePolylines(List plines2del)
	{
		Colls.removeAll(_lines, plines2del);
		wipeVertices(plines2del);
		// prepare report text
		final StringBuffer sb = new StringBuffer(((TcObjPolyline) plines2del
		        .get(0)).toString());
		for (int i = 1; i < plines2del.size(); i++)
		{
			sb.append(',');
			sb.append(((TcObjPolyline) plines2del.get(i)).toString());
		}
		_log.logLine("Plines removed:" + sb.toString());
	}

	private void wipePolyline(TcObjPolyline pline)
	{
		wipeVertices(pline);
		_lines.remove(pline);
		_log.logLine("Polyline removed:" + pline.toString());
	}

	/**
	 * removes all vertices, counted in other arrays of TcObjVertex which
	 * belongs to collected polylines.
	 * 
	 * @param actions -
	 *            TcObjPolyline list containing sorted array of polylines to
	 *            remove polylines from a system at this iteration
	 */
	private void wipeVertices(List plines2del)
	{
		Collections.sort(plines2del); // already sorted on TcId, may remove in
										// the future
		int index;
		TcObjPolyline pline = new TcObjPolyline(null, null);

		// remove connected states
		for (index = _connected_verts.size() - 1; index >= 0; index--)
		{
			pline._tcid = ((TcObjVertex) _connected_verts.get(index)).tcparent();
			if (Collections.binarySearch(plines2del, pline) >= 0)
				_connected_verts.remove(index);
		}
		// remove dangling states
		for (index = _dangling_verts.size() - 1; index >= 0; index--)
		{
			pline._tcid = ((TcObjVertex) _dangling_verts.get(index)).tcparent();
			if (Collections.binarySearch(plines2del, pline) >= 0)
				_dangling_verts.remove(index);
		}
	}

	private void wipeVertices(TcObjPolyline pline)
	{
		// remove connected states
		for (int index = _connected_verts.size() - 1; index >= 0; index--)
		{
			TcId tcid = ((TcObjVertex) _connected_verts.get(index)).tcparent();
			if (tcid.equals(pline.tcid()))
				_connected_verts.remove(index);
		}
		// remove dangling states
		for (int index = _dangling_verts.size() - 1; index >= 0; index--)
		{
			TcId tcid = ((TcObjVertex) _dangling_verts.get(index)).tcparent();
			if (tcid.equals(pline.tcid()))
				_dangling_verts.remove(index);
		}
	}

	private void addPline(TcObjPolyline pline)
	{
		_lines.add(pline);
		_log.logLine("Pline added:" + pline.toString());
	}

	private void addNode(TcObjNode node)
	{
		_nodes.add(node);
		_log.logLine("Node added:" + node.toString());
	}
}
