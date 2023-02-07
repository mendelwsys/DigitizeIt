package ru.ts.xml;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.DTDHandler;

/**
 * Класс создан для того что обеспечить распределенный между объектами разбор xml потока
 * содержит методы замещения перехватчика событий разборщика
 */
public class HandlerEx extends DefaultHandler
{
	private ContentHandler handler;
	private DTDHandler dtdhandler;

	protected XMLReader reader;

	public HandlerEx(XMLReader reader)
	{
		this.reader = reader;
		handler=reader.getContentHandler();
		dtdhandler=reader.getDTDHandler();
		reader.setContentHandler(this);
		reader.setDTDHandler(this);
	}

	public void ret2callerHadnler()
	{
		reader.setContentHandler(handler);
		reader.setDTDHandler(dtdhandler);
	}
}
