/*
 * Created on 03.05.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.ts.toykernel.raster.googleearh;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * @author Administrator
 * 
 */
public class GEHelper {
	
	private int lg2(double x,boolean longitude)
	{
		double d;
		if(longitude)d = 180; else d = Envelope.init_lat;
		int lg = 0;
		while(true)
		{
			if(x>=d) return lg;
			d=d/2;
			lg++;
		}
	}
	int getDetailLevel (Envelope e,int screen_width_pixels)
	{		
		//РґР»СЏ Р·Р°РґР°РЅРЅРѕРіРѕ СЌРЅРІРµР»РѕРїР° РїРѕРґСЃС‡РёС‚Р°РµРј, РёСЃС…РѕРґСЏ РёР· РµРіРѕ РґР»РёРЅС‹-С€РёСЂРёРЅС‹, РєР°РєРѕР№ СѓСЂРѕРІРµРЅСЊ РґРµС‚Р°Р»РёР·Р°С†РёРё
		double w = e.getWidth();
		double h = e.getHeight();
		
		Envelope s = Envelope.TR;
		int level = 2;// СЌС‚Рѕ СЃРєРѕР»СЊРєРѕ Р±СѓРєРІ Р±СѓРґРµС‚ РІ URL С‚РёРїР° qrst
		while(true)
		{
			if((w>s.getWidth())&&(h>s.getHeight()))
			{
				level--;
				break;
			}
			else s = s.getQ();
			level++;
		}
		//С‚РµРїРµСЂСЊ РµСЃР»Рё РІ РѕРєРЅРµ СѓРјРµС‰Р°РµС‚СЃСЏ РЅРµСЃРєРѕР»СЊРєРѕ РєР°СЂС‚РёРЅРѕРє 255*255 РїРёРєСЃРµР»РµР№, С‚Рѕ Р»РµРІРµР» РЅР°РґРѕ СѓРІРµР»РёС‡РёРІР°С‚СЊ.
		while(screen_width_pixels > 255)
		{
			level++;
			screen_width_pixels = screen_width_pixels /2;			
		}
		return level;
	}
	double getLatStep(int level)
	{//РґР»СЏ Р·Р°РґР°РЅРЅРѕРіРѕ СѓСЂРѕРІРЅСЏ РґРµС‚Р°Р»РёР·Р°С†РёРё РІС‹РґР°С‚СЊ С€Р°Рі РІ РіСЂР°РґСѓСЃР°С…
		if(level<2) return 0;
		
		double res = GEHelper.toMercator(Envelope.init_lat);
		level = level-2;
		while (level >0)
		{
			res = res / 2;
			level=level-1;
		}
		return res;
	}
	double getLngStep(int level)
	{
		if(level<2) return 0;		
		double res = 180;
		level = level-2;
		while (level >0)
		{
			res = res / 2;
			level=level-1;
		}
		return res;		
	}
	Image getImage(double lat, double lng, int level)
	//Р·Р°РґР°РµС‚СЃСЏ РІРµСЂС…РЅСЏСЏ Р»РµРІР°СЏ РєРѕРѕСЂРґРёРЅР°С‚Р° РєРІР°РґСЂР°С‚Р° Рё СѓСЂРѕРІРµРЅСЊ РґРµС‚Р°Р»РёР·Р°С†РёРё
	{	
		boolean q,r,s,t;
		
		Image image=  new Image();
		image.qrst_name = "tr";
		level = level-2;
		Envelope e = Envelope.TR;
		
		double e_center_lat,e_center_lng;
		while(level!=0)
		{
			q=r=s=t=true;
			e_center_lat = e.getCenterX();
			e_center_lng = e.getCenterY();
			if(e_center_lng>lng) r=s=false;else q=t=false; 
			if(e_center_lat>lat) q=r=false;else t=s=false; 
			if(q){ e = e.getQ(); image.qrst_name  = image.qrst_name.concat("q");}
			if(r){ e = e.getR(); image.qrst_name  = image.qrst_name.concat("r");}
			if(s){ e  =e.getS(); image.qrst_name  = image.qrst_name.concat("s");}
			if(t){ e = e.getT(); image.qrst_name  = image.qrst_name.concat("t");}
			level = level - 1;
		}
		image.upper_left_lat = fromMercator(e.upper_left_lat)+e.upper_left_lat*GEHelper.delta_lat(e.upper_left_lat);		
		image.upper_left_lng = e.upper_left_lng;
		image.bottom_right_lat = fromMercator(e.bottom_right_lat)+e.bottom_right_lat*GEHelper.delta_lat(e.bottom_right_lat);		
		image.bottom_right_lng = e.bottom_right_lng;
		
		return image;
	}

	public Image [][] getPictureQRST(Envelope e, int screen_width_pixels)
	{	
		int level = getDetailLevel(e,screen_width_pixels);
		double lat_step = getLatStep(level);
		double lng_step = getLngStep(level);
		int num_horisont_pictures = (int) (e.getWidth()/lng_step) + 2;
		int num_vertical_pictures = (int) (e.getHeight()/lat_step) +2;
		Image [][] res = new Image [num_horisont_pictures][num_vertical_pictures];
		System.out.println("Picture is "+num_horisont_pictures + "x"+num_vertical_pictures + " fragments");
		double lat = e.upper_left_lat;	
		double lng;
		for(int i = 0;i<num_vertical_pictures;i++)
		{
			lng = e.upper_left_lng;			
			for(int j = 0; j< num_horisont_pictures; j++)
			{
				//System.out.println("("+(fromMercator(lat)+delta_lat)+","+(lng+delta_lng)+")");
				res[j][i] = getImage(lat,lng,level);
				lng = lng + lng_step;	
			}
			lat = lat - lat_step;
		}
		 
		return res;
	}
	static int last_server_num = 0;

	public byte [] getImage(String qrts)
	{
		try {
					System.setProperty("http.proxyHost", "192.168.105.226");
					System.setProperty("http.proxyPort", "81");
					last_server_num=0;
					String host = "kh"+last_server_num+".google.com";
					URL c_url = new URL("http://"+host+"/kh?n=404&v=17&t=" + qrts);
					last_server_num ++; if(last_server_num==4) last_server_num =0;
					
					URLConnection http_connection = c_url.openConnection();

					((HttpURLConnection) http_connection).setRequestMethod("GET");
					http_connection.setUseCaches(true);
					http_connection.setDoInput(true);
					http_connection.setDoOutput(true);
//					http_connection.addRequestProperty("Accept","*/*");
//					http_connection.addRequestProperty("Referer","http://maps.google.com");
//					http_connection.addRequestProperty("Accept-Language","ru");
//					http_connection.addRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.1.4322");
//					http_connection.addRequestProperty("Host",host);
//					http_connection.addRequestProperty("Cookie","khcookie=fzwq2rEhPGhKjMiCYoQfTeVy41KncTaeiHV-Og; PREF=ID=fc5a8ed0b04bdbd1:TB=2:TM=1179389426:LM=1179823939:S=xF0rWev0d-l6Z2lZ; testcookie=");
					InputStream is = http_connection.getInputStream();
					int cl  = http_connection.getContentLength();
					byte [] res = new byte[cl];
					String input;
					int read = 1;				
					
					int r = 0;
					while (read != -1) {
						read = is.read(res,r,cl-r);
						if(read!=-1) r = r + read;
					                
					}					
					return res;

				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;

	}
	static double toMercator(double lat)
	{
		double res;
		if(lat == 0 ) return 0;
		res = java.lang.Math.log(java.lang.Math.tan((java.lang.Math.PI*(90+lat))/360));
		return res;
	}
	static double fromMercator(double lat)
	{
		double res;
		res = 2*java.lang.Math.atan(java.lang.Math.exp(lat))-(java.lang.Math.PI/2);
		res=res*180/java.lang.Math.PI;
		return res;		
	}		
    public static void main(String[] args) {
		try {
            GEHelper gh = new GEHelper();
            //GEHelper.Envelope e =  gh. new Envelope(55.751761,37.617072,55.750967,37.619009);
            
            /*sicilia*/
            //Envelope e =  new Envelope(39.0277-delta_lat,12.3046-delta_lng,36.102376-delta_lat,16.7871-delta_lng);
            		
            /*moscow*/
            //Envelope e =  new Envelope(55.755263-delta_lat,37.617594-delta_lng,55.6402-delta_lat,37.7349-delta_lng);
			
			/*sadovoe ring*/			
			Envelope e =  new Envelope(55.7748,37.5799,55.7251,37.6604);

			/*РіРґРµ С‚Рѕ РЅР° РІРѕСЃС‚РѕРєРµ РЅР° С€РёСЂРѕС‚Рµ РјРѕСЃРєРІС‹*/			
			//Envelope e =  new Envelope(56.25,43.801,56.175,43.904);


			/*Litva*/
			//Envelope e =  new Envelope(57.160794-delta_lat,24.2623delta_lng,57.1428-delta_lat,24.3020delta_lng);
			
			/*Sebastopol*/
			//Envelope e =  new Envelope(44.5906,33.4163,44.5680,33.4447);
			
			/*krimea*/
			//Envelope e =  new Envelope(46.3697-delta_lat,32.5711-delta_lng,44.0915-delta_lat,35.5603-delta_lng);

			/*equator in africa*/
			//Envelope e =  new Envelope(0.05,42.8820,0.01,42.9266);
			
			/*Murmansk*/
			//Envelope e =  new Envelope(68.967,33.037,68.951,33.0892);
			
			/*РІСЃС‚РѕРє РґРµР»Рѕ С‚РѕРЅРєРѕРµ*/
			//Envelope e =  new Envelope(64.7421,177.68283,64.7250,177.68728);
			
//            Image [][] res = gh.getPictureQRST(e,500);
//            for(int  i =0;i<res[0].length;i++)
//            for(int j = 0;j<res.length;j++)
//            {
//            	System.out.println(res[j][i]);
//            	byte [] image =  gh.getImage(res[j][i].qrst_name);
//            	FileOutputStream fos = new FileOutputStream((InParamsApp.options.get(InParamsApp.optarr[InParamsApp.O_gld]) +i)+j+".jpg");
//            	if(image!=null) fos.write(image);
//            	fos.close();
//            }
            	
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }	    
    }
    static double delta_lat(double lat_merc)
    {
		return 0;
//		double res =  lat_merc*(-0.0811111)+0.20439314267281105990783410138249;		
//		if(lat_merc>0.8)
//		{
//			res = res - 0.00059988*((lat_merc - 0.8)/(1.673-0.8));
//		}
//		if((lat_merc>1.1) && (lat_merc < 1.3))
//		{
//			res = res - lat_merc*0.002;
//		}
//		return res-2.0/3600;
    	
    	
    }
	
     
}
