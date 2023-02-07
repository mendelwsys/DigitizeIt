/**
 * 
 */
package ru.ts.gisutils.common.records;

import java.lang.reflect.Field;

/**
 * @author sygsky
 * 
 */
abstract class FieldProcessorBase
{
	static public int TYPE_BYTE 	= 0;
	static public int TYPE_BOOLEAN	= 1; 
	static public int TYPE_CHAR		= 2; 
	static public int TYPE_SHORT	= 3; 
	static public int TYPE_INT		= 4; 
	static public int TYPE_FLOAT	= 5; 
	static public int TYPE_LONG		= 6; 
	static public int TYPE_DOUBLE	= 7;
	
	static private int TYPE_FIRST	= TYPE_BYTE;
	static private int TYPE_LAST	= TYPE_DOUBLE;

	/**
	 * gets internal type of the date of this processor. See
	 * constants of this base class for detail and mnemonic name
	 * @return
	 */
	abstract public int get_internal_type();
	
	/**
	 * length in bytes of internal storage array. Init it correctly as soon as
	 * possible!!!
	 */
	protected int	_array_item_length;

	Field	      _field;

	/**
	 * field name handled by this processor
	 * 
	 * @return the name of the field represented by this Field object
	 */
	public String getFieldName()
	{
		return _field.getName();
	}

	/**
	 * size of field in bytes. Init it in the specific constructor for each
	 * class
	 */
	protected int	_size;

	/**
	 * size of the field in bytes
	 */
	int size()
	{
		return _size;
	}

	/**
	 * offset to the field in array record basket in terms of array base item.
	 * E.g. if array is byte[], offset is the single only needed value to
	 * point to the beginning of the field start byte, else, if array is int[],
	 * offset only point to the start in the basket of the object. For fields
	 * with length being true dividend of sizeof(int), it is enough, but for such 
	 * primitive type as <code>byte</code>, <code>boolean</code>, <code>char</code>
	 * or </code>short</code> we will need some more, namely <code>_index</code>
	 * in the int item of the object basket
	 */
	protected int	_offset;

	/**
	 * @return offset to the field according to the begin of a record
	 */
	int getOffset()
	{
		return _offset;
	}
	
	/**
	 * index in the object basket item longer than byte (i.g. integer) if field is
	 * shorter (boolean, byte, char, short) than item length (integer in this example)
	 */
	protected int	_index;

	/**
	 * @return index to the field according to the begin of the array item if
	 *         field is shorter than smallest item of an array (i.g. field is
	 *         'short' but item is 'int')
	 */
	int getIndex()
	{
		return _index;
	}

	private FieldProcessorBase()
	{
	}

	FieldProcessorBase( Field field, int offset, int index )
	{
		_field = field;
		_offset = offset;
		_index = index;
	}

	/**
	 * reads value from array to the object field
	 * 
	 * @param arr
	 *            base array
	 * @param off
	 *            offset to start of value
	 * @param obj
	 *            object to store value
	 * @return size of field in bytes. 
	 */
	public abstract int readFromArray( Object arr, int off, Object obj )
	        throws IllegalAccessException;

	/**
	 * writes field from object into byte array
	 * 
	 * @param arr
	 *            base array
	 * @param off
	 *            offset to start of value
	 * @param obj
	 *            object to get field value
	 * @return size of record in array items. Depends on array type used (
	 *         bytes, ints etc)
	 */
	public abstract int writeToArray( Object arr, int off, Object obj )
	        throws IllegalAccessException;

	/**
	 * compares two values
	 * 
	 * @param array
	 *            of internal storage(may be byte[] or ahort[] or int[] or any
	 *            other
	 * @param recoff1
	 *            offset in array to 1st record
	 * @param recoff2
	 *            offset in array to 2nd record
	 * @return 1 if 1st > 2nd, 0 if they are equal, -1 if 1st < 2nd
	 */
	public abstract int compare( Object array, int recoff1, int recoff2 );

	/**
	 * compares two records (objects)
	 * 
	 * @param arr1
	 *            array where 1st object is stored
	 * @param recoff1
	 *            array offset to 1st record
	 * @param arr2
	 *            array where 2nd object is stored
	 * @param recoff2
	 *            array offset to 2nd record
	 * @return 1 if object > array record, 0 if they are equal, -1 if object <
	 *         array record
	 */
	public abstract int compare( Object arr1, int recoff1, Object arr2,
	        int recoff2 );

	/**
	 * compares the field of record in array and record in an object
	 *  
	 * @param arr -
	 *            some array where records are stored
	 * @param recoff -
	 *            array offset to the record
	 * @param record -
	 *            object where record is stored
	 * @return 1 if object > array record, 0 if they are equal, -1 if object <
	 *         array record
	 */
	abstract int compare( Object arr, int recoff, Object record )
	        throws IllegalAccessException;

	/**
	 * factory of field processors for int[]
	 * 
	 * @param fld -
	 *            Field for processor. Note - only fields of primitive type are
	 *            allowed as parameter!
	 * @param offset
	 *            in bbytes to the field value according to the start of record
	 * 
	 * @return new object of corresponding field processor
	 */
	static FieldProcessorBase getInstanceI( Field fld, int offset, int index )
	        throws IllegalArgumentException, ClassNotFoundException
	{
		Class type = fld.getType();
		if ( !type.isPrimitive() )
			throw new IllegalArgumentException( "Type should be primitive" );
		if ( type.equals( Integer.TYPE ) )
		{
			return new FieldProcessorIntI( fld, offset, index );
		}
		else if ( type.equals( Long.TYPE ) )
		{
			return new FieldProcessorLongI( fld, offset, index );
		}
		else if ( type.equals( Short.TYPE ) )
		{
			return new FieldProcessorShortI( fld, offset, index );
		}
		else if ( type.equals( Character.TYPE ) )
		{
			return new FieldProcessorCharI( fld, offset, index );
		}
		else if ( type.equals( Byte.TYPE ) )
		{
			return new FieldProcessorByteI( fld, offset, index );
		}
		else if ( type.equals( Float.TYPE ) )
		{
			return new FieldProcessorFloatI( fld, offset, index );
		}
		else if ( type.equals( Double.TYPE ) )
		{
			return new FieldProcessorDoubleI( fld, offset, index );
		}
		else if ( type.equals( Boolean.TYPE ) )
		{
			return new FieldProcessorBooleanI( fld, offset, index );
		}
		else
		{
			// We missed a primitive type!
			throw new java.lang.ClassNotFoundException(
			        "Unknown primitive type " + type.toString() );
		}
	}

	/**
	 * factory of field processors for byte[]
	 * 
	 * @param fld -
	 *            Field for processor. Note - only fields of primitive type are
	 *            allowed as parameter!
	 * @param offset
	 *            in bytes to the field value according to the start of record
	 * 
	 * @return new object of corresponding field processor
	 */
	static FieldProcessorBase getInstanceB( Field fld, int offset )
	        throws IllegalArgumentException, ClassNotFoundException
	{
		Class type = fld.getType();
		if ( !type.isPrimitive() )
			throw new IllegalArgumentException( "Type should be primitive" );
		if ( type.equals( Integer.TYPE ) )
		{
			return new FieldProcessorIntB( fld, offset );
		}
		else if ( type.equals( Long.TYPE ) )
		{
			return new FieldProcessorLongB( fld, offset );
		}
		else if ( type.equals( Short.TYPE ) )
		{
			return new FieldProcessorShortB( fld, offset );
		}
		else if ( type.equals( Character.TYPE ) )
		{
			return new FieldProcessorCharB( fld, offset );
		}
		else if ( type.equals( Byte.TYPE ) )
		{
			return new FieldProcessorByteB( fld, offset );
		}
		else if ( type.equals( Float.TYPE ) )
		{
			return new FieldProcessorFloatB( fld, offset );
		}
		else if ( type.equals( Double.TYPE ) )
		{
			return new FieldProcessorDoubleB( fld, offset );
		}
		else if ( type.equals( Boolean.TYPE ) )
		{
			return new FieldProcessorBooleanB( fld, offset );
		}
		else
		{
			// We missed a primitive type!
			throw new java.lang.ClassNotFoundException(
			        "Unknown primitive type " + type.toString() );
		}
	}

}
