package ru.ts.toykernel.plugins.styles;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 05.01.14
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class MyFocusAdapter extends FocusAdapter
{
    private ColorPanel colorPane;
    private JTextField colorText;

    public MyFocusAdapter(ColorPanel colorLinePane, JTextField colorLine)
    {

        this.colorPane = colorLinePane;
        this.colorText = colorLine;
    }

    public void focusGained(FocusEvent e)
    {
        super.focusGained(e);
    }

    public void focusLost(FocusEvent e)
    {
        super.focusLost(e);

        refreshcolorePane();
    }

    public void refreshcolorePane()
    {
        try
        {
            String strcolor = colorText.getText();

            int color = (int) Long.parseLong(strcolor, 16);
            colorPane.setColor(color);
            colorPane.repaint();
        }
        catch (NumberFormatException e1)
        {
            colorText.setText("");
        }
    }
}
