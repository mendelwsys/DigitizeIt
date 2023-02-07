/**
 * 
 */
package ru.ts.gisutils.common.records;

import ru.ts.gisutils.common.Sys;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorIntI extends FieldProcessorBase
{
	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.records.FieldProcessorBase#get_internal_type()
     */
    //@Override
    public int get_internal_type()
    {
	    return TYPE_INT;
    }

	/**
	 * @param fld  Field instance for this field
	 * @param offset offset in the basket
	 * @param index index in the record
	 */
	public FieldProcessorIntI(Field fld, int offset, int index)
	{
		super(fld, offset, index);
	    _size = Integer.SIZE >> 3;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, int)
	 */
	public int compare(int[] array, int recoff1, int recoff2)
	{
		return array[recoff1 + _offset] - array[recoff2 + _offset];
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, int[], int)
	 */
	public int compare(int[] arr1, int recoff1, int[] arr2, int recoff2)
	{
		return arr1[recoff1 + _offset] - arr2[recoff2 + _offset];
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, java.lang.Object)
	 */
	int compare(int[] arr, int recoff, Object record)
	        throws IllegalAccessException
	{
		return  Sys.signum( Bits.getInt(arr, recoff + _offset) - _field.getInt(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#readFromInts(int[], int, java.lang.Object)
	 */
	public int readFromInts(int[] arr, int recoff, Object obj)
	        throws IllegalAccessException
	{
		_field.setInt(obj, Bits.getInt(arr, recoff + _offset));
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#writeToInts(int[], int, java.lang.Object)
	 */
	public int writeToInts( int[] arr, int recoff, Object obj )
	        throws IllegalAccessException
	{
		Bits.putInt(arr, recoff + _offset, _field.getInt(obj));
		return _size;
	}

	//@Override
    public int compare( Object array, int recoff1, int recoff2 )
    {
	    return compare( (int[]) array, recoff1, recoff2 );
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
	    return readFromInts((int[])arr, off, obj);
    }

	//@Override
    public int writeToArray(Object arr, int off, Object obj) throws IllegalAccessException
    {
	    return writeToInts( (int[])arr, off, obj );
    }

}
