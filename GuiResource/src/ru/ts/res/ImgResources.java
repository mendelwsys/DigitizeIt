package ru.ts.res;

import javax.swing.*;
import java.net.URL;

/**
 * Images Resources loader
 */
public class ImgResources
{
	/**
	 * load icon by resource path and resource name
	 * @param respath - resource path
	 * @param resname - resource name
	 * @return loaded icon or null
	 */
	public static ImageIcon getIconByName(String respath,String resname)
	{
		URL imageURL = ImgResources.class.getResource(respath);
		ImageIcon icon =  null;
		if (imageURL != null)
		{
			icon=new ImageIcon(imageURL, resname);
			return icon;
		}
	    return icon;
	}
}
