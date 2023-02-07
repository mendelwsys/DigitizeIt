/**
 * Created 29.07.2008 15:24:56 by Syg for the "MapRuler" project
 */
package ru.ts.gisutils.legend;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.mwlib.utils.Enc;
import ru.ts.utils.Files;
import ru.ts.utils.Text;

/**
 * <pre>
 * There are follow conceptions in the realization of the ruler drawing:
 * 
 * Terminology:
 * Ruler - the whole drawing for scale displaying
 * Smaller unit - the base unit to draw onto a ruler bar, 
 * 			20 such units in width and 2 in height put together 
 * 			the whole ruler.
 * Bigger unit - the unit containing 5 smaller ones in width and 2 in height, is marked
 * 			by vertical touches and is signed by value of its length in centimetres, metres
 * 			or kilometres.
 * </pre>
 * 
 * @author Syg
 * 
 */
public class MapRuler implements IMapRuler
{

	/**
	 * @author Syg
	 * 
	 */
	public class RectI
	{
		int	left, top, right, bottom;

		RectI( int x, int y, int w, int h )
		{
			left = x;
			top = y;
			right = x + w;
			bottom = y + h;
		}

		RectI( RectI rect )
		{
			left = rect.left;
			bottom = rect.bottom;
			right = rect.right;
			top = rect.top;
		}

		int width()
		{
			return right - left;
		}

		int height()
		{
			return bottom - top;
		}

		void offset( int dx, int dy )
		{
			left += dx;
			right += dx;
			top += dy;
			bottom += dy;
		}
	}

	/**
	 * World unit to device unit scale
	 */
	private float	     _ratio;

	/* world unit length in meters */
	private float	     _unit_in_meters;

	/**
	 * ruler print unit (the smallest rectangle printable) in draw units.
	 * Default units are pixels (if draw on a bitmap)
	 */
	private int	         _ruler_cell_width;

	/**
	 * ruler chessboard unit height
	 */
	private int	         _ruler_unit_height;

	/* length of ruler cell (smallest unit) */
	private int	         _cell_in_dev;

	/* ruler indentation from a right screen border */
	private int	         _right_indent;

	/**
	 * how far bottom of ruler main body is from bottom of graphics
	 */
	private int	         _bottom_indent;

	/**
	 * how far bottom of text is from top of ruler
	 */
	private int	         _txt_bottom_indent;

	/**
	 * Height of the ruler main body
	 */
	private int	         _height;

	/**
	 * How long is a touch
	 */
	private int	         _touch_height;

	/**
	 * Defines if text is drawing on to a white background (<code>true</code>)
	 * or is transparent (<code>false</code>)
	 */
	private boolean	     _draw_text_bg;

	/**
	 * big unit bounding 5 cells in device coordinates on width and 2 on height
	 */
	private int	         _scale_seg_in_dev;

	private int	         _font_size;

	public static String	STR_CENTIMETER	= Enc.get("$28");

	public static String	STR_METER	   = Enc.get("$29");

	public static String	STR_KILOMETER	= Enc.get("$30");

	/**
	 * What the scale to output
	 */
	private float[]	     _tags	           = new float[] { 0.01F, 0.05F, 0.1F,
	        0.5F, 1.0F, 5.0F, 10.0F, 50.0F, 100.0F, 500.0F, 1000.0F, 5000.0F,
	        10000.0F, 50000.0F, 100000.0F, 250000.0F, 500000.0F, 1000000.0F,
	        5000000.0F, 10000000.0F	   };

	/**
	 * Main constructor - use for all purposes
	 * 
	 * @param unit_size
	 *            size of world rect unit in meters. That is is you use
	 *            centimetres, set it to 0.01, if meters 1.0 and kilometres
	 *            1000.0
	 */
	public MapRuler( float unit_size )
	{
		this();
		_unit_in_meters = unit_size;
	}

	public MapRuler()
	{
		_ratio = 0.0F;
		_unit_in_meters = 1.0F;
		_right_indent = 6;
		_bottom_indent = 6;
		_height = 6;
		_ruler_cell_width = 10;
		_ruler_unit_height = _height / 2;
		_touch_height = 3;
		_txt_bottom_indent = _touch_height;
		_draw_text_bg = false;
		_font_size = 12;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.IMapRuler#buildRuler(float, Graphics2D, Rectangle2D,
	 *      Rectangle2D)
	 */
	public float buildRuler( float unit_size, Graphics gra, Rectangle dev_rect,
	        Rectangle world_rect )
	{
		_unit_in_meters = unit_size;
		/*
		 * First find the ration between two scales: in world rect and display
		 * rect. Really the ration shows - how many world units are contained in
		 * a single display device unit
		 */
		_ratio = (float) ( ( world_rect.getWidth() * _unit_in_meters ) / dev_rect
		        .getWidth() );
		if ( buildRuler( unit_size, gra, dev_rect, _ratio ) )
			return _ratio;
		else
			return 0.0F;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.legend.IMapRuler#buildRuler(float, java.awt.Graphics,
	 *      float, int, int)
	 */
	public boolean buildRuler( float world_unit_size, Graphics gra,
	        Rectangle dev_rect, float ratio, int right, int bottom )
	{
		_right_indent = right;
		_bottom_indent = bottom;
		return buildRuler( world_unit_size, gra, dev_rect, ratio );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.legend.IMapRuler#buildRuler(float, java.awt.Graphics,
	 *      float)
	 */
	public boolean buildRuler( float world_unit_size, Graphics gra,
	        Rectangle dev_rect, float ratio )
	{
		_unit_in_meters = world_unit_size;
		/*
		 * First find the ratio between two coordinate system: world rect and
		 * display rect. Really the ratio shows - how many meters are contained
		 * in a single display device unit. World units are always calculated in
		 * meters
		 */
		_ratio = ratio * _unit_in_meters;
		if ( _ratio == 0.0 )
		{
			System.err.println( "MapRuler.buildRuler: Can't draw ruler" );
			return false;
		}

		/* Now find the nearest printable value for ratio */
		_cell_in_dev = 0;
		int ind;
		for ( ind = 0; ind < _tags.length; ind++ )
		{
			float x = _tags[ ind ] / _ratio;
			_cell_in_dev = Math.round( x );
			if ( _cell_in_dev >= _ruler_cell_width )
			{
				/*
				 * Note that ruler consists of 20 smaller units - black/white
				 * rectangles, 10 of them are drawn in chessboard, 10 in two
				 * empty spaces
				 */
				double left = dev_rect.getMaxX() - _cell_in_dev * 20
				        - _right_indent;
				if ( left > dev_rect.getMinX() )
					break;
				/*
				 * then left border of the ruler is out of the screen. Try to
				 * adopt to other scale
				 */
				if ( ind > 0 )
				{
					/* it is not the first, so optimisation is possible */
					int cell_dev = Math.round( _tags[ ind - 1 ] / _ratio );
					if ( cell_dev > _ruler_cell_width )
					{
						_cell_in_dev = cell_dev;
						ind--;
					}
				}
				break;
			}
		}
		/* set scale unit into centimetres, which are minimum allowed  */
		_scale_seg_in_dev = (int) ( _tags[ ind ] * 100.0F * 5.0F * _unit_in_meters );
		String u_str;
		/* find the true name for the scale unit, that is "cm", "m" or "km" */
		if ( _scale_seg_in_dev < 100 )
		{
			u_str = STR_CENTIMETER;
		}
		else if ( _scale_seg_in_dev < 100000 )
		{
			u_str = STR_METER;
			_scale_seg_in_dev /= 100; /* make metres */
		}
		else
		{
			u_str = STR_KILOMETER;
			_scale_seg_in_dev /= 100000; /* make kilometres */
		}

		gra.setPaintMode();
		int right = (int) ( dev_rect.getWidth() - _right_indent );
		int left = right - _cell_in_dev * 20;
		int bottom = (int) ( dev_rect.getHeight() - _bottom_indent );
		int top = bottom - _height;
		int w = right - left - 1;
		int h = bottom - top - 1;
		RectI rect = new RectI( left, top, w, h );

		/*
		 * Now it is a time to draw the ruler. Clear the whole ruler area
		 * rectangle.
		 */
		gra.setColor( Color.WHITE );
		gra.fillRect( rect.left, rect.top, w, h );

		/*
		 * Draw outline
		 */
		gra.setColor( Color.BLACK );
		gra.drawRect( rect.left, rect.top, w, h );

		/* draw texts above the ruler main vertical touches using default font */
		Font font = gra.getFont();
		int fsi = font.getSize();
		int fs = Math.min( _font_size, fsi );
		font = new Font( font.getFontName(), Font.PLAIN, fs );
		gra.setFont( font );
		FontMetrics fm = gra.getFontMetrics();
		int x = rect.left;
		/* get bottom of text */
		int y = rect.top - _txt_bottom_indent;
		for ( int i = 0; i < 5; i++ )
		{
			String txt = Integer.toString( _scale_seg_in_dev * i )
			        + ( i == 1 ? " " + u_str : "" );
			Rectangle2D rect2 = fm.getStringBounds( txt, gra );
			if ( i == 4 ) // to align on right boundary
				x = rect.right;
			int draw_x = (int) ( x - rect2.getWidth() );
			if ( _draw_text_bg )
			{
				int ch = fs;
				int cy = y - ch + 1;
				int cw = (int) Math.round( rect2.getWidth() );
				gra.setColor( Color.WHITE );
				gra.fillRect( draw_x, cy, cw, ch );
			}
			gra.setColor( Color.BLACK );
			gra.drawString( txt, draw_x, y );
			x += _cell_in_dev * 5;
		}

		/* Draw a chessboard part on the bar */
		RectI rect1 = new RectI( rect.left, rect.top, _cell_in_dev,
		        _ruler_unit_height );
		RectI rect2 = new RectI( rect1 );
		rect2.offset( _cell_in_dev, _ruler_unit_height );
		for ( int i = 0; i < 5; i++ )
		{
			gra.fillRect( rect1.left, rect1.top, rect1.width(), rect1.height() );
			rect1.offset( _cell_in_dev * 2, 0 );
			gra.fillRect( rect2.left, rect2.top, rect2.width(), rect2.height() );
			rect2.offset( _cell_in_dev * 2, 0 );
		}

		/* draw 4 touches, ensure last is in line with right ruler border */
		x = rect.left;
		int y1 = rect.bottom;
		int y2 = rect.top - _touch_height;
		for ( int i = 0; i < 4; i++ )
		{
			gra.drawLine( x, y1, x, y2 );
			x += _cell_in_dev * 5;
		}
		x = rect.right;
		gra.drawLine( x, y1, x, y2 );
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.legend.IMapRuler#get_cell_height()
	 */
	public int get_cell_height()
	{
		return _ruler_unit_height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.legend.IMapRuler#get_cell_width()
	 */
	public int get_cell_width()
	{
		return _ruler_cell_width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.legend.IMapRuler#set_cell_height(int)
	 */
	public void set_cell_height( int h )
	{
		_ruler_unit_height = h;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.legend.IMapRuler#set_cell_width(int)
	 */
	public void set_cell_width( int w )
	{
		_ruler_cell_width = w;
	}

	/**
	 * Still not realised. I'll do it after return from vacations
	 */
	public boolean canRebuild( float scale )
	{
		return false;
		/*
		 * return Math.abs( _ratio - scale ) < 0.0000001;
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.IMapRuler#rebuildRuler(float, java.awt.Graphics2D,
	 *      java.awt.Rectangle, java.awt.Rectangle)
	 */
	public void rebuildRuler( float scale, Graphics gra, Rectangle dev_rect )
	{
		if ( !canRebuild( scale ) )
			return;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.IMapRuler#getRatio()
	 */
	public float getRatio()
	{
		return _ratio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.IMapRuler2#get_right_indent()
	 */
	public final int get_right_indent()
	{
		return _right_indent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.IMapRuler2#set_right_indent(int)
	 */
	public final void set_right_indent( int right_indent )
	{
		this._right_indent = right_indent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.IMapRuler2#get_bottom_indent()
	 */
	public final int get_bottom_indent()
	{
		return _bottom_indent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.IMapRuler2#set_bottom_indent(int)
	 */
	public final void set_bottom_indent( int bottom_indent )
	{
		this._bottom_indent = bottom_indent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.legend.IMapRuler#set_text_background(boolean)
	 */
	public boolean set_text_background( boolean on )
	{
		boolean old_prop = _draw_text_bg;
		_draw_text_bg = on;
		return old_prop;
	}

	/**
	 * Тестовый пример использования рисовальщика масштабной линейки - рулера
	 * 
	 * @param args -
	 *            имя растрового файла для подгрузки в качестве подложки
	 */
	public static void main( String[] args )
	{
		/*
		 * проверяем параметр на наличие имени графического файла (JPG, PNG,
		 * BMP? может быть и TIFF - не знаю точно про возможности Java в этой
		 * области)
		 */

		if ( Text.isEmpty( args ) )
		{
			System.err
			        .println( "Can't proceed without file name in command line..." );
			return;
		}
		String fname = args[ 0 ];
		File file = new File( fname );
		if ( !file.exists() )
		{
			System.out.println( "File not found :\"" + fname + "\"" );
			return;
		}

		/* Загружаем картинку */
		BufferedImage mapimg = null;
		try
		{
			mapimg = ImageIO.read( file );
			System.out.println( "Image file opened :\"" + fname + "\"" );
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
			return;
		}

		/*
		 * Получаем ссылку на её графику (Graphics) и вычисляем прямоугольник
		 * рисования. Возможно, есть более лёгкие пути для этого, но я их пока
		 * не знаю.
		 */
		Graphics gra = mapimg.getGraphics();
		GraphicsConfiguration gc = ( (Graphics2D) gra )
		        .getDeviceConfiguration();
		Rectangle drect = gc.getBounds();
		int dw = (int) drect.getWidth();
		int dh = (int) drect.getHeight();

		/* Задаём отступ снизу от области рисования */
		int rbottom = dh - 30;
		/* Задаём начальный масштаб для линейки */
		float scale = 100000.0F;
		/* Создаём экземпляр класса рисования */
		MapRuler mr = new MapRuler( 1.0F );
		/* Задаём рисовку текста на белом прямоугольнике */
		mr.set_text_background( true );

		/*
		 * Далее в цикле варьируем масштаб и рисуем в одной графике но так,
		 * чтобы линейки не перекрывали друг друга. Выход из цикла либо по
		 * выходу за пределы рисунка либо по достижение минимально разумного
		 * масштаба (0.0) либо по вырождению размера проекта
		 */
		while ( ( rbottom > 30 ) && ( scale > 0.0 ) )
		{
			/*
			 * Задаём отступ от низа графики. Линейка всегда располагается
			 * считая от низа графики - так принято в топографических стнадратах
			 */
			mr.set_bottom_indent( rbottom );
			/*
			 * Задаём прямоугольник проекта, подобный прямоугольнику рисования.
			 * Следует понять, что масштаб рассчитывается только от
			 * горизонтальной оси. Если по вертикальной оси масштаб будет
			 * другой, то это никак не отразится на отрисованной линейке. В
			 * принципе, можно подумать и о вертикальном рулере - для
			 * экзотических случаев несовпадения вертикального и горизонтального
			 * масштабов.
			 */
			Rectangle wrect = new Rectangle( 0, 0, (int) ( dw * scale ),
			        (int) ( dh * scale ) );
			if ( wrect.getWidth() == 0.0 )
			{
				System.out.println( "World rect is now empty!" );
				break;
			}
			/*
			 * Смещаем прямоугольник проекта для придания тесту большей
			 * достоверности, не более того
			 */
			wrect.translate( dw / 2, dh / 2 );

			/*******************************************************************
			 * Строим линейку для текущего масштаба, указывая единицы проекта в
			 * метрах (1.0F), графику растра gra, прямоугольник растра drect и
			 * прямоугольник в проекте wrect
			 ******************************************************************/
			mr.buildRuler( 1.0F, gra, drect, wrect );

			/* Следующий рулер будем строить на графике ниже на 30 пикселов */
			rbottom -= 30;
			/* Масштаб уменьшим вдвое */
			scale /= 2.0F;
			/* И повторим ещё раз */
		}

		/*
		 * Сохраним результирующий растр в той же директории, где расположен
		 * исходный
		 */
		String dir = Files.getDirectory( fname );
		save2img( mapimg, dir, "RuleredImage.png" );

		/* Вот и всё, ребята! */
	}

	private static void save2img( BufferedImage bi, String dir, String fname )
	{
		String ext = Files.getExtension( fname ).toLowerCase().substring( 1 );
		File file = new File( dir, fname );
		try
		{
			ImageIO.write( bi, ext, file );
			System.out.println( "Image created: \"" + fname + "\"" );
		}
		catch ( Exception ex )
		{
			System.out.println( "Image \"" + fname + "\" creation error:"
			        + ex.getMessage() );
		}
	}

}
