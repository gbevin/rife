/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AnnotationsSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Datalink;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.Flowlink;
import com.uwyn.rife.engine.annotations.OutputProperty;

@Elem(
	flowlinks = {
		@Flowlink(
			srcExit = "exit1",
			destClass = AnnotationsDest.class,
			destClassIdPrefix = "ANNOTATIONS",
			datalinks = {
				@Datalink(srcOutput = "output1", destInput = "input1"),
				@Datalink(srcOutput = "output2", destInput = "input2")
			})
	}
)
public class AnnotationsSource extends Element
{
	private String	mOutput1;
	private String	mOutput2;
	
	@OutputProperty
	public String getOutput1()
	{
		return mOutput1;
	}
	
	@OutputProperty
	public String getOutput2()
	{
		return mOutput2;
	}
	
	public void processElement()
	{
		mOutput1 = "output value 1";
		mOutput2 =  "output value 2";
		
		exit("exit1");
	}
}

