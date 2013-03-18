/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template.antlr;

public class TemplateParsingListener extends TemplateParserBaseListener
{
    @Override
    public void enterContent(TemplateParser.ContentContext ctx)
    {
        super.enterContent(ctx);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void exitContent(TemplateParser.ContentContext ctx)
    {
        super.exitContent(ctx);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void enterElement(TemplateParser.ElementContext ctx)
    {
        super.enterElement(ctx);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void exitElement(TemplateParser.ElementContext ctx)
    {
        System.out.println("exitElement "+ctx.toString());
        super.exitElement(ctx);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void enterDocument(TemplateParser.DocumentContext ctx)
    {
        super.enterDocument(ctx);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void exitDocument(TemplateParser.DocumentContext ctx)
    {
        super.exitDocument(ctx);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void enterChardata(TemplateParser.ChardataContext ctx)
    {
        System.out.println("enterChardata "+ctx.TEXT().getText());
        super.enterChardata(ctx);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void exitChardata(TemplateParser.ChardataContext ctx)
    {
        System.out.println("exitChardata "+ctx.TEXT().getText());
        super.exitChardata(ctx);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
