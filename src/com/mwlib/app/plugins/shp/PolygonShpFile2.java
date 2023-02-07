package com.mwlib.app.plugins.shp;

import shp.core.*;
import shp.exception.ShpDbfException;
import shp.utils.GeomContainer;
import su.gis.utils.shp.ShpPgon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 20.04.14
 * Time: 18:10
 *
 */
public class PolygonShpFile2
{


    public static void add2Vector(GeomContainer container, Shape shape,AffineTransform af,double flatness)
    {
        PathIterator pathItr = shape.getPathIterator(af, flatness);
        double[] coords = new double[6];
        ShpPgon pgon =null;

        try {
            while (!pathItr.isDone())
            {
                int segType = pathItr.currentSegment(coords);
                pgon=processPathPart(segType, coords,pgon,container);
                pathItr.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static long addPolygon2(
            ShpFileBase shpBase,
            long fileLength,
            GeomContainer container,
            Map<String, Integer> numberValues,
            Map<String, Double> doubleValues,
            Map<String, String> stringValues

    ) throws IOException, ShpDbfException
    {
        DataOutputStream dos = new DataOutputStream(shpBase.getMainOutputStream());
        long ln=generateMainFileRecordAsByteArray2(container, dos);

        byte[] indexFileRecord = generateIndexFileRecordAsByteArray(container, (int)fileLength / 2);

        shpBase.getIndexOutputStream().write(indexFileRecord);
        shpBase.getDbfFile().addValues(numberValues,doubleValues,stringValues);

         shpBase.getMainBoundingBox2D().update(
                 container.boundingBox2D.getXMin(),
                 container.boundingBox2D.getYMin()
         );

        shpBase.getMainBoundingBox2D().update(
                container.boundingBox2D.getXMin(),
                container.boundingBox2D.getYMax()
        );

        shpBase.getMainBoundingBox2D().update(
                container.boundingBox2D.getXMax(),
                container.boundingBox2D.getYMin()
        );

        shpBase.getMainBoundingBox2D().update(
                container.boundingBox2D.getXMax(),
                container.boundingBox2D.getYMax()
        );


        fileLength += ln;

        /* ---------------- logging ---------------- */
        if(ShpCoreLogger.isLoggingEnabled() && ShpCoreLogger.getLogger() != null)
        {
            String logMessage = "record number = " + (container.getRecordNumber() + "\n" +
                                "fileLength    = " + fileLength                 + "\n" +
                                "index record hex dump\n"                       +
                                ShpCoreLogger.toHexString(indexFileRecord)      + "\n" +
                                "main record hex dump (array length = " + ln + ")\n");
//                    +
//                                ShpCoreLogger.toHexString(mainFileRecord);

            ShpCoreLogger.getLogger().log(Level.INFO, logMessage);
        }
        /* -------------- logging end -------------- */
        return fileLength;
    }

    static byte[] bt=new byte[10*1024*1024];

    public static long generateMainFileRecordAsByteArray2
    (
            GeomContainer container,DataOutputStream retVal

    ) throws IOException
    {

        BoundingBox2D localBoundingBox2D = container.boundingBox2D;
        long bufferLength                 = ShpFileBase.RECORD_HEADER_LENGTH   +
                                           PolygonShpFile.MAIN_RECORD_INITIAL_CONTENT_LENGTH +
                                           container.getBSize();

        /*-- Adding record header ---------------- */
        retVal.writeInt(container.getRecordNumber());
        retVal.writeInt(((int)bufferLength - ShpFileBase.RECORD_HEADER_LENGTH) / 2);

        /*-- Adding content ---------------------- */
        /* Write shape type */
        retVal.writeInt(ShpUtil.convertToLittleEndian(ShpFileHeader.SHPTYPE_POLYGON));

        /* Write initial local bounding box. */
        retVal.writeLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMin()));
        retVal.writeLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMin()));
        retVal.writeLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getXMax()));
        retVal.writeLong(ShpUtil.convertToLittleEndian(localBoundingBox2D.getYMax()));

        /* Write number of parts. */
        retVal.writeInt(ShpUtil.convertToLittleEndian(container.numOfParts));

        /* Write number of points. */
        retVal.writeInt(ShpUtil.convertToLittleEndian(container.numOfPoints));

        InputStream geom2 = container.getGeom2();
        copyStream(retVal, geom2);

        InputStream geom1 = container.getGeom1();
        copyStream(retVal, geom1);


        return bufferLength;
    }

    private static void copyStream(DataOutputStream retVal, InputStream geom) throws IOException {
        try {
            int ln;
            while ((ln=geom.read(bt))>0)
                retVal.write(bt,0,ln);
        }
        finally
        {
            if (geom!=null)
                geom.close();
        }
    }

    /**
     * Generates an index file record as a byte array based on the given polygon, and offset.
     *
     * @param container    The polygon.
     * @param offset     The offset.
     * */
    public static byte[] generateIndexFileRecordAsByteArray(GeomContainer container, int offset)
    {
        ByteBuffer retVal = ByteBuffer.allocate(ShpFileBase.INDEX_RECORD_LENGTH);

        retVal.putInt(offset);
        retVal.putInt(container.getBSize());

        return retVal.array();
    }

    static boolean verbose = false;

    protected static ShpPgon processPathPart(int segType, double[] coords,ShpPgon pgon,GeomContainer container) throws IOException
    {


      switch ( segType )
      {
        case PathIterator.SEG_MOVETO:
          // check if polygon already exists and not empty, so we should first dump its content
          if ( (pgon != null) && (!pgon.isEmpty()) )
          {
            // it is not first polygon in the shape
            pgon.close();
            container.add(pgon);
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
          container.add(pgon);
          if (verbose)
          {
            log("* CLOSE");
          }
          break;
      }
        return pgon;
    }


    static private void log( String str )
    {
        System.out.println(str);
    }

    static private String c(double d)
    {
        return String.format("%.2f", d);
    }


}
