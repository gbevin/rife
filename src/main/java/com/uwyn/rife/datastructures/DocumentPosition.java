/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.datastructures;

public class DocumentPosition implements Cloneable
{
    private String lineContent;
    private int line = -1;
    private int column = -1;

    public DocumentPosition(String lineContent, int line, int column)
    {
        assert lineContent != null;
        assert line >= 0;
        assert column >= 0;

        this.lineContent = lineContent;
        this.line = line;
        this.column = column;
    }

    public String getLineContent()
    {
        return lineContent;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (null == other)
        {
            return false;
        }

        if (!(other instanceof DocumentPosition))
        {
            return false;
        }

        DocumentPosition other_documentposition = (DocumentPosition)other;

        return lineContent.equals(other_documentposition.lineContent) &&
               line == other_documentposition.line &&
               column == other_documentposition.column;
    }

    public DocumentPosition clone()
    throws CloneNotSupportedException
    {
        return (DocumentPosition)super.clone();
    }

    public int hashCode()
    {
        return lineContent.hashCode() * line * column;
    }
}
