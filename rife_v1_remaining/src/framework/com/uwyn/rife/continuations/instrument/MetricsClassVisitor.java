/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetricsClassVisitor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.*;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import com.uwyn.rife.continuations.instrument.ContinuationDebug;
import com.uwyn.rife.continuations.instrument.MetricsMethodVisitor;
import com.uwyn.rife.continuations.instrument.NoOpAnnotationVisitor;
import java.util.ArrayList;
import java.util.logging.Level;

class MetricsClassVisitor implements ClassVisitor
{
	private ContinuationConfigInstrument	mConfig = null;
	private String							mClassName = null;
	private String							mEntryMethodName = null;
	private String							mEntryMethodDesc = null;
	private int								mMaxLocals = -1;
	private int								mPauseCount = -1;
	private int								mAnswerCount = -1;
	private ArrayList<String>				mExceptionTypes = null;
	private NoOpAnnotationVisitor			mAnnotationVisitor = new NoOpAnnotationVisitor();

	MetricsClassVisitor(ContinuationConfigInstrument config, String className)
	{
		mConfig = config;
		mClassName = className;
		mEntryMethodName = config.getEntryMethodName();
		mEntryMethodDesc = Type.getMethodDescriptor(config.getEntryMethodReturnType(), config.getEntryMethodArgumentTypes());
	}
	
	void setMaxLocals(int maxLocals)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("maxlocals = "+maxLocals);
		///CLOVER:ON

		mMaxLocals = maxLocals;
	}
	
	int getMaxLocals()
	{
		return mMaxLocals;
	}
	
	void setPauseCount(int pauseCount)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("pauseCount = "+pauseCount);
		///CLOVER:ON

		mPauseCount = pauseCount;
	}
	
	void setAnswerCount(int answerCount)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("answerCount = "+answerCount);
		///CLOVER:ON
		
		mAnswerCount = answerCount;
	}
	
	void setExceptionTypes(ArrayList<String> exceptionTypes)
	{
		mExceptionTypes = exceptionTypes;
	}
	
	String nextExceptionType()
	{
		return mExceptionTypes.remove(0);
	}
	
	int getPauseCount()
	{
		return mPauseCount;
	}
	
	int getAnswerCount()
	{
		return mAnswerCount;
	}
	
	boolean makeResumable()
	{
		return mPauseCount > 0 || mAnswerCount > 0;
	}
	
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		if (mEntryMethodName.equals(name) &&
			mEntryMethodDesc.equals(desc))
		{
			return new MetricsMethodVisitor(mConfig, this, mClassName);
		}
		
		return null;
	}
	
	public void visitInnerClass(String name, String outerName, String innerName, int access)
	{
	}
	
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
	}
	
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
	{
		return null;
	}
	
	public void visitAttribute(Attribute attr)
	{
	}
	
	public void visitSource(String source, String debug)
	{
	}

	public void visitOuterClass(String owner, String name, String desc)
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
