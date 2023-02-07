package ru.ts.utils.gui.elems;

import ru.ts.utils.data.SimpleAutomaton;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 10.11.2006
 * Time: 17:40:02
 * Changes: yugl, 25.04.2008,
 * исходная функциональность - расширение SimpleAutomaton, задающее конкретную
 * таблицу состояний, сигналов и переходов, позволяющее приписать переходам
 * соответствующие исполняемые процедуры через интерфейс IActionPerform.
 * doing refactoring, added commentaries, changed dependencies
 */
public class PictureCtrl extends SimpleAutomaton
{
	public static final String  S_INIT = "INIT"; //- Начальное состояние
	public static final String  S_P1SET = "P1SET"; //- Задана 1 точка и происходит движение мыши
	public static final String  S_P2SET = "P2SET"; //- Задана 2 точка
	public static final String  S_ERROR = "ERROR";

//Действия
	public static final String T0 = "SET P1"; //Установка первой точки
	public static final String T2 = "MVMOUSE"; //Движение мыши
	public static final String T3 = "SET P2"; //Отпускание мыши
	public static final String T4 = "RESET ALL";

	static  final String[] states = {S_INIT,S_P1SET,S_P2SET,S_ERROR};
	static  final String[] actions = {T0,T2,T3,T4};
//Таблица переходов
	static  final String[][] transmitTable={
//          {S_INIT,S_P1SET,S_P2SET,S_ERROR};
			{S_P1SET,S_ERROR,S_ERROR,S_ERROR}, //T0
			{S_INIT,S_P1SET,S_ERROR,S_ERROR}, //T2
			{S_INIT,S_P2SET,S_ERROR,S_ERROR}, //T3
			{S_INIT,S_INIT,S_INIT,S_INIT}, //T4
	};

	private String currentState=S_INIT;
	private IActionPerform[] actionperform;

	public String getCurrentState()
	{
		return currentState;
	}


   	public void resetState()
	   {
		   currentState=S_INIT;
	   }

	public PictureCtrl(IActionPerform[] actionperform)
	{
		super (states,actions,transmitTable);
		this.actionperform = actionperform;
	}

	private IActionPerform getActionByName(String action)
	{
		for (int i = 0; i < actions.length; i++)
		{
			if (actions[i].equals(action))
			{
			    if (i<actionperform.length)
					return actionperform[i];
				else
					return null;
			}
		}
		return null;
	}

	public void performAction(String action, EventObject event)
	{
		if (!currentState.equals(S_ERROR))
		{
			IActionPerform relact=getActionByName(action);
			if (relact!=null)
				relact.actionPerformed(currentState,event);
		}
		currentState=this.getNextState(currentState,action);
	}


	public int getControlObjectID()
	{
		return controlObjectID;
	}

	public void setControlObjectID(int controlObjectID)
	{
		this.controlObjectID = controlObjectID;
	}

	private int controlObjectID;

}
