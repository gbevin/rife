/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TypesContext.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.Type;
import com.uwyn.rife.continuations.instrument.ContinuationDebug;
import com.uwyn.rife.continuations.instrument.TypesContext;
import com.uwyn.rife.continuations.instrument.TypesNode;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
	
class TypesContext implements Cloneable
{
	public final static String CAT1_BOOLEAN = "1Z";
	public final static String CAT1_CHAR = "1C";
	public final static String CAT1_FLOAT = "1F";
	public final static String CAT1_BYTE = "1B";
	public final static String CAT1_SHORT = "1S";
	public final static String CAT1_INT = "1I";
	public final static String CAT1_ADDRESS = "1A";
	public final static String CAT2_DOUBLE = "2D";
	public final static String CAT2_LONG = "2J";
	public final static String ARRAY_BOOLEAN = "[Z";
	public final static String ARRAY_CHAR = "[C";
	public final static String ARRAY_FLOAT = "[F";
	public final static String ARRAY_BYTE = "[B";
	public final static String ARRAY_SHORT = "[S";
	public final static String ARRAY_INT = "[I";
	public final static String ARRAY_DOUBLE = "[D";
	public final static String ARRAY_LONG = "[J";
	public final static String TYPE_NULL = "NULL";
	
	private Map<Integer, String>	mVars = null;
	private Stack<String>			mStack = null;

	private int			mSort = TypesNode.REGULAR;
	
	private String		mDebugIndent = null;
	
	TypesContext()
	{
		mVars = new HashMap<Integer, String>();
		mStack = new Stack<String>();
	}
	
	TypesContext(Map<Integer, String> vars, Stack<String> stack)
	{
		mVars = vars;
		mStack = stack;
	}
	
	Map<Integer, String> getVars()
	{
		return mVars;
	}
	
	Stack<String> getStack()
	{
		return mStack;
	}
	
	boolean hasVar(int var)
	{
		return mVars.containsKey(var);
	}
	
	String getVar(int var)
	{
		return mVars.get(var);
	}
	
	void setVar(int var, String type)
	{
		mVars.put(var, type);
	}
	
	int getVarType(int var)
	{
		String type = getVar(var);
		if (CAT1_INT == type)
		{
			return Type.INT;
		}
		else if (CAT1_FLOAT == type)
		{
			return Type.FLOAT;
		}
		else if (CAT2_LONG == type)
		{
			return Type.LONG;
		}
		else if (CAT2_DOUBLE == type)
		{
			return Type.DOUBLE;
		}
		else
		{
			return Type.OBJECT;
		}
	}
	
	String peek()
	{
		return mStack.peek();
	}
	
	String pop()
	{
		String result = null;
		if (mStack.size() > 0)
		{
			result = mStack.pop();
		}
		printStack();
		return result;
	}
	
	void push(String type)
	{
		mStack.push(type);
		printStack();
	}

	Stack<String> getStackClone()
	{
		return (Stack<String>)mStack.clone();
	}

	void cloneVars()
	{
		mVars = new HashMap<Integer, String>(mVars);
	}
	
	void setSort(int type)
	{
		mSort = type;
	}

	int getSort()
	{
		return mSort;
	}
	
	void printStack()
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
		{
			if (0 == mStack.size())
			{
				ContinuationDebug.LOGGER.finest(mDebugIndent+"  | empty");
			}
			else
			{
				for (int i = 0; i < mStack.size(); i++)
				{
					ContinuationDebug.LOGGER.finest(mDebugIndent+"  | "+i+" : "+mStack.get(i));
				}
			}
		}
		///CLOVER:ON
	}
	
	void setDebugIndent(String debugIndent)
	{
		mDebugIndent = debugIndent;
	}
	
	TypesContext clone(TypesNode node)
	{
		TypesContext new_context = new TypesContext(new HashMap<Integer, String>(mVars), (Stack<String>)mStack.clone());
		new_context.setSort(node.getSort());
		return new_context;
	}
	
	public TypesContext clone()
	{
        TypesContext new_context = null;
		try
		{
			new_context = (TypesContext)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// this should never happen
			Logger.getLogger("com.uwyn.rife.continuations").severe(ExceptionUtils.getExceptionStackTrace(e));
		}
		
		new_context.mVars = new HashMap<Integer, String>(mVars);
		new_context.mStack = (Stack<String>)mStack.clone();
		
		return new_context;
	}
}
