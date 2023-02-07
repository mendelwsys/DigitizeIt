package ru.ts.toykernel.geom.def;

import ru.ts.gisutils.geometry.IGisVolume;
import ru.ts.gisutils.geometry.GisVolume;
import ru.ts.gisutils.geometry.ICoordinate;
import ru.ts.gisutils.proj.transform.ITransformer;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.GeomAlgs;
import ru.ts.gisutils.algs.common.MPointZM;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.geom.IGisObject;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;

/**
 * Имплементация гео объекта модифицированная
 */
public class ROCurveM 	implements IGisObject
{

	protected IAttrsPool attrsPool;
	protected INameConverter storNm2CodeNm;

	protected IGisVolume gisvalume = new GisVolume();

	protected MPoint[][] pArr;//Множекство точек  объекта
	protected String m_sCurveID = "";
	protected String geotype = "";

	/**
	 * Для внутреннего пользования
	 *
	 * @return - ссылки на атрибуты объекта
	 */
	protected IAttrs getIObjAttrs()
	{
		return attrsPool.get(m_sCurveID);
	}

	/**
	 * Вернуть аттрибуты объекта (Запрещены для редактирования)
	 *
	 * @return - аттрибуты объекта
	 */
	public IAttrs getObjAttrs()
	{
		if (attrsPool==null)
			return null;
		IAttrs attrs = attrsPool.get(m_sCurveID);
		if (attrs != null)
			return new DefaultAttrsImpl(attrs, true);
		else
			return attrs;
	}

	public void setInstance(IBaseGisObject _curve) throws Exception
	{

		if (_curve instanceof ROCurveM)
		{
			ROCurveM curve = (ROCurveM) _curve;

			curve.attrsPool = attrsPool;
			curve.pArr = pArr;
			curve.m_sCurveID = m_sCurveID;
			curve.geotype = geotype;
			curve.gisvalume = gisvalume;
			curve.storNm2CodeNm = storNm2CodeNm;
		}
		else
			throw new UnsupportedOperationException("Can't set instance for object of type:" + _curve.getClass().getCanonicalName());
	}


	public INameConverter getStorNm2CodeNm()
	{
		return storNm2CodeNm;
	}

	public void setStorNm2CodeNm(INameConverter storNm2CodeNm)
	{
		this.storNm2CodeNm = storNm2CodeNm;
	}

	protected ROCurveM(INameConverter storNm2CodeNm)
	{
		this.storNm2CodeNm = storNm2CodeNm;
	}


	public ROCurveM(MPoint[][] pArr,String geotype, String curveId, IAttrsPool attrsPool, INameConverter storNm2CodeNm)
	{
		this.pArr=pArr;
		this.geotype = geotype;
		this.m_sCurveID = curveId;
		this.attrsPool = attrsPool;
		this.storNm2CodeNm = storNm2CodeNm;
		rebuildGisValume();

	}

	public void transformIt(ITransformer transformer, boolean isdirect) throws Exception
	{
		throw new UnsupportedOperationException();

//TODO		MPoint ICrdMyPoint = new MPoint();
//
//		for (Map<Integer, MPoint> mp : m_Curve)
//			for (MPoint myPoint : mp.values())
//			{
//				if (isdirect)
//					transformer.TransformDirect(myPoint, ICrdMyPoint);
//				else
//					transformer.TransformInverse(myPoint, ICrdMyPoint);
//				myPoint.setByICoordinate(ICrdMyPoint);
//			}
//	    rebuildGisValume();
	}

	public MPoint getMidlePoint()
	{
		MPointZM rv=null;
		double cnt=0;
		for (int i = 0; i < pArr.length; i++)
		{
			for (int j = 0; j < pArr[i].length; j++)
			{
				if (rv==null)
					rv=new MPointZM(pArr[i][j]);
				else
				{
					rv.setX(rv.getX()+pArr[i][j].getX());
					rv.setY(rv.getY()+pArr[i][j].getY());
					rv.setZ(rv.getZ()+pArr[i][j].getZ());
					if (pArr[i][j] instanceof MPointZM)
						rv.setM(rv.getM()+((MPointZM)pArr[i][j]).getM());
				}
				cnt++;
			}
		}

		if (rv!=null)
		{
			rv.setX(rv.getX()/cnt);
			rv.setY(rv.getY()/cnt);
			rv.setZ(rv.getZ()/cnt);
			if (rv instanceof MPointZM)
				rv.setM(rv.getM()/cnt);
		}
		return rv;
	}

	public void rebuildGisValume()
	{
		gisvalume = new GisVolume();
		for (int i = 0; i < pArr.length; i++)
			for (int j = 0; j < pArr[i].length; j++)
				gisvalume.add(pArr[i][j]);
	}

	public MRect getMBB(MRect boundrect)
	{

		if (boundrect == null)
			boundrect = new MRect(new MPoint(gisvalume.getMinX(), gisvalume.getMinY()),
					new MPoint(gisvalume.getMaxX(), gisvalume.getMaxY()));
		else
		{
			if (gisvalume.getMinX() < boundrect.p1.x)
				boundrect.p1.x = gisvalume.getMinX();
			if (gisvalume.getMaxX() > boundrect.p4.x)
				boundrect.p4.x = gisvalume.getMaxX();

			if (gisvalume.getMinY() < boundrect.p1.y)
				boundrect.p1.y = gisvalume.getMinY();
			if (gisvalume.getMaxY() > boundrect.p4.y)
				boundrect.p4.y = gisvalume.getMaxY();
		}
		boundrect.resetInternalPoint();
		return boundrect;
	}

	public String getGeotype()
	{
		return geotype;
	}

	public MPoint getNearestBoundPointByPoint(MPoint pnt)
	{

		double s = -1;
		MPoint rv = null;
		for (int i = 0; i < pArr.length; i++)
		{
			if (geotype.equals(KernelConst.LINEARRING) || geotype.equals(KernelConst.LINESTRING)|| geotype.equals(KernelConst.LINEARRINGH))
			{
				for (int j = 0; j < pArr[i].length - 1; j++)
				{
					MPoint p1 = pArr[i][j].getCopyOfObject();
					MPoint p2 = pArr[i][j + 1].getCopyOfObject();

					MPoint l_nearest = new MPoint();
					double ls = GeomAlgs.getShortestDistanceToSeg(p1, p2, pnt, l_nearest);

					if (s < 0 || s > ls)
					{
						s = ls;
						rv = l_nearest;
					}
				}

				if (geotype.equals(KernelConst.LINEARRING) || geotype.equals(KernelConst.LINEARRINGH))//проверим еще одну сторону
				{

					MPoint p1 = pArr[i][0].getCopyOfObject();
					MPoint p2 = pArr[i][pArr[i].length - 1].getCopyOfObject();

					MPoint l_nearest = new MPoint();
					double ls = GeomAlgs.getShortestDistanceToSeg(p1, p2, pnt, l_nearest);

					if (s < 0 || s > ls)
					{
						s = ls;
						rv = l_nearest;
					}
				}
			}
			else if (geotype.equals(KernelConst.POINT))
			{
				for (int j = 0; j < pArr[i].length; j++)
				{
					MPoint l_nearest = pArr[i][j].getCopyOfObject();
					double dx = l_nearest.x - pnt.x;
					double dy = l_nearest.y - pnt.y;
					double ls = Math.sqrt(dx*dx+dy*dy);
					if (s < 0 || s > ls)
					{
						s = ls;
						rv = l_nearest;
					}
				}
			}
		}
		return rv;
	}

	public int getNearestIndexByPoint(MPoint pnt)
	{
		int index = 0;
		double s = -1;
		int curindex = -1;
		for (int i = 0; i < pArr.length; i++)
		{
			for (int j = 0; j < pArr[i].length; j++)
			{
				double dx = pArr[i][j].getX() - pnt.x;
				double dy = pArr[i][j].getY() - pnt.y;
				double ls = dx * dx + dy * dy;

				if (s < 0 || s > ls)
				{
					s = ls;
					curindex = index + j;
				}
			}
			index += pArr[i].length;
		}
		return curindex;
	}

	public int getIndexByPoint(MPoint pnt)
	{
		int index = 0;
		for (int i = 0; i < pArr.length; i++)
		{
			for (int j = 0; j < pArr[i].length; j++)
				if (pnt.equals(pArr[i][j]))
					return index + j;
			index += pArr[i].length;
		}
		return -1;
	}

	public int getSegsNumbers()
	{
		return pArr.length;
	}

	public int getSegLength(int segindex)
	{
		return pArr[segindex].length;
	}

	/**
	 * Отдать координаты точек сегмента кривой по его номеру
	 *
	 * @param segindex - индекс сегмента
	 * @return пара точек принадлежащие сегменту
	 */
	public MPoint[] getSegmentById(int segindex)
	{
		MPoint[] rv = new MPoint[pArr.length];
		for (int i = 0; i < rv.length; i++)
			rv[i] = pArr[segindex][i].getCopyOfObject();
		return rv;
	}

	public String toString()
	{

		StringBuffer str = new StringBuffer("");
		for (int i = 0; i < pArr.length; i++)
		{
			str.append("segment: ").append(i).append("\n");
			for (int j = 0; j < pArr[i].length; j++)
				str.append("\t\t ").append(pArr[i][j].toString()).append("\n");
		}
		return str.toString();
	}

	public double[][][] getRawGeometry()
	{

		int dim=getDimensions();
		if (dim==0)
			return new double[0][][];

		double[][][] rv=new double[dim][][];

		for (int i=0;i<dim;i++)
		{
			rv[i]=new double[pArr.length][];//Для каждого измерения создаем макссив сегментов
			for (int j = 0; j < pArr.length; j++)
			{
				rv[i][j]=new double[pArr[j].length];//Для каждого сегмента размещаем массив значений
				for (int k = 0; k < pArr[j].length; k++)
					rv[i][j][k]=pArr[j][k].getDimension(i);//Который и заполняем
			}
		}

		return rv;
	}

	public MPoint[][] getGeometry()
	{
		MPoint[][] points = new MPoint[pArr.length][];
		for (int i = 0; i < pArr.length; i++)
		{
			points[i] = new MPoint[pArr[i].length];
			for (int j = 0; j < points[i].length; j++)
						points[i][j] = pArr[i][j].getCopyOfObject();
		}
		return points;
	}


	public int numberOfPoints()
	{
		int sz = 0;
		for (ICoordinate[] iCoordinates : pArr)
			sz += iCoordinates.length;
		return sz;
	}

	public String getCurveId()
	{
		return m_sCurveID;
	}

	public MPoint getPoint(int index)
	{
		Pair<Integer, Integer> prindex = splitIndex(index);
		return pArr[prindex.first][prindex.second].getCopyOfObject();
	}

	public Pair<Integer, Integer> splitIndex(int index)
	{
		for (int i = 0; i < pArr.length; i++)
		{
			if (index - pArr[i].length < 0)
				return new Pair<Integer, Integer>(i, index);
			else
				index -= pArr[i].length;
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	public int mergeIndex(Pair<Integer, Integer> index)
	{
		int rv = 0;
		for (int i = 0; i < pArr.length; i++)
		{
			if (index.first == i && index.second < pArr[i].length)
				return rv + index.second;
			rv += pArr[i].length;
		}
		throw new ArrayIndexOutOfBoundsException();

	}

	/*
	* (non-Javadoc)
	*
	* @see ru.ts.gisutils.datamine.IHasExtent#getExtentVolume()
	*/
	public int getDimensions()
	{
		if (pArr.length>0 && pArr[0].length>0)
			return pArr[0][0].getDimensions();
		return 0;
	}

}