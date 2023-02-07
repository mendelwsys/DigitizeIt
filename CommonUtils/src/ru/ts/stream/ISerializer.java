package ru.ts.stream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * Serializer for serialized object
 */
public interface ISerializer
{
	/**
	 * save object to stream
	 * @param dos - data output stream
	 * @throws Exception - when error
	 */
	void saveToStream(DataOutputStream dos) throws Exception;

	/**
	 * restore object from stream
	 * @param dis - data input stream
	 * @throws Exception = whne error
	 */
	void loadFromStream(DataInputStream dis) throws Exception;
}
