/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PersonCloneable.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rifetestmodels;

public class PersonCloneable extends Person implements Cloneable
{
	public Object clone()
	throws CloneNotSupportedException
	{
		PersonCloneable new_person = (PersonCloneable)super.clone();

		if (null == getFirstname())
		{
			new_person.setFirstname("autofirst");
			return new_person;
		}
		
		if (null == getLastname())
		{
			new_person.setLastname("autolast");
			return new_person;
		}

		return new_person;
	}
}
