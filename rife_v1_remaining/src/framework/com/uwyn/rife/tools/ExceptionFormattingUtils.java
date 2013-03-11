/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExceptionFormattingUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.template.Template;

public abstract class ExceptionFormattingUtils
{
	private static final int	DEFAULT_STACKTRACELIMIT = 15;
	private static final int	DEFAULT_BUFFERLIMIT = 300000;
	
	public static String formatExceptionStackTrace(Throwable exception, Template template)
	{
		return formatExceptionStackTrace(exception, template, DEFAULT_STACKTRACELIMIT, DEFAULT_BUFFERLIMIT);
	}
	
	public static String formatExceptionStackTrace(Throwable exception, Template template, int stacktraceLimit)
	{
		return formatExceptionStackTrace(exception, template, stacktraceLimit, DEFAULT_BUFFERLIMIT);
	}
	
	public static String formatExceptionStackTrace(Throwable exception, Template template, int stacktraceLimit, int bufferLimit)
	{
		if (null == exception)		throw new IllegalArgumentException("exception can't be null.");
		if (null == template)		throw new IllegalArgumentException("template can't be null.");
		if (stacktraceLimit <= 0)	throw new IllegalArgumentException("stacktraceLimit has to be bigger than 0.");
		if (bufferLimit <= 0)		throw new IllegalArgumentException("bufferLimit has to be bigger than 0.");
		
		String	exception_name = null;
		String	message = null;
		String	class_name = null;
		String	method_name = null;
		String	file_name = null;
		
		StringBuilder exceptions = new StringBuilder();
		while (exception != null)
		{
			exception_name = exception.getClass().getName();
			message = exception.getMessage();
			if (null == exception_name)
			{
				// this should never happen
				exception_name = "<unknown exception name>";
			}
			if (null == message)
			{
				message = "<no message>";
			}
			template.setValue("exception_class_name", template.getEncoder().encode(exception_name));
			template.setValue("exception_message", template.getEncoder().encode(message));
			
			if (template.hasValueId("exception_stack_trace"))
			{
				StackTraceElement[]	stack_trace = exception.getStackTrace();
				StringBuilder		stack_trace_out = new StringBuilder();
				StringBuilder		stack_trace_details = null;
				for (int i = 0; i < stack_trace.length; i++)
				{
					class_name = stack_trace[i].getClassName();
					method_name = stack_trace[i].getMethodName();
					if (null == class_name)
					{
						// this should never happen either
						class_name = "<unknown class>";
					}
					if (null == class_name)
					{
						// this should never happen either
						method_name = "<unknown method>";
					}
					template.setValue("class_name", template.getEncoder().encode(class_name));
					template.setValue("method_name", template.getEncoder().encode(method_name));
					stack_trace_details = new StringBuilder();
					file_name = stack_trace[i].getFileName();
					if (null == file_name)
					{
						file_name =  "<unknown>";
					}
					template.setValue("file_name", template.getEncoder().encode(file_name));
					stack_trace_details.append(template.getBlock("file_name"));
					if (stack_trace[i].getLineNumber() > 0)
					{
						template.setValue("line_number", stack_trace[i].getLineNumber());
						stack_trace_details.append(template.getBlock("line_number"));
					}
					template.setValue("details", stack_trace_details.toString());
					
					stack_trace_out.append(template.getBlock("stack_trace_line"));
				
					if (i > stacktraceLimit)
					{
						template.setValue("count", stack_trace.length-1-i);
						stack_trace_out.append(template.getBlock("more_stack_trace"));
						break;
					}
					else if (exceptions.length()+stack_trace_out.length() > bufferLimit)
					{
						template.setValue("count", stack_trace.length-1-i);
						stack_trace_out.append(template.getBlock("more_stack_trace"));
						exception = null;
						break;
					}
				}
			
				template.setValue("exception_stack_trace", stack_trace_out.toString());
			}
			
			exceptions.append(template.getBlock("exception"));
			
			if (template.hasBlock("more_exceptions") &&
				exceptions.length() > bufferLimit)
			{
				exceptions.append(template.getBlock("more_exceptions"));
				exception = null;
				break;
			}
			
			if (exception != null)
			{
				exception = exception.getCause();
			}
		}

		return exceptions.toString();
	}
}

