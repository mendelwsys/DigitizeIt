package ru.ts.common.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 22.01.14
 * Time: 21:56
 * To change this template use File | Settings | File Templates.
 */
public class Text extends ru.ts.utils.Text
 {
     public static double [] splitDoubles( String str, char delim,
         boolean countEmpty)
     {

         String[] res = splitItems(str, delim, countEmpty);
         double [] rv=new double[res.length];
         for (int i = 0, resLength = res.length; i < resLength; i++)
             rv[i]=Double.parseDouble(res[i]);
         return rv;
     }

     public static int [] splitInts( String str, char delim,
         boolean countEmpty)
     {

         String[] res = splitItems(str, delim, countEmpty);
         int [] rv=new int[res.length];
         for (int i = 0, resLength = res.length; i < resLength; i++)
             rv[i]=Integer.parseInt(res[i]);
         return rv;
     }

     public static void sout(String s) {
         System.out.println(s);
     }

     public static void serr(String s) {
         System.err.println(s);
     }


     /**
      * Gets next word from a string. Skips all space characters before possible word. Call {@link #skipWord(String, int)}
      * to find next position after the word
      *
      * @param val String to get from
      * @param pos int position ot find word from
      * @return String with a word found or empty string if end of string reqched
      */
     public static String getWord( String val, int pos )
     {
         // skip spaces just in case
         pos = Text.skipSpaces( val, pos );
         if ( pos >= val.length() )
             return "";
         // now we are at word start
         int pos1 = Text.skipNonSpaces( val, pos );
         return val.substring( pos, pos1 );
     }

     /**
      * Checks if designated line consists of order of designated words
      * @param line String to check for order presence
      * @param words String[] with words to be checked
      * @return {@code true} if line consists of designated words order else {@code false}
      */
     public static boolean orderOfWords(String line, String[] words)
     {
       if ( Text.isEmpty( words ) )  // empty set always is presented in any line :o)
         return true;
       int pos = 0;
       final int size = words.length;
       final int len = line.length();
       for( int cnt = 0; cnt < size; cnt++ )
       {
         String word = getWord( line, pos );
         if ( Text.isEmpty(word) || ( !word.equalsIgnoreCase( words[cnt] ) ) )
           return false;
         pos = skipWord( line, pos );
       }
       return true;
     }


     /**
      * Method skips all white spaces in the designated string beginning from pos
      *
      * @param str String with text to test
      * @param pos offset to begin skipping
      * @return int index >= pos with first non white space symbol position. If no such
      *         symbols  found, index pos after last symbol in the string is returned (str.length())
      */
     public static int skipSpaces( final String str, final int pos )
     {
         final int len = str.length();
         for ( int i = pos; i < len; i++ )
         {
             if ( !Character.isWhitespace( str.charAt( i ) ) )
             {
                 return i;
             }
         }
         return len;
     }

     /**
      * Method skips all non white spaces in the designated string beginning from pos
      *
      * @param str String with text to test
      * @param pos offset to begin skipping
      * @return int index >= pos with first white space symbol position. If no such
      *         symbols  found, index pos after last symbol in the string is returned (str.length())
      */
     public static int skipNonSpaces( final String str, final int pos )
     {
         final int len = str.length();
         for ( int i = pos; i < len; i++ )
         {
             if ( Character.isWhitespace( str.charAt( i ) ) )
             {
                 return i;
             }
         }
         return len;
     }

     /**
      * Unescapes with designated esChar character, usually '\'
      *
      * @param txt    text to unescape
      * @param esChar escape character, usually '\'
      * @return String with unescaped string
      */
     public static String unEscape( String txt, char esChar )
     {
         final StringBuffer sb = new StringBuffer( txt.length() );
         int len = txt.length();
         char ch;
         for ( int i = 0; i < len; i++ )
         {
             ch = txt.charAt( i );
             if ( ch == esChar )
             {
                 if ( i >= ( len - 1 ) )// skip escape char if not EOString
                 {
                     sb.append( txt.charAt( ++i ) );
                 }
                 // if last symbol at string is an escape one we skip it
             }
             else
             {
                 sb.append( ch );// copy current character
             }
         }
         if ( sb.length() == len )
         {
             return txt;// nothing was changed
         }
         return sb.toString();
     }

     /**
      * Unescapes string with predefined '\' escape char
      *
      * @param txt text to unescape
      * @return String with unescaped string
      */
     public static String unEscapeStd( String txt )
     {
         return unEscape( txt, '\'' );
     }

     /**
      * Read the whole String from a {@link Reader} instance
      *
      * @param rd {@link Reader} instance to read from
      * @return String with a whole rd content
      * @throws IOException on any error
      */
     public static String readString( Reader rd ) throws IOException
     {
         final StringBuffer sb = new StringBuffer();
         int c;
         while ( ( c = rd.read() ) >= 0 )
         {
             sb.append( ( char ) c );
         }
         return sb.toString();
     }

     /**
      * Tests if the substring of this string beginning at the
      * specified index starts with the specified prefix caseinsensitive manner.
      *
      * @param value   String to check for
      * @param prefix  the prefix.
      * @param toffset where to begin looking in this string.
      * @return <code>true</code> if the character sequence represented by the
      *         argument is a caseinsensitive prefix of the substring of this object starting
      *         at index <code>toffset</code>; <code>false</code> otherwise.
      *         The result is <code>false</code> if <code>toffset</code> is
      *         negative or greater than the length of this
      *         <code>String</code> object; otherwise the result is the same
      *         as the result of the expression
      *         <pre>
      *                 this.substring(toffset).startsWith(prefix)
      *                 </pre>
      */
     public static boolean startsWithNoCase( String value, String prefix, int toffset )
     {
         int pc = prefix.length();
         // Note: toffset might be near -1>>>1.
         if ( ( toffset < 0 ) || ( toffset > ( value.length() - pc ) ) )
             return false;
         return prefix.equalsIgnoreCase( value.substring( toffset, toffset + pc ) );
     }

     /**
      * Checks if str1 starts with str2 caseinsentively
      *
      * @param str1 String to search in
      * @param str2 String with no case sample to search
      * @return {@code true} if str1 starts with str2 with no case sensitive or {@code false} if not
      */
     public static boolean startsWithNoCase( String str1, String str2 )
     {
         return startsWithNoCase( str1, str2, 0 );
     }

     public static int compareTexts( String str1, String str2 )
     {
         return compareTexts( str1, str2, false );
     }

     /**
      * Compares two text string lexically as for {@link String#compareTo(String)}. Additionally
      * checks if any of string or both are {@code null}
      *
      * @param str1       first String to compare
      * @param str2       second String to compare
      * @param ignoreCase if {@code true} comparison is ignored the case else not ignore
      * @return negative, zero or positive depending which string is lexigrafically greater
      */
     public static int compareTexts( String str1, String str2, boolean ignoreCase )
     {
         if ( str1 != null )
         {
             if ( str2 == null )
                 return 1;
         }
         else
         {
             if ( str2 != null )
                 return -1;
             return 0;// both are null
         }
         if ( ignoreCase )
             return str1.compareToIgnoreCase( str2 );
         return str1.compareTo( str2 );
     }

     /**
      * Any symbols in 'str' presented in 'chars' is replaced with value of 'replacement' character
      *
      * @param str
      * @param chars
      * @param replacement
      * @return
      */
     public static String replaceAnyChar( String str, String chars, char replacement )
     {
         boolean changed = false;
         if ( str == null )
         {
             return null;
         }
         if ( ( chars == null ) || ( chars.length() == 0 ) )
         {
             return str;
         }
         int cnt;
         final char[] chrs = new char[cnt = str.length()];
         int j = 0;
         for ( int i = 0; i < cnt; i++ )
         {
             char chr = str.charAt( i );
             if ( chars.indexOf( chr ) < 0 )
             {
                 chrs[ j++ ] = chr;
             }
             else
             {
                 chrs[ j++ ] = replacement;
                 changed = true;
             }
         }
         if ( !changed )// no replacements found
         {
             return str;
         }
         return new String( chrs, 0, j );
     }


     /**
      * Detects if the string has only blank symbols. Blank symbol is any one that
      * has integer value smaller than the space (' ') one;
      *
      * @param str
      * @return
      */
     public static boolean notEmpty( String str )
     {
         return !isEmpty( str );
     }

     /**
      * Checks if obj is empty, that is <code>null</code> or no real symbols in a String
      *
      * @param arr String[] to check for emptiness
      * @return <code>true</code> if obj reference is <code>null</code> or
      *         ((String)obj) has no symbols except blank ones, else
      *         <code>false</code>
      */
     public static boolean isEmpty( String[] arr )
     {
         return ( arr == null ) || ( arr.length == 0 );
     }

     /**
      * Checks if any String in the checked array is empty, that is a <code>null</code> or have
      * no real symbols in the String
      *
      * @param arr String[] to check for emptiness
      * @return <code>true</code> if obj reference is <code>null</code> or
      *         String[] have at least one blank string, else
      *         <code>false</code>
      */
     public static boolean isAnyItemEmpty( String[] arr )
     {
         if ( isEmpty( arr ) )
             return true;
         for ( int i = 0; i < arr.length; i++ )
             if ( isEmpty( arr[ i ] ) )
                 return true;
         return false;
     }

     private static String m_defcp = "";
     private static final Object m_lock = new Object();

     /**
      * Returns default charset used by this OS at the start of Java virtual engine
      *
      * @return String with default charset name
      */
     public static String defaultCharSet()
     {
         if ( Text.notEmpty( m_defcp ) )
         {
             synchronized ( m_lock )
             {
                 if ( Text.notEmpty( m_defcp ) )
                 {
                     return m_defcp;
                 }
                 File file = null;
                 FileWriter filewrt = null;
                 try
                 {
                     file = File.createTempFile( "$temp", null );
                     filewrt = new FileWriter( file );
                     return m_defcp = filewrt.getEncoding();
                     //System.out.println("Default encoding is :"+defaultcharset);
                 }
                 catch ( IOException e )
                 {
                 }
                 finally
                 {
                     if ( filewrt != null )
                     {
                         try
                         {
                             filewrt.close();
                         }
                         catch ( IOException e )
                         {
                         }
                     }
                     if ( file != null )
                     {
                         try
                         {
                             file.delete();
                         }
                         catch ( Exception e )
                         {
                         }
                     }
                 }
             }
         }
         return "Cp1251";
     }


     /**
      * Finds the word from a line situated between 'from' sample and 'to'
      * sample.<br> For example, if a line contains "Text to find is
      * BEFORE=FINDMENOW;AFTER", <br> the call to findWord( line, 0, "BEFORE=",
      * ";AFTER" ) will return "FINDMENOW"
      *
      * @param line  String with whole text line
      * @param pos   int with start search position for a 'from' sample
      * @param from  String with sample BEFORE the word
      * @param after String with sample AFTER the word
      * @return String with a found word, or {@code null} if bad parameters or no 'from' or 'after'
      *         samples were found
      */
     public static String findWord( String line, int pos, String from, String after )
     {
         if ( line == null || from == null || after == null )
             return null;
         pos = line.indexOf( from, pos );
         if ( pos < 0 )
         {
             return null;
         }
         pos += from.length();
         int pos1 = line.indexOf( after, pos );
         if ( pos1 < 0 )
         {
             return null;
         }
         return line.substring( pos, pos1 );
     }

     /**
      * Skips next word in the string. really it skips all spaces BEFORE word and a word itself.
      *
      * @param val String to skip at
      * @param pos index of (space[s] before) word position.
      * @return int index of the first space after word or val.length if end of string reached
      */
     public static int skipWord( String val, int pos )
     {
         // skip spaces just in case
         pos = Text.skipSpaces( val, pos );
         // now we are at coordinate text
         return Text.skipNonSpaces( val, pos );
     }



 }
