/*
 * Created on 31.07.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.List;

import ru.ts.gisutils.geometry.Rect;


/**
 * @author yugl
 *
 * To control the TC process. 
 */
public interface ITcProcess {

	/**
	 * Начинает очередной шаг топологической чистки по всем данным. 
	 * @param taskType - задание (тип дефектов) 
	 * @param taskDelta - дельта, используется для определения "близости" объектов 
	 * @return - список найденных дефектов
	 */
	public List newIteration (int taskType, double taskDelta);
	/**
	 * Начинает очередной шаг топологической чистки в прямоугольнике. 
	 * @param taskType - задание (тип дефектов) 
	 * @param taskDelta - дельта, используется для определения "близости" объектов 
	 * @param taskRect - прямоугольник поиска 
	 * @return - список найденных дефектов
	 */
	public List newIteration (int taskType, double taskDelta, Rect taskRect);
	
	/**
	 * Завершает шаг топологической чистки. Все назначенные операции чистки исполняются. 
	 */
	public void endIteration ();
	
	/**
	 * Завершает шаг топологической чистки. Все назначенные операции чистки отменяются. 
	 */
	public void cancelIteration ();
	
	/**
	 * Предлагает автоматические способы исправления дефекта указанного типа. 
	 * Набор всегда не пуст, так как включает операцию Skip. 
	 * @param defectType - тип исправляемого дефекта
	 * @return - массив возможных операций
	 */
	public int[] getAutoCorrections (int defectType);
	
	/**
	 * Возвращает автоматически сформированный набор операций исправления дефекта (возможно, пустой). 
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления дефекта
	 */
	public List getAutoActions (TcLocInfo defect, int correction);
	
	/**
	 * Задает операцию топологической чистки для исправления дефекта.
	 * Один дефект может обрабатываться серией операций. 
	 * @param defect - исправляемый дефект
	 * @param action - операция исправления 
	 */
	public void processDefect (TcLocInfo defect, TcAction action);
	
	/**
	 * Задает операции топологической чистки для исправления дефекта.
	 * @param defect - исправляемый дефект
	 * @param actions - набор операций исправления 
	 */
	public void processDefect (TcLocInfo defect, List actions);
	
	/**
	 * Автоматически формирует и задает набор операций для исправления дефекта.
	 * @param defect - исправляемый дефект
	 * @param autoAction - способ исправления 
	 */
	public void autoprocessDefect (TcLocInfo defect, int correction);
		
	/**
	 * Автоматически формирует и задает набор операций для исправления дефектов.
	 * @param defects - список дефектов
	 * @param autoAction - способ исправления 
	 */
	public void autoprocessDefects (List defects, int correction);
	
	/**
	 * Автоматически формирует и задает набор операций для исправления дефектов.
	 * Способ исправления выбирается по умолчанию. Возможно, что ничего и не делается.
	 * @param defects - список дефектов
	 */
	public void autoprocessDefects (List defects);
	
	/**
	 * Сохраняет сделанные изменения в хранилище данных.
	 */
	public void saveResults ();
	
}
