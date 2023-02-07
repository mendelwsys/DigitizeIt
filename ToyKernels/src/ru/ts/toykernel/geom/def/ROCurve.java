package ru.ts.toykernel.geom.def;

import ru.ts.gisutils.geometry.IGisVolume;
import ru.ts.gisutils.geometry.GisVolume;
import ru.ts.gisutils.proj.transform.ITransformer;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.GeomAlgs;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.geom.IGisObject;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;

/**
 * Имплементация гео объекта
 */
public class ROCurve implements IGisObject
{

	protected IAttrsPool attrsPool;
	protected INameConverter storNm2CodeNm;

	protected IGisVolume gisvalume = new GisVolume();

	protected double[][] pArrX = new double[0][0];//множество точек по X
	protected double[][] pArrY = new double[0][0];//множество точек по Y

	protected String m_sCurveID = "";//TODO Уникальный идентификатор линии, должен преписываться исключительно только в конструкторе
	protected String geotype = "";//TODO Вообще говоря вторым параметром должен быть объект имплементирующий определенный интерфейс

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
		IAttrs attrs = attrsPool.get(m_sCurveID);
		if (attrs != null)
			return new DefaultAttrsImpl(attrs, true);
		else
			return attrs;
	}

	public void setInstance(IBaseGisObject _curve) throws Exception
	{

		if (_curve instanceof ROCurve)
		{
			ROCurve curve = (ROCurve) _curve;

			curve.attrsPool = attrsPool;
			curve.pArrX = pArrX;
			curve.pArrY = pArrY;
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

	protected ROCurve(INameConverter storNm2CodeNm)
	{
		this.storNm2CodeNm = storNm2CodeNm;
	}


	protected ROCurve(String geotype, String curveId, IAttrsPool attrsPool, INameConverter storNm2CodeNm)
	{
		this.geotype = geotype;
		this.m_sCurveID = curveId;
		this.attrsPool = attrsPool;
		this.storNm2CodeNm = storNm2CodeNm;

	}

	/*
			 * (non-Javadoc)
			 *
			 * @see ru.ts.gisutils.datamine.IHasExtent#getExtentVolume()
			 */

//	public IGisVolume getExtentVolume(IGisVolume vol)
//	{
//		// yg, 03.09.2008, wanted to add calculation of size of text linked to the object.
//		// but i don't know the size of font, as result i don't know the size of text.
//		// Vlad said that we will use some magic font size, so let's try to do it
//		if (vol == null)
//			vol = new GisVolume(gisvalume);
//		else
//			vol.set(gisvalume);
//		addTextSize(vol);
//		return vol;
//	}

	//++ ------- Font related ---------------
//	private static Graphics _testGraphics = null;
//
//	private static Graphics getTestGraphics()
//	{
//		if (_testGraphics == null)
//		{
//			BufferedImage image = new BufferedImage(3200, 3200, BufferedImage.TYPE_INT_RGB);
//			_testGraphics = image.getGraphics();
//		}
//		return _testGraphics;
//	}
//
//	private static Font _testFont = null;
//
//	private static Font getTestFont()
//	{
//		if (_testFont == null)
//		{
//			Graphics g = getTestGraphics();
//			_testFont = g.getFont();
//		}
//		return _testFont;
//	}
//
//	private static FontRenderContext _testFontRenderContext = null;
//
//	private static FontRenderContext getTestFontRenderContext()
//	{
//		if (_testFontRenderContext == null)
//		{
//			_testFontRenderContext = new FontRenderContext(null, false, false);
//		}
//		return _testFontRenderContext;
//	}
//


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
		Double mx=null;
		Double my=null;
		double cnt=0;
		for (int i = 0; i < pArrX.length; i++)
		{
			for (int j = 0; j < pArrX[i].length; j++)
			{
				if (mx==null)
					mx=pArrX[i][j];
				else
					mx+= pArrX[i][j];

				if (my==null)
					my= pArrY[i][j];
				else
					my+= pArrY[i][j];
				cnt++;
			}
		}
		if (mx!=null && my!=null)
			return new MPoint(mx/cnt,my/cnt);
		else
			return null;
	}

	public void rebuildGisValume()
	{
		gisvalume = new GisVolume();
		for (int i = 0; i < pArrX.length; i++)
			for (int j = 0; j < pArrX[i].length; j++)
				gisvalume.add(pArrX[i][j], pArrY[i][j]);
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
		for (int i = 0; i < pArrX.length; i++)
		{
			if (geotype.equals(KernelConst.LINEARRING) || geotype.equals(KernelConst.LINESTRING) || geotype.equals(KernelConst.LINEARRINGH))
			{
				for (int j = 0; j < pArrX[i].length - 1; j++)
				{
					MPoint p1 = new MPoint(pArrX[i][j], pArrY[i][j]);
					MPoint p2 = new MPoint(pArrX[i][j + 1], pArrY[i][j + 1]);

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

					MPoint p1 = new MPoint(pArrX[i][0], pArrY[i][0]);
					MPoint p2 = new MPoint(pArrX[i][pArrX[i].length - 1], pArrY[i][pArrX[i].length - 1]);

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
				for (int j = 0; j < pArrX[i].length; j++)
				{
					MPoint l_nearest = new MPoint(pArrX[i][j],pArrY[i][j]);
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
		for (int i = 0; i < pArrX.length; i++)
		{
			for (int j = 0; j < pArrX[i].length; j++)
			{
				double dx = pArrX[i][j] - pnt.x;
				double dy = pArrY[i][j] - pnt.y;
				double ls = dx * dx + dy * dy;

				if (s < 0 || s > ls)
				{
					s = ls;
					curindex = index + j;
				}
			}
			index += pArrX[i].length;
		}
		return curindex;
	}

	public int getIndexByPoint(MPoint pnt)
	{
		int index = 0;
		for (int i = 0; i < pArrX.length; i++)
		{
			for (int j = 0; j < pArrX[i].length; j++)
				if (pArrX[i][j] == pnt.x && pArrY[i][j] == pnt.y)
					return index + j;
			index += pArrX[i].length;
		}
		return -1;
	}

	public int getSegsNumbers()
	{
		return pArrX.length;
	}

	public int getSegLength(int segindex)
	{
		return pArrX[segindex].length;
	}

	/**
	 * Отдать координаты точек сегмента кривой по его номеру
	 *
	 * @param segindex - индекс сегмента
	 * @return пара точек принадлежащие сегменту
	 */
	public MPoint[] getSegmentById(int segindex)
	{
		MPoint[] rv = new MPoint[pArrX[segindex].length];
		for (int i = 0; i < rv.length; i++)
			rv[i]= new MPoint(pArrX[segindex][i],pArrY[segindex][i]);
		return rv;
	}

	public String toString()
	{

		StringBuffer str = new StringBuffer("");
		for (int i = 0; i < pArrX.length; i++)
		{
			str.append("segment: ").append(i).append("\n");
			for (int j = 0; j < pArrX[i].length; j++)
				str.append("\t\t X ").append(pArrX[i][j]).append(" Y ").append(pArrY[i][j]).append("\n");
		}
		return str.toString();
	}

	public double[][][] getRawGeometry()
	{
		return new double[][][]{pArrX, pArrY};
	}

	public MPoint[][] getGeometry()
	{
		MPoint[][] points = new MPoint[pArrX.length][];
		for (int i = 0; i < pArrX.length; i++)
		{
			points[i] = new MPoint[pArrX[i].length];
			for (int j = 0; j < points[i].length; j++)
				points[i][j] = new MPoint(pArrX[i][j], pArrY[i][j]);
		}
		return points;
	}


	public int numberOfPoints()
	{
		int sz = 0;
		for (double[] aPArrX : pArrX)
			sz += aPArrX.length;
		return sz;
	}

	public String getCurveId()
	{
		return m_sCurveID;
	}

	public MPoint getPoint(int index)
	{
		Pair<Integer, Integer> prindex = splitIndex(index);
		return new MPoint(pArrX[prindex.first][prindex.second], pArrY[prindex.first][prindex.second]);
	}

	public Pair<Integer, Integer> splitIndex(int index)
	{
		for (int i = 0; i < pArrX.length; i++)
		{
			if (index - pArrX[i].length < 0)
				return new Pair<Integer, Integer>(i, index);
			else
				index -= pArrX[i].length;
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	public int mergeIndex(Pair<Integer, Integer> index)
	{
		int rv = 0;
		for (int i = 0; i < pArrX.length; i++)
		{
			if (index.first == i && index.second < pArrX[i].length)
				return rv + index.second;
			rv += pArrX[i].length;
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
		return 2;
	}

}
