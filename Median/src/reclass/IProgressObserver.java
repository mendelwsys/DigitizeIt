package reclass;

/**
	 * An asynchronous update interface for receiving notifications about progress information update
 */
public interface IProgressObserver
{
	/**
	 * Sets progress value in range from 0.0f (zero progress) to 1.0f (max progress)
	 * @param value
	 */
	void setProgress( float value );
}