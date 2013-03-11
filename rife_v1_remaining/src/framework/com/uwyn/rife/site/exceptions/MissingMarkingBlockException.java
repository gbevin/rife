/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingMarkingBlockException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.exceptions;

public class MissingMarkingBlockException extends ValidationBuilderException
{
	private static final long serialVersionUID = 6014580362473175137L;
	
	private String mBlockId = null;
	
	public MissingMarkingBlockException(String blockId)
	{
		super("The template requires the '"+blockId+"' block to be able to generate the validation markings.");
		
		mBlockId = blockId;
	}
	
	public String getBlockId()
	{
		return mBlockId;
	}
}
