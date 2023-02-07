/**
 * Created on 11.10.2007 12:36:25 2007 by sygsky
 * for project in 'ru.ts.gisutils.common.records' of 'test' 
 */
package ru.ts.gisutils.common.records;

import ru.ts.utils.FunctEmul;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorByteB extends FieldProcessorBase
{
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return super.TYPE_BYTE;
    }

	/**
	 * 
	 */
	public FieldProcessorByteB(Field fld, int offset)
	{
	    super(fld, offset, -1);
	    _size = Byte.SIZE >> 3;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, int)
	 */
	public int compare(Object array, int recoff1, int recoff2)
	{
		return FunctEmul.isignum(((byte[])array)[recoff1 + _offset] - ((byte[])array)[recoff2 + _offset] );
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object, int)
	 */
	public int compare(Object arr1, int recoff1, Object arr2, int recoff2)
	{
		return FunctEmul.isignum(((byte[])arr1)[recoff1 + _offset] - ((byte[])arr2)[recoff2 + _offset] );
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#compare(java.lang.Object, int, java.lang.Object)
	 */
	int compare(Object arr, int recoff, Object record)
	        throws IllegalAccessException
	{
	    return FunctEmul.isignum(((byte[])arr)[recoff + _offset] - _field.getByte(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#readFromArray(java.lang.Object, int, java.lang.Object)
	 */
	public int readFromArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		_field.setByte(obj, ((byte[])arr)[off + _offset]);
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBase#writeToArray(java.lang.Object, int, java.lang.Object)
	 */
	public int writeToArray(Object arr, int off, Object obj)
	        throws IllegalAccessException
	{
		((byte[])arr)[off + _offset] = _field.getByte(obj);
		return _size;
	}

}
