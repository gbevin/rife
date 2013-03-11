/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ClassInterfaceDetector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.instrument;

import com.uwyn.rife.asm.*;

import com.uwyn.rife.instrument.ClassBytesProvider;
import com.uwyn.rife.instrument.exceptions.VisitInterruptionException;

/**
 * Detects whether a class implements a particular interface by analyzing the
 * bytecode instead of loading the class and performing reflection calls.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ClassInterfaceDetector
{
	private final String	OBJECT_INTERNAL_NAME = "java/lang/Object";
	private final String	OBJECT_NAME = "java.lang.Object";
	
	private ClassBytesProvider	mBytesProvider = null;
	private String				mInterfaceNameInternal = null;

	/**
	 * Creates a new instance of the interface detector.
	 *
	 * @param bytesProvider the bytecode provider that will be used to load the
	 * bytes of the parent classes or interfaces
	 * @param internalInterfaceNameInterned the name of the interface that should
	 * be detected, it should be in the internal bytecode naming format
	 * (/ instead of .) and it should be interned.
	 * @since 1.6
	 */
	public ClassInterfaceDetector(ClassBytesProvider bytesProvider, String internalInterfaceNameInterned)
	{
		mBytesProvider = bytesProvider;
		mInterfaceNameInternal = internalInterfaceNameInterned;
	}

	/**
	 * Perform the detection.
	 *
	 * @param bytes the bytecode of the class that is being analyzed
	 * @param doAutoReload indicator if the class should be automatically
	 * reloaded after a modification to the sources, in case the
	 * {@code ClassBytesProvider} supports this
	 * @return {@code true} if the detection was successful; or
	 * <p>{@code false} otherwise
	 * @throws ClassNotFoundException
	 */
	public boolean detect(byte[] bytes, boolean doAutoReload)
	throws ClassNotFoundException
	{
		DetectionClassVisitor	visitor = new DetectionClassVisitor();
		ClassReader				detection_reader = null;
		
		while (!visitor.isClassOrInterface())
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
			
			if (null == visitor.getSuperName() ||
				OBJECT_NAME == visitor.getSuperName())
			{
				break;
			}
			
			// get the parent's class' bytecode
			if (!visitor.isClassOrInterface())
			{
				bytes = mBytesProvider.getClassBytes(visitor.getSuperName(), doAutoReload);
			}
		}
		
		return visitor.isClassOrInterface();
	}
	
	private class DetectionClassVisitor implements ClassVisitor
	{
		private boolean	mIsClassOrInterface = false;
		private String	mSuperName = null;
		
		private DetectionClassVisitor()
		{
		}
		
		private boolean isClassOrInterface()
		{
			return mIsClassOrInterface;
		}
		
		private String getSuperName()
		{
			return mSuperName;
		}
		
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
		{
			if (null == superName)
			{
				return;
			}
			
			for (String interface_name : interfaces)
			{
				if (mInterfaceNameInternal == interface_name.intern())
				{
					mIsClassOrInterface = true;
					break;
				}
			}
			
			if (null == superName)
			{
				mSuperName = null;
			}
			else if (OBJECT_INTERNAL_NAME == superName.intern())
			{
				mSuperName = OBJECT_NAME;
			}
			else
			{
				mSuperName = superName.replace('/', '.').intern();
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
