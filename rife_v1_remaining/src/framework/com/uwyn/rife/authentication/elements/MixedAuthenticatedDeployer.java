/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MixedAuthenticatedDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.credentialsmanagers.CredentialsManagerFactoryFactory;
import com.uwyn.rife.authentication.credentialsmanagers.DatabaseUsersFactory;
import com.uwyn.rife.authentication.remembermanagers.DatabaseRememberFactory;
import com.uwyn.rife.authentication.remembermanagers.RememberManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionmanagers.MemorySessions;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionmanagers.SimpleSessionManagerFactory;
import com.uwyn.rife.authentication.sessionvalidators.BasicSessionValidator;
import com.uwyn.rife.authentication.sessionvalidators.SessionValidatorFactoryFactory;
import com.uwyn.rife.authentication.sessionvalidators.SimpleSessionValidatorFactory;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.ioc.HierarchicalProperties;

/**
 * Deployer for {@link Authenticated} elements that by default sets up the
 * credentials and remember-me managers for database storage, but the session
 * managers for memory storage.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class MixedAuthenticatedDeployer extends FactoryPropertyAuthenticatedDeployer
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
		
		if (getElementInfo().isPropertyEmpty(SessionValidatorFactoryFactory.PROPERTYNAME_FACTORY_CLASS))
		{
			properties.put(SessionValidatorFactoryFactory.PROPERTYNAME_FACTORY_CLASS, SimpleSessionValidatorFactory.class.getName());
		}
		if (getElementInfo().isPropertyEmpty(SimpleSessionValidatorFactory.PROPERTYNAME_MANAGER_CLASS))
		{
			properties.put(SimpleSessionValidatorFactory.PROPERTYNAME_MANAGER_CLASS, BasicSessionValidator.class.getName());
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
