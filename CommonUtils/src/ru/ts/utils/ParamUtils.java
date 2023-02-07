package ru.ts.utils;

import java.util.Map;

/**
 * Утилиты получения параметров
 */
abstract public class ParamUtils
{
	public static String getStrParByName(Map parmap, String parname)
	{
		String[] pars = (String[]) parmap.get(parname);
		if (pars != null && pars.length > 0)
			return pars[0];
		return null;
	}

}
