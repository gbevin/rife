/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExpressionNotBooleanException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class ExpressionNotBooleanException extends ProcessingException
{
	private static final long serialVersionUID = -37462186995432114L;
	
	private String	mLanguage = null;
	private String	mTemplateName = null;
	private String	mExpression = null;
	private Class	mType = null;

	public ExpressionNotBooleanException(String language, String templateName, String expression, Class type)
	{
		super("The "+language+" expression [[ "+expression+" ]] in template '"+templateName+"' returns a value of type '"+type+"', while it should return a boolean.");
		
		mLanguage = language;
		mTemplateName = templateName;
		mExpression = expression;
		mType = type;
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
	
	public Class getType()
	{
		return mType;
	}
}
