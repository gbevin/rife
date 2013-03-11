/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AuthenticationUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication;

import com.uwyn.rife.authentication.credentialsmanagers.exceptions.AuthenticatedElementNotFoundException;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.NotAuthenticatedElementException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.engine.ElementInfo;
import com.uwyn.rife.engine.Site;

/**
 * This abstract class provides convenience shortcut methods to
 * perform common operations with the authentication framework
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.4
 */
public abstract class AuthenticationUtils
{
	/**
	 * Starts a new authentication session for a particular user and 
	 * {@link com.uwyn.rife.authentication.elements.Authenticated Authenticated}
	 * element in a site.
	 * 
	 * @param site the site in which the authenticated element is declared
	 * @param authElementId the absolute ID of the authenticated element that
	 * provides all the authentication related managers
	 * @param reference a reference element against which to resolve the id;
	 * or {@code null} if the provided id is absolute
	 * @param userId The id that uniquely identifies the user that is allowed
	 * to use this session.
	 * @param hostIp The ip address of the host from which the user accesses
	 * the application.
	 * @param remembered Indicates whether the session is started through
	 * remember me or from scratch.
	 * @return A {@code String} that uniquely identifies the
	 * authentication session that was just started.
	 * @exception AuthenticatedElementNotFoundException when the element ID
	 * couldn't be found in the site
	 * @exception NotAuthenticatedElementException when the element ID doesn't
	 * refer to an {@code Authenticated} element
	 * @exception SessionManagerException when an error occurred while startin
	 * the authentication session in a manager
	 * @since 1.4
	 */
	public static String startAuthenticationSession(Site site, String authElementId, ElementInfo reference, long userId, String hostIp, boolean remembered)
	throws AuthenticatedElementNotFoundException, NotAuthenticatedElementException, SessionManagerException
	{
		if (null == site)                   throw new IllegalArgumentException("site can't be null");
		if (null == authElementId)          throw new IllegalArgumentException("authElementId can't be null");
		if (0 == authElementId.length())    throw new IllegalArgumentException("authElementId can't be empty");
		
		return SessionValidatorRetriever
			.getSessionValidator(site, authElementId, reference)
			.getSessionManager()
			.startSession(userId, hostIp, remembered);
	}
	
	/**
	 * Starts a new authentication session for a particular user and 
	 * {@link com.uwyn.rife.authentication.elements.Authenticated Authenticated}
	 * element in a site.
	 * 
	 * @param authElementInfo the {@code ElementInfo} of the authenticated
	 * element that provides all the authentication related managers
	 * @param userId The id that uniquely identifies the user that is allowed
	 * to use this session.
	 * @param hostIp The ip address of the host from which the user accesses
	 * the application.
	 * @param remembered Indicates whether the session is started through
	 * remember me or from scratch.
	 * @return A {@code String} that uniquely identifies the
	 * authentication session that was just started.
	 * @exception AuthenticatedElementNotFoundException when the element ID
	 * couldn't be found in the site
	 * @exception NotAuthenticatedElementException when the element ID doesn't
	 * refer to an {@code Authenticated} element
	 * @exception SessionManagerException when an error occurred while startin
	 * the authentication session in a manager
	 * @since 1.4
	 */
	public static String startAuthenticationSession(ElementInfo authElementInfo, long userId, String hostIp, boolean remembered)
	throws AuthenticatedElementNotFoundException, NotAuthenticatedElementException, SessionManagerException
	{
		if (null == authElementInfo)	throw new IllegalArgumentException("authElementInfo can't be null");
		
		return SessionValidatorRetriever
			.getSessionValidator(authElementInfo)
			.getSessionManager()
			.startSession(userId, hostIp, remembered);
	}
}

