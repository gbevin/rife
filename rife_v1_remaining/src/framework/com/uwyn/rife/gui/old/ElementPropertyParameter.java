/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementPropertyParameter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.uwyn.rife.swing.MouseMotionEventTranslator;
import com.uwyn.rife.tools.Localization;

abstract class ElementPropertyParameter extends ElementProperty
{
	public ElementPropertyParameter(Element element, String name)
	{
		super(element, name);
	}
	
	public void draw(Graphics2D g2d)
	{
		Rectangle clip = g2d.getClipBounds();
		if(clip == null ||
		   clip.intersects(getNameBounds()))
		{
			Color backgroundColor = null;
			ElementStyle element_style = getElement().getElementStyleScaled();
			if(getElement().isSelected())
			{
				backgroundColor = element_style.mBodyBackgroundColorSelected;
			}
			else
			{
				backgroundColor = element_style.mBodyBackgroundColor;
			}
			element_style.drawParameterText(g2d, getName(), backgroundColor,
											(int)(getNameBounds().getX()+getNameBounds().getWidth()-element_style.mParamFontLineMetrics.getDescent()),
											(int)(getNameBounds().getY()+getNameBounds().getHeight()));
		}
	}

	public Color getColor()
	{
		return getElement().getElementStyleScaled().mParamTextColor;
	}

	public Font getDrawFont()
	{
		return  getElement().getElementStyleScaled().mParamFont;
	}

	public Font getEditFont()
	{
		return getElement().getElementStyleScaled().mParamFont;
	}

	public int getEditAlignment()
	{
		return JTextField.CENTER;
	}

	public Rectangle getEditRectangle(Point clickedPoint)
	{
		ElementStyle element_style = getElement().getElementStyleScaled();
		Rectangle2D title_rect_outside_bounds = getElement().getTitleRectangleOutside().getBounds2D();
		int parameter_width = (int)(Math.floor(title_rect_outside_bounds.getX()+title_rect_outside_bounds.getWidth())-Math.ceil(element_style.mElementBorderWidth)*2);
		int parameter_height = (int)(getEditFont().getLineMetrics("M", element_style.mFontRenderContext).getHeight());
		int parameter_x = (int)Math.ceil(element_style.mElementBorderWidth);
		int parameter_y = (int)(clickedPoint.getY()-parameter_height/2);
		return new Rectangle(parameter_x, parameter_y, parameter_width, parameter_height);
	}

	protected void showPopupMenuReal(JPopupMenu popupMenu, Point clickedPoint)
	{
		DynamicMenuBuilder menu_builder = new DynamicMenuBuilder();
		MouseMotionEventTranslator event_translator = new MouseMotionEventTranslator(getElement());
		JMenuItem menu_item = null;
		
		popupMenu.addMouseMotionListener(event_translator);
		menu_item = menu_builder.addMenuItem(popupMenu, Localization.getString("rife.element.property.popupmenu.edit"), new Edit(clickedPoint), Localization.getChar("rife.element.property.popupmenu.edit.mnemonic"));
		menu_item.addMouseMotionListener(event_translator);
		menu_item = menu_builder.addMenuItem(popupMenu, Localization.getString("rife.element.property.popupmenu.delete"), new Delete(), Localization.getChar("rife.element.property.popupmenu.delete.mnemonic"));
		menu_item.addMouseMotionListener(event_translator);
	}
}
