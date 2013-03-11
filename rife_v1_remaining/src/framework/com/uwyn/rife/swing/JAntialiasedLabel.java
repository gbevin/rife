/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JAntialiasedLabel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.JLabel;

public class JAntialiasedLabel extends JLabel
{
	private static final long serialVersionUID = -5909786319590631081L;

	public JAntialiasedLabel()
	{
		super();
	}

	public JAntialiasedLabel(Icon image)
	{
		super(image);
	}
	
	public JAntialiasedLabel(Icon image, int horizontalAlignment)
	{
		super(image, horizontalAlignment);
	}
	
	public JAntialiasedLabel(String text)
	{
		super(text);
	}

	public JAntialiasedLabel(String text, Icon icon, int horizontalAlignment)
	{
		super(text, icon, horizontalAlignment);
	}
	
	public JAntialiasedLabel(String text, int horizontalAlignment)
	{
		super(text, horizontalAlignment);
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g2d);
	}
}


