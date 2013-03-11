/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2Data.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.xml.exceptions.FatalParsingErrorsException;
import com.uwyn.rife.xml.exceptions.ParserCreationErrorException;
import com.uwyn.rife.xml.exceptions.ParserExecutionErrorException;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class Xml2Data extends DefaultHandler
{
	private boolean				mDisableValidation = false;
	private boolean				mEnableValidation = false;
	private XmlErrorRedirector	mErrorRedirector = null;
	private	String				mXmlPath = null;
	private ResourceFinder		mResourceFinder = null;
	
	public void disableValidation(boolean activation)
	{
		mDisableValidation = activation;
		if (activation)
		{
			mEnableValidation = false;
		}
	}
	
	public void enableValidation(boolean activation)
	{
		mEnableValidation = activation;
		if (activation)
		{
			mDisableValidation = false;
		}
	}

	public void warning(SAXParseException e)
	{
		mErrorRedirector.warning(e);
	}

	public void error(SAXParseException e)
	{
		mErrorRedirector.error(e);
	}

	public void fatalError(SAXParseException e)
	{
		mErrorRedirector.fatalError(e);
	}

	protected String getXmlPath()
	{
		return mXmlPath;
	}

	protected ResourceFinder getResourceFinder()
	{
		return mResourceFinder;
	}
	
	protected XmlErrorRedirector getErrorRedirector()
	{
		return mErrorRedirector;
	}

	protected XmlErrorRedirector createErrorRedirector()
	{
		return new ExceptionErrorRedirector(this);
	}

	public synchronized XMLReader processXml(String xmlPath)
	throws XmlErrorException
	{
		return processXml(xmlPath, ResourceFinderClasspath.getInstance(), null);
	}
	
	public synchronized XMLReader processXml(String xmlPath, ResourceFinder resourceFinder)
	throws XmlErrorException
	{
		return processXml(xmlPath, resourceFinder, null);
	}
	
	public synchronized XMLReader processXml(String xmlPath, ResourceFinder resourceFinder, XMLReader reader)
	throws XmlErrorException
	{
		if (null == xmlPath)		throw new IllegalArgumentException("xmlPath can't be null.");
		if (xmlPath.length() == 0)	throw new IllegalArgumentException("xmlPath can't be empty.");
		if (null == resourceFinder)	throw new IllegalArgumentException("resourceFinder can't be null.");

		XmlInputSource inputsource = new XmlInputSource(xmlPath, resourceFinder);
		
		mXmlPath = xmlPath;
		mResourceFinder = resourceFinder;
		mErrorRedirector = createErrorRedirector();
		
		XmlEntityResolver entity_resolver = new XmlEntityResolver(resourceFinder);

		if (null == reader)
		{
			try
			{
				reader = XMLReaderFactory.createXMLReader();
			}
			catch (SAXException e)
			{
				try
				{
					// JDK 1.4 default parser
					reader = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
				}
				catch (SAXException e2)
				{
					throw new ParserCreationErrorException(xmlPath, e);
				}
			}
		}
		
		reader.setContentHandler(this);
		reader.setEntityResolver(entity_resolver);
		reader.setErrorHandler(mErrorRedirector);
		
		try
		{
			if (mEnableValidation ||
				(!mDisableValidation &&
				 (!Config.hasRepInstance() ||
				  !Rep.getParticipant("ParticipantConfig").isFinished() ||
				  RifeConfig.Xml.getXmlValidation())))
			{
				reader.setFeature("http://xml.org/sax/features/validation", true);
			}
			else
			{
				reader.setFeature("http://xml.org/sax/features/validation", false);
			}
		}
		catch (SAXException e)
		{
			Logger.getLogger("com.uwyn.rife.xml").warning("The parser '"+reader.getClass().getName()+"' doesn't support validation.");
		}
		
		try
		{
			reader.parse(inputsource);
		}
		catch (SAXException e)
		{
			if (e.getException() != null &&
				e.getException() instanceof RuntimeException)
			{
				throw (RuntimeException)e.getException();
			}
			else
			{
				throw new ParserExecutionErrorException(xmlPath, e);
			}
		}
		catch (IOException e)
		{
			throw new ParserExecutionErrorException(xmlPath, e);
		}
		
		if (mErrorRedirector.hasWarnings())
		{
			Logger.getLogger("com.uwyn.rife.xml").warning("The following XML warnings occured during the parsing of "+xmlPath+"'.\n"+StringUtils.join(formatExceptions(mErrorRedirector.getWarnings()), "\n"));
		}
		if (mErrorRedirector.hasErrors())
		{
			Logger.getLogger("com.uwyn.rife.xml").severe("The following XML errors occured during the parsing of "+xmlPath+"'.\n"+StringUtils.join(formatExceptions(mErrorRedirector.getErrors()), "\n"));
		}
		if (mErrorRedirector.hasFatalErrors())
		{
			throw new FatalParsingErrorsException(xmlPath, formatExceptions(mErrorRedirector.getFatalErrors()));
		}
		
		return reader;
	}
	
	private Collection<String> formatExceptions(Collection<SAXParseException> exceptions)
	{
		if (null == exceptions)
		{
			return null;
		}
		
		ArrayList<String> result = new ArrayList<String>();
		for (SAXParseException e: exceptions)
		{
			StringBuilder formatted = new StringBuilder();
			if (e.getSystemId() != null)
			{
				formatted.append(e.getSystemId());
			}
			
			if (e.getPublicId() != null)
			{
				if (formatted.length() > 0)
				{
					formatted.append(", ");
				}
				formatted.append(e.getPublicId());
			}
			
			if (e.getLineNumber() >= 0)
			{
				if (formatted.length() > 0)
				{
					formatted.append(", ");
				}
				formatted.append("line ");
				formatted.append(e.getLineNumber());
			}
			
			if (e.getColumnNumber() >= 0)
			{
				if (formatted.length() > 0)
				{
					formatted.append(", ");
				}
				formatted.append("col ");
				formatted.append(e.getColumnNumber());
			}
			
			if (formatted.length() > 0)
			{
				formatted.append(" : ");
			}
			formatted.append(e.getMessage());
			
			result.add(formatted.toString());
		}
		
		return result;
	}
}
