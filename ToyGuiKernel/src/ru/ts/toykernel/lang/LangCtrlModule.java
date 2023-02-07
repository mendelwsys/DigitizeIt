package ru.ts.toykernel.lang;

import com.mwlib.utils.Enc;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.gui.apps2.SFViewer2;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.plugins.IGuiModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 11.09.16
 * Time: 13:27
 * Модуль переключения языка ru.ts.toykernel.lang.LangCtrlModule
 */
public class LangCtrlModule extends BaseInitAble implements IGuiModule
{
    public static final String MODULENAME = "LANGMODULE";
    private SFViewer2 viewer2;


    public LangCtrlModule(SFViewer2 viewer2)
    {
        this.viewer2 = viewer2;
    }

    public String getMenuName()
    {
        return Enc.get("$69");
    }


    public JMenu addMenu(JMenu inmenu) throws Exception
    {
        {
            JMenu menuItem = new JMenu(Enc.get("$305"));
            inmenu.add(menuItem);
//            menuItem.addActionListener(new ActionListener()
//            {
//                public void actionPerformed(ActionEvent e)
//                {
//                    try {
////                        reInitProject();
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            });

            if (!Enc.getLang().equals(Enc.get("$307")))
            {
                JMenuItem mPath=new JMenuItem(Enc.get("$306"));
                mPath.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        languageSet(Enc.get("$307"));
                    }
                });
                menuItem.add(mPath);
            }

            if (!Enc.getLang().equals(Enc.get("$309")))
            {
                JMenuItem mPath=new JMenuItem(Enc.get("$308"));
                mPath.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        languageSet(Enc.get("$309"));
                    }
                });
                menuItem.add(mPath);
            }
        }
        return inmenu;
    }

    private void languageSet(String lng)
    {
        try
        {
            Enc.initEncoder(LangCtrlModule.class,lng);
            this.viewer2.setMenu();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception {
        return inmenu;
    }

    public void registerListeners(JComponent component) throws Exception {
    }

    public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception {
        return systemtoolbar;
    }

    public void paintMe(Graphics g) throws Exception {
    }

    public String getModuleName()
    {
        return MODULENAME;
    }

    public IAnswerBean execute(ICommandBean cmd)
    {
        throw new UnsupportedOperationException();
    }

    public void registerNameConverter(INameConverter nameConverter, IFactory<INameConverter> factory) throws Exception {
    }

    public void unload()
    {
    }


    public Object init(Object obj) throws Exception
    {
//        IParam attr=(IParam)obj;
        return null;
    }
}
