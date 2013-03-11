/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedMimeType.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.cmf.format.Formatter;
import com.uwyn.rife.cmf.validation.CmfPropertyValidationRule;
import com.uwyn.rife.site.ConstrainedProperty;

public abstract class UnsupportedMimeType extends MimeType
{
	public static final MimeType    UNSUPPORTED = new MimeType("unsupported/unknown") {
			public CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty)
			{
				return null;
			}
			
			public Formatter getFormatter()
			{
				return null;
			}
		};

	UnsupportedMimeType(String identifier)
	{
		super(identifier);
	}
}
