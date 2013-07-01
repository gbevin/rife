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
    final private List<ParseStep> nextSteps;
    final private ParseDirectiveChange directive;
    ParserToken token;

    ParseStep()
    {
        this.condition = null;
        this.nextSteps = new ArrayList<>();
        this.directive = null;
        this.token = null;
    }

    ParseStep(ParseCondition condition)
    {
        this.condition = condition;
        this.nextSteps = new ArrayList<>();
        this.directive = null;
        this.token = null;
    }

    ParseStep(ParseDirectiveChange directive)
    {
        this.condition = null;
        this.nextSteps = new ArrayList<>();
        this.directive = directive;
        this.token = null;
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
        return next(ParseSteps.chr(chr));
    }

    ParseStep ws()
    {
        return next(ParseSteps.ws());
    }

    ParseStep optional(ParseStep step)
    {
        return next(new ParseStep(new EnterDirective(ParseDirective.OPTIONAL))).next(step).next(new ParseStep(new LeaveDirective(ParseDirective.OPTIONAL)));
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

    ParseDirectiveChange getDirective()
    {
        return directive;
    }
}
