/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemorySessionsFactory.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.elements.exceptions.UnknownSessionValidatorClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;

/**
 * Factory that instantiates session validators based on the class name
 * specified in element properties.
 * 
 * <p>
 * Element properties used:
 * <dl>
 * <dt>{@value #PROPERTYNAME_MANAGER_CLASS}</dt>
 * <dd>Name of the session manager class. If the class name is not fully
 * qualified, the package name
 * {@code com.uwyn.rife.authentication.sessionvalidators}
 * will be assumed.</dd>
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @see SessionValidator
 * @since 1.6
 */
public class SimpleSessionValidatorFactory implements SessionValidatorFactory
{
	/** Element property that specifies the class name for session validators. */
	public static final String PROPERTYNAME_MANAGER_CLASS = "sessionvalidator_class";

	public SessionValidator getValidator(HierarchicalProperties properties)
	throws PropertyValueException
	{
		String className = properties.getValueTyped(PROPERTYNAME_MANAGER_CLASS, String.class);
		if (null == className)
		{
			throw new MandatoryPropertyMissingException(PROPERTYNAME_MANAGER_CLASS);
		}
		if (className.indexOf(".") < 0)
		{
			className = SimpleSessionValidatorFactory.class.getPackage().getName() + "." + className;
		}

		try
		{
			Class<SessionValidator> clazz = (Class)Class.forName(className);
			return (SessionValidator) clazz.newInstance();
		}
		catch (Exception e)
		{
			throw new UnknownSessionValidatorClassException(className, e);
		}
	}
}
