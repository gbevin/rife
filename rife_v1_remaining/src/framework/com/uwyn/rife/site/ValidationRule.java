/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationRule.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

public interface ValidationRule extends Cloneable
{
	public boolean validate();
	public String getSubject();
	public ValidationError getError();
	public Object getBean();
	public <T extends ValidationRule> T setBean(Object bean);
	public Object clone() throws CloneNotSupportedException;
}
