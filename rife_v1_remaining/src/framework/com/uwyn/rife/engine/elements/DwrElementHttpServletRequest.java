/*
 * Copyright 2001-2008 Joe Walker <joe[remove] at getahead dot ltd dot uk> and
 * Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DwrElementHttpServletRequest.java 3943 2008-04-27 09:09:02Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.tools.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This is an implementation of <code>HttpServletRequest</code> so that DWR
 * can easily be integrated into RIFE. Data that is sent and retrieved is only
 * done through DWR and thus doesn't create a security hole. The main purpose
 * is for the URLs to be relocatable
 * 
 * @author Joe Walker (joe[remove] at getahead dot ltd dot uk)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 */
final class DwrElementHttpServletRequest implements HttpServletRequest
{
	private ElementSupport mSupport;
	private HttpServletRequest mRequest;
	
	DwrElementHttpServletRequest(ElementSupport support)
	{
		mSupport = support;
		mRequest = support.getHttpServletRequest();
	}
	
	public String getPathInfo()
	{
		return mSupport.getPathInfo();
	}
	
	public String getPathTranslated()
	{
		return mRequest.getPathTranslated();
	}
	
	public String getContextPath()
	{
		return StringUtils.stripFromEnd(mSupport.getWebappRootUrl(), "/");
	}
	
	public String getRequestURI()
	{
		return mRequest.getRequestURI();
	}
	
	public StringBuffer getRequestURL()
	{
		return mRequest.getRequestURL();
	}
	
	public String getRealPath(String arg0)
	{
		return mRequest.getRealPath(arg0);
	}
	
	public String getServletPath()
	{
		return mSupport.getElementInfo().getUrl();
	}
	
	public String getContentType()
	{
		return mSupport.getContentType();
	}
	
	public String getProtocol()
	{
		return mSupport.getProtocol();
	}
	
	public String getScheme()
	{
		return mSupport.getScheme();
	}
	
	public String getServerName()
	{
		return mSupport.getServerName();
	}
	
	public int getServerPort()
	{
		return mSupport.getServerPort();
	}
	
	public String getRemoteAddr()
	{
		return mSupport.getRemoteAddr();
	}
	
	public String getRemoteHost()
	{
		return mSupport.getRemoteHost();
	}
	
	

	public String getAuthType()
	{
		return mRequest.getAuthType();
	}
	
	public Cookie[] getCookies()
	{
		return mRequest.getCookies();
	}
	
	public long getDateHeader(String name)
	{
		return mRequest.getDateHeader(name);
	}
	
	public String getHeader(String name)
	{
		return mRequest.getHeader(name);
	}
	
	public Enumeration getHeaders(String name)
	{
		return mRequest.getHeaders(name);
	}
	
	public Enumeration getHeaderNames()
	{
		return mRequest.getHeaderNames();
	}
	
	public int getIntHeader(String name)
	{
		return mRequest.getIntHeader(name);
	}
	
	public String getMethod()
	{
		return mRequest.getMethod();
	}
	
	public String getQueryString()
	{
		return mRequest.getQueryString();
	}
	
	public String getRemoteUser()
	{
		return mRequest.getRemoteUser();
	}
	
	public boolean isUserInRole(String arg0)
	{
		return mRequest.isUserInRole(arg0);
	}
	
	public Principal getUserPrincipal()
	{
		return mRequest.getUserPrincipal();
	}
	
	public String getRequestedSessionId()
	{
		return mRequest.getRequestedSessionId();
	}
	
	public HttpSession getSession(boolean arg0)
	{
		return mRequest.getSession(arg0);
	}
	
	public HttpSession getSession()
	{
		return mRequest.getSession();
	}
	
	public boolean isRequestedSessionIdValid()
	{
		return mRequest.isRequestedSessionIdValid();
	}
	
	public boolean isRequestedSessionIdFromCookie()
	{
		return mRequest.isRequestedSessionIdFromCookie();
	}
	
	public boolean isRequestedSessionIdFromURL()
	{
		return mRequest.isRequestedSessionIdFromURL();
	}
	
	public boolean isRequestedSessionIdFromUrl()
	{
		return mRequest.isRequestedSessionIdFromUrl();
	}
	
	public Object getAttribute(String name)
	{
		return mRequest.getAttribute(name);
	}
	
	public Enumeration getAttributeNames()
	{
		return mRequest.getAttributeNames();
	}
	
	public String getCharacterEncoding()
	{
		return mRequest.getCharacterEncoding();
	}
	
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException
	{
		mRequest.setCharacterEncoding(arg0);
	}
	
	public int getContentLength()
	{
		return mRequest.getContentLength();
	}	
	
	public ServletInputStream getInputStream() throws IOException
	{
		return mRequest.getInputStream();
	}
	
	public String getParameter(String name)
	{
		return mRequest.getParameter(name);
	}
	
	public Enumeration getParameterNames()
	{
		return mRequest.getParameterNames();
	}
	
	public String[] getParameterValues(String name)
	{
		return mRequest.getParameterValues(name);
	}
	
	public Map getParameterMap()
	{
		return mRequest.getParameterMap();
	}
	
	public BufferedReader getReader() throws IOException
	{
		return mRequest.getReader();
	}
	
	public void setAttribute(String name, Object object)
	{
		mRequest.setAttribute(name, object);
	}
	
	public void removeAttribute(String name)
	{
		mRequest.removeAttribute(name);
	}
	
	public Locale getLocale()
	{
		return mRequest.getLocale();
	}
	
	public Enumeration getLocales()
	{
		return mRequest.getLocales();
	}
	
	public boolean isSecure()
	{
		return mRequest.isSecure();
	}
	
	public RequestDispatcher getRequestDispatcher(String arg0)
	{
		return mRequest.getRequestDispatcher(arg0);
	}

	public int getRemotePort()
	{
		return mRequest.getRemotePort();
	}

	public String getLocalName()
	{
		return mRequest.getLocalName();
	}

	public String getLocalAddr()
	{
		return mRequest.getLocalAddr();
	}

	public int getLocalPort()
	{
		return mRequest.getLocalPort();
	}
}


