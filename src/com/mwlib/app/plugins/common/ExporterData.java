package com.mwlib.app.plugins.common;

import com.mwlib.app.plugins.shp.PoTrace2Shp;
import com.mwlib.app.storages.raster.IRasterContainerEx;
import ru.ts.factory.IParam;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;
import ru.ts.toykernel.storages.raster.BindStruct;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import ru.ts.utils.Files;
import ru.ts.utils.data.Pair;

import java.io.File;
import java.util.*;

public class ExporterData  implements Cloneable
{

    public final static String  PATHNAME_TAG = "PATHNAME";

    public final static String  COMMONNAME_TAG = "COMMONNAME";
    public final static String  UNIONALL_TAG = "UNIONALL";
    public static final boolean DEF_UNION_ALL = true;


    private String pathName;
    private String commonName;
    private boolean unionAll = DEF_UNION_ALL;

    public static Set<String> getAcceptedModuleTags()
    {
        return new HashSet<String>(Arrays.asList(PATHNAME_TAG,COMMONNAME_TAG, UNIONALL_TAG
//                ,RESOLUTION_TAG
        )
        );
    }

    public static boolean isAcceptedModuleTags(String tag)
    {
        return tag!=null && getAcceptedModuleTags().contains(tag.toUpperCase());
    }


    private boolean getBoolean(String val) {
        boolean bVal;
        try
        {
            bVal=Boolean.parseBoolean(val);
        }
        catch (Exception e)
        {
            try {
                bVal = (Integer.parseInt(val) >0);
            } catch (NumberFormatException e1) {
                bVal = "true".equalsIgnoreCase(val);
            }
        }
        return bVal;
    }

    public List<IParam> getDescModuleParameters()
    {
        List<IParam> params= new LinkedList<IParam>();

        params.add(new DefAttrImpl(PATHNAME_TAG,pathName));

        params.add(new DefAttrImpl(COMMONNAME_TAG,commonName));

        params.add(new DefAttrImpl(UNIONALL_TAG,String.valueOf(isUnionAll())));

//        params.add(new DefAttrImpl(RESOLUTION_TAG,resolution));

        return params;
    }


    public void fillByRasterContainer(IRasterContainer rasterContainer) throws Exception
    {
        Pair<BindStruct, Integer> struct = rasterContainer.getCurrentStruct();
        BindStruct bindStruct = struct.getKey();
        String fName= bindStruct.flnames[0][0];
        String pictDesc=bindStruct.pictdir;
        String fNameNoExt = Files.getNameNoExt(fName);

        setFname(fNameNoExt);
        setCommonName(fNameNoExt);
        setPathName(pictDesc);

        setTabFile(pictDesc + File.separator + fNameNoExt + ".tab");
        setTranslate(new File(getTabFile()).exists());

        setParametersFromRaster(rasterContainer);
    }

    public void setParametersFromRaster(IRasterContainer rasterContainer) {
        if (rasterContainer instanceof IRasterContainerEx)
            setResolution(String.format(Locale.ENGLISH, "%.2f", ((IRasterContainerEx)rasterContainer).getResolution()));
        else
            setResolution(String.valueOf(PoTrace2Shp.DEF_RESOLUTION));
    }

    public ExporterData clone() throws CloneNotSupportedException
    {
        return (ExporterData)super.clone();
    }

    private String blackColor;


    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    private String colorName;

    private String whiteColor;

    private String flatness;
    private String asShapeObject;



    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public static final String FNAME_TAG ="FNAME";
    public static final String TABFILE_TAG ="TABFILE";

    public static final String TRANSLATE_TAG ="TRANSLATE";
//    public static final String RESOLUTION_TAG ="RESOLUTION";

    public static final String BLACKCOLOR_TAG ="BLACKCOLOR";
    public static final String COLORNAME_TAG ="COLORNAME";


    public static final String WHITECOLOR_TAG ="WHITECOLOR";
    public static final String WHITECOLORNAME_TAG ="WHITECOLORNAME";


    public static final String FLATNESS_TAG ="FLATNESS";
    public static final String ASSHAPEOBJECT_TAG="ASSHAPEOBJECT";

    public static final String ATTRIBUTE_TAG="ATTRIBUTE";
    public static final String ATTRIBUTENAME_TAG="ATTRIBUTENAME";

    public static final String SQUARENAME_TAG="SQUARENAME";  //square



    private String fname;
    private String tabFile;
    private boolean translate;
    private String resolution;


    public void parseByTagName(String tagName,String val) throws Exception
    {
        if (PATHNAME_TAG.equalsIgnoreCase(tagName))
            pathName=val;
        else if (COMMONNAME_TAG.equalsIgnoreCase(tagName))
            commonName=val;
        else if (FNAME_TAG.equalsIgnoreCase(tagName))
            fname=val;
        else if (TABFILE_TAG.equalsIgnoreCase(tagName))
            tabFile=val;
        else if (TRANSLATE_TAG.equalsIgnoreCase(tagName))
            translate=getBoolean(val);
//        else if (RESOLUTION_TAG.equalsIgnoreCase(tagName))
//            resolution =val;
        else if (BLACKCOLOR_TAG.equalsIgnoreCase(tagName))
            blackColor=val;
        else if (COLORNAME_TAG.equalsIgnoreCase(tagName))
            colorName =val;
        else if (WHITECOLOR_TAG.equalsIgnoreCase(tagName))
            whiteColor=val;

        else if (ASSHAPEOBJECT_TAG.equalsIgnoreCase(tagName))
            asShapeObject=val;
        else if (ATTRIBUTENAME_TAG.equalsIgnoreCase(tagName))
            attributeName=val;
        else if (ATTRIBUTE_TAG.equalsIgnoreCase(tagName))
            attribute=val;
        else if (SQUARENAME_TAG.equalsIgnoreCase(tagName))
            squareName=val;
        else if (FLATNESS_TAG.equalsIgnoreCase(tagName))
            flatness=val;
        else if (UNIONALL_TAG.equalsIgnoreCase(tagName))
            unionAll=getBoolean(val);
    }

    public List<IParam> getDescParameters()
    {
        List<IParam> params= new LinkedList<IParam>();

        String val = getFname();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(FNAME_TAG, val));

        val = getTabFile();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(TABFILE_TAG, val));

        params.add(new DefAttrImpl(TRANSLATE_TAG,String.valueOf(getTranslate())));

//        val = getResolution();
//        if (val!=null && val.length()>0)
//            params.add(new DefAttrImpl(RESOLUTION_TAG,val));

        val =getBlackColor();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(BLACKCOLOR_TAG,val));

        val = getColorName();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(COLORNAME_TAG,val));

        val =getWhiteColor();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(WHITECOLOR_TAG,val));

        val =getAsShapeObject();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(ASSHAPEOBJECT_TAG,val));

        val =getAttributeName();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(ATTRIBUTENAME_TAG,val));

        val =getSquareName();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(SQUARENAME_TAG,val));

        val =getAttribute();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(ATTRIBUTE_TAG,val));

        val =getFlatness();
        if (val!=null && val.length()>0)
            params.add(new DefAttrImpl(FLATNESS_TAG,val));

        return params;
    }

    public static Set<String> getAcceptedLayerTags()
    {
        return new HashSet<String>(Arrays.asList(
                FNAME_TAG,TABFILE_TAG,TRANSLATE_TAG,
                //RESOLUTION_TAG,
                BLACKCOLOR_TAG,COLORNAME_TAG,
                WHITECOLOR_TAG,WHITECOLORNAME_TAG,ASSHAPEOBJECT_TAG,ATTRIBUTENAME_TAG,ATTRIBUTE_TAG,
                SQUARENAME_TAG,FLATNESS_TAG
                ));
    }

    public static boolean isAcceptedLayerTags(String tag)
    {
        return tag!=null && getAcceptedModuleTags().contains(tag.toUpperCase());
    }


    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    private String  attributeName;
    private String  attribute;

    public String getSquareName() {
        return squareName;
    }

    public void setSquareName(String squareName) {
        this.squareName = squareName;
    }

    private String  squareName;

    public static String[] getExcludeFiles()
    {
        return new String[]{"PathName","CommonName","UnionAll"};//TODO настройки модуля экспорта
    }

    public String getResolution()
    {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public ExporterData() {
    }

    public String getBlackColor() {
        return blackColor;
    }

    public void setBlackColor(final String blackColor) {
        this.blackColor = blackColor;
    }

    public String getWhiteColor() {
        return whiteColor;
    }

    public void setWhiteColor(final String whiteColor) {
        this.whiteColor = whiteColor;
    }

    public String getFlatness() {
        return flatness;
    }

    public void setFlatness(final String flatness) {
        this.flatness = flatness;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(final String pathName) {
        this.pathName = pathName;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(final String fname) {
        this.fname = fname;
    }

    public String getTabFile() {
        return tabFile;
    }

    public void setTabFile(final String tabFile) {
        this.tabFile = tabFile;
    }

    public boolean isTranslate() {
        return translate;
    }

    public boolean getTranslate() {
        return translate;
    }
    public void setTranslate(final Boolean translate) {
        this.translate = translate;
    }

    public String getAsShapeObject() {
        return asShapeObject;
    }


    public void setAsShapeObject(final String asShapeObject) {
        this.asShapeObject = asShapeObject;
    }

    public boolean isUnionAll() {
        return unionAll;
    }

    public boolean getUnionAll() {
        return unionAll;
    }

    public void setUnionAll(boolean unionAll) {
        this.unionAll = unionAll;
    }

    public void setDefExportData(CommonStyle style,IRasterContainer imagestorage) throws Exception
    {

        fillByRasterContainer(imagestorage);
        setWhiteColor(Integer.toHexString(PoTrace2Shp.DEF_WHITE_COLOR));

        if (style!=null)
           setBlackColor(style.getsHexColorFill());
        else
           setBlackColor(Integer.toHexString(PoTrace2Shp.DEF_BLACK_COLOR));

        setFlatness(String.format(Locale.ENGLISH, "%.5f", PoTrace2Shp.DEF_M_FLATNESS));
        setAsShapeObject(PoTrace2Shp.DEF_SHAPEOBJ);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}