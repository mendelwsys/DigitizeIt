package ru.ts.toykernel.pcntxt;

import ru.ts.factory.IInitAble;

/**
 * Meta Inforamtion of map
 *
 */
public interface IMetaInfoBean  extends IInitAble
{
	String getFormatVersion();

	void setFormatVersion(String formatVersion);

	int getMajor();

	void setMajor(int major);

	int getMinor();

	void setMinor(int minor);

	int getBoxColor();

	void setBoxColor(int boxColor);

	int getBackgroundColor();

	void setBackgroundColor(int backgroundColor);

	String getProjName();

	void setProjName(String projName);

	String getS_mapversion();

	void setS_mapversion(String s_mapversion);

//	String getS_convertertype();
//
//	void setS_convertertype(String s_convertertype);
//
//	byte[] getB_converter();
//
//	void setB_converter(byte[] b_converter);
//
//	byte[] getB_currentP0();
//
//	void setB_currentP0(byte[] b_currentP0);

	String getS_MapUnitsName();

	void setS_MapUnitsName(String s_MapUnitsName);
}
