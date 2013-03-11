/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2Config.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.config;

import com.uwyn.rife.config.exceptions.ConfigErrorException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.xml.Xml2Data;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import org.xml.sax.Attributes;

public class Xml2Config extends Xml2Data
{
	private StringBuilder						mCharacterDataStack = null;

	private String								mCurrentListName = null;
	private String								mCurrentParameterName = null;

	private HashMap<String, String>				mParameters = null;
	private ArrayList<String>					mFinalParameters = null;
	private HashMap<String, ArrayList<String>>	mLists = null;
	private ArrayList<String>					mFinalLists = null;
	
	public Xml2Config()
	{
		this(null, null, null, null);
	}
	
	public Xml2Config(HashMap<String, String> parameters, ArrayList<String> finalParameters, HashMap<String, ArrayList<String>> lists, ArrayList<String> finalLists)
	{
		mParameters = parameters;
		mFinalParameters = finalParameters;
		mLists = lists;
		mFinalLists = finalLists;

		if (null == mParameters)
		{
			mParameters = new HashMap<String, String>();
		}
		if (null == mFinalParameters)
		{
			mFinalParameters = new ArrayList<String>();
		}
		if (null == mLists)
		{
			mLists = new HashMap<String, ArrayList<String>>();
		}
		if (null == mFinalLists)
		{
			mFinalLists = new ArrayList<String>();
		}
	}
	
	public HashMap<String, String> getParameters()
	{
		return mParameters;
	}
	
	public ArrayList<String> getFinalParameters()
	{
		return mFinalParameters;
	}

	public HashMap<String, ArrayList<String>> getLists()
	{
		return mLists;
	}
	
	public ArrayList<String> getFinalLists()
	{
		return mFinalLists;
	}

	public void startDocument()
	{
		mCharacterDataStack = null;
		mCurrentListName = null;
		mCurrentParameterName = null;
	}
	
	public void endDocument()
	{
		mCharacterDataStack = null;
		mCurrentListName = null;
		mCurrentParameterName = null;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		if (qName.equals("param") ||
            qName.equals("item"))
		{
			mCharacterDataStack = new StringBuilder();
		}

		if (qName.equals("config"))
		{
			// do nothing
		}
		else if (qName.equals("list"))
		{
			String	name = atts.getValue("name");
			
			if (!mFinalLists.contains(name))
			{
				mCurrentListName = name;
				mLists.put(name, new ArrayList<String>());
				
				String	final_attribute = atts.getValue("final");
				if (final_attribute != null  &&
					(final_attribute.equals("1") ||
					 final_attribute.equals("t") ||
					 final_attribute.equals("true")))
				{
					mFinalLists.add(name);
				}
			}
		}
		else if (qName.equals("param"))
		{
			String name = atts.getValue("name");
			
			if (!mFinalParameters.contains(name))
			{
				mCurrentParameterName = name;
				
				String	final_attribute = atts.getValue("final");
				if (final_attribute != null  &&
					(final_attribute.equals("1") ||
					 final_attribute.equals("t") ||
					 final_attribute.equals("true")))
				{
					mFinalParameters.add(name);
				}
			}
		}
		else if (qName.equals("item"))
		{
		}
		else if (qName.equals("value"))
		{
			String	parameter_name = atts.getValue("name");
			
			if (mParameters.containsKey(parameter_name))
			{
				mCharacterDataStack.append(mParameters.get(parameter_name));
			}
		}
		else if (qName.equals("property"))
		{
			String	property_name = atts.getValue("name");

			HierarchicalProperties properties = Rep.getProperties();
			properties.contains(property_name);
			
			if (properties.contains(property_name))
			{
				try
				{
					mCharacterDataStack.append(properties.get(property_name).getValueString());
				}
				catch (PropertyValueException e)
				{
					throw new XmlErrorException("Error while obtain the String value of property '"+property_name+"'.", e);
				}
			}
		}
		else if (qName.equals("include"))
		{
			String	included_file = atts.getValue("file");
			
			try
			{
				new Config(included_file, getResourceFinder(), mParameters, mFinalParameters, mLists, mFinalLists);
			}
			catch (ConfigErrorException e)
			{
				throw new XmlErrorException("Error while processing the included config file '"+included_file+"'.", e);
			}
		}
		else
		{
			throw new XmlErrorException("Unsupport element name '"+qName+"'.");
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName)
	{
		if (qName.equals("config"))
		{
		}
		else if (qName.equals("param"))
		{
			if (mCurrentParameterName != null)
			{
				String	parameter_name = mCurrentParameterName;
				
				mParameters.put(parameter_name, mCharacterDataStack.toString());
				
				mCurrentParameterName = null;
			}
			
			mCharacterDataStack = null;
		}
		else if (qName.equals("list"))
		{
			mCurrentListName = null;
		}
		else if (qName.equals("item"))
		{
			if (mCurrentListName != null)
			{
				mLists.get(mCurrentListName).add(mCharacterDataStack.toString());
				mCharacterDataStack = null;
			}
		}
		else if (qName.equals("value"))
		{
		}
	}
	
	public void characters(char[] ch, int start, int length)
	{
		if (mCharacterDataStack != null &&
			length > 0)
		{
			mCharacterDataStack.append(String.copyValueOf(ch, start, length));
		}
	}
}

