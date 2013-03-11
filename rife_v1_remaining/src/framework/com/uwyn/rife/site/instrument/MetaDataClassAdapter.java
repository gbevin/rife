/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataClassAdapter.java 3965 2008-07-30 08:02:23Z gbevin $
 */
package com.uwyn.rife.site.instrument;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import com.uwyn.rife.asm.*;
import com.uwyn.rife.site.MetaDataBeanAware;
import com.uwyn.rife.site.MetaDataMerged;

class MetaDataClassAdapter extends ClassAdapter implements Opcodes
{
	final static String	DELEGATE_VAR_NAME = "$Rife$Meta$Data$Delegate$";
	
	private Map<String, List<String>>	mExistingMethods = null;
	
	private Class	mMetaData = null;
	private String	mMetaDataInternalName = null;
	private String	mBaseInternalName = null;
	
	MetaDataClassAdapter(Map<String, List<String>> existingMethods, Class metaData, ClassVisitor writer)
	{
		super(writer);
		
		mExistingMethods = existingMethods;
		mMetaData = metaData;
		mMetaDataInternalName = Type.getInternalName(mMetaData);
	}
	
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		if (mBaseInternalName != null)
		{
			if (name.equals("<init>"))
			{
				return new MetaDataDefaultConstructorAdapter(mBaseInternalName, mMetaDataInternalName, super.visitMethod(access, name, desc, signature, exceptions));
			}
			else if (name.equals("clone"))
			{
				return new MetaDataCloneableAdapter(mBaseInternalName, mMetaDataInternalName, super.visitMethod(access, name, desc, signature, exceptions));
			}
		}
		
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
	
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		// go over all the interfaces of the meta data class and its parents
		List<Class> meta_interfaces = new ArrayList<Class>();
		Stack<Class> meta_classes = new Stack<Class>();
		meta_classes.push(mMetaData);
		while (meta_classes.size() > 0)
		{
			Class current = meta_classes.pop();
			if (current == Object.class)
			{
				continue;
			}
			
			Class super_class = current.getSuperclass();
			if (super_class != null)
			{
				meta_classes.push(super_class);
			}
			
			Class[] current_interfaces = current.getInterfaces();
			for (Class i : current_interfaces)
			{
				if (i == Cloneable.class ||
					i == Serializable.class ||
					i == MetaDataMerged.class ||
					i == MetaDataBeanAware.class ||
					i.getName().startsWith("com.tc."))
				{
					continue;
				}
				
				if (!meta_interfaces.contains(i))
				{
					meta_interfaces.add(i);
				}
				
				meta_classes.push(i);
			}
		}
			
		// only instrument this class when the meta data class actually implements interfaces
		if (meta_interfaces.size() > 0)
		{
			mBaseInternalName = name;
			
			// add a member variable that will be used to delegate the interface method calls to
			cv.visitField(ACC_PRIVATE|ACC_SYNTHETIC|ACC_TRANSIENT, DELEGATE_VAR_NAME, Type.getDescriptor(mMetaData), null, null);
			
			// obtain the already existing interfaces
			List<String> interfaces_merged = new ArrayList<String>();
			interfaces_merged.addAll(Arrays.asList(interfaces));
			
			// process all interfaces and add those that are not yet implemented
			// to the base class all the interface methods will be delegated to the
			// member variable delegate
			String internal_name;
			for (Class interface_class : meta_interfaces)
			{
				internal_name = Type.getInternalName(interface_class);
				if (!interfaces_merged.contains(internal_name))
				{
					// implement and delegate all the methods of the interface
					List<String> descriptors;
					for (Method method : interface_class.getDeclaredMethods())
					{
						// check if the class already has an implementation for this method
						// and if it's the case, don't implement it automatically
						descriptors = mExistingMethods.get(method.getName());
						if (descriptors != null &&
							descriptors.contains(Type.getMethodDescriptor(method)))
						{
							continue;
						}
						
						// convert the exceptions into internal types
						String[] exceptions_types = null;
						if (method.getExceptionTypes().length > 0)
						{
							List<String> exceptions_lists = new ArrayList<String>(method.getExceptionTypes().length);
							for (Class exception : method.getExceptionTypes())
							{
								exceptions_lists.add(Type.getInternalName(exception));
							}
							
							exceptions_types = new String[exceptions_lists.size()];
							exceptions_lists.toArray(exceptions_types);
						}
						
						// implement the interface method to delegate the call to the synthetic member variable
						String	method_descriptor = Type.getMethodDescriptor(method);
						int		method_param_count = method.getParameterTypes().length;
						
						MethodVisitor mv = cv.visitMethod(ACC_PUBLIC|ACC_SYNTHETIC, method.getName(), method_descriptor, null, exceptions_types);
						mv.visitCode();
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, mBaseInternalName, DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");
						
						// handle the method parameters correctly
						int param_count = 1;
						for (Class param : method.getParameterTypes())
						{
							switch (Type.getType(param).getSort())
							{
								case Type.INT:
								case Type.BOOLEAN:
								case Type.CHAR:
								case Type.BYTE:
								case Type.SHORT:
									mv.visitVarInsn(ILOAD, param_count);
									break;
								case Type.LONG:
									mv.visitVarInsn(LLOAD, param_count);
									break;
								case Type.FLOAT:
									mv.visitVarInsn(FLOAD, param_count);
									break;
								case Type.DOUBLE:
									mv.visitVarInsn(DLOAD, param_count);
									break;
								default:
									mv.visitVarInsn(ALOAD, param_count);
									break;
							}
							param_count++;
						}
						
						mv.visitMethodInsn(INVOKEVIRTUAL, mMetaDataInternalName, method.getName(), method_descriptor);
						// handle the return type correctly
						switch (Type.getReturnType(method).getSort())
						{
							case Type.VOID:
								mv.visitInsn(RETURN);
								break;
							case Type.INT:
							case Type.BOOLEAN:
							case Type.CHAR:
							case Type.BYTE:
							case Type.SHORT:
								mv.visitInsn(IRETURN);
								break;
							case Type.LONG:
								mv.visitInsn(LRETURN);
								break;
							case Type.FLOAT:
								mv.visitInsn(FRETURN);
								break;
							case Type.DOUBLE:
								mv.visitInsn(DRETURN);
								break;
							default:
								mv.visitInsn(ARETURN);
								break;
						}
						mv.visitMaxs(method_param_count+1, method_param_count+2);
						mv.visitEnd();
					}
					
					interfaces_merged.add(internal_name);
				}
			}
			
			// handle clonability correctly when the meta data class is tied to one
			// particular instance of the base class
			if (MetaDataBeanAware.class.isAssignableFrom(mMetaData))
			{
				// implement the Cloneable interface in case this hasn't been done yet
				String cloneable_internal = Type.getInternalName(Cloneable.class);
				if (!interfaces_merged.contains(cloneable_internal))
				{
					interfaces_merged.add(cloneable_internal);
				}
				
				// check if the clone method has to be added from scratch
				List<String> clone_method_descriptors = mExistingMethods.get("clone");
				if (null == clone_method_descriptors ||
					(!clone_method_descriptors.contains("()Ljava/lang/Object;") &&
					 !clone_method_descriptors.contains("()L"+mBaseInternalName+";")))
				{
					MethodVisitor mv = cv.visitMethod(ACC_PUBLIC|ACC_SYNTHETIC, "clone", "()Ljava/lang/Object;", null, new String[] { "java/lang/CloneNotSupportedException" });
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "clone", "()Ljava/lang/Object;");
					mv.visitTypeInsn(CHECKCAST, mBaseInternalName);
					mv.visitVarInsn(ASTORE, 1);
					
					mv.visitVarInsn(ALOAD, 1);
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, mBaseInternalName, DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");
					mv.visitMethodInsn(INVOKEVIRTUAL, mMetaDataInternalName, "clone", "()Ljava/lang/Object;");
					mv.visitTypeInsn(CHECKCAST, mMetaDataInternalName);
					mv.visitFieldInsn(PUTFIELD, mBaseInternalName, DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");
					mv.visitVarInsn(ALOAD, 1);
					mv.visitFieldInsn(GETFIELD, mBaseInternalName, DELEGATE_VAR_NAME, "L"+mMetaDataInternalName+";");
					mv.visitVarInsn(ALOAD, 1);
					mv.visitMethodInsn(INVOKEVIRTUAL, mMetaDataInternalName, "setMetaDataBean", "(Ljava/lang/Object;)V");
					mv.visitVarInsn(ALOAD, 1);
					mv.visitInsn(ARETURN);
					Label l0 = new Label();
					mv.visitLabel(l0);
					mv.visitJumpInsn(GOTO, l0);
					mv.visitMaxs(2, 3);
					mv.visitEnd();
				}
			}
			
			// use the new collection of interfaces for the class
			interfaces = new String[interfaces_merged.size()];
			interfaces_merged.toArray(interfaces);
		}
		
		super.visit(version, access, name, signature, superName, interfaces);
	}
}
