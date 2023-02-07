/*
 * Created on 11 Oct 2007
 *
 */
package ru.ts.gisutils.tc;

/**
 * @author yugl
 *
 * Интерфейс базового класса для представления объектов, обрабатываемых процессом
 * топологической чистки.
 */
public interface ITcObjBase {

	/**
	 * Идентификатор объекта (в рамках процесса чистки)
	 */ 
	public abstract TcId tcid();

	/**
	 * Идентификатор объекта-родителя (для самостоятельных объектов = null)
	 */ 
	public abstract TcId tcparent();

	/**
	 * Данные, связанные с объектом в процессе чистки.
	 * Содержание данных зависит от содержания процесса обработки.
	 */ 
	public abstract Object tcdata();

	/**
	 * Привязка данных к объекту (используется процессом чистки).
	 */ 
	public abstract void attachTcdata(Object tcdata);

	// creates string representation of this object  
	public abstract String toString();

}