/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StatefulExplicitProcessing.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.template.Template;

@Elem
public class StatefulExplicitProcessing extends Element
{
	public void processElement()
	{
		Template t = getPropertyTyped("template", Template.class);
		int counter = 0;
		for (String text : new String[] {"one", "two", "three", "four", "five", "six", "seven"})
		{
			processEmbeddedElement(t, getPropertyString("embedname"), String.valueOf(counter++), text);
			t.appendBlock("buttons", "button");
		}
		print(t);
	}
}
