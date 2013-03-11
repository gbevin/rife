/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PrintAndWriteBuffer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;
import java.io.IOException;

public class PrintAndWriteBuffer extends Element
{
	public void processElement()
	{
		enableTextBuffer(true);
		
		try
		{
			print("print1");
			getOutputStream().write("write2".getBytes(getResponseCharacterEncoding()));
			print("print3");
			getOutputStream().write("write4".getBytes(getResponseCharacterEncoding()));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}

