/**
 * 
 */
package ru.ts.gisutils.common;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import ru.ts.gisutils.common.logger.BaseLogger;
import ru.ts.gisutils.common.logger.ILogger;
import ru.ts.gisutils.common.records.RLInt;
import ru.ts.gisutils.common.records.Record;
import ru.ts.utils.Files;

/**
 * test program for any very different purposes
 * 
 * @author sygsky
 * 
 */
//@SuppressWarnings("unchecked")
public class TestCommon
{

	public static ILogger	log;
	static
	{
		log = new BaseLogger(System.out);
	}
	/**
	 * @param args -
	 *            string arguments
	 */
	//@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		if (false)
		{
			log.logLine("Play with decimal point in float numbers");
			NumberFormat f = NumberFormat.getNumberInstance();
			DecimalFormatSymbols ds = new DecimalFormatSymbols();
			log
			        .logLine("DecimalSeparator = '" + ds.getDecimalSeparator()
			                + "'");
			ds.setDecimalSeparator('.');
			log.logLine("DecimalSeparator changed to = '"
			        + ds.getDecimalSeparator() + "'");
			if (f instanceof DecimalFormat)
			{
				((DecimalFormat) f).setDecimalFormatSymbols(ds);
			}
			try
			{
				final String dir = Files.appendFileSeparator(Files
				        .getCurrentDir());
				log.logLine("Current directory is \"" + dir + "\"");
				log.appendStream(new PrintStream(new FileOutputStream(dir
				        + "test.log", true), true));
			}
			catch (Exception e)
			{
			}
		}
		if (false)
		{
			log.logLine("Play with rounding numbers");
			logRounded(1.2);
			logRounded(1.0);
			logRounded(2.2);
			logRounded(2.0);
			logRounded(2394050.0);
			logRounded(2394050.3857834);
			logBinary(0.0);
			logBinary(-0.0);
			logBinary(1.0);
			logBinary(2.0);
			logBinary(3.0);
			logBinary(4.0);
			logBinary(5.0);
			logBinary(6.0);
			logBinary(8.0);
			logBinary(16.0);
			logBinary(32.0);
			logBinary(64.0);
			log.logLine("+++ TEST RecordListI +++");
			try
			{
				RLInt rl = new RLInt(Record.class);
				int sz = rl.size();
				log.logLine("RecordListI(Record).Size=" + sz);
			}
			catch (Exception e)
			{
			}
			finally
			{
				log.logLine("--- TEST end ---");
			}

		}

		/**
		 * test Object to array conversion
		 */

		// check short form of array creation
		if (true)
		{
			log.logLine("--- TEST of array handling as Object");
			int[] arr = { 10, 12, 13, 14 };
			int i;
			Object obj = arr;
			arr = (int[])obj;
			Class cls = obj.getClass();
            boolean is_primitive;
			boolean is_array;
			Class ct = cls.getComponentType();
			is_array = ct.isArray();
			is_primitive = ct.isPrimitive();
			i = 0;
			i = java.lang.reflect.Array.getInt(obj, 1);
			Object o = java.lang.reflect.Array.get(obj, 1);
			cls = o.getClass();
			log.logLine("Class name = " + cls.getName());
			Double od = new Double(Math.PI);
			java.lang.reflect.Array.set(obj, 1, od);
			
			log.logLine("--- END of TEST");
		}

		/**
		 * test long and double conversion routines
		 */

	}

	public static void logBinary(double dbl)
	{
		long l = Double.doubleToRawLongBits(dbl);
		String str = Long.toBinaryString(l);
		log.logf("%10f -> %20d -> %64s%n", new Object[] { new Double(dbl),
		        new Long(l), str });
	}

	public static void logRounded(double dbl)
	{
		log.logf("isrounded(%f) == %b%n", new Object[] { new Double(dbl),
		        Boolean.valueOf(Sys.isaligned(dbl)) });
	}

	public static void logClass(Class cls)
	{
		boolean old = ((BaseLogger) log).timebefore;
		((BaseLogger) log).timebefore = false;

		log.logLine("Canonical class name is \"" + cls.getCanonicalName()
		        + "\"");
		cls.getClasses();
		Field[] fields = cls.getFields();
		if (fields.length > 0)
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[ i ];
				String name = field.getName();
				Type type = field.getGenericType();
				log.logf("Field name is '%10s'-#%4d is %s%n", new Object[] {
				        name, new Integer(i), type.toString() });
			}
		else
			log.logLine("no field");

		((BaseLogger) log).timebefore = old;
	}

}
