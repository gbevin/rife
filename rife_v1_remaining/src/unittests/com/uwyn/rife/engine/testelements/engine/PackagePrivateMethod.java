/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PackagePrivateMethod.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;

public class PackagePrivateMethod extends Element
{
	public void processElement()
	{
		print(new PackagePrivateMethodOutput().getOutput("string", new int[] {1, 2, 3}, true, (byte)9, 'k', 23.8f, 2389, (short)23, 398743.343d, 3434L));
	}
}

