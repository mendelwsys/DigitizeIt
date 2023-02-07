package com.mwlib.app.plugins.digitizer;

import com.mwlib.app.plugins.shp.PoTrace2Shp;
import com.mwlib.ptrace.Param;
import reclass.bitmatrix.BitMatrix;
import ru.ts.factory.IParam;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.utils.data.Pair;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 11.01.14
 * Time: 13:16
 *
 */
public class ParamEx extends Param
{

    public static final int METERS_IN_GA = 10000;
    public static final int DEF_MSIZE = 3;
    public static final double DEF_COLOR_DIST = 25.0d;
    public static final int DEF_MEDIAN = 1;

    public ParamEx  clone() throws CloneNotSupportedException
    {
        return (ParamEx)super.clone();
    }


    public int borderType = BitMatrix.BORDER_ZERO;
    public int mSize= DEF_MSIZE; //Размер окна медианного фильтра
    public double colorDist = DEF_COLOR_DIST;//Цветовое расстояние
    public int median= DEF_MEDIAN;

    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    private double resolution = PoTrace2Shp.DEF_RESOLUTION;

    public double dturdsize = 2;            /* area of largest path to be ignored */


    public List<IParam> getDescParameters()
    {
        List<IParam> params= new LinkedList<IParam>();

        params.add(new DefAttrImpl(BORDERTYPE_TAG,getExpandPolicyName(borderType)));

        params.add(new DefAttrImpl(MSIZE_TAG,String.valueOf(mSize)));

        params.add(new DefAttrImpl(COLORDIST_TAG,String.format(Locale.ENGLISH, "%.2f", colorDist)));

        params.add(new DefAttrImpl(MEDIAN_TAG,median));

        params.add(new DefAttrImpl(TURNPOLICY_TAG,getTurnPolicyName(turnPolicy)));

        params.add(new DefAttrImpl(TURDSIZE_TAG,dturdsize));

        params.add(new DefAttrImpl(ALPHAMAX_TAG,String.format(Locale.ENGLISH, "%.5f",alphamax)));

        params.add(new DefAttrImpl(OPTICURVE_TAG,Boolean.toString(opticurve!=0)));

        params.add(new DefAttrImpl(OPTTOLERANCE_TAG,String.format(Locale.ENGLISH, "%.2f",opttolerance)));

        params.add(new DefAttrImpl(BPRINT_TAG, Boolean.toString(bPrint)));

        params.add(new DefAttrImpl(RCOLOR_TAG,Integer.toHexString(rColor)));

//        params.add(new DefAttrImpl(RESOLUTION_TAG,String.format(Locale.ENGLISH, "%.2f", resolution)));

        return params;
    }

    public static String getExpandPolicyName(int val)
    {
        Pair<String, Integer>[] names = getExpandPolicyNames();
        for (Pair<String, Integer> name : names)
        {
            if (name.getValue()==val)
                return name.getKey();
        }
        throw new IllegalArgumentException("wrong value for turn policy "+val);
    }

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


    public final static String  TURNPOLICY_TAG = "TURNPOLICY";
    public final static String  TURDSIZE_TAG = "TURDSIZE";
    public final static String  ALPHAMAX_TAG = "ALPHAMAX";
    public final static String  OPTICURVE_TAG = "OPTICURVE";
    public final static String  OPTTOLERANCE_TAG = "OPTTOLERANCE";
    public final static String  BPRINT_TAG = "BPRINT";
    public final static String  RCOLOR_TAG = "RCOLOR";

    public final static String  BORDERTYPE_TAG = "BORDERTYPE";
    public final static String  MSIZE_TAG = "MSIZE";
    public final static String  COLORDIST_TAG = "COLORDIST";
    public final static String  MEDIAN_TAG = "MEDIAN";
    //public static final String  RESOLUTION_TAG ="RESOLUTION";

    public static Set<String> getAcceptedTags()
    {
        return new HashSet<String>(Arrays.asList
        (
                TURNPOLICY_TAG, TURDSIZE_TAG, ALPHAMAX_TAG, OPTICURVE_TAG, OPTTOLERANCE_TAG, BPRINT_TAG, RCOLOR_TAG,
                BORDERTYPE_TAG,MSIZE_TAG,COLORDIST_TAG,MEDIAN_TAG
                //,RESOLUTION_TAG
        ));
    }

    public static boolean isAcceptedTags(String tag)
    {
        return tag!=null && getAcceptedTags().contains(tag.toUpperCase());
    }

    public void parseByTagName(String tagName,String val) throws Exception {
        try
        {
            br2:
            {
//                if (RESOLUTION_TAG.equalsIgnoreCase(tagName))
//                {
//                    try {
//                        resolution =Double.parseDouble(val);
//                        break br2;
//                    }
//                    catch (NumberFormatException e)
//                    {//
//                    }
//                }
//                else

                if (MSIZE_TAG.equalsIgnoreCase(tagName))
                {
                    try {
                        mSize=Integer.parseInt(val);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                }
                else if (COLORDIST_TAG.equalsIgnoreCase(tagName))
                {
                    try {
                        colorDist=Double.parseDouble(val);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                }
                else  if (BORDERTYPE_TAG.equalsIgnoreCase(tagName))
                {
                    Pair<String, Integer>[] expandPolicyNames = getExpandPolicyNames();
                    try {
                        int ival=Integer.parseInt(val);
                        for (Pair<String, Integer> expandPolicyName : expandPolicyNames) {
                            if (ival==expandPolicyName.getValue())
                            {
                                turnPolicy=ival;
                                break br2;
                            }
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        for (Pair<String, Integer> expandPolicyName : expandPolicyNames)
                            if (expandPolicyName.getKey().equalsIgnoreCase(val))
                            {
                                turnPolicy=expandPolicyName.getValue();
                                break br2;
                            }
                    }
                }
                else if (MEDIAN_TAG.equalsIgnoreCase(tagName))
                {
                    try
                    {
                        median= Integer.parseInt(val);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                    try
                    {
                        median= Boolean.parseBoolean(val.toLowerCase())?1:0;
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                }
                else
                if (TURNPOLICY_TAG.equalsIgnoreCase(tagName))
                {
                    Pair<String, Integer>[] turnPolicyNames = getTurnPolicyNames();
                    try {
                        int ival=Integer.parseInt(val);
                        for (Pair<String, Integer> turnPolicyName : turnPolicyNames) {
                            if (ival==turnPolicyName.getValue())
                            {
                                turnPolicy=ival;
                                break br2;
                            }
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        for (Pair<String, Integer> turnPolicyName : turnPolicyNames) {
                            if (turnPolicyName.getKey().equalsIgnoreCase(val))
                            {
                                turnPolicy=turnPolicyName.getValue();
                                break br2;
                            }
                        }
                    }
                }
                else if (TURDSIZE_TAG.equalsIgnoreCase(tagName))
                {
                    try {
                        dturdsize=Double.parseDouble(val);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }

                }
                else if (ALPHAMAX_TAG.equalsIgnoreCase(tagName))
                {
                    try
                    {
                        alphamax= Double.parseDouble(val);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                }
                else if (OPTICURVE_TAG.equalsIgnoreCase(tagName))
                {
                    try
                    {
                        opticurve= Integer.parseInt(val);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                    try
                    {
                        opticurve= Boolean.parseBoolean(val.toLowerCase())?1:0;
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                }
                else if (OPTTOLERANCE_TAG.equalsIgnoreCase(tagName))
                {
                    try
                    {
                        opttolerance= Double.parseDouble(val);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                }
                else if (BPRINT_TAG.equalsIgnoreCase(tagName))
                {
                    try
                    {
                        bPrint= (Integer.parseInt(val)!=0);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                    try {
                        bPrint= Boolean.parseBoolean(val.toLowerCase());
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                }
                else if (RCOLOR_TAG.equalsIgnoreCase(tagName))
                {
                    if (val.startsWith("0x"))
                        val=val.substring("0x".length());
                    try {
                        rColor=(int)Long.parseLong(val,16);
                        break br2;
                    }
                    catch (NumberFormatException e)
                    {//
                    }
                }
                else
                    throw new Exception("Unknown input Tag");
                throw new Exception("Wrong value");
            }
        }
        catch (Exception e) {
            throw new Exception("Error initialization or unknown tagNme:"+tagName+" value:"+val,e);
        }
    }

    public static Pair<String,Integer>[] getTurnPolicyNames()
    {
        List<Pair> rv=new LinkedList<Pair>();
        Field[] flds = Param.class.getFields();
        for (Field fld : flds) {
            String name = fld.getName();
            if (name.contains("TURNPOLICY"))
            {
                try {
                    rv.add(new Pair<String,Integer>(name,fld.getInt(null)));
                } catch (IllegalAccessException e) {
                    //Skip the Exception
                }
            }
        }
        return rv.toArray(new Pair[rv.size()]);
    }

    public static String getTurnPolicyName(int val)
    {
        Pair<String, Integer>[] names = getTurnPolicyNames();
        for (Pair<String, Integer> name : names)
        {
            if (name.getValue()==val)
                return name.getKey();
        }
        throw new IllegalArgumentException("wrong value for turn policy "+val);
    }


    public ParamEx()
    {

    }

    public ParamEx(Param param)
    {
        turnPolicy = param.turnPolicy;
        turdsize=param.turdsize;
        dturdsize = getGaByPixel(param.turdsize, resolution);
        //dturdsize = getGaByPixel(param.turdsize, PoTrace2Shp.DEF_RESOLUTION);
        alphamax =  param.alphamax;
        opticurve = param.opticurve;
        opttolerance = param.opttolerance;
    }

    public static double getGaByPixel(int pixels, double resolution) {
        return (pixels*resolution*resolution)/METERS_IN_GA;
    }

    public static int getPixelsByGa(double ga,double resolution) {
        return (int)Math.round(ga* ParamEx.METERS_IN_GA /(resolution*resolution));
    }


    public int rColor=0xFFFF0000;//Какой цвет считать единицой
}
