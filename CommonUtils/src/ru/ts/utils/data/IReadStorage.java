package ru.ts.utils.data;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * Абстрактное хранилище сериализированных объектов
 */
public interface IReadStorage<T extends Serializable>
		extends Enumeration<T>
{
	int getCurpos();

	T get(int i);

	boolean hasMoreElements();

	boolean hasPrevElements();

	T nextElement();

	void setposon(int pos);

	T prevElement();

	int size();

	T remove(int i);
}
