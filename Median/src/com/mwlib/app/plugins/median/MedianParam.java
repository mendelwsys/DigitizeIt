package com.mwlib.app.plugins.median;

import reclass.bitmatrix.BitMatrix;
import ru.ts.utils.data.Pair;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.04.14
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class MedianParam implements Cloneable
{
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public int borderType = BitMatrix.BORDER_ZERO;
    public int rColor=0xFFFF0000;//Какой цвет считать единицой
    public int mSize=3; //Размер окна медианного фильтра
    public double colorDist = 25.0d;//Цветовое расстояние


    static public Pair<String, Integer>[] getExpandPolicyNames()
    {
        Pair[] pairs = {
                new Pair<String, Integer>("", BitMatrix.BORDER_ZERO),
                new Pair<String, Integer>("", BitMatrix.BORDER_COPY),
                new Pair<String, Integer>("", BitMatrix.BORDER_WRAP),
                new Pair<String, Integer>("", BitMatrix.BORDER_REFLECT)
        };
        for (Pair<String, Integer> pair : pairs)
            pair.first=BitMatrix.borderType2String(pair.second);
        return pairs;
    }

}
