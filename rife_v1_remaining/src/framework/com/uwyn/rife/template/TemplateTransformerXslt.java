/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateTransformerXslt.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.template.exceptions.FilterNotFoundException;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.template.exceptions.TransformerErrorException;
import com.uwyn.rife.xml.XmlInputSource;
import com.uwyn.rife.xml.XmlUriResolver;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class TemplateTransformerXslt implements TemplateTransformer
{
	private ResourceFinder			mResourceFinder = ResourceFinderClasspath.getInstance();
	private ArrayList<SAXSource>	mFilters = null;
	private Properties				mProperties = null;
	private String					mState = null;

	public final static String	OUTPUT_METHOD = "method";
	public final static String	OUTPUT_INDENT = "indent";
	public final static String	OUTPUT_MEDIA_TYPE = "media-type";
	public final static String	OUTPUT_VERSION = "version";
	public final static String	OUTPUT_INDENT_AMOUNT = "{http\u003a//xml.apache.org/xalan}indent-amount";
	public final static String	OUTPUT_USE_URL_ESCAPING = "{http\u003a//xml.apache.org/xalan}use-url-escaping";
	public final static String	OUTPUT_OMIT_META_TAG = "{http\u003a//xml.apache.org/xalan}omit-meta-tag";
	
	public String getState()
	{
		if (null == mState)
		{
			StringBuilder	state = new StringBuilder();
			
			if (mFilters != null)
			{
				for (SAXSource filter : mFilters)
				{
					state.append(filter.getInputSource().toString());
					state.append(";");
				}
			}
			
			if (mProperties != null)
			{
				state.append("\n");
				Enumeration property_names = mProperties.propertyNames();
				String		property_name = null;
				while (property_names.hasMoreElements())
				{
					property_name = (String)property_names.nextElement();
					state.append(property_name);
					state.append("=");
					state.append(mProperties.getProperty(property_name));
					state.append(";");
				}
			}
			
			mState = state.toString();
		}
		
		return mState;
	}
	
	public void addFilter(String xmlPath)
	throws TemplateException
	{
		if (null == xmlPath)		throw new IllegalArgumentException("xmlPath can't be null");
		if (0 == xmlPath.length())	throw new IllegalArgumentException("xmlPath can't be empty");
		
		if (null == mFilters)
		{
			mFilters = new ArrayList<SAXSource>();
		}
		
		URL resource = mResourceFinder.getResource(xmlPath);
		if (null == resource)
		{
			throw new FilterNotFoundException(xmlPath);
		}
		
		mState = null;
		mFilters.add(new SAXSource(new XmlInputSource(resource)));
	}
	
	public void clearFilters()
	{
		mState = null;
		mFilters = null;
	}
	
	public ResourceFinder getResourceFinder()
	{
		return mResourceFinder;
	}
	
	public void setResourceFinder(ResourceFinder resourceFinder)
	{
		mResourceFinder = resourceFinder;
	}

	public void setOutputProperty(String name, String value)
	throws IllegalArgumentException
	{
		if (null == mProperties)
		{
			mProperties = new Properties();
		}
		
		mState = null;
		mProperties.setProperty(name, value);
	}

	public void setOutputProperties(Properties properties)
	throws IllegalArgumentException
	{
		mState = null;
		mProperties = properties;
	}
	
	public Collection<URL> transform(String templateName, URL resource, OutputStream result, String encoding)
	throws TemplateException
	{
		ArrayList<URL>	stylesheets = new ArrayList<URL>();
		
		XmlInputSource	input = new XmlInputSource(resource);
		
		if (encoding != null)
		{
			input.setEncoding(encoding);
		}
		
		try
		{
			SAXParserFactory parser_factory = SAXParserFactory.newInstance();
			parser_factory.setNamespaceAware(true);
			
			SAXParser	parser = parser_factory.newSAXParser();
			XMLReader	reader = parser.getXMLReader();
			XMLReader	parent = reader;
			XMLFilter	filter = null;
			
			SAXTransformerFactory transformer_factory = (SAXTransformerFactory)TransformerFactory.newInstance();
			transformer_factory.setURIResolver(new XmlUriResolver(mResourceFinder));
			
			// try to obtain the associated stylesheet and use it as the first filter
			Source stylesheet = transformer_factory.getAssociatedStylesheet(new SAXSource(input), null, null, null);
			if (stylesheet != null)
			{
				filter = transformer_factory.newXMLFilter(stylesheet);
				filter.setParent(parent);
				parent = filter;

				// store the stylesheet so that it can be included in the modification checks
				stylesheets.add(new URL(stylesheet.getSystemId()));

				reader = filter;
			}
			
			// set up the additional filters
			if (mFilters != null &&
				mFilters.size() > 0)
			{
				Iterator<SAXSource>	filters_it = mFilters.iterator();
				while (filters_it.hasNext())
				{
					stylesheet = filters_it.next();
					filter = transformer_factory.newXMLFilter(stylesheet);
					filter.setParent(parent);
					parent = filter;
					
					// store the stylesheet so that it can be included in the modification checks
					stylesheets.add(new URL(stylesheet.getSystemId()));
				}
				reader = filter;
			}
			
			// setup the transformer by applying the custom properties
			Transformer	transformer = transformer_factory.newTransformer();
			if (mProperties != null)
			{
				Properties merged_properties = transformer.getOutputProperties();
				merged_properties.putAll(mProperties);
				transformer.setOutputProperties(merged_properties);
			}

			// perform the transformation
			StreamResult	stream_result = new StreamResult(result);
			SAXSource		transform_source = new SAXSource(reader, input);
			transformer.transform(transform_source, stream_result);
		}
		catch (MalformedURLException e)
		{
			throw new TransformerErrorException(resource, e);
		}
		catch (TransformerConfigurationException e)
		{
			throw new TransformerErrorException(resource, e);
		}
		catch (TransformerException e)
		{
			throw new TransformerErrorException(resource, e);
		}
		catch (FactoryConfigurationError e)
		{
			throw new TransformerErrorException(resource, e);
		}
		catch (ParserConfigurationException e)
		{
			throw new TransformerErrorException(resource, e);
		}
		catch (SAXException e)
		{
			throw new TransformerErrorException(resource, e);
		}
		
		return stylesheets;
	}

	public String getEncoding()
	{
		if (mProperties != null &&
			mProperties.containsKey("encoding"))
		{
			return mProperties.getProperty("encoding");
		}
		
		return null;
	}
}

