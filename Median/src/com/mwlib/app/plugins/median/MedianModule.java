package com.mwlib.app.plugins.median;

import com.mwlib.utils.Enc;
import com.mwlib.utils.raster.PartialImageReader;
import reclass.IProgressObserver;
import reclass.MedianFilter;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.raster.BindStruct;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import ru.ts.utils.data.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileInputStream;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.04.14
 * Time: 16:02
 * Модуль медианного фильтра
 */
public class MedianModule extends BaseInitAble implements IGuiModule
       {
           public static final String MODULENAME = "MEDIANMODULE";

//           public static final String MENU_CAPTION_MEDIAN= Enc.get("$78");
//           public static final String HINT_CAPTION_ON = MENU_CAPTION_MEDIAN;

           protected IViewControl mainmodule;
           private MedianParam param= new MedianParam();

           public MedianModule()
           {
           }

           public MedianModule(IViewControl mainmodule)
           {
               this.mainmodule = mainmodule;
           }

           public String getMenuName()
           {
               return Enc.get("$79");
           }

           public JMenu addMenu(JMenu inmenu) throws Exception
           {
               if (inmenu.getItemCount()>0)
                   inmenu.addSeparator();

               {
//                   JMenuItem moduleMenu = new JMenuItem(MENU_CAPTION_MEDIAN, KeyEvent.VK_S);
                   JMenuItem moduleMenu = new JMenuItem(Enc.get("$78"), KeyEvent.VK_S);
                   inmenu.add(moduleMenu);
                   moduleMenu.addActionListener(new ActionListener()
                   {
                       public void actionPerformed(ActionEvent e)
                       {
                               paramters();
                       }
                   });
               }


               return inmenu;
           }

           public JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception
           {
//               MenuElement[] subElements = inmenu.getSubElements();
//               if (subElements !=null && subElements.length>0)
//                   inmenu.addSeparator();

               return inmenu;
           }

           public void registerListeners(JComponent component) throws Exception
           {

           }

           public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception

           {

               return systemtoolbar;
           }


           BufferedImage ress;
           public void paintMe(Graphics g)
           {
               if (ress!=null)
                g.drawImage(ress,0,0,new ImageObserver()
                {
                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return false;
                    }
                });
           }

           public void registerNameConverter(INameConverter nameConverter, IFactory<INameConverter> factory)
           {
           }

           public void unload()
           {
           }

           public String getModuleName()
           {
               return MODULENAME;
           }

           public IAnswerBean execute(ICommandBean cmd)
           {
               throw new UnsupportedOperationException();
           }


           public Object init(Object obj) throws Exception
           {
               IParam attr=(IParam)obj;
               if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME))
                   this.mainmodule = (IViewControl) attr.getValue();
               return null;
           }


           protected void resetFilter()
           {
               try {
                   ress=null;
                   mainmodule.refresh(null);
               } catch (Exception e)
               {//
               }
           }

           protected void viewFilter(MedianParam param)
           {
               try
               {
                   IProjContext proj = mainmodule.getProjContext();

                   INodeStorage mainstor = (INodeStorage) proj.getStorage();
                   Collection<INodeStorage> childStorages = mainstor.getChildStorages();

                   IRasterContainer stor=null;
                   for (INodeStorage st : childStorages) {
                       if (st instanceof IRasterContainer)
                           stor=(IRasterContainer)st;
                   }

                   if (stor==null)
                       throw new Exception("Can't find storage for raster");

                   Pair<BindStruct,Integer> currentStruct = stor.getCurrentStruct();
                   if (currentStruct!=null)
                   {
                       BindStruct bindStruct = currentStruct.getKey();
                       if (bindStruct.flnames.length>0)
                       {

                           IViewPort vp = mainmodule.getViewPort();
                           Point pt=vp.getDrawSize();

                           Point.Double dst0 = vp.getCopyConverter().getDstPointByPointD(new MPoint(0, 0));

                           dst0.x= Math.max(0,dst0.x);
                           dst0.y= Math.max(0,dst0.y);

                           Point.Double dst1 = vp.getCopyConverter().getDstPointByPointD(new MPoint(pt));

                           String filename=bindStruct.pictdir + "/" + bindStruct.flnames[0][0];
                           String extensionName = filename.substring(filename.lastIndexOf('.') + 1);
                           FileInputStream inputStream = new FileInputStream(filename);
                           PartialImageReader pr = new PartialImageReader(inputStream,extensionName);

                           Point pti=pr.getImageSize();
                           dst1.x=Math.min(pti.x, dst1.x);
                           dst1.y=Math.min(pti.y, dst1.y);


                           int width = (int) (dst1.getX() - dst0.getX());
                           int height = (int) (dst1.getY() - dst0.getY());

                           if (width>0 && height>0)
                           {
                               BufferedImage bufferedImage =pr.getImageByRectangle(new Rectangle((int)dst0.getX(),(int)dst0.getY(), width, height));
                               Color selColor = new Color(param.rColor);
                               ress = MedianProcessor.processMedian(bufferedImage, param.mSize, selColor, param.colorDist, selColor, param.borderType, null);
                               mainmodule.refresh(null);
                           }
                       }
                   }

               } catch (Exception e)
               {
                   e.printStackTrace();
               }
           }


           protected void paramters()
           {
               try {
                   //param.rColor= getSelectedRGB();
                   MedianParam param=(MedianParam)this.param.clone();
                   ParametersForm frm=new ParametersForm(this);
                   frm.setData(param);
                   frm.pack();
                   frm.setVisible(true);
                   if (frm.isOkStatus() && frm.getData(param))
                   {
                       this.param=param;
                   }
               }
               catch (CloneNotSupportedException e)
               {
                   //
               }
           }


       }
