/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * Obfuscation code Copyright (c) 1998 Mort Bay Consulting (Australia) Pty. Ltd.
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.datastructures.EnumClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringEncryptor extends EnumClass<String>
{
    private static final String IDENTIFIER_HEX_SUFFIX = "HEX";
    public static final String IDENTIFIER_OBF = "OBF";
    public static final String IDENTIFIER_MD5 = "MD5";
    public static final String IDENTIFIER_MD5HEX = IDENTIFIER_MD5 + IDENTIFIER_HEX_SUFFIX;
    public static final String IDENTIFIER_SHA = "SHA";
    public static final String IDENTIFIER_SHAHEX = IDENTIFIER_SHA + IDENTIFIER_HEX_SUFFIX;
    public static final String IDENTIFIER_WHIRLPOOL = "WRP";
    public static final String IDENTIFIER_WHIRLPOOLHEX = IDENTIFIER_WHIRLPOOL + IDENTIFIER_HEX_SUFFIX;
    private static final String PREFIX_SEPERATOR_SUFFIX = ":";
    private static final String PREFIX_OBF = IDENTIFIER_OBF + PREFIX_SEPERATOR_SUFFIX;
    private static final String PREFIX_MD5 = IDENTIFIER_MD5 + PREFIX_SEPERATOR_SUFFIX;
    private static final String PREFIX_MD5HEX = IDENTIFIER_MD5HEX + PREFIX_SEPERATOR_SUFFIX;
    private static final String PREFIX_SHA = IDENTIFIER_SHA + PREFIX_SEPERATOR_SUFFIX;
    private static final String PREFIX_SHAHEX = IDENTIFIER_SHAHEX + PREFIX_SEPERATOR_SUFFIX;
    private static final String PREFIX_WHIRLPOOL = IDENTIFIER_WHIRLPOOL + PREFIX_SEPERATOR_SUFFIX;
    private static final String PREFIX_WHIRLPOOLHEX = IDENTIFIER_WHIRLPOOLHEX + PREFIX_SEPERATOR_SUFFIX;
    public static final StringEncryptor OBF = new StringEncryptor(PREFIX_OBF);
    public static final StringEncryptor MD5 = new StringEncryptor(PREFIX_MD5);
    public static final StringEncryptor MD5HEX = new StringEncryptor(PREFIX_MD5HEX);
    public static final StringEncryptor SHA = new StringEncryptor(PREFIX_SHA);
    public static final StringEncryptor SHAHEX = new StringEncryptor(PREFIX_SHAHEX);
    public static final StringEncryptor WHIRLPOOL = new StringEncryptor(PREFIX_WHIRLPOOL);
    public static final StringEncryptor WHIRLPOOLHEX = new StringEncryptor(PREFIX_WHIRLPOOLHEX);

    private StringEncryptor(String identifier)
    {
        super(identifier);
    }

    public static StringEncryptor getEncryptor(String identifier)
    {
        return getMember(StringEncryptor.class, identifier + PREFIX_SEPERATOR_SUFFIX);
    }

    private static String encodeBase64(byte[] bytes)
    {
        return Base64.encodeToString(bytes, false);
    }

    private static String encodeHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static String autoEncrypt(String value)
    throws NoSuchAlgorithmException
    {
        if (null == value) throw new IllegalArgumentException("value can't be null");

        if (value.startsWith(PREFIX_OBF))
        {
            return PREFIX_OBF + obfuscate(value.substring(PREFIX_OBF.length()));
        }
        else
        {
            boolean encode_base64;
            String prefix;
            byte[] bytes;
            if (value.startsWith(PREFIX_SHA) || value.startsWith(PREFIX_SHAHEX))
            {
                if (value.startsWith(PREFIX_SHA))
                {
                    prefix = PREFIX_SHA;
                    encode_base64 = true;
                }
                else
                {
                    prefix = PREFIX_SHAHEX;
                    encode_base64 = false;
                }
                MessageDigest digest = MessageDigest.getInstance("SHA");
                digest.update(value.substring(prefix.length()).getBytes());
                bytes = digest.digest();
            }
            else if (value.startsWith(PREFIX_WHIRLPOOL) || value.startsWith(PREFIX_WHIRLPOOLHEX))
            {
                if (value.startsWith(PREFIX_WHIRLPOOL))
                {
                    prefix = PREFIX_WHIRLPOOL;
                    encode_base64 = true;
                }
                else
                {
                    prefix = PREFIX_WHIRLPOOLHEX;
                    encode_base64 = false;
                }
                Whirlpool whirlpool = new Whirlpool();
                whirlpool.NESSIEinit();
                whirlpool.NESSIEadd(value.substring(prefix.length()));
                byte[] digest = new byte[Whirlpool.DIGESTBYTES];
                whirlpool.NESSIEfinalize(digest);
                bytes = digest;
            }
            else if (value.startsWith(PREFIX_MD5) || value.startsWith(PREFIX_MD5HEX))
            {
                if (value.startsWith(PREFIX_MD5))
                {
                    prefix = PREFIX_MD5;
                    encode_base64 = true;
                }
                else
                {
                    prefix = PREFIX_MD5HEX;
                    encode_base64 = false;
                }
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(value.substring(prefix.length()).getBytes());
                bytes = digest.digest();
            }
            else
            {
                return value;
            }

            if (encode_base64)
            {
                value = prefix + encodeBase64(bytes);
            }
            else
            {
                value = prefix + encodeHex(bytes);
            }
        }

        return value;
    }

    public static boolean matches(String checkedValue, String encryptedValue)
    throws NoSuchAlgorithmException
    {
        if (null == checkedValue) throw new IllegalArgumentException("checkedValue can't be null");
        if (null == encryptedValue) throw new IllegalArgumentException("encryptedValue can't be null");

        return encryptedValue.equals(adaptiveEncrypt(checkedValue, encryptedValue));
    }

    public static String adaptiveEncrypt(String clearValue, String encryptedValue)
    throws NoSuchAlgorithmException
    {
        if (null == clearValue) throw new IllegalArgumentException("clearValue can't be null");
        if (null == encryptedValue) throw new IllegalArgumentException("encryptedValue can't be null");

        if (encryptedValue.startsWith(PREFIX_OBF))
        {
            clearValue = PREFIX_OBF + clearValue;
        }
        else if (encryptedValue.startsWith(PREFIX_WHIRLPOOL))
        {
            clearValue = PREFIX_WHIRLPOOL + clearValue;
        }
        else if (encryptedValue.startsWith(PREFIX_WHIRLPOOLHEX))
        {
            clearValue = PREFIX_WHIRLPOOLHEX + clearValue;
        }
        else if (encryptedValue.startsWith(PREFIX_MD5))
        {
            clearValue = PREFIX_MD5 + clearValue;
        }
        else if (encryptedValue.startsWith(PREFIX_MD5HEX))
        {
            clearValue = PREFIX_MD5HEX + clearValue;
        }
        else if (encryptedValue.startsWith(PREFIX_SHA))
        {
            clearValue = PREFIX_SHA + clearValue;
        }
        else if (encryptedValue.startsWith(PREFIX_SHAHEX))
        {
            clearValue = PREFIX_SHAHEX + clearValue;
        }

        return autoEncrypt(clearValue);
    }

    public static String obfuscate(String value)
    {
        if (null == value) throw new IllegalArgumentException("value can't be null");

        StringBuilder buffer = new StringBuilder();
        byte[] bytes = value.getBytes();
        for (int i = 0; i < bytes.length; i++)
        {
            byte b1 = bytes[i];
            byte b2 = bytes[value.length() - (i + 1)];
            int i1 = (int)b1 + (int)b2 + 127;
            int i2 = (int)b1 - (int)b2 + 127;
            int i0 = i1 * 256 + i2;
            String x = Integer.toString(i0, 36);

            switch (x.length())
            {
                case 1: buffer.append('0');
                case 2: buffer.append('0');
                case 3: buffer.append('0');
                default: buffer.append(x);
            }
        }

        return buffer.toString();
    }

    public static String deobfuscate(String value)
    {
        if (null == value) throw new IllegalArgumentException("value can't be null");

        if (value.startsWith(PREFIX_OBF))
        {
            value = value.substring(PREFIX_OBF.length());
        }

        byte[] bytes = new byte[value.length() / 2];
        int l = 0;

        for (int i = 0; i < value.length(); i += 4)
        {
            String x = value.substring(i, i + 4);
            int i0 = Integer.parseInt(x, 36);
            int i1 = (i0 / 256);
            int i2 = (i0 % 256);
            bytes[l++] = (byte)((i1 + i2 - 254) / 2);
        }

        return new String(bytes, 0, l);
    }

    public static void main(String[] arguments)
    {
        boolean valid_arguments = true;
        if (arguments.length < 1 ||
            arguments.length > 3)
        {
            valid_arguments = false;
        }
        else if (!arguments[0].startsWith("-"))
        {
            if (arguments.length > 1)
            {
                valid_arguments = false;
            }
        }
        else
        {
            if (!arguments[0].equals("-e") &&
                !arguments[0].equals("-d") &&
                !arguments[0].equals("-c"))
            {
                valid_arguments = false;
            }
            else if (!arguments[0].equals("-c") &&
                     3 == arguments.length)
            {
                valid_arguments = false;
            }
        }

        if (!valid_arguments)
        {
            System.err.println("Usage : java " + StringEncryptor.class.getName() + " [-edc] string {encrypted}");
            System.err.println("Encrypts strings for usage with RIFE.");
            System.err.println("  -e  encrypt a string (default)");
            System.err.println("  -d  decrypt a string if the algorithm support it");
            System.err.println("  -c  check the validity of the string against an encrypted version");
            System.exit(1);
        }
        try
        {
            if (1 == arguments.length)
            {
                System.err.println(autoEncrypt(arguments[0]));
                System.exit(0);
            }
            else if (arguments[0].equals("-e"))
            {
                System.err.println(autoEncrypt(arguments[1]));
                System.exit(0);
            }
            if (arguments[0].equals("-d"))
            {
                if (arguments[1].startsWith(PREFIX_OBF))
                {
                    System.err.println(deobfuscate(arguments[1]));
                }
                else
                {
                    System.err.println("ERROR: the algorithm doesn't support decoding.");
                }
                System.exit(0);
            }
            if (arguments[0].equals("-c"))
            {
                if (matches(arguments[1], arguments[2]))
                {
                    System.err.println("VALID: the strings match.");
                }
                else
                {
                    System.err.println("INVALID: the strings don't match.");
                }
                System.exit(0);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    public String encrypt(String value)
    throws NoSuchAlgorithmException
    {
        if (null == value) throw new IllegalArgumentException("value can't be null");

        return autoEncrypt(identifier + value);
    }
}
