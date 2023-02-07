package ru.ts.toykernel.converters;

import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.factory.DefIFactory;
import ru.ts.factory.IFactory;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 17.05.2009
 * Time: 20:58:30
 * To change this template use File | Settings | File Templates.
 */
public class ConvB64Initializer extends DefIFactory<ILinearConverter>
{
	protected String s_convertertype;//type of converter
	protected byte[] b_converter;//converter in base64
	protected byte[] b_currentP0;//bind point in base64

	public ConvB64Initializer(IFactory<ILinearConverter> fconverter)
	{
		this.addFactory(fconverter);
	}
	public ConvB64Initializer(String s_convertertype, byte[] b_currentP0, byte[] b_converter, IFactory<ILinearConverter> fconverter)
	{
		this(fconverter);
		this.s_convertertype = s_convertertype;
		this.b_currentP0 = b_currentP0;
		this.b_converter = b_converter;
	}

	public String getS_convertertype()
	{
		return s_convertertype;
	}

	public void setS_convertertype(String s_convertertype)
	{
		this.s_convertertype = s_convertertype;
	}

	public byte[] getB_converter()
	{
		return b_converter;
	}

	public void setB_converter(byte[] b_converter)
	{
		this.b_converter = b_converter;
	}

	public byte[] getB_currentP0()
	{
		return b_currentP0;
	}

	public void setB_currentP0(byte[] b_currentP0)
	{
		this.b_currentP0 = b_currentP0;
	}

}
