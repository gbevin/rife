/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ConstrainedDetector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.instrument;

import com.uwyn.rife.asm.*;

import com.uwyn.rife.instrument.exceptions.ClassBytesNotFoundException;
import com.uwyn.rife.instrument.exceptions.VisitInterruptionException;
import com.uwyn.rife.tools.ClassBytesLoader;

/**
 * Detects whether a class implements the {@code Constrained} interface or not,
 * by analyzing its bytecode.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ConstrainedDetector
{
	private final static String    CONSTRAINED_INTERNAL_NAME = "com/uwyn/rife/site/Constrained";
	private final static String    CONSTRAINED_NAME = "com.uwyn.rife.site.Constrained";
	private final static String    OBJECT_INTERNAL_NAME = "java/lang/Object";
	
	private ClassBytesLoader    mBytesLoader = null;

	/**
	 * Creates new instance by providing a loader that is able to retrieve the
	 * bytes of any parent classes that are extended by the class that is
	 * being analyzed.
	 * 
	 * @param bytesLoader the loader that will be used to retrieve the bytes
	 * of the additional classes
	 * @since 1.6
	 */
	public ConstrainedDetector(ClassBytesLoader bytesLoader)
	{
		mBytesLoader = bytesLoader;
	}

	/**
	 * Verifies if the Constrained interface is implemented by the class that
	 * is defined by the bytes that are provided.
	 * 
	 * @param internedClassname the class name as the previously interned
	 * string
	 * @param bytes the array of bytes that defines the class that needs to be
	 * analyzed
	 * @return {@code true} if the analyzed class implements the constrained
	 * interface; or
	 * <p>{@code false} otherwise
	 * @exception ClassBytesNotFoundException when the bytes of a parent class
	 * can be found
	 * @since 1.6
	 */
	public boolean isConstrained(String internedClassname, byte[] bytes)
	throws ClassBytesNotFoundException
	{
		if (CONSTRAINED_NAME == internedClassname)
		{
			return false;
		}
		
		ConstrainedDetectionClassVisitor	visitor = new ConstrainedDetectionClassVisitor();
		ClassReader							detection_reader = null;
		
		while (!visitor.isConstrained())
		{
			detection_reader = new ClassReader(bytes);
			try
			{
				detection_reader.accept(visitor, ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
			}
			catch (VisitInterruptionException e)
			{
				// do nothing
			}
			
			if (null == visitor.getSuperNameInternal() ||
				OBJECT_INTERNAL_NAME == visitor.getSuperNameInternal())
			{
				break;
			}
			
			// get the parent's class' bytecode
			if (!visitor.isConstrained())
			{                                       
				String filename = visitor.getSuperNameInternal() + ".class";
				try
				{
					bytes = mBytesLoader.getClassBytes(filename);
				}
				catch (ClassNotFoundException e)
				{                                                                                                             
					throw new ClassBytesNotFoundException(filename, e);
				}
			}
		}
		
		return visitor.isConstrained();
	}
	
	private class ConstrainedDetectionClassVisitor implements ClassVisitor
	{
		private boolean	mIsConstrained = false;
		private String	mSuperNameInternal = null;
		
		private boolean isConstrained()
		{
			return mIsConstrained;
		}
		
		private String getSuperNameInternal()
		{
			return mSuperNameInternal;
		}
		
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
		{
			for (String interface_name : interfaces)
			{
				if (CONSTRAINED_INTERNAL_NAME == interface_name.intern())
				{
					mIsConstrained = true;
					break;
				}
			}
			
			if (null == superName)
			{
				mSuperNameInternal = null;
			}
			else
			{
				mSuperNameInternal = superName.intern();
			}
			
			throw new VisitInterruptionException();
		}
		
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			return null;
		}
		
		public void visitInnerClass(String name, String outerName, String innerName, int access)
		{
		}
		
		public void visitOuterClass(String owner, String name, String desc)
		{
		}
		
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
		{
			return null;
		}
		
		public void visitSource(String source, String debug)
		{
		}
		
		public AnnotationVisitor visitAnnotation(String desc, boolean visible)
		{
			return null;
		}
		
		public void visitAttribute(Attribute attr)
		{
		}
		
		public void visitEnd()
		{
		}
	}
}
