package ru.ts.utils.gui.elems;

public class EmptyProgress implements IViewProgress
{
	public void setCurrentOperation(String nameoperation)
	{
	}

	public void setMaxProgress(int maxval)
	{
	}

	public void setProgress(double val)
	{
	}

	public String getCurrentOperation()
	{
		return "";
	}

	public int getMaxProgress()
	{
		return 0;
	}

	public int getProgress()
	{
		return 0;
	}

	public boolean isTerminate()
	{
		return false;
	}

	public void setTittle(String title)
	{
	}
}
