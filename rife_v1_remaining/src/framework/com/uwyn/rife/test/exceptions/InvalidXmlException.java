/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidXmlException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test.exceptions;

import com.uwyn.rife.tools.StringUtils;
import java.util.Set;

public class InvalidXmlException extends Exception
{
	private static final long serialVersionUID = -5367999859421621515L;
	
	private Set<String> mErrors;
	
	public InvalidXmlException(Set<String> errors)
	{
		super("Invalid XML document:\n"+StringUtils.join(errors, "\n"));
		
		mErrors = errors;
	}
	
	public Set<String> getErrors()
	{
		return mErrors;
	}
}
