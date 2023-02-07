package ru.ts.toykernel.plugins;

/**
 * Интерфейс управления графическим интерфейсом
 * веб клиента.
 */
public interface IGraphCtrl
{
	void multScale(double mult);

	void setSizeByCode( String widthCode,String heightCode);

	void setChangeIconCmd(int nmodule,int nmode,String pictref);

	void setChangeParamCmd(int nmodule,int nmode,String[] props,String value);

	String getHeightCode();

	String getWidthCode();

	int getHeightPX();

	int getWidthPX();
}
