/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ScriptedEngineBSF.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.ScriptErrorException;
import com.uwyn.rife.tools.exceptions.LightweightError;
import java.lang.reflect.InvocationTargetException;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

class ScriptedEngineBSF extends ScriptedEngine
{
	protected BSFManager	mManager = null;
	protected BSFEngine		mEngine = null;

	ScriptedEngineBSF(String language, String code)
	throws EngineException
	{
		super(language, code);

		mManager = new BSFManager();
		try
		{
			mEngine = mManager.loadScriptingEngine(mLanguage);
		}
		catch (BSFException e)
		{
			e.fillInStackTrace();
			throw new EngineException(e);
		}
	}
	
	void processElement()
	{
		try
		{
			mManager.declareBean("element", getElement(), ElementScripted.class);
			mManager.exec(mLanguage, getElement().getElementInfo().getImplementation(), 0, 0, mCode);
			mManager.undeclareBean("element");
			mEngine = null;
			mManager.terminate();
			mManager = null;
		}
		catch (BSFException e)
		{
			extractEngineException(e);
		}
	}
	
	boolean childTriggered(String name, String[] values)
	{
		try
		{
			Object result = null;
			
			result = mEngine.call(null, "childTriggered", new Object[] {name, values});
			
			if (result != null &&
				(result.toString().equals("true") ||
				 result.toString().equals("1")))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (BSFException e)
		{
			extractEngineException(e);
			return false;
		}
		catch (Throwable e)
		{
			return false;
		}
	}
	
	private void extractEngineException(BSFException e)
	throws EngineException
	{
		if (e.getTargetException() != null)
		{
			// Rhino and Pnuts
			if (e.getTargetException() instanceof LightweightError)
			{
				throw (LightweightError)e.getTargetException();
			}
			else if (e.getTargetException() instanceof EngineException)
			{
				throw (EngineException)e.getTargetException();
			}
			
			// Jython
			// using string comparison to make it possible for scripting engines to be pluggable
			if (e.getTargetException().getClass().getName().equals("org.python.core.PyException"))
			{
				ElementJythonExceptionHandler python_handler = new ElementJythonExceptionHandler();
				python_handler.execute(e);
			}

			// JRuby
			// using string comparison to make it possible for scripting engines to be pluggable
			if (e.getTargetException().getClass().getName().equals("org.jruby.exceptions.RaiseException") &&
				e.getTargetException().getCause() != null)
			{
				if (e.getTargetException().getCause() instanceof EngineException)
				{
					throw (EngineException)e.getTargetException().getCause();
				}
				else if (e.getTargetException().getCause() instanceof LightweightError)
				{
					throw (LightweightError)e.getTargetException().getCause();
				}
			}
			
			// Jacl
			// using string comparison to make it possible for scripting engines to be pluggable
			if (e.getTargetException().getClass().getName().equals("tcl.lang.ReflectException") &&
				e.getTargetException().getCause() != null)
			{
				Throwable cause = null;
				
				// Jacl 1.2.6
				if (e.getTargetException().getCause() instanceof InvocationTargetException &&
					e.getTargetException().getCause().getCause() != null)
				{
					cause = e.getTargetException().getCause().getCause();
				}
				// Jacl 1.3.3
				else
				{
					cause = e.getTargetException().getCause();
				}

				if (cause != null)
				{
					if (cause instanceof EngineException)
					{
						throw (EngineException)cause;
					}
					else if (cause instanceof LightweightError)
					{
						throw (LightweightError)cause;
					}
				}
			}
			
			// Beanshell
			// using string comparison to make it possible for scripting engines to be pluggable
			if (e.getTargetException().getClass().getName().equals("bsh.TargetError") &&
				((bsh.TargetError)e.getTargetException()).getTarget() != null)
			{
				if (((bsh.TargetError)e.getTargetException()).getTarget() instanceof EngineException)
				{
					throw (EngineException)((bsh.TargetError)e.getTargetException()).getTarget();
				}
				else if (((bsh.TargetError)e.getTargetException()).getTarget() instanceof LightweightError)
				{
					throw (LightweightError)((bsh.TargetError)e.getTargetException()).getTarget();
				}
			}
		}
		
		throw new ScriptErrorException(getElement().getElementInfo().getDeclarationName(), e);
	}
	
	private class ElementJythonExceptionHandler
	{
		private void execute(BSFException e)
		{
			org.python.core.PyException python_exception = (org.python.core.PyException)e.getTargetException();
			try
			{
				Object converted;
				
				converted = python_exception.value.__tojava__(EngineException.class);
				if (converted != org.python.core.Py.NoConversion)
				{
					throw (EngineException)converted;
				}
				
				converted = python_exception.value.__tojava__(LightweightError.class);
				if (converted != org.python.core.Py.NoConversion)
				{
					throw (LightweightError)converted;
				}
				
				throw new ScriptErrorException(getElement().getElementInfo().getDeclarationName(), e);
			}
			catch (ClassCastException e2)
			{
				throw new EngineException(e);
			}
		}
	}
}

