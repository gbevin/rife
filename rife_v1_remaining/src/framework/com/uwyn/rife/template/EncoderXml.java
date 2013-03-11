/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EncoderXml.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.tools.StringUtils;

public class EncoderXml implements TemplateEncoder
{
	EncoderXml()
	{
	}
	
	public static EncoderXml getInstance()
	{
		return EncoderXmlSingleton.INSTANCE;
	}
	
	public String encode(String value)
	{
		return StringUtils.encodeXml(value);
	}
	
	public final String encodeDefensive(String value)
	{
		return value;
	}
}
