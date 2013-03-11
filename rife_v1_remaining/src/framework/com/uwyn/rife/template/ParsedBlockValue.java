/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParsedBlockValue.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.asm.ClassVisitor;
import com.uwyn.rife.asm.MethodVisitor;
import com.uwyn.rife.template.ParsedBlockPart;

public class ParsedBlockValue extends ParsedBlockPart
{
	private String mValueKey = null;
	private String mValueTag = null;

	ParsedBlockValue(String valueKey, String valueTag)
	{
		assert valueKey != null;
		assert valueKey.length() > 0;
		assert valueTag != null;
		assert valueTag.length() > 0;

		mValueKey = valueKey;
		mValueTag = valueTag;
	}

	String getData()
	{
		return mValueKey;
	}

	String getTag()
	{
		return mValueTag;
	}

	int getType()
	{
		return VALUE;
	}
	
	void visitByteCodeExternalForm(MethodVisitor visitor, String className, String staticIdentifier)
	{
		visitor.visitVarInsn            (ALOAD, 0);
		visitor.visitFieldInsn          (GETSTATIC, className, staticIdentifier, "Ljava/lang/String;");
		visitor.visitFieldInsn          (GETSTATIC, className, staticIdentifier+"Tag", "Ljava/lang/String;");
		visitor.visitVarInsn            (ALOAD, 2);
		visitor.visitMethodInsn         (INVOKEVIRTUAL, className, "appendValueExternalForm", "(Ljava/lang/String;Ljava/lang/String;Lcom/uwyn/rife/template/ExternalValue;)V");
	}
	
	void visitByteCodeInternalForm(MethodVisitor visitor, String className, String staticIdentifier)
	{
		visitor.visitVarInsn            (ALOAD, 0);
		visitor.visitFieldInsn          (GETSTATIC, className, staticIdentifier, "Ljava/lang/String;");
		visitor.visitFieldInsn          (GETSTATIC, className, staticIdentifier+"Tag", "Ljava/lang/String;");
		visitor.visitVarInsn            (ALOAD, 2);
		visitor.visitMethodInsn         (INVOKEVIRTUAL, className, "appendValueInternalForm", "(Ljava/lang/String;Ljava/lang/String;Lcom/uwyn/rife/template/InternalValue;)V");
	}
	
	void visitByteCodeStaticDeclaration(ClassVisitor visitor, String staticIdentifier)
	{
		visitor.visitField(ACC_PRIVATE|ACC_STATIC, staticIdentifier, "Ljava/lang/String;", null, null);
		visitor.visitField(ACC_PRIVATE|ACC_STATIC, staticIdentifier+"Tag", "Ljava/lang/String;", null, null);
	}
	
	void visitByteCodeStaticDefinition(MethodVisitor visitor, String className, String staticIdentifier)
	{
		visitor.visitLdcInsn            (mValueKey);
		visitor.visitFieldInsn          (PUTSTATIC, className, staticIdentifier, "Ljava/lang/String;");
		visitor.visitLdcInsn            (mValueTag);
		visitor.visitFieldInsn          (PUTSTATIC, className, staticIdentifier+"Tag", "Ljava/lang/String;");
	}
}
