/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Rife.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.gui.old.MainFrame;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.swing.JDialogSystemError;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Rife
{
	private static MainFrame    mMainFrame = null;

	/**
	 * This is the first method that is called when the
	 * Rife application launches.
	 * <p>
	 * It verifies if the correct arguments have been provided.
	 * If this is the case, the initialization procedure
	 * initiates.
	 *
	 * @param args   the string array that contains the parameters that have
	 *               been passed on the commandline
	 * @since 1.0
	 */
	public static void main(String[] args)
	{
        if(verifyArguments(args))
		{
			try
			{
				Rep.initialize(System.getProperty("rep.path"));

				mMainFrame = new MainFrame();
				mMainFrame.setVisible(true);
			}
			catch (Throwable e)
			{
				try
				{
					(new JDialogSystemError(mMainFrame, "main() : Unexpected system exception : "+ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
				}
				catch (Throwable exception2)
				{
					Logger.getLogger("com.uwyn.rife.gui").severe("main() : Unexpected system exception : "+ExceptionUtils.getExceptionStackTrace(e));
				}
				quit();
			}
		}
		else
		{
			quit();
		}
	}

	/**
	 * Verifies if the commandline arguments are valid.
	 *
	 * @param args   the string array that contains the parameters that have
	 *               been passed on the commandline
	 * @return <code>true</code> if the arguments were valid
	 *         <code>false</code> if the arguments were invalid
	 * @since 1.0
	 */
	private static boolean verifyArguments(String[] args)
	{
		return true;
	}

	public static MainFrame getMainFrame()
	{
		return mMainFrame;
	}

	/**
	 * Allows easy changement of the application's look and feel
	 *
	 * @param className string containing the class name of the look and feel that should beset
	 * @since 1.0
	 */
	public static void setLookAndFeel(String className)
	{
		try
		{
			UIManager.setLookAndFeel(className);
			if(mMainFrame != null)
			{
				SwingUtilities.updateComponentTreeUI(mMainFrame);
			}
			Config.getRepInstance().setParameter("LOOK_AND_FEEL", className);
		}
		catch (Throwable e)
		{
			(new JDialogSystemError(Rife.getMainFrame(), "setLookAndFeel() : Error while setting the look & feel to '"+className+"' : "+ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
		}
	}

	public static void quit()
	{
		System.exit(1);
	}
}
