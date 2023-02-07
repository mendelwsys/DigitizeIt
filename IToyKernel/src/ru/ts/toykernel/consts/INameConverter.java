package ru.ts.toykernel.consts;

import ru.ts.factory.IInitAble;

/**
 *Interface for convert names from/to code names to/from storage names to/from gui names
 *Интерфейс вводит косвеность между именами атрибутов объектов прописанными в хранилище и
 *в коде,а так же по имени аттрибута позволяет получить его имя для визуализации
 */
public interface INameConverter extends IInitAble
{
	/**
	 * add name converter for cascade name converters
	 * @param nmconverter - cascade name converter, current converter try to convert name, if convertion is fail
	 * it calls cascade converter
	 */
	void addNameConverter(INameConverter nmconverter);

	/**
	 * convert code attribute name to gui view attribute name
	 * @param attrName - attribute name for convertion
	 * @return - gui view attribute name
	 */
	String codeAttrNm2ViewNm(String attrName);
	/**
	 * convert code attribute name to storage attribute name
	 * @param attrName - attribute name for convertion
	 * @return - storage attribute name
	 */
	String codeAttrNm2StorAttrNm(String attrName);
	/**
	 * convert storage attribute name to code attribute name
	 * @param attrName - attribute name for convertion
	 * @return - code attribute name
	 */
	String storAttrNm2codeAttrNm(String attrName);
}
