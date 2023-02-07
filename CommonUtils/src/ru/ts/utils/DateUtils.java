package ru.ts.utils;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 25.09.2008
 * Time: 16:27:03
 * To change this template use File | Settings | File Templates.
 */
public class DateUtils
{
	public static Date getCurrentDate()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));

		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		return calendar.getTime();
	}
	public static String strDateTime(int d, int m, int y, String sep)
	{
		String sd = String.valueOf(d);
		if (d < 10)
			sd = "0" + sd;

		String sm = String.valueOf(m);
		if (m < 10)
			sm = "0" + sm;

		String sy = String.valueOf(y);
		if (y < 10)
			sy = "0" + sy;

		return sd + sep + sm + sep + sy;
	}

	public static String strDateTime(int d, int m,String sep)
	{
		String sd = String.valueOf(d);
		if (d < 10)
			sd = "0" + sd;

		String sm = String.valueOf(m);
		if (m < 10)
			sm = "0" + sm;
		return sd + sep + sm;
	}

	public static String strShortTime()
	{
		Calendar c= Calendar.getInstance();
		c.setTime(new Date(System.currentTimeMillis()));
		int hh = c.get(Calendar.HOUR_OF_DAY);
		int mm = c.get(Calendar.MINUTE);
		return strDateTime(hh, mm,":");
	}

	public static String strShortTime(Date date)
	{
		Calendar c= Calendar.getInstance();
		c.setTime(date);
		int hh = c.get(Calendar.HOUR_OF_DAY);
		int mm = c.get(Calendar.MINUTE);
		return strDateTime(hh, mm,":");
	}

	public static String strShortTime(Calendar c)
	{
		int hh = c.get(Calendar.HOUR_OF_DAY);
		int mm = c.get(Calendar.MINUTE);
		return strDateTime(hh, mm,":");
	}

	public static String strLongTime(Calendar c)
	{
		int hh = c.get(Calendar.HOUR_OF_DAY);
		int mm = c.get(Calendar.MINUTE);
		int ss = c.get(Calendar.SECOND);

		return strDateTime(hh, mm, ss, ":");
	}

	public static String strDateTime(Calendar c)
	{

		int d = c.get(Calendar.DAY_OF_MONTH);
		int m = c.get(Calendar.MONTH);
		int y = c.get(Calendar.YEAR);

		int hh = c.get(Calendar.HOUR_OF_DAY);
		int mm = c.get(Calendar.MINUTE);
		int ss = c.get(Calendar.SECOND);

		return strDateTime(d, m + 1, y, ".") + " " + strDateTime(hh, mm, ss, ":");
	}

	public static String strCalendar(Calendar c)
	{
		int d = c.get(Calendar.DAY_OF_MONTH);
		int m = c.get(Calendar.MONTH);
		int y = c.get(Calendar.YEAR);
		return strDateTime(d, m + 1, y, ".");
	}

	public static String strCalendar(Date d)
	{
		Calendar cl = Calendar.getInstance();
		cl.setTime(d);
		return strCalendar(cl);
	}

	public static String strCurrDate()
	{
		return strCalendar(Calendar.getInstance());
	}

	public static String strPrevDate()
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		return strCalendar(c);
	}

	/**
	 * Отдать строку даты отстоящую от текущей на delta
	 * @param delta -
	 * @return -
	 */
	public static String strDeltaDate(int delta)
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, delta);
		return strCalendar(c);
	}

	public static class ConvertEx extends Exception
	{

		public ConvertEx()
		{

		}

		public ConvertEx(Throwable cause)
		{
			super(cause);
		}

		public ConvertEx(String message)
		{
			super(message);
		}

		public ConvertEx(String message, Throwable cause)
		{
			super(message, cause);
		}
	}

	public static Date dateFromStr(String s, char del, String dmy) throws ConvertEx
	{
		return dateFromStr(s, del, dmy, 0);
	}

	public static Date dateFromStr(String s, char del, String dmy, long addtime) throws ConvertEx
	{

		String cmp[] = s.split("\\" + del);
		if (cmp == null || cmp.length == 0)
			throw new ConvertEx();
		Calendar c = Calendar.getInstance();
		try
		{
			int dindex = dmy.indexOf("d");
			int mindex = dmy.indexOf("m");
			int yindex = dmy.indexOf("y");
			c.set(Integer.parseInt(cmp[yindex]), Integer.parseInt(cmp[mindex]) - 1, Integer.parseInt(cmp[dindex]),0,0,0);
		}
		catch (NumberFormatException e)
		{
			throw new ConvertEx(e);
		}
		return new Date(c.getTime().getTime() + addtime);
	}

	public static Date dateFrom2Str(String sdate, char deldate, String dmy, String stime, char deltime, String hms) throws ConvertEx
	{
		return dateFromStr(sdate, deldate, dmy, timeFromStr(stime, deltime, hms));
	}

	public static long timeFromStr(String s, char del, String hms) throws ConvertEx
	{

		String cmp[] = s.split("\\" + del);
		if (cmp == null || cmp.length == 0)
			throw new ConvertEx();
		try
		{
			int hindex = hms.indexOf("h");
			int mindex = hms.indexOf("m");
			int sindex = hms.indexOf("s");
			int hh = Integer.parseInt(cmp[hindex]);
			int mins = Integer.parseInt(cmp[mindex]) - 1;
			int secs = Integer.parseInt(cmp[sindex]);
			return (hh * 60 * 60 + mins * 60 + secs) * 1000;
		}
		catch (NumberFormatException e)
		{
			throw new ConvertEx(e);
		}
	}

}
