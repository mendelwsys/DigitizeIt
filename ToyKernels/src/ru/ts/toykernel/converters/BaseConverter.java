package ru.ts.toykernel.converters;

/**
 * Базовая реализация проектного конвертера
 */
public abstract class BaseConverter extends BBaseConverter 
		implements IProjConverter
{

	public IRotateConverter getAsRotateConverter()
	{
		throw new UnsupportedOperationException();
	}

	public IScaledConverter getAsScaledConverter()
	{
		throw new UnsupportedOperationException();
	}


	public IShiftConverter getAsShiftConverter()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return вернуть ковертер масштабирования
	 */
	public IScaledConverterCtrl getAsScaledConverterCtrl()
	{
		   return getAsScaledConverter(); 
	}
}
