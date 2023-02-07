package com.mwlib.app.plugins.digitizer;

import com.mwlib.utils.Enc;
import ru.ts.toykernel.gui.util.GuiFormEncoder;
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
public class CursorOptions
     extends JDialog
{
    private JButton closeIt;
    private JButton preview;
    private JButton saveIt;
    private JCheckBox opticurve;
    private JComboBox turnpolicy;
    private JTextField opttolerance;
    private JTextField alphamax;
    private JTextField turdsize;
    private JTextField colorData;
    private JPanel contentPanel;
    private JPanel colorDataPanel;
    private JComboBox borderType;
    private JTextField mSize;
    private JTextField colorDist;
    private JCheckBox switchFilterOff;
    private JCheckBox isMedian;
    private JTextField resolution;


    public static final int ERR_RGB = 0xFF0000;
    public static final int GOOD_RGB = 0x000000;

    private ColorPanel colorDataPanel1 =new ColorPanel();
    private MyFocusAdapter fAblackColor;

    private boolean previewWasClick=false;
    public CursorOptions(final DigitizerModule module)
    {


        setContentPane(contentPanel);
        setTitle(Enc.get("$87"));
        setModal(true);
        getRootPane().setDefaultButton(saveIt);

        preview.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                 if (module!=null)
                    try {
                        ParamEx inparam = new ParamEx();
                        if (getData(inparam))
                        {
                            module.reset(false);
                            module.digitizer(inparam);
                            previewWasClick=true;
                        }
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
            public void actionPerformed(ActionEvent e) {
                if (previewWasClick)
                    module.reset(true);
                dispose();
            }
        });


        FocusAdapter focusAdapter = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);    //To change body of overridden methods use File | Settings | File Templates.
                boolean isEnable = getData(new ParamEx());
                saveIt.setEnabled(isEnable);
                preview.setEnabled(isEnable);
            }
        };


        mSize.addFocusListener(focusAdapter);
        colorDist.addFocusListener(focusAdapter);
        isMedian.addFocusListener(focusAdapter);
        resolution.addFocusListener(focusAdapter);

        opticurve.addFocusListener(focusAdapter);
        opttolerance.addFocusListener(focusAdapter);
        alphamax.addFocusListener(focusAdapter);
        turdsize.addFocusListener(focusAdapter);
        colorData.addFocusListener(focusAdapter);

        fAblackColor = new MyFocusAdapter(colorDataPanel1, colorData);
        colorData.addFocusListener(fAblackColor);
        colorDataPanel.add(colorDataPanel1);

        GuiFormEncoder.getInstance().rec(contentPanel);

    }

    public boolean isOkStatus() {
        return okStatus;
    }

    private boolean okStatus;

    private void onOK() {
        okStatus=true;
        dispose();
    }

    public void setData(ParamEx params)
    {

        {
            Pair<String, Integer>[] policyNames = ParamEx.getExpandPolicyNames();
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
        }

        colorDist.setText(String.valueOf(params.colorDist));
        mSize.setText(String.valueOf(params.mSize));
        resolution.setText(String.format(Locale.ENGLISH, "%.2f", params.getResolution()));

        isMedian.setSelected(params.median!=0);


        {
            Pair<String, Integer>[] policyNames = ParamEx.getTurnPolicyNames();
            for (int ix = 0, policyNamesLength = policyNames.length; ix < policyNamesLength; ix++)
            {
                Pair<String, Integer> policyName = policyNames[ix];
                turnpolicy.addItem(new Pair(policyName.getKey(), policyName.getValue()) {
                    public String toString() {
                        return this.getKey() + "[" + this.getValue() + "]";
                    }
                });
                if (policyName.getValue() == params.turnPolicy)
                turnpolicy.setSelectedIndex(ix);
            }
        }

        turdsize.setText(String.valueOf(params.dturdsize));
        alphamax.setText(String.format(Locale.ENGLISH, "%.5f",params.alphamax));
        opticurve.setSelected(params.opticurve!=0);
        opttolerance.setText(String.format(Locale.ENGLISH, "%.2f", params.opttolerance));
        colorData.setText(Integer.toHexString(params.rColor));
        fAblackColor.refreshcolorePane();
    }

    public boolean getData(ParamEx params)
    {
        boolean rv=true;

        {
            Pair<String,Integer> pr= (Pair) borderType.getSelectedItem();
            params.borderType=pr.getValue();
        }

        Double resolution=getDoubleTextField(this.resolution);
        if (resolution!=null)
        {
            if (resolution>0)
            {
                this.resolution.setForeground(new Color(GOOD_RGB));
                params.setResolution(resolution);
            }
            else
            {
                this.resolution.setForeground(new Color(ERR_RGB));
                rv=false;
            }
        }

        Integer mSize=getIntTextField(this.mSize,10);
        if (mSize!=null)
        {
            if ( mSize%2!=0)
            {
                this.mSize.setForeground(new Color(GOOD_RGB));
                params.mSize=mSize;
            }
            else
            {
                this.mSize.setForeground(new Color(ERR_RGB));
                rv=false;
            }
        }
        else
            rv=false;

        Double colorDist=getDoubleTextField(this.colorDist);
        if (colorDist!=null)
            params.colorDist=colorDist;
        else
            rv=false;

        params.median= isMedian.isSelected()?1:0;

        Double iturdsize=getDoubleTextField(turdsize);
        if (turdsize!=null)
            params.dturdsize=iturdsize;
        else
            rv=false;

        Double dalphamax=getDoubleTextField(alphamax);
        if (dalphamax!=null)
            params.alphamax=dalphamax;
        else
            rv=false;

        params.opticurve=opticurve.isSelected()?1:0;
        Double dopttolerance=getDoubleTextField(opttolerance);
        if (dopttolerance!=null)
            params.opttolerance=dopttolerance;
        else
            rv=false;

        Long lColor=getLongTextField(colorData, 16);
        if (lColor!=null)
            params.rColor=lColor.intValue();
        else
            rv=false;
        {
            Pair<String,Integer> pr= (Pair) turnpolicy.getSelectedItem();
            params.turnPolicy=pr.getValue();
        }

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
//        if (turdsize.getText() == null || Integer.parseInt(turdsize.getText())!=data.getTurdsize())
//            return true;
//        if (alphamax.getText() != null ? !alphamax.getText().equals(data.getAlphamax()) : data.getAlphamax() != null)
//            return true;
//        if (opticurve.isSelected() != data.isOpticurve()) return true;
//        if (opttolerance.getText() != null ? !opttolerance.getText().equals(data.getOptimization()) : data.getOptimization() != null)
//            return true;
//        return false;
//    }
}
