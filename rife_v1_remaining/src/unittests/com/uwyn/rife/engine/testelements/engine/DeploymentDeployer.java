/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DeploymentDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.ElementDeployer;
import com.uwyn.rife.engine.exceptions.EngineException;

public class DeploymentDeployer extends ElementDeployer
{
	private static int	mCount = 0;
	
	public DeploymentDeployer()
	{
	}

	public void deploy()
	throws EngineException
	{
		mCount++;
	}
	
	public String getParam()
	{
		return getElementInfo().getPropertyString("theproperty");
	}
	
	public int getCount()
	{
		return mCount;
	}
}

