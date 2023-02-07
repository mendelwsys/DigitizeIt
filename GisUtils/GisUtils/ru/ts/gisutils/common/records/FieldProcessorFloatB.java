/**
 * Created on 11.10.2007 17:02:56 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorFloatB extends FieldProcessorBase
{
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return super.TYPE_FLOAT;
    }

	public FieldProcessorFloatB(Field field, int offset, int index)
    {
	    super(field, offset, index);
		_size = Float.SIZE >> 3;
    }
	public FieldProcessorFloatB(Field field, int offset)
    {
		this(field, offset, -1);
    }

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object, int)
	 */
	public int compare(Object arr1, int recoff1, Object arr2, int recoff2)
	{
		return Float.compare(Bits.getFloat((byte[])arr1, recoff1 + _offset), Bits.getFloat((byte[])arr2, recoff2 + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object)
	 */
	int compare(Object arr, int recoff, Object record)
	        throws IllegalAccessException
	{
	    return Float.compare(Bits.getFloat((byte[])arr, recoff + _offset), _field.getFloat(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#readFromArray(java.lang.Object, int, java.lang.Object)
	 */
	public int readFromArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		_field.setFloat(obj, Bits.getFloat((byte[])arr, off + _offset));
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#writeToArray(java.lang.Object, int, java.lang.Object)
	 */
	public int writeToArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		Bits.putFloat((byte[])arr, off + _offset, _field.getFloat(obj));
		return _size;
	}
	
	public int compare(Object array, int recoff1, int recoff2)
    {
		return Float.compare(Bits.getFloat((byte[])array, recoff1 + _offset), Bits.getFloat((byte[])array, recoff2 + _offset));
    }

}
