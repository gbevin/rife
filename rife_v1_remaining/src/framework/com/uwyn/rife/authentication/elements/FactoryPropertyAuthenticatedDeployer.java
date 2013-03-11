/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FactoryPropertyAuthenticatedDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.credentialsmanagers.CredentialsManagerFactoryFactory;
import com.uwyn.rife.authentication.remembermanagers.RememberManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionvalidators.SessionValidatorFactoryFactory;
import com.uwyn.rife.engine.exceptions.PropertyValueErrorException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;

/**
 * Deployer for {@link Authenticated} elements that uses properties to determine
 * which factory classes will create the various authentication objects.
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see SessionManagerFactoryFactory
 * @see SessionValidatorFactoryFactory
 * @see CredentialsManagerFactoryFactory
 * @see RememberManagerFactoryFactory
 * @since 1.6
 */
public class FactoryPropertyAuthenticatedDeployer extends AbstractPropertyAuthenticatedDeployer
{
	public SessionManager createSessionManager()
	{
		try
		{
			return SessionManagerFactoryFactory.getManager(getElementInfo().getProperties());
		}
		catch (PropertyValueException e)
		{
			throw new PropertyValueErrorException(getElementInfo().getDeclarationName(), e);
		}
	}
	
	public SessionValidator createSessionValidator()
	{
		try
		{
			return SessionValidatorFactoryFactory.getValidator(getElementInfo().getProperties());
		}
		catch (PropertyValueException e)
		{
			throw new PropertyValueErrorException(getElementInfo().getDeclarationName(), e);
		}
	}
	
	public CredentialsManager createCredentialsManager()
	{
		try
		{
			return CredentialsManagerFactoryFactory.getManager(getElementInfo().getProperties());
		}
		catch (PropertyValueException e)
		{
			throw new PropertyValueErrorException(getElementInfo().getDeclarationName(), e);
		}
	}
	
	public RememberManager createRememberManager()
	{
		try
		{
			return RememberManagerFactoryFactory.getManager(getElementInfo().getProperties());
		}
		catch (PropertyValueException e)
		{
			throw new PropertyValueErrorException(getElementInfo().getDeclarationName(), e);
		}
	}
}
