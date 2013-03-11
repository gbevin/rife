/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RifeServlet.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.uwyn.rife.engine.EngineClassLoader;
import com.uwyn.rife.instrument.RifeAgent;

public class RifeServlet extends HttpServlet
{
	private static final long serialVersionUID = 781560128846727886L;
	
	private Object		mLifeCycle = null;
	private Object		mGate = null;
	private String		mGateUrl = null;
	private ClassLoader mEngineClassloader = null;
	private Class		mRequestClass = null;
	private Class		mResponseClass = null;
	private Class		mHttpRequestClass = null;
	private Class		mHttpResponseClass = null;
	
	/**
	 * Returns the gate that this filter sets up.
	 * <p>
	 * Note that this is deliberately returned as <code>Object</code> to prevent
	 * the {@link com.uwyn.rife.engine.Gate} class to be loaded by the wrong
	 * classloader.
	 *
	 * @return the {@link com.uwyn.rife.engine.Gate} that has been setup by this filter.
	 * @since 1.6
	 */
	public Object getGate()
	{
		return mGate;
	}
	
	public void init(ServletConfig config)
	throws ServletException
	{
		ClassLoader classloader = getClass().getClassLoader();
		String enabled = config.getInitParameter("engineclassloader.enabled");
		if (!(classloader instanceof EngineClassLoader) &&
			(null == enabled ||
			 enabled.equalsIgnoreCase("true") ||
			 enabled.equalsIgnoreCase("t") ||
			 enabled.equalsIgnoreCase("yes") ||
			 enabled.equalsIgnoreCase("y") ||
			 enabled.equalsIgnoreCase("on") ||
			 enabled.equalsIgnoreCase("1")))
		{
			String agent_active = System.getProperty(RifeAgent.AGENT_ACTIVE_PROPERTY, String.valueOf(false));
			if (!agent_active.equals(String.valueOf(true)))
			{
				classloader = new EngineClassLoader(classloader);
				Thread.currentThread().setContextClassLoader(classloader);
	
				mEngineClassloader = classloader;
			}
		}

		try
		{
			Class		initconfig_class = classloader.loadClass("com.uwyn.rife.engine.InitConfig");

			Class		initconfig_servlet_class = classloader.loadClass("com.uwyn.rife.engine.InitConfigServlet");
			Constructor	initconfig_servlet_constructor = initconfig_servlet_class.getConstructor(new Class[] {ServletConfig.class});
			Object		initconfig_servlet = initconfig_servlet_constructor.newInstance(new Object[] {config});

			Class		lifecycle_class = classloader.loadClass("com.uwyn.rife.servlet.RifeLifecycle");
			mLifeCycle = lifecycle_class.newInstance();
			
			Method		lifecycle_init = lifecycle_class.getMethod("init", new Class[] {initconfig_class});
			mGate = lifecycle_init.invoke(mLifeCycle, new Object[] {initconfig_servlet});

			mRequestClass = classloader.loadClass("com.uwyn.rife.engine.Request");
			mResponseClass = classloader.loadClass("com.uwyn.rife.engine.Response");
			mHttpRequestClass = classloader.loadClass("com.uwyn.rife.servlet.HttpRequest");
			mHttpResponseClass = classloader.loadClass("com.uwyn.rife.servlet.HttpResponse");
		}
		catch (InvocationTargetException e)
		{
			if (e.getCause() != null)
			{
				if (e.getCause() instanceof RuntimeException)
				{
					throw (RuntimeException)e.getCause();
				}
				else
				{
					throw new ServletException(e.getCause());
				}
			}
			else
			{
				throw new ServletException(e);
			}
		}
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
	}
	
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
	throws ServletException
	{
		if (mEngineClassloader != null)
		{
			Thread.currentThread().setContextClassLoader(mEngineClassloader);
		}
		
		try
		{
			// create the servlet path
			if (null == mGateUrl)
			{
				String	context_path = httpServletRequest.getContextPath();
				
				// build a correct gate URL by using the servlet path and ensuring the value is acceptable
				String	servlet_path = httpServletRequest.getServletPath();
				if (context_path != null &&
					!context_path.equals(".") &&
					!context_path.equals("/"))
				{
					mGateUrl = context_path;
					if (servlet_path != null &&
						!servlet_path.equals(".") &&
						!servlet_path.equals("/"))
					{
						mGateUrl += servlet_path;
					}
				}
				else
				{
					mGateUrl = "";
				}
			}
			
			String	element_url = httpServletRequest.getPathInfo();
			Method	gate_handlerequest = mGate.getClass().getMethod("handleRequest", new Class[] {String.class, String.class, mRequestClass, mResponseClass});
			
			// handle the request
			Object	http_request = mHttpRequestClass.getConstructor(new Class[] {HttpServletRequest.class}).newInstance(httpServletRequest);
			Object	http_response = mHttpResponseClass.getConstructor(new Class[] {mRequestClass, HttpServletResponse.class, boolean.class}).newInstance(http_request, httpServletResponse, false);
			
			Boolean result = (Boolean)gate_handlerequest.invoke(mGate, new Object[] {mGateUrl, element_url, http_request, http_response});
			if (!result.booleanValue())
			{
				try
				{
					httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
			else
			{
				return;
			}
		}
		catch (InvocationTargetException e)
		{
			if (e.getCause() != null)
			{
				if (e.getCause() instanceof RuntimeException)
				{
					throw (RuntimeException)e.getCause();
				}
				else
				{
					throw new ServletException(e.getCause());
				}
			}
			else
			{
				throw new ServletException(e);
			}
		}
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
	}

	public void destroy()
	{
		if (mEngineClassloader != null)
		{
			Thread.currentThread().setContextClassLoader(mEngineClassloader);
		}
		
		try
		{
			Class	lifecycle_class = mLifeCycle.getClass();
			Method 	lifecycle_destroy = lifecycle_class.getMethod("destroy", (Class[])null);
			lifecycle_destroy.invoke(mLifeCycle, (Object[])null);
		}
		catch (InvocationTargetException e)
		{
			if (e.getCause() != null)
			{
				if (e.getCause() instanceof RuntimeException)
				{
					throw (RuntimeException)e.getCause();
				}
				else
				{
					throw new RuntimeException(e.getCause());
				}
			}
			else
			{
				throw new RuntimeException(e);
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
}

