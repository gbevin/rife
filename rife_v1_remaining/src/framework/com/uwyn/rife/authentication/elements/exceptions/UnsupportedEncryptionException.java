/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedEncryptionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class UnsupportedEncryptionException extends EngineException
{
	private static final long serialVersionUID = 652415152207765304L;

	private String	mEncryption = null;
	
	public UnsupportedEncryptionException(String encryption)
	{
		super("No encryptor could be found for encryption '"+encryption+"'.");
		mEncryption = encryption;
	}
	
	public String getEncryption()
	{
		return mEncryption;
	}
}
