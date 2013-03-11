/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataCloneableAdapter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.instrument;

import com.uwyn.rife.asm.MethodAdapter;
import com.uwyn.rife.asm.MethodVisitor;
import com.uwyn.rife.asm.Opcodes;

class MetaDataCloneableAdapter extends MethodAdapter implements Opcodes
{
	private String mBaseInternalName = null;
	private String mMetaDataInternalName = null;
	
	MetaDataCloneableAdapter(String baseInternalName, String metaDataInternalName, MethodVisitor visitor)
	{
		super(visitor);
		
		mBaseInternalName = baseInternalName;
		mMetaDataInternalName = metaDataInternalName;
	}

	public void visitInsn(int opcode)
	{
		if (ARETURN == opcode)
		{
			mv.visitTypeInsn(CHECKCAST, mBaseInternalName);
			mv.visitVarInsn(ASTORE, 1);
			
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mBaseInternalName, MetaDataClassAdapter.DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");
			mv.visitMethodInsn(INVOKEVIRTUAL, mMetaDataInternalName, "clone", "()Ljava/lang/Object;");
			mv.visitTypeInsn(CHECKCAST, mMetaDataInternalName);
			mv.visitFieldInsn(PUTFIELD, mBaseInternalName, MetaDataClassAdapter.DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");

			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(GETFIELD, mBaseInternalName, MetaDataClassAdapter.DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");

			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, mMetaDataInternalName, "setMetaDataBean", "(Ljava/lang/Object;)V");
			
			mv.visitVarInsn(ALOAD, 1);
		}

		super.visitInsn(opcode);
	}
}
