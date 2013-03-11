/*
 * Copyright 2003 Geert Bevin <gbevin@uwyn.com>
 * $Id: Null.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class Null extends Element
{
	public void processElement()
	{
		String	response = null;
		
		print("before null pause\n"+getContinuationId());
		pause();
		
		response = getInput("response");
		
		print(response);
	}
}
