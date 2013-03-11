/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RoleUsersManagerRetriever.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.SessionValidatorRetriever;
import com.uwyn.rife.authentication.credentialsmanagers.exceptions.NotRoleUsersManagedException;
import com.uwyn.rife.engine.ElementInfo;
import com.uwyn.rife.engine.Site;

/**
 * This abstract class provides the functionalities to retrieve a {@link
 * com.uwyn.rife.authentication.credentialsmanagers.RoleUsersManager
 * RoleUsersManager} from a particular {@link
 * com.uwyn.rife.authentication.elements.Authenticated Authenticated} element
 * in a site.
 * <p>Since you can have many authentication schemes and backends being active
 * in a single web application. it's quite verbose to retrieve a
 * RoleUsersManager when you want to perform some operations on its stored
 * credentials. This class provides the functionalities to quickly perform
 * this retrieval.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @author JR Boyens (gnu-jrb[remove] at gmx dot net)
 * @see com.uwyn.rife.authentication.credentialsmanagers.RoleUsersManager
 * @see com.uwyn.rife.authentication.elements.Authenticated
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class RoleUsersManagerRetriever
{
	/**
	 * Retrieves a {@code RoleUsersManager} manager from an
	 * {@code Authenticated} element in a {@code Site}.
	 * 
	 * @param site the site in which the authenticated element is declared
	 * @param authElementId the absolute ID of the authenticated element that
	 * provides the {@code RoleUsersManager}
	 * @param reference a reference element against which to resolve the id; or
	 * {@code null} if the provided id is absolute
	 * @exception com.uwyn.rife.authentication.credentialsmanagers.exceptions.AuthenticatedElementNotFoundException when the element ID
	 * couldn't be found in the site
	 * @exception com.uwyn.rife.authentication.credentialsmanagers.exceptions.NotAuthenticatedElementException when the element ID doesn't
	 * refer to an {@code Authenticated} element
	 * @exception NotRoleUsersManagedException when the
	 * {@code CredentialsManager} of the Authenticated element is not a
	 * {@code RoleUsersManager}
	 * @since 1.0
	 */
	public static RoleUsersManager getRoleUsersManager(Site site, String authElementId, ElementInfo reference)
	{
		SessionValidator    validator = SessionValidatorRetriever.getSessionValidator(site, authElementId, reference);
		CredentialsManager  credentials = validator.getCredentialsManager();
		if (null == credentials ||
			!(credentials instanceof RoleUsersManager))
		{
			throw new NotRoleUsersManagedException(authElementId);
		}
		
		return (RoleUsersManager)credentials;
	}
}

