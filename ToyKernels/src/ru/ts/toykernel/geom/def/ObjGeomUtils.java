package ru.ts.toykernel.geom.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.GeomAlgs;

/**
 * Некоторые геометрические утилиты для работы с геометрией
 */
public class ObjGeomUtils
{



	//Выкинуть соседние одинаковые точки
	public double[][] seed(double [] x , double [] y,int curr)
	{
		int currentindex=0;
		while
			(
				x[(curr+currentindex)%x.length]==x[(curr+currentindex+1)%x.length]
					&&
				y[(curr+currentindex)%x.length]==y[(curr+currentindex+1)%x.length]
					&&
				currentindex<x.length
		  	)
		  currentindex++;

		if (currentindex==0)
			return new double[][]{x,y};

		//Выбросим точки от 0 до currentindex
		double[] lpX = new double[x.length - currentindex];
		double[] lpY = new double[y.length - currentindex];

		for (int j=curr+currentindex,k=0;k<lpY.length;j++,k++)
		{
			lpX[k]=x[j%x.length];
			lpY[k]=y[j%x.length];
		}

		for (int j=0;j<lpX.length;j++)
			if (
					lpX[j]==lpX[(j+1)%lpX.length]
						&&
					lpY[j]==lpY[(j+1)%lpY.length]
				)
			{
				return seed(lpX,lpY,j);
			}
		return new double[][]{lpX,lpY};
	}


	/**
	 *
	 * @param x - координаты x полигона
	 * @param y - координаты y полигона
	 * @return true если справа по обходу вдоль границы false слева
	 * @throws Exception -
	 */
	public boolean isRigthInternal(double [] x , double [] y) throws Exception
	{
		int[] sign_res = cntIntersects(x, y);
		if (sign_res[0]>0)
			return sign_res[1]%2!=0; //Нечетное число пересечений справа внутренняя область
		else
			return Math.abs(sign_res[1]) % 2 == 0; //Четное число пересечений, слева внешняя область, а справа внутренняя
	}

	/**
	 *
	 * @param x - координаты x полигона
	 * @param y - координаты y полигона
	 * @return массив 1/-1 луч пущен направо/налево по ходу вдоль границы, кол-во пересечений луча пущенного из середины
	 * первого ребра полигона в +бескончность по X или в -бескончность по Y (зависит от наклона первого ребра)
	 * @throws Exception - полигон не удовлетворяет отсутсвию самопересечений или кол-ву ребер
	 */
	public int[] cntIntersects(double [] x , double [] y) throws Exception
	{

		double [][] rv=seed(x,y,0);

		x=rv[0];
		y=rv[1];

		if (x.length==0 || x.length==1)
			throw new Exception("Dirty Polygon with length:"+x.length+" of different points");

		MPoint pnt = new MPoint();
		boolean isX=isRayX(pnt,x,y,0);

		if (isX)
		{
			int cnt = cntIntersects(x, y, 1, pnt);
			int sign = (int)((y[1] - y[0]) / Math.abs(y[1] - y[0]));

			return new int[]{sign,cnt};
		}
		else
		{
			double[] y1=new double[y.length];
			for (int i = 0; i < y.length; i++)
				y1[i]=-y[i];
			int cnt = cntIntersects(y1, x, 1, new MPoint(-pnt.y, pnt.x));

			int sign = (int)((x[1] - x[0]) / Math.abs(x[1] - x[0]));

			return new int[]{sign,cnt};
		}
	}


	//Подсчет пересечений сторон многоугольника начиная с индекса currentindex, в сторону + бесконечность
	//луча пущенного из точки pnt
	public int cntIntersects(double [] pX , double [] pY,int currentindex,MPoint pnt) throws Exception
	{

		int cntIntersects = 0;
		for (int j = currentindex; j < pX.length; j++)
		{
			MPoint p1s = new MPoint(pX[j], pY[j]); //Точка 1 стороны
			MPoint p2s; //Точка 2 строны
			int indexp2s;
			if (j == pX.length - 1)
			{
				p2s = new MPoint(pX[0], pY[0]);
				indexp2s = 0;
			}
			else
			{
				p2s = new MPoint(pX[j + 1], pY[j + 1]);
				indexp2s = j + 1;
			}

			if (p1s.y == pnt.y && p1s.y != p2s.y) //пропустим точку только в том случае если сторона не горизонтальна
			{
				//System.out.println("ObjGeomUtils:Skip first point");
			}
			else if (p2s.y == pnt.y) //Особый случай, вершина лежит на луче или сторона может быть горизонтальная
			{
				if (p1s.y == p2s.y && pnt.x <= Math.max(p1s.x, p2s.x))
				{//Сторона горизонтальна
				//Убрать текущую точку и расчитать для полученного прямоугольника
					if (Math.min(p1s.x, p2s.x) <= pnt.x)
						throw new Exception("Dirty Polygon point lays on side: "+p1s+" : "+p2s);
					else
					{
					double[] lpX = new double[pX.length - 1];
					double[] lpY = new double[pY.length - 1];
					for (int i = 0, k = 0; i < lpY.length; i++, k++)
					{
						if (i == j)
							k++;
						lpX[i] = pX[k];
						lpY[i] = pY[k];
					}
					return cntIntersects(lpX,lpY,1,pnt);
					}
				}
				else if (pnt.x <= p2s.x) //(Условие p2s.y == pnt.y выполнено)   (Если pnt.x > p2s.x, пересечения нет, рассматриваемая точка лежит за точкой p2s по оси X )
				{//Проверка того что следующая по обходу точка после p2s и точка p1s дежат по разные стороны луча
				  //если это так мы можем засчитать пересечение (т.е. мы не касаемся угла а именно пересекаем его)
					if (pnt.x == p2s.x)
						throw new Exception("Dirty Polygon point equals: "+p2s);

					MPoint p_prev;
					MPoint p_next;

					p_prev = p1s;
					int k = indexp2s + 1;
					if (k >= pX.length)
						k = 0;
					p_next = new MPoint(pX[k], pY[k]);

					if (GeomAlgs.mult(1, 0, pnt, p_next) *
							GeomAlgs.mult(1, 0, pnt, p_prev) < 0) //Если у нас равентсво тогда
						// какая-то из вершин многоугольника лежат на одной прямой сл. мы рассмотрим этот варинт рекурсивным вызовом на сл. итерации
						cntIntersects++;
				}
			}
			else if (isRayXIntersect(pnt, new MPoint[]{p1s, p2s})) //Если
				// луч не пересекает отрезок, тогда проверяем на пересечение
				cntIntersects++;
		}
		return cntIntersects;
	}
	/**
	 * Заполнить серединную точку, и вернуть (горизонтальный или вертикальный луч надо пускать для проверки внутренности области)
	 * @param pnt - серединая точка переданного отреха
	 * @return - true - если  луч горизонтальный, false если вертикальный
	 */
	public boolean isRayX(MPoint pnt, double[] x,double[] y,int currentindex)
	{
		int ix0=currentindex;
		int ix1=(currentindex+1)%x.length;

		pnt.x =(x[ix0]+x[ix1])/2;
		pnt.y =(y[ix0]+y[ix1])/2;
		return Math.abs(x[ix0]-x[ix1])<=Math.abs(y[ix0]-y[ix1]);
	}


	/**
	 * проверить находится ли точка в полигоне
	 * @param baseGisObject
	 * @param pnt
	 * @return
	 */
	public boolean isInPolyPolyGon(IBaseGisObject baseGisObject, MPoint pnt)
	{
		double[][][] rawg = baseGisObject.getRawGeometry();

		return isInPolyPolyGon(pnt, rawg);
	}

	public boolean isInPolyPolyGon(MPoint pnt, double[][][] rawg)
	{
		double pXs[][] = rawg[0];
		double pYs[][] = rawg[1];
		for (int i = 0; i < pXs.length; i++)
		{
			double[] pX = pXs[i];
			double[] pY = pYs[i];
			if (isInPolyGon(pnt, pX, pY))
			{
//				System.out.println("In polygon");
				return true;
			}

		}

//		System.out.println("Out polygon");
		return false;
	}

	public double[][] getPxPy(MPoint[] pnts)
	{
		double[] pX=new double[pnts.length];
		double[] pY=new double[pnts.length];
		for (int i = 0; i < pnts.length; i++)
		{
			pX[i]=pnts[i].x;
			pY[i]=pnts[i].y;
		}
		return new double[][]{pX,pY};
	}

	public boolean isInPolyGon(MPoint pnt, MPoint[] pnts)
	{
		double[][] pXpY=getPxPy(pnts);
		return isInPolyGon(pnt,pXpY[0],pXpY[1]);
	}

	public boolean isRigthInternal(MPoint[] pnts) throws Exception
	{
		double[][] pXpY=getPxPy(pnts);
		return isRigthInternal(pXpY[0],pXpY[1]);
	}


/*
	Алгоритм - кол-во пересеченных сторон (если четное тогда вне области, если не четное тогда внутри)

	Если лежит на границе считаем что внутри многоугольника.

	Для каждой стороны

	1. Проверим совпадают ли Y pnt.y, если нет перейти к шагу 2
		если да взять X данной точки стороны и проверить является ли он больше или равным pnt.x
		если нет то перейти к шагу 2. В противном случае взять векторное произведение двух концевых точек для этих
		сторон, если оно <0 тогда увеличить счетчик на единицу.
		пропустить анлиз следующей стороны                  пересека

	2. определяем максимальный X, после чего проверим пересекаются ли отрезки
	сторона и pnt,new Mpoint(pnt.x+0.1*pnt.x,pnt.y).Если да тогда увеличиваем счетчик пересечений на единицу
 */
	public boolean isInPolyGon(MPoint pnt, double[] pX, double[] pY)
	{

		if (pX == null || pX.length == 0)
			return false;
		if (pX.length == 1)
			return pnt.equals(new MPoint(pX[0], pY[0]));
		if (pX.length == 2)
		{
			return
					GeomAlgs.isonline(pnt,new MPoint(pX[0], pY[0]),new MPoint(pX[1], pY[1])) &&
					Math.min(pX[0], pX[1]) <= pnt.x && pnt.x <= Math.max(pX[0], pX[1]) ;
			//Точка лежит на прямой и между двумя концевыми точками отрезка
		}

//		Double maxX= null;
//		for (double aPX : pX)
//		{
//			if (maxX == null)
//				maxX = aPX;
//			else
//				if (maxX < aPX)
//					maxX = aPX;
//		}

		int cntIntersects = 0;
		for (int j = 0; j < pX.length; j++)
		{
			MPoint p1s = new MPoint(pX[j], pY[j]);
			MPoint p2s;
			int indexp2s;
			if (j == pX.length - 1)
			{
				p2s = new MPoint(pX[0], pY[0]);
				indexp2s = 0;
			}
			else
			{
				p2s = new MPoint(pX[j + 1], pY[j + 1]);
				indexp2s = j + 1;
			}

			if (p1s.y == pnt.y && p1s.y != p2s.y) //пропустим точку только в том случае если сторона не горизонтальна
			{
				System.out.println("ObjGeomUtils:Skip first point");
			}
			else if (p2s.y == pnt.y) //Особый случай, точка МОЖЕТ лежать на луче
			{
				if (p1s.y == p2s.y && pnt.x <= Math.max(p1s.x, p2s.x))
				{//Сторона горизонтальна
					if (Math.min(p1s.x, p2s.x) <= pnt.x)
						return true;//Точка лежит на строне а значит внутри прямоугольника
					else
					{
						//Убрать текущую точку и проверить для полученного прямоугольника
						double[] lpX = new double[pX.length - 1];
						double[] lpY = new double[pY.length - 1];
						for (int i = 0, k = 0; i < lpY.length; i++, k++)
						{
							if (i == j)
								k++;
							lpX[i] = pX[k];
							lpY[i] = pY[k];
						}
						return isInPolyGon(pnt, lpX, lpY);
					}
				}
				else if (pnt.x <= p2s.x) //(Условие p2s.y == pnt.y выполнено)
				{//Проверка того что следующая по обходу точка после p2s и точка p1s дежат по разные стороны луча
				  //если это так мы можем засчитать пересечение (т.е. мы не касаемся угла а именно пересекаем его)
					if (pnt.x == p2s.x)
						return true;

					MPoint p_prev;
					MPoint p_next;

					p_prev = p1s;
					int k = indexp2s + 1;
					if (k >= pX.length)
						k = 0;
					p_next = new MPoint(pX[k], pY[k]);

					if (GeomAlgs.mult(1, 0, pnt, p_next) *
							GeomAlgs.mult(1, 0, pnt, p_prev) < 0) //Если у нас рвентсво тогда
						// какая-то из вершин многоугольника лежат на одной прямой сл. мы рассмотрим этот варинт рекурсивным вызовом на сл. итерации
						cntIntersects++;
				}
			}
			else
			if (isRayXIntersect(pnt, new MPoint[]{p1s, p2s})) //			if (isRayXIntersect_DEB(pnt, new MPoint[]{p1s, p2s},maxX))
			{
				if (GeomAlgs.isonline(pnt,p1s,p2s)) //Проверим лежит ли точка на стороне
						return true; //Если да тогда вернем true
				cntIntersects++;
			}
		}
		return cntIntersects % 2 != 0; //Если кол-во пересечений четно тогда вне области
	}

	public boolean isRayXIntersect_DEB(MPoint pnt, MPoint[] seg,double vmax)
	{
		if (pnt.x < vmax)
		{
			return GeomAlgs.isIIntersect_с(pnt, new MPoint(vmax + (vmax-pnt.x), pnt.y), seg[0], seg[1]);
		}
		else
			return (pnt.x == seg[0].x && pnt.x == seg[1].x && Math.min(seg[0].y, seg[1].y) <= pnt.y && pnt.y <= Math.max(seg[0].y, seg[1].y));
	}

	/**
	 * пересекаются ли луч выпущенный из точки pnt в сторону плюс бесконечность вдоль оси Х c отрезком seg
	 * @param pnt - точка из которой выпускается луч
	 * @param seg -  сегмент
	 * @return true если пересекается false в противном случае
	 */
	public boolean isRayXIntersect(MPoint pnt, MPoint[] seg)
	{
		double vmax = Math.max(seg[0].x, seg[1].x);
		if (pnt.x < vmax)
		{
//			boolean iIntersect_с =GeomAlgs.isIIntersect_с(pnt, new MPoint(vmax + (vmax-pnt.x), pnt.y), seg[0], seg[1]); 
//			if (iIntersect_с)
//			{
//				MPoint pnt1=new MPoint((int)pnt.x,(int)pnt.y);
//				MPoint sseg0 = new MPoint((int)seg[0].x,(int)seg[0].y);
//				MPoint sseg1 = new MPoint((int)seg[1].x,(int)seg[1].y);
//
//				boolean a=GeomAlgs.isIIntersect_с(pnt1, new MPoint((int)(vmax + (vmax-pnt.x)),pnt1.y), sseg0, sseg1);
//				if (a!=iIntersect_с)
//					System.out.println("a = " + a);
//			}
			return GeomAlgs.isIIntersect_с(pnt, new MPoint(vmax + (vmax-pnt.x), pnt.y), seg[0], seg[1]);
		}
		else
		{
//			boolean rv = ;
//			if (rv)
//				System.out.println("rv = " + rv);
			return (pnt.x == seg[0].x && pnt.x == seg[1].x && Math.min(seg[0].y, seg[1].y) <= pnt.y && pnt.y <= Math.max(seg[0].y, seg[1].y));
		}
	}

	/**
	 * Проверить пересекаются ли стороны объекта и прямоугольника
	 *
	 * @param baseGisObject - объект
	 * @param proj_rect	 - прямоугольник
	 * @return true если пересекаются
	 */
	public boolean ObjIntersects(IBaseGisObject baseGisObject, MRect proj_rect)
	{


		double[][][] rawg = baseGisObject.getRawGeometry();

		double pXs[][] = rawg[0];
		double pYs[][] = rawg[1];
		String geotype = baseGisObject.getGeotype();

		if (geotype.equals(KernelConst.LINESTRING) || geotype.equals(KernelConst.LINEARRING) || geotype.equals(KernelConst.LINEARRINGH))
		{

			for (int i = 0; i < pXs.length; i++)
				if (proj_rect.isIIntersect(pXs[i], pYs[i]))
					return true;

			if (geotype.equals(KernelConst.LINEARRING) || geotype.equals(KernelConst.LINEARRINGH))
				for (int i = 0; i < pXs.length; i++)
					if (isInPolyGon(proj_rect.p1,pXs[i],pYs[i]))
						return true;
		}
		else
		{
			for (int i = 0; i < pXs.length; i++)
				if (proj_rect.isInRect(pXs[i][0], pYs[i][0]))
					return true;
		}
		return false;
	}


	public static void checkWalkByBoard(String[] args) throws Exception
	{
//		double[] pX=new double[]{4,1,1,2,4,5,7,10,8,10,13,9,7};
//		double[] pY=new double[]{7,7,4,1,3,1,1, 1,3, 5, 7,7,5};
//		System.out.println("pX.length = " + pX.length);
//		System.out.println("pY.length = " + pY.length);

//		double[] pX=new double[]{1,1,6,4}; //true expected
//		double[] pY=new double[]{1,5,3,3};

//		double[] pX=new double[]{6,1,1,4}; //false expected
//		double[] pY=new double[]{3,5,1,3};


//		double[] pX=new double[]{5,1,3,3};  //false expected
//		double[] pY=new double[]{7,7,4,1};


//		double[] pX=new double[]{1,5,3,3};  //true expected
//		double[] pY=new double[]{7,7,1,4};


		double[] pX=new double[]{5,3,3,1};  //true expected
		double[] pY=new double[]{7,1,4,7};

	   boolean res=new ObjGeomUtils().isRigthInternal(pX,pY);

		System.out.println("res = " + res);
//		int[] cnt=cntIntersects(pX,pY);
//		System.out.println("cnt = " + cnt[0]+" "+cnt[1]);

	}

	public static void main(String[] args)
	{
		double[] pX= new double[]{4,1,3,4,9,10,7,0};
		double[] pY= new double[]{2,1,1,1,3, 4,6,7};
		boolean b = false;
		for (int i = 0; i < pX.length; i++)
		{
			b|=new ObjGeomUtils().isInPolyGon(new MPoint(pX[i],pY[i]),
				new double[]{2,4,5,7,10,8,10,13,9,7,4,1,1},
				new double[]{1,3,1,1, 1,3, 5, 7,7,5,7,7,4});//false

		}
		System.out.println("expect false b = " + b);

		pX= new double[]{1,5,6,8,9,2,2,11};
		pY= new double[]{7,4,2,2,6,2,6,6.5};
		b = true;
		for (int i = 0; i < pX.length; i++)
		{
			b&=new ObjGeomUtils().isInPolyGon(new MPoint(pX[i],pY[i]),
				new double[]{2,4,5,7,10,8,10,13,9,7,4,1,1},
				new double[]{1,3,1,1, 1,3, 5, 7,7,5,7,7,4});//true

		}
		System.out.println("expect true b = " + b);

//		boolean b = new ObjGeomUtils().isInPolyGon(new MPoint(0, 1), new double[]{1,1, 2, 3}, new double[]{2,1, 1,1});//false
//		System.out.println("b = " + b);

//		boolean b=new ObjGeomUtils().isInPolyGon(new MPoint(1.5, 1), new double[]{3, 2, 2}, new double[]{1, 2, 0});//false
//		System.out.println("b = " + b);
//		b=new ObjGeomUtils().isInPolyGon(new MPoint(1.5, 1), new double[]{1, 2, 2}, new double[]{1, 2, 0});//true
//		System.out.println("b = " + b);
//		b=new ObjGeomUtils().isInPolyGon(new MPoint(0, 2), new double[]{1, 1, 2}, new double[]{1, 2, 1});//false
//		System.out.println("b = " + b);
	}


}
