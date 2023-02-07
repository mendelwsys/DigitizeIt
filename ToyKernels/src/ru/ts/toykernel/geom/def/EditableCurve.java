package ru.ts.toykernel.geom.def;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.geom.IEditableGisObject;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.consts.INameConverter;

import java.util.*;


/**
 * Editble gis object
 * Редактируемая кривая
 */
public class EditableCurve
		extends ROCurve implements IEditableGisObject
{
	protected String parentId;

	public boolean isChangeGeom()
	{
		return isChangeGeom;
	}

	public boolean isChangeAttrs()
	{
		return isChangeAttrs;
	}

	protected boolean isChangeGeom;
	protected  boolean isChangeAttrs;

	protected IAttrs getIObjAttrs()
	{
		if (parentId!=null)
			return attrsPool.get(parentId);
		else
			return super.getIObjAttrs();
	}

	public IAttrs getObjAttrs()
	{
		IAttrs attrs = getIObjAttrs();
		if (attrs!=null)
			return new DefaultAttrsImpl(attrs,true);
		return attrs;
	}

	public void setInstance(IBaseGisObject _curve) throws Exception
	{
		super.setInstance(_curve);
		if (_curve instanceof IEditableGisObject)
			((IEditableGisObject)_curve).setParentId(this.parentId);
	}


	public void setGeotype(String geotype)
	{
		isChangeGeom=isChangeGeom || ((this.geotype==null || geotype==null) && this.geotype!=geotype) || !this.geotype.equals(geotype);
		this.geotype=geotype;
	}


	public String getParentId() throws Exception
	{
		return parentId;
	}

	public void setParentId(String parentId) throws Exception
	{
		this.parentId=parentId;
	}

	public void removePoint(int index)
	{
		Pair<Integer, Integer> prindex = splitIndex(index);

		List<double[]> pArrXL = new LinkedList<double[]>();
		List<double[]> pArrYL = new LinkedList<double[]>();

		//Копирование не затронутых массивов
		for (int i = 0; i < prindex.first; i++)
		{
			pArrXL.add(pArrX[i]);
			pArrYL.add(pArrY[i]);
		}

		if (pArrX[prindex.first].length - 1 > 0)
		{
			double[] pX = new double[pArrX[prindex.first].length - 1];
			double[] pY = new double[pArrY[prindex.first].length - 1];

			for (int j = 0, i = 0; i < pX.length; i++, j++)
			{
				if (prindex.second == j)
					j++;

				pX[i] = pArrX[prindex.first][j];
				pY[i] = pArrY[prindex.first][j];
			}
			pArrXL.add(pX);
			pArrYL.add(pY);
		}

		for (int i = prindex.first + 1; i < pArrX.length; i++)
		{
			pArrXL.add(pArrX[i]);
			pArrYL.add(pArrY[i]);
		}

		pArrX = pArrXL.toArray(new double[pArrXL.size()][]);
		pArrY = pArrYL.toArray(new double[pArrYL.size()][]);
		onChangeGeometry();
	}

	public void add2AllCoordinates(MPoint addpnt) throws Exception
	{
		for (int i = 0; i < pArrX.length; i++)
			for (int j = 0; j < pArrX[i].length; j++)
			{
				pArrX[i][j] += addpnt.x;
				pArrY[i][j] += addpnt.y;
			}
		onChangeGeometry();
	}

	public void add2SegCoordinates(int segindex, MPoint addpnt) throws Exception
	{
		for (int j = 0; j < pArrX[segindex].length; j++)
		{
			pArrX[segindex][j] += addpnt.x;
			pArrY[segindex][j] += addpnt.y;
		}
		onChangeGeometry();
	}

	public void setPoint(int index, MPoint pnt) throws Exception
	{
		Pair<Integer, Integer> prindex = splitIndex(index);
		pArrX[prindex.first][prindex.second] = pnt.x;
		pArrY[prindex.first][prindex.second] = pnt.y;
		onChangeGeometry();
	}

	public void add2Point(int index, MPoint addpnt) throws Exception
	{
		Pair<Integer, Integer> prindex = splitIndex(index);
		pArrX[prindex.first][prindex.second] += addpnt.x;
		pArrY[prindex.first][prindex.second] += addpnt.y;
		onChangeGeometry();
	}

	public void addPoint(int index, MPoint pntnew) throws Exception
	{
		Pair<Integer, Integer> prindex;
		if (index>=0)
			prindex = splitIndex4Add(index);
		else
			prindex=new Pair<Integer, Integer>(pArrX.length,pArrX.length>0?pArrX[pArrX.length-1].length:0);

		List<double[]> pArrXL = new LinkedList<double[]>();
		List<double[]> pArrYL = new LinkedList<double[]>();

		//Копирование не затронутых массивов
		for (int i = 0; i < prindex.first; i++)
		{
			pArrXL.add(pArrX[i]);
			pArrYL.add(pArrY[i]);
		}

		if (pArrX.length<=prindex.first)
		{
			pArrXL.add(new double[]{pntnew.x});
			pArrYL.add(new double[]{pntnew.y});
		}
		else
		if (pArrX[prindex.first].length > 0)
		{
			double[] pX = new double[pArrX[prindex.first].length + 1];
			double[] pY = new double[pArrY[prindex.first].length + 1];

			int j = 0, i = 0;
			for (; j < pArrX[prindex.first].length; i++, j++)
			{
				if (prindex.second == i)
				{
					pX[i] = pntnew.x;
					pY[i] = pntnew.y;
					i++;
				}
				pX[i] = pArrX[prindex.first][j];
				pY[i] = pArrY[prindex.first][j];
			}

			if (prindex.second == i)
			{
				pX[i] = pntnew.x;
				pY[i] = pntnew.y;
			}

			pArrXL.add(pX);
			pArrYL.add(pY);
		}

		for (int i = prindex.first + 1; i < pArrX.length; i++)
		{
			pArrXL.add(pArrX[i]);
			pArrYL.add(pArrY[i]);
		}

		pArrX = pArrXL.toArray(new double[pArrXL.size()][]);
		pArrY = pArrYL.toArray(new double[pArrYL.size()][]);
		onChangeGeometry();
	}

	/**
	 * Точка по которой производится разбиение отходит новому сегменту
	 * @param index - индекс точки
	 */
	public void splitCurveByPoint(int index)
	{
		Pair<Integer, Integer> seg2index = splitIndex(index);

		if (seg2index.second==0)
			return;

		int newseglength=pArrX[seg2index.first].length-seg2index.second;

		List<double[]> pArrXL = new LinkedList<double[]>();
		List<double[]> pArrYL = new LinkedList<double[]>();

		double[] pOldX = new double[seg2index.second];
		double[] pOldY = new double[seg2index.second];

		double[] pNewX = new double[newseglength];
		double[] pNewY = new double[newseglength];

		//Заполняем то что осталось от старого сегмента
		for (int i = 0; i < seg2index.second; i++)
		{
			pOldX[i]=pArrX[seg2index.first][i];
			pOldY[i]=pArrY[seg2index.first][i];
		}

		//Заполняем то что осталось новый сегмент
		for (int i = 0,j=seg2index.second; i < newseglength; i++,j++)
		{
			pNewX[i]=pArrX[seg2index.first][j];
			pNewY[i]=pArrY[seg2index.first][j];
		}

		int i = 0;
		for ( ;i < pArrX.length; i++)
		{
			if (i==seg2index.first)
			{
				pArrXL.add(pOldX);
				pArrYL.add(pOldY);

				pArrXL.add(pNewX);
				pArrYL.add(pNewY);
			}
			else
			{
				pArrXL.add(pArrX[i]);
				pArrYL.add(pArrY[i]);
			}
		}

		pArrX = pArrXL.toArray(new double[pArrXL.size()][]);
		pArrY = pArrYL.toArray(new double[pArrYL.size()][]);
		onChangeGeometry();

	}

	public void mergeSegments(int segindex1, int segindex2)
	{
		if (segindex1==segindex2)
			return;

		double[] pX=pArrX[segindex2];
		double[] pY=pArrY[segindex2];

		add2Segment(segindex1,getSegLength(segindex1),pX,pY);
		removeSegment(segindex2);

	}

	public int mergeSegments(int segindex1, int segindex2, MPoint pntnew)
	{
		add2Segment(segindex1,getSegLength(segindex1),new MPoint[]{pntnew});
		int rindex = mergeIndex(new Pair<Integer, Integer>(segindex1, getSegLength(segindex1) - 1));
		mergeSegments(segindex1,segindex2);
		return rindex;
	}

	public void add2Segment(int segindex, int index, MPoint[] points)
	{
		double[] pX = new double[points.length];
		double[] pY = new double[points.length];
		for (int i = 0; i < pX.length; i++)
		{
			pX[i]=points[i].x;
			pY[i]=points[i].y;
		}
		add2Segment(segindex, index, pX, pY);
	}

	public void add2Segment(int segindex, int index, double[] pX, double[] pY)
	{
		double [] oldsX=pArrX[segindex];
		double [] oldsY=pArrY[segindex];


		pArrX[segindex]=new double[oldsX.length+pX.length];
		pArrY[segindex]=new double[oldsY.length+pY.length];

		System.arraycopy(oldsX,0,pArrX[segindex],0,index);
		System.arraycopy(pX,0,pArrX[segindex],index,pX.length);
		System.arraycopy(oldsX,index,pArrX[segindex],index+pX.length,oldsX.length-index);

		System.arraycopy(oldsY,0,pArrY[segindex],0,index);
		System.arraycopy(pY,0,pArrY[segindex],index,pY.length);
		System.arraycopy(oldsY,index,pArrY[segindex],index+pY.length,oldsY.length-index);
		onChangeGeometry();
	}

	public void addSegment(int segindex, MPoint[] points)
	{
		double[] pX = new double[points.length];
		double[] pY = new double[points.length];
		for (int i = 0; i < pX.length; i++)
		{
			if (points[i]==null)
				System.out.println("Error");
			pX[i]=points[i].x;
			pY[i]=points[i].y;
		}
		addSegment(segindex, pX, pY);
	}

	public void addSegment(int segindex, double[] pX, double[] pY)
	{
		List<double[]> pArrXL = new LinkedList<double[]>();
		List<double[]> pArrYL = new LinkedList<double[]>();
		int i = 0;
		for ( ;i < pArrX.length; i++)
		{
			if (i==segindex)
			{
				pArrXL.add(pX);
				pArrYL.add(pY);
			}
			pArrXL.add(pArrX[i]);
			pArrYL.add(pArrY[i]);
		}

		if (i==segindex)
		{
			pArrXL.add(pX);
			pArrYL.add(pY);
		}
		pArrX = pArrXL.toArray(new double[pArrXL.size()][]);
		pArrY = pArrYL.toArray(new double[pArrYL.size()][]);
		onChangeGeometry();
	}

	public void removeSegment(int segindex)
	{
		List<double[]> pArrXL = new LinkedList<double[]>();
		List<double[]> pArrYL = new LinkedList<double[]>();


		for (int i = 0; i < pArrX.length; i++)
		{
			if (i!=segindex)
			{
				pArrXL.add(pArrX[i]);
				pArrYL.add(pArrY[i]);
			}
		}

		pArrX = pArrXL.toArray(new double[pArrXL.size()][]);
		pArrY = pArrYL.toArray(new double[pArrYL.size()][]);
		onChangeGeometry();
	}

	public void onChangeGeometry()
	{
		super.rebuildGisValume();
		isChangeGeom=true;
	}
	protected EditableCurve(IBaseGisObject obj,String parentId) throws Exception
	{
		super(null);
		obj.setInstance(this);
		this.parentId = parentId;
	}

	protected EditableCurve(String geotype, String curveId, IAttrsPool attrsPool, INameConverter storNm2CodeNm, String parentId)
	{
		super(geotype, curveId, attrsPool, storNm2CodeNm);
		this.parentId = parentId;
	}

	protected EditableCurve(INameConverter storNm2CodeNm)
	{
		super(storNm2CodeNm);
	}

	public void setCurveId(String m_sCurveID)
	{
		this.m_sCurveID = m_sCurveID;
	}


	/**
	 * Установить аттрибут  (Устанавливает ссылку на переданный атрибут)
	 * @param value - значение аттрибута
	 * @throws Exception -
	 */
	public void setCurveAttr(IDefAttr value) throws Exception
	{
		IAttrs attrs = getIObjAttrs();
		if (attrs != null)
		{
			attrs.put(value.getName(),value);
			isChangeAttrs=true;
		}
	}

	/**
	 * Добавить аттрибуты из переданного объект (Устанавливает ссылки на переданные атрибуты)
	 * @param curve- переданный объект
	 * @throws Exception -
	 */
	public void addCurveAttrs(IBaseGisObject curve)
			throws Exception
	{
		IAttrs attrs = getIObjAttrs();
		if (attrs != null)
		{
			attrs.putAll(curve.getObjAttrs());
			isChangeAttrs=true;
		}
	}

	/**
	 * Установить аттрибуты (Устанавливает ссылки на переданные атрибуты)
	 * @param sattrs - аттрибуты объекта (Имя атрибута -> контейнер хранящий его значение)
	 * @throws Exception -
	 */
	public void setCurveAttrs(Map<String,IDefAttr> sattrs)
			throws Exception
	{
		IAttrs attrs = getIObjAttrs();
		if (sattrs != null)
		{
			attrs.clear();
			for (String attrname : sattrs.keySet())
				attrs.put(attrname, sattrs.get(attrname));
			isChangeAttrs=true;
		}
	}

	public void setCurveIdPrefix(String prefix)
	{
		m_sCurveID=new StringBuffer(prefix).append(getCurveId()).toString();
	}


	protected Pair<Integer,Integer> splitIndex4Add(int index)
	{
		for (int i = 0; i < pArrX.length; i++)
		{
			if (index-pArrX[i].length<0)
				return new Pair<Integer,Integer>(i,index);
			else
				index-=pArrX[i].length;
		}

		int first;
		if (index==0 && (first=pArrX.length - 1)>=0)
			return new Pair<Integer,Integer>(first,pArrX[first].length);

		throw new ArrayIndexOutOfBoundsException();
	}

}
