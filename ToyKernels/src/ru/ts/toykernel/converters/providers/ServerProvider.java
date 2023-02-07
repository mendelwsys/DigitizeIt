package ru.ts.toykernel.converters.providers;

import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.converters.IScaleDescBean;
import ru.ts.gisutils.algs.common.MPoint;

/**
 *  Серверный провайдер конвертера
 * 
 */
public class ServerProvider extends BaseInitAble implements IConvProvider
{
	private IProjConverter projconv;
	public ServerProvider()
	{
	}

	public ServerProvider(IProjConverter projconv)
	{
		this.projconv = projconv;
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.CONVERTER_TAGNAME))
			this.projconv = (IProjConverter) attr.getValue();
		return null;
	}

	public MPoint multScale(double mscale) throws Exception
	{
		return projconv.getAsScaledConverterCtrl().increaseMap(mscale);
	}

	public MPoint multScale(MPoint mscale, boolean evently) throws Exception
	{
		return projconv.getAsScaledConverterCtrl().increaseMap(mscale, evently);
	}

	public void setScaleRange(IScaleDescBean scalebean) throws Exception
	{
		projconv.getAsScaledConverterCtrl().setScaleBean(scalebean);
	}

}
