/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.datastructures.DocumentPosition;
import junit.framework.TestCase;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class TestStringUtils extends TestCase
{
    public TestStringUtils(String name)
    {
        super(name);
    }

    public TestStringUtils()
    {
        super();
    }

    public void testEncodeClassname()
    {
        assertNull(StringUtils.encodeClassname(null));
        assertEquals("just_a_string_", StringUtils.encodeClassname("just.a:string="));
    }

    public void testEncodeURL()
    {
        assertNull(StringUtils.encodeUrl(null));
        assertEquals("a+test+%26", StringUtils.encodeUrl("a test &"));
        String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.*";
        assertSame(valid, StringUtils.encodeUrl(valid));
        assertEquals("%21abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.*%3D", StringUtils.encodeUrl("!abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.*="));
    }

    public void testEncodeURLValue()
    {
        assertNull(StringUtils.encodeUrlValue(null));
        assertEquals("a+test+%26", StringUtils.encodeUrlValue("a test &"));
        String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.*";
        assertSame(valid, StringUtils.encodeUrlValue(valid));
        assertEquals("%21abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.*%3D", StringUtils.encodeUrlValue("!abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.*="));
        assertEquals("%02%02YWJjZGVmw4zDjcOOw4/DkcOSw5PDlMOVw5bDmMOZw5rDm8Ocw5/DoMOhw6I%3D", StringUtils.encodeUrlValue("abcdefÌÍÎÏÑÒÓÔÕÖØÙÚÛÜßàáâ"));

        assertFalse(StringUtils.doesUrlValueNeedDecoding("a+test+%26"));
        assertFalse(StringUtils.doesUrlValueNeedDecoding("%02%02YWJjZGVmw4zDjcOOw4/DkcOSw5PDlMOVw5bDmMOZw5rDm8Ocw5/DoMOhw6I%3D"));
        assertTrue(StringUtils.doesUrlValueNeedDecoding(URLDecoder.decode("%02%02YWJjZGVmw4zDjcOOw4/DkcOSw5PDlMOVw5bDmMOZw5rDm8Ocw5/DoMOhw6I%3D")));
        assertEquals("abcdefÌÍÎÏÑÒÓÔÕÖØÙÚÛÜßàáâ", StringUtils.decodeUrlValue(URLDecoder.decode("%02%02YWJjZGVmw4zDjcOOw4/DkcOSw5PDlMOVw5bDmMOZw5rDm8Ocw5/DoMOhw6I%3D")));
    }

    public void testEncodeHtml()
    {
        assertNull(StringUtils.encodeHtml(null));
        assertEquals(StringUtils.encodeHtml("&<>\"¡¢£¥§¨©ª«¬®¯°±´µ¶·¸º»¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÑÒÓÔÕÖØÙÚÛÜßàáâãäåæçèéêëìíîïñòóôõö÷øùúûüÿƒΩπ•…⁄™∂∏∑√∞∫≈≠≤≥◊ascii∛plainΘ❤"),
                     "&amp;&lt;&gt;&quot;&iexcl;&cent;&pound;&yen;&sect;&uml;&copy;&ordf;&laquo;&not;&reg;&macr;&deg;&plusmn;&acute;&micro;&para;&middot;&cedil;&ordm;&raquo;&iquest;&Agrave;&Aacute;&Acirc;&Atilde;&Auml;&Aring;&AElig;&Ccedil;&Egrave;&Eacute;&Ecirc;&Euml;&Igrave;&Iacute;&Icirc;&Iuml;&Ntilde;&Ograve;&Oacute;&Ocirc;&Otilde;&Ouml;&Oslash;&Ugrave;&Uacute;&Ucirc;&Uuml;&szlig;&agrave;&aacute;&acirc;&atilde;&auml;&aring;&aelig;&ccedil;&egrave;&eacute;&ecirc;&euml;&igrave;&iacute;&icirc;&iuml;&ntilde;&ograve;&oacute;&ocirc;&otilde;&ouml;&divide;&oslash;&ugrave;&uacute;&ucirc;&uuml;&yuml;&fnof;&Omega;&pi;&bull;&hellip;&frasl;&trade;&part;&prod;&sum;&radic;&infin;&int;&asymp;&ne;&le;&ge;&loz;ascii&#8731;plain&Theta;&#10084;");
    }

    public void testDecodeHtml()
    {
        assertNull(StringUtils.decodeHtml(null));

        assertEquals("plain", StringUtils.decodeHtml("plain"));

        assertEquals("Dépôt direct", StringUtils.decodeHtml("D&eacute;p&ocirc;t direct"));

        assertEquals("trailingchar&a", StringUtils.decodeHtml("trailingchar&amp;a"));

        assertEquals("ï&badentity", StringUtils.decodeHtml("&iuml;&badentity"));

        assertEquals("badnumeric&#1bad;oo°o", StringUtils.decodeHtml("badnumeric&#1bad;oo&deg;o"));

        assertEquals("à&unknown;é", StringUtils.decodeHtml("&agrave;&unknown;&eacute;"));

        assertEquals("µhexaÄ", StringUtils.decodeHtml("&#xB5;hexa&#XC4;"));

        assertEquals(StringUtils.decodeHtml("&amp;&lt;&gt;&quot;&iexcl;&cent;&pound;&yen;&sect;&uml;&copy;&ordf;&laquo;&not;&reg;&macr;&deg;&plusmn;&acute;&micro;&para;&middot;&cedil;&ordm;&raquo;&iquest;&Agrave;&Aacute;&Acirc;&Atilde;&Auml;&Aring;&AElig;&Ccedil;&Egrave;&Eacute;&Ecirc;&Euml;&Igrave;&Iacute;&Icirc;&Iuml;&Ntilde;&Ograve;&Oacute;&Ocirc;&Otilde;&Ouml;&Oslash;&Ugrave;&Uacute;&Ucirc;&Uuml;&szlig;&agrave;&aacute;&acirc;&atilde;&auml;&aring;&aelig;&ccedil;&egrave;&eacute;&ecirc;&euml;&igrave;&iacute;&icirc;&iuml;&ntilde;&ograve;&oacute;&ocirc;&otilde;&ouml;&divide;&oslash;&ugrave;&uacute;&ucirc;&uuml;&yuml;&fnof;&Omega;&pi;&bull;&hellip;&frasl;&trade;&part;&prod;&sum;&radic;&infin;&int;&asymp;&ne;&le;&ge;&loz;ascii&#8731;plain&Theta;&#10084;"),
                     "&<>\"¡¢£¥§¨©ª«¬®¯°±´µ¶·¸º»¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÑÒÓÔÕÖØÙÚÛÜßàáâãäåæçèéêëìíîïñòóôõö÷øùúûüÿƒΩπ•…⁄™∂∏∑√∞∫≈≠≤≥◊ascii∛plainΘ❤");
    }

    public void testEncodeHtmlDefensive()
    {
        assertNull(StringUtils.encodeHtmlDefensive(null));
        assertEquals(StringUtils.encodeHtmlDefensive("&<>\"¡¢£¥§¨©ª«¬®¯°±´µ¶·¸º»¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÑÒÓÔÕÖØÙÚÛÜßàáâãäåæçèéêëìíîïñòóôõö÷øùúûüÿƒΩπ•…⁄™∂∏∑√∞∫≈≠≤≥◊"),
                     "&<>\"&iexcl;&cent;&pound;&yen;&sect;&uml;&copy;&ordf;&laquo;&not;&reg;&macr;&deg;&plusmn;&acute;&micro;&para;&middot;&cedil;&ordm;&raquo;&iquest;&Agrave;&Aacute;&Acirc;&Atilde;&Auml;&Aring;&AElig;&Ccedil;&Egrave;&Eacute;&Ecirc;&Euml;&Igrave;&Iacute;&Icirc;&Iuml;&Ntilde;&Ograve;&Oacute;&Ocirc;&Otilde;&Ouml;&Oslash;&Ugrave;&Uacute;&Ucirc;&Uuml;&szlig;&agrave;&aacute;&acirc;&atilde;&auml;&aring;&aelig;&ccedil;&egrave;&eacute;&ecirc;&euml;&igrave;&iacute;&icirc;&iuml;&ntilde;&ograve;&oacute;&ocirc;&otilde;&ouml;&divide;&oslash;&ugrave;&uacute;&ucirc;&uuml;&yuml;&fnof;&Omega;&pi;&bull;&hellip;&frasl;&trade;&part;&prod;&sum;&radic;&infin;&int;&asymp;&ne;&le;&ge;&loz;");
    }

    public void testEncodeUnicode()
    {
        assertNull(StringUtils.encodeUnicode(null));
        assertEquals("\\u0061\\u0062\\u0063\\u0064\\u0065\\u006B", StringUtils.encodeUnicode("abcdek"));
    }

    public void testEncodeXml()
    {
        assertNull(StringUtils.encodeXml(null));
        assertEquals(StringUtils.encodeXml("abcd\'\"<>&wxyz"), "abcd&apos;&quot;&lt;&gt;&amp;wxyz");
    }

    public void testEncodeSql()
    {
        assertNull(StringUtils.encodeSql(null));
        assertEquals(StringUtils.encodeSql("abcd'wxyz"), "abcd''wxyz");
    }

    public void testEncodeString()
    {
        assertNull(StringUtils.encodeString(null));
        assertEquals(StringUtils.encodeString("abcd\"\na\t\r\\wxyz"), "abcd\\\"\\na\\t\\r\\\\wxyz");
    }

    public void testEncodeLatex()
    {
        assertNull(StringUtils.encodeLatex(null));
        assertEquals("\\\\\\#\\$\\%\\&\\~\\_\\^\\{\\}\\`{A}\\'{A}\\^{A}\\H{A}\\\"{A}\\AA\\AE\\c{C}\\`{E}\\'{E}\\^{E}\\\"{E}\\`{I}\\'{I}\\^{I}\\\"{I}\\H{N}\\`{O}\\'{O}\\^{O}\\H{O}\\\"{O}\\O\\`{U}\\'{U}\\^{U}\\\"{U}\\ss\\`{a}\\'{a}\\^{a}\\H{a}\\\"{a}\\aa\\ae\\c{c}\\`{e}\\'{e}\\^{e}\\\"{e}\\`{i}\\'{i}\\^{i}\\\"{i}\\H{n}\\`{o}\\'{o}\\^{o}\\H{o}\\\"{o}\\o\\`{u}\\'{u}\\^{u}\\\"{u}\\\"{y}\\LaTeX\\LaTeX", StringUtils.encodeLatex("\\#$%&~_^{}ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÑÒÓÔÕÖØÙÚÛÜßàáâãäåæçèéêëìíîïñòóôõöøùúûüÿlatexLaTeX"));
    }

    public void testCount()
    {
        assertEquals(StringUtils.count(null, null), 0);
        assertEquals(StringUtils.count("", null), 0);
        assertEquals(StringUtils.count("onetwoonethreefouroneONE", "one"), 3);
        assertEquals(StringUtils.count("onetwoonethreefouroneO", "one"), 3);
        assertEquals(StringUtils.count("101010101", "1"), 5);
    }

    public void testCountCase()
    {
        assertEquals(StringUtils.count("ONEtwooNethreefourone", "onE", false), 3);
    }

    public void testSplit()
    {
        assertEquals(StringUtils.split(null, null).size(), 0);
        assertEquals(StringUtils.split("one", null).size(), 1);
        assertEquals(StringUtils.split("one", null).get(0), "one");

        String string_to_split = "onextwoxthreeXfour";
        List<String> string_parts = StringUtils.split(string_to_split, "x");

        assertNotNull(string_parts);
        assertEquals(string_parts.get(0), "one");
        assertEquals(string_parts.get(1), "two");
        assertEquals(string_parts.get(2), "threeXfour");
    }

    public void testSplitCase()
    {
        String string_to_split = "oneaAatwoAAAthree";
        List<String> string_parts = StringUtils.split(string_to_split, "aaa", false);

        assertNotNull(string_parts);
        assertEquals(string_parts.get(0), "one");
        assertEquals(string_parts.get(1), "two");
        assertEquals(string_parts.get(2), "three");
    }

    public void testSplitToArray()
    {
        String string_to_split = "onextwoxthreeXfour";
        String[] string_parts = StringUtils.splitToArray(string_to_split, "x");

        assertNotNull(string_parts);
        assertEquals(string_parts[0], "one");
        assertEquals(string_parts[1], "two");
        assertEquals(string_parts[2], "threeXfour");
    }

    public void testSplitToArrayCase()
    {
        String string_to_split = "oneaAatwoAAAthree";
        String[] string_parts = StringUtils.splitToArray(string_to_split, "aaa", false);

        assertNotNull(string_parts);
        assertEquals(string_parts[0], "one");
        assertEquals(string_parts[1], "two");
        assertEquals(string_parts[2], "three");
    }

    public void testSplitToIntArray()
    {
        String string_to_split = "1x5x10xezfzefx50x100X200";
        int[] string_parts = StringUtils.splitToIntArray(string_to_split, "x");
        assertNotNull(string_parts);
        assertTrue(1 == string_parts[0]);
        assertTrue(5 == string_parts[1]);
        assertTrue(10 == string_parts[2]);
        assertTrue(50 == string_parts[3]);
    }

    public void testSplitToIntArrayCase()
    {
        String string_to_split = "1xXx5XXX10xxxezfzefXxX50";
        int[] string_parts = StringUtils.splitToIntArray(string_to_split, "XXX", false);
        assertNotNull(string_parts);
        assertTrue(1 == string_parts[0]);
        assertTrue(5 == string_parts[1]);
        assertTrue(10 == string_parts[2]);
        assertTrue(50 == string_parts[3]);
    }

    public void testSplitToByteArray()
    {
        String string_to_split = "sdfsdx5x1078456x50x100X200";
        byte[] string_parts = StringUtils.splitToByteArray(string_to_split, "x");
        assertNotNull(string_parts);
        assertTrue(5 == string_parts[0]);
        assertTrue(50 == string_parts[1]);
    }

    public void testSplitToByteArrayCase()
    {
        String string_to_split = "sdfsd_A_5_a_1078456_A_50";
        byte[] string_parts = StringUtils.splitToByteArray(string_to_split, "_a_", false);
        assertNotNull(string_parts);
        assertTrue(5 == string_parts[0]);
        assertTrue(50 == string_parts[1]);
    }

    public void testStripFromFront()
    {
        assertNull(StringUtils.stripFromFront(null, null));
        assertEquals(StringUtils.stripFromFront("2frontmiddleback", null), "2frontmiddleback");

        assertEquals(StringUtils.stripFromFront("2frontmiddleback", "front"), "2frontmiddleback");
        assertEquals(StringUtils.stripFromFront("frontmiddleback", "front"), "middleback");
        assertEquals(StringUtils.stripFromFront("FRONTmiddleback", "front"), "FRONTmiddleback");
        assertEquals(StringUtils.stripFromFront("frontfront2frontmiddleback", "front"), "2frontmiddleback");
        assertEquals(StringUtils.stripFromFront("frontmiddleback", "middle"), "frontmiddleback");
        assertEquals(StringUtils.stripFromFront("frontmiddleback", "back"), "frontmiddleback");
    }

    public void testStripFromFrontCase()
    {
        assertEquals(StringUtils.stripFromFront("2fRoNtmiddleback", "front", false), "2fRoNtmiddleback");
        assertEquals(StringUtils.stripFromFront("FRONTmiddleback", "front", false), "middleback");
        assertEquals(StringUtils.stripFromFront("fROntFRONt2frontmiddleback", "front", false), "2frontmiddleback");
        assertEquals(StringUtils.stripFromFront("frontmIDDLeback", "middle", false), "frontmIDDLeback");
        assertEquals(StringUtils.stripFromFront("frontmiddleBACk", "back", false), "frontmiddleBACk");
    }

    public void testStripFromEnd()
    {
        assertNull(StringUtils.stripFromEnd(null, null));
        assertEquals(StringUtils.stripFromEnd("frontmiddleback", null), "frontmiddleback");

        assertEquals(StringUtils.stripFromEnd("frontmiddleback", "front"), "frontmiddleback");
        assertEquals(StringUtils.stripFromEnd("frontmiddleback", "middle"), "frontmiddleback");
        assertEquals(StringUtils.stripFromEnd("frontmiddleback", "back"), "frontmiddle");
        assertEquals(StringUtils.stripFromEnd("frontmiddleBACK", "back"), "frontmiddleBACK");
        assertEquals(StringUtils.stripFromEnd("frontmiddleback2", "back"), "frontmiddleback2");
        assertEquals(StringUtils.stripFromEnd("frontmiddleback2backbackback", "back"), "frontmiddleback2");
    }

    public void testStripFromEndCase()
    {
        assertEquals(StringUtils.stripFromEnd("fRONtmiddleback", "front", false), "fRONtmiddleback");
        assertEquals(StringUtils.stripFromEnd("frontMIDDLEback", "middle", false), "frontMIDDLEback");
        assertEquals(StringUtils.stripFromEnd("frontmiddleBAcK", "back", false), "frontmiddle");
        assertEquals(StringUtils.stripFromEnd("frontmiddleBACK2", "back", false), "frontmiddleBACK2");
        assertEquals(StringUtils.stripFromEnd("frontmiddleback2BAckbackBACK", "back", false), "frontmiddleback2");
    }

    public void testReplace()
    {
        assertNull(StringUtils.replace(null, "one", "five"));
        assertEquals(StringUtils.replace("onetwooneTWOthreeONEfourone", null, "five"), "onetwooneTWOthreeONEfourone");
        assertEquals(StringUtils.replace("onetwooneTWOthreeONEfourone", "one", null), "onetwooneTWOthreeONEfourone");

        assertEquals(StringUtils.replace("onetwooneTWOthreeONEfourone", "one", "five"), "fivetwofiveTWOthreeONEfourfive");
        assertEquals(StringUtils.replace("onetwooneTWOthreeONEfourone", "two", "five"), "onefiveoneTWOthreeONEfourone");
        assertEquals(StringUtils.replace("onetwoonethreefourone", "six", "five"), "onetwoonethreefourone");
    }

    public void testReplaceCase()
    {
        assertEquals(StringUtils.replace("ONEtwoOnEthreefouroNE", "one", "five", false), "fivetwofivethreefourfive");
        assertEquals(StringUtils.replace("onetWOonethreefourone", "two", "five", false), "onefiveonethreefourone");
        assertEquals(StringUtils.replace("onetwoonethreefourone", "six", "five", false), "onetwoonethreefourone");
    }

    public void testRepeat()
    {
        assertNull(StringUtils.repeat(null, 0), null);

        assertEquals(StringUtils.repeat("one", 0), "");
        assertEquals(StringUtils.repeat("one", 1), "one");
        assertEquals(StringUtils.repeat("one", 3), "oneoneone");
    }

    public void testToStringArray()
    {
        assertEquals(StringUtils.toStringArray(null).length, 0);

        Vector<String> strings = new Vector<>();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        String[] string_array = {"one", "two", "three"};

        String[] string_array_new;
        string_array_new = StringUtils.toStringArray(strings.iterator());
        assertEquals(string_array_new.length, string_array.length);
        assertEquals(string_array_new[0], string_array[0]);
        assertEquals(string_array_new[1], string_array[1]);
        assertEquals(string_array_new[2], string_array[2]);
    }

    public void testToArrayList()
    {
        assertEquals(StringUtils.toArrayList(null).size(), 0);

        String[] string_array = {"one", "two", "three"};
        List<String> string_arraylist = StringUtils.toArrayList(string_array);
        assertEquals(string_array.length, string_arraylist.size());
        assertEquals(string_array[0], string_arraylist.get(0));
        assertEquals(string_array[1], string_arraylist.get(1));
        assertEquals(string_array[2], string_arraylist.get(2));
    }

    public void testJoinCollection()
    {
        assertNull(StringUtils.join((ArrayList<String>)null, null));
        assertEquals("", StringUtils.join(new ArrayList<String>(), ""));
        assertEquals("", StringUtils.join(new ArrayList<String>(), null));

        ArrayList<String> strings = new ArrayList<>();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        String joined_string = StringUtils.join(strings, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "one,two,three");
    }

    public void testJoinStringArray()
    {
        assertNull(StringUtils.join((String[])null, null));
        assertEquals("", StringUtils.join(new String[0], ""));
        assertEquals("", StringUtils.join(new String[0], null));

        String[] string_array = {"one", "two", "three"};
        String joined_string = StringUtils.join(string_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "one,two,three");

        joined_string = StringUtils.join(string_array, ",", "'");
        assertNotNull(joined_string);
        assertEquals(joined_string, "'one','two','three'");
    }

    public void testJoinStringArrayEncode()
    {
        String[] string_array = {"one\"", "two", "thr\"ee"};
        String joined_string = StringUtils.join(string_array, ",", "'", true);
        assertNotNull(joined_string);
        assertEquals(joined_string, "'one\\\"','two','thr\\\"ee'");
    }

    public void testJoinBooleanArray()
    {
        assertNull(StringUtils.join((boolean[])null, null));
        assertEquals("", StringUtils.join(new boolean[0], ""));
        assertEquals("", StringUtils.join(new boolean[0], null));

        boolean[] boolean_array = {true, false, false, true};
        String joined_string = StringUtils.join(boolean_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "true,false,false,true");
    }

    public void testJoinByteArray()
    {
        assertNull(StringUtils.join((byte[])null, null));
        assertEquals("", StringUtils.join(new byte[0], ""));
        assertEquals("", StringUtils.join(new byte[0], null));

        byte[] byte_array = {1, 6, 3, 5};
        String joined_string = StringUtils.join(byte_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "1,6,3,5");
    }

    public void testJoinShortArray()
    {
        assertNull(StringUtils.join((short[])null, null));
        assertEquals("", StringUtils.join(new short[0], ""));
        assertEquals("", StringUtils.join(new short[0], null));

        short[] short_array = {8, 3, 9, 6, 4};
        String joined_string = StringUtils.join(short_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "8,3,9,6,4");
    }

    public void testJoinCharArray()
    {
        assertNull(StringUtils.join((char[])null, null));
        assertEquals("", StringUtils.join(new char[0], ""));
        assertEquals("", StringUtils.join(new char[0], null));

        char[] short_array = {'k', 'L', 's', 'O'};
        String joined_string = StringUtils.join(short_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "k,L,s,O");
        joined_string = StringUtils.join(short_array, ",", "'");
        assertNotNull(joined_string);
        assertEquals(joined_string, "'k','L','s','O'");
    }

    public void testJoinIntArray()
    {
        assertNull(StringUtils.join((int[])null, null));
        assertEquals("", StringUtils.join(new int[0], ""));
        assertEquals("", StringUtils.join(new int[0], null));

        int[] int_array = {1, 5, 10, 50};
        String joined_string = StringUtils.join(int_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "1,5,10,50");
    }

    public void testJoinLongArray()
    {
        assertNull(StringUtils.join((long[])null, null));
        assertEquals("", StringUtils.join(new long[0], ""));
        assertEquals("", StringUtils.join(new long[0], null));

        long[] long_array = {78L, 98934L, 232L, 97834L};
        String joined_string = StringUtils.join(long_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "78,98934,232,97834");
    }

    public void testJoinFloatArray()
    {
        assertNull(StringUtils.join((float[])null, null));
        assertEquals("", StringUtils.join(new float[0], ""));
        assertEquals("", StringUtils.join(new float[0], null));

        float[] float_array = {23.1f, 76.3f, 3.87f};
        String joined_string = StringUtils.join(float_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "23.1,76.3,3.87");
    }

    public void testJoinDoubleArray()
    {
        assertNull(StringUtils.join((double[])null, null));
        assertEquals("", StringUtils.join(new double[0], ""));
        assertEquals("", StringUtils.join(new double[0], null));

        double[] double_array = {1.2d, 5.7d, 10.12d, 50.98d};
        String joined_string = StringUtils.join(double_array, ",");
        assertNotNull(joined_string);
        assertEquals(joined_string, "1.2,5.7,10.12,50.98");
    }

    public void testIndicesOf()
    {
        assertEquals(0, StringUtils.indicesOf(null, "one").length);
        assertEquals(0, StringUtils.indicesOf("onetwoonethreefouroneONE", null).length);

        int[] indices1 = StringUtils.indicesOf("onetwoonethreefouroneONE", "one");
        assertEquals(indices1.length, 3);
        assertEquals(indices1[0], 0);
        assertEquals(indices1[1], 6);
        assertEquals(indices1[2], 18);

        int[] indices2 = StringUtils.indicesOf("onetwoonethreefouroneO", "one");
        assertEquals(indices2.length, 3);
        assertEquals(indices2[0], 0);
        assertEquals(indices2[1], 6);
        assertEquals(indices2[2], 18);

        int[] indices3 = StringUtils.indicesOf("101010101", "1");
        assertEquals(indices3.length, 5);
        assertEquals(indices3[0], 0);
        assertEquals(indices3[1], 2);
        assertEquals(indices3[2], 4);
        assertEquals(indices3[3], 6);
        assertEquals(indices3[4], 8);
    }

    public void testIndicesOfCase()
    {
        int[] indices = StringUtils.indicesOf("oNEtwoONEthreefourOne", "one", false);
        assertEquals(indices.length, 3);
        assertEquals(indices[0], 0);
        assertEquals(indices[1], 6);
        assertEquals(indices[2], 18);
    }

    public void testGetMatchingRegexp()
    {
        Pattern pattern1 = Pattern.compile("reg(.*)lar");
        Pattern pattern2 = Pattern.compile("exp(.*)ion");

        ArrayList<Pattern> regexps = new ArrayList<>();
        regexps.add(pattern1);
        regexps.add(pattern2);

        assertSame(pattern1, StringUtils.getMatchingRegexp("regular", regexps).pattern());
        assertSame(pattern2, StringUtils.getMatchingRegexp("expression", regexps).pattern());
        assertNull(StringUtils.getMatchingRegexp("nomatch", regexps));
    }

    public void testGetRegexpMatch()
    {
        String value1 = "regular";
        String value2 = "expression";

        ArrayList<String> values = new ArrayList<>();
        values.add(value1);
        values.add(value2);

        assertSame(value1, StringUtils.getRegexpMatch(values, Pattern.compile("reg(.*)lar")).group());
        assertSame(value2, StringUtils.getRegexpMatch(values, Pattern.compile("exp(.*)ion")).group());
        assertNull(StringUtils.getRegexpMatch(values, Pattern.compile("no(.*)match")));
    }

    public void testFilterSingular()
    {
        assertFalse(StringUtils.filter(null, null, (Pattern)null));

        assertTrue(StringUtils.filter("test", null, Pattern.compile(".*a.*")));
        assertFalse(StringUtils.filter("test", null, Pattern.compile(".*t.*")));

        assertTrue(StringUtils.filter("test", Pattern.compile(".*e.*"), null));
        assertFalse(StringUtils.filter("test", Pattern.compile(".*x.*"), null));

        assertTrue(StringUtils.filter("test", Pattern.compile(".*e.*"), Pattern.compile(".*a.*")));
        assertFalse(StringUtils.filter("test", Pattern.compile(".*e.*"), Pattern.compile(".*t.*")));
        assertFalse(StringUtils.filter("test", Pattern.compile(".*x.*"), Pattern.compile(".*a.*")));
    }

    public void testFilterMultiple()
    {
        assertFalse(StringUtils.filter(null, null, (Pattern[])null));

        assertTrue(StringUtils.filter("test", null, new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*b.*")}));
        assertFalse(StringUtils.filter("test", null, new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*t.*")}));
        assertFalse(StringUtils.filter("test", null, new Pattern[]{Pattern.compile(".*e.*"), Pattern.compile(".*t.*")}));

        assertTrue(StringUtils.filter("test", new Pattern[]{Pattern.compile(".*e.*"), Pattern.compile(".*s.*")}, null));
        assertTrue(StringUtils.filter("test", new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*s.*")}, null));
        assertFalse(StringUtils.filter("test", new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*b.*")}, null));

        assertTrue(StringUtils.filter("test", new Pattern[]{Pattern.compile(".*e.*"), Pattern.compile(".*s.*")}, new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*b.*")}));
        assertTrue(StringUtils.filter("test", new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*s.*")}, new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*b.*")}));
        assertFalse(StringUtils.filter("test", new Pattern[]{Pattern.compile(".*e.*"), Pattern.compile(".*s.*")}, new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*t.*")}));
        assertFalse(StringUtils.filter("test", new Pattern[]{Pattern.compile(".*x.*"), Pattern.compile(".*b.*")}, new Pattern[]{Pattern.compile(".*a.*"), Pattern.compile(".*t.*")}));
    }

    public void testCapitalize()
    {
        assertNull(StringUtils.capitalize(null));
        assertEquals("", StringUtils.capitalize(""));
        assertEquals("Hohoho", StringUtils.capitalize("Hohoho"));
        assertEquals("HohoHo", StringUtils.capitalize("hohoHo"));
    }

    public void testUncapitalize()
    {
        assertNull(StringUtils.uncapitalize(null));
        assertEquals("", StringUtils.uncapitalize(""));
        assertEquals("hohoho", StringUtils.uncapitalize("Hohoho"));
        assertEquals("hohoHo", StringUtils.uncapitalize("hohoHo"));
    }

    public void testConvertBbcode()
    {
        assertNull(StringUtils.convertBbcode(null));

        String source =
                "[B]text[/B]text[b]text[/b]text\n" +
                "[U]text[/U]text[u]text[/u]text\r\n" +
                "[I]text[/I]text[i]text[/i]text\n" +
                "[I]text[/I]text[i]text[/i]text\r\n" +
                "[pre]text[/pre]text[pre]text[/pre]text\n" +
                "[LIST]text[/LIST]text[list]text[/list]text\n" +
                "[*]text\r\n" +
                "[LIST]\n" +
                "[*]text\n" +
                "[/LIST]\n" +
                "[LIST]\r\n" +
                "[*]text\r\n" +
                "[/LIST]\r\n" +
                "[color= #ffcc00 ]text[/color]text[COLOR=#FFCC00]text[/COLOR]text\n" +
                "[size= -2 ]text[/size]text[SIZE=3]text[/SIZE]text\r\n" +
                "[img] http://www.uwyn.com/images/logo.png [/img]\n" +
                "[url]  www.uwyn.com [/url]\n" +
                "[url] http://www.uwyn.com [/url]\n" +
                "[url] /index.html [/url]\n" +
                "[url=http://www.uwyn.com] The site of Uwyn. [/url]\n" +
                "[url=www.uwyn.com] The site of Uwyn. [/url]\n" +
                "[url=index.html] The site of Uwyn. [/url]\n";

        assertEquals("<b>text</b>text<b>text</b>text<br />\n" +
                     "<u>text</u>text<u>text</u>text<br />\r\n" +
                     "<i>text</i>text<i>text</i>text<br />\n" +
                     "<i>text</i>text<i>text</i>text<br />\r\n" +
                     "<pre>text</pre>text<pre>text</pre>text<br />\n" +
                     "<ul>text</ul>text<ul>text</ul>text<br />\n" +
                     "<li>text<br />\r\n" +
                     "</li><ul>\n" +
                     "<li>text<br />\n" +
                     "</li></ul>\n" +
                     "<ul>\r\n" +
                     "<li>text<br />\r\n" +
                     "</li></ul>\r\n" +
                     "<font color=\"#ffcc00\">text</font>text<font color=\"#FFCC00\">text</font>text<br />\n" +
                     "<font size=\"-2\">text</font>text<font size=\"3\">text</font>text<br />\r\n" +
                     "<div class=\"bbcode_img\"><img src=\"http://www.uwyn.com/images/logo.png\" border=\"0\" alt=\"\" /></div><br />\n" +
                     "<a href=\"www.uwyn.com\">www.uwyn.com</a><br />\n" +
                     "<a href=\"http://www.uwyn.com\" target=\"_blank\">http://www.uwyn.com</a><br />\n" +
                     "<a href=\"/index.html\">/index.html</a><br />\n" +
                     "<a href=\"http://www.uwyn.com\" target=\"_blank\"> The site of Uwyn. </a><br />\n" +
                     "<a href=\"www.uwyn.com\"> The site of Uwyn. </a><br />\n" +
                     "<a href=\"index.html\"> The site of Uwyn. </a><br />\n", StringUtils.convertBbcode(source));

        source = "[url]http://www.uwyn.com/page?really=long&statement=that&should=be&reduced=dont&you=think&so=blaat[/url]\n" +
                 "[url=http://www.uwyn.com/page?really=long&statement=that&should=be&reduced=dont&you=think&so=blaat] The site of Uwyn. [/url]\n" +
                 "[url]http://www.uwyn.com/page/really/long/statement/that/should/be/reduced/dont/you/think/so/blaat[/url]\n" +
                 "[url=http://www.uwyn.com/page/really/long/statement/that/should/be/reduced/dont/you/think/so/blaat] The site of Uwyn. [/url]\n" +
                 "[url]http://www.uwyn.com/other/page?really=long&statement=that&should=be&reduced=dont&you=think&so=blaat[/url]\n" +
                 "[url]http://www.uwyn.com/page/really/long/statement/that/should/be/reduced/dont/you/think/so/blaat?really=long&statement=that&should=be&reduced=dont&you=think&so=blaat[/url]\n" +
                 "[url]http://www.uwyn.com/short?url=true[/url]\n";

        assertEquals("<a href=\"http://www.uwyn.com/page?really=long&statement=that&should=be&reduced=dont&you=think&so=blaat\" target=\"_blank\">http://www.uwyn.com/page?...</a><br />\n" +
                     "<a href=\"http://www.uwyn.com/page?really=long&statement=that&should=be&reduced=dont&you=think&so=blaat\" target=\"_blank\"> The site of Uwyn. </a><br />\n" +
                     "<a href=\"http://www.uwyn.com/page/really/long/statement/that/should/be/reduced/dont/you/think/so/blaat\" target=\"_blank\">http://www.uwyn.com/.../blaat</a><br />\n" +
                     "<a href=\"http://www.uwyn.com/page/really/long/statement/that/should/be/reduced/dont/you/think/so/blaat\" target=\"_blank\"> The site of Uwyn. </a><br />\n" +
                     "<a href=\"http://www.uwyn.com/other/page?really=long&statement=that&should=be&reduced=dont&you=think&so=blaat\" target=\"_blank\">http://www.uwyn.com/other/page?...</a><br />\n" +
                     "<a href=\"http://www.uwyn.com/page/really/long/statement/that/should/be/reduced/dont/you/think/so/blaat?really=long&statement=that&should=be&reduced=dont&you=think&so=blaat\" target=\"_blank\">http://www.uwyn.com/.../blaat?...</a><br />\n" +
                     "<a href=\"http://www.uwyn.com/short?url=true\" target=\"_blank\">http://www.uwyn.com/short?url=true</a><br />\n",
                     StringUtils.convertBbcode(source, StringUtils.SHORTEN_URL));

        source = "[code]This is code[/code]";

        assertEquals("<div class=\"codebody\"><pre>This is code</pre></div>", StringUtils.convertBbcode(source, StringUtils.SHORTEN_URL));

        source = "[b]Check out my righteous code[/b]\n" +
                 "[code]This is code[/code]\n" +
                 "[u]VERY COOL[/u]";

        assertEquals("<b>Check out my righteous code</b><br />\n" +
                     "<div class=\"codebody\"><pre>This is code</pre></div><br />\n" +
                     "<u>VERY COOL</u>", StringUtils.convertBbcode(source, StringUtils.SHORTEN_URL));

        source = "[url]javascript:self.close();[/url]\n" +
                 "[url=javascript:self.close();]Click here[/url]";

        assertEquals("<a href=\"javascript:self.close();\">javascript:self.close();</a><br />\n" +
                     "<a href=\"javascript:self.close();\">Click here</a>", StringUtils.convertBbcode(source, StringUtils.SHORTEN_URL));

        assertEquals("<a href=\"http://self.close();\" target=\"_blank\">javascript:self.close();</a><br />\n" +
                     "<a href=\"http://self.close();\" target=\"_blank\">Click here</a>", StringUtils.convertBbcode(source, StringUtils.SHORTEN_URL, StringUtils.SANITIZE_URL));

        source = "[quote]This is a quote[/quote]\n" +
                 "[quote=Bob]This is a quote from Bob[/quote]";

        assertEquals("<div class=\"quotebody\">This is a quote</div><br />\n" +
                     "<div class=\"quoteaccount\">Bob:</div><div class=\"quotebody\">This is a quote from Bob</div>", StringUtils.convertBbcode(source));

        source = "[code]test1[/code]\n\n" +
                 "[b]mijnstijl.css[/b]\n" +
                 "[code]test2[/code]\n\n" +
                 "[b]mijndocument.html[/b]\n" +
                 "[code]test3[/code]";

        assertEquals("<div class=\"codebody\"><pre>test1</pre></div><br />\n<br />\n" +
                     "<b>mijnstijl.css</b><br />\n" +
                     "<div class=\"codebody\"><pre>test2</pre></div><br />\n<br />\n" +
                     "<b>mijndocument.html</b><br />\n" +
                     "<div class=\"codebody\"><pre>test3</pre></div>", StringUtils.convertBbcode(source));

        source = "[code]test1[/code]\n\n" +
                 "[code]test2[/code]\n\n" +
                 "[code]test3[/code]";

        assertEquals("<div class=\"codebody\"><pre>test1</pre></div><br />\n<br />\n" +
                     "<div class=\"codebody\"><pre>test2</pre></div><br />\n<br />\n" +
                     "<div class=\"codebody\"><pre>test3</pre></div>", StringUtils.convertBbcode(source));

        source = "http://www.uwyn.com";

        assertEquals("<a href=\"http://www.uwyn.com\" target=\"_blank\">http://www.uwyn.com</a>", StringUtils.convertBbcode(source, StringUtils.CONVERT_BARE_URLS));

        assertEquals("http://www.uwyn.com", StringUtils.convertBbcode(source));

        source = "Spacing test http://www.uwyn.com tset gnicapS";

        assertEquals("Spacing test <a href=\"http://www.uwyn.com\" target=\"_blank\">http://www.uwyn.com</a> tset gnicapS", StringUtils.convertBbcode(source, StringUtils.CONVERT_BARE_URLS));

        source = "http://www.uwyn.com\n" +
                 "[url]http://www.uwyn.com[/url]\n" +
                 "[url=http://www.uwyn.com]Uwyn[/url]";

        assertEquals("<a href=\"http://www.uwyn.com\" target=\"_blank\" rel=\"nofollow\">http://www.uwyn.com</a><br />\n" +
                     "<a href=\"http://www.uwyn.com\" target=\"_blank\" rel=\"nofollow\">http://www.uwyn.com</a><br />\n" +
                     "<a href=\"http://www.uwyn.com\" target=\"_blank\" rel=\"nofollow\">Uwyn</a>", StringUtils.convertBbcode(source, StringUtils.CONVERT_BARE_URLS, StringUtils.NO_FOLLOW_LINKS));

        source = "[code]" +
                 "codepart" +
                 "[code]" +
                 "text" +
                 "[/code]codepart3[/code]" +
                 "[code]codepart4[/code]";

        assertEquals("<div class=\"codebody\"><pre>codepart</pre></div><div class=\"codebody\"><pre>text</pre></div>codepart3[/code]<div class=\"codebody\"><pre>codepart4</pre></div>",
                     StringUtils.convertBbcode(source));
    }

    public void testConvertToBoolean()
    {
        assertFalse(StringUtils.convertToBoolean(null));
        assertFalse(StringUtils.convertToBoolean("blabla"));
        assertFalse(StringUtils.convertToBoolean("0"));
        assertTrue(StringUtils.convertToBoolean("1"));
        assertTrue(StringUtils.convertToBoolean("t"));
        assertTrue(StringUtils.convertToBoolean("true"));
        assertTrue(StringUtils.convertToBoolean("y"));
        assertTrue(StringUtils.convertToBoolean("yes"));
        assertTrue(StringUtils.convertToBoolean("on"));
        assertTrue(StringUtils.convertToBoolean("T"));
        assertTrue(StringUtils.convertToBoolean("TRUE"));
        assertTrue(StringUtils.convertToBoolean("Y"));
        assertTrue(StringUtils.convertToBoolean("YES"));
        assertTrue(StringUtils.convertToBoolean("ON"));
    }

    public void testGetDocumentPosition()
    {
        DocumentPosition position;

        String document1 = "0123456789\n" +
                           "9012345678\n" +
                           "\n" +
                           "\n" +
                           "8901234567\n" +
                           "7890123456\n" +
                           "\n" +
                           "6789012345\n";

        assertNull(StringUtils.getDocumentPosition(null, 1));
        assertNull(StringUtils.getDocumentPosition(document1, -2));

        position = StringUtils.getDocumentPosition(document1, 0);
        assertEquals("0123456789", position.getLineContent());
        assertEquals(1, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 10);
        assertEquals("0123456789", position.getLineContent());
        assertEquals(1, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 11);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 12);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(2, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 21);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 22);
        assertEquals("", position.getLineContent());
        assertEquals(3, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 23);
        assertEquals("", position.getLineContent());
        assertEquals(4, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 24);
        assertEquals("8901234567", position.getLineContent());
        assertEquals(5, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 25);
        assertEquals("8901234567", position.getLineContent());
        assertEquals(5, position.getLine());
        assertEquals(2, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 38);
        assertEquals("7890123456", position.getLineContent());
        assertEquals(6, position.getLine());
        assertEquals(4, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 38);
        assertEquals("7890123456", position.getLineContent());
        assertEquals(6, position.getLine());
        assertEquals(4, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 47);
        assertEquals("6789012345", position.getLineContent());
        assertEquals(8, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 57);
        assertEquals("6789012345", position.getLineContent());
        assertEquals(8, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document1, 58);
        assertEquals("", position.getLineContent());
        assertEquals(9, position.getLine());
        assertEquals(1, position.getColumn());

        assertNull(StringUtils.getDocumentPosition(document1, 59));

        String document2 = "0123456789\r" +
                           "9012345678\r" +
                           "\r" +
                           "\r" +
                           "8901234567\r" +
                           "7890123456\r" +
                           "\r" +
                           "6789012345\r";

        assertNull(StringUtils.getDocumentPosition(null, 1));
        assertNull(StringUtils.getDocumentPosition(document2, -2));

        position = StringUtils.getDocumentPosition(document2, 0);
        assertEquals("0123456789", position.getLineContent());
        assertEquals(1, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 10);
        assertEquals("0123456789", position.getLineContent());
        assertEquals(1, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 11);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 12);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(2, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 21);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 22);
        assertEquals("", position.getLineContent());
        assertEquals(3, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 23);
        assertEquals("", position.getLineContent());
        assertEquals(4, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 24);
        assertEquals("8901234567", position.getLineContent());
        assertEquals(5, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 25);
        assertEquals("8901234567", position.getLineContent());
        assertEquals(5, position.getLine());
        assertEquals(2, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 38);
        assertEquals("7890123456", position.getLineContent());
        assertEquals(6, position.getLine());
        assertEquals(4, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 38);
        assertEquals(6, position.getLine());
        assertEquals("7890123456", position.getLineContent());
        assertEquals(4, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 47);
        assertEquals("6789012345", position.getLineContent());
        assertEquals(8, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 57);
        assertEquals("6789012345", position.getLineContent());
        assertEquals(8, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document2, 58);
        assertEquals("", position.getLineContent());
        assertEquals(9, position.getLine());
        assertEquals(1, position.getColumn());

        assertNull(StringUtils.getDocumentPosition(document2, 59));

        String document3 = "0123456789\r\n" +
                           "9012345678\r\n" +
                           "\r\n" +
                           "\r\n" +
                           "8901234567\r\n" +
                           "7890123456\r\n" +
                           "\r\n" +
                           "6789012345\r\n";

        position = StringUtils.getDocumentPosition(document3, 0);
        assertEquals("0123456789", position.getLineContent());
        assertEquals(1, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 10);
        assertEquals("0123456789", position.getLineContent());
        assertEquals(1, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 11);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(0, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 12);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 13);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(2, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 22);
        assertEquals("9012345678", position.getLineContent());
        assertEquals(2, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 23);
        assertEquals("", position.getLineContent());
        assertEquals(3, position.getLine());
        assertEquals(0, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 24);
        assertEquals("", position.getLineContent());
        assertEquals(3, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 25);
        assertEquals("", position.getLineContent());
        assertEquals(4, position.getLine());
        assertEquals(0, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 26);
        assertEquals("", position.getLineContent());
        assertEquals(4, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 27);
        assertEquals("8901234567", position.getLineContent());
        assertEquals(5, position.getLine());
        assertEquals(0, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 28);
        assertEquals("8901234567", position.getLineContent());
        assertEquals(5, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 29);
        assertEquals("8901234567", position.getLineContent());
        assertEquals(5, position.getLine());
        assertEquals(2, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 43);
        assertEquals("7890123456", position.getLineContent());
        assertEquals(6, position.getLine());
        assertEquals(4, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 54);
        assertEquals("6789012345", position.getLineContent());
        assertEquals(8, position.getLine());
        assertEquals(1, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 64);
        assertEquals("6789012345", position.getLineContent());
        assertEquals(8, position.getLine());
        assertEquals(11, position.getColumn());

        position = StringUtils.getDocumentPosition(document3, 66);
        assertEquals("", position.getLineContent());
        assertEquals(9, position.getLine());
        assertEquals(1, position.getColumn());

        assertNull(StringUtils.getDocumentPosition(document3, 67));
    }

    public void testWordWrap()
    {
        String buffer = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Suspendisse nisi enim, rutrum eget, condimentum quis, malesuada cursus, magna. Fusce suscipit, lorem accumsan hendrerit convallis, purus sem feugiat nulla, a consequat turpis nisi sed ipsum. Duis iaculis suscipit quam. Praesent placerat nibh lobortis nulla. Morbi scelerisque. Etiam et libero. Aliquam viverra tortor eget lectus. Cras quis sem id massa tempor imperdiet. Morbi posuere purus sit amet tortor. Curabitur venenatis ultrices elit. Integer vitae neque. Suspendisse at ipsum sed orci interdum dictum. Praesent condimentum augue et diam. Nunc a neque. Quisque arcu.\n" +
                        "Praesent diam dolor, gravida eget, faucibus in, aliquet sed, elit. Sed lacinia lorem eu leo condimentum lacinia. Proin egestas. Sed porta magna. Nunc ut est. Sed vitae sem. Nunc tempor mattis felis. Nunc urna magna, aliquet quis, consequat ut, ullamcorper eget, libero. Mauris eu dui. Integer ante nibh, lobortis ut, sagittis eu, pretium sed, quam. Praesent fringilla nisi non metus mollis cursus. Ut convallis. Pellentesque imperdiet rhoncus nulla. Fusce tempor. Sed mollis. Fusce feugiat. Proin porttitor nulla sit amet velit.\n" +
                        "\n" +
                        "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed eu est. Duis viverra orci non nisi mollis feugiat. Morbi ut felis. Phasellus placerat elit ac ligula. Vivamus vitae augue. Curabitur pharetra porta risus. Nullam eget est nec arcu bibendum condimentum. Quisque sed pede vitae odio tristique interdum. Aenean magna dolor, sagittis eu, vestibulum ut, varius luctus, erat. Duis tempus libero non lacus. Sed sapien enim, elementum at, laoreet vel, adipiscing sed, purus. Vestibulum magna. Quisque in pede. Proin vitae ligula. Aenean accumsan blandit magna. Nullam cursus tellus in urna. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.";

        String result =
                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Suspendisse nisi enim,\n" +
                "rutrum eget, condimentum quis, malesuada cursus, magna. Fusce suscipit, lorem \n" +
                "accumsan hendrerit convallis, purus sem feugiat nulla, a consequat turpis nisi \n" +
                "sed ipsum. Duis iaculis suscipit quam. Praesent placerat nibh lobortis nulla. \n" +
                "Morbi scelerisque. Etiam et libero. Aliquam viverra tortor eget lectus. Cras \n" +
                "quis sem id massa tempor imperdiet. Morbi posuere purus sit amet tortor. \n" +
                "Curabitur venenatis ultrices elit. Integer vitae neque. Suspendisse at ipsum sed\n" +
                "orci interdum dictum. Praesent condimentum augue et diam. Nunc a neque. Quisque \n" +
                "arcu.\n" +
                "Praesent diam dolor, gravida eget, faucibus in, aliquet sed, elit. Sed lacinia \n" +
                "lorem eu leo condimentum lacinia. Proin egestas. Sed porta magna. Nunc ut est. \n" +
                "Sed vitae sem. Nunc tempor mattis felis. Nunc urna magna, aliquet quis, \n" +
                "consequat ut, ullamcorper eget, libero. Mauris eu dui. Integer ante nibh, \n" +
                "lobortis ut, sagittis eu, pretium sed, quam. Praesent fringilla nisi non metus \n" +
                "mollis cursus. Ut convallis. Pellentesque imperdiet rhoncus nulla. Fusce tempor.\n" +
                "Sed mollis. Fusce feugiat. Proin porttitor nulla sit amet velit.\n" +
                "\n" +
                "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia\n" +
                "Curae; Sed eu est. Duis viverra orci non nisi mollis feugiat. Morbi ut felis. \n" +
                "Phasellus placerat elit ac ligula. Vivamus vitae augue. Curabitur pharetra porta\n" +
                "risus. Nullam eget est nec arcu bibendum condimentum. Quisque sed pede vitae \n" +
                "odio tristique interdum. Aenean magna dolor, sagittis eu, vestibulum ut, varius \n" +
                "luctus, erat. Duis tempus libero non lacus. Sed sapien enim, elementum at, \n" +
                "laoreet vel, adipiscing sed, purus. Vestibulum magna. Quisque in pede. Proin \n" +
                "vitae ligula. Aenean accumsan blandit magna. Nullam cursus tellus in urna. Cum \n" +
                "sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus \n" +
                "mus.";

        assertEquals(result, StringUtils.wordWrap(buffer, 80, null));
    }
}
