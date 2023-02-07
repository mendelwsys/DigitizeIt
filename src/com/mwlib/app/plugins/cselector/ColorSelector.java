package com.mwlib.app.plugins.cselector;

import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.util.GuiFormEncoder;
import ru.ts.toykernel.plugins.styles.ColorPanel;
import ru.ts.toykernel.plugins.styles.MyFocusAdapter;

import javax.swing.*;
import java.awt.event.*;

public class ColorSelector extends JDialog {
    private JPanel contentPanel;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField colorData;
    private JButton preview;
    private JPanel colorDataPanel;

    private ColorPanel colorDataPanel1 =new ColorPanel();

    public ColorSelector(final IViewControl mainmodule) {
        setContentPane(contentPanel);
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

// call onCancel() on ESCAPE
        contentPanel.registerKeyboardAction(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onCancel();
                    }
                }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        final MyFocusAdapter fAblackColor = new MyFocusAdapter(colorDataPanel1, colorData);
        colorDataPanel.addFocusListener(fAblackColor);
        colorDataPanel.add(colorDataPanel1);

        preview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 if (mainmodule!=null)
                    try {
                        //TODO Установка слоя для отображения цветоделения
                        mainmodule.refresh(null);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
            }
        });

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

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        ColorSelector dialog = new ColorSelector(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void setData(ColorSelData data) {
        colorData.setText(data.getColorData());
    }

    public void getData(ColorSelData data) {
        data.setColorData(colorData.getText());
    }

    public boolean isModified(ColorSelData data) {
        if (colorData.getText() != null ? !colorData.getText().equals(data.getColorData()) : data.getColorData() != null)
            return true;
        return false;
    }
}
