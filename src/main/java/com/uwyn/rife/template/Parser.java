/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import static com.uwyn.rife.template.ParseSteps.ws;

public class Parser
{
    private ParseStep begin;

    public Parser()
    {
        begin = new ParseStep();
        begin.txt("<!--").optional(ws()).txt("gap").ws().identifier().optional(ws()).txt("/-->").token(ParserToken.GAP_SHORT);
        begin.txt("<!--").optional(ws()).txt("gap").ws().identifier().optional(ws()).txt("-->").token(ParserToken.GAP_BEGIN);
        begin.txt("<!--/").optional(ws()).txt("gap").optional(ws()).txt("-->").token(ParserToken.GAP_TERM);
        begin.txt("<!--").optional(ws()).txt("snip").ws().identifier().optional(ws()).txt("-->").token(ParserToken.SNIP_BEGIN);
        begin.txt("<!--/").optional(ws()).txt("snip").optional(ws()).txt("-->").token(ParserToken.SNIP_TERM);
        begin.txt("<!--").optional(ws()).txt("load").ws().identifier().optional(ws()).txt("/-->").token(ParserToken.LOAD);
        begin.txt("<!--").optional(ws()).txt("note").optional(ws()).txt("-->").token(ParserToken.NOTE_BEGIN);
        begin.txt("<!--/").optional(ws()).txt("note").optional(ws()).txt("-->").token(ParserToken.NOTE_TERM);
    }

    public Parsed parse(String content)
    {
        Parsed result = new Parsed(this);
        ParseState state = new ParseState(begin);

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
