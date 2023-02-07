package com.mwlib.ptrace;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 12.04.14
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
public class IOUtils
{

    public static final String SEPARATOR = "#@@$";

    public void saveAllToStream(PathDef def,DataOutputStream os) throws IOException
    {
        long code=1;
        Map<PathDef,Long> path2Code=new HashMap<PathDef, Long>();
        while (def!=null)
        {
            code = saveDefCode(def, os, code, path2Code);
            code = saveDefCode(def.getFirstChild(), os, code, path2Code);
            code = saveDefCode(def.getSibling(), os, code, path2Code);
            saveToStream(def,os);
            os.write(SEPARATOR.getBytes());
            def=def.getNext();
        }
        os.writeLong(-1L);
    }

    public PathDef loadAllFromStream(DataInputStream is) throws IOException
    {
        PathDef _def = new PathDef(null);
        PathDef def=_def;
        while(true)
        {
            long thisCode = is.readLong();
            if (thisCode<0)
                break;
            long childCode = is.readLong();
            long siblingCode = is.readLong();
            def=def.createNextL(thisCode,childCode,siblingCode);

            loadFromStream(def,is);
            byte[] bseparator = new byte[SEPARATOR.length()];
            is.readFully(bseparator);
            if (!new String(bseparator).equals(SEPARATOR))
                throw new IOException("Wrong format file");
        }
        PathDef rv = _def.getNext();
        rv.reInitAllByCodes();
        return rv;
    }


    private long saveDefCode(PathDef def, DataOutputStream os, long code, Map<PathDef, Long> path2Code) throws IOException {
        if (def==null)
            os.writeLong(0L);
        else
        {
            Long thisCode=path2Code.get(def);
            if (thisCode==null)
                path2Code.put(def,thisCode=code++);
            os.writeLong(thisCode);
        }
        return code;
    }

    public void loadFromStream(PathDef def,DataInputStream is) throws IOException
    {
        def.setWidth(is.readInt());
        def.setHeight(is.readInt());
        def.setArea(is.readInt());
        def.setSign(is.readInt());

        SegmentDef[] segs = new SegmentDef[is.readInt()];
        CurveDef curve = new CurveDef(segs);
        segs=curve.getSegmentDefs();
        for (int i = 0; i < segs.length; i++) {
            segs[i] = new SegmentDef();
            segs[i].cmd=is.readInt();
            segs[i].xs=loadDoubleArray(is);
            segs[i].ys=loadDoubleArray(is);
        }
        def.setCurve(curve);

    }

    public void saveToStream(PathDef def,DataOutputStream os) throws IOException
    {

        os.writeInt(def.getWidth());
        os.writeInt(def.getHeight());
        os.writeInt(def.getArea());
        os.writeInt(def.getSign());

        CurveDef curve = def.getCurve();
        SegmentDef[] segs = curve.getSegmentDefs();
        os.writeInt(segs.length);
        for (SegmentDef seg : segs)
        {
            os.writeInt(seg.getCmd());
            saveDoubleArray(os, seg.getX());
            saveDoubleArray(os, seg.getY());
        }

    }

    private double[] loadDoubleArray(DataInputStream is) throws IOException
    {
        double[] rv=new double[is.readInt()];
        for (int i = 0; i < rv.length; i++)
            rv[i]=is.readDouble();
        return rv;
    }

    private void saveDoubleArray(DataOutputStream os, double[] xs) throws IOException {
        os.writeInt(xs.length);
        for (double x : xs)
            os.writeDouble(x);
    }


    public PathDef testIOUtils(PathDef def) throws IOException
    {
        Param p=def.getParam();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bos);
        saveAllToStream(def, os);
        os.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        DataInputStream is=new DataInputStream(bis);
        PathDef pathDef = loadAllFromStream(is);
        pathDef.setParam(p);
        return pathDef;
    }


}
