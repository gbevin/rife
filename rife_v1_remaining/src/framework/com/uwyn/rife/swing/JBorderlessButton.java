/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JBorderlessButton.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A JButton that maintains its borderless look when the look
 * & feel changes.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see JButton
 * @since 1.0
 */
public class JBorderlessButton extends JButton
{
	private static final long serialVersionUID = 6092380900896756322L;

	/**
	 * Creates a button with no set text or icon.
	 * @since 1.0
	 */
	public JBorderlessButton()
	{
		super();
		setStyle();
	}

	/**
	 * Creates a button with an icon.
	 *
	 * @param icon   the Icon image to display on the button
	 * @since 1.0
	 */
	public JBorderlessButton(Icon icon)
	{
		super(icon);
		setStyle();
	}

	/**
	 * Creates a button with text.
	 *
	 * @param text   the text of the button
	 * @since 1.0
	 */
	public JBorderlessButton(String text)
	{
		super(text);
		setStyle();
	}

	/**
	 * Creates a button where properties are taken from the
	 * Action supplied.
	 *
	 * @param a
	 * @since 1.0
	 */
	public JBorderlessButton(Action a)
	{
		super(a);
		setStyle();
	}

	/**
	 * Creates a button with initial text and an icon.
	 *
	 * @param text   the text of the button.
	 * @param icon   the Icon image to display on the button
	 * @since 1.0
	 */
	public JBorderlessButton(String text, Icon icon)
	{
		super(text, icon);
		setStyle();
	}

	/**
	 * Notification from the UIFactory that the L&F
	 * has changed. Maintains the borderless look.
	 *
	 * @see JButton#updateUI()
	 * @since 1.0
	 */
	public void updateUI()
	{
		super.updateUI();
		setStyle();
	}

	/**
	 * Makes this button borderless.
	 * @since 1.0
	 */
	private void setStyle()
	{
		setBorder(null);
	}
}
