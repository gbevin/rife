/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: HttpRequest.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.servlet;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Request;
import com.uwyn.rife.engine.RequestMethod;
import com.uwyn.rife.engine.StateStore;
import com.uwyn.rife.engine.UploadedFile;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.servlet.MultipartRequest;
import com.uwyn.rife.tools.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpRequest implements Request
{
	private HttpServletRequest			mHttpServletRequest = null;
	
	private Map<String, String[]>		mParameters = null;
	private Map<String, UploadedFile[]>	mFiles = null;
	
	public HttpRequest(HttpServletRequest request)
	throws EngineException
	{
		assert request != null;
		
		mHttpServletRequest = request;
	}
	
	public void init(StateStore stateStore)
	{
		assert stateStore != null;
		
		if (MultipartRequest.isValidContentType(mHttpServletRequest.getContentType()))
		{
			MultipartRequest	multipart_request = new MultipartRequest(mHttpServletRequest);
			mParameters = multipart_request.getParameterMap();
			mFiles = multipart_request.getFileMap();
		}
		else
		{
			mParameters = new HashMap<String, String[]>();
			
			try
			{
				mHttpServletRequest.setCharacterEncoding(RifeConfig.Engine.getRequestEncoding());
			}
			catch (UnsupportedEncodingException e)
			{
				// should never happen
			}
			
			Enumeration<String>	parameter_names = mHttpServletRequest.getParameterNames();
			String				parameter_name = null;
			String[]			parameter_values = null;
			while (parameter_names.hasMoreElements())
			{
				parameter_name = parameter_names.nextElement();
				if (StringUtils.doesUrlValueNeedDecoding(parameter_name))
				{
					parameter_name = StringUtils.decodeUrlValue(parameter_name);
				}
				
				parameter_values = mHttpServletRequest.getParameterValues(parameter_name);
				for (int i = 0; i < parameter_values.length; i++)
				{
					if (StringUtils.doesUrlValueNeedDecoding(parameter_values[i]))
					{
						parameter_values[i] = StringUtils.decodeUrlValue(parameter_values[i]);
					}
				}
				
				mParameters.put(parameter_name, parameter_values);
			}
		}
		
		Map<String, String[]> parameters = stateStore.restoreParameters(this);
		if (parameters != null)
		{
			mParameters = parameters;
		}
	}
	
	public RequestMethod getMethod()
	{
		return RequestMethod.getMethod(mHttpServletRequest.getMethod());
	}
	
	public Map<String, String[]> getParameters()
	{
		return mParameters;
	}
	
	public Map<String, UploadedFile[]> getFiles()
	{
		return mFiles;
	}
	
	public boolean hasFile(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		if (null == getFiles())
		{
			return false;
		}
		
		if (!getFiles().containsKey(name))
		{
			return false;
		}
		
		UploadedFile[] uploaded_files = getFiles().get(name);
		
		if (0 == uploaded_files.length)
		{
			return false;
		}
		
		for (UploadedFile uploaded_file : uploaded_files)
		{
			if (uploaded_file != null &&
				uploaded_file.getName() != null)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public UploadedFile getFile(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		if (null == getFiles())
		{
			return null;
		}
		
		UploadedFile[] files = getFiles().get(name);
		if (null == files)
		{
			return null;
		}
		
		return files[0];
	}
	
	public UploadedFile[] getFiles(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		if (null == getFiles())
		{
			return null;
		}
		
		return getFiles().get(name);
	}
	
	public boolean hasCookie(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		Cookie[] cookies = mHttpServletRequest.getCookies();
		
		if (null == cookies)
		{
			return false;
		}
		
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Cookie getCookie(String name)
	{
		assert name != null;
		assert name.length() > 0;
		
		Cookie[] cookies = mHttpServletRequest.getCookies();
		
		if (null == cookies)
		{
			return null;
		}
		
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals(name))
			{
				return cookie;
			}
		}
		
		return null;
	}
	
	public Cookie[] getCookies()
	{
		return mHttpServletRequest.getCookies();
	}
	
	// simply wrapped methods
	public Object getAttribute(String name)
	{
		return mHttpServletRequest.getAttribute(name);
	}
	
	public boolean hasAttribute(String name)
	{
		return getAttribute(name) != null;
	}
	
	public Enumeration getAttributeNames()
	{
		return mHttpServletRequest.getAttributeNames();
	}
	
	public String getCharacterEncoding()
	{
		return mHttpServletRequest.getCharacterEncoding();
	}
	
	public String getContentType()
	{
		return mHttpServletRequest.getContentType();
	}
	
	public long getDateHeader(String name)
	{
		return mHttpServletRequest.getDateHeader(name);
	}
	
	public String getHeader(String name)
	{
		return mHttpServletRequest.getHeader(name);
	}
	
	public Enumeration getHeaderNames()
	{
		return mHttpServletRequest.getHeaderNames();
	}
	
	public Enumeration getHeaders(String name)
	{
		return mHttpServletRequest.getHeaders(name);
	}
	
	public int getIntHeader(String name)
	{
		return mHttpServletRequest.getIntHeader(name);
	}
	
	public Locale getLocale()
	{
		return mHttpServletRequest.getLocale();
	}
	
	public Enumeration getLocales()
	{
		return mHttpServletRequest.getLocales();
	}
	
	public String getProtocol()
	{
		return mHttpServletRequest.getProtocol();
	}
	
	public String getRemoteAddr()
	{
		return mHttpServletRequest.getRemoteAddr();
	}
	
	public String getRemoteUser()
	{
		return mHttpServletRequest.getRemoteUser();
	}
	
	public String getRemoteHost()
	{
		return mHttpServletRequest.getRemoteHost();
	}
	
	public RequestDispatcher getRequestDispatcher(String url)
	{
		return mHttpServletRequest.getRequestDispatcher(url);
	}
	
	public HttpSession getSession()
	{
		return mHttpServletRequest.getSession();
	}
	
	public HttpSession getSession(boolean create)
	{
		return mHttpServletRequest.getSession(create);
	}
	
	public int getServerPort()
	{
		if (null == mHttpServletRequest ||
			null == mHttpServletRequest)
		{
			return -1;
		}
		
		return mHttpServletRequest.getServerPort();
	}
	
	public String getScheme()
	{
		return mHttpServletRequest.getScheme();
	}
	
	public String getServerName()
	{
		if (null == mHttpServletRequest ||
			null == mHttpServletRequest)
		{
			return null;
		}
		
		return mHttpServletRequest.getServerName();
	}
	
	public String getContextPath()
	{
		if (null == mHttpServletRequest ||
			null == mHttpServletRequest)
		{
			return null;
		}
		
		return mHttpServletRequest.getContextPath();
	}
	
	public boolean isSecure()
	{
		return mHttpServletRequest.isSecure();
	}
	
	public void removeAttribute(String name)
	{
		mHttpServletRequest.removeAttribute(name);
	}
	
	public void setAttribute(String name, Object object)
	{
		mHttpServletRequest.setAttribute(name, object);
	}
	
	public String getServerRootUrl(int port)
	{
		StringBuilder server_root = new StringBuilder();
		server_root.append(getScheme());
		server_root.append("://");
		server_root.append(getServerName());
		if (port <= -1)
		{
			port = getServerPort();
		}
		if (port != 80)
		{
			server_root.append(":");
			server_root.append(port);
		}
		return server_root.toString();
	}
	
	public HttpServletRequest getHttpServletRequest()
	{
		return mHttpServletRequest;
	}
}
