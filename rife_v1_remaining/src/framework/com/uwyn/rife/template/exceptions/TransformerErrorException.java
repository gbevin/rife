/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TransformerErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import java.net.URL;

public class TransformerErrorException extends ProcessingException
{
	private static final long serialVersionUID = -7256768610167542980L;
	
	private URL	mResource = null;

	public TransformerErrorException(URL resource, Throwable cause)
	{
		super("Error while transforming resource '"+resource+"'.", cause);
		mResource = resource;
	}

	public URL getResource()
	{
		return mResource;
	}
}
