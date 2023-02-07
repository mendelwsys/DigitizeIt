package com.mwlib.app.utils;


import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 11.04.14
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public class ObjectBuilderUtils
{
    public enum ToCase {
        ToLowerCase,
        notChange,
        ToUpperCase,
        CaseIgnore
    }

//    // WARN: merge ON
//    public <T> T setObjectByRequest(T bean, final HttpServletRequest request) {
//        return this.setObjectByRequest(bean, request, DaoUtils.ToCase.ToLowerCase);
//    }
//
//    public <T> T setObjectByRequest(T bean, final HttpServletRequest request, DaoUtils.ToCase toCase) {
//        Map<String, String[]> reqparams = request.getParameterMap();
//
//        Map<String, String> params = new HashMap<String, String>(reqparams.size());
//        for (String paramKey : reqparams.keySet()) {
//            String[] paramVals = reqparams.get(paramKey);
//            if (paramVals != null && paramVals.length > 0) {
//                switch (toCase) {
//                    case ToLowerCase:
//                        paramKey = paramKey.toLowerCase();
//                        break;
//                    case ToUpperCase:
//                        paramKey = paramKey.toUpperCase();
//                        break;
//                }
//
//                params.put(paramKey, paramVals[0]);
//            }
//        }      //import com.topsbi.fss.presentation.dispatchers.actions.repln.sickform.utils.db.DaoUtils;
//        DaoUtils.setObjectBySettersParams(bean, params, toCase);
//        return bean;
//    }


    public static Map<String, String> createRefMap(String[][] refs) {
        Map<String, String> rv = new HashMap<String, String>();
        for (String[] ref : refs)
            rv.put(ref[0], ref[1]);
        return rv;
    }


    public static String object2XML(Object obj, ToCase toCase, Map<String, String> replaceMap) {

        String rs = "";

        Map<String, Object> rv = object2paramMap(obj, toCase, replaceMap,null);
        for (String name : rv.keySet()) {

            Object nameobj = rv.get(name);
            if (nameobj == null)
                continue;
            if (nameobj instanceof Iterable) {
                Iterable coll = (Iterable) nameobj;
                rs += "<" + name + ">";
                for (Object co : coll) {
                    rs += "<" + co.getClass().getName() + ">";
                    rs += object2XML(co, toCase, replaceMap);
                    rs += "</" + co.getClass().getName() + ">";
                }
                rs += "<" + name + ">";
            } else if (!(nameobj.getClass().isArray())) {
                rs += "<" + name + ">";

                if (nameobj instanceof String ||
                        nameobj instanceof Integer ||
                        nameobj instanceof Long ||
                        nameobj instanceof Boolean ||
                        nameobj instanceof Character ||
                        nameobj instanceof StringBuffer) {

                    try {
                        rs += forXml(nameobj.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (nameobj instanceof Class) {
                    rs += ((Class) nameobj).getName();
                } else
                    rs += object2XML(nameobj, toCase, replaceMap);
                rs += "</" + name + ">";
            }
        }
        return rs;
    }


    public static Map<String, Method> getObjectMethods(Object obj, ToCase toCase, String prefix) {
        try {
            Map<String, Method> rv = new HashMap<String, Method>();
            if (obj != null)
            {
                Class<? extends Object> objClass = obj.getClass();
                Method[] met = objClass.getMethods();
                for (Method method : met) { //find setters and call it
                    String mname = method.getName();
                    Class[] types;
                    if (mname.startsWith(prefix)) {
                        mname = mname.substring(prefix.length());
                        switch (toCase) {
                            case ToUpperCase:
                                mname = mname.toUpperCase();
                                break;
                            case ToLowerCase:
                                mname = mname.toLowerCase();
                                break;
                        }
                        //mname=(""+mname.charAt(0)).toLowerCase()+mname.substring(1);
                        rv.put(mname, method);
                    }
                }
            }
            return rv;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static void setObjectBySettersParams(Object obj, Map<String,Object> params, ToCase toCase,Set<String> ignore) {
        try
        {
            String[] names=null;
            if (toCase==ToCase.CaseIgnore)
                names=params.keySet().toArray(new String[params.size()]);


            Class<? extends Object> objClass = obj.getClass();

            Method[] met = objClass.getMethods();
            for (Method method : met) { //find setters and call it
                String mname = method.getName();
                Class[] types;
                if (mname.startsWith("set") && (types = method.getParameterTypes()).length == 1
//                        &&
//                        (
//                                types[0].isInstance("") || types[0].isInstance(Boolean.class) ||
//
                        )
                {
                    mname = mname.substring("set".length());
                    switch (toCase)
                    {
                        case ToUpperCase:
                            if (ignore!=null && ignore.contains(mname.toUpperCase()))
                                continue;
                            mname = mname.toUpperCase();
                            break;
                        case ToLowerCase:
                            if (ignore!=null && ignore.contains(mname.toLowerCase()))
                                continue;
                            mname = mname.toLowerCase();
                            break;
                        case CaseIgnore:
                            if (ignore!=null && ignore.contains(mname))
                                continue;
                            if (names!=null)
                                for (String name : names)
                                    if (name.equalsIgnoreCase(mname))
                                    {
                                        mname=name;
                                        break;
                                    }
                    }
                    //mname=(""+mname.charAt(0)).toLowerCase()+mname.substring(1);
                    Object val = params.get(mname);
                    if (val != null)
                    {
                        if (val.getClass().isAssignableFrom(types[0]))
                            method.invoke(obj, val);
                        else
                        if (val instanceof String)
                            invokeMethodByArg(obj, method, types[0], (String) val);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void invokeMethodByArg(Object obj, Method method, Class type, String val)
    {

        if (val==null || val.length()==0)
            return;
        if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class))
        {
            try {
                method.invoke(obj,Boolean.parseBoolean(val));
            }
            catch (Exception e)
            {//
            }
        }
        else if (type.isAssignableFrom(Character.class) || type.isAssignableFrom(char.class))
        {
            try {
                method.invoke(obj, val.charAt(0));
            }
            catch (Exception e)
            {//
            }
        }
        else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class))
        {
            try {
                method.invoke(obj, Byte.parseByte(val));
            }
            catch (Exception e)
            {//
            }
        }
        else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class))
        {
            try {
                method.invoke(obj, Short.parseShort(val));
            }
            catch (Exception e)
            {//
            }
        }
        else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class))
        {
            try {
                method.invoke(obj, Integer.parseInt(val));
            }
            catch (Exception e)
            {//
            }
        }
        else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class))
        {
            try {
                method.invoke(obj, Long.parseLong(val));
            }
            catch (Exception e)
            {//
            }
        }
        else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class))
        {
            try
            {
                method.invoke(obj, Float.parseFloat(val));
            }
            catch (Exception e)
            {//
            }
        }
        else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class))
        {
            try {
                method.invoke(obj, Double.parseDouble(val));
            }
            catch (Exception e)
            {//
            }
        }
    }

    /**
     * @param obj        - object for translation
     * @param toCase
     * @param replaceMap - заместить в возвращаемом заначении значения с именем key на значения с именем value  @return - object getter name or field name to value
     */
    public static Map<String, Object> object2paramMap(Object obj, ToCase toCase, Map<String, String> replaceMap,Set<String> ignore) {
        try {
            Map<String, Object> rv = new HashMap<String, Object>();
            Class<? extends Object> objClass = obj.getClass();
            Field[] res = objClass.getFields();
            for (Field re : res) {
                String name = re.getName();
                Object p = re.get(obj);
                if (p != null)
                    rv.put(name, p);
            }

            Method[] met = objClass.getMethods();
            for (Method method : met) { //find getter
                String mname = method.getName();
                if (mname.startsWith("get") && method.getParameterTypes().length == 0)
                {
                    mname = mname.substring("get".length());
                    switch (toCase) {
                        case ToUpperCase:
                            if (ignore!=null && ignore.contains(mname.toUpperCase()))
                                continue;
                            rv.put(mname.toUpperCase(), method.invoke(obj));
                            break;
                        case ToLowerCase:
                            if (ignore!=null && ignore.contains(mname.toLowerCase()))
                                continue;
                            rv.put(mname.toLowerCase(), method.invoke(obj));
                            break;
                        default:
                            if (ignore!=null && ignore.contains(mname))
                                continue;
                            rv.put(mname, method.invoke(obj));
                    }
                }
            }

            if (replaceMap != null && replaceMap.size() > 0) {
                Map<String, Object> rv2 = new HashMap<String, Object>();
                Set<String> keys = new HashSet<String>(rv.keySet());
                for (String key : keys) {
                    String inproc = replaceMap.get(key);
                    if (inproc != null)
                        rv2.put(inproc, rv.remove(key));
                }
                rv2.putAll(rv);
                rv = rv2;
            }
            return rv;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String forXml(String text) throws IOException{
        //
        if(text==null || (text!=null && text.length()==0)){return "";}
        //
        final StringWriter out = new StringWriter(text.length());
        int start = 0, last = 0;
        char[] data = text.toCharArray();
	while(last < data.length){
	      char c = data[last];
              //
	      // escape markup delimiters only ... and do bulk
	      // writes wherever possible, for best performance
	      //
	      // note that character data can't have the CDATA
	      // termination "]]>"; escaping ">" suffices, and
	      // doing it very generally helps simple parsers
	      // that may not be quite correct.
	      //
              if(c == '<'){			// not legal in char data
		 out.write (data, start, last - start);
		 start = last + 1;
		 out.write ("&lt;");
	      }else if(c == '>'){		// see above
		out.write (data, start, last - start);
		start = last + 1;
		out.write ("&gt;");
	      }else if (c == '&'){		// not legal in char data
		out.write (data, start, last - start);
		start = last + 1;
		out.write ("&amp;");
	      }
	      last++;
	}
	out.write (data, start, last - start);
        return out.toString();
    }


}
