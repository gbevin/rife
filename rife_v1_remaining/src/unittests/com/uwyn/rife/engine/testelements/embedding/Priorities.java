/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Priorities.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class Priorities extends Element
{
	public void processElement()
	{
		setRequestAttribute("aggregation", "");
		Template template = getHtmlTemplate("engine_embedding_priorities");
		setRequestAttribute("aggregation", getRequestAttribute("aggregation")+getElementInfo().getId()+"\n");
		print(template);
		print(getRequestAttribute("aggregation"));
	}
}

