/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseAuthenticatedDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.credentialsmanagers.CredentialsManagerFactoryFactory;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsersFactory;
import com.uwyn.rife.authentication.remembermanagers.DatabaseRememberFactory;
import com.uwyn.rife.authentication.remembermanagers.RememberManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionmanagers.DatabaseSessionsFactory;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionvalidators.DatabaseSessionValidatorFactory;
import com.uwyn.rife.authentication.sessionvalidators.SessionValidatorFactoryFactory;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.ioc.HierarchicalProperties;

/**
 * Deployer for {@link Authenticated} elements that by default sets up all the
 * authentication managers for database storage.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class DatabaseAuthenticatedDeployer extends FactoryPropertyAuthenticatedDeployer
{
	public void deploy()
	throws EngineException
	{
		HierarchicalProperties properties = getElementInfo().getProperties();
		if (getElementInfo().isPropertyEmpty(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS))
		{
			properties.put(SessionManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseSessionsFactory.class.getName());
		}
		if (getElementInfo().isPropertyEmpty(SessionValidatorFactoryFactory.PROPERTYNAME_FACTORY_CLASS))
		{
			properties.put(SessionValidatorFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseSessionValidatorFactory.class.getName());
		}
		if (getElementInfo().isPropertyEmpty(CredentialsManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS))
		{
			properties.put(CredentialsManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseUsersFactory.class.getName());
		}
		if (getElementInfo().isPropertyEmpty(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS))
		{
			properties.put(RememberManagerFactoryFactory.PROPERTYNAME_FACTORY_CLASS, DatabaseRememberFactory.class.getName());
		}
		
		super.deploy();
	}
}
