/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Scope.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.datastructures.EnumClass;

public class Scope extends EnumClass<String>
{
	public static final Scope	LOCAL = new Scope("local");
	public static final Scope	GLOBAL = new Scope("global");
	
	Scope(String identifier)
	{
		super(identifier);
	}
	
	public static Scope getScope(String name)
	{
		return getMember(Scope.class, name);
	}
}

