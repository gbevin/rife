/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PrivateMethod.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.exceptions.EngineException;

public class PrivateMethod extends Element
{
	public int getInt()
	{
		return 1234;
	}
	
	public void processElement() throws EngineException
	{
		int result = getInt();
		
		print(getContinuationId());
		pause();
		print(result);
	}
}
