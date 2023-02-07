package ru.ts.utils.gui.elems;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 10.11.2006
 * Time: 17:38:50
 * Changes: yugl, 25.04.2008,
 * исходная функциональность - интерфейс для универсального анонимного класса
 * с реакцией на событие, описанное строкой состояния
 * doing refactoring, added commentaries, changed dependencies
 */
public interface IActionPerform
{
	void actionPerformed(String fromState, EventObject event);
}