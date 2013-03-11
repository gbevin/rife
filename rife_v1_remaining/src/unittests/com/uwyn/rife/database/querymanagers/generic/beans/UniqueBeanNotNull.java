/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UniqueBeanNotNull.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.beans;

import com.uwyn.rife.database.querymanagers.generic.beans.UniqueBean;
import com.uwyn.rife.site.ConstrainedProperty;

public class UniqueBeanNotNull extends UniqueBean
{
	protected void activateValidation()
	{
		super.activateValidation();
		
		addConstraint(new ConstrainedProperty("thirdString").maxLength(50).notNull(true).defaultValue(""));
	}
}

