package ru.ts.gisutils.common.records;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.mwlib.utils.Enc;
import ru.ts.gisutils.common.Sys;
import ru.ts.gisutils.common.TimeSpan;
import ru.ts.gisutils.common.logger.BaseLogger;

public class TestDebugging
{
	static BaseLogger log = new BaseLogger();

	/**
	 * @param args -
	 *            string arguments
	 */
	public static void main_real( String[] args )
	{
		/**
		 * test long and double conversion routines
		 */
		int[] ints = new int[128];
		long l = 0x000000FF0000FF00L, l1;
		double dbl, dbl1, dbl2;
		Record rec, rec1, rec2;

		/*
		 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		 */
		log.logLine( "+++ TEST RLByte +++" );
		logClass( Record.class );
		try
		{
			ArrayList alist = new ArrayList();
			RecordListBase rlist;
			rlist = new RLInt( Record.class );
			int size = 128 * 1024;
			log
			        .logLine( "create "
			                + size
			                + " entries and store them in common List and new RecordList" );
			int dblind = rlist.getFieldIndex( "dbl1" );
			int lngind = rlist.getFieldIndex( "lng1" );
			int intind = rlist.getFieldIndex( "int1" );
			int indind = rlist.getFieldIndex( "ind" );
			int btind1 = rlist.getFieldIndex( "bt1" );
			int btind3 = rlist.getFieldIndex( "bt3" );
			for ( int i = 0; i < size; i++ )
			{
				rec = new Record();
				rec.ind = i;
				/*
				 * rec.dbl1 = dbl; rec.lng1 = Double.doubleToLongBits(dbl);
				 */
				alist.add( rec );
				rlist.add( rec );
				rec1 = (Record) rlist.get( i );
				if ( ( rec1.dbl1 != rlist.getFieldDouble( i, dblind ) )
				        || ( rec1.lng1 != rlist.getFieldLong( i, lngind ) )
				        || ( rec1.bt3 != rlist.getFieldByte( i, btind3 ) ) )
				{
					log
					        .logLine( "Error use new interface getFieldXXXX(int, int)" );
				}
			}

			for ( int i = 0; i < size; i++ )
			{
				rec1 = (Record) alist.get( i );
				rec2 = (Record) rlist.get( i );
				if ( !rec1.equals( rec2 ) )
				{
					log.logf( "Entry #%d, records are different!%n",
					        new Object[] { new Integer( i + 1 ) } );
					ints[ 0 ] = rec1.ind;
					l = rec1.lng1;
					Bits.putLong( ints, 1, l );
					l1 = Bits.getLong( ints, 1 );
					if ( l != l1 )
						log.logLine( "long not coincided" );

					dbl = rec1.dbl1;
					l1 = Double.doubleToLongBits( dbl );
					Bits.putDouble( ints, 3, dbl );

					dbl1 = Double.longBitsToDouble( l1 );
					dbl2 = Bits.getDouble( ints, 3 );
					if ( ( dbl != dbl1 ) || ( dbl != dbl2 ) )
						log.logLine( "double  not coincided" );
				}
			}

			// first check embedded sort procedure
			rlist.setIndexKey( "ind" );
			if ( false )
			{
				RecordListBase rl1 = new RLByte( Record.class );
				for ( int i = 0; i < 32; i++ )
				{
					log.logLine( "Step #" + ( i + 1 ) );
					rlist.shuffle();
					rl1.clear();
					rl1.addAll( rlist );
					rlist.sort();
					if ( !checkSort( rlist ) )
					{
						log.logLine( "Error.. DEBUG it NOW!!!" );
						rlist.clear();
						rlist.addAll( rl1 );
						rlist.sort( true );
						if ( checkSort( rlist ) )
							log
							        .logLine( "... but now - CORRECT. That is impossible!!! " );
						break;
					}
				}
			}

			log.logLine( "+++ TEST embedded sorting on internal key" );
			TimeSpan ts;
			int testcnt = 10;
			rlist.setIndexKey( "ind" );
			Date dt = new Date(); // start time
			/*
			 * get shuffle time
			 */
			for ( int i = 0; i < testcnt; i++ )
			{
				rlist.shuffle();
			}
			ts = new TimeSpan( new Date(), dt );
			long shtime = ts.getMilliseconds(); // shuffle time
			dt = new Date();

			for ( int i = 0; i < testcnt; i++ )
			{
				rlist.shuffle();
				rlist.sort();
			}
			ts = new TimeSpan( new Date(), dt );
			ts.subtract( shtime );
			log.logLine( "+++ TEST finished in " + ts.toString() );

			/*
			 * check sort correctness
			 */
			if ( checkSort( rlist ) )
				log.logLine( "+++ SORTED correctly +++" );
			else
				log.logLine( "--- NOT SORTED correctly ---" );

			log.logLine( "+++ TEST system sorting method" );
			dt = new Date();
			for ( int i = 0; i < testcnt; i++ )
			{
				rlist.shuffle();
				Collections.sort( rlist );
			}
			ts = new TimeSpan( new Date(), dt );
			ts.subtract( shtime );
			log.logLine( "+++ TEST finished in " + ts.toString() );

			log.logLine( "+++ TEST std ArrayList sorting" );

			// get shuffle time
			dt = new Date();
			for ( int i = 0; i < testcnt; i++ )
			{
				Collections.shuffle( alist );
			}
			ts = new TimeSpan( new Date(), dt );
			shtime = ts.getMilliseconds(); // shuffle time

			// get sort time
			dt = new Date();
			for ( int i = 0; i < 10; i++ )
			{
				Collections.shuffle( alist );
				Collections.sort( alist );
			}
			ts = new TimeSpan( new Date(), dt );
			ts.subtract( shtime );
			log.logLine( "+++ TEST finished in " + ts.toString() );

		}
		catch ( Exception e )
		{
			log.logLine( e.getMessage() );
		}
		finally
		{
			log.logLine( "--- CHECK completed ---" );
		}
	}

	public static void logBinary( double dbl )
	{
		long l = Double.doubleToRawLongBits( dbl );
		String str = Long.toBinaryString( l );
		log.logf( "%10f -> %20d -> %64s%n", new Object[] { new Double( dbl ),
		        new Long( l ), str } );
	}

	public static void logRounded( double dbl )
	{
		log.logf( "isrounded(%f) == %b%n", new Object[] { new Double( dbl ),
		        Boolean.valueOf( Sys.isaligned( dbl ) ) } );
	}

	public static void logClass( Class cls )
	{
		boolean old = ( (BaseLogger) log ).timebefore;
		( (BaseLogger) log ).timebefore = false;

		log.logLine( "Canonical class name is \"" + cls.getCanonicalName()
		        + "\"" );
		cls.getClasses();
		Field[] fields = cls.getFields();
		if ( fields.length > 0 )
			for ( int i = 0; i < fields.length; i++ )
			{
				Field field = fields[ i ];
				String name = field.getName();
				Type type = field.getGenericType();
				log.logf( "Field name is '%10s'-#%4d is %s%n", new Object[] {
				        name, new Integer( i ), type.toString() } );
			}
		else
			log.logLine( "no field" );

		( (BaseLogger) log ).timebefore = old;
	}

	/**
	 * @param lst
	 * @return
	 */
	private static boolean checkSort( RecordListBase lst )
	{
		/*
		 * check sort correctness
		 */
		Record rec = (Record) lst.get( 0 );
		int val = rec.ind;
		for ( int i = 1; i < lst.size(); i++ )
		{
			lst.get( rec, i );
			if ( rec.ind < val )
			{
				log.logLine( Enc.get("$23") + rec.ind
				        + " on step " + i + " prev. ind = " + val + " !!!" );
				return false;
			}
			val = rec.ind;
		}
		return true;
	}

	private static boolean checkRecs( Record orig, Record rlrec )
	{
		return orig.compareTo( rlrec ) == 0;
	}

}
