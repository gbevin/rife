/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: List.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class List extends Element
{
	public void processElement()
	{
		Template template = getHtmlTemplate("engine_embedding_list");
		for (int i = 0; i < 10; i++)
		{
			template.setValue("counter", i);
			processEmbeddedElement(template, ".LISTENTRY_EMBEDDED", String.valueOf(i));
			template.appendBlock("divs", "div");
		}
		print(template);
	}
}

