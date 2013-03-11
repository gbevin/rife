/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MockInitConfig.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import com.uwyn.rife.engine.InitConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.ServletContext;

class MockInitConfig implements InitConfig
{
	public Enumeration getInitParameterNames()
	{
		return Collections.enumeration(new ArrayList());
	}
	
	public String getInitParameter(String name)
	{
		return null;
	}
	
	public ServletContext getServletContext()
	{
		return null;
	}
}

