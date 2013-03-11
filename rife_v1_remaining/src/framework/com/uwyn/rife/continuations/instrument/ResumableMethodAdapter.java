/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResumableMethodAdapter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.*;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import java.util.Stack;
import java.util.logging.Level;

import static com.uwyn.rife.continuations.instrument.ContinuationDebug.*;

class ResumableMethodAdapter implements MethodVisitor, Opcodes
{	
	private ContinuationConfigInstrument	mConfig = null;

	private TypesClassVisitor	mTypes = null;
	
	private MethodVisitor	mMethodVisitor = null;
	private String			mClassName = null;
	private String			mClassNameInternal = null;
	private boolean			mVisit = false;
	private boolean			mAdapt = false;
	private int				mContextIndex = -1;
	private int				mCallTargetIndex = -1;
	private int				mAnswerIndex = -1;
	private int				mTempIndex = -1;

	private Label			mDefaultLabel = null;
	private Label			mRethrowLabel = null;
	private boolean			mVisitRethrowLabel = false;
	private Label[]			mLabels = null;
	private int				mLabelIndex = 0;
	
	private int				mMaxLocalIndex = 0;
	
	private TypesContext	mLabelContext = null;
	
	private boolean			mDisableCodeguideBackInTime = false;
	private boolean			mDisabledCodeguideBackInTime = false;

	private NoOpAnnotationVisitor	mAnnotationVisitor = new NoOpAnnotationVisitor();

	private void debugMessage(String message)
	{
 		if (ContinuationDebug.sTrace &&
			ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
		{
			mMethodVisitor.visitFieldInsn(GETSTATIC, "com/uwyn/rife/continuations/instrument/ContinuationDebug", "LOGGER", "Ljava/util/logging/Logger;");
			mMethodVisitor.visitLdcInsn(message);
			mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/logging/Logger", "finest", "(Ljava/lang/String;)V");
		}
	}

	ResumableMethodAdapter(ContinuationConfigInstrument config, TypesClassVisitor types, MethodVisitor methodVisitor, String className, boolean adapt, int maxLocals, int pauseCount)
	{
		mConfig = config;
		mTypes = types;
		mMethodVisitor = methodVisitor;
		mClassName = className;
		if (className != null)
		{
			mClassNameInternal = mClassName.replace('.', '/');
		}
		mVisit = (mMethodVisitor != null);
		mAdapt = adapt;
		mContextIndex = maxLocals;
		mCallTargetIndex = mContextIndex+1;
		mAnswerIndex = mCallTargetIndex+1;
		mTempIndex = mAnswerIndex+1;
			
		if (mAdapt)
		{
			// create all the labels beforehand
			mDefaultLabel = new Label();
			if (pauseCount > 0)
			{
				mLabels = new Label[pauseCount];
				for (int i = 0; i < pauseCount; i++)
				{
					mLabels[i] = new Label();
				}
			}
			
			debugMessage("CONT: context initializing");
			// get the current context for the current method and register it
			// after the last local variable
			mMethodVisitor.visitVarInsn(ALOAD, 0);
			mMethodVisitor.visitMethodInsn(INVOKESTATIC, "com/uwyn/rife/continuations/ContinuationContext", "createOrResetContext", "(Ljava/lang/Object;)Lcom/uwyn/rife/continuations/ContinuationContext;");
			mMethodVisitor.visitVarInsn(ASTORE, mContextIndex);
			debugMessage("CONT: context set up");

			if (pauseCount > 0)
			{
				debugMessage("CONT: context obtain label");
				// get a reference to the context object
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				// retrieve the current label index
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLabel", "()I");
				debugMessage("CONT: evaluate tableswitch");
				mMethodVisitor.visitTableSwitchInsn(0, pauseCount-1, mDefaultLabel, mLabels);
			}
			
			// set the default label to the start of the code
			mMethodVisitor.visitLabel(mDefaultLabel);
			debugMessage("CONT: begin of code");
		}
	}
	
	// store an integer on the stack
	private void addIntegerConst(int value)
	{
		switch (value)
		{
			case 0:
				mMethodVisitor.visitInsn(ICONST_0);
				break;
			case 1:
				mMethodVisitor.visitInsn(ICONST_1);
				break;
			case 2:
				mMethodVisitor.visitInsn(ICONST_2);
				break;
			case 3:
				mMethodVisitor.visitInsn(ICONST_3);
				break;
			case 4:
				mMethodVisitor.visitInsn(ICONST_4);
				break;
			case 5:
				mMethodVisitor.visitInsn(ICONST_5);
				break;
			default:
				mMethodVisitor.visitLdcInsn(value);
				break;
		}
	}
	
	/**
	 * Visits a local variable instruction. A local variable instruction is an
	 * instruction that loads or stores the value of a local variable.
	 *
	 * @param opcode the opcode of the local variable instruction to be visited.
	 *      This opcode is either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE,
	 *      LSTORE, FSTORE, DSTORE, ASTORE or RET.
	 * @param var the operand of the instruction to be visited. This operand is
	 *      the index of a local variable.
	 */
	public void visitVarInsn(int opcode, int var)
	{
		///CLOVER:OFF
 		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitVarInsn            ("+OPCODES[opcode]+", "+var+")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			// execute the original opcode
			mMethodVisitor.visitVarInsn(opcode, var);
			
			// if this is an exception block, check if the caught exception is a
			// pause exception, if it is, just throw it further again.
			if (mLabelContext != null &&
				TypesNode.EXCEPTION == mLabelContext.getSort() &&
				ASTORE == opcode)
			{
				if (null == mRethrowLabel)
				{
					mRethrowLabel = new Label();
					mVisitRethrowLabel = true;
				}
				
				Label label = new Label();
				mMethodVisitor.visitVarInsn(ALOAD, var);
				mMethodVisitor.visitTypeInsn(INSTANCEOF, "com/uwyn/rife/tools/exceptions/ControlFlowRuntimeException");
				mMethodVisitor.visitJumpInsn(IFEQ, label);
				mMethodVisitor.visitVarInsn(ALOAD, var);
				mMethodVisitor.visitJumpInsn(GOTO, mRethrowLabel);
				mMethodVisitor.visitLabel(label);
			}
			
			// catch local variable store opcodes so that they can also be
			// stored in the context object
			if (opcode == ISTORE ||
				opcode == LSTORE ||
				opcode == FSTORE ||
				opcode == DSTORE ||
				opcode == ASTORE)
			{
				// retain the maximum index of the local var storage
				if (var > mMaxLocalIndex)
				{
					mMaxLocalIndex = var;
				}
				
				// prepare the arguments of the context storage method
				
				// get a reference to the context object
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				// get a reference to the local variable stack
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalVars", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				
				// push the index of local var that has to be stored on the
				// stack
				addIntegerConst(var);
				
				// detect the opcode and handle the different local variable
				// types correctly
				switch (opcode)
				{
					// store ints
					case ISTORE:
						mMethodVisitor.visitVarInsn(ILOAD, var);
						mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "storeInt", "(II)V");
						break;
					// store longs
					case LSTORE:
						mMethodVisitor.visitVarInsn(LLOAD, var);
						mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "storeLong", "(IJ)V");
						break;
					// store floats
					case FSTORE:
						mMethodVisitor.visitVarInsn(FLOAD, var);
						mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "storeFloat", "(IF)V");
						break;
					// store doubles
					case DSTORE:
						mMethodVisitor.visitVarInsn(DLOAD, var);
						mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "storeDouble", "(ID)V");
						break;
					// store references
					case ASTORE:
						mMethodVisitor.visitVarInsn(ALOAD, var);
						mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "storeReference", "(ILjava/lang/Object;)V");
						break;
				}
			}
			
			// if this was the first ASTORE of an exception block, restore
			// the local types and change the type of the block so that this
			// isn't executed anymore
			if (mLabelContext != null &&
				TypesNode.EXCEPTION == mLabelContext.getSort() &&
				ASTORE == opcode)
			{
				// restore the local variable stack
				restoreLocalStack(mLabelContext);
				
				mLabelContext.setSort(TypesNode.REGULAR);
			}
		}
		else if (mVisit)
		{
			mMethodVisitor.visitVarInsn(opcode, var);
		}
	}
	
	/**
	 * Visits a method instruction. A method instruction is an instruction that
	 * invokes a method.
	 *
	 * @param opcode the opcode of the type instruction to be visited. This opcode
	 *      is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
	 *      INVOKEINTERFACE.
	 * @param owner the internal name of the method's owner class (see {@link
	 *      Type#getInternalName getInternalName}).
	 * @param name the method's name.
	 * @param desc the method's descriptor (see {@link Type Type}).
	 */
	public void visitMethodInsn(int opcode, String owner, String name, String desc)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitMethodInsn         ("+OPCODES[opcode]+", \""+owner+"\", \""+name+"\", \""+desc+"\")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			String owner_classname = owner.replace('/', '.');
			
			if (owner_classname.equals(mConfig.getContinuableSupportClassName()) || mClassName.equals(owner_classname))
			{
				if (mConfig.getPauseMethodName().equals(name) && "()V".equals(desc))
				{
					debugMessage("CONT: pause : undoing method call");
					
					// pop the ALOAD opcode off the stack
					mMethodVisitor.visitInsn(POP);
					
					TypesContext	context = mTypes.nextPauseContext();
					Stack<String>	stack = context.getStackClone();
					debugMessage("CONT: pause : saving operand stack");
					saveOperandStack(stack);
					
					debugMessage("CONT: pause : storing resume label");
					// get a reference to the context object
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					// set the index of the current label
					addIntegerConst(mLabelIndex);
					// set the new label index
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "setLabel", "(I)V");
					
					// generate the pause exception
					debugMessage("CONT: pause : throwing pause exception");
					mMethodVisitor.visitTypeInsn(NEW, "com/uwyn/rife/continuations/exceptions/PauseException");
					mMethodVisitor.visitInsn(DUP);
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitMethodInsn(INVOKESPECIAL, "com/uwyn/rife/continuations/exceptions/PauseException", "<init>", "(Lcom/uwyn/rife/continuations/ContinuationContext;)V");
					mMethodVisitor.visitInsn(ATHROW);
					
					// add label for skipping over resumed code
					mMethodVisitor.visitLabel(mLabels[mLabelIndex]);
					debugMessage("CONT: pause : resumed execution");

					// get a reference to the context object
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					// clear the label
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "clearLabel", "()V");

					// restore the local variable stack
					debugMessage("CONT: pause : restoring local stack");
					restoreLocalStack(context);
					
					// restore the local operand stack
					debugMessage("CONT: pause : restoring operand stack");
					restoreOperandStack(stack);

					mLabelIndex++;
				
					return;
				}
				else if (mConfig.getStepbackMethodName().equals(name) && "()V".equals(desc))
				{
					debugMessage("CONT: stepBack : undoing method call");
					
					// pop the ALOAD opcode off the stack
					mMethodVisitor.visitInsn(POP);
					
					TypesContext	context = mTypes.nextPauseContext();
					Stack<String>	stack = context.getStackClone();
					debugMessage("CONT: stepBack : saving operand stack");
					saveOperandStack(stack);
					
					// generate the stepBack exception
					debugMessage("CONT: stepBack : throwing pause exception");
					mMethodVisitor.visitTypeInsn(NEW, "com/uwyn/rife/continuations/exceptions/StepBackException");
					mMethodVisitor.visitInsn(DUP);
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitMethodInsn(INVOKESPECIAL, "com/uwyn/rife/continuations/exceptions/StepBackException", "<init>", "(Lcom/uwyn/rife/continuations/ContinuationContext;)V");
					mMethodVisitor.visitInsn(ATHROW);
					
					// add label for skipping over resumed code
					mMethodVisitor.visitLabel(mLabels[mLabelIndex]);
					debugMessage("CONT: stepBack : resumed execution");
					
					// restore the local variable stack
					debugMessage("CONT: stepBack : restoring local stack");
					restoreLocalStack(context);
					
					// restore the local operand stack
					debugMessage("CONT: stepBack : restoring operand stack");
					restoreOperandStack(stack);
					
					mLabelIndex++;
					
					return;
				}
				else if (mConfig.getCallMethodName().equals(name) && Type.getMethodDescriptor(mConfig.getCallMethodReturnType(), mConfig.getCallMethodArgumentTypes()).equals(desc))
				{
					// store the call target
					debugMessage("CONT: call : storing call target");
					mMethodVisitor.visitVarInsn(ASTORE, mCallTargetIndex);

					// pop the ALOAD opcode off the stack
					debugMessage("CONT: call : undoing method call");
					mMethodVisitor.visitInsn(POP);
					
					TypesContext	context = mTypes.nextPauseContext();
					Stack<String>	stack = context.getStackClone();
					stack.pop();
					debugMessage("CONT: call : saving operand stack");
					saveOperandStack(stack);
					
					debugMessage("CONT: call : storing resume label");
					// get a reference to the context object
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					// set the index of the current label
					addIntegerConst(mLabelIndex);
					// set the new label index
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "setLabel", "(I)V");
					
					// generate the pause exception
					debugMessage("CONT: call : throwing call exception");
					mMethodVisitor.visitTypeInsn(NEW, "com/uwyn/rife/continuations/exceptions/CallException");
					mMethodVisitor.visitInsn(DUP);
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitVarInsn(ALOAD, mCallTargetIndex);
					mMethodVisitor.visitMethodInsn(INVOKESPECIAL, "com/uwyn/rife/continuations/exceptions/CallException", "<init>", "(Lcom/uwyn/rife/continuations/ContinuationContext;Ljava/lang/Object;)V");
					mMethodVisitor.visitInsn(ATHROW);
					
					// add label for skipping over resumed code
					mMethodVisitor.visitLabel(mLabels[mLabelIndex]);
					debugMessage("CONT: call : resumed execution");

					// get a reference to the context object
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					// clear the label
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "clearLabel", "()V");

					// restore the local variable stack
					debugMessage("CONT: call : restoring local stack");
					restoreLocalStack(context);
					// restore the local operand stack
					debugMessage("CONT: call : restoring operand stack");
					restoreOperandStack(stack);
					
					debugMessage("CONT: call : retrieving call answer");
					// get a reference to the context object
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					// get the call answer
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getCallAnswer", "()Ljava/lang/Object;");
					mMethodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(mConfig.getCallMethodReturnType()));

					mLabelIndex++;
				
					return;
				}
				else if (mConfig.getAnswerMethodName().equals(name) && ("()V".equals(desc) || "(Ljava/lang/Object;)V".equals(desc)))
				{
					if ("()V".equals(desc))
					{
						mMethodVisitor.visitInsn(ACONST_NULL);
					}
					
					// store the answer
					debugMessage("CONT: call : storing answer");
					mMethodVisitor.visitVarInsn(ASTORE, mAnswerIndex);
					
					// pop the ALOAD opcode off the stack
					debugMessage("CONT: call : undoing method call");
					mMethodVisitor.visitInsn(POP);
					
					// generate the answer exception
					debugMessage("CONT: answer : throwing answer exception");
					mMethodVisitor.visitTypeInsn(NEW, "com/uwyn/rife/continuations/exceptions/AnswerException");
					mMethodVisitor.visitInsn(DUP);
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitVarInsn(ALOAD, mAnswerIndex);
					mMethodVisitor.visitMethodInsn(INVOKESPECIAL, "com/uwyn/rife/continuations/exceptions/AnswerException", "<init>", "(Lcom/uwyn/rife/continuations/ContinuationContext;Ljava/lang/Object;)V");
					mMethodVisitor.visitInsn(ATHROW);
					
					return;
				}
			}
		}
		
		if (mVisit)
		{
			mMethodVisitor.visitMethodInsn(opcode, owner, name, desc);
		}
	}

	/**
	 * Restore the local variable stack, first the computation
	 * types of category 1 and afterwards those of category 2
	 */
	private void restoreLocalStack(TypesContext context)
	{
		for (int i = 1; i <= mMaxLocalIndex; i++)
		{
			if (!context.hasVar(i))
			{
				continue;
			}
			
			switch (context.getVarType(i))
			{
				case Type.INT:
					debugMessage("CONT: restore local : "+i+", int");
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalVars", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
					addIntegerConst(i);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "getInt", "(I)I");
					mMethodVisitor.visitVarInsn(ISTORE, i);
					break;
				case Type.FLOAT:
					debugMessage("CONT: restore local : "+i+", float");
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalVars", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
					addIntegerConst(i);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "getFloat", "(I)F");
					mMethodVisitor.visitVarInsn(FSTORE, i);
					break;
				case Type.OBJECT:
					debugMessage("CONT: restore local : "+i+", "+context.getVar(i)+"");
					String type = context.getVar(i);
					if (TypesContext.TYPE_NULL.equals(type))
					{
						mMethodVisitor.visitInsn(ACONST_NULL);
						mMethodVisitor.visitVarInsn(ASTORE, i);
					}
					else
					{
						mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
						mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalVars", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
						addIntegerConst(i);
						mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "getReference", "(I)Ljava/lang/Object;");
						mMethodVisitor.visitTypeInsn(CHECKCAST, type);
						mMethodVisitor.visitVarInsn(ASTORE, i);
					}

					break;
			}
		}
		for (int i = 1; i <= mMaxLocalIndex; i++)
		{
			if (!context.hasVar(i))
			{
				continue;
			}
			
			switch (context.getVarType(i))
			{
				case Type.LONG:
					debugMessage("CONT: restore local : "+i+", long");
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalVars", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
					addIntegerConst(i);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "getLong", "(I)J");
					mMethodVisitor.visitVarInsn(LSTORE, i);
					break;
				case Type.DOUBLE:
					debugMessage("CONT: restore local : "+i+", double");
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalVars", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
					addIntegerConst(i);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "getDouble", "(I)D");
					mMethodVisitor.visitVarInsn(DSTORE, i);
					break;
			}
		}
	}

	/**
	 * Save the operand stack
	 */
	private void saveOperandStack(Stack<String> stack)
	{
		String tupe = null;
		
		// save all stack entries besides the last one pushed, it's the
		// element's object reference that is used for the stub continuation
		// methods
		for (int i = stack.size()-1; i >= 0 ; i--)
		{
			tupe = stack.get(i);

			if (tupe.equals(TypesContext.CAT1_BOOLEAN) ||
				tupe.equals(TypesContext.CAT1_CHAR) ||
				tupe.equals(TypesContext.CAT1_BYTE) ||
				tupe.equals(TypesContext.CAT1_SHORT) ||
				tupe.equals(TypesContext.CAT1_INT))
			{
				mMethodVisitor.visitVarInsn(ISTORE, mTempIndex);
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitVarInsn(ILOAD, mTempIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "pushInt", "(I)V");
			}
			else if (tupe.equals(TypesContext.CAT1_FLOAT))
			{
				mMethodVisitor.visitVarInsn(FSTORE, mTempIndex);
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitVarInsn(FLOAD, mTempIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "pushFloat", "(F)V");
			}
			else if (tupe.equals(TypesContext.CAT2_DOUBLE))
			{
				mMethodVisitor.visitVarInsn(DSTORE, mTempIndex);
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitVarInsn(DLOAD, mTempIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "pushDouble", "(D)V");
			}
			else if (tupe.equals(TypesContext.CAT2_LONG))
			{
				mMethodVisitor.visitVarInsn(LSTORE, mTempIndex);
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitVarInsn(LLOAD, mTempIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "pushLong", "(J)V");
			}
			else if (tupe.equals(TypesContext.CAT1_ADDRESS))
			{
				// this should never happen
				throw new RuntimeException("Invalid local stack type");
			}
			else
			{
				mMethodVisitor.visitVarInsn(ASTORE, mTempIndex);
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitVarInsn(ALOAD, mTempIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "pushReference", "(Ljava/lang/Object;)V");
			}
		}
	}

	/**
	 * Restore the operand stack
	 */
	private void restoreOperandStack(Stack<String> stack)
	{
		String type = null;
		
		// restore all stack entries besides the last one pushed, it's the
		// element's object reference that is used for the stub continuation
		// methods
		for (int i = 0; i < stack.size() ; i++)
		{
			type = stack.get(i);

			if (type.equals(TypesContext.CAT1_BOOLEAN) ||
				type.equals(TypesContext.CAT1_CHAR) ||
				type.equals(TypesContext.CAT1_BYTE) ||
				type.equals(TypesContext.CAT1_SHORT) ||
				type.equals(TypesContext.CAT1_INT))
			{
				debugMessage("CONT: restore operand : "+i+", int");
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "popInt", "()I");
			}
			else if (type.equals(TypesContext.CAT1_FLOAT))
			{
				debugMessage("CONT: restore operand : "+i+", float");
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "popFloat", "()F");
			}
			else if (type.equals(TypesContext.CAT2_DOUBLE))
			{
				debugMessage("CONT: restore operand : "+i+", double");
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "popDouble", "()D");
			}
			else if (type.equals(TypesContext.CAT2_LONG))
			{
				debugMessage("CONT: restore operand : "+i+", long");
				mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
				mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "popLong", "()J");
			}
			else if (type.equals(TypesContext.CAT1_ADDRESS))
			{
				// this should never happen
				throw new RuntimeException("Invalid local stack type");
			}
			else
			{
				debugMessage("CONT: restore operand : "+i+", "+type);
				if (TypesContext.TYPE_NULL.equals(type))
				{
					mMethodVisitor.visitInsn(ACONST_NULL);
					mMethodVisitor.visitVarInsn(ASTORE, i);
				}
				else
				{
					mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalStack", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
					mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "popReference", "()Ljava/lang/Object;");
					mMethodVisitor.visitTypeInsn(CHECKCAST, type);
				}
			}
		}
	}
	
	/**
	 * Visits a type instruction. A type instruction is an instruction that
	 * takes a type descriptor as parameter.
	 *
	 * @param opcode the opcode of the type instruction to be visited. This opcode
	 *      is either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
	 * @param desc the operand of the instruction to be visited. This operand is
	 *      must be a fully qualified class name in internal form, or the type
	 *      descriptor of an array type (see {@link Type Type}).
	 */
	public void visitTypeInsn(int opcode, String desc)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitTypeInsn           ("+OPCODES[opcode]+", \""+desc+"\")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitTypeInsn(opcode, desc);
		}
	}
	
	/**
	 * Visits a LDC instruction.
	 *
	 * @param cst the constant to be loaded on the stack. This parameter must be
	 *      a non null {@link java.lang.Integer Integer}, a {@link java.lang.Float
	 *      Float}, a {@link java.lang.Long Long}, a {@link java.lang.Double
	 *      Double} or a {@link String String}.
	 */
	public void visitLdcInsn(Object cst)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitLdcInsn            ("+cst+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitLdcInsn(cst);
		}
	}
	
	/**
	 * Visits a MULTIANEWARRAY instruction.
	 *
	 * @param desc an array type descriptor (see {@link Type Type}).
	 * @param dims number of dimensions of the array to allocate.
	 */
	public void visitMultiANewArrayInsn(String desc, int dims)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitMultiANewArrayInsn (\""+desc+"\", "+dims+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitMultiANewArrayInsn(desc, dims);
		}
	}
	
	/**
	 * Visits a zero operand instruction.
	 *
	 * @param opcode the opcode of the instruction to be visited. This opcode is
	 *      either NOP, ACONST_NULL, ICONST_1, ICONST_0, ICONST_1, ICONST_2,
	 *      ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1, FCONST_0, FCONST_1,
	 *      FCONST_2, DCONST_0, DCONST_1,
	 *
	 *      IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
	 *      IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE,
	 *      SASTORE,
	 *
	 *      POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP,
	 *
	 *      IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL,
	 *      DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG,
	 *      FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND, IOR,
	 *      LOR, IXOR, LXOR,
	 *
	 *      I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C,
	 *      I2S,
	 *
	 *      LCMP, FCMPL, FCMPG, DCMPL, DCMPG,
	 *
	 *      IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN,
	 *
	 *      ARRAYLENGTH,
	 *
	 *      ATHROW,
	 *
	 *      MONITORENTER, or MONITOREXIT.
	 */
	public void visitInsn(int opcode)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitInsn               ("+OPCODES[opcode]+")");
		///CLOVER:ON
		
		if (mAdapt &&
			RETURN == opcode)
		{
			debugMessage("CONT: context deactivation");
			
			// get a reference to the context object
			mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
			// remove the context from the manager
			mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "registerContext", "()V");

			// get a reference to the context object
			mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
			// remove the context from the manager
			mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "deactivate", "()V");
			
			mMethodVisitor.visitInsn(opcode);
			
			if (mRethrowLabel != null &&
				mVisitRethrowLabel)
			{
				mMethodVisitor.visitLabel(mRethrowLabel);
				debugMessage("CONT: rethrowing exception");
				mMethodVisitor.visitInsn(ATHROW);
				mVisitRethrowLabel = false;
			}
		}
		else if (mVisit)
		{
			mMethodVisitor.visitInsn(opcode);
		}
	}
	
	/**
	 * Visits an IINC instruction.
	 *
	 * @param var index of the local variable to be incremented.
	 * @param increment amount to increment the local variable by.
	 */
	public void visitIincInsn(int var, int increment)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitIincInsn           ("+var+", "+increment+")");
		///CLOVER:ON
		
		if (mAdapt)
		{
			// execute the original opcode
			mMethodVisitor.visitIincInsn(var, increment);
			
			// retain the maximum index of the local var storage
			if (var > mMaxLocalIndex)
			{
				mMaxLocalIndex = var;
			}
			
			// prepare the arguments of the context storage method
			
			// get a reference to the context object
			mMethodVisitor.visitVarInsn(ALOAD, mContextIndex);
			// get a reference to the local variable stack
			mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationContext", "getLocalVars", "()Lcom/uwyn/rife/continuations/ContinuationStack;");
			
			// push the index of local var that has to be stored on the
			// stack and put the increment amount on it also
			addIntegerConst(var);
			addIntegerConst(increment);
			mMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/uwyn/rife/continuations/ContinuationStack", "incrementInt", "(II)V");
		}
		else if (mVisit)
		{
			mMethodVisitor.visitIincInsn(var, increment);
		}
	}
	
	/**
	 * Visits a field instruction. A field instruction is an instruction that
	 * loads or stores the value of a field of an object.
	 *
	 * @param opcode the opcode of the type instruction to be visited. This opcode
	 *      is either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
	 * @param owner the internal name of the field's owner class (see {@link
	 *      Type#getInternalName getInternalName}).
	 * @param name the field's name.
	 * @param desc the field's descriptor (see {@link Type Type}).
	 */
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitFieldInsn          ("+OPCODES[opcode]+", \""+owner+"\", \""+name+"\", \""+desc+"\")");
		///CLOVER:ON
		
		if (mAdapt &&
			!mDisabledCodeguideBackInTime &&
			opcode == GETSTATIC &&
			mClassNameInternal.equals(owner) &&
			name.startsWith("debugEnabled$") &&
			"Z".equals(desc))
		{
			mDisableCodeguideBackInTime = true;
		}
		
		if (mVisit)
		{
			mMethodVisitor.visitFieldInsn(opcode, owner, name, desc);
		}
	}
	
	/**
	 * Visits an instruction with a single int operand.
	 *
	 * @param opcode the opcode of the instruction to be visited. This opcode is
	 *      either BIPUSH, SIPUSH or NEWARRAY.
	 * @param operand the operand of the instruction to be visited.
	 */
	public void visitIntInsn(int opcode, int operand)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitIntInsn            ("+OPCODES[opcode]+", "+operand+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitIntInsn(opcode, operand);
		}
	}
	
	/**
	 * Visits a try catch block.
	 *
	 * @param start beginning of the exception handler's scope (inclusive).
	 * @param end end of the exception handler's scope (exclusive).
	 * @param handler beginning of the exception handler's code.
	 * @param type internal name of the type of exceptions handled by the handler,
	 *      or <tt>null</tt> to catch any exceptions (for "finally" blocks).
	 * @throws IllegalArgumentException if one of the labels has not already been
	 *      visited by this visitor (by the {@link #visitLabel visitLabel}
	 *      method).
	 */
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitTryCatchBlock      ("+start+", "+end+", "+handler+", \""+type+"\")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitTryCatchBlock(start, end, handler, type);
		}
	}
	
	/**
	 * Visits a LOOKUPSWITCH instruction.
	 *
	 * @param dflt beginning of the default handler block.
	 * @param keys the values of the keys.
	 * @param labels beginnings of the handler blocks. <tt>labels[i]</tt> is the
	 *      beginning of the handler block for the <tt>keys[i]</tt> key.
	 */
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitLookupSwitchInsn   ("+dflt+", "+(null == keys ? null : join(keys, ","))+", "+(null == labels ? null : join(labels, ","))+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitLookupSwitchInsn(dflt, keys, labels);
		}
	}
	
	/**
	 * Visits a jump instruction. A jump instruction is an instruction that may
	 * jump to another instruction.
	 *
	 * @param opcode the opcode of the type instruction to be visited. This opcode
	 *      is either IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE,
	 *      IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE,
	 *      GOTO, JSR, IFNULL or IFNONNULL.
	 * @param label the operand of the instruction to be visited. This operand is
	 *      a label that designates the instruction to which the jump instruction
	 *      may jump.
	 */
	public void visitJumpInsn(int opcode, Label label)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitJumpInsn           ("+OPCODES[opcode]+", "+label+")");
		///CLOVER:ON
		
		if (mAdapt &&
			mDisableCodeguideBackInTime &&
			opcode == IFEQ)
		{
			// pop the condition value off the stack
			mMethodVisitor.visitInsn(POP);
			mMethodVisitor.visitJumpInsn(GOTO, label);
			mDisableCodeguideBackInTime = false;
			mDisabledCodeguideBackInTime = true;
		}
		else if (mVisit)
		{
			mMethodVisitor.visitJumpInsn(opcode, label);
		}
	}
	
	/**
	 * Visits a label. A label designates the instruction that will be visited
	 * just after it.
	 *
	 * @param label a {@link Label Label} object.
	 */
	public void visitLabel(Label label)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitLabel              ("+label+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitLabel(label);
		}
		
		if (mAdapt)
		{
			mLabelContext = mTypes.nextLabelTypes();
		}
	}
	
	/**
	 * Visits a TABLESWITCH instruction.
	 *
	 * @param min the minimum key value.
	 * @param max the maximum key value.
	 * @param dflt beginning of the default handler block.
	 * @param labels beginnings of the handler blocks. <tt>labels[i]</tt> is the
	 *      beginning of the handler block for the <tt>min + i</tt> key.
	 */
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitTableSwitchInsn    ("+min+", "+max+", "+dflt+", "+(null == labels ? null : join(labels, ","))+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitTableSwitchInsn(min, max, dflt, labels);
		}
	}

	/**
	 * Visits the maximum stack size and the maximum number of local variables of
	 * the method.
	 *
	 * @param maxStack maximum stack size of the method.
	 * @param maxLocals maximum number of local variables for the method.
	 */
	public void visitMaxs(int maxStack, int maxLocals)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitMaxs               ("+maxStack+", "+maxLocals+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitMaxs(maxStack, maxLocals);
		}
	}
	
	/**
	 * Visits a local variable declaration.
	 *
	 * @param name the name of a local variable.
	 * @param desc the type descriptor of this local variable.
	 * @param signature the type signature of this local variable. May be
	 *      <tt>null</tt> if the local variable type does not use generic types.
	 * @param start the first instruction corresponding to the scope of this
	 *      local variable (inclusive).
	 * @param end the last instruction corresponding to the scope of this
	 *      local variable (exclusive).
	 * @param index the local variable's index.
	 * @throws IllegalArgumentException if one of the labels has not already been
	 *      visited by this visitor (by the {@link #visitLabel visitLabel}
	 *      method).
	 */
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitLocalVariable      (\""+name+"\", \""+desc+", \""+signature+"\", "+start+", "+end+", "+index+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitLocalVariable( name, desc, signature, start, end, index);
		}
	}
	
	/**
	 * Visits a line number declaration.
	 *
	 * @param line a line number. This number refers to the source file
	 *      from which the class was compiled.
	 * @param start the first instruction corresponding to this line number.
	 * @throws IllegalArgumentException if <tt>start</tt> has not already been
	 *      visited by this visitor (by the {@link #visitLabel visitLabel}
	 *      method).
	 */
	public void visitLineNumber(int line, Label start)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitLineNumber         ("+line+", "+start+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitLineNumber(line, start);
		}
	}
	
  /**
   * Visits a non standard attribute of the code. This method must visit only
   * the first attribute in the given attribute list.
   *
   * @param attr a non standard code attribute. Must not be <tt>null</tt>.
   */
	public void visitAttribute(Attribute attr)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitAttribute          ("+attr+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitAttribute(attr);
		}
	}
	
	public void visitCode()
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitCode               ()");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitCode();
		}
	}
	
	public AnnotationVisitor visitAnnotationDefault()
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitAnnotationDefault  ()");
		///CLOVER:ON
		
		if (mVisit)
		{
			return mMethodVisitor.visitAnnotationDefault();
		}
		
		return mAnnotationVisitor;
	}
	
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitAnnotation         (\""+desc+"\", "+visible+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			return mMethodVisitor.visitAnnotation(desc, visible);
		}
		
		return mAnnotationVisitor;
	}
	
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitAnnotation         ("+parameter+", \""+desc+"\", "+visible+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			return mMethodVisitor.visitParameterAnnotation(parameter, desc, visible);
		}
		
		return mAnnotationVisitor;
	}
	
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitFrame              ("+type+", "+nLocal+", "+local+", "+nStack+", "+stack+")");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitFrame(type, nLocal, local, nStack, stack);
		}
	}
	
	public void visitEnd()
	{
		///CLOVER:OFF
		if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
			ContinuationDebug.LOGGER.finest(" Code:visitEnd                ()");
		///CLOVER:ON
		
		if (mVisit)
		{
			mMethodVisitor.visitEnd();
		}
	}

}
