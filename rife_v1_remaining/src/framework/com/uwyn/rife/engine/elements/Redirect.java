/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Redirect.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.ElementInfo;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.exceptions.PropertyRequiredException;
import com.uwyn.rife.tools.Localization;
import com.uwyn.rife.tools.StringUtils;

@Elem
public class Redirect extends Element
{
	public void processElement()
	{
		String url = null;
		
		// obtain the mandatory to property
		String to = null;
		if (hasProperty("to"))
		{
			to = getPropertyString("to");
		}
		
		to = Localization.extractLocalizedUrl(to);
		
		if (null == to)
		{
			throw new PropertyRequiredException(getDeclarationName(), "to");
		}
		
		// obtain the type property
		String type = "url";
		if (hasProperty("type"))
		{
			type = getPropertyString("type");
		}
		
		// interprete to according to the redirect type
		if (type.equals("url"))
		{
			url = to;
		}
		else if (type.equals("element"))
		{
			ElementInfo element_info = getSite().resolveId(to, getElementInfo());
			if (null == element_info)
			{
				throw new IllegalArgumentException("The element '"+getDeclarationName()+"' specified an unknown element id '"+to+"' for redirection.");
			}
			url = element_info.getUrl();
		}
		
		// verify if the url was successfully obtained
		if (null == url)
		{
			throw new IllegalArgumentException("The element '"+getDeclarationName()+"' declared an unsupported redirect type '"+type+"'.");
		}
		
		String redirect = null;
		
		// handle full-blown urls
		if (url.startsWith("http:"))
		{
			redirect = url;
		}
		// handle absolute urls
		else if (url.startsWith("/"))
		{
			String root = getWebappRootUrl();
			root = root.substring(0, root.length()-1);
			
			StringBuilder buffer = new StringBuilder(root);
			buffer.append(url);
			redirect = buffer.toString();
		}
		// handle relative urls
		else
		{
			String root = getWebappRootUrl();
			
			StringBuilder buffer = new StringBuilder(root);
			
			String element_url = getElementInfo().getUrl();
			int last_slash = element_url.lastIndexOf("/");
			if (last_slash != -1)
			{
				buffer.append(element_url.substring(0, last_slash));
			}
			
			buffer.append(url);
			
			redirect = buffer.toString();
		}
		
		// obtain the redirectInputs property
		boolean redirect_inputs = false;
		if (hasProperty("redirectInputs"))
		{
			Object value = getProperty("redirectInputs");
			if (value instanceof Boolean)
			{
				redirect_inputs = ((Boolean)value).booleanValue();
			}
			else
			{
				redirect_inputs = StringUtils.convertToBoolean(String.valueOf(value));
			}
		}
		
		if (redirect_inputs)
		{
			String anchor = null;
			int index_anchor = redirect.indexOf('#');
			if (index_anchor != -1)
			{
				redirect = redirect.substring(0, index_anchor);
				anchor = redirect.substring(index_anchor);
			}
			
			String query = null;
			int index_query = redirect.indexOf('?');
			if (index_query != -1)
			{
				redirect = redirect.substring(0, index_query);
				query = redirect.substring(index_query+1);
			}
			
			boolean has_querystring_seperator = false;

			StringBuilder buffer = new StringBuilder(redirect);
			for (String input : getElementInfo().getInputNames())
			{
				// add query string parameters
				String[] values = getInputValues(input);
				if (null == values)
				{
					continue;
				}

				for (String value : values)
				{
					if (!has_querystring_seperator)
					{
						buffer.append("?");
						has_querystring_seperator = true;
					}
					else
					{
						buffer.append("&");
					}
					
					buffer.append(StringUtils.encodeUrl(input));
					buffer.append("=");
					buffer.append(StringUtils.encodeUrl(value));
				}
			}
			
			// add the previous query string
			if (query != null)
			{
				if (!has_querystring_seperator)
				{
					buffer.append("?");
				}
				buffer.append(query);
			}
			
			// add the previous anchor
			if (anchor != null)
			{
				buffer.append(anchor);
			}
			
			redirect =  buffer.toString();
		}
		
		
		sendRedirect(redirect);
	}
}

