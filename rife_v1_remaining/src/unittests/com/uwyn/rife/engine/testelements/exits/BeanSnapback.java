/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanSnapback.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.BeanUtils;
import java.util.Calendar;

public class BeanSnapback extends Element
{
	public void processElement()
	{
		Calendar cal = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone());
		cal.clear();

		if (hasInputValue("selector") &&
			getInput("selector").equals("exit"))
		{
			BeanImpl1 bean = new BeanImpl1();
			bean.setString1("stringvalue1");
			bean.setString2("stringvalue2");
			bean.setString3("stringvalue3");
			bean.setEnum4(BeanImpl1.Day.SUNDAY);
			cal.set(2007, 2, 13, 8, 27, 12);
			bean.setDate5(cal.getTime());
	
			setOutputBean(bean);
			exit("beanexit");
		}
		BeanImpl1	bean1 = getInputBean(BeanImpl1.class);
		print(bean1.getString1()+","+bean1.getString2()+","+bean1.getString3()+","+bean1.getEnum4()+","+BeanUtils.getConcisePreciseDateFormat().format(bean1.getDate5()));
	}
}

