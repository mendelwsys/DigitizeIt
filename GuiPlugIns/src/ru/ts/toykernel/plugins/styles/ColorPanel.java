package ru.ts.toykernel.plugins.styles;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 19.04.2007
 * Time: 14:26:59
 * Color panel for setting color panel
 */
public class ColorPanel extends JPanel
{
	public ColorPanel(LayoutManager layout, boolean isDoubleBuffered)
	{
		super(layout, isDoubleBuffered);
	}

	public ColorPanel(LayoutManager layout)
	{
		super(layout);
	}

	public ColorPanel(boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
	}

	public ColorPanel()
	{
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public void setColor(String color)
	{
		try
		{
			this.color = (int) Long.parseLong(color,16);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
	}

	private int color;
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(new Color(color,true));
		g.fillRect(0,0,getWidth(),getHeight());
	}
}
