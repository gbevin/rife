/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

import java.io.ByteArrayInputStream;
import java.io.File;

public abstract class InstrumentationUtils
{
    public static final String PROPERTY_RIFE_INSTRUMENTATION_DUMP = "rife.instrumentation.dump";

    public static void dumpClassBytes(String type, String classname, byte[] bytes)
    {
        boolean write_to_disk = (System.getProperty(PROPERTY_RIFE_INSTRUMENTATION_DUMP) != null);
        if (write_to_disk)
        {
            String user_home = System.getProperty("user.home");
            String file_out_name = user_home + File.separatorChar + "rife_instrumentation_" + type + File.separatorChar + classname.replace('.', File.separatorChar) + ".class";
            String dir_out_name = file_out_name.substring(0, file_out_name.lastIndexOf(File.separatorChar));
            // ensure that all the parent dirs are present
            new File(dir_out_name).mkdirs();
            File file_out = new File(file_out_name);
            try
            {
                System.out.println("Dumping " + type + " resource: " + file_out.getAbsolutePath());
                FileUtils.copy(new ByteArrayInputStream(bytes), file_out);
            }
            catch (FileUtilsErrorException e)
            {
                e.printStackTrace();
            }
        }
    }
}