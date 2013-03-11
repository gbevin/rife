/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Colors.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import java.awt.Color;

public abstract class Colors
{
	public static final Color	ERROR_FOREGROUND = new Color(132, 33, 33);
	public static final Color	ERROR_BACKGROUND = new Color(247, 138, 118);
	public static final Color	CONFIRM_BACKGROUND = new Color(255, 179, 97);
	public static final Color	INFO_FOREGROUND = new Color(33, 132, 33);
	public static final Color	INFO_BACKGROUND = new Color(158, 229, 130);
	public static final Color	GRAY_BACKGROUND = new Color(240, 240, 240);
	public static final Color	BORDER_HIGHLIGHT = Color.lightGray;
	public static final Color	BORDER_SHADOW = Color.lightGray.darker();
}

