/**
 *</pre>
 * Created on 28.08.2009 11:51:41<br> 
 * by Syg<br> 
 * for project in 'ru.ts.common.records'
 *</pre>
 */
package reclass.cmd;

import java.util.*;

/**
 * Package ru.ts.common.records<br> Author 'Syg'<br> Created  28.08.2009
 * 11:51:41<br> Works to handle with records containing only string in a robust
 * and an effective way. Records can be added, removed and changed.
 * In future versions it may be improved father. And of course you can clean the
 * whole bunch :o)<br><br>
 * <p/>
 * <b>Class is not thread safe!</b>
 */
public class DirectStringArray implements List
{

	class Itr implements Iterator
  {
    int m_pos;
    int m_lastPos;

    public Itr()
    {
      m_pos = m_lastPos = 0;
    }

    public boolean hasNext()
    {
      return m_pos < size();
    }

    public Object next()
    {
      return get( m_lastPos = m_pos++ );
    }

    /* @exception IllegalStateException if the <tt>next</tt> method has not
    *		  yet been called, or the <tt>remove</tt> method has already
    *		  been called after the last call to the <tt>next</tt>
    *		  method.
    */

    public void remove()
    {
      DirectStringArray.this.remove( m_lastPos );
    }
  }

  class ListItr extends Itr implements ListIterator
  {
    ListItr( int pos )
    {
      m_pos = pos;
    }

    public boolean hasPrevious()
    {
      return m_pos > 0;
    }

    public Object previous()
    {
      return get( m_lastPos = --m_pos );
    }

    public int nextIndex()
    {
      return m_pos;
    }

    public int previousIndex()
    {
      return m_pos - 1;
    }

    public void set( final Object str )
    {
      DirectStringArray.this.set( m_lastPos, str );
    }

    public void add( final Object str )
    {
      DirectStringArray.this.add( m_pos, str );
    }
  }

  /**
   * The only goal is to insert method to increment offsets after new offset
   * insertion in the middle of the array
   */
  class DirectOffsetArray extends DirectIntArray
  {

    DirectOffsetArray( final int size )
    {
      super( size );
    }

    public DirectOffsetArray( final DirectOffsetArray strOffs )
    {
      super( strOffs );
    }

    void incOffsetsFrom( int index, int offset )
    {
      if ( offset == 0 )  // no need for incrementing values
        return;
      int[] base = this.baseArray;
      for ( int i = index; i < this.size(); i++ )
        base[ i ] += offset;
    }

    /**
     * Allows to get offset for the string with designated index
     *
     * @param index int with index for the string to get offset
     *
     * @return int with offset for the string or throw {@link
     *         ArrayIndexOutOfBoundsException} exception on illegal index used
     */
    public int get( int index )
    {
      if ( index == size() )
        return _buf.length();
      return super.get( index );
    }

    /**
     * Gets the length of the string with designated index
     *
     * @param index int with string index
     *
     * @return int with designated string length or throws {@link
     *         ArrayIndexOutOfBoundsException}
     */
    public int getLength( int index )
    {
      return get( index + 1 ) - get( index );
    }
  }

  DirectOffsetArray _strOffs;
  StringBuffer _buf;

	/**
	 * Constructor from iterator
	 *
	 * @param stringIterator
	 */
	public DirectStringArray( Iterator stringIterator )
	{
		this();
		for ( Iterator iterator = stringIterator; iterator.hasNext(); )
		{
			String s = ( String ) iterator.next();
			this.add( s );
		}
	}

	/**
	 * Constructor from string array
	 *
	 * @param arr String[] to init
	 */
	public DirectStringArray( String[] arr )
	{
		this(arr.length);
		for ( int i = 0; i < arr.length; i++ )
		{
			add( arr[i]);
		}
	}

	/**
	 * Constructor for the single string
	 *
	 * @param singleString the single string to init out class instance
	 */
	public DirectStringArray( String singleString )
	{
		this(1);
			add( singleString );
	}

	/**
   * Main constructor
   */
  public DirectStringArray()
  {
    this( 16 );
  }

  /**
   * Main constructor
   */
  public DirectStringArray( int capacity )
  {
    _strOffs = new DirectOffsetArray( capacity );
    _buf = new StringBuffer( capacity * 16 );
  }

  /**
   * Clone constructor
   */
  public DirectStringArray( DirectStringArray dsa )
  {
    _strOffs = new DirectOffsetArray( dsa._strOffs );
    _buf = new StringBuffer( dsa._buf.length() );
    _buf.append( dsa._buf );
  }

  /**
   * Returns number of strings stored
   *
   * @return int value with number of strings stored
   */
  public int size()
  {
    return _strOffs.size(); // one offset for each string
  }

  public boolean isEmpty()
  {
    return size() == 0;
  }

  public boolean contains( final Object o )
  {
    return indexOf( o ) >= 0;
  }

  public Iterator iterator()
  {
    return new Itr();
  }

  public Object[] toArray()
  {
    final String[] arr = new String[size()];
    return toArray( arr );
  }

  public Object[] toArray( Object[] a )
  {
    final int size = size();
    if ( a.length < size )
      a = ( String[] ) java.lang.reflect.Array.
        newInstance( a.getClass().getComponentType(), size );
    if ( a.length > size )
      a[ size ] = null;
    for ( int i = 0; i < size; i++ )
      a[ i ] = ( String ) get( i );
    return a;
  }

  /**
   * Checks designated index to be in array boundaries
   *
   * @param index inv value to be checked
   *
   * @return {@code true} if it is in boundaries or {@code false} if not, that is
   *         (index < 0) || (index >= size())
   */
  private boolean checkIndex( int index )
  {
    return ( index >= 0 ) && ( index < _strOffs.size() );
  }

  /**
   * Gets the string at designated index from the array
   *
   * @param index int value of the string index to get
   *
   * @return input String  value or {@code null} if 'index' is out of range
   */
  public Object get( int index )
  {
    if ( checkIndex( index ) )
    {
      int off = _strOffs.get( index );
      int off1;
      try
      {
        off1 = _strOffs.get( index + 1 );
      }
      catch ( Exception e )
      {
        off1 = _buf.length();
      }
      return _buf.substring( off, off1 );
    }
    return null;
  }

  /**
   * Gets the part of the string at designated index
   *
   * @param ind  int value of the string index to get
   * @param off1 start offset int string to get
   * @param off2 end offset in string to get (not included char at this offset)
   *
   * @return input String  value or {@code null} if 'index' is out of range
   */
  public String get( final int ind, final int off1, final int off2 )
  {
    if ( checkIndex( ind ) )
    {
      int end = ( ind == size() - 1 ) ? volume() : _strOffs.get( ind + 1 );
      if ( ( off1 >= 0 ) && ( off2 > off1 ) && ( off2 <= end ) )
      {
        int off = _strOffs.get( ind ) + off1;
        int offend = off + off2;
        return _buf.substring( off, offend );
      }
    }
    return null;
  }

  public Object set( final int index, final Object element )
  {
    this.replace( index, element );
    return element;
  }

  public void add( final int index, final Object element )
  {
    this.insert( element, index );
  }

  public Object remove( final int index )
  {
    String ret = ( String ) get( index );
    delete( index );
    return ret;
  }

  public int indexOf( final Object o )
  {
    if ( isEmpty() )
      return -1;
    if ( !o.getClass().isAssignableFrom( String.class ) )
      return -1;
    String s = ( String ) o;
    char[] chrs = s.toCharArray();
    int off = _strOffs.get( 0 ), off1;
    for ( int i = 0; i < size() - 1; i++ )
    {
      off1 = _strOffs.get( i + 1 );
      if ( compare( off, off1 - off, chrs ) == 0 )
        return i;
      off = off1;
    }
    off1 = volume();
    if ( compare( off, off1 - off, chrs ) == 0 )
      return size() - 1;
    return -1;
  }

  public int indexWith( final Object o )
  {
    if ( isEmpty() )
      return -1;
    if ( !o.getClass().isAssignableFrom( String.class ) )
      return -1;
    String s = ( String ) o;
    char[] chrs = s.toCharArray();
    int off = _strOffs.get( 0 ), off1;
    for ( int i = 0; i < size() - 1; i++ )
    {
      off1 = _strOffs.get( i + 1 );
      if ( compareW( off, off1 - off, chrs ) == 0 )
        return i;
      off = off1;
    }
    off1 = volume();
    if ( compareW( off, off1 - off, chrs ) == 0 )
      return size() - 1;
    return -1;
  }

  /**
   * Returns first string index that starts with designated findStr
   *
   * @param startIndex int value to start search
   * @param findStr    String to find
   *
   * @return positive value if found, negative if no such string
   */
  public int indexOf( int startIndex, final Object findStr )
  {
    if ( isEmpty() )
      return -1;
    if ( !findStr.getClass().isAssignableFrom( String.class ) )
      return -1;
    if ( ( startIndex < 0 ) || ( startIndex >= _strOffs.size() ) )
      return -1;
    String s = ( String ) findStr;
    char[] chrs = s.toCharArray();
    int off = _strOffs.get( startIndex ), off1;
    for ( int i = startIndex; i < ( size() - 1 ); i++ )
    {
      off1 = _strOffs.get( i + 1 );
      if ( compare( off, off1 - off, chrs ) == 0 )
        return i;
      off = off1;
    }
    off1 = volume();
    if ( compare( off, off1 - off, chrs ) == 0 )
      return size() - 1;
    return -1;
  }

  /**
   * Returns first string index that starts with designated findStr
   *
   * @param startIndex int value to start search
   * @param findStr    String to find
   *
   * @return positive value if found, negative if no such string
   */
  public int indexWith( int startIndex, final Object findStr )
  {
    if ( isEmpty() )
      return -1;
    if ( !findStr.getClass().isAssignableFrom( String.class ) )
      return -1;
    if ( ( startIndex < 0 ) || ( startIndex >= _strOffs.size() ) )
      return -1;
    String s = ( String ) findStr;
    char[] chrs = s.toCharArray();
    int off = _strOffs.get( startIndex ), off1;
    for ( int i = startIndex; i < ( size() - 1 ); i++ )
    {
      off1 = _strOffs.get( i + 1 );
      if ( compareW( off, off1 - off, chrs ) == 0 )
        return i;
      off = off1;
    }
    off1 = volume();
    if ( compareW( off, off1 - off, chrs ) == 0 )
      return size() - 1;
    return -1;
  }

  /**
   * Returns first string index that starts with designated findStr
   *
   * @param startIndex int value with String index to start search
   * @param endIndex   int value with a String index (exclusive itself value )to
   *                   be started with findStr
   * @param findStr    String to find
   *
   * @return positive value if found, negative if no such string
   */
  public int indexWith( int startIndex, int endIndex, final Object findStr )
  {
    if ( isEmpty() )
      return -1;
    if ( !findStr.getClass().isAssignableFrom( String.class ) )
      return -1;
    if ( startIndex < 0 )
      return -1;
    if ( ( endIndex <= startIndex ) || ( startIndex >= _strOffs.size() ) )
      return -1;
    String s = ( String ) findStr;
    char[] chrs = s.toCharArray();
    int off = _strOffs.get( startIndex ), off1;
    for ( int i = startIndex; i < endIndex; i++ )
    {
      off1 = _strOffs.get( i + 1 );
      if ( compareW( off, off1 - off, chrs ) == 0 )
        return i;
      off = off1;
    }
    off1 = volume();
    if ( compareW( off, off1 - off, chrs ) == 0 )
      return size() - 1;
    return -1;
  }

  public int lastIndexOf( final Object o )
  {
    if ( isEmpty() )
      return -1;
    if ( !o.getClass().isAssignableFrom( String.class ) )
      return -1;
    String s = ( String ) o;
    char[] chrs = s.toCharArray();
    int off, off1 = _buf.length();
    for ( int i = size() - 1; i > 0; i-- )
    {
      off = _strOffs.get( i );
      if ( compare( off, off1 - off, chrs ) == 0 )
        return i;
      off1 = off;
    }
    off = 0;
    if ( compare( off, off1 - off, chrs ) == 0 )
      return 0;
    return -1;
  }

  public int lastIndexWith( final Object o )
  {
    if ( isEmpty() )
      return -1;
    if ( !o.getClass().isAssignableFrom( String.class ) )
      return -1;
    String s = ( String ) o;
    char[] chrs = s.toCharArray();
    int off, off1 = _buf.length();
    for ( int i = size() - 1; i > 0; i-- )
    {
      off = _strOffs.get( i );
      if ( compareW( off, off1 - off, chrs ) == 0 )
        return i;
      off1 = off;
    }
    off = 0;
    if ( compareW( off, off1 - off, chrs ) == 0 )
      return 0;
    return -1;
  }

  public ListIterator listIterator()
  {
    return new ListItr( 0 );
  }

  public ListIterator listIterator( final int index )
  {
    return new ListItr( index );
  }

  public List subList( final int fromIndex, final int toIndex )
  {
		// todo: optimize to extract the whole sub-buffer and whole sub-list at once
		final int size = toIndex - fromIndex + 1;
    DirectStringArray arr = new DirectStringArray( size );
    for ( int i = fromIndex; i <= toIndex; i++ )
      arr.add( get( i ) );
    return arr;
  }

  /**
   * Adds String to the storage end
   *
   * @param str String with text to append to the array
   */
  public boolean add( Object str )
  {
		_strOffs.add( _buf.length() );
    _buf.append( str );
    return true;
  }

	/**
	 * Adds empty string to the array. Always successfull :o)
	 */
	public void add()
	{
		add( "" );
	}

  /**
   * Appends a subsequence of the specified String to thу end of the list.
   * Characters of the argument str, starting at index start, are appended, in
   * order, to the contents of next list entry up to the (exclusive) index end.
   *
   * @param str   String with text to append to the array
   * @param start - the starting index of the subsequence to be appended. end -
   *              the end index of the subsequence to be appended.
   * @param end   length of the substring
   *
   * @return always <code>true</code>
   */
  public boolean add( Object str, int start, int end )
  {
		_strOffs.add( _buf.length() );
    _buf.append( ( String ) str, start, end );
    return true;
  }

	private int itemLength(int index )
	{
		if ( index != ( size() - 1 ) )
			return _strOffs.get( index + 1 ) - _strOffs.get( index );
		else
			return _buf.length() - _strOffs.get( index );
	}

	public boolean replace( int index, Object str )
  {
    if ( !checkIndex( index ) )
      return false;
    final int lens = ( ( String ) str ).length(); // new length
		int offset = _strOffs.get( index );
		// get current length
		int len = itemLength(index);
		_buf.replace( offset, offset + len, ( String ) str );
    // process offsets
    _strOffs.incOffsetsFrom( index + 1, lens - len );
    return true;
  }

  public boolean remove( final Object o )
  {
    int ind = indexOf( o );
    if ( ind >= 0 )
    {
      remove( ind );
      return true;
    }
    return false;
  }

  public boolean containsAll( final Collection c )
  {
    final Iterator it = c.iterator();
    while ( it.hasNext() )
    {
      Object o = it.next();
      if ( !contains( o ) )
        return false;
    }
    return true;
  }

  public boolean addAll( final Collection c )
  {
    final Iterator it = c.iterator();
    while ( it.hasNext() )
    {
      String s = ( String ) it.next();
      if ( !add( s ) )
        return false;
    }
    return true;
  }

  public boolean addAll( final int index, final Collection c )
  {
    // prepare separate list of the array after insertion
    List sublist = subList( index, size() - 1 );
    // remove all after insertion
    deleteFrom( index );
    // now add the saved list directly after inserted portion
    if ( !addAll( c ) )
      return false;
    return addAll( sublist );
  }

  /**
   * Trims the array from 'index' to the list end
   *
   * @param index int with index to trim from it inclusivelly so the method
   *              {@link this#size()} will return 'index' value
   */
  public void deleteFrom( final int index )
  {
    int off = _strOffs.get( index );
    _buf.setLength( off );
    _strOffs.setSize( index ); //+++ Syg: important error removing
  }

  public boolean removeAll( final Collection c )
  {
    final Iterator it = c.iterator();
    while ( it.hasNext() )
    {
      String s = ( String ) it.next();
      int index = indexOf( s );
      if ( !delete( index ) )
        return false;
    }
    return true;
  }

  public boolean retainAll( final Collection c )
  {
    for ( int i = size() - 1; i >= 0; i-- )
    {
      String s = ( String ) get( i );
      if ( c.contains( s ) )
        continue;
      if ( !delete( i ) )
        return false;
    }
    return true;
  }

  /**
   * Inserts the string before existing string with designated index
   *
   * @param str   String to insert
   * @param index existing index in the array.If it is < 0, string is inserted
   *              <b>before</b> first item, if it is > array lentgh - 1, is
   *              appended to the array
   */
  public void insert( Object str, int index )
  {
    final int len = ( ( String ) str ).length();
    if ( ( str == null ) || ( len == 0 ) )
    {
      _strOffs.add( index, 0 );   // add zero length string
      return;
    }
    int offset;
    if ( index >= size() )
      offset = _buf.length(); // append
    else
    {
      if ( index < 0 )
        index = 0;   // prepend
      offset = _strOffs.get( index );
    }
    _buf.insert( offset, str );

    // process offsets
    _strOffs.add( index, offset );
    _strOffs.incOffsetsFrom( index + 1, len );
  }

  /**
   * Delete string with designated index
   *
   * @param index int with value of the existing index of string to remove. if
   *              illegal index used, nothing occured and {@code false}
   *              returned.
   *
   * @return returns {@code true} if string deleted, and {@code false} if index
   *         is out of range
   */
  public boolean delete( int index )
  {
    if ( !checkIndex( index ) )
      return false;
    int off = _strOffs.get( index );
    int off1;
    try
    {
      off1 = _strOffs.get( index + 1 );
    }
    catch ( Exception e )
    {
      off1 = _buf.length();
    }
    _buf.delete( off, off1 );
    _strOffs.remove( index );
    _strOffs.incOffsetsFrom( index, off - off1 );
    return true;
  }

  /**
   * Delete string with designated index
   *
   * @param start int with value of the start existing index of string to
   *              remove.
   * @param stop  int with value of the stop existing index of string to remove.
   *
   * @return returns {@code true} if string deleted, and {@code false} if index
   *         is out of range
   */
  public boolean delete( int start, int stop )
  {
    if ( !( checkIndex( start ) && ( checkIndex( stop ) ) ) )
      return false;
    int off = _strOffs.get( start );
    int off1;
    try
    {
      off1 = _strOffs.get( stop + 1 );
    }
    catch ( Exception e )
    {
      off1 = _buf.length();
    }
    _buf.delete( off, off1 );
    _strOffs.remove( start, stop );
    _strOffs.incOffsetsFrom( start, off - off1 );
    return true;
  }


  /**
   * Free whole space removing all existing strings
   */
  public void clear()
  {
    _buf.delete( 0, Integer.MAX_VALUE );
    _strOffs.clear();
  }

  /**
   * Close this object and free any resources. No more method should be called
   */
  public void close()
  {
    _strOffs.baseArray = null;
    _buf.setLength( 0 );
    //_buf.setLength( _buf.length() );
    _buf = null;
  }

  /**
   * Attempts to reduce storage used for the character sequence. If the buffer is
   * larger than necessary to hold its current sequence of characters, then it
   * may be resized to become more space efficient. Calling this method may, but
   * is not required to, optimize a memory usage
   */
  public void trimToSize()
  {
    _buf.setLength( _buf.length() );
		_buf.trimToSize();
		_strOffs.trimToSize();
  }

  /**
   * Compares stored strings with indexes 'i1' and 'i2'
   *
   * @param i1 int with first {@link String} index
   * @param i2 int with last {@link String} index
   *
   * @return -1 if 1st string is lexically smaller, 0 if strings are equal and +1
   *         if 2nd string is lexically smaller.
   */
  public int compare( int i1, int i2 )
  {
    int off1 = _strOffs.get( i1 );
    int off2 = _strOffs.get( i2 );
    return compare( off1, _strOffs.get( i1 + 1 ) - off1, off2,
      _strOffs.get( i2 + 1 ) );
  }

  /**
   * Compares two character sequences
   *
   * @param off1 int with first char seq offset
   * @param len1 int with first char seq length
   * @param off2 int with second char seq offset
   * @param len2 int with second char seq length
   *
   * @return -1 if 1st < 2nd, 0 if 1st == 2nd, +1 if 1s > 2nd
   */
  public int compare( int off1, int len1, int off2, int len2 )
  {
    int offmax = len1 > len2 ? off1 + len2 : off1 + len1;
    int diff;
    for (; off1 < offmax; off1++, off2++ )
    {
      diff = Sys.signum( _buf.charAt( off1 ) - _buf.charAt( off2 ) );
      if ( diff != 0 )
        return diff;
    }
    return Sys.signum( len1 - len2 );
  }


	/**
	 * compares 2 char arrays ignore case
	 * @param c1	first char[]
	 * @param c2 second char[]
	 * @return -n, 0, +n as usually
	 */
	private int compareIgnoreCase( char[] c1, char[] c2 )
	{
		if ( c1 == c2 )
			return 0;
		int n1 = c1.length, n2 = c2.length;
		int cnt = Math.min( n1, n2 );
		for ( int i = 0; i < cnt; i++ )
		{
			if ( c1[ i ] != c2[ i ] )
			{
				char ch1 = Character.toUpperCase( c1[i] );
				char ch2 = Character.toUpperCase( c2[i] );
				if ( ch1 != ch2 )
				{
					ch1 = Character.toLowerCase( c1[ i ] );
					ch2 = Character.toLowerCase( c2[ i ] );
					if ( c1 != c2 )
					{
						return ch1 - ch2;
					}
				}
			}
		}
		return n1 - n2;
	}


	/**
	 * compares 2 char arrays ignore case
	 *
	 * @param c1 first char[]
	 * @param c2 second char[]
	 * @return -n, 0, +n as usually
	 */
	private int compareWithCase( char[] c1, char[] c2 )
	{
		int n1 = c1.length, n2 = c2.length;
		int cnt = Math.min( n1, n2 );
		for ( int i = 0; i < cnt; i++)
		{
			if ( c1[ i ] != c2[ i ] )
						return c1[ i ] - c2[ i ];
		}
		return n1 - n2;
	}


	/**
   * Compares two character sequences case sensitive
   *
   * @param off  int with first char seq offset
   * @param len1 int with first char seq length
   * @param chrs char[] with second string contents
   *
   * @return -1 if 1st < 2nd, 0 if 1st == 2nd, +1 if 1s > 2nd
   */
  public int compare( int off, int len1, char[] chrs )
  {
    final int len2 = chrs.length;
    int offmax = len1 > len2 ? off + len2 : off + len1;
    int diff;
    try
    {
      for ( int i = 0; off < offmax; off++, i++ )
      {
        diff = Sys.signum( _buf.charAt( off ) - chrs[ i ] );
        if ( diff != 0 )
          return diff;
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return Sys.signum( len1 - len2 );
  }

	/**
	 * Compares two character sequences without case sensitive
	 *
	 * @param off	int with first char seq offset
	 * @param len1 int with first char seq length
	 * @param chrs char[] with second string contents
	 * @return -1 if 1st < 2nd, 0 if 1st == 2nd, +1 if 1s > 2nd
	 */
	public int compareIgnoreCase( int off, int len1, char[] chrs )
	{
		final int len2 = chrs.length;
		int offmax = len1 > len2 ? off + len2 : off + len1;
		int diff;
		try
		{
			for ( int i = 0; off < offmax; off++, i++ )
			{
				char ch1 = _buf.charAt( off );
				char ch2 = chrs[i];
				if ( Character.toUpperCase( ch1 ) !=  Character.toUpperCase( ch2 ) )
				{
					if ( Character.toLowerCase( ch1 ) != Character.toLowerCase( ch2 ) )
					{
						return ch1 - ch2;
					}
				}
			}
			return len1 - len2;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		return Sys.signum( len1 - len2 );
	}

	/**
   * Compares two character sequences to be equals partially, up to the maximum
   * common length
   *
   * @param off  int offset to start compare
   * @param len1 int length to compare to
   * @param chrs char[] to compare to internal buffer + off
   *
   * @return -1 if buffer < chrs, 0 if buffer == chrs, +1 if buffer > chrs
   */
  private int compareW( int off, int len1, final char[] chrs )
  {
    final int len2 = chrs.length;
    int offmax = len1 > len2 ? off + len2 : off + len1;
    int diff;
    try
    {
      for ( int i = 0; off < offmax; off++, i++ )
      {
        diff = Sys.signum( _buf.charAt( off ) - chrs[ i ] );
        if ( diff != 0 )
          return diff;
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return 0;
  }


  /**
   * Compares stored strings with indexes 'i1' and 'i2' ignoring case
   *
   * @param i1 int with first {@link String} index
   * @param i2 int with last {@link String} index
   *
   * @return -1 if 1st string is lexically smaller, 0 if strings are equal and +1
   *         if 2nd string is lexically smaller.
   */
  public int compareIgnoreCase( int i1, int i2 )
  {
    return ( ( String ) get( i1 ) ).compareToIgnoreCase( ( String ) get( i2 ) );
  }

  /**
   * Adds the contents of the {@link String[]} into this storage
   *
   * @param arr {@link String[]} instance with strings to add to the pool
   */
  public void add( String[] arr )
  {
    for ( int i = 0; i < arr.length; i++ )
      add( arr[ i ] );
  }

  /**
   * Adds the contents of the {@link String[]} into this storage
   *
   * @param lst {@link java.util.List<String>} instance with strings to add to the pool
   */
  public void add( List lst )
  {
    for ( int i = 0; i < lst.size(); i++ )
      add( lst.get( i ) );
  }

  public boolean equals( final Object o )
  {
    if ( this == o )
      return true;
    if ( o == null || (! (o instanceof DirectStringArray ) ) )
      return false;

    final DirectStringArray that = (DirectStringArray) o;

    if ( !_buf.equals( that._buf ) )
      return false;
    if ( !_strOffs.equals( that._strOffs ) )
      return false;

    return true;
  }

  /**
   * {@link StringBuffer} hash code
   *
   * @return int with {@link StringBuffer} hascode
   */
  private int sbHashCode()
  {
    int result = 1; // String Buffer Hash Code
    for ( int i = 0; i < this._buf.length(); i++ )
      result = 31 * result + _buf.charAt( i );
    return result;
  }

  public int hashCode()
  {
    return 31 * _strOffs.hashCode() + sbHashCode();
  }

  /**
   * Gets the hashcode for the string with designated index
   *
   * @param index int with index of the string to get hashcode
   *
   * @return int value for the hashcode or 0 if bad index used
   */
  public int strHashCode( int index )
  {
    int res = 0;
    if ( checkIndex( index ) )
    {
      int off = _strOffs.get( index );
      int off1;
      if ( index == ( size() - 1 ) )
        off1 = _buf.length();
      else
        off1 = _strOffs.get( index + 1 );
      for ( int i = off; i < off1; i++ )
        res = 31 * res + _buf.charAt( i );
    }
    return res;
  }

  /**
   * Return the summary string length
   *
   * @return int value of the summary string[s] length
   */
  public int volume()
  {
    return _buf.length();
  }

  /**
   * Internal {@link StringBuffer}. Use at your own risk
   *
   * @return
   */
  public StringBuffer getSB()
  {
    return _buf;
  }

  /**
   * Moves all characters of the array strings to the upper case
   */
  public void toUpperCase()
  {
    final StringBuffer sb = this.getSB();
    char ch;
    for ( int i = 0, len = sb.length(); i < len; i++ )
      sb.setCharAt( i, Character.toUpperCase( sb.charAt( i ) ) );
  }

  /**
   * Replace all occurrence of designated char to the replacement
   *
   * @param oldChar     character to replace
   * @param replacement for the oldChar
   */
  public void replace( char oldChar, char replacement )
  {
    final StringBuffer sb = this.getSB();
		for ( int i = 0, len = sb.length(); i < len; i++ )
			if (  sb.charAt( i ) == oldChar )
				sb.setCharAt( i, replacement );
	}

	/**
	 * Creates text from content
	 * @return String as result
	 */
	@Override
	public String toString()
	{
		return toString("\n");
	}

	public String toString( String separator)
	{
		StringBuilder sb = new StringBuilder( this._buf.length() + this.size() * separator.length() );
		for ( int i = 0; i < this.size(); i++ )
		{
			String str = ( String ) this.get( i );
			if ( i > 0 )
				sb.append( separator );
			sb.append( str );
		}
		return sb.toString();
	}

	/**
	 * Replaces in each strings designated sample with a designated replacements
   * todo: add optimal replace functionality to change whole buffer at once, not string by string
	 *
	 * @param sample					 sample substring to replace
	 * @param replacer					 replacement string
	 * @param caseSensitive if {@code true} replace is case sensitive else not
	 */
	public void replace( String sample, String replacer, boolean caseSensitive )
	{
		char[] sarr = sample.toCharArray();
		int slen = sample.length();
		int rlen = replacer.length();
		StringBuffer sb = null; // result value
		for ( int i = 0; i < size(); i++ )
		{
			int off1 = _strOffs.get( i );
			int len = itemLength( i );
			// find sample[s] in the string
			int j = 0;
			final int tlen = len - slen;
			for ( ; j <= tlen; j++ )
			{
				int res;
				if ( caseSensitive )
					res = compare(off1 + j, slen, sarr );
				else
					res = compareIgnoreCase( off1 + j, slen, sarr );
				if ( res == 0 ) // found
				{
					if ( sb == null )
					{
						sb = new StringBuffer( );
					}
					// initialize with previous changed source chars
					if ( sb.length() == 0)
						for ( int k = 0; k < j; k++ )
							sb.append( _buf.charAt( off1 + k ) );
					sb.append( replacer );
					j += slen - 1; // bump after replaced piece
				}
				else
					if ( sb != null && sb.length() > 0 )
						sb.append( _buf.charAt( off1 + j ) );
			}
			if ( ( sb != null ) && ( sb.length() > 0 ) )
			{
				// append reminder of the string
				for ( ; j < len; j++ )
					sb.append( _buf.charAt( off1 + j ) );
				this.set( i, sb.toString() ); // replace with a new string
				sb.delete( 0, Integer.MAX_VALUE );
			}
		}
	}


}