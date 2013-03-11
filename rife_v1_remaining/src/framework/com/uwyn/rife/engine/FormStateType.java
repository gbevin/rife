/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FormStateType.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.datastructures.EnumClass;

public class FormStateType extends EnumClass<String>
{
	public static final FormStateType	PARAMS = new FormStateType("PARAMS");
	public static final FormStateType	JAVASCRIPT = new FormStateType("JAVASCRIPT");

	FormStateType(String identifier)
	{
		super(identifier);
	}

	public static FormStateType getStateType(String name)
	{
		if (null == name)
		{
			return PARAMS;
		}

		name = name.toUpperCase();
		FormStateType method = getMember(FormStateType.class, name);
		if (null == method)
		{
			method = new FormStateType(name);
		}
		return method;
	}
}

