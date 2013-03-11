/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SoapXFireObjectInvoker.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import java.lang.reflect.Method;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.AbstractInvoker;
import org.codehaus.xfire.service.invoker.FactoryInvoker;
import org.codehaus.xfire.service.invoker.LocalFactory;
import org.codehaus.xfire.service.invoker.ScopePolicy;

// FIXME: needs a better implementation as soon as XFire 1.2.3 is released, the Invoker API is currently a bit broken
class SoapXFireObjectInvoker extends AbstractInvoker
{
    /**
     * Constant to denote the implementation class for the service.
     */
    public static final String SERVICE_IMPL_CLASS = "xfire.serviceImplClass";
	
    // localfactory uses a ThreadLocal to ensure thread safety.
    // using localfactory, we don't need to "new" at each request.
    private static final LocalFactory LOCALFACTORY = new LocalFactory(SERVICE_IMPL_CLASS);
	
    private final FactoryInvoker fwd;
	
    private final ScopePolicy policy;
	
    public SoapXFireObjectInvoker(ScopePolicy policy)
    {
        this.policy = policy;
        this.fwd = new SoapXFireFactoryInvoker(LOCALFACTORY, policy);
    }
	
    public Object invoke(Method m, Object[] params, MessageContext context) throws XFireFault
    {
        final Service service = context.getService();
        LOCALFACTORY.setService(service);
        return fwd.invoke(m, params, context);
    }
	
    public Object getServiceObject(final MessageContext context) throws XFireFault
	{
        final Service service = context.getService();
        LOCALFACTORY.setService(service);    
        return fwd.getServiceObject(context);
    }
	
    /**
     * Get the scope policy used by this class.
     */
    public ScopePolicy getScope()
    {
        return policy;
    }
}
