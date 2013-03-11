/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NoPause.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class NoPause extends Element
{
	public void processElement()
	{
		String before = "before simple pause";
		assert before != null;
		String after = "after simple pause";
		assert after != null;
		
		print(getContinuationId());
	}
}

