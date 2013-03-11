/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PurgingMixedAuthenticatedDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.ioc.HierarchicalProperties;

/**
 * Deployer for {@link Authenticated} elements that by default sets up the
 * credentials and remember-me managers for database storage, the session
 * managers for memory storage and enables automatic in-process purging.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class PurgingMixedAuthenticatedDeployer extends MixedAuthenticatedDeployer
{
	public void deploy()
	throws EngineException
	{
		HierarchicalProperties properties = getElementInfo().getProperties();
		if (getElementInfo().isPropertyEmpty(PROPERTYNAME_ENABLE_PURGING))
		{
			properties.put(PROPERTYNAME_ENABLE_PURGING, true);
		}
		
		super.deploy();
	}
}
