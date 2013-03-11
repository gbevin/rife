/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubmissionRequiredException extends EngineException
{
	private static final long serialVersionUID = 4874700849954720527L;

	public SubmissionRequiredException()
	{
		super("A submission is required to obtain a parameter.");
	}
}
