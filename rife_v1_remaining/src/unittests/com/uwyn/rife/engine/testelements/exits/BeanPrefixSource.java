/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanPrefixSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Element;
import java.util.Calendar;

public class BeanPrefixSource extends Element
{
	public void processElement()
	{
		Calendar cal = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone());
		cal.clear();

		BeanImpl1 bean = new BeanImpl1();
		bean.setString1("stringvalue1");
		bean.setString2("stringvalue2");
		bean.setString3("stringvalue3");
		bean.setEnum4(BeanImpl1.Day.WEDNESDAY);
		cal.set(2007, 2, 13, 8, 13, 24);
		bean.setDate5(cal.getTime());

		setNamedOutputBean("outbean1", bean);
		
		exit("beanexit");
	}
}

