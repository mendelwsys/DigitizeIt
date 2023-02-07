package ru.ts.toykernel.plugins;

/**
 * ответ плагина
 */
public interface IAnswerBean
{
	ICommandBean getCommand();
	String getJSAnswer();
	byte[] getbAnswer();
}
