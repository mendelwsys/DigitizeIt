/**
 * Created on 11.10.2007 17:09:31 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import ru.ts.utils.FunctEmul;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorIntB extends FieldProcessorBase
{
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return super.TYPE_INT;
    }

	/**
	 * @param field
	 * @param offset
	 * @param index
	 */
	public FieldProcessorIntB(Field field, int offset, int index)
	{
		super(field, offset, index);
	    _size = Integer.SIZE >> 3;
	}

	public FieldProcessorIntB(Field field, int offset)
	{
		this(field, offset, -1);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, int)
	 */
	public int compare(Object array, int recoff1, int recoff2)
	{
		int i1 = Bits.getInt((byte[])array, recoff1 + _offset);
		int i2 = Bits.getInt((byte[])array, recoff2 + _offset);
		return FunctEmul.isignum(i1 - i2);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object, int)
	 */
	public int compare(Object arr1, int recoff1, Object arr2, int recoff2)
	{
		int i1 = Bits.getInt((byte[])arr1, recoff1 + _offset);
		int i2 = Bits.getInt((byte[])arr2, recoff2 + _offset);
		return FunctEmul.isignum(i1 - i2);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object)
	 */
	int compare(Object arr, int recoff, Object record)
	        throws IllegalAccessException
	{
	    return  FunctEmul.isignum(Bits.getInt((byte[])arr, recoff) - _field.getInt(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#readFromArray(java.lang.Object, int, java.lang.Object)
	 */
	public int readFromArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		_field.setInt(obj, Bits.getInt((byte[])arr, off + _offset));
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#writeToArray(java.lang.Object, int, java.lang.Object)
	 */
	public int writeToArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		Bits.putInt((byte[])arr, off + _offset, _field.getInt(obj));
		return _size;
	}

}
