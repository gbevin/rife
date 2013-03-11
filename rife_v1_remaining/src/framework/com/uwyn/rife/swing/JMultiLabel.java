/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JMultiLabel.java 3957 2008-05-26 07:57:51Z gbevin $
 */
package com.uwyn.rife.swing;

import com.uwyn.rife.tools.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collection;

public class JMultiLabel extends JPanel implements ComponentListener
{
	private static final long serialVersionUID = 4732617870135188398L;
	private String			mMessageText = null;
	private Font			mMessageFont = null;
	private Color			mMessageColor = null;
	private int				mWrapWidth = 0;
	private boolean			mAutoWrap = false;
	
	private ArrayList<TextLayout>	mTextLayouts = null;
	private double					mTextWidth = 0;
	private double					mTextHeight = 0;
	
	private int	mCachedWidth = -1;

	public JMultiLabel()
	{
		this("");
	}

	public JMultiLabel(String messageText)
	{
        this(messageText, 0);
    }

	public JMultiLabel(String messageText, int wrapWidth)
	{
        mMessageText = messageText;
		setOpaque(false);
		addComponentListener(this);
        setFont(UIManager.getFont("Label.font"));
		setWrapWidth(wrapWidth);
    }

	public void setMessageText(String messageText)
	{
        mMessageText = messageText;
		mCachedWidth = -1;
	}

	public void setWrapWidth(int wrapWidth)
	{
        mWrapWidth = wrapWidth;
		mCachedWidth = -1;
	}
	
	public void setAutoWrap(boolean autoWrap)
	{
		mAutoWrap = autoWrap;
	}

	public void setFont(Font font)
	{
		mCachedWidth = -1;
		mMessageFont = font;
	}

	public Font getFont()
	{
		return mMessageFont;
	}

	public void setColor(Color color)
	{
		mMessageColor = color;
	}

	public Dimension getPreferredSize()
	{
		Graphics2D g2 =(Graphics2D)getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		ensureValidLayouts(g2);
		
		Dimension	dimension = null;
		Border		border = getBorder();
		if (null == border)
		{
			dimension = new Dimension((int)Math.ceil(mTextWidth), (int)Math.ceil(mTextHeight));
		}
		else
		{
			Insets border_insets = border.getBorderInsets(this);
			dimension = new Dimension(((int)Math.ceil(mTextWidth)) + border_insets.left + border_insets.right, ((int)Math.ceil(mTextHeight)) + border_insets.top + border_insets.bottom);
		}

		return dimension;
	}
	
	private int getWrappingWidth()
	{
		if (mWrapWidth <= 0)
		{
			if (!mAutoWrap)
			{
				return Integer.MAX_VALUE;
			}
			
			Border	border = getBorder();
			int		border_horiz = 0;
			if (border != null)
			{
				Insets border_insets = border.getBorderInsets(this);
				border_horiz = border_insets.left + border_insets.right;
			}
			
			int width = super.getWidth() -border_horiz;
			if (width <= 0)
			{
				width = Integer.MAX_VALUE;
			}
			
			return width;
		}
		
		return mWrapWidth;
	}

	public Dimension getMinimumSize()
	{
		Dimension	minimum_size = super.getMinimumSize();
		Dimension	preferred_size = getPreferredSize();
		return new Dimension((int)Math.ceil(minimum_size.getWidth()), (int)Math.ceil(preferred_size.getHeight()));
	}

	public Dimension getMaximumSize()
	{
		return super.getMaximumSize();
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		paintMessage((Graphics2D)g);
	}
	
	private void addTextLayout(TextLayout layout)
	{
		mTextLayouts.add(layout);
		
		// update the global text dimensions
		if (mTextHeight != 0)
		{
			mTextHeight += layout.getLeading();
		}
		double layout_width = layout.getBounds().getWidth()+1;
		if (layout_width > mTextWidth)
		{
			mTextWidth = layout_width;
		}
		mTextHeight += layout.getAscent()+layout.getDescent();
	}

	private void ensureValidLayouts(Graphics2D g2) 
	{
		int	wrapping_width = getWrappingWidth();

		if (mCachedWidth == wrapping_width)
		{
			return;
		}
		
		mTextLayouts = new ArrayList<TextLayout>();
		
		FontRenderContext	render_context = g2.getFontRenderContext();
		if (null == mMessageText ||
			0 == mMessageText.length())
		{
			TextLayout layout = new TextLayout(" ", mMessageFont, render_context);
			mTextLayouts.add(layout);
			
			// update the global text dimensions
			mTextWidth = 0;
			mTextHeight = 0;
		}
		else
		{
			// Get the newlines and create a new text that has them stripped away
			Collection<String>	lines = StringUtils.split(mMessageText, "\n");
			int					newline_count = lines.size();
			int					character_count = 0;
			for (String line : lines)
			{
				character_count += line.length();
			}
			
			TextLayout	layout = null;
			// It might be possible that the string only contains linebreaks
			// the stripped string will then be empty and no linebreakmeasurer
			// can be used
			if (0 == character_count)
			{
				for (int i = 0; i < newline_count; i++)
				{
					layout = new TextLayout(" ", getFont(), render_context);
					addTextLayout(layout);
				}
			}
			else
			{
				mTextWidth = 0;
				mTextHeight = 0;
				
				for (String line : lines)
				{
					// handle empty newlines
					if (0 == line.length())
					{
						layout = new TextLayout(" ", getFont(), render_context);
						addTextLayout(layout);
					}
					else
					{
						// Create an Attributed String
						AttributedString attributed_string = new AttributedString(line);
						attributed_string.addAttribute(TextAttribute.FONT, getFont());
						
						// Get the iterator.
						AttributedCharacterIterator	iterator = attributed_string.getIterator();
						
						// Create the layouts
						LineBreakMeasurer	measurer = new LineBreakMeasurer(iterator, render_context);
						while (measurer.getPosition() < iterator.getEndIndex())
						{
							try
							{
								layout = measurer.nextLayout(wrapping_width);
							}
							// fix around jdk bug
							catch (ArrayIndexOutOfBoundsException e)
							{
								break;
							}
							
							addTextLayout(layout);
						}
					}
				}
			}
		}
		
		// Cache the width so that all these calculations are not needlessly
		// done
		if (Integer.MAX_VALUE != wrapping_width)
		{
			mCachedWidth = -1;
		}
		else
		{
			mCachedWidth = wrapping_width;
		}
	}
	
	public boolean paintMessage(Graphics2D g2)
	{
		Object	previous_aliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Object	previous_textaliasing = g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		Color	previous_color = g2.getColor();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(mMessageColor);

		ensureValidLayouts(g2);
		
		Border	border = getBorder();
		int		border_x = 0;
		int		border_y = 0;
		if (border != null)
		{
			Insets border_insets = border.getBorderInsets(this);
			border_x = border_insets.left;
			border_y = border_insets.top;
		}
		boolean all_lines_shown = true;
		if (mTextLayouts != null &&
			mTextLayouts.size() > 0)
		{
			float	x = border_x;
			float	y1 = border_y;
			float	y2 = -1;
			for (TextLayout layout : mTextLayouts)
			{
				y1 += layout.getAscent();
				y2 = y1+layout.getDescent();
				
				// don't draw lines that can't be rendered completely
				int height = getHeight();
				if (y2 > height)
				{
					all_lines_shown = false;
					break;
				}
				
				layout.draw(g2, x, y1);
				
				y1 = y2+layout.getLeading();
			}
		}
		
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, previous_textaliasing);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, previous_aliasing);
		g2.setColor(previous_color);
		
		return all_lines_shown;
	}
	
	public void componentMoved(ComponentEvent e)
	{
	}
	
	public void componentHidden(ComponentEvent e)
	{
	}
	
	public void componentShown(ComponentEvent e)
	{
	}
	
	public void componentResized(ComponentEvent e)
	{
		if (mAutoWrap)
		{
			mWrapWidth = 0;
			mCachedWidth = -1;
		}
	}
}


