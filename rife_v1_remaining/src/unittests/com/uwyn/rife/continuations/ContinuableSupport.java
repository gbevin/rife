/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuableSupport.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations;

public class ContinuableSupport
{
	public final void pause()
	{
		// this should not be triggered, since bytecode rewriting will replace this
		// method call with the appropriate logic
		throw new UnsupportedOperationException();
	}
	
	public final void stepback()
	{
		// this should not be triggered, since bytecode rewriting will replace this
		// method call with the appropriate logic
		throw new UnsupportedOperationException();
	}
	
	public final Object call(Class target)
	{
		// this should not be triggered, since bytecode rewriting will replace this
		// method call with the appropriate logic
		throw new UnsupportedOperationException();
	}
	
	public final void answer()
	{
		// this should not be triggered, since bytecode rewriting will replace this
		// method call with the appropriate logic
		throw new UnsupportedOperationException();
	}
	
	public final void answer(Object answer)
	{
		// this should not be triggered, since bytecode rewriting will replace this
		// method call with the appropriate logic
		throw new UnsupportedOperationException();
	}
}
