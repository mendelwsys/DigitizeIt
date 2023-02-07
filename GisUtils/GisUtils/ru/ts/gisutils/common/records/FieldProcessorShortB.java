/**
 * Created on 11.10.2007 17:17:40 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import ru.ts.utils.FunctEmul;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorShortB extends FieldProcessorBase
{
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return super.TYPE_SHORT;
    }

	/**
	 * @param field
	 * @param offset
	 * @param index
	 */
	public FieldProcessorShortB(Field field, int offset, int index)
	{
		super(field, offset, index);
		_size = Short.SIZE >> 3;
	}

	public FieldProcessorShortB(Field field, int offset)
	{
		this(field, offset, -1);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, int)
	 */
	public int compare(Object array, int recoff1, int recoff2)
	{
	    return FunctEmul.isignum(Bits.getShort((byte[])array, recoff1  + _offset) - Bits.getShort((byte[])array, recoff2  + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object, int)
	 */
	public int compare(Object arr1, int recoff1, Object arr2, int recoff2)
	{
	    return FunctEmul.isignum(Bits.getShort((byte[])arr1, recoff1  + _offset) - Bits.getShort((byte[])arr1, recoff2  + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object)
	 */
	int compare(Object arr, int recoff, Object record)
	        throws IllegalAccessException
	{
	    return  FunctEmul.isignum(Bits.getShort((byte[])arr, recoff) - _field.getShort(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#readFromArray(java.lang.Object, int, java.lang.Object)
	 */
	public int readFromArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		_field.setShort(obj, Bits.getShort((byte[])arr, off + _offset));
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#writeToArray(java.lang.Object, int, java.lang.Object)
	 */
	public int writeToArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		Bits.putShort((byte[])arr, off + _offset, _field.getShort(obj));
		return _size;
	}

}
