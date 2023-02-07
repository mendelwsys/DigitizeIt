/**
 * Created on 22.10.2007 15:54:05 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

/**
 * {@link RecordListBase} extended for int[] data storage.
 * 
 * @author sygsky
 *
 * 
 * 
 */
public final class RLInt extends RecordListBase
{

    private static final long serialVersionUID = 1L;
    
    /* 
	 * MAIN storage for the records
	 */
	private transient int[] _data;
	
	/**
	 * @param record
	 * @param initialCapacity
	 * @throws ClassNotFoundException
	 */
	public RLInt(Class record, int initialCapacity)
	        throws ClassNotFoundException
	{
		super(record, initialCapacity);
	}

	/**
	 * @param record
	 * @throws ClassNotFoundException
	 */
	public RLInt(Class record) throws ClassNotFoundException
	{
		super(record);
	}

	/**
	 * internal constructor for clone method only
	 * 
	 * @param ra -
	 *            parent RecordList object
	 */
	RLInt(RLInt ra) throws ClassNotFoundException
	{
		this(ra._objclass, ra._size);
		System.arraycopy(_data, 0, ra._data, 0, _size * _objlen);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#_init(java.lang.Class)
	 */
	//@Override
	protected void _init(Class record) throws ClassNotFoundException
	{
		// init some of internal variables
		_objclass = record;
		_size = 0;
		_data = new int[0];
		_indexkey = new int[0];

		int fldind = 0; // next free integer field in the record
		int w1ind = -1; // current int index used for 1 byte entries packing
		int w1cnt = 0; // counter of used 1byte entries in the current int
		int w2ind = -1; // current int index used for 2 byte entries packing
		int w2cnt = 0; // counter of used 2 byte entries in the current int
		/*
		 * Do obtaining a list of all public fields, both declared and
		 */
		Field[] fields = record.getFields();
		_processors = new FieldProcessorBase[fields.length];
		for (int i = 0; i < fields.length; i++)
		{
			Field field = fields[ i ];

			/*
			 * ensure that class contains ONLY primitive fields
			 */
			if (!field.getType().isPrimitive()) // illegal field found
				throw new IllegalArgumentException("Class \""
				        + record.getName() + "\" field \"" + field.getName()
				        + "\" should be primitive.");

			/*
			 * create processor with offset index still not set
			 */
			FieldProcessorBase fp = FieldProcessorBase.getInstanceI(field, 0,
			        0);
			int size = fp.size(); // size of the current field in bytes

			/*
			 * calculate offset for every and index for some field type
			 */
			switch ( size )
			{
			case 1: // 'byte' and 'boolean' field types are here
				if (w1ind == -1) // init for first time usage
				{
					w1ind = fldind++;
					w1cnt = 0;
				}
				if (w1cnt == 4) // current int filled totally, reserve next
				{
					w1ind = fldind++;
					w1cnt = 0;
				}
				fp._offset = w1ind;
				fp._index = w1cnt++; // bump used counter
				break;
			case 2: // 'short' and 'char' types
				if (w2ind == -1) // init for first time usage
				{
					w2ind = fldind++;
					w2cnt = 0;
				}
				if (w2cnt == 2) // current int filled totally, reserve next
				{
					w2ind = fldind++;
					w2cnt = 0;
				}
				fp._offset = w2ind;
				fp._index = w2cnt++; // bump used counter
				break;
			case 4: // 'int' and 'float'
				fp._offset = fldind++; // bump to the next int basket in array
				fp._index = 0; // dummy value in this case
				break;
			case 8: // 'long' and 'double' are here
				fp._offset = fldind; // bump to the next int basket in array
				fldind += 2; // bump to next basket reserving 8 bytes in array
				fp._index = 0; // dummy value in this case
				break;
			}
			_processors[ i ] = fp;
		}
		_objlen = fldind;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#arraycopy(int, int, int)
	 */
	//@Override
	protected void arraycopy(int srcindex, int dstindex, int num)
	{
		System.arraycopy(_data, srcindex * _objlen, _data, dstindex
		        * _objlen, num * _objlen);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#clearAtIndex(int)
	 */
	//@Override
	protected void clearAtIndex(int index)
	{
		final int end = (index *= _objlen) + _objlen;
		for (; index < end;)
			_data[ index++ ] = 0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#clearRange(int, int)
	 */
	//@Override
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
	//@Override
	public Object clone()
	{
		try
		{
			return new RLInt(this);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#compare(int, int)
	 */
	//@Override
	protected int compare(int index1, int index2)
	{
		// for each element of record compare
		int res, 
			off1 = index1 * _objlen, 
			off2 = index2 * _objlen;
		for (int i = 0; i < _indexkey.length; i++)
		{
			FieldProcessorBase fp1 = _processors[ _indexkey[ i ] ];
			if ((res = fp1.compare(_data, off1 , off2)) != 0)
				return res;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#compare(int, java.lang.Object)
	 */
	//@Override
	protected int compare(int index1, Object key) throws IllegalAccessException
	{
		// for each element of record compare
		int res, off = index1 * _objlen;
		for (int i = 0; i < _indexkey.length; i++)
		{
			FieldProcessorBase fp1 = _processors[ _indexkey[ i ] ];
			if ( (res = fp1.compare(_data, off, key)) != 0 )
				return res;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#ensureCapacity(int)
	 */
	//@Override
	public void ensureCapacity(int minCapacity)
	{
		modCount++;
		minCapacity *= _objlen;
		int oldCapacity = _data.length;
		if (minCapacity > oldCapacity)
		{
			int[] oldData = _data;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			_data = new int[newCapacity];
			System.arraycopy(oldData, 0, _data, 0, _size());
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#objectAtIndex(int)
	 */
	//@Override
	protected Object objectAtIndex(int index)
	{
		return objectFromArray(index);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#objectFromArray(int)
	 */
	//@Override
	protected Object objectFromArray(int index)
	{
		Object obj = null;
		try
		{
			obj = _objclass.newInstance();
			final int off = index * _objlen;
			// read field by field
			for (int i = 0; i < _processors.length; i++)
				_processors[ i ].readFromArray(_data, off, obj);
		}
		catch (Exception ex)
		{
			System.err.println( ex.getMessage() );
			return null;
		}
		return obj;
	}


	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldBoolean(int, int)
     */
    //@Override
    public boolean getFieldBoolean( int index_obj, int index_fld )
    {
    	return Bits.getBoolean( _data, index_obj * _objlen + _processors[index_fld]._offset, 
    			_processors[index_fld]._index );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldByte(int, int)
     */
    //@Override
    public byte getFieldByte( int index_obj, int index_fld )
    {
    	return Bits.getByte( _data,  index_obj * _objlen + _processors[index_fld]._offset, 
    			_processors[index_fld]._index );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldChar(int, int)
     */
    //@Override
    public char getFieldChar( int index_obj, int index_fld )
    {
    	return Bits.getChar( _data,  index_obj * _objlen + _processors[index_fld]._offset, 
    			_processors[index_fld]._index );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldDouble(int, int)
     */
    //@Override
    public double getFieldDouble( int index_obj, int index_fld )
    {
    	return Bits.getDouble( _data, index_obj * _objlen + _processors[index_fld]._offset );
    }

    
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#setFieldDouble(int, int, double)
     */
    //@Override
    public void setFieldDouble( int index_obj, int index_fld, double value )
    {
	    Bits.putDouble( _data, index_obj * _objlen + _processors[index_fld]._offset, value );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldFloat(int, int)
     */
    //@Override
    public float getFieldFloat( int index_obj, int index_fld )
    {
    	return Bits.getFloat( _data, index_obj * _objlen + _processors[index_fld]._offset );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldInt(int, int)
     */
    //@Override
    public int getFieldInt( int index_obj, int index_fld )
    {
    	return _data[ index_obj * _objlen  + _processors[ index_fld ]._offset ]; 
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldLong(int, int)
     */
    //@Override
    public long getFieldLong( int index_obj, int index_fld )
    {
    	return Bits.getLong( _data, index_obj * _objlen +  _processors[index_fld]._offset );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.RecordListBase#getFieldShort(int, int)
     */
    //@Override
    public short getFieldShort( int index_obj, int index_fld )
    {
    	return Bits.getShort( _data,  index_obj * _objlen + _processors[index_fld]._offset, 
    			_processors[index_fld]._index );
    }

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#objectToArray(java.lang.Object, int)
	 */
	//@Override
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
			return 0;
		}
		return _objlen;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#readArray(java.io.ObjectInputStream)
	 */
	//@Override
	protected void readArray(ObjectInputStream s) throws IOException,
	        ClassNotFoundException
	{
		// Read in array length and allocate array
		_size = 0;
		final int newsize = s.readInt();
		ensureCapacity(newsize);
		// Read in all elements in the proper order.
		for( int i = 0; i < newsize; i++)
			_data[ i ] = s.readInt();
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#readFromArray(java.lang.Object, int)
	 */
	//@Override
	protected boolean readFromArray(Object obj, int index)
	{
		final int off = index * _objlen;
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
	 * @see ru.ts.gisutils.common.records.RecordListBase#swap1(int, int)
	 */
	//@Override
	protected void swap1(int index1, int index2)
	{
		if ( index1 != index2)
		{
			int val;
			int off1 = index1 * _objlen, off2 = index2 * _objlen;
			for (int i = 0; i < _objlen; i++)
			{
				val = _data[ off1 ];
				_data[ off1++ ] = _data[ off2 ];
				_data[ off2++ ] = val;
			}
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#trimToSize()
	 */
	//@Override
	public void trimToSize()
	{
		modCount++;
		int oldCapacity = _data.length;
		int newCapacity = _size();
		if (newCapacity < oldCapacity)
		{
			int[] oldData = _data;
			_data = new int[newCapacity];
			System.arraycopy(oldData, 0, _data, 0, newCapacity);
		}
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.RecordListBase#vecswap(int, int, int)
	 */
	//@Override
	protected void vecswap(int a, int b, int n)
	{
		if ( (n == 0) || (a== b) )
			return;
		int val;
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
	//@Override
	protected void writeArray(ObjectOutputStream s) throws IOException
	{
		// Write out array length
		s.writeInt(_size);
		// Write out all elements in the proper order.
		for(int i = 0; i < _size; i++)
		s.write(_data[ i ]);
	}
	
}
