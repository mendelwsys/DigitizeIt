package ru.ts.gisutils.common.records;

import java.util.Random;

/**
 * test class to understand how to access fields of different type
 * through reflection
 * 
 * @author sygsky
 *
 */
public class Record implements Comparable
{
	public int ind;
	public long lng1;
	public double dbl1;
	public int int1;
	public short shrt1;
	public byte bt1;
	public char ch1;
	public float flt1;
	public boolean bool1;
	public char ch2;
	public int int2;
	public byte bt2;
	public boolean bool2;
	public short shrt2;
	public byte bt3;

	static Random rnd;
	static final String Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890";
	static { rnd = new Random(); }
	
	public Record()
	{
		dbl1 = rnd.nextDouble();
		lng1 = Double.doubleToLongBits(dbl1);
		int1 = rnd.nextInt(1000);
		int2 = 1000 + rnd.nextInt(1000);
		shrt1 = (short)(256 + rnd.nextInt(256));
		shrt2 = (short)rnd.nextInt();
		byte[] bytes = new byte[4];
		rnd.nextBytes(bytes);
		bt1 = bytes[0];
		bt2 = bytes[1];
		bt3 = bytes[2];
		ch1 = Alphabet.charAt(rnd.nextInt(Alphabet.length()));
		ch2 = Alphabet.charAt(rnd.nextInt(Alphabet.length()));
		flt1 = rnd.nextFloat();
		bool1 = rnd.nextInt() > 0;
		bool2 = rnd.nextInt() > 0;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
    	Record rec = (Record)obj;

    	return (ind == rec.ind) 
	    &&(lng1 == rec.lng1)
	    && (dbl1 == rec.dbl1)
	    && (int1 == rec.int1)
	    && (shrt1 == rec.shrt1)
	    && (bt1 == rec.bt1)
	    && (ch1 == rec.ch1)
	    && (flt1 == rec.flt1)
	    && (bool1 == rec.bool1);
    }

	public int compareTo(Object arg0)
    {
	    return this.ind - ((Record)arg0).ind;
    }
}
