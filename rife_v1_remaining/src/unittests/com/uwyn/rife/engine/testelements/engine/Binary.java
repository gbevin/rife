/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Binary.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.IntegerUtils;
import java.io.IOException;
import java.io.OutputStream;

public class Binary extends Element
{
	public void processElement()
	{
		OutputStream outputstream = getOutputStream();
		try
		{
			outputstream.write(IntegerUtils.intToBytes(87634675));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}

