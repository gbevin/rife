/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LazyLoadClassAdapter.java 3966 2008-07-31 13:16:56Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.instrument;

import static com.uwyn.rife.database.querymanagers.generic.instrument.LazyLoadAccessorsBytecodeTransformer.GQM_VAR_NAME;
import static com.uwyn.rife.database.querymanagers.generic.instrument.LazyLoadAccessorsBytecodeTransformer.LAZYLOADED_VAR_NAME;

import java.util.Map;

import com.uwyn.rife.asm.*;

class LazyLoadClassAdapter extends ClassAdapter implements Opcodes
{	
	private String				mClassName = null;
	private Map<String, String>	mLazyLoadingMethods = null;
	
	LazyLoadClassAdapter(Map<String, String> lazyLoadingMethods, ClassVisitor writer)
	{
		super(writer);
		
		mLazyLoadingMethods = lazyLoadingMethods;
	}
	
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		// retrieve the method from the previously detected lazy loading methods
		String stored_desc = mLazyLoadingMethods.get(name);
		
		// check if it's the same method by comparing the description
		if (stored_desc != null &&
			stored_desc.equals(desc))
		{
			return new LazyLoadMethodAdapter(mClassName, name, desc, super.visitMethod(access, name, desc, signature, exceptions));
		}
		
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
	
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		mClassName = name;
		
		// add a member variable that will be used to store the GenericQueryManager instance in when the bean instance is retrieved
		cv.visitField(ACC_PRIVATE | ACC_SYNTHETIC | ACC_TRANSIENT, GQM_VAR_NAME, "Lcom/uwyn/rife/database/querymanagers/generic/GenericQueryManager;", null, null);
		
		// add a member variable that will be used to store the lazily loaded values of many to one properties
		cv.visitField(ACC_PRIVATE | ACC_SYNTHETIC | ACC_TRANSIENT, LAZYLOADED_VAR_NAME, Type.getDescriptor(Map.class), null, null);
		
		super.visit(version, access, name, signature, superName, interfaces);
	}
}
