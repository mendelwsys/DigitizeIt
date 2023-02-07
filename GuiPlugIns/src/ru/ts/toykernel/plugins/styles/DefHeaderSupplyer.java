package ru.ts.toykernel.plugins.styles;

import ru.ts.utils.gui.tables.TNode;
import ru.ts.utils.gui.tables.THeader;
import ru.ts.toykernel.gui.deftable.*;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.utils.gui.tables.IHeaderSupplyer;


/**
 * Default header supplyer for layers
 */
public class DefHeaderSupplyer implements IHeaderSupplyer
{

	public DefHeaderSupplyer(INameConverter storNm2attrNm)
	{
		styleheaders = new THeader[]
			{
					new TDefaultHeader(new TNode(storNm2attrNm.codeAttrNm2ViewNm(KernelConst.LAYER_NAME)), storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.LAYER_NAME),false, String.class),
					new TDefaultHeader(new TNode(storNm2attrNm.codeAttrNm2ViewNm(KernelConst.LAYER_VISIBLE)), storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.LAYER_VISIBLE),true, Boolean.class)
			};

	}

	private THeader[] styleheaders;

	public THeader[] getOptionsRepresent()
	{
		return styleheaders;
	}
}
