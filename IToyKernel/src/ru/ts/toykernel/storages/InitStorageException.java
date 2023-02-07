package ru.ts.toykernel.storages;

/**
 * Init storage exception
 */
public class InitStorageException extends Exception
{
	public InitStorageException()
	{
	}

	public InitStorageException(Throwable cause)
	{
		super(cause);
	}

	public InitStorageException(String message)
	{
		super(message);
	}

	public InitStorageException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
