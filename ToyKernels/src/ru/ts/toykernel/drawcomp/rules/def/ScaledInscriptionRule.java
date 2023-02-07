package ru.ts.toykernel.drawcomp.rules.def;

import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.ITextParamPainter;
import ru.ts.toykernel.drawcomp.painters.def.DefScaledInscriptPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.converters.ILinearConverter;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: HP
 * Date: 25.02.2012
 * Time: 19:30:15
 * Scaled text rule wich uses DefScaledInscriptPainter for drawing scaled text
 * ru.ts.toykernel.drawcomp.rules.def.ScaledInscriptionRule
 */
public class ScaledInscriptionRule
		extends CnStyleRuleImpl
{
	public static final String RULETYPENAME ="TXT_RL";


	public ScaledInscriptionRule()
	{
		textMode=true;
	}

	public ScaledInscriptionRule(CommonStyle defStyle, Font ft)
	{
		super(defStyle, ft);
	}

	public String getRuleType()
	{
		return RULETYPENAME;
	}

	protected void pointPainter(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer lr, IBaseGisObject obj, Integer composite, Image pointImg, Point central) throws Exception
	{
		ITextParamPainter rv;
		if (cacheTextParamPainter == null || !(cacheTextParamPainter instanceof DefScaledInscriptPainter))
		{
			getInstanceTextPainter(paintersClass.get(point_text), point_text, DefScaledInscriptPainter.class);
			rv=cacheTextParamPainter;
		}
		else
			rv=cacheTextParamPainter;

		IDefAttr defName = null;
		if (attrasname!=null)
			defName = obj.getObjAttrs().get((nameConverter.codeAttrNm2StorAttrNm(attrasname)));
		if (defName!=null && defName.getValue()!=null)
		{
			if (rv.getFont() == null)
				rv.setFont(getFont());
			rv.setText(defName.getValue().toString());
			setPainterParams(rv, paintfill, linecolor, stroke, radPnt, composite);
		}
	}

	public boolean isVisibleLayer(ILayer lr, ILinearConverter converter)
	{
		cacheTextParamPainter=null;//сброс перед началом рисования обязателен, поскольку рисователь накапливает информацию о нарисованных объектах
		return super.isVisibleLayer(lr, converter);
	}
}
