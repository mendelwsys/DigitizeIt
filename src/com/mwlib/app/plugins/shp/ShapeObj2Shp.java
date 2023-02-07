package com.mwlib.app.plugins.shp;

import shp.core.PolygonShpFile;
import shp.core.ShpPoint;
import su.gis.utils.shp.ShpPgon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.01.14
 * Time: 20:02
 * Converter ShapeObjects to Shape
 */
public class ShapeObj2Shp
{
    public static final String NAME_ID = "Id";
    public static final String NAME_POLY_CNT = "Poly_cnt";
    public static final String NAME_POINT_CNT = "Point_cnt";
    public static final String DEF_NAME_COLOR = "COLOR";


    public final static String POLY_ONLY="POLY_ONLY";
    public final static String POLY_WITH_HOLES="POLY_WITH_HOLES";
    public final static String WHOLE_OBJECT="WHOLE_OBJECT";
    public final static String DEF_SHAPEOBJ=POLY_WITH_HOLES;
    public static final double DEF_PIXEL_FLATNESS = 0.00005;
    public static final double DEF_M_FLATNESS = 5;
    public static final double DEF_RESOLUTION = 30;


    public String getAsShapeObject() {
        return asShapeObject;
    }

    public void setAsShapeObject(String asShapeObject)
    {
        if (    POLY_WITH_HOLES.equalsIgnoreCase(asShapeObject)
                ||
                WHOLE_OBJECT.equalsIgnoreCase(asShapeObject)
                ||
                POLY_ONLY.equalsIgnoreCase(asShapeObject)
                )
            this.asShapeObject = asShapeObject;
        else
            this.asShapeObject = DEF_SHAPEOBJ;
    }

    protected String asShapeObject=DEF_SHAPEOBJ;
    protected double flatness = DEF_M_FLATNESS;
    protected boolean verbose =false;


    public AffineTransform getAf() {
        return af;
    }

    public void setAf(AffineTransform af) {
        this.af = af;
    }

    private AffineTransform af;

    public double getFlatness() {
        return flatness;
    }

    public String[] getNumNames()
    {
        return new String[]{ NAME_ID, NAME_POLY_CNT, NAME_POINT_CNT, DEF_NAME_COLOR};
    }

    public String[] getDoubleNames()
    {
        return new String[]{};
    }

    public String[] getTxtNames()
    {
        return new String[]{};
    }


    public void setFlatness(double flatness) {
        this.flatness = flatness;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

//    public void createShapeFile(String shpPath, Iterator<ShapeObject> shpObjs) throws Exception
//    {
//        PolygonShpFile shpFile=ShpFactory.getPolygonMaker(shpPath, getNumNames(), getTxtNames());
//        fillShapeFile(shpFile, shpObjs);
//    }

    public void fillShapeFile(PolygonShpFile shpFile, Iterator<ShapeObject> shpObjs,boolean isCreate) throws Exception
    {
        if (isCreate)
            shpFile.create();
        Vector<Vector<ShpPoint>> pgons = new Vector<Vector<ShpPoint>>();
        Map<String, Integer> numVals = new HashMap<String, Integer>();
        int id = 0;

        AffineTransform af = getAf();
        if (af==null)
        {
            af = AffineTransform.getTranslateInstance(0, 0);
            af.setToScale(1,-1);
        }


        while (shpObjs.hasNext())
        {
            int pntCnt = 0;

            ShapeObject shapeObject = shpObjs.next();
            Shape shape = shapeObject.getShape();
            PathIterator pathItr = shape.getPathIterator(af, flatness);

            double[] coords = new double[6];
            ShpPgon pgon =null;
            while (!pathItr.isDone())
            {
                int segType = pathItr.currentSegment(coords);
                pgon=processPathPart(segType, coords,pgon,pgons);
                pathItr.next();
            }

            for (Vector<ShpPoint> pgon1 : pgons)
                pntCnt += pgon1.size();

            if ( pntCnt <= 2 )
              continue;

            numVals.clear();

            numVals.putAll(shapeObject.getNm2int());

            numVals.put(NAME_ID, id++);
            numVals.put(NAME_POLY_CNT, pgons.size());
            numVals.put(NAME_POINT_CNT, pntCnt);

            shpFile.addPolygon2(pgons, numVals, shapeObject.getNm2dbl(),shapeObject.getNm2string());

            pgons.clear();
        }
    }

    private void log( String str )
    {
        System.out.println(str);
    }

    private String c(double d)
    {
        return String.format("%.2f", d);
    }

    protected ShpPgon processPathPart(int segType, double[] coords,ShpPgon pgon,Vector<Vector<ShpPoint>> pgons)
    {
      switch ( segType )
      {
        case PathIterator.SEG_MOVETO:
          // check if polygon already exists and not empty, so we should first dump its content
          if ( (pgon != null) && (!pgon.isEmpty()) )
          {
            // it is not first polygon in the shape
            pgon.close();
            pgons.add(pgon);
          }
          pgon = new ShpPgon();
          pgon.add(new ShpPoint(coords[0], coords[1]));
          if (verbose)
          {
            log("* MOVETO: " + c(coords[ 0 ]) + ", " + c(coords[ 1 ]) + ";");
          }
          break;
        case PathIterator.SEG_LINETO:
          pgon.add(new ShpPoint(coords[ 0 ], coords[ 1 ]));
          if (verbose)
          {
            log("* LINETO: " + c(coords[ 0 ]) + ", " + c(coords[ 1 ]) + ";");
          }
          break;
        case PathIterator.SEG_QUADTO:
          if (verbose)
          {
            log("- QUADTO: " + c(coords[ 0 ]) + ", " + c(coords[ 1 ]) + ", " + c(coords[ 2 ]) + ", " + c(coords[ 3 ]) + ";");
          }
          break;
        case PathIterator.SEG_CUBICTO:
          if (verbose)
          {
            log("- CUBICTO: " + c(coords[ 0 ]) + ", " + c(coords[ 1 ]) + ", " + c(coords[ 2 ]) + ", " + c(coords[ 3 ]) + ", " + c(coords[ 4 ]) + ", " + c(coords[ 5 ]) + ";");
          }
          break;
        case PathIterator.SEG_CLOSE:
          // complete polygon and dump it
          pgon.close();
          pgons.add(pgon);
          if (verbose)
          {
            log("* CLOSE");
          }
          break;
      }
        return pgon;
    }
}
