package ru.ts.gisutils.algs.common;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * @author Vladm
 * Собрание некоторых общих геометрических алгоритмов
 */
public class General
{

	/**
	 * Упростить линию
	 * @param tol -
	 * @param j -
	 * @param k -
	 * @param mk -
	 * @param points -
	 */
	public static void simplifyDP(double tol, int j, int k, int[] mk,Point2D[] points)
	{

		if (k <= (j + 1))
			return;

		int maxi = j;
		double maxd2 = 0.0;

		Line2D.Double l = new Line2D.Double(points[j],points[k]);

		double dv2 = 0.0;

		for (int i = j + 1; i < k; i++) {
			dv2 = l.ptLineDist(points[i]);
			if (dv2 > maxd2) {
				maxi = i;
				maxd2 = dv2;
			}
		}

		if (maxd2 > tol) // error is worse than the tolerance
		{

			mk[maxi] = 1;
			simplifyDP(tol, j, maxi, mk,points);
			simplifyDP(tol, maxi, k, mk,points);
		}
	}

}
