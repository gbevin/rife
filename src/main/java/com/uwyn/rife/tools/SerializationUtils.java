/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.tools.exceptions.DeserializationErrorException;
import com.uwyn.rife.tools.exceptions.SerializationErrorException;
import com.uwyn.rife.tools.exceptions.SerializationUtilsErrorException;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class SerializationUtils
{
    public static <TargetType extends Serializable> TargetType deserializeFromString(String value)
    throws SerializationUtilsErrorException
    {
        if (null == value)
        {
            return null;
        }

        byte[] value_bytes_decoded = Base64.decode(value);
        if (null == value_bytes_decoded)
        {
            throw new DeserializationErrorException(null);
        }

        ByteArrayInputStream bytes_is = new ByteArrayInputStream(value_bytes_decoded);
        GZIPInputStream gzip_is;
        ObjectInputStream object_is;
        try
        {
            gzip_is = new GZIPInputStream(bytes_is);
            object_is = new ObjectInputStream(gzip_is);
            return (TargetType)object_is.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new DeserializationErrorException(e);
        }
    }

    public static String serializeToString(Serializable value)
    throws SerializationUtilsErrorException
    {
        if (null == value) throw new IllegalArgumentException("value can't be null.");

        ByteArrayOutputStream byte_os = new ByteArrayOutputStream();
        GZIPOutputStream gzip_os;
        ObjectOutputStream object_os;
        try
        {
            gzip_os = new GZIPOutputStream(byte_os);
            object_os = new ObjectOutputStream(gzip_os);
            object_os.writeObject(value);
            object_os.flush();
            gzip_os.flush();
            gzip_os.finish();
        }
        catch (IOException e)
        {
            throw new SerializationErrorException(value, e);
        }

        byte[] value_bytes_decoded = byte_os.toByteArray();

        return Base64.encodeToString(value_bytes_decoded, false);
    }
}


