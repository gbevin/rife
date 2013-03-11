/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InitConfig.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.Enumeration;
import javax.servlet.ServletContext;

public interface InitConfig
{
	public Enumeration getInitParameterNames();
	public String getInitParameter(String name);
	public ServletContext getServletContext();
}

