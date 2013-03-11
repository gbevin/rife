/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartInvalidUploadDirectoryException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import java.io.File;

public class MultipartInvalidUploadDirectoryException extends MultipartRequestException
{
	private static final long serialVersionUID = 2872851131466908315L;

	private File	mDirectory = null;

	public MultipartInvalidUploadDirectoryException(File directory)
	{
		super("Invalid upload directory '"+directory.getAbsolutePath()+"'.");
		
		mDirectory = directory;
	}
	
	public File getDirectory()
	{
		return mDirectory;
	}
}
