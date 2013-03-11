/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Errors.java 3930 2008-04-24 11:10:22Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;

@Elem
public class Errors extends Element
{
	public void processElement()
	{
		print(getErrorElement().getElementInfo().getId());
		print("\n");
		print(getErrorException().getMessage());
		print("\n");
		print(getErrorException().getClass().getName());
	}
}