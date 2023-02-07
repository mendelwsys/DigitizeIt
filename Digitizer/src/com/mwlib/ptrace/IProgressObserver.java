package com.mwlib.ptrace;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 10.05.14
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public interface IProgressObserver
{
	/**
	 * Sets progress value in range from 0.0f (zero progress) to 1.0f (max progress)
     * @param pathDef
     */
	void setTraceProgress(PathDef pathDef);

    void showProgress(int val);
}