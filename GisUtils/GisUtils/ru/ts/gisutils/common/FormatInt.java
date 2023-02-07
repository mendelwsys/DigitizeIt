/**
 * Created 30.09.2008 10:59:44 by Syg for the "GisUtils" project
 */
package ru.ts.gisutils.common;

/**
 * @author Syg
 * 
 */
public class FormatInt
{

	final static int[]	sizeTable	= { 9, 99, 999, 9999, 99999, 999999,
	        9999999, 99999999, 999999999, Integer.MAX_VALUE };

	final static char[]	DigitTens	= { '0', '0', '0', '0', '0', '0', '0', '0',
	        '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2',
	        '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3',
	        '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4',
	        '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
	        '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7',
	        '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
	        '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9',
	        '9',	              };

	final static char[]	DigitOnes	= { '0', '1', '2', '3', '4', '5', '6', '7',
	        '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
	        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
	        '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
	        '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2',
	        '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
	        '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
	        '9',	              };

	/**
	 * All possible chars for representing a number as a String
	 */
	final static char[]	digits	  = { '0', '1', '2', '3', '4', '5', '6', '7',
	        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
	        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
	        'y', 'z'	          };

	// Requires positive x
	static int stringSize( int x )
	{
		for ( int i = 0;; i++ )
			if ( x <= sizeTable[ i ] )
				return i + 1;
	}

	/**
	 * Returns a <code>String</code> object representing the specified
	 * integer. The argument is converted to signed decimal representation and
	 * returned as a string, exactly as if the argument and radix 10 were given
	 * as arguments to the {@link #toString(int, int)} method.
	 * 
	 * @param i
	 *            an integer to be converted.
	 * @param min_digits
	 *            minimum digits to output, that means if output digits number
	 *            is lesser than this preset, leading zeros will be added
	 * @param sign_on
	 *            if <code>true</code> plus sigh (+) will always be added to
	 *            output string
	 * @return a string representation of the argument in base&nbsp;10.
	 */
	public static String toString(int i, int min_digits, boolean sign_on) {
        if (i == Integer.MIN_VALUE)
            return "-2147483648";
        boolean minus;
        if ( minus = i < 0 )
        	i = -i;
        int sign_size = minus || sign_on ? 1 : 0;
        
        /* Size with a sign */
        int size = stringSize(i) + sign_size;
        int buf_size = Math.max( min_digits, size );
        char[] buf = new char[ buf_size ];
        
        /* Create the main body of the output */
        getIntChars(i, buf_size, buf);
        int pos = 0;
        if ( sign_size > 0)
        {
        	if ( minus )
        		buf[pos++] = '-';
        	else
        		buf[pos++] = '+';
        }
        if ( buf_size > size )
        	for( ; pos < buf_size - size + sign_size; pos++ )
        		buf[ pos ]  = '0';
        return new String( buf );
    }

	/**
	 * Places characters representing the integer i into the character array
	 * buf. The characters are placed into the buffer backwards starting with
	 * the least significant digit at the specified index (exclusive), and
	 * working backwards from there.
	 * 
	 * Will fail if i == Integer.MIN_VALUE
	 */
	static void getIntChars( int i, int index, char[] buf )
	{
		int q, r;
		int charPos = index;
		char sign = 0;

		if ( i < 0 )
		{
			sign = '-';
			i = -i;
		}

		// Generate two digits per iteration
		while ( i >= 65536 )
		{
			q = i / 100;
			// really: r = i - (q * 100);
			r = i - ( ( q << 6 ) + ( q << 5 ) + ( q << 2 ) );
			i = q;
			buf[ --charPos ] = DigitOnes[ r ];
			buf[ --charPos ] = DigitTens[ r ];
		}

		// Fall through to fast mode for smaller numbers
		// assert(i <= 65536, i);
		for ( ;; )
		{
			q = ( i * 52429 ) >>> ( 16 + 3 );
			r = i - ( ( q << 3 ) + ( q << 1 ) ); // r = i-(q*10) ...
			buf[ --charPos ] = digits[ r ];
			i = q;
			if ( i == 0 )
				break;
		}
		if ( sign != 0 )
		{
			buf[ --charPos ] = sign;
		}
	}

	/**
	 * @param args
	 */
	public static void main( String[] args )
	{
		// TODO Auto-generated method stub
		int i = -123456;
		System.out.println("Print toString(" + i + ", 2, false) :" + toString(i, 2, false));
		System.out.println("Print toString(" + i + ", 12, false):" + toString(i, 12, false));
		i = 654321;
		System.out.println("Print toString(" + i + ", 2, false)  :" + toString(i, 2, false));
		System.out.println("Print toString(" + i + ", 12, false) :" + toString(i, 12, false));
		System.out.println("Print toString(" + i + ", 12, true)  :" + toString(i, 12, true));
	}

}
