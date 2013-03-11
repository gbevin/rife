/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LazyLoadMethodCollector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.instrument;

import com.uwyn.rife.asm.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class LazyLoadMethodCollector implements ClassVisitor
{
	private Map<String, String>	mMethods = null;
	private AnnotationVisitor	mAnnotationVisitor = new LazyLoadNoOpAnnotationVisitor();
	
	LazyLoadMethodCollector()
	{
	}
	
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		// only consider public non static and non synthetic methods
		if ((access & Opcodes.ACC_PUBLIC) != 0 &&
			(access & Opcodes.ACC_STATIC) == 0 &&
			(access & Opcodes.ACC_SYNTHETIC) == 0)
		{
			if (name.length() > 3)
			{
				Type	return_type = Type.getReturnType(desc);
				Type[]	argument_types = Type.getArgumentTypes(desc);
				
				boolean add = false;
				
				// only consider getters without arguments and an object return type
				// that is not part of the JDK java.lang package
				if (name.startsWith("get") &&
					0 == argument_types.length &&
					Type.OBJECT == return_type.getSort() &&
					!return_type.getClassName().startsWith("java.lang."))
				{
					add = true;
				}		
				// only consider setters with one argument type that is not part of the
				// JDK java.lang package and a void return type
				else if (name.startsWith("set") &&
						 1 == argument_types.length &&
						 Type.VOID == return_type.getSort() &&
						 !argument_types[0].getClassName().startsWith("java.lang."))
				{
					add = true;
				}
				
				if (add)
				{
					if (null == mMethods)
					{
						mMethods = new HashMap<String, String>();
					}
					
					mMethods.put(name, desc);
				}
			}
		}
		
		return null;
	}
	
	public Map<String, String> getMethods()
	{
		if (null == mMethods)
		{
			return Collections.EMPTY_MAP;
		}
		
		return mMethods;
	}
	
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
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
	
	public void visitAttribute(Attribute attr)
	{
	}
	
	public void visitInnerClass(String name, String outerName, String innerName, int access)
	{
	}
	
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
	{
		return null;
	}
	
	public void visitEnd()
	{
	}
	
	private class LazyLoadNoOpAnnotationVisitor implements AnnotationVisitor
	{
		public void visit(String name, Object value)
		{
		}
		
		public void visitEnum(String name, String desc, String value)
		{
		}
		
		public AnnotationVisitor visitAnnotation(String name, String desc)
		{
			return this;
		}
		
		public AnnotationVisitor visitArray(String name)
		{
			return this;
		}
		
		public void visitEnd()
		{
		}
	}
}
