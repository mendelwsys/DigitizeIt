package ru.ts.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 29.09.2008
 * Time: 20:20:24
 * To change this template use File | Settings | File Templates.
 */
public class DateTimeUtils
{
	public static Calendar setUTCCalendar()
	{
		Calendar calendar = Calendar.getInstance();
		long date = calendar.getTime().getTime();
		long offset=calendar.getTimeZone().getOffset(date);
		calendar.setTime(new Date(date-offset));
		return calendar;
	}

	public static long getUTCOffset()
	{
		Calendar calendar = Calendar.getInstance();
		long date = calendar.getTime().getTime();
		return calendar.getTimeZone().getOffset(date);
	}

	public static double doubleDate(int d, int m, int y)
	{
	   String sd=String.valueOf(d);
		if (d<10)
			sd="0"+sd;

	   String sm=String.valueOf(m);
	   if (m<10)
		  sm="0"+sm;
		String sy = String.valueOf(y);
		return Double.parseDouble(sd+sm+sy.substring(sy.length()-2));
	}

	public static double doubleTime(int hh, int mm, int ss)
	{
	   String shh=String.valueOf(hh);
		if (hh<10)
			shh="0"+shh;

	   String smm=String.valueOf(mm);
	   if (mm<10)
		  smm="0"+smm;

		String sss = String.valueOf(ss);
		if (ss<10)
		   sss="0"+ss;

		return Double.parseDouble(shh+smm+sss);
	}

	public static double[] getDateTime(Calendar c) {

		int d = c.get(Calendar.DAY_OF_MONTH);
	  	int m = c.get(Calendar.MONTH);
	  	int y = c.get(Calendar.YEAR);

		int hh=c.get(Calendar.HOUR_OF_DAY);
		int mm=c.get(Calendar.MINUTE);
		int ss=c.get(Calendar.SECOND);
		return new double[]{doubleDate(d, m+1, y),doubleTime(hh, mm, ss)};
	}
}
