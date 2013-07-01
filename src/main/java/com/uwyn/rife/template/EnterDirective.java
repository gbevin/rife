/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

public class EnterDirective implements ParseDirectiveChange
{
    final ParseDirective directive;

    public EnterDirective(ParseDirective directive)
    {
        this.directive = directive;
    }

    @Override
    public void applyDirective(ParseTrail trail)
    {
        trail.addDirective(directive);
    }
}
