/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutolinkAnnotationDestination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.annotations.InBeanProperty;
import com.uwyn.rife.engine.annotations.InputProperty;
import com.uwyn.rife.tools.BeanUtils;

@Elem(
	id = "AUTOLINK_ANN_DST"
)
public class AutolinkAnnotationDestination extends Element
{
	private String mValue1;
	private String mValue2;
	private String mValue4;
	private BeanImpl1 mBean1;
	private BeanImpl2 mBean3;
	
	@InputProperty
	public void setValue1(String value1)
	{
		mValue1 = value1;
	}
	
	@InputProperty
	public void setValue2(String value2)
	{
		mValue2 = value2;
	}
	
	@InputProperty
	public void setValue4(String value4)
	{
		mValue4 = value4;
	}
	
	@InBeanProperty
	public void setBean1(BeanImpl1 bean1)
	{
		mBean1 = bean1;
	}
	
	@InBeanProperty(prefix = "in_")
	public void setBean3(BeanImpl2 bean3)
	{
		mBean3 = bean3;
	}
	
	public void processElement()
	{
		print(mValue1+","+mValue2+","+mValue4+"\n");
		print(mBean1.getString1()+","+mBean1.getString2()+","+mBean1.getString3()+","+mBean1.getEnum4()+","+BeanUtils.getConcisePreciseDateFormat().format(mBean1.getDate5())+"\n");
		print(mBean3.getEnum4()+","+mBean3.getDate5()+","+mBean3.getString6()+","+mBean3.getString7()+","+mBean3.getString8()+"\n");
	}
}
