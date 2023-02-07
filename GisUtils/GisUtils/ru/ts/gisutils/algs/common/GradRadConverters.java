package ru.ts.gisutils.algs.common;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 28.06.2011
 * Time: 13:28:06
 * Небольшой набор конвертеров для преобразований радианов в градусы и обратно
 */
public class GradRadConverters
{
	/**
	 * преобразование градусы минуты секуны в градусы.десградусы
	 *
	 * @param grminsec - набор градусы, минуты, секунды
	 * @return градусы.десградусы
	 */
	public static double gMS2GWD(int[] grminsec)
	{
		return grminsec[0] + grminsec[1] / 60.0 + grminsec[2] / 3600.0;
	}

	/**
	 * преобразование градусы.десградусы в градусы минуты секуны
	 *
	 * @param decgrad -градусы.десградусы
	 * @return набор градусы, минуты, секунды
	 */
	public static int[] gWD2GMS(double decgrad)
	{
		double sec = ((decgrad - (int) decgrad) * 3600);

		int min = (int) (sec / 60.0);

		sec = sec - min * 60;

		return new int[]{(int) decgrad, min, (int) sec};
	}

	/**
	 * @param decgrad -градусы.десградусы
	 * @return - радианы
	 */
	public static double gWD2Rad(double decgrad)
	{
		return Math.toRadians(decgrad);
	}

	/**
	 * @param rad - радианы
	 * @return -градусы.десградусы
	 */
	public static double rad2GWD(double rad)
	{
		return Math.toDegrees(rad);
	}


	public static void main(String[] args)
	{
		{
			int[] rv = gWD2GMS(37.75); //37°45′0″
			for (int i : rv)
				System.out.println("i = " + i);


			double decgrad = gMS2GWD(rv);
			System.out.println("decgrad = " + decgrad);
		}
		
		{
			int[] rv = gWD2GMS(55.67); //55°40′12″
			for (int i : rv)
				System.out.println("i = " + i);


			double decgrad = gMS2GWD(rv);
			System.out.println("decgrad = " + decgrad);
		}
	}
}
