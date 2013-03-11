/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationDebug.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.tools.RawFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configures the debug output of the continuations engine.
 * <p>Note that this has little use besides for developing on the
 * continuations instrumentation itself.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ContinuationDebug
{
	static boolean	sDebug = false;
	static boolean	sTrace = false;	
	static Level	sLevel = Level.parse("FINEST");

	/**
	 * The logger instance that is used for the debugging.
	 * @since 1.6
	 */
	public static final Logger	LOGGER = Logger.getLogger("com.uwyn.rife.continuations");
																															  
	private static Handler sHandler = null;

	static
	{
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new RawFormatter());
		LOGGER.addHandler(handler);
		sHandler = handler;
	}

	/**
	 * Configures the tracing of the continuations instrumentation while it's
	 * executing.
	 *
	 * @param trace {@code true} if tracing should be enabled; or
	 * <p>{@code false} otherwise
	 * @since 1.6
	 */
	public static void setTrace(boolean trace)
	{
		sTrace = trace;
	}
	
	/**
	 * Enables or disables debugging.
	 *
	 * @param debug {@code true} if debugging should be enabled; or
	 * <p>{@code false} otherwise
	 * @since 1.6
	 */
	public static void setDebug(boolean debug)
	{
		sDebug = debug;
		reconfigure();
	}
	
	private static void reconfigure()
	{
		///CLOVER:OFF
		if (sDebug)
		{
            sHandler.setLevel(sLevel);
            LOGGER.setLevel(sLevel);
		}
		else
		{
            sHandler.setLevel(Level.OFF);
            LOGGER.setLevel(Level.OFF);
		}
		///CLOVER:ON
	}

	/**
	 * An array with textual representations of the bytecode opcodes.
	 * This is mainly used during tracing.
	 */
	public static final String[] OPCODES =
	{
		"NOP",
		"ACONST_NULL",
		"ICONST_M1",
		"ICONST_0",
		"ICONST_1",
		"ICONST_2",
		"ICONST_3",
		"ICONST_4",
		"ICONST_5",
		"LCONST_0",
		"LCONST_1",
		"FCONST_0",
		"FCONST_1",
		"FCONST_2",
		"DCONST_0",
		"DCONST_1",
		"BIPUSH",
		"SIPUSH",
		"LDC",
		null,
		null,
		"ILOAD",
		"LLOAD",
		"FLOAD",
		"DLOAD",
		"ALOAD",
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		"IALOAD",
		"LALOAD",
		"FALOAD",
		"DALOAD",
		"AALOAD",
		"BALOAD",
		"CALOAD",
		"SALOAD",
		"ISTORE",
		"LSTORE",
		"FSTORE",
		"DSTORE",
		"ASTORE",
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		"IASTORE",
		"LASTORE",
		"FASTORE",
		"DASTORE",
		"AASTORE",
		"BASTORE",
		"CASTORE",
		"SASTORE",
		"POP",
		"POP2",
		"DUP",
		"DUP_X1",
		"DUP_X2",
		"DUP2",
		"DUP2_X1",
		"DUP2_X2",
		"SWAP",
		"IADD",
		"LADD",
		"FADD",
		"DADD",
		"ISUB",
		"LSUB",
		"FSUB",
		"DSUB",
		"IMUL",
		"LMUL",
		"FMUL",
		"DMUL",
		"IDIV",
		"LDIV",
		"FDIV",
		"DDIV",
		"IREM",
		"LREM",
		"FREM",
		"DREM",
		"INEG",
		"LNEG",
		"FNEG",
		"DNEG",
		"ISHL",
		"LSHL",
		"ISHR",
		"LSHR",
		"IUSHR",
		"LUSHR",
		"IAND",
		"LAND",
		"IOR",
		"LOR",
		"IXOR",
		"LXOR",
		"IINC",
		"I2L",
		"I2F",
		"I2D",
		"L2I",
		"L2F",
		"L2D",
		"F2I",
		"F2L",
		"F2D",
		"D2I",
		"D2L",
		"D2F",
		"I2B",
		"I2C",
		"I2S",
		"LCMP",
		"FCMPL",
		"FCMPG",
		"DCMPL",
		"DCMPG",
		"IFEQ",
		"IFNE",
		"IFLT",
		"IFGE",
		"IFGT",
		"IFLE",
		"IF_ICMPEQ",
		"IF_ICMPNE",
		"IF_ICMPLT",
		"IF_ICMPGE",
		"IF_ICMPGT",
		"IF_ICMPLE",
		"IF_ACMPEQ",
		"IF_ACMPNE",
		"GOTO",
		"JSR",
		"RET",
		"TABLESWITCH",
		"LOOKUPSWITCH",
		"IRETURN",
		"LRETURN",
		"FRETURN",
		"DRETURN",
		"ARETURN",
		"RETURN",
		"GETSTATIC",
		"PUTSTATIC",
		"GETFIELD",
		"PUTFIELD",
		"INVOKEVIRTUAL",
		"INVOKESPECIAL",
		"INVOKESTATIC",
		"INVOKEINTERFACE",
		null,
		"NEW",
		"NEWARRAY",
		"ANEWARRAY",
		"ARRAYLENGTH",
		"ATHROW",
		"CHECKCAST",
		"INSTANCEOF",
		"MONITORENTER",
		"MONITOREXIT",
		null,
		"MULTIANEWARRAY",
		"IFNULL",
		"IFNONNULL",
		null,
		null
	};
	
	static String join(int[] array, String seperator)
	{
		if (null == array)
		{
			return null;
		}

		if (null == seperator)
		{
			seperator = "";
		}

		if (0 == array.length)
		{
			return "";
		}
		else
		{
			int current_index = 0;
			String result = "";
			while (current_index < array.length - 1)
			{
				result = result + array[current_index] + seperator;
				current_index++;
			}

			result = result +  array[current_index];
			return result;
		}
	}
	
	static String join(Object[] array, String seperator)
	{
		if (null == array)
		{
			return null;
		}

		if (null == seperator)
		{
			seperator = "";
		}

		if (0 == array.length)
		{
			return "";
		}
		else
		{
			int current_index = 0;
			String result = "";
			while (current_index < array.length - 1)
			{
				result = result + array[current_index] + seperator;
				current_index++;
			}

			result = result +  array[current_index];
			return result;
		}
	
	}
	
	static String repeat(String source, int count)
	{
		if (null == source)
		{
			return null;
		}

		StringBuilder new_string = new StringBuilder();
		while (count > 0)
		{
			new_string.append(source);
			count --;
		}

		return new_string.toString();
	}
}
