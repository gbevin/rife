package com.uwyn.rife.instrument.exceptions;

/**
 * This exception is thrown and when the bytecode for a class couldn't be
 * found.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3811 $
 * @since 1.6
 */
public class ClassBytesNotFoundException extends Exception
{
	private static final long serialVersionUID = -7419114745142128756L;
	
	private String  mFilename;

	public ClassBytesNotFoundException(String filename, Throwable cause)
	{
		super("Unexpected exception while loading the bytes of class file '" + filename + "'.", cause);

		mFilename = filename;
	}

	public String getFilename()
	{
		return mFilename;
	}
}
