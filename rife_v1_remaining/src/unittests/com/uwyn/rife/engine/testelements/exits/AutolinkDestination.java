/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutolinkDestination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.BeanUtils;

public class AutolinkDestination extends Element
{
	public void processElement()
	{
		print(getInput("value1")+","+getInput("value2")+","+getInput("value4")+"\n");
		BeanImpl1	bean1 = getNamedInputBean("bean1");
		print(bean1.getString1()+","+bean1.getString2()+","+bean1.getString3()+","+bean1.getEnum4()+","+BeanUtils.getConcisePreciseDateFormat().format(bean1.getDate5())+"\n");
		BeanImpl2	bean3 = getNamedInputBean("bean3");
		print(bean3.getEnum4()+","+bean3.getDate5()+","+bean3.getString6()+","+bean3.getString7()+","+bean3.getString8()+"\n");
	}
}

