/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutolinkSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Element;
import java.util.Calendar;

public class AutolinkSource extends Element
{
	public void processElement()
	{
		Calendar cal = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone());
		cal.clear();

		setOutput("value1", "output1 value");
		setOutput("value2", "output2 value");
		setOutput("value3", "output3 value");
		
		cal.set(2007, 3, 21, 8, 12, 28);
		BeanImpl2 bean2 = new BeanImpl2();
		bean2.setEnum4(BeanImpl1.Day.FRIDAY);
		bean2.setDate5(cal.getTime());
		bean2.setString6("stringvalue6");
		bean2.setString7("stringvalue7");
		bean2.setString8("stringvalue8");
		setOutputBean(bean2);
		
		cal.set(2007, 2, 13, 8, 12, 28);
		BeanImpl1 bean1 = new BeanImpl1();
		bean1.setString1("stringvalue1");
		bean1.setString2("stringvalue2");
		bean1.setString3("stringvalue3");
		bean1.setEnum4(BeanImpl1.Day.MONDAY);
		bean1.setDate5(cal.getTime());
		setOutputBean(bean1);
		
		if (getInput("type", "").equals("directlink"))
		{
			print("<html><body>");
			print("<a href=\""+getExitQueryUrl("AUTOLINK_DESTINATION")+"\">link</a>");
			print("</body></html>");
		}
		else
		{
			exit("AUTOLINK_DESTINATION");
		}
	}
}

