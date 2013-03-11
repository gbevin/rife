/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InstanceOf.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class InstanceOf extends Element
{
	public void processElement()
	{
		String before = "before instanceof pause";
		Object after = "after instanceof pause";
		
		print(before+"\n"+getContinuationId());
		pause();
		
		String after_string = null;
		if (after instanceof String)
		{
			after_string = (String)after;
		}
		
		print(after_string);
	}
}

