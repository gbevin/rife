/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StoreXmlErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import java.io.File;

public class StoreXmlErrorException extends DatasourcesException
{
	private static final long serialVersionUID = 6332480072469992960L;

	private File	mDestination = null;
	
	public StoreXmlErrorException(File destination, Throwable cause)
	{
		super("An error occurred while storing the xml data to the destination file '"+destination.getAbsolutePath()+"'.", cause);
		
		mDestination = destination;
	}
	
	public File getDestination()
	{
		return mDestination;
	}
}
