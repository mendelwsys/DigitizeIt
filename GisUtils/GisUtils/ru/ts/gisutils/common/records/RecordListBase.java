/**
 * Created by Sygsky on 10-OCT-2007 11:52:03
 */
package ru.ts.gisutils.common.records;

import ru.ts.utils.Text;

import java.util.*;

/**
 * @author sygsky
 * 
 * This abstract class purpose is to be a base for any descendant classes, for
 * example using byte[] or int[] for storing GeomAlgs. <br>
 * <br>
 * Note: Only the <strong>public primitive fields</strong> are used to store in
 * this class. <br>
 * And more - only <strong>class with all public primitive fields</strong> can
 * be used as a base one.
 * 
 */
// @SuppressWarnings("unchecked")
abstract class RecordListBase extends AbstractList implements RandomAccess,
        Cloneable, java.io.Serializable
{

	/**
	 * The size of the RecordListBase descendant - (the number of elements its
	 * array contains).
	 */
	protected int	_size;

	/**
	 * internal object length in n-byte chunks. <strong>n</strong> depends on
	 * elementData item type. For <code>int[]</code> it will be 4, for byte[]
	 * it is 1
	 */
	protected int	_objlen;

	/**
	 * internal object class
	 */
	protected Class	_objclass;

	/**
	 * physical memory size of buffer needed to keep _size objects
	 * 
	 * @return integer value of buffer _size
	 */
	protected int _size()
	{
		return _size * _objlen;
	}

	/**
	 * processors for each field of the storage object class
	 */
	FieldProcessorBase[]	_processors;

	/**
	 * key index to sort/search by internal fast methods
	 */
	int[]	             _indexkey;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param record
	 *            Class to be set as a base for the storage item
	 * @param initialCapacity
	 *            the initial capacity of the list.
	 * @exception ClassNotFoundException
	 *                if the specified class is illegal for some case
	 */
	RecordListBase( Class record, int initialCapacity )
	        throws ClassNotFoundException
	{
		super();
		if ( initialCapacity <= 0 )
			initialCapacity = 10;
		_init( record );
		ensureCapacity( initialCapacity );
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public RecordListBase( Class record ) throws ClassNotFoundException
	{
		this( record, 10 );
	}

	/**
	 * main internal initiate method - accepts base record object class, detect
	 * its fields and corresponding length of each
	 * 
	 * @param record -
	 *            record object class to be stored in our class. Please assure
	 *            that this class can have access to fields of record class by
	 *            Reflect methods
	 */
	abstract protected void _init( Class record ) throws ClassNotFoundException;

	/**
	 * Increases the capacity of this <tt>RecordListBase</tt> descendant
	 * instance, if necessary, to ensure that it can hold at least the number of
	 * elements specified by the minimum capacity argument.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity.
	 */
	abstract public void ensureCapacity( int minCapacity );

	/**
	 * Returns the number of elements in this list.
	 * 
	 * @return the number of elements in this list.
	 */
	public int size()
	{
		return _size;
	}

	/**
	 * Tests if this list has no elements.
	 * 
	 * @return <tt>true</tt> if this list has no elements; <tt>false</tt>
	 *         otherwise.
	 */
	public boolean isEmpty()
	{
		return _size == 0;
	}

	/**
	 * Returns <tt>true</tt> if this list contains the specified element.
	 * 
	 * @param elem
	 *            element whose presence in this List is to be tested.
	 * @return <code>true</code> if the specified element is present;
	 *         <code>false</code> otherwise.
	 */
	public boolean contains( Object elem )
	{
		return indexOf( elem ) >= 0;
	}

	/**
	 * Searches for the first occurrence of the given argument, testing for
	 * equality using the <tt>equals</tt> method.
	 * 
	 * @param elem
	 *            an object.
	 * @return the index of the first occurrence of the argument in this list;
	 *         returns <tt>-1</tt> if the object is not found.
	 * @see Object#equals(Object)
	 */
	public int indexOf( Object elem )
	{
		if ( elem != null ) // allow to search null element by always returning
		// -1
		{
			this.checkClass( elem );
			for ( int i = 0; i < _size; i++ )
				if ( elem.equals( objectAtIndex( i ) ) )
					return i;
		}
		return -1;
	}

	/**
	 * returns copy of indexes array used to sort records and for binary search
	 * through them
	 * 
	 * @return int[] of index for all fields consisting the record index key
	 */
	public int[] getIndexKey()
	{
		int[] ret = new int[_indexkey.length];
		System.arraycopy( _indexkey, 0, ret, 0, _indexkey.length );
		return ret;
	}

	/**
	 * @param keyfields
	 *            array of field index keys composing the helper to search
	 *            through record values. Correct key should consists of indexes
	 *            of existing fields of the record and should have only unique
	 *            indexes of course
	 * @throws IndexOutOfBoundsException
	 *             if any the specified keys is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>).
	 * @throws IllegalArgumentException
	 *             if any the specified keys is used twice.
	 */
	public void setIndexKey( int[] keyfields )
	        throws IndexOutOfBoundsException, IllegalArgumentException
	{
		int[] keys = new int[keyfields.length];
		int maxkeynum = _processors.length;
		for ( int i = 0; i < keyfields.length; i++ )
		{
			int key = keyfields[ i ];
			if ( key >= maxkeynum )
				throw new IllegalArgumentException( "value of key == " + key
				        + " is out of range 0.." + ( maxkeynum - 1 ) );
			for ( int j = 0; j < i; j++ )
				if ( key == keys[ j ] )
					throw new IllegalArgumentException( "value of key == "
					        + key + " is used twice" );
			keys[ i ] = key;
		}

		System.arraycopy( keys, 0, _indexkey = new int[keys.length], 0,
		        keys.length );
	}

	/**
	 * @param keystr
	 *            string with key field names, separated by commas
	 * @throws IndexOutOfBoundsException
	 *             if any the specified keys is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>).
	 * @throws IllegalArgumentException
	 *             if any the specified keys is used twice.
	 */
	public void setIndexKey( String keystr ) throws IndexOutOfBoundsException,
	        IllegalArgumentException
	{
		String[] keys = Text
		        .arrayToUpper( Text.splitItems( keystr, ',', false ) );
		int[] indexes = new int[keys.length];
		java.util.Arrays.fill( indexes, -1 );
		for ( int i = 0; i < _processors.length; i++ )
		{
			String fname = _processors[ i ].getFieldName().toUpperCase();
			for ( int j = 0; j < keys.length; j++ )
				if ( fname.equals( keys[ j ] ) )
				{
					indexes[ j ] = i;
					break; // found, go to the next one
				}
		}
		setIndexKey( indexes );
	}

	/**
	 * Returns the index of the last occurrence of the specified object in this
	 * list.
	 * 
	 * @param elem
	 *            the desired element.
	 * @return the index of the last occurrence of the specified object in this
	 *         list; returns -1 if the object is not found.
	 */
	public int lastIndexOf( Object elem )
	{
		if ( elem != null )
		{
			for ( int i = _size - 1; i >= 0; i-- )
				if ( elem.equals( objectAtIndex( i ) ) )
					return i;
		}
		return -1;
	}

	/**
	 * Returns a full copy of this <tt>RecordListBase</tt> descendant
	 * instance. (The elements themselves are not copied.)
	 * 
	 * @return a clone of this <tt>RecordList</tt> instance.
	 */
	abstract public Object clone();

	/**
	 * Trims the capacity of this <tt>RecordListBase</tt> descendant instance
	 * to be the list's current _size. An application can use this operation to
	 * minimise the storage of an <tt>RecordList</tt> instance.
	 */
	abstract public void trimToSize();

	/**
	 * Returns an array containing all of the elements in this list in the
	 * correct order.
	 * 
	 * @return an array containing all of the elements in this list in the
	 *         correct order.
	 */
	public Object[] toArray()
	{
		Object[] result = new Object[_size];
		for ( int index = 0; index < _size; )
			result[ index ] = objectFromArray( index++ );
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list in the
	 * correct order; the runtime type of the returned array is that of the
	 * specified array. If the list fits in the specified array, it is returned
	 * therein. Otherwise, a new array is allocated with the runtime type of the
	 * specified array and the _size of this list.
	 * <p>
	 * 
	 * If the list fits in the specified array with room to spare (i.e., the
	 * array has more elements than the list), the element in the array
	 * immediately following the end of the collection is set to <tt>null</tt>.
	 * This is useful in determining the length of the list <i>only</i> if the
	 * caller knows that the list does not contain any <tt>null</tt> elements.
	 * 
	 * @param a
	 *            the array into which the elements of the list are to be
	 *            stored, if it is big enough; otherwise, a new array of the
	 *            same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list.
	 * @throws ArrayStoreException
	 *             if the runtime type of a is not a supertype of the runtime
	 *             type of every element in this list.
	 */
	public Object[] toArray( Object[] a ) throws IllegalArgumentException
	{
		Class cls = a.getClass().getComponentType();
		checkClass( cls );
		if ( a.length < _size )
			a = (Object[]) java.lang.reflect.Array.newInstance( cls, _size );
		for ( int index = 0; index < _size; index++ )
			a[ index ] = objectFromArray( index );
		if ( a.length > _size )
			a[ _size ] = null;
		return a;
	}

	/**
	 * internal method to extract record object from record data array
	 * 
	 * @param index
	 *            of the record
	 * @return reconstructed record object with record data restored
	 */
	abstract protected Object objectFromArray( int index );

	// Positional Access Operations

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index
	 *            index of element to return.
	 * @return the element at the specified position in this list.
	 * @throws IndexOutOfBoundsException
	 *             if index is out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= _size())</tt>.
	 */
	public Object get( int index )
	{
		RangeCheck( index );
		return objectFromArray( index );
	}

	/**
	 * Fills all fields from array item to the existing object
	 * 
	 * @param obj -
	 *            Object of native type, used in the list
	 * @param index
	 *            index of element to return.
	 */
	public void get( Object obj, int index )
	{
		readFromArray( obj, index );
	}

	/**
	 * gets directly value from a buffer and return it to user. No any parameter
	 * checks are realized for execution speed purposes. It means that you can
	 * get incorrect value without any exceptions, e.g. you always get some
	 * value for BYTE type field even if you indicated incorrect field index.
	 * But the value could be random ones.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            get incorrect value without any exception
	 * @return the int value extracted
	 */
	abstract public int getFieldInt( int index_obj, int index_fld );

	/**
	 * gets directly value from a buffer and return it to user. No any parameter
	 * checks are realized for execution speed purposes. It means that you can
	 * get incorrect value without any exceptions, e.g. you always get some
	 * value for BYTE type field if you indicated incorrect field index. But the
	 * value could be random ones.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            get incorrect value without any exception
	 * @return the boolean value extracted
	 */
	abstract public boolean getFieldBoolean( int index_obj, int index_fld );

	/**
	 * gets directly value from a buffer and return it to user. No any parameter
	 * checks are realized for execution speed purposes. It means that you can
	 * get incorrect value without any exceptions, e.g. you always get some
	 * value for BYTE type field if you indicated incorrect field index. But the
	 * value could be random ones.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            get incorrect value without any exception
	 * @return the short value extracted
	 */
	abstract public short getFieldShort( int index_obj, int index_fld );

	/**
	 * gets directly value from a buffer and return it to user. No any parameter
	 * checks are realized for execution speed purposes. It means that you can
	 * get incorrect value without any exceptions, e.g. you always get some
	 * value for BYTE type field if you indicated incorrect field index. But the
	 * value could be random ones.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            get incorrect value without any exception
	 * @return the char value extracted
	 */
	abstract public char getFieldChar( int index_obj, int index_fld );

	/**
	 * gets directly value from a buffer and return it to user. No any parameter
	 * checks are realized for execution speed purposes. It means that you can
	 * get incorrect value without any exceptions, e.g. you always get some
	 * value for BYTE type field if you indicated incorrect field index. But the
	 * value could be random ones.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            get incorrect value without any exception
	 * @return the byte value extracted
	 */
	abstract public byte getFieldByte( int index_obj, int index_fld );

	/**
	 * gets directly value from a buffer and return it to user. No any parameter
	 * checks are realized for execution speed purposes. It means that you can
	 * get incorrect value without any exceptions, e.g. you always get some
	 * value for BYTE type field if you indicated incorrect field index. But the
	 * value could be random ones.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            get incorrect value without any exception
	 * @return the float value extracted
	 */
	abstract public float getFieldFloat( int index_obj, int index_fld );

	/**
	 * gets directly value from a buffer and return it to user. No any parameter
	 * checks are realized for execution speed purposes. It means that you can
	 * get incorrect value without any exceptions, e.g. you always get some
	 * value for BYTE type field if you indicated incorrect field index. But the
	 * value could be random ones.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            get incorrect value without any exception
	 * @return the double value extracted
	 */
	abstract public double getFieldDouble( int index_obj, int index_fld );

	/**
	 * sets directly user value to a buffer. No any parameter checks are
	 * realized for execution speed purposes. It means that you can set
	 * incorrect value without any exceptions, e.g. you could set some value if
	 * you indicated indexes that wouldn't throw illegal address exception. But
	 * the value could overwrite other fields value of different type.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            set incorrect value without any exception
	 * @param value
	 *            double new value to set
	 */
	abstract public void setFieldDouble( int index_obj, int index_fld,
	        double value );

	/**
	 * gets directly value from a buffer and return it to user. No any parameter
	 * checks are realized for execution speed purposes. It means that you can
	 * get incorrect value without any exceptions, e.g. you always get some
	 * value for BYTE type field if you indicated incorrect field index. But the
	 * value could be random ones.
	 * 
	 * @param index_obj
	 *            object index in the collection
	 * @param index_fld
	 *            field index in the object. Please use correct index or could
	 *            get incorrect value without any exception
	 * @return the long value extracted
	 */
	abstract public long getFieldLong( int index_obj, int index_fld );

	/**
	 * gets internally stored record class
	 */
	public Class getRecordClass()
	{
		return _objclass;
	}

	/**
	 * gets internal record fields counter
	 * 
	 * @return number of fields int the internal record class
	 */
	public int getFieldCount()
	{
		return _processors.length;
	}

	final protected boolean checkFieldIndex( int field_index )
	        throws IndexOutOfBoundsException
	{
		return ( field_index < getFieldCount() ) && ( field_index >= 0 );
	}

	/**
	 * gets field type of stored record at indicated index it can be one of the
	 * follow classes:<br>
	 * {@link java.lang.Boolean}<br>
	 * {@link java.lang.Byte}<br>
	 * {@link java.lang.Character}<br>
	 * {@link java.lang.Short}<br>
	 * {@link java.lang.Integer}<br>
	 * {@link java.lang.Float}<br>
	 * {@link java.lang.Long}<br>
	 * {@link java.lang.Double}
	 * 
	 * @param field_index
	 *            index of the field to gets
	 * @return Class of the field or <code>null</code>if illegal index
	 *         indicated
	 */
	public Class getFieldType( int field_index )
	{
		if ( !checkFieldIndex( field_index ) )
			return null;
		return _processors[ field_index ]._field.getType();
	}

	/**
	 * gets field name for the indicated index
	 * 
	 * @param field_index
	 *            index of field to get its name as it is in the record class
	 * @return String with name or <code>null</code> if no field at such index
	 *         (e.g. -1 indicated)
	 */
	public String getFieldName( int field_index )
	{
		if ( !checkFieldIndex( field_index ) )
			return null;
		return _processors[ field_index ].getFieldName();
	}

	/**
	 * gets index of the field with indicated case sensitive name
	 * 
	 * @param fieldName
	 *            String with case sensitive name
	 * @return index in the range 0.. MaxFieldIndex or -1 if no such field
	 */
	public int getFieldIndex( String fieldName )
	{
		for ( int i = 0; i < _processors.length; i++ )
			if ( _processors[ i ].getFieldName().equals( fieldName ) )
				return i;
		return -1;
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 * 
	 * @param index
	 *            index of element to replace.
	 * @param element
	 *            element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws IndexOutOfBoundsException
	 *             if index out of range
	 *             <tt>(index &lt; 0 || index &gt;= _size())</tt>.
	 */
	public Object set( int index, Object element )
	{
		RangeCheck( index );
		checkClass( element );
		Object oldValue = objectFromArray( index );
		objectToArray( element, index );
		return oldValue;
	}
	/**
	 * Fast set object method, doesn't extract any form an array
	 * @param index index to set object's values
	 * @param element object to get new properties
	 */
	public void setFast(int index, Object element )
	{
		RangeCheck( index );
		checkClass( element );
		objectToArray( element, index );
	}

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param o
	 *            element to be appended to this list.
	 * @return <tt>true</tt> (as per the general contract of Collection.add)
	 *         if success or <code>false</code> if not
	 */
	public boolean add( Object o )
	{
		ensureCapacity( _size + 1 ); // Increments modCount!!
		return objectToArray( o, _size++ ) > 0;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted.
	 * @param element
	 *            element to be inserted.
	 * @throws IndexOutOfBoundsException
	 *             if index is out of range
	 *             <tt>(index &lt; 0 || index &gt; _size())</tt>.
	 */
	public void add( int index, Object element )
	{
		if ( index > _size || index < 0 )
			throw new IndexOutOfBoundsException( "Index: " + index + ", Size: "
			        + _size );

		ensureCapacity( _size + 1 ); // Increments modCount!!
		arraycopy( index, index + 1, _size - index );
		objectToArray( element, index );
		_size++;
	}

	/**
	 * Checks if designated object can be added to the list
	 * 
	 * @param item
	 *            Object instance to check to be possible to add to the list
	 * @return <code>true</code> if it is possible to add else
	 *         <code>false</code>
	 */
	public boolean canAdd( Object item )
	{
		return ( item != null ) && ( item.getClass().equals( _objclass ) );
	}

	/**
	 * Removes the element at the specified position in this list. Shifts any
	 * subsequent elements to the left (subtracts one from their indices).
	 * 
	 * @param index
	 *            the index of the element to removed.
	 * @return the element that was removed from the list.
	 * @throws IndexOutOfBoundsException
	 *             if index out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= _size())</tt>.
	 */
	public Object remove( int index )
	{
		RangeCheck( index );

		modCount++;
		Object oldValue = objectFromArray( index );

		int numMoved = _size - index - 1;
		if ( numMoved > 0 )
			arraycopy( index + 1, index, numMoved );
		clearAtIndex( --_size );
		return oldValue;
	}

	/**
	 * Removes a single instance of the specified element from this list, if it
	 * is present (optional operation). More formally, removes an element
	 * <tt>e</tt> such that <tt>(o==null ? e==null :
	 * o.equals(e))</tt>, if
	 * the list contains one or more such elements. Returns <tt>true</tt> if
	 * the list contained the specified element (or equivalently, if the list
	 * changed as a result of the call).
	 * <p>
	 * 
	 * @param o
	 *            element to be removed from this list, if present.
	 * @return <tt>true</tt> if the list contained the specified element.
	 */
	public boolean remove( Object o )
	{
		if ( o == null )
		{
			return false;
		}
		else
		{
			checkClass( o );
			for ( int index = 0; index < _size; index++ )
				if ( o.equals( objectAtIndex( index ) ) )
				{
					fastRemove( index );
					return true;
				}
		}
		return false;
	}

	/*
	 * Private remove method that skips bounds checking and does not return the
	 * value removed.
	 */
	private void fastRemove( int index )
	{
		modCount++;
		int numMoved = _size - index - 1;
		if ( numMoved > 0 )
			arraycopy( index + 1, index, numMoved );
		clearAtIndex( --_size );
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public void clear()
	{
		modCount++;
		clearRange( 0, _size );
		_size = 0;
	}

	/**
	 * Appends all of the elements in the specified Collection to the end of
	 * this list, in the order that they are returned by the specified
	 * Collection's Iterator. The behaviour of this operation is undefined if the
	 * specified Collection is modified while the operation is in progress.
	 * (This implies that the behaviour of this call is undefined if the
	 * specified Collection is this list, and this list is nonempty.)
	 * 
	 * @param c
	 *            the elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws NullPointerException
	 *             if the specified collection is null.
	 */
	public boolean addAll( Collection c )
	{
		int numNew = c.size();
		ensureCapacity( _size + numNew ); // Increments modCount
		for ( java.util.Iterator it = c.iterator(); it.hasNext(); )
		{
			Object o = it.next();
			checkClass( o );
			objectToArray( o, _size++ );
		}
		return numNew != 0;
	}

	/**
	 * Inserts all of the elements in the specified Collection into this list,
	 * starting at the specified position. Shifts the element currently at that
	 * position (if any) and any subsequent elements to the right (increases
	 * their indices). The new elements will appear in the list in the order
	 * that they are returned by the specified Collection's iterator.
	 * 
	 * @param index
	 *            index at which to insert first element from the specified
	 *            collection.
	 * @param c
	 *            elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws IndexOutOfBoundsException
	 *             if index out of range <tt>(index
	 *		  &lt; 0 || index &gt; _size())</tt>.
	 * @throws NullPointerException
	 *             if the specified Collection is null.
	 */
	public boolean addAll( int index, Collection c )
	{
		RangeCheck( index );
		int numNew = c.size();
		ensureCapacity( _size + numNew ); // Increments modCount
		int numMoved = _size - index;
		if ( numMoved > 0 )
			arraycopy( index, index + numNew, numMoved );
		for ( Iterator it = c.iterator(); it.hasNext(); )
			objectToArray( it.next(), index++ );
		_size += numNew;
		return numNew != 0;
	}

	/**
	 * Removes from this List all of the elements whose index is between
	 * fromIndex, inclusive and toIndex, exclusive. Shifts any succeeding
	 * elements to the left (reduces their index). This call shortens the list
	 * by <tt>(toIndex - fromIndex)</tt> elements. (If
	 * <tt>toIndex==fromIndex</tt>, this operation has no effect.)
	 * 
	 * @param fromIndex
	 *            index of first element to be removed.
	 * @param toIndex
	 *            index after last element to be removed.
	 */
	protected void removeRange( int fromIndex, int toIndex )
	{
		modCount++;
		int numMoved = _size - toIndex;
		arraycopy( toIndex, fromIndex, numMoved );
		clearRange( _size - ( toIndex - fromIndex ), _size );
	}

	/**
	 * Check if the given index is in range. If not, throw an appropriate
	 * runtime exception. This method does *not* check if the index is negative:
	 * It is always used immediately prior to an array access, which throws an
	 * ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void RangeCheck( int index )
	{
		if ( index >= _size )
			throw new IndexOutOfBoundsException( "Index: " + index + ", Size: "
			        + _size );
	}

	/**
	 * checks that indicated index is in the collection
	 * 
	 * @param record_index
	 *            index to check
	 * @return <code>true</code> if such index exists in the collection, else
	 *         <code>false</code>.
	 */
	final protected boolean checkRecordIndex( int record_index )
	{
		return ( record_index >= 0 ) && ( record_index < size() );
	}

	/**
	 * Save the state of the <tt>RecordList</tt> instance to a stream (that
	 * is, serialise it).
	 * 
	 * @serialData The length of the array backing the <tt>RecordList</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject( java.io.ObjectOutputStream s )
	        throws java.io.IOException
	{
		int expectedModCount = modCount;
		// Write out element count, and any hidden stuff
		s.defaultWriteObject();

		writeArray( s );
		if ( modCount != expectedModCount )
		{
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * writes buffer array to the stream in follow order: 1. length of array in
	 * records stored 2. the body of array itself
	 * 
	 * @param s
	 *            stream to write buffer
	 */
	abstract protected void writeArray( java.io.ObjectOutputStream s )
	        throws java.io.IOException;

	/**
	 * Reconstitutes the <tt>RecordList</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject( java.io.ObjectInputStream s )
	        throws java.io.IOException, ClassNotFoundException
	{
		// Read in _size, and any hidden stuff
		s.defaultReadObject();
		readArray( s );
	}

	/**
	 * reads the buffer from the stream. The order of read data is as follow: 1.
	 * int value of records (not bytes of other items) stored in the array 2.
	 * the array body itself
	 * 
	 * @param s
	 *            stream to read all the data
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	abstract protected void readArray( java.io.ObjectInputStream s )
	        throws java.io.IOException, ClassNotFoundException;

	/**
	 * internal method to access records dynamically
	 * 
	 * @param index
	 *            index of the wanted object
	 * @return Object created and filled with all its fields
	 */
	abstract protected Object objectAtIndex( int index );

	/**
	 * internal method to clear empty object place
	 * 
	 * @param index
	 *            where to clear
	 */
	abstract protected void clearAtIndex( int index );

	/**
	 * internal method to clear all record <code>from</code> inclusively and
	 * <code>to</code> exclusively
	 * 
	 * @param from
	 *            begin index to clear inclusive
	 * @param to
	 *            end index to clear exclusive
	 */
	abstract protected void clearRange( int from, int to );

	/**
	 * internal method to put object into bytes at specified index
	 * 
	 * @param o
	 *            Object to insert
	 * @param index
	 *            of byte array to start put object fields of primitive types
	 * @return number of bytes wrote to the array
	 */
	abstract protected int objectToArray( Object o, int index );

	/**
	 * internal method to read fields and copy them into existing objext
	 * 
	 * @param obj
	 *            Object to fill its fields only
	 * @param index
	 *            position of fields in the storage
	 * @return result of operation
	 */
	abstract protected boolean readFromArray( Object obj, int index );

	/**
	 * internal method to copy records from one place to another in the main
	 * array
	 * 
	 * @param srcindex -
	 *            from where to copy
	 * @param dstindex -
	 *            to where to copy
	 * @param num -
	 *            number of records
	 */
	abstract protected void arraycopy( int srcindex, int dstindex, int num );

	/**
	 * check if specified object is of legal internal record class
	 * 
	 * @param obj -
	 *            Object to check
	 * @return <code>true</code> if it was of the same class, else<code>false</code>
	 */
	protected void checkClass( Object obj )
	{
		if ( ( obj == null ) || ( !obj.getClass().equals( _objclass ) ) )
			throw new IllegalArgumentException( "Expected class was \""
			        + _objclass.getName() + "\"" );
	}

	/**
	 * Sorts the specified range of the specified array of floats into ascending
	 * numerical order. The range to be sorted extends from index
	 * <tt>fromIndex</tt>, inclusive, to index <tt>toIndex</tt>,
	 * exclusive. (If <tt>fromIndex==toIndex</tt>, the range to be sorted is
	 * empty.)
	 * <p>
	 * The <code>&lt;</code> relation does not provide a total order on all
	 * floating-point values; although they are distinct numbers
	 * <code>-0.0f == 0.0f</code> is <code>true</code> and a NaN value
	 * compares neither less than, greater than, nor equal to any floating-point
	 * value, even itself. To allow the sort to proceed, instead of using the
	 * <code>&lt;</code> relation to determine ascending numerical order, this
	 * method uses the total order imposed by {@link Float#compareTo}. This
	 * ordering differs from the <code>&lt;</code> relation in that
	 * <code>-0.0f</code> is treated as less than <code>0.0f</code> and NaN
	 * is considered greater than any other floating-point value. For the
	 * purposes of sorting, all NaN values are considered equivalent and equal.
	 * <p>
	 * The sorting algorithm is a tuned quicksort, adapted from Jon L. Bentley
	 * and M. Douglas McIlroy's "Engineering a Sort Function", Software-Practice
	 * and Experience, Vol. 23(11) P. 1249-1265 (November 1993). This algorithm
	 * offers n*log(n) performance on many data sets that cause other quicksorts
	 * to degrade to quadratic performance.
	 * 
	 * @param fromIndex
	 *            the index of the first element (inclusive) to be sorted.
	 * @param toIndex
	 *            the index of the last element (exclusive) to be sorted.
	 * @throws IllegalArgumentException
	 *             if <tt>fromIndex &gt; toIndex</tt>
	 * @throws ArrayIndexOutOfBoundsException
	 *             if <tt>fromIndex &lt; 0</tt> or
	 *             <tt>toIndex &gt; a.length</tt>
	 */
	protected void sort( int fromIndex, int toIndex )
	{
		rangeCheck( _size, fromIndex, toIndex );
		sort1( fromIndex, toIndex );
	}

	/**
	 * Sorts the specified sub-array into ascending order.
	 */
	protected void sort1( int off, int len )
	{
		/*
		 * debugSort("enter sort1("+off+","+len+")");
		 */
		// Insertion sort on smallest arrays
		if ( len < 7 )
		{
			for ( int i = off; i < len + off; i++ )
				for ( int j = i; j > off && ( compare( j - 1, j ) > 0 ); j-- )
					swap1( j, j - 1 );
			/*
			 * debugSort("exit after bubblesort");
			 */return;
		}

		// Choose a partition element, v
		int m = off + ( len >> 1 ); // Small arrays, middle element
		if ( len > 7 )
		{
			int l = off;
			int n = off + len - 1;
			if ( len > 40 )
			{ // Big arrays, pseudomedian of 9
				int s = len / 8;
				l = med3( l, l + s, l + 2 * s );
				m = med3( m - s, m, m + s );
				n = med3( n - 2 * s, n - s, n );
			}
			m = med3( l, m, n ); // Mid-size, med of 3
			/*
			 * debugOutput("med3(l="+l+",m="+m+",n="+n+")= "+m+"");
			 */
		}
		int v = m;

		// Establish Invariant: v* (<v)* (>v)* v*
		int a = off, b = a, c = off + len - 1, d = c;
		/*
		 * debugOutput("v="+v+"; a="+a+"; b="+b+"; c="+c+"; d="+d+"");
		 */while ( true )
		{
			/*
			 * debugOutput(" while (b <= c && (compare(b, v) <= 0))");
			 */while ( b <= c && ( compare( b, v ) <= 0 ) )
			{
				if ( compare( b, v ) == 0 )
				{
					if ( b == v ) // then median index is changing
					{
						swap1( v = a++, b );
						/*
						 * debugOutput("v=" + v);
						 */}
					else
						swap1( a++, b );
					/*
					 * debugSort("if ( (b="+b+" <= v="+v+") == 0) compare(b=" +
					 * b + ",v=" + v + "))swap(a++=" + a + ",b=" + b + ") ");
					 */}
				b++;
				/*
				 * debugOutput("b++=" + b);
				 */}
			/*
			 * debugOutput(" while (c >= b && (compare(c, v) >= 0))");
			 */while ( c >= b && ( compare( c, v ) >= 0 ) )
			{
				if ( compare( c, v ) == 0 )
				{
					if ( c == v ) // median index is changing
					{
						swap1( c, v = d-- );
						/*
						 * debugOutput("v=" + v);
						 */}
					else
						swap1( c, d-- );
					/*
					 * debugSort(" if ( compare(" + c + "," + v + ") == 0 )
					 * swap(c="+c+", d--="+d+")");
					 */}
				c--;
				/*
				 * debugOutput("c--=" + c);
				 */}
			if ( b > c )
			{
				/*
				 * debugOutput("if (b="+b+" > c="+c+") break;");
				 */break;
			}
			swap1( b++, c-- );
			/*
			 * debugSort("swap( b++=" + b + ", c--=" + c + ")");
			 */}

		// Swap partition elements back to middle
		int s, n = off + len;
		/*
		 * debugOutput("n="+n);
		 */s = Math.min( a - off, b - a );
		vecswap( off, b - s, s );
		/*
		 * debugSort("vecswap(off="+off+", b-s="+(b-s)+",s="+s+")");
		 */
		s = Math.min( d - c, n - d - 1 );
		vecswap( b, n - s, s );
		/*
		 * debugSort("vecswap(b="+b+", n-s="+(n-s)+",s="+s+")");
		 */
		// Recursively sort non-partition-elements
		if ( ( s = b - a ) > 1 )
			sort1( off, s );
		if ( ( s = d - c ) > 1 )
			sort1( n - s, s );
		/*
		 * debugSort("exit from sort1");
		 */}

	/**
	 * Returns the index of the median of the three indexed records.
	 */
	protected int med3( int a, int b, int c )
	{
		return compare( a, b ) < 0 ? ( compare( b, c ) < 0 ? b
		        : compare( a, c ) < 0 ? c : a ) : ( compare( b, c ) > 0 ? b
		        : compare( a, c ) > 0 ? c : a );
	}

	/**
	 * Check that fromIndex and toIndex are in range, and throw an appropriate
	 * exception if they aren't.
	 */
	protected static void rangeCheck( int arrayLen, int fromIndex, int toIndex )
	{
		if ( fromIndex > toIndex )
			throw new IllegalArgumentException( "fromIndex(" + fromIndex
			        + ") > toIndex(" + toIndex + ")" );
		if ( fromIndex < 0 )
			throw new ArrayIndexOutOfBoundsException( fromIndex );
		if ( toIndex > arrayLen )
			throw new ArrayIndexOutOfBoundsException( toIndex );
	}

	protected boolean	_debug	= false;

	/**
	 * sorts record list on the current key index. If key index is not set,
	 * nothing occures.
	 */
	public void sort( boolean debug )
	{
		boolean old_state = _debug;
		try
		{
			_debug = debug;
			if ( _indexkey.length > 0 )
				sort( 0, _size );
		}
		finally
		{
			_debug = old_state;
		}
	}

	/**
	 * sorts record list on the current key index. If key index is not set,
	 * nothing occures.
	 */
	public void sort()
	{
		if ( _indexkey.length > 0 )
			sort( 0, _size );
	}

	/**
	 * 
	 * Searches internal array of records for the specified record using the
	 * binary search algorithm. The array <strong>must</strong> be sorted (as
	 * by the <tt>sort</tt> method, above) prior to making this call. If it is
	 * not sorted, the results are undefined. If the array contains multiple
	 * elements with the specified value, there is no guarantee which one will
	 * be found.
	 * 
	 * @param key
	 *            the record to be searched for.
	 * @return index of the search key, if it is contained in the list;
	 *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
	 *         <i>insertion point</i> is defined as the point at which the key
	 *         would be inserted into the list: the index of the first element
	 *         greater than the key, or <tt>list.size()</tt>, if all elements
	 *         in the list are less than the specified key. Note that this
	 *         guarantees that the return value will be &gt;= 0 if and only if
	 *         the key is found. Remember, that search is produced according to
	 *         the index key previously set by corresponding call
	 *         setIndexKey(...)
	 * @throws IllegalArgumentException
	 *             if key is not set
	 * 
	 */
	public int binarySearch( Object key ) throws IllegalArgumentException,
	        IllegalAccessException
	{
		if ( _indexkey.length == 0 )
			throw new IllegalArgumentException(
			        "Please call setIndexKey(keyfields) and sort() before binarySearch(key)" );

		if ( size() == 0 )
			return -1;
		int low = 0;
		int high = this.size() - 1;

		// while ( compare( low, high ) <= 0 )
		while ( low <= high )
		{
			int mid = ( low + high ) >> 1;

			int cmp = compare( mid, key );
			if ( cmp < 0 )
				low = mid + 1;
			else if ( cmp > 0 )
				high = mid - 1;
			else
				return mid; // key found
		}
		return -( low + 1 ); // key not found.
	}

	/**
	 * Randomly permute the list using the specified source of randomness.
	 * <p>
	 * 
	 * This implementation traverses the list backwards, from the last element
	 * up to the second, repeatedly swapping a randomly selected element into
	 * the "current position". Elements are randomly selected from the portion
	 * of the list that runs from the first element to the current position,
	 * inclusive.
	 * <p>
	 * 
	 * This method runs in linear time.
	 * 
	 * @param r
	 *            the source of randomness to use to shuffle the list.
	 */
	public void shuffle( Random r )
	{
		for ( int i = _size; i > 1; i-- )
			swap( i - 1, r.nextInt( i ) );
	}

	/**
	 * the same as shuffle(Random)
	 */
	public void shuffle()
	{
		shuffle( _rnd );
	}

	private Random	_rnd	= new Random();

	/**
	 * compare two elements of the record array
	 * 
	 * @param index1
	 *            index of first record
	 * @param index2
	 *            index of second record
	 * @return as usual 1 if 1st > 2nd, 0 if 1st == 2nd, -1 of 1st < 2nd
	 */
	abstract protected int compare( int index1, int index2 );

	/**
	 * Compares item of array and external object
	 * 
	 * @param index1
	 *            index of item in array
	 * @param key
	 *            object to compare
	 * @return as usually -1, 0, + 1 depending on comparison result
	 * @throws IllegalAccessException
	 */
	abstract protected int compare( int index1, Object key )
	        throws IllegalAccessException;

	/**
	 * swaps two elements of array
	 * 
	 * @param index1 -
	 *            index of 1st element to swap
	 * @param index2 -
	 *            index of 2nd element to swap
	 */
	public void swap( int index1, int index2 )
	{
		RangeCheck( index1 );
		RangeCheck( index2 );
		swap1( index1, index2 );
	}

	/**
	 * swaps two elements of the record array
	 * 
	 * @param index1
	 *            index of first record
	 * @param index2
	 *            index of second record
	 */
	abstract protected void swap1( int index1, int index2 );

	/**
	 * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
	 */
	abstract protected void vecswap( int a, int b, int n );

	/*
	 * abstract protected void debugSort(String suffix);
	 * 
	 * protected void debugOutput(String str) { if ( !_debug ) return;
	 * System.out.println(str); }
	 */
}
