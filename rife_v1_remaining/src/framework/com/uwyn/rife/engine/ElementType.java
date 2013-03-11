/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementType.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.datastructures.EnumClass;

public class ElementType extends EnumClass<String>
{
	public static final ElementType	JAVA_CLASS = new ElementType("java class");
	public static final ElementType	JAVA_INSTANCE = new ElementType("java instance");
	public static final ElementType	SCRIPT = new ElementType("scripted");
	
	ElementType(String identifier)
	{
		super(identifier);
	}
}

