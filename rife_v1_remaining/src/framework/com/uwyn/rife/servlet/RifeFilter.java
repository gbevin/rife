/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RifeFilter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.uwyn.rife.engine.EngineClassLoader;
import com.uwyn.rife.instrument.RifeAgent;

public class RifeFilter implements Filter
{
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
	
	public void init(FilterConfig config)
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

			Class		initconfig_filter_class = classloader.loadClass("com.uwyn.rife.engine.InitConfigFilter");
			Constructor	initconfig_filter_constructor = initconfig_filter_class.getConstructor(new Class[] {FilterConfig.class});
			Object		initconfig_filter = initconfig_filter_constructor.newInstance(new Object[] {config});

			String lifecycle_classname = config.getInitParameter("lifecycle.classname");
			if (null == lifecycle_classname)
			{
				lifecycle_classname = "com.uwyn.rife.servlet.RifeLifecycle";
			}
			Class		lifecycle_class = classloader.loadClass(lifecycle_classname);
			mLifeCycle = lifecycle_class.newInstance();
			
			Method		lifecycle_init = lifecycle_class.getMethod("init", new Class[] {initconfig_class});
			mGate = lifecycle_init.invoke(mLifeCycle, new Object[] {initconfig_filter});

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
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	throws IOException, ServletException
	{
		if (mEngineClassloader != null)
		{
			Thread.currentThread().setContextClassLoader(mEngineClassloader);
		}
		
		if (request instanceof HttpServletRequest &&
			response instanceof HttpServletResponse)
		{
			HttpServletRequest	http_servlet_request = (HttpServletRequest)request;
			HttpServletResponse	http_servlet_response = (HttpServletResponse)response;

			try
			{
				// create the servlet path
				if (null == mGateUrl)
				{
					String	context_path = http_servlet_request.getContextPath();

					// ensure a valid context path
					if (context_path != null &&
						!context_path.equals(".") &&
						!context_path.equals("/"))
					{
						mGateUrl = context_path;
					}
					else
					{
						mGateUrl = "";
					}
				}
			
				// construct the element url by stripping away the gate url
				String	element_url = http_servlet_request.getRequestURI().substring(mGateUrl.length());
				Method	gate_handlerequest = mGate.getClass().getMethod("handleRequest", new Class[] {String.class, String.class, mRequestClass, mResponseClass});
				
				// handle the request
				Object	http_request = mHttpRequestClass.getConstructor(new Class[] {HttpServletRequest.class}).newInstance(http_servlet_request);
				Object	http_response = mHttpResponseClass.getConstructor(new Class[] {mRequestClass, HttpServletResponse.class, boolean.class}).newInstance(http_request, http_servlet_response, false);
				
				Boolean result = (Boolean)gate_handlerequest.invoke(mGate, new Object[] {mGateUrl, element_url, http_request, http_response});
				if (result.booleanValue())
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
		
		chain.doFilter(request, response);
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
