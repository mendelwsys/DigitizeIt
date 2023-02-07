/*
 * Created on 22.03.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.ts.gisutils.algs.common;

/*
 * Created on 22.03.2007
 * @author Vladm
 * @author Alexeev
 * Changes: yugl, 30.04.2008,
 * исходная функциональность - структуры данных для точек, прямоугольника и т.д.
 * doing refactoring, added commentaries, changed dependencies, reduced functionality
 */

import java.awt.*;

import ru.ts.utils.FunctEmul;


public class GeomAlgs
{

	static class Point3D
	{
		public double x;
		public double y;
		public double z;

		public Point3D()
		{
		}

		public Point3D(double x, double y, double z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Point3D(Point3D p)
		{
			this.x = p.x;
			this.y = p.y;
			this.z = p.z;
		}

		public boolean equals(Object o)
		{
			if (o instanceof GeomAlgs.Point3D)
			{
				GeomAlgs.Point3D sp = (GeomAlgs.Point3D) o;
				//System.out.println("x "+x+" sp.x "+sp.x+" y "+y+" sp.y "+sp.y+" z "+z+" sp.z "+sp.z+" result "+!((x==sp.x) && (y==sp.y) && (z==sp.z)));
				return (x == sp.x) && (y == sp.y) && (z == sp.z);
			}
			else
				return false;
		}

		public String toString()
		{
			return "X " + x + " Y " + y;
		}
	}



	/**
	 * Проверить пересекает ли прямая образованная точками l1 и l2 отрезок с точками seg1 и seg2
	 *
	 * @param l1   -точка 1 прямой
	 * @param l2   - точка 2 прямой
	 * @param seg1 - точка 1 отрезка
	 * @param seg2 - точка 2 отрезка
	 * @return true если пересекает и false если нет
	 */
	static public boolean isIntersectLine_с(MPoint l1, MPoint l2,
											MPoint seg1, MPoint seg2)
	//проверка векторными произведениями 1
	{
		double x1 = l2.x - l1.x;
		double y1 = l2.y - l1.y;

		double check = mult(x1, y1, l1, seg1) *
				mult(x1, y1, l1, seg2);
		if (check >= 0) //TODO -1
			return false;
		return true;
	}

	/**
	 * Пересекаются ли  два отрезка (c исключением)
	 *
	 * @param p1 - точка 1 отрезка 1
	 * @param p2 - точка 2 отрезка 1
	 * @param p3 - точка 1 отрезка 2
	 * @param p4 - точка 2 отрезка 2
	 * @return true если пересекает и false если нет
	 */

	static public boolean isIntersect_с(MPoint p1, MPoint p2,
										MPoint p3, MPoint p4)
	{

		//Проверка ограничивающими прямоугольниками
		{
			//первый прямоугольник
			double X1 = Math.min(p1.x, p2.x);
			double X2 = Math.max(p1.x, p2.x);
			double Y1 = Math.min(p1.y, p2.y);
			double Y2 = Math.max(p1.y, p2.y);
			//второй прямоугольник
			double X3 = Math.min(p3.x, p4.x);
			double X4 = Math.max(p3.x, p4.x);
			double Y3 = Math.min(p3.y, p4.y);
			double Y4 = Math.max(p3.y, p4.y);

			//проверка пересечения
			if (X2 <= X3 || X4 <= X1 || Y2 <= Y3 || Y4 <= Y1) //TODO 1
				return false;
		}

		//проверка векторными произведениями 1
		{
			double x1 = p2.x - p1.x;
			double y1 = p2.y - p1.y;

			if (mult(x1, y1, p1, p3) *
					mult(x1, y1, p1, p4) >= 0) //TODO 2
				return false;
		}

		//проверка векторными произведениями 2
		{
			double x1 = p4.x - p3.x;
			double y1 = p4.y - p3.y;
			if (mult(x1, y1, p3, p1) *
					mult(x1, y1, p3, p2) >= 0)  //TODO 1
				return false;
		}
		return true;
	}


	static public boolean isIIntersect_с(Point.Double p1, Point.Double p2,
										 Point.Double p3, Point.Double p4)
	{
		return isIIntersect_с(new MPoint(p1), new MPoint(p2),
				new MPoint(p3), new MPoint(p4)
		);
	}

	/**
	 * Пересекаются ли  два отрезка (c включением)
	 *
	 * @param p1 - точка 1 отрезка 1
	 * @param p2 - точка 2 отрезка 1
	 * @param p3 - точка 1 отрезка 2
	 * @param p4 - точка 2 отрезка 2
	 * @return true если пересекает и false если нет
	 */
	static public boolean isIIntersect_с(MPoint p1, MPoint p2,
										 MPoint p3, MPoint p4)
	{

		//Проверка ограничивающими прямоугольниками
		{
			//первый прямоугольник
			double X1 = Math.min(p1.x, p2.x);
			double X2 = Math.max(p1.x, p2.x);
			double Y1 = Math.min(p1.y, p2.y);
			double Y2 = Math.max(p1.y, p2.y);
			//второй прямоугольник
			double X3 = Math.min(p3.x, p4.x);
			double X4 = Math.max(p3.x, p4.x);
			double Y3 = Math.min(p3.y, p4.y);
			double Y4 = Math.max(p3.y, p4.y);

			//проверка пересечения
			if (X2 < X3 || X4 < X1 || Y2 < Y3 || Y4 < Y1)
				return false;
		}

		//проверка векторными произведениями 1
		{
			double x1 = p2.x - p1.x;
			double y1 = p2.y - p1.y;

			if (mult(x1, y1, p1, p3) *
					mult(x1, y1, p1, p4) > 0)
				return false;
		}

		//проверка векторными произведениями 2
		{
			double x1 = p4.x - p3.x;
			double y1 = p4.y - p3.y;
			if (mult(x1, y1, p3, p1) *
					mult(x1, y1, p3, p2) > 0)
				return false;
		}
		return true;
	}

	static public boolean isIIntersect_c_Sphere(MPoint p1, MPoint p2,
												MPoint p3, MPoint p4)
	{
//		Проверка ограничивающими прямоугольниками
		{
			//первый прямоугольник
			double X1 = Math.min(p1.x, p2.x);
			double X2 = Math.max(p1.x, p2.x);
			double Y1 = Math.min(p1.y, p2.y);
			double Y2 = Math.max(p1.y, p2.y);
			//второй прямоугольник
			double X3 = Math.min(p3.x, p4.x);
			double X4 = Math.max(p3.x, p4.x);
			double Y3 = Math.min(p3.y, p4.y);
			double Y4 = Math.max(p3.y, p4.y);

			//проверка пересечения
			if (X2 < X3 || X4 < X1 || Y2 < Y3 || Y4 < Y1)
				return false;
		}

		Point3D vectorRes = multSphere(p3.x, p3.y, p1.x, p1.y, p3.x, p3.y, p4.x, p4.y);
		Point3D vectorRes1 = multSphere(p3.x, p3.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);

		if (vectorRes.equals(vectorRes1))
			return false;

		vectorRes = multSphere(p2.x, p2.y, p3.x, p3.y, p2.x, p2.y, p1.x, p1.y);
		vectorRes1 = multSphere(p2.x, p2.y, p4.x, p4.y, p2.x, p2.y, p1.x, p1.y);

		return !vectorRes.equals(vectorRes1);

	}

	/**
	 * Лежит ли точка pnt на ПРЯМОЙ p1p2
	 * @param pnt - точка
	 * @param p1 - точка 1 отрезка
	 * @param p2 - точка 1 отрезка
	 * @return true если лежит
	 */
	public static boolean isonline(MPoint pnt,MPoint p1, MPoint p2)
	{
		   return mult(p1.x-pnt.x,p1.y-pnt.y,p1,p2)==0;
	}

	/**
	 * Вектороное произведение двух векторов
	 * @param p11 - точка 1 вектор 1
	 * @param p12 - точка 2 вектор 1
	 * @param p21 - точка 1 вектор 2
	 * @param p22 - точка 2 вектор 2
	 * @return значение двух векторов
	 */
	public static int mult(MPoint p11, MPoint p12, MPoint p21, MPoint p22)
	{
		return mult(p12.x-p11.x,p12.y-p11.y,p21,p22);
	}

	//Векторное произведение векторов p1p2 и вектора представленного координатами x1,y1
	public static int mult(double x1, double y1, MPoint p1, MPoint p2)
	{
		double x2 = p2.x - p1.x;
		double y2 = p2.y - p1.y;
		return mult(x1, y1, x2, y2);
	}

	public static int mult(double x1, double y1, double x2, double y2)
	{
		if (x1 * y2 > x2 * y1)
			return 1;
		else
			if (x1 * y2 == x2 * y1)
				return 0;
		return -1;
	}

	private static Point3D multSphere(double theta1, double fi1, double theta2, double fi2, double theta3, double fi3,
									  double theta4, double fi4)
	{

		double x1 = (Math.cos(theta2) * Math.cos(fi2) - Math.cos(theta1) * Math.cos(fi1));
		double y1 = (Math.cos(theta2) * Math.sin(fi2) - Math.cos(theta1) * Math.sin(fi1));
		double z1 = (Math.sin(theta2) - Math.sin(theta1));

		double x2 = (Math.cos(theta4) * Math.cos(fi4) - Math.cos(theta3) * Math.cos(fi3));
		double y2 = (Math.cos(theta4) * Math.sin(fi4) - Math.cos(theta3) * Math.sin(fi3));
		double z2 = (Math.sin(theta4) - Math.sin(theta3));

		double ResX = FunctEmul.dsignum(y1 * z2 - z1 * y2);
		double ResY = FunctEmul.dsignum(z1 * x2 - x1 * z2);
		double ResZ = FunctEmul.dsignum(x1 * y2 - y1 * x2);
		//System.out.println("--->"+ResX+" Y "+ResY+" Z "+ResZ);
		return new Point3D(ResX, ResY, ResZ);

	}

	static public void shiftLeft(int cntshift, double[] img1, int width)
	{
		for (int k = 0; k < cntshift; k++)
			for (int i = 0; i < img1.length; i++)
			{
				if (img1[i] != 0)
				{
					if (i > 0 && i % width != 0)
						img1[i - 1] = img1[i];
					img1[i] = 0;
				}
			}
	}

	static public void shiftRigth(int cntshift, double[] img1, int width)
	{
		for (int k = 0; k < cntshift; k++)
			for (int i = img1.length - 1; i >= 0; --i)
			{
				if (img1[i] != 0)
				{
					if (i < img1.length - 1 && (i + 1) % width != 0)
						img1[i + 1] = img1[i];
					img1[i] = 0;
				}
			}
	}

	static public void shiftUp(int cntshift, double[] img1, int height)
	{
		for (int k = 0; k < cntshift; k++)
			for (int i = 0; i < img1.length; i++)
			{
				if (img1[i] != 0)
				{
					if (i - height >= 0)
						img1[i - height] = img1[i];
					img1[i] = 0;
				}
			}
	}

	static public void shiftDown(int cntshift, double[] img1, int height)
	{
		for (int k = 0; k < cntshift; k++)
			for (int i = img1.length - 1; i >= 0; --i)
			{
				if (img1[i] != 0)
				{
					if (i + height < img1.length)
						img1[i + height] = img1[i];
					img1[i] = 0;
				}
			}
	}


	/**
	 * Получить косинус между векторами (можно брать эти вектора из ковариционной матрицы и )
	 *
	 * @param vec1 - вектор 1
	 * @param vec2 - вектор 2
	 * @return косинус между этими векторами
	 */
	static public double getCosByCov(double[] vec1, double[] vec2)
	{
		return (vec1[0] * vec2[0] + vec1[1] * vec2[1])
				/ Math.sqrt(vec1[0] * vec1[0] + vec1[1] * vec1[1]) * Math.sqrt(vec2[0] * vec2[0] + vec2[1] * vec2[1]);

	}

	/**
	 * Получить отношение макс. расстояния между точками по меньшей оси к расстоянию по большей оси
	 *
	 * @param midle  - середина
	 * @param cov	- нормализованая ковариционная матрица
	 * @param matrix - матрица пикселей
	 * @return - отношение макс. расстояния между точками по меньшей оси к расстоянию по большей оси
	 */
	static public double getProlate(MPoint midle, double[][] cov, int[][] matrix)
	{
		double maxX = 0, minX = 0;
		double maxY = 0, minY = 0;
		boolean isstart = true;

		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				if (matrix[i][j] != 0)
				{
					double x = i - midle.x;
					double y = j - midle.y;
					double k = (cov[0][0] * x + cov[0][1] * y);
					double l = (cov[1][0] * x + cov[1][1] * y);

					if (isstart)
					{
						maxX = minX = k;
						maxY = minY = l;
						isstart = false;
					}
					else
					{
						if (maxX < k)
							maxX = k;
						else
							if (minX > k)
								minX = k;

						if (maxY < l)
							maxY = l;
						else
							if (minY > l)
								minY = l;
					}
				}
			}
		}


		double dy = Math.abs(maxY - minY);
		double dx = Math.abs(maxX - minY);

		if (dx <= dy)
		{
			if (dy == 0)
				return Double.NaN;
			return dx / dy;//Отношение малой оси к большой
		}
		else
			return dy / dx;//Отношение малой оси к большой

	}


	static public double convertToRadiansLattitude(double degrees)
	{
		return (degrees * Math.PI / (2 * 90));
	}

	static public double convertToRadiansLongitude(double degrees)
	{
		return (degrees * Math.PI / 180);
	}

	static public double convertToDegreesLattitude(double radians)
	{
		return (radians * (2 * 90) / Math.PI);
	}

	static public double convertToDegreesLongitude(double radians)
	{
		return (radians * 180 / Math.PI);
	}


	public static MPoint mp(MPoint pnt)
	{
		return new MPoint(pnt.y, pnt.x);
	}

	public static Point.Double mp(Point.Double pnt)
	{
		return new Point.Double(pnt.y, pnt.x);
	}

	public static void mp2(Point.Double pnt)
	{
		double tmp = pnt.x;
		pnt.x = pnt.y;
		pnt.y = tmp;
	}

	public static double getShortestDistanceToLine(Point.Double p1, Point.Double p2,
												   Point.Double p3,
												   Point.Double pointRef)
	{
		if (Math.abs(p1.x - p2.x) >= Math.abs(p1.y - p2.y))
			return _getShortestDistanceToLine(p1, p2, p3, pointRef);
		else
		{
			double dist = _getShortestDistanceToLine(mp(p1), mp(p2), mp(p3), pointRef);
			if (pointRef != null)
				mp2(pointRef);
			return dist;
		}
	}

	/**
	 * @param p1-first  point of the line from which distance will be calculated
	 * @param p2-second point of the line from which distance will be calculated
	 * @param p3-point  for which the distance will be found
	 * @param pointRef  - projection of point to line  (for perpendicular line k1*k2=-1 in perpendicular we swap coordinates)
	 * @return distanse from line
	 */
	private static double _getShortestDistanceToLine(Point.Double p1, Point.Double p2,
													 Point.Double p3,
													 Point.Double pointRef)
	{
		double distance;
		double x4, y4;

		double k = (p1.y - p2.y) / (p1.x - p2.x);

		if (!Double.isNaN(k))
		{
			double b = p1.y - k * p1.x;
			x4 = (p3.x + k * p3.y - k * b) / (1 + k * k);
			y4 = k * x4 + b;
			if (pointRef != null)
			{
				pointRef.x = x4;
				pointRef.y = y4;
			}
			distance = Math.sqrt((p3.x - x4) * (p3.x - x4) + (p3.y - y4) * (p3.y - y4));
		}
		else
		{
			if (pointRef != null)
			{
				pointRef.x = p1.x;
				pointRef.y = p3.y;
			}
			distance = Math.abs(p3.x - p2.x);
		}
		return distance;
	}

	/**
	 * Получить точку пересечения отрезков когда ясно что они пересекаются
	 *
	 * @param p11 - точка 1 первого отрезка
	 * @param p12 - точка 2 первого отрезка
	 * @param p21 - точка 1 второго отрезка
	 * @param p22 - точка 2 второго отрезка
	 * @return - точка пересечения отрезков
	 */
	private static Point.Double getIntersectPoint(Point.Double p11, Point.Double p12,
												  Point.Double p21, Point.Double p22)
	{
		Point.Double rpnt = new Point.Double();
		if (Math.abs(p11.x - p12.x) > 0 && Math.abs(p21.x - p22.x) > 0)
		{
			getIntersectPoint(p11, p12, p21, p22, rpnt);
		}
		else
			if (Math.abs(p11.y - p12.y) > 0 && Math.abs(p21.y - p22.y) > 0)
			{ //Поворот системы 180 градусов
				getIntersectPoint(mp(p11), mp(p12), mp(p21), mp(p22), rpnt);
				mp2(rpnt);
			}
			else //Один из коэфициентов 0 второй  бесконечость, либо один вообще не определен
				getShortestDistanceToLine(p11, p12, p21, rpnt);
		return rpnt;
	}

	private static void getIntersectPoint(Point.Double p11, Point.Double p12, Point.Double p21, Point.Double p22,
										  Point.Double rpnt)
	{
		double k1 = (p11.y - p12.y) / (p11.x - p12.x);
		double k2 = (p21.y - p22.y) / (p21.x - p22.x);

		double c1 = p11.y - k1 * p11.x;
		double c2 = p22.y - k2 * p22.x;

		rpnt.x = (c2 - c1) / (k1 - k2);
		if (!Double.isNaN(rpnt.x))
			rpnt.y = rpnt.x * k1 + c1;
		else
		{
			Point.Double p1 = new Point.Double(Math.min(p11.x, p12.x), Math.min(p11.y, p12.y));
			Point.Double p2 = new Point.Double(Math.max(p11.x, p12.x), Math.max(p11.y, p12.y));
			if (new MRect(p1, p2).isInRectI(new MPoint(p21)))
			{
				rpnt.x = p21.x;
				rpnt.y = p21.y;
			}
			else
			{
				rpnt.x = p22.x;
				rpnt.y = p22.y;
			}
		}
	}

	public static double getShortestDistanceWithSeg(Point.Double p11, Point.Double p12,
													Point.Double p21, Point.Double p22,
													Point.Double[] l_nearest)
	{
		double dmin = -1;
		if (isIIntersect_с(
				p11,
				p12,
				p21,
				p22))
		{//Найдем тоску пересечения и вернем ее
			if (l_nearest != null)
			{
				Point.Double rp = getIntersectPoint(p11, p12, p21, p22);
				l_nearest[0] = l_nearest[1] = rp;
			}
			dmin = 0;
		}
		else
		{//Произведем вычисление четырех точек
			Point.Double l_nearest_l = new Point.Double();
			dmin = getShortestDistanceToSeg(p11, p12, p21, l_nearest_l);
			l_nearest[0] = new Point.Double(l_nearest_l.x, l_nearest_l.y);
			l_nearest[1] = p21;

			double dmin2 = getShortestDistanceToSeg(p11, p12, p22, l_nearest_l);
			if (dmin2 < dmin)
			{
				l_nearest[0] = new Point.Double(l_nearest_l.x, l_nearest_l.y);
				l_nearest[1] = p22;
				dmin = dmin2;
			}

			dmin2 = getShortestDistanceToSeg(p21, p22, p11, l_nearest_l);
			if (dmin2 < dmin)
			{
				l_nearest[0] = p11;
				l_nearest[1] = new Point.Double(l_nearest_l.x, l_nearest_l.y);
				dmin = dmin2;
			}

			dmin2 = getShortestDistanceToSeg(p21, p22, p12, l_nearest_l);
			if (dmin2 < dmin)
			{
				l_nearest[0] = p12;
				l_nearest[1] = new Point.Double(l_nearest_l.x, l_nearest_l.y);
				dmin = dmin2;
			}
		}
		return dmin;
	}


	public static double getShortestDistanceToSeg(Point.Double p1, Point.Double p2,
												  Point.Double objpnt,
												  Point.Double l_nearest)
	{
		return getShortestDistanceToSeg(new Point.Double[]{p1, p2}, objpnt,
				l_nearest);
	}

	public static double getShortestDistanceToSeg(Point.Double[] seg, Point.Double objpnt,
												  Point.Double l_nearest)
	{
		if (l_nearest == null)
			l_nearest = new Point.Double();

		double l_distance = getShortestDistanceToLine(seg[0], seg[1], objpnt, l_nearest);


		double minx = Math.min(seg[0].x, seg[1].x);
		double maxx = Math.max(seg[0].x, seg[1].x);

		double miny = Math.min(seg[0].y, seg[1].y);
		double maxy = Math.max(seg[0].y, seg[1].y);

		if (
				!((minx <= l_nearest.x && l_nearest.x <= maxx &&
						miny <= l_nearest.y && l_nearest.y <= maxy)))
		{
			double dx1 = (objpnt.x - seg[0].x);
			double dx2 = (objpnt.x - seg[1].x);

			double dy1 = (objpnt.y - seg[0].y);
			double dy2 = (objpnt.y - seg[1].y);


			double dist1 = dx1 * dx1 + dy1 * dy1;
			double dist2 = dx2 * dx2 + dy2 * dy2;

			Point.Double l_nearest1 = ((dist1 < dist2) ? seg[0] : seg[1]);
			l_nearest.x = l_nearest1.x;
			l_nearest.y = l_nearest1.y;
			l_distance = Math.sqrt(Math.min(dist1, dist2));
		}
		return l_distance;
	}


	public static double getShortestDistanceToSeg(MPoint p1, MPoint p2,
												  MPoint objpnt,
												  MPoint l_nearest)
	{
		return getShortestDistanceToSeg(new MPoint[]{p1, p2}, objpnt,
				l_nearest);
	}

	public static double getShortestDistanceToSeg(MPoint[] seg,
												  MPoint objpnt,
												  MPoint l_nearest)
	{
		Point.Double l_nearestx = null;
		if (l_nearest != null)
			l_nearestx = new Point.Double();
		double res = getShortestDistanceToSeg
				(
						new Point.Double(seg[0].x, seg[0].y),
						new Point.Double(seg[1].x, seg[1].y),
						new Point.Double(objpnt.x, objpnt.y),
						l_nearestx
				);
		if (l_nearestx != null)
		{
			l_nearest.x = l_nearestx.x;
			l_nearest.y = l_nearestx.y;
		}
		return res;
	}

}
