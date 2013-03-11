/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Capability.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.capabilities;

import com.uwyn.rife.datastructures.*;

public class Capability extends EnumClass<String>
{
	public static final Capability	LIMIT = new Capability("LIMIT");
	public static final Capability	LIMIT_PARAMETER = new Capability("LIMIT_PARAMETER");
	public static final Capability	OFFSET = new Capability("OFFSET");
	public static final Capability	OFFSET_PARAMETER = new Capability("OFFSET_PARAMETER");
	
	Capability(String identifier)
	{
		super(identifier);
	}
	
	public static Capability getMethod(String name)
	{
		return getMember(Capability.class, name);
	}
}

