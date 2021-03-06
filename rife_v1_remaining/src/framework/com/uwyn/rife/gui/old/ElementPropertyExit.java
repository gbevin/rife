/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementPropertyExit.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.gui.old.DynamicMenuBuilder;
import com.uwyn.rife.swing.JDialogError;
import com.uwyn.rife.swing.MouseMotionEventTranslator;
import com.uwyn.rife.tools.Localization;

class ElementPropertyExit extends ElementProperty
{
	public ElementPropertyExit(Element element, String name)
	{
		super(element, name);
	}
	
	public void draw(Graphics2D g2d)
	{
		Rectangle clip = g2d.getClipBounds();
		if(clip == null ||
		   clip.intersects(getNameBounds()))
		{
			g2d.setColor(getColor());
			g2d.setFont(getDrawFont());
			g2d.drawString(getName(),
						   (int)(getNameBounds().getX()),
						   (int)(getNameBounds().getY()+getNameBounds().getHeight()-getElement().getElementStyleScaled().mExitFontLineMetrics.getDescent()));
		}
	}

	public Color getColor()
	{
		return getElement().getElementStyleScaled().mExitTextColor;
	}

	public Font getDrawFont()
	{
		return getElement().getElementStyleScaled().mExitFont;
	}

	public Font getEditFont()
	{
		return getElement().getElementStyleScaled().mExitFont;
	}

	public int getEditAlignment()
	{
		return JTextField.RIGHT;
	}

	public Rectangle getEditRectangle(Point clickedPoint)
	{
		Rectangle2D hotspot_bounds = getHotSpot().getBounds2D();
		ElementStyle element_style = getElement().getElementStyleScaled();
		int exit_width = (int)(Math.ceil(hotspot_bounds.getWidth())-Math.ceil(element_style.mElementBorderWidth));
		int exit_height = (int)(Math.floor(hotspot_bounds.getHeight())-Math.ceil(element_style.mElementBorderWidth*2));
		int exit_x = (int)(Math.floor(hotspot_bounds.getX()));
		int exit_y = (int)(Math.floor(hotspot_bounds.getY())+Math.floor(element_style.mElementBorderWidth));
		return new Rectangle(exit_x, exit_y, exit_width, exit_height);
	}

	public JDialogError getUnicityErrorDialog()
	{
		return new JDialogError(Rife.getMainFrame(), "rife.dialog.exitexists.title", Localization.getString("rife.dialog.exitexists.message"));
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
