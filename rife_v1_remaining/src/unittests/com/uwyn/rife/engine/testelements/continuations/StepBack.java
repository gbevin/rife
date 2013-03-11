/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StepBack.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class StepBack extends Element
{
	private int mTotal = -5;
	
	private boolean mStart = false;
	public void setStart(boolean start) { mStart = start; }
	
	public void processElement()
	{
		Template template = getHtmlTemplate("engine_continuation_stepback");
		
		if (mTotal < 0)
		{
			mTotal++;
			stepBack();
		}

		template.setValue("stepback", duringStepBack());
		print(template);
		pause();
		
		if (mStart)
		{
			template.setValue("subtotal", mTotal);
			template.setValue("stepback", duringStepBack());
			print(template);
			pause();
			mTotal += getParameterInt("answer", 0);
			
			if (mTotal < 50)
			{
				stepBack();
			}
		}
		
		template.setValue("subtotal", "got a total of "+mTotal);
		template.setValue("stepback", duringStepBack());
		print(template);
	}
}
