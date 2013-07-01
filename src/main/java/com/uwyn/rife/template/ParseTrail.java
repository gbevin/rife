/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ParseTrail
{
    private ParseStep current = null;
    private ParseStep lastValid = null;
    private List<ParserToken> tokens = new ArrayList<>();
    private Map<ParseDirective, Integer> directives = new HashMap<>();

    ParseTrail(ParseStep start)
    {
        this.current = start;
    }

    ParseStep getCurrent()
    {
        return current;
    }

    void setCurrent(ParseStep current)
    {
        this.current = current;
    }

    ParseStep getLastValid()
    {
        return lastValid;
    }

    void setLastValid(ParseStep lastValid)
    {
        this.lastValid = lastValid;
    }

    void addToken(ParseStep step)
    {
        ParserToken token = step.getToken();
        if (token != null)
        {
            tokens.add(token);
        }
    }

    ParseTrail splitTrail(ParseStep step)
    {
        ParseTrail split = new ParseTrail(step);
        split.tokens.addAll(tokens);
        return split;
    }

    void addDirective(ParseDirective directive)
    {
        int count = 1;
        if (directives.containsKey(directive))
        {
            count = directives.get(directive)+1;
        }
        directives.put(directive, count);
    }

    void removeDirective(ParseDirective directive)
    {
        if (directives.containsKey(directive))
        {
            int count = directives.get(directive)-1;
            if (0 == count)
            {
                directives.remove(directive);
            }
            else
            {
                directives.put(directive, count);
            }
        }
    }

    boolean hasDirective(ParseDirective directive)
    {
        return directives.containsKey(directive);
    }
}
