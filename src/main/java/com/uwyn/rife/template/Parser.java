/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import com.uwyn.rife.datastructures.collections.primitives.ArrayIntList;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

enum TemplateToken
{
    BEGIN_START, WHITESPACE, GAP
}

interface ParseCondition
{
    boolean isValid(int codePoint);
}

class SingleChar implements ParseCondition
{
    private final int expected;

    public SingleChar(int expected)
    {
        this.expected = expected;
    }

    @Override
    public boolean isValid(int codePoint)
    {
        return expected == codePoint;
    }
}

class Whitespace implements ParseCondition
{
    @Override
    public boolean isValid(int codePoint)
    {
        return Character.isWhitespace(codePoint);
    }
}

class IdentifierStart implements ParseCondition
{
    @Override
    public boolean isValid(int codePoint)
    {
        return Character.isJavaIdentifierStart(codePoint);
    }
}

class IdentitifierPart implements ParseCondition
{
    @Override
    public boolean isValid(int codePoint)
    {
        return Character.isJavaIdentifierPart(codePoint);
    }
}

class ParseState
{
    private ParseCondition condition;
    private TemplateToken token;
    private SortedSet<ParseState> nextStates = new TreeSet<>();

    ParseState()
    {
        this.condition = null;
    }

    ParseState(ParseCondition condition)
    {
        this.condition = condition;
        this.token = null;
    }

    ParseState next(ParseState next)
    {
        nextStates.add(next);
        return next;
    }

    ParseState txt(String next)
    {
        ParseState last = this;
        final int length = next.length();
        for (int cpi = 0; cpi < length; )
        {
            final int cp = next.codePointAt(cpi);
            last = last.next(new ParseState(new SingleChar(cp)));
            cpi += Character.charCount(cp);
        }

        return last;
    }

    ParseState chr(char chr)
    {
        return next(new ParseState(new SingleChar(chr)));
    }

    ParseState ws()
    {
        return next(new ParseState(new Whitespace()));
    }
}

public class Parser
{
    private ParseState beginStartStates;
    private String[] beginStarts;
    private String[] beginEnds;

    public Parser()
    {
        beginStartStates = new ParseState();
        beginStartStates.txt("<!--").ws().txt("gap").ws().identifier();
        beginStarts = new String[]{"<!--", "${", "r=\"", "/*"};
        beginEnds = new String[]{"-->", "}", "\"", "*/"};

        int[][] beginStartsInts = new int[beginStarts.length][];

        for (int i = 0; i < beginStarts.length; ++i)
        {
            String entry = beginStarts[i];

            ArrayIntList ints = new ArrayIntList();
            final int length = entry.length();
            for (int cpi = 0; cpi < length; )
            {
                final int cp = entry.codePointAt(cpi);
                ints.add(cp);
                cpi += Character.charCount(cp);
            }

            beginStartsInts[i] = ints.toArray();
        }
    }

    public Parsed parse(String content)
    {
        Parsed result = new Parsed(this);

        int[] buffer = new int[20];
        clearBuffer(buffer);

        final int length = content.length();
        for (int i = 0; i < length; )
        {
            final int cp = content.codePointAt(i);

            i += Character.charCount(cp);
        }

        return result;
    }

    private void clearBuffer(int[] buffer)
    {
        Arrays.fill(buffer, 0);
    }

    enum TOKENS
    {
        HTML_BEGIN_START
    }
}
