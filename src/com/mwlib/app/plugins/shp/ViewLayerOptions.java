package com.mwlib.app.plugins.shp;

import com.mwlib.app.plugins.common.ExporterData;
import com.mwlib.app.plugins.digitizer.ParamEx;
import com.mwlib.app.storages.mem.DigitizedStorage;
import com.mwlib.app.storages.mem.IPathDefContainer;
import com.mwlib.app.utils.InitAbleUtils;
import com.mwlib.app.utils.LayerUtils;
import com.mwlib.app.utils.ObjectBuilderUtils;
import com.mwlib.app.utils.PathUtils;
import com.mwlib.ptrace.PathDef;
import com.mwlib.ptrace.Utils;
import com.mwlib.utils.Enc;
import ru.ts.factory.IParam;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.rules.def.CnStyleRuleImpl;
import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.util.GuiFormEncoder;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.plugins.styles.ColorPanel;
import ru.ts.toykernel.plugins.styles.MyFocusAdapter;
import ru.ts.toykernel.proj.xml.IXMLProjBuilder;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.utils.Files;
import ru.ts.utils.Operation;
import ru.ts.utils.data.Pair;
import ru.ts.utils.gui.tables.IHeaderSupplyer;
import ru.ts.utils.gui.tables.THeader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

//com.mwlib.app.plugins.shp.ViewLayerOptions
public class ViewLayerOptions
		extends JDialog
{
    public static final String TITLE_DEF = Enc.get("$137");
    private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField colorLine;
	private JTextField colorFill;
	private JTextField lineStyle;
	private JTextField scaleFrom;
	private JTextField scaleTo;
	private JPanel colorLinePanel;
	private JPanel colorFillPanel;
	private JTable tlayers;

	private JButton buttonRefresh;
	private JButton setAllButton;
	private JButton resetAllButton;

	private JTextField lineThickness;
	private JTextField colorBox;
	private JTextField colorBackground;
	private JPanel colorBackgroundPanel;
	private JPanel colorBoxPanel;
    private JButton dellButton;
    private JTabbedPane layersCtrl;
    private JTextField pathName;
    private JButton setPathName;
    private JTextField fname;
    private JButton tabChooser;
    private JTextField tabFile;
    private JCheckBox translate;
    private JTextField blackColor;
    private JPanel blackColorPanel;
    private JTextField whiteColor;
    private JTextField flatness;
    private JPanel whiteColorPanel;
    private JComboBox splitObject;
    private JTextField attribute;
    private JTextField resolution;
    private JTextField square;
    private JButton export;
    private JCheckBox unionFiles;
    private JTextField attributeName;
    private JTextField squareName;
    private JTextField colorName;
    private JButton downLayer;
    private JButton upLayer;
    public boolean isOk = false;

    private ColorPanel blackColorPanel1=new ColorPanel();
    private ColorPanel whiteColorPanel1=new ColorPanel();


	private IHeaderSupplyer headersupplyer;
	private IViewControl mainmodule;
	private IProjContext projContext;


    private ColorPanel colorLinePanel1=new ColorPanel();
    private ColorPanel colorFillPanel1=new ColorPanel();

    private ColorPanel colorBackgroundPanel1=new ColorPanel();
    private ColorPanel colorBoxPanel1=new ColorPanel();

    public static final int ERR_RGB = 0xFF0000;
    public static final int GOOD_RGB = 0x000000;
    public static final int attrLimit =10;

	TableModel dataModel = new AbstractTableModel()
	{
        public int getColumnCount()
		{
			return headersupplyer.getOptionsRepresent().length;
		}

		public int getRowCount()
		{
			return projContext.getLayerList().size();
		}

		public boolean isCellEditable(int row, int col)
		{
			return headersupplyer.getOptionsRepresent()[col].isEditable(col, row, getValueAt(row, col));
		}

		public Class getColumnClass(int col)
		{
			THeader tblheader = headersupplyer.getOptionsRepresent()[col];
			return tblheader.getClassValue();
		}

		public void setValueAt(Object val, int row, int col)
		{
			THeader tblheader = headersupplyer.getOptionsRepresent()[col];
			tblheader.setValueAt(val, col, row, projContext.getLayerList().get(row).getLrAttrs());
		}

		public Object getValueAt(int row, int col)
		{
			THeader tblheader = headersupplyer.getOptionsRepresent()[col];
			return tblheader.getValueAt(col, row, projContext.getLayerList().get(row).getLrAttrs());
		}

		public String getColumnName(int col)
		{
			return headersupplyer.getOptionsRepresent()[col].getNameField(0);
		}

	};
	private MyFocusAdapter fAColorFill;
	private MyFocusAdapter fAColorLine;

    private boolean exportStatus;
    private ExporterData defExporterData= new ExporterData();
    private Utils poTraceUtils = new Utils();

    private MyFocusAdapter fAblackColor;
    private MyFocusAdapter fAwhiteColor;
    private IXMLProjBuilder builder;


    private boolean checkExportEnable()
    {
        boolean rv=true;
        if (!checkColorTextField(blackColor, 16))
            rv=false;

        if (!checkColorTextField(whiteColor, 16))
            rv=false;

        if (!checkDoubleTextField(flatness)) rv=false;

        if (!checkDoubleTextField(resolution)) rv=false;

        if (!checkDoubleTextField(square)) rv=false;

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
            rv=rv && exists;
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

    private boolean checkIntegerTextField(JTextField textField, int radix) {
        try {
            Integer.parseInt(textField.getText(), radix);
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


    public void initExportOptions()
    {
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
                setByTranslate();
                setExportEnable();
                setCommonFields2ExportData(defExporterData);
            }
        });

        unionFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = unionFiles.isSelected();
                defExporterData.setUnionAll(b);
                if (b)
                    fname.setText(defExporterData.getCommonName());
                else
                {
                    int index = tlayers.getSelectedRow();
                    if (index>=0 && index<tlayers.getRowCount())
                    {
                        try {
//                            setCommonFields2ExportData(defExporterData);
                            refreshDlgByTabIndex(index);
                        } catch (CloneNotSupportedException e1) {
                            ;//
                        }
                    }
                }
                setExportEnable();

                setCommonFields2ExportData(defExporterData);
            }
        });



        FocusAdapter focusAdapter = new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                setExportEnable();
                setCommonFields2ExportData(defExporterData);
            }
        };

        pathName.addFocusListener(focusAdapter);
        tabFile.addFocusListener(focusAdapter);
        flatness.addFocusListener(focusAdapter);
        resolution.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                recalcTotalArea(tlayers.getSelectedRow());
                setExportEnable();
                setCommonFields2ExportData(defExporterData);
            }
        });


        fAblackColor = new MyFocusAdapter(blackColorPanel1, blackColor)
        {
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                setExportEnable();

            }

        };
        blackColor.addFocusListener(fAblackColor);
        blackColorPanel.add(blackColorPanel1);



        fAwhiteColor = new MyFocusAdapter(whiteColorPanel1, whiteColor)
        {
                public void focusLost(FocusEvent e)
                {
                    super.focusLost(e);
                    setExportEnable();

                }
        };
        whiteColor.addFocusListener(fAwhiteColor);
        whiteColorPanel.add(whiteColorPanel1);

        setPathName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String path=pathName.getText();
                File dirPath = Operation.getDirPath(MainformMonitor.frame, Enc.get("$138"), PathUtils.getInitPath(path, MainformMonitor.workDir), null);
                if (dirPath!=null)
                {
                    pathName.setText(dirPath.getAbsolutePath());
                    setExportEnable();
                    setCommonFields2ExportData(defExporterData);
                }
            }
        });


        tabChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String path=tabFile.getText();
                File filePath = Operation.getFilePath(MainformMonitor.frame, Enc.get("$139"), Enc.get("$140"), "tab", PathUtils.getInitPath(path, MainformMonitor.workDir));
                if (filePath!=null)
                {
                    tabFile.setText(filePath.getAbsolutePath());

                    setExportEnable();

                    setCommonFields2ExportData(defExporterData);
                }
            }
        });
        this.addWindowFocusListener(new WindowAdapter()
        {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                fAblackColor.refreshcolorePane();
                fAwhiteColor.refreshcolorePane();
                setExportEnable();
            }
        });

        splitObject.addItem(new Pair(ShapeObj2Shp.POLY_ONLY, Enc.get("$141")) {
            public String toString() {
                return (String)this.getValue();
            }
        });

        splitObject.addItem(new Pair(ShapeObj2Shp.POLY_WITH_HOLES,Enc.get("$142")) {
            public String toString() {
                return (String)this.getValue();
            }
        });

        splitObject.addItem(new Pair(ShapeObj2Shp.WHOLE_OBJECT, Enc.get("$143")) {
            public String toString() {
                return (String)this.getValue();
            }
        });
        splitObject.setSelectedIndex(1);
    }

    private void setByTranslate() {
        boolean selected = translate.isSelected();
        tabFile.setEnabled(selected);
        tabChooser.setEnabled(selected);
    }

    private void setExportEnable()
    {
        boolean b1 = checkExportEnable();
        buttonRefresh.setEnabled(b1);
        export.setEnabled(b1);
    }

    private void recalcTotalArea(int selectedRow)
    {
        //int selectedRow = ;
        List<ILayer> layerList = projContext.getLayerList();
        if (selectedRow>=0 && layerList.size()>selectedRow)
        {
            ILayer lr = layerList.get(selectedRow);
            INodeStorage rmstor = (INodeStorage) lr.getStorage();
            if (rmstor instanceof IPathDefContainer)
            {
                PathDef pathDef=((IPathDefContainer) rmstor).getPathDef();
                if (pathDef!=null)
                {
                    int totalAreaPixels=poTraceUtils.getTotalArea(pathDef);
                    String sResolution= resolution.getText();
                    try {
                        double resolution = Double.parseDouble(sResolution);
                        square.setText(
                                String.format(Locale.ENGLISH, "%.2f", ParamEx.getGaByPixel(totalAreaPixels, resolution))
                        );
                    } catch (NumberFormatException e) {
                        square.setText("");
                    }
                }
            }
        }
    }

    public ViewLayerOptions(IViewControl mainmodule,IHeaderSupplyer headsuplyer,IXMLProjBuilder builder) throws Exception
	{

        this.mainmodule = mainmodule;
		this.projContext = mainmodule.getProjContext();
        this.builder=builder;
		this.headersupplyer = headsuplyer;
		setContentPane(contentPane);
		setTitle(TITLE_DEF);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onCancel();
			}
		});

// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				onCancel();
			}
		});

// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


		fAColorLine = new MyFocusAdapter(colorLinePanel1, colorLine);
		fAColorFill = new MyFocusAdapter(colorFillPanel1, colorFill);

		colorLine.addFocusListener(fAColorLine);
		colorFill.addFocusListener(fAColorFill);

		MyFocusAdapter fAColorBackground = new MyFocusAdapter(colorBackgroundPanel1, colorBackground);
		colorBackground.addFocusListener(fAColorBackground);
		MyFocusAdapter fAColorBox = new MyFocusAdapter(colorBoxPanel1, this.colorBox);
		this.colorBox.addFocusListener(fAColorBox);


		colorBackground.setText(Integer.toHexString(projContext.getProjMetaInfo().getBackgroundColor()));
		colorBox.setText(Integer.toHexString(projContext.getProjMetaInfo().getBoxColor()));

		fAColorBackground.refreshcolorePane();
		fAColorBox.refreshcolorePane();

		buttonRefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                refreshViewer(tlayers.getSelectedRow());
            }
		});

		setAllButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setLayersVisible(true);
			}
		});

		resetAllButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setLayersVisible(false);
			}
		});


		tlayers.setModel(dataModel);
		tlayers.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			int oldIndex = -1;
            ExporterData data;

			public void valueChanged(ListSelectionEvent e)
			{
                try
                {


                    if (data!=null && oldIndex>=0 && isOnLayerModified(data,oldIndex))
                    {
                        ImageIcon icon1 = ImgResources.getIconByName("images/warningDialog.png", "Message");
                        if (JOptionPane.showConfirmDialog(ViewLayerOptions.this, new String[]
                                {
                                        Enc.get("$144"),

                                }, Enc.get("$145"), JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, icon1)!=JOptionPane.NO_OPTION)
                        {
                            refreshViewer(oldIndex);
                        }
                    }

                    int index = tlayers.getSelectedRow();
                    if (oldIndex != index)
                    {
                        setCommonFields2ExportData(defExporterData);
                        refreshDlgByTabIndex(index);
                        oldIndex=index;
                        set2ExportData(data=defExporterData.clone());
                    }

                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }

            }
		});

        initExportOptions();


        colorFillPanel.add(colorFillPanel1);
        colorLinePanel.add(colorLinePanel1);

        colorBackgroundPanel.add(colorBackgroundPanel1);
        colorBoxPanel.add(colorBoxPanel1);


        export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportStatus=true;
                refreshViewer(tlayers.getSelectedRow());
                dispose();
            }
        });

        dellButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteLayer();
            }
        });

        layersCtrl.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {
                int ix=layersCtrl.getSelectedIndex();
                buttonRefresh.setEnabled(ix==0);
                if (ix>0)
                    setExportEnable();
            }
        });

        this.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent we)
            {
                int selectedRow = tlayers.getSelectedRow();
                if (selectedRow<0)
                    selectedRow=0;
                tlayers.setRowSelectionInterval(selectedRow, selectedRow);
                try
                {
                    refreshDlgByTabIndex(selectedRow);
                } catch (CloneNotSupportedException e1)
                {//
                }
                setExportEnable();
            }
        });

        upLayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveLayer(true);
            }
        });

        downLayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveLayer(false);
            }
        });

        GuiFormEncoder.getInstance().rec(contentPane);
    }

    //private ExporterData currentData;
    private void refreshDlgByTabIndex(int index)
            throws CloneNotSupportedException
    {
        List<ILayer> layerList = projContext.getLayerList();
        if (index<0 || layerList.size()<=index)
            return;
        ILayer lr = layerList.get(index);
        IDrawObjRule drwRule = lr.getDrawRule();

        if (drwRule instanceof CnStyleRuleImpl)
        {
            CommonStyle style = ((CnStyleRuleImpl) drwRule).getDefStyle();
            IAttrs map = lr.getLrAttrs();
            Map<String, Object> lrParams = InitAbleUtils.getMapParams(map);

            ExporterData obj = defExporterData.clone();
            obj.setBlackColor(style.getsHexColorFill());
            ObjectBuilderUtils.setObjectBySettersParams(obj, lrParams, ObjectBuilderUtils.ToCase.CaseIgnore, new HashSet<String>(Arrays.asList(ExporterData.getExcludeFiles())));
            style2Dlg(style,obj);
            setByTranslate();
            recalcTotalArea(index);
            setExportEnable();


            fAColorLine.refreshcolorePane();
            fAColorFill.refreshcolorePane();

            fAblackColor.refreshcolorePane();
            fAwhiteColor.refreshcolorePane();
        }
        else
            style2Dlg(null,null);
    }


    private boolean isLayersUpdate=false;
    private void moveLayer(boolean upDown)
    {
        isLayersUpdate=false;
        int selectedRow = tlayers.getSelectedRow();
        List<ILayer> layerList = this.projContext.getLayerList();
        if (selectedRow>=0 && layerList.size()>selectedRow)
        {
            ILayer moveLayer = layerList.get(selectedRow);
            if (    upDown && selectedRow>0
                     ||
                    !upDown && layerList.size()>selectedRow+1
                    )
            {
                layerList.remove(selectedRow);
                selectedRow+=(upDown?-1:1);
                layerList.add(selectedRow,moveLayer);
                tlayers.setRowSelectionInterval(selectedRow, selectedRow);
                tlayers.addNotify();
                isLayersUpdate=true;
            }
        }
    }

    private void deleteLayer()
    {
        try {
            int selectedRow = tlayers.getSelectedRow();
            List<ILayer> layerList = this.projContext.getLayerList();
            if (selectedRow>=0 && layerList.size()>selectedRow)
            {
                ILayer removeLayer = layerList.get(selectedRow);

                INodeStorage removeStorage = (INodeStorage) removeLayer.getStorage();

                InitAbleUtils.removeStorage(removeStorage, this.projContext);
                InitAbleUtils.removeFromProject(removeLayer, this.projContext);
                layerList.remove(selectedRow);
                IParam rmMark=InitAbleUtils.getParamByDescriptor(removeLayer,LayerUtils.RM_MARK);

                IParam fName = InitAbleUtils.getParamByDescriptor(removeStorage, DigitizedStorage.PO_TRACE_FILE);
                if (fName!=null)
                    PathUtils.deleteFile((String)fName.getValue(),MainformMonitor.workDir);

                InitAbleUtils.removeByParam(rmMark, builder,KernelConst.STORAGE_TAGNAME);
                InitAbleUtils.removeByParam(rmMark, builder,KernelConst.LAYER_TAGNAME);
                InitAbleUtils.removeByParam(rmMark, builder,KernelConst.RULE_TAGNAME);
                InitAbleUtils.removeByParam(rmMark, builder,KernelConst.FILTER_TAGNAME);
            }

            if (layerList.size()>0)
                selectedRow=layerList.size()-1;
            else
                selectedRow=-1;

            if (selectedRow>=0 && layerList.size()>selectedRow)
                tlayers.setRowSelectionInterval(selectedRow, selectedRow);
            else
                style2Dlg(null,null);

            tlayers.addNotify();
            mainmodule.refresh(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    private void refreshViewer(int selectedRow) {
        try
        {

            setCommonFields2ExportData(defExporterData_ext);
            setCommonFields2ExportData(defExporterData);


            //int selectedRow = tlayers.getSelectedRow();
            List<ILayer> layerList = this.projContext.getLayerList();
            if (selectedRow>=0 && layerList.size()>selectedRow)
            {
                ILayer lr = layerList.get(selectedRow);
                IDrawObjRule drwRule = lr.getDrawRule();

                ExporterData expData = defExporterData.clone();

                CommonStyle style = null;
                if (drwRule instanceof CnStyleRuleImpl)
                    style = ((CnStyleRuleImpl) drwRule).getDefStyle();

                dlg2Style(style, expData);
                //Map<String, Object> params = ObjectBuilderUtils.object2paramMap(expData, ObjectBuilderUtils.ToCase.notChange, null,new HashSet<String>(Arrays.asList(ExporterData.getExcludeFiles())));
//                IAttrs attr = lr.getLrAttrs();
//                InitAbleUtils.setAttrByParams(attr,params);

                List<IParam> params = expData.getDescParameters();
                InitAbleUtils.setInitAbleByParams(lr,params);
            }
            setGlobalSettings();
            if (isLayersUpdate)
            {
                isLayersUpdate=false;
                InitAbleUtils.reOrderLayerProject(this.projContext);
            }


            this.mainmodule.refresh(null);
        }
        catch (Exception e1)
        {//
        }
    }

    private void setLayersVisible(boolean isvisible)
	{
		try
		{
			List<ILayer> lrlist = this.projContext.getLayerList();
			for (ILayer lr : lrlist)
				lr.setVisible(isvisible);
			tlayers.repaint();
		}
		catch (Exception e1)
		{//
		}
	}

	private void onOK()
	{
        refreshViewer(tlayers.getSelectedRow());
        isOk = true;
		dispose();
	}

	private void onCancel()
	{
		dispose();
	}

	protected void style2Dlg(CommonStyle data,ExporterData expData)
	{
		if (data != null)
		{

			String colorLine = data.getsHexColorLine();
			this.colorLine.setEnabled(true);
			this.colorLine.setText(colorLine);
			colorLinePanel1.setColor(colorLine);

			String colorFill = data.getsHexColorFill();
			this.colorFill.setEnabled(true);
			this.colorFill.setText(colorFill);
			colorFillPanel1.setColor(colorFill);

			this.lineThickness.setEnabled(true);
			lineThickness.setText(data.getsLineThickness());

			this.scaleTo.setEnabled(true);
			this.scaleFrom.setEnabled(true);

			scaleTo.setText(data.getScaleHiRange());
			scaleFrom.setText(data.getScaleLowRange());

			lineStyle.setText(data.getsHexLineStyle());
			this.lineStyle.setEnabled(true);

			colorLinePanel.setVisible(true);
			colorFillPanel.setVisible(true);
		}
		else
		{
			this.colorLine.setText("");
			this.colorLine.setEnabled(false);
			this.colorFill.setText("");
			this.colorFill.setEnabled(false);
			this.lineThickness.setText("");
			this.lineThickness.setEnabled(false);
			this.scaleTo.setText("");
			this.scaleTo.setEnabled(false);
			this.scaleFrom.setText("");
			this.scaleFrom.setEnabled(false);
			this.lineStyle.setText("");
			this.lineStyle.setEnabled(false);
			colorLinePanel.setVisible(false);
			colorFillPanel.setVisible(false);
		}

        set2DlgData(expData);
	}

	protected void dlg2Style(CommonStyle data,ExporterData expData)
	{
        if (data!=null)
        {
            data.setsHexColorLine(colorLine.getText());
            data.setsHexColorFill(colorFill.getText());
            data.setsHexLineStyle(lineStyle.getText());
            data.setsLineThickness(lineThickness.getText());

            data.setLowRange(scaleFrom.getText());
            data.setHiRange(scaleTo.getText());
        }
        set2ExportData(expData);
    }


    private ExporterData defExporterData_ext;
    public void setDefExporter(ExporterData data) throws Exception
    {
        defExporterData_ext=data;
        defExporterData=data.clone();
        set2DlgData(defExporterData);
    }

    protected void set2DlgData(ExporterData data)
    {
        if (data!=null)
        {
            blackColor.setText(data.getBlackColor());

            colorName.setText(data.getColorName());
            colorName.addKeyListener(addKeyListener(colorName,attrLimit));

            squareName.setText(data.getSquareName());
            squareName.addKeyListener(addKeyListener(squareName,attrLimit));

            whiteColor.setText(data.getWhiteColor());

            flatness.setText(data.getFlatness());
            pathName.setText(data.getPathName());

            unionFiles.setSelected(data.isUnionAll());
            if (data.isUnionAll())
                fname.setText(data.getCommonName());
            else
                fname.setText(data.getFname());

            tabFile.setText(data.getTabFile());
            translate.setSelected(data.isTranslate());
            resolution.setText(data.getResolution());

            attribute.setText(data.getAttribute());
            attributeName.setText(data.getAttributeName());

            attributeName.addKeyListener(addKeyListener(attributeName,attrLimit));

            squareName.setText(data.getSquareName());
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
        else
        {

            blackColor.setText("");


            whiteColor.setText("");
            colorName.setText("");
            squareName.setText("");

            flatness.setText("");
            pathName.setText("");
            fname.setText("");
            tabFile.setText("");
            translate.setSelected(false);
            unionFiles.setSelected(false);
            tabFile.setEnabled(false);
            tabChooser.setEnabled(false);
            splitObject.setSelectedIndex(1);

            resolution.setText("");

            attribute.setText("");
            attributeName.setText("");

            squareName.setText("");

        }
    }

    private KeyAdapter addKeyListener(final JTextField attributeName1,final int attrLimit1) {
        return new KeyAdapter()
        {
            public void keyTyped(KeyEvent e) {
                String text = attributeName1.getText();
                int length = text.length();
                if (length == attrLimit1)
                    e.consume();
            }
        };
    }


    public void set2ExportData(ExporterData data) {

        if (data!=null)
        {
            data.setBlackColor(blackColor.getText());


            data.setWhiteColor(whiteColor.getText());
            data.setColorName(colorName.getText());
            data.setSquareName(squareName.getText());


            data.setFlatness(flatness.getText());
            data.setPathName(pathName.getText());

            data.setAttribute(attribute.getText());
            data.setAttributeName(attributeName.getText());

            data.setResolution(resolution.getText());

            data.setSquareName(squareName.getText());

            boolean b=unionFiles.isSelected();
            data.setUnionAll(b);
            if (b)
                data.setCommonName(fname.getText());
            else
                data.setFname(fname.getText());


            data.setTabFile(tabFile.getText());
            data.setTranslate(translate.isSelected());
            data.setAsShapeObject(((Pair<String,String>)splitObject.getSelectedItem()).getKey());

            int ix=splitObject.getSelectedIndex();
            if (ix<0)
                splitObject.setSelectedIndex(ix);
            data.setAsShapeObject(((Pair<String,String>)splitObject.getSelectedItem()).getKey());
        }
    }

    //TODO !!!!Имена общих полей брать из бина!!!!
    public void setCommonFields2ExportData(ExporterData data)
    {
        if (data!=null)
        {
            data.setPathName(pathName.getText());
            data.setResolution(resolution.getText());
            boolean selected = unionFiles.isSelected();
            data.setUnionAll(selected);
            if (selected)
                data.setCommonName(fname.getText());

        }
    }

    public boolean isOnLayerModified(ExporterData data,int oldIndex)
    {
        if (flatness.getText() != null ? !flatness.getText().equals(data.getFlatness()) : data.getFlatness() != null)
            return true;

        if (colorName.getText() != null ? !colorName.getText().equals(data.getColorName()) : data.getColorName() != null)
            return true;
        if (blackColor.getText() != null ? !blackColor.getText().equals(data.getBlackColor()) : data.getBlackColor() != null)
            return true;
        if (whiteColor.getText() != null ? !whiteColor.getText().equals(data.getWhiteColor()) : data.getWhiteColor() != null)
            return true;
        if (attributeName.getText() != null ? !attributeName.getText().equals(data.getAttributeName()) : data.getAttributeName() != null)
            return true;
        if (attribute.getText() != null ? !attribute.getText().equals(data.getAttribute()) : data.getAttribute() != null)
            return true;
        if (squareName.getText() != null ? !squareName.getText().equals(data.getSquareName()) : data.getSquareName() != null)
            return true;

        int ix=splitObject.getSelectedIndex();
        if (ix>=0)
        {
            Pair<String,String> item=(Pair<String,String>)splitObject.getItemAt(ix);
            if (!item.first.equalsIgnoreCase(data.getAsShapeObject()))
                return true;
        }

        if (unionFiles.isSelected() != unionFiles.isSelected())
            return true;
        if (!unionFiles.isSelected())
            if (fname.getText() != null ? !fname.getText().equals(data.getFname()) : data.getFname() != null)
                return true;
        else
            if (fname.getText() != null ? !fname.getText().equals(data.getCommonName()) : data.getCommonName() != null)
                return true;

        if (translate.isSelected() != data.isTranslate())
            return true;
        if (tabFile.getText() != null ? !tabFile.getText().equals(data.getTabFile()) : data.getTabFile() != null)
            return true;


        if (resolution.getText() != null ? !resolution.getText().equals(data.getResolution()) : data.getResolution() != null)
            return true;

        List<ILayer> layerList = projContext.getLayerList();

        if (oldIndex>=0 && oldIndex<layerList.size())
        {
            ILayer lr = layerList.get(oldIndex);
            IDrawObjRule drwRule = lr.getDrawRule();
            CommonStyle style;
            if (drwRule instanceof CnStyleRuleImpl)
            {
                style = ((CnStyleRuleImpl) drwRule).getDefStyle();
                return isModified(style);
            }
        }
        return false;
    }



    private void setGlobalSettings() {
        try
        {
            projContext.getProjMetaInfo().setBackgroundColor((int) Long.parseLong(colorBackground.getText(), 16));
        }
        catch (NumberFormatException e)
        {
            colorBackground.setText("0x0");
        }
        try
        {
            projContext.getProjMetaInfo().setBoxColor((int) Long.parseLong(colorBox.getText(), 16));
        }
        catch (NumberFormatException e)
        {
            colorBox.setText("0x0");
        }
    }

    public boolean isModified(CommonStyle data)
	{
		if (colorBackground.getText() == null || !colorBackground.getText().equalsIgnoreCase(Integer.toHexString(projContext.getProjMetaInfo().getBackgroundColor())))
			return true;

		if (colorBox.getText() == null || !colorBox.getText().equalsIgnoreCase(Integer.toHexString(projContext.getProjMetaInfo().getBoxColor())))
			return true;

		if (colorLine.getText() != null ? !colorLine.getText().equals(
				data.getsHexColorLine()) : data.getsHexColorLine() != null) return true;
		if (colorFill.getText() != null ? !colorFill.getText().equals(
				data.getsHexColorFill()) : data.getsHexColorFill() != null) return true;

		if (lineStyle.getText() != null ? !lineStyle.getText().equals(
				data.getsHexLineStyle()) : data.getsHexLineStyle() != null) return true;

		if (lineThickness.getText() != null ? !lineThickness.getText().equals(
				data.getsLineThickness()) : data.getsLineThickness() != null) return true;

		if (scaleFrom.getText() != null ? !scaleFrom.getText().equals(
				data.getScaleLowRange()) : data.getScaleLowRange() != null) return true;

		if (scaleTo.getText() != null ? !scaleTo.getText().equals(
				data.getScaleHiRange()) : data.getScaleHiRange() != null) return true;

		return false;
	}

    public boolean isExportStatus()
    {
        return exportStatus;
    }

    public static void main(String[] args)
    {
        try
        {
            ViewLayerOptions dialog = new ViewLayerOptions(null, null,null);
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
