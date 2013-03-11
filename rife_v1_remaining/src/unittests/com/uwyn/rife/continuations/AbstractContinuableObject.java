/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractContinuableObject.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations;

public abstract class AbstractContinuableObject extends ContinuableSupport implements ContinuableObject
{
	public Object clone()
	throws CloneNotSupportedException
	{
		return super.clone();
	}
}
