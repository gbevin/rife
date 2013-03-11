/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParticipantSpringWeb.java 3943 2008-04-27 09:09:02Z gbevin $
 */
package com.uwyn.rife.rep.participants;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.uwyn.rife.rep.BlockingParticipant;

public class ParticipantSpringWeb extends BlockingParticipant
{
	private BeanFactory	mBeanFactory = null;

	public ParticipantSpringWeb()
	{
		setInitializationMessage("Obtaining the Spring web application context ...");
		setCleanupMessage("Releasing the Spring web application context ...");
	}

	protected void initialize()
	{
		ServletContext 	context = (ServletContext)getRepository().getContext();
		if (getParameter() != null && !getParameter().equals(""))
		{
			ContextLoader 	loader = new ContextLoader();
			mBeanFactory = loader.initWebApplicationContext(new ServletContextWrapper(context));
		}
		else
		{
			mBeanFactory = WebApplicationContextUtils.getWebApplicationContext(context);
		}
	}

	protected Object _getObject()
	{
		return mBeanFactory;
	}

	protected Object _getObject(Object key)
	{
		if (null == key)
		{
			return null;
		}

		return mBeanFactory.getBean(String.valueOf(key));
	}

	public class ServletContextWrapper implements ServletContext
	{
		private ServletContext	mDelegate;
		private Vector			mInitParamNames = new Vector();

		ServletContextWrapper(ServletContext delegate)
		{
			mDelegate = delegate;

			Enumeration names_enum = mDelegate.getInitParameterNames();
			while (names_enum.hasMoreElements())
			{
				mInitParamNames.add(names_enum.nextElement());
			}
			if (!mInitParamNames.contains(ContextLoader.CONFIG_LOCATION_PARAM))
			{
				mInitParamNames.add(ContextLoader.CONFIG_LOCATION_PARAM);
			}
		}

		public Object getAttribute(String name)
		{
			return mDelegate.getAttribute(name);
		}

		public Enumeration getAttributeNames()
		{
			return mDelegate.getAttributeNames();
		}

		public ServletContext getContext(String uripath)
		{
			return mDelegate.getContext(uripath);
		}

		public String getInitParameter(String name)
		{
			if (ContextLoader.CONFIG_LOCATION_PARAM.equals(name))
			{
				return getParameter();
			}

			return mDelegate.getInitParameter(name);
		}

		public Enumeration getInitParameterNames()
		{
			return mInitParamNames.elements();
		}

		public int getMajorVersion()
		{
			return mDelegate.getMajorVersion();
		}

		public String getMimeType(String file)
		{
			return mDelegate.getMimeType(file);
		}

		public int getMinorVersion()
		{
			return mDelegate.getMinorVersion();
		}

		public RequestDispatcher getNamedDispatcher(String name)
		{
			return mDelegate.getNamedDispatcher(name);
		}

		public String getRealPath(String path)
		{
			return mDelegate.getRealPath(path);
		}

		public RequestDispatcher getRequestDispatcher(String path)
		{
			return mDelegate.getRequestDispatcher(path);
		}

		public URL getResource(String path) throws MalformedURLException
		{
			return mDelegate.getResource(path);
		}

		public InputStream getResourceAsStream(String path)
		{
			return mDelegate.getResourceAsStream(path);
		}

		public Set getResourcePaths(String path)
		{
			return mDelegate.getResourcePaths(path);
		}

		public String getServerInfo()
		{
			return mDelegate.getServerInfo();
		}

		public Servlet getServlet(String name) throws ServletException
		{
			return mDelegate.getServlet(name);
		}

		public String getServletContextName()
		{
			return mDelegate.getServletContextName();
		}

		public Enumeration getServletNames()
		{
			return mDelegate.getServletNames();
		}

		public Enumeration getServlets()
		{
			return mDelegate.getServlets();
		}

		public void log(Exception exception, String msg)
		{
			mDelegate.log(exception, msg);
		}

		public void log(String message, Throwable throwable)
		{
			mDelegate.log(message, throwable);
		}

		public void log(String msg)
		{
			mDelegate.log(msg);
		}

		public void removeAttribute(String name)
		{
			mDelegate.removeAttribute(name);
		}

		public void setAttribute(String name, Object object)
		{
			mDelegate.setAttribute(name, object);
		}

		public String getContextPath()
		{
			return mDelegate.getContextPath();
		}
	}
}

