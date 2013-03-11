/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FactoryPropertyAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

/**
 * Authenticated element that uses element properties to determine which
 * authentication objects to use. Most of the properties are actually
 * used by the deployer class, {@link FactoryPropertyAuthenticatedDeployer}.
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
import com.uwyn.rife.engine.annotations.Elem;

@Elem
public class FactoryPropertyAuthenticated extends RoleUserAuthenticated
{
	public Class getDeploymentClass()
	{
		return FactoryPropertyAuthenticatedDeployer.class;
	}
}
