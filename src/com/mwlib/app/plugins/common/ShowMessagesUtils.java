package com.mwlib.app.plugins.common;

import ru.ts.res.ImgResources;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.04.14
 * Time: 21:18
 * To change this template use File | Settings | File Templates.
 */
public class ShowMessagesUtils
{

    public static void showMessage(String caption,String[] message)
    {
        showMessage(null,caption,message);
    }

    public static void showMessage(Component parentComponent,String caption,String[] message)
    {
        ImageIcon icon1 = ImgResources.getIconByName("images/warningDialog.png", "Message");
        JOptionPane.showMessageDialog(parentComponent, message,  caption,
                JOptionPane.INFORMATION_MESSAGE, icon1);
    }

    public static void showException(Exception e,String caption)
    {
        showException(null,e,caption);
    }

    public static void showException(Component parentComponent,Exception e,String caption)
    {
        e.printStackTrace();
        ImageIcon icon1 = ImgResources.getIconByName("images/breakpoint.png", "Error");
        JOptionPane.showMessageDialog(parentComponent, new String[]{e.getMessage()},  caption,
                JOptionPane.INFORMATION_MESSAGE, icon1);
    }

}
