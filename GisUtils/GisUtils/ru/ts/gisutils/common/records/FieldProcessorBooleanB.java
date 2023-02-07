/**
 * Created on 11.10.2007 16:42:30 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorBooleanB extends FieldProcessorBase
{

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return super.TYPE_BOOLEAN;
    }

	/**
	 * @param field
	 * @param offset
	 * @param index
	 */
	public FieldProcessorBooleanB(Field field, int offset)
	{
		super(field, offset, -1);
		_size = 1;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, int)
	 */
	public int compare(Object array, int recoff1, int recoff2)
	{
		boolean b1 = Bits.getBoolean((byte[])array, recoff1 + _offset);
		boolean b2 = Bits.getBoolean((byte[])array, recoff2 + _offset);
		return (b1 ? 1: 0) - (b2 ? 1 : 0);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object, int)
	 */
	public int compare(Object arr1, int recoff1, Object arr2, int recoff2)
	{
		int b1 = ((byte[])arr1)[recoff1 + _offset] > 0 ? 1 : 0;
		int b2 = ((byte[])arr2)[recoff2 + _offset] > 0 ? 1 : 0;
		return b1 - b2;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object)
	 */
	int compare(Object arr, int recoff, Object record)
	        throws IllegalAccessException
	{
	    return (Bits.getBoolean((byte[])arr, recoff + _offset) ? 1 : 0) - (_field.getBoolean(record) ? 1 :0);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#readFromArray(java.lang.Object, int, java.lang.Object)
	 */
	public int readFromArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		_field.setBoolean(obj, Bits.getBoolean((byte[])arr, off + _offset) );
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#writeToArray(java.lang.Object, int, java.lang.Object)
	 */
	public int writeToArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		Bits.putBoolean((byte[])arr, off + _offset, _field.getBoolean(obj));
		return _size;
	}

}
