/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PropertyMismatchParam.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.annotations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.ParamProperty;

@Elem
public class PropertyMismatchParam extends Element
{
	public final static String PROPERTY_NAME = "badname";

	@ParamProperty(name=PROPERTY_NAME)
	public void setParam(String var)
	{
	}
}
