/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import com.uwyn.rife.datastructures.collections.primitives.ArrayIntList;
import com.uwyn.rife.datastructures.collections.primitives.IntList;

enum TemplateToken
{
    BEGIN_START, WHITESPACE, GAP
}

interface ParseCondition extends Cloneable
{
    boolean isValid(int codePoint);

    boolean isRepeatable();
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

    @Override
    public boolean isRepeatable()
    {
        return false;
    }
}

class Whitespace implements ParseCondition
{
    @Override
    public boolean isValid(int codePoint)
    {
        return Character.isWhitespace(codePoint);
    }

    @Override
    public boolean isRepeatable()
    {
        return true;
    }
}

class IdentifierStart implements ParseCondition
{
    @Override
    public boolean isValid(int codePoint)
    {
        return Character.isJavaIdentifierStart(codePoint);
    }

    @Override
    public boolean isRepeatable()
    {
        return false;
    }
}

class IdentitifierPart implements ParseCondition
{
    @Override
    public boolean isValid(int codePoint)
    {
        return Character.isJavaIdentifierPart(codePoint);
    }

    @Override
    public boolean isRepeatable()
    {
        return true;
    }
}

class ParseStep
{
    final private ParseCondition condition;
    private ParseStep nextStep;

    ParseStep()
    {
        this.condition = null;
        this.nextStep = null;
    }

    ParseStep(ParseCondition condition)
    {
        this.condition = condition;
        this.nextStep = null;
    }

    ParseStep next(ParseStep next)
    {
        assert nextStep == null;

        nextStep = next;
        return next;
    }

    ParseStep txt(String next)
    {
        ParseStep last = this;
        final int length = next.length();
        for (int cpi = 0; cpi < length; )
        {
            final int cp = next.codePointAt(cpi);
            last = last.next(new ParseStep(new SingleChar(cp)));
            cpi += Character.charCount(cp);
        }

        return last;
    }

    ParseStep chr(char chr)
    {
        return next(new ParseStep(new SingleChar(chr)));
    }

    ParseStep ws()
    {
        return next(new ParseStep(new Whitespace()));
    }

    ParseStep identifier()
    {
        return next(new ParseStep(new IdentifierStart())).next(new ParseStep(new IdentitifierPart()));
    }

    public ParseCondition getCondition()
    {
        return condition;
    }

    public ParseStep getNextStep()
    {
        return nextStep;
    }
}

class ParseState
{
    private ParseStep current = null;
    private ParseStep lastValid = null;
    private boolean valid = true;

    ParseState(ParseStep begin)
    {
        this.current = begin;
    }

    boolean process(int codePoint)
    {
        boolean result = true;

        while (true)
        {
            if (current == null)
            {
                return false;
            }

            ParseStep step = current;
            ParseCondition condition = step.getCondition();
            if (condition.isValid(codePoint))
            {
                lastValid = current;

                if (!condition.isRepeatable())
                {
                    current = current.getNextStep();
                }
                break;
            }
            else if (step == lastValid && condition.isRepeatable())
            {
                lastValid = null;
                current = current.getNextStep();
            }
            else
            {
                lastValid = null;
                valid = false;
                result = false;
                break;
            }
        }

        return result;
    }

    boolean isValid()
    {
        return valid;
    }
}

public class Parser
{
    private ParseStep begin;

    public Parser()
    {
        begin = new ParseStep();
        begin.txt("<!--").ws().txt("gap").ws().identifier().ws().txt("/-->");
    }

    public Parsed parse(String content)
    {
        Parsed result = new Parsed(this);
        ParseState state = new ParseState(begin.getNextStep());

        final int length = content.length();
        for (int i = 0; i < length; )
        {
            final int cp = content.codePointAt(i);

            if (!state.process(cp))
            {
                break;
            }

            i += Character.charCount(cp);
        }

        return result;
    }
}
