/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Data.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import java.util.HashMap;

public class Data extends Element
{
	public void processElement()
	{
		Template t = getHtmlTemplate("engine_embedding_data");
		processEmbeddedElement(t, ".DATA_EMBEDDED", new HashMap<String, String>(){{ put("1st", "value1"); }});
		processEmbeddedElement(t, ".DATA_EMBEDDED", "second", new HashMap<String, String>(){{ put("2nd", "value2"); }});
		processEmbeddedElement(t, ".DATA_EMBEDDED", "third", new HashMap<String, String>(){{ put("3rd", "value3"); }});
		print(t);
	}
}

