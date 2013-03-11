/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MockLink.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import com.uwyn.rife.engine.RequestMethod;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Node;

/**
 * Corresponds to a link in a HTML document after it has been parsed with
 * {@link ParsedHtml#parse}.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public class MockLink
{
	private MockResponse            mResponse;
	private Node                    mNode;
	private String                  mText;
	private Map<String, String[]>   mParameters;
	
	MockLink(MockResponse response, Node node)
	{
		assert node != null;
		
		mResponse = response;
		mNode = node;
		mText = mNode.getTextContent();
		
		mParameters = MockConversation.extractParameters(getHref());
		if (null == mParameters)
		{
			mParameters = new LinkedHashMap<String, String[]>();
		}
	}
	
	/**
	 * Retrieves the DOM XML node that this link corresponds to.
	 * 
	 * @return the corresponding DOM XML node
	 * @since 1.0
	 */
	public Node getNode()
	{
		return mNode;
	}
	
	/**
	 * Creates a new {@link MockRequest} that contains the parameters this
	 * link.
	 * 
	 * @return the created <code>MockRequest</code>
	 * @since 1.0
	 */
	public MockRequest getRequest()
	{
		return new MockRequest().method(RequestMethod.GET);
	}
	
	/**
	 * Click this link with its current parameters and returns the response.
	 * 
	 * @return the resulting {@link MockResponse}
	 * @since 1.0
	 */
	public MockResponse click()
	{
		return mResponse.getMockConversation().doRequest(getHref(), getRequest());
	}
	
	private String getAttribute(String attributeName)
	{
		return ParsedHtml.getNodeAttribute(mNode, attributeName, null);
	}
	
	/**
	 * Retrieves all the parameters of this link.
	 * 
	 * @return a <code>Map</code> of the parameters with the names as the keys
	 * and their value arrays as the values
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @since 1.1
	 */
	public Map<String, String[]> getParameters()
	{
		return mParameters;
	}
	
	/**
	 * Retrieves all the parameter names of this link.
	 * 
	 * @return a <code>Collection</code> of the parameter names
	 * @see #getParameters
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @since 1.1
	 */
	public Collection<String> getParameterNames()
	{
		return mParameters.keySet();
	}
	
	/**
	 * Checks whether a named parameter is present in this link.
	 * 
	 * @param name the name of the parameter to check
	 * @return <code>true</code> if the parameter is present; or
	 * <p><code>false</code> otherwise
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @since 1.1
	 */
	public boolean hasParameter(String name)
	{
		return mParameters.containsKey(name);
	}
	
	/**
	 * Retrieves the first value of a parameter in this link.
	 * 
	 * @param name the name of the parameter
	 * @return the first value of the parameter; or
	 * <p><code>null</code> if no such parameter could be found
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValues
	 * @since 1.1
	 */
	public String getParameterValue(String name)
	{
		String[] values = getParameterValues(name);
		if (null == values ||
			0 == values.length)
		{
			return null;
		}
		
		return values[0];
	}
	
	/**
	 * Retrieves the values of a parameter in this link.
	 * 
	 * @param name the name of the parameter
	 * @return the values of the parameter; or
	 * <p><code>null</code> if no such parameter could be found
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @since 1.1
	 */
	public String[] getParameterValues(String name)
	{
		return mParameters.get(name);
	}
	
	/**
	 * Retrieves the content of this link's <code>id</code> attribute.
	 * 
	 * @return the content of the <code>id</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getId()
	{
		return getAttribute("id");
	}
	
	/**
	 * Retrieves the content of this link's <code>class</code> attribute.
	 * 
	 * @return the content of the <code>class</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getClassName()
	{
		return getAttribute("class");
	}
	
	/**
	 * Retrieves the content of this link's <code>title</code> attribute.
	 * 
	 * @return the content of the <code>title</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getTitle()
	{
		return getAttribute("title");
	}
	
	/**
	 * Retrieves the content of this link's <code>href</code> attribute.
	 * 
	 * @return the content of the <code>href</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getHref()
	{
		return getAttribute("href");
	}
	
	/**
	 * Retrieves the content of this link's <code>target</code> attribute.
	 * 
	 * @return the content of the <code>target</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getTarget()
	{
		return getAttribute("target");
	}
	
	/**
	 * Retrieves the content of this link's <code>name</code> attribute.
	 * 
	 * @return the content of the <code>name</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getName()
	{
		return getAttribute("name");
	}
	
	/**
	 * Retrieves the text that this links surrounds.
	 * 
	 * @return the surrounded text
	 * @since 1.0
	 */
	public String getText()
	{
		return mText;
	}
}
