/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExternalValue.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.tools.StringUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ExternalValue extends ArrayList<CharSequence> implements CharSequence
{
	private static final long serialVersionUID = -7361025452353622788L;
	
	private int	mSize = 0;
	
	public ExternalValue()
	{
		super();
	}
	
	public int length()
	{
		return toString().length();
	}
	
	public char charAt(int index)
	{
		return toString().charAt(index);
	}
	
	public CharSequence subSequence(int start, int end)
	{
		return toString().subSequence(start, end);
	}
	
	public void append(CharSequence value)
	{
		mSize += value.length();
		add(value);
	}
	
	public String toString()
	{
		StringBuilder	result = new StringBuilder(mSize);
		
		for (CharSequence charsequence: this)
		{
			// force JDK 1.4 compatibility by preventing that the append(CharSequence) is used
			result.append((Object)charsequence);
		}
		
		return result.toString();
	}
	
	public void write(OutputStream out, String charsetName)
	throws IOException
	{
		if (null == charsetName)
		{
			charsetName = StringUtils.ENCODING_UTF_8;
		}
		
		for (CharSequence charsequence: this)
		{
			if (charsequence instanceof com.uwyn.rife.template.InternalString)
			{
				out.write(((InternalString)charsequence).getBytes(charsetName));
			}
			else if (charsequence instanceof java.lang.String)
			{
				out.write(((String)charsequence).getBytes(charsetName));
			}
		}
	}
}

