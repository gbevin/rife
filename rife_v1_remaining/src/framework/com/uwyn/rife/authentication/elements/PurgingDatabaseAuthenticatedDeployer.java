/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PurgingDatabaseAuthenticatedDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.ioc.HierarchicalProperties;

/**
 * Deployer for {@link Authenticated} elements that by default sets up all the
 * authentication managers for database storage and enables automatic in-process purging.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class PurgingDatabaseAuthenticatedDeployer extends DatabaseAuthenticatedDeployer
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
