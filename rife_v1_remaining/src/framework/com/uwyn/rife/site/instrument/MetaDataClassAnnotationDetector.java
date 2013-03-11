/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataClassAnnotationDetector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.instrument;

import com.uwyn.rife.asm.*;

/**
 * Detects whether a class has the {@code MetaDataClass} class annotation
 * by analyzing its byte code.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6.2
 */
public abstract class MetaDataClassAnnotationDetector
{
	private final static String META_DATA_ANNOTATION_INTERNAL_TYPE = "Lcom/uwyn/rife/site/annotations/MetaDataClass;";

	/**
	 * Retrieves the class name of the meta data class that is associated
	 * through the {@code MetaDataClass} class annotation.
	 *
	 * @param bytes the array of bytes that defines the class that needs to be
	 * analyzed
	 * @return the name of the associated meta data class; or
	 * <p>
	 * {@code null} if no meta data class was specified through an annotation
	 * @since 1.6.2
	 */
	public static String getMetaDataClassName(final byte[] bytes)
	{
		DetectionClassVisitor visitor = new DetectionClassVisitor();
		ClassReader detection_reader = new ClassReader(bytes);
		detection_reader.accept(visitor, ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);

		return visitor.getMetaDataClassName();
	}

	private static class DetectionClassVisitor implements ClassVisitor
	{
		private String	mMetaDataClassName = null;

		private String getMetaDataClassName()
		{
			return mMetaDataClassName;
		}

		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
		{
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
			if (META_DATA_ANNOTATION_INTERNAL_TYPE.equals(desc)) {
				return new AnnotationVisitor() {
					public void visit(String name, Object value)
					{
						if ("value".equals(name) &&
							value != null)
						{
							mMetaDataClassName = ((Type)value).getClassName();
						}
					}

					public void visitEnum(String name, String desc, String value)
					{
					}

					public AnnotationVisitor visitAnnotation(String name, String desc)
					{
						return null;
					}

					public AnnotationVisitor visitArray(String name)
					{
						return null;
					}

					public void visitEnd()
					{
					}
				};
			}
			
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