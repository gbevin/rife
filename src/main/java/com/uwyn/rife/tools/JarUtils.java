/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public abstract class JarUtils
{
    public static Collection<String> getFileList(File file)
    {
        return getFileList(file, true);
    }

    private static ArrayList<String> getFileList(File file, boolean root)
    {
        ArrayList<String> filelist = new ArrayList<>();
        if (file.isDirectory())
        {
            String[] list = file.list();
            for (String filename : list)
            {
                File next_file = new File(file.getAbsolutePath() + File.separator + filename);
                ArrayList<String> dir = getFileList(next_file, false);

                for (String file_name : dir)
                {
                    if (!root)
                    {
                        file_name = file.getName() + File.separator + file_name;
                    }

                    int filelist_size = filelist.size();
                    for (int j = 0; j < filelist_size; j++)
                    {
                        if ((filelist.get(j)).compareTo(file_name) > 0)
                        {
                            filelist.add(j, file_name);
                            break;
                        }
                    }
                    if (filelist_size == filelist.size())
                    {
                        filelist.add(file_name);
                    }
                }
            }
        }
        else if (file.isFile())
        {
            filelist.add(file.getName());
        }

        return filelist;
    }
}
