/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class ExceptionUtils
{
    public static String getExceptionStackTrace(Throwable exception)
    {
        if (null == exception) throw new IllegalArgumentException("exception can't be null;");

        String stack_trace;

        StringWriter string_writer = new StringWriter();
        try (PrintWriter print_writer = new PrintWriter(string_writer))
        {
            exception.printStackTrace(print_writer);
            stack_trace = string_writer.getBuffer().toString();
        }

        return stack_trace;
    }

    public static String getExceptionStackTraceMessages(Throwable exception)
    {
        if (null == exception) throw new IllegalArgumentException("exception can't be null;");

        StringBuilder messages = new StringBuilder();
        Throwable t = exception;
        while (t != null)
        {
            if (messages.length() > 0)
            {
                messages.append("; ");
            }
            messages.append(StringUtils.replace(t.getMessage(), "\n", ""));

            if (t == t.getCause())
            {
                break;
            }
            t = t.getCause();
        }

        return messages.toString();
    }
}

