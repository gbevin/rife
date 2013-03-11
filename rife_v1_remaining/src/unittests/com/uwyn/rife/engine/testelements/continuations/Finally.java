/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Finally.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class Finally extends Element
{
	public void processElement()
	{
		String test = "start";
		String nl = "\n";
		print(test+"\n");
		print(getContinuationId());
		pause();
		try
		{
			print("try"+nl);
			print(getContinuationId());
			pause();
			throw new RuntimeException();
		}
		catch (RuntimeException e)
		{
			print("catch"+nl);
			print(getContinuationId());
			pause();
		}
		finally
		{
			String empty = "";
			print("fi"+empty+"nal"+empty+"ly"+nl);
			print(getContinuationId());
			pause();
		}
		
		test = "after finally";
		print(test);
	}
}
