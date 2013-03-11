/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UploadedSubmissionFilesInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class UploadedSubmissionFilesInjectionException extends EngineException
{
	private static final long serialVersionUID = 7932152562834625462L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mFileName = null;

	public UploadedSubmissionFilesInjectionException(String declarationName, Class elementClass, String fileName, Throwable e)
	{
		super("An error occurred while injecting the uploaded submission file '"+fileName+"' of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);

		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mFileName = fileName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public Class getElementClass()
	{
		return mElementClass;
	}
	
	public String getFileName()
	{
		return mFileName;
	}
}
