package com.mwlib.app.utils;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 25.01.14
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class PathUtils
{

    public static String getStoragesPaths(String projName) {
        return "." + projName;
    }

    public static String getInitPath(String inPath,final String DEF_PATH)
    {

        try {
            if (inPath==null || inPath.length()==0)
                return DEF_PATH;

            File fl = new File(inPath);
            while (fl!=null && !fl.exists())
                fl = fl.getParentFile();
            if (fl==null)
                inPath=DEF_PATH;
            else
                inPath=fl.getAbsolutePath();
            return inPath;
        } catch (Throwable e) {
            return DEF_PATH;
        }
    }

    public static boolean deleteFile(String fname,String workDir)
    {
        if (fname!=null)
        {
            fname=getAbsolutePath(fname, workDir);
            return new File(fname).delete();
        }
        return false;
    }
    public static String getAbsolutePath(String path,String workDir)
    {
        if (isAbsolutePath(path, workDir))
            return path;
        else
        { //Рассматриваем относительно рабочей директории
            if (path.startsWith("."+File.pathSeparator))
                return workDir+File.separator+path.substring(1);
            else
                return workDir+File.separator+path;
        }

    }

    public static boolean isAbsolutePath(String path, String workDir) {
        return workDir==null || (!path.startsWith("."+File.pathSeparator) && (path.contains(":") || path.startsWith(File.separator)));
    }

    public static boolean isAsDefaultWorkDir(String workDir)
    {
        return System.getProperty("user.dir").equals(workDir);
    }

    public static String getDefaultWorkDir()
    {
        return System.getProperty("user.dir");
    }

}
