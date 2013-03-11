/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Erroneous.java 3930 2008-04-24 11:10:22Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;

@Elem(
	url = ""
)
public class Erroneous extends Element
{
	public void processElement()
	{
		throw new RuntimeException("This is an error.");
	}
}