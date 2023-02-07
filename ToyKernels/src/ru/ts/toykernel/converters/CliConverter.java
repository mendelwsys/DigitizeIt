package ru.ts.toykernel.converters;

import ru.ts.toykernel.converters.providers.IConvProvider;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;

/**
 * Клиентский конвертер
 * ru.ts.toykernel.converters.CliConverter
 */
public class CliConverter extends CrdConverterFactory.ShiftConverter implements IProjConverter
{
	private IConvProvider provider;

	public CliConverter()
	{
	}

	public ILinearConverter createCopyConverter() //TODO Здесь возникает вопрос по синхронизованности провайдреа IConvProvider provider, 
	// поскольку обращение может происходит из различных клиентских потоков
	{
		return new CliConverter(this, provider);
	}

	protected CliConverter(CrdConverterFactory.ShiftConverter converter, IConvProvider provider)
	{
		super(converter);
		this.provider = provider;
	}

	public CliConverter(MPoint pnt, IConvProvider provider)
	{
		super(pnt);
		this.provider = provider;
	}

	public void setByConverter(IProjConverter converter) throws Exception
	{
		this.getAsShiftConverter().setBindP0(converter.getAsShiftConverter().getBindP0());
	}

	public IRotateConverter getAsRotateConverter()
	{
		throw new UnsupportedOperationException();
	}

	public IScaledConverter getAsScaledConverter()
	{
		throw new UnsupportedOperationException();
	}


	private IScaledConverterCtrl scalconv= new IScaledConverterCtrl()
	{
			private MPoint scale;//Текущий масштаб

			public void recalcScale(MRect proj_rect, int[] szXY) throws Exception
			{
				double h=proj_rect.getHeight();
				if (h==0) h=1;
				double w=proj_rect.getWidth();
				if (w==0) w=1;
				double dscale= Math.min(szXY[0]/h,szXY[1]/w);
				if (dscale<1)
				{
					System.out.println("Error of calculate scale");
					return;
				}

				setInitScale();

				MPoint old_scale=new MPoint(this.scale);
				MPoint scale=increaseMap(dscale);

				currentP0.x=(scale.x/old_scale.x)*proj_rect.p1.x;
				currentP0.y=(scale.y/old_scale.y)*proj_rect.p1.y;
			}

			public MPoint increaseMap(double dS) throws Exception
			{

				setInitScale();

				MPoint viewsz=getViewSize();
				MPoint dp=null;
				if (viewsz!=null)
					dp=new MPoint(viewsz.x/2,viewsz.y/2);

				MPoint scale=provider.multScale(dS);

				currentP0.x=(scale.x/this.scale.x)*(currentP0.x);
				currentP0.y=(scale.y/this.scale.y)*(currentP0.y);
				if (dp!=null)
				{
					currentP0.x-=dp.x-(scale.x/this.scale.x)*(dp.x);
					currentP0.y-=dp.y-(scale.y/this.scale.y)*(dp.y);

				}

				this.scale=scale;
				return scale;
			}

			public MPoint increaseMap(MPoint dS, boolean evently) throws Exception
			{
				throw  new UnsupportedOperationException();
			}


			private void setInitScale() throws Exception
			{
				if (scale==null)
					scale=provider.multScale(1.0);
			}

			public MPoint decreaseMap(double dS) throws Exception
			{
				return increaseMap(1/dS);
			}

			public MPoint getUnitsOnPixel() throws Exception
			{
				setInitScale();
				return new MPoint(1/scale.x,1/scale.y);
			}

			public void reSetInitScale()
			{
				throw new UnsupportedOperationException();
			}

		public void setScale(MPoint newscale)
		{
			scale=new MPoint(scale);
		}

		public MPoint getScale()
		{
			return new MPoint(scale);
		}

		public void setScaleBean(IScaleDescBean scalebean) throws Exception
		{
			throw new UnsupportedOperationException();
		}

	};
	public IScaledConverterCtrl getAsScaledConverterCtrl()
	{
		return scalconv;
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.CONVPROVIDER_TAGNAME))
			provider=(IConvProvider)attr.getValue();
		else
			return super.init(obj);
		return null;
	}

}
