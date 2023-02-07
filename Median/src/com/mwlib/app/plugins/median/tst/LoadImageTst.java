package com.mwlib.app.plugins.median.tst;


import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 04.04.14
 * Time: 18:23
 * Тестирование времени загрузки большого растра
 */
public class LoadImageTst
{
    public static void main(String[] args) throws Exception
    {
        long tm=System.currentTimeMillis();
        String path = "C:\\PapaWK\\Projects\\JavaProj\\Victor\\DigitizeItRes\\MAPDIR\\";
        InputStream is = new BufferedInputStream(new FileInputStream(path+"MAPS.png"));
        BufferedImage bi = ImageIO.read(is);
        is.close();
        System.out.println("Load Time = " + (System.currentTimeMillis()-tm)/1000);

//        String[] nms=ImageIO.getWriterFormatNames();
//        for (String nm : nms) {
//            System.out.println("nm = " + nm);
//        }

        WritableRaster r=bi.getWritableTile(0,0);
        SampleModel model = r.getSampleModel();

        int rgb=bi.getRGB(0,0);
        System.out.println("rgb = " + Integer.toHexString(rgb));


        System.out.println("model = " + model);


//        DataBufferInt dataBuffer = (DataBufferInt)r.getDataBuffer();
//        int[] bt=dataBuffer.getData();

        DataBufferByte dataBuffer = (DataBufferByte)r.getDataBuffer();
        byte [] bt=dataBuffer.getData();


        ByteOutputStream out = new ByteOutputStream();


        Deflater d = new Deflater(Deflater.BEST_SPEED);
        DeflaterOutputStream dout = new DeflaterOutputStream(out, d);

        int capacity = 1024 * 1024;
        ByteBuffer bb = ByteBuffer.allocate(capacity);


//        int j=0;
//        for (int i : bt)
//        {
//            bb.putInt(i);
//            j++;
//            if (j*4==capacity)
//            {
//              dout.write(bb.array());
//              j=0;
//                bb.rewind();
//            }
//        }
//        dout.write(bb.array(),0,j);

        dout.write(bt);
        dout.close();

        System.out.println("Load Time = " + (System.currentTimeMillis()-tm)/1000+" szC:"+out.size()/(1024*1024)+" SZ:"+bt.length/(1024*1024));



        tm=System.currentTimeMillis();

        Inflater inf = new Inflater();


        InflaterInputStream ins = new InflaterInputStream(new ByteInputStream(out.getBytes(), out.size()), inf, 1024 * 1024);
        byte[] b = new byte[capacity];
        int ln;
        int cnt=0;
        while ((ln=ins.read(b,0,capacity))>0)
        {
            bb.rewind();
            bb.put(b,0,ln);
            cnt+=ln;
            //int[] ints=bb.asIntBuffer().array();
        }

        System.out.println("Load Time2 = " + (System.currentTimeMillis()-tm)+" sz:"+cnt/(1024*1024));


//        int dat=r.getNumDataElements();
//        if (DataBuffer.TYPE_BYTE ==r.getTransferType())
//            System.out.println("byte = ");
//
//        int nmd=r.getNumDataElements();
//        System.out.println("nmd = " + nmd);
//
//
//        int bitsz=DataBuffer.getDataTypeSize(r.getTransferType());
//
//
//
//
//
//        int[] rv=r.getPixel(0,0,new int[10]);
//        for (int i : rv) {
//            System.out.println("i = " + Integer.toHexString(i));
//        }


        //model.createCompatibleSampleModel(10,10);



//        OutputStream output = new BufferedOutputStream(new FileOutputStream(path + "MAPS.bmp"));
//        boolean rv=ImageIO.write(bi,"BMP", output);
//        System.out.println("rv = " + rv);
//        output.flush();
//        output.close();

//        Iterator<ImageWriter> ir = ImageIO.getImageWritersByFormatName("BMP");
//        while (ir.hasNext()) {
//            ImageWriter next = ir.next();
//            if (next!=null)
//            {
//                ImageOutputStream out = ImageIO.createImageOutputStream(new File(path + "MAPS.bmp"));
//                next.setOutput(out);
//                next.write(bi);
//                out.flush();
//                out.close();
//                break;
//            }
//        }

        System.out.println("Save Time = " + (System.currentTimeMillis()-tm)/1000);
    }

    static byte[] conv = new byte[4];

    static byte[] getBytesByInt(int input)
    {
        conv[3] = (byte) (input & 0xff);
        input >>= 8;
        conv[2] = (byte) (input & 0xff);
        input >>= 8;
        conv[1] = (byte) (input & 0xff);
        input >>= 8;
        conv[0] = (byte) input;

        return conv;
    }

}
