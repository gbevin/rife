/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PathInfoSpecificationInvalidException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class PathInfoSpecificationInvalidException extends EngineException
{
	static final long serialVersionUID = 3426967109636662881L;

	private String	mSpecification = null;

	public PathInfoSpecificationInvalidException(String specification)
	{
		super("The pathinfo specification '"+specification+"' is not valid, a valid example is $year(\\d{4})$month(\\d{2})/$album.");

		mSpecification = specification;
	}

	public String getSpecification()
	{
		return mSpecification;
	}
}
