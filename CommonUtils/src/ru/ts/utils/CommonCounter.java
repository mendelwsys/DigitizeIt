package ru.ts.utils;

/**
 * Простой счетчик актуальный во время исполнения
 */
public class CommonCounter
{
	static int objcntr=0;
	public static synchronized int getComCounter()
	{
		return ++objcntr;
	}
}
