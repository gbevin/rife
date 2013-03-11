/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EncoderSql.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.tools.StringUtils;

public class EncoderSql implements TemplateEncoder
{
	EncoderSql()
	{
	}
	
	public static EncoderSql getInstance()
	{
		return EncoderSqlSingleton.INSTANCE;
	}
	
	public String encode(String value)
	{
		return StringUtils.encodeSql(value);
	}
	
	public final String encodeDefensive(String value)
	{
		return value;
	}
}
