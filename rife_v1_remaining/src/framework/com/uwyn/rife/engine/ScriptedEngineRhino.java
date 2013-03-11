/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ScriptedEngineRhino.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.ChildTriggerNotImplementedException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.ScriptErrorException;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.exceptions.LightweightError;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

class ScriptedEngineRhino extends ScriptedEngine
{
	private Context		mContext = null;
	private Scriptable	mScope = null;
	
	ScriptedEngineRhino(String code)
	throws EngineException
	{
		super("javascript", code);
		
		mContext = Context.enter();
		mScope = mContext.initStandardObjects();
	}
	
	void processElement()
	throws EngineException
	{
		Object wrappedOut = Context.javaToJS(getElement(), mScope);
		ScriptableObject.putProperty(mScope, "element", wrappedOut);
		try
		{
			mContext.evaluateString(mScope, mCode, getElement().getElementInfo().getImplementation(), 1, null);
		}
		catch (WrappedException e)
		{
			if (e.getWrappedException() instanceof LightweightError)
			{
				throw (LightweightError)e.getWrappedException();
			}
			else if (e.getWrappedException() instanceof EngineException)
			{
				throw (EngineException)e.getWrappedException();
			}
			
			throw new ScriptErrorException(getElement().getElementInfo().getDeclarationName(), e);
		}
		finally
		{
			Context.exit();
		}
	}
	
	boolean childTriggered(String name, String[] values)
	{
		Object function_object = mScope.get("childTriggered", mScope);
		if (!(function_object instanceof Function))
		{
			throw new ChildTriggerNotImplementedException(getElement().getDeclarationName(), name);
		}
		else
		{
			Function function = (Function)function_object;
			try
			{
				Object result_object = function.call(mContext, mScope, mScope, new Object[] {name, values});
				String result = Context.toString(result_object);
				return Convert.toBoolean(result, false);
			}
			catch (WrappedException e)
			{
				if (e.getWrappedException() instanceof LightweightError)
				{
					throw (LightweightError)e.getWrappedException();
				}
				else if (e.getWrappedException() instanceof EngineException)
				{
					throw (EngineException)e.getWrappedException();
				}
				
				throw new ScriptErrorException(getElement().getElementInfo().getDeclarationName(), e);
			}
		}
	}
}

