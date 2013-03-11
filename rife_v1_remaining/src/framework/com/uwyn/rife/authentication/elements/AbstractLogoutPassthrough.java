/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractLogoutPassthrough.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

public abstract class AbstractLogoutPassthrough extends AbstractLogout
{
	protected AbstractLogoutPassthrough()
	{
	}
	
	protected void init()
	{
	}
	
	protected void entrance()
	{
	}

	protected void loggedOut()
	{
	}
	
	public void processElement()
	{
		assert mSessionManager != null;
		
		init();
		
		entrance();
		performLogout();
		loggedOut();
		
		if (getElementInfo().getFlowLink("logged_out") != null)
		{
			exit("logged_out");
		}
	}
}
