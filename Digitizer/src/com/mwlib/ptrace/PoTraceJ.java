package com.mwlib.ptrace;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 22.12.13
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
public class PoTraceJ
{
    static
    {
      System.loadLibrary("jpotrace");
    }


    public Param createDefaultParams()
    {
        Param p=new Param();
        initP(p);
        return p;
    }

    public PathDef trace(Param p, Bitmap bitmap)
    {
        return trace( p,bitmap,null);
    }

    public PathDef trace(Param p, Bitmap bitmap,IProgressObserver observer)
    {
        PathDef.setObserver(observer);
        PathDef pathDef=new PathDef(p);
        this.trace(bitmap.w,bitmap.h,bitmap.arr,pathDef);
        pathDef=pathDef.getNext();
        if (pathDef!=null)
        {
            pathDef.reInitAllByCodes();
            pathDef.setWidth(bitmap.w);
            pathDef.setHeight(bitmap.h);
        }
        return pathDef;
    }

    private native void trace(int w,int h,byte[] bmp,PathDef pathDef);

    private native void initP(Param param);
}
