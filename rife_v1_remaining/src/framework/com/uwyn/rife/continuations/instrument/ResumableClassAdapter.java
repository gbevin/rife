/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResumableClassAdapter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.*;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import com.uwyn.rife.continuations.instrument.ContinuationDebug;
import com.uwyn.rife.continuations.instrument.MetricsClassVisitor;
import com.uwyn.rife.continuations.instrument.NoOpAnnotationVisitor;
import com.uwyn.rife.continuations.instrument.ResumableMethodAdapter;
import com.uwyn.rife.continuations.instrument.TypesClassVisitor;
import java.util.logging.Level;

import static com.uwyn.rife.continuations.instrument.ContinuationDebug.*;

class ResumableClassAdapter implements ClassVisitor
{
	private ContinuationConfigInstrument	mConfig = null;
	private MetricsClassVisitor				mMetrics = null;
	private TypesClassVisitor				mTypes = null;

	private String					mClassName = null;
	private String					mEntryMethodName = null;
	private String					mEntryMethodDesc = null;
	private ClassVisitor			mClassVisitor = null;
	private boolean					mAdapt = false;
	private NoOpAnnotationVisitor	mAnnotationVisitor = new NoOpAnnotationVisitor();

	
	ResumableClassAdapter(ContinuationConfigInstrument config, MetricsClassVisitor metrics, TypesClassVisitor types, String className, ClassVisitor classVisitor)
	{
		mConfig = config;
		mMetrics = metrics;
		mTypes = types;

		mClassName = className;
		mEntryMethodName = config.getEntryMethodName();
		mEntryMethodDesc = Type.getMethodDescriptor(config.getEntryMethodReturnType(), config.getEntryMethodArgumentTypes());
		mClassVisitor = classVisitor;
		mAdapt = (classVisitor != null);
	}
	
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visit                   ("+access+", \""+name+"\", \""+signature+"\", \""+superName+"\", "+(null == interfaces ? null : join(interfaces, ","))+")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			mClassVisitor.visit(version, access, name, signature, superName, interfaces);
		}
	}
	
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visitField              ("+access+", \""+name+"\", \""+desc+"\", "+signature+", "+value+")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			return mClassVisitor.visitField(access, name, desc, signature, value);
		}
		
		return null;
	}
	
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visitMethod             ("+access+", \""+name+"\", \""+desc+"\", \""+signature+"\", "+(null == exceptions ? null : join(exceptions, ","))+")");
		///CLOVER:ON
		
		// only adapt the processElement method
		if (mEntryMethodName.equals(name) &&
			mEntryMethodDesc.equals(desc))
		{
			if (mAdapt)
			{
				if (!mMetrics.makeResumable())
				{
					return new ResumableMethodAdapter(mConfig, null, mClassVisitor.visitMethod(access, name, desc, signature, exceptions), mClassName, false, -1, 0);
				}
				else
				{
					return new ResumableMethodAdapter(mConfig, mTypes, mClassVisitor.visitMethod(access, name, desc, signature, exceptions), mClassName, true, mMetrics.getMaxLocals(), mMetrics.getPauseCount());
				}
			}
			else
			{
				return new ResumableMethodAdapter(mConfig, null, null, null, false, -1, 0);
			}
		}
		
		if (null == mClassVisitor)
		{
			return null;
		}
		else
		{
			return new MethodAdapter(mClassVisitor.visitMethod(access, name, desc, signature, exceptions));
		}
	}
	
	public void visitInnerClass(String name, String outerName, String innerName, int access)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visitInnerClass         (\""+name+"\", \""+outerName+"\", \""+innerName+", "+access+")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			mClassVisitor.visitInnerClass(name, outerName, innerName, access);
		}
	}
	
	public void visitAttribute(Attribute attr)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visitAttribute          ("+attr+")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			mClassVisitor.visitAttribute(attr);
		}
	}
	
	public void visitSource(String source, String debug)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visitSource             (\""+source+"\", \""+debug+"\")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			mClassVisitor.visitSource(source, debug);
		}
	}
	
	public void visitOuterClass(String owner, String name, String desc)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visitOuterClass         (\""+owner+"\", \""+name+"\", \""+desc+"\")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			mClassVisitor.visitOuterClass(owner, name, desc);
		}
	}
	
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visitAnnotation         (\""+desc+"\", "+visible+")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			return mClassVisitor.visitAnnotation(desc, visible);
		}
		
		return mAnnotationVisitor;
	}
	
	public void visitEnd()
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest("Class:visitEnd                ()");
		///CLOVER:ON
		
		if (mAdapt)
		{
			mClassVisitor.visitEnd();
		}
	}
}
