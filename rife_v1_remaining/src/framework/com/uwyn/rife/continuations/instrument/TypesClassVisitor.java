/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TypesClassVisitor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.*;
import com.uwyn.rife.continuations.ContinuationConfigInstrument;

class TypesClassVisitor implements ClassVisitor
{
	private ContinuationConfigInstrument	mConfig = null;
	private MetricsClassVisitor				mMetrics = null;
	private String							mClassName = null;
	private String							mEntryMethodDesc = null;
	private String							mEntryMethodName = null;
	private TypesContext[]					mPauseContexts = null;
	private TypesContext[]					mLabelContexts = null;
	private int								mPauseContextCounter = 0;
	private int								mLabelContextCounter = 0;
	private NoOpAnnotationVisitor			mAnnotationVisitor = new NoOpAnnotationVisitor();

	TypesClassVisitor(ContinuationConfigInstrument config, MetricsClassVisitor metrics, String className)
	{
		mConfig = config;
		mMetrics = metrics;
		mClassName = className;
		mEntryMethodName = config.getEntryMethodName();
		mEntryMethodDesc = Type.getMethodDescriptor(config.getEntryMethodReturnType(), config.getEntryMethodArgumentTypes());
	}
	
	MetricsClassVisitor getMetrics()
	{
		return mMetrics;
	}
	
	void setPauseContexts(TypesContext[] pauseContexts)
	{
		mPauseContexts = pauseContexts;
	}
	
	TypesContext nextPauseContext()
	{
		return mPauseContexts[mPauseContextCounter++];
	}
	
	void setLabelContexts(TypesContext[] labelContexts)
	{
		mLabelContexts = labelContexts;
	}
	
	TypesContext nextLabelTypes()
	{
		return mLabelContexts[mLabelContextCounter++];
	}

	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		if (mEntryMethodName.equals(name) &&
			mEntryMethodDesc.equals(desc))
		{
			return new TypesMethodVisitor(mConfig, this, mClassName);
		}
		
		return null;
	}
	
	public void visitInnerClass(String name, String outerName, String innerName, int access)
	{
	}
	
	public void visitOuterClass(String owner, String name, String desc)
	{
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
	}
	
	public void visitSource(String source, String debug)
	{
	}

	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
	{
		return null;
	}
	
	public void visitAttribute(Attribute attr)
	{
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		return mAnnotationVisitor;
	}
	
	public void visitEnd()
	{
	}
}
