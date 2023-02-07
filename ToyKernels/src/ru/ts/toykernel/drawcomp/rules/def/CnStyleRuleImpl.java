package ru.ts.toykernel.drawcomp.rules.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.drawcomp.painters.def.*;
import ru.ts.toykernel.drawcomp.*;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.factory.IFactory;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.converters.IScaledConverterCtrl;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.gisutils.algs.common.MPoint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Rule implementation base on common style
 * ru.ts.toykernel.drawcomp.rules.def.CnStyleRuleImpl
 */
public class CnStyleRuleImpl implements IDrawObjRule
{
	public static final String RULETYPENAME = "CS_RL";

	protected String attrasname=KernelConst.ATTR_CURVE_NAME;

	protected String point_text;
	protected String poly_text;
	protected String line_text;

	protected String point;
	protected String poly;
	protected String polyh;

	protected String line;


	protected String ELLIPS;
	protected String ELLIPSF;

	protected String RECT;
	protected String RECTF;


	protected boolean textMode = false;
	protected Font ft;
	protected Float fontSize = (float) 12;
	protected String fontname = "sunserif";
	protected Integer fstyle = Font.BOLD;
	protected Integer defRadPoint=0;


	protected IXMLObjectDesc desc;
	protected INameConverter nameConverter;


	public static final String TEXT_MODE = "TextMode";
	public static final String FONT_SIZE = "fontSize";
	public static final String FONT_NAME = "fontName";
	public static final String FONT_STYLE = "fontStyle";
	public static final String COMPOSITE = "Composite";
	public static final String STROKE = "stroke";
	public static final String PAINT = "paint";
	public static final String IMGPOINT = "imgpoint";
	public static final String IMGCENTRALPOINT = "imgcentralpoint";

	public static final String FLOATSEP = ";";



	public String getRuleType()
	{
		return RULETYPENAME;
	}

	public INameConverter getNameConverter()
	{
		return nameConverter;
	}

	public void setNameConverter(INameConverter nameConverter)
	{
		this.nameConverter = nameConverter;
	}

	public Map<String, IFactory<IParamPainter>> getPaintersClass()
	{
		return paintersClass;
	}


	public void setPaintersClass(Map<String, IFactory<IParamPainter>> paintersClass)
	{
		this.paintersClass = paintersClass;
		resetCache();
	}

	public void resetCache()
	{
		cacheTextParamPainter = null;
		cacheParamPainter = null;
	}


	public void setPointTextFactory(IFactory<IParamPainter> factory)
	{
		paintersClass.put(point_text, factory);
		resetCache();
	}

	public void setPointFactory(IFactory<IParamPainter> factory)
	{
		paintersClass.put(point, factory);
		resetCache();
	}


	public void setLineTextFactory(IFactory<IParamPainter> factory)
	{
		paintersClass.put(line_text, factory);
		resetCache();
	}

	public void setLineFactory(IFactory<IParamPainter> factory)
	{
		paintersClass.put(line, factory);
		resetCache();
	}


	public void setPolyTextFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(poly_text, factory);
		resetCache();
	}

	public void setPolyFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(poly, factory);
		resetCache();
	}


	public void setEllipsFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(ELLIPS, factory);
		resetCache();
	}

	public void setEllipsfFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(ELLIPSF, factory);
		resetCache();
	}


	public void setRectFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(RECT, factory);
		resetCache();
	}

	public void setRectfFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(RECTF, factory);
		resetCache();
	}

	public IFactory<IParamPainter> getPointTextFactory()
	{
		return paintersClass.get(point_text);
	}

	public IFactory<IParamPainter> getPointFactory()
	{
		return paintersClass.get(point);
	}


	public IFactory<IParamPainter> getLineTextFactory()
	{
		return paintersClass.get(line_text);
	}

	public IFactory<IParamPainter> getLineFactory()
	{
		return paintersClass.get(line);
	}

	public IFactory<IParamPainter> getPolyTextFactory()
	{
		return paintersClass.get(poly_text);
	}

	public IFactory<IParamPainter> getPolyPainterFactory()
	{
		return paintersClass.get(poly);
	}

	public IFactory<IParamPainter> getEllipsPainterFactory()
	{
		return paintersClass.get(ELLIPS);
	}

	public IFactory<IParamPainter> getEllipsfPainterFactory()
	{
		return paintersClass.get(ELLIPSF);
	}


	public IFactory<IParamPainter> getRectPainterFactory()
	{
		return paintersClass.get(RECT);
	}

	public IFactory<IParamPainter> getRectfPainterFactory()
	{
		return paintersClass.get(RECTF);
	}

	protected Map<String, IFactory<IParamPainter>> paintersClass = new HashMap<String, IFactory<IParamPainter>>();



	public CnStyleRuleImpl()
	{
	}


	public CnStyleRuleImpl(CommonStyle defStyle)
	{
		this(defStyle, (Map<String, IFactory<IParamPainter>>) null, null);
	}

	public CnStyleRuleImpl(CommonStyle defStyle,String attrasname)
	{
		this(defStyle, (Map<String, IFactory<IParamPainter>>) null, null,attrasname);
	}

	public CnStyleRuleImpl(CommonStyle defStyle, Font ft)
	{
		this(defStyle, ft, null, null);
	}

	public CnStyleRuleImpl(CommonStyle defStyle, Font ft,String attrasname)
	{
		this(defStyle, ft, null, null,attrasname);
	}

	public CnStyleRuleImpl(CommonStyle defStyle, Font ft, Map<String, IFactory<IParamPainter>> paintersClass, INameConverter nameConverter)
	{
		this(defStyle, paintersClass, nameConverter);
		this.ft = ft;
		this.textMode = true;
	}

	public CnStyleRuleImpl(CommonStyle defStyle, Font ft, Map<String, IFactory<IParamPainter>> paintersClass, INameConverter nameConverter,String attrasname)
	{
		this(defStyle, paintersClass, nameConverter);
		this.attrasname=attrasname;
		this.ft = ft;
		this.textMode = true;

	}

	public CnStyleRuleImpl(CommonStyle defStyle, Map<String, IFactory<IParamPainter>> paintersFactory, INameConverter nameConverter)
	{
		initAll(defStyle, paintersFactory, nameConverter,attrasname);
	}

	public CnStyleRuleImpl(CommonStyle defStyle, Map<String, IFactory<IParamPainter>> paintersFactory, INameConverter nameConverter,String attrasname)
	{
		initAll(defStyle, paintersFactory, nameConverter,attrasname);
	}


	public boolean isTextMode()
	{
		return textMode;
	}

	public void setTextMode(boolean textMode)
	{
		this.textMode = textMode;
	}

	public Font getFont()
	{
		if (ft == null && fontname != null && fstyle != null && fontSize != null)
		{
			ft = new Font(fontname, fstyle, fontSize.intValue());
			ft=ft.deriveFont(fontSize);
		}
		return ft;
	}


	protected void initAll(CommonStyle defStyle, Map<String, IFactory<IParamPainter>> paintersFactory, INameConverter nameConverter,String attrasname)
	{
		this.attrasname=attrasname;
		this.defStyle = defStyle;
		if (paintersFactory != null)
			this.paintersClass = paintersFactory;
		if (nameConverter == null)
			nameConverter = new DefNameConverter();
		this.nameConverter = nameConverter;

		point_text = nameConverter.codeAttrNm2StorAttrNm(KernelConst.POINT_TEXT);
		line_text = nameConverter.codeAttrNm2StorAttrNm(KernelConst.LINE_TEXT);
		poly_text = nameConverter.codeAttrNm2StorAttrNm(KernelConst.POLY_TEXT);

		point = nameConverter.codeAttrNm2StorAttrNm(KernelConst.POINT);
		line = nameConverter.codeAttrNm2StorAttrNm(KernelConst.LINESTRING);
		poly = nameConverter.codeAttrNm2StorAttrNm(KernelConst.LINEARRING);
		polyh = nameConverter.codeAttrNm2StorAttrNm(KernelConst.LINEARRINGH);


		ELLIPS = nameConverter.codeAttrNm2StorAttrNm(KernelConst.ELLIPS);
		ELLIPSF = nameConverter.codeAttrNm2StorAttrNm(KernelConst.ELLIPSF);

		RECT = nameConverter.codeAttrNm2StorAttrNm(KernelConst.RECT);
		RECTF = nameConverter.codeAttrNm2StorAttrNm(KernelConst.RECTF);


	}


	public void setDefStyle(CommonStyle defStyle)
	{
		this.defStyle = defStyle;
	}

	public CommonStyle getDefStyle()
	{
		return defStyle;
	}

	protected CommonStyle defStyle;//Style of layer
	protected Integer defComposite;//Как рисовать перекрывающиеся объекты

	protected IParamPainter cacheParamPainter; //Point painter
	protected String typenamepainter;//type of painter

	protected ITextParamPainter cacheTextParamPainter;//Text painter for all objects
	protected String typenametextpainter;//type of text painter


	protected Stroke defStroke;//Stroke Если он установлен тогда использовать его
	protected boolean setExternalStroke = false;//флаг показывает что Stroke был установлен целиком а не через аттрибуты
	//в этом случае классические аттрибуты не анализируются

	protected Paint defPaint;//painter Если есть он устанаваливается и рисуется с помощью него
	//1. Паинтер это цвет (через комон стайл)
	//2. Паинтер содержит тектстуру
	protected boolean setExternalPaint = false;//флаг показывает что Paint был установлен целиком а не через аттрибуты


	protected Image defImgPoint;//Изображение для точки по умолчанию
	private Point defCentralPoint;//Центральная точка изображения
	protected boolean setExternalImgPoint = false;//флаг показывает что ImgPoint был установлен целиком а не через аттрибуты

	protected IDrawObjRule interceptor;

	public void resetPainters()
	{
		resetCache();
	}

	public IDrawObjRule setInterceptor(IDrawObjRule interseptor)
	{

		IDrawObjRule rv = this.interceptor;
		this.interceptor = interseptor;
		return rv;
	}

	public IDrawObjRule getInterceptor()
	{
		return interceptor;
	}

	public IPainter cretatePainter(Graphics g, ILayer layer, IBaseGisObject obj) throws Exception
	{

		if (interceptor != null)
		{
			IPainter rv = interceptor.cretatePainter(g, layer, obj);
			if (rv != null)
				return rv;
		}

		if (textMode && getFont() == null)
			ft = g.getFont();


		Paint paintfill = defPaint;
		Image imgPoint= defImgPoint;
		Point centralImgPoint=defCentralPoint;

		Integer linecolor = null;
		Stroke stroke = defStroke;


		Integer radpnt = null;
		Integer composite = null;


		IAttrs curveattrs = obj.getObjAttrs();

		IDefAttr iDefAttr = null;
		if (curveattrs != null)
		{
			iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_COLOR_LINE));
			if (iDefAttr != null)
				linecolor = (Integer) iDefAttr.getValue();


			iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_RADPNT));
			if (iDefAttr != null)
				radpnt = (Integer) iDefAttr.getValue();

			iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_COMPOSITE));
			if (iDefAttr != null)
				composite = (Integer) iDefAttr.getValue();

			if (!setExternalPaint)
			{
				iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_COLOR_FILL));
				if (iDefAttr != null)
				{
					Integer color = (Integer) iDefAttr.getValue();
					paintfill = new Color(color, true);
				}
				else
				{
					iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_IMG_FILL));
					if (iDefAttr != null)
					{
						BufferedImage bi = (BufferedImage) iDefAttr.getValue();
						paintfill = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
					}
				}
			}

			if (!setExternalImgPoint)
			{
					iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_IMG_POINT));
					if (iDefAttr != null)
						imgPoint = (BufferedImage) iDefAttr.getValue();
					iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_IMG_CENTRALPOINT));
					if (iDefAttr != null)
						centralImgPoint = (Point) iDefAttr.getValue();
			}

			if (!setExternalStroke)
			{
				BasicStroke newstroke = null;
				iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_LINE_THICKNESS));
				if (iDefAttr != null)
				{
					if (newstroke == null)
						newstroke = getStrokeByStyle(defStyle);
					float linethickness = newstroke.getLineWidth();
					linethickness = getValueAsFloat(iDefAttr, linethickness);
					if (linethickness != newstroke.getLineWidth()) //Сформируем новый Stroke
						stroke = newstroke = new BasicStroke(linethickness, newstroke.getEndCap(), newstroke.getLineJoin(), newstroke.getMiterLimit(),
								newstroke.getDashArray(), newstroke.getDashPhase());
				}

				iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_LINE_CAP));
				if (iDefAttr != null)
				{
					if (newstroke == null)
						newstroke = getStrokeByStyle(defStyle);

					int cap = newstroke.getEndCap();
					cap = getValueAsInt(iDefAttr, cap);
					if (cap != newstroke.getEndCap())
						stroke = newstroke = new BasicStroke(newstroke.getLineWidth(), cap, newstroke.getLineJoin(), newstroke.getMiterLimit(),
								newstroke.getDashArray(), newstroke.getDashPhase());

				}

				iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_LINE_JOIN));
				if (iDefAttr != null)
				{
					if (newstroke == null)
						newstroke = getStrokeByStyle(defStyle);

					int join = newstroke.getLineJoin();
					join = getValueAsInt(iDefAttr, join);
					if (join != newstroke.getLineJoin())
						stroke = newstroke = new BasicStroke(newstroke.getLineWidth(), newstroke.getEndCap(), join, newstroke.getMiterLimit(),
								newstroke.getDashArray(), newstroke.getDashPhase());
				}

				iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_MITER_LIMIT));
				if (iDefAttr != null)
				{
					if (newstroke == null)
						newstroke = getStrokeByStyle(defStyle);

					float miterlimit = newstroke.getMiterLimit();
					miterlimit = getValueAsFloat(iDefAttr, miterlimit);
					if (miterlimit != newstroke.getMiterLimit())
						stroke = newstroke = new BasicStroke(newstroke.getLineWidth(), newstroke.getEndCap(), newstroke.getLineJoin(), miterlimit,
								newstroke.getDashArray(), newstroke.getDashPhase());
				}

				iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_DASHARRAY));
				if (iDefAttr != null)
				{
					if (newstroke == null)
						newstroke = getStrokeByStyle(defStyle);

					float[] dasharray = newstroke.getDashArray();
					dasharray = getValueAsFloatArray(iDefAttr, dasharray);
					if (dasharray != newstroke.getDashArray())
						stroke = newstroke = new BasicStroke(newstroke.getLineWidth(), newstroke.getEndCap(), newstroke.getLineJoin(), newstroke.getMiterLimit(),
								dasharray, newstroke.getDashPhase());
				}

				iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_DASHPHASE));
				if (iDefAttr != null)
				{
					if (newstroke == null)
						newstroke = getStrokeByStyle(defStyle);

					float dashphase = newstroke.getDashPhase();
					dashphase = getValueAsFloat(iDefAttr, dashphase);
					if (dashphase != newstroke.getDashPhase())
						stroke = newstroke = new BasicStroke(newstroke.getLineWidth(), newstroke.getEndCap(), newstroke.getLineJoin(), newstroke.getMiterLimit(),
								newstroke.getDashArray(), dashphase);
				}
			}
		}

		if (linecolor == null)
			linecolor = defStyle.getColorLine();
		if (radpnt == null)
			radpnt = defStyle.getRadPnt();
		if (composite == null)
			composite = defComposite;


		String geotype = obj.getGeotype();
		if (geotype.equals(poly))
			lineRingPainter(paintfill, linecolor, stroke, radpnt, layer, obj, composite);
		else if (geotype.equals(polyh))
			lineRingPainterH(paintfill, linecolor, stroke, radpnt, layer, obj, composite);
		else if (geotype.equals(line))
			lineStringPainter(paintfill, linecolor, stroke, radpnt, layer, obj, composite);
		else if (geotype.equals(point))
			pointPainter(paintfill, linecolor, stroke, radpnt, layer, obj, composite, imgPoint, centralImgPoint);
		else if (
				geotype.equals(ELLIPS)
						||
						geotype.equals(ELLIPSF)
						||
						geotype.equals(RECT)
						||
						geotype.equals(RECTF)
						
				)
			ovalPainter(paintfill, linecolor, stroke, radpnt, layer, obj, composite);
		else
			 otherPainter(paintfill, linecolor, stroke, radpnt, layer, obj, composite);

		if (textMode)
		{
			if (curveattrs != null)
			{
				String text = null;
				if (attrasname!=null)
					iDefAttr = curveattrs.get(nameConverter.codeAttrNm2StorAttrNm(attrasname));
				else
					iDefAttr = null;

				if (iDefAttr != null)
					text = (String) iDefAttr.getValue();
				cacheTextParamPainter.setText(text);
			}
			return cacheTextParamPainter;
		}
		else
			return cacheParamPainter;
	}

	private float[] getValueAsFloatArray(IDefAttr iDefAttr, float[] floatarray)
	{
		Object ofloatarray = iDefAttr.getValue();
		if (ofloatarray != null)
		{
			if (ofloatarray instanceof float[])
				floatarray = (float[]) ofloatarray;
			else
			{
				try
				{
					floatarray = getFloatArrayByObject(ofloatarray);
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
		return floatarray;
	}

	private float[] getFloatArrayByObject(Object ofloatarray)
	{
		if (ofloatarray != null)
		{
			String da = ofloatarray.toString();
			String[] sfloas = da.split(FLOATSEP);
			float[] l_dasharray = new float[sfloas.length];
			for (int i = 0; i < sfloas.length; i++)
				l_dasharray[i] = Float.parseFloat(sfloas[i]);
			return l_dasharray;
		}
		return null;
	}

	private int getValueAsInt(IDefAttr iDefAttr, int intval)
	{
		Object ointval = iDefAttr.getValue();
		if (ointval != null)
		{
			if (ointval instanceof Integer)
				intval = (Integer) ointval;
			else
			{
				try
				{
					intval = Integer.parseInt(ointval.toString());
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
		return intval;
	}

	private float getValueAsFloat(IDefAttr iDefAttr, float floatval)
	{
		Object ofloatval = iDefAttr.getValue();
		if (ofloatval != null)
		{
			if (ofloatval instanceof Integer)
				floatval = ((Integer) ofloatval).floatValue();
			if (ofloatval instanceof Float)
				floatval = (Float) ofloatval;
			else
			{
				try
				{
					floatval = Float.parseFloat(ofloatval.toString());
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
		return floatval;
	}

	protected void lineRingPainter(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer lr, IBaseGisObject obj, Integer composite)
			throws Exception
	{
		IParamPainter rv = null;
		if (textMode)
		{
			getInstanceTextPainter(paintersClass.get(poly_text), poly_text, DefPolyTextPainter.class);
			rv = cacheTextParamPainter;
			if (cacheTextParamPainter.getFont() == null)
				cacheTextParamPainter.setFont(getFont());
		}
		else
		{
			getInstancePainter(paintersClass.get(poly), poly, DefPolyPainter.class);
			rv = cacheParamPainter;
		}
		setPainterParams(rv, paintfill, linecolor, stroke, radPnt, composite);
	}

	protected void lineRingPainterH(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer lr, IBaseGisObject obj, Integer composite)
			throws Exception
	{
		IParamPainter rv = null;
		if (textMode)
		{
			getInstanceTextPainter(paintersClass.get(poly_text), poly_text, DefPolyTextPainter.class);
			rv = cacheTextParamPainter;
			if (cacheTextParamPainter.getFont() == null)
				cacheTextParamPainter.setFont(getFont());
		}
		else
		{
			getInstancePainter(paintersClass.get(polyh), polyh, DefPolyHPainter.class);
			rv = cacheParamPainter;
		}
		setPainterParams(rv, paintfill, linecolor, stroke, radPnt, composite);
	}


	protected void lineStringPainter(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer lr, IBaseGisObject obj, Integer composite)
			throws Exception
	{
		IParamPainter rv = null;
		if (textMode)
		{
			getInstanceTextPainter(paintersClass.get(line_text), line_text, DefLineTextPainter.class);
			rv = cacheTextParamPainter;
			if (cacheTextParamPainter.getFont() == null)
				cacheTextParamPainter.setFont(getFont());
		}
		else
		{
			getInstancePainter(paintersClass.get(line), line, DefLinePainter.class);
			rv = cacheParamPainter;
		}
		setPainterParams(rv, paintfill, linecolor, stroke, radPnt, composite);
	}


	protected void ovalPainter(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer lr, IBaseGisObject obj, Integer composite)
			throws Exception
	{
		IParamPainter rv = null;
		getInstancePainter(paintersClass.get(obj.getGeotype()), obj.getGeotype(), DefOvalPainter.class);
		rv = cacheParamPainter;
		setPainterParams(rv, paintfill, linecolor, stroke, radPnt, composite);
	}

	protected void pointPainter(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer proj_rect, IBaseGisObject obj, Integer composite, Image pointImg, Point central) throws Exception
	{
		IParamPainter rv = null;
		if (textMode)
		{
			getInstanceTextPainter(paintersClass.get(point_text), point_text, DefPointTextPainter.class);
			rv = cacheTextParamPainter;
			if (cacheTextParamPainter.getFont() == null)
				cacheTextParamPainter.setFont(getFont());
		}
		else
		{
			getInstancePainter(paintersClass.get(point), point, DefPointPainter.class);
			rv = cacheParamPainter;
		}

		if (pointImg==null)
		{
			pointImg=defStyle.getPointImg();
			central=defStyle.getPointCentralAsPoint();
			if (pointImg!=null && defStyle.isAutocenral() && central==null)
			{
				int w=pointImg.getWidth(null);
				int h=pointImg.getHeight(null);
				central= new Point(w/2,h/2);
			}
		}
		if (rv instanceof DefPointPainter)
			((DefPointPainter)rv).setPntImage(pointImg, central);


		setPainterParams(rv, paintfill, linecolor, stroke, radPnt, composite);
	}


	/**
	 * painter of other type then describe in KernelConst list
	 * @param paintfill - fill object
	 * @param linecolor - color of line
	 * @param stroke - line stroke
	 * @param radPnt - painter radius
	 * @param layer - current draw layer
	 *@param obj - object for painter
	 * @param composite - composit identifer @throws Exception -
	 */
	protected void otherPainter(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer layer, IBaseGisObject obj, Integer composite) throws Exception
	{
		throw new Exception("Unsupported object type:" + obj.getGeotype());
	}


	protected void getInstancePainter(IFactory<IParamPainter> factory, String typename, Class defPainter)
			throws Exception
	{
		if (cacheParamPainter == null || !typename.equals(typenamepainter))
		{
			if (factory == null)
			{
				Constructor constr = defPainter.getConstructor();
				cacheParamPainter = (IParamPainter) constr.newInstance();
			}
			else
				cacheParamPainter = factory.createByTypeName(typename);
			typenamepainter = typename;
		}
	}

	protected void getInstanceTextPainter(IFactory<IParamPainter> factory, String typename, Class defPainter)
			throws Exception
	{
		if (cacheTextParamPainter == null || !typename.equals(typenametextpainter))
		{
			if (factory == null)
			{
				Constructor constr = defPainter.getConstructor();
				cacheTextParamPainter = (ITextParamPainter) constr.newInstance();
			}
			else
				cacheTextParamPainter = (ITextParamPainter) factory.createByTypeName(typename);
			typenametextpainter = typename;
		}
	}


	protected void setPainterParams(IParamPainter painter, Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, Integer composite)
	{

		if (paintfill == null)
		{
			if (defStyle.getTypeFill() == CommonStyle.FILL_COLOR)
			{
				int color = defStyle.getColorFill();
				Paint сFillcolor = painter.getPaintFill();
				if (!(сFillcolor instanceof Color) || ((Color) сFillcolor).getRGB() != color)
					paintfill = new Color(color, true);
			}
			else if (defStyle.getTypeFill() == CommonStyle.FILL_IMG)
			{
				BufferedImage bi = (BufferedImage) defStyle.getTextureImg();
				if (bi != null)
					defPaint = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
			}
		}

		if (paintfill != null) //Здесь производим утсановку тогда когда это действительно требуется (мы проверили это на предедущем шаге)
			painter.setPaintFill(paintfill);

		Color cLinecolor = painter.getColorLine();
		if (cLinecolor == null || cLinecolor.getRGB() != linecolor)
			painter.setColorLine(new Color(linecolor, true));

		if (radPnt != null)
			painter.setSizePnt(radPnt);
		else
			painter.setSizePnt(defRadPoint);

		painter.setComposite(composite);
		painter.setStroke(stroke);
		painter.setScaledThickness(defStyle.isScaleLineThickness());
	}

	public boolean isVisibleLayer(ILayer lr, ILinearConverter converter)
	{
		IScaledConverterCtrl converter_p = null;
		if (converter instanceof IScaledConverterCtrl)
			converter_p = (IScaledConverterCtrl) converter;
		else if (converter instanceof IProjConverter)
			converter_p = ((IProjConverter) converter_p).getAsScaledConverterCtrl();
		else
			throw new UnsupportedOperationException("Unknown converter: " + converter.getClass().getCanonicalName());

		double scale = 0;
		try
		{
			MPoint unistOnPixel = converter_p.getUnitsOnPixel();
			scale = Math.min(unistOnPixel.x, unistOnPixel.y);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		double lowRange = defStyle.getScaleLow();
		double hiRange = defStyle.getScaleHi();

		if (hiRange < 0)
			hiRange = scale;
		if (lowRange < 0)
			lowRange = scale;
		return !(scale < lowRange || hiRange < scale);
	}

	protected String ruleName;

	public String getObjName()
	{
		return ruleName;
	}

	public Object[] init(Object... objs) throws Exception
	{

		final CommonStyle defStyle = getStyleByStroke(getDefaultStroke());

		initAll(defStyle, null, null,attrasname);//TODO передать еще конвертер имен 3-м параметром
		for (Object obj : objs)
			init(obj);


		//Из defStyle формируем stroke
		setExternalStroke = (defStroke != null);
		setExternalPaint = (defPaint != null);
		setExternalImgPoint=(defImgPoint != null);

		if (!setExternalStroke)
			defStroke=getStrokeByStyle(defStyle);


//		if (!setExternalPaint)
//		{ //Установливать сразу текстуру не будем поскольку это может замедлить начальную загрузку
//		}
		return null;
	}

	private BasicStroke getStrokeByStyle(CommonStyle defStyle)
	{
		BasicStroke rStroke=null;
		BasicStroke defstroke =rStroke= getDefaultStroke();//Здесь стили строка по умолчанию
		if (defStyle.getLinethickness() != defstroke.getLineWidth())
			rStroke = defstroke = new BasicStroke(defStyle.getLinethickness());

		if (defStyle.getEndCap() != defstroke.getEndCap() || defStyle.getLineJoin() != defstroke.getLineJoin())
			rStroke = defstroke = new BasicStroke(defstroke.getLineWidth(), defStyle.getEndCap(), defStyle.getLineJoin());

		if (defStyle.getMiterLimit() != defstroke.getMiterLimit())
			rStroke = defstroke = new BasicStroke(defstroke.getLineWidth(), defstroke.getEndCap(), defstroke.getLineJoin(), defstroke.getMiterLimit());

		if (defStyle.getDash() != null || defStyle.getDashphase() != defstroke.getDashPhase())
			rStroke = defstroke = new BasicStroke(defstroke.getLineWidth(), defstroke.getEndCap(), defstroke.getLineJoin(), defstroke.getMiterLimit(), defStyle.getDash(), defStyle.getDashphase());

		if (rStroke==null && defStyle.getLineStyle()!= CommonStyle.SOLID)
			rStroke = defstroke = new BasicStroke(defstroke.getLineWidth(), defstroke.getEndCap(), defstroke.getLineJoin(), defstroke.getMiterLimit(), CommonStyle.DOTTEDSTYLE, CommonStyle.DOTTEDPHASE);
		return rStroke;
	}

	private BasicStroke getDefaultStroke()
	{
		return new BasicStroke();//Здесь стили строка по умолчанию
	}

	protected CommonStyle getStyleByStroke(BasicStroke defstroke)
	{
		CommonStyle style = new CommonStyle();
//Копируем эти стили в style
		style.setLineWidth(defstroke.getLineWidth());
		style.setLineJoin(defstroke.getLineJoin());
		style.setMiterLimit(defstroke.getMiterLimit());
		style.setEndCap(defstroke.getEndCap());
		style.setDashArray(defstroke.getDashArray());
		style.setDashPhase(defstroke.getDashPhase());
		return style;
	}

	//TODO Добавить LineType - линия или кривая
	//При этом заметим, что данное правило применяется для отрисовки одного слоя
	//Т.е. еще раз слой это набор объектов с одинаковыми правилами рисования для всех объектов

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr = (IDefAttr) obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
			ruleName = (String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
			this.desc = (IXMLObjectDesc) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.RULE_TAGNAME))
			interceptor = (IDrawObjRule) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(CommonStyle.HI_RANGE))
			defStyle.setHiRange((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.LOW_RANGE))
			defStyle.setLowRange((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.COLOR_LINE))
			defStyle.setsHexColorLine((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.COLOR_FILL))
			defStyle.setsHexColorFill((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.TEXTURE_IMAGE_PATH))
			defStyle.setTextureImagePath((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.TEXTURE_RECT))
			defStyle.setTextureRect((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.POINT_IMAGE_PATH))
			defStyle.setPointImagePath((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.POINT_IMAGE_RECT))
			defStyle.setPointRect((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.POINT_IMAGE_СENTRAL))
			defStyle.setPointCentral((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.AUTO_IMAGE_СENTRAL))
		{
			try
			{
				defStyle.setAutocenral(Boolean.parseBoolean((String)attr.getValue()));
			}
			catch (Exception e)
			{//
			}
		}
		else if (attr.getName().equalsIgnoreCase(CommonStyle.LINE_STYLE))
			defStyle.setsHexLineStyle((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.SDOTTED))
			defStyle.setLineStyle(CommonStyle.DOTTED);
		else if (attr.getName().equalsIgnoreCase(CommonStyle.SSOLID))
			defStyle.setLineStyle(CommonStyle.SOLID);
		else if (attr.getName().equalsIgnoreCase(CommonStyle.RAD_PNT))
			defStyle.setsRadPnt((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.LINE_THICKNESS))
			defStyle.setsLineThickness((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase(CommonStyle.SCALE_LINE_THICKNESS))
		{
			String val=(String) attr.getValue();
			boolean sc= (val==null);
			if (!sc)
				try
				{
					sc=!val.equalsIgnoreCase("false") && (Integer.parseInt(val)!=0);
				}
				catch (NumberFormatException e)
				{
					sc=true;
				}
			defStyle.setScaleLineThickness(sc);
		}
		else if (attr.getName().equalsIgnoreCase(CommonStyle.END_CAP))
		{
			String sendCap = (String) attr.getValue();
			try
			{
				int endCap;
				if (sendCap.equals("CAP_BUTT"))
					endCap = BasicStroke.CAP_BUTT;
				else if (sendCap.equals("CAP_ROUND"))
					endCap = BasicStroke.CAP_ROUND;
				else if (sendCap.equals("CAP_SQUARE"))
					endCap = BasicStroke.CAP_SQUARE;
				else
					endCap = Integer.parseInt(sendCap);
				defStyle.setEndCap(endCap);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}

		else if (attr.getName().equalsIgnoreCase(CommonStyle.LINE_JOIN))
		{
			try
			{
				String slineJoin = (String) attr.getValue();
				int lineJoin;
				if (slineJoin.equals("JOIN_MITER"))
					lineJoin = BasicStroke.JOIN_MITER;
				if (slineJoin.equals("JOIN_ROUND"))
					lineJoin = BasicStroke.JOIN_ROUND;
				if (slineJoin.equals("JOIN_BEVEL"))
					lineJoin = BasicStroke.JOIN_BEVEL;
				else
					lineJoin = Integer.parseInt(slineJoin);
				defStyle.setEndCap(lineJoin);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		else if (attr.getName().equalsIgnoreCase(CommonStyle.MITER_LIMIT))
		{
			try
			{
				float fl = Float.parseFloat((String) attr.getValue());
				defStyle.setMiterLimit(fl);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		else if (attr.getName().equalsIgnoreCase(CommonStyle.DASH_ARRAY))
		{
			try
			{
				String dashstring = (String) attr.getValue();
				defStyle.setDashArray(getFloatArrayByObject(dashstring));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		else if (attr.getName().equalsIgnoreCase(CommonStyle.DASH_PHASE))
		{
			try
			{
				float fl = Float.parseFloat((String) attr.getValue());
				defStyle.setDashPhase(fl);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		else if (attr.getName().equalsIgnoreCase(TEXT_MODE))
		{
			try
			{
				setTextMode("true".equalsIgnoreCase((String) attr.getValue()));
			}
			catch (Exception e)
			{//
			}
		}
		else if (attr.getName().equalsIgnoreCase(FONT_SIZE))
			try
			{
				fontSize = Float.parseFloat((String) attr.getValue());
			}
			catch (NumberFormatException e)
			{//
			}
		else if (attr.getName().equalsIgnoreCase(FONT_NAME))
			fontname = (String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(FONT_STYLE))
		{
			String sStyle = (String) attr.getValue();
			Class fclass = Font.class;
			Field[] flds = fclass.getFields();
			for (Field fld : flds)
				if (fld.getName().equals(sStyle))
				{
					fstyle = fld.getInt(null);
					break;
				}
		}
		else if (attr.getName().equalsIgnoreCase(COMPOSITE))
		{
			String sComposite = (String) attr.getValue();

			Class comp = AlphaComposite.class;
			Field[] flds = comp.getFields();
			for (Field fld : flds)
				if (fld.getName().equals(sComposite))
				{
					defComposite = fld.getInt(null);
					break;
				}
		}
		else if (attr.getName().equalsIgnoreCase(STROKE))
			defStroke = (Stroke) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(PAINT))
			defPaint = (Paint) attr.getValue();

		else if (attr.getName().equalsIgnoreCase(IMGPOINT))
			defImgPoint = (Image) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(IMGCENTRALPOINT))
			defCentralPoint =(Point) attr.getValue();

		else if (attr.getName().equalsIgnoreCase(KernelConst.USE_AS_ATTRIBUTENAME))
			this.attrasname = (String) attr.getValue();
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		List<IParam> params = desc.getParams();
		for (IParam attr : params)
		{
			if (attr.getName().equalsIgnoreCase(CommonStyle.HI_RANGE))
				attr.setValue(defStyle.getScaleHiRange());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.LOW_RANGE))
				attr.setValue(defStyle.getScaleLowRange());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.COLOR_LINE))
				attr.setValue(defStyle.getsHexColorLine());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.COLOR_FILL))
				attr.setValue(defStyle.getsHexColorFill());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.LINE_STYLE))
				attr.setValue(defStyle.getsHexLineStyle());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.LINE_THICKNESS))
				attr.setValue(defStyle.getLinethickness());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.SCALE_LINE_THICKNESS))
				attr.setValue(defStyle.isScaleLineThickness());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.TEXTURE_IMAGE_PATH))
				attr.setValue(defStyle.getTextureImagePath());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.TEXTURE_RECT))
				attr.setValue(defStyle.getTextureRect());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.POINT_IMAGE_PATH))
				attr.setValue(defStyle.getPointImagePath());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.POINT_IMAGE_RECT))
				attr.setValue(defStyle.getPointRect());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.POINT_IMAGE_СENTRAL))
				attr.setValue(defStyle.getPointCentral());
			else if (attr.getName().equalsIgnoreCase(CommonStyle.AUTO_IMAGE_СENTRAL))
				attr.setValue(String.valueOf(defStyle.isAutocenral()));
			else if (attr.getName().equalsIgnoreCase(CommonStyle.END_CAP))
			{
				int endCap = defStyle.getEndCap();
				if (endCap == BasicStroke.CAP_BUTT)
					attr.setValue("CAP_BUTT");
				else if (endCap == BasicStroke.CAP_ROUND)
					attr.setValue("CAP_ROUND");
				else if (endCap == BasicStroke.CAP_SQUARE)
					attr.setValue("CAP_SQUARE");
				else
					attr.setValue(Integer.toString(endCap));
			}
			else if (attr.getName().equalsIgnoreCase(CommonStyle.LINE_JOIN))
			{
				int lineJoin = defStyle.getLineJoin();
				if (lineJoin == BasicStroke.JOIN_MITER)
					attr.setValue("JOIN_MITER");
				if (lineJoin == BasicStroke.JOIN_ROUND)
					attr.setValue("JOIN_ROUND");
				if (lineJoin == BasicStroke.JOIN_BEVEL)
					attr.setValue("JOIN_BEVEL");
				else
					attr.setValue(Integer.toString(lineJoin));
			}
			else if (attr.getName().equalsIgnoreCase(CommonStyle.MITER_LIMIT))
				attr.setValue(Float.toString(defStyle.getMiterLimit()));
			else if (attr.getName().equalsIgnoreCase(CommonStyle.RAD_PNT))
				attr.setValue(defStyle.getsRadPnt());
			else if (attr.getName().equalsIgnoreCase(KernelConst.USE_AS_ATTRIBUTENAME))
				attr.setValue((String) attr.getValue());
		}
		return desc;
	}
}

//--------------------------- trash ---------------------
//		else
//		{
//			if (dash!=null)
//			{
//				Stroke stroke = painter.getStroke();
//				if (stroke == null || (stroke instanceof BasicStroke))
//				{
//
//					final BasicStroke basicStroke = (BasicStroke) stroke;
//
//					final int endCap = basicStroke.getEndCap();
//					final int lineJoin = basicStroke.getLineJoin();
//					final float miterLimit = basicStroke.getMiterLimit();
//
//					new BasicStroke(linethickness, endCap, lineJoin, miterLimit,dash,dashphase);
//				}
//			}
//			else
//			{
//				Stroke stroke = painter.getStroke();
//				if (stroke == null || (stroke instanceof BasicStroke && ((BasicStroke) stroke).getLineWidth() != linethickness))
//					painter.setStroke(new BasicStroke(linethickness));
//			}
//		}
