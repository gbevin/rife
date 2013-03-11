/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SomeEnum.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.SomeEnum;

public enum SomeEnum
{
	VALUE_ONE(1), VALUE_TWO(2), VALUE_THREE(3);
	
	private int mCount;
	
	SomeEnum(int count)
	{
		mCount = count;
	}
	
	public int getCount()
	{
		return mCount;
	};
}
