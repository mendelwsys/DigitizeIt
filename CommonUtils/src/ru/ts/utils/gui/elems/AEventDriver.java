package ru.ts.utils.gui.elems;

import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 07.08.2007
 * Time: 16:35:50
 * Changes: yugl, 25.04.2008,
 * исходная функциональность - заготовка для обработки основных GUI-событий,
 * в конце, правда, слои упоминаются, можно бы было эту лабуду в GisUtils унести,
 * да ладно уж.
 * уже не упоминаются :-) vladm
 * doing refactoring, added commentaries, changed dependencies
 */
public abstract class AEventDriver implements MouseListener, KeyListener, MouseMotionListener
{
	abstract protected PictureCtrl getPictureCtrl();
	abstract protected void repaintPicture();

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent event)
	{
		if (getPictureCtrl()!=null)
		{
			synchronized (this)
			{
				getPictureCtrl().resetState();
				getPictureCtrl().performAction(PictureCtrl.T0, event);
			}
			repaintPicture();
		}
	}

	public void mouseReleased(MouseEvent event)
	{
		if ( getPictureCtrl()!=null)
		{
			synchronized (this)
			{
				getPictureCtrl().performAction(PictureCtrl.T3, event);
				getPictureCtrl().performAction(PictureCtrl.T4, event);
			}
			repaintPicture();
		}
	}

	public void mouseDragged(MouseEvent e)
	{
		if (getPictureCtrl()!=null)
		{
			synchronized (this)
			{
				getPictureCtrl().performAction(PictureCtrl.T2, e);
			}
			repaintPicture();
		}
	}

	public void keyTyped(KeyEvent e)
	{
	}
}