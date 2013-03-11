/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanNormalSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Element;
import java.util.Calendar;

public class BeanNormalSource extends Element
{
	public void processElement()
	{
		Calendar cal = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone());
		cal.clear();

		if (getInput("selector").equals("first"))
		{
			cal.set(2007, 2, 13, 8, 32, 17);
			BeanImpl1 bean = new BeanImpl1();
			bean.setString1("stringvalue1");
			bean.setString2("stringvalue2");
			bean.setString3("stringvalue3");
			bean.setEnum4(BeanImpl1.Day.MONDAY);
			bean.setDate5(cal.getTime());
	
			setOutputBean(bean);
		}
		else if (getInput("selector").equals("second"))
		{
			BeanImpl2 bean = new BeanImpl2();
			bean.setEnum4(BeanImpl1.Day.TUESDAY);
			cal.set(2007, 3, 21, 8, 32, 17);
			bean.setDate5(cal.getTime());
			bean.setString6("stringvalue6");
			bean.setString7("stringvalue7");
			bean.setString8("stringvalue8");
	
			setOutputBean(bean);
		}
		exit("beanexit");
	}
}

