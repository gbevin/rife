/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
  * $Id: Xml2Site.java 3928 2008-04-22 16:25:18Z gbevin $
*/
package com.uwyn.rife.engine;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.FlowLinkSpecificDataLinkDestIdSpecifiedException;
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
import java.util.List;

import org.xml.sax.Attributes;

class Xml2Site implements SiteProcessor
{
	Xml2Site()
	{
	}

	public void processSite(SiteBuilder builder, String declarationName, ResourceFinder resourceFinder)
	throws EngineException
	{
		XmlProcessor processor = new XmlProcessor(builder);
		
		String processed_path = null;
		try
		{
			// process the site xml file
			try
			{
				processed_path = declarationName;
				processor.processXml(processed_path, resourceFinder);
			}
			catch (CantFindResourceException e)
			{
				processed_path = SiteProcessor.DEFAULT_SITES_PATH+declarationName;
				processor.processXml(processed_path, resourceFinder);
			}
		}
		catch (XmlErrorException e)
		{
			throw new ProcessingErrorException("site", declarationName, e);
		}
	
		// obtain the modification time
		if (RifeConfig.Engine.getSiteAutoReload())
		{
			URL resource = resourceFinder.getResource(processed_path);
			if (null == resource)
			{
				throw new NotFoundProcessingErrorException("site", processed_path, null);
			}
			
			try
			{
				builder.addResourceModificationTime(new UrlResource(resource, processed_path), resourceFinder.getModificationTime(resource));
			}
			catch (ResourceFinderErrorException e)
			{
				throw new ProcessingErrorException("site", declarationName, "Error while retrieving the modification time.", e);
			}
		}
	}
	
	private class XmlProcessor extends Xml2Data
	{
		private SiteBuilder					mSiteBuilder = null;
	
		private ElementInfoBuilder			mCurrentElementInfoBuilder = null;
		private XmlElementInfoProcessor		mCurrentElementInfoProcessor = null;
		private FlowLinkBuilder				mCurrentFlowLinkBuilder = null;

		private String						mCurrentPropertyName = null;
	
		private String						mCurrentGlobalVar = null;
		private ArrayList<String>			mCurrentGlobalVarDefaults = null;
		
		private String 						mCurrentGlobalCookie = null;
		private String 						mCurrentGlobalCookieDefault = null;
		
		private StringBuilder				mCharacterData = null;
		
		private Stack<String> 				mParticipantNameStack = null;
		private Stack<PropertyValueList>	mPropertyValuesStack = null;
		private	String						mCurrentTemplateType = null;

		private String						mCurrentErrorDestId = null;
		private List<Class>					mCurrentTypes = null;
		
		private XmlProcessor(SiteBuilder builder)
		{
			mSiteBuilder = builder;
		}
		
		public void startDocument()
		{
			mCurrentPropertyName = null;
	
			mCurrentGlobalVar = null;
			mCurrentGlobalVarDefaults = null;
		
			mCurrentGlobalCookie = null;
			mCurrentGlobalCookieDefault = null;
		
			mParticipantNameStack = null;
			mPropertyValuesStack = null;
			mCharacterData = null;

			mCurrentErrorDestId = null;
			mCurrentTypes = null;
		}
		
		public void endDocument()
		{
			mCurrentGlobalVarDefaults = null;
			mCharacterData = null;
			mParticipantNameStack = null;
			mPropertyValuesStack = null;
		}
		
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
		{
			if (qName.equals("site"))
			{
				mSiteBuilder.setFallback(atts.getValue("fallbackid"));
			}
			else if (qName.equals("subsite"))
			{
				mSiteBuilder = mSiteBuilder
					.enterSubsiteDeclaration(atts.getValue("file"))
						.setId(atts.getValue("id"))
						.setUrlPrefix(atts.getValue("urlprefix"))
						.enterSubsite()
							.setInherits(atts.getValue("inherits"))
							.setPre(atts.getValue("pre"));
			}
			else if (qName.equals("group"))
			{
				String inherits = atts.getValue("inherits");
				String pre = atts.getValue("pre");
				
				mSiteBuilder.enterGroup()
					.setInherits(inherits)
					.setPre(pre);
			}
			else if (qName.equals("globalvar"))
			{
				mCurrentGlobalVar = atts.getValue("name");
				mCurrentGlobalVarDefaults = new ArrayList<String>();
			}
			else if (qName.equals("globalcookie"))
			{
				mCurrentGlobalCookie = atts.getValue("name");
				mCurrentGlobalCookieDefault = null;
			}
			else if (qName.equals("globalbean"))
			{
				String classname = atts.getValue("classname");
				String prefix = atts.getValue("prefix");
				String name = atts.getValue("name");
				String group = atts.getValue("group");
				
				mSiteBuilder.addGlobalBean(classname, prefix, name, group);
			}
			else if (qName.equals("globalexit"))
			{
				String name = atts.getValue("name");
				String destid = atts.getValue("destid");
				boolean reflective = false;
				boolean snapback = false;
				boolean	cancel_inheritance = false;
				boolean	cancel_embedding = false;
				boolean redirect = false;
				boolean	cancel_continuations = false;
				
				if (atts.getValue("reflect") != null &&
					(atts.getValue("reflect").equals("1") ||
					 atts.getValue("reflect").equals("t") ||
					 atts.getValue("reflect").equals("true")))
				{
					reflective = true;
				}
	
				if (atts.getValue("snapback") != null &&
					(atts.getValue("snapback").equals("1") ||
					 atts.getValue("snapback").equals("t") ||
					 atts.getValue("snapback").equals("true")))
				{
					snapback = true;
				}
				
				if (atts.getValue("redirect") != null &&
					(atts.getValue("redirect").equals("1") ||
					 atts.getValue("redirect").equals("t") ||
					 atts.getValue("redirect").equals("true")))
				{
					redirect = true;
				}
				
				String	inheritance = atts.getValue("inheritance");
				if (inheritance != null &&
					inheritance.equals("cancel"))
				{
					cancel_inheritance = true;
				}
				
				String	embedding = atts.getValue("embedding");
				if (embedding != null &&
					embedding.equals("cancel"))
				{
					cancel_embedding = true;
				}
				
				String	continuations = atts.getValue("continuations");
				if (continuations != null &&
					continuations.equals("cancel"))
				{
					cancel_continuations = true;
				}
				
				mSiteBuilder.addGlobalExit(name, destid, reflective, snapback, cancel_inheritance, cancel_embedding, redirect, cancel_continuations);
			}
			else if (qName.equals("arrival"))
			{
				boolean redirect = false;
				if (atts.getValue("redirect") != null &&
					(atts.getValue("redirect").equals("1") ||
					atts.getValue("redirect").equals("t") ||
					atts.getValue("redirect").equals("true")))
				{
					redirect = true;
				}

				mSiteBuilder.setArrival(atts.getValue("destid"), redirect);
			}
			else if (qName.equals("departure"))
			{
				mSiteBuilder.addDeparture(atts.getValue("srcid"));
			}
			else if (qName.equals("state"))
			{
				String state = atts.getValue("store");
				if (null == state)
				{
					state = StateStoreQuery.IDENTIFIER;
				}
				mSiteBuilder.enterState(state);
			}
			else if (qName.equals("element"))
			{
				mCurrentElementInfoBuilder = mSiteBuilder.enterElement(atts.getValue("file"))
					.setId(atts.getValue("id"))
					.setUrl(atts.getValue("url"))
					.setInherits(atts.getValue("inherits"))
					.setPre(atts.getValue("pre"));
				mCurrentElementInfoProcessor = new XmlElementInfoProcessor(mCurrentElementInfoBuilder);
				mCurrentElementInfoProcessor.startElement(namespaceURI, localName, qName, atts);
				String pathinfo = atts.getValue("pathinfo");
				if (pathinfo != null)
				{
					mCurrentElementInfoBuilder.setPathInfoMode(PathInfoMode.getMode(atts.getValue("pathinfo")));
				}
			}
			else if (qName.equals("datalink"))
			{
				String srcoutput = atts.getValue("srcoutput");
				String srcoutbean = atts.getValue("srcoutbean");
				
				String dest_id = atts.getValue("destid");
				boolean snapback = false;
				
				String destinput = atts.getValue("destinput");
				String destinbean = atts.getValue("destinbean");
				
				if (atts.getValue("snapback") != null &&
					(atts.getValue("snapback").equals("1") ||
					 atts.getValue("snapback").equals("t") ||
					 atts.getValue("snapback").equals("true")))
				{
					snapback = true;
				}
				
				if (mCurrentFlowLinkBuilder != null)
				{
					if (dest_id != null)
					{
						throw new FlowLinkSpecificDataLinkDestIdSpecifiedException(getXmlPath(), mCurrentFlowLinkBuilder.getElementInfoBuilder().getElementDeclaration().getId(), mCurrentFlowLinkBuilder.getSrcExit());
					}
					mCurrentFlowLinkBuilder.addDataLink(srcoutput, srcoutbean, snapback, destinput, destinbean);
				}
				else
				{
					mCurrentElementInfoBuilder.addDataLink(srcoutput, srcoutbean, dest_id, snapback, destinput, destinbean);
				}
			}
			else if (qName.equals("flowlink"))
			{
				String srcexit = atts.getValue("srcexit");
				String destid = atts.getValue("destid");
				boolean snapback = false;
				boolean	cancel_inheritance = false;
				boolean	cancel_embedding = false;
				boolean redirect = false;
				boolean	cancel_continuations = false;
				
				if (atts.getValue("snapback") != null &&
					(atts.getValue("snapback").equals("1") ||
					 atts.getValue("snapback").equals("t") ||
					 atts.getValue("snapback").equals("true")))
				{
					snapback = true;
				}
				
				if (atts.getValue("redirect") != null &&
					(atts.getValue("redirect").equals("1") ||
					 atts.getValue("redirect").equals("t") ||
					 atts.getValue("redirect").equals("true")))
				{
					redirect = true;
				}
				
				String	inheritance = atts.getValue("inheritance");
				if (inheritance != null &&
					inheritance.equals("cancel"))
				{
					cancel_inheritance = true;
				}
				
				String	embedding = atts.getValue("embedding");
				if (embedding != null &&
					embedding.equals("cancel"))
				{
					cancel_embedding = true;
				}
				
				String	continuations = atts.getValue("continuations");
				if (continuations != null &&
					continuations.equals("cancel"))
				{
					cancel_continuations = true;
				}
				
				mCurrentFlowLinkBuilder = mCurrentElementInfoBuilder.enterFlowLink(srcexit)
					.destId(destid)
					.snapback(snapback)
					.cancelInheritance(cancel_inheritance)
					.cancelEmbedding(cancel_embedding)
					.redirect(redirect)
					.cancelContinuations(cancel_continuations);
			}
			else if (qName.equals("autolink"))
			{
				String srcexit = atts.getValue("srcexit");
				String destid = atts.getValue("destid");
				boolean	cancel_inheritance = false;
				boolean	cancel_embedding = false;
				boolean redirect = false;
				boolean	cancel_continuations = false;
				
				if (atts.getValue("redirect") != null &&
					(atts.getValue("redirect").equals("1") ||
					atts.getValue("redirect").equals("t") ||
					atts.getValue("redirect").equals("true")))
				{
					redirect = true;
				}
				
				String	inheritance = atts.getValue("inheritance");
				if (inheritance != null &&
					inheritance.equals("cancel"))
				{
					cancel_inheritance = true;
				}
				
				String	embedding = atts.getValue("embedding");
				if (embedding != null &&
					embedding.equals("cancel"))
				{
					cancel_embedding = true;
				}
				
				String	continuations = atts.getValue("continuations");
				if (continuations != null &&
					continuations.equals("cancel"))
				{
					cancel_continuations = true;
				}
				
				mCurrentElementInfoBuilder.addAutoLink(srcexit, destid, cancel_inheritance, cancel_embedding, redirect, cancel_continuations);
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
				if (mCurrentElementInfoProcessor != null &&
					mCurrentElementInfoProcessor.getDefaults() != null)
				{
					mCurrentElementInfoProcessor.startElement(namespaceURI, localName, qName, atts);
				}
				else
				{
					if (mCharacterData != null &&
						Config.hasRepInstance())
					{
						mCharacterData.append(Config.getRepInstance().getString(atts.getValue("param"), ""));
					}
				}
			}
			else if (qName.equals("default"))
			{
				if (mCurrentElementInfoProcessor != null)
				{
					mCurrentElementInfoProcessor.startElement(namespaceURI, localName, qName, atts);
				}
				else
				{
					mCharacterData = new StringBuilder();
				}
			}
			else if (qName.equals("errorhandler"))
			{
				mCurrentErrorDestId = atts.getValue("destid");
				mCurrentTypes = new ArrayList<Class>();
			}
			else if (qName.equals("type"))
			{
				String classname = atts.getValue("classname");
				try
				{
					mCurrentTypes.add(Class.forName(classname));
				}
				catch (ClassNotFoundException e)
				{
					throw new ParsingErrorException("site", getXmlPath(), "Can't find the type class '"+classname+"' for the error handler that targets the element '"+mCurrentErrorDestId+"'", e);
				}
			}
			else
			{
				if (mCurrentElementInfoProcessor != null)
				{
					mCurrentElementInfoProcessor.startElement(namespaceURI, localName, qName, atts);
				}
				else
				{
					throw new ParsingErrorException("site", getXmlPath(), "Unsupport element name '"+qName+"'.", null);
				}
			}
		}
		
		public void endElement(String namespaceURI, String localName, String qName)
		{
			if (qName.equals("element"))
			{
				mCurrentElementInfoProcessor = null;
				mCurrentElementInfoBuilder.leaveElement();
				mCurrentElementInfoBuilder = null;
			}
			else if (qName.equals("flowlink"))
			{
				mCurrentFlowLinkBuilder.leaveFlowLink();
				mCurrentFlowLinkBuilder = null;
			}
			else if (qName.equals("subsite"))
			{
				mSiteBuilder = mSiteBuilder.leaveSubsite().leaveSubsiteDeclaration();
			}
			else if (qName.equals("state"))
			{
				mSiteBuilder.leaveState();
			}
			else if (qName.equals("group"))
			{
				mSiteBuilder.leaveGroup();
			}
			else if (qName.equals("globalvar"))
			{
				String[]	defaults = null;
				if (mCurrentGlobalVarDefaults.size() > 0)
				{
					defaults = new String[mCurrentGlobalVarDefaults.size()];
					defaults = mCurrentGlobalVarDefaults.toArray(defaults);
				}
				mSiteBuilder.addGlobalVar(mCurrentGlobalVar, defaults);
				mCurrentGlobalVar = null;
				mCurrentGlobalVarDefaults = null;
			}
			else if (qName.equals("globalcookie"))
			{
				mSiteBuilder.addGlobalCookie(mCurrentGlobalCookie, mCurrentGlobalCookieDefault);
				mCurrentGlobalCookie = null;
				mCurrentGlobalCookieDefault = null;
			}
			else if (qName.equals("property"))
			{
				PropertyValueList propvals = mPropertyValuesStack.pop();
				
				// store the character data to the current property value series
				propvals.add(new PropertyValueObject(mCharacterData.toString()));
			
				try
				{
					if (mCurrentElementInfoBuilder != null)
					{
						mCurrentElementInfoBuilder.addProperty(mCurrentPropertyName, propvals.makePropertyValue());
					}
					else if (mSiteBuilder.getSubsiteDeclaration() != null)
					{
						mSiteBuilder.getSubsiteDeclaration().addProperty(mCurrentPropertyName, propvals.makePropertyValue());
					}
					else
					{
						mSiteBuilder.addProperty(mCurrentPropertyName, propvals.makePropertyValue());
					}
				}
				catch (PropertyValueException e)
				{
					throw new PropertyConstructionException("site", getXmlPath(), mCurrentPropertyName, e);
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
					throw new PropertyConstructionException("site", getXmlPath(), mCurrentPropertyName, e);
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
					throw new PropertyConstructionException("site", getXmlPath(), mCurrentPropertyName, e);
				}
	
				mCharacterData = new StringBuilder();
			}
			else if (qName.equals("default"))
			{
				if (mCurrentElementInfoProcessor != null)
				{
					mCurrentElementInfoProcessor.endElement(namespaceURI, localName, qName);
				}
				else
				{
					if (null != mCurrentGlobalCookie)
					{
						mCurrentGlobalCookieDefault = mCharacterData.toString();
					}
					else if( null != mCurrentGlobalVar )
					{
						mCurrentGlobalVarDefaults.add(mCharacterData.toString());
					}
					
					mCharacterData = null;
				}
			}
			else if (qName.equals("errorhandler"))
			{
				mSiteBuilder.addErrorHandler(mCurrentErrorDestId, mCurrentTypes);
				mCurrentErrorDestId = null;
				mCurrentTypes = null;
			}
			else
			{
				if (mCurrentElementInfoProcessor != null)
				{
					mCurrentElementInfoProcessor.endElement(namespaceURI, localName, qName);
				}
			}
		}
		
		public void characters(char[] ch, int start, int length)
		{
			if (mCurrentElementInfoProcessor != null &&
				mCurrentElementInfoProcessor.getDefaults() != null)
			{
				mCurrentElementInfoProcessor.characters(ch, start, length);
			}
			else
			{
				if (length > 0 &&
					mCharacterData != null)
				{
					mCharacterData.append(String.copyValueOf(ch, start, length));
				}
			}
		}
	}
}



