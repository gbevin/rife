/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EmbeddedStream.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.tools.ExceptionUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;

class EmbeddedStream extends ByteArrayOutputStream
{
	private ArrayList<CharSequence>	mEmbeddedContent = new ArrayList<CharSequence>();
	
	public void write(CharSequence chars)
	{
		mEmbeddedContent.add(chars);
	}
	
	public void flush() throws IOException
	{
		try
		{
			write(toString(RifeConfig.Engine.getResponseEncoding()));
		}
		catch (UnsupportedEncodingException e)
		{
			Logger.getLogger("com.uwyn.rife.engine").severe(ExceptionUtils.getExceptionStackTrace(e));
		}
		
		reset();
	}
	
	ArrayList<CharSequence> getEmbeddedContent()
	{
		return mEmbeddedContent;
	}
}
