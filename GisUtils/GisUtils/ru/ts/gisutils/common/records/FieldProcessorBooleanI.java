/**
 * 
 */
package ru.ts.gisutils.common.records;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorBooleanI extends FieldProcessorBase
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
	 * @param fld
	 * @param offset
	 * @param index
	 */
	public FieldProcessorBooleanI(Field fld, int offset, int index)
	{
		super(fld, offset, index);
		_size = 1;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, int)
	 */
	public int compare(int[] array, int recoff1, int recoff2)
	{
		boolean b1 = Bits.getBoolean(array, recoff1 + _offset, _index);
		boolean b2 = Bits.getBoolean(array, recoff2 + _offset, _index);
		return (b1 ? 1: 0) - (b2 ? 1 : 0);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, int[], int)
	 */
	public int compare(int[] arr1, int recoff1, int[] arr2, int recoff2)
	{
		boolean b1 = Bits.getBoolean(arr1, recoff1 + _offset, _index);
		boolean b2 = Bits.getBoolean(arr2, recoff2 + _offset, _index);
		return (b1 ? 1: 0) - (b2 ? 1 : 0);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, java.lang.Object)
	 */
	int compare(int[] arr, int recoff, Object record)
	        throws IllegalAccessException
	{
		boolean b1 = Bits.getBoolean(arr, recoff + _offset, _index);
		boolean b2 = _field.getBoolean(record);
		return (b1 ? 1: 0) - (b2 ? 1 : 0);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#readFromInts(int[], int, java.lang.Object)
	 */
	public int readFromInts(int[] arr, int recoff, Object obj)
	        throws IllegalAccessException
	{
		_field.setBoolean( obj, Bits.getBoolean(arr, recoff + _offset, _index) );
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#writeToInts(int[], int, java.lang.Object)
	 */
	public int writeToInts(int[] arr, int recoff, Object obj)
	        throws IllegalAccessException
	{
		Bits.putBoolean( arr, recoff + _offset, _index, _field.getBoolean(obj) );
		return _size;
	}

	//@Override
    public int compare(Object array, int recoff1, int recoff2)
    {
	    return compare( (int[])array, recoff1, recoff2 );
    }

	//@Override
    public int compare(Object arr1, int recoff1, Object arr2, int recoff2)
    {
	    return compare( (int[])arr1, recoff1, (int[])arr2, recoff2 );
    }

	//@Override
    int compare(Object arr, int recoff, Object record) throws IllegalAccessException
    {
		return compare( (int[])arr, recoff, record );
    }

	//@Override
    public int readFromArray(Object arr, int off, Object obj) throws IllegalAccessException
    {
		return readFromInts( (int[])arr, off, obj );
    }

	//@Override
    public int writeToArray(Object arr, int off, Object obj) throws IllegalAccessException
    {
		return writeToInts( (int[])arr, off, obj );
    }

}
