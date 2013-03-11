/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemoryLogoutDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.sessionmanagers.MemorySessions;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionmanagers.SimpleSessionManagerFactory;
import com.uwyn.rife.engine.ElementDeployer;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.ioc.HierarchicalProperties;

/**
 * Deployer for logout elements that by default sets up all the
 * authentication managers for memory storage.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class MemoryLogoutDeployer extends ElementDeployer
{
	public void deploy()
	throws EngineException
	{
		HierarchicalProperties properties = getElementInfo().getProperties();
		if (getElementInfo().isPropertyEmpty(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS))
		{
			properties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, SimpleSessionManagerFactory.class.getName());
		}
		if (getElementInfo().isPropertyEmpty(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_CLASS))
		{
			properties.put(SimpleSessionManagerFactory.PROPERTYNAME_MANAGER_CLASS, MemorySessions.class.getName());
		}
	}
}
