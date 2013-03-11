/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuableObject.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations;

/**
 * Interface that needs to be implemented by classes that should support
 * continuations functionalities and become resumable.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public interface ContinuableObject extends Cloneable
{
	/**
	 * When continuations are resumed, they are by default cloned to ensure
	 * that their state is properly isolated. Implementing this method
	 * allows for full customization of the cloning behavior.
	 *
	 * @see ContinuationConfigRuntime#cloneContinuations
	 * @return the cloned instance of this continuable object
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException;
}
