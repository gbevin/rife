/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PathInfoMode.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.datastructures.EnumClass;

public class PathInfoMode extends EnumClass<String>
{
	public static final PathInfoMode	LOOSE = new PathInfoMode("LOOSE");
	public static final PathInfoMode	STRICT = new PathInfoMode("STRICT");

	PathInfoMode(String identifier)
	{
		super(identifier);
	}

	public static PathInfoMode getMode(String name)
	{
		if (null == name)
		{
			return LOOSE;
		}

		name = name.toUpperCase();
		PathInfoMode mode = getMember(PathInfoMode.class, name);
		if (null == mode)
		{
			mode = new PathInfoMode(name);
		}
		return mode;
	}
}

