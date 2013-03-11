/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParsedBlockPart.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.asm.ClassVisitor;
import com.uwyn.rife.asm.MethodVisitor;
import com.uwyn.rife.asm.Opcodes;

abstract class ParsedBlockPart implements Opcodes
{
	public static final int TEXT = 0;
	public static final int VALUE = 1;

	abstract String getData();
	abstract int getType();
	abstract void visitByteCodeExternalForm(MethodVisitor visitor, String className, String staticIdentifier);
	abstract void visitByteCodeInternalForm(MethodVisitor visitor, String className, String staticIdentifier);
	abstract void visitByteCodeStaticDeclaration(ClassVisitor visitor, String staticIdentifier);
	abstract void visitByteCodeStaticDefinition(MethodVisitor visitor, String className, String staticIdentifier);
}
