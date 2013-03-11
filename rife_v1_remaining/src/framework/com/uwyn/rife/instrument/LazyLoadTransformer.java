package com.uwyn.rife.instrument;

import com.uwyn.rife.database.querymanagers.generic.instrument.LazyLoadAccessorsBytecodeTransformer;
import com.uwyn.rife.site.instrument.ConstrainedDetector;
import com.uwyn.rife.tools.ClassBytesLoader;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * This is a bytecode transformer that will modify classes so that they
 * receive the functionalities that are required to support lazy-loading
 * of relationships when the {@code GenericQueryManager} is being used.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3908 $
 * @since 1.6
 */
public class LazyLoadTransformer extends RifeTransformer
{
	protected byte[] transformRife(ClassLoader loader, String classNameInternal, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
	{
		String classname_dotted_interned = classNameInternal.replace('/', '.').intern();

		boolean is_constrained = false;

		try
		{
			is_constrained = new ConstrainedDetector(new ClassBytesLoader(loader)).isConstrained(classname_dotted_interned, classfileBuffer);

			if (is_constrained)
			{
				return LazyLoadAccessorsBytecodeTransformer.addLazyLoadToBytes(classfileBuffer);
			}
		}
		catch (Throwable e)
		{
			is_constrained = false;
		}

		return classfileBuffer;
	}
}
