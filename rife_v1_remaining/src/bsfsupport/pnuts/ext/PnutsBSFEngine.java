/*
 * @(#) PnutsBSFEngine.java	1.5 00/08/01
 *
 * Copyright (c) 1997-1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * See the file "license.txt" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package pnuts.ext;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFException;
import org.apache.bsf.util.BSFEngineImpl;
import pnuts.compiler.DynamicRuntime;
import pnuts.lang.Context;
import pnuts.lang.Package;
import pnuts.lang.Pnuts;
import pnuts.lang.PnutsException;
import pnuts.lang.PnutsFunction;

/**
 * This is the interface to Pnuts from Bean Scripting Framework.
 *
 * @see <a href="../../../doc/bsf.html">Pnuts User's Guide</a>
 * @see com.ibm.bsf.BSFEngine
 * @see	pnuts.lang.Package
 * @see	pnuts.lang.Context
 */
public class PnutsBSFEngine extends BSFEngineImpl {

    private Context context;
    private Package pkg;

    public PnutsBSFEngine(){
	pkg = new Package();
	context = new Context(pkg);
	context.setPnutsImpl(new CachedPnutsImpl());
    }

    /**
     * This is used by an application to evaluate an expression. The
     * expression may be string or some other type, depending on the
     * language. (For example, for BML it'll be an org.w3c.dom.Element
     * object.)
     *
     * @param source   (context info) the source of this expression
     *                 (e.g., filename)
     * @param lineNo   (context info) the line number in source for expr
     * @param columnNo (context info) the column number in source for expr
     * @param expr     the expression to evaluate
     *
     * @throws BSFException if anything goes wrong while eval'ing a
     *            BSFException is thrown. The reason indicates the problem.
     */
    public Object eval(String source, int lineNo, int columnNo, Object script)
	throws BSFException
    {
	try {
	    return Pnuts.eval((String)script, context);
	} catch (PnutsException e){
	    throw new BSFException(BSFException.REASON_EXECUTION_ERROR,
				   e.getMessage(),
				   e.getThrowable());
	}
    }

    /**
     * This is used by an application to call into the scripting engine
     * to make a function/method call. The "object" argument is the object
     * whose method is to be called, if that applies. For non-OO languages,
     * this is typically ignored and should be given as null. For pretend-OO
     * languages such as VB, this would be the (String) name of the object.
     * The arguments are given in the args array.
     *
     * @param object object on which to make the call
     * @param name   name of the method / procedure to call
     * @param args   the arguments to be given to the procedure
     *
     * @throws BSFException if anything goes wrong while eval'ing a
     *            BSFException is thrown. The reason indicates the problem.
     */
    public Object call(Object object, String method, Object[] args)
	throws BSFException
    {
       try {
	   if (object == null){
	       return PnutsFunction.call(method, args, context);
	   } else {
	       return DynamicRuntime.callMethod(context, object.getClass(), method, args, null, object);
	   }
       } catch (PnutsException e){
	    throw new BSFException(BSFException.REASON_EXECUTION_ERROR, e.getMessage(), e.getThrowable());
       }
    }

    /**
     * Declare a bean
     */
    public void declareBean (BSFDeclaredBean bean) throws BSFException {
	pkg.set(bean.name.intern(), bean.bean, context);
    }

    /**
     * Undeclare a previously declared bean.
     */
    public void undeclareBean (BSFDeclaredBean bean) throws BSFException {
	pkg.clear(bean.name.intern(), context);
    }
}
