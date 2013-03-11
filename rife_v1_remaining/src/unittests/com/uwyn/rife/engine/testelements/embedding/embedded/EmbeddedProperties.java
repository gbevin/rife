/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EmbeddedProperties.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;

import java.util.Properties;

public class EmbeddedProperties extends Element
{
	public void processElement()
	{
		Properties properties = getEmbedProperties();
		print("Properties");
		print(properties.getProperty("key1"));
		print(properties.getProperty("key2"));
		print(properties.getProperty("something"));
		print(properties.getProperty("oh"));
		print(""+properties.getProperty("key3"));
	}
}

