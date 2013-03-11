/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EncoderDummy.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

public class EncoderDummy implements TemplateEncoder
{
	EncoderDummy()
	{
	}
	
	public static EncoderDummy getInstance()
	{
		return EncoderDummySingleton.INSTANCE;
	}
	
	public final String encode(String value)
	{
		return value;
	}
	
	public final String encodeDefensive(String value)
	{
		return value;
	}
}
