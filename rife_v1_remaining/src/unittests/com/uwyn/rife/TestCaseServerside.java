/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCaseServerside.java 3933 2008-04-25 20:41:45Z gbevin $
 */
package com.uwyn.rife;

import com.uwyn.rife.engine.Gate;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.rep.Repository;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.servlet.RifeFilter;
import com.uwyn.rife.servlet.RifeServlet;
import junit.framework.TestCase;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.log.Log;
import org.mortbay.log.Logger;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Stack;

public abstract class TestCaseServerside extends TestCase
{
	public final static int SITE_SERVLET = 1;
	public final static int SITE_FILTER = 2;

	private int mSiteType = SITE_SERVLET;
	private String mWebAppName = "empty";
	private String mWebXmlPath = "/WEB-INF/web.xml";
	private Server mServer = null;
	private CollectingLogger mLogger = null;

	static
	{
		System.setProperty("org.mortbay.log.class", NullLogger.class.getName());
	}

	public TestCaseServerside(int siteType, String name)
	{
		super(name);

		setSiteType(siteType);
	}

	protected CollectingLogger getLogSink()
	{
		return mLogger;
	}

	private WebAppContext prepareWebapp(String contextPath) throws Exception
	{
		// Disable debug output
		mLogger = new CollectingLogger();
		Log.setLog(mLogger);

		// stop previous server if it was set up
		stopServer();

		// Create the server
		mServer = new Server();

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setHost("localhost");
		connector.setPort(8181);
		connector.setResolveNames(true);
		mServer.setConnectors(new Connector[]{connector});

		// Find the absolute path of the web application in the class path
		ResourceFinderClasspath resource_finder = ResourceFinderClasspath.getInstance();
		URL webapp_resource = resource_finder.getResource(mWebAppName + mWebXmlPath);
		String webapp_resource_path = URLDecoder.decode(webapp_resource.getFile());
		webapp_resource_path = webapp_resource_path.substring(0, webapp_resource_path.length() - mWebXmlPath.length());

		WebAppContext context = new WebAppContext();
		context.setContextPath(contextPath);
		context.setResourceBase(webapp_resource_path);
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		context.setDefaultsDescriptor(null);

		mServer.setHandler(context);

		return context;
	}

	private void startServer() throws Exception
	{
		mServer.start();
	}

	public void setSiteType(int type)
	{
		if (type != SITE_SERVLET && type != SITE_FILTER)
		{
			throw new IllegalArgumentException("invalid site type.");
		}

		mSiteType = type;
	}

	public void setWebAppName(String name)
	{
		mWebAppName = name;
	}

	public void setWebXmlPath(String path)
	{
		mWebXmlPath = path;
	}

	protected Gate setupSite(String siteXmlPath) throws Exception
	{
		return setupSite("/", siteXmlPath, null);
	}

	protected Gate setupSite(String siteXmlPath, String[][] initParams) throws Exception
	{
		return setupSite("/", siteXmlPath, initParams);
	}

	protected Gate setupSite(String context, String siteXmlPath) throws Exception
	{
		return setupSite(context, siteXmlPath, null);
	}

	protected Gate setupSite(String context, String siteXmlPath, String[][] initParams) throws Exception
	{
		switch (mSiteType)
		{
		case SITE_SERVLET:
			return setupServlet(context, siteXmlPath, initParams);
		case SITE_FILTER:
			return setupFilter(context, siteXmlPath, initParams);
		default:
			return null;
		}
	}

	private Gate setupServlet(String contextPath, String siteXmlPath, String[][] initParams) throws Exception
	{
		// Register the servlet
		Context context = prepareWebapp(contextPath);
		ServletHolder servlet_holder = context.addServlet(RifeServlet.class, "/*");
		servlet_holder.setInitOrder(1);

		// Setup the site if the xml path was provided
		if (siteXmlPath != null)
		{
			servlet_holder.setInitParameter("site.xml.path", siteXmlPath);
		}

		// add the provided init parameters
		if (initParams != null)
		{
			for (String[] param : initParams)
			{
				servlet_holder.setInitParameter(param[0], param[1]);
			}
		}

		startServer();

		return (Gate) ((RifeServlet) servlet_holder.getServlet()).getGate();
	}

	private Gate setupFilter(String contextPath, String siteXmlPath, String[][] initParams) throws Exception
	{
		// Register the filter
		Context context = prepareWebapp(contextPath);
		ServletHolder servlet_holder = context.addServlet(DefaultServlet.class, "/");
		FilterHolder filter_holder = context.addFilter(RifeFilter.class, "/*", Handler.REQUEST);

		// Configure the default servlet
		servlet_holder.setInitParameter("acceptRanges", "true");
		servlet_holder.setInitParameter("dirAllowed", "true");
		servlet_holder.setInitParameter("redirectWelcome", "false");
		servlet_holder.setInitOrder(1);

		// Setup the site if the xml path was provided
		if (siteXmlPath != null)
		{
			filter_holder.setInitParameter("site.xml.path", siteXmlPath);
		}

		// add the provided init parameters
		if (initParams != null)
		{
			for (String[] param : initParams)
			{
				filter_holder.setInitParameter(param[0], param[1]);
			}
		}

		startServer();

		return (Gate) ((RifeFilter) filter_holder.getFilter()).getGate();
	}

	protected void stopServer() throws Exception
	{
		if (mServer != null)
		{
			// disconnect default repository, otherwise it will be shutdown
			// by the RIFE's lifecycle cleanup
			Repository rep = Rep.getDefaultRepository();
			Rep.setDefaultRepository(null);

			// Stop the http server
			mServer.stop();
			mServer.join();
			mServer = null;

			// put the default repository back
			Rep.setDefaultRepository(rep);
		}
	}

	public void tearDown() throws Exception
	{
		stopServer();
	}

	public static class NullLogger implements Logger
	{
		public boolean isDebugEnabled() {return false;}
		public void setDebugEnabled(boolean debug) {}
		public void info(String msg, Object arg0, Object arg1) {}
		public void debug(String msg, Throwable throwable) {}
		public void debug(String msg, Object arg0, Object arg1) {}
		public void warn(String msg, Object arg0, Object arg1) {}
		public void warn(String msg, Throwable throwable) {}
		public Logger getLogger(String name) {return this;}
	}

	public static class CollectingLogger implements Logger
	{
		private static boolean DEBUG = System.getProperty("DEBUG",null)!=null;

		private Stack<Object> mLog = new Stack<Object>();

		public boolean isDebugEnabled()
		{
			return DEBUG;
		}

		public void setDebugEnabled(boolean debug)
		{
			DEBUG = debug;
		}

		public void info(String msg, Object arg0, Object arg1)
		{
		}

		public void debug(String msg, Throwable throwable)
		{
		}

		public void debug(String msg, Object arg0, Object arg1)
		{
		}

		public void warn(String msg, Object arg0, Object arg1)
		{
		}

		public void warn(String msg, Throwable throwable)
		{
			mLog.push(throwable);
		}

		public Logger getLogger(String name)
		{
			return this;
		}

		public Stack<Object> getLog()
		{
			return mLog;
		}

		public Throwable getInternalException()
		{
			Object last_entry = mLog.peek();
			if (!(last_entry instanceof Throwable))
			{
				return null;
			}

			return (Throwable)last_entry;
		}
	}
}