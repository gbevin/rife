/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExpressionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class ExpressionException extends ProcessingException
{
	private static final long serialVersionUID = 7212151954221867460L;
	
	private String mLanguage = null;
	private String mTemplateName = null;
	private String mExpression = null;

	public ExpressionException(String language, String templateName, String expression, Throwable cause)
	{
		super("The "+language+" expression [[ "+expression+" ]] in template '"+templateName+"' threw an exception.", cause);
		
		mLanguage = language;
		mTemplateName = templateName;
		mExpression = expression;
	}

	public String getLanguage()
	{
		return mLanguage;
	}

	public String getTemplateName()
	{
		return mTemplateName;
	}

	public String getExpression()
	{
		return mExpression;
	}
}
