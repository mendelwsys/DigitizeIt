/**
 * 
 */
package ru.ts.gisutils.common.records;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorFloatI extends FieldProcessorBase
{
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return super.TYPE_FLOAT;
    }

    /**
	 * @param fld
	 * @param offset
	 * @param index
	 */
	public FieldProcessorFloatI(Field fld, int offset, int index)
	{
		super(fld, offset, index);
		_size = Float.SIZE >> 3;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, int)
	 */
	public int compare(int[] array, int recoff1, int recoff2)
	{
		return Float.compare(Bits.getFloat(array, recoff1 + _offset), Bits.getFloat(array, recoff2 + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, int[], int)
	 */
	public int compare(int[] arr1, int recoff1, int[] arr2, int recoff2)
	{
		return Float.compare(Bits.getFloat(arr1, recoff1 + _offset), Bits.getFloat(arr2, recoff2 + _offset));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, java.lang.Object)
	 */
	int compare(int[] arr, int recoff, Object record)
	        throws IllegalAccessException
	{
	    return Float.compare(Bits.getFloat(arr, recoff + _offset), _field.getFloat(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#readFromInts(int[], int, java.lang.Object)
	 */
	public int readFromInts(int[] arr, int recoff, Object obj)
	        throws IllegalAccessException
	{
		_field.setFloat(obj, Bits.getFloat(arr, recoff + _offset));
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#writeToInts(int[], int, java.lang.Object)
	 */
	public int writeToInts(int[] arr, int recoff, Object obj)
	        throws IllegalAccessException
	{
		Bits.putFloat(arr, recoff + _offset, _field.getFloat(obj));
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
