package ru.ts.toykernel.pcntxt;

import ru.ts.gisutils.datamine.ProjBaseConstatnts;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.attrs.IDefAttr;


/**
 * Meta Inforamtion of map
 *
 */
public class MetaInfoBean extends BaseInitAble implements IMetaInfoBean
{
	public String getFormatVersion()
	{
		return formatVersion;
	}

	public void setFormatVersion(String formatVersion)
	{
		this.formatVersion = formatVersion;
	}

	public int getMajor()
	{
		return major;
	}

	public void setMajor(int major)
	{
		this.major = major;
	}

	public int getMinor()
	{
		return minor;
	}

	public void setMinor(int minor)
	{
		this.minor = minor;
	}

	public int getBoxColor()
	{
		return boxColor;
	}

	public void setBoxColor(int boxColor)
	{
		this.boxColor = boxColor;
	}

	public int getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	public String getProjName()
	{
		return projName;
	}

	public void setProjName(String projName)
	{
		this.projName = projName;
	}

	public String getS_mapversion()
	{
		return s_mapversion;
	}

	public void setS_mapversion(String s_mapversion)
	{
		this.s_mapversion = s_mapversion;
	}


	public String getS_MapUnitsName()
	{
		return s_MapUnitsName;
	}

	public void setS_MapUnitsName(String s_MapUnitsName)
	{
		this.s_MapUnitsName = s_MapUnitsName;
	}

	protected String formatVersion;//format version
	protected  int major;
	protected  int minor;

	protected  int boxColor;//color of box border
	protected  int backgroundColor;//fill color of immage background

	protected  String projName;//name of the project
	protected String s_mapversion;//Версия карты
	protected String s_MapUnitsName;//name of untis in map



	public MetaInfoBean()
	{
		s_MapUnitsName= ProjBaseConstatnts.USER;//Default we are don't know which units in projections
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase("FORMAT VERSION"))
			formatVersion=(String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase("major"))
			major=Integer.parseInt((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase("minor"))
			minor=Integer.parseInt((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase("projname"))
			projName=(String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase("boxColor"))
			boxColor=(int)Long.parseLong((String) attr.getValue(),16);
		else if (attr.getName().equalsIgnoreCase("backColor"))
			backgroundColor=(int)Long.parseLong((String) attr.getValue(),16);
		else if (attr.getName().equalsIgnoreCase("mapver"))
			s_mapversion=(String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase("units"))
			s_MapUnitsName=(String) attr.getValue();
		return null;
	}
}