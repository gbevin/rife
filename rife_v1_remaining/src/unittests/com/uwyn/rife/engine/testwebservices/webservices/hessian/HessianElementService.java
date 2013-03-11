/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: HessianElementService.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testwebservices.webservices.hessian;

import com.uwyn.rife.engine.ElementService;
import com.uwyn.rife.engine.ElementSupport;

public class HessianElementService implements HessianElementServiceApi, ElementService
{
	private ElementSupport	mElement = null;
	
	public void setRequestElement(ElementSupport element)
	{
		mElement = element;
	}
	
	public String getElementInput(String name)
	{
		return mElement.getInput(name);
	}
}
