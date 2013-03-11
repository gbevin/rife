/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IncookiesInjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.cookies;

import com.uwyn.rife.engine.Element;

public class IncookiesInjection extends Element
{
	private String	mFirstname;
	private String	mMiddlename;
	private String	mLastname;
	private String	mGlobalcookie1;
	private String	mGlobalcookie2;
	private String	mGlobalcookie3;

	public void setFirstname(String firstname) { mFirstname = firstname; }
	public void setMiddlename(String middlename) { mMiddlename = middlename; }
	public void setLastname(String lastname) { mLastname = lastname; }
	public void setGlobalcookie1(String globalcookie1) { mGlobalcookie1 = globalcookie1; }
	public void setGlobalcookie2(String globalcookie2) { mGlobalcookie2 = globalcookie2; }
	public void setGlobalcookie3(String globalcookie3) { mGlobalcookie3 = globalcookie3; }

	public void processElement()
	{
		print("Welcome ");
		print(mFirstname);
		print(" ");
		print(mMiddlename);
		print(" ");
		print(mLastname);
		print("\n");
		print(mGlobalcookie1);
		print(" ");
		print(mGlobalcookie2);
		print(" ");
		print(mGlobalcookie3);
	}
}
