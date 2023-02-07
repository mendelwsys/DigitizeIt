/**
 * Created on 11.10.2007 12:13:08 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

/**
 * {@link RecordListBase} extended for byte[] data storage.
 * @author sygsky
 *
 */
public final class RLByte extends RecordListBase
{

	/** 
	 * MAIN storage for the records
	 */
	private transient byte[] _data;	
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/**
	 * @param record
	 * @param initialCapacity
	 * @throws ClassNotFoundException
	 */
	public RLByte(Class record, int initialCapacity)
	        throws ClassNotFoundException
	{
		super(record, initialCapacity);
	}

	public RLByte(Class record)throws ClassNotFoundException
    {
		super(record);
    }
	
	/**
	 * internal constructor for clone method only
	 * 
	 * @param ra -
	 *            parent RecordList object
	 */
	RLByte(RLByte ra) throws ClassNotFoundException
	{
		this(ra._objclass, ra._size);
		System.arraycopy(_data, 0, ra._data, 0, _size * _objlen);
	}


	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#_init(java.lang.Class)
	 */
	protected void _init(Class record) throws ClassNotFoundException
	{
		_objclass = record;
		_objlen = 0;
		_size = 0;
		_data = new byte[0];
		_indexkey = new int[0];

		// By obtaining a list of all public fields, both declared and
		// inherited.
		Field[] fields = record.getFields();
		_processors = new FieldProcessorBase[fields.length];
		for (int i = 0; i < fields.length; i++)
		{
			Field field = fields[ i ];
			// check if class contains ONLY primitive fields
			if (!field.getType().isPrimitive())
				throw new IllegalArgumentException("Class \""
				        + record.getName() + "\" field \"" + field.getName()
				        + "\" should be primitive.");
			_objlen += ( _processors[ i ] = 
				FieldProcessorBase.getInstanceB(field, _objlen) ).size();
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#arraycopy(int, int, int)
	 */
	protected void arraycopy(int srcindex, int dstindex, int num)
	{
		System.arraycopy(_data, srcindex * _objlen, _data, 
						dstindex * _objlen, num * _objlen);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#clearAtIndex(int)
	 */
	protected void clearAtIndex(int index)
	{
		index *= _objlen;
		final int end = index + _objlen;
		for (; index < end;)
			_data[ index++ ] = 0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#clearRange(int, int)
	 */
	protected void clearRange(int from, int to)
	{
		from *= _objlen;
		to *= _objlen;
		for (; from < to;)
			_data[ from++ ] = 0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#clone()
	 */
	public Object clone()
	{
		try
		{
			return new RLByte(this);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#compare(int, int)
	 */
	protected int compare(int index1, int index2)
	{
		// for each element of record compare
		if (index1 != index2)
		{
			int res;
			for (int i = 0; i < _indexkey.length; i++)
			{
				FieldProcessorBase fp1 = _processors[ _indexkey[ i ] ];
				if ( (res = fp1.compare(_data, index1 * _objlen, index2 * _objlen) ) != 0)
					return res;
			}
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#compare(int, java.lang.Object)
	 */
	protected int compare(int index1, Object key) throws IllegalAccessException
	{
		// for each element of record compare
		int res, off = index1 * _objlen;
		for (int i = 0; i < _indexkey.length; i++)
		{
			FieldProcessorBase fp1 = _processors[ _indexkey[ i ] ];
			if ( ( res = fp1.compare( _data, off, key ) ) != 0 )
				return res;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#ensureCapacity(int)
	 */
	public void ensureCapacity( int minCapacity )
	{
		modCount++;
		minCapacity *= _objlen;
		int oldCapacity = _data.length;
		if ( minCapacity > oldCapacity )
		{
			byte[] oldData = _data;
			int newCapacity = ( oldCapacity * 3 ) / 2 + 1;
			if ( newCapacity < minCapacity )
				newCapacity = minCapacity;
			_data = new byte[ newCapacity ];
			System.arraycopy( oldData, 0, _data, 0, _size() );
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#objectAtIndex(int)
	 */
	protected Object objectAtIndex(int index)
	{
		return objectFromArray(index);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#objectFromArray(int)
	 */
	protected Object objectFromArray(int index)
	{
		Object obj = null;
		try
		{
			obj = _objclass.newInstance();
			int off = index * _objlen;
			// read field by field
			for (int i = 0; i < _processors.length; i++)
				_processors[ i ].readFromArray(_data, off, obj);
		}
		catch (Exception ex)
		{
			obj = null;
		}
		return obj;
	}

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldBoolean(int, int)
     */
    //@Override
    public boolean getFieldBoolean( int index_obj, int index_fld )
    {
   		return Bits.getBoolean( _data, _processors[index_fld]._offset  + index_obj * _objlen);
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldByte(int, int)
     */
    //@Override
    public byte getFieldByte( int index_obj, int index_fld )
    {
    	return _data[ _processors[index_fld]._offset + index_obj * _objlen ];
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldChar(int, int)
     */
    //@Override
    public char getFieldChar( int index_obj, int index_fld )
    {
    	return Bits.getChar( _data, _processors[index_fld]._offset + index_obj * _objlen );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldDouble(int, int)
     */
    //@Override
    public double getFieldDouble( int index_obj, int index_fld )
    {
    	return Bits.getDouble( _data, _processors[index_fld]._offset + index_obj * _objlen );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#setFieldDouble(int, int, double)
     */
    //@Override
    public void setFieldDouble( int index_obj, int index_fld, double value )
    {
	    Bits.putDouble( _data, index_obj * _objlen + _processors[index_fld].getIndex() +
	    		_processors[index_fld].getOffset(), value );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldFloat(int, int)
     */
    //@Override
    public float getFieldFloat( int index_obj, int index_fld )
    {
    	return Bits.getFloat( _data, _processors[index_fld]._offset + index_obj * _objlen );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldInt(int, int)
     */
    //@Override
    public int getFieldInt( int index_obj, int index_fld )
    {
    	return Bits.getInt( _data, _processors[index_fld]._offset + index_obj * _objlen );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldLong(int, int)
     */
    //@Override
    public long getFieldLong( int index_obj, int index_fld )
    {
    	return Bits.getLong( _data, _processors[index_fld]._offset + index_obj * _objlen );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldShort(int, int)
     */
    //@Override
    public short getFieldShort( int index_obj, int index_fld )
    {
    	return Bits.getShort( _data, _processors[index_fld]._offset + index_obj * _objlen );
    }

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#objectToArray(java.lang.Object, int)
	 */
	protected int objectToArray(Object o, int index)
	{
		checkClass(o);
		try
		{
			int off = index * _objlen;
			// store field by field to byte[]
			for (int i = 0; i < _processors.length; i++)
				_processors[ i ].writeToArray(_data, off, o);
		}
		catch (Exception ex)
		{
		}
		return _objlen;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#readFromArray(java.lang.Object, int)
	 */
	protected boolean readFromArray(Object obj, int index)
	{
		int off = index * _objlen;
		try
		{
			// read field by field
			for (int i = 0; i < _processors.length; i++)
				_processors[ i ].readFromArray(_data, off, obj);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#trimToSize()
	 */
	public void trimToSize()
	{
		modCount++;
		int oldCapacity = _data.length;
		int newCapacity = _size();
		if ( newCapacity < oldCapacity )
		{
			byte[] oldData = _data;
			_data = new byte[newCapacity];
			System.arraycopy( oldData, 0, _data, 0, newCapacity );
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#swap1(int, int)
	 */
	protected void swap1(int index1, int index2)
	{
		if ( index1 != index2)
		{
			byte b;
			int off1 = index1 * _objlen, off2 = index2 * _objlen;
			for (int i = 0; i < _objlen; i++)
			{
				b = _data[ off1 ];
				_data[ off1++ ] = _data[ off2 ];
				_data[ off2++ ] = b;
			}
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#vecswap(int, int, int)
	 */
	protected void vecswap(int a, int b, int n)
	{
		if ( (n == 0) || (a== b) )
			return;
		byte val;
		int off1 = a * _objlen, off2 = b * _objlen;
		for (int i = 0; i < _objlen * n; i++)
		{
			val = _data[ off1 ];
			_data[ off1++ ] = _data[ off2 ];
			_data[ off2++ ] = val;
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#writeArray(java.io.ObjectOutputStream)
	 */
	protected void writeArray(ObjectOutputStream s)
    	throws java.io.IOException
	{
		// Write out array length
		s.writeInt(_size);

		// Write out all elements in the proper order.
		s.write(_data, 0, _size());
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#readArray(java.io.ObjectInputStream)
	 */
	protected void readArray(ObjectInputStream s) throws IOException, ClassNotFoundException
	{
		// Read in array length and allocate array
		_size = 0;
		final int newsize = s.readInt();
		ensureCapacity(newsize);
		// Read in all elements in the proper order.
		s.read(_data, 0, _size());
	}

	protected void debugSort(String suffix)
	{
		if (!_debug)
			return;
		if ( (_processors.length != 1) || (_size > 16))
			return;
		if ( _processors[0]._field.getName() != "ind" )
			return;
		StringBuffer sb = new StringBuffer();
		int off = 0;
		int val = Bits.getInt(_data, off);
		String str = String.valueOf(val);
		if ( str.length() < 2)
			sb.append('0');
		sb.append(str);
		for( int i = 1; i < _size; i++)
		{
			sb.append(' ');
			val = Bits.getInt( _data, i * _objlen );
			str = String.valueOf(val);
			if ( str.length() < 2)
				sb.append('0');
			sb.append(str);
		}
		if ( (suffix != null) && (suffix.length() > 0))
			sb.append(" : " + suffix);
		System.out.println(sb.toString());
	}	
}
