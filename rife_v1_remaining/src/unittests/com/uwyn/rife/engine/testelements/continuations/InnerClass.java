/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InnerClass.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class InnerClass extends Element
{
	public void processElement()
	{
		Inner	inner = new Inner();
		String	before = "before pause";
		
		print(before+"\n"+getContinuationId());
		pause();
		print(inner.getOutput());
	}
	
	class Inner implements Cloneable
	{
		public Inner()
		{
		}
		
		public String getOutput()
		{
			return "InnerClass's output";
		}

		public Object clone()
		throws CloneNotSupportedException
		{
			return super.clone();
		}
	}
}

