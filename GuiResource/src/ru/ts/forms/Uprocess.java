package ru.ts.forms;

import javax.swing.*;
import java.awt.event.*;

public class Uprocess
        extends JDialog {
    public JPanel contentPane;
    public JButton buttonCancel;
    public JProgressBar uploadProgress;
    public JTextField operation;

    private Thread ctrlprocess;
    UpRocessCtrl ctrl;

    public abstract static class UpRocessCtrl implements Runnable {
        protected Uprocess uprocess;

        void setProgressBar(Uprocess uprocess) {
            this.uprocess = uprocess;
        }

        abstract void onCancel();
    }

	public Uprocess(String title)
	{
		setTitle(title);
		setContentPane(contentPane);
		setModal(true);
		uploadProgress.setMaximum(100);
		uploadProgress.setValue(0);

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

	public Uprocess(String title, UpRocessCtrl ctrl)
	{
        setTitle(title);
        ctrl.setProgressBar(this);
        this.ctrl = ctrl;
        setContentPane(contentPane);
        setModal(true);
        uploadProgress.setMaximum(100);
        uploadProgress.setValue(0);
        this.addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent e) {
                (ctrlprocess = new Thread(Uprocess.this.ctrl)).start();
            }

            public void windowClosing(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
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

    private void onCancel() {
		if (ctrl!=null)
		{
			ctrl.onCancel();
			ctrlprocess.interrupt();
		}
		dispose();
    }

    public static void main(String[] args) {
        Uprocess dialog = new Uprocess("", null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
