/**
 * 
 */
package ru.ts.gisutils.common.records;

import java.util.Random;

import com.mwlib.utils.Enc;
import ru.ts.gisutils.common.logger.BaseLogger;
import ru.ts.gisutils.common.logger.ILogger;

/**
 * Тестовая программ на все случаи жизни с кратким комментарием, достаточным для
 * начала работы с Java кусщкв list (множеством экземпляров простого класса Java
 * в одним блоке памяти).
 * 
 * @author sygsky
 * 
 */
// @SuppressWarnings("unchecked")
public class TestRecordList
{

	/**
	 * Структура данных наподобие struct для C/C++ для использования c Java
	 * классами RLInt и RLByte. Так как все элементы публичные, нет строгой
	 * необходимости создавать методы для доступа к полям. Ну разве что
	 * конструктор для экономии кода при создании и заполнении отдельных
	 * экземпляров класса.
	 */
	public static class CPPLikeStruct
	{
		public int		id;

		public int		val;

		public boolean	loaded;

		public float	val_raw;

		public double	val_accurate;

		/**
		 * @param id
		 * @param val
		 * @param loaded
		 * @param val_raw
		 * @param val_accurate
		 */
		public CPPLikeStruct( int id, int val, boolean loaded, float val_raw,
		        double val_accurate )
		{
			this.id = id;
			this.val = val;
			this.loaded = loaded;
			this.val_raw = val_raw;
			this.val_accurate = val_accurate;
		}

		public CPPLikeStruct()
		{
			this.loaded = false;
		}
	}

	public static ILogger	log;
	static
	{
		log = new BaseLogger( System.out );
	}

	/**
	 * Пример с кратким описанием по использованию псевдо-структур в языке Java.
	 * 
	 * <pre>
	 * Идея заключается в слудующем:
	 * 		Раз уж в Java отсутствуют структуры данных по типу struct как в C/C++ 
	 * 		или record в Pascal, то почему бы не создать интерфейс, который сможет 
	 * 		хотя бы сохранить набор структур не в виде отдельных кусков памяти по 
	 * 		одному на структуру с последующей компоновкой указателей на них в	
	 * 		отдельном массиве, а просто хранить данные каждой отдельной структуры 
	 * 		в одном массиве данных (и одном цельном куске памяте) с  выдачей 
	 * 		необходимой структуры со всеми её элементами по запросу пользователя.
	 * 
	 * Для этого структура (класс) должна отвечать следующим требованиям: 
	 * 1. Она должна быть относительно простым классов с &lt;b&gt;ТОЛЬКО&lt;/b&gt; примитивными
	 * 	полями данных типа byte, char, short, int, float, long, double. Если 
	 * 	тип хотя бы одного из полей класса не является таковым, использовать 
	 * 	этот класс как Java struct - невозможно.
	 * 2. &lt;b&gt;Все&lt;/b&gt; поля должны быть объявлены как public.
	 * 
	 * В этом случае вы сможете организовать хранение набора элементов этого класса 
	 * в манере, свойственной для struct или record, т.е. в одном блоке памяти.
	 * 
	 * Сделать это возможно с помощью (пока) двух классов, а именно:
	 * 
	 * {@link ru.ts.gisutils.common.records.RLByte} и  
	 * {@link ru.ts.gisutils.common.records.RLInt}.
	 * 
	 * Оба они реализуют абстрактный класс 
	 * {@link ru.ts.gisutils.common.records.RecordListBase}, в свою очередь расширяющий  
	 * {@link java.util.AbstractList} и реализующий интерфейсы  
	 * {@link java.util.RandomAccess},
     * {@link java.util.Cloneable}, 
     * {@link java.util.java.io.Serializable}
	 *  
	 * 
	 *  Разница между ними только в том, что ваши структуры в случае использования
	 *  RLInt хранятся в массиве целых, а при использовании RLByte - в байтовом массиве.
	 *  Рекомендации для выбора просты - если большинство полей вашего класса, который
	 *  вы хотите хранить как struct, имеют физическую длину в памяти &gt;= 4 байт, 
	 *  используйте RLInt. Если &lt; 4, то RLByte. В остальном поведение этих классов 
	 *  абсолютно идентично. В примере используется класс RLInt ( в соответствии 
	 *  с рекомендациями). Оба эти класса унаследованы от базового класса 
	 *  {@link ru.ts.gisutils.common.records.RecordListBase}.
	 *  
	 *  Для удобства работы реализована функция быстрой сортировки и поиска по ключевым 
	 *  полям, когда вы не пишите свои компаратор (Comparator) для этого, а указываете 
	 *  ключ, состоящий из одного или нескольких полей любого типа вашего класса. 
	 *  Обычно этого достаточно. Сортировка полей осуществляется &lt;b&gt;ВСЕГДА&lt;/b&gt; по убыванию.  
	 *  Также вы можете использовать системный средства, но учтите, что они 
	 *  работают намного медленнее. А встроенные средства сортировки и поиска работают
	 *  почти также хорошо, как и системные. 
	 *  
	 * </pre>
	 * 
	 * @param args -
	 *            string arguments
	 */
	public static void main( String[] args )
	{
		/*
		 * Пример создания экземпляра класса для хранения структуры (записи)
		 * данных
		 */
		CPPLikeStruct rec = new CPPLikeStruct();

		/*
		 * Теперь создадим массив из нескольких экземпляров этого же класса. Для
		 * заполнения полей будем использовать случайные значения
		 */
		Random rnd = new Random();

		/*
		 * будем создавать 1 программистскую тысячу экземпляров для размещения в
		 * record list
		 */
		int num = 1024 /* * 1024 */;

		/*
		 * Создаём массивчик для хранения наших экземпляров структур.
		 * Присваиваем чтобы избежать предупреждения компилятора. Заметьте, что
		 * в качестве типа диста избирется базовый класс, чтобы можно было в
		 * дальнейшем выбрать байтовый (RLByte) или целочисленный (RLInt) тип
		 * хранилища. Что бы вы не избрали, тест пройдёт одинаково, но
		 * интегральная скорость работы с тем или иным видом записей может
		 * варьировать. А пока можно запомнить следующее: RLInt может занять
		 * больше места, а RLByte может медленнее работать на записях со
		 * смешанными типами полей.
		 */
		RecordListBase list = null;

		try
		{
			/* создаём хранилище на базе целого массива */
			list = new RLInt( CPPLikeStruct.class );
		}
		catch ( ClassNotFoundException cnfe )
		{
			System.err.println( Enc.get("$24")
			        + cnfe.getMessage() );
			return;
		}

		for ( int i = 0; i < num; i++ )
		{
			/* index будет в диапазон 1..1023 */
			rec.id = rnd.nextInt( num ) + 1;
			rec.val = rnd.nextInt( num );
			rec.loaded = true;
			rec.val_raw = (float) ( rec.val_accurate = ( rnd.nextDouble() * num ) );
			/*
			 * Отметьте, что мы можем использовать один экземпляр нашего класса
			 * CPPLikeStruct при добавления любого числа записей, так как наш
			 * лист поглощает только внутренние поля класса, сам экземпляр ему
			 * <b>НЕ</b> нужен. Это тоже экономит нам память и время
			 * исполнения, что есть то самое удобства, к которому мы и
			 * стремимся, не так ли?
			 */
			list.add( rec );
		}

		/*
		 * Теперь, после того как мы закончили наполнение и нам не нужно лишняя
		 * память, возможно выделенная в нашем классе для оптимизации
		 * множественных вставок, очистим её
		 */
		list.trimToSize();

		/*
		 * Теперь приготовимся отсортировать. Индекс будет с повторениями,
		 * скорее всего,но нашим целям демонстранции это не мешает :o)
		 * Устанавливаем индекс по имени индексного поля и полю значения.
		 */
		list.setIndexKey( "id,val" );

		/* сортируем по индексу */
		list.sort();
		/*
		 * Теперь напечаем повторяющиеся индексы Для этого возьмём 1й экземпляр
		 * как эталон и просканируем, меняя эталон, весь список. Печатать будем
		 * повторяющиея по первому полю, чтобы продемострировать сортировку и по
		 * второму полю тоже
		 */
		list.get( rec, 0 );
		int id = rec.id;
		/* счётчик повторений для значений индекса */
		int cnt = 0, wcnt = 0;
		for ( int i = 1; i < list.size(); i++ )
		{
			/*
			 * Опять используем только один экземпляр - это снова экономия
			 * памяти
			 */
			list.get( rec, i );
			if ( rec.id == id )
			{
				cnt++;
				wcnt++;
			}
			else
			{
				if ( cnt > 1 )
				{
					/* печатаем записи, равные по первому полю */
					for ( int j = cnt; j > 0; j-- )
					{
						list.get( rec, i - j );
						System.out.println( String.format(
						        "Pos %4d, Id %04d, Val %4d", new Object[] {
						                new Integer( i - j ),
						                new Integer( rec.id ),
						                new Integer( rec.val ) } ) );
					}
					/* обнуляем счётчик равных индексов */
					cnt = 0;
				}
				/* сменим эталон на новый */
				id = rec.id;
			}
		}
		/* Печатаем статистику */
		System.out.println( "Whole " + list.size()
		        + " item[s] in a list, and non unique are " + wcnt );

		/* Теперь, для разминки, удалим совпадающие индексы */
		list.get( rec, list.size() - 1 );
		id = rec.id;
		/* удаляем с конца массива - так быстрее o:) */
		for ( int i = list.size() - 2; i >= 0; i-- )
		{
			list.get( rec, i );
			if ( rec.id == id )
			{
				list.remove( i );
				/* продолжаем поиск с тем же эталоном */
				continue;
			}
			/* обновляем эталон */
			id = rec.id;
		}
		System.out.println( "After removing non-unique whole " + list.size()
		        + " item[s] in a list remained" );

		/* Проверим теперь есть ли структуры с неуникальными индексами */
		list.get( rec, 0 );
		id = rec.id;
		wcnt = 0;
		for ( int i = 1; i < list.size(); i++ )
		{
			list.get( rec, i );
			if ( rec.id == id )
			{
				wcnt++;
				/* продолжаем поиск с тем же эталоном */
				continue;
			}
			/* обновляем эталон */
			id = rec.id;
		}
		/* Напечатаем опять - теперь уже повторяющихся индесков быть не должно */
		System.out.println( "Non-unique indexes counter " + wcnt
		        + ". Must be 0" );

		/*
		 * ну, как изменить отдельное поле в записи, вы уже догадались - нужно
		 * просто считать запись, изменить нужное поел и снова записать её
		 * обратно, например так:
		 */

		int index = rnd.nextInt( list.size() );
		list.get( rec, index );
		rec.val += 10;
		rec.val_raw = (float) ( rec.val_accurate -= 128.0 );
		list.set( index, rec );

		/*
		 * А для особой продвинутости в использования этой технологии
		 * предусмотрена возможность исзвлечения ОТДЕЛЬНЫХ полей из любой
		 * записи, т.е. даже нет нужды извлекать её всю! Например:
		 */

		/*
		 * получим индекс поля в записи. Обязательно получайте его так, а не
		 * считайте вручную, на какой позиции находится поле в программном коде
		 * класса
		 */
		int flt_ind = list.getFieldIndex( "val_raw" );
		index = rnd.nextInt( list.size() );
		float f_val = list.getFieldFloat( index, flt_ind );

		/* Теперь проверим это через чтение объекта */
		System.out
		        .println( "Checks direct reading of the fields form a Record List at index "
		                + index );
		list.get( rec, index );

		/*
		 * Напоследок перечислим все поля записи, которая является единицей
		 * хранения в нашем листе, показав, как можно найти номер поля, его имя
		 * и его тип, не зная предварительно, что за класс является базовым для
		 * нашего листа.
		 */
		for ( int i = 0; i < list.getFieldCount(); i++ )
		{
			String name = list.getFieldName( i );
			Class cls = list.getFieldType( i );
			System.out.println( "Field \"" + name + "\", index " + i + ", type"
			        + cls.getSimpleName() );
		}

		System.out.println( "list.getFieldFloat(" + index + ", " + flt_ind
		        + ")[" + f_val + "] " + ( rec.val_raw == f_val ? "==" : "<>" )
		        + " rec.val_raw [" + rec.val_raw + "]" );

		/*
		 * Вот и всё, ребята!!! Есть ещё много возможностей, не приведённых в
		 * примере, но главное вам должно уже быть ясно :o)
		 */
		list.clear(); /* j */
	}
}
