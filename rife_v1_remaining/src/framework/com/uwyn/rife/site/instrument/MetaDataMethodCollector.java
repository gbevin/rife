/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataMethodCollector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.instrument;

import java.util.*;

import com.uwyn.rife.asm.*;

class MetaDataMethodCollector implements ClassVisitor
{
	private Map<String, List<String>>	mMethods = null;
	private AnnotationVisitor			mAnnotationVisitor = new MetaDataNoOpAnnotationVisitor();

	MetaDataMethodCollector()
	{
	}
	
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		if (null == mMethods)
		{
			mMethods = new HashMap<String, List<String>>();
		}
		List<String> descriptions = mMethods.get(name);
		if (null == descriptions)
		{
			descriptions = new ArrayList<String>();
			mMethods.put(name, descriptions);
		}
		descriptions.add(desc);
		
		return null;
	}
	
	public Map<String, List<String>> getMethods()
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
	
	private class MetaDataNoOpAnnotationVisitor implements AnnotationVisitor
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
