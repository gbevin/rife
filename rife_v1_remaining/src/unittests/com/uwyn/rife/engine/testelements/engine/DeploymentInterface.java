/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DeploymentInterface.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.ElementAware;
import com.uwyn.rife.engine.ElementSupport;

public class DeploymentInterface implements ElementAware
{
	private ElementSupport	mElement = null;
	
	public void noticeElement(ElementSupport element)
	{
		mElement = element;
		mElement.setDeploymentClass(DeploymentDeployerInterface.class);
	}
	
	public void processElement()
	{
		mElement.print(((DeploymentDeployerInterface)mElement.getDeployer()).getParam()+":"+((DeploymentDeployerInterface)mElement.getDeployer()).getCount());
	}
}

