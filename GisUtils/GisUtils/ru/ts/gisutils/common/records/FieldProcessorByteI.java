/**
 * 
 */
package ru.ts.gisutils.common.records;

import ru.ts.utils.FunctEmul;

import java.lang.reflect.Field;

/**
 * @author sygsky
 *
 */
final class FieldProcessorByteI extends FieldProcessorBase
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
	 * @param fld
	 * @param offset
	 * @param index
	 */
	public FieldProcessorByteI(Field fld, int offset, int index)
	{
		super(fld, offset, index);
	    _size = Byte.SIZE >> 3;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, int)
	 */
	public int compare(int[] array, int recoff1, int recoff2)
	{
		return FunctEmul.isignum((Bits.getByte(array, recoff1 + _offset, _index) - Bits.getByte(array, recoff2 + _offset, _index)));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, int[], int)
	 */
	public int compare(int[] arr1, int recoff1, int[] arr2, int recoff2)
	{
		return FunctEmul.isignum(Bits.getByte(arr1, recoff1 + _offset, _index) - Bits.getByte(arr2, recoff2 + _offset, _index));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#compare(int[], int, java.lang.Object)
	 */
	int compare(int[] arr, int recoff, Object record)
	        throws IllegalAccessException
	{
		return FunctEmul.isignum(Bits.getByte(arr, recoff + _offset, _index) - _field.getByte(record));
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#readFromInts(int[], int, java.lang.Object)
	 */
	public int readFromInts(int[] arr, int recoff, Object obj)
	        throws IllegalAccessException
	{
		_field.setByte(obj, Bits.getByte(arr, recoff +_offset, _index));
		return _size;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.records.FieldProcessorBaseI#writeToInts(int[], int, java.lang.Object)
	 */
	public int writeToInts(int[] arr, int recoff, Object obj)
	        throws IllegalAccessException
	{
		Bits.putByte(arr, recoff + _offset, _index,  _field.getByte(obj));
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
	    return compare((int[])arr1, recoff1, (int[])arr2, recoff2);
    }

	//@Override
    int compare(Object arr, int recoff, Object record) throws IllegalAccessException
    {
	    return compare((int[])arr, recoff, record);
    }

	//@Override
    public int readFromArray(Object arr, int off, Object obj) throws IllegalAccessException
    {
	    return readFromInts( (int[])arr, off, obj );
    }

	//@Override
    public int writeToArray(Object arr, int off, Object obj) throws IllegalAccessException
    {
	    return writeToInts((int[])arr, off, obj);
    }

}
