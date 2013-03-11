/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineTemplateProcessingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.template.Template;

public class EngineTemplateProcessingException extends EngineException
{
	private static final long serialVersionUID = -7379794641614184497L;

	private final Template mTemplate;

	public EngineTemplateProcessingException(final Template template, final String msg)
	{
		this(template, msg, null);
	}

	public EngineTemplateProcessingException(final Template template, final String msg, final Throwable cause)
	{
		super("Error while processing template '" + template.getFullName() + "' : " + msg, cause);

		mTemplate = template;
	}

	public Template getTemplate()
	{
		return mTemplate;
	}
}