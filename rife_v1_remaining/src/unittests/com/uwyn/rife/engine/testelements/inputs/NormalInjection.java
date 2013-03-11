/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NormalInjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.BeanUtils;
import java.util.Date;

public class NormalInjection extends Element
{
	private String	mInput1;
	private Date	mInput2;
	private Date	mInput3;
	
	public void setInput1(String input1)	{ mInput1 = input1; }
	public void setInput2(Date input2)		{ mInput2 = input2; }
	public void setInput3(Date input3)		{ mInput3 = input3; }
	
	public void processElement()
	{
		print("another response");
		
		if (mInput1 != null)
		{
			print(mInput1);
		}
		print(",");
		if (mInput2 != null)
		{
			print(BeanUtils.getConcisePreciseDateFormat().format(mInput2));
		}
		print(",");
		if (mInput3 != null)
		{
			print(mInput3.getTime());
		}
	}
}

