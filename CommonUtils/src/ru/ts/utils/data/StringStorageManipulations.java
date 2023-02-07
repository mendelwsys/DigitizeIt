package ru.ts.utils.data;

/**
 * Объект для монипулирования данными в виде строк
 */
public class StringStorageManipulations
{
	public int curentlastpos;
	public String currentString;
	public boolean islock; //флаг блокировки поиска по следуюющему вводимому символу, устанавливается когда слово, после очередного введеного символа не содержится в словаре
	public int insertpos; //позиция в которую можно вставлять новую строку если словарь данных не содержит ее

	public void resetsearchstorage()
	{
		currentString = "";
		islock=false;
		curentlastpos = namesstorage.size()-1;
		insertpos =0;
	}

	public StringStorageManipulations()
	{
		this.namesstorage = new Storage<String>();
		resetsearchstorage();
	}

	public StringStorageManipulations(IReadStorage<String> namesstorage)
	{
		this.namesstorage = namesstorage;
		if (namesstorage.size()>0)
			namesstorage.setposon(0);
		resetsearchstorage();
	}

	public int findLastPosOfPrefix(String str, int curpos, int lastpos)
	{

		int newpos = lastpos;

		while (newpos > curpos)
		{
			lastpos = newpos;
			if (lastpos - curpos == 1)
			{
				if (namesstorage.get(lastpos).toUpperCase().startsWith(str.toUpperCase()))
					return lastpos;
				else if (namesstorage.get(curpos).toUpperCase().startsWith(str.toUpperCase()))
					return curpos;
				else
					return -1;
			}

			newpos = curpos + (lastpos - curpos) / 2;
			if (namesstorage.get(newpos).toUpperCase().startsWith(str))
				return findLastPosOfPrefix(str, newpos, lastpos);
		}
		return curpos;
	}

	public int findFirstOfLetter(char letter,int indexleter, int curpos, int lastpos)
	{
		if (namesstorage.size()<=0)
		{
			insertpos =0;
			return -1;
		}

		String curposstring = namesstorage.get(curpos).toUpperCase();
		if (curposstring.length()>indexleter && curposstring.charAt(indexleter)==letter)
		{
			insertpos =curpos;
			return curpos;
		}
		if (lastpos == curpos)
		{
			insertpos =curpos;
			return -1;
		}

		if (lastpos-curpos==1)
		{
			String lastposString = namesstorage.get(lastpos).toUpperCase();
			if (
					lastposString.length()>indexleter
					&& lastposString.charAt(indexleter) ==letter
			   )
			{
			   insertpos =lastpos;
			   return lastpos;
			}

			if (namesstorage.get(lastpos).length()>indexleter && lastposString.charAt(indexleter) <letter)
				insertpos =lastpos;//Если на lastpos находится название длиннее чем искомое и lastpos "легче чем" текущая буква, тогда вернем позицию для вставки
			else
				insertpos =curpos;

			return -1;
		}

		int newpos = curpos + (lastpos - curpos) / 2;
		String snewpos = namesstorage.get(newpos).toUpperCase();
		if (snewpos.length() >indexleter && letter<=snewpos.charAt(indexleter))
			return findFirstOfLetter(letter,indexleter, curpos,newpos);
		else
			return findFirstOfLetter(letter,indexleter, newpos,lastpos);
	}

	public int setAttrNameByLetter(char letter)
	{
		letter=Character.toUpperCase(letter);
		int firstpos=findFirstOfLetter(letter,currentString.length(),namesstorage.getCurpos(),curentlastpos);
		if (firstpos<0)
		{
			islock=true;
			return -1;
		}
		namesstorage.setposon(firstpos);
		currentString += letter;
		curentlastpos = findLastPosOfPrefix(currentString, namesstorage.getCurpos(), curentlastpos);
		return firstpos;
	}

	public int setAttrByString(String str)
	{
		curentlastpos = namesstorage.size()-1;
		if (namesstorage.size()>0)
			namesstorage.setposon(0);
		currentString = "";
		islock=false;
		int res=-1;
		for (int i = 0; i < str.length(); i++)
			res=setAttrNameByLetter(str.charAt(i));
		return res;
	}

	public IReadStorage<String> namesstorage;
}