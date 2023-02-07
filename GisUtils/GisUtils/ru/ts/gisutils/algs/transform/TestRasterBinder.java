package ru.ts.gisutils.algs.transform;

import ru.ts.utils.data.Pair;
import ru.ts.gisutils.algs.common.MPoint;

import java.util.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 21.11.2008
 * Time: 16:07:27
 * To change this template use File | Settings | File Templates.
 */
public class TestRasterBinder
{
	public static void _loadBindPoints(List<Pair<MPoint, MPoint>> pBindPoint) throws Exception
	{
		BufferedReader in = null;
		try
		{
			File fl = new File("C:\\MAPDIR\\Rasters\\xy.txt.bin");
					//"C:\\MAPDIR\\Rasters5\\yy.pnts");
					//"C:\\MAPDIR\\Rasters\\xy.txt.bin");
			if (fl != null)
			{
				in = new BufferedReader(new InputStreamReader(new FileInputStream(fl)));
				String bln = null;
				java.util.List<Pair<MPoint, MPoint>> l_bindPoints = new LinkedList<Pair<MPoint, MPoint>>();
				while ((bln = in.readLine()) != null)
				{
					StringTokenizer st = new StringTokenizer(bln, "|");

					MPoint pnt = new MPoint(Double.parseDouble(st.nextToken()),
							Double.parseDouble(st.nextToken()));
					MPoint mypnt = new MPoint(Double.parseDouble(st.nextToken()),
							Double.parseDouble(st.nextToken()));
					l_bindPoints.add(new Pair<MPoint, MPoint>(pnt, mypnt));
				}
				pBindPoint.clear();
				pBindPoint.addAll(l_bindPoints);
			}
		}
		catch (NoSuchElementException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null)
				in.close();
		}
	}


	public static void main(String[] args) throws Exception
	{
	   List<Pair<MPoint, MPoint>> bindPoints=new LinkedList<Pair<MPoint, MPoint>>();

		_loadBindPoints(bindPoints);

		List<Search2DTransformation.ITrainingElem> projtoraster = new LinkedList<Search2DTransformation.ITrainingElem>();
		List<Search2DTransformation.ITrainingElem> rastertoproj = new LinkedList<Search2DTransformation.ITrainingElem>();

		Random random = new Random();
		List<Pair<MPoint, MPoint>> testpnts = getTestPoints(bindPoints, random);

		for (Pair<MPoint, MPoint> bindPoint : bindPoints) //<Растровая точка точка,Точка проекта>
		{
			projtoraster.add(new Search2DTransformation.ITrainingElem.Base(bindPoint.second, bindPoint.first));
			rastertoproj.add(new Search2DTransformation.ITrainingElem.Base(bindPoint.first, bindPoint.second));
		}

		double maxMeanErr1 = 10;
		Search2DTransformation.ITransformer projtorastere = Search2DTransformation.findPolynomialTransformer(
				projtoraster, 1E-24,
				maxMeanErr1);

		double maxMeanErr2 = 100;
		Search2DTransformation.ITransformer rastertoproje = Search2DTransformation.findPolynomialTransformer(
				rastertoproj, 1E-24,
				maxMeanErr2);

//		if (rastertoproje!=null)
//		{
//			for (
//			Pair<MPoint, MPoint> myPointMyPointPair : testpnts)
//			{
//				IXY res = rastertoproje.transform(myPointMyPointPair.first);
//				System.out.println(
//						"res.getX()-myPointMyPointPair.second.getX() = " + (res.getX() - myPointMyPointPair.second.getX()));
//				System.out.println(
//						"res.getY()-myPointMyPointPair.second.getY() = " + (res.getY() - myPointMyPointPair.second.getY()));
//			}
//		}
//
//		if (projtorastere!=null)
//		{
//			for (
//			Pair<MPoint, MPoint> myPointMyPointPair : testpnts)
//			{
//				IXY res = projtorastere.transform(myPointMyPointPair.second);
//				System.out.println(
//						"res.getX()-myPointMyPointPair.second.getX() = " + (res.getX() - myPointMyPointPair.first.getX()));
//				System.out.println(
//						"res.getY()-myPointMyPointPair.second.getY() = " + (res.getY() - myPointMyPointPair.first.getY()));
//			}
//		}

	}

	private static List<Pair<MPoint, MPoint>> getTestPoints(
			List<Pair<MPoint, MPoint>> bindPoints, Random random)
	{
		List<Pair<MPoint, MPoint>> tstpnts = new LinkedList<Pair<MPoint, MPoint>>();
		for (int i = 0; i < Math.ceil(bindPoints.size() / 10); i++)
			tstpnts.add(bindPoints.remove(random.nextInt(bindPoints.size())));
		return tstpnts;
	}

}
