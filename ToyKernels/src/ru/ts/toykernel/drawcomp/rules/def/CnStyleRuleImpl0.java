package ru.ts.toykernel.drawcomp.rules.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.drawcomp.painters.def.*;
import ru.ts.toykernel.drawcomp.*;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.converters.IScaledConverterCtrl;
import ru.ts.factory.IObjectDesc;
import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 02.03.2009
 * Time: 15:03:50
 * Rule implementation base on common style
 */
public class CnStyleRuleImpl0 implements IDrawObjRule
{
	protected String point_text;
	protected String poly_text;
	protected String line_text;

	protected String POINT;
	protected String POLY ;
	protected String LINE;


	protected String attrasname = KernelConst.ATTR_CURVE_NAME;

	public static final String RULETYPENAME ="CS_RL0";
	public String getRuleType()
	{
		return RULETYPENAME;
	}

	protected INameConverter storNm2attrNm;

	public INameConverter getStorNm2attrNm()
	{
		return storNm2attrNm;
	}

	public void setStorNm2attrNm(INameConverter storNm2attrNm)
	{
		this.storNm2attrNm = storNm2attrNm;
	}

	public Map<String, Class> getPaintersClass()
	{
		return paintersClass;
	}


	public void setPaintersClass(Map<String, Class> paintersClass)
	{
		this.paintersClass = paintersClass;
		resetCache();
	}

	public void resetCache()
	{
		cacheTextParamPainter = null;
		cacheParamPainter = null;
	}


	protected IDrawObjRule interceptor;

	public void resetPainters()
	{
		resetCache();
	}

	public IDrawObjRule setInterceptor(IDrawObjRule interseptor)
	{

		IDrawObjRule rv = this.interceptor;
		this.interceptor=interseptor;
		return rv;
	}

	public IDrawObjRule getInterceptor()
	{
		return interceptor;
	}
	
	public void setClassPointTextPainter(Class aClass) throws Exception
	{
		paintersClass.put(point_text, aClass);
		resetCache();
	}

	public void setClassPointPainter(Class aClass) throws Exception
	{
		paintersClass.put(POINT, aClass);
		resetCache();
	}


	public void setClassLineTextPainter(Class aClass) throws Exception
	{
		paintersClass.put(line_text, aClass);
		resetCache();
	}

	public void setClassLinePainter(Class aClass) throws Exception
	{
		paintersClass.put(LINE, aClass);
		resetCache();
	}


	public void setClassPolyTextPainter(Class aClass) throws Exception
	{
		paintersClass.put(poly_text, aClass);
		resetCache();
	}

	public void setClassPolyPainter(Class aClass) throws Exception
	{
		paintersClass.put(POLY, aClass);
		resetCache();
	}


	public Class getClassPointTextPainter()
	{
		return paintersClass.get(point_text);
	}

	public Class getClassPointPainter()
	{
		return paintersClass.get(POINT);
	}


	public Class getClassLineTextPainter()
	{
		return paintersClass.get(line_text);
	}

	public Class getClassLinePainter()
	{
		return paintersClass.get(LINE);
	}

	public Class getClassPolyTextPainter()
	{
		return paintersClass.get(poly_text);
	}

	public Class getClassPolyPainter()
	{
		return paintersClass.get(POLY);
	}


	protected Map<String, Class> paintersClass = new HashMap<String, Class>();


	protected CnStyleRuleImpl0()
	{
	}

	public CnStyleRuleImpl0(CommonStyle defStyle, Font ft)
	{
		this(defStyle,ft,null,null);
	}

	public CnStyleRuleImpl0(CommonStyle defStyle, Font ft,Map<String, Class> paintersClass, INameConverter nameConverter)
	{
		this(defStyle,paintersClass, nameConverter);
		this.ft = ft;
		this.textMode = true;
	}

	public CnStyleRuleImpl0(CommonStyle defStyle)
	{
		this(defStyle,(Map<String, Class> )null,null);
	}

	public CnStyleRuleImpl0(CommonStyle defStyle,Map<String, Class> paintersClass, INameConverter storNm2attrNm)
	{
		initAll(defStyle, paintersClass, storNm2attrNm);
	}

	protected void initAll(CommonStyle defStyle, Map<String, Class> paintersClass, INameConverter storNm2attrNm)
	{
		this.defStyle = defStyle;
		if (paintersClass!=null)
			this.paintersClass=paintersClass;
		if (storNm2attrNm==null)
			storNm2attrNm=new DefNameConverter();
		this.storNm2attrNm=storNm2attrNm;

		point_text =storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.POINT_TEXT);
		line_text =storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.LINE_TEXT);
		poly_text =storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.POLY_TEXT);

		POINT =storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.POINT);
		LINE =storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.LINESTRING);
		POLY =storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.LINEARRING);
	}

	protected boolean textMode = false;
	protected Font ft;

	public CommonStyle getDefStyle()
	{
		return defStyle;
	}

	protected CommonStyle defStyle;//Style of layer
	protected IParamPainter cacheParamPainter; //Point painter
	protected ITextParamPainter cacheTextParamPainter;//Text painter for all objects

	public IPainter cretatePainter(Graphics g, ILayer layer, IBaseGisObject obj) throws Exception
	{
		if (textMode && ft == null)
			ft = g.getFont();

		Integer fillcolor = null;
		Integer linecolor = null;
		Integer linethickness = null;


		IAttrs curveattrs = obj.getObjAttrs();

		IDefAttr iDefAttr = null;
		if (curveattrs!=null)
		{
			iDefAttr = curveattrs.get(storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.ATTR_COLOR_FILL));
			if (iDefAttr == null)
				fillcolor = (Integer) iDefAttr.getValue();
			iDefAttr = curveattrs.get(storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.ATTR_COLOR_LINE));
			if (iDefAttr == null)
				linecolor = (Integer) iDefAttr.getValue();

			iDefAttr = curveattrs.get(storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.ATTR_LINE_THICKNESS));
			if (iDefAttr != null)
				linethickness = (Integer) iDefAttr.getValue();
		}
		if (fillcolor == null)
			fillcolor = defStyle.getColorFill();
		if (linecolor == null)
			linecolor = defStyle.getColorLine();
		if (linethickness == null)
			linethickness = (int)defStyle.getLinethickness();


		String geotype = obj.getGeotype();
		if (geotype.equals(POLY))
			lineRingPainter(fillcolor, linecolor, linethickness, obj);
		else if (geotype.equals(LINE))
			lineStringPainter(fillcolor, linecolor, linethickness, obj);
		else if (geotype.equals(POINT))
			pointPainter(fillcolor, linecolor, linethickness, obj);
		else
		{
			throw new Exception("Unsupported type object:" + geotype);
		}

		if (textMode)
		{
			if (curveattrs!=null)
			{
				String text = null;
				iDefAttr = curveattrs.get(storNm2attrNm.codeAttrNm2StorAttrNm(attrasname));
				if (iDefAttr != null)
					text = (String) iDefAttr.getValue();
				cacheTextParamPainter.setText(text);
			}
			return cacheTextParamPainter;
		} else
			return cacheParamPainter;
	}

	protected void lineRingPainter(Integer fillcolor, Integer linecolor, Integer linethickness, IBaseGisObject obj)
			throws Exception
	{
		IParamPainter rv = null;
		if (textMode)
		{
			getInstanceTextPainter(paintersClass.get(poly_text), DefPolyTextPainter.class);
			rv = cacheTextParamPainter;
			if (cacheTextParamPainter.getFont() == null)
				cacheTextParamPainter.setFont(new Font(ft.getName(), Font.BOLD, 3 * ft.getSize() / 4));
		} else
		{
			getInstancePainter(paintersClass.get(POLY), DefPolyPainter.class);
			rv = cacheParamPainter;
		}
		setColorPainter(rv, fillcolor, linecolor, linethickness);
	}

	protected void lineStringPainter(Integer fillcolor, Integer linecolor, Integer linethickness, IBaseGisObject obj)
			throws Exception
	{
		IParamPainter rv = null;
		if (textMode)
		{
			getInstanceTextPainter(paintersClass.get(line_text), DefLineTextPainter.class);
			rv = cacheTextParamPainter;
			if (cacheTextParamPainter.getFont() == null)
				cacheTextParamPainter.setFont(new Font(ft.getName(), Font.BOLD, ft.getSize()));
		}
		else
		{
			getInstancePainter(paintersClass.get(LINE), DefLinePainter.class);
			rv = cacheParamPainter;
		}
		setColorPainter(rv, fillcolor, linecolor, linethickness);
	}

	protected void pointPainter(Integer fillcolor, Integer linecolor, Integer linethickness, IBaseGisObject obj) throws Exception
	{
		IParamPainter rv = null;
		if (textMode)
		{
			getInstanceTextPainter(paintersClass.get(point_text), DefPointTextPainter.class);
			rv = cacheTextParamPainter;
			if (cacheTextParamPainter.getFont() == null)
				cacheTextParamPainter.setFont(new Font(ft.getName(), Font.BOLD, 3 * ft.getSize() / 4));
		} else
		{
			getInstancePainter(paintersClass.get(POINT), DefPointPainter.class);
			rv = cacheParamPainter;
		}
		setColorPainter(rv, fillcolor, linecolor, linethickness);
	}

	protected void getInstancePainter(Class aClass, Class defPainter)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		if (cacheParamPainter == null)
		{
			if (aClass == null)
				aClass = defPainter;

			Constructor constr = aClass.getConstructor();
			cacheParamPainter = (IParamPainter) constr.newInstance();
		}
	}

	protected void getInstanceTextPainter(Class aClass, Class defPainter)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		if (cacheTextParamPainter == null)
		{
			if (aClass == null)
				aClass = defPainter;

			Constructor constr = aClass.getConstructor();
			cacheTextParamPainter = (ITextParamPainter) constr.newInstance();
		}
	}


	private void setColorPainter(IParamPainter painter, Integer fillcolor, Integer linecolor, Integer linethickness)
	{
		Paint сFillcolor = painter.getPaintFill();
		if (сFillcolor == null || (сFillcolor instanceof  Color) || ((Color)сFillcolor).getRGB() != fillcolor)
			painter.setPaintFill(new Color(fillcolor, true));
		Color cLinecolor = painter.getColorLine();
		if (cLinecolor == null || cLinecolor.getRGB() != linecolor)
			painter.setColorLine(new Color(linecolor,true));
		Stroke stroke = painter.getStroke();
		if (stroke == null || (stroke instanceof BasicStroke && ((BasicStroke) stroke).getLineWidth() != linethickness))
			painter.setStroke(new BasicStroke(linethickness));
	}

	public boolean isVisibleLayer(ILayer lr, ILinearConverter converter)
	{
		IScaledConverterCtrl converter_p=null;
		if (converter instanceof IScaledConverterCtrl)
			converter_p=(IScaledConverterCtrl)converter;
		else if (converter instanceof IProjConverter)
			converter_p=((IProjConverter)converter_p).getAsScaledConverterCtrl();
		else
			throw new UnsupportedOperationException("Unknown converter: "+converter.getClass().getCanonicalName());

		double scale = 0;
		try
		{
			scale = Math.min(converter_p.getUnitsOnPixel().x,converter_p.getUnitsOnPixel().y);
		} catch (Exception e)
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

	public String getObjName()
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Object[] init(Object... objs) throws Exception
	{
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}