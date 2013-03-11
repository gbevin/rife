/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UniqueIDGenerator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

public abstract class UniqueIDGenerator
{
	private static final String ALGORITHM = "MD5";

	private static Random	sRandom = new Random(System.currentTimeMillis());
	private static String	sLocalHostSeed = null;

	public static UniqueID generate()
	{
		if (null == sLocalHostSeed)
		{
			String seed = null;
			try
			{
				seed = InetAddress.getLocalHost().toString();
			}
			catch (UnknownHostException e)
			{
				seed = "localhost/127.0.0.1";
			}
			
			sLocalHostSeed = seed;
		}
		
		return generate(sLocalHostSeed);
	}

	public static UniqueID generate(String seed)
	{
		seed = seed + (new Date()).toString();
		seed = seed + Long.toString(sRandom.nextLong());
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance(ALGORITHM);
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
		md.update(seed.getBytes());

		return new UniqueID(md.digest());
	}
}

