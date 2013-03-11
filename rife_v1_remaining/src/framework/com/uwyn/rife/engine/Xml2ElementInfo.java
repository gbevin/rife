/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2ElementInfo.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.NotFoundProcessingErrorException;
import com.uwyn.rife.engine.exceptions.ParsingErrorException;
import com.uwyn.rife.engine.exceptions.ProcessingErrorException;
import com.uwyn.rife.ioc.PropertyValue;
import com.uwyn.rife.ioc.PropertyValueList;
import com.uwyn.rife.ioc.PropertyValueObject;
import com.uwyn.rife.ioc.PropertyValueParticipant;
import com.uwyn.rife.ioc.PropertyValueTemplate;
import com.uwyn.rife.ioc.exceptions.PropertyConstructionException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.xml.Xml2Data;
import com.uwyn.rife.xml.exceptions.CantFindResourceException;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

class Xml2ElementInfo implements ElementInfoProcessor
{
	private XMLReader	mReader = null;
	
	Xml2ElementInfo()
	{
	}

	public void processElementInfo(ElementInfoBuilder builder, String declarationName, ResourceFinder resourceFinder)
	throws EngineException
	{
		XmlElementInfoProcessor processor = new XmlElementInfoProcessor(builder);
		
		String processed_path = null;
		try
		{
			// process the element xml file
			try
			{
				processed_path = declarationName;
				mReader = processor.processXml(processed_path, resourceFinder, mReader);
			}
			catch (CantFindResourceException e)
			{
				processed_path = DEFAULT_ELEMENTS_PATH+declarationName;
				mReader = processor.processXml(processed_path, resourceFinder, mReader);
			}
		}
		catch (XmlErrorException e)
		{
			throw new ProcessingErrorException("element", declarationName, e);
		}
	
		// obtain the modification time
		if (RifeConfig.Engine.getSiteAutoReload())
		{
			URL resource = resourceFinder.getResource(processed_path);
			if (null == resource)
			{
				throw new NotFoundProcessingErrorException("element", processed_path, null);
			}
			
			try
			{
				builder.addResourceModificationTime(new UrlResource(resource, processed_path), resourceFinder.getModificationTime(resource));
			}
			catch (ResourceFinderErrorException e)
			{
				throw new ProcessingErrorException("element", declarationName, "Error while retrieving the modification time.", e);
			}
		}
	}
}
	
class XmlElementInfoProcessor extends Xml2Data
{
	private ElementInfoBuilder			mElementInfoBuilder = null;

	private StringBuilder				mCharacterData = null;
	
	private	String						mCurrentPropertyName = null;
	
	private	String						mCurrentInput = null;
	private	String						mCurrentOutput = null;
	private	String						mCurrentIncookie = null;
	private	String						mCurrentOutcookie = null;
	private	String						mCurrentParameter = null;
	
	private ArrayList<String>			mDefaults = null;
	
	private	SubmissionBuilder			mSubmissionBuilder = null;
	
	private Stack<String> 				mParticipantNameStack = null;
	private Stack<PropertyValueList>	mPropertyValuesStack = null;
	private	String						mCurrentTemplateType = null;
	
	XmlElementInfoProcessor(ElementInfoBuilder builder)
	{
		mElementInfoBuilder = builder;
	}
	
	public ArrayList<String> getDefaults()
	{
		return mDefaults;
	}
		
	public void startDocument()
	{
		mCharacterData = null;
	}
	
	public void endDocument()
	{
		mCharacterData = null;
		mParticipantNameStack = null;
		mPropertyValuesStack = null;
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		if (qName.equals("element"))
		{
			mElementInfoBuilder
				.setImplementation(atts.getValue("implementation"))
				.extendsFrom(atts.getValue("extends"))
				.setContentType(atts.getValue("contenttype"));
			String pathinfo = atts.getValue("pathinfo");
			if (pathinfo != null)
			{
				mElementInfoBuilder.setPathInfoMode(PathInfoMode.getMode(atts.getValue("pathinfo")));
			}
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
			mCharacterData.append(Config.getRepInstance().getString(atts.getValue("param"), ""));
		}
		else if (qName.equals("input"))
		{
			mCurrentInput = atts.getValue("name");
			mDefaults = new ArrayList<String>();
		}
		else if (qName.equals("inbean"))
		{
			String classname = atts.getValue("classname");
			String prefix = atts.getValue("prefix");
			String name = atts.getValue("name");
			String group = atts.getValue("group");
			
			mElementInfoBuilder.addInBean(classname, prefix, name, group);
		}
		else if (qName.equals("output"))
		{
			mCurrentOutput = atts.getValue("name");
			mDefaults = new ArrayList<String>();
		}
		else if (qName.equals("outbean"))
		{
			String classname = atts.getValue("classname");
			String prefix = atts.getValue("prefix");
			String name = atts.getValue("name");
			String group = atts.getValue("group");
			
			mElementInfoBuilder.addOutBean(classname, prefix, name, group);
		}
		else if (qName.equals("incookie"))
		{
			mCurrentIncookie = atts.getValue("name");
			mDefaults = new ArrayList<String>();
		}
		else if (qName.equals("outcookie"))
		{
			mCurrentOutcookie = atts.getValue("name");
			mDefaults = new ArrayList<String>();
		}
		else if (qName.equals("childtrigger"))
		{
			mElementInfoBuilder.addChildTrigger(atts.getValue("name"));
		}
		else if (qName.equals("submission"))
		{
			mSubmissionBuilder = mElementInfoBuilder.enterSubmission(atts.getValue("name"));

			String continuations_attr = atts.getValue("continuations");
			if (continuations_attr != null)
			{
				mSubmissionBuilder.cancelContinuations(continuations_attr.equals("cancel"));
			}

			String scope_attr = atts.getValue("scope");
			if (scope_attr != null)
			{
				mSubmissionBuilder.setScope(Scope.getScope(scope_attr));
			}
		}
		else if (qName.equals("param"))
		{
			if (atts.getValue("name") != null &&
				atts.getValue("regexp") != null)
			{
				throw new ParsingErrorException("element", getXmlPath(), "A submission parameter can't have both a name and a regexp attribute.", null);
			}
			
			if (atts.getValue("name") != null)
			{
				mCurrentParameter = atts.getValue("name");
				mDefaults = new ArrayList<String>();
			}
			else if (atts.getValue("regexp") != null)
			{
				mSubmissionBuilder.addParameterRegexp(atts.getValue("regexp"));
				mDefaults = null;
			}
			else
			{
				throw new ParsingErrorException("element", getXmlPath(), "A submission parameter needs either a name or a regexp attribute.", null);
			}
		}
		else if (qName.equals("bean"))
		{
			String classname = atts.getValue("classname");
			String prefix = atts.getValue("prefix");
			String name = atts.getValue("name");
			String group = atts.getValue("group");
			
			mSubmissionBuilder.addBean(classname, prefix, name, group);
		}
		else if (qName.equals("file"))
		{
			if (atts.getValue("name") != null &&
				atts.getValue("regexp") != null)
			{
				throw new ParsingErrorException("element", getXmlPath(), "A submission file can't have both a name and a regexp attribute.", null);
			}
			
			if (atts.getValue("name") != null)
			{
				mSubmissionBuilder.addFile(atts.getValue("name"));
			}
			else if (atts.getValue("regexp") != null)
			{
				mSubmissionBuilder.addFileRegexp(atts.getValue("regexp"));
			}
			else
			{
				throw new ParsingErrorException("element", getXmlPath(), "A submission file needs either a name or a regexp attribute.", null);
			}
		}
		else if (qName.equals("default"))
		{
			mCharacterData = new StringBuilder();
		}
		else if (qName.equals("exit"))
		{
			mElementInfoBuilder.addExit(atts.getValue("name"));
		}
		else if (qName.equals("pathinfo"))
		{
			mElementInfoBuilder.addPathInfoMapping(atts.getValue("mapping"));
		}
		else
		{
			throw new ParsingErrorException("element", getXmlPath(), "Unsupport element name '"+qName+"'.", null);
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName)
	{
		if (qName.equals("property"))
		{
			PropertyValueList propvals = mPropertyValuesStack.pop();
			
			// store the character data to the current property value series
			propvals.add(new PropertyValueObject(mCharacterData.toString()));
			
			try
			{
				mElementInfoBuilder.addStaticProperty(mCurrentPropertyName, propvals.makePropertyValue());
			}
			catch (PropertyValueException e)
			{
				throw new PropertyConstructionException("element", getXmlPath(), mCurrentPropertyName, e);
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
				throw new PropertyConstructionException("element", getXmlPath(), mCurrentPropertyName, e);
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
				throw new PropertyConstructionException("element", getXmlPath(), mCurrentPropertyName, e);
			}

			mCharacterData = new StringBuilder();
		}
		else if (qName.equals("input"))
		{
			String[]	defaults = null;
			if (mDefaults.size() > 0)
			{
				defaults = new String[mDefaults.size()];
				defaults = mDefaults.toArray(defaults);
			}
			mElementInfoBuilder.addInput(mCurrentInput, defaults);
			mCurrentInput = null;
			mDefaults = null;
		}
		else if (qName.equals("output"))
		{
			String[]	defaults = null;
			if (mDefaults.size() > 0)
			{
				defaults = new String[mDefaults.size()];
				defaults = mDefaults.toArray(defaults);
			}
			mElementInfoBuilder.addOutput(mCurrentOutput, defaults);
			mCurrentOutput = null;
			mDefaults = null;
		}
		else if (qName.equals("incookie"))
		{
			String defaultValue = null;
			if (mDefaults.size() > 0)
			{
				defaultValue = mDefaults.get(0);
			}
			mElementInfoBuilder.addIncookie(mCurrentIncookie, defaultValue);
			mCurrentIncookie = null;
			mDefaults = null;
		}
		else if (qName.equals("outcookie"))
		{
			String defaultValue = null;
			if (mDefaults.size() > 0)
			{
				defaultValue = mDefaults.get(0);
			}
			mElementInfoBuilder.addOutcookie(mCurrentOutcookie, defaultValue);
			mCurrentOutcookie = null;
			mDefaults = null;
		}
		else if (qName.equals("submission"))
		{
			mSubmissionBuilder.leaveSubmission();
		}
		else if (qName.equals("param"))
		{
			if (mDefaults != null)
			{
				String[]	defaults = null;
				if (mDefaults.size() > 0)
				{
					defaults = new String[mDefaults.size()];
					defaults = mDefaults.toArray(defaults);
				}
				mSubmissionBuilder.addParameter(mCurrentParameter, defaults);
			}
			mCurrentParameter = null;
			mDefaults = null;
		}
		else if (qName.equals("default"))
		{
			if (null == mDefaults)
			{
				throw new ParsingErrorException("element", getXmlPath(), "A submission parameter that's defined by a regular expression can't have default values.", null);
			}
			
			mDefaults.add(mCharacterData.toString());
			mCharacterData = null;
		}
	}
	
	public void characters(char[] ch, int start, int length)
	{
		if (length > 0 &&
			mCharacterData != null)
		{
			String text = String.copyValueOf(ch, start, length);
			mCharacterData.append(text);
		}
	}
}

