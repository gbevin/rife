/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RoleUserCredentials.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentials;

import com.uwyn.rife.authentication.Credentials;

/**
 * <p>This interface needs to be implemented by all credentials classes that
 * work with {@link
 * com.uwyn.rife.authentication.credentialsmanagers.RoleUsersManager}s, which
 * is the default user management in RIFE.
 * <p>Credentials aren't the same as the actual account information of a user,
 * they provide the data that is submitted and that needs to be verified.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface RoleUserCredentials extends Credentials, RememberMe
{
	/**
	 * Retrieves the submitted login.
	 * 
	 * @return the login
	 * @since 1.0
	 */
	public String getLogin();
	/**
	 * Sets the login to submit.
	 * 
	 * @param login the login
	 * @since 1.0
	 */
	public void setLogin(String login);
	/**
	 * Retrieves the submitted password.
	 * 
	 * @return the password
	 * @since 1.0
	 */
	public String getPassword();
	/**
	 * Sets the password to submit.
	 * 
	 * @param password the password
	 * @since 1.0
	 */
	public void setPassword(String password);
	/**
	 * Retrieves the submitted role.
	 * 
	 * @return the role
	 * @since 1.0
	 */
	public String getRole();
	/**
	 * Sets the role to submit.
	 * 
	 * @param role the role
	 * @since 1.0
	 */
	public void setRole(String role);
}
