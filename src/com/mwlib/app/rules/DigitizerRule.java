package com.mwlib.app.rules;

import com.mwlib.app.painters.DigitizerPainter;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.rules.def.CnStyleRuleImpl;
import ru.ts.toykernel.geom.IBaseGisObject;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 03.01.14
 * Time: 18:25
 * com.mwlib.app.rules.DigitizerRule
 */
public class DigitizerRule extends CnStyleRuleImpl
{

    protected void otherPainter(Paint paintfill, Integer linecolor, Stroke stroke, Integer radPnt, ILayer layer, IBaseGisObject obj, Integer composite) throws Exception
    {
        getInstancePainter(paintersClass.get(obj.getGeotype()), obj.getGeotype(), DigitizerPainter.class);
        setPainterParams(cacheParamPainter, paintfill, linecolor, stroke, radPnt, composite);
    }

}
