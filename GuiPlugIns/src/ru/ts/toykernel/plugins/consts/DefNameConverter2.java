package ru.ts.toykernel.plugins.consts;

import com.mwlib.utils.Enc;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.consts.KernelConst;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 07.03.2009
 * Time: 19:18:57
 * Extends known attribute visualization (TODO Каждый модуль будет предоставлять эту конвертацию)
 */
public class DefNameConverter2 extends DefNameConverter
{
	public static final String LAYERNAMEHNAME = Enc.get("$64");
	public  static final String VISIBLEHNAME = Enc.get("$65");

	public  static final String ATTRIBUTEHNAME = Enc.get("$66");
	public  static final String ATTRIMGNAME = Enc.get("$67");

	public String codeAttrNm2ViewNm(String attrName)
	{
		if (attrName.equals(KernelConst.ATTR_CURVE_NAME))
			return ATTRIBUTEHNAME;
		if (attrName.equals(KernelConst.ATTR_IMG_REF))
			return ATTRIMGNAME;
		if (attrName.equals(KernelConst.LAYER_NAME))
			return LAYERNAMEHNAME;
		if (attrName.equals(KernelConst.LAYER_VISIBLE))
			return VISIBLEHNAME;
		return super.codeAttrNm2ViewNm(attrName);
	}

}
