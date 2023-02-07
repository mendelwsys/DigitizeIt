package ru.ts.gisutils.datamine;

import com.mwlib.utils.Enc;

public class ProjBaseConstatnts
{

	//Known units of translation
	public static final String GEORADIANS="GEORADIANS";//Radians
	public static final String DEGREE ="DEGREE";//Radians
	public static final String METERS="METERS";//METERS
	public static final String USER="USER";//Unknown


	public static String getNameUnitsByUnitsName(String unitsname)
	{
		if (unitsname.equals(DEGREE))
			return Enc.get("$25");
		if (unitsname.equals(GEORADIANS))
			return Enc.get("$26");
		else if (unitsname.equals(METERS))
			return Enc.get("$27");
		else
			return unitsname;
	}

}
