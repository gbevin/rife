/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PackagePrivateAccessDisfunctionalException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class PackagePrivateAccessDisfunctionalException extends EngineException
{
	private static final long serialVersionUID = -52620023902304981L;

	private String	mElementClass = null;
	private String	mTargetClass = null;
	private String	mTargetMethod = null;
	
	public PackagePrivateAccessDisfunctionalException(String elementClass, String targetClass, String targetMethod)
	{
		super("The element with java class '"+elementClass+"' tries to access the package private method '"+targetMethod+"' of the class '"+targetClass+"'. Due to limitations in the java virtual machine, this will fail even though it's valid java code.");

		mElementClass = elementClass;
		mTargetClass = targetClass;
		mTargetMethod = targetMethod;
	}
	
	public String getElementClass()
	{
		return mElementClass;
	}
	
	public String getTargetClass()
	{
		return mTargetClass;
	}
	
	public String getTargetMethod()
	{
		return mTargetMethod;
	}
}
