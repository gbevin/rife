/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SimpleLogoutTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.remembermanagers.RememberManagerFactoryFactory;
import com.uwyn.rife.authentication.sessionmanagers.SessionManagerFactoryFactory;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.exceptions.PropertyValueErrorException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;

/**
 * 
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
@Elem
public class SimpleLogoutTemplate extends AbstractLogoutTemplate
{
	public void initialize()
	{
		try
		{
			setSessionManager(SessionManagerFactoryFactory.getManager(getElementInfo().getProperties()));
			setRememberManager(RememberManagerFactoryFactory.getManager(getElementInfo().getProperties()));
		}
		catch (PropertyValueException e)
		{
			throw new PropertyValueErrorException(getDeclarationName(), e);
		}
	}
}
