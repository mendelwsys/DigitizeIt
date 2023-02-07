package ru.ts.toykernel.gui.util;

import com.mwlib.utils.Enc;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 10.09.16
 * Time: 23:29
 * To change this template use File | Settings | File Templates.
 */
public class GuiFormEncoder
{
    private GuiFormEncoder(){}

    static final GuiFormEncoder gui=new GuiFormEncoder();

    public static synchronized GuiFormEncoder getInstance()
    {
        return gui;
    }
    public void rec(Component component)
    {
        if (component instanceof JLabel)
        {
            JLabel component1 = (JLabel) component;
            String text= component1.getText();
            if (text.startsWith("$"))
              component1.setText(Enc.get(text));
        }
        else
        if (component instanceof JButton)
        {
            JButton component1 = (JButton) component;
            String text= component1.getText();
            if (text.startsWith("$"))
              component1.setText(Enc.get(text));
        }
        else
        if (component instanceof JCheckBox)
        {
            JCheckBox component1 = (JCheckBox) component;
            String text= component1.getText();
            if (text.startsWith("$"))
              component1.setText(Enc.get(text));
        }
        else if (component instanceof Container)
        {
            if (component instanceof JTabbedPane)
            {
                JTabbedPane component1 = ((JTabbedPane) component);
                int cnt= component1.getTabCount();
                for (int i = 0; i < cnt; i++)
                {
                    String text=component1.getTitleAt(i);
                    if (text.startsWith("$"))
                      component1.setTitleAt(i,Enc.get(text));
                }
            }

            Component[] comps = ((Container) component).getComponents();
            for (Component comp : comps) {
                rec(comp);
            }
        }
    }

}
