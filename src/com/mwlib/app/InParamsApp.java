package com.mwlib.app;

import ru.ts.utils.data.InParams;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 02.10.2007
 * Time: 14:56:07
 *
 */
public class InParamsApp extends InParams

{
	// имена используемых параметров (префиксы в командной строке)
	public static final String optarr[] = {"-wfl","-cfg","-lng"};
	// значения параметров по умолчанию
	public static final String defarr[] =
	{
			"",
			"",
            "en"
	};

	public static final int O_wfl=0;//Входной файл проекта
    public static final int O_cfg=1;//Файл конфигурации
    public static final int O_lng=2;//Язык

	public InParamsApp()
	{
		super(optarr, defarr);
	}


}