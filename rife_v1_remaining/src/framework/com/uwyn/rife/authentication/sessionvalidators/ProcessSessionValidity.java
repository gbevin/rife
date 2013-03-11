/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ProcessSessionValidity.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.database.DbRowProcessor;

public abstract class ProcessSessionValidity extends DbRowProcessor
{
	public abstract int getValidity();
}

