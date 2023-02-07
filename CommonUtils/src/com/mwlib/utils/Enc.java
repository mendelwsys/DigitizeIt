package com.mwlib.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 09.09.16
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */
public class Enc
{

    static Map<String,Properties> mp;
    static String lng;

    public static void initEncoder(Class _class,String lng) throws IOException
    {
        Map<String, Properties> lang2Names = new HashMap<String, Properties>();
        InputStream ri = _class.getResourceAsStream("/res/" + lng + "Lng.properties");
        Properties properties = new Properties();
        properties.load(new InputStreamReader(ri,"UTF8"));
        ri.close();
        lang2Names.put(lng,properties);
        Enc.setEnc(lang2Names, lng);
    }


    static public void setEnc(Map<String, Properties> mp, String defLng)
    {
        Enc.mp=mp;
        Enc.lng=defLng;
    }

    static public String[] getLanguages()
    {
        return mp.keySet().toArray(new String[mp.keySet().size()]);
    }

    static public String getLang()
    {
        return Enc.lng;
    }

    static public void setLang(String lng)
    {
        Enc.lng=lng;
    }

    static public String get(String enc)
    {
        String property = mp.get(lng).getProperty(enc);

        return property.substring(1,property.length()-1);
    }

}
