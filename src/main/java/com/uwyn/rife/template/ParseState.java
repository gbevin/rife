/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import java.util.ArrayList;
import java.util.List;

class ParseState
{
    private List<ParseTrail> trails = new ArrayList<>();

    ParseState(ParseStep begin)
    {
        for (ParseStep step : begin.getNextSteps())
        {
            trails.add(new ParseTrail(step));
        }
    }

    boolean process(int codePoint)
    {
        boolean result = false;

        List<ParseTrail> current_trails = new ArrayList<>(trails);
        if (current_trails.isEmpty())
        {
            return false;
        }

        for (ParseTrail trail : current_trails)
        {
            while (true)
            {
                ParseStep step = trail.current;
                ParseCondition condition = step.getCondition();
                if (condition.isValid(codePoint))
                {
                    trail.lastValid = trail.current;
                    result |= true;

                    if (!condition.isRepeatable())
                    {
                        handleValidStep(trail, step);
                    }

                    break;
                }
                else if (step == trail.lastValid && condition.isRepeatable())
                {
                    trail.lastValid = null;

                    handleValidStep(trail, step);
                }
                else
                {
                    trail.lastValid = null;
                    trail.current = null;

                    trails.remove(trail);

                    result |= false;

                    break;
                }
            }
        }

        return result;
    }

    private void handleValidStep(ParseTrail trail, ParseStep step)
    {
        trail.addToken(step);
        advanceToNextSteps(trail, step);
    }

    private void advanceToNextSteps(ParseTrail trail, ParseStep step)
    {
        List<ParseStep> next_steps = step.getNextSteps();
        for (int i = 0; i < next_steps.size(); ++i)
        {
            ParseStep next = next_steps.get(i);
            if (0 == i)
            {
                trail.current = next;
            }
            else
            {
                trails.add(trail.splitTrail(next));
            }
        }
    }

    private class ParseTrail
    {
        private ParseStep current = null;
        private ParseStep lastValid = null;
        private List<ParserToken> tokens = new ArrayList<>();

        ParseTrail(ParseStep start)
        {
            this.current = start;
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
    }
}
