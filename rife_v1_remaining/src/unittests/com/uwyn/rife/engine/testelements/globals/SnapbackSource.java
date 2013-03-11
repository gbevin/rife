/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SnapbackSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;

import com.uwyn.rife.engine.Element;

public class SnapbackSource extends Element
{
	public void processElement()
	{
		if (hasInputValue("activate_exit"))
		{
			exit("exit1");
		}
		
		print("the content of SnapbackSource");
	}
}

