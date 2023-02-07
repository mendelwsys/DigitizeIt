package ru.ts.utils;

import java.io.Serializable;
import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 14.05.2007
 * Time: 13:00:07
 * Some operation by RawImage
 */
public class RawImageOperations
{


	public static int[][] getImgByRaster(Raster l_raster)
	{
		if (l_raster == null)
			return null;

		DataBuffer buffer = l_raster.getDataBuffer();
		int[][] resImmage = new int[l_raster.getWidth()][];
		for (int x = 0; x < l_raster.getWidth(); x++)
		{
			resImmage[x] = new int[l_raster.getHeight()];
			for (int y = 0; y < l_raster.getHeight(); y++)
				resImmage[x][y] = buffer.getElem(x + y * l_raster.getWidth());
		}
		return resImmage;
	}

	/**
	 * Слой изображения содержит признаки слоя, по которым будет происходит разделения
	 * изображения
	 */
	public static class MetaLayer
			implements Serializable
	{
		public String name;
		public double active;
		public double mcorridor;
		public double pcorridor;

		public boolean isIsmarked()
		{
			return ismarked;
		}

		public void setIsmarked(boolean ismarked)
		{
			this.ismarked = ismarked;
		}

		public boolean  ismarked;

		public MetaLayer(String name, double active, double mcorridor, double pcorridor)
		{
			this.name = name;
			this.mcorridor = mcorridor;
			this.active = active;
			this.pcorridor = pcorridor;
		}

		public MetaLayer(MetaLayer layer)
		{
			this.name = layer.name;
			this.mcorridor = layer.mcorridor;
			this.active = layer.active;
			this.pcorridor = layer.pcorridor;
		}

	}

	/**
	 * Кластер, матрица данных сформированная по определенному признаку
	 */
	public static class Cluster implements Serializable
	{
		public int[][] data;
		public Point leftUpCorner;
		public Point rigthDownCorner;

		public Cluster(int[][] rawdata)
		{
			leftUpCorner= new Point(-1,-1);
			rigthDownCorner =new Point(0,0);
			for (int i = 0; i < rawdata.length; i++)
				for (int j = 0; j < rawdata[i].length; j++)
					if (rawdata[i][j]>0)
					{
						if (leftUpCorner.x<0 || leftUpCorner.x>i)
							leftUpCorner.x=i;
						if (leftUpCorner.y<0 || leftUpCorner.y>j)
							leftUpCorner.y=j;
						if (rigthDownCorner.x<i)
							rigthDownCorner.x=i;
						if (rigthDownCorner.y<j)
							rigthDownCorner.y=j;
					}


			if (leftUpCorner.x<0 || leftUpCorner.y<0)
				this.data = new int[0][0];
			else
			{
				this.data = new int[rigthDownCorner.x-leftUpCorner.x+1][];
				for (int i = 0; i < data.length; i++)
					data[i]= new int[rigthDownCorner.y-leftUpCorner.y+1];

				for (int i = leftUpCorner.x; i <= rigthDownCorner.x; i++)
					for (int j = leftUpCorner.y; j <= rigthDownCorner.y; j++)
						this.data[i-leftUpCorner.x][j-leftUpCorner.y]=rawdata[i][j];
			}

		}


		public Cluster(LinkedList<Point> points,int[][] rawdata)
		{
			leftUpCorner= new Point(-1,-1);
			rigthDownCorner =new Point(0,0);
			for (Point point : points)
			{
				if (leftUpCorner.x<0 || leftUpCorner.x>point.x)
					leftUpCorner.x=point.x;
				if (leftUpCorner.y<0 || leftUpCorner.y>point.y)
					leftUpCorner.y=point.y;
				if (rigthDownCorner.x<point.x)
					rigthDownCorner.x=point.x;
				if (rigthDownCorner.y<point.y)
					rigthDownCorner.y=point.y;
			}


			if (leftUpCorner.x<0 || leftUpCorner.y<0)
				this.data = new int[0][0];
			else
			{
				this.data = new int[rigthDownCorner.x-leftUpCorner.x+1][];
				for (int i = 0; i < data.length; i++)
					data[i]= new int[rigthDownCorner.y-leftUpCorner.y+1];

				for (Point point : points)
						this.data[point.x-leftUpCorner.x][point.y-leftUpCorner.y]=rawdata[point.x][point.y];
			}
		}

//
		public Point getMidle()
		{
			return RawImageOperations.getMidleByImage(data);
		}

		public double[][] getCovMatrixImage()
		{
			Point midlexy=RawImageOperations.getMidleByImage(data);
			return RawImageOperations.getNormolizeCovMatrixImage(midlexy,data);
		}

		public HashSet<Point> getPerimeter(Point shift)
		{
			if (shift==null)
				shift= new Point(0,0);
//			int width=data.length;
//			int hight=data[0].length;
			HashSet<Point> retSet=new HashSet<Point>();

			for (int i = 0; i < data.length; i++)
			{
				boolean b = (i != 0 && i != data.length - 1);
				for (int j = 0; j < data[i].length; j+=(b?(data.length-1):1))
//					if (data[i][j]!=0)
					   retSet.add(new Point(i+leftUpCorner.x-shift.x,j+leftUpCorner.y-shift.y));
			}
			return retSet;
		}
	}


	private static boolean isin(double mcorridor,double pcorridor,int active)
	{
		return (mcorridor<=active && active<=pcorridor);
	}

	public static int[][] separateImage(int[][] immage, MetaLayer layer)
	{
		int red=(0xFF0000&((int)layer.active))>>16;
		double mred=(red -((0xFF*layer.mcorridor)/100));
		if (mred<0)
			mred=0;
		double pred=(red +((0xFF*layer.pcorridor)/100));
		if (pred>0xFF)
			pred=0xFF;




		int green=(0xFF00&((int)layer.active))>>8;
		double mgreen=(green -((0xFF*layer.mcorridor)/100));
		if (mgreen<0)
			mgreen=0;
		double pgreen=(green +((0xFF*layer.pcorridor)/100));
		if (pgreen>0xFF)
			pgreen=0xFF;


		int blue=0xFF&((int)layer.active);
		double mblue=(blue -((0xFF*layer.mcorridor)/100));
		if (mblue<0)
			mblue=0;
		double pblue=(blue +((0xFF*layer.pcorridor)/100));
		if (pblue>0xFF)
			pblue=0xFF;

		for (int i = 0; i < immage.length; i++)
		{
			for (int j = 0; j < immage[i].length; j++)
			{
				red=(0xFF0000&((int)immage[i][j]))>>16;
				green=(0xFF00&((int)immage[i][j]))>>8;
				blue=0xFF&((int)immage[i][j]);


				if (isin(mred,pred,red) && isin(mgreen,pgreen,green)&& isin(mblue,pblue,blue))
					immage[i][j]=(0x00FFFFFF);
				else
					immage[i][j]=0;
			}
		}
		return immage;
	}

	static private double getAxisXByY(int y,Point midle,double a11,double a12)
	{
		return a12*(midle.y-y)/a11+midle.x;
	}

	static private double getAxisYByX(int x,Point midle,double a11,double a12)
	{
		return a11*(midle.x-x)/a12+midle.y;
	}


	/**
	 * Получить центр тяжести изображения
	 * @return точка центра тяжести
	 * @param x -
	 * @param y -
	 */
	static public Point getMidleByImage(int[] x,int[] y)
	{
		if (x==null || x.length==0 || y==null || y.length==0 || x.length != y.length)
			return null;

		int SUMMX=0;
		int SUMMY=0;

		for (int i = 0; i < x.length; i++)
				{
					SUMMX+=x[i];
					SUMMY+=y[i];
				}

		return new Point(SUMMX/x.length,SUMMY/y.length);
	}

	/**
	 * Получить центр тяжести изображения
	 * @param matrix - матрица изображения
	 * @return точка центра тяжести черно-белого изображения
	 */
	static public Point getMidleByImage(int [][] matrix)
	{
		if (matrix==null || matrix.length==0 || matrix[0].length==0)
			return null;

		int cnts=0;
		int SUMMX=0;
		int SUMMY=0;

		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				if (matrix[i][j]!=0)
				{
					int x=matrix[i][j];

					cnts++;
					SUMMX+=i;
					SUMMY+=j;
				}

		if (cnts>0)
			return new Point(SUMMX/cnts,SUMMY/cnts);
		else
			return null;
	}

	/**
	 * Получить собственные заначения ковариционной матрицы
	 * @param cov - ковариционная матрица
	 * @return собственные значения
	 */
	static public double[] getEigenValueCovMatrixImage(double[][] cov)
	{
		double b = -cov[0][0] - cov[1][1];
		double c=cov[0][0] * cov[1][1]-cov[0][1] * cov[1][0];
		double D=Math.sqrt(b*b-4*c);
		return new double[]{(D-b)/2,-(D+b)/2};
	}

//	static public double[] getDiaganaledMatrix(double[][] cov)
//	{
//		double a=cov[0][0];
//		double b=cov[0][1];
//
//		double c=cov[1][0];
//		double d=cov[1][1];
//
//
//		return new double[]{a*(d*a-b*c),d*a-b*c};
//	}

	/**
	 * Получить нормализованую ковариционную матрицу, при этом вектора упорядочены сверху-вниз, согласно
	 * собственным занчениям, т.о. верхний вектор соответсвует основному направлению образа
	 * (образ наиболее вытянут относительно первого вектора)
	 * @param midlxy - средняя точка
	 * @param matrix - входная матрица
	 * @return нормализованая матрица (матрица состоящая из собственных хначений начальной ковариционной матрицы)
	 */
	static public double[][] getNormolizeCovMatrixImage(Point midlxy,int [][] matrix)
	{
		double[][] cov=getCovMatrixImage(midlxy,matrix);

		double[] eigenval=RawImageOperations.getEigenValueCovMatrixImage(cov);

		double v = eigenval[0]>eigenval[1]?eigenval[0]:eigenval[1];
		double x1=Math.sqrt(1/(1+cov[1][0]*cov[1][0]/((v -cov[1][1])*(v -cov[1][1]))));
		double y1=cov[1][0]*x1/(v -cov[1][1]);

		double v1 = eigenval[0]>eigenval[1]?eigenval[1]:eigenval[0];
		double x2=Math.sqrt(1/(1+cov[1][0]*cov[1][0]/((v1 -cov[1][1])*(v1 -cov[1][1]))));
		double y2=cov[1][0]*x2/(v1 -cov[1][1]);

		return new double[][] {
				{x1,y1},
				{x2,y2},
		};
	}

	/**
	 * Повернуть изображение согласно главным осям
	 * @param cov - ковариционная матрица
	 * @param matrix - исхдная матрица
	 * @param midle - средняя точка
	 * @return результирующая марица
	 */
//TODO Возможно поворачивать образ перед разделением?, а что это даст?
	static public int[][] turnByMatrix (double[][] cov,int [][] matrix,Point midle)
	{
		int[][] cpymatrix=RawImageOperations.createSameEmptyMatrix(matrix);
		Hashtable<Point,Point> map=new Hashtable<Point,Point>();
		Point minpnt=new Point(0,0);
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				if (matrix[i][j]!=0)
				{
					int x=i-midle.x;
					int y=j-midle.y;
					int k= (int) Math.round  (cov[0][0]*x+cov[0][1]*y);
					int l= (int) Math.round (-cov[0][1]*x+cov[0][0]*y);
//					int l= (int) (cov[1][0]*x+cov[1][1]*y);
					map.put(new Point(i,j),new Point(k,l));

					if (k<minpnt.x)
						minpnt.x=k;
					if (l<minpnt.y)
						minpnt.y=l;
				}
			}
		}


		Enumeration<Point> en = map.keys();
		while (en.hasMoreElements())
		{
			Point srcpnt = en.nextElement();
			Point respnt= map.get(srcpnt);
			cpymatrix[respnt.x-minpnt.x+10][respnt.y-minpnt.y+10]=matrix[srcpnt.x][srcpnt.y];
		}
		return cpymatrix;
	}


	/**
	 * Получить ковариционную матрицу изображения
	 * @param midlxy - пиксельная матрица
	 * @param matrix - центр тяжести изображения
	 * @return ковариционная матрица
	 */
	static public double[][] getCovMatrixImage(Point midlxy,int [][] matrix)
	{

		if (midlxy==null || matrix==null || matrix.length==0 || matrix[0].length==0)
			return new double[2][2];

		int cnts=0;
		double[][] retVal=new double[2][2];

		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				if (matrix[i][j]>0)
				{
					cnts++;

					int  dispx = (i-midlxy.x);
					retVal[0][0]+= dispx*dispx;

					int dispy = (j-midlxy.y);
					retVal[1][1]+= dispy*dispy;

					retVal[1][0]+= dispx*dispy;
					retVal[0][1]+= dispx*dispy;
				}

		for (int i = 0; i < retVal.length; i++)
			for (int j = 0; j < retVal[i].length; j++)
				retVal[i][j]/=cnts;

		return retVal;
	}


	static private void filterImageByCicle(Point p,int rad,int [][] srcmatrix,int [][] matrixtarget)
	{
		for (int k = p.x - rad; k <= p.x + rad; k++)
		{
			int li = Math.abs(p.x - k);

			for (int l = p.y - rad; l <= p.y + rad; l++)
			{
				int lj = Math.abs(p.y - l);
				if (
						!(k < 0 || l < 0 || k >= srcmatrix.length || l >= srcmatrix[0].length)
								&&
								rad * rad >= (li * li + lj * lj)
					)
				{
						matrixtarget[k][l]=//0x00FFFFFF;
								srcmatrix[k][l];
				}
			}
		}
	}

	static private void filterImageByEdgeX (Point pp1,Point pp2,int rad,int [][] srcmatrix,int [][] matrixtarget)
	{
		Point p1=pp1;
		Point p2=pp2;

		if (pp1.x>pp2.x)
		{
			p2=pp1;
			p1=pp2;
		}

		int deltax=p2.x-p1.x;
		double k=(1.0*(p2.y-p1.y))/deltax;

		Point center=new Point();
		for (int i = 0; i <= deltax; i++)
		{
			center.x = i+p1.x;
		    center.y= (int) (k*i+p1.y);
			filterImageByCicle(center,rad,srcmatrix,matrixtarget);
		}
	}

	static private void filterImageByEdgeY (Point pp1,Point pp2,int rad,int [][] srcmatrix,int [][] matrixtarget)
	{
		Point p1=pp1;
		Point p2=pp2;

		if (pp1.y>pp2.y)
		{
			p2=pp1;
			p1=pp2;
		}

		int deltay=p2.y-p1.y;
		double k=(1.0*(p2.x-p1.x))/deltay;

		Point center=new Point();
		for (int i = 0; i <= deltay; i++)
		{
		    center.x= (int) (k*i+p1.x);
			center.y = i+p1.y;

			filterImageByCicle(center,rad,srcmatrix,matrixtarget);
		}
	}


	/**
	 * Формирование изображения из мно-ва слоев методом надожения
	 * @param images - мно-во слоев
	 * @return результирующие изображение
	 */
	static public int[][] imposeImages(LinkedList<int[][]> images)
	{
		if (images!=null && images.size()>0)
		{
			int maxX=0;
			int maxY=0;
			for (int[][] image : images)
			{
				if (image.length>maxX)
					maxX=image.length;
				if (image.length>0 && image[0].length>maxY)
					maxY=image[0].length;
			}
			int[][] resimg=new int[maxX][];
			for (int i = 0; i < resimg.length; i++)
				resimg[i]=new int[maxY];

			for (int[][] image : images)
				for (int i = 0; i < image.length; i++)
					for (int j = 0; j < image[i].length; j++)
						if (image[i][j]>0)
							resimg[i][j] = image[i][j];
		    return resimg;
		}
		return new int[0][];
	}



	/**
	 * Процедура кластеризации на матрице
	 * @param rad - радиус кластеризации
	 * @param matrix - матрица на которой производится кластеризация
	 * @return Список кластеров
	 */
	static public LinkedList<Cluster> createClusters(int rad,int[][] matrix)
	{

		int[][] copymatrix=createcopymatrix(matrix);

		LinkedList<Cluster> retVal= new LinkedList<Cluster>();

		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
			{
				if (matrix[i][j] >0)
				{
					LinkedList<Point> excludepoints=new LinkedList<Point>();
					createClasters(new Point(i,j),rad,matrix,excludepoints);
					retVal.add(new Cluster(excludepoints,copymatrix));
				}
			}

		return retVal;
	}

	/**
	 *
	 * @param p - центр кластеризации
	 * @param rad - радиус кластеризации
	 * @param srcmatrix - матрица пикселей на которой производится кластеризация
	 * @param excludepoints - мно-во точек которые были исключены при кластеризации
	 */
	static private void createClasters(Point p,int rad,int [][] srcmatrix,LinkedList<Point> excludepoints)
	{
		LinkedList<Point> l_exludepoints= new LinkedList<Point>();
		srcmatrix[p.x][p.y]=0;

		for (int k = p.x - rad; k <= p.x + rad; k++)
		{
			int li = Math.abs(p.x - k);

			for (int l = p.y - rad; l <= p.y + rad; l++)
			{
				int lj = Math.abs(p.y - l);
				if (
						!(k < 0 || l < 0 || k >= srcmatrix.length || l >= srcmatrix[0].length)
								&&
								rad * rad >= (li * li + lj * lj)
					)
				{
							if (srcmatrix[k][l]>0)
							{
								l_exludepoints.add(new Point(k,l));
								srcmatrix[k][l]=0;
							}
				}
			}
		}

		for (Point point : l_exludepoints)
			createClasters(point,rad,srcmatrix,excludepoints);
		excludepoints.addAll(l_exludepoints);
		excludepoints.add(p);
	}


	/**
	 * Кластеризация и разрисовка кластров тремя разными цветами, для того что бы отличать их друг от друга
	 * @param rad - радиус кластеризации
	 * @param matrix - матрица над которой производится кластеризация
	 * @return - набор разрисованных кластеров
	 */
	static public LinkedList<RawImageOperations.Cluster> dpaintclusters(int rad,int[][] matrix)
	{
		LinkedList<RawImageOperations.Cluster> retVal = createClusters(rad, matrix);

		int[] color={0xFF0000,0x00FF00,0x0000FF};
		int cntcolor=0;
		for (RawImageOperations.Cluster cluster : retVal)
		{
			int curcolor=color[cntcolor++%color.length];

			for (int i = 0; i < cluster.data.length; i++)
				for (int j = 0; j < cluster.data[i].length; j++)
					if (cluster.data[i][j]>0)
						matrix[i+cluster.leftUpCorner.x][j+cluster.leftUpCorner.y]=curcolor;
		}
		return retVal;
	}


	static public void resetmatrix(int[][] matrix)
	{
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				matrix[i][j]=0;
	}

	static public int[][] createcopymatrix(int[][] matrix)
	{
		int[][] retmatrix= new int[matrix.length][];
		for (int i = 0; i < matrix.length; i++)
			retmatrix[i]= new int[matrix[i].length];

		for (int i = 0; i < matrix.length; i++)
			System.arraycopy(matrix[i], 0, retmatrix[i], 0, matrix[i].length);

		return retmatrix;

	}

	private static int[][] createSameEmptyMatrix(int[][] matrix)
	{
		int[][] retVal=new int[matrix.length][];
		for (int i = 0; i < retVal.length; i++)
			retVal[i] = new int[matrix[i].length];
		return retVal;
	}

	public static int getMaxY(int[][] sample)
	{
		int retVal=0;
		for (int i = 0; i < sample.length; i++)
			if (sample[i].length>retVal)
				retVal=sample[i].length;
		return retVal;
	}

	private static HashMap<Character,Integer> colormap=null;

	public static int getColor(int color,char c)
	{
		if (colormap==null)
		{
			int shifts[]={16,8,0};
			char cnames[]={'R','G','B'};
			colormap= new HashMap<Character,Integer>();
			for (int i = 0; i < shifts.length; i++)
				colormap.put(cnames[i],shifts[i]);
		}

		if (colormap.get(c)!=null)
			return  ((color>>colormap.get(c))&0xFF);
		return -1;
	}




}
