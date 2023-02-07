package com.mwlib.app.plugins.median;

import com.mwlib.utils.Enc;
import ru.ts.toykernel.plugins.styles.ColorPanel;
import ru.ts.toykernel.plugins.styles.MyFocusAdapter;
import ru.ts.utils.data.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 05.01.14
 * Time: 13:08
 * Paramtes form setting
 */
public class ParametersForm
     extends JDialog
{
    private JButton closeIt;
    private JButton preview;
    private JButton saveIt;
    private JComboBox borderType;
    private JTextField colorDist;
    private JTextField mSize;
    private JTextField colorData;
    private JPanel contentPanel;
    private JPanel colorDataPanel;


    public static final int ERR_RGB = 0xFF0000;
    public static final int GOOD_RGB = 0x000000;

    private ColorPanel colorDataPanel1 =new ColorPanel();
    private MyFocusAdapter fAblackColor;

    public ParametersForm(final MedianModule module)
    {


        setContentPane(contentPanel);
        setTitle(Enc.get("$80"));
        setModal(true);
        getRootPane().setDefaultButton(saveIt);

        preview.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                 if (module!=null)
                    try {
                        MedianParam inparam = new MedianParam();
                        if (getData(inparam))
                            module.viewFilter(inparam);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
            }
        });
        saveIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });
        closeIt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                module.resetFilter();
                dispose();
            }
        });


        FocusAdapter focusAdapter = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);    //To change body of overridden methods use File | Settings | File Templates.
                boolean isEnable = getData(new MedianParam());
                saveIt.setEnabled(isEnable);
                preview.setEnabled(isEnable);
            }
        };

        colorDist.addFocusListener(focusAdapter);
        mSize.addFocusListener(focusAdapter);
        colorData.addFocusListener(focusAdapter);

        fAblackColor = new MyFocusAdapter(colorDataPanel1, colorData);
        colorData.addFocusListener(fAblackColor);
        colorDataPanel.add(colorDataPanel1);

    }

    public boolean isOkStatus() {
        return okStatus;
    }

    private boolean okStatus;

    private void onOK() {
        okStatus=true;
        dispose();
    }

    public void setData(MedianParam params)
    {
        Pair<String, Integer>[] policyNames = MedianParam.getExpandPolicyNames();
        for (int ix = 0, policyNamesLength = policyNames.length; ix < policyNamesLength; ix++)
        {
            Pair<String, Integer> policyName = policyNames[ix];
            borderType.addItem(new Pair(policyName.getKey(), policyName.getValue()) {
                public String toString() {
                    return this.getKey() + "[" + this.getValue() + "]";
                }
            });
            if (policyName.getValue() == params.borderType)
            borderType.setSelectedIndex(ix);
        }

        mSize.setText(String.valueOf(params.mSize));
        colorDist.setText(String.format(Locale.ENGLISH, "%.2f",params.colorDist));
        colorData.setText(Integer.toHexString(params.rColor));
        fAblackColor.refreshcolorePane();
    }

    public boolean getData(MedianParam params)
    {
        boolean rv=true;
        Integer iturdsize=getIntTextField(mSize,10);
        if (mSize!=null)
            params.mSize=iturdsize;
        else
            rv=false;

        Double dalphamax=getDoubleTextField(colorDist);
        if (dalphamax!=null)
            params.colorDist=dalphamax;
        else
            rv=false;

        Long lColor=getLongTextField(colorData, 16);
        if (lColor!=null)
            params.rColor=lColor.intValue();
        else
            rv=false;

        Pair<String,Integer> pr= (Pair) borderType.getSelectedItem();
        params.borderType=pr.getValue();

        return rv;
    }


    private Long getLongTextField(JTextField textField, int radix) {
        try {
            Long rv=Long.parseLong(textField.getText(), radix);
            textField.setForeground(new Color(GOOD_RGB));
            return rv;
        } catch (NumberFormatException e) {
            textField.setForeground(new Color(ERR_RGB));
        }
        return null;
    }


    private Integer getIntTextField(JTextField textField, int radix) {
        try {
            int rv = Integer.parseInt(textField.getText(), radix);
            textField.setForeground(new Color(GOOD_RGB));
            return rv;
        } catch (NumberFormatException e) {
            textField.setForeground(new Color(ERR_RGB));
        }
        return null;
    }


    private Double getDoubleTextField(JTextField textField)
    {
        try {
            double rv=Double.parseDouble(textField.getText());
            textField.setForeground(new Color(GOOD_RGB));
            return rv;
        } catch (NumberFormatException e) {
            textField.setForeground(new Color(ERR_RGB));
        }
        return null;
    }

//
//    public boolean isModified(DataParameters data) {
//        if (mSize.getText() == null || Integer.parseInt(mSize.getText())!=data.getTurdsize())
//            return true;
//        if (colorDist.getText() != null ? !colorDist.getText().equals(data.getAlphamax()) : data.getAlphamax() != null)
//            return true;
//        if (opticurve.isSelected() != data.isOpticurve()) return true;
//        if (opttolerance.getText() != null ? !opttolerance.getText().equals(data.getOptimization()) : data.getOptimization() != null)
//            return true;
//        return false;
//    }
}
