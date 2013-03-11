/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EditorPaneToolAction.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui.actions;

import javax.swing.*;

import com.uwyn.rife.gui.ui.EditorPane;
import com.uwyn.rife.swing.Command;
import com.uwyn.rife.swing.JAction;

public abstract class EditorPaneToolAction extends JAction
{
    private EditorPane mEditorPane = null;

	public EditorPaneToolAction(Command command, String name)
	{
        this(command, name, null, null, null, null);
    }

	public EditorPaneToolAction(Command command, String name, char mnemonic)
	{
        this(command, name, new Integer(mnemonic), null, null, null);
    }

	public EditorPaneToolAction(Command command, String name, Integer mnemonic)
	{
        this(command, name, mnemonic, null, null, null);
    }

	public EditorPaneToolAction(Command command, String name, char mnemonic, KeyStroke accelerator)
	{
        this(command, name, new Integer(mnemonic), accelerator, null, null);
    }

	public EditorPaneToolAction(Command command, String name, Integer mnemonic, KeyStroke accelerator)
	{
        this(command, name, mnemonic, accelerator, null, null);
    }

	public EditorPaneToolAction(Command command, String name, char mnemonic, KeyStroke accelerator, Icon icon)
	{
        this(command, name, new Integer(mnemonic), accelerator, icon, null);
    }

	public EditorPaneToolAction(Command command, String name, Integer mnemonic, KeyStroke accelerator, Icon icon)
	{
        this(command, name, mnemonic, accelerator, icon, null);
    }

	public EditorPaneToolAction(Command command, Icon icon)
	{
        this(command, null, null, null, icon, null);
    }

	public EditorPaneToolAction(Command command, Icon icon, String shortDescription)
	{
        this(command, null, null, null, icon, shortDescription);
    }

	public EditorPaneToolAction(Command command, String name, Integer mnemonic, KeyStroke accelerator, Icon icon, String shortDescription)
    {
		super(command, name, mnemonic, accelerator, icon, shortDescription);
    }

	public void setEditorPane(EditorPane pane)
    {
		mEditorPane = pane;
    }

	public EditorPane getEditorPane()
    {
        return mEditorPane;
    }
}


