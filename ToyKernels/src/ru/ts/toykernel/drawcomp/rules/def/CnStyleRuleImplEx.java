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
import ru.ts.factory.DefIFactory;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.converters.IScaledConverterCtrl;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.gisutils.algs.common.MPoint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.lang.reflect.Field;

/**
 * Rule implementation base on common style
 * ru.ts.toykernel.drawcomp.rules.def.CnStyleRuleImpl
 */
public class CnStyleRuleImplEx
		extends DefIFactory<IParamPainter>
		implements IDrawObjRule

	//Цель реоргангизации: класс по умолчанию должен переопределяться одним методом!!!
{
	public static final String RULETYPENAME = "CS_RLEX";

	protected String attrAsName =KernelConst.ATTR_CURVE_NAME;//the name of attribute which text value will be draw

	protected String point_text;
	protected String poly_text;
	protected String line_text;

	protected String point;
	protected String line;
	protected String poly;
	protected String polyh;


	protected String ellips;
	protected String ellipsf;

	protected String rect;
	protected String rectf;

	protected Map<String, String> t2refT = new HashMap<String,String>();//type mapping
	protected boolean refMode = false;
	//Если установлен этот флаг, объекты рисуются паинтерами типов получеными так :geotype=t2refT.get(geotype)
	//по умолчанию это отображение объекта -> в объект с заголовком.
	protected Font ft;
	protected Float fontSize = (float) 12;
	protected String fontname = "sunserif";
	protected Integer fstyle = Font.BOLD;
	protected Integer defRadPoint=0;


	protected IXMLObjectDesc desc;
	protected INameConverter nameConverter;


	public static final String REF_MODE = "RefMode";
	public static final String FONT_SIZE = "fontSize";
	public static final String FONT_NAME = "fontName";
	public static final String FONT_STYLE = "fontStyle";
	public static final String COMPOSITE = "Composite";
	public static final String STROKE = "stroke";

	public static final String PAINT = "paint";

	public static final String IMGPOINT = "imgpoint";
	public static final String IMGCENTRALPOINT = "imgcentralpoint";
    public static final String FLOATSEP = ";";

//object type to paramter painter
	public static final String PTYPE = "PTYPE";
	public static final String PAINTER = "PAINTER";

	


	protected Map<String,List<IParam>> globalsParam = new HashMap<String,List<IParam>>(); //global params of the rule (ready for use for painter incarnation )
	protected Map<String,Map<String,List<IParam> >> otype2ParamPainters=new HashMap<String,Map<String,List<IParam> >>(); //painters params for individual painter



	public String getRuleType()
	{
		return RULETYPENAME;
	}

	public INameConverter getNameConverter()
	{
		if (nameConverter == null)
			nameConverter = new DefNameConverter();
		return nameConverter;
	}

	public void setNameConverter(INameConverter nameConverter)
	{
		if (nameConverter == null)
			this.nameConverter = new DefNameConverter();
		else
			this.nameConverter=nameConverter;
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
		paintersClass.put(ellips, factory);
		resetCache();
	}

	public void setEllipsfFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(ellipsf, factory);
		resetCache();
	}


	public void setRectFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(rect, factory);
		resetCache();
	}

	public void setRectfFactory(IFactory<IParamPainter> factory) throws Exception
	{
		paintersClass.put(rectf, factory);
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
		return paintersClass.get(ellips);
	}

	public IFactory<IParamPainter> getEllipsfPainterFactory()
	{
		return paintersClass.get(ellipsf);
	}


	public IFactory<IParamPainter> getRectPainterFactory()
	{
		return paintersClass.get(rect);
	}

	public IFactory<IParamPainter> getRectfPainterFactory()
	{
		return paintersClass.get(rectf);
	}

	protected Map<String, IFactory<IParamPainter>> paintersClass = new HashMap<String, IFactory<IParamPainter>>();



	public CnStyleRuleImplEx()
	{
	}


	public CnStyleRuleImplEx(CommonStyle defStyle)
	{
		this(defStyle, (Map<String, IFactory<IParamPainter>>) null, null);
	}

	public CnStyleRuleImplEx(CommonStyle defStyle,String attrAsName)
	{
		this(defStyle, (Map<String, IFactory<IParamPainter>>) null, null, attrAsName);
	}

	public CnStyleRuleImplEx(CommonStyle defStyle, Font ft)
	{
		this(defStyle, ft, null, null);
	}

	public CnStyleRuleImplEx(CommonStyle defStyle, Font ft,String attrAsName)
	{
		this(defStyle, ft, null, null, attrAsName);
	}

	public CnStyleRuleImplEx(CommonStyle defStyle, Font ft, Map<String, IFactory<IParamPainter>> paintersClass, INameConverter nameConverter)
	{
		this(defStyle, paintersClass, nameConverter);
		this.ft = ft;
	}

	public CnStyleRuleImplEx(CommonStyle defStyle, Font ft, Map<String, IFactory<IParamPainter>> paintersClass, INameConverter nameConverter,String attrAsName)
	{
		this(defStyle, paintersClass, nameConverter);
		this.attrAsName = attrAsName;
		this.ft = ft;
	}

	public CnStyleRuleImplEx(CommonStyle defStyle, Map<String, IFactory<IParamPainter>> paintersFactory, INameConverter nameConverter)
	{
		initAll(defStyle, paintersFactory, nameConverter, attrAsName);
	}

	public CnStyleRuleImplEx(CommonStyle defStyle, Map<String, IFactory<IParamPainter>> paintersFactory, INameConverter nameConverter,String attrAsName)
	{
		initAll(defStyle, paintersFactory, nameConverter, attrAsName);
	}


	public boolean isRefMode()
	{
		return refMode;
	}

	public void setRefMode(boolean refMode)
	{
		this.refMode = refMode;
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
		this.setNameConverter(nameConverter);
		final INameConverter l_nameConverter = getNameConverter();

		this.attrAsName =attrasname;
		this.defStyle = defStyle;
		if (paintersFactory != null)
			this.paintersClass = paintersFactory;

		point_text = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.POINT_TEXT);
		line_text = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.LINE_TEXT);
		poly_text = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.POLY_TEXT);

		point = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.POINT);
		line = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.LINESTRING);
		poly = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.LINEARRING);
		polyh = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.LINEARRINGH);

		setType2TextType();


		ellips = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ELLIPS);
		ellipsf = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ELLIPSF);

		rect = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.RECT);
		rectf = l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.RECTF);


	}

	/**
	 * Setting type to text type reflection
	 * when rule in textmode it get painter type from this reflection
	 */
	protected void setType2TextType()
	{
		t2refT.put(point,point_text);
		t2refT.put(line,line_text);
		t2refT.put(poly,poly_text);
		t2refT.put(polyh,poly_text);
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

	protected Stroke defStroke;//Stroke Если он установлен тогда использовать его
	protected boolean setExternalStroke = false;//флаг показывает что Stroke был установлен целиком а не через аттрибуты
	//в этом случае классические аттрибуты не анализируются

	protected Paint defPaint;//painter Если есть он устанаваливается и рисуется с помощью него
	//1. Паинтер это цвет (через комон стайл)
	//2. Паинтер содержит тектстуру
	protected boolean setExternalPaint = false;//флаг показывает что Paint был установлен целиком а не через аттрибуты


	protected Image defImgPoint;//Изображение для точки по умолчанию
	protected Point defCentralPoint;//Центральная точка изображения


	protected boolean setExternalImgPoint = false;//флаг показывает что ImgPoint был установлен целиком а не через аттрибуты
	//TODO На самом деле достаточно передавать только этот флаг, для того что бы система попыталась получить параметры рисования из объекта
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

		if (getFont() == null)
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
		final INameConverter l_nameConverter = getNameConverter();
		if (curveattrs != null)
		{

			iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_COLOR_LINE));
			if (iDefAttr != null)
				linecolor = (Integer) iDefAttr.getValue();


			iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_RADPNT));
			if (iDefAttr != null)
				radpnt = (Integer) iDefAttr.getValue();

			iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_COMPOSITE));
			if (iDefAttr != null)
				composite = (Integer) iDefAttr.getValue();

			if (!setExternalPaint)
			{
				iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_COLOR_FILL));
				if (iDefAttr != null)
				{
					Integer color = (Integer) iDefAttr.getValue();
					paintfill = new Color(color, true);
				}
				else
				{
					iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_IMG_FILL));
					if (iDefAttr != null)
					{
						BufferedImage bi = (BufferedImage) iDefAttr.getValue();
						paintfill = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
					}
				}
			}

			if (!setExternalImgPoint)
			{
					iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_IMG_POINT));
					if (iDefAttr != null)
						imgPoint = (BufferedImage) iDefAttr.getValue();
					iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_IMG_CENTRALPOINT));
					if (iDefAttr != null)
						centralImgPoint = (Point) iDefAttr.getValue();
			}

			if (!setExternalStroke)
			{
				BasicStroke newstroke = null;
				iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_LINE_THICKNESS));
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

				iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_LINE_CAP));
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

				iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_LINE_JOIN));
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

				iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_MITER_LIMIT));
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

				iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_DASHARRAY));
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

				iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(KernelConst.ATTR_STROKE_DASHPHASE));
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


		initPainter(paintfill, linecolor, stroke, radpnt, layer, obj, composite,imgPoint,centralImgPoint);
		if (cacheParamPainter instanceof ITextParamPainter)
		{
			ITextParamPainter cacheTextParamPainter= (ITextParamPainter) cacheParamPainter;
			if (curveattrs != null)
			{
				String text = null;
				if (attrAsName !=null)
					iDefAttr = curveattrs.get(l_nameConverter.codeAttrNm2StorAttrNm(attrAsName));
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

	/**
	 * painter of other type then describe in KernelConst list
	 * @param paintfill - fill object
	 * @param linecolor - color of line
	 * @param stroke - line stroke
	 * @param radPnt - painter radius
	 * @param layer - current draw layer
	 * @param obj - object for painter
	 * @param composite - composit identifer @throws Exception -
	 * @param imgPoint - the image for the point
	 * @param imageAdjust - central point of image
	 * @throws Exception -
	 */
	protected void initPainter(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer layer, IBaseGisObject obj, Integer composite, Image imgPoint, Point imageAdjust)
			throws Exception
	{
		IParamPainter rv = null;
		String geotype=obj.getGeotype();
		if (refMode)
		{
			String textTypename=this.t2refT.get(geotype);
			if (textTypename!=null)
				geotype=textTypename;
		}

		{
			getInstancePainter(paintersClass.get(geotype), geotype);
			if (cacheParamPainter instanceof ITextParamPainter)
			{
				ITextParamPainter cacheTextParamPainter=(ITextParamPainter)cacheParamPainter;
				if (cacheTextParamPainter.getFont() == null)
					cacheTextParamPainter.setFont(getFont());
			}
			rv = cacheParamPainter;
		}
//Установка параметров которые могут зависеть от объекта
		setPainterParams(rv, paintfill, linecolor, stroke, radPnt, composite,imgPoint,imageAdjust);

	}

	protected void getInstancePainter(IFactory<IParamPainter> factory, String typename)
			throws Exception
	{
		if (cacheParamPainter == null || !typename.equals(typenamepainter))
		{
			if (factory == null)
				cacheParamPainter=this.createByTypeName(typename);
			else
				cacheParamPainter = factory.createByTypeName(typename);
			typenamepainter = typename;
		}
	}

	protected void setPainterParams(IParamPainter painter, Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, Integer composite, Image imgPoint, Point imageAdjust)
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

		if (paintfill != null) //Здесь производим установку тогда, когда это действительно требуется (мы проверили это на предедущем шаге)
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
		if (painter instanceof DefPointPainter)
		{
			if (imgPoint==null)
			{  	//TODO Здесь проблема может отсложняться тем, что у нас здесь еще defStyle, вообщем -то хорошо бы от него избавиться
				//TODO т.е порядок такой - сначала параметр, потом аттрибут (это мы все едим), потом defStyle, я полагаю
				//TODO убрать !!!defStyle!!!
				/* TODO (Запись сделана 09042012)
					Мы не будем больше редактировать по !!defStyle!!! мы будем редактировать по списку параметров, и параметров типов паинтеров
					таких которые передаются в xml.

					Т.о. для заершения работ по преобразованию правил отображения необходимо

					1. Написать модуль редактирования который позволяет редактировать параметры правил.
					2. Реорганизовать это правило удалив из него ссылки на defStyle
				 */
				imgPoint=defStyle.getPointImg();
				imageAdjust=defStyle.getPointCentralAsPoint();
				if (imgPoint!=null && imageAdjust==null && defStyle.isAutocenral() )
				{
					int w=imgPoint.getWidth(null);
					int h=imgPoint.getHeight(null);
					imageAdjust= new Point(w/2,h/2);
				}
			}
			((DefPointPainter)painter).setPntImage(imgPoint, imageAdjust);
		}

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

		initAll(defStyle, null, null, attrAsName);//TODO передать еще конвертер имен 3-м параметром
		for (Object obj : objs)
			init(obj);


		//Из defStyle формируем stroke
		setExternalStroke = (defStroke != null);
		setExternalPaint = (defPaint != null);
		setExternalImgPoint=(defImgPoint != null);

		if (!setExternalStroke)
			defStroke=getStrokeByStyle(defStyle);

		List<IParam> params = desc.getParams();
		for (IParam param : params)
		{
			String pname = param.getName();
			if (!pname.equalsIgnoreCase(KernelConst.OBJNAME) &&
				!pname.equalsIgnoreCase(KernelConst.DESCRIPTOR)
				)
			{
				List<IParam> pparams = globalsParam.get(pname.toUpperCase());
				if (pparams==null)
					globalsParam.put(pname.toUpperCase(),pparams= new LinkedList<IParam>());
				pparams.add(param);
			}
		}
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

	private CommonStyle getStyleByStroke(BasicStroke defstroke)
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

	//При этом заметим, что данное правило применяется для отрисовки одного слоя
	//Т.е. еще раз слой это набор объектов с одинаковыми правилами рисования для всех объектов
	public Object init(Object obj) throws Exception
	{
		IDefAttr attr = (IDefAttr) obj;

		if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
			ruleName = (String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
			this.desc = (IXMLObjectDesc) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.NAMECONVERTER_TAGNAME))
			this.nameConverter= (INameConverter) attr.getValue();
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
			defStyle.setAutocenral(Boolean.parseBoolean((String)attr.getValue()));
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
		else if (attr.getName().equalsIgnoreCase(REF_MODE))
		{
			try
			{
				setRefMode("true".equalsIgnoreCase((String) attr.getValue()));
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
			this.attrAsName = (String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(PAINTER) || attr.getName().equals(""))
		{
			List<IParam> lst = attr.getAttributes();
			Map<String,List<IParam> > nm2param=new HashMap<String,List<IParam>>();
			if (lst!=null  && lst.size()>0)
			{
				for (IParam iParam : lst)
				{
					String paramname = iParam.getName().toUpperCase();
					List<IParam> params=nm2param.get(paramname);
					if (params==null)
						nm2param.put(paramname.toUpperCase(),params=new LinkedList<IParam>());
					//It is very important we have case insensitive  parameters name
					//for increase search speed
					params.add(iParam);
				}

				List<IParam> otype=nm2param.get(PTYPE);
				if (otype!=null && otype.size()==1)
				{
					String typename = (String) otype.get(0).getValue();
					if (typename!=null)
						otype2ParamPainters.put(typename,nm2param);
				}
			}
		}
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
//-------------------------------------- Default painter classes ------------------------------------------------------//
	protected Class getDefPolyTextPainter()
	{
		return DefPolyTextPainter.class;
	}

	protected Class getDefLineTextPainter()
	{
		return DefLineTextPainter.class;
	}

	protected Class getDefPointTextPainter()
	{
		return DefPointTextPainter.class;
	}


	protected Class getDefPolyPainter()
	{
		return DefPolyPainter.class;
	}

	protected Class getDefPolyHPainter()
	{
		return DefPolyHPainter.class;
	}

	protected Class getDefLinePainter()
	{
		return DefLinePainter.class;
	}

	protected Class getDefPointPainter()
	{
		return DefPointPainter.class;
	}

	protected Class getDefOvalPainter()
	{
		return DefOvalPainter.class;
	}

	protected Class getDefOtherPainter(String otype)
	{
		return null;
	}


	public IParamPainter createByTypeName(String otype) throws Exception
	{
		IParamPainter rv=super.createByTypeName(otype);
		if (rv!=null)
			return rv;

		Class cl;
		if (otype.equals(poly_text))
			cl=getDefPointTextPainter();
		else if (otype.equals(line_text))
			cl=getDefLineTextPainter();
		else if (otype.equals(point_text))
			cl=getDefPointTextPainter();
		else if (otype.equals(poly))
			cl=getDefPolyPainter();
		else if (otype.equals(polyh))
			cl=getDefPolyHPainter();
		else if (otype.equals(line))
			cl=getDefLinePainter();
		else if (otype.equals(point))
			cl = getDefPointPainter();
		else if (
				otype.equals(ellips)
						||
						otype.equals(ellipsf)
						||
						otype.equals(rect)
						||
						otype.equals(rectf)
				)
			cl = getDefOvalPainter();
		else
			cl = getDefOtherPainter(otype);
		if (cl==null)
			throw new Exception("Unknown default type");

		rv=(IParamPainter)cl.newInstance();


		Map<String, List<IParam>> params= globalsParam;

		INameConverter typenameConverter=getNameConverter();
		if (otype2ParamPainters.containsKey(otype))
		{
			params=new HashMap<String, List<IParam> >(globalsParam);
			params.putAll(otype2ParamPainters.get(otype));

			List<IParam> nconverters = otype2ParamPainters.get(otype).get(KernelConst.NAMECONVERTER_TAGNAME);
			if (nconverters!=null && nconverters.size()>0)
				typenameConverter=(INameConverter)nconverters.get(nconverters.size()-1).getValue();//get converter (last one from list)
		}

		List<IParam> resParamList =  new LinkedList<IParam>();
		for (List<IParam> paramList : params.values())
			resParamList.addAll(paramList);

		rv.setNameConverter(typenameConverter);//need to set converter for further paramters treatment
		rv.init(resParamList.toArray(new IParam[resParamList.size()]));

		return rv;
	}
}