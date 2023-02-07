package ru.ts.toykernel.drawcomp;

import java.awt.*;

/**
 * Рисователь для текста
 * Text Painter with common paramters
 */
public interface ITextParamPainter extends IParamPainter
{

	/**
	 * @return get font of text
	 */
	Font getFont();

	/**
	 * @param font - font of text
	 */
	void setFont(Font font);

	/**
	 * @return text for drawing
	 */
	String getText();

	/**
	 * @param text - text for drawing
	 */
	void setText(String text);

	/**
	 * @return color text for drawing
	 */
	Color getColorText();

	/**
	 * @param colorText - set color text for drawing
	 */
	void setColorText(Color colorText);
}
