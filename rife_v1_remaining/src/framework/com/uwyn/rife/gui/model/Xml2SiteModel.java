/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2SiteModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.xml.Xml2Data;
import org.xml.sax.Attributes;

public class Xml2SiteModel extends Xml2Data
{
	private StringBuilder	mCharacterData = null;
	private SiteModel		mCurrentSite = null;

	public SiteModel getSiteModel()
	{
		return mCurrentSite;
	}
	
	public void startDocument()
	{
		mCharacterData = new StringBuilder();
		mCurrentSite = null;
	}
	
	public void endDocument()
	{
		mCharacterData = null;
		mCurrentSite = null;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		if (qName.equals("element"))
		{
		}
		else if (qName.equals("description"))
		{
			mCharacterData = new StringBuilder();
		}
	}
	public void endElement(String namespaceURI, String localName, String qName)
	{
		if (qName.equals("description"))
		{
			mCharacterData = new StringBuilder();
		}
	}
	
	public void characters(char[] ch, int start, int length)
	{
		if (length > 0)
		{
			mCharacterData.append(String.copyValueOf(ch, start, length));
		}
	}
}

