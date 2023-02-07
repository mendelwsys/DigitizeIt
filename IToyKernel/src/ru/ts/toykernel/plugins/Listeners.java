package ru.ts.toykernel.plugins;

import ru.ts.gisutils.algs.common.MPoint;

import java.util.Map;
import java.util.Collection;
import java.awt.Point;

/**
 * Интерфейсы для слушателей модулей
 */
public class Listeners
{
	//Модификатор нажатия кнопки
	public  static class Modifiers
	{
		public boolean metaKey=false;
		public boolean ctrlKey=false;
		public boolean shiftKey=false;
		public boolean altKey=false;
	}

	public static class KeybEvent
	{
		public Modifiers getModifier()
		{
			return modifier;
		}

		public String getKeyb()
		{
			return keyb;
		}

		public Modifiers modifier;
		public String keyb;

		public KeybEvent(String keyb,Modifiers modifier)
		{
			this.modifier = modifier;
			this.keyb = keyb;
		}
	}

	public static class MEvent
	{
		public MPoint getPnt()
		{
			return pnt;
		}

		public int getMbutton()
		{
			return mbutton;
		}

		public Modifiers getModifier()
		{
			return modifier;
		}

		public Object getServsrc()
		{
			return servsrc;
		}

		private Object servsrc;
		public MPoint pnt;
		public int mbutton=0;//Номер конпки мыши
		public Modifiers modifier;

		public MEvent(MPoint pnt)
		{
			this.pnt = pnt;
		}

		public MEvent(Object servsrc,MPoint pnt, Modifiers modifier, int mbutton)
		{
			this.servsrc = servsrc;
			this.pnt = pnt;
			this.modifier = modifier;
			this.mbutton = mbutton;
		}
	}

	//Описатель режима
	public interface IIconDesc
	{
		/**
		 * @return номер режима  в модуле
		 */
		int getnmode();

		/**
		 * @return описание режима
		 */
		String getHint();

		/**
		 * @return ссылка на картинку которая показываеи
		 * ВНИМАНИЕ картинка должна быть в обрамлена ковычками ''
		 * сделано для того что бы вставлять было можно код на js
		 */
		String getIcon();

		/**
		 * @return точка позиционирования картинки
		 */
		Point getPoint();

		/**
		 * @return посылать ли по клику команду нажатия
		 */
		boolean isClickSend();

		String getPrefixCode(String arrcodename);

		String getCodeMUp(String arrcodename);

		String getCodeMMove(String arrcodename);

		String getCodeMDown(String arrcodename);

		String getCodeMPictClk(String arrcodename);

		String getCodeSetoffmode(String arrcodename);

		/**
		 * @return заминить собой обработку событий клавиатуры и мыши по умолчанию (Движение карты увеличение и уменьшение масштаба)
		 */
		boolean isReplaceDefault();

		Collection<IIconListener> getIIconListeners();

	}

	/**
	 * Слушатель модуля
	 */
	public interface IModuleListener
	{

		String getModuleTimerCode();
		/**
		 * @return отдать номер модуля
		 */
		int getnmodule();

		/**
		 * @return Описатель кнопок переключения режимов модуля
		 */
		IIconDesc[] getIconDesc();

		Map<Integer, Collection<IIconListener>> getIconListeners();

		Map<Integer, Collection<ICmdListener>> getCmdListeners();


		/**
		 * Отдать отображение номер режима модуля -> слушатель мыши
		 * @return номер режима модуля -> слушатель мыши
		 */
		Map<Integer, Collection<IMUpListener>> getMListener();

		String getListnerInitCode();
	}
/*
	TODO Мы можем и будем отправлять тем же рейсом команды для исполнения
	TODO в терминах компоненты в которой зарегистрирован
*/
//В координатах карты!!! (для того что бы не тащить сюда доп. интерфейсы)
	public interface IMUpListener
	{
//Возникает при отжатии кнопки
		IAnswerBean mup(MEvent ev);
	}

	public interface IDownListener extends IMUpListener
	{
		/**
		 *
		 * @param ev -
		 * @return содержит код на скрипте который должен быть исполнен на клиентской стороне.
		 */
		IAnswerBean mdown(MEvent ev);
	}



	public interface IIconListener
	{
		IAnswerBean clickIconListener(MEvent ev);
	}

	//Слушатель команд модуля (Это собственно карточный плагин)
	public interface ICmdListener
	{
		/**
		 * Исполенение команды модуля
		 * @param cmd - собственно команда
		 * @return - ответ модуля
		 * @throws Exception -
		 */
		IAnswerBean execute(ICommandBean cmd) throws Exception;
	}

//TODO Возможная перспектива если понадобится
//	public interface IKeyListener
//	{
//		void kup(KeybEvent ev);
//
//		void kdown(MEvent ev);
//
//		void kpress(MEvent ev);
//	}

}
