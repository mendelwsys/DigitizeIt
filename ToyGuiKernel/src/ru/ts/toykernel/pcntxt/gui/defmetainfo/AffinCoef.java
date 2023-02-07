package ru.ts.toykernel.pcntxt.gui.defmetainfo;

import javax.swing.*;
import java.awt.event.*;

public class AffinCoef extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    public String getMatrix() {
        return matrix.getText();
    }

    private JTextField matrix;
    public boolean isOk = false;

    public AffinCoef(String matrix) {
        this.matrix.setText(matrix);
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

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        isOk = true;
        dispose();
    }

    private void onCancel() {
        isOk = false;
        dispose();
    }

    public static void main(String[] args) {
        AffinCoef dialog = new AffinCoef("");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}
