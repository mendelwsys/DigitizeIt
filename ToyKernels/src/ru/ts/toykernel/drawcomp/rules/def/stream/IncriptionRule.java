package ru.ts.toykernel.drawcomp.rules.def.stream;

import ru.ts.toykernel.drawcomp.rules.def.stream.InscriptionRule;
import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: VMendelevich
 * Date: 29.02.2012
 * Time: 16:37:54
 * Для поддержки старых проектов
 */

public class IncriptionRule extends InscriptionRule
{
	public IncriptionRule()
	{
	}

	public IncriptionRule(CommonStyle defStyle, Font ft)
	{
		super(defStyle, ft);
	}
}
