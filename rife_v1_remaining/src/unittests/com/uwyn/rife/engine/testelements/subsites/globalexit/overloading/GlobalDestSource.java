/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalDestSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites.globalexit.overloading;

import com.uwyn.rife.engine.Element;

public class GlobalDestSource extends Element
{
	public void processElement()
	{
		exit("globalexit"+getInput("exitselector"));
	}
}

