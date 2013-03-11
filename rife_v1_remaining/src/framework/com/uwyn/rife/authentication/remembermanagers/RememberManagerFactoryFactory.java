/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.remembermanagers;

import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.elements.exceptions.UnknownRememberManagerFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.tools.SingletonFactory;

/**
 * Creates RememberManager factories based on configuration options.
 * 
 * <p>Element properties used:
 * <dl>
 * <dt>{@value #PROPERTYNAME_FACTORY_CLASS}</dt>
 * <dd>The name of the class that will be used to instantiate RememberManager
 * objects. If not fully qualified, the package name
 * {@code com.uwyn.rife.authentication.remembermanagers} will be
 * assumed. If not specified at all, no remember manager will be created.</dd>
 * </dl>
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @see RememberManagerFactory
 * @see DatabaseRememberFactory
 * @since 1.6
 */
public abstract class RememberManagerFactoryFactory
{
	/** Name of the element property that controls the factory class to instantiate. */
	public static final String PROPERTYNAME_FACTORY_CLASS = "remembermanagerfactory_class";

	private static SingletonFactory<RememberManagerFactory> mFactories = new SingletonFactory(RememberManagerFactory.class);

	/**
	 * Returns a {@code RememberManagerFactory} instance.
	 * 
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public static RememberManagerFactory getInstance(HierarchicalProperties properties)
	throws PropertyValueException
	{
		try
		{
			return mFactories.getInstance(properties, PROPERTYNAME_FACTORY_CLASS, RememberManagerFactoryFactory.class);
		}
		catch (MandatoryPropertyMissingException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new UnknownRememberManagerFactoryClassException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns a {@code RememberManager} instance using the configured factory.
	 * 
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public static RememberManager getManager(HierarchicalProperties properties)
	throws PropertyValueException
	{
		/* 
		 * Remember managers are optional; if one isn't specified it's
		 * not an error condition.
		 */
		if (!properties.contains(PROPERTYNAME_FACTORY_CLASS))
		{
			return null;
		}
		
		RememberManagerFactory factory = getInstance(properties);
		return factory.getRememberManager(properties);
	}
}
