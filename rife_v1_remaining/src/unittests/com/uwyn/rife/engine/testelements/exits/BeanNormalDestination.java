/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanNormalDestination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.BeanUtils;

public class BeanNormalDestination extends Element
{
	public void processElement()
	{
		BeanImpl1	bean1 = getInputBean(BeanImpl1.class);
		print(bean1.getString1()+","+bean1.getString2()+","+bean1.getString3()+","+bean1.getEnum4()+","+BeanUtils.getConcisePreciseDateFormat().format(bean1.getDate5()));
		BeanImpl2	bean2 = getInputBean(BeanImpl2.class);
		print(bean2.getEnum4()+","+BeanUtils.getConcisePreciseDateFormat().format(bean2.getDate5())+","+bean2.getString6()+","+bean2.getString7()+","+bean2.getString8());
	}
}

