package ru.ts.toykernel.converters;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.factory.DefIFactory;
import ru.ts.factory.IInitAble;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.utils.data.Pair;
import ru.ts.xml.IXMLObjectDesc;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.List;

import org.apache.xerces.utils.Base64;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 16.11.2007
 * Time: 17:59:16
 * ru.ts.toykernel.converters
 */
public class CrdConverterFactory extends DefIFactory<ILinearConverter>
{
	public static final String DEFAULTCONVERTER = "DEFAULT";

	public ILinearConverter createByTypeName(String typeStorage) throws Exception
	{
		ILinearConverter rv;
		if ((rv = super.createByTypeName(typeStorage)) != null)
			return rv;
		return createConverterByType(typeStorage);
	}

	public CrdConverterFactory()
	{
	}

	private ILinearConverter createConverterByType(String convname)
			throws Exception
	{
		if (convname.equalsIgnoreCase(LinearConverterAB.LINEARPROJAB))
			return new LinearConverterAB();
		else
		if (convname.equalsIgnoreCase(LinearConverterRSS.LINEARPROJRSS) || convname.equalsIgnoreCase(DEFAULTCONVERTER))
			return new LinearConverterRSS();
		else if (convname.equalsIgnoreCase(ChainConverter.CHAINCONVERTER))
			return new ChainConverter();
		else if (convname.equalsIgnoreCase(InverseConverter.INVERSECONVERTER))
			return new InverseConverter();
		else if (convname.equalsIgnoreCase(RotateConverter.ROTATECONVERTER))
			return new RotateConverter();
		else if (convname.equalsIgnoreCase(ScaledConverter.SCALECONVERTER))
			return new ScaledConverter();
		else if (convname.equalsIgnoreCase(ShiftConverter.SHIFTCONVERTER))
			return new ShiftConverter();
		else
			throw new Exception(
					"Converter with parameters convname:" + convname + " not found");
	}

	static public class LinearConverterAB
			implements IProjConverter
			, IRotateConverter, IShiftConverter, IScaledConverter
	{

		public static final String LINEARPROJAB = "LINEARPROJAB";

		//Линейная область проекции возможно повернутая с помощью координат x,y (декартова система обычная а не экранная)
		//Область рисования линейная область масштабированная и повернутая на экраннные координаты

		public double[] getRotMatrix()
		{
			return matrix;
		}

		public void setRotMatrix(double[] matrix)
		{
			this.matrix=new double[matrix.length];
			System.arraycopy(matrix, 0, this.matrix , 0, matrix.length);
		}

		private double[] matrix = new double[]{0, 1, -1, 0};
		private double scale = 0;
		private double initscale = 0;

//		//Установить максимально и минимально допустимый масштаб
//		public void setMaxMinscale(double kmax,double kmin)
//		{
//			if (kmax>0)
//				maxscale=kmax*initscale;
//			else
//				maxscale=-1;
//
//			if (kmin>0)
//				minscale=kmin*initscale;
//			else
//				minscale=-1;
//		}

//		//Транзитивные переменные
//		private double maxscale=-1; //px/meter
//		private double minscale=-1; //px/meter

		protected LinearConverterAB()
		{

		}

		public LinearConverterAB(double[] matrix, double scale, MPoint pnt)
		{
			this.matrix = new double[matrix.length];
			System.arraycopy(matrix, 0, this.matrix, 0, matrix.length);
			this.scale = scale;
			this.initscale = scale;
			this.currentP0 = new MPoint(pnt);
		}

		LinearConverterAB(LinearConverterAB copy)
		{
			matrix=new double[copy.matrix.length];
			System.arraycopy(copy.matrix, 0, matrix, 0, copy.matrix.length);
			this.scale = copy.scale;
			this.initscale = copy.initscale;
			currentP0=new MPoint(copy.currentP0);
		}

		/**
		 * Конвертирует в координаты проекта в координаты точек экрана
		 *
		 * @param pnt - точка экрана
		 * @return - координаты проекта
		 */
		public MPoint getPointByDstPoint(Point.Double pnt)
		{
			return getPointByLinearPoint(getLinearPointByDrawPoint(new MPoint(pnt)));
		}


		public Point.Double getDstPointByPointD(MPoint pt)
		{
			return getDrawPointByLinearPointD(getLinearPointByPoint(pt));
		}


		public void recalcScale(MRect proj_rect, int[] szXY) throws Exception
		{
			MRect linearRect = getLinearRectByRect(proj_rect);
			recalcScaleByLinearRect(linearRect, szXY);
			initscale = scale;
		}


		public MPoint getPointByDstPoint(Point pnt)
		{
			return getPointByDstPoint(new Point.Double(pnt.x, pnt.y));
		}

		public MPoint getPointByDstPoint(MPoint pnt
		)
		{
			return getPointByDstPoint(new Point.Double(pnt.x, pnt.y));
		}

		public void setConverterChain(List<ILinearConverter> converterchain)
		{
		}

		public List<ILinearConverter> getConverterChain() throws Exception
		{
			java.util.List<ILinearConverter> ll = new LinkedList<ILinearConverter>();
			ll.add(new RotateConverter(getAsRotateConverter().getRotMatrix()));
			ll.add(new ScaledConverter(new MPoint(1.0 / getAsScaledConverterCtrl().getUnitsOnPixel().getX(), 1.0 / getAsScaledConverterCtrl().getUnitsOnPixel().getY())));
			ll.add(new ShiftConverter(getAsShiftConverter().getBindP0()));
			return ll;
		}

		public ILinearConverter createCopyConverter()
		{
			return new LinearConverterAB(this);
		}

		public String getTypeConverter()
		{
			return LINEARPROJAB;
		}

		public MPoint getUnitsOnPixel()
		{
			return new MPoint(1.0 / scale, 1.0 / scale);
		}

		public boolean isscaleChange(double[] wasscale)
		{
			if (wasscale[0] != scale || wasscale[1] != matrix[0] || wasscale[2] != matrix[1] || wasscale[3] != matrix[2] || wasscale[4] != matrix[3])
			{
				System.arraycopy(matrix, 0, wasscale, 0, matrix.length);
				return true;
			}
			return false;
		}

		public MRect getDstRectByRect(MRect rect)
		{
			Point p1 = getDstPointByPoint(rect.p1);
			Point p4 = getDstPointByPoint(rect.p4);

			return new MRect(new MPoint(Math.min(p1.x, p4.x), Math.min(p1.y, p4.y)),
					new MPoint(Math.max(p1.x, p4.x), Math.max(p1.y, p4.y)));

//			return new MRect(new MPoint(p1.x, p1.y),
//					new MPoint(p4.x, p4.y));
		}

		public MRect getLinearRectByRect(MRect rect)
		{
			MPoint p1 = getLinearPointByPoint(rect.p1);
			MPoint p4 = getLinearPointByPoint(rect.p4);

			return new MRect(new MPoint(Math.min(p1.x, p4.x), Math.min(p1.y, p4.y)),
					new MPoint(Math.max(p1.x, p4.x), Math.max(p1.y, p4.y)));
		}

		public MRect getRectByDstRect(MRect drawrect,
									  MRect wholeRect)
		{


			MPoint pmin = null;
			MPoint pmax = null;

			MPoint[] pnts = {getPointByDstPoint(drawrect.p1), getPointByDstPoint(drawrect.p4
			),
					getPointByDstPoint(new MPoint(drawrect.p1.x, drawrect.p4.y)),
					getPointByDstPoint(new MPoint(drawrect.p4.x, drawrect.p1.y))};

			if (wholeRect != null)
			{
				pmin = new MPoint(wholeRect.p4);
				pmax = new MPoint(wholeRect.p1);
			}
			else
			{
				pmin = new MPoint(pnts[0]);
				pmax = new MPoint(pnts[0]);
			}

			for (MPoint pnt : pnts)
			{
				if (pnt.x < pmin.x)
					pmin.x = pnt.x;
				if (pnt.y < pmin.y)
					pmin.y = pnt.y;
				if (pnt.x > pmax.x)
					pmax.x = pnt.x;
				if (pnt.y > pmax.y)
					pmax.y = pnt.y;
			}

			if (wholeRect != null)
			{
				pmin.x = Math.max(wholeRect.p1.x, pmin.x);
				pmin.y = Math.max(wholeRect.p1.y, pmin.y);

				pmax.x = Math.min(wholeRect.p4.x, pmax.x);
				pmax.y = Math.min(wholeRect.p4.y, pmax.y);
			}

			return new MRect(new MPoint(pmin.x, pmin.y),
					new MPoint(pmax.x, pmax.y));
		}

		public double[] getLinearDxDyByDrawDxDy(double[] dXdY)
		{
			return new double[]{dXdY[0] / scale, dXdY[1] / scale};//Широта, долгота
		}


		public double[] getDrawDxDyByLinearDxDy(double[] dXdY)
		{
			return new double[]{dXdY[0] * scale, dXdY[1] * scale};
		}

		public MPoint getLinearPointByDrawPoint(Point drawPoint)
		{
			return getLinearPointByDrawPoint(new MPoint(drawPoint));
		}


		public MPoint getLinearPointByDrawPoint(MPoint pnt
		)
		{
			return new MPoint
					(
							pnt.x / scale + currentP0.x,
							pnt.y / scale + currentP0.y
					);
		}


		public Point.Double getDrawPointByLinearPointD(MPoint lp)
		{
			double x = (lp.x - currentP0.x) * scale;
			double y = (lp.y - currentP0.y) * scale;
			return new Point.Double(x, y);
		}

		public Point getDrawPointByLinearPoint(MPoint pt)
		{
			Point.Double rv = getDrawPointByLinearPointD(pt);
			return new Point((int) Math.round(rv.x), (int) Math.round(rv.y));
		}

		private MPoint currentP0 = new MPoint(Double.NaN, Double.NaN);

		public MPoint getBindP0()
		{
			return new MPoint(currentP0);
		}

		public MPoint getViewSize()
		{
			return sz;
		}

		protected MPoint sz;

		public MPoint setViewSize(MPoint sz)
		{
			MPoint w_sz = null;
			if (this.sz != null)
				w_sz = new MPoint(this.sz);
			this.sz = sz;

			return w_sz;
		}

		public MPoint setBindP0(MPoint currentP0)
		{
			MPoint rv = this.currentP0;
			this.currentP0 = new MPoint(currentP0);
			return rv;
		}

		public MRect getDrawRectByLinearRect(MRect rect)
		{
			Point p1 = getDrawPointByLinearPoint(rect.p1);
			Point p4 = getDrawPointByLinearPoint(rect.p4);
			return new MRect(new MPoint(Math.min(p1.x, p4.x), Math.min(p1.y, p4.y)),
					new MPoint(Math.max(p1.x, p4.x), Math.max(p1.y, p4.y)));
		}

		public MRect getLinearRectByDrawRect(MRect rect)
		{
			MPoint p1 = getLinearPointByDrawPoint(rect.p1);
			MPoint p4 = getLinearPointByDrawPoint(rect.p4);
			return new MRect(new MPoint(Math.min(p1.x, p4.x), Math.min(p1.y, p4.y)),
					new MPoint(Math.max(p1.x, p4.x), Math.max(p1.y, p4.y)));
		}


		public MPoint getLinearPointByPoint(IGetXY pt)
		{
			double x = pt.getX() * matrix[0] + pt.getY() * matrix[1];
			double y = pt.getX() * matrix[2] + pt.getY() * matrix[3];
			return new MPoint(x, y);

		}

		public MPoint getPointByLinearPoint(IGetXY pt)
		{
			double y;
			double x;
			if (matrix[0] > matrix[1])
			{
				y = (pt.getY() - pt.getX() * matrix[2] / matrix[0]) / (matrix[3] - matrix[1] * matrix[2] / matrix[0]);
				x = (pt.getX() - y * matrix[1]) / matrix[0];
			}
			else
			{
				x = (pt.getY() - pt.getX() * matrix[3] / matrix[1]) / (matrix[2] - matrix[0] * matrix[3] / matrix[1]);
				y = (pt.getX() - x * matrix[0]) / matrix[1];
			}
			return new MPoint(x, y);
		}

		public double[] getDstSzBySz(MRect rect)
		{
			return getDrawDxDyByLinearDxDy(new double[]{rect.getWidth(), rect.getHeight()});
		}


		public Point getDstPointByPoint(MPoint pt)
		{
			Point.Double rp = getDstPointByPointD(pt);
			return new Point((int) Math.round(rp.x), (int) Math.round(rp.y));
		}

		public MPoint increaseMap(double dS)
		{
			double res = scale * dS;
			if (!Double.isNaN(res)
//					&&
//					(maxscale<0 || res<=maxscale)
//					&&
//					(minscale<0 || minscale<=res)
					)
				scale = res;
			return new MPoint(scale, scale);
		}

		public MPoint increaseMap(MPoint dS, boolean evently) throws Exception
		{
			if (dS.x!=dS.y)
				throw new UnsupportedOperationException();
			else
				return increaseMap(dS.x);
		}

		public MPoint decreaseMap(double dS)
		{
			double res = scale / dS;
			if (!Double.isNaN(res)
//					&&
//					(maxscale<0 || res<=maxscale)
//					&&
//					(minscale<0 || minscale<=res)
					)
				scale = res;
			return new MPoint(scale, scale);
		}

//		public void changeScale(MRect wholerect, double scale, double dscale)
//		{
//			//double deltaM = Math.abs(wholerect.p1.y - wholerect.p4.y);
//			double targetscale = (scale + dscale);
//
//
//			if (dscale > 0)
//				decreaseMap(targetscale/scale);
//			else
//				increaseMap(scale/targetscale);
//
//		}


		public void recalcBindPointByDrawDxDy(double[] dXdY)
		{
			double[] dxdy = getLinearDxDyByDrawDxDy(dXdY);
			currentP0.x += dxdy[0];
			currentP0.y += dxdy[1];
		}

		public MPoint getLinearPointByBindPoint(MPoint currentP0)
		{
			return currentP0;
		}

		public void reSetInitScale()
		{
			this.scale = initscale;
		}

		public void setScale(MPoint newscale)
		{
			scale= newscale.x;
		}

		public MPoint getScale()
		{
			return new MPoint(scale,scale);
		}

		public void setScaleBean(IScaleDescBean scalebean) throws Exception
		{
			throw new UnsupportedOperationException();
		}

		public MPoint getBindPointByLinearPoint(MPoint currentP0)
		{
			return currentP0;
		}

		public void recalcScaleByLinearRect(MRect linearRect, int[] szXY) throws Exception
		{
			double scale = (Math.min(szXY[0], szXY[1]) * 1.0) /
					Math.max
							(
									Math.abs(linearRect.getWidth()), Math.abs(linearRect.getHeight())
							);


			if (!Double.isNaN(scale))
			{
				this.scale = scale;
				currentP0.x = linearRect.p1.x;
				currentP0.y = linearRect.p1.y;
			}
			else
				throw new Exception("Can't calculate scale");
		}

		public void translatedata(DataOutputStream dos) throws Exception
		{

			byte[] b = getBase64Converter();
			dos.write(b);
			dos.flush();
		}

		public byte[] getBase64Converter()
				throws Exception
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream ddos = new DataOutputStream(bos);

			saveToStream(ddos);

			ddos.flush();
			ddos.close();
			return Base64.encode(bos.toByteArray());
		}

		public void saveToStream(DataOutputStream dos) throws IOException
		{
			dos.writeDouble(initscale);
			dos.writeDouble(scale);
			for (double v : matrix)
			{
				dos.writeDouble(v);
			}
		}

		public void loadFromStream(DataInputStream dis) throws IOException
		{
			initscale = dis.readDouble();
			scale = dis.readDouble();
			for (int i = 0; i < matrix.length; i++)
				matrix[i] = dis.readDouble();
		}

		public void initByBase64Point(byte[] bbase64) throws IOException
		{
			loadFromStream(new DataInputStream(new ByteArrayInputStream(Base64.decode(bbase64))));
		}

		public void setByConverter(IProjConverter converter) throws Exception
		{
			setRotMatrix(converter.getAsRotateConverter().getRotMatrix());
			setScale(converter.getAsScaledConverter().getScale());
			setBindP0(converter.getAsShiftConverter().getBindP0());
			setViewSize(converter.getAsShiftConverter().getViewSize());
		}

		public IRotateConverter getAsRotateConverter()
		{
			return this;
		}

		public IScaledConverter getAsScaledConverter()
		{
			return this;
		}

		public IShiftConverter getAsShiftConverter()
		{
			return this;
		}

		public IScaledConverterCtrl getAsScaledConverterCtrl()
		{
			return getAsScaledConverter();
		}
	}

	static public class InverseConverter extends BBaseConverter implements IInitAble
	{
		private ILinearConverter converter;
		public static final String INVERSECONVERTER = "INVERSECONVERTER";

		public InverseConverter()
		{
		}

		public InverseConverter(ILinearConverter converter)
		{
			this.converter = converter.createCopyConverter();
		}

		InverseConverter(InverseConverter copy)
		{
			this.convname = copy.convname;
			this.converter = copy.converter.createCopyConverter();

		}

		public ILinearConverter createCopyConverter()
		{
			return new InverseConverter(this);
		}

		public String getTypeConverter()
		{
			return INVERSECONVERTER;
		}

		public MPoint getPointByDstPoint(Point.Double pnt)
		{
			return new MPoint(converter.getDstPointByPointD(new MPoint(pnt)));
		}

		public Point.Double getDstPointByPointD(MPoint pt)
		{
			MPoint dstPoint = converter.getPointByDstPoint(pt);
			return new Point.Double(dstPoint.x, dstPoint.y);
		}

		public void saveToStream(DataOutputStream dos) throws Exception
		{

			dos.writeUTF(converter.getTypeConverter());
			converter.saveToStream(dos);
		}

		public void loadFromStream(DataInputStream dis) throws Exception
		{
			converter = new CrdConverterFactory().createConverterByType(dis.readUTF());
			converter.loadFromStream(dis);
		}

		protected String convname;
		protected IXMLObjectDesc desc;

		public String getObjName()
		{
			return convname;
		}

		public Object[] init(Object... objs) throws Exception
		{
			for (Object obj : objs)
				init(obj);
			return null;
		}

		public Object init(Object obj) throws Exception
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.convname = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else if (attr.getName().equalsIgnoreCase("converter"))
				converter = ((ILinearConverter) attr.getValue());
			return null;
		}

		public IObjectDesc getObjectDescriptor()
		{
			return desc;
		}

	}

	static public class ChainConverter extends BBaseConverter implements IInitAble
	{
		public static final String CHAINCONVERTER = "CHAINCONVERTER";
		protected IXMLObjectDesc desc;

		public ChainConverter()
		{
		}

		public ChainConverter(List<ILinearConverter> converterchain)
		{
			this.converterchain = converterchain;
		}

		ChainConverter(ChainConverter copy)
		{
			convname = copy.convname;
			converterchain = new LinkedList<ILinearConverter>();
			for (ILinearConverter aConverterchain : copy.converterchain)
				converterchain.add(aConverterchain.createCopyConverter());

		}

		public ILinearConverter createCopyConverter()
		{
			return new ChainConverter(this);
		}

		public String getTypeConverter()
		{
			return CHAINCONVERTER;
		}

		public MPoint getPointByDstPoint(Point.Double pnt)
		{
			List<ILinearConverter> cl = getConverterChain();
			MPoint res = new MPoint(pnt);
			ListIterator<ILinearConverter> it = cl.listIterator(cl.size());
			while (it.hasPrevious())
			{
				ILinearConverter iLinearConverter = it.previous();
				res = iLinearConverter.getPointByDstPoint(res);
			}
			return res;
		}

		public Point.Double getDstPointByPointD(MPoint pt)
		{
			List<ILinearConverter> cl = getConverterChain();
			Point.Double res = new Point.Double(pt.x, pt.y);
			for (ILinearConverter iLinearConverter : cl)
				res = iLinearConverter.getDstPointByPointD(new MPoint(res));
			return res;
		}

		public void saveToStream(DataOutputStream dos) throws Exception
		{
			dos.write(converterchain.size());
			for (ILinearConverter aConverterchain : converterchain)
			{
				dos.writeUTF(aConverterchain.getTypeConverter());
				aConverterchain.saveToStream(dos);
			}
		}

		public void loadFromStream(DataInputStream dis) throws Exception
		{
			converterchain = new LinkedList<ILinearConverter>();
			int sz = dis.readInt();
			while (sz > 0)
			{
				String type = dis.readUTF();
				converterchain.add(new CrdConverterFactory().createConverterByType(type));
				sz--;
			}
		}

		protected String convname;

		public String getObjName()
		{
			return convname;
		}

		public Object[] init(Object... objs) throws Exception
		{
			for (Object obj : objs)
				init(obj);
			return null;
		}

		public Object init(Object obj) throws Exception
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.convname = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.CONVERTER_TAGNAME))
				converterchain.add(((ILinearConverter) attr.getValue()));
			return null;
		}

		public IObjectDesc getObjectDescriptor()
		{
			return desc;
		}
	}

	static public class LinearConverterRSS extends BaseConverter
			implements IRProjectConverter, IScaledConverter, IRotateConverter, IShiftConverter, IInitAble
	{
		public static final String LINEARPROJRSS = "LINEARPROJRSS";

		public LinearConverterRSS()
		{
		}

		public LinearConverterRSS(IRotateConverter convrotate, IScaledConverter convscaled, IShiftConverter convshift)
		{
			this.convrotate = convrotate;
			this.convscaled = convscaled;
			this.convshift = convshift;
			converterchain.add(this.convrotate);
			converterchain.add(this.convscaled);
			converterchain.add(this.convshift);
		}

		public LinearConverterRSS(double[] matrix, MPoint scale, MPoint pnt)
		{
			this(new RotateConverter(matrix), new ScaledConverter(scale), new ShiftConverter(pnt));
		}

		LinearConverterRSS(LinearConverterRSS srcConv)
		{
			convname = srcConv.convname;
			for (ILinearConverter converter : srcConv.converterchain)
			{
				ILinearConverter copyConverter = converter.createCopyConverter();
				if (converter == srcConv.convrotate)
					convrotate = (IRotateConverter)copyConverter;
				if (converter == srcConv.convscaled)
					convscaled = (IScaledConverter)copyConverter;
				if (converter == srcConv.convshift)
					convshift = (IShiftConverter) copyConverter;
				converterchain.add(copyConverter);
			}
		}

		public void setByConverter(IProjConverter converter) throws Exception
		{
			convrotate.setRotMatrix(converter.getAsRotateConverter().getRotMatrix());
			convscaled.setScale(converter.getAsScaledConverter().getScale());
			convshift.setBindP0(converter.getAsShiftConverter().getBindP0());
			convshift.setViewSize(converter.getAsShiftConverter().getViewSize());
		}

		public IRotateConverter getAsRotateConverter()
		{
			return this;
		}

		public IScaledConverter getAsScaledConverter()
		{
			return this;
		}

		public IShiftConverter getAsShiftConverter()
		{
			return this;
		}

		protected IRotateConverter convrotate;
		protected IScaledConverter convscaled;
		protected IShiftConverter convshift;
		protected IXMLObjectDesc desc;

		public ILinearConverter createCopyConverter()
		{
			return new LinearConverterRSS(this);
		}

		public String getTypeConverter()
		{
			return LINEARPROJRSS;
		}

		public MPoint getPointByDstPoint(Point.Double pnt)
		{
			List<ILinearConverter> cl = getConverterChain();

			MPoint res = new MPoint(pnt);
			ListIterator<ILinearConverter> it = cl.listIterator(cl.size());
			while (it.hasPrevious())
			{
				ILinearConverter iLinearConverter = it.previous();
				res = iLinearConverter.getPointByDstPoint(res);
			}
			return res;
		}

		public Point.Double getDstPointByPointD(MPoint pt)
		{
			List<ILinearConverter> cl = getConverterChain();
			Point.Double res = new Point.Double(pt.x, pt.y);
			for (ILinearConverter iLinearConverter : cl)
				res = iLinearConverter.getDstPointByPointD(new MPoint(res));
			return res;
		}

		public void saveToStream(DataOutputStream dos) throws Exception
		{
			convscaled.saveToStream(dos);
			convrotate.saveToStream(dos);
		}

		public void loadFromStream(DataInputStream dis) throws Exception
		{
			convscaled.loadFromStream(dis);
			convshift.loadFromStream(dis);

		}

		public MPoint getBindP0()  throws Exception
		{
			return convshift.getBindP0();
		}

		public MPoint setBindP0(MPoint currentP0)  throws Exception
		{
			return convshift.setBindP0(currentP0);
		}

		public MPoint getViewSize()  throws Exception
		{
			return convshift.getViewSize();
		}

		public MPoint setViewSize(MPoint sz) throws Exception
		{
			return convshift.setViewSize(sz);
		}

		public void recalcBindPointByDrawDxDy(double[] dXdY)  throws Exception
		{
			convshift.recalcBindPointByDrawDxDy(dXdY);
		}

		public void recalcScale(MRect proj_rect, int[] szXY) throws Exception
		{
			convscaled.recalcScale(convrotate.getDstRectByRect(proj_rect), szXY);
			MPoint pt = getViewSize();
			if (pt != null)
			{
				Point2D.Double res = this.getDstPointByPointD(new MPoint(proj_rect.getMidlX(), proj_rect.getMidlY()));
				recalcBindPointByDrawDxDy(new double[]{res.x - pt.x / 2, res.y - pt.y / 2});
			}
		}

		private Pair<MPoint,MPoint> getProjMidle(MPoint viewsize) throws Exception
		{

			if (viewsize == null)
				viewsize = new MPoint();
			else
			{
				viewsize.x = viewsize.x / 2;
				viewsize.y = viewsize.y / 2; //Середина в координатах экрана
			}
			return new Pair<MPoint,MPoint> (this.getPointByDstPoint(viewsize),viewsize);
		}

		public MPoint increaseMap(double dS) throws Exception
		{
			MPoint pt = getViewSize();
			Pair<MPoint,MPoint> projp_midl2drawPnt = getProjMidle(pt);
			MPoint rv = convscaled.increaseMap(dS);

			Point2D.Double res = this.getDstPointByPointD(projp_midl2drawPnt.first);
			recalcBindPointByDrawDxDy(new double[]{res.x - projp_midl2drawPnt.second.x, res.y - projp_midl2drawPnt.second.y});

			return rv;

		}

		public MPoint increaseMap(MPoint dS, boolean evently) throws Exception
		{
			MPoint pt = getViewSize();
			Pair<MPoint,MPoint> projp_midl2drawPnt = getProjMidle(pt);
			MPoint rv = convscaled.increaseMap(dS, evently);

			Point2D.Double res = this.getDstPointByPointD(projp_midl2drawPnt.first);
			recalcBindPointByDrawDxDy(new double[]{res.x - projp_midl2drawPnt.second.x, res.y - projp_midl2drawPnt.second.y});

			return rv;
		}

		public MPoint decreaseMap(double dS) throws Exception
		{
			return increaseMap(1.0/dS);
		}

		public MPoint getUnitsOnPixel() throws Exception
		{
			return convscaled.getUnitsOnPixel();
		}

		public void reSetInitScale()
		{
			convscaled.reSetInitScale();
		}

		public void setScale(MPoint newscale) throws Exception
		{
			convscaled.setScale(newscale);
		}

		public MPoint getScale()  throws Exception
		{
			return convscaled.getScale();
		}

		public void setScaleBean(IScaleDescBean scalebean) throws Exception
		{
			convscaled.setScaleBean(scalebean);
		}

		public double[] getRotMatrix()  throws Exception
		{
			return convrotate.getRotMatrix();
		}

		public void setRotMatrix(double[] matrix)  throws Exception
		{
			convrotate.setRotMatrix(matrix);
		}

		public ILinearConverter getSrc2SyncConverter()
		{

			List<ILinearConverter> conv = new LinkedList<ILinearConverter>();
			conv.add(convrotate);
			return new ChainConverter(conv);
		}

		public ILinearConverter getSync2DstConverter()
		{
			List<ILinearConverter> conv = new LinkedList<ILinearConverter>();
			conv.add(convscaled);
			conv.add(convshift);
			return new ChainConverter(conv);
		}

		protected String convname;

		public String getObjName()
		{
			return convname;
		}

		public Object[] init(Object... objs) throws Exception
		{
			for (Object obj : objs)
				init(obj);

			if (convrotate == null)
				convrotate = new RotateConverter();
			if (convscaled == null)
				convscaled = new ScaledConverter();
			if (convshift == null)
				convshift = new ShiftConverter();

			converterchain.add(convrotate);
			converterchain.add(convscaled);
			converterchain.add(convshift);
			return null;
		}

		public Object init(Object obj) throws Exception
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.convname = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else if (attr.getName().equalsIgnoreCase("converter"))
			{
				ILinearConverter lc = (ILinearConverter) attr.getValue();
				if (lc.getTypeConverter().equals(ROTATECONVERTER))
					convrotate = (RotateConverter) lc;
				else if (lc.getTypeConverter().equals(SCALECONVERTER))
					convscaled = (ScaledConverter) lc;
				else if (lc.getTypeConverter().equals(SHIFTCONVERTER))
					convshift = (ShiftConverter) lc;
			}
			return null;
		}

		public IObjectDesc getObjectDescriptor()
		{
			return desc;
		}
	}

	static public class ShiftConverter extends BBaseConverter
			implements IShiftConverter, IInitAble
	{

		protected MPoint viewSize;
		protected IXMLObjectDesc desc;

		public boolean equals(Object obj)
		{
			if (obj instanceof ShiftConverter)
			{
				ShiftConverter sc = (ShiftConverter) obj;
				return sc.viewSize.equals(viewSize) && sc.currentP0.equals(currentP0);
			}
			return false;
		}


		public MPoint getViewSize()
		{
			if (viewSize != null)
				return new MPoint(viewSize);
			return null;
		}

		public MPoint setViewSize(MPoint sz)
		{
			MPoint old_sz = viewSize;
			if (sz != null)
				viewSize = new MPoint(sz);
			else
				viewSize = sz;
			return old_sz;
		}

		public IShiftConverter getAsShiftConverter()
		{
			return this;
		}

		protected MPoint currentP0 = new MPoint(Double.NaN, Double.NaN);

		public ShiftConverter()
		{

		}

		public ShiftConverter(MPoint pnt)
		{
			this.currentP0 = new MPoint(pnt);
		}

		protected ShiftConverter(ShiftConverter copy)
		{

			this.convname = copy.convname;
			if (copy.viewSize != null)
				viewSize = new MPoint(copy.viewSize);
			currentP0 = new MPoint(copy.currentP0);
		}

		/**
		 * Конвертирует в координаты проекта координаты точек экрана
		 *
		 * @param pnt - точка экрана
		 * @return - координаты проекта
		 */
		public MPoint getPointByDstPoint(Point.Double pnt)
		{
			double x = (pnt.x + currentP0.x);
			double y = (pnt.y + currentP0.y);
			return new MPoint(x, y);
		}


		public Point.Double getDstPointByPointD(MPoint pt)
		{
			double x = (pt.x - currentP0.x);
			double y = (pt.y - currentP0.y);
			return new Point.Double(x, y);
		}


		public ILinearConverter createCopyConverter()
		{
			return new ShiftConverter(this);
		}

		public String getTypeConverter()
		{
			return SHIFTCONVERTER;
		}


		public MPoint getBindP0()
		{
			return new MPoint(currentP0);
		}

		public MPoint setBindP0(MPoint currentP0)
		{
			MPoint rv = this.currentP0;
			this.currentP0 = new MPoint(currentP0);
			return rv;
		}

		public void recalcBindPointByDrawDxDy(double[] dXdY)
		{
			currentP0.x += dXdY[0];
			currentP0.y += dXdY[1];
		}

		public void saveToStream(DataOutputStream dos) throws IOException
		{
		}

		public void loadFromStream(DataInputStream dis) throws IOException
		{
		}

		protected String convname;

		public String getObjName()
		{
			return convname;
		}

		public Object[] init(Object... objs) throws Exception
		{
			for (Object obj : objs)
				init(obj);
			return null;
		}

		public Object init(Object obj) throws Exception
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.convname = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else if (attr.getName().equalsIgnoreCase("bindp"))
			{
				String bindp0 = (String) attr.getValue();
				String[] sbindp = bindp0.split(" ");
				currentP0 = new MPoint(Double.parseDouble(sbindp[0]), Double.parseDouble(sbindp[1]));
			}
			else if (attr.getName().equalsIgnoreCase("viewsz"))
			{
				String viewsz = (String) attr.getValue();
				String[] sviewsz = viewsz.split(" ");
				viewSize = new MPoint(Double.parseDouble(sviewsz[0]), Double.parseDouble(sviewsz[1]));
			}
			return null;
		}

		public IObjectDesc getObjectDescriptor()
		{
			return desc;
		}

	}

	static public class ScaledConverter extends BBaseConverter
			implements IScaledConverter, IInitAble
	{
		protected IXMLObjectDesc desc;

		public boolean equals(Object obj)
		{
			if (obj instanceof ScaledConverter)
			{
				ScaledConverter sc = (ScaledConverter) obj;
				return sc.scale == scale;
			}
			return false;
		}

		public IScaledConverterCtrl getAsScaledConverter()
		{
			return this;
		}

		private MPoint scale;
		private MPoint initscale;
		private MPoint[] scalerange = new MPoint[]{new MPoint(-1, -1), new MPoint(-1, -1)};
		private IScaleDescBean scalebean;

		public ScaledConverter()
		{

		}

		public ScaledConverter(MPoint scale)
		{
			this.scale = scale;
			this.initscale = scale;
		}

		ScaledConverter(ScaledConverter copy)
		{
			this.convname = copy.convname;
			this.scale = copy.scale;
			this.initscale = copy.initscale;
		}

		/**
		 * Конвертирует в координаты проекта в координаты точек экрана
		 *
		 * @param pnt - точка экрана
		 * @return - координаты проекта
		 */
		public MPoint getPointByDstPoint(Point.Double pnt)
		{
			return new MPoint(pnt.x / scale.x, pnt.y / scale.y);
		}


		public Point.Double getDstPointByPointD(MPoint pt)
		{
			return new Point.Double(pt.x * scale.x, pt.y * scale.y);
		}


		public void recalcScale(MRect rect, int[] szXY) throws Exception
		{
			recalcScaleByLinearRect(rect, szXY);
			initscale = scale;
		}


		public ILinearConverter createCopyConverter()
		{
			return new ScaledConverter(this);
		}

		public String getTypeConverter()
		{
			return SCALECONVERTER;
		}

		public MPoint getUnitsOnPixel()
		{
			return new MPoint(1.0 / scale.x, 1.0 / scale.y);
		}

		private double normolizeScale(double[] scalerange, double curscale, double dS)
		{
			double res = curscale * dS;
			if (scalerange[0] > 0 && scalerange[0] > res)
			{
				dS = scalerange[0] / curscale;//исходим из того что всегда scalerange[0] <= curscale

				if (scalebean != null && scalebean.getSclCnt() > 0)
				{
					int n = (int) Math.floor(Math.log(1 / dS) / Math.log(scalebean.getdScale()));
					dS = Math.pow(scalebean.getdScale(), n);
					dS = 1 / dS;
				}
				return dS;
			}

			if (scalerange[1] > 0 && scalerange[1] < res)
			{
				dS = scalerange[1] / curscale; //исходим из того что всегда scalerange[1] >= curscale
				if (scalebean != null && scalebean.getSclCnt() > 0)
				{
					int n = (int) Math.floor(Math.log(dS) / Math.log(scalebean.getdScale()));
					dS = Math.pow(scalebean.getdScale(), n);
				}
				return dS;
			}

			return dS;
		}

		public MPoint increaseMap(double dS)
		{
//			if (dS == 1)
//				return scale;
//
//			dS = calcdS(dS);
//
//			double dSx = normolizeScale(new double[]{scalerange[0].getX(), scalerange[1].getX()}, scale.x, dS);
//			double dSy = normolizeScale(new double[]{scalerange[0].getY(), scalerange[1].getY()}, scale.y, dS);
//
//			if (!Double.isNaN(dSx) && !Double.isNaN(dSy))
//			{
//				if (dS > 1)
//					dS = Math.min(dSx, dSy);
//				if (dS < 1)
//					dS = Math.max(dSx, dSy);
//
//				MPoint res = new MPoint(scale.x * dS, scale.y * dS);
//				if (!Double.isNaN(res.x) && !Double.isNaN(res.y))
//					scale = res;
//			}
//			return scale;

			return increaseMap(new MPoint(dS,dS), true);
		}


		public MPoint increaseMap(MPoint dS, boolean evently)
		{
			if (dS.x == 1 && dS.y ==1)
				return scale;

			dS = new MPoint(dS);

			dS.x = calcdS(dS.x);
			if (evently)
				dS.y = dS.x;
			else
				dS.y = calcdS(dS.y);

			double dSx = normolizeScale(new double[]{scalerange[0].getX(), scalerange[1].getX()}, scale.x, dS.x);
			double dSy = normolizeScale(new double[]{scalerange[0].getY(), scalerange[1].getY()}, scale.y, dS.y);

			if (!Double.isNaN(dSx) && !Double.isNaN(dSy))
			{

				if (evently)
				{
					double dS1=dS.x;
					if (dS1 > 1)
						dS1 = Math.min(dSx, dSy);
					if (dS1 < 1)
						dS1 = Math.max(dSx, dSy);
					dS.y = dS.x = dS1;
				}

				MPoint res = new MPoint(scale.x * dS.x, scale.y * dS.y);
				if (!Double.isNaN(res.x) && !Double.isNaN(res.y))
					scale = res;
			}
			return scale;
		}

		private double calcdS(double dS)
		{
			if (scalebean != null && scalebean.getSclCnt() > 0)
			{
				int n;
				if (dS > 1)
				{
					n = (int) Math.round(Math.log(dS) / Math.log(scalebean.getdScale()));
					dS = Math.pow(scalebean.getdScale(), n);

				}
				else if (dS < 1)
				{
					n = (int) Math.round(Math.log(1.0 / dS) / Math.log(scalebean.getdScale()));
					dS = Math.pow(scalebean.getdScale(), n);
					dS = 1 / dS;
				}
			}
			return dS;
		}


		public MPoint decreaseMap(double dS)
		{
			return increaseMap(1 / dS);
		}

		public void reSetInitScale()
		{
			this.scale = initscale;
		}

		public void setScale(MPoint newscale)
		{
			scale=new MPoint(newscale);
		}

		public MPoint getScale()
		{
			return new MPoint(scale);
		}

		public void setScaleBean(IScaleDescBean scalebean) throws Exception
		{
			this.scalebean = scalebean;
			if (scalebean != null)
				this.scalerange = new MPoint[]{new MPoint(scalebean.getMinScale()), new MPoint(scalebean.getMaxScale())};
		}

		public void recalcScaleByLinearRect(MRect linearRect, int[] szXY) throws Exception
		{
			double scale = (Math.min(szXY[0], szXY[1]) * 1.0) /
					Math.max
							(
									Math.abs(linearRect.getWidth()), Math.abs(linearRect.getHeight())
							);


			if (!Double.isNaN(scale))
				this.scale = new MPoint(scale, scale);
			else
				throw new Exception("Can't calculate scale");
		}

		public void saveToStream(DataOutputStream dos) throws IOException
		{
			dos.writeDouble(initscale.x);
			dos.writeDouble(scale.x);
		}

		public void loadFromStream(DataInputStream dis) throws IOException
		{
			double l_scale = dis.readDouble();
			initscale = new MPoint(l_scale, l_scale);
			l_scale = dis.readDouble();
			scale = new MPoint(l_scale, l_scale);
		}

		protected String convname;

		public String getObjName()
		{
			return convname;
		}

		public Object[] init(Object... objs) throws Exception
		{
			for (Object obj : objs)
				init(obj);
			if (scale == null && initscale != null)
				scale = new MPoint(initscale);
			else if (initscale == null && scale != null)
				initscale = new MPoint(scale);
			return null;
		}

		public Object init(Object obj) throws Exception
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.convname = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else if (attr.getName().equalsIgnoreCase("scale"))
			{
				String sclaestring = (String) attr.getValue();
				String[] sscale = sclaestring.split(" ");
				scale = new MPoint(Double.parseDouble(sscale[0]), Double.parseDouble(sscale[1]));
			}
			else if (attr.getName().equalsIgnoreCase("initscale"))
			{
				String sclaestring = (String) attr.getValue();
				String[] sscale = sclaestring.split(" ");
				initscale = new MPoint(Double.parseDouble(sscale[0]), Double.parseDouble(sscale[1]));
			}
			return null;
		}

		public IObjectDesc getObjectDescriptor()
		{
			return desc;
		}

	}

	static public class RotateConverter extends BBaseConverter
			implements IRotateConverter, IInitAble
	{
		protected IXMLObjectDesc desc;

		public boolean equals(Object obj)
		{
			if (obj instanceof RotateConverter)
			{
				RotateConverter rc = (RotateConverter) obj;
				if ((rc.matrix.length != matrix.length))
					return false;

				for (int i = 0; i < matrix.length; i++)
					if (matrix[i] != rc.matrix[i])
						return false;
				return true;
			}
			return false;
		}

		public IRotateConverter getAsRotateConverter()
		{
			return this;
		}

		public double[] getRotMatrix()
		{
			return matrix;
		}

		public void setRotMatrix(double[] matrix)
		{
			this.matrix = new double[matrix.length];
			System.arraycopy(matrix, 0, this.matrix, 0, matrix.length);
		}

		private double[] matrix;// = new double[]{0,1,-1,0};


		public RotateConverter()
		{
			matrix = new double[]{1, 0, 0, 1};
		}

		public RotateConverter(double[] matrix)
		{
				setRotMatrix(matrix);
		}

		RotateConverter(RotateConverter copy)
		{
			this.convname = copy.convname;
			setRotMatrix(copy.matrix);
		}

		/**
		 * Конвертирует в координаты проекта в координаты точек экрана
		 *
		 * @param pnt - точка экрана
		 * @return - координаты проекта
		 */
		public MPoint getPointByDstPoint(Point.Double pnt)
		{
			double y;
			double x;
			if (matrix[0] > matrix[1])
			{
				y = (pnt.getY() - pnt.getX() * matrix[2] / matrix[0]) / (matrix[3] - matrix[1] * matrix[2] / matrix[0]);
				x = (pnt.getX() - y * matrix[1]) / matrix[0];
			}
			else
			{
				x = (pnt.getY() - pnt.getX() * matrix[3] / matrix[1]) / (matrix[2] - matrix[0] * matrix[3] / matrix[1]);
				y = (pnt.getX() - x * matrix[0]) / matrix[1];
			}
			return new MPoint(x, y);
		}


		public Point.Double getDstPointByPointD(MPoint pt)
		{
			double x = pt.getX() * matrix[0] + pt.getY() * matrix[1];
			double y = pt.getX() * matrix[2] + pt.getY() * matrix[3];
			return new Point.Double(x, y);
		}

		public ILinearConverter createCopyConverter()
		{
			return new RotateConverter(this);
		}

		public String getTypeConverter()
		{
			return ROTATECONVERTER;
		}

		public MRect getLinearRectByRect(MRect rect)
		{
			Point.Double p1 = getDstPointByPointD(rect.p1);
			Point.Double p4 = getDstPointByPointD(rect.p4);

			return new MRect(new MPoint(Math.min(p1.x, p4.x), Math.min(p1.y, p4.y)),
					new MPoint(Math.max(p1.x, p4.x), Math.max(p1.y, p4.y)));
		}


		public void saveToStream(DataOutputStream dos) throws IOException
		{
			for (double v : matrix)
			{
				dos.writeDouble(v);
			}
		}

		public void loadFromStream(DataInputStream dis) throws IOException
		{
			for (int i = 0; i < matrix.length; i++)
				matrix[i] = dis.readDouble();
		}


		protected String convname;

		public String getObjName()
		{
			return convname;
		}

		public Object[] init(Object... objs) throws Exception
		{
			for (Object obj : objs)
				init(obj);
			return null;
		}

		public Object init(Object obj) throws Exception
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.convname = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else if (attr.getName().equalsIgnoreCase("matrix"))
			{
				String matrixstring = (String) attr.getValue();
				String[] smatrix = matrixstring.split(" ");
				for (int i = 0; i < smatrix.length; i++)
					matrix[i] = Double.parseDouble(smatrix[i]);
			}
			return null;
		}

		public IObjectDesc getObjectDescriptor()
		{
			return desc;
		}
	}
}
