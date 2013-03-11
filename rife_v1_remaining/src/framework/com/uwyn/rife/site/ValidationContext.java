/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationContext.java 3964 2008-07-30 07:56:38Z gbevin $
 */
package com.uwyn.rife.site;

/**
 * This interface has to be implemented by all classes that provide a context
 * in which <code>Validated</code> bean instances can be validated.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3964 $
 * @see Validation
 * @see ValidationError
 * @since 1.0
 */
public interface ValidationContext
{
	/**
	 * Validates a <code>Validated</code> in this context.
	 * <p>This method is not supposed to reset the validation errors or to
	 * start the validation from scratch, but it's intended to add additional
	 * errors to an existing collection.
	 *
	 * @param validated the <code>Validated</code> instance that will be validated
	 * @since 1.0
	 */
	public void validate(Validated validated);
}
