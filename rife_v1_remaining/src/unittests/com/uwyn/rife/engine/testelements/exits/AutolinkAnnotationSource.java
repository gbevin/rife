/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutolinkAnnotationSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Autolink;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.InputProperty;
import com.uwyn.rife.engine.annotations.OutBeanProperty;
import com.uwyn.rife.engine.annotations.OutputProperty;
import java.util.Calendar;

@Elem(
	id = "AUTOLINK_ANN_SRC",
	autolinks = {
		@Autolink(destClass = AutolinkAnnotationDestination.class)
	}
)
public class AutolinkAnnotationSource extends Element
{
	private String mType;
	private String mValue1;
	private String mValue2;
	private String mValue3;
	private BeanImpl1 mBean1;
	private BeanImpl2 mBean2;
	
	@InputProperty
	public void setType(String type)
	{
		mType = type;
	}
	
	@OutputProperty
	public String getValue1()
	{
		return mValue1;
	}
	
	@OutputProperty
	public String getValue2()
	{
		return mValue2;
	}
	
	@OutputProperty
	public String getValue3()
	{
		return mValue3;
	}

	@OutBeanProperty	
	public BeanImpl1 getBean1()
	{
		return mBean1;
	}
	
	@OutBeanProperty(prefix = "out_")
	public BeanImpl2 getBean2()
	{
		return mBean2;
	}
	
	public void processElement()
	{
		Calendar cal = Calendar.getInstance(RifeConfig.Tools.getDefaultTimeZone());
		cal.clear();

		mValue1 = "output1 value";
		mValue2 = "output2 value";
		mValue3 = "output3 value";
		
		cal.set(2007, 3, 21, 8, 23, 32);
		mBean2 = new BeanImpl2();
		mBean2.setEnum4(BeanImpl1.Day.SATURDAY);
		mBean2.setDate5(cal.getTime());
		mBean2.setString6("stringvalue6");
		mBean2.setString7("stringvalue7");
		mBean2.setString8("stringvalue8");
		
		cal.set(2007, 2, 13, 8, 23, 32);
		mBean1 = new BeanImpl1();
		mBean1.setString1("stringvalue1");
		mBean1.setString2("stringvalue2");
		mBean1.setString3("stringvalue3");
		mBean1.setEnum4(BeanImpl1.Day.TUESDAY);
		mBean1.setDate5(cal.getTime());
		
		if ("directlink".equals(mType))
		{
			print("<html><body>");
			print("<a href=\""+getExitQueryUrl("AUTOLINK_ANN_DST")+"\">link</a>");
			print("</body></html>");
		}
		else
		{
			exit("AUTOLINK_ANN_DST");
		}
	}
}

