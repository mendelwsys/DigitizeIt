package ru.ts.toykernel.drawcomp.rules.def.stream;

import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.factory.DefIFactory;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 05.03.2009
 * Time: 19:22:42
 * Default common rule factory factory (Get default common style rule)
 */
public class CnStyleRuleFactory extends DefIFactory<IDrawObjRule>
{
	public IDrawObjRule createByTypeName(String typeRule) throws Exception
	{
		if (typeRule.equals(CnSerialDrawRule.RULETYPENAME))
			return new CnSerialDrawRule();
		IDrawObjRule res = super.createByTypeName(typeRule);
		if (res==null)
			return new CnSerialDrawRule();
		return res;
	}
}
