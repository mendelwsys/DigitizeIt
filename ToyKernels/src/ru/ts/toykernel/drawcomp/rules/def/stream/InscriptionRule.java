package ru.ts.toykernel.drawcomp.rules.def.stream;

import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;
import ru.ts.toykernel.drawcomp.ITextParamPainter;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.painters.def.DefInscriptPainter;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 15.03.2009
 * Time: 18:49:01
 * Для рисования надписей методом замещения
 */
public class InscriptionRule extends CnSerialDrawRule
{
	public static final String RULETYPENAME ="INCRIPT_RL";


	public InscriptionRule()
	{
		textMode=true;
	}

	public InscriptionRule(CommonStyle defStyle, Font ft)
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
		if (cacheTextParamPainter == null || !(cacheTextParamPainter instanceof DefInscriptPainter))
		{
			getInstanceTextPainter(paintersClass.get(point_text), point_text, DefInscriptPainter.class);
			rv=cacheTextParamPainter;
			//cacheTextParamPainter=rv=new DefInscriptPainter();
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
