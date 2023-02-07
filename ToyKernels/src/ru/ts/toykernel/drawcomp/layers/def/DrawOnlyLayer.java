package ru.ts.toykernel.drawcomp.layers.def;

import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.filters.DefMBBFilter;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.IPainter;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.utils.data.Pair;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 24.02.2009
 * Time: 15:42:31
 * TODO Тестовый слой проекта переделать процедуру рисования IDrawObjRule и рисование типов объекта
 * ru.ts.toykernel.drawcomp.layers.def.DrawOnlyLayer
 */
public class DrawOnlyLayer implements ILayer
{
	protected IBaseStorage basestorage;//Интерфейс достпу к объектам рисования (до фильтрации)

	protected List<IBaseFilter> filters;//фильтр слоя
	protected IBaseStorage storage;//Интерфейс достпу к объектам рисования (множество объектов после наложения фильтров слоя)

	protected IAttrs attrs;//атрибуты слоя
	protected IDrawObjRule drawObjRule;//правило рисования
	public static final String TYPENAME = "DrawOnlyLayer";
	protected IXMLObjectDesc desc;
	public List<Pair<ILayer,List<IParam>>> addlayers; //Мотивация использовать именно Layer для границ -

	/*
		   1. Для установки границ используется базовая операция createBoundShape
		   2. Используется стандартный метод Shape, этот шейп генерируется Layer
		   и устанавливает его в контекст во время рисования.
		   3.Можно объекты образующие границы хранить отдельно от объектов
		   образующих

	   */


	//Аттрибуты слоя существуют в рамках проекта, это собственно аттрибуты группы объектов, которые относятся ко все группе в целом
	//В частности отображать или не отображать группу имя группы
	//TODO Возможно в зависимости от атрибутов группы можно потом ввести правила отображения если это будет необходимо
	public DrawOnlyLayer()
	{
	}

	public DrawOnlyLayer(IBaseStorage basestorage,IBaseFilter filter, IAttrs attrs, IDrawObjRule drawObjRule) throws Exception
	{
		List<IBaseFilter> filters=new LinkedList<IBaseFilter>();
		if (filter!=null)
			filters.add(filter);
		initLayer(basestorage, filters, attrs, drawObjRule);
	}

	public DrawOnlyLayer(IBaseStorage basestorage, List<IBaseFilter> filters, IAttrs attrs, IDrawObjRule drawObjRule) throws Exception
	{
		initLayer(basestorage, filters, attrs, drawObjRule);
	}

	public void addFilters(List<IBaseFilter> filters) throws Exception
	{
		for (IBaseFilter filter : filters)
		{
			this.filters.add(filter);
			this.storage = storage.filter(filter);
		}
	}

	public void setFilters(List<IBaseFilter> filters) throws Exception
	{
		this.filters=filters;
        if (basestorage!=null)
        {
            if (filters==null || filters.size()==0)
                this.storage = basestorage.filter(null);
            else
            {
                this.storage = basestorage.filter(filters.get(0));
                for (int i = 1; i < filters.size(); i++)
                    this.storage = storage.filter(filters.get(i));
            }
        }
	}

	public List<IBaseFilter> getFilters()
	{
		  return filters;
	}

	public IBaseStorage getStorage()
	{
		return storage;
	}


	public void setBaseStorage(IBaseStorage storage)
	{
		this.basestorage = storage;
		filters=null;
	}


	public void initLayer(IBaseStorage basestorage, List<IBaseFilter> filters, IAttrs attrs, IDrawObjRule drawObjRule)
			throws Exception
	{
		this.filters=filters;
		this.basestorage=basestorage;
		this.drawObjRule = drawObjRule;
		this.attrs = attrs;
		setFilters(filters);
	}

	public IDrawObjRule getDrawRule()
	{
		return drawObjRule;
	}

	public void setDrawRule(IDrawObjRule drawObjRule)
	{
		this.drawObjRule= drawObjRule;
	}

	public void setLrAttrs(IAttrs attrs)
	{
		this.attrs=attrs;
	}

	public IAttrs getLrAttrs()
	{
		return attrs;
	}

	public void setVisible(Boolean visible)
	{
		IDefAttr rv = getLrAttrs().get(KernelConst.LAYER_VISIBLE);
		rv.setValue(visible);
	}

	public boolean isVisible()
	{
		IDefAttr rv = getLrAttrs().get(KernelConst.LAYER_VISIBLE);
		return (Boolean) rv.getValue();
	}

	protected Iterator<IBaseGisObject> getVisibleObjects(Graphics graphics, ILinearConverter converter,
										 Point drawSize) throws Exception
	{

		if (drawSize !=null)
		{
			MRect drawrect = new MRect(new MPoint(),
					new MPoint(drawSize.x, drawSize.y));
			MRect proj_rect = converter.getRectByDstRect(drawrect,null);
			return storage.filterObjs(new DefMBBFilter(proj_rect));
		}
		else
			return storage.getAllObjects();
	}


	public MRect getObjectDrawRect(IBaseGisObject drawMe,
			Graphics graphics, IViewPort viewPort) throws Exception
	{
		IProjConverter iProjConverter = viewPort.getCopyConverter();
		IPainter painter = drawObjRule.cretatePainter(graphics, this, drawMe);
		if (painter != null)
			return painter.getDrawRect(graphics, drawMe, iProjConverter);

		MRect mbb=drawMe.getMBB(null);
		return iProjConverter.getDstRectByRect(mbb);
	}

	public Iterator<IBaseGisObject> getVisibleObjects(Graphics graphics,IViewPort viewPort)
			throws Exception
	{
		IProjConverter converter = (IProjConverter) viewPort.getCopyConverter();
		return getVisibleObjects(graphics,converter,viewPort.getDrawSize());
	}

	public int[] paintLayerObject(IBaseGisObject drawMe,
			Graphics graphics, IViewPort viewPort) throws Exception
	{
		int[] rv = new int[]{0, 0, 0};
		IProjConverter iProjConverter = (IProjConverter)viewPort.getCopyConverter();
		return drawObject(graphics, iProjConverter, drawMe,rv, viewPort.getDrawSize());
	}

	/**
	 * Прорисовать слой
	 *
	 * @param graphics  -графический контекст в котором надо рисовать, если он null значит
	 *                  сгенерировать BufferedImage и рисовать в нем
	 */
	public int[] paintLayer(
			Graphics graphics, IViewPort viewPort) throws Exception
	{

		List<Pair<ILayer,Object[]>> oldsparam=new LinkedList<Pair<ILayer,Object[]>>();
		Shape shp = graphics.getClip();

		try
		{
			int[] rv = new int[]{0, 0, 0};
			if (graphics == null)
				return rv;

			if (!isVisible())
				return rv;

			IProjConverter iProjConverter = (IProjConverter)viewPort.getCopyConverter();
			if (drawObjRule==null || !drawObjRule.isVisibleLayer(this, iProjConverter))
				return rv;

			Point drawSize = viewPort.getDrawSize();
			Iterator<IBaseGisObject>  baseObjects = getVisibleObjects(graphics, iProjConverter, drawSize);

			if (baseObjects.hasNext() && addlayers!=null)
			{
				Path2D p2d=null;
				for (Pair<ILayer, List<IParam>> addlayer : addlayers)
				{
					Object[] oldpar=null;
					if (addlayer.second!=null)
					{
						oldpar=addlayer.first.init(addlayer.second.toArray(new Object[addlayer.second.size()]));
						//save paramters before setting bound by layer
						oldsparam.add(new Pair<ILayer,Object[]>(addlayer.first,oldpar));
					}

					if (addlayer.first.getLrAttrs().get(KernelConst.LAYER_BOUND)!=null)
					{
						Shape bnd=addlayer.first.setBounds(graphics,iProjConverter);
						if (p2d==null)
							p2d=new Path2D.Double(Path2D.WIND_EVEN_ODD); //TODO Устанавовить параметр характеризующий клипп???
						p2d.append(bnd,false); //Кажды слой описывает законченную границу, поэтому слои не соеденены между собой.
					}
				}

				if (p2d!=null)
				{
//					{ //TODO Debug code
//						Color color=graphics.getColor();
//						graphics.setColor(new Color(0xFF0000FF,true));
//						((Graphics2D)graphics).draw(p2d);
//						graphics.setColor(color);
//					}
					((Graphics2D)graphics).clip(p2d);
				}
			}

			while (baseObjects.hasNext())
			{
				try
				{
					IBaseGisObject drawMe = baseObjects.next();
					drawObject(graphics, iProjConverter, drawMe, rv, drawSize);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			return rv;
		}
		finally
		{
			graphics.setClip(shp);
			//restore parameters after drawing layer
			for (Pair<ILayer, Object[]> iLayerPair : oldsparam)
				iLayerPair.first.init(iLayerPair.second);
		}
	}

	public Shape setBounds(Graphics graphics, IProjConverter iProjConverter)
			throws Exception
	{
		Path2D p2d=new Path2D.Double();
		Iterator<IBaseGisObject>  baseObjects = storage.getAllObjects();

		while (baseObjects.hasNext())
		{
			IBaseGisObject boundByMe = baseObjects.next();
			if (boundByMe != null)
			{
				IPainter painter = drawObjRule.cretatePainter(graphics, this, boundByMe);
				if (painter != null)
				{
					Shape shp = painter.createShape(boundByMe, iProjConverter);
					if (shp!=null)
						p2d.append(shp,true); //Все шейпы слоя описывают законченый элемент границы, поэтому соеденены
				}
			}
		}
		//p2d.closePath();
		return p2d;
	}

	protected int[] drawObject(Graphics graphics, IProjConverter iProjConverter, IBaseGisObject drawMe, int[] rv, Point drawSize)
			throws Exception
	{
		if (drawMe != null)
		{
			IPainter painter = drawObjRule.cretatePainter(graphics, this, drawMe);
			if (painter != null)
			{
				int[] rvv = painter.paint(graphics, drawMe, iProjConverter, drawSize);
				for (int i = 0; i < rv.length; i++)
					rv[i] += rvv[i];
			}
		}
		return rv;
	}

	protected MRect layer_rect=null;
	protected long sttm;

	/**
	 * Получить квадратную границу слоя которая не менее чем переданная
	 * @param proj_rect - external project rect
	 * @return граница слоя
	 */
	public MRect getMBBLayer(MRect proj_rect) throws Exception
	{
		if (layer_rect==null || sttm!=storage.getLastModified())
		{
			sttm=storage.getLastModified();
			Iterator<IBaseGisObject> gset = storage.getAllObjects();
			while (gset.hasNext())
			{
				IBaseGisObject curve = gset.next();
				proj_rect = curve.getMBB(proj_rect);
			}
			return layer_rect=proj_rect;
		}
		else
			return layer_rect.getMBB(proj_rect);
	}

	public String getObjName()
	{
		IDefAttr iDefAttr = getLrAttrs().get(KernelConst.LAYER_NAME);
		if (iDefAttr!=null)
			return (String)iDefAttr.getValue();
		return "";
	}

	public Object[] init(Object... objs) throws Exception
	{
		List oobjs=new LinkedList();
        if (getLrAttrs()==null)
		    setLrAttrs(new DefaultAttrsImpl());
		for (Object obj : objs)
		{
			Object oobj=init(obj);
			if (oobj!=null)
				oobjs.add(oobjs);
		}
		setFilters(filters);
		return oobjs.toArray(new Object[oobjs.size()]);
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
			getLrAttrs().put(KernelConst.LAYER_NAME,attr);
		else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
			this.desc=(IXMLObjectDesc)attr.getValue();
        else if (attr.getName().equalsIgnoreCase(KernelConst.LAYER_VISIBLE))
		{
			IDefAttr oattr = getLrAttrs().get(KernelConst.LAYER_VISIBLE);
			attr.setValue(Boolean.valueOf((String) attr.getValue()));
			getLrAttrs().put(KernelConst.LAYER_VISIBLE, attr);
			if (oattr!=null)
				return oattr;
		}
		else if (attr.getName().equalsIgnoreCase(KernelConst.STORAGE_TAGNAME))
			this.basestorage = (IBaseStorage) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.FILTER_TAGNAME))
		{
			if (this.filters == null)
				this.filters =new LinkedList<IBaseFilter>();
			this.filters.add((IBaseFilter)attr.getValue());
		}
		else if (attr.getName().equalsIgnoreCase(KernelConst.RULE_TAGNAME))
			this.drawObjRule= (IDrawObjRule) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.LAYER_BOUND))
		{
			IDefAttr oattr = getLrAttrs().get(KernelConst.LAYER_BOUND);
			getLrAttrs().put(KernelConst.LAYER_BOUND, attr);
			if (oattr!=null)
				return oattr;
		}
		else if (attr.getName().equalsIgnoreCase(KernelConst.LAYER_TAGNAME))
		{
			if (addlayers == null)
				addlayers= new LinkedList();
			ILayer layer = (ILayer) attr.getValue();
			addlayers.add(new Pair<ILayer,List<IParam>>(layer,attr.getAttributes()));
		}
        else
            getLrAttrs().put(attr.getName(), attr);
		return null;
	}


	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}
}
