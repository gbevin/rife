/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NoOpAnnotationVisitor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.AnnotationVisitor;

class NoOpAnnotationVisitor implements AnnotationVisitor
{
	public void visit(String name, Object value)
	{
	}
	
	public void visitEnum(String name, String desc, String value)
	{
	}
	
	public AnnotationVisitor visitAnnotation(String name, String desc)
	{
		return this;
	}
	
	public AnnotationVisitor visitArray(String name)
	{
		return this;
	}
	
	public void visitEnd()
	{
	}
}
