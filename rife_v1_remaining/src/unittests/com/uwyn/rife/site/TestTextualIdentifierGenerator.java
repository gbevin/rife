/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestTextualIdentifierGenerator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.site.TextualIdentifierGenerator;
import junit.framework.TestCase;

public class TestTextualIdentifierGenerator extends TestCase
{
	public TestTextualIdentifierGenerator(String name)
	{
		super(name);
	}

	public void testGenerate()
	{
		TextualIdentifierGenerator<InitializedBeanImpl> identifier = new AbstractTextualIdentifierGenerator<InitializedBeanImpl>()
		{
				public String generateIdentifier()
				{
					return mBean.getString()+":"+mBean.getChar();
				}
			};
		
		InitializedBeanImpl bean = new InitializedBeanImpl();
		identifier.setBean(bean);
		assertEquals("default:i", identifier.generateIdentifier());
		bean.setString("override");
		bean.setChar('z');
		assertEquals("override:z", identifier.generateIdentifier());
			
		identifier.setBean(new InitializedBeanImpl());
		assertEquals("default:i", identifier.generateIdentifier());
	}
}
