package ru.ts.utils.data;

import java.util.Map;

/**
 * Changes: yugl, 28.04.2008,
 * функциональность - пара разнотипных полей.
 * Поскольку это очень похоже на Map.Entry, добавил наследование от него,
 * для определенности first - это ключ, а second - значение.
 */

public class Pair<F, S> implements Map.Entry<F,S>
{
	public F first;
	public S second;

	public Pair(F first, S second)
	{
		this.first = first;
		this.second = second;
	}

	public String toString()
	{
		return (first!=null?first.toString():"null")+"_"+(second!=null?second.toString():"null");
	}

    //++ Map.Entry
    public boolean equals(Object o)
	{
		if (o instanceof Pair)
		{
			Pair pr=(Pair)o;
			return (first!=null?first.equals(pr.first):first==pr.first) &&
					(second!=null?second.equals(pr.second):second==pr.second);
		}
		return false;
	}

	public int hashCode()
	{
		return toString().hashCode();
	}

    public F getKey()
    {
        return first;
    }
    public S getValue()
    {
        return second;
    }

    public S setValue(S value)
    {
        S s = second;
        second = value;
        return s;
    }
    //-- Map.Entry

}