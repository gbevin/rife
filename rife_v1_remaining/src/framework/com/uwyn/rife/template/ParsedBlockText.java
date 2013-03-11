/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParsedBlockText.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.asm.ClassVisitor;
import com.uwyn.rife.asm.MethodVisitor;

class ParsedBlockText extends ParsedBlockPart
{
	private String mText = null;
	
	ParsedBlockText(String text)
	{
		assert text != null;
		assert text.length() > 0;

		mText = text;
	}

	String getData()
	{
		return mText;
	}

	int getType()
	{
		return TEXT;
	}
	
	void visitByteCodeExternalForm(MethodVisitor visitor, String className, String staticIdentifier)
	{
		visitor.visitVarInsn            (ALOAD, 2);
		visitor.visitFieldInsn          (GETSTATIC, className, staticIdentifier, "Lcom/uwyn/rife/template/InternalString;");
		visitor.visitMethodInsn         (INVOKEVIRTUAL, "com/uwyn/rife/template/ExternalValue", "append", "(Ljava/lang/CharSequence;)V");
	}
	
	void visitByteCodeInternalForm(MethodVisitor visitor, String className, String staticIdentifier)
	{
		visitor.visitVarInsn            (ALOAD, 0);
		visitor.visitVarInsn            (ALOAD, 2);
		visitor.visitFieldInsn          (GETSTATIC, className, staticIdentifier, "Lcom/uwyn/rife/template/InternalString;");
		visitor.visitMethodInsn         (INVOKEVIRTUAL, className, "appendTextInternal", "(Lcom/uwyn/rife/template/InternalValue;Ljava/lang/CharSequence;)V");
	}
	
	void visitByteCodeStaticDeclaration(ClassVisitor visitor, String staticIdentifier)
	{
		visitor.visitField(ACC_PRIVATE|ACC_STATIC, staticIdentifier, "Lcom/uwyn/rife/template/InternalString;", null, null);
	}
	
	void visitByteCodeStaticDefinition(MethodVisitor visitor, String className, String staticIdentifier)
	{
		visitor.visitTypeInsn           (NEW, "com/uwyn/rife/template/InternalString");
		visitor.visitInsn               (DUP);
		visitor.visitLdcInsn            (mText);
		visitor.visitMethodInsn         (INVOKESPECIAL, "com/uwyn/rife/template/InternalString", "<init>", "(Ljava/lang/String;)V");
		visitor.visitFieldInsn          (PUTSTATIC, className, staticIdentifier, "Lcom/uwyn/rife/template/InternalString;");
	}
}
