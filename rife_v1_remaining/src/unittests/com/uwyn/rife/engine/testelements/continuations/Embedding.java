/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Embedding.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class Embedding extends Element
{
	public void processElement()
	{
		int total = 0;
		while (total < 50)
		{
			print(getHtmlTemplate("engine_continuation_embedding"));
			pause();
			total += getParameterInt("answer", 0);
		}
		
		print("got a total of "+total);
	}
}

