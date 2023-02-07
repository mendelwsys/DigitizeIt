package ru.ts.toykernel.converters;

/**
 * Rotate converter
 */
public interface IRotateConverter extends ILinearConverter
{
	public final String ROTATECONVERTER="ROTATECONVERTER";
	double[] getRotMatrix()  throws Exception;
	void setRotMatrix(double[] matrix)  throws Exception;
}
