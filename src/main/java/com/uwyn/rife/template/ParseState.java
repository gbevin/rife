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
                ParseStep step = trail.getCurrent();

                // handle parse directives
                ParseDirectiveChange directive = step.getDirective();
                if (directive != null)
                {
                    directive.applyDirective(trail);
                    advanceToNextSteps(trail, step);
                    continue;
                }

                // handle parse conditions
                ParseCondition condition = step.getCondition();
                if (condition.isValid(codePoint))
                {
                    trail.setLastValid(trail.getCurrent());
                    result |= true;

                    if (!condition.isRepeatable())
                    {
                        handleValidStep(trail, step);
                    }

                    break;
                }
                else if (trail.hasDirective(ParseDirective.OPTIONAL) ||
                         step == trail.getLastValid() && condition.isRepeatable())
                {
                    trail.setLastValid(null);

                    handleValidStep(trail, step);
                }
                else
                {
                    trail.setLastValid(null);
                    trail.setCurrent(null);

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
                trail.setCurrent(next);
            }
            else
            {
                trails.add(trail.splitTrail(next));
            }
        }
    }
}

