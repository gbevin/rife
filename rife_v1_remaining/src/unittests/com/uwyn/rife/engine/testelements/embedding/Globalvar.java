/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Globalvar.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;

public class Globalvar extends Element
{
	public void processElement()
	{
		setOutput("var1", "value 1");
		setOutput("var2", "value 2");
		setOutput("var3", "value 3");
		setOutput("var4", "value 4");
		
		print(getHtmlTemplate("engine_embedding_globalvar"));
	}
}

