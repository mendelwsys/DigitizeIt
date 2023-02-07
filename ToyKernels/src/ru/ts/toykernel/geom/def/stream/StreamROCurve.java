package ru.ts.toykernel.geom.def.stream;

import ru.ts.toykernel.geom.def.ROCurve;
import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.storages.mem.IStreamStorageable;
import ru.ts.stream.ISerializer;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.gisutils.geometry.GisVolume;

import java.io.DataOutputStream;
import java.io.DataInputStream;

/**
 * This object can be storaged in MemStorageLr
 */
public class StreamROCurve extends ROCurve implements IStreamStorageable
{

	private ROImpl serializer;

	public void setCurveIdPrefix(String prefix)
	{
		m_sCurveID=new StringBuffer(prefix).append(getCurveId()).toString();
	}
	
	protected StreamROCurve(String geotype, String curveId, IAttrsPool attrsPool, INameConverter storNm2CodeNm)
	{
		super(geotype, curveId, attrsPool, storNm2CodeNm);
	}


	public StreamROCurve(INameConverter storNm2CodeNm)
	{
		super(storNm2CodeNm);
	}

	private StreamROCurve()
	{
		super(null);
	}
    private StreamROCurve(IAttrsPool attrsPool, INameConverter storNm2CodeNm)
    {
		super(storNm2CodeNm);
		this.attrsPool=attrsPool;
    }

	public ISerializer getSerializer(IAttrsPool pool)
	{
        if (serializer==null)
            serializer = new ROImpl(this,pool);
        return serializer;
	}

	public static class  ROImpl implements ISerializer
	{
		private ROCurve m_rocurve;
        private IAttrsPool pool;

        public ROImpl(ROCurve curve,IAttrsPool pool)
		{
			this.m_rocurve=curve;
            this.pool=pool;
        }

		public void saveToStream(DataOutputStream dos) throws Exception
		{
			StreamROCurve memGisObject=new StreamROCurve();
			m_rocurve.setInstance(memGisObject);

			{//TODO to save compatibility with old format (!!!!delete it after conversion!!!!!)
				dos.writeInt(0);
				/* get attributes group Id */
				dos.writeInt(0);
			}


			dos.writeInt(memGisObject.pArrX.length);

			for (int i = 0; i < memGisObject.pArrX.length; i++)
			{
				dos.writeInt(memGisObject.pArrX[i].length);
				for (int j = 0; j < memGisObject.pArrX[i].length; j++)
				{
					dos.writeInt(j);
					dos.writeDouble(memGisObject.pArrX[i][j]);
					dos.writeDouble(memGisObject.pArrY[i][j]);
				}
			}

			String id = memGisObject.m_sCurveID;
			if (id.contains(INodeStorage.GROUP_SEPARATOR))
				id=id.split("["+ INodeStorage.GROUP_SEPARATOR+"]")[2];
			dos.writeUTF(id);
			dos.writeUTF(memGisObject.geotype);

			dos.writeDouble(memGisObject.gisvalume.getX());
			dos.writeDouble(memGisObject.gisvalume.getY());

			dos.writeDouble(memGisObject.gisvalume.getWidth());
			dos.writeDouble(memGisObject.gisvalume.getHeight());
		}

		public void loadFromStream(DataInputStream dis) throws Exception
		{
			StreamROCurve memGisObject=new StreamROCurve(this.pool,m_rocurve.getStorNm2CodeNm());

			{//TODO to save compatibility with old format (!!!!delete it after conversion!!!!!)
				/* get OIC as an integer */
				int _OIC = dis.readInt();
				/* get attributes group Id */
				final int attrId = dis.readInt();
			}

			int arrSize = dis.readInt();

			memGisObject.pArrX=new double[arrSize][];
			memGisObject.pArrY=new double[arrSize][];

			int i=0;
			while (arrSize > 0)
			{
				int j=0;
				int elemSize = dis.readInt();
				memGisObject.pArrX[i]=new double[elemSize];
				memGisObject.pArrY[i]=new double[elemSize];
				while (elemSize > 0)
				{
					int key = dis.readInt();
					memGisObject.pArrX[i][j]=dis.readDouble();
					memGisObject.pArrY[i][j]=dis.readDouble();
					if (key!=j)
						System.out.println("key:"+key+" j:"+j);
					j++;
					elemSize--;
				}
				i++;
				arrSize--;
			}

			memGisObject.m_sCurveID = dis.readUTF();
			memGisObject.geotype = dis.readUTF();

			double x = dis.readDouble();
			double y = dis.readDouble();
			double w = dis.readDouble();
			double h = dis.readDouble();
			memGisObject.gisvalume = new GisVolume(x, y, w, h);

            memGisObject.setInstance(m_rocurve);
		}
	}
}
