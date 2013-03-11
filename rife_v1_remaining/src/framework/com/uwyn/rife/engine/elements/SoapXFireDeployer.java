/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SoapXFireDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.ElementDeployer;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.PropertyRequiredException;
import com.uwyn.rife.tools.ClassUtils;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.service.invoker.ScopePolicyEditor;
import org.codehaus.xfire.transport.http.XFireServletController;

public class SoapXFireDeployer extends ElementDeployer
{
	private Service			mService = null;
	private XFireController	mController = null;
	
	public SoapXFireDeployer()
	{
	}
	
	public void deploy()
	throws EngineException
	{
		if (!getElementInfo().containsProperty("home-class"))
		{
			throw new PropertyRequiredException(getElementInfo().getDeclarationName(), "home-class");
		}
		if (!getElementInfo().containsProperty("home-api"))
		{
			throw new PropertyRequiredException(getElementInfo().getDeclarationName(), "home-api");
		}
		
		String home_class_classname = getElementInfo().getPropertyString("home-class");
		String home_api_classname = getElementInfo().getPropertyString("home-api");
		try
		{
			Class home_class = loadClass(home_class_classname);
			Class home_api = loadClass(home_api_classname);
			String service_name = getElementInfo().getPropertyString("service-name", ClassUtils.simpleClassName(home_class));
			String scope = getElementInfo().getPropertyString("scope", "");
			
			XFire xfire = XFireFactory.newInstance().getXFire();
			ServiceFactory factory = new ObjectServiceFactory(xfire.getTransportManager(), null);
			mService = factory.create(home_api, service_name, null, null);
			mService.setInvoker(new SoapXFireObjectInvoker(ScopePolicyEditor.toScopePolicy(scope)));
			mService.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, home_class);
			xfire.getServiceRegistry().register(mService);
			mController = new XFireController(xfire);
		}
		catch (ClassNotFoundException e)
		{
			throw new EngineException(e);
		}
	}
	
	XFireController getController()
	{
		return mController;
	}
	
	private Class loadClass(String className)
	throws ClassNotFoundException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		if (loader != null)
		{
			return Class.forName(className, false, loader);
		}
		else
		{
			return Class.forName(className);
		}
	}
	
	class XFireController extends XFireServletController
	{
		public XFireController(XFire xfire)
		{
			super(xfire);
		}
		
		protected String getService(HttpServletRequest request)
		{
			return mService.getName().getLocalPart();
		}
	}
}
