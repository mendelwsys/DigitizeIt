package ru.ts.common.arrays;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 22.01.14
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class DirectPointArray extends ArrayList<Point2D.Double>
{

    public  DirectPointArray(int initialCapacity) {
        super(initialCapacity);
    }

    public  DirectPointArray() {
    }

    public double getX(int ix)
    {
        return this.get(ix).getX();
    }

    public double getY(int ix)
    {
        return this.get(ix).getY();
    }

    public void append(double x, double y)
    {
        this.add(new Point2D.Double(x,y));
    }

}
