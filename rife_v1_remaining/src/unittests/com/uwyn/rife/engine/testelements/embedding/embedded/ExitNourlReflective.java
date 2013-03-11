/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitNourlReflective.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class ExitNourlReflective extends Element
{
	public void processElement()
	{
		Template template = getHtmlTemplate("engine_embedding_embedded_exitnourlreflective");
		setExitQuery(template, "exit", new String[] {"embeddedoutput", "outputvalue"});
		print(template);
	}
}

