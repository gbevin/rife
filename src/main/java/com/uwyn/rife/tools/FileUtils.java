/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class FileUtils
{
    public static ArrayList<String> getFileList(File file)
    {
        return getFileList(file, null, null, true);
    }

    public static ArrayList<String> getFileList(File file, Pattern included, Pattern excluded)
    {
        return getFileList(file, new Pattern[]{included}, new Pattern[]{excluded}, true);
    }

    public static ArrayList<String> getFileList(File file, Pattern[] included, Pattern[] excluded)
    {
        return getFileList(file, included, excluded, true);
    }

    private static ArrayList<String> getFileList(File file, Pattern[] included, Pattern[] excluded, boolean root)
    {
        if (null == file)
        {
            return new ArrayList<>();
        }

        ArrayList<String> filelist = new ArrayList<>();
        if (file.isDirectory())
        {
            String[] list = file.list();
            if (null != list)
            {
                for (String list_entry : list)
                {
                    File next_file = new File(file.getAbsolutePath() + File.separator + list_entry);
                    ArrayList<String> dir = getFileList(next_file, included, excluded, false);

                    for (String file_name : dir)
                    {
                        if (root)
                        {
                            // if the file is not accepted, don't process it further
                            if (!StringUtils.filter(file_name, included, excluded))
                            {
                                continue;
                            }

                        }
                        else
                        {
                            file_name = file.getName() + File.separator + file_name;
                        }

                        int filelist_size = filelist.size();
                        for (int j = 0; j < filelist_size; j++)
                        {
                            if (filelist.get(j).compareTo(file_name) > 0)
                            {
                                filelist.add(j, file_name);
                                break;
                            }
                        }
                        if (filelist.size() == filelist_size)
                        {
                            filelist.add(file_name);
                        }
                    }
                }
            }
        }
        else if (file.isFile())
        {
            String file_name = file.getName();

            if (root)
            {
                if (StringUtils.filter(file_name, included, excluded))
                {
                    filelist.add(file_name);
                }
            }
            else
            {
                filelist.add(file_name);
            }
        }

        return filelist;
    }

    public static void moveFile(File source, File target)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");
        if (null == target) throw new IllegalArgumentException("target can't be null.");

        if (!source.exists())
        {
            throw new FileUtilsErrorException("The source file '" + source.getAbsolutePath() + "' does not exist.");
        }

        // copy
        copy(source, target);

        // then delete sourcefile
        deleteFile(source);
    }

    public static void moveDirectory(File source, File target)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");
        if (null == target) throw new IllegalArgumentException("target can't be null.");

        if (!source.exists())
        {
            throw new FileUtilsErrorException("The source directory '" + source.getAbsolutePath() + "' does not exist.");
        }

        // Create target if it does not exist already
        if (!target.exists())
        {
            if (!target.mkdirs())
            {
                throw new FileUtilsErrorException("The target directory '" + target.getAbsolutePath() + "' couldn't be created.");
            }
        }

        String[] filelist = source.list();

        for (String filelist_element : filelist)
        {
            File current_file = new File(source.getAbsolutePath() + File.separator + filelist_element);
            File target_file = new File(target.getAbsolutePath() + File.separator + filelist_element);

            if (current_file.isDirectory())
            {
                moveDirectory(current_file, target_file);
            }
            else
            {
                moveFile(current_file, target_file);
            }
        }

        // If we get here it means we're finished with this directory ... delete it.
        deleteFile(source);
    }

    public static void deleteDirectory(File source)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");

        if (!source.exists())
        {
            throw new FileUtilsErrorException("The directory '" + source.getAbsolutePath() + "' does not exist");
        }

        String[] filelist = source.list();

        for (String filelist_element : filelist)
        {
            File current_file = new File(source.getAbsolutePath() + File.separator + filelist_element);

            if (current_file.isDirectory())
            {
                deleteDirectory(current_file);
            }
            else
            {
                deleteFile(current_file);
            }
        }

        // If we get here it means we're finished with this directory ... delete it.
        deleteFile(source);
    }

    public static void copy(InputStream inputStream, OutputStream outputStream)
    throws FileUtilsErrorException
    {
        if (null == inputStream) throw new IllegalArgumentException("inputStream can't be null.");
        if (null == outputStream) throw new IllegalArgumentException("outputStream can't be null.");

        try
        {
            byte[] buffer = new byte[1024];
            int return_value = inputStream.read(buffer);

            while (-1 != return_value)
            {
                outputStream.write(buffer, 0, return_value);
                return_value = inputStream.read(buffer);
            }
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error during the copying of streams.", e);
        }
    }

    public static void copy(InputStream inputStream, File target)
    throws FileUtilsErrorException
    {
        if (null == inputStream) throw new IllegalArgumentException("inputStream can't be null.");
        if (null == target) throw new IllegalArgumentException("target can't be null.");

        try (FileOutputStream file_output_stream = new FileOutputStream(target))
        {
            copy(inputStream, file_output_stream);
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while copying an input stream to file '" + target.getAbsolutePath() + "'.", e);
        }
    }

    public static void copy(File source, OutputStream outputStream)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");
        if (null == outputStream) throw new IllegalArgumentException("outputStream can't be null.");

        try (FileInputStream file_input_stream = new FileInputStream(source))
        {
            copy(file_input_stream, outputStream);
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while copying file '" + source.getAbsolutePath() + "' to an output stream.", e);
        }
    }

    public static void copy(File source, File target)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");
        if (null == target) throw new IllegalArgumentException("target can't be null.");

        try (FileInputStream file_input_stream = new FileInputStream(source);
             FileOutputStream file_output_stream = new FileOutputStream(target))
        {

            copy(file_input_stream, file_output_stream);
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while copying file '" + source.getAbsolutePath() + "' to file '" + target.getAbsolutePath() + "'.", e);
        }
    }

    public static ByteArrayOutputStream readStream(InputStream inputStream)
    throws FileUtilsErrorException
    {
        if (null == inputStream) throw new IllegalArgumentException("inputStream can't be null.");

        byte[] buffer = new byte[1024];
        try (ByteArrayOutputStream output_stream = new ByteArrayOutputStream(buffer.length);
             InputStream input = inputStream)
        {
            int return_value = input.read(buffer);

            while (-1 != return_value)
            {
                output_stream.write(buffer, 0, return_value);
                return_value = input.read(buffer);
            }

            return output_stream;
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while reading the complete contents of an input stream.", e);
        }
    }

    public static String readString(InputStream inputStream)
    throws FileUtilsErrorException
    {
        if (null == inputStream) throw new IllegalArgumentException("inputStream can't be null.");

        return readStream(inputStream).toString();
    }

    public static String readString(Reader reader)
    throws FileUtilsErrorException
    {
        if (null == reader) throw new IllegalArgumentException("reader can't be null.");

        try
        {
            char[] buffer = new char[1024];
            StringBuilder result = new StringBuilder();

            int size = reader.read(buffer);
            while (size != -1)
            {
                result.append(buffer, 0, size);
                size = reader.read(buffer);
            }

            return result.toString();
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while reading the complete contents of an reader.", e);
        }
    }

    public static String readString(InputStream inputStream, String encoding)
    throws FileUtilsErrorException
    {
        if (null == inputStream) throw new IllegalArgumentException("inputStream can't be null.");

        try
        {
            return readStream(inputStream).toString(encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new FileUtilsErrorException("Encoding '" + encoding + "' is not supported.", e);
        }
    }

    public static byte[] readBytes(InputStream inputStream)
    throws FileUtilsErrorException
    {
        if (null == inputStream) throw new IllegalArgumentException("inputStream can't be null.");

        return readStream(inputStream).toByteArray();
    }

    public static String readString(URL source)
    throws FileUtilsErrorException
    {
        return readString(source, null);
    }

    public static String readString(URL source, String encoding)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");

        try
        {
            URLConnection connection = source.openConnection();
            connection.setUseCaches(false);
            try (InputStream input_stream = connection.getInputStream())
            {
                String content;
                if (null == encoding)
                {
                    content = readString(input_stream);
                }
                else
                {
                    content = readString(input_stream, encoding);
                }
                return content;
            }
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while reading url '" + source.toString() + ".", e);
        }
    }

    public static byte[] readBytes(URL source)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");

        try
        {
            URLConnection connection = source.openConnection();
            connection.setUseCaches(false);
            try (InputStream input_stream = connection.getInputStream())
            {
                return readBytes(input_stream);
            }
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while reading url '" + source.toString() + ".", e);
        }
    }

    public static String readString(File source)
    throws FileUtilsErrorException
    {
        return readString(source, null);
    }

    public static String readString(File source, String encoding)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");

        try (FileInputStream file_input_stream = new FileInputStream(source))
        {
            String content;
            if (null == encoding)
            {
                content = readString(file_input_stream);
            }
            else
            {
                content = readString(file_input_stream, encoding);
            }
            return content;
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while reading url '" + source.getAbsolutePath() + ".", e);
        }
    }

    public static byte[] readBytes(File source)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");

        try (FileInputStream file_input_stream = new FileInputStream(source))
        {
            return readBytes(file_input_stream);
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while reading file '" + source.getAbsolutePath() + ".", e);
        }
    }

    public static void writeBytes(byte[] content, File destination)
    throws FileUtilsErrorException
    {
        if (null == content) throw new IllegalArgumentException("content can't be null.");
        if (null == destination) throw new IllegalArgumentException("destination can't be null.");

        try (FileOutputStream file_output_stream = new FileOutputStream(destination))
        {
            file_output_stream.write(content);
            file_output_stream.flush();
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while write a string to '" + destination.getAbsolutePath() + ".", e);
        }
    }

    public static void writeString(String content, File destination)
    throws FileUtilsErrorException
    {
        if (null == content) throw new IllegalArgumentException("content can't be null.");
        if (null == destination) throw new IllegalArgumentException("destination can't be null.");

        try (FileWriter file_writer = new FileWriter(destination))
        {
            file_writer.write(content, 0, content.length());
            file_writer.flush();
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while write a string to '" + destination.getAbsolutePath() + ".", e);
        }
    }

    public static String convertPathToSystemSeperator(String path)
    {
        if (null == path) throw new IllegalArgumentException("path can't be null.");

        List<String> path_parts = StringUtils.split(path, "/");
        return StringUtils.join(path_parts, File.separator);
    }

    public static void deleteFile(File file)
    {
        if (null == file) throw new IllegalArgumentException("file can't be null.");

        file.delete();
    }

    public static String getUniqueFilename()
    {
        Date current_date = new Date();

        return current_date.getTime() + "-" + (long)(1000000 * Math.random());
    }

    public static void unzipFile(File source, File destination)
    throws FileUtilsErrorException
    {
        if (null == source) throw new IllegalArgumentException("source can't be null.");
        if (null == destination) throw new IllegalArgumentException("destination can't be null.");

        Enumeration entries;
        try (ZipFile zip_file = new ZipFile(source))
        {
            entries = zip_file.entries();

            while (entries.hasMoreElements())
            {
                ZipEntry entry;
                String output_filename;
                File output_file;
                StringBuilder output_file_directoryname;
                File output_file_directory;
                byte[] buffer = new byte[1024];
                int return_value;

                entry = (ZipEntry)entries.nextElement();
                try (InputStream input_stream = zip_file.getInputStream(entry))
                {
                    output_filename = destination.getAbsolutePath() + File.separator + entry.getName().replace('/', File.separatorChar);
                    output_file = new File(output_filename);
                    output_file_directoryname = new StringBuilder(output_file.getPath());
                    output_file_directoryname.setLength(output_file_directoryname.length() - output_file.getName().length() - File.separator.length());
                    output_file_directory = new File(output_file_directoryname.toString());
                    if (!output_file_directory.exists())
                    {
                        if (!output_file_directory.mkdirs())
                        {
                            throw new FileUtilsErrorException("Couldn't create directory '" + output_file_directory.getAbsolutePath() + "' and its parents.");
                        }
                    }
                    else
                    {
                        if (!output_file_directory.isDirectory())
                        {
                            throw new FileUtilsErrorException("Destination '" + output_file_directory.getAbsolutePath() + "' exists and is not a directory.");
                        }
                    }

                    try (FileOutputStream file_output_stream = new FileOutputStream(output_filename))
                    {
                        try
                        {
                            return_value = input_stream.read(buffer);

                            while (-1 != return_value)
                            {
                                file_output_stream.write(buffer, 0, return_value);
                                return_value = input_stream.read(buffer);
                            }
                        }
                        catch (IOException e)
                        {
                            throw new FileUtilsErrorException("Error while uncompressing entry '" + output_filename + "'.", e);
                        }
                    }
                    catch (IOException e)
                    {
                        throw new FileUtilsErrorException("Error while creating the output stream for file '" + output_filename + "'.", e);
                    }
                }
                catch (IOException e)
                {
                    throw new FileUtilsErrorException("Error while obtaining the inputstream for entry '" + entry.getName() + "'.", e);
                }
            }
        }
        catch (IOException e)
        {
            throw new FileUtilsErrorException("Error while creating the zipfile '" + source.getAbsolutePath() + "'.", e);
        }
    }

    public static String getBaseName(File file)
    {
        return getBaseName(file.getName());
    }

    public static String getBaseName(String fileName)
    {
        if (null == fileName) throw new IllegalArgumentException("fileName can't be null.");

        String basename = null;

        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1)
        {
            basename = fileName.substring(0, index);
        }

        return basename;
    }

    public static String getExtension(File file)
    {
        return getExtension(file.getName());
    }

    public static String getExtension(String fileName)
    {
        if (null == fileName) throw new IllegalArgumentException("fileName can't be null.");

        String ext = null;

        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1)
        {
            ext = fileName.substring(index + 1).toLowerCase();
        }

        return ext;
    }
}
