/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Forward.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;

public class Forward extends Element
{
	public void processElement()
	{
		int go = getInputInt("go", 0);
		switch (go)
		{
			case 1:
				forward("/simple/html");
				break;
			case 2:
				forward("/notfound");
				break;
		}
		
		print("not forwarded");
	}
}

