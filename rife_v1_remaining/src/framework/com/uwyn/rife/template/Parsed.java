/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Parsed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import java.net.URL;
import java.util.*;

import com.uwyn.rife.asm.ClassWriter;
import com.uwyn.rife.asm.Label;
import com.uwyn.rife.asm.MethodVisitor;
import com.uwyn.rife.asm.Opcodes;
import com.uwyn.rife.template.exceptions.TemplateException;

final class Parsed implements Opcodes
{
	private Parser						mParser = null;
	private String						mTemplateName = null;
	private String						mPackage = null;
	private String						mClassName = null;
	private URL							mResource = null;
	private long						mModificationTime = -1;

	private Map<String, ParsedBlockData>	mBlocks = new LinkedHashMap<String, ParsedBlockData>();
	private Set<String>						mValueIds = new LinkedHashSet<String>();
	private Map<String, String>				mDefaultValues = new HashMap<String, String>();
	private List<String>					mBlockvalues = new ArrayList<String>();
	private Map<URL, Long>					mDependencies = new HashMap<URL, Long>();
	private String							mModificationState = null;
	private FilteredTagsMap					mFilteredValuesMap = null;
	private FilteredTagsMap					mFilteredBlocksMap = null;

	Parsed(Parser parser)
	{
		assert parser != null;

		mParser = parser;
	}
	
	private Map<Integer, ArrayList<String>> getHashcodeKeysMapping(Collection<String> stringCollection)
	{
		// create a mapping of all string hashcodes to their possible real values
		// hashcodes will be used in a switch for quick lookup of blocks
		Map<Integer, ArrayList<String>>	hashcode_keys_mapping = new HashMap<Integer, ArrayList<String>>();
		int										hashcode;
		ArrayList<String>						keys = null;
		for (String key : stringCollection)
		{
			hashcode = key.hashCode();
			keys = hashcode_keys_mapping.get(hashcode);
			if (null == keys)
			{
				keys = new ArrayList<String>();
				hashcode_keys_mapping.put(hashcode, keys);
			}
			keys.add(key);
		}
		
		return hashcode_keys_mapping;
	}
	
	// store an integer on the stack
	private void addIntegerConst(MethodVisitor method, int value)
	{
		switch (value)
		{
			case -1:
				method.visitInsn(ICONST_M1);
				break;
			case 0:
				method.visitInsn(ICONST_0);
				break;
			case 1:
				method.visitInsn(ICONST_1);
				break;
			case 2:
				method.visitInsn(ICONST_2);
				break;
			case 3:
				method.visitInsn(ICONST_3);
				break;
			case 4:
				method.visitInsn(ICONST_4);
				break;
			case 5:
				method.visitInsn(ICONST_5);
				break;
			default:
				method.visitLdcInsn(value);
				break;
		}
	}
	
	byte[] getByteCode()
	{
		ClassWriter	class_writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MethodVisitor method = null;
		
		String full_classname = (getPackage()+"."+getClassName()).replace('.', '/');

		// define the template class
class_writer.visit(V1_4, ACC_PUBLIC|ACC_SYNCHRONIZED, full_classname, null, "com/uwyn/rife/template/AbstractTemplate", null);
		
		// generate the template constructor
method = class_writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
method.visitVarInsn            (ALOAD, 0);
method.visitMethodInsn         (INVOKESPECIAL, "com/uwyn/rife/template/AbstractTemplate", "<init>", "()V");
method.visitInsn               (RETURN);
method.visitMaxs               (0, 0);

        // define the method that will return the template name
method = class_writer.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
method.visitLdcInsn            (mClassName);
method.visitInsn               (ARETURN);
method.visitMaxs               (0, 0);
		
		// define the method that will return the full template name
method = class_writer.visitMethod(ACC_PUBLIC, "getFullName", "()Ljava/lang/String;", null, null);
method.visitLdcInsn            (mTemplateName);
method.visitInsn               (ARETURN);
method.visitMaxs               (0, 0);
		
		// define the methods that will return the modification time
method = class_writer.visitMethod(ACC_STATIC, "getModificationTimeReal", "()J", null, null);
method.visitLdcInsn            (getModificationTime());
method.visitInsn               (LRETURN);
method.visitMaxs               (0, 0);

method = class_writer.visitMethod(ACC_PUBLIC, "getModificationTime", "()J", null, null);
method.visitMethodInsn         (INVOKESTATIC, full_classname, "getModificationTimeReal", "()J");
method.visitInsn               (LRETURN);
method.visitMaxs               (0, 0);
		
		// define the method that will return the modification state
method = class_writer.visitMethod(ACC_STATIC, "getModificationState", "()Ljava/lang/String;", null, null);
		if (null == mModificationState)
		{
method.visitInsn               (ACONST_NULL);
		}
		else
		{
method.visitLdcInsn            (mModificationState);
		}
method.visitInsn               (ARETURN);
method.visitMaxs               (0, 0);

		// prepare the blocks for lookup switches
		ParsedBlockData		block_data = null;
		ArrayList<String>	keys = null;

		Map<Integer, ArrayList<String>>	hashcode_keys_mapping = null;
		Map<String, ParsedBlockPart>	blockparts_order = null;
		int[]							hashcodes = null;
		
		hashcode_keys_mapping = getHashcodeKeysMapping(mBlocks.keySet());
		blockparts_order = new LinkedHashMap<String, ParsedBlockPart>();
		{
			Set<Integer> hashcodes_set = hashcode_keys_mapping.keySet();
			hashcodes = new int[hashcodes_set.size()];
			int hashcode_index = 0;
			for (Integer i : hashcodes_set) {
				hashcodes[hashcode_index++] = i;
			}
		}
		Arrays.sort(hashcodes);
		keys = null;

		// generate the method that will append the block parts according to the current set of values
		// for external usage
		{
method = class_writer.visitMethod(ACC_PROTECTED, "appendBlockExternalForm", "(Ljava/lang/String;Lcom/uwyn/rife/template/ExternalValue;)Z", null, null);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I");
			Label external_default = new Label();
			Label external_found = new Label();
			Label[] external_labels = new Label[hashcodes.length];
			for (int i = 0; i < external_labels.length; i++)
			{
				external_labels[i] = new Label();
			}
			
method.visitLookupSwitchInsn   (external_default, hashcodes, external_labels);
			String	blockdata_static_prefix = "sBlockPart";
			long	blockdata_static_counter = 0L;
			String	static_identifier = null;
			for (int i = 0; i < hashcodes.length; i++)
			{
method.visitLabel              (external_labels[i]);
	
				keys = hashcode_keys_mapping.get(hashcodes[i]);
				if (1 == keys.size())
				{
					block_data = mBlocks.get(keys.get(0));
		
					Iterator<ParsedBlockPart>	block_data_it = block_data.iterator();
					ParsedBlockPart				block_part = null;
					while (block_data_it.hasNext())
					{
						block_part = block_data_it.next();
						
						static_identifier = blockdata_static_prefix+(blockdata_static_counter++);
						
						blockparts_order.put(static_identifier, block_part);
block_part.visitByteCodeExternalForm(method, full_classname, static_identifier);
					}
				}
				else
				{
					for (String key : keys)
					{
						Label after_key_label = new Label();
method.visitVarInsn            (ALOAD, 1);
method.visitLdcInsn            (key);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
method.visitJumpInsn           (IFEQ, after_key_label);
					
						block_data = mBlocks.get(key);

						for (ParsedBlockPart block_part : block_data)
						{
							static_identifier = blockdata_static_prefix+(blockdata_static_counter++);
							
							blockparts_order.put(static_identifier, block_part);
block_part.visitByteCodeExternalForm(method, full_classname, static_identifier);
						}
method.visitJumpInsn           (GOTO, external_found);
method.visitLabel              (after_key_label);
					}
method.visitInsn               (ICONST_0);
method.visitInsn               (IRETURN);
				}
method.visitJumpInsn           (GOTO, external_found);
			}
method.visitLabel              (external_default);
method.visitInsn               (ICONST_0);
method.visitInsn               (IRETURN);
method.visitLabel              (external_found);
method.visitInsn               (ICONST_1);
method.visitInsn               (IRETURN);
method.visitMaxs               (0, 0);
		}
		
		// generate the method that will append the block parts according to the current set of values
		// for internal usage
		{
method = class_writer.visitMethod(ACC_PROTECTED, "appendBlockInternalForm", "(Ljava/lang/String;Lcom/uwyn/rife/template/InternalValue;)Z", null, null);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I");
			Label internal_default = new Label();
			Label internal_found = new Label();
			Label[] internal_labels = new Label[hashcodes.length];
			for (int i = 0; i < internal_labels.length; i++)
			{
				internal_labels[i] = new Label();
			}
			
method.visitLookupSwitchInsn   (internal_default, hashcodes, internal_labels);
			String	static_identifier = null;
			Iterator<String> static_identifiers_it = blockparts_order.keySet().iterator();
			for (int i = 0; i < hashcodes.length; i++)
			{
method.visitLabel              (internal_labels[i]);
	
				int	text_count = 0;
				int	value_count = 0;
				
				keys = hashcode_keys_mapping.get(hashcodes[i]);
				if (1 == keys.size())
				{
					block_data = mBlocks.get(keys.get(0));
					
					Iterator<ParsedBlockPart>	block_data_it = null;
					ParsedBlockPart				block_part = null;
		
					block_data_it = block_data.iterator();
					while (block_data_it.hasNext())
					{
						block_part = block_data_it.next();
						
						if (ParsedBlockPart.TEXT == block_part.getType())
						{
							text_count++;
						}
						else if (ParsedBlockPart.VALUE == block_part.getType())
						{
							value_count++;
						}
					}
					
					if (text_count+value_count > 0)
					{
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 2);
addIntegerConst                (method, text_count+value_count);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "increasePartsCapacityInternal", "(Lcom/uwyn/rife/template/InternalValue;I)V");
					}

					if (value_count > 0)
					{
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 2);
addIntegerConst                (method, value_count);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "increaseValuesCapacityInternal", "(Lcom/uwyn/rife/template/InternalValue;I)V");
					}
					
					block_data_it = block_data.iterator();
					while (block_data_it.hasNext())
					{
						block_part = block_data_it.next();

						static_identifier = static_identifiers_it.next();
						
						block_part.visitByteCodeInternalForm(method, full_classname, static_identifier);
					}
method.visitJumpInsn           (GOTO, internal_found);
				}
				else
				{
					for (String key : keys)
					{
						Label after_key_label = new Label();
method.visitVarInsn            (ALOAD, 1);
method.visitLdcInsn            (key);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
method.visitJumpInsn           (IFEQ, after_key_label);
					
						block_data = mBlocks.get(key);
						
						Iterator<ParsedBlockPart>	block_data_it = null;
						ParsedBlockPart				block_part = null;
			
						block_data_it = block_data.iterator();
						while (block_data_it.hasNext())
						{
							block_part = block_data_it.next();
							
							if (ParsedBlockPart.TEXT == block_part.getType())
							{
								text_count++;
							}
							else if (ParsedBlockPart.VALUE == block_part.getType())
							{
								value_count++;
							}
						}
					
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 2);
addIntegerConst                (method, text_count+value_count);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "increasePartsCapacityInternal", "(Lcom/uwyn/rife/template/InternalValue;I)V");

method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 2);
addIntegerConst                (method, value_count);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "increaseValuesCapacityInternal", "(Lcom/uwyn/rife/template/InternalValue;I)V");
					
						block_data_it = block_data.iterator();
						while (block_data_it.hasNext())
						{
							block_part = block_data_it.next();
	
							static_identifier = static_identifiers_it.next();
							
							block_part.visitByteCodeInternalForm(method, full_classname, static_identifier);
						}
						
method.visitJumpInsn           (GOTO, internal_found);
method.visitLabel              (after_key_label);
					}
method.visitInsn               (ICONST_0);
method.visitInsn               (IRETURN);
				}
			}
			
method.visitLabel              (internal_default);
method.visitInsn               (ICONST_0);
method.visitInsn               (IRETURN);
method.visitLabel              (internal_found);
method.visitInsn               (ICONST_1);
method.visitInsn               (IRETURN);
method.visitMaxs               (0, 0);
		}
		
		// generate the method that will return the defined default values
method = class_writer.visitMethod(ACC_PUBLIC, "getDefaultValue", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
		{
			Label	after_null_check = new Label();
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ALOAD, 1);
method.visitJumpInsn           (IF_ACMPNE, after_null_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("id can't be null.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_null_check);

			Label	after_empty_check = new Label();
method.visitInsn               (ICONST_0);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "length", "()I");
method.visitJumpInsn           (IF_ICMPNE, after_empty_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("id can't be empty.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_empty_check);
		
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ASTORE, 2);

			if (mBlockvalues.size() > 0)
			{
				Label blockvalue_doesntexist_label = new Label();
method.visitFieldInsn          (GETSTATIC, full_classname, "sBlockvalues", "Ljava/util/ArrayList;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/ArrayList", "contains", "(Ljava/lang/Object;)Z");
method.visitJumpInsn           (IFEQ, blockvalue_doesntexist_label);
method.visitTypeInsn           (NEW, "com/uwyn/rife/template/ExternalValue");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "com/uwyn/rife/template/ExternalValue", "<init>", "()V");
method.visitVarInsn            (ASTORE, 3);
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 1);
method.visitVarInsn            (ALOAD, 3);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "appendBlockExternalForm", "(Ljava/lang/String;Lcom/uwyn/rife/template/ExternalValue;)Z");
method.visitInsn               (POP);
method.visitVarInsn            (ALOAD, 3);
method.visitMethodInsn         (INVOKEVIRTUAL, "com/uwyn/rife/template/ExternalValue", "toString", "()Ljava/lang/String;");
method.visitVarInsn            (ASTORE, 2);
method.visitLabel              (blockvalue_doesntexist_label);
			}
			if (mDefaultValues.size() > 0)
			{
				Label defaultvalues_alreadyexist_label = new Label();
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ALOAD, 2);
method.visitJumpInsn           (IF_ACMPNE, defaultvalues_alreadyexist_label);
method.visitFieldInsn          (GETSTATIC, full_classname, "sDefaultValues", "Ljava/util/HashMap;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
method.visitTypeInsn           (CHECKCAST, "java/lang/String");
method.visitVarInsn            (ASTORE, 2);
method.visitLabel              (defaultvalues_alreadyexist_label);
			}

method.visitVarInsn            (ALOAD, 2);
method.visitInsn               (ARETURN);
method.visitMaxs               (0, 0);
		}
		
		// generate the method that will append defined default values
		// for external usage
method = class_writer.visitMethod(ACC_PROTECTED, "appendDefaultValueExternalForm", "(Ljava/lang/String;Lcom/uwyn/rife/template/ExternalValue;)Z", null, null);
		{
method.visitInsn               (ICONST_0);
method.visitVarInsn            (ISTORE, 3);
			if (mBlockvalues.size() > 0)
			{
				Label blockvalue_doesntexist_label = new Label();
method.visitFieldInsn          (GETSTATIC, full_classname, "sBlockvalues", "Ljava/util/ArrayList;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/ArrayList", "contains", "(Ljava/lang/Object;)Z");
method.visitJumpInsn           (IFEQ, blockvalue_doesntexist_label);
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 1);
method.visitVarInsn            (ALOAD, 2);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "appendBlockExternalForm", "(Ljava/lang/String;Lcom/uwyn/rife/template/ExternalValue;)Z");
method.visitInsn               (POP);
method.visitInsn               (ICONST_1);
method.visitVarInsn            (ISTORE, 3);
method.visitLabel              (blockvalue_doesntexist_label);
			}
			if (mDefaultValues.size() > 0)
			{
				Label alreadyfound_defaultvalue_label = new Label();
method.visitVarInsn            (ILOAD, 3);
method.visitJumpInsn           (IFNE, alreadyfound_defaultvalue_label);
method.visitFieldInsn          (GETSTATIC, full_classname, "sDefaultValues", "Ljava/util/HashMap;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "containsKey", "(Ljava/lang/Object;)Z");
method.visitJumpInsn           (IFEQ, alreadyfound_defaultvalue_label);
method.visitVarInsn            (ALOAD, 2);
method.visitFieldInsn          (GETSTATIC, full_classname, "sDefaultValues", "Ljava/util/HashMap;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
method.visitTypeInsn           (CHECKCAST, "java/lang/String");
method.visitMethodInsn         (INVOKEVIRTUAL, "com/uwyn/rife/template/ExternalValue", "append", "(Ljava/lang/CharSequence;)V");
method.visitInsn               (ICONST_1);
method.visitVarInsn            (ISTORE, 3);
method.visitLabel              (alreadyfound_defaultvalue_label);
			}
method.visitVarInsn            (ILOAD, 3);
method.visitInsn               (IRETURN);
method.visitMaxs               (0, 0);
		}
		
		// generate the method that will append defined default values
		// for internal usage
method = class_writer.visitMethod(ACC_PROTECTED, "appendDefaultValueInternalForm", "(Ljava/lang/String;Lcom/uwyn/rife/template/InternalValue;)Z", null, null);
		{
			if (mBlockvalues.size() > 0)
			{
				Label blockvalue_doesntexist_label = new Label();
method.visitFieldInsn          (GETSTATIC, full_classname, "sBlockvalues", "Ljava/util/ArrayList;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/ArrayList", "contains", "(Ljava/lang/Object;)Z");
method.visitJumpInsn           (IFEQ, blockvalue_doesntexist_label);
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 1);
method.visitVarInsn            (ALOAD, 2);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "appendBlockInternalForm", "(Ljava/lang/String;Lcom/uwyn/rife/template/InternalValue;)Z");
method.visitInsn               (POP);
method.visitInsn               (ICONST_1);
method.visitInsn               (IRETURN);
method.visitLabel              (blockvalue_doesntexist_label);
			}
method.visitInsn               (ICONST_0);
method.visitInsn               (IRETURN);
method.visitMaxs               (0, 0);
		}
		
		// generate the method that checks the modification status of this particular template class
method = class_writer.visitMethod(ACC_PUBLIC|ACC_STATIC, "isModified", "(Lcom/uwyn/rife/resources/ResourceFinder;Ljava/lang/String;)Z", null, null);
method.visitFieldInsn          (GETSTATIC, full_classname, "sResource", "Ljava/net/URL;");
method.visitMethodInsn         (INVOKESTATIC, full_classname, "getModificationTimeReal", "()J");
method.visitFieldInsn          (GETSTATIC, full_classname, "sDependencies", "Ljava/util/HashMap;");
method.visitMethodInsn         (INVOKESTATIC, full_classname, "getModificationState", "()Ljava/lang/String;");
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKESTATIC, full_classname, "isTemplateClassModified", "(Ljava/net/URL;JLjava/util/Map;Ljava/lang/String;Lcom/uwyn/rife/resources/ResourceFinder;Ljava/lang/String;)Z");
method.visitInsn               (IRETURN);
method.visitMaxs               (0, 0);

		// generate the method that checks if a value is present in a template
method = class_writer.visitMethod(ACC_PUBLIC, "hasValueId", "(Ljava/lang/String;)Z", null, null);
			if (mValueIds.size() > 0)
			{
method.visitFieldInsn          (GETSTATIC, full_classname, "sValueIds", "Ljava/util/HashSet;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashSet", "contains", "(Ljava/lang/Object;)Z");
			}
			else
			{
method.visitInsn               (ICONST_0);
			}
method.visitInsn               (IRETURN);
method.visitMaxs               (0, 0);

		// generate the method that returns all values that are available
method = class_writer.visitMethod(ACC_PUBLIC, "getAvailableValueIds", "()[Ljava/lang/String;", null, null);
			if (mValueIds.size() > 0)
			{
method.visitFieldInsn          (GETSTATIC, full_classname, "sValueIdsArray", "[Ljava/lang/String;");
			}
			else
			{
method.visitInsn               (ICONST_0);
method.visitTypeInsn           (ANEWARRAY, "java/lang/String");
			}
method.visitInsn               (ARETURN);
method.visitMaxs               (0, 0);

			// generate the method that returns all values that aren't set yet
method = class_writer.visitMethod(ACC_PUBLIC, "getUnsetValueIds", "()Ljava/util/Collection;", null, null);
method.visitTypeInsn           (NEW, "java/util/ArrayList");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
method.visitVarInsn            (ASTORE, 1);
			if (mValueIds.size() > 0)
			{
				Label while_start_label = new Label();
				Label while_end_label = new Label();
method.visitFieldInsn          (GETSTATIC, full_classname, "sValueIds", "Ljava/util/HashSet;");
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashSet", "iterator", "()Ljava/util/Iterator;");
method.visitVarInsn            (ASTORE, 2);
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ASTORE, 3);
method.visitLabel              (while_start_label);
method.visitVarInsn            (ALOAD, 2);
method.visitMethodInsn         (INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
method.visitJumpInsn           (IFEQ, while_end_label);
method.visitVarInsn            (ALOAD, 2);
method.visitMethodInsn         (INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
method.visitTypeInsn           (CHECKCAST, "java/lang/String");
method.visitVarInsn            (ASTORE, 3);
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 3);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "isValueSet", "(Ljava/lang/String;)Z");
method.visitJumpInsn           (IFNE, while_start_label);
method.visitVarInsn            (ALOAD, 0);
method.visitVarInsn            (ALOAD, 3);
method.visitMethodInsn         (INVOKEVIRTUAL, full_classname, "hasDefaultValue", "(Ljava/lang/String;)Z");
method.visitJumpInsn           (IFNE, while_start_label);
method.visitVarInsn            (ALOAD, 1);
method.visitVarInsn            (ALOAD, 3);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
method.visitInsn               (POP);
method.visitJumpInsn           (GOTO, while_start_label);
method.visitLabel              (while_end_label);
			}
method.visitVarInsn            (ALOAD, 1);
method.visitInsn               (ARETURN);
method.visitMaxs               (0, 0);

			// generate the method that returns the list of blocks according to a filter
method = class_writer.visitMethod(ACC_PUBLIC, "getFilteredBlocks", "(Ljava/lang/String;)Ljava/util/List;", null, null);
			{
				Label	after_null_check = new Label();
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ALOAD, 1);
method.visitJumpInsn           (IF_ACMPNE, after_null_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("filter can't be null.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_null_check);
				
				Label	after_empty_check = new Label();
method.visitInsn               (ICONST_0);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "length", "()I");
method.visitJumpInsn           (IF_ICMPNE, after_empty_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("filter can't be empty.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_empty_check);
				
				if (mFilteredBlocksMap != null)
				{
method.visitFieldInsn          (GETSTATIC, full_classname, "sFilteredBlocksMap", "Ljava/util/HashMap;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
method.visitTypeInsn           (CHECKCAST, "java/util/List");
method.visitVarInsn            (ASTORE, 2);
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ALOAD, 2);
				Label	list_null_check = new Label();
method.visitJumpInsn           (IF_ACMPNE, list_null_check);
method.visitFieldInsn          (GETSTATIC, "java/util/Collections", "EMPTY_LIST", "Ljava/util/List;");
method.visitVarInsn            (ASTORE, 2);
method.visitLabel              (list_null_check);
method.visitVarInsn            (ALOAD, 2);
method.visitInsn               (ARETURN);
				}
				else
				{
method.visitFieldInsn          (GETSTATIC, "java/util/Collections", "EMPTY_LIST", "Ljava/util/List;");
method.visitInsn               (ARETURN);
				}
method.visitMaxs               (0, 0);
			}

			// generate the method that verifies if blocks are present that match a certain filter
method = class_writer.visitMethod(ACC_PUBLIC, "hasFilteredBlocks", "(Ljava/lang/String;)Z", null, null);
			{
				Label	after_null_check = new Label();
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ALOAD, 1);
method.visitJumpInsn           (IF_ACMPNE, after_null_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("filter can't be null.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_null_check);
				
				Label	after_empty_check = new Label();
method.visitInsn               (ICONST_0);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "length", "()I");
method.visitJumpInsn           (IF_ICMPNE, after_empty_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("filter can't be empty.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_empty_check);
				
				if (mFilteredBlocksMap != null)
				{
method.visitFieldInsn          (GETSTATIC, full_classname, "sFilteredBlocksMap", "Ljava/util/HashMap;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "containsKey", "(Ljava/lang/Object;)Z");
method.visitInsn               (IRETURN);
				}
				else
				{
method.visitInsn               (ICONST_0);
method.visitInsn               (IRETURN);
				}
method.visitMaxs               (0, 0);
			}

			// generate the method that returns the list of values according to a filter
method = class_writer.visitMethod(ACC_PUBLIC, "getFilteredValues", "(Ljava/lang/String;)Ljava/util/List;", null, null);
			{
				Label	after_null_check = new Label();
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ALOAD, 1);
method.visitJumpInsn           (IF_ACMPNE, after_null_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("filter can't be null.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_null_check);
				
				Label	after_empty_check = new Label();
method.visitInsn               (ICONST_0);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "length", "()I");
method.visitJumpInsn           (IF_ICMPNE, after_empty_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("filter can't be empty.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_empty_check);
				
				if (mFilteredValuesMap != null)
				{
method.visitFieldInsn          (GETSTATIC, full_classname, "sFilteredValuesMap", "Ljava/util/HashMap;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
method.visitTypeInsn           (CHECKCAST, "java/util/List");
method.visitVarInsn            (ASTORE, 2);
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ALOAD, 2);
				Label	list_null_check = new Label();
method.visitJumpInsn           (IF_ACMPNE, list_null_check);
method.visitFieldInsn          (GETSTATIC, "java/util/Collections", "EMPTY_LIST", "Ljava/util/List;");
method.visitVarInsn            (ASTORE, 2);
method.visitLabel              (list_null_check);
method.visitVarInsn            (ALOAD, 2);
method.visitInsn               (ARETURN);
				}
				else
				{
method.visitFieldInsn          (GETSTATIC, "java/util/Collections", "EMPTY_LIST", "Ljava/util/List;");
method.visitInsn               (ARETURN);
				}
method.visitMaxs               (0, 0);
			}

			// generate the method that verifies if values are present that match a certain filter
method = class_writer.visitMethod(ACC_PUBLIC, "hasFilteredValues", "(Ljava/lang/String;)Z", null, null);
			{
				Label	after_null_check = new Label();
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ALOAD, 1);
method.visitJumpInsn           (IF_ACMPNE, after_null_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("filter can't be null.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_null_check);
				
				Label	after_empty_check = new Label();
method.visitInsn               (ICONST_0);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/lang/String", "length", "()I");
method.visitJumpInsn           (IF_ICMPNE, after_empty_check);
method.visitTypeInsn           (NEW, "java/lang/IllegalArgumentException");
method.visitInsn               (DUP);
method.visitLdcInsn            ("filter can't be empty.");
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
method.visitInsn               (ATHROW);
method.visitLabel              (after_empty_check);
				
				if (mFilteredValuesMap != null)
				{
method.visitFieldInsn          (GETSTATIC, full_classname, "sFilteredValuesMap", "Ljava/util/HashMap;");
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "containsKey", "(Ljava/lang/Object;)Z");
method.visitInsn               (IRETURN);
				}
				else
				{
method.visitInsn               (ICONST_0);
method.visitInsn               (IRETURN);
				}
method.visitMaxs               (0, 0);
			}
			
			// generate the method that returns the dependencies
method = class_writer.visitMethod(1, "getDependencies", "()Ljava/util/Map;", null, null);
method.visitFieldInsn          (GETSTATIC, full_classname, "sDependencies", "Ljava/util/HashMap;");
method.visitInsn               (ARETURN);
method.visitMaxs               (0, 0);
			
			// performs all the static initialization
class_writer.visitField(ACC_PRIVATE|ACC_STATIC, "sResource", "Ljava/net/URL;", null, null);
class_writer.visitField(ACC_PRIVATE|ACC_STATIC, "sDependencies", "Ljava/util/HashMap;", null, null);
			for (Map.Entry<String, ParsedBlockPart> entry : blockparts_order.entrySet())
			{
entry.getValue().visitByteCodeStaticDeclaration(class_writer, entry.getKey());
			}
			if (mDefaultValues.size() > 0)
			{
class_writer.visitField(ACC_PRIVATE|ACC_STATIC, "sDefaultValues", "Ljava/util/HashMap;", null, null);
			}
			if (mBlockvalues.size() > 0)
			{
class_writer.visitField(ACC_PRIVATE|ACC_STATIC, "sBlockvalues", "Ljava/util/ArrayList;", null, null);
			}
			if (mValueIds.size() > 0)
			{
class_writer.visitField(ACC_PRIVATE|ACC_STATIC, "sValueIds", "Ljava/util/HashSet;", null, null);
class_writer.visitField(ACC_PRIVATE|ACC_STATIC, "sValueIdsArray", "[Ljava/lang/String;", null, null);
			}
			if (mFilteredBlocksMap != null)
			{
class_writer.visitField(ACC_PRIVATE|ACC_STATIC, "sFilteredBlocksMap", "Ljava/util/HashMap;", null, null);
			}
			if (mFilteredValuesMap != null)
			{
class_writer.visitField(ACC_PRIVATE|ACC_STATIC, "sFilteredValuesMap", "Ljava/util/HashMap;", null, null);
			}

			// static initialization
method = class_writer.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);

			// set the resource
			Label resource_start_label = new Label();
			Label resource_end_label = new Label();
			Label resource_handler_label = new Label();
			Label after_resource_label = new Label();
method.visitLabel              (resource_start_label);
method.visitTypeInsn           (NEW, "java/net/URL");
method.visitInsn               (DUP);
method.visitLdcInsn            (mResource.getProtocol());
method.visitLdcInsn            (mResource.getHost());
addIntegerConst(method, mResource.getPort());
method.visitLdcInsn            (mResource.getFile());
method.visitMethodInsn         (INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V");
method.visitFieldInsn          (PUTSTATIC, full_classname, "sResource", "Ljava/net/URL;");
method.visitLabel              (resource_end_label);
method.visitJumpInsn           (GOTO, after_resource_label);
method.visitLabel              (resource_handler_label);
method.visitVarInsn            (ASTORE, 0);
method.visitInsn               (ACONST_NULL);
method.visitFieldInsn          (PUTSTATIC, full_classname, "sResource", "Ljava/net/URL;");
method.visitTryCatchBlock      (resource_start_label, resource_end_label, resource_handler_label, "java/net/MalformedURLException");
method.visitLabel              (after_resource_label);
			
			// set the file dependencies
method.visitTypeInsn           (NEW, "java/util/HashMap");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
method.visitFieldInsn          (PUTSTATIC, full_classname, "sDependencies", "Ljava/util/HashMap;");
			if (mDependencies.size() > 0)
			{
				for (URL url : mDependencies.keySet())
				{
					Label url_start_label = new Label();
					Label url_end_label = new Label();
					Label url_handler_label = new Label();
					Label after_url_label = new Label();
method.visitLabel              (url_start_label);
method.visitFieldInsn          (GETSTATIC, full_classname, "sDependencies", "Ljava/util/HashMap;");
method.visitTypeInsn           (NEW, "java/net/URL");
method.visitInsn               (DUP);
method.visitLdcInsn            (url.getProtocol());
method.visitLdcInsn            (url.getHost());
addIntegerConst(method, url.getPort());
method.visitLdcInsn            (url.getFile());
method.visitMethodInsn         (INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V");
method.visitTypeInsn           (NEW, "java/lang/Long");
method.visitInsn               (DUP);
method.visitLdcInsn            (mDependencies.get(url).longValue());
method.visitMethodInsn         (INVOKESPECIAL, "java/lang/Long", "<init>", "(J)V");
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
method.visitInsn               (POP);
method.visitLabel              (url_end_label);
method.visitJumpInsn           (GOTO, after_url_label);
method.visitLabel              (url_handler_label);
method.visitVarInsn            (ASTORE, 0);
method.visitTryCatchBlock      (url_start_label, url_end_label, url_handler_label, "java/net/MalformedURLException");
method.visitLabel              (after_url_label);
				}
			}

			// generate the static initialization for the block data
			for (Map.Entry<String, ParsedBlockPart> entry : blockparts_order.entrySet())
			{
entry.getValue().visitByteCodeStaticDefinition(method, full_classname, entry.getKey());
			}
		
			// set the default values if they're present
			if (mDefaultValues.size() > 0)
			{
method.visitTypeInsn           (NEW, "java/util/HashMap");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
method.visitFieldInsn          (PUTSTATIC, full_classname, "sDefaultValues", "Ljava/util/HashMap;");
				for (String key : mDefaultValues.keySet())
				{
method.visitFieldInsn          (GETSTATIC, full_classname, "sDefaultValues", "Ljava/util/HashMap;");
method.visitLdcInsn            (key);
method.visitLdcInsn            (mDefaultValues.get(key));
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
method.visitInsn               (POP);
				}
			}
		
			// set the blockvalues if they're present
			if (mBlockvalues.size() > 0)
			{
method.visitTypeInsn           (NEW, "java/util/ArrayList");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
method.visitFieldInsn          (PUTSTATIC, full_classname, "sBlockvalues", "Ljava/util/ArrayList;");
				for (String key : mBlockvalues)
				{
method.visitFieldInsn          (GETSTATIC, full_classname, "sBlockvalues", "Ljava/util/ArrayList;");
method.visitLdcInsn            (key);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
method.visitInsn               (POP);
				}
			}
		
			// set the values ids if they're present
			if (mValueIds.size() > 0)
			{
method.visitTypeInsn           (NEW, "java/util/HashSet");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/HashSet", "<init>", "()V");
method.visitFieldInsn          (PUTSTATIC, full_classname, "sValueIds", "Ljava/util/HashSet;");
				for (String id : mValueIds)
				{
method.visitFieldInsn          (GETSTATIC, full_classname, "sValueIds", "Ljava/util/HashSet;");
method.visitLdcInsn            (id);
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashSet", "add", "(Ljava/lang/Object;)Z");
method.visitInsn               (POP);
				}
method.visitFieldInsn          (GETSTATIC, full_classname, "sValueIds", "Ljava/util/HashSet;");
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashSet", "size", "()I");
method.visitTypeInsn           (ANEWARRAY, "java/lang/String");
method.visitFieldInsn          (PUTSTATIC, full_classname, "sValueIdsArray", "[Ljava/lang/String;");
method.visitFieldInsn          (GETSTATIC, full_classname, "sValueIds", "Ljava/util/HashSet;");
method.visitFieldInsn          (GETSTATIC, full_classname, "sValueIdsArray", "[Ljava/lang/String;");
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashSet", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;");
method.visitInsn               (POP);
			}
		
			// write the filtered blocks if they're present
			if (mFilteredBlocksMap != null)
			{
method.visitTypeInsn           (NEW, "java/util/HashMap");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
method.visitFieldInsn          (PUTSTATIC, full_classname, "sFilteredBlocksMap", "Ljava/util/HashMap;");

method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ASTORE, 0);
				FilteredTags		filtered_blocks = null;
				
				for (String key : mFilteredBlocksMap.keySet())
				{
					filtered_blocks = mFilteredBlocksMap.getFilteredTag(key);
method.visitTypeInsn           (NEW, "java/util/ArrayList");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
method.visitVarInsn            (ASTORE, 1);

					for (String[] captured_groups : filtered_blocks)
					{
method.visitVarInsn            (ALOAD, 1);
addIntegerConst(method, captured_groups.length);
method.visitTypeInsn           (ANEWARRAY, "java/lang/String");
						for (int i = 0; i < captured_groups.length; i++)
						{
method.visitInsn               (DUP);
addIntegerConst(method, i);
method.visitLdcInsn            (captured_groups[i]);
method.visitInsn               (AASTORE);
						}
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
method.visitInsn               (POP);
					}
method.visitFieldInsn          (GETSTATIC, full_classname, "sFilteredBlocksMap", "Ljava/util/HashMap;");
method.visitLdcInsn            (key);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn		   (INVOKESTATIC, "java/util/Collections", "unmodifiableList", "(Ljava/util/List;)Ljava/util/List;");
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
method.visitInsn               (POP);
				}
			}
		
			// write the filtered values if they're present
			if (mFilteredValuesMap != null)
			{
method.visitTypeInsn           (NEW, "java/util/HashMap");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
method.visitFieldInsn          (PUTSTATIC, full_classname, "sFilteredValuesMap", "Ljava/util/HashMap;");
				
method.visitInsn               (ACONST_NULL);
method.visitVarInsn            (ASTORE, 1);
				FilteredTags		filtered_values = null;
				
				for (String key : mFilteredValuesMap.keySet())
				{
					filtered_values = mFilteredValuesMap.getFilteredTag(key);
method.visitTypeInsn           (NEW, "java/util/ArrayList");
method.visitInsn               (DUP);
method.visitMethodInsn         (INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
method.visitVarInsn            (ASTORE, 1);

					for (String[] captured_groups : filtered_values)
					{
method.visitVarInsn            (ALOAD, 1);
addIntegerConst(method, captured_groups.length);
method.visitTypeInsn           (ANEWARRAY, "java/lang/String");
						for (int i = 0; i < captured_groups.length; i++)
						{
method.visitInsn               (DUP);
addIntegerConst(method, i);
							if (null == captured_groups[i])
							{
method.visitInsn               (ACONST_NULL);
							}
							else
							{
method.visitLdcInsn            (captured_groups[i]);
							}
method.visitInsn               (AASTORE);
						}
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
method.visitInsn               (POP);
					}
method.visitFieldInsn          (GETSTATIC, full_classname, "sFilteredValuesMap", "Ljava/util/HashMap;");
method.visitLdcInsn            (key);
method.visitVarInsn            (ALOAD, 1);
method.visitMethodInsn		   (INVOKESTATIC, "java/util/Collections", "unmodifiableList", "(Ljava/util/List;)Ljava/util/List;");
method.visitMethodInsn         (INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
method.visitInsn               (POP);
				}
			}
method.visitInsn               (RETURN);
method.visitMaxs               (0, 0);

class_writer.visitEnd();
		
		return class_writer.toByteArray();
	}
	
	void setTemplateName(String templateName)
	{
		assert templateName != null;

		mTemplateName = templateName;
	}
	
	String getTemplateName()
	{
		return mTemplateName;
	}

	void setClassName(String className)
	{
		assert className != null;

		mClassName = className;
	}

	String getClassName()
	{
		return mClassName;
	}

	String getFullClassName()
	{
		if (null != mClassName)
		{
			return mPackage+"."+mClassName;
		}
		else
		{
			return null;
		}
	}

	void setResource(URL resource)
	{
		assert resource != null;

		mResource = resource;
	}

	URL getResource()
	{
		return mResource;
	}

	private long getModificationTime()
	throws TemplateException
	{
		if (-1 == mModificationTime)
		{
			mModificationTime = Parser.getModificationTime(mParser.getTemplateFactory().getResourceFinder(), getResource());
		}
		
		assert mModificationTime > 0;
		
		return mModificationTime;
	}
	
	void setBlock(String id, ParsedBlockData blockData)
	{
		assert id != null;
		assert blockData != null;

		mBlocks.put(id, blockData);
	}

	ParsedBlockData getContent()
	{
		return getBlock("");
	}

	ParsedBlockData getBlock(String id)
	{
		assert id != null;

		return mBlocks.get(id);
	}

	Collection<String> getBlockIds()
	{
		return mBlocks.keySet();
	}

	Map<String, ParsedBlockData> getBlocks()
	{
		return mBlocks;
	}
	
	void addValue(String id)
	{
		assert id != null;
		assert id.length() > 0;
		
		mValueIds.add(id);
	}
	
	Collection<String> getValueIds()
	{
		return mValueIds;
	}

	void setDefaultValue(String id, String value)
	{
		assert id != null;
		assert id.length() > 0;
		assert value != null;

		mDefaultValues.put(id, value);
	}

	void setBlockvalue(String id)
	{
		assert id != null;
		assert id.length() > 0;

		mBlockvalues.add(id);
	}
	
	String getDefaultValue(String id)
	{
		assert id != null;
		assert id.length() > 0;

		return mDefaultValues.get(id);
	}

	boolean hasDefaultValue(String id)
	{
		assert id != null;
		assert id.length() > 0;

		return mDefaultValues.containsKey(id);
	}

	boolean hasBlockvalue(String id)
	{
		assert id != null;
		assert id.length() > 0;

		return mBlockvalues.contains(id);
	}
	
	Map<String, String> getDefaultValues()
	{
		return mDefaultValues;
	}

	List<String> getBlockvalues()
	{
		return mBlockvalues;
	}
	
	void addDependency(Parsed parsed)
	throws TemplateException
	{
		assert parsed != null;

		long modification_time = -1;

		// store the filename in the array of dependent files
		try
		{
			modification_time = parsed.getModificationTime();
		}
		catch (TemplateException e)
		{
			// set the modification time to -1, this means that the dependent file will be considered
			// as outdated at the next verification
			modification_time = -1;
		}
		mDependencies.put(parsed.getResource(), new Long(modification_time));
	}

	void addDependency(URL resource, Long modificationTime)
	{
		mDependencies.put(resource, modificationTime);
	}

	Map<URL, Long> getDependencies()
	{
		return mDependencies;
	}
	
	void setModificationState(String state)
	{
		mModificationState = state;
	}

	String getPackage()
	{
		return mPackage;
	}

	void setPackage(String thePackage)
	{
		assert thePackage != null;

		mPackage = thePackage;
	}
	
	void setFilteredValues(FilteredTagsMap filteredValues)
	{
		mFilteredValuesMap = filteredValues;
	}
	
	FilteredTagsMap getFilteredValuesMap()
	{
		return mFilteredValuesMap;
	}
	
	void setFilteredBlocks(FilteredTagsMap filteredBlocks)
	{
		mFilteredBlocksMap = filteredBlocks;
	}
	
	FilteredTagsMap getFilteredBlocksMap()
	{
		return mFilteredBlocksMap;
	}
}


