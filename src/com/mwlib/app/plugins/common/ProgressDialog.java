package com.mwlib.app.plugins.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProgressDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;

    public JProgressBar getProgressView() {
        return progressView;
    }

    private JProgressBar progressView;

    public ProgressDialog(String title,Component parent) {

        setContentPane(contentPane);
        setModal(true);
        setTitle(title);
        setResizable(false);
//        setAlwaysOnTop(true);
        setUndecorated(true);
        if (parent!=null)
            setLocationRelativeTo(parent);

//        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

//        buttonCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        });

//// call onCancel() when cross is clicked
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel();
//            }
//        });
//
//// call onCancel() on ESCAPE
//        contentPane.registerKeyboardAction(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                        onCancel();
//                    }
//                }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void main(String[] args) {
        ProgressDialog dialog = new ProgressDialog("",null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
