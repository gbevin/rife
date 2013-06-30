/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

interface ParseCondition extends Cloneable
{
    boolean isValid(int codePoint);

    boolean isRepeatable();
}
