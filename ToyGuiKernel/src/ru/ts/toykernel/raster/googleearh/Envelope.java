/*
 * Created on 10.05.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.ts.toykernel.raster.googleearh;

/**
 * @author ROV
 * 
 */
public class Envelope
	{
		//static double init_lat = 85;
		static double init_lat = 85.0511287798066;
		static Envelope TR = new Envelope(init_lat,0,0,180,false);
		public Envelope(Envelope e)
		{
			upper_left_lat = e.upper_left_lat;
			upper_left_lng= e.upper_left_lng;
			bottom_right_lat= e.bottom_right_lat;
			bottom_right_lng= e.bottom_right_lng;
		}
		
		public Envelope(double ullat,double ullng,double brlat,double brlng, boolean shift)
		{			
			upper_left_lat= GEHelper.toMercator(ullat);
			if(shift) upper_left_lat= GEHelper.toMercator(ullat*upper_left_lat*1.11674);			
			upper_left_lng=ullng;
			bottom_right_lat = GEHelper.toMercator(brlat);
			if(shift) bottom_right_lat = GEHelper.toMercator(brlat*bottom_right_lat*1.11674); 			
			bottom_right_lng=brlng;
		}
		public Envelope(double ullat,double ullng,double brlat,double brlng)
		{
			upper_left_lat= GEHelper.toMercator(ullat);		
			upper_left_lat= GEHelper.toMercator(ullat-upper_left_lat*GEHelper.delta_lat(upper_left_lat));				
			upper_left_lng=ullng;
			bottom_right_lat = GEHelper.toMercator(brlat);		
			bottom_right_lat = GEHelper.toMercator(brlat -bottom_right_lat*GEHelper.delta_lat(bottom_right_lat)); 	 			
			bottom_right_lng=brlng;
		}
		double upper_left_lat;
		double upper_left_lng;
		double bottom_right_lat;
		double bottom_right_lng; 
		
		double getHeight(){return upper_left_lat-bottom_right_lat;}
		double getWidth(){return bottom_right_lng-upper_left_lng;}
		double getCenterX(){return (upper_left_lat - (getHeight()/2));}
		double getCenterY(){return (upper_left_lng + (getWidth()/2));}
		
		Envelope getQ()
		{
			Envelope q = new Envelope(this);
			
			q.bottom_right_lat = bottom_right_lat + (getHeight()/2);
			q.bottom_right_lng = bottom_right_lng - (getWidth()/2);
			return q;			
		}
		Envelope getR()
		{
			Envelope r = new Envelope(this);
			r.upper_left_lng = upper_left_lng + (getWidth()/2);
			r.bottom_right_lat = bottom_right_lat + (getHeight()/2);
			return r;
		}
		Envelope getS()
		{
			Envelope s = new Envelope(this);			
			s.upper_left_lat =  upper_left_lat - (getHeight()/2);
			s.upper_left_lng = upper_left_lng + (getWidth()/2);
			return s;			
		}
		Envelope getT()
		{
			Envelope t = new Envelope(this);
			t.upper_left_lat = upper_left_lat - (getHeight()/2);
			t.bottom_right_lng = bottom_right_lng - (getWidth()/2);
			return t;			
		}
	}
