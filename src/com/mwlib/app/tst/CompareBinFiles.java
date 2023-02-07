package com.mwlib.app.tst;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.04.14
 * Time: 13:21
 * To change this template use File | Settings | File Templates.
 */
public class CompareBinFiles
{
    public static void main(String[] args) throws Exception
    {
        InputStream bis1 = new BufferedInputStream(new FileInputStream(args[0]));
        InputStream bis2 = new BufferedInputStream(new FileInputStream(args[1]));

        byte[] bt1=new byte[10*1024*1024];
        byte[] bt2=new byte[10*1024*1024];

        int k=0;
            int ln;
            while ((ln=bis1.read(bt1))>0)
            {
                int ln2=0;
                int lnx;
                while ((lnx=bis2.read(bt2,ln2,ln-ln2))>0 && ln2+lnx<ln)
                {
                    ln2+=lnx;
                }
                if (ln2+lnx!=ln)
                    System.out.println("Error1 " );

                for (int i = 0; i < bt2.length; i++)
                    if (bt2[i]!=bt1[i])
                    {
                        System.out.println("Error2 i = " + Integer.toHexString(i)+" "+k);
                        return;
                    }

                k++ ;
            }
            System.out.println("Compare Ok");

       }
}


