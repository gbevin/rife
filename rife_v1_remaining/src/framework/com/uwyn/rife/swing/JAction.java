/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JAction.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import com.uwyn.rife.swing.Command;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

public class JAction extends AbstractAction
{
	private static final long serialVersionUID = 8035921004854064778L;
	
	private Command mCommand = null;

    private JAction()
    {
    }

	public JAction(Command command, String name)
	{
        this(command, name, null, null, null, null);
    }

	public JAction(Command command, String name, char mnemonic)
	{
        this(command, name, new Integer(mnemonic), null, null, null);
    }

	public JAction(Command command, String name, Integer mnemonic)
	{
        this(command, name, mnemonic, null, null, null);
    }

	public JAction(Command command, String name, char mnemonic, KeyStroke accelerator)
	{
        this(command, name, new Integer(mnemonic), accelerator, null, null);
    }

	public JAction(Command command, String name, Integer mnemonic, KeyStroke accelerator)
	{
        this(command, name, mnemonic, accelerator, null, null);
    }

	public JAction(Command command, String name, char mnemonic, KeyStroke accelerator, Icon icon)
	{
        this(command, name, new Integer(mnemonic), accelerator, icon, null);
    }

	public JAction(Command command, String name, Integer mnemonic, KeyStroke accelerator, Icon icon)
	{
        this(command, name, mnemonic, accelerator, icon, null);
    }

	public JAction(Command command, Icon icon)
	{
        this(command, null, null, null, icon, null);
    }

	public JAction(Command command, Icon icon, String shortDescription)
	{
        this(command, null, null, null, icon, shortDescription);
    }

	public JAction(Command command, String name, char mnemonic, KeyStroke accelerator, Icon icon, String shortDescription)
	{
		this(command, name, new Integer(mnemonic), accelerator, icon, shortDescription);
    }

	public JAction(Command command, String name, Integer mnemonic, KeyStroke accelerator, Icon icon, String shortDescription)
	{
		super();

        setCommand(command);

        if (null != name)
        {
            setName(name);
        }
        if (null != mnemonic)
        {
            setMnemonic(mnemonic);
        }
        if (null != accelerator)
        {
            setAccelerator(accelerator);
        }
        if (null != icon)
        {
            setIcon(icon);
        }
        if (null != shortDescription)
        {
            setShortDescription(shortDescription);
        }
	}

    public void setCommand(Command command)
    {
		mCommand = command;
    }

    public void setName(String name)
    {
		putValue(Action.NAME, name);
    }

    public void setMnemonic(char mnemonic)
    {
		setMnemonic(new Integer(mnemonic));
    }

    public void setMnemonic(Integer mnemonic)
    {
		putValue(Action.MNEMONIC_KEY, mnemonic);
    }

    public void setAccelerator(KeyStroke accelerator)
    {
		putValue(Action.ACCELERATOR_KEY, accelerator);
    }

    public void setIcon(Icon icon)
    {
		putValue(Action.SMALL_ICON, icon);
    }

    public void setShortDescription(String shortDescription)
    {
		putValue(Action.SHORT_DESCRIPTION, shortDescription);
    }

	public void actionPerformed(ActionEvent e)
	{
		if (null != mCommand)
		{
			new Thread() {
				public void run()
				{
					mCommand.execute();
				}
			}.start();
		}
	}
}


