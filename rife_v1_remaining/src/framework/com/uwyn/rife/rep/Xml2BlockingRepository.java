/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2BlockingRepository.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.ioc.PropertyValue;
import com.uwyn.rife.ioc.PropertyValueList;
import com.uwyn.rife.ioc.PropertyValueObject;
import com.uwyn.rife.ioc.PropertyValueParticipant;
import com.uwyn.rife.ioc.PropertyValueTemplate;
import com.uwyn.rife.ioc.exceptions.PropertyConstructionException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.rep.exceptions.ParticipantNotFoundException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.xml.Xml2Data;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.util.ArrayList;
import java.util.Stack;
import org.xml.sax.Attributes;

/**
 * Processes a <code>Rep</code> XML document and add all the declared
 * participants to a <code>BlockingRepository</code>.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see BlockingRepository
 * @since 1.0
 */
public class Xml2BlockingRepository extends Xml2Data
{
	private BlockingRepository	mRepository = null;
	private String				mName = null;
	private boolean				mBlocking = false;
	private String				mParameter = null;
	private StringBuilder		mCharacterData = null;
	
	private String						mCurrentPropertyName = null;
	
	private Stack<String> 				mParticipantNameStack = null;
	private Stack<PropertyValueList>	mPropertyValuesStack = null;
	private	String						mCurrentTemplateType = null;
	
	Xml2BlockingRepository(BlockingRepository repository)
	{
		mRepository = repository;
	}
	
	/**
	 * Adds all the participants of a provided resource to the repository.
	 *
	 * @param resource the name of the resource that contains the declaration
	 * of the participants.
	 * @param resourcefinder a <code>ResourceFinder</code> that will be used
	 * to retrieve the resource
	 * @exception XmlErrorException if an error occurs during the processing
	 * of the document
	 */
	public void addRepParticipants(String resource, ResourceFinder resourcefinder)
	throws XmlErrorException
	{
		processXml(resource, resourcefinder);
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		if (qName.equals("participant") &&
			null == mCurrentPropertyName)
		{
			mCharacterData = new StringBuilder();

			mName = atts.getValue("name");

			mBlocking = false;
			String blocking = atts.getValue("blocking");
			if (blocking != null &&
				(blocking.equals("1") ||
				 blocking.equals("t") ||
				 blocking.equals("true")))
			{
				mBlocking = true;
			}

			mParameter = atts.getValue("param");
		}
		else if (qName.equals("property"))
		{
			mCurrentPropertyName = atts.getValue("name");
			mCharacterData = new StringBuilder();
			mPropertyValuesStack = new Stack<PropertyValueList>();
			mPropertyValuesStack.push(new PropertyValueList());
		}
		else if (qName.equals("participant") ||
				 qName.equals("datasource"))
		{
			// check if inappropriate attributes aren't used
			if (qName.equals("participant"))
			{
				if (null == atts.getValue("name"))
				{
					throw new XmlErrorException("The repository 'participant' tag requires the 'name' attribute when it's used as a property value.");
				}
				if (atts.getValue("param") != null)
				{
					throw new XmlErrorException("The repository 'participant' tag can't have the 'param' attribute when it's used as a property value.");
				}
			}
				
			// store the character data of the previous property value series
			mPropertyValuesStack.peek().add(new PropertyValueObject(mCharacterData.toString()));
			
			// initialize the new nested participant
			if (null == mParticipantNameStack)
			{
				mParticipantNameStack = new Stack<String>();
			}
			
			mCharacterData = new StringBuilder();
			
			String name;
			if (qName.equals("datasource"))
			{
				name = Datasources.DEFAULT_PARTICIPANT_NAME;
			}
			else
			{
				name = atts.getValue("name");
			}
			
			mParticipantNameStack.push(name);
			mPropertyValuesStack.push(new PropertyValueList());
		}
		else if (qName.equals("template"))
		{
			// store the character data of the previous property value series
			mPropertyValuesStack.peek().add(new PropertyValueObject(mCharacterData.toString()));
			
			mCurrentTemplateType = atts.getValue("type");
			
			mCharacterData = new StringBuilder();
			mPropertyValuesStack.push(new PropertyValueList());
		}
		else if (qName.equals("config"))
		{
			// store the character data of the previous property value series
			mPropertyValuesStack.peek().add(new PropertyValueObject(mCharacterData.toString()));

			// add the property value for the configuration
			mPropertyValuesStack.peek().add(new PropertyValueParticipant(Config.DEFAULT_PARTICIPANT_NAME, new PropertyValueObject(atts.getValue("param"))));
			
			mCharacterData = new StringBuilder();
		}
		else if (qName.equals("rep"))
		{
			// do nothing
		}
		else
		{
			throw new XmlErrorException("Unsupport element name '"+qName+"'.");
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
	{
		if (qName.equals("participant") &&
			null == mCurrentPropertyName)
		{
			if (!mRepository.addParticipant(StringUtils.trim(mCharacterData.toString()), mName, mBlocking, mParameter))
			{
				throw new ParticipantNotFoundException(mCharacterData.toString());
			}
		}
		else if (qName.equals("property"))
		{
			PropertyValueList propvals = mPropertyValuesStack.pop();
			
			// store the character data to the current property value series
			propvals.add(new PropertyValueObject(mCharacterData.toString()));
			
			try
			{
				mRepository.getProperties().put(mCurrentPropertyName, propvals.makePropertyValue());
			}
			catch (PropertyValueException e)
			{
				throw new PropertyConstructionException("repository", getXmlPath(), mCurrentPropertyName, e);
			}
			mCharacterData = null;
			mCurrentPropertyName = null;
			mPropertyValuesStack = null;
		}
		else if (qName.equals("participant") ||
				 qName.equals("datasource"))
		{
			PropertyValueList propvals = mPropertyValuesStack.pop();
			
			// store the character data to the current property value series
			propvals.add(new PropertyValueObject(mCharacterData.toString()));
			
			try
			{
				PropertyValue propval = new PropertyValueParticipant(mParticipantNameStack.pop(), propvals.makePropertyValue());
				ArrayList<PropertyValue> containing_propval_series = mPropertyValuesStack.peek();
				containing_propval_series.add(propval);
			}
			catch (PropertyValueException e)
			{
				throw new PropertyConstructionException("repository", getXmlPath(), mCurrentPropertyName, e);
			}
			
			mCharacterData = new StringBuilder();
		}
		else if (qName.equals("template"))
		{
			PropertyValueList propvals = mPropertyValuesStack.pop();
			
			// store the character data to the current property value series
			propvals.add(new PropertyValueObject(mCharacterData.toString()));
			
			try
			{
				PropertyValue propval = new PropertyValueTemplate(mCurrentTemplateType, propvals.makePropertyValue().getValueString());
				ArrayList<PropertyValue> containing_propval_series = mPropertyValuesStack.peek();
				containing_propval_series.add(propval);
			}
			catch (PropertyValueException e)
			{
				throw new PropertyConstructionException("repository", getXmlPath(), mCurrentPropertyName, e);
			}
			
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

