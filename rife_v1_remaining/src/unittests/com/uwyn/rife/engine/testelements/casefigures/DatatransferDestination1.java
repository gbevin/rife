/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatatransferDestination1.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.casefigures;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class DatatransferDestination1 extends Element
{
	public void processElement()
	{
		print(StringUtils.join(getInputValues("input1"),"|")+","+getInput("input2"));
	}
}

