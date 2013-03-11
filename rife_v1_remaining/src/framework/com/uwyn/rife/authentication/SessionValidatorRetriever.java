/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SessionValidatorRetriever.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication;

import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.AuthenticatedElementNotFoundException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.NotAuthenticatedElementException;
import com.uwyn.rife.authentication.elements.AuthenticatedDeployer;
import com.uwyn.rife.engine.ElementDeployer;
import com.uwyn.rife.engine.ElementInfo;
import com.uwyn.rife.engine.Site;

/**
 * This abstract class provides the functionalities to retrieve a {@link
 * com.uwyn.rife.authentication.SessionValidator SessionValidator} from a
 * particular {@link com.uwyn.rife.authentication.elements.Authenticated
 * Authenticated} element in a site.
 * <p>Since you can have many authentication schemes and backends being active
 * in a single web application. it's quite verbose to retrieve a
 * {@code SessionValidator} when you want to perform some operations on
 * its stored credentials. This class provides the functionalities to quickly
 * perform this retrieval.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @author JR Boyens (gnu-jrb[remove] at gmx dot net)
 * @see com.uwyn.rife.authentication.SessionValidator
 * @see com.uwyn.rife.authentication.elements.Authenticated
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class SessionValidatorRetriever
{
	/**
	 * Retrieves a {@code SessionValidator} manager from an
	 * {@code Authenticated} element in a {@code Site}.
	 * 
	 * @param site the site in which the authenticated element is declared
	 * @param authElementId the absolute ID of the authenticated element that
	 * provides all the authentication related managers
	 * @param reference a reference element against which to resolve the id;
	 * or {@code null} if the provided id is absolute
	 * @exception AuthenticatedElementNotFoundException when the element ID
	 * couldn't be found in the site
	 * @exception NotAuthenticatedElementException when the element ID doesn't
	 * refer to an {@code Authenticated} element
	 * @since 1.0
	 */
	public static SessionValidator getSessionValidator(Site site, String authElementId, ElementInfo reference)
	{
		if (null == site)                   throw new IllegalArgumentException("site can't be null");
		if (null == authElementId)          throw new IllegalArgumentException("authElementId can't be null");
		if (0 == authElementId.length())    throw new IllegalArgumentException("authElementId can't be empty");
		
		ElementInfo     auth_elementinfo = site.resolveId(authElementId, reference);
		if (null == auth_elementinfo)
		{
			throw new AuthenticatedElementNotFoundException(authElementId);
		}
		
		return getSessionValidator(auth_elementinfo);
	}
	
	/**
	 * Retrieves a {@code SessionValidator} manager from an
	 * {@code Authenticated} element in a {@code Site}.
	 * 
	 * @param authElementInfo the {@code ElementInfo} of the authenticated
	 * element that provides all the authentication related managers
	 * @exception NotAuthenticatedElementException when the provided element info
	 * doesn't refer to an {@code Authenticated} element
	 * @since 1.4
	 */
	public static SessionValidator getSessionValidator(ElementInfo authElementInfo)
	{
		if (null == authElementInfo)	throw new IllegalArgumentException("authElementInfo can't be null");
		
		ElementDeployer deployer = authElementInfo.getDeployer();
		if (null == deployer ||
			!(deployer instanceof AuthenticatedDeployer))
		{
			throw new NotAuthenticatedElementException(authElementInfo.getId());
		}
		AuthenticatedDeployer auth_deployer = (AuthenticatedDeployer)deployer;
		
		SessionValidator    validator = auth_deployer.getSessionValidator();
		
		return validator;
	}
}

