/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MockConversation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import com.uwyn.rife.engine.Gate;
import com.uwyn.rife.engine.Site;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.ArrayUtils;
import com.uwyn.rife.tools.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;

/**
 * Simulates a conversation between a web browser and a servlet container.
 * <p>Cookies will be remembered between requests and can be easily examined.
 * To check which new cookies have been set during a request, the {@link
 * MockResponse#getNewCookieNames} method can be used.
 * <p>An instance of this class is tied to a regular {@link Site} structure
 * instance. Your tests can thus reference existing site XML declarations,
 * combine different sites into one, build a new site-structure on-the-fly in
 * Java, modify existing element declarations, override property injections,
 * ...
 * <p>Note that RIFE relies on {@link com.uwyn.rife.engine.EngineClassLoader}
 * to provide continuations functionalities to pure Java elements. If you want
 * to test elements that use continuations, you have to make sure the first
 * class in your test setup is loaded by {@link
 * com.uwyn.rife.engine.EngineClassLoader}. The easiest way to do so is to run
 * your main class with {@link RunWithEngineClassLoader}.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public class MockConversation
{
	final static String SESSION_ID_COOKIE = "JSESSIONID";
	final static String SESSION_ID_URL = "jsessionid";
	final static String SESSION_URL_PREFIX=";"+SESSION_ID_URL+"=";
	
	private Gate    mGate = null;
	
	private HashMap<String, Cookie>         mCookies = new HashMap<String, Cookie>();
	private HashMap<String, MockSession>    mSessions = new HashMap<String, MockSession>();
	private String                          mScheme = "http";
	private String                          mServerName = "localhost";
	private int                             mServerPort = 80;
	private String                          mContextPath = "";
	
	/**
	 * Creates a new <code>MockConversation</code> instance for a particular
	 * site.
	 *
	 * @param site the site structure that will be tested
	 * @since 1.1
	 */
	public MockConversation(Site site)
	throws EngineException
	{
		MockInitConfig initconfig = new MockInitConfig();
		mGate = new Gate(site);
		mGate.init(initconfig);
	}
	
	/**
	 * Perform a request for a particular URL.
	 *
	 * @param url the url that should be tested
	 * @return the response of the request as a {@link MockResponse} instance;
	 * or
	 * <p><code>null</code> if the scheme, hostname and port don't correspond
	 * to the conversation setup
	 * @see #doRequest(String, MockRequest)
	 * @since 1.1
	 */
	public MockResponse doRequest(String url)
	throws EngineException
	{
		return doRequest(url, new MockRequest());
	}
	
	/**
	 * Perform a request for a particular URL and request configuration.
	 * <p>The request can either be complete with the scheme and hostname, or
	 * an absolute path. These two URLs are thus considered the same:
	 * <pre>http://localhost/some/url?name1=value1&amp;name2=value2</pre>
	 * <pre>/some/url?name1=value1&amp;name2=value2</pre>
	 * <p>Note that when the complete URL form is used, it should correspond
	 * to the scheme, hostname and port configuration of this conversation.
	 *
	 * @param url the url that should be tested
	 * @param request the request that will be used
	 * @return the response of the request as a {@link MockResponse} instance;
	 * or
	 * <p><code>null</code> if the scheme, hostname and port don't correspond
	 * to the conversation setup
	 * @see #doRequest(String)
	 * @since 1.1
	 */
	public MockResponse doRequest(String url, MockRequest request)
	throws EngineException
	{
		if (null == url)        throw new IllegalArgumentException("url can't be null");
		if (null == request)    throw new IllegalArgumentException("request can't be null");
		
		request.setMockConversation(this);

		// strip away the server root URL, or fail the request in case
		// the url doesn't start with the correct server root
		String server_root = request.getServerRootUrl(-1);
		if (url.indexOf(":/") != -1)
		{
			if (!url.startsWith(server_root))
			{
				return null;
			}
			
			url = url.substring(server_root.length());
		}
		
		// add the parameters in the URL to the request parameters
		Map<String, String[]> parameters = extractParameters(url);
		if (parameters != null)
		{
			for (Map.Entry<String, String[]> entry : parameters.entrySet())
			{
				if (!request.hasParameter(entry.getKey()))
				{
					request.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}
		
		// get the path parameters
		String path_parameters = null;
		int path_parameters_index = url.indexOf(";");
		if (path_parameters_index != -1)
		{
			path_parameters = url.substring(0, path_parameters_index);
		}
		
		// remove the query string
		int index_query = url.indexOf("?");
		if (index_query != -1)
		{
			url = url.substring(0, index_query);
		}
		
		// perform the request
		MockResponse response = new MockResponse(this, request);
		request.setMockResponse(response);
		request.setRequestedSessionId(path_parameters);
		mGate.handleRequest("", url, request, response);
		return response;
	}
	
	/**
	 * Retrieves the scheme that is used by this conversation.
	 *
	 * @return the scheme of this conversation
	 * @see #setScheme
	 * @see #scheme
	 * @since 1.1
	 */
	public String getScheme()
	{
		return mScheme;
	}
	
	/**
	 * Sets the scheme that will be used by this conversation.
	 *
	 * @param scheme the scheme
	 * @see #getScheme
	 * @see #scheme
	 * @since 1.1
	 */
	public void setScheme(String scheme)
	{
		mScheme = scheme;
	}
	
	/**
	 * Sets the scheme that will be used by this conversation.
	 *
	 * @param scheme the scheme
	 * @return this <code>MockConversation</code> instance
	 * @see #getScheme
	 * @see #setScheme
	 * @since 1.1
	 */
	public MockConversation scheme(String scheme)
	{
		setScheme(scheme);
		
		return this;
	}
	
	/**
	 * Retrieves the server name that is used by this conversation.
	 *
	 * @return the server name of this conversation
	 * @see #setServerName
	 * @see #serverName
	 * @since 1.1
	 */
	public String getServerName()
	{
		return mServerName;
	}
	
	/**
	 * Sets the server name that will be used by this conversation.
	 *
	 * @param serverName the server name
	 * @see #getServerName
	 * @see #serverName
	 * @since 1.1
	 */
	public void setServerName(String serverName)
	{
		mServerName = serverName;
	}
	
	/**
	 * Sets the server name that will be used by this conversation.
	 *
	 * @param serverName the server name
	 * @return this <code>MockConversation</code> instance
	 * @see #getServerName
	 * @see #setServerName
	 * @since 1.1
	 */
	public MockConversation serverName(String serverName)
	{
		setServerName(serverName);
		
		return this;
	}
	
	/**
	 * Retrieves the server port that is used by this conversation.
	 *
	 * @return the server port of this conversation
	 * @see #setServerPort
	 * @see #serverPort
	 * @since 1.1
	 */
	public int getServerPort()
	{
		return mServerPort;
	}
	
	/**
	 * Sets the server port that will be used by this conversation.
	 *
	 * @param serverPort the server port
	 * @see #getServerPort
	 * @see #serverPort
	 * @since 1.1
	 */
	public void setServerPort(int serverPort)
	{
		mServerPort = serverPort;
	}
	
	/**
	 * Sets the server port that will be used by this conversation.
	 *
	 * @param serverPort the server port
	 * @return this <code>MockConversation</code> instance
	 * @see #getServerPort
	 * @see #setServerPort
	 * @since 1.1
	 */
	public MockConversation serverPort(int serverPort)
	{
		setServerPort(serverPort);
		
		return this;
	}
	
	/**
	 * Retrieves the context path that is used by this conversation.
	 *
	 * @return the context path of this conversation
	 * @see #setContextPath
	 * @see #contextPath
	 * @since 1.1
	 */
	public String getContextPath()
	{
		return mContextPath;
	}
	
	/**
	 * Sets the context path that will be used by this conversation.
	 *
	 * @param contextPath the context path
	 * @see #getContextPath
	 * @see #contextPath
	 * @since 1.1
	 */
	public void setContextPath(String contextPath)
	{
		mContextPath = contextPath;
	}
	
	/**
	 * Sets the context path that will be used by this conversation.
	 *
	 * @param contextPath the context path
	 * @return this <code>MockConversation</code> instance
	 * @see #getContextPath
	 * @see #setContextPath
	 * @since 1.1
	 */
	public MockConversation contextPath(String contextPath)
	{
		setContextPath(contextPath);
		
		return this;
	}
	
	/**
	 * Checks whether a cookie is present.
	 *
	 * @param name the name of the cookie.
	 * @return <code>true</code> if the cookie was present; or
	 * <p><code>false</code> otherwise
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookies()
	 * @see #addCookie(Cookie)
	 * @see #addCookie(String, String)
	 * @since 1.1
	 */
	public boolean hasCookie(String name)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");
		
		if (null == mCookies)
		{
			return false;
		}
		
		for (Cookie cookie : mCookies.values())
		{
			if (cookie.getName().equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Retrieves a cookie.
	 *
	 * @param name the name of the cookie.
	 * @return the instance of the cookie; or
	 * <p><code>null</code> if no such cookie is present
	 * @see #hasCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookies()
	 * @see #addCookie(Cookie)
	 * @see #addCookie(String, String)
	 * @since 1.1
	 */
	public Cookie getCookie(String name)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty");
		
		if (null == mCookies)
		{
			return null;
		}
		
		for (Cookie cookie : mCookies.values())
		{
			if (cookie.getName().equals(name))
			{
				return cookie;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the value of a cookie.
	 *
	 * @param name the name of the cookie.
	 * @return the value of the cookie; or
	 * <p><code>null</code> if no such cookie is present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookies()
	 * @see #addCookie(Cookie)
	 * @see #addCookie(String, String)
	 * @since 1.1
	 */
	public String getCookieValue(String name)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty");
		
		Cookie cookie = getCookie(name);
		if (null == cookie)
		{
			return null;
		}
		
		return cookie.getValue();
	}
	
	/**
	 * Retrieves all cookies.
	 *
	 * @return an array with all the cookies; or
	 * <p><code>null</code> if no cookies are present
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #addCookie(Cookie)
	 * @see #addCookie(String, String)
	 * @since 1.1
	 */
	public Cookie[] getCookies()
	{
		if (null == mCookies ||
			0 == mCookies.size())
		{
			return null;
		}
		
		Cookie[] cookies = new Cookie[mCookies.size()];
		mCookies.values().toArray(cookies);
		return cookies;
	}
	
	/**
	 * Add a cookie.
	 *
	 * @param cookie the cookie instance that will be added
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookies()
	 * @see #addCookie(String, String)
	 * @since 1.1
	 */
	public void addCookie(Cookie cookie)
	{
		if (null == cookie)
		{
			return;
		}
		
		mCookies.put(buildCookieId(cookie), cookie);
	}
	
	/**
	 * Add a cookie with only a name and a value, the other fields will be
	 * empty.
	 *
	 * @param name the name of the cookie
	 * @param value the value of the cookie
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookies()
	 * @see #addCookie(Cookie)
	 * @since 1.1
	 */
	public void addCookie(String name, String value)
	{
		if (null == name)       throw new IllegalArgumentException("name can't be null");
		if (0 == name.length()) throw new IllegalArgumentException("name can't be empty");

		addCookie(new Cookie(name, value));
	}
	
	/**
	 * Add a cookie.
	 *
	 * @param cookie the cookie instance that will be added
	 * @return this <code>MockConversation</code> instance
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookies()
	 * @see #addCookie(Cookie)
	 * @see #addCookie(String, String)
	 * @since 1.1
	 */
	public MockConversation cookie(Cookie cookie)
	{
		addCookie(cookie);
		
		return this;
	}
	
	/**
	 * Add a cookie with only a name and a value, the other fields will be
	 * empty.
	 *
	 * @param name the name of the cookie
	 * @param value the value of the cookie
	 * @return this <code>MockConversation</code> instance
	 * @see #hasCookie(String)
	 * @see #getCookie(String)
	 * @see #getCookieValue(String)
	 * @see #getCookies()
	 * @see #addCookie(Cookie)
	 * @see #addCookie(String, String)
	 * @since 1.1
	 */
	public MockConversation cookie(String name, String value)
	{
		addCookie(name, value);
		
		return this;
	}
	
	static String buildCookieId(Cookie cookie)
	{
		StringBuilder cookie_id = new StringBuilder();
		if (cookie.getDomain() != null)
		{
			cookie_id.append(cookie.getDomain());
		}
		cookie_id.append("\n");
		if (cookie.getPath() != null)
		{
			cookie_id.append(cookie.getPath());
		}
		cookie_id.append("\n");
		if (cookie.getName() != null)
		{
			cookie_id.append(cookie.getName());
		}
		return cookie_id.toString();
	}
	
	static Map<String, String[]> extractParameters(String url)
	{
		if (null == url)
		{
			return null;
		}
		
		int index_query = url.indexOf("?");
		int index_anchor = url.indexOf("#");
		
		if (-1 == index_query)
		{
			return null;
		}
		
		String query = null;
		if (index_anchor != -1)
		{
			query = url.substring(index_query+1, index_anchor);
		}
		else
		{
			query = url.substring(index_query+1);
		}
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		
		List<String> query_parts = StringUtils.split(query, "&");
		for (String query_part : query_parts)
		{
			List<String> parameter = StringUtils.split(query_part, "=");
			if (2 == parameter.size())
			{
				try
				{
					String name = URLDecoder.decode(parameter.get(0), StringUtils.ENCODING_ISO_8859_1);
					String value = URLDecoder.decode(parameter.get(1), StringUtils.ENCODING_ISO_8859_1);
					
					String[] values = parameters.get(name);
					if (null == values)
					{
						values = new String[] {value};
					}
					else
					{
						values = ArrayUtils.join(values, value);
					}
					
					parameters.put(name, values);
				}
				catch (UnsupportedEncodingException e)
				{
					// can't happen, encoding is always supported
				}
			}
		}
		
		return parameters;
	}
	
	MockSession getSession(String id)
	{
		return mSessions.get(id);
	}
	
	MockSession newHttpSession()
	{
		MockSession session = new MockSession(this);
		mSessions.put(session.getId(), session);
		
		return session;
	}
	
	void removeSession(String id)
	{
		mSessions.remove(id);
	}
}
