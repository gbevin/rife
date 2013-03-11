/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LazyLoadMethodAdapter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.instrument;

import static com.uwyn.rife.database.querymanagers.generic.instrument.LazyLoadAccessorsBytecodeTransformer.GQM_VAR_NAME;
import static com.uwyn.rife.database.querymanagers.generic.instrument.LazyLoadAccessorsBytecodeTransformer.LAZYLOADED_VAR_NAME;

import java.util.Map;

import com.uwyn.rife.asm.*;

class LazyLoadMethodAdapter extends MethodAdapter implements Opcodes
{
	private String mClassName = null;
	private String mMethodName = null;
	private String mMethodDescriptor = null;
	private String mPropertyName = null;
	private boolean mIsGetter = false;
	private boolean mIsSetter = false;
	
	LazyLoadMethodAdapter(String className, String methodName, String methodDescriptor, MethodVisitor visitor)
	{
		super(visitor);
		
		mClassName = className;
		mMethodName = methodName;
		mMethodDescriptor = methodDescriptor;
		mPropertyName = uncapitalize(methodName.substring(3)); // the length of the get/set accessor prefix
		
		mIsGetter = mMethodName.startsWith("get");
		mIsSetter = mMethodName.startsWith("set");
	}
	
	private static String uncapitalize(String source)
	{
		if (source == null || source.length() == 0)
		{
			return source;
		}

		if (source.length() > 1 &&
			Character.isLowerCase(source.charAt(0)))
		{
			return source;
		}

		char chars[] = source.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	public void visitInsn(int opcode)
	{			
		if (ARETURN == opcode &&
			mIsGetter)
		{
			Label label_begin = new Label();
			Label label_retrieve = new Label();
			Label label_return = new Label();
			Label label_end = new Label();
			
			Type type_object = Type.getType(Object.class);
			
			mv.visitLabel(label_begin);
			
			// don't fetch a value if the current returned value is not null
			mv.visitInsn(DUP);
			mv.visitJumpInsn(IFNONNULL, label_end);
			
			// don't fetch a value if there's no generic query manager stored in the bean
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mClassName, GQM_VAR_NAME, "Lcom/uwyn/rife/database/querymanagers/generic/GenericQueryManager;");
			mv.visitJumpInsn(IFNULL, label_end);
			
			// don't fetch a value if there's already one stored in the map
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mClassName, LAZYLOADED_VAR_NAME, Type.getDescriptor(Map.class));
			mv.visitLdcInsn(mPropertyName);
			mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Map.class), "containsKey", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] {type_object}));
			mv.visitJumpInsn(IFNE, label_retrieve);
			
			// load the value from the database
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mClassName, GQM_VAR_NAME, "Lcom/uwyn/rife/database/querymanagers/generic/GenericQueryManager;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn(mPropertyName);
			mv.visitLdcInsn(Type.getReturnType(mMethodDescriptor).getClassName());
			mv.visitMethodInsn(INVOKESTATIC, "com/uwyn/rife/database/querymanagers/generic/GenericQueryManagerRelationalUtils", "restoreLazyManyToOneProperty", "(Lcom/uwyn/rife/database/querymanagers/generic/GenericQueryManager;Lcom/uwyn/rife/site/Constrained;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
			mv.visitInsn(DUP);
			
			// store the lazily loaded value in the hash map
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mClassName, LAZYLOADED_VAR_NAME, Type.getDescriptor(Map.class));
			mv.visitInsn(SWAP);
			mv.visitLdcInsn(mPropertyName);
			mv.visitInsn(SWAP);
			mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Map.class), "put", Type.getMethodDescriptor(type_object, new Type[] {type_object, type_object}));
			mv.visitInsn(POP);
			mv.visitJumpInsn(GOTO, label_return);
			
			// fetch the existing value from the map
			mv.visitLabel(label_retrieve);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mClassName, LAZYLOADED_VAR_NAME, Type.getDescriptor(Map.class));
			mv.visitLdcInsn(mPropertyName);
			mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Map.class), "get", Type.getMethodDescriptor(type_object, new Type[] {type_object}));
			
			// return the value
			mv.visitLabel(label_return);
			mv.visitTypeInsn(CHECKCAST, Type.getReturnType(mMethodDescriptor).getInternalName());
			mv.visitInsn(ARETURN);
			
			mv.visitLabel(label_end);
		}
		
		super.visitInsn(opcode);
	}
	
	public void visitCode()
	{
		if (mIsSetter)
		{
			Type type_object = Type.getType(Object.class);
			
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mClassName, LAZYLOADED_VAR_NAME, Type.getDescriptor(Map.class));
			Label l1 = new Label();
			mv.visitJumpInsn(IFNULL, l1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitLineNumber(55, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, mClassName, LAZYLOADED_VAR_NAME, Type.getDescriptor(Map.class));
			mv.visitLdcInsn(mPropertyName);
			mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Map.class), "remove", Type.getMethodDescriptor(type_object, new Type[] {type_object}));
			mv.visitInsn(POP);
			mv.visitLabel(l1);		
		}
		super.visitCode();
	}
}
