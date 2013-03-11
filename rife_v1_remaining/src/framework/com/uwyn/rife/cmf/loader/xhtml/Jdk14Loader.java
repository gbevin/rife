/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Jdk14Loader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.xhtml;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.loader.XhtmlContentLoaderBackend;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.xml.LoggingErrorRedirector;
import com.uwyn.rife.xml.XmlEntityResolver;
import com.uwyn.rife.xml.XmlErrorRedirector;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class Jdk14Loader extends XhtmlContentLoaderBackend
{
	public String loadFromString(String data, boolean fragment, Set<String> errors)
	throws ContentManagerException
	{
		return new LoaderDelegate().load(data, fragment, errors);
	}
	
	public boolean isBackendPresent()
	{
		try
		{
			return null != Class.forName("org.xml.sax.XMLReader");
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
	
	private static class LoaderDelegate extends DefaultHandler
	{
		public String load(String data, boolean fragment, Set<String> errors)
		throws ContentManagerException
		{
            XmlEntityResolver	entity_resolver = null;
            XmlErrorRedirector	error_redirector = null;

			String complete_page = data;

			Reader reader = null;
			if (fragment)
			{
				Template t = TemplateFactory.XHTML.get("cmf.container.template");
				t.setValue("fragment", data);
				complete_page = t.getContent();
			}
	
			reader = new StringReader(complete_page);
			
			try
			{
				InputSource inputsource = new InputSource(reader);
				
				entity_resolver = new XmlEntityResolver(ResourceFinderClasspath.getInstance())
					.addToCatalog("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", "/dtd/cmf/xhtml1-transitional.dtd")
					.addToCatalog("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", "/dtd/cmf/xhtml1-strict.dtd")
					.addToCatalog("http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd", "/dtd/cmf/xhtml1-frameset.dtd")
					.addToCatalog("http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent", "/dtd/cmf/xhtml-lat1.ent")
					.addToCatalog("http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent", "/dtd/cmf/xhtml-symbol.ent")
					.addToCatalog("http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent", "/dtd/cmf/xhtml-special.ent")
					.restrictToCatalog(true);
				error_redirector = new LoggingErrorRedirector();
				
				XMLReader xml_reader = null;
				
				try
				{
					xml_reader = XMLReaderFactory.createXMLReader();
				}
				catch (SAXException e)
				{
					try
					{
						xml_reader = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
					}
					catch (SAXException e2)
					{
						throw new XmlErrorException(e2);
					}
				}
				
				xml_reader.setEntityResolver(entity_resolver);
				xml_reader.setErrorHandler(error_redirector);
				
				try
				{
					xml_reader.setFeature("http://xml.org/sax/features/validation", true);
				}
				catch (SAXException e)
				{
					throw new XmlErrorException("The parser '"+xml_reader.getClass().getName()+"' doesn't support validation.", e);
				}
				
				try
				{
					xml_reader.parse(inputsource);
				}
				catch (SAXParseException e)
				{
					if (errors != null)
					{
						errors.add(formatException(fragment, e));
					}
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
						throw new XmlErrorException(e);
					}
				}
				catch (IOException e)
				{
					throw new XmlErrorException(e);
				}
				
				if (errors != null)
				{
					if (error_redirector.hasErrors())
					{
						errors.addAll(formatExceptions(fragment, error_redirector.getErrors()));
					}
					if (error_redirector.hasFatalErrors())
					{
						errors.addAll(formatExceptions(fragment, error_redirector.getFatalErrors()));
					}
				}
			}
			catch (RuntimeException e)
			{
				if (errors != null)
				{
					errors.add(e.getMessage());
				}
				return null;
			}
			
			if ((errors != null &&
				 errors.size() > 0) ||
				(error_redirector.hasErrors() ||
				 error_redirector.hasFatalErrors()))
			{
				return null;
			}
			
			return data;
		}
		
		private Collection<String> formatExceptions(boolean fragment, Collection<SAXParseException> exceptions)
		{
			if (null == exceptions)
			{
				return null;
			}
			
			ArrayList<String> result = new ArrayList<String>();
			for (SAXParseException e : exceptions)
			{
				result.add(formatException(fragment, e));
			}
			
			return result;
		}

		private String formatException(boolean fragment, SAXParseException e)
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
				if (fragment)
				{
					formatted.append(e.getLineNumber()-3);
				}
				else
				{
					formatted.append(e.getLineNumber());
				}
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
			
			return formatted.toString();
		}
	}
}
