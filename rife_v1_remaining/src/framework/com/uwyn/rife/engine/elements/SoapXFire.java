/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SoapXFire.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;

@Elem
public class SoapXFire extends Element
{
	public Class getDeploymentClass()
	{
		return SoapXFireDeployer.class;
	}
	
	public void processElement()
	{
		SoapXFireDeployer deployer = (SoapXFireDeployer)getDeployer();
		try
		{
			deployer.getController().doService(getHttpServletRequest(), getHttpServletResponse());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public boolean prohibitRawAccess()
	{
		return false;
	}
}
