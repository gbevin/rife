/*
 * Copyright 2006 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DwrService.java 3884 2007-08-22 08:52:24Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.servlet.PathConstants;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.StringUtils;

@Elem
public class DwrService extends Element
{
	public final static String PROPERTY_NAMES = "names";
	public final static String PROPERTY_INCLUDE_UTIL = "includeUtil";
	public final static String PROPERTY_XML_CONFIGURATOR_PATH = "xmlConfiguratorPath";
	
	public final static List<String> DWR_ELEMENT_PROPERTIES = new ArrayList<String>() {{
			add(PROPERTY_NAMES); 
			add(PROPERTY_INCLUDE_UTIL);
			add(PROPERTY_XML_CONFIGURATOR_PATH);
		}};
	
	public Class getDeploymentClass()
	{
		return DwrServiceDeployer.class;
	}
	
	public void processElement()
	{
		DwrServiceDeployer deployer = (DwrServiceDeployer)getDeployer();
		String engineHandlerUrl = (String) deployer.getContainer().getBean("engineHandlerUrl");
		String utilHandlerUrl = (String) deployer.getContainer().getBean("utilHandlerUrl");
		String interfaceHandlerUrl = (String) deployer.getContainer().getBean("interfaceHandlerUrl");
		
        try
        {
            setProhibitRawAccess(false);

			HttpServletRequest request = new DwrElementHttpServletRequest(this);
			HttpServletResponse response = getHttpServletResponse();
			
            deployer.initWebContextBuilder(request, response);
			
			if (isEmbedded())
			{
				String dwr_root = StringUtils.stripFromEnd(getWebappRootUrl(), "/")+getElementInfo().getUrl();
				
				Properties props = getEmbedProperties();
				for (String name : StringUtils.split(props.getProperty(PROPERTY_NAMES), ","))
				{
					printJavascriptInclusion(dwr_root+interfaceHandlerUrl+name.trim()+PathConstants.EXTENSION_JS);
				}
				
				printJavascriptInclusion(dwr_root+engineHandlerUrl);
				
				if (Convert.toBoolean(props.getProperty(PROPERTY_INCLUDE_UTIL), false))
				{
					printJavascriptInclusion(dwr_root+utilHandlerUrl);
				}
			}
			else
			{
				deployer.getProcessor().handle(request, response);
			}
        }
		catch (SecurityException e)
		{
			defer();
		}
		catch (Exception e)
		{
			throw new EngineException(e);
		}
        finally
        {
        	deployer.deinitWebContextBuilder();
        }
	}
	
	private void printJavascriptInclusion(String srcPath)
	{
		print("<script type=\"text/javascript\" src=\""+srcPath+"\"> </script>\n");
	}
}
