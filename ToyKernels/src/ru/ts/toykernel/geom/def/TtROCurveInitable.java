package ru.ts.toykernel.geom.def;

import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.toykernel.attrs.IAttrsPoolImpl;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.factory.IInitAble;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.gisutils.algs.common.MPoint;

import java.util.List;
import java.util.LinkedList;

/**
 * Тестовая имплементация инициализируемого объекта
 * ru.ts.toykernel.geom.def.TtROCurveInitable
 */
public class TtROCurveInitable
		extends ROCurve implements IInitAble
{

	public TtROCurveInitable()
	{
		super(null);
		attrsPool=new IAttrsPoolImpl();
	}

	public TtROCurveInitable(INameConverter storNm2CodeNm)
	{
		super(storNm2CodeNm);
	}

	public TtROCurveInitable(String geotype, String curveId, IAttrsPool attrsPool, INameConverter storNm2CodeNm)
	{
		super(geotype, curveId, attrsPool, storNm2CodeNm);
		rebuildGisValume();
	}


	protected String ObjName;
	protected IXMLObjectDesc desc;

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}

	public String getObjName()
	{
		return ObjName;
	}

	public Object[] init(Object... objs) throws Exception
	{
		for (Object obj : objs)
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				ObjName = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else
				init(obj);
		}

		attrsPool.put(m_sCurveID,objattr);

		pArrX = new double[lp.size()][];
		pArrY = new double[lp.size()][];

		for (int i = 0; i < lp.size(); i++)
		{
			List<MPoint> mPoints = lp.get(i);
			pArrX[i]= new double[mPoints.size()];
			pArrY[i]= new double[mPoints.size()];
			for (int j = 0; j < mPoints.size(); j++)
			{
				MPoint mPoint = mPoints.get(j);
				pArrX[i][j]=mPoint.getX();
				pArrY[i][j]=mPoint.getY();
			}
		}
		rebuildGisValume();
		return null;
	}

	private List<List<MPoint>> lp =new LinkedList<List<MPoint>>();
	private int currseg=0;
	protected DefaultAttrsImpl objattr=new DefaultAttrsImpl();

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;

		if (attr.getName().equalsIgnoreCase(KernelConst.OBJTYPE))
		{
			geotype = (String) attr.getValue();

			br:
			{
				String[] tt = KernelConst.types;
				for (String s : tt)
					if (geotype.equalsIgnoreCase(s))
					{
						geotype=s;
						break br;
					}
				throw new Exception("Unknown type:"+geotype);
			}


		}
		else if (attr.getName().equalsIgnoreCase(KernelConst.OBJECTID))
			m_sCurveID = (String) attr.getValue();
		else  if (attr.getName().equalsIgnoreCase(KernelConst.NAMECONVERTER_TAGNAME))
			storNm2CodeNm=(INameConverter)attr.getValue();
		else  if (attr.getName().equalsIgnoreCase("ADDSEG"))
		{
			lp.add(new LinkedList<MPoint>());
			currseg++;
		}
		else  if (attr.getName().equalsIgnoreCase("PREVSEG"))
		{
			if (currseg>0)
				currseg--;
		}
		else if (attr.getName().equalsIgnoreCase(KernelConst.CRDPOINT))
		{
			String pntstr=(String)attr.getValue();
			String[] sbindp = pntstr.split(" ");
			while (lp.size()<=currseg)
				lp.add(new LinkedList<MPoint>());
			List<MPoint> currlist=lp.get(currseg);
			currlist.add(new MPoint(Double.parseDouble(sbindp[0]), Double.parseDouble(sbindp[1])));
		}
		else
			objattr.put(attr.getName(),new DefAttrImpl(attr.getName(),attr.getValue()));
		return null;
	}
}
