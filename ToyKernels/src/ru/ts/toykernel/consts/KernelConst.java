package ru.ts.toykernel.consts;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 06.03.2009
 * Time: 11:56:09
 * Contansants of default kernel
 */
public class KernelConst
{
	public static final String POINT = "Point";
	public static final String LINESTRING = "LineString";
	public static final String LINEARRING = "LinearRing";
	public static final String LINEARRINGH = "LinearRingH";
	public static final String RASTR = "Rastr";
	public static final String DEFAULTYPE = "LineString";

	public static final String ELLIPS="ellipse"; //Эллипс
	public static final String ELLIPSF="ellipsef";//Заполненный эллипс

	public static final String RECT="rect";//прямоугольник (введен для удобства)
	public static final String RECTF="rectf";//прямоугольник (введен для удобства)






	public static final String POINT_TEXT =POINT + "_TEXT";
	public static final String POLY_TEXT = LINEARRING + "_TEXT";
	public static final String LINE_TEXT = LINESTRING + "_TEXT";

	public static String[] types = {
			LINESTRING, LINEARRING,LINEARRINGH,POINT,ELLIPS,ELLIPSF,
			LINE_TEXT, POLY_TEXT, POINT_TEXT,
	};//Типы геометрических объектов

	//TODO ALL attributes constans should start with ATTR_ prefix
	//Style of drawing
	//Стили прорисовок

	
	public static final String ATTR_COLOR_LINE = "COLOR_LINE";
	public static final String ATTR_LINE_STYLE = "LINE_STYLE";

	public static final String ATTR_RADPNT = "RADPNT";
	public static final String ATTR_COMPOSITE = "COMPOSITE";



//	TODO public static final String ATTR_FORCE_STROCKE_REPLACE = "LINE_THICKNESS";
//	
	public static final String ATTR_COLOR_FILL = "COLOR_FILL";
	public static final String ATTR_IMG_FILL = "IMG_FILL";
	public static final String ATTR_IMG_POINT = "IMG_POINT";
	public static final String ATTR_IMG_CENTRALPOINT = "IMG_CENTRALPOINT";


	public static final String ATTR_LINE_THICKNESS = "LINE_THICKNESS";
	public static final String ATTR_SCALE_THICKNESS = "ATTR_SCALE_THICKNESS";


	public static final String ATTR_STROKE_LINE_CAP = "STROKE_LINE_CAP";
	public static final String ATTR_STROKE_LINE_JOIN = "STROKE_LINE_JOIN";
	public static final String ATTR_STROKE_MITER_LIMIT = "STROKE_MITER_LIMIT";
	public static final String ATTR_STROKE_DASHARRAY = "STROKE_DASHARRAY";
	public static final String ATTR_STROKE_DASHPHASE = "STROKE_DASHPHASE";
	public static final String ATTR_SCALE_DASHARRAY = "SCALE_DASHARRAY";
	public static final String ATTR_BOUND = "BOUND";



	public static final String INITSZ = "InitSz";





	//Имя приписанное геометрическому объекту
	//Object name attribute
	public static final String ATTR_CURVE_NAME = "CURVE_NAME";
	public static final String USE_AS_ATTRIBUTENAME="ATTRIBUTENAME";


	//Через задание этого параметра устанавливается имя аттрибута по которому производится поиск,
	//подсвечивается имя выбранного объекта и т.д


	//ССылка на изображение связанное с объектом
	//reference to image connected with object
	public static final String ATTR_IMG_REF = "CURVE_CUSTOM_IMG_REF";

	public static final String ATTR_CURVE_CUSTOM = "CURVE_CUSTOM";//С этого аттрибута начинаются все добавленные аттрибуты объекта
    public static final String ATTR_LAYER_CUSTOM = "LAYER_CUSTOM";//С этого аттрибута начинаются все добавленные аттрибуты слоя

	//Layer name attribute
	//Object name attribute
	public static final String LAYER_VISIBLE = "VISIBLE";
	public static final String OBJNAME = "OBJNAME";
	public static final String DESCRIPTOR = "DESCRIPTOR";
	public static final String LAYER_NAME = "LAYERNAME";
	public static final String LAYER_BOUND = "BOUND";


	//Tag names for parsing from XML
	public static final String SYSTEMNAME = "SYSTEM";
	public static final String DIR_TAGNAME = "dirname";

	public static final String GISO_TAGNAME = "giso";
	public static final String LAYER_TAGNAME = "layer";
	public static final String STORAGE_TAGNAME = "storage";
	public static final String FILTER_TAGNAME = "filter";
	public static final String RULE_TAGNAME = "rule";
	public static final String META_TAGNAME = "metainfo";
	public static final String PROJ_TAGNAME = "project";
	public static final String CONVERTER_TAGNAME = "converter";
	public static final String PROJCTXT_TAGNAME = "projcont";
	public static final String TRANSFORMER_TAGNAME = "transformer";
	public static final String APPLICATION_TAGNAME = "application";
	public static final String VIEWCNTRL_TAGNAME = "viewctrl";
	public static final String NAMECONVERTER_TAGNAME = "nameconv";
	public static final String CONVPROVIDER_TAGNAME="convprovider";
	public static final String STORPROVIDER_TAGNAME="storprovider";
	public static final String CMDPROVIDER_TAGNAME="cmdprovider";
	public static final String SERVAPP_TAGNAME="servapp";
	public static final String CONFPROVIDERS_TAGNAME="configprovider";
	public static final String CLASSLOADER_TAGNAME ="classloader";
	public static final String PLUGIN_TAGNAME ="plugin";

	public static final String APPBUILDER_TAGNAME ="appbuilder";
	public static final String COMPILLER_TAGNAME ="compiler";

	//COMMAND NAME CONSTANS
	public static final String P_SKIPDRAWWM = "SKIPDRAWWM";//Команда пропуска рисования вектороной карты при отрисовке панели

	public static final String OBJTYPE = "OBJTYPE";//Тип объекта
	public static final String OBJECTID = "OBJECTID";//Ид объекта
	public static final String CRDPOINT = "CRDPOINT";//Координаты точки

	public static final String SEGMENT = "SEGMENT";//Сегмент

	public static final String NMOD="NMOD";//Имя модуля


	public static final String ATTR_HAREA = "HAREA";
	public static final String ATTR_WAREA = "WAREA";
	public static final int DEF_WAREA = 7; //Область интереса вокруг точки курсора в координтах отображения по x
	public static final int DEF_HAREA = 7; //Область интереса вокруг точки курсора в координтах отображения по у


}
