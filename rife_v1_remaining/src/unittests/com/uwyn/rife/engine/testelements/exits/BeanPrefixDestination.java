/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanPrefixDestination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.BeanUtils;

public class BeanPrefixDestination extends Element
{
	public void processElement()
	{
		BeanImpl1	bean1 = getNamedInputBean("inbean1");
		print(bean1.getString1()+","+bean1.getString2()+","+bean1.getString3()+","+bean1.getEnum4()+","+(null == bean1.getDate5() ? null : BeanUtils.getConcisePreciseDateFormat().format(bean1.getDate5())));
		BeanImpl1	bean2 = getNamedInputBean("inbean2");
		print(bean2.getString1()+","+bean2.getString2()+","+bean2.getString3()+","+bean2.getEnum4()+","+bean2.getDate5());
	}
}

