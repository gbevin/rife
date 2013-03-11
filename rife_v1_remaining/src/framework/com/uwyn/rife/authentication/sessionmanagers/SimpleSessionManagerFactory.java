/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SimpleSessionManagerFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers;

import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.elements.exceptions.UnknownSessionManagerFactoryClassException;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import java.util.HashMap;

/**
 * Simple caching session manager factory. This keeps a cache of session manager
 * instances by name. This is used, for example, to create
 * {@link MemorySessions} objects; it may be used for any session manager that
 * doesn't require configuration information at startup time.
 * <p>
 * Element properties used:
 * <dl>
 * <dt>{@value #PROPERTYNAME_MANAGER_CLASS}</dt>
 * <dd>Name of the session manager class. If the class name is not fully
 * qualified, the package name
 * {@code com.uwyn.rife.authentication.sessionmanagers}
 * will be assumed.</dd>
 * <dt>{@value #PROPERTYNAME_MANAGER_ID}</dt>
 * <dd>Unique ID for this session manager instance. Optional. Use this if you
 * need different elements to maintain separate session stores.</dd>
 * </dl>
 * <p>
 * If you need logic other than a simple "new" for your session managers,
 * implement {@link SessionManagerFactory} instead.
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see SessionManager
 * @see SessionManagerFactory
 * @since 1.6
 */
public class SimpleSessionManagerFactory implements SessionManagerFactory
{
	/** Element property that specifies the ID for a session manager instance. */
	public static final String PROPERTYNAME_MANAGER_ID = "sessionmanager_id";
	
	/** Element property that specifies the class name for session managers. */
	public static final String PROPERTYNAME_MANAGER_CLASS = "sessionmanager_class";
	
	private static HashMap<String, SessionManager>	mSessionManagers = new HashMap<String, SessionManager>();
	
	public SessionManager getManager(HierarchicalProperties properties)
	throws PropertyValueException
	{
		String className = properties.getValueString(PROPERTYNAME_MANAGER_CLASS);
		if (null == className)
		{
			throw new MandatoryPropertyMissingException(PROPERTYNAME_MANAGER_CLASS);
		}
		if (className.indexOf(".") < 0)
		{
			className = SimpleSessionManagerFactory.class.getPackage().getName() + "." + className;
		}

		// Include the class name in the cache key so we don't give the caller
		// an unexpected session manager class if there's an identifier
		// collision (e.g. because the caller isn't specifying an ID
		// explicitly.)
		String identifier = className + ":" + properties.getValueString(PROPERTYNAME_MANAGER_ID, "");

		SessionManager session_manager = mSessionManagers.get(identifier);

		if (null == session_manager)
		{
			try
			{
				Class<SessionManager> clazz = (Class)Class.forName(className);
				session_manager = clazz.newInstance();
				mSessionManagers.put(identifier, session_manager);
			}
			catch (Exception e)
			{
				throw new UnknownSessionManagerFactoryClassException(className, e);
			}
		}

		return session_manager;
	}
}
