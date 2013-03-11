/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationsAgent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import com.uwyn.rife.instrument.FinalTransformer;
import com.uwyn.rife.instrument.InitialTransformer;
import java.lang.instrument.Instrumentation;

/**
 * Provides a continuations instrumentation agent that will modify
 * the bytecode of the classes that are loaded. It enhances the classes with
 * continuations capabilities that are otherwise provided by a class-loader.
 * <p>To activate the agent you need to execute the Java application with the
 * proper argument, for example:
 * <pre>java -javaagent:/path/to/rife-continuations-agent-1.6-jdk15.jar=com.your.ContinuationConfigInstrumentClass com.your.mainClass</pre>
 * <p>When the agent is active the {@link com.uwyn.rife.continuations.basic.BasicContinuableClassLoader} will
 * automatically be disabled to ensure that they are not conflicting with each
 * other. The agent is packaged in its own jar file which should correspond
 * to the RIFE/Continuations version that you are using in your application.
 * <p>It is possible to debug the bytecode instrumentation by using the
 * functionatilies provided by the {@link InitialTransformer} and
 * {@link FinalTransformer} transformers that are included in this agent.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ContinuationsAgent
{
	public final static String AGENT_ACTIVE_PROPERTY = "rife.agent.active";
	
	public static void premain(String agentArguments, Instrumentation instrumentation)
	{
		if (null == agentArguments) throw new IllegalArgumentException("expecting the fully qualified class name of a ContinuationConfigInstrument class");
		ContinuationConfigInstrument config = null;
		try
		{
			Class config_class = Class.forName(agentArguments);
			config = (ContinuationConfigInstrument)config_class.newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unexpected error while creating an instance of the instrumentation configuration with class name '"+agentArguments+"'", e);
		}
		
		System.getProperties().setProperty(AGENT_ACTIVE_PROPERTY, Boolean.TRUE.toString());
		
		instrumentation.addTransformer(new InitialTransformer());
		instrumentation.addTransformer(new ContinuationsTransformer(config));
		instrumentation.addTransformer(new FinalTransformer());
	}
}
