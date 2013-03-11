/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataMerged.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

/**
 * This interface is merely a marker interface to indicate to RIFE that it
 * should consider this class as a <code>MetaData</code> class whose
 * interfaces will be automatically merged into the sibling class that it's
 * augmenting.
 * <p>So, consider a class <code>Foo</code> and another class
 * <code>FooMetaData</code>. When <code>FooMetaData</code> implements
 * <code>MetaDataMerged</code>, RIFE will adapt <code>Foo</code> and make it
 * implement all the interfaces that <code>FooMetaData</code> implements.
 * Also, when the default constructor of <code>Foo</code> is called, an
 * instance of <code>FooMetaData</code> will be created and stored in a new
 * hidden member variable. The added method implementations simple delegate to
 * the instance of <code>FooMetaData</code>.
 * <p>Optionally, <code>FooMetaData</code> can also implement
 * <code>MetaDataBeanAware</code>, in which case the instance of
 * <code>FooMetaData</code> will receive the instance of Foo that it belongs
 * to right after it has been instantiated.
 * <p>Note that the relationship between <code>Foo</code> and
 * <code>FooMetaData</code> is purely name based (the <code>MetaData</code>
 * suffix). RIFE will look up the meta data class through the classpath, which
 * means that it's possible to augment any class, anywhere, in any package,
 * even without having the source code.
 * 
 * @see MetaDataBeanAware
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.4
 */
public interface MetaDataMerged
{
}
