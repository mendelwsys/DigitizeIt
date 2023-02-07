package shp.utils;

import shp.core.BoundingBox2D;
import shp.core.ShpPoint;
import shp.core.ShpUtil;

import java.io.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 20.04.14
 * Time: 17:49
 * Контайнер для добаления точек
 */
public class GeomContainer
{
    public int getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    private int recordNumber;
    public BoundingBox2D boundingBox2D = new BoundingBox2D();

//    private ByteArrayOutputStream geom =new ByteArrayOutputStream();
//    private DataOutputStream retVal1=new DataOutputStream(geom);

    private DataOutputStream retVal1;
    private String tmpFile1 =System.getProperty("user.dir")+ File.separator+"tmp1#$$$.tmp";

    private DataOutputStream retVal2;
    private String tmpFile2 =System.getProperty("user.dir")+ File.separator+"tmp2#$$$.tmp";


    public GeomContainer() throws Exception
    {
        retVal1 =new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile1)));
        retVal2 =new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile2)));
    }

    public void reset()
    {
        try {
            if (retVal1 !=null)
                retVal1.close();
        }
        catch (IOException e)
        {//
        }

        try
        {
            if (retVal2 !=null)
                retVal2.close();
        }
        catch (IOException e)
        {//
        }

    }


    public InputStream getGeom1() throws IOException
    {
        retVal1.flush();
        retVal1.close();

//        String s = maxsize / (1024 * 1024) + " M  ";
//        if (maxsize<1024*1024)
//            s = maxsize / (1024 ) + " K ";
//        if (size>1024*1024)
//            System.out.println("size= "+size/(1024*1024)+"M maxsize = " + s);
//        else {
//
//            System.out.println("size= " + size / 1024 + "K maxsize = " + s);
//        }
        return new BufferedInputStream(new FileInputStream(tmpFile1));
    }

    public InputStream getGeom2() throws IOException
    {
        retVal2.flush();
        retVal2.close();

//        String s = maxsize / (1024 * 1024) + " M  ";
//        if (maxsize<1024*1024)
//            s = maxsize / (1024 ) + " K ";
//        if (size>1024*1024)
//            System.out.println("size= "+size/(1024*1024)+"M maxsize = " + s);
//        else {
//
//            System.out.println("size= " + size / 1024 + "K maxsize = " + s);
//        }
        return new BufferedInputStream(new FileInputStream(tmpFile2));
    }


    public int getBSize()
    {
        return size;//geom.size();
    }

    public int numOfPoints;
    public int numOfParts;


    public int size=0;

    private int currentPartIndex = 0;

    public void add(Vector<ShpPoint> part) throws IOException
    {
            retVal2.writeInt(ShpUtil.convertToLittleEndian(currentPartIndex));
            currentPartIndex += part.size();
            size+=4;

            for(ShpPoint point: part)
            {
                boundingBox2D.update(point.x, point.y);
                retVal1.writeLong(ShpUtil.convertToLittleEndian(point.x));
                retVal1.writeLong(ShpUtil.convertToLittleEndian(point.y));

                size+=16;
            }

            numOfParts++;
            numOfPoints+=part.size();

            if (size>maxsize)
                maxsize=size;
    }

    static int maxsize=0;

}
