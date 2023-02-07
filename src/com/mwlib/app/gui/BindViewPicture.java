package com.mwlib.app.gui;

import ru.ts.factory.IParam;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.toykernel.gui.panels.ViewPicturePanel2;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 06.04.14
 * Time: 13:51
 * Показывает связанные панели (com.mwlib.app.gui.BindViewPicture)
 */
public class BindViewPicture
    		extends ViewPicturePanel2
{
    private IViewControl viewControl;

    public void refresh(Object arg)
    {
        super.refresh(arg);
        try {
            if (viewControl!=null)
            {
                IViewPort viewPort = viewControl.getViewPort();
                IProjConverter converterRef = viewPort.getCopyConverter();
                converterRef.getAsShiftConverter().setBindP0(converter.getAsShiftConverter().getBindP0());
                viewPort.setCopyConverter(converterRef);
                viewControl.refresh(arg);
            }
        } catch (Exception e)
        {//
        }
    }

    public void shiftPictureXY(int[] dXdY)
    {
        super.shiftPictureXY(dXdY);
        if (viewControl!=null)
        {
            viewControl.shiftPictureXY(dXdY);
            JComponent component = viewControl.getComponent();
            if (component!=null)
                component.repaint();
        }
    }
    public Object init(Object obj) throws Exception
    {
        IParam attr=(IParam)obj;
        if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME) && attr.getValue() instanceof IViewControl)
            viewControl=(IViewControl)attr.getValue();
        else
            return super.init(obj);
        return null;
    }

}
