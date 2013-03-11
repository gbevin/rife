/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.authentication.elements.exceptions.UnknownCredentialsManagerFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.tools.SingletonFactory;

/**
 * Creates CredentialsManager factories based on configuration options.
 * 
 * <p>Element properties used:
 * <dl>
 * <dt>{@value #PROPERTYNAME_FACTORY_CLASS}</dt>
 * <dd>The name of the class that will be used to instantiate {@code CredentialsManager}
 * objects. If not fully qualified, the package name
 * {@code com.uwyn.rife.authentication.credentialsmanagers} will be
 * assumed.</dd>
 * </dl>
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @see DatabaseUsersFactory
 * @see MemoryUsersFactory
 * @since 1.6
 */
public abstract class CredentialsManagerFactoryFactory
{
	/** Name of the element property that controls the factory class to instantiate. */
	public static final String PROPERTYNAME_FACTORY_CLASS = "credentialsmanagerfactory_class";

	private static SingletonFactory<CredentialsManagerFactory> mFactories = new SingletonFactory(CredentialsManagerFactory.class);

	/**
	 * Returns a {@code CredentialsManagerFactory} instance.
	 * 
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public static CredentialsManagerFactory getInstance(HierarchicalProperties properties)
	throws PropertyValueException
	{
		try
		{
			return mFactories.getInstance(properties, PROPERTYNAME_FACTORY_CLASS, CredentialsManagerFactoryFactory.class);
		}
		catch (MandatoryPropertyMissingException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new UnknownCredentialsManagerFactoryClassException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns a {@code CredentialsManagerFactory} instance using the configured factory.
	 * 
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public static CredentialsManager getManager(HierarchicalProperties properties)
	throws PropertyValueException
	{
		CredentialsManagerFactory factory = getInstance(properties);
		return factory.getCredentialsManager(properties);
	}
}
