/**
 * Created on 11.10.2007 16:58:51 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorDoubleB extends FieldProcessorBase
{
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return super.TYPE_DOUBLE;
    }

	/**
	 * @param field
	 * @param offset
	 * @param index
	 */
	public FieldProcessorDoubleB(Field field, int offset)
	{
		super(field, offset, -1);
		_size = Double.SIZE >> 3;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, int)
	 */
	public int compare(Object array, int recoff1, int recoff2)
	{
		return Double.compare(Bits.getDouble((byte[])array, recoff1 + _offset), Bits.getDouble((byte[])array, recoff2 + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object, int)
	 */
	public int compare(Object arr1, int recoff1, Object arr2, int recoff2)
	{
		return Double.compare(Bits.getDouble((byte[])arr1, recoff1 + _offset), Bits.getDouble((byte[])arr2, recoff2 + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object)
	 */
	int compare(Object arr, int recoff, Object record)
	        throws IllegalAccessException
	{
	    return Double.compare(Bits.getDouble((byte[])arr, recoff + _offset), _field.getDouble(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#readFromArray(java.lang.Object, int, java.lang.Object)
	 */
	public int readFromArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		_field.setDouble(obj, Bits.getDouble((byte[])arr, off + _offset));
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#writeToArray(java.lang.Object, int, java.lang.Object)
	 */
	public int writeToArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		Bits.putDouble((byte[])arr, off + _offset, _field.getDouble(obj));
		return _size;
	}

}
