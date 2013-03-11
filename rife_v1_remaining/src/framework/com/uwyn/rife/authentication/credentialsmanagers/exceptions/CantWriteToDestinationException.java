/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CantWriteToDestinationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import java.io.File;

public class CantWriteToDestinationException extends CredentialsManagerException
{
	private static final long serialVersionUID = -3301152934526542924L;
	
	private File	mDestination = null;
	
	public CantWriteToDestinationException(File destination)
	{
		super("The destination file for the xml data '"+destination.getAbsolutePath()+"' is not writable.");
		
		mDestination = destination;
	}
	
	public File getDestination()
	{
		return mDestination;
	}
}
