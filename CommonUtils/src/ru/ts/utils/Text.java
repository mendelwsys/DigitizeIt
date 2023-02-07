/**
 * 
 */
package ru.ts.utils;

import java.util.ArrayList;

/**
 * @author sygsky
 * 
 */
// @SuppressWarnings("unchecked")
public class Text
{

	/**
	 * split string with any valid separators of 1 character length by cutting
	 * it to array of substrings. Separators are removed, substrings put trimmed
	 * in a resulting array (countEmpty parameter).
	 * 
	 * @param str -
	 *            string to be processed
	 * 
	 * @param delim -
	 *            delimiter to be used to cut string to small parts
	 * 
	 * @param countEmpty -
	 *            {@code true} means that part with zero length will be put into
	 *            resulting array.
	 * 
	 * @return String[] of all string parts cut by delimiter
	 */
	public static String[] splitItems( String str, char delim,
	        boolean countEmpty )
	{
		if ( str == null )
			return new String[0];
		str = str.trim();
		String tmp;
		char c = '\0';
		final ArrayList arr = new ArrayList();
		int beginIndex = 0;
		for ( int i = 0; i < str.length(); i++ )
		{
			c = str.charAt( i );
			if ( c == delim )
			{
				tmp = str.substring( beginIndex, i ).trim();
				if ( ( tmp.length() > 0 ) || countEmpty )
					arr.add( tmp );
				beginIndex = i + 1;
			}
		}
		/* check if last item is not a delimiter */
		if ( beginIndex < str.length() )
			arr.add( str.substring( beginIndex ).trim() );
		else if ( ( c == delim ) && countEmpty ) /*
													 * check last item to be a
													 * delimiter
													 */
			arr.add( "" ); /*
							 * yes, last item is a delimiter, add empty value if
							 * ready
							 */
		final String[] res = new String[arr.size()];
		for ( int i = 0; i < res.length; i++ )
			res[ i ] = (String) arr.get( i );
		return res;
	}

	/**
	 * split string with any valid separators of 1 character length by cutting
	 * it to array of substrings. Separators are removed, substrings put trimmed
	 * in a resulting array (countEmpty parameter).
	 * 
	 * @param str -
	 *            string to be processed
	 * 
	 * @param seps -
	 *            String with all separators to be used to cut string to pieces
	 * 
	 * @param countEmpty -
	 *            {@code true} means that part with zero length will be put into
	 *            resulting array.
	 * 
	 * @return String[] of all string parts cut by separator
	 */
	public static String[] splitItems( String str, String seps,
	        boolean countEmpty )
	{
		if ( str == null )
			return new String[0];
		final char[] chrs = seps.toCharArray();
		final int sepnum = chrs.length;
		str = str.trim();
		String tmp;
		char c;
		ArrayList arr = new ArrayList();
		int beginIndex = 0;
		for ( int i = 0; i < str.length(); i++ )
		{
			c = str.charAt( i );
			for ( int j = 0; j < sepnum; j++ )
				if ( c == chrs[ j ] )
				{
					tmp = str.substring( beginIndex, i ).trim();
                    if ( ( tmp.length() > 0 ) || countEmpty )
                        arr.add( tmp );
					beginIndex = i + 1;
					break;
				}
		}
		if ( beginIndex < str.length() )
			arr.add( str.substring( beginIndex ).trim() );
		String[] res = new String[arr.size()];
		for ( int i = 0; i < res.length; i++ )
			res[ i ] = (String) arr.get( i );
		return res;
	}

	/**
	 * converts array of strings to the upper case
	 * 
	 * @param strarr
	 *            array to make an upper case one
	 * @return the same array but with strings in upper case
	 */
	public static String[] arrayToUpper( String[] strarr )
	{
		for ( int i = 0; i < strarr.length; i++ )
			strarr[ i ] = strarr[ i ].toUpperCase();
		return strarr;
	}

	/**
	 * converts array of strings to the lower case
	 * 
	 * @param strarr
	 *            array to make an lower case one
	 * @return the same array but with strings in lower case
	 */
	public static String[] arrayToLower( String[] strarr )
	{
		for ( int i = 0; i < strarr.length; i++ )
			strarr[ i ] = strarr[ i ].toLowerCase();
		return strarr;
	}

	/**
	 * finds last substring occurrence in a designated String between designated
	 * boundaries
	 * 
	 * @param str
	 *            is a String to search
	 * @param left
	 *            is a left boundary (inclusively) of searching
	 * @param right
	 *            is a right boundary (exclusively) of search
	 * @param sample
	 *            is a substring to find in
	 * @return index of last found substring or -1 if not found
	 */
	public static int getLastPos( String str, int left, int right, String sample )
	{
		int lastpos = sample.length() - 1, pos = lastpos;
		for ( int index = right - 1; index >= left; index-- )
			if ( str.charAt( index ) == sample.charAt( pos-- ) )
			{
				if ( pos < 0 ) // then last coincidence occured - we found!!!
					return index;
			}
			else
				pos = lastpos; // restart comparison from the rightmost
		// position of the sample
		return -1;
	}

	/**
	 * joins string values of args into single string separating items by comma.
	 * Example:<br>
	 * <br>
	 * <code>
	 * Object[] arr = new Object[] {"Hello ", new Integer(15), new Boolean(false)};<br>
	 * System.out.println(joinByCommas(arr));<br><br>
	 * ... result of print is<br><br> 
	 * Hello,15,false
	 * </code>
	 * 
	 * @param args
	 *            Object[] with items to join in text form
	 * @return String object resulting join by comma operation
	 */
	public static String joinByCommas( final Object[] args )
	{
		return joinBySeps( args, ',' );
	}

	/**
	 * Joins string values of args in range defined by user into single string
	 * separating items by sep value. Example:<br>
	 * <br>
	 * <code>
	 * Object[] arr = new Object[] {"Hello ", new Integer(15), new Boolean(false)};<br>
	 * System.out.println(joinBySeps(arr, ','));<br><br>
	 * ... result of print is<br><br> 
	 * Hello,15,false
	 * </code>
	 * 
	 * @param args
	 *            Object[] with items to join in text form
	 * @param sep
	 *            char of separator to insert between arguments from args
	 * @return String object resulting join by user defined separator
	 */
	public static String joinBySeps( final Object[] args, char sep )
	{
		return joinBySeps( args, sep, 0, args.length );
	}

	/**
	 * Joins string values of args in range defined by user into a single string
	 * separating items by sep value. Example:<br>
	 * <br>
	 * <code>
	 * Object[] arr = new Object[] {"Hello ", new Integer(15), new Boolean(false)};<br>
	 * System.out.println(joinBySeps(arr, ','));<br><br>
	 * ... result of print is<br><br> 
	 * Hello,15,false
	 * </code>
	 * 
	 * @param args
	 *            Object[] with items to join in text form
	 * @param sep
	 *            char of separator to insert between arguments from args
	 * @param start -
	 *            int value for the start index of item (inclusively) in the
	 *            array to append
	 * @param end -
	 *            int value for the end index (exclusively) of item in the array
	 *            to append
	 * @return String object resulting join by separator operation
	 */
	public static String joinBySeps( final Object[] args, char sep, int start,
	        int end )
	{
		if ( args == null )
			return "null";
		final int len;
		if ( ( len = args.length ) == 0 )
			return "";
		final StringBuffer sb = new StringBuffer(); //НЕ МЕНЯТЬ на StringBuilder - нет в java 4.0 !!!!!!(Влад)
		sb.append( args[ start ] );
		for ( int index = start + 1; index < end; index++ )
		{
			sb.append( sep );
			sb.append( args[ index ] );
		}
		return sb.toString();
	}

	public static String joinBySeps( final int[] args, char sep )
	{
		if ( args == null )
			return "null";
		final int len;
		if ( ( len = args.length ) == 0 )
			return "";
		final StringBuffer sb = new StringBuffer(); //НЕ МЕНЯТЬ на StringBuilder - нет в java 4.0 !!!!!!(Влад)
		sb.append( args[ 0 ] );
		for ( int index = 1; index < len; index++ )
		{
			sb.append( sep );
			sb.append( args[ index ] );
		}
		return sb.toString();
	}

	/**
	 * frames each item with prefix and suffix
	 * 
	 * @param items
	 *            String[] array of items
	 * @param prefix
	 *            String to set BEFORE item. May be <code>null</code>
	 * @param suffix
	 *            String to set AFTER item. May be <code>null</code>
	 * @return the same array of items but framed with prefixes and suffixes
	 */
	public static String[] frameItems( String[] items, String prefix,
	        String suffix )
	{
		if ( items == null )
			return null;
		if ( prefix == null )
			prefix = "";
		if ( suffix == null )
			suffix = "";
		for ( int index = 0; index < items.length; index++ )
			items[ index ] = prefix + items[ index ] + suffix;
		return items;
	}

	/**
	 * Searches the string for the first occurrence of any of symbols
	 * 
	 * @param str
	 *            String to search for symbols
	 * @param symbols
	 *            set of chars to find in the source string
	 * @return position of first found symbol from string symbols or -1 if no
	 *         such one was found
	 */
	public static int indexOf( String str, String symbols )
	{
		return indexOf( str, 0, symbols );
	}

	/**
	 * Searches the string for the first occurrence of any of symbols
	 * 
	 * @param str
	 *            String to search for symbols
	 * @param start
	 *            starting index of searching, first offset is 0
	 * @param symbols
	 *            set of chars to find in the source string
	 * @return position of first found symbol from string symbols or -1 if no
	 *         such one was found
	 */
	public static int indexOf( String str, int start, String symbols )
	{
		if ( isEmpty( str ) || isEmpty( symbols ) || ( str.length() <= start ) )
			return -1;
		if ( start < 0 )
			start = 0;
		char ch;
		for ( int i = start; i < str.length(); i++ )
			if ( symbols.indexOf( str.charAt( i ) ) >= 0 )
				return i;
		return -1;
	}

	/**
	 * Puts the string to the char array, overwriting the information in it.
	 * 
	 * @param str
	 *            String to put into an array
	 * @param arr
	 *            array of char
	 * @param from
	 *            index in array to put the string to
	 * @exception IndexOutOfBoundsException
	 *                if <code>from</code> or <code>String.length()</code>
	 *                are incorrect for the indicated char array
	 */
	public static void putToCharArray( String str, char[] arr, int from )
	        throws IndexOutOfBoundsException
	{
		if ( ( from < 0 ) || ( from + str.length() ) >= arr.length )
			throw new IndexOutOfBoundsException(
			        "Impossible to put a string with length " + str.length()
			                + " at the index " + from
			                + " into an array of length " + arr.length );
		str.getChars( 0, str.length() - 1, arr, from );
	}

	/**
	 * Checks if String is empty (no contents) or {@code null}
	 * 
	 * @param str
	 *            String to check
	 * @return <code>true</code> if argument is <code>null</code> or
	 *         contains only space characters, else <code>false</code>
	 */
	public static boolean isEmpty( String str )
	{
		return ( str == null ) || ( str.trim().length() == 0 );
	}

	/**
	 * Checks if obj is empty, that is <code>null</code> or no real symbols if
	 * obj is a String
	 * 
	 * @param arr
	 *            Object or String to test
	 * @return <code>true</code> if obj reference is <code>null</code> or
	 *         ((String)obj) has no symbols except blank ones, else
	 *         <code>false</code>
	 */
	public static boolean isEmpty( String[] arr )
	{
		return ( arr == null ) || ( arr.length == 0 );
	}

	/**
	 * Checks if obj is empty, that is <code>null</code> or no real symbols if
	 * obj is a String
	 * 
	 * @param obj
	 *            Object or String to test
	 * @return <code>true</code> if obj reference is <code>null</code> or
	 *         ((String)obj) has no symbols except blank ones, else
	 *         <code>false</code>
	 */
	public static boolean isEmpty( Object obj )
	{
		if ( obj == null )
			return true;
		if ( obj instanceof String )
			return ( (String) obj ).trim().length() == 0;
		return false;
	}

	/**
	 * Replaces commas with point
	 * 
	 * @param str
	 *            string with potential commas
	 * @return String with points except possible commas
	 */
	public static String replaceCommaByPoint( String str )
	{
		return str.replace( ',', '.' );
	}

	public static String format_int( String format, int arg )
	{
		return "";
	}

}
