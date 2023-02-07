package com.mwlib.app.plugins.median;

import reclass.IProgressObserver;
import reclass.MedianFilter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.04.14
 * Time: 21:06
 * Небольшой класс обеспечивает собственно работу с медианным фильтром
 */
public class MedianProcessor
{
    /**
     * Processes full image with median filter of designated size
     *
     * @return {@link java.awt.image.BufferedImage}
     */
    public static BufferedImage processMedian( BufferedImage bi, int mSize, Color selColor, double colorDist, Color outColor, int borderType, IProgressObserver prog )
    {
        if ( bi == null )
        {
            throw new NullPointerException( "Expected BufferedImage == null" );
        }

        if ( mSize < 3 || ( ( mSize & 1 ) == 0 ) )
        {
            throw new IllegalArgumentException( "Expected median apperture is < 3 or even (must be odd)" );
        }
        bi = processMedianFilter( bi, mSize, selColor, colorDist, outColor, borderType, prog );
        return bi;
    }


    /**
     * Apply median filter onto designated {@link BufferedImage}
     *
     * @param src
     * @param medianSize
     * @return newly created 1-bit {@link BufferedImage} with bits set in1 (ones) as result and 0 (xero) as filtered out
     */
    public static BufferedImage processMedianFilter( BufferedImage src, int medianSize, Color selColor, double colorDist, Color outColor, int borderType, IProgressObserver prog )
    {
        MedianFilter mflt = new MedianFilter( medianSize );
        return mflt.filterSelectedColors( src, selColor, colorDist, outColor, borderType/*IConsts.BORDER_ZERO*/, prog );
    }

}
