/*
 * ReflectException.java --
 *
 *	The file implements the handling of the Exception's caught
 *	while invoking the Reflection API.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: ReflectException.java 3308 2006-06-15 18:54:14Z gbevin $
 *
 */
package tcl.lang;

import java.lang.reflect.*;

/**
 * This class handles Exception's caught while invoking the Reflection
 * API. It records the string form of the Exception into the result
 * of the interpreter and stores the actual Exception object in the
 * errorCode of the interpreter.
 */

public class ReflectException extends TclException {


/*
 *----------------------------------------------------------------------
 *
 * ReflectException --
 *
 *	Records the string form of the Exception into the result
 *	of the interpreter and stores the actual Exception object in the
 *	errorCode of the interpreter.
 *
 * Results:
 * 	None.
 *
 * Side effects:
 *	If interp is non-null, the interpreter result and errorCode
 *	are modified
 *
 *----------------------------------------------------------------------
 */

private static final long serialVersionUID = 4651756357452262833L;

public
ReflectException(
    Interp interp,		// Current interpreter. May be null.
				// If non-null, its result object and
				// errorCode variable will be changed.
    Throwable e)		// The exception to record in the interp.
{
    super(TCL.ERROR);
	initCause(e);

    if (interp != null) {
	Throwable t = e;
	if (t instanceof InvocationTargetException) {
	    // The original exception is wrapped in InvocationTargetException
	    // for us by the Java Reflection API. This fact doesn't provide
	    // any interesting information to script writers, so we'll
	    // unwrap it so that is more convenient for scripts to
	    // figure out the exception.

	    t = ((InvocationTargetException) t).getTargetException();
	}

	TclObject errCode = TclList.newInstance();
	errCode.preserve();

	try {
	    TclList.append(interp, errCode, TclString.newInstance("JAVA"));
	    TclList.append(interp, errCode,
                ReflectObject.newInstance(interp,Throwable.class,t)
            );
	} catch (TclException tclex) {
	    throw new TclRuntimeError("unexpected TclException: " + tclex);
	}

	// interp.setErrorCode() may fail silently if there is an bad
	// trace on the "errorCode" variable. If that happens, the
	// errCode list we created above may hang around
	// forever. Hence, we added the pair of preserve() + release()
	// calls to ensure that errCode will get cleaned up if
	// interp.setErrorCode() fails.


	interp.setErrorCode(errCode);
	errCode.release();

	interp.setResult(t.toString());
    }
}

} // end ReflectException

