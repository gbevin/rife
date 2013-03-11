/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Annotations2ElementInfo.java 3961 2008-07-11 11:35:59Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.annotations.*;
import com.uwyn.rife.engine.exceptions.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.annotations.Submission;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.ClassUtils;
import com.uwyn.rife.tools.JavaSpecificationUtils;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

class Annotations2ElementInfo implements ElementInfoProcessor
{
	public void processElementInfo(ElementInfoBuilder builder, String declarationName, ResourceFinder resourceFinder)
	throws EngineException
	{
		if (!JavaSpecificationUtils.isAtLeastJdk15())
		{
			throw new Jdk15RequiredForAnnotationsException(builder.getSiteBuilder().getDeclarationName(), declarationName);
		}
		try
		{
			Class klass = Class.forName("com.uwyn.rife.engine.Annotations2ElementInfoProcessor");
			ElementInfoProcessor processor = (ElementInfoProcessor)klass.newInstance();
			processor.processElementInfo(builder, declarationName, resourceFinder);
		}
		catch (Exception e)
		{
			throw new EngineException(e);
		}
	}
}

class Annotations2ElementInfoProcessor implements ElementInfoProcessor
{
	private URL getSourceResource(String sourceLocation, boolean getElement)
	{
		URL	source_resource = getClass().getClassLoader().getResource(sourceLocation);
		if (null == source_resource &&
			getElement)
		{
			source_resource = getClass().getClassLoader().getResource(EngineClassLoader.DEFAULT_IMPLEMENTATIONS_PATH+sourceLocation);
		}
		return source_resource;
	}

	public void processElementInfo(ElementInfoBuilder builder, String declarationName, ResourceFinder resourceFinder)
	throws EngineException
	{
		resourceFinder = ResourceFinderClasspath.getInstance();
		try
		{
			Class element_class = ElementFactory.INSTANCE.getJavaClass(declarationName, declarationName);
			if (RifeConfig.Engine.getSiteAutoReload())
			{
				URL source_resource = getSourceResource(EngineClassLoader.constructSourcePath(declarationName), true);
				if (source_resource != null)
				{
					builder.addResourceModificationTime(new UrlResource(source_resource, declarationName), EngineClassLoader.getSourceModificationTime(source_resource));
				}
			}
			
			Stack<Class> parent_stack = new Stack<Class>();
			while (ElementAware.class.isAssignableFrom(element_class) &&
				   !(Element.class == element_class))
			{
				parent_stack.push(element_class);
				element_class = element_class.getSuperclass();
			}
			
			element_class = parent_stack.pop();
			while (element_class != null)
			{
				SubmissionBuilder submissionbuilder = null;
				if (element_class.isAnnotationPresent(Elem.class))
				{
					// handle class annotations
					Elem elem = (Elem)element_class.getAnnotation(Elem.class);
					
					// only set the ID and the URL for the top level element class
					if (0 == parent_stack.size())
					{
						if (!builder.getElementDeclaration().hasDeclaredId())
						{
							if (elem.id().equals(""))
							{
								builder.setId(ClassUtils.simpleClassName(element_class));
							}
							else
							{
								builder.setId(elem.id());
							}
						}
						
						if (!builder.getElementDeclaration().hasDeclaredUrl())
						{
							if (elem.url().equals(Elem.DEFAULT_URL))
							{
								builder.setUrl(ClassUtils.shortenClassName(element_class));
							}
							else if (elem.url().equals(""))
							{
								// set no URL
							}
							else
							{
								builder.setUrl(elem.url());
							}
						}
					}
					
					String inherits = getElementId(builder, elem.inheritsClass(), elem.inheritsClassIdPrefix(), elem.inheritsId());
					String pre = getElementId(builder, elem.preClass(), elem.preClassIdPrefix(), elem.preId());
					if (!inherits.equals(""))	builder.setInherits(inherits);
					if (!pre.equals(""))		builder.setInherits(pre);
					
					if (!elem.contentType().equals(Elem.DEFAULT_CONTENT_TYPE))	builder.setContentType(elem.contentType());
					
					for (Input input : elem.inputs())			builder.addInput(input.name(), input.defaultValues());
					for (InBean bean : elem.inbeans())			builder.addInBean(bean.beanclass(),
																		   (0 == bean.prefix().length() ? null : bean.prefix()),
																		   (0 == bean.name().length() ? null : bean.name()),
																		   (0 == bean.group().length() ? null : bean.group()));
					for (InCookie cookie : elem.incookies())	builder.addIncookie(cookie.name(), cookie.defaultValue());
					
					for (Output output : elem.outputs())		builder.addOutput(output.name(), output.defaultValues());
					for (OutBean bean : elem.outbeans())		builder.addOutBean(bean.beanclass(),
																			 (0 == bean.prefix().length() ? null : bean.prefix()),
																			 (0 == bean.name().length() ? null : bean.name()),
																			 (0 == bean.group().length() ? null : bean.group()));
					for (OutCookie cookie : elem.outcookies())	builder.addOutcookie(cookie.name(), cookie.defaultValue());
					
					for (Submission submission : elem.submissions())
					{
						if (submissionbuilder != null) submissionbuilder.leaveSubmission();
						submissionbuilder = builder.enterSubmission(submission.name());
						submissionbuilder.cancelContinuations(Submission.Continuations.CANCEL.equals(submission.continuations()));
						submissionbuilder.setScope(Scope.getScope(submission.scope().toString()));
						
						for (Param param : submission.params())				submissionbuilder.addParameter(param.name(), param.defaultValues());
						for (ParamRegexp param : submission.paramRegexps())	submissionbuilder.addParameterRegexp(param.value());
						for (SubmissionBean bean : submission.beans())		submissionbuilder.addBean(bean.beanclass(),
																								  (0 == bean.prefix().length() ? null : bean.prefix()),
																								  (0 == bean.name().length() ? null : bean.name()),
																								  (0 == bean.group().length() ? null : bean.group()));
						for (File file : submission.files())				submissionbuilder.addFile(file.name());
						for (FileRegexp file : submission.fileRegexps())	submissionbuilder.addFileRegexp(file.value());
						
					}
					
					for (Exit exit : elem.exits())	builder.addExit(exit.name());
					for (ChildTrigger childtrigger : elem.childTriggers())	builder.addChildTrigger(childtrigger.name());
					
					Pathinfo pathinfo = elem.pathinfo();
					if (null == builder.getPathInfoMode()) {
						builder.setPathInfoMode(PathInfoMode.getMode(pathinfo.policy().toString()));
					}
					for (Mapping mapping : pathinfo.mappings())	builder.addPathInfoMapping(mapping.value());
					
					for (Autolink autolink : elem.autolinks())
					{
						String dest_id = getElementId(builder, autolink.destClass(), autolink.destClassIdPrefix(), autolink.destId());
						String src_exit = autolink.srcExit();
						if (0 == src_exit.length())
						{
							src_exit = dest_id;
							if (autolink.destClassIdPrefix() != null &&
								autolink.destClassIdPrefix().length() > 0)
							{
								src_exit = src_exit.substring(autolink.destClassIdPrefix().length() + 1);
							}
						}
						builder.addAutoLink(src_exit, dest_id, autolink.inheritance().equals(Flowlink.Inheritance.CANCEL), autolink.embedding().equals(Flowlink.Embedding.CANCEL), autolink.redirect(), autolink.continuations().equals(Flowlink.Continuations.CANCEL));
					}
					
					for (Flowlink flowlink : elem.flowlinks())
					{
						String dest_id = getElementId(builder, flowlink.destClass(), flowlink.destClassIdPrefix(), flowlink.destId());
						FlowLinkBuilder flowlinkbuilder = builder.enterFlowLink(flowlink.srcExit())
							.destId(dest_id)
							.snapback(flowlink.snapback())
							.cancelInheritance(flowlink.inheritance().equals(Flowlink.Inheritance.CANCEL))
							.cancelEmbedding(flowlink.embedding().equals(Flowlink.Embedding.CANCEL))
							.redirect(flowlink.redirect());
						for (Datalink datalink : flowlink.datalinks())
						{
							flowlinkbuilder.addDataLink(datalink.srcOutput(), datalink.srcOutbean(), datalink.snapback(), datalink.destInput(), datalink.destInbean());
						}
						flowlinkbuilder.leaveFlowLink();
					}
					
					for (Datalink datalink : elem.datalinks())
					{
						String dest_id = getElementId(builder, datalink.destClass(), datalink.destClassIdPrefix(), datalink.destId());
						builder.addDataLink(datalink.srcOutput(), datalink.srcOutbean(), dest_id, datalink.snapback(), datalink.destInput(), datalink.destInbean());
					}
				}
				
				// process all bean property accessors, validate the annotations used on them,
				// and keep them in a list of methods that need to be processed
				Map<Method, String> methods_to_process = new HashMap<Method, String>();
				// obtain the BeanInfo class
				BeanInfo bean_info = BeanUtils.getBeanInfo(element_class);
				
				// process the properties of the bean
				PropertyDescriptor[]	bean_properties = bean_info.getPropertyDescriptors();
				if (bean_properties.length > 0)
				{
					// iterate over the properties of the bean
					for (PropertyDescriptor descriptor : bean_properties)
					{
						{
							Method write_method = descriptor.getWriteMethod();
							if (write_method != null)
							{
								// ensure that read annotations aren't used with write methods
								if (write_method.isAnnotationPresent(OutBeanProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), OutBeanProperty.class, "on setters ("+write_method.getName()+")", null);
								}
								if (write_method.isAnnotationPresent(OutCookieProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), OutCookieProperty.class, "on setters ("+write_method.getName()+")", null);
								}
								if (write_method.isAnnotationPresent(OutputProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), OutputProperty.class, "on setters ("+write_method.getName()+")", null);
								}

								// ensure that the property name is correct
								if (write_method.isAnnotationPresent(InBeanProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, write_method, InBeanProperty.class, write_method.getAnnotation(InBeanProperty.class).name());
								}
								if (write_method.isAnnotationPresent(InCookieProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, write_method, InCookieProperty.class, write_method.getAnnotation(InCookieProperty.class).name());
								}
								if (write_method.isAnnotationPresent(InputProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, write_method, InputProperty.class, write_method.getAnnotation(InputProperty.class).name());
								}
								if (write_method.isAnnotationPresent(ParamProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, write_method, ParamProperty.class, write_method.getAnnotation(ParamProperty.class).name());
								}
								if (write_method.isAnnotationPresent(SubmissionBeanProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, write_method, SubmissionBeanProperty.class, write_method.getAnnotation(SubmissionBeanProperty.class).name());
								}
								if (write_method.isAnnotationPresent(FileProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, write_method, FileProperty.class, write_method.getAnnotation(FileProperty.class).name());
								}

								// use the annotations
								if (write_method.isAnnotationPresent(InBeanProperty.class) ||
									write_method.isAnnotationPresent(InCookieProperty.class) ||
									write_method.isAnnotationPresent(InputProperty.class) ||
									write_method.isAnnotationPresent(ParamProperty.class) ||
									write_method.isAnnotationPresent(SubmissionBeanProperty.class) ||
									write_method.isAnnotationPresent(FileProperty.class))
								{
									methods_to_process.put(write_method, descriptor.getName());
								}
							}
						}

						{
							Method read_method = descriptor.getReadMethod();
							if (read_method != null)
							{
								// ensure that the write annotations aren't used with read methods
								if (read_method.isAnnotationPresent(InBeanProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), InBeanProperty.class, "on getters ("+read_method.getName()+")", null);
								}
								if (read_method.isAnnotationPresent(InCookieProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), InCookieProperty.class, "on getters ("+read_method.getName()+")", null);
								}
								if (read_method.isAnnotationPresent(InputProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), InputProperty.class, "on getters ("+read_method.getName()+")", null);
								}
								if (read_method.isAnnotationPresent(ParamProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), ParamProperty.class, "on getters ("+read_method.getName()+")", null);
								}
								if (read_method.isAnnotationPresent(SubmissionBeanProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), SubmissionBeanProperty.class, "on getters ("+read_method.getName()+")", null);
								}
								if (read_method.isAnnotationPresent(FileProperty.class))
								{
									throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), FileProperty.class, "on getters ("+read_method.getName()+")", null);
								}

								// ensure that the property name is correct
								if (read_method.isAnnotationPresent(OutBeanProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, read_method, OutBeanProperty.class, read_method.getAnnotation(OutBeanProperty.class).name());
								}
								if (read_method.isAnnotationPresent(OutBeanProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, read_method, OutBeanProperty.class, read_method.getAnnotation(OutBeanProperty.class).name());
								}
								if (read_method.isAnnotationPresent(OutputProperty.class))
								{
									ensureCorrespondingPropertyName(declarationName, builder, descriptor, read_method, OutputProperty.class, read_method.getAnnotation(OutputProperty.class).name());
								}

								// use the annotations
								if (read_method.isAnnotationPresent(OutBeanProperty.class) ||
									read_method.isAnnotationPresent(OutCookieProperty.class) ||
									read_method.isAnnotationPresent(OutputProperty.class))
								{
									methods_to_process.put(read_method, descriptor.getName());
								}
							}
						}
					}
				}
				
				// handle field annotation types
				try
				{
					for (Field field : element_class.getDeclaredFields())
					{
						if (field.isAnnotationPresent(ExitField.class))
						{
							requireFinalString(ExitField.class, field, builder, declarationName);
							builder.addExit((String)field.get(null));
						}
						
						if (field.isAnnotationPresent(FlowlinkExitField.class))
						{
							requireFinalString(FlowlinkExitField.class, field, builder, declarationName);
							FlowlinkExitField flowlink = field.getAnnotation(FlowlinkExitField.class);
							String dest_id = getElementId(builder, flowlink.destClass(), flowlink.destClassIdPrefix(), flowlink.destId());
							FlowLinkBuilder flowlinkbuilder = builder.enterFlowLink((String)field.get(null))
								.destId(dest_id)
								.snapback(flowlink.snapback())
								.cancelInheritance(flowlink.inheritance().equals(Flowlink.Inheritance.CANCEL))
								.cancelEmbedding(flowlink.embedding().equals(Flowlink.Embedding.CANCEL))
								.redirect(flowlink.redirect());
							for (Datalink datalink : flowlink.datalinks())
							{
								flowlinkbuilder.addDataLink(datalink.srcOutput(), datalink.srcOutbean(), datalink.snapback(), datalink.destInput(), datalink.destInbean());
							}
							flowlinkbuilder.leaveFlowLink();
						}

						if (field.isAnnotationPresent(AutolinkExitField.class))
						{
							requireFinalString(AutolinkExitField.class, field, builder, declarationName);
							AutolinkExitField autolink = field.getAnnotation(AutolinkExitField.class);
							String dest_id = getElementId(builder, autolink.destClass(), autolink.destClassIdPrefix(), autolink.destId());
							builder.addAutoLink((String)field.get(null), dest_id,
												autolink.inheritance().equals(Autolink.Inheritance.CANCEL),
												autolink.embedding().equals(Autolink.Embedding.CANCEL),
												autolink.redirect(),
												autolink.continuations().equals(Autolink.Continuations.CANCEL));
						}
					}
				}
				catch (IllegalAccessException e)
				{
					throw new ElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), "Unexpected error while introspecting the class for field annotation types.", e);
				}
				
				// Create a set that is sorted according to the method priorities
				SortedSet<PrioritizedMethod> method_set = new TreeSet<PrioritizedMethod>();
				for (Method method : element_class.getDeclaredMethods())
				{
					int[] priority = null;
					if (method.isAnnotationPresent(Priority.class))
					{
						priority = method.getAnnotation(Priority.class).value();
					}
					method_set.add(new PrioritizedMethod(method, priority));
				}
				
				// Process the methods according to their priorities
				for (PrioritizedMethod prioritized_method : method_set)
				{
					Method method = prioritized_method.getMethod();
					
					// process all the setters and getters that have been detected to have RIFE annotations
					if (methods_to_process.containsKey(method))
					{
						// handle the InBeanProperty annotation
						if (method.isAnnotationPresent(InBeanProperty.class))
						{
							InBeanProperty bean = method.getAnnotation(InBeanProperty.class);
							builder.addInBean(method.getParameterTypes()[0],
											  (0 == bean.prefix().length() ? null : bean.prefix()),
											  methods_to_process.get(method),
											  (0 == bean.group().length() ? null : bean.group()));
						}
						
						// handle the InCookieProperty annotation
						if (method.isAnnotationPresent(InCookieProperty.class))
						{
							InCookieProperty cookie = method.getAnnotation(InCookieProperty.class);
							builder.addIncookie(methods_to_process.get(method), cookie.defaultValue());
						}
						
						// handle the InputProperty annotation
						if (method.isAnnotationPresent(InputProperty.class))
						{
							InputProperty input = method.getAnnotation(InputProperty.class);
							builder.addInput(methods_to_process.get(method), input.defaultValues());
						}
						
						// handle the ParamProperty annotation
						if (method.isAnnotationPresent(ParamProperty.class))
						{
							if (null == submissionbuilder)
							{
								throw new SubmissionElementAnnotationNeededException(declarationName, builder.getSiteBuilder().getDeclarationName(), ParamProperty.class, null);
							}
							ParamProperty param = method.getAnnotation(ParamProperty.class);
							submissionbuilder.addParameter(methods_to_process.get(method), param.defaultValues());
						}
						
						// handle the SubmissionBeanProperty annotation
						if (method.isAnnotationPresent(SubmissionBeanProperty.class))
						{
							if (null == submissionbuilder)
							{
								throw new SubmissionElementAnnotationNeededException(declarationName, builder.getSiteBuilder().getDeclarationName(), SubmissionBeanProperty.class, null);
							}
							SubmissionBeanProperty bean = method.getAnnotation(SubmissionBeanProperty.class);
							submissionbuilder.addBean(method.getParameterTypes()[0],
													  (0 == bean.prefix().length() ? null : bean.prefix()),
													  methods_to_process.get(method),
													  (0 == bean.group().length() ? null : bean.group()));
						}
						
						// handle the FileProperty annotation
						if (method.isAnnotationPresent(FileProperty.class))
						{
							if (!UploadedFile.class.isAssignableFrom(method.getParameterTypes()[0]))
							{
								throw new InvalidFilePropertyElementAnnotationException(declarationName, builder.getSiteBuilder().getDeclarationName(), method.getName(), null);
							}
							if (null == submissionbuilder)
							{
								throw new SubmissionElementAnnotationNeededException(declarationName, builder.getSiteBuilder().getDeclarationName(), FileProperty.class, null);
							}
							submissionbuilder.addFile(methods_to_process.get(method));
						}
						
						// handle the OutBeanProperty annotation
						if (method.isAnnotationPresent(OutBeanProperty.class))
						{
							OutBeanProperty bean = method.getAnnotation(OutBeanProperty.class);
							builder.addOutBean(method.getReturnType(),
											   (0 == bean.prefix().length() ? null : bean.prefix()),
											   methods_to_process.get(method),
											   (0 == bean.group().length() ? null : bean.group()));
						}
						
						// handle the OutCookieProperty annotation
						if (method.isAnnotationPresent(OutCookieProperty.class))
						{
							OutCookieProperty cookie = method.getAnnotation(OutCookieProperty.class);
							builder.addOutcookie(methods_to_process.get(method), cookie.defaultValue());
						}
						
						// handle the OutputProperty annotation
						if (method.isAnnotationPresent(OutputProperty.class))
						{
							OutputProperty output = method.getAnnotation(OutputProperty.class);
							builder.addOutput(methods_to_process.get(method), output.defaultValues());
						}
					}
					// ensure that property annotations are only used on setters and getters
					else if (method.isAnnotationPresent(InBeanProperty.class) ||
							 method.isAnnotationPresent(InCookieProperty.class) ||
							 method.isAnnotationPresent(InputProperty.class) ||
							 method.isAnnotationPresent(ParamProperty.class) ||
							 method.isAnnotationPresent(SubmissionBeanProperty.class) ||
							 method.isAnnotationPresent(FileProperty.class) ||
							 method.isAnnotationPresent(OutBeanProperty.class) ||
							 method.isAnnotationPresent(OutCookieProperty.class) ||
							 method.isAnnotationPresent(OutputProperty.class))
					{
						throw new InvalidUseOfElementPropertyAnnotationException(declarationName, builder.getSiteBuilder().getDeclarationName(), method.getName(), null);
					}
					// handle the SubmissionHandler annotation
					else if (method.isAnnotationPresent(SubmissionHandler.class))
					{
						// ensure that the handler method conforms to the convention
						if (!method.getName().startsWith("do") ||
							method.getName().length() == 2 ||
							method.getReturnType() != void.class ||
							method.getParameterTypes().length > 0)
						{
							throw new InvalidUseOfElementSubmissionHandlerAnnotationException(declarationName, builder.getSiteBuilder().getDeclarationName(), method.getName(), null);
						}
						// add the suitable submission
						else
						{
							if (submissionbuilder != null) submissionbuilder.leaveSubmission();
							submissionbuilder = builder.enterSubmission(StringUtils.uncapitalize(method.getName().substring(2)));
							
							SubmissionHandler submission = method.getAnnotation(SubmissionHandler.class);
							submissionbuilder.cancelContinuations(SubmissionHandler.Continuations.CANCEL == submission.continuations());
							submissionbuilder.setScope(Scope.getScope(submission.scope().toString()));
							
							for (Param param : submission.params())				submissionbuilder.addParameter(param.name(), param.defaultValues());
							for (ParamRegexp param : submission.paramRegexps())	submissionbuilder.addParameterRegexp(param.value());
							for (SubmissionBean bean : submission.beans())		submissionbuilder.addBean(bean.beanclass(),
																									  (0 == bean.prefix().length() ? null : bean.prefix()),
																									  (0 == bean.name().length() ? null : bean.name()),
																									  (0 == bean.group().length() ? null : bean.group()));
							for (File file : submission.files())				submissionbuilder.addFile(file.name());
							for (FileRegexp file : submission.fileRegexps())	submissionbuilder.addFileRegexp(file.value());
						}
					}
				}
				if (submissionbuilder != null) submissionbuilder.leaveSubmission();

				if (0 == parent_stack.size())
				{
					element_class = null;
					break;
				}
				
				element_class = parent_stack.pop();
			}
		}
		catch (BeanUtilsException e)
		{
			throw new ElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), "Unexpected error while introspecting the class.", e);
		}
 	}

	private void ensureCorrespondingPropertyName(String declarationName, ElementInfoBuilder builder, PropertyDescriptor descriptor, Method write_method, Class property_annotation_class, String expected_property_name)
	{
		if (property_annotation_class != null &&
			expected_property_name != null &&
			expected_property_name.length() > 0 &&
			!descriptor.getName().equals(expected_property_name))
		{
			throw new PropertyNameMismatchErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), write_method, property_annotation_class, expected_property_name, descriptor.getName());
		}
	}

	private void requireFinalString(Class annotationType, Field field, ElementInfoBuilder builder, String declarationName) throws UnsupportedElementAnnotationErrorException
	{
		if (field.getType() != String.class)
		{
			throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), annotationType, "on non-String field ("+field.getName()+")", null);
		}
		if (!Modifier.isFinal(field.getModifiers()))
		{
			throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), annotationType, "on non-final field ("+field.getName()+")", null);
		}
		if (!Modifier.isStatic(field.getModifiers()))
		{
			throw new UnsupportedElementAnnotationErrorException(declarationName, builder.getSiteBuilder().getDeclarationName(), annotationType, "on non-static field ("+field.getName()+")", null);
		}
	}
	
	private String getElementId(ElementInfoBuilder builder, Class elementClass, String elementClassIdPrefix, String elementId)
	{
		if (elementClass != void.class)
		{
			if (!elementClass.isAnnotationPresent(Elem.class))
			{
				throw new ElementAnnotationMissingException(elementClass.getName(), builder.getSiteBuilder().getDeclarationName(), Elem.class, null);
			}

			Elem destclass_elem = (Elem)elementClass.getAnnotation(Elem.class);
			if (destclass_elem.id().equals(""))
			{
				elementId = ClassUtils.simpleClassName(elementClass);
			}
			else
			{
				elementId = destclass_elem.id();
			}
			
			if (elementClassIdPrefix != null &&
				elementClassIdPrefix.length() > 0)
			{
				elementId = elementClassIdPrefix+"."+elementId;
			}
		}

		return elementId;
	}
}
