/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RoleUser.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentials;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;

/**
 * <p>Provides standard {@link RoleUserCredentials} functionalities by
 * implementing the property accessors and setting up basic validation rules.
 * These rules make the login and password mandatory and limit their length
 * according to the settings in {@link
 * com.uwyn.rife.config.RifeConfig.Authentication}.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class RoleUser extends Validation<ConstrainedBean, ConstrainedProperty> implements RoleUserCredentials, RememberMe
{
	private String  mLogin = null;
	private String  mPassword = null;
	private String  mRole = null;
	private boolean mRemember = false;

	public RoleUser()
	{
	}
	
	protected void activateValidation()
	{
		addConstraint(new ConstrainedProperty("login")
						  .notNull(true)
						  .minLength(RifeConfig.Authentication.getLoginMinimumLength())
						  .maxLength(RifeConfig.Authentication.getLoginMaximumLength()));
		addConstraint(new ConstrainedProperty("password")
						  .notNull(true)
						  .minLength(RifeConfig.Authentication.getPasswordMinimumLength())
						  .maxLength(RifeConfig.Authentication.getPasswordMaximumLength()));
	}

	public RoleUser(String login, String password)
	{
		this();
		setLogin(login);
		setPassword(password);
	}

	public RoleUser(String login, String password, String role)
	{
		this();
		setLogin(login);
		setPassword(password);
		setRole(role);
	}

	public String getLogin()
	{
		return mLogin;
	}
	
	public RoleUser login(String login)
	{
		setLogin(login);
		
		return this;
	}

	public void setLogin(String login)
	{
		mLogin = login;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public RoleUser password(String password)
	{
		setPassword(password);
		
		return this;
	}

	public void setPassword(String password)
	{
		mPassword = password;
	}

	public String getRole()
	{
		return mRole;
	}

	public RoleUser role(String role)
	{
		setRole(role);
		
		return this;
	}

	public void setRole(String role)
	{
		mRole = role;
	}
	
	public boolean getRemember()
	{
		return mRemember;
	}
	
	public RoleUser remember(boolean remember)
	{
		setRemember(remember);
		
		return this;
	}
	
	public void setRemember(boolean remember)
	{
		mRemember = remember;
	}
}

