/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CredentialsManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

/**
 * This interface defines the methods that classes with
 * {@code CredentialsManager} functionalities have to implement.
 * <p>A {@code CredentialsManager} is in charge of verifying
 * {@code Credentials} instances. Using the information that a
 * {@code CredentialsManager} provides, the authentication system is able
 * to take appropriate actions (ie. start a new session, provide informational
 * messages about a user's status, and so on).
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see Credentials
 * @see SessionValidator
 * @since 1.0
 */
public interface CredentialsManager
{
	/**
	 * Verifies the validity of the provided {@code Credentials}
	 * instance.
	 * 
	 * @param credentials The {@code Credentials} instance that needs to
	 * be verified.
	 * @return A {@code long} that uniquely identifies the user that
	 * corresponds to the validated credentials; or
	 * <p>{@code -1} if the credentials are invalid.
	 * @exception CredentialsManagerException An undefined number of
	 * exceptional cases or error situations can occur when credentials are
	 * verified. They are all indicated by throwing an instance of
	 * {@code CredentialsManagerException}. It's up to the
	 * implementations of this interface to give more specific meanings to
	 * these exceptions.
	 * @since 1.0
	 */
	public long verifyCredentials(Credentials credentials) throws CredentialsManagerException;
}

