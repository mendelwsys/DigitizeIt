/**
 * <pre>
 * Created on 07.04.2009 16:02:56<br> 
 * by Syg<br> 
 * for project in 'ru.ts.common.misc'
 *</pre>
 */
package reclass.cmd;

/**
 * Package ru.ts.common.misc<br> Author 'Syg'<br> Created  07.04.2009
 */
public interface IGrpBuf
{
	/**
		 * Number of groups in the internal list
	 * @return int with size of the internal storage
	 */
	int groupNumber();

	/**
		 * Number of items in the storage, the same groupNumber()* groupSize()
	 * @return int value with items number in the internal array
	 */
	int itemsNumber();

	/**
		 * Number of items in the group
	 * @return int value with item in the group. Normally should be defined during
	 * constructor call
	 */
	int groupSize();

	/**
		 * Compact internal array to the real size needed
	 */
	void trimToSize();

	/**
	 * removes designated group
	 * @param ind index of group to remove
	 * @return true if index was correct else false
	 */
	boolean removeGroup( int ind );

	/**
	 * removes designated group
	 * @param start start index of group to remove inclusively
	 * @param end end index of group to remove exclusively
	 * @return true if index was correct else false
	 */
	boolean removeGroups( int start, int end );

	/**
	 * Clears whole internal group out of memory
	 */
	void clear();

	public abstract class Base implements IGrpBuf
	{
		/**
		 * Group size in items
		 */
		protected final int _groupSize;

		/**
		 * Size of internal list in groups
		 */
		protected int _size;

		abstract protected void _ensureSize( int size );

		protected Base(int groupSize)
		{
			_groupSize = groupSize;
			_size = 0;
		}

		public int groupNumber()
		{
			return _size;
		}

		public int itemsNumber()
		{
			return _size * _groupSize;
		}

		public int groupSize()
		{
			return _groupSize;
		}

		abstract public void trimToSize();

		public boolean removeGroup( int ind )
		{
			return removeGroups( ind, ind + 1 );
		}

		abstract public boolean removeGroups( int start, int end );

		public void clear()
		{
			_size = 0;
		}
	}
}