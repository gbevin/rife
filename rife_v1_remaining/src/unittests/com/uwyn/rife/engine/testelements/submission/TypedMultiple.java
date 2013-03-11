/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TypedMultiple.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class TypedMultiple extends Element
{
	public void processElement()
	{
		print("paramstring:"+StringUtils.join(getParameterValues("paramstring"), ","));
		print("paramint:"+StringUtils.join(getParameterIntValues("paramint"), ","));
		print("paramlong:"+StringUtils.join(getParameterLongValues("paramlong"), ","));
		print("paramdouble:"+StringUtils.join(getParameterDoubleValues("paramdouble"), ","));
		print("paramfloat:"+StringUtils.join(getParameterFloatValues("paramfloat"), ","));
	}
}

