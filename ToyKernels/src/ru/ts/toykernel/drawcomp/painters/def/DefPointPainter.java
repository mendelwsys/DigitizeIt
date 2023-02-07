package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.toykernel.drawcomp.IParamPainter;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.factory.IParam;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

/**
 * Default point painter
 */
public class DefPointPainter
		extends BaseInitAble
		implements IParamPainter
{
	public static final String ATTR_ROT = "DROT";

	public static final String ATTR_SCX = "SCX";
	public static final String ATTR_SCY = "SCY";

	public static final String ATTR_SHX = "SHX";
	public static final String ATTR_SHY = "SHY";
	


	protected String  attrDashArray = KernelConst.ATTR_STROKE_DASHARRAY;
	protected boolean isScaleDashArray =false;//scale dashing in painter
	protected double drot=0;//rotation angle in degree (positive angle - counterclockwise)

	protected INameConverter nameConverter;

	public static final float DOTLEN = 0.01f; //length of dot in persent of total line length



	public Image getPntImage()
	{
		return pntImage;
	}

	public void setPntImage(Image pntimage, Point imageAdjust)
	{
		this.pntImage = pntimage;
		this.imageAdjust = imageAdjust;
	}

	protected Image pntImage;
	private Point imageAdjust;
	protected Color colorLine;
	protected Stroke stroke;
	protected Paint paintFill;
	protected Integer composite;
	protected boolean scaledThickness = false;

	public void setComposite(Integer composite)
	{
		this.composite=composite;
	}

	public Integer getComposite()
	{
		return composite;
	}

	public int getSizePnt()
	{
		return radPnt;
	}

	public void setSizePnt(int radPnt)
	{
		this.radPnt = radPnt;
	}

	protected int radPnt = 3;

	public DefPointPainter()
	{
	}

	public DefPointPainter(Paint paintFill, Color colorLine, Stroke stroke, int radPnt, Image pntImage)
	{
		this.stroke = stroke;
		this.paintFill = paintFill;
		this.colorLine = colorLine;
		this.radPnt = radPnt;
		this.pntImage = pntImage;
	}

	public Paint getPaintFill()
	{
		return paintFill;
	}

	public void setPaintFill(Paint paintFill)
	{
		this.paintFill = paintFill;
	}

	public Color getColorLine()
	{
		return colorLine;
	}

	public void setColorLine(Color colorLine)
	{
		this.colorLine = colorLine;
	}

	public Stroke getStroke()
	{
		return stroke;
	}

	public boolean isScaledThickness()
	{
		return scaledThickness;
	}


	public void setScaledThickness(boolean scaledThickness)
	{
		this.scaledThickness = scaledThickness;
	}

	public void setStroke(Stroke stroke)
	{
		this.stroke = stroke;
	}


	protected void convert2drawCrds(ILinearConverter converter, double[][][] pnts, int[][] x, int[][] y)
	{
		MPoint cnvPnt=new MPoint();
		for (int i = 0; i < x.length; i++)
		{
			x[i] = new int[pnts[0][i].length];
			y[i] = new int[pnts[1][i].length];

			for (int j = 0; j < x[i].length; j++)
			{
				cnvPnt.x=pnts[0][i][j];
				cnvPnt.y=pnts[1][i][j];
				Point pi = converter.getDstPointByPoint(cnvPnt);
				x[i][j] = pi.x;
				y[i][j] = pi.y;
			}
		}
	}

	public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
	{
		setPainterParams(graphics, drawMe, converter, drawSize);

		double[][][] arrpoint = drawMe.getRawGeometry();
		Point pobj = converter.getDstPointByPoint(new MPoint(arrpoint[0][0][0], arrpoint[1][0][0]));
		drawPoint(graphics, pobj.x, pobj.y);
		return new int[]{1, 0, 0};
	}

	protected void setPainterParams(Graphics graphics, IBaseGisObject obj, ILinearConverter converter, Point drawSize)
	{
		if (graphics instanceof Graphics2D)
		{
			if (composite!=null)
				((Graphics2D)graphics).setComposite(AlphaComposite.getInstance(composite));
			else
				((Graphics2D)graphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		}

		stroke = setStrokeByRule(converter,stroke);
//TODO Включить после отладки		stroke = setStrokeByObject(converter,obj,stroke);
	}

	protected void drawPoint(Graphics graphics, int x, int y)
	{
		if (pntImage!=null)
		{
			ImageObserver observer = new ImageObserver()
			{
				public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
				{
					return false;
				}
			};

			int w=0;
			int h=0;

			if (imageAdjust !=null)
			{
				w= imageAdjust.x;
				h= imageAdjust.y;
			}

			if (paintFill instanceof Color)
				graphics.drawImage(pntImage,x-w, y-h,(Color)paintFill,
						observer);
			else
				graphics.drawImage(pntImage,x-w, y-h,observer);
		}
		else
		if (radPnt > 0)
		{
			setFillPainter(graphics, paintFill);
			graphics.fillOval(x - radPnt, y - radPnt, 2 * radPnt, 2 * radPnt);
		}
	}

	protected void setFillPainter(Graphics graphics, Paint paintFill)
	{
		if (graphics instanceof Graphics2D)
			((Graphics2D)graphics).setPaint(paintFill);
		else
			if (paintFill instanceof Color)
				graphics.setColor((Color) paintFill);
		else
			throw new UnsupportedOperationException("Can't draw with graphics type: "+graphics.getClass().getCanonicalName()
			+" and "+ paintFill.getClass().getCanonicalName());
	}

	public MRect getRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		MRect rect = getDrawRect(graphics,obj, converter);
		return converter.getRectByDstRect(rect, null);
	}

	public MRect getDrawRect(Graphics graphics,IBaseGisObject obj, ILinearConverter converter)  throws Exception
	{
		double[][][] arrpoint = obj.getRawGeometry();
		Point pobj = converter.getDstPointByPoint(new MPoint(arrpoint[0][0][0], arrpoint[1][0][0]));
		return new MRect(new Point(pobj.x - radPnt, pobj.y - radPnt), new Point(pobj.x + radPnt, pobj.y + radPnt));
	}

	public Shape createShape(IBaseGisObject drawMe, ILinearConverter converter) throws Exception
	{
		return null; //Нет шейпа если null, хотя сделать надо кое что поумнее.
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
//		if (attr.getName().equalsIgnoreCase(KernelConst.ATTR_STROKE_DASHARRAY))
//			attrDashArray =(String)attr.getValue();
//		else
		if (attr.getName().equalsIgnoreCase(getNameConverter().codeAttrNm2StorAttrNm(KernelConst.ATTR_SCALE_DASHARRAY)))
		{

			boolean isScaleDashArray=this.isScaleDashArray;
			this.isScaleDashArray = Boolean.parseBoolean((String)attr.getValue());
			IParam rattr = attr.getCopy();
			rattr.setValue(""+isScaleDashArray);
			return rattr;
		}
		else if (attr.getName().equalsIgnoreCase(getNameConverter().codeAttrNm2StorAttrNm(ATTR_ROT)))
		{
			double drot=this.drot;
			this.drot= Double.parseDouble((String)attr.getValue());
			IParam rattr = attr.getCopy();
			rattr.setValue(""+drot);
			return rattr;
		}
		return null;
	}


	protected Stroke setStrokeByObject(ILinearConverter converter,IBaseGisObject obj,Stroke stroke)
	{
		if (stroke instanceof BasicStroke)
		{
			BasicStroke bs = (BasicStroke) stroke;
			IAttrs attr = obj.getObjAttrs();
			IDefAttr dashsttr = attr.get(attrDashArray);
			if (dashsttr!=null)
			{

				float[] dash= (float[]) dashsttr.getValue();
				normolizeDash(converter, dash);
				return new BasicStroke(bs.getLineWidth(), bs.getEndCap(), bs.getLineJoin(), bs.getMiterLimit(),
								dash, 0);  //TODO Сделать еще фазу которую можно задавать через аттрибут
			}
		}
		return stroke;
	}

	protected void normolizeDash(ILinearConverter converter, float[] dash)
	{
		if (dash!=null && dash.length>0)
		{
			MRect mrect=new MRect();
			float totaldash=0;
			boolean neednoraml=false;
			for (float v : dash)
			{
				neednoraml= neednoraml || (v==0);
				totaldash+=v;
			}
			if (neednoraml || isScaleDashArray)
				for (int i = 0; i < dash.length; i++)
				{
					float v = dash[i];
					if (v==0)
						v=totaldash* DOTLEN;
					if (isScaleDashArray)
					{
						mrect.p4.x = v;
						mrect.p4.y = v;
						dash[i]= (float) converter.getDstSzBySz(mrect)[0];
					}
					else
						dash[i]=v;

					if (dash[i]<1)
						dash[i]=1;
				}
		}
	}

	/**
	 * set stroke by rule paramters
	 * @param converter - coordinate converter
	 * @param stroke - stroke was set by rule
	 * @return - stroke set by painter
	 */
	protected Stroke setStrokeByRule(ILinearConverter converter,Stroke stroke)
	{
		if (stroke instanceof BasicStroke)
		{
			BasicStroke bs = (BasicStroke) stroke;
			float lw = bs.getLineWidth();
			if (isScaledThickness())
			{
				if (lw==1.0)
					System.out.println("lw = " + lw);
				double[] res = converter.getDstSzBySz(new MRect(new MPoint(), new MPoint(lw, lw)));
				if (res[0]<1)
					System.out.println("res = " + res[0]);
				lw=(float)res[0];
			}
			float[] dashArray = bs.getDashArray();
			normolizeDash(converter, dashArray);
			return new BasicStroke(lw, bs.getEndCap(), bs.getLineJoin(), bs.getMiterLimit(),
							dashArray, bs.getDashPhase());
		}
		return stroke;
	}
	protected AffineTransform getTransform(IBaseGisObject obj, MPoint anchor)
	{
		return getTransform(obj,anchor,ATTR_ROT);
	}

	protected AffineTransform getTransform(IBaseGisObject obj, MPoint anchor,String attrRotName)
	{
		IAttrs oattrs = obj.getObjAttrs();
		IDefAttr rta = oattrs.get(attrRotName);

		double rangl=0;
//		double sx=1;
//		double sy=1;
		if (rta!=null)
		{
			double rt = (Double) rta.getValue();
			rangl = -Math.PI * rt / 180;
		}
		else if (drot!=0)
			rangl = -Math.PI * drot / 180;

//		if (rangl!=0 || sx!=1 || sy!=1)
		if (rangl!=0)
		{
			//positive angle -  clockwise in AffineTransform
			AffineTransform transform = AffineTransform.getRotateInstance(rangl, anchor.getX(), anchor.getY());
			return transform;
			//new AffineTransform(Math.cos(rangl), -Math.sin(rangl), Math.sin(rangl), Math.cos(rangl),0,0);
		}
		return null;
	}

	public INameConverter getNameConverter()
	{
		if (nameConverter==null)
			nameConverter=new DefNameConverter(); //for backward compatibility with olds and simple rules
		return nameConverter;
	}

	public void setNameConverter(INameConverter nameConverter)
	{
		this.nameConverter = nameConverter;
	}


}
