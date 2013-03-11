/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.config.RifeConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ConciseLogFormatter extends Formatter
{
    private static SimpleDateFormat DATE_FORMAT = null;

    static
    {
        DATE_FORMAT = new SimpleDateFormat("yyyyMMdd HHmm");
        DATE_FORMAT.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
    }

    // Line separator string.  This is the value of the line.separator
    // property at the moment that the ConciseLogFormatter was created.
    private String lineSeparator = (String)java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction()
            {
                public Object run()
                {
                    return System.getProperty("line.separator");
                }
            });

    /**
     * Format the given LogRecord.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public String format(LogRecord record)
    {
        StringBuilder sb = new StringBuilder();
        String message = formatMessage(record);
        sb.append(DATE_FORMAT.format(new Date()));
        sb.append(" ");
        sb.append(record.getLevel());
        sb.append(" ");
        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null)
        {
            try
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            }
            catch (Exception ignored)
            {
            }
        }
        return sb.toString();
    }
}

