package ru.ts.toykernel.drawcomp.rules.def.stream;

import ru.ts.toykernel.drawcomp.rules.def.CnStyleRuleImpl;
import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.IParamPainter;
import ru.ts.stream.ISerializer;
import ru.ts.factory.IFactory;
import ru.ts.toykernel.consts.INameConverter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.Map;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 09.03.2009
 * Time: 18:50:16
 * Draw rule
 * ru.ts.toykernel.drawcomp.rules.def.stream.CnSerialDrawRule
 */
public class CnSerialDrawRule extends CnStyleRuleImpl 
		implements IDrawObjRule, ISerializer
{

	public CnSerialDrawRule()
	{
	}


	public CnSerialDrawRule(CommonStyle defStyle, Map<String, IFactory<IParamPainter>> paintersFactory, INameConverter nameConverter)
	{
		super(defStyle, paintersFactory, nameConverter,null);
	}

	public CnSerialDrawRule(CommonStyle defStyle, Font ft, Map<String, IFactory<IParamPainter>> paintersClass, INameConverter nameConverter)
	{
		super(defStyle, ft, paintersClass, nameConverter,null);
	}

	public CnSerialDrawRule(CommonStyle defStyle, Map<String, IFactory<IParamPainter>> paintersFactory, INameConverter nameConverter,String attrasname)
	{
		super(defStyle, paintersFactory, nameConverter,attrasname);
	}

	public CnSerialDrawRule(CommonStyle defStyle, Font ft,String attrasname)
	{
		super(defStyle, ft,attrasname);
	}

	public CnSerialDrawRule(CommonStyle defStyle, Font ft)
	{
		super(defStyle, ft);
	}

	public CnSerialDrawRule(CommonStyle defStyle)
	{
		super(defStyle);
	}

	public void saveToStream(DataOutputStream dos) throws IOException
	{
		//TODO Здесь по большому счету надо бы сохранять объекты paintersClass
		dos.writeUTF(getRuleType());
		defStyle.savetoStream(dos);
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		//TODO А Здесь пнадо бы загружать объекты из paintersClass
		initAll(CommonStyle.loadFromStream(dis),(Map<String, IFactory<IParamPainter>>)null,null,null);
	}
}
