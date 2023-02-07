package com.mwlib.app.plugins.shp;

import com.mwlib.app.plugins.common.ExporterData;
import com.mwlib.app.utils.PathUtils;
import com.mwlib.utils.Enc;
import ru.ts.toykernel.gui.util.GuiFormEncoder;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.plugins.styles.ColorPanel;
import ru.ts.toykernel.plugins.styles.MyFocusAdapter;
import ru.ts.utils.Operation;
import ru.ts.utils.data.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ExpOptions extends JDialog {
    public static final int ERR_RGB = 0xFF0000;
    public static final int GOOD_RGB = 0x000000;
    //public static final String DEF_OPENLOCATION = System.getProperty("user.dir") + File.separator + "MAPDIR";
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField blackColor;
    private JTextField whiteColor;
    private JPanel blackColorPanel;
    private JTextField flatness;
    private JTextField pathName;
    private JTextField fname;
    private JCheckBox translate;
    private JTextField tabFile;

    private JButton tabChooser;
    private JButton setPathName;
    private JPanel whiteColorPanel;
    private JComboBox splitObject;


    private ColorPanel blackColorPanel1=new ColorPanel();
    private ColorPanel whiteColorPanel1=new ColorPanel();


    public boolean isOkStatus() {
        return okStatus;
    }

    private boolean okStatus;

    public ExpOptions() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        translate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                boolean selected = translate.isSelected();
                tabFile.setEnabled(selected);
                tabChooser.setEnabled(selected);
                buttonOK.setEnabled(checkOkEnable());
            }
        });

        FocusAdapter focusAdapter = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);    //To change body of overridden methods use File | Settings | File Templates.
                buttonOK.setEnabled(checkOkEnable());
            }
        };

        pathName.addFocusListener(focusAdapter);
        tabFile.addFocusListener(focusAdapter);
        flatness.addFocusListener(focusAdapter);



        final MyFocusAdapter fAblackColor = new MyFocusAdapter(blackColorPanel1, blackColor)
        {
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                buttonOK.setEnabled(checkOkEnable());
            }

        };
        blackColor.addFocusListener(fAblackColor);
        blackColorPanel.add(blackColorPanel1);



        final MyFocusAdapter fAwhiteColor = new MyFocusAdapter(whiteColorPanel1, whiteColor)
        {
                public void focusLost(FocusEvent e)
                {
                    super.focusLost(e);
                    buttonOK.setEnabled(checkOkEnable());
                }
        };
        whiteColor.addFocusListener(fAwhiteColor);
        whiteColorPanel.add(whiteColorPanel1);

        setPathName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String path=pathName.getText();
                File dirPath = Operation.getDirPath(MainformMonitor.frame, Enc.get("$119"), PathUtils.getInitPath(path, MainformMonitor.workDir), null);
                if (dirPath!=null)
                {
                    pathName.setText(dirPath.getAbsolutePath());
                    buttonOK.setEnabled(checkOkEnable());
                }
            }
        });


        tabChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String path=tabFile.getText();
                File filePath = Operation.getFilePath(MainformMonitor.frame, Enc.get("$120"), Enc.get("$121"), "tab", PathUtils.getInitPath(path, MainformMonitor.workDir));
                if (filePath!=null)
                {
                    tabFile.setText(filePath.getAbsolutePath());
                    buttonOK.setEnabled(checkOkEnable());
                }
            }
        });
        this.addWindowFocusListener(new WindowAdapter()
        {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                fAblackColor.refreshcolorePane();
                fAwhiteColor.refreshcolorePane();
                buttonOK.setEnabled(checkOkEnable());
            }
        });

        splitObject.addItem(new Pair(ShapeObj2Shp.POLY_ONLY, Enc.get("$122")) {
            public String toString() {
                return (String)this.getValue();
            }
        });

        splitObject.addItem(new Pair(ShapeObj2Shp.POLY_WITH_HOLES,Enc.get("$123")) {
            public String toString() {
                return (String)this.getValue();
            }
        });

        splitObject.addItem(new Pair(ShapeObj2Shp.WHOLE_OBJECT, Enc.get("$124")) {
            public String toString() {
                return (String)this.getValue();
            }
        });
        splitObject.setSelectedIndex(1);

        GuiFormEncoder.getInstance().rec(contentPane);
    }

    private boolean checkOkEnable()
    {
        boolean rv=true;
        if (!checkColorTextField(blackColor, 16))
            rv=false;

        if (!checkColorTextField(whiteColor, 16))
            rv=false;

        if (!checkDoubleTextField(flatness)) rv=false;

        if (!new File(pathName.getText()).exists())
        {
            pathName.setForeground(new Color(ERR_RGB));
            rv=false;
        }
        else
            pathName.setForeground(new Color(GOOD_RGB));


        if (translate.isSelected())
        {

            boolean exists = new File(tabFile.getText()).exists();
            if (!exists)
                tabFile.setForeground(new Color(ERR_RGB));
            else
                tabFile.setForeground(new Color(GOOD_RGB));
            rv=exists;
        }
        return rv;
    }
    private boolean checkColorTextField(JTextField textField, int radix) {
        return textField.getText().length() <= 8 && checkLongTextField(textField, radix);
    }

    private boolean checkLongTextField(JTextField textField, int radix) {
        try {
            Long.parseLong(textField.getText(), radix);
            textField.setForeground(new Color(GOOD_RGB));
        } catch (NumberFormatException e) {
            textField.setForeground(new Color(ERR_RGB));
            return false;
        }
        return true;
    }

    private boolean checkDoubleTextField(JTextField textField)
    {
        try {
            Double.parseDouble(textField.getText());
            textField.setForeground(new Color(GOOD_RGB));
        } catch (NumberFormatException e) {
            textField.setForeground(new Color(ERR_RGB));
            return false;
        }
        return true;
    }
    private void onOK() {
        okStatus=true;
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        ExpOptions dialog = new ExpOptions();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

/*
        if (!data.isTranslate())
        {
            tabFile.setEnabled(false);
            tabChooser.setEnabled(false);
        }
 */
    public void setData(ExporterData data) {
        blackColor.setText(data.getBlackColor());
        whiteColor.setText(data.getWhiteColor());
        flatness.setText(data.getFlatness());
        pathName.setText(data.getPathName());
        fname.setText(data.getFname());
        tabFile.setText(data.getTabFile());
        translate.setSelected(data.isTranslate());
        if (!data.isTranslate())
        {
            tabFile.setEnabled(false);
            tabChooser.setEnabled(false);
        }

        int cnt=splitObject.getItemCount();
        br:
        {
            for (int ix=0;ix<cnt;ix++)
            {
                Pair<String,String> item=(Pair<String,String>)splitObject.getItemAt(ix);
                if (item.first.equalsIgnoreCase(data.getAsShapeObject()))
                {
                    splitObject.setSelectedIndex(ix);
                    break br;
                }
            }
            splitObject.setSelectedIndex(1);
        }
    }

    public void getData(ExporterData data) {
        data.setBlackColor(blackColor.getText());
        data.setWhiteColor(whiteColor.getText());
        data.setFlatness(flatness.getText());
        data.setPathName(pathName.getText());
        data.setFname(fname.getText());
        data.setTabFile(tabFile.getText());
        data.setTranslate(translate.isSelected());
        data.setAsShapeObject(((Pair<String,String>)splitObject.getSelectedItem()).getKey());

        int ix=splitObject.getSelectedIndex();
        if (ix<0)
            splitObject.setSelectedIndex(ix);
        data.setAsShapeObject(((Pair<String,String>)splitObject.getSelectedItem()).getKey());
    }

    public boolean isModified(ExporterData data) {
        if (blackColor.getText() != null ? !blackColor.getText().equals(data.getBlackColor()) : data.getBlackColor() != null)
            return true;
        if (whiteColor.getText() != null ? !whiteColor.getText().equals(data.getWhiteColor()) : data.getWhiteColor() != null)
            return true;
        if (flatness.getText() != null ? !flatness.getText().equals(data.getFlatness()) : data.getFlatness() != null)
            return true;
        if (pathName.getText() != null ? !pathName.getText().equals(data.getPathName()) : data.getPathName() != null)
            return true;
        if (fname.getText() != null ? !fname.getText().equals(data.getFname()) : data.getFname() != null) return true;
        if (tabFile.getText() != null ? !tabFile.getText().equals(data.getTabFile()) : data.getTabFile() != null)
            return true;
        if (translate.isSelected() != data.isTranslate()) return true;

        return false;
    }
}
