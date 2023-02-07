package ru.ts.toykernel.filters;

/**
 * Специализированный фильтр по формуле, формула обычно задается
 * над параметрами объекта
 */
public interface IFormulaFilter  extends IBaseFilter
{

	/**
	 * @return Получить формулу
	 */
	String getFormula();

	/**
	 * задать формулу
	 * @param formula задаем формулу
	 */
	void setFormula(String formula);

}
