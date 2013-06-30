/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import java.util.ArrayList;
import java.util.List;

class ParseStep
{
    final private ParseCondition condition;
    final private List<ParseStep> nextSteps = new ArrayList<>();
    private ParserToken token = null;

    ParseStep()
    {
        this.condition = null;
    }

    ParseStep(ParseCondition condition)
    {
        this.condition = condition;
    }

    ParseStep next(ParseStep next)
    {
        nextSteps.add(next);
        return next;
    }

    ParseStep token(ParserToken token)
    {
        if (this.token != null) throw new IllegalArgumentException("tokens should only be set once");

        this.token = token;
        return this;
    }

    ParseStep txt(String txt)
    {
        ParseStep last = this;
        final int length = txt.length();
        for (int cpi = 0; cpi < length; )
        {
            final int cp = txt.codePointAt(cpi);
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

    ParseCondition getCondition()
    {
        return condition;
    }

    List<ParseStep> getNextSteps()
    {
        return nextSteps;
    }

    ParserToken getToken()
    {
        return token;
    }
}
