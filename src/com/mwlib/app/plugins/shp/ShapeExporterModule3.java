package com.mwlib.app.plugins.shp;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 20.04.14
 * Time: 19:22
 * Замена генератором который хранит геометрию в массиве точек
 */
public class ShapeExporterModule3
    extends ShapeExporterModule2
{

    protected PoTrace2Shp getConverter() {
        return new PoTrace2ShpIO();
    }

}
