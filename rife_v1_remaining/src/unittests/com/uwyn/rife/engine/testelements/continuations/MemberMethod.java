/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemberMethod.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class MemberMethod extends Element
{
	public String createString(String first, String second, long number)
	{
		return first+" "+second+" "+number;
	}
						  
	public void processElement()
	{
		StringBuffer stringbuffer1 = new StringBuffer(createString("some","value", 6899L));

		print("before pause"+"\n"+getContinuationId());
		pause();
		
		print(stringbuffer1.substring(2));
	}
}

