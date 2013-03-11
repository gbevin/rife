/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OpenBeanshell.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui.commands;

import bsh.EvalError;
import bsh.Interpreter;
import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.swing.Command;
import com.uwyn.rife.swing.JDialogSystemError;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class OpenBeanshell implements Command
{
	public void execute()
	{
		(new RealExecute()).start();
	}

	private class RealExecute extends Thread
	{
		public void run()
		{
			final String beanshell_desktop_path = "shared/scripts/rife_desktop.bsh";

			try
			{
				(ResourceFinderClasspath.getInstance()).useStream(beanshell_desktop_path, new InputStreamUser() {
						public Object useInputStream(InputStream stream)
						throws InnerClassException
						{
							InputStreamReader input_stream_reader = null;
							BufferedReader buffered_reader = null;
								
							if(stream != null)
							{
								try
								{
									input_stream_reader = new InputStreamReader(stream, "ISO8859_1");
				
									if(input_stream_reader != null)
									{
										buffered_reader = new BufferedReader(input_stream_reader);
				
										if(buffered_reader != null)
										{
											try
											{
												new Interpreter().eval(buffered_reader);
												return null;
											}
											catch (EvalError e)
											{
												(new JDialogSystemError(Rife.getMainFrame(),
													"OpenBeanshell.RealExecute.run() : " +
													"Error while evaluating the beanshell desktop script : " +
													ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
													return null;
											}
										}
									}
									else
									{
										(new JDialogSystemError(Rife.getMainFrame(),
											"OpenBeanshell.RealExecute.run() : " +
											"Couldn't create the buffered reader for the beanshell desktop script resource at '" +
											beanshell_desktop_path + "'.")).setVisible(true);
										return null;
									}
								}
								catch (UnsupportedEncodingException e)
								{
									(new JDialogSystemError(Rife.getMainFrame(),
										"OpenBeanshell.RealExecute.run() : " +
										"Error while creating the inputstream reader for the beanshell desktop script resource at '" +
										beanshell_desktop_path + "' : " + ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
									return null;
								}
							}
							else
							{
								(new JDialogSystemError(Rife.getMainFrame(),
									"OpenBeanshell.RealExecute.run() : " +
									"Couldn't open the beanshell desktop script resource at '" + beanshell_desktop_path + "'.")).setVisible(true);
								return null;
							}
							(new JDialogSystemError(Rife.getMainFrame(),
								"OpenBeanshell.RealExecute.run() : Couldn't open the beanshell desktop.")).setVisible(true);
							
							return null;
						}
					});
			}
			catch (ResourceFinderErrorException e)
			{
				(new JDialogSystemError(Rife.getMainFrame(),
					"OpenBeanshell.RealExecute.run() : " +
					"Error while evaluating the beanshell desktop script : " +
					ExceptionUtils.getExceptionStackTrace(e))).setVisible(true);
					return;
			}
			return;
		}
	}
}



