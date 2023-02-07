package ru.ts.toykernel.converters;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 16.11.2007
 * Time: 17:53:00
 * Интерфейс преобразователя координат
 * Терминология:
 *  Point - точка координатной системы, в которой задавются объекты (в БД например)
 *  GeoPoint - точка в географической системе координат
 *  LinearPoint - точка в плоской системе координат, в которую отображается Point после проецирования.
 *  DrawPoint - точка в системе координат экрана
 *  currentP0 - точка привязки экрана, которая соответствует определенной
 *  точке экрана (обычно верхней-левой)

 * Необходимость абстрагирования координатной системы, в которой задается изначально объекты, связана с необходимостью
 * отображать данные в разных системах отсчета
 */
public interface IProjConverter extends ILinearConverter
{
	void setByConverter(IProjConverter converter) throws Exception;
	/**
	 * @return венуть конвертер вращения
	 */
	IRotateConverter getAsRotateConverter();

	/**
	 * @return вернуть ковертер масштабирования
	 */
	IScaledConverter getAsScaledConverter();

	/**
	 * @return вернуть ковертер сдвига
	 */
	IShiftConverter getAsShiftConverter();


	/**
	 * @return вернуть контроллер конвертора масштабирования
	 */
	IScaledConverterCtrl getAsScaledConverterCtrl();

}
