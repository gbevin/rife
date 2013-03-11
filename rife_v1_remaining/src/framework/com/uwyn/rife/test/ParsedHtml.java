/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParsedHtml.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Retrieves the text content of a {@link MockResponse} and parses it as HTML.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public class ParsedHtml
{
	private static final String DEFAULT_ENCODING = "http://cyberneko.org/html/properties/default-encoding";
	private static final String TAG_NAME_CASE = "http://cyberneko.org/html/properties/names/elems";
	private static final String ATTRIBUTE_NAME_CASE = "http://cyberneko.org/html/properties/names/attrs";

	private MockResponse	mResponse;
	private Document		mDocument;
	private List<MockForm>	mForms = new ArrayList<MockForm>();
	private List<MockLink>	mLinks = new ArrayList<MockLink>();
	
	private ParsedHtml(MockResponse response, Document document)
	{
		mResponse = response;
		mDocument = document;
		
		// get all the forms
		NodeList form_nodes = document.getElementsByTagName("form");
		for (int i = 0; i < form_nodes.getLength(); i++)
		{
			Node form_node = form_nodes.item(i);
			MockForm form = new MockForm(mResponse, form_node);
			mForms.add(form);
		}
		
		// get all the links
		NodeList link_nodes = document.getElementsByTagName("a");
		for (int i = 0; i < link_nodes.getLength(); i++)
		{
			Node link_node = link_nodes.item(i);
			MockLink link = new MockLink(mResponse, link_node);
			mLinks.add(link);
		}
	}
	
	/**
	 * Parses the text content of a {@link MockResponse} object as HTML and
	 * returns the result as an instance of <code>ParsedHtml</code>.
	 * 
	 * @param response the response whose text content will be parsed
	 * @return the resulting instance of <code>ParsedHtml</code>
	 * @since 1.1
	 */
	public static ParsedHtml parse(MockResponse response)
	throws IOException, SAXException
	{
		return parse(response, response.getText());
	}
	
	static ParsedHtml parse(MockResponse response, String text)
	throws IOException, SAXException
	{
		Reader reader = new StringReader(text);
		InputSource inputsource = new InputSource(reader);
		
		HTMLConfiguration config = new HTMLConfiguration();
		config.setProperty(DEFAULT_ENCODING, "UTF-8");
		config.setProperty(TAG_NAME_CASE, "lower");
		config.setProperty(ATTRIBUTE_NAME_CASE, "lower");
		DOMParser parser = new DOMParser(config);
		
		parser.parse(inputsource);

		Document document = parser.getDocument();
		
		return new ParsedHtml(response, document);
	}
	
	/**
	 * Retrieves the DOM XML document that corresponds to the parsed HTML.
	 * 
	 * @return the DOM XML document
	 * @since 1.1
	 */
	public Document getDocument()
	{
		return mDocument;
	}
	
	/**
	 * Retrieves the text of the <code>title</code> tag.
	 * 
	 * @return the title
	 * @since 1.1
	 */
	public String getTitle()
	{
		NodeList list = mDocument.getElementsByTagName("title");
		if (0 == list.getLength())
		{
			return null;
		}
		
		return list.item(0).getTextContent();
	}
	
	/**
	 * Retrieves the list of all the forms in the HTML document.
	 * 
	 * @return a list with {@link MockForm} instances
	 * @see #getFormWithName
	 * @see #getFormWithId
	 * @since 1.1
	 */
	public List<MockForm> getForms()
	{
		return mForms;
	}
	
	/**
	 * Retrieves the first form in the HTML document with a particular
	 * <code>name</code> attribute.
	 * 
	 * @param name the content of the <code>name</code> attribute
	 * @return the first {@link MockForm} whose <code>name</code> attribute
	 * matches; or
	 * <p><code>null</code> if no such form could be found
	 * @see #getForms
	 * @see #getFormWithId
	 * @since 1.1
	 */
	public MockForm getFormWithName(String name)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		for (MockForm form : mForms)
		{
			if (name.equals(form.getName()))
			{
				return form;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the first form in the HTML document with a particular
	 * <code>id</code> attribute.
	 * 
	 * @param id the content of the <code>id</code> attribute
	 * @return the first {@link MockForm} whose <code>id</code> attribute
	 * matches; or
	 * <p><code>null</code> if no such form could be found
	 * @see #getForms
	 * @see #getFormWithName
	 * @since 1.1
	 */
	public MockForm getFormWithId(String id)
	{
		if (null == id)       throw new IllegalArgumentException("id can't be null");
		if (0 == id.length()) throw new IllegalArgumentException("id can't be empty");
		
		for (MockForm form : mForms)
		{
			if (form.getId().equals(id))
			{
				return form;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the list of all the links in the HTML document.
	 * 
	 * @return a list with {@link MockLink} instances
	 * @see #getLinkWithName
	 * @see #getLinkWithId
	 * @see #getLinkWithText
	 * @see #getLinkWithImageAlt
	 * @see #getLinkWithImageName
	 * @since 1.1
	 */
	public List<MockLink> getLinks()
	{
		return mLinks;
	}
	
	/**
	 * Retrieves the first link in the HTML document with a particular
	 * <code>name</code> attribute.
	 * 
	 * @param name the content of the <code>name</code> attribute
	 * @return the first {@link MockLink} whose <code>name</code> attribute
	 * matches; or
	 * <p><code>null</code> if no such link could be found
	 * @see #getLinks
	 * @see #getLinkWithId
	 * @see #getLinkWithText
	 * @see #getLinkWithImageAlt
	 * @see #getLinkWithImageName
	 * @since 1.1
	 */
	public MockLink getLinkWithName(String name)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		
		for (MockLink link : mLinks)
		{
			if (link.getName().equals(name))
			{
				return link;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the first link in the HTML document with a particular
	 * <code>id</code> attribute.
	 * 
	 * @param id the content of the <code>id</code> attribute
	 * @return the first {@link MockLink} whose <code>id</code> attribute
	 * matches; or
	 * <p><code>null</code> if no such link could be found
	 * @see #getLinks
	 * @see #getLinkWithName
	 * @see #getLinkWithText
	 * @see #getLinkWithImageAlt
	 * @see #getLinkWithImageName
	 * @since 1.1
	 */
	public MockLink getLinkWithId(String id)
	{
		if (null == id)       throw new IllegalArgumentException("id can't be null");
		if (0 == id.length()) throw new IllegalArgumentException("id can't be empty");
		
		for (MockLink link : mLinks)
		{
			if (id.equals(link.getId()))
			{
				return link;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the first link in the HTML document that surrounds a particular
	 * text.
	 * 
	 * @param text the surrounded text
	 * @return the first {@link MockLink} whose surrounded text matches; or
	 * <p><code>null</code> if no such link could be found
	 * @see #getLinks
	 * @see #getLinkWithName
	 * @see #getLinkWithId
	 * @see #getLinkWithText
	 * @see #getLinkWithImageName
	 * @since 1.1
	 */
	public MockLink getLinkWithText(String text)
	{
		if (null == text)       throw new IllegalArgumentException("text can't be null");

		for (MockLink link : mLinks)
		{
			if (link.getText() != null &&
				link.getText().equals(text))
			{
				return link;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the first link in the HTML document that surrounds an
	 * <code>img</code> tag with a certain <code>alt</code> attribute.
	 * 
	 * @param alt the content of the <code>alt</code> attribute
	 * @return the first {@link MockLink} that has an <code>img</code> tag
	 * whose <code>alt</code> attribute matches; or
	 * <p><code>null</code> if no such link could be found
	 * @see #getLinks
	 * @see #getLinkWithName
	 * @see #getLinkWithId
	 * @see #getLinkWithText
	 * @see #getLinkWithImageName
	 * @since 1.1
	 */
	public MockLink getLinkWithImageAlt(String alt)
	{
		if (null == alt)       throw new IllegalArgumentException("alt can't be null");
		
		for (MockLink link : mLinks)
		{
			Node node = link.getNode();
			NodeList child_nodes = node.getChildNodes();
			if (child_nodes != null &&
				child_nodes.getLength() > 0)
			{
				for (int i = 0; i < child_nodes.getLength(); i++)
				{
					Node child_node = child_nodes.item(i);
					if ("img".equals(child_node.getNodeName()))
					{
						String alt_text = getNodeAttribute(child_node, "alt", null);
						if (alt_text != null &&
							alt_text.equals(alt))
						{
							return link;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the first link in the HTML document that surrounds an
	 * <code>img</code> tag with a certain <code>name</code> attribute.
	 * 
	 * @param name the content of the <code>name</code> attribute
	 * @return the first {@link MockLink} that has an <code>img</code> tag
	 * whose <code>name</code> attribute matches; or
	 * <p><code>null</code> if no such link could be found
	 * @see #getLinks
	 * @see #getLinkWithName
	 * @see #getLinkWithId
	 * @see #getLinkWithText
	 * @see #getLinkWithImageAlt
	 * @since 1.1
	 */
	public MockLink getLinkWithImageName(String name)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		
		for (MockLink link : mLinks)
		{
			Node node = link.getNode();
			NodeList child_nodes = node.getChildNodes();
			if (child_nodes != null &&
				child_nodes.getLength() > 0)
			{
				for (int i = 0; i < child_nodes.getLength(); i++)
				{
					Node child_node = child_nodes.item(i);
					if ("img".equals(child_node.getNodeName()))
					{
						String alt_text = getNodeAttribute(child_node, "name", null);
						if (alt_text != null &&
							alt_text.equals(name))
						{
							return link;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the value of the attribute of an XML DOM node.
	 * 
	 * @param node the node where the attribute should be obtained from
	 * @param attributeName the name of the attribute
	 * @return the value of the attribute; or
	 * <p><code>null</code> if no attribute could be found
	 * @since 1.2
	 */
	public static String getNodeAttribute(Node node, String attributeName)
	{
		return getNodeAttribute(node, attributeName, null);
	}
	
	static String getNodeAttribute(Node node, String attributeName, String defaultValue)
	{
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null) return defaultValue;
		
		Node attribute = attributes.getNamedItem(attributeName);
		return (attribute == null) ? defaultValue : attribute.getNodeValue();
	}
}
