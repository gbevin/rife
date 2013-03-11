/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataDefaultConstructorAdapter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.instrument;

import com.uwyn.rife.asm.*;
import com.uwyn.rife.site.MetaDataBeanAware;

class MetaDataDefaultConstructorAdapter extends MethodAdapter implements Opcodes
{
	private String mBaseInternalName = null;
	private String mMetaDataInternalName = null;
	
	MetaDataDefaultConstructorAdapter(String baseInternalName, String metaDataInternalName, MethodVisitor visitor)
	{
		super(visitor);
		
		mBaseInternalName = baseInternalName;
		mMetaDataInternalName = metaDataInternalName;
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc)
	{
		super.visitMethodInsn(opcode, owner, name, desc);

		if (INVOKESPECIAL == opcode &&
			"<init>".equals(name) &&
			"()V".equals(desc))
		{
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, mMetaDataInternalName);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, mMetaDataInternalName, "<init>", "()V");
			mv.visitFieldInsn(PUTFIELD, mBaseInternalName, MetaDataClassAdapter.DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");
			
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mBaseInternalName, MetaDataClassAdapter.DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");
			mv.visitTypeInsn(INSTANCEOF, Type.getInternalName(MetaDataBeanAware.class));
			Label not_aware = new Label();
			mv.visitJumpInsn(IFEQ, not_aware);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mBaseInternalName, MetaDataClassAdapter.DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");
			mv.visitTypeInsn(CHECKCAST, Type.getInternalName(MetaDataBeanAware.class));
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(MetaDataBeanAware.class), "setMetaDataBean", "(Ljava/lang/Object;)V");
			mv.visitLabel(not_aware);
		}
	}
}
