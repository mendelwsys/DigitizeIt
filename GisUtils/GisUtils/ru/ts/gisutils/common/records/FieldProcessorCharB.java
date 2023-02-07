/**
 * Created on 11.10.2007 16:50:32 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import ru.ts.utils.FunctEmul;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorCharB extends FieldProcessorBase
{
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return super.TYPE_CHAR;
    }

	/**
	 * @param field
	 * @param offset
	 * @param index
	 */
	public FieldProcessorCharB(Field field, int offset)
	{
		super(field, offset, -1);
		_size = Character.SIZE >> 3;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, int)
	 */
	public int compare(Object array, int recoff1, int recoff2)
	{
		return FunctEmul.isignum(Bits.getChar((byte[])array, recoff1 + _offset) - Bits.getChar((byte[])array, recoff2 + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object, int)
	 */
	public int compare(Object arr1, int recoff1, Object arr2, int recoff2)
	{
		return FunctEmul.isignum(Bits.getChar((byte[])arr1, recoff1 + _offset)
				- Bits.getChar((byte[])arr2, recoff2 + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object)
	 */
	int compare(Object arr, int recoff, Object record)
	        throws IllegalAccessException
	{
		return FunctEmul.isignum(Bits.getChar((byte[])arr, recoff + _offset)
				- _field.getChar(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#readFromArray(java.lang.Object, int, java.lang.Object)
	 */
	public int readFromArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		_field.setChar(obj, Bits.getChar((byte[])arr, off + _offset));
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#writeToArray(java.lang.Object, int, java.lang.Object)
	 */
	public int writeToArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		Bits.putChar((byte[])arr, off + _offset, _field.getChar(obj));
		return _size;
	}

}
