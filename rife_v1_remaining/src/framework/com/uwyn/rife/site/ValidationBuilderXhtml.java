/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationBuilderXhtml.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

public class ValidationBuilderXhtml extends AbstractValidationBuilder
{
	protected String formatLine(String content)
	{
		return content+"<br />\n";
	}
}
