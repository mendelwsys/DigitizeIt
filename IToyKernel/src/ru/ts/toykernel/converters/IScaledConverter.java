package ru.ts.toykernel.converters;

import ru.ts.toykernel.converters.ILinearConverter;

/**
 * Scale converter
 */
public interface IScaledConverter extends ILinearConverter, IScaledConverterCtrl
{
	public final String SCALECONVERTER="SCALECONVERTER";

}
