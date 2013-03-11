/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2ElementModel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.model;

import com.uwyn.rife.gui.model.exceptions.GuiModelException;
import com.uwyn.rife.xml.Xml2Data;
import org.xml.sax.Attributes;

public class Xml2ElementModel extends Xml2Data
{
	private StringBuilder			mCharacterData = null;
	private ElementModel			mCurrentElement = null;
	private SubmissionModel			mCurrentSubmission = null;
	private ParticlePropertyModel	mCurrentParticleProperty = null;
	
	public ElementModel getElementModel()
	{
		return mCurrentElement;
	}
	
	public void startDocument()
	{
		mCharacterData = new StringBuilder();
		mCurrentElement = null;
		mCurrentSubmission = null;
		mCurrentParticleProperty = null;
	}
	
	public void endDocument()
	{
		mCharacterData = null;
		mCurrentSubmission = null;
		mCurrentParticleProperty = null;
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		try
		{
			if (qName.equals("element"))
			{
				mCurrentElement = new ElementModel(atts.getValue("id"));
				String implementation = atts.getValue("implementation");
				if (implementation != null)
				{
					mCurrentElement.setImplementation(implementation);
				}
			}
			else if (qName.equals("exit"))
			{
				mCurrentParticleProperty = mCurrentElement.addExit(atts.getValue("name"));
			}
			else if (qName.equals("input"))
			{
				mCurrentParticleProperty = mCurrentElement.addInput(atts.getValue("name"));
			}
			else if (qName.equals("output"))
			{
				mCurrentParticleProperty = mCurrentElement.addOutput(atts.getValue("name"));
			}
			else if (qName.equals("param"))
			{
				mCurrentParticleProperty = mCurrentSubmission.addParameter(atts.getValue("name"));
			}
			else if (qName.equals("submission"))
			{
				mCurrentSubmission = mCurrentElement.addSubmission(atts.getValue("name"));
			}
			else if (qName.equals("description"))
			{
				mCharacterData = new StringBuilder();
			}
		}
		catch (GuiModelException e)
		{
			mCurrentElement = null;
		}
	}
	public void endElement(String namespaceURI, String localName, String qName)
	{
		if (qName.equals("description"))
		{
			if (mCurrentParticleProperty != null)
			{
				mCurrentParticleProperty.setDescription(mCharacterData.toString());
			}
			else if (mCurrentSubmission != null)
			{
				mCurrentSubmission.setDescription(mCharacterData.toString());
			}
			else if (mCurrentElement != null)
			{
				mCurrentElement.setDescription(mCharacterData.toString());
			}
			mCharacterData = new StringBuilder();
		}
		else if (qName.equals("exit") ||
				 qName.equals("input") ||
				 qName.equals("output") ||
				 qName.equals("param"))
		{
			mCurrentParticleProperty = null;
		}
		else if (qName.equals("submission"))
		{
			mCurrentSubmission = null;
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

