package ru.ts.toykernel.plugins;

/**
 * standart answer of the
 */
public class AnswerBean implements IAnswerBean
{
	private ICommandBean cmd;
	private String jsAnswver;
	private byte[] binanswer;

	public AnswerBean(ICommandBean cmd,String jsAnswver,byte[] binanswer)
	{
		this.cmd = cmd;
		this.jsAnswver = jsAnswver;
		this.binanswer = binanswer;
	}
	public ICommandBean getCommand()
	{
		return cmd;
	}

	public String getJSAnswer()
	{
		return jsAnswver;
	}

	public byte[] getbAnswer()
	{
		return binanswer;
	}
}
