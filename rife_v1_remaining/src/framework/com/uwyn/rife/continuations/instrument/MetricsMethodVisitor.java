/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetricsMethodVisitor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.*;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import com.uwyn.rife.continuations.instrument.MetricsClassVisitor;
import com.uwyn.rife.continuations.instrument.NoOpAnnotationVisitor;
import java.util.ArrayList;
import java.util.HashMap;

class MetricsMethodVisitor implements MethodVisitor, Opcodes
{
	private ContinuationConfigInstrument	mConfig = null;
	private MetricsClassVisitor 			mClassVisitor = null;
	private String							mClassName = null;
	private int								mPausecount = 0;
	private int								mAnswercount = 0;
	private ArrayList<Label>				mLabelsOrder = new ArrayList<Label>();
	private HashMap<Label, String>			mExceptionTypes = new HashMap<Label, String>();
	private NoOpAnnotationVisitor			mAnnotationVisitor = new NoOpAnnotationVisitor();
	
	MetricsMethodVisitor(ContinuationConfigInstrument config, MetricsClassVisitor classVisitor, String className)
	{
		mConfig = config;
		mClassVisitor = classVisitor;
		mClassName = className;
	}
	
	public void visitMaxs(int maxStack, int maxLocals)
	{
		// go over all the labels in their order of appearance and check if
		// they are exception labels with thus an initial exception
		// type
		ArrayList<String> exception_labels_types = new ArrayList<String>(mLabelsOrder.size());
		for (Label label : mLabelsOrder)
		{
			exception_labels_types.add(mExceptionTypes.get(label));
		}
		
		// store all the metrics in the class visitor
		mClassVisitor.setMaxLocals(maxLocals);
		mClassVisitor.setPauseCount(mPausecount);
		mClassVisitor.setAnswerCount(mAnswercount);
		mClassVisitor.setExceptionTypes(exception_labels_types);
	}
	
	public void visitMethodInsn(int opcode, String owner, String methodname, String desc)
	{
		String owner_classname = owner.replace('/', '.');
		
		if ((owner_classname.equals(mConfig.getContinuableSupportClassName()) || mClassName.equals(owner_classname)) &&
			((mConfig.getPauseMethodName().equals(methodname) && "()V".equals(desc)) ||
			 (mConfig.getStepbackMethodName().equals(methodname) && "()V".equals(desc)) ||
			 (mConfig.getCallMethodName().equals(methodname) &&
			  Type.getMethodDescriptor(mConfig.getCallMethodReturnType(), mConfig.getCallMethodArgumentTypes()).equals(desc))))
		{
			mPausecount++;
		}
		else if (mConfig.getAnswerMethodName().equals(methodname) &&
				 ("()V".equals(desc) || "(Ljava/lang/Object;)V".equals(desc)))
		{
			mAnswercount++;
		}
	}
	
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
	}
	
	public void visitVarInsn(int opcode, int var)
	{
	}
	
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
	{
		// store the types of the exception labels so that the exception
		// instance can be cast to it when restoring the local
		// variable stack
		if (null == type)
		{
			type = "java/lang/Throwable";
		}
		
		mExceptionTypes.put(handler, type);
	}
	
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
	{
	}
	
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
	}
	
	public void visitJumpInsn(int opcode, Label label)
	{
	}
	
	public void visitLabel(Label label)
	{
		// remember the order of the labels
		mLabelsOrder.add(label);
	}
	
	public void visitMultiANewArrayInsn(String desc, int dims)
	{
	}
	
	public void visitLineNumber(int line, Label start)
	{
	}
	
	public void visitIntInsn(int opcode, int operand)
	{
	}
	
	public void visitIincInsn(int var, int increment)
	{
	}
	
	public void visitTypeInsn(int opcode, String desc)
	{
	}
	
	public void visitLdcInsn(Object cst)
	{
	}
	
	public void visitInsn(int opcode)
	{
	}
	
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
	{
	}
	
	public void visitAttribute(Attribute attr)
	{
	}
	
	public void visitCode()
	{
	}
	
	public AnnotationVisitor visitAnnotationDefault()
	{
		return mAnnotationVisitor;
	}
	
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		return mAnnotationVisitor;
	}
	
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible)
	{
		return mAnnotationVisitor;
	}
	
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
	{
	}
	
	public void visitEnd()
	{
	}
}

