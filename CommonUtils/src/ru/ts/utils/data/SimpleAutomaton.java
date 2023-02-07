package ru.ts.utils.data;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 17.10.2006
 * Time: 21:42:15
 * Changes: yugl, 25.04.2008,
 * исходная функциональность - конечный автомат,
 * состояния и сигналы описываются массивами строк, переходы - матрицей строк.
 * doing refactoring, added commentaries, changed dependencies
 */
public class SimpleAutomaton
{

	private Hashtable wkTable = new Hashtable();

	public SimpleAutomaton(String[] states,String[] action,String[][] transmitTable)
	{
		for (int i = 0; i < action.length; i++)
		{
			Hashtable aC = new Hashtable();
			for (int j = 0; j < states.length; j++)
				if (states[j] != null && transmitTable[i][j]!=null)
					aC.put(states[j],transmitTable[i][j]);
			wkTable.put(action[i],aC);
		}
	}


	public String getNextState(String initState,String action)
	{
	 	Hashtable statesl = (Hashtable) wkTable.get(action);
		if (statesl!=null)
			return (String) statesl.get(initState);
		return null;
	}
}