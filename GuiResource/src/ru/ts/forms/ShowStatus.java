package ru.ts.forms;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ShowStatus extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField statusText;

    public ShowStatus(String title, String status) {
        setTitle(title);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        statusText.setText(status);
    }

    public static void main(String[] args) {
        ShowStatus dialog = new ShowStatus("", "");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}
