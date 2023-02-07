/**
 * Created on 17.03.2009 12:33:15<br> 
 * by Syg<br> 
 * for project in 'ru.ts.common.misc'
 */
package reclass.cmd;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * <pre>
 * Package ru.ts.common.misc
 * Author 'Syg'<br>
 * Created  17.03.2009 12:33:15
 * <br> used to process command line of a follow format: <-a val> ... [-aN valN], where<br>
 * '-a' the <b>1 symbol</b> key of the argument and 'val' is a value for the key,
 * so pairs of key/value only should be recognised in the command line. If no
 * value in the pair, it assumed to be absent, if no key, the value is dropped
 * from processing. Key can support optional modifiers, for example -a[bcdef],
 * they can be accessed separatedly from a key by corresponding method call.
 * <b>Keys are case sensitive and are examined as a 1 symbol items with trailing subkeys!!!</b>
 * And lastly keys are one symbol items !!!
 * *
 * Example: "-i C:\dir\file.txt -o C:\dir\output.txt -DSomePath" will be processed
 * into 3 pairs of
 *   key    value
 * 1. "-i", "C:\dir\input.txt"
 * 2. "-o", "C:\dir\output.txt"
 * 3. "-dSomePath", null
 * *
 * and you can get them as follow:
 * ...
 * public static void main(String[] args)
 * ...
 *  ICmdAgrs cmd = new ICmdArgs.Impl( args );
 *  String param1 = cmd.value( "-i" ); // {@code "C:\dir\input.txt"}
 *  String param2 = cmd.value( "-o" ); // {@code "C:\dir\output.txt"}
 *  String param3 = cmd.value( "-d" ); // {@code null}
 *  String mods3 = cmd.modifiers( "-d" ); // {@code "SomePath"}
 * ...
 * <p/>
 * Extended functionaly contains follow news:
 * key can be combined with value in 3 ways:
 * 1. by space, e.e. "-i <input_path>"
 * 2. by semicolor, e.g. -i:<inpit_path>
 * 3. by equal sign, e.g. -i=<inpit_path>
 * <p/>
 * all other cases expects that follow value (no '-' or '/' prefix argument) is single value, not next pair.
 * So extended expample: "-i C:\dir\file.txt -o C:\dir\output.txt -DSomePath <path2> <path3>" will be processed into
 * 5 items:
 *   key    value
 * 1. "-i", "C:\dir\input.txt"
 * 2. "-o", "C:\dir\output.txt"
 * 3. "-dSomePath", null
 * 4. null, <path2>
 * 5. null, <path3>
 * and 4th and 5th valaues can be extracted by call to value(0) and value(1) as they are values without keys and are
 * stored separately of pair key/value
 * ...
 *  ICmdAgrs cmd = new ICmdArgs.Impl( args );
 *  String param1 = cmd.value( "-i" ); // {@code "C:\dir\input.txt"}
 *  String param2 = cmd.value( "-o" ); // {@code "C:\dir\output.txt"}
 *  String param3 = cmd.value( "-d" ); // {@code null}
 *  String mods3 = cmd.modifiers( "-d" ); // {@code "SomePath"}
 *  int valCnt = valCount(); // {@code 2}
 *  String val1 = cmd.value(0); // {@code "<path2>"}
 *  String val2 = cmd.value(1); // {@code "<path3>"}
 * </pre>
 */
public interface ICmdArgs
{
	/**
	 * Returns count of the keys found in the command line
	 *
	 * @return int value for the cmdline key count
	 */
	int count();

	/**
	 * Returns count of the values existing without keys
	 *
	 * @return int value for the cmdline non-key value count
	 */
	int valCount();

	/**
	 * Gets iterator for each key found in a command line.
	 *
	 * @return {@link java.util.Iterator} for String instance
	 */
	Iterator getKeyIterator();

	/**
	 * Return {@code true} if no correct arguments we used for this application, or
	 * {@code false} if there were arguments. Correct arguments are the arguments with at least
	 * one key used, for example "java program key1 val1" are illegal args, but
	 * "java program -k1 valk1 - k2 valk2" are legal.
	 *
	 * @return boolean value of {@code true} if there is at least one key in command line arguments,
	 *         else {@code false}
	 */
	boolean isEmpty();

	/**
	 * Gets the value of the designated key
	 *
	 * @param key String with the wanted key
	 * @return String with a value for the designated key, or {@code null} if no
	 *         such key
	 */
	String value( String key );

	/**
	 * gets value with default option, so if key or value is absent default value
	 * is returned instead
	 *
	 * @param key    String with the wanted key
	 * @param defval default value to return for absent key/value
	 * @return String with a value for the designated key, or defval if no
	 *         such key/value
	 */
	String value( String key, String defval );

	/**
	 * Gets the value of the designated key
	 *
	 * @param key char with the wanted key char. Heading '-' as prepended to the value before processing
	 * @return String with a value for the designated key, or {@code null} if no such key
	 */
	String value( char key );

	/**
	 * Gets first found value for any of designated keys
	 *
	 * @param keys array of keys in char form
	 * @return String with a value for first detected key in array or {@code null} if no such one
	 */
	public String value( char[] keys );

	/**
	 * gets value with default option, so if key or value is absent default value
	 * is returned instead
	 *
	 * @param key    String with the wanted key
	 * @param defVal default value to return for absent key/value
	 * @return String with a value for the designated key, or defval if no
	 *         such key/value
	 */
	String value( char key, String defVal );

	/**
	 * gets value with default option, so if key or value is absent default value
	 * is returned instead
	 *
	 * @param keys   char array with designated key group
	 * @param defVal default value to return for absent key/value
	 * @return String with a value for the first detected of designated key array, or defval if no
	 *         such key/value
	 */
	String value( char[] keys, String defVal );

	/**
	 * Gets single value with designated index
	 *
	 * @param index index for value to get
	 * @return String with value at designated value or {@code null} if index out of range
	 */
	String value( int index );

	/**
	 * Gets single value with designated index or defaul one in out of range
	 *
	 * @param index index for value to get
	 * @return String with value at designated value or defVal if index out of range
	 */
	String value( int index, String defVal );

	/**
	 * Returns {@code true} if a command line have had the designated key
	 *
	 * @param key String with a designated key. Key text is key sensitive
	 * @return {@code true} if such key exists, else {@code false}
	 */
	boolean hasKey( String key );

	/**
	 * Returns {@code true} if a command line have had the designated key
	 *
	 * @param key char with a designated key. '-' prepended to key value before processing
	 * @return {@code true} if such key exists, else {@code false}
	 */
	boolean hasKey( char key );

	/**
	 * Returns modifiers of the key. Modifiers are any symbols situated directly
	 * after minus sign and the key char, e.g. if "-k" is key , in the string "-kabcd"
	 * the substring "abcd" will be modifiers
	 *
	 * @param key String for the key designated
	 * @return String with modifiers, may be {@code null} if no modifiers or no
	 *         such key exists :o)
	 */
	String modifiers( String key );

	/**
	 * The synonim  of {@link ICmdArgs#hasKey(String)}
	 *
	 * @param key key to check for existence
	 * @return {@code true} if key exists, else {@code false}
	 */
	boolean contains( String key );

	boolean contains( char key );

	boolean contains( char... keys );

	/**
	 * Adds one option to emulate it
	 *
	 * @param key String with a key value. Length >= 2
	 * @param val String with a data value. May be empty
	 */
	public void add( final String key, final String val );

	/**
	 * Parse integer value from string
	 *
	 * @param val    String with integer value
	 * @param defVal default value to return if string is not integer number
	 * @return int value parsed or defVal on null or bad string
	 */
	int parseInt( String val, int defVal );

	/**
	 * Parse double value from string
	 *
	 * @param val    String with double value
	 * @param defVal default value to return if string is not double number
	 * @return double value parsed or defVal on null or bad string
	 */
	double parseDouble( String val, double defVal );

	//++++++++++++++++++++++++++++++++++ Realization ++++++++++++++++++++++++++//

	public static class Impl implements ICmdArgs
	{

		/**
		 * Iterator for a keys
		 */
		static class MyIterator implements Iterator<String>
		{
			private Iterator<String> m_iter;

			MyIterator( Iterator<String> iter )
			{
				m_iter = iter;
			}

			public boolean hasNext()
			{
				return m_iter.hasNext();
			}

			public String next()
			{
				return m_iter.next().substring( 0, 2 );
			}

			public void remove()
			{
				m_iter.remove();
			}
		}


		private Map _map;
		private ArrayList<String> _vals;

		public Impl( String[] args )
		{
			init( args );
		}

		public Impl()
		{
		}

		/**
		 * Reset all previous setting and starts from zero as if constructor was called
		 *
		 * @param args String[]
		 */
		public void reset( String[] args )
		{
			init( args );
		}

		private void init( String[] args )
		{
			_map = new LinkedHashMap( ( args.length / 2 ) + 1 );
			_vals = new ArrayList<String>();
			for ( int i = 0; i < args.length; i++ )
			{
				String key = args[ i ];
				String val = null;
				if ( isAKey( key ) )
				{
					// check if key already have value appended to key by separator without spaces
					if ( key.length() > 2 )
					{
						if ( key.charAt( 2 ) == ':' || key.charAt( 2 ) == '=' )
							if ( key.length() > 3 )
								val = key.substring( 3 );
						key = key.substring( 0, 2 );
					}
					if ( val != null )
					{
						addEntry( key, val );
						continue;
					}
					// it is a single key so we expect value as next argument
					if ( i < ( args.length - 1 ) )// check for a next argument as a value for this key
					{
						val = args[ i + 1 ];
						if ( isAKey( val ) )// then dont use as a value but next key
						{
							val = null;
						}
						else
						{
							i++;// skip value from list of args
						}
					}
					addEntry( key, val );
				}
				else // it is single value only
				{
					_vals.add( args[ i ] );
				}
			}
		}

		/**
		 * Removes keys with ot without values only
		 *
		 * @param args String[] with command line arguments
		 * @return String[] with input values except of keys with or without value pairs
		 */
		public static String[] removeKeysWithVals( String[] args )
		{
			final DirectStringArray dsa = new DirectStringArray();
			final int cnt = args.length;
			final int lastIndex = cnt - 1;
			for ( int i = 0; i < cnt; i++ )
			{
				String arg = args[ i ];
				if ( isAKey( arg ) )// skip it
				{
					if ( i < lastIndex )// and its value
					{
						if ( !isAKey( args[ i + 1 ] ) )
						{
							i++;
						}
					}
				}
				else
				{
					dsa.add( arg );
				}
			}
			return (String[]) dsa.toArray();
		}

		/**
		 * Removes keys only
		 *
		 * @param args String[] with command line arguments
		 * @return String[] with input values except of keys (arguments starting with '-' symbol
		 */
		public static String[] removeKeysOnly( String[] args )
		{
			final DirectStringArray dsa = new DirectStringArray();
			for ( int i = 0; i < args.length; i++ )
			{
				String arg = args[ i ];
				if ( !isAKey( arg ) )// skip key only
				{
					dsa.add( arg );
				}
			}
			return (String[]) dsa.toArray();
		}

		private static boolean isAVal( String val )
		{
			return val.charAt( 0 ) != '-';
		}

		private static boolean isAKey( String str )
		{
			return ( str.startsWith( "-" ) || str.startsWith( "/" ) ) && str.length() > 1;
		}

		public int count()
		{
			return _map.size();
		}

		public int valCount()
		{
			return _vals.size();
		}

		public Iterator getKeyIterator()
		{
			return new MyIterator( _map.keySet().iterator() );
		}

		public boolean isEmpty()
		{
			return _map.size() == 0;
		}

		public String value( String key )
		{
			String val = (String) _map.get( key );
			if ( val == null )
			{
				return null;
			}
			return parseVal( val );
		}

		public String value( String key, String defval )
		{
			final String val = value( key );
			return val == null ? defval : parseVal( val );
		}

		public String value( char key )
		{
			return value( "-" + key );
		}

		public String value( char[] keys )
		{
			for ( int i = 0; i < keys.length; i++ )
			{
				if ( hasKey( keys[ i ] ) )
				{
					return value( "-" + keys[ i ] );
				}
			}
			return null;
		}

		public String value( char key, String defVal )
		{
			return value( "-" + key, defVal );
		}

		public String value( char[] keys, String defVal )
		{
			for ( int i = 0; i < keys.length; i++ )
			{
				if ( hasKey( keys[ i ] ) )
				{
					return value( "-" + keys[ i ] );
				}
			}
			return defVal;
		}

		public String value( int index )
		{
			if ( index < 0 || index >= valCount() )
			{
				return null;
			}
			return _vals.get( index );
		}

		public String value( int index, String defVal )
		{
			if ( index < 0 || index >= valCount() )
			{
				return defVal;
			}
			return _vals.get( index );
		}

		private String parseVal( String val )
		{
			int pos = val.indexOf( '\0' ) + 1;
			if ( pos <= 0 )
			{
				return val;
			}
			return val.substring( pos );
		}

		private String parseMods( String val )
		{
			int pos = val.indexOf( '\0' );
			if ( pos < 0 )
			{
				return null;
			}
			return val.substring( 0, pos );
		}

		public boolean hasKey( String key )
		{
			return _map.containsKey( key );
		}

		public boolean hasKey( char key )
		{
			return hasKey( "-" + key );
		}

		public String modifiers( String key )
		{
			String val = (String) _map.get( key );
			if ( val == null )
			{
				return null;
			}
			return parseMods( val );
		}

		public boolean contains( String key )
		{
			if ( key != null && key.length() > 0 )
			{
				if ( key.charAt( 0 ) != '-' )
				{
					key = "-" + key.substring( 1 );
				}
			}
			return hasKey( key );
		}

		public boolean contains( char key )
		{
			return contains( "-" + key );
		}

		public boolean contains( char... keys )
		{
			for ( char key : keys )
			{
				if ( contains( "-" + key ) )
				{
					return true;
				}
			}
			return false;
		}

		public Iterator iterator()
		{
			return getKeyIterator();
		}

		private void addEntry( String key, String val )
		{
			if ( ( key == null) || (key.length() < 2) )
				return;
			if ( key.charAt( 0 ) == '/' )
				key = "-" + key.substring( 1 );
			if ( key.charAt( 0 ) != '-' )
				key = "-" + key.charAt( 0 );
			if ( key.length() > 2 )// there is a modifiers of the key, e.g. -k[modifiers]
			{
				String mods = key.substring( 2 );
				val = mods + '\0' + ( val == null ? "" : val );

				key = key.substring( 0, 2 );
			}
			_map.put( key, val );
		}

		public void add( final String key, final String val )
		{
			addEntry( key, val );
		}

		public int parseInt( String val, int defVal )
		{
			try
			{
				return Integer.parseInt( val );
			}
			catch ( NumberFormatException e )
			{
				return defVal;
			}
		}

		public double parseDouble( String val, double defVal )
		{
			try
			{
				return Double.parseDouble( val );
			}
			catch ( Exception e )
			{
				return defVal;
			}
		}
	}
}