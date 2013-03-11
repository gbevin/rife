/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

public class UniqueID
{
    private byte[] id = null;
    private String idString = null;

    UniqueID(byte[] id)
    {
        setID(id);
    }

    public byte[] getID()
    {
        return id;
    }

    void setID(byte[] id)
    {
        this.id = id;
        idString = null;
    }

    public String toString()
    {
        if (null == idString)
        {
            StringBuilder string_id = new StringBuilder();
            int byterange = Math.abs(Byte.MAX_VALUE) + Math.abs(Byte.MIN_VALUE) + 1;
            int maxhexdigitsperbyte = (Integer.toHexString(byterange - 1)).length();
            String hexadecimal;
            for (int decimal : id)
            {
                if (decimal < 0)
                {
                    decimal = byterange + decimal;
                }

                hexadecimal = Integer.toHexString(decimal);
                for (int j = hexadecimal.length(); j < maxhexdigitsperbyte; j++)
                {
                    string_id.append('0');
                }
                string_id.append(hexadecimal);
            }
            idString = string_id.toString();
        }

        return idString;
    }
}
