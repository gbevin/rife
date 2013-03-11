/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Gate.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.Version;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.DeferException;
import com.uwyn.rife.engine.exceptions.ElementCompilationFailedException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.rep.participants.ParticipantSite;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.template.exceptions.SyntaxErrorException;
import com.uwyn.rife.tools.ExceptionFormattingUtils;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.TerracottaUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

public class Gate
{
	public final static String	INIT_PARAM_SITE_XML_PATH = "site.xml.path";
	public final static String	REQUEST_ATTRIBUTE_RIFE_ENGINE_EXCEPTION = "rife.engine.exception";
	
	private Throwable			mInitException = null;
	private InitConfig			mInitConfig = null;
	private Site				mSiteFromInit = null;
	private boolean				mSiteFromRep = false;
	private volatile Boolean	mTempPathSet = false;
	private String				mWebappContextPath = null;

	public Gate()
	{
		mWebappContextPath = RifeConfig.Engine.getWebappContextPath();
	}

	public Gate(Site site)
	{
		this();
		
		mSiteFromInit = site;
	}

	public void init(InitConfig config)
	{
		mInitConfig = config;
		
		setApplicationClasspath(config);

		if (TerracottaUtils.isTcPresent())
		{
			// force early creating of the TemplateClassLoader - needed for DSO page-in on second node
			TemplateFactory.HTML.get("errors.rife.engine_error_default");
		}
	}
	
	private void setApplicationClasspath(InitConfig config)
	{
		// initialize the classpath of the web application
		// this is the best possible effort that can be made
		// since there's no standard way to obtain the classpath
		// of a web application
		StringBuilder	application_classpath = new StringBuilder();
		if (mInitConfig.getServletContext() != null)
		{
			// get the root of the web application
			String	webapp_root = mInitConfig.getServletContext().getRealPath("/");
			if (webapp_root != null)
			{
				if (webapp_root.endsWith(File.separator))
				{
					webapp_root = webapp_root.substring(0, webapp_root.length()-File.separator.length());
				}

				// add the classes dir
				application_classpath.append(webapp_root);
				application_classpath.append(File.separator).append("WEB-INF").append(File.separator).append("classes");

				// add the jars in the lib dir
				Set<String>	lib_resources = mInitConfig.getServletContext().getResourcePaths("/WEB-INF/lib");
				if (lib_resources != null)
				{
					for (String lib_resource : lib_resources)
					{
						if (lib_resource.endsWith(".jar"))
						{
							application_classpath.append(File.pathSeparator);
							application_classpath.append(webapp_root);
							application_classpath.append(lib_resource);
						}
					}
				}
			}
			else
			{
				Logger.getLogger("com.uwyn.rife.engine").warning("Due to the behavior of your servlet container ("+mInitConfig.getServletContext().getServerInfo()+"), automatic element compilation is not supported and might fail while resolving imported classes.");
			}
		}

		// add the rife.webapp.path paths
		if (EngineClassLoaderRifeWebappPath.RIFE_WEBAPP_PATH != null)
		{
			for (String path : EngineClassLoaderRifeWebappPath.RIFE_WEBAPP_PATH)
			{
				application_classpath.append(File.pathSeparator);
				application_classpath.append(path);
			}
		}

		if (application_classpath.length() > 0)
		{
			RifeConfig.Global.setApplicationClassPath(application_classpath.toString());
		}
	}

	public boolean handleRequest(String gateUrl, String elementUrl, Request request, Response response)
	{
		ensureExistingTempPath(request);
		
		// check if the gateUrl hasn't been overridden by a webapp context path configuration parameter
		if (mWebappContextPath != null)
		{
			gateUrl = mWebappContextPath;
		}

		// ensure a valid element url
		if (null == elementUrl ||
			0 == elementUrl.length())
		{
			elementUrl = "/";
		}

		// strip away the optional path parameters
		int path_parameters_index = elementUrl.indexOf(";");
		if (path_parameters_index != -1)
		{
			elementUrl = elementUrl.substring(0, path_parameters_index);
		}

		// If an internal error occured, try to build the site at each request
		Site site = getSite();

		// Handle the request
		// check if an exception occurred during the initialization
		if (mInitException != null)
		{
			printExceptionDetails(response, mInitException);
			return true;
		}

		// if no site is ready yet, don't continue processing
		if (null == site)
		{
			return handleSiteNotReady(elementUrl, response);
		}

		// Set up the element request and process it.
		try
		{
			ElementToService element_match = site.findElementForRequest(elementUrl);

			// If no element was found, don't continue executing the gate logic.
			// This could allow a next filter in the chain to be executed.
			if (null == element_match)
			{
				return false;
			}

			ElementInfo element_info = element_match.getElementInfo();
			StateStore state_store = element_info.getStateStore();

			state_store.init(request);
			request.init(state_store);

			RequestState request_state = RequestState.getInstance(mInitConfig, site, request, response, gateUrl, state_store.restoreResultStates(request), element_match.getPathInfo(), element_info);
			request_state.service();
			response.close();
		}
		catch (DeferException e)
		{
			return false;
		}
		catch (Throwable e)
		{
			handleRequestException(site, request, response, e);
		}

		return true;
	}

	private void ensureExistingTempPath(Request request) throws EngineException
	{
		if (!mTempPathSet &&
			(!Config.hasRepInstance() ||
			!Config.getRepInstance().hasParameter(RifeConfig.Global.PARAM_TEMP_PATH)))
		{
			// construct a temp path which is unique for the server and virtual host
			String tmpdir = System.getProperty("java.io.tmpdir");
			tmpdir = StringUtils.stripFromEnd(tmpdir, File.separator);
			String context_path = request.getContextPath();
			context_path = context_path.replace('/', File.separatorChar);
			RifeConfig.Global.setTempPath(tmpdir + File.separator + "rife_" + request.getServerName() + "_" + request.getServerPort() + "_" + context_path);
			
			// ensure that the temp path exists
			File	file_temp_path = new File(RifeConfig.Global.getTempPath());
			if (!file_temp_path.exists())
			{
				if (!file_temp_path.mkdirs())
				{
					throw new EngineException("Couldn't create the temporary directory : '" + RifeConfig.Global.getTempPath() + "'.");
				}
			}
			else if (!file_temp_path.isDirectory())
			{
				throw new EngineException("The element package directory '" + RifeConfig.Global.getTempPath() + "' exists but is not a directory.");
			}
			else if (!file_temp_path.canWrite())
			{
				throw new EngineException("The element package directory '" + RifeConfig.Global.getTempPath() + "' is not writable.");
			}
			
			// set the flag that indicates that the temp path has been set
			synchronized (mTempPathSet)
			{
				mTempPathSet = true;
			}
		}
		else
		{
			synchronized (mTempPathSet)
			{
				mTempPathSet = true;
			}
		}
	}

	public Site getSite()
	{
		// if the site has previously been initialized from init, return the
		// result immediately
		if (mSiteFromInit != null)
		{
			return mSiteFromInit;
		}

		Site result = null;

		try
		{
			// Ensure the presence of a servlet init configuration
			if (null == mInitConfig)
			{
				throw new Exception("No servlet configuration is available, it is required to set up the site structure.");
			}

			// Clear the init exception
			mInitException = null;

			// If there's a site participant, try to obtain a site instance from it
			if (Site.hasRepInstance())
			{
				// only continue if the default repository has finished initializing
				if (!Site.getRepParticipant().isFinished() &&
					!RifeConfig.Engine.getResponseRequiresSite())
				{
					return null;
				}

				result = Site.getRepInstance();

				if (null == result &&
					Rep.getParticipant(Site.DEFAULT_PARTICIPANT_NAME) instanceof ParticipantSite &&
					((ParticipantSite)Rep.getParticipant(Site.DEFAULT_PARTICIPANT_NAME)).getException() != null)
				{
					throw ((ParticipantSite)Rep.getParticipant(Site.DEFAULT_PARTICIPANT_NAME)).getException();
				}
			}

			// only proces the init parameter if the site hasn't previously been
			// obtained frop the rep
			if (!mSiteFromRep)
			{
				// Try to obtain a site xml path specification from an init config
				String	site_xml_path = mInitConfig.getInitParameter(INIT_PARAM_SITE_XML_PATH);
				if (null == site_xml_path)
				{
					// If it doesn't exist, launch an error when the site hasn't been
					// obtained through a participant earlier on.
					if (null == result)
					{
						throw new Exception("A site couldn't be obtained through the repository from the "+Site.DEFAULT_PARTICIPANT_NAME+" participant, nor through the init parameter '"+INIT_PARAM_SITE_XML_PATH+"'.");
					}
					// since no init param exists and a site could be obtained
					// from the rep, remember this setup
					else
					{
						mSiteFromRep = true;
					}
				}
				else
				{
					// If the site xml path could be found, use it to populate a new
					// site instance. This will override a prior site instance that was
					// obtained through a participant.
					// Like that, a site xml path specified in an init config overrides
					// a site participant.
					ResourceFinderClasspath	resourcefinder = ResourceFinderClasspath.getInstance();
					SiteBuilder				builder = new SiteBuilder(site_xml_path, resourcefinder);

					result = builder.getSite();
					mSiteFromInit = result;
					mSiteFromRep = false;
				}
			}
		}
		catch (Throwable e)
		{
			handleSiteInitException(e);
		}

		return result;
	}

	private void handleSiteInitException(Throwable e)
	{
		// ensure the later init exceptions don't overwrite earlier ones
		if (null == mInitException)
		{
			if (RifeConfig.Engine.getPrettyEngineExceptions())
			{
				mInitException = e;
			}
			else
			{
				if (e instanceof RuntimeException)
				{
					throw (RuntimeException)e;
				}
				else
				{
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private boolean handleSiteNotReady(String elementUrl, Response response)
	{
			// check if the url matches one of the passthrough suffixes
			boolean passthrough = false;
			for (String suffix : RifeConfig.Engine.getSiteInitializingPassthroughSuffixes())
			{
				if (elementUrl.endsWith(suffix))
				{
					passthrough = true;
					break;
				}
			}

			if (passthrough)
			{
				return false;
			}
			else
			{
				String initializing_url = RifeConfig.Engine.getSiteInitializingRedirectUrl();
				try
				{
					// either set the 'service is unavailable' status
					if (null == initializing_url)
					{
						response.sendError(503); // SC_SERVICE_UNAVAILABLE
					}
					// either redirect to a dedicated URL
					else
					{
						response.sendRedirect(initializing_url);
					}
				}
				catch (EngineException e)
				{
					response.sendError(503); // SC_SERVICE_UNAVAILABLE
				}
				return true;
			}
	}

	private void handleRequestException(Site site, Request request, Response response, Throwable e)
	{
		if (site != null)
		{
			site.resetLastModificationCheck();
		}
		
		String message = "Error on host " + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath();
		if (RifeConfig.Engine.getLogEngineExceptions())
		{
			Logger.getLogger("com.uwyn.rife.engine").severe(message + "\n" + ExceptionUtils.getExceptionStackTrace(e));
		}
		
		if (!RifeConfig.Engine.getPrettyEngineExceptions())
		{
			request.setAttribute(REQUEST_ATTRIBUTE_RIFE_ENGINE_EXCEPTION, e);
			
			if (e instanceof RuntimeException)
			{
				throw (RuntimeException)e;
			}
			else
			{
				throw new RuntimeException(message, e);
			}
		}
		
		printExceptionDetails(response, e);
	}
	
	private void printExceptionDetails(Response response, Throwable exception)
	{
		TemplateFactory template_factory = null;
		if (response.isContentTypeSet())
		{
			String content_type = response.getContentType();
			if (content_type.startsWith("text/xml") ||
				content_type.startsWith("application/xhtml+xml"))
			{
				template_factory = TemplateFactory.XML;
				response.setContentType("text/xml");
			}
		}
		if (null == template_factory)
		{
			template_factory = TemplateFactory.HTML;
			response.setContentType("text/html");
		}
		
		// pretty exception formatting and outputting instead of the default servlet
		// engine's formatting
		Template template = null;

		Throwable cause = exception;
		while (cause != null && cause.getCause() != cause)
		{

			if (cause instanceof ElementCompilationFailedException ||
				cause instanceof SyntaxErrorException)
			{
				template = template_factory.get("errors.rife.engine_error_compilation");
				break;
			}

			cause = cause.getCause();
		}
		
		if (null == template)
		{
			template = template_factory.get("errors.rife.engine_error_default");
		}


		template.setValue("exceptions", ExceptionFormattingUtils.formatExceptionStackTrace(exception, template));
		template.setValue("RIFE_VERSION", template.getEncoder().encode(Version.getVersion()));

		try
		{
			response.getWriter().print(template.getContent());
		}
		catch (IOException e2)
		{
			throw new RuntimeException(e2);
		}
	}
}

