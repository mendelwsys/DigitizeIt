package ru.ts.toykernel.shared;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.geom.IBaseGisObject;

import java.awt.*;

/**
 * Some frequency used operations on gis objects
 */
public class DefGisOperations
{
	public Point[] getCentralDrawPoints(ILinearConverter converter, IBaseGisObject gisobj)
	{
		MPoint[] lnpnt= getCentralPoints(gisobj);
		Point[] rv=new Point[lnpnt.length];
		for (int i = 0; i < lnpnt.length; i++)
			rv[i]=converter.getDstPointByPoint(lnpnt[i]);
		return rv;
	}
	
	public MPoint[] getCentralPoints(IBaseGisObject gisobj)
	{
		double rawgeom[][][]=gisobj.getRawGeometry();

		MPoint[] rv=new MPoint[rawgeom[0].length];

		for (int i=0;i<rawgeom[0].length;i++)
		{
			rv[i]=new MPoint();
			for (int j=0;j<rawgeom[0][i].length;j++)
			{
				MPoint res = new MPoint(rawgeom[0][i][j], rawgeom[1][i][j]);
				rv[i].x+=res.x;
				rv[i].y+=res.y;
			}
			rv[i].x/=rawgeom[0][i].length;
			rv[i].y/=rawgeom[0][i].length;
		}

		return rv;
	}
}
