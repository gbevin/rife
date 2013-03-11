/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import junit.framework.TestCase;

import java.util.Random;
import java.util.regex.Pattern;

public class TestPasswordGenerator extends TestCase
{
    public TestPasswordGenerator(String name)
    {
        super(name);
    }

    public void testIllegalArguments()
    {
        try
        {
            PasswordGenerator.get(0);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            PasswordGenerator.get(1, 3);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }
    }

    public void testLength()
    {
        String password;

        password = PasswordGenerator.get(10);
        assertEquals(10, password.length());
        password = PasswordGenerator.get(20);
        assertEquals(20, password.length());
        password = PasswordGenerator.get(30);
        assertEquals(30, password.length());
    }

    public void testDefaultMixed()
    {
        String password;

        Pattern mixed_pattern;

        password = PasswordGenerator.get(10);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{10}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(98);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{98}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(12);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{12}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(32);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{32}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(75);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{75}$");
        assertTrue(mixed_pattern.matcher(password).matches());
    }

    public void testMixed()
    {
        String password;

        Pattern mixed_pattern;

        password = PasswordGenerator.get(10, PasswordGenerator.MIXED);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{10}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(98, PasswordGenerator.MIXED);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{98}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(12, PasswordGenerator.MIXED);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{12}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(32, PasswordGenerator.MIXED);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{32}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(75, PasswordGenerator.MIXED);
        mixed_pattern = Pattern.compile("^[a-zA-Z0-9]{75}$");
        assertTrue(mixed_pattern.matcher(password).matches());
    }

    public void testDigitsOnly()
    {
        String password;

        Pattern mixed_pattern;

        password = PasswordGenerator.get(10, PasswordGenerator.DIGITS_ONLY);
        mixed_pattern = Pattern.compile("^[0-9]{10}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(98, PasswordGenerator.DIGITS_ONLY);
        mixed_pattern = Pattern.compile("^[0-9]{98}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(12, PasswordGenerator.DIGITS_ONLY);
        mixed_pattern = Pattern.compile("^[0-9]{12}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(32, PasswordGenerator.DIGITS_ONLY);
        mixed_pattern = Pattern.compile("^[0-9]{32}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(75, PasswordGenerator.DIGITS_ONLY);
        mixed_pattern = Pattern.compile("^[0-9]{75}$");
        assertTrue(mixed_pattern.matcher(password).matches());
    }

    public void testLettersOnly()
    {
        String password;

        Pattern mixed_pattern;

        password = PasswordGenerator.get(10, PasswordGenerator.LETTERS_ONLY);
        mixed_pattern = Pattern.compile("^[a-zA-Z]{10}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(98, PasswordGenerator.LETTERS_ONLY);
        mixed_pattern = Pattern.compile("^[a-zA-Z]{98}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(12, PasswordGenerator.LETTERS_ONLY);
        mixed_pattern = Pattern.compile("^[a-zA-Z]{12}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(32, PasswordGenerator.LETTERS_ONLY);
        mixed_pattern = Pattern.compile("^[a-zA-Z]{32}$");
        assertTrue(mixed_pattern.matcher(password).matches());
        password = PasswordGenerator.get(75, PasswordGenerator.LETTERS_ONLY);
        mixed_pattern = Pattern.compile("^[a-zA-Z]{75}$");
        assertTrue(mixed_pattern.matcher(password).matches());
    }

    public void testRandom()
    {
        Random random1 = new Random(System.currentTimeMillis());
        Random random2 = new Random("the seed".hashCode());

        assertTrue(!PasswordGenerator.get(random1, 10, PasswordGenerator.MIXED).equals(PasswordGenerator.get(random2, 10, PasswordGenerator.MIXED)));

        assertTrue(!PasswordGenerator.get(random1, 10, PasswordGenerator.MIXED).equals(PasswordGenerator.get(random2, 10, PasswordGenerator.MIXED)));

        assertTrue(!PasswordGenerator.get(random1, 10, PasswordGenerator.MIXED).equals(PasswordGenerator.get(random2, 10, PasswordGenerator.MIXED)));

        assertTrue(!PasswordGenerator.get(random1, 10, PasswordGenerator.MIXED).equals(PasswordGenerator.get(random2, 10, PasswordGenerator.MIXED)));

        assertTrue(!PasswordGenerator.get(random1, 10, PasswordGenerator.MIXED).equals(PasswordGenerator.get(random2, 10, PasswordGenerator.MIXED)));
    }
}
