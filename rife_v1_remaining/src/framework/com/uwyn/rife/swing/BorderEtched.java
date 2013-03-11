/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BorderEtched.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

public class BorderEtched extends AbstractBorder
{
	private static final long serialVersionUID = -1065186910206819539L;

	private int mEdgeType = 0;

	public static final int RAISED = 0;
	public static final int LOWERED = 1;

	public BorderEtched()
	{
		this(RAISED);
	}

	public BorderEtched(int edgeType)
	{
		mEdgeType = edgeType;
	}

	public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height)
	{
		Graphics new_graphics = graphics.create();

		if (null != new_graphics)
		{
			try
			{
				new_graphics.translate(x, y);

				Color background_color = null;
				Color shadow_color = null;
				Color highlight_color = null;

				if (component.isEnabled())
				{
					background_color = new Color(205, 206, 205);
					shadow_color = Colors.BORDER_SHADOW;
					highlight_color = Color.white;
				}
				else
				{
					background_color = new Color(205, 206, 205);
					shadow_color = new Color(156, 153, 156);
					highlight_color = background_color;
				}

				new_graphics.setColor(background_color);
				new_graphics.drawRect(2, 2, width - 5, height - 5);
				new_graphics.drawRect(3, 3, width - 7, height - 7);

				if (RAISED == mEdgeType)
				{
					new_graphics.setColor(shadow_color);
					new_graphics.drawRect(0, 0, width - 2, height - 2);

					new_graphics.setColor(highlight_color);

					new_graphics.drawLine(width - 3, 1, 1, 1);
					new_graphics.drawLine(1, 1, 1, height - 3);
					new_graphics.drawLine(0, height - 1, width - 1, height - 1);
					new_graphics.drawLine(width - 1, height - 1, width - 1, 0);

					new_graphics.drawLine(width - 7, 5, 5, 5);
					new_graphics.drawLine(5, 5, 5, height - 6);
					new_graphics.drawLine(width - 5, 4, width - 5, height - 5);
					new_graphics.drawLine(width - 5, height - 5, 4, height - 5);

					new_graphics.setColor(shadow_color);
					new_graphics.drawRect(4, 4, width - 10, height - 10);
				}
				else if (LOWERED == mEdgeType)
				{
					new_graphics.setColor(shadow_color);
					new_graphics.drawRect(1, 1, width - 2, height - 2);

					new_graphics.setColor(highlight_color);

					new_graphics.drawLine(width - 1, 0, 0, 0);
					new_graphics.drawLine(0, 0, 0, height - 1);
					new_graphics.drawLine(width - 2, 2, width - 2, height - 2);
					new_graphics.drawLine(width - 2, height - 2, 2, height - 2);

					new_graphics.drawLine(width - 5, 4, 4, 4);
					new_graphics.drawLine(4, 4, 4, height - 5);
					new_graphics.drawLine(width - 6, 6, width - 6, height - 6);
					new_graphics.drawLine(width - 6, height - 6, 6, height - 6);

					new_graphics.setColor(shadow_color);
					new_graphics.drawRect(5, 5, width - 10, height - 10);
				}
			}
			finally
			{
				new_graphics.dispose();
			}
		}
	}

	public Insets getBorderInsets(Component component)
	{
		return getBorderInsets(component, new Insets(0, 0, 0, 0));
	}

	public Insets getBorderInsets(Component component, Insets insets)
	{
		insets.top = 6;
		insets.bottom = 6;
		insets.left = 6;
		insets.right = 6;

		return insets;
	}
}

