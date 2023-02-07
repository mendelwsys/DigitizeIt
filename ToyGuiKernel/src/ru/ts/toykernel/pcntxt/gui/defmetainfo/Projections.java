package ru.ts.toykernel.pcntxt.gui.defmetainfo;

import com.mwlib.utils.Enc;
import ru.ts.utils.gui.tables.IHeaderSupplyer;
import ru.ts.utils.gui.tables.THeader;
import ru.ts.utils.Operation;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import ru.ts.gisutils.proj.transform.IMapTransformer;
import ru.ts.gisutils.proj.transform.TrasformerFactory;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.pcntxt.IProjContext;

public class Projections
        extends JDialog {
    private JPanel contentPane;
    private JButton buttonTransl;
    private JButton buttonCancel;
    private JTable projtable;
    private JButton loadProjection;
    private JButton buttonDel;


    private IHeaderSupplyer headersupplyer;

    private List<Map<String, Object>> fields = new LinkedList<Map<String, Object>>();
    private IProjContext mapprovider;
    public Pair<String, Boolean> retKey;


    public Projections(String stitle, IProjContext mapprovider) {

        setTableByTransformers(mapprovider);

        this.headersupplyer = ProjectionHeaders.getOptionsHeaderSupplyer();
        this.setTitle(stitle);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonTransl);

        buttonTransl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onApply();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        loadProjection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onLoad();
            }
        });

        buttonDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDelete();
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


        TableModel dataModel = new AbstractTableModel() {
            public int getColumnCount() {
                return Projections.this.headersupplyer.getOptionsRepresent().length;
            }

            public int getRowCount() {
                return Projections.this.fields.size();
            }


            public boolean isCellEditable(int rowIndex, int columnIndex) {

                THeader tblheader = Projections.this.headersupplyer.getOptionsRepresent()[columnIndex];
                return tblheader.getNameField(0).equals(ProjectionHeaders.WKT);
            }

            public Class getColumnClass(int col) {
                THeader tblheader = Projections.this.headersupplyer.getOptionsRepresent()[col];
                return tblheader.getClassValue();
            }

            public Object getValueAt(int row, int col) {
                THeader tblheader = Projections.this.headersupplyer.getOptionsRepresent()[col];
                return tblheader.getValueAt(col, row, Projections.this.fields.get(row));
            }

            public String getColumnName(int col) {
                return Projections.this.headersupplyer.getOptionsRepresent()[col].getNameField(0);
            }

        };

        projtable.setModel(dataModel);
//		projtable.setCellEditor();

    }

    private void setTableByTransformers(IProjContext mapprovider) {
        this.mapprovider = mapprovider;
        fields.clear();
        Map<Pair<String, Boolean>, IMapTransformer> transformers = mapprovider.getMapTransformers();
        List<Pair<String, Boolean>> keylist = new LinkedList<Pair<String, Boolean>>(transformers.keySet());
        for (Pair<String, Boolean> stringBooleanPair : keylist) {
            Map<String, Object> row = new HashMap<String, Object>();
            IMapTransformer transformer = transformers.get(stringBooleanPair);

            row.put(ProjectionHeaders.WKT, transformer.getWKT());
            if (!stringBooleanPair.second) {
                row.put(ProjectionHeaders.SRCMEASURE, transformer.getTransformerType().first);
                row.put(ProjectionHeaders.DSTMEASURE, transformer.getTransformerType().second);
            } else {
                row.put(ProjectionHeaders.SRCMEASURE, transformer.getTransformerType().second);
                row.put(ProjectionHeaders.DSTMEASURE, transformer.getTransformerType().first);
            }
            row.put(ProjectionHeaders.DIRECTION, stringBooleanPair.second);
            fields.add(row);
        }
    }

    private void onApply() {
        int selrow = projtable.getSelectedRow();
        Map<String, Object> mp = fields.get(selrow);

        boolean bl = (Boolean) mp.get(ProjectionHeaders.DIRECTION);
        String src = (String) mp.get(ProjectionHeaders.DSTMEASURE);
        retKey = new Pair<String, Boolean>(src, bl);
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void onDelete() {
// add your code here if necessary
        int selrow = projtable.getSelectedRow();
        Map<String, Object> mp = fields.get(selrow);
        boolean bl = (Boolean) mp.get(ProjectionHeaders.DIRECTION);
        String src = (String) mp.get(ProjectionHeaders.DSTMEASURE);
        Pair<String, Boolean> key = new Pair<String, Boolean>(src, bl);
        mapprovider.getMapTransformers().remove(key);
        setTableByTransformers(mapprovider);
        projtable.revalidate();
        projtable.repaint();
    }

    private void onLoad() {

        File file = Operation.getFilePath(MainformMonitor.frame, Enc.get("$192"), Enc.get("$193"), ".ini", null);
        if (file == null)
            return;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            IMapTransformer transformer = TrasformerFactory.createTransformerByTextFile(is, "WINDOWS-1251");
//TODO            Layer.addProjection(mapprovider, transformer);
            setTableByTransformers(mapprovider);
            projtable.revalidate();
            projtable.repaint();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (is != null)
                    is.close();
            }
            catch (IOException e) {//
            }
        }
    }

    public static void main(String[] args) {
        Projections dialog = new Projections("", null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}