package ru.ts.toykernel.converters;

import java.util.LinkedList;
import java.util.List;

/**
 * Серверный конвертер которым пользуются в серверном контексте
 * 
 */
public class ServerConverter  extends CrdConverterFactory.ChainConverter implements IRProjectConverter
{
	private IRotateConverter rotateconv;
	private IScaledConverter scaleConverter;

	public ServerConverter()
	{
	}


	public ServerConverter(IRotateConverter rotateconv,IScaledConverter scaleConverter)
	{
		this.rotateconv = rotateconv;
		this.scaleConverter = scaleConverter;
		this.converterchain.add(rotateconv);
		this.converterchain.add(scaleConverter);
	}

	public ILinearConverter getSrc2SyncConverter()
	{

		List<ILinearConverter> conv=new LinkedList<ILinearConverter>();
		conv.add(rotateconv);
		return new CrdConverterFactory.ChainConverter(conv);
	}

	public ILinearConverter getSync2DstConverter()
	{
		List<ILinearConverter> conv=new LinkedList<ILinearConverter>();
		conv.add(scaleConverter);
		return new CrdConverterFactory.ChainConverter(conv);
	}

	public void setByConverter(IProjConverter converter) throws Exception
	{
		rotateconv.setRotMatrix(converter.getAsRotateConverter().getRotMatrix());
		scaleConverter.setScale(converter.getAsScaledConverterCtrl().getScale());
	}

	public IRotateConverter getAsRotateConverter()
	{
		return rotateconv;
	}

	public IScaledConverter getAsScaledConverter()
	{
		return scaleConverter;
	}

	public IShiftConverter getAsShiftConverter()
	{
		throw new UnsupportedOperationException();
	}

	public IScaledConverterCtrl getAsScaledConverterCtrl()
	{
		return getAsScaledConverter();
	}

	public Object[] init(Object... objs) throws Exception
	{
		super.init(objs);
		rotateconv= (IRotateConverter) converterchain.get(0);
		scaleConverter= (IScaledConverter) converterchain.get(1);
		return null;
	}

}
