package com.mwlib.app.plugins.digitizer;

import ru.ts.toykernel.gui.util.GuiFormEncoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmLayerName extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField nickName;
    private JButton checkName;

    public boolean isOkStatus() {
        return isOkStatus;
    }

    public String getNickName()
    {
        return nickName.getText();
    }

    public void setNickName(String nickName)
    {
        this.nickName.setText(nickName);
    }

    private boolean isOkStatus=false;
    public ConfirmLayerName(String title,Component parent)
    {
        setTitle(title);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        if (parent!=null)
            setLocationRelativeTo(parent);

        checkName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isOkStatus=false;
                dispose();
            }
        });
        GuiFormEncoder.getInstance().rec(contentPane);
    }

    private void onOK() {
        isOkStatus=true;
        dispose();
    }

    public static void main(String[] args) {
        ConfirmLayerName dialog = new ConfirmLayerName("",null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
