/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: HttpResponse.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.servlet;

import com.uwyn.rife.engine.AbstractResponse;
import com.uwyn.rife.engine.Request;
import com.uwyn.rife.engine.Response;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.servlet.HttpResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpResponse extends AbstractResponse
{
	private HttpServletResponse		mResponse = null;
	
	public HttpResponse(Request request, HttpServletResponse response, boolean embedded)
	{
		super(request, embedded);
		
		mResponse = response;
	}
	
	protected void _setContentType(String contentType)
	{
		mResponse.setContentType(contentType);
	}
	
	protected String _getCharacterEncoding()
	{
		return mResponse.getCharacterEncoding();
	}
	
	protected void _setContentLength(int length)
	{
		mResponse.setContentLength(length);
	}
	
	protected void _sendRedirect(String location)
	{
		try
		{
			mResponse.sendRedirect(location);
		}
		catch (IOException e)
		{
			throw new EngineException(e);
		}
	}
	
	protected OutputStream _getOutputStream() throws IOException
	{
		return mResponse.getOutputStream();
	}
	
	public Response createEmbeddedResponse(String valueId, String differentiator)
	{
		return new HttpResponse(getRequest(), mResponse, true);
	}
	
	public void addCookie(Cookie cookie)
	{
		mResponse.addCookie(cookie);
	}
	
	public void addHeader(String name, String value)
	{
		mResponse.addHeader(name, value);
	}
	
	public void addDateHeader(String name, long date)
	{
		mResponse.addDateHeader(name, date);
	}
	
	public void addIntHeader(String name, int integer)
	{
		mResponse.addIntHeader(name, integer);
	}
	
	public boolean containsHeader(String name)
	{
		return mResponse.containsHeader(name);
	}
	
	public void sendError(int statusCode)
	throws EngineException
	{
		try
		{
			mResponse.sendError(statusCode);
		}
		catch (IOException e)
		{
			throw new EngineException(e);
		}
	}
	
	public void sendError(int statusCode, String message)
	throws EngineException
	{
		try
		{
			mResponse.sendError(statusCode, message);
		}
		catch (IOException e)
		{
			throw new EngineException(e);
		}
	}
	
	public void setDateHeader(String name, long date)
	{
		mResponse.setDateHeader(name, date);
	}
	
	public void setHeader(String name, String value)
	{
		mResponse.setHeader(name, value);
	}
	
	public void setIntHeader(String name, int value)
	{
		mResponse.setIntHeader(name, value);
	}
	
	public void setStatus(int statusCode)
	{
		mResponse.setStatus(statusCode);
	}
	
	public String encodeURL(String url)
	{
		return mResponse.encodeURL(url);
	}
	
	public void setLocale(Locale locale)
	{
		mResponse.setLocale(locale);
	}
	
	public Locale getLocale()
	{
		return mResponse.getLocale();
	}
	
	public PrintWriter getWriter()
	throws IOException
	{
		return mResponse.getWriter();
	}
	
	public HttpServletResponse getHttpServletResponse()
	{
		return mResponse;
	}
}
