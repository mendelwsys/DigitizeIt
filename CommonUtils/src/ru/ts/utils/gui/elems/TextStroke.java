package ru.ts.utils.gui.elems;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/*
 * Changes: yugl, 04.05.2008,
 * doing refactoring, added commentaries, changed dependencies
 */
public class TextStroke implements Stroke {
    private String text;

    private boolean stretchToFit = false;

    private int numberOfStrings;

    private boolean repeat = false;

    private AffineTransform t = new AffineTransform();

    GlyphVector gv = null;

    private int align = ALIGN_START;

    FontRenderContext frc = null;

    float space = 1f;

    /**
     * Align the text at the start of the path.
     */
    public static final int ALIGN_START = 0;

    /**
     * Align the text at the middle of the path.
     */
    public static final int ALIGN_MIDDLE = 1;

    /**
     * Align the text at the end of the path.
     */
    public static final int ALIGN_END = 2;

    private static final float FLATNESS = 1;

    public TextStroke(GlyphVector gv, FontRenderContext frc, String text) {
        this(gv, frc, ALIGN_START, 1f, 1, text);
    }

    public TextStroke(GlyphVector gv, FontRenderContext frc, int align,
            float space, int numberOfStrings, String text) {

        this.gv = gv;
        this.numberOfStrings = numberOfStrings;
        this.frc = frc;
        if (numberOfStrings > 1)
            this.align = ALIGN_START;
        else
            this.align = align;
        this.text = text;
        this.space = space;
    }

    public Shape createStrokedShape(Shape shape) {
        GeneralPath newPath = new GeneralPath();
        shape = getTrasformedShape(shape);
        double pathLength = measurePathLength(shape);
        double glyphsLength = gv.getLogicalBounds().getWidth();
        int length = gv.getNumGlyphs();
        if (this.numberOfStrings > 1) {
            formnewGV(pathLength, glyphsLength, length);
            glyphsLength = gv.getLogicalBounds().getWidth();
            length = gv.getNumGlyphs();
//            System.out.println("**********************");
        }
        if (shape == null || gv == null || gv.getNumGlyphs() == 0 ||

        glyphsLength == 0f) {
            return newPath;
        }

        float currentPosition = 0f;

        if (align == ALIGN_END) {
            currentPosition += (pathLength - (glyphsLength + space
                    * (length - 1)));
        } else if (align == ALIGN_MIDDLE) {
            currentPosition += (pathLength - (glyphsLength + space
                    * (length - 1))) / 2;
        }

        GeneralPath result = new GeneralPath();
        PathIterator it = new FlatteningPathIterator(shape
                .getPathIterator(null), FLATNESS);
        double points[] = new double[6];
        double moveX = 0, moveY = 0;
        double lastX = 0, lastY = 0;
        double thisX = 0, thisY = 0;
        int type = 0;
        boolean first = false;
        double next = 0;
        int currentChar = 0;
        double curdist = 0.0;
        //  double lLastX = 0.0;
        // double lLastY = 0.0;
        boolean fl = false;
        if (length == 0)
            return result;

        //        double factor = stretchToFit ? measurePathLength(shape)
        //                / (float) gv.getLogicalBounds().getWidth() : 1.0f;
        double nextAdvance = 0;

        while (curdist < currentPosition && !it.isDone()) {
            type = it.currentSegment(points);
            switch (type) {
            case PathIterator.SEG_MOVETO:
                moveX = lastX = points[0];
                moveY = lastY = points[1];
                //				result.moveTo((float) moveX,(float) moveY );
                nextAdvance = gv.getGlyphMetrics(currentChar).getAdvance() / 2.0;

                break;

            case PathIterator.SEG_CLOSE:
                points[0] = moveX;
                points[1] = moveY;

            case PathIterator.SEG_LINETO:
                thisX = points[0];
                thisY = points[1];
                double dx = thisX - lastX;
                double dy = thisY - lastY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                //   first = false;
                //				lLastX=lastX;
                //				lLastY=lastY;
                lastX = thisX;
                lastY = thisY;
                fl = true;
                curdist += distance;
                break;
            }
            it.next();
        }
        //      if (fl)
        //      {
        //          lastX=lLastX;
        //          lastY=lLastY;
        //      }

        while (currentChar < length && !it.isDone()) {

            type = it.currentSegment(points);
            if (fl) {
                result.moveTo((float) lastX, (float) lastY);
                fl = false;
            }
            switch (type) {
            case PathIterator.SEG_MOVETO:
                moveX = lastX = points[0];
                moveY = lastY = points[1];
                result.moveTo((float) moveX, (float) moveY);
                first = true;
                nextAdvance =

                gv.getGlyphMetrics(currentChar).getAdvance() / 2.0;
                next = nextAdvance;
                break;

            case PathIterator.SEG_CLOSE:
                points[0] = moveX;
                points[1] = moveY;

            case PathIterator.SEG_LINETO:
                thisX = points[0];
                thisY = points[1];
                double dx = thisX - lastX;
                double dy = thisY - lastY;
                double distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance >= next) {
                    double r = 1.0f / distance;
                    double angle = Math.atan2(dy, dx);
                    while (currentChar < length && distance >= next) {

						Shape glyph = gv.getGlyphOutline(currentChar);
                        Point2D p = gv.getGlyphPosition(currentChar);
                        double px = p.getX();
                        double py = p.getY();
                        double x = lastX + next * dx * r;
                        double y = lastY + next * dy * r;
                        double advance = nextAdvance;

                        nextAdvance = gv.getGlyphMetrics(currentChar)
                                .getAdvance() / 2.0;
                        t.setToTranslation(x, y);
                        t.rotate(angle);
                        t.translate(-px - advance, -py);
                        result.append(t.createTransformedShape(glyph), false);
                        next += (advance + nextAdvance + space) /** factor */
                        ;
                        currentChar++;
                        if (repeat)
                            currentChar %= length;
                    }
                }
                next -= distance;
                first = false;
                lastX = thisX;
                lastY = thisY;
                break;
            }
            it.next();
        }

        return result;
    }

    public float measurePathLength(Shape shape) {
        PathIterator it = new FlatteningPathIterator(shape
                .getPathIterator(null), Double.MAX_VALUE);
        float points[] = new float[6];
        float moveX = 0, moveY = 0;
        float lastX = 0, lastY = 0;
        float thisX = 0, thisY = 0;
        int type = 0;
        float total = 0;

        while (!it.isDone()) {
            type = it.currentSegment(points);
            switch (type) {
            case PathIterator.SEG_MOVETO:
                moveX = lastX = points[0];
                moveY = lastY = points[1];
                break;

            case PathIterator.SEG_CLOSE:
                points[0] = moveX;
                points[1] = moveY;

            case PathIterator.SEG_LINETO:
                thisX = points[0];
                thisY = points[1];
                float dx = thisX - lastX;
                float dy = thisY - lastY;
                total += (float) Math.sqrt(dx * dx + dy * dy);
                lastX = thisX;
                lastY = thisY;
                break;
            }
            it.next();
        }

        return total;
    }

    void formnewGV(double pathlen, double gvw, int gvl) {
        double spgv = gv.getFont().getStringBounds(" ", frc).getWidth();
        String newtext = new String("");
        double chisl = -((-pathlen) + numberOfStrings * gvw + numberOfStrings
                * space * gvl - numberOfStrings * space - spgv - space);
        double nos = (chisl / (spgv + space));
        int allnumberofspaces = (int) Math.floor(nos);
        int numberofspaces = (int) Math.floor(allnumberofspaces
                / (numberOfStrings + 1));
        if (numberofspaces < 1) {
            numberOfStrings--;
            formnewGV(pathlen, gvw, gvl);
            return;
        }
        int overallsymbols = allnumberofspaces + numberOfStrings * gvl;
        int cp = 0;
        while (cp < overallsymbols) {
            for (int i = 0; i < numberofspaces; i++, cp++) {
                newtext = newtext.concat(" ");
            }
            if (cp >= overallsymbols || (cp + text.length()) >= overallsymbols)
                break;
            newtext = newtext.concat(text);
            cp += text.length();
        }

        text = null;
        text = new String(newtext);

        gv = gv.getFont().createGlyphVector(frc, text);

//        System.out.println("gvl="
//                + (gv.getLogicalBounds().getWidth() + (space * (gv
//                        .getNumGlyphs() - 1))) + " pl=" + pathlen);
        //        if ((gv.getLogicalBounds().getWidth() + (space * (gv.getNumGlyphs() - 1)))>pathlen)
        //        {
        //            numberOfStrings--;
        //            formnewGV(pathlen, gv.getLogicalBounds().getWidth(), gv.getNumGlyphs());
        //
        //        }

    }

    Shape getTrasformedShape(Shape shp) {
        PathIterator it = new FlatteningPathIterator(shp.getPathIterator(null),
                Double.MAX_VALUE);
        float points[] = new float[6];
        GeneralPath newshape = new GeneralPath();
        Point2D.Double sp = null;
        Point2D.Double lp = null;
        ArrayList rpoints = new ArrayList();

        int type;
        while (!it.isDone()) {
            type = it.currentSegment(points);
            switch (type) {
            case PathIterator.SEG_MOVETO:
                sp = new Point2D.Double(points[0], points[1]);
                rpoints.add(new Point2D.Double(points[0], points[1]));
                break;

            case PathIterator.SEG_CLOSE:

                // rpoints.add(new Point2D.Double(points[0],nodes[1]));
                break;
            case PathIterator.SEG_LINETO:
                lp = new Point2D.Double(points[0], points[1]);
                rpoints.add(new Point2D.Double(points[0], points[1]));
                break;
            }
            it.next();
        }

        if (((lp.getY() - sp.getY()) < 0 && ((lp.getX() - sp.getX()) < 0))
                || ((lp.getY() - sp.getY()) > 0 && ((lp.getX() - sp.getX()) < 0))) {

            newshape.moveTo((float) ((Point2D.Double) (rpoints.get(rpoints
                    .size() - 1))).x, (float) ((Point2D.Double) (rpoints
                    .get(rpoints.size() - 1))).y);
            for (int i = rpoints.size() - 2; i >= 0; i--)
                newshape.lineTo((float) ((Point2D.Double) (rpoints.get(i))).x,
                        (float) ((Point2D.Double) (rpoints.get(i))).y);

        } else
            return shp;
        return newshape;
    }

}