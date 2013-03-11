/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PathInfoReflexive.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class PathInfoReflexive extends Element
{
	public void processElement()
	{ 
		Template template = getHtmlTemplate();
		clearOutput("id");
		print(template);
	}
}
