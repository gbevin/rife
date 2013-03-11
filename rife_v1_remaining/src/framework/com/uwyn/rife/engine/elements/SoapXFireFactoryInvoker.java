/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SoapXFireFactoryInvoker.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.ElementContext;
import com.uwyn.rife.engine.ElementService;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.invoker.FactoryInvoker;
import org.codehaus.xfire.service.invoker.ScopePolicy;
import org.codehaus.xfire.util.factory.Factory;

// FIXME: needs a better implementation as soon as XFire 1.2.3 is released, the Invoker API is currently a bit broken
class SoapXFireFactoryInvoker extends FactoryInvoker
{
	public SoapXFireFactoryInvoker(Factory factory, ScopePolicy scope)
	{
		super(factory, scope);
	}
	
	public Object getServiceObject(MessageContext context) throws XFireFault
	{
		Object instance = super.getServiceObject(context);
		if (instance instanceof ElementService)
		{
			((ElementService)instance).setRequestElement(ElementContext.getActiveElementSupport());
		}
		
		return instance;
	}
}
