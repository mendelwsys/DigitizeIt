package ru.ts.utils.gui.elems;

/**
 * Progress viewer
 */
public interface IViewProgress
{
	/**
	 * set name of current operation
	 * @param nameoperation - name of operation
	 */
	void setCurrentOperation(String nameoperation);

	/**
	 * set maximal value of progress
	 * @param maxval - maximal value
	 */
	void setMaxProgress(int maxval);

	/**
	 * set progerss value of opeartion
	 * @param val - value of operation
	 */
	void setProgress(double val);

	/**
	 * @return name of current operation
	 */
	String getCurrentOperation();

	/**
	 * @return maximal value of progress
	 */
	int getMaxProgress();

	/**
	 * get value of progress
	 * @return value of progress
	 */
	int getProgress();

	/**
	 * @return is terminate loading
	 */
	boolean isTerminate();

	void setTittle(String title);
}