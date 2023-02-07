package ru.ts.gisutils.common.records;

/**
 * Utility methods for packing/unpacking primitive values in/out of byte and
 * integer arrays using big-endian byte ordering.
 */
public class Bits
{

	/*
	 * Methods for unpacking primitive values from byte arrays starting at given
	 * offsets.
	 */

	public static boolean getBoolean(byte[] b, int off)
	{
		return b[ off ] != 0;
	}

	static int[]	BYTE_MASK;

	static int[]	BYTE_SHIFT;
	static
	{
		BYTE_MASK = new int[] { 0xFF, 0xFF00, 0xFF0000, 0xFF000000 };
		BYTE_SHIFT = new int[] { 0, 8, 16, 24 };
	}

	static byte getByte(int[] b, int off, int ind)
	{
		return (byte)((b[ off ] & BYTE_MASK[ ind ]) >> BYTE_SHIFT[ ind ]);
	}

	static boolean getBoolean(int[] b, int off, int ind)
	{
		return ((b[ off ] & BYTE_MASK[ ind ]) >> BYTE_SHIFT[ ind ]) != 0;
	}

	public static char getChar(byte[] b, int off)
	{

		return (char) (((b[ off + 1 ] & 0xFF) << 0) + ((b[ off + 0 ] & 0xFF) << 8));
	}

	static int[]	CHAR_MASK;
	static int[]	CHAR_SHIFT;
	static
	{
		CHAR_MASK = new int[] { 0xFFFF, 0xFFFF0000 };
		CHAR_SHIFT = new int[] { 0, 16 };
	}
	/**
	 * 
	 * @param b bit array containing of char looked for
	 * @param off offset to the array
	 * @param ind index of char packed into int. May be 0 or 1 ONLY!!!
	 * @return character stored in the array
	 */
	public static char getChar(int[] b, int off, int ind)
	{
		return (char) ( (b[off] & CHAR_MASK[ind]) >> CHAR_SHIFT[ind]);
	}

	static short getShort(byte[] b, int off)
	{
		return (short) (((b[ off + 1 ] & 0xFF) << 0) + ((b[ off + 0 ] & 0xFF) << 8));
	}

	/**
	 * the same as char
	 * @param b
	 * @param off
	 * @param ind	index of short packed into int may be 0 or 1 ONLY
	 * @return
	 */
	static short getShort(int[] b, int off, int ind)
	{
		return (short) ( (b[off] & CHAR_MASK[ind]) >> CHAR_SHIFT[ind] );
	}

	static int getInt(byte[] b, int off)
	{
		return ((b[ off + 3 ] & 0xFF) << 0) + ((b[ off + 2 ] & 0xFF) << 8)
		        + ((b[ off + 1 ] & 0xFF) << 16) + ((b[ off + 0 ] & 0xFF) << 24);
	}

	static int getInt(int[] b, int off)
	{
		return b[ off ];
	}

	static float getFloat(byte[] b, int off)
	{
		int i = ((b[ off + 3 ] & 0xFF) << 0) + ((b[ off + 2 ] & 0xFF) << 8)
		        + ((b[ off + 1 ] & 0xFF) << 16) + ((b[ off + 0 ] & 0xFF) << 24);
		return Float.intBitsToFloat(i);
	}

	static float getFloat(int[] b, int off)
	{
		return Float.intBitsToFloat( b[off] );
	}

	static long getLong(byte[] b, int off)
	{
		return ((b[ off + 7 ] & 0xFFL) << 0) + ((b[ off + 6 ] & 0xFFL) << 8)
		        + ((b[ off + 5 ] & 0xFFL) << 16)
		        + ((b[ off + 4 ] & 0xFFL) << 24)
		        + ((b[ off + 3 ] & 0xFFL) << 32)
		        + ((b[ off + 2 ] & 0xFFL) << 40)
		        + ((b[ off + 1 ] & 0xFFL) << 48)
		        + ((b[ off + 0 ] & 0xFFL) << 56);
	}

	public static long getLong(int[] b, int off)
	{
		return ((long)b[ off ] & 0xFFFFFFFFL) + ( ((long)(b[ off + 1 ])) << 32L);
	}

	public static double getDouble(byte[] b, int off)
	{
		long j = ((b[ off + 7 ] & 0xFFL) << 0) + ((b[ off + 6 ] & 0xFFL) << 8)
		        + ((b[ off + 5 ] & 0xFFL) << 16)
		        + ((b[ off + 4 ] & 0xFFL) << 24)
		        + ((b[ off + 3 ] & 0xFFL) << 32)
		        + ((b[ off + 2 ] & 0xFFL) << 40)
		        + ((b[ off + 1 ] & 0xFFL) << 48)
		        + ((b[ off + 0 ] & 0xFFL) << 56);
		return Double.longBitsToDouble(j);
	}

	public static double getDouble(int[] b, int off)
	{
		long j = ((long)b[ off ] & 0xFFFFFFFFL) + ( ((long)(b[ off + 1 ])) << 32L);
		return Double.longBitsToDouble(j);
	}

	/*
	 * Methods for packing primitive values into byte arrays starting at given
	 * offsets.
	 */

	static void putBoolean(byte[] b, int off, boolean val)
	{
		b[ off ] = (byte) (val ? 1 : 0);
	}

	
	static void putBoolean(int[] b, int off, int ind, boolean val)
	{
		b[ off ] &= ~(BYTE_MASK[ind]);
		b[ off ] |= (byte) (val ? 1 : 0) << BYTE_SHIFT[ind];
	}
	
	static void putByte(int[] b, int off, int ind, byte val)
	{
		b[ off ] &= ~(BYTE_MASK[ind]);
		b[ off ] |= val << BYTE_SHIFT[ind];
	}

	static void putChar(byte[] b, int off, char val)
	{
		b[ off + 1 ] = (byte) (val >>> 0);
		b[ off + 0 ] = (byte) (val >>> 8);
	}

	static void putChar(int[] b, int off, int ind, char val)
	{
		b[off] &= ~CHAR_MASK[ind];
		b[off] |= ((short)val) << CHAR_SHIFT[ind];
	}

	static void putShort(byte[] b, int off, short val)
	{
		b[ off + 1 ] = (byte) (val >>> 0);
		b[ off + 0 ] = (byte) (val >>> 8);
	}

	static void putShort(int[] b, int off, int ind, short val)
	{
		b[off] &= ~CHAR_MASK[ind];
		b[off] |= val << CHAR_SHIFT[ind];
	}

	static void putInt(byte[] b, int off, int val)
	{
		b[ off + 3 ] = (byte) (val >>> 0);
		b[ off + 2 ] = (byte) (val >>> 8);
		b[ off + 1 ] = (byte) (val >>> 16);
		b[ off + 0 ] = (byte) (val >>> 24);
	}

	static void putInt(int[] b, int off, int val)
	{
		b[ off ] = val;
	}

	static void putFloat(byte[] b, int off, float val)
	{
		int i = Float.floatToIntBits(val);
		b[ off + 3 ] = (byte) (i >>> 0);
		b[ off + 2 ] = (byte) (i >>> 8);
		b[ off + 1 ] = (byte) (i >>> 16);
		b[ off + 0 ] = (byte) (i >>> 24);
	}

	static void putFloat(int[] b, int off, float val)
	{
		int i = Float.floatToIntBits(val);
		b[ off ] = (i);
	}

	static void putLong(byte[] b, int off, long val)
	{
		b[ off + 7 ] = (byte) (val >>> 0);
		b[ off + 6 ] = (byte) (val >>> 8);
		b[ off + 5 ] = (byte) (val >>> 16);
		b[ off + 4 ] = (byte) (val >>> 24);
		b[ off + 3 ] = (byte) (val >>> 32);
		b[ off + 2 ] = (byte) (val >>> 40);
		b[ off + 1 ] = (byte) (val >>> 48);
		b[ off + 0 ] = (byte) (val >>> 56);
	}
	
	public static void putLong(int[] b, int off, long val)
	{
		b[ off ] = (int) (val & 0x00000000FFFFFFFFL);
		b[ off + 1 ] = (int) ( val >>> 32);
	}

	public static void putDouble(byte[] b, int off, double val)
	{
		long j = Double.doubleToLongBits(val);
		b[ off + 7 ] = (byte) (j >>> 0);
		b[ off + 6 ] = (byte) (j >>> 8);
		b[ off + 5 ] = (byte) (j >>> 16);
		b[ off + 4 ] = (byte) (j >>> 24);
		b[ off + 3 ] = (byte) (j >>> 32);
		b[ off + 2 ] = (byte) (j >>> 40);
		b[ off + 1 ] = (byte) (j >>> 48);
		b[ off + 0 ] = (byte) (j >>> 56);
	}

	public static void putDouble(int[] b, int off, double val)
	{
		long j = Double.doubleToLongBits(val);
		b[ off ] = (int) (j & 0xFFFFFFFFL);
		b[ off + 1 ] = (int) (j >>> 32L);
	}
}
