/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PurgingMemoryAuthenticated.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.engine.annotations.Elem;

/**
 * Sets up the standard {@link Authenticated} element for managers that
 * work with an in-memory back-end and enables automatic in-process purging.
 *
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
@Elem
public class PurgingMemoryAuthenticated extends RoleUserAuthenticated
{
	public Class getDeploymentClass()
	{
		return PurgingMemoryAuthenticatedDeployer.class;
	}
}
