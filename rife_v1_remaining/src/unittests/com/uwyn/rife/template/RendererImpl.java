/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RendererImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

public class RendererImpl implements ValueRenderer
{
	private static int sCount = 0;
	
	public String render(Template template, String valueName, String differentiator)
	{
		return valueName.toUpperCase()+differentiator+":"+(++sCount);
	}
}
