package com.mwlib.app.plugins.shp;

import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.01.14
 * Time: 20:16
 * ShapeObject contains java awt shape and its simple attributes
 */
public class ShapeObject
{
    private Shape shape;//java awt shape object

    private Map<String,Integer> nm2int; //Integer attributes

    public Map<String, Double> getNm2dbl() {
        return nm2dbl;
    }

    public void setNm2dbl(Map<String, Double> nm2dbl) {
        this.nm2dbl = nm2dbl;
    }

    private Map<String,Double> nm2dbl; //Integer attributes
    private Map<String, String> nm2string;//String attributes

    public ShapeObject(Shape shape, Map<String, Integer> nm2int,Map<String,Double> nm2dbl,Map<String, String> nm2string) {
        this.shape = shape;
        this.nm2int = nm2int;
        this.nm2dbl = nm2dbl;
        this.nm2string=nm2string;
    }


    public ShapeObject(Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Map<String, Integer> getNm2int() {
        return nm2int;
    }

    public void setNm2int(Map<String, Integer> nm2int) {
        this.nm2int = nm2int;
    }

    public Map<String, String> getNm2string() {
        return nm2string;
    }

    public void setNm2string(Map<String, String> nm2string) {
        this.nm2string = nm2string;
    }
}
