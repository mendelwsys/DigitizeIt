package com.mwlib.app.layers;

import ru.ts.toykernel.drawcomp.layers.def.DrawOnlyLayer;
import ru.ts.toykernel.gui.IViewPort;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 03.01.14
 * Time: 21:07
 * com.mwlib.app.layers.DigitizerLayer
 */
public class DigitizerLayer extends DrawOnlyLayer
{
    public static final String NICKNAME = "LAYER_NAME";

    public int[] paintLayer(
            Graphics graphics, IViewPort viewPort) throws Exception
    {

        Point sz = viewPort.getDrawSize();
        BufferedImage rv=new BufferedImage(sz.x,sz.y, BufferedImage.TYPE_INT_ARGB);
        int[] res = super.paintLayer(rv.getGraphics(), viewPort);

        for (int re : res) {
            if (re > 0) {
                graphics.drawImage(rv, 0, 0, new ImageObserver() {
                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return false;
                    }
                });
                return res;
            }
        }
        return new int[]{0,0,0,0};
    }

}
