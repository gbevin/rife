/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.sessionmanagers;

import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.elements.exceptions.UnknownSessionManagerFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.tools.SingletonFactory;

/**
 * Creates SessionManager factories based on configuration options.
 * <p>
 * Element properties used:
 * <dl>
 * <dt>{@value #PROPERTYNAME_FACTORY_CLASS}</dt>
 * <dd>The name of the class that will be used to instantiate SessionManager
 * objects. If not fully qualified, the package name
 * {@code com.uwyn.rife.authentication.sessionmanagers} will be
 * assumed.</dd>
 * </dl>
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @see SessionManagerFactory
 * @see SimpleSessionManagerFactory
 * @since 1.6
 */
public abstract class SessionManagerFactoryFactory
{
	/** Name of the element property that controls the factory class to instantiate. */
	public static final String PROPERTYNAME_FACTORY_CLASS = "sessionmanagerfactory_class";

	private static SingletonFactory<SessionManagerFactory> mFactories = new SingletonFactory(SessionManagerFactory.class);

	/**
	 * Returns a {@code SessionManagerFactory} instance.
	 *
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public static SessionManagerFactory getInstance(HierarchicalProperties properties)
	throws PropertyValueException
	{
		try
		{
			return mFactories.getInstance(properties, PROPERTYNAME_FACTORY_CLASS, SessionManagerFactoryFactory.class);
		}
		catch (MandatoryPropertyMissingException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new UnknownSessionManagerFactoryClassException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns a {@code SessionManager} instance using the configured factory.
	 *
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public static SessionManager getManager(HierarchicalProperties properties)
	throws PropertyValueException
	{
		SessionManagerFactory factory = getInstance(properties);
		return factory.getManager(properties);
	}
}
