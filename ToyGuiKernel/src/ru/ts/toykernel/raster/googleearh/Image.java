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
public class Image {

	public String qrst_name;
	public double upper_left_lat;
	public double upper_left_lng;
	public double bottom_right_lat;
	public double bottom_right_lng;	
	public String toString()
	{
		return qrst_name + ": ("+upper_left_lat+","+upper_left_lng+";"+bottom_right_lat+","+bottom_right_lng+")";
	}
}
