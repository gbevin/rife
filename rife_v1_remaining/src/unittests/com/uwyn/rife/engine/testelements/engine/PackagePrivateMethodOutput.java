/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PackagePrivateMethodOutput.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

public class PackagePrivateMethodOutput
{
	public PackagePrivateMethodOutput()
	{
	}
	
	String getOutput(String objectArg, int[] arrayArg, boolean booleanArg, byte byteArg, char charArg, float floatArg, int intArg, short shortArg, double doubleArg, long longArg)
	{
		return "PackagePrivateMethodOutput's output";
	}
}

