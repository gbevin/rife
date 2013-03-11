/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.elements.exceptions.UnknownSessionValidatorFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.tools.SingletonFactory;

/**
 * Creates SessionValidator factories based on configuration options.
 * 
 * <p>Element properties used:
 * <dl>
 * <dt>{@value #PROPERTYNAME_FACTORY_CLASS}</dt>
 * <dd>The name of the class that will be used to instantiate SessionValidator
 * objects. If not fully qualified, the package name
 * {@code com.uwyn.rife.authentication.sessionvalidators} will be
 * assumed.</dd>
 * </dl>
 * 
 * @see SimpleSessionValidatorFactory
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @see SimpleSessionValidatorFactory
 * @since 1.6
 */
public abstract class SessionValidatorFactoryFactory
{
	/** Name of the element property that controls the factory class to instantiate. */
	public static final String PROPERTYNAME_FACTORY_CLASS = "sessionvalidatorfactory_class";

	private static SingletonFactory<SessionValidatorFactory> mFactories = new SingletonFactory(SessionValidatorFactory.class);

	/**
	 * Returns a {@code SessionValidatorFactory} instance.
	 *
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public static SessionValidatorFactory getInstance(HierarchicalProperties properties)
	throws PropertyValueException
	{
		try
		{
			return mFactories.getInstance(properties, PROPERTYNAME_FACTORY_CLASS, SessionValidatorFactoryFactory.class);
		}
		catch (MandatoryPropertyMissingException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new UnknownSessionValidatorFactoryClassException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns a {@code SessionValidator} instance using the configured factory.
	 *
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public static SessionValidator getValidator(HierarchicalProperties properties)
	throws PropertyValueException
	{
		SessionValidatorFactory factory = getInstance(properties);
		return factory.getValidator(properties);
	}
}
