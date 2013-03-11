/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RifeTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * An abstract base class that will only execute the bytecode transformation
 * when the class is considered to not be part of a core package, like for
 * example the JDK or the standard XML parsers.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
abstract public class RifeTransformer implements ClassFileTransformer
{
	final public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
	{
		if (className.startsWith("java/") ||
			className.startsWith("javax/") ||
			className.startsWith("com/sun/") ||
			className.startsWith("sun/") ||
			className.startsWith("org/apache/") ||			
			className.startsWith("org/xml/") ||
			className.startsWith("org/w3c/") ||
			(className.startsWith("com/uwyn/rife/") && !className.startsWith("com/uwyn/rife/continuations/Test")) ||
			className.startsWith("com/tc/"))
		{
			return classfileBuffer;
		}
		
		return transformRife(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
	}

	/**
	 * This transform method will only be called when the class is not part of
	 * a core package.
	 * <p>For the rest it functions exactly as the regular
	 * {@code transform} method.
	 * 
	 * @see #transform
	 * @since 1.6
	 */
	abstract protected byte[] transformRife(ClassLoader loader, String classNameInternal, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException;
}
