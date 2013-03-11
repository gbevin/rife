/*
 * Copyright 2004-2007 Joe Walker and Geert Bevin
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * $Id: DwrServiceDeployer.java 3943 2008-04-27 09:09:02Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.ElementDeployer;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import org.directwebremoting.Container;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.WebContextFactory.WebContextBuilder;
import org.directwebremoting.extend.DwrConstants;
import org.directwebremoting.extend.PageNormalizer;
import org.directwebremoting.impl.ContainerUtil;
import org.directwebremoting.impl.DefaultContainer;
import org.directwebremoting.impl.DwrXmlConfigurator;
import org.directwebremoting.servlet.UrlProcessor;
import org.directwebremoting.util.ServletLoggingOutput;
import org.xml.sax.SAXException;

public class DwrServiceDeployer extends ElementDeployer
{
    /**
     * The processor will actually handle the http requests
     */
    protected UrlProcessor mProcessor;
	
    /**
     * The WebContext that keeps http objects local to a thread
     */
    protected WebContextBuilder mWebContextBuilder;
	
    /**
     * The IoC container
     */
    protected DefaultContainer mContainer;
	
    public UrlProcessor getProcessor()
    {
    	return mProcessor;
    }
	
    public Container getContainer()
    {
    	return mContainer;
    }
	
    /**
     * Proxy to <code>WebContextBuilder.set()</code>.
     * @param request The HTTP request
     * @param response The HTTP response
     */
    public void initWebContextBuilder(HttpServletRequest request, HttpServletResponse response)
    {
        // And we lace it with the context so far to help init go smoothly
        mWebContextBuilder.set(request, response, null, null, mContainer);
    }
	
    /**
     * Proxy to <code>WebContextBuilder.unset()</code>.
     */
    public void deinitWebContextBuilder()
    {
    	if (mWebContextBuilder != null)
    	{
            mWebContextBuilder.unset();
    	}
    }
	
    /**
     * Add the element level configurator to the end of the list of
     * configurators.
     * @throws SAXException If the config file parse fails
     * @throws ParserConfigurationException If the config file parse fails
     * @throws IOException If the config file read fails
     */
    public void addElementDwrXmlConfigurator() throws IOException, ParserConfigurationException, SAXException
    {
		if (!getElementInfo().containsProperty(DwrService.PROPERTY_XML_CONFIGURATOR_PATH))
		{
			return;
		}
		
		DwrXmlConfigurator system = new DwrXmlConfigurator();
		if (getElementInfo().isPropertyEmpty(DwrService.PROPERTY_XML_CONFIGURATOR_PATH))
		{
			throw new EngineException("The DWR configuration file should be specified in the property '"+DwrService.PROPERTY_XML_CONFIGURATOR_PATH+"' of element "+getElementInfo().getDeclarationName()+".");
		}
		String config_path = getElementInfo().getPropertyString(DwrService.PROPERTY_XML_CONFIGURATOR_PATH);
		URL resource = ResourceFinderClasspath.getInstance().getResource(config_path);
		if (null == resource)
		{
			throw new EngineException("The DWR configuration file '"+config_path+"' could not be found. It was specified in the property '"+DwrService.PROPERTY_XML_CONFIGURATOR_PATH+"' of element "+getElementInfo().getDeclarationName()+".");
		}
		system.setInputStream(resource.openStream());
		system.configure(mContainer);
    }
	
    public void deploy() throws EngineException
    {
        mContainer = new DefaultContainer();
        try
        {
            ContainerUtil.setupDefaults(mContainer, new ServletConfig() {
					public String getServletName()
					{
						return "";
					}
					
					public ServletContext getServletContext()
					{
						return new ServletContext() {						
							public ServletContext getContext(String p1)
							{
								return null;
							}
							
							public int getMajorVersion()
							{
								return 0;
							}
							
							public int getMinorVersion()
							{
								return 0;
							}
							
							public String getMimeType(String p1)
							{
								return null;
							}
							
							public Set getResourcePaths(String p1)
							{
								return Collections.emptySet();
							}
							
							public URL getResource(String p1) throws MalformedURLException
							{
								return null;
							}
							
							public InputStream getResourceAsStream(String p1)
							{
								return null;
							}
							
							public RequestDispatcher getRequestDispatcher(String p1)
							{
								return null;
							}
							
							public RequestDispatcher getNamedDispatcher(String p1)
							{
								return null;
							}
							
							public Servlet getServlet(String p1) throws ServletException
							{
								return null;
							}
							
							public Enumeration getServlets()
							{
								return Collections.enumeration(Collections.emptyList());
							}
							
							public Enumeration getServletNames()
							{
								return Collections.enumeration(Collections.emptyList());
							}
							
							public void log(String p1)
							{
							}
							
							public void log(Exception p1, String p2)
							{
							}
							
							public void log(String p1, Throwable p2)
							{
							}
							
							public String getRealPath(String p1)
							{
								return "";
							}
							
							public String getServerInfo()
							{
								return "";
							}
							
							public String getInitParameter(String p1)
							{
								return null;
							}
							
							public Enumeration getInitParameterNames()
							{
								return Collections.enumeration(Collections.emptyList());
							}
							
							public Object getAttribute(String p1)
							{
								return null;
							}
							
							public Enumeration getAttributeNames()
							{
								return Collections.enumeration(Collections.emptyList());
							}
							
							public void setAttribute(String p1, Object p2)
							{
							}
							
							public void removeAttribute(String p1)
							{
							}
							
							public String getServletContextName()
							{
								return "";
							}

							public String getContextPath()
							{
								return null;
							}
						};
					}
					
					public String getInitParameter(String p1)
					{
						return null;
					}
					
					public Enumeration getInitParameterNames()
					{
						return Collections.enumeration(Collections.emptyList());
					}
				});
			
			// override the default page normalizer with RIFE's version
			mContainer.addParameter(PageNormalizer.class.getName(), new DwrRifePageNormalizer(getElementInfo().getSite()));
			
			// create a shadow copy of the element properties to exclude the root
			// properties (these are the system properties), this to prevent DWR to be
			// overpopulated with unrelated properties
			HierarchicalProperties properties = getElementInfo().getProperties();
			HierarchicalProperties shadow = properties.createShadow(properties.getRoot());
			
			// add all element properties that are not RIFE-specific to the DWR container
			for (String property_name : shadow.getInjectableNames())
			{
				if (!DwrService.DWR_ELEMENT_PROPERTIES.contains(property_name))
				{
					mContainer.addParameter(property_name, shadow.getValue(property_name));
				}
			}
			
            mContainer.setupFinished();
			
            // Cached to save looking them up
            mWebContextBuilder = (WebContextBuilder)mContainer.getBean(WebContextBuilder.class.getName());
            mProcessor = (UrlProcessor)mContainer.getBean(UrlProcessor.class.getName());
			
            // Now we have set the implementations we can set the WebContext up
            WebContextFactory.setWebContextBuilder(mWebContextBuilder);
			
            initWebContextBuilder(null, null);
			
            // The dwr.xml from within the JAR file.
            DwrXmlConfigurator system = new DwrXmlConfigurator();
            system.setClassResourceName(DwrConstants.FILE_DWR_XML);
            system.configure(mContainer);
			
			addElementDwrXmlConfigurator();
        }
        catch (EngineException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new EngineException(e);
        }
        finally
        {
            deinitWebContextBuilder();
            ServletLoggingOutput.unsetExecutionContext();
        }
    }
}

