/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.datastructures.DocumentPosition;
import com.uwyn.rife.tools.StringUtils;

public class TemplateException extends RuntimeException
{
	private static final long serialVersionUID = 8643896354837543058L;

	public TemplateException(String message)
	{
		super(message);
	}

	public TemplateException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public static String formatError(String name, DocumentPosition errorLocation, String message)
	{
		String without_tabs = StringUtils.stripFromFront(errorLocation.getLineContent(), "\t");
		int removed_tab_count = errorLocation.getLineContent().length() - without_tabs.length();
		
		return "\n"+name+":"+errorLocation.getLine()+":"+errorLocation.getColumn()+": "+message+"\n"+
			StringUtils.repeat("    ", removed_tab_count)+without_tabs+"\n"+
			StringUtils.repeat("   ", removed_tab_count)+StringUtils.repeat(" ", errorLocation.getColumn()-1)+"^\n";
	}
}
