package ru.ts.utils.data;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 02.10.2007
 * Time: 14:56:07
 * Changes: yugl, 24.04.2008,
 * Класс был переработан и перенесен в библиотеку для общего пользования.
 * Функциональность - хранение параметров командной строки (или чего-либо аналогичного),
 * можно использовать непосредственно или как базовый класс для конкретных приложений.
 * Значения имен параметров и значений по умолчанию удалены сознательно,
 * они будут задаваться при создании конкретного экземпляра класса в приложении.
 */
public class InParams
{
	static protected String[] mergeArrays(String[] array,String [] addarry)
	{
		Collection<String> ll = new LinkedList<String>(Arrays.asList(array));
		ll.addAll(Arrays.asList(addarry));
		return ll.toArray(new String[ll.size()]);
	}

	public String[] getArgs()
	{
		return args;
	}

	protected String[] args;
	// имена параметров (префиксы в командной строке)
	protected String _optarr[];
    // значения параметров по умолчанию
	protected String _defarr[];
	//Значения коментариев
	protected String _comments[];
	// параметры командной строки
    public Map<String,Object> options = new HashMap<String,Object>();


	public String getParamNameByIx(int ix)
	{
		return _optarr[ix];
	}

	/**
	 * Добавить параметры в массив параметров
	 * @param params - мно-во добавляемых параметров
	 */
	public void addParams(Collection<Pair<String,Object>> params)
	{
		for (Pair<String,Object> param : params)
			options.put(param.first,param.second);
	}

	public void addParam(Pair<String,Object> param)
	{
			options.put(param.first,param.second);
	}

	public InParams(String[] optarr, String[] defarr)
	{
		setArrays(optarr, defarr,new String[]{});
	}

	public void setArrays(String[] optarr, String[] defarr,String [] comments)
	{
		_optarr = new String[optarr.length];
		System.arraycopy(optarr,0,_optarr,0,optarr.length);

		_defarr=new String[_optarr.length];
		for (int i = 0; i < _defarr.length; i++)
			_defarr[i]="";
		System.arraycopy(defarr,0,_defarr,0,defarr.length);

		_comments=new String[_optarr.length];
		for (int i = 0; i < _comments.length; i++)
			_comments[i]="";
		System.arraycopy(comments,0,_comments,0,comments.length);

		options = new HashMap<String,Object>();
		setDefaults();
	}

	public InParams(String[] optarr, String[] defarr,String[] comments)
	{
		setArrays(optarr,defarr,comments);
	}


    // устанавливает значения options по умолчанию
    public void setDefaults() {
        options.clear();
        for (int i = 0; i < _optarr.length; i++)
            options.put(_optarr[i], _defarr[i]);
    }

    // устанавливает значения options по массиву параметров командной строки
    public void translateOptions(String args[])
	{
		this.args=args;
		if (args == null)
			return;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i] != null)
			{
				for (int j = 0; j < _optarr.length; j++)
					if (args[i].startsWith(_optarr[j]))
					{
						String opt = args[i].substring(_optarr[j].length());
						options.put(_optarr[j], opt);
						break;
					}
			}
		}
	}

    // формирует строку параметра с заданным индексом по заданному значению
    public String buildArg(int optIdx, String optVal)
	{
        try {
            String optName = _optarr[optIdx];
            return optName + optVal;
        }
        catch(Exception ex) {
            return "";
        }
	}
    // восстанавливает строку параметра с заданным индексом по текущему значению
    public String rebuildArg(int optIdx)
	{
        try {
            String optName = _optarr[optIdx];            
            Object optVal = getObject(optName);
            return optName + optVal;
        }
        catch(Exception ex) {
            return "";
        }
	}

    // возвращает значение options по имени параметра
    public Object getObject(String optName)
	{
        return options.get(optName);
    }

	public String get(String optName)
	{
		return (String) getObject(optName);
	}

	public String getComments(int optIdx)
	{
		try
		{
			return _comments[optIdx];
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{//
		}
		return null;
	}


    // возвращает значение options по индексу параметра в массиве _optarr
    public Object getObject(int optIdx)
	{
        try
		{
            String optName = _optarr[optIdx];
            return getObject(optName);
        }
        catch(Exception ex)
		{
            return null;
        }
    }

	public String get(int optIdx)
	{
		return (String) getObject(optIdx);
	}

}