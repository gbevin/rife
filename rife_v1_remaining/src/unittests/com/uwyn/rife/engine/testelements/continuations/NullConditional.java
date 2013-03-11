/*
 * Copyright 2003 Geert Bevin <gbevin@uwyn.com>
 * $Id: NullConditional.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class NullConditional extends Element
{
	public void processElement()
	{
		String value = null;
		if (hasInputValue("value"))
		{
			value = getInput("value");
		}

		print(value);
		print(getContinuationId());
		pause();

		print(value);
	}
}

