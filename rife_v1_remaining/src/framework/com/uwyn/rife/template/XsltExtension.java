/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XsltExtension.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.tools.ExceptionUtils;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.transformer.TransformerImpl;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.ContentHandler;

public class XsltExtension implements ErrorListener
{
	public void fatalError(TransformerException exception)
	throws TransformerException
	{
		Logger.getLogger("com.uwyn.rife.template").severe(ExceptionUtils.getExceptionStackTrace(exception));
	}
	
	public void error(TransformerException exception)
	throws TransformerException
	{
		Logger.getLogger("com.uwyn.rife.template").severe(ExceptionUtils.getExceptionStackTrace(exception));
	}
	
	public void warning(TransformerException exception)
	throws TransformerException
	{
		Logger.getLogger("com.uwyn.rife.template").warning(ExceptionUtils.getExceptionStackTrace(exception));
	}
	
	public void value(XSLProcessorContext context, ElemExtensionCall element)
	throws FactoryConfigurationError, IOException, ParserConfigurationException, TransformerException
	{
		// ensure that the name of the tag has been provided
		String name = element.getAttribute("name");
		if (null == name)
		{
			return;
		}

		// build a document for constructing the alternate output elements
		DocumentBuilderFactory	factory = null;
		DocumentBuilder			builder = null;
		Document				document = null;
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(false);
		builder = factory.newDocumentBuilder();
		document = builder.newDocument();
		
		if (element.hasChildNodes())
		{
			// create several document fragments to create the start and end tags
			DocumentFragment	fragment_start = null;
			DocumentFragment	fragment_children = null;
			DocumentFragment	fragment_end = null;
			fragment_start = document.createDocumentFragment();
			fragment_children = document.createDocumentFragment();
			fragment_end = document.createDocumentFragment();
			
			fragment_start.appendChild(document.createComment("V '"+name+"'"));
			fragment_end.appendChild(document.createComment("/V"));
	
			// process all child nodes and output the result to the middle
			// document fragment
			DOMResult		children_result = null;
			TransformerImpl	transformer = null;
			ContentHandler	handler = null;
			children_result = new DOMResult();
			children_result.setNode(fragment_children);
			transformer = context.getTransformer();
			handler = createContentHandler(transformer, children_result);
			transformer.setErrorListener(this);
			transformer.executeChildTemplates(element, context.getContextNode(), context.getMode(), handler);
			
			// output the document fragments
			context.outputToResultTree(context.getStylesheet(), fragment_start);
			context.outputToResultTree(context.getStylesheet(), fragment_children);
			context.outputToResultTree(context.getStylesheet(), fragment_end);
		}
		else
		{
			// obtain a document fragment and add the correct comment to it
			// so that RIFE's template engine understands it
			DocumentFragment	fragment_short = document.createDocumentFragment();
			fragment_short.appendChild(document.createComment("V '"+name+"'/"));
	
			// output the document fragment
			context.outputToResultTree(context.getStylesheet(), fragment_short);
		}
	}

	private ContentHandler createContentHandler(TransformerImpl transformer, DOMResult childrenResult)
	throws TransformerException
	{
		ContentHandler handler = null;
		
		// support ibm and sun jdk which both contain a different version of
		// xalan
		try
		{
			Method method = null;
			try
			{
				method = transformer.getClass().getMethod("createResultContentHandler", Result.class);
			}
			catch (NoSuchMethodException e)
			{
				try
				{
					method = transformer.getClass().getMethod("createSerializationHandler", Result.class);
				}
				catch (NoSuchMethodException e2)
				{
					throw new TransformerException(e);
				}
			}
			handler = (ContentHandler)method.invoke(transformer, childrenResult);
		}
		catch (Exception e)
		{
			throw new TransformerException(e);
		}
		
		return handler;
	}

	public void block(XSLProcessorContext context, ElemExtensionCall element)
	throws FactoryConfigurationError, IOException, ParserConfigurationException, TransformerException
	{
		block("B", context, element);
	}

	public void blockvalue(XSLProcessorContext context, ElemExtensionCall element)
	throws FactoryConfigurationError, IOException, ParserConfigurationException, TransformerException
	{
		block("BV", context, element);
	}
	
	private void block(String tagName, XSLProcessorContext context, ElemExtensionCall element)
	throws FactoryConfigurationError, IOException, ParserConfigurationException, TransformerException
	{
		// ensure that the name of the tag has been provided
		String name = element.getAttribute("name");
		if (null == name)
		{
			return;
		}

		// build a document for constructing the alternate output elements
		DocumentBuilderFactory	factory = null;
		DocumentBuilder			builder = null;
		Document				document = null;
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(false);
		builder = factory.newDocumentBuilder();
		document = builder.newDocument();
		
		// create several document fragments to create the start and end tags
		DocumentFragment	fragment_start = null;
		DocumentFragment	fragment_end = null;
		fragment_start = document.createDocumentFragment();
		fragment_end = document.createDocumentFragment();
		fragment_start.appendChild(document.createComment(tagName+" '"+name+"'"));
		fragment_end.appendChild(document.createComment("/"+tagName));

		// output the start tag document fragment
		context.outputToResultTree(context.getStylesheet(), fragment_start);
		if (element.hasChildNodes())
		{
			// if there are child nodes, create a middle document fragment
			DOMResult			children_result = new DOMResult();
			DocumentFragment	fragment_children = null;
			fragment_children = document.createDocumentFragment();
			children_result.setNode(fragment_children);
			
			// process all child nodes and output the result to the middle
			// document fragment
			TransformerImpl	transformer = context.getTransformer();
			ContentHandler	handler = createContentHandler(transformer, children_result);
			transformer.setErrorListener(this);
			transformer.executeChildTemplates(element, context.getContextNode(), context.getMode(), handler);
			// output the middle document fragment
			context.outputToResultTree(context.getStylesheet(), fragment_children);
		}
		// output the end tag document fragment
		context.outputToResultTree(context.getStylesheet(), fragment_end);
	}
	
	public void include(XSLProcessorContext context, ElemExtensionCall element)
	throws FactoryConfigurationError, IOException, ParserConfigurationException, TransformerException
	{
		// ensure that the name of the tag has been provided
		String name = element.getAttribute("name");
		if (null == name)
		{
			return;
		}

		// build a document for constructing the alternate output elements
		DocumentBuilderFactory	factory = null;
		DocumentBuilder			builder = null;
		Document				document = null;
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(false);
		builder = factory.newDocumentBuilder();
		document = builder.newDocument();
		
		// obtain a document fragment and add the correct comment to it
		// so that RIFE's template engine understands it
		DocumentFragment	fragment = document.createDocumentFragment();
		fragment.appendChild(document.createComment("I '"+name+"'/"));

		// output the document fragment
		context.outputToResultTree(context.getStylesheet(), fragment);
	}
}
