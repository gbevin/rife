/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.uwyn.rife.antlr4.runtime.misc;

import com.uwyn.rife.antlr4.runtime.ANTLRInputStream;
import com.uwyn.rife.antlr4.runtime.CharStream;
import com.uwyn.rife.antlr4.runtime.CommonTokenStream;
import com.uwyn.rife.antlr4.runtime.DiagnosticErrorListener;
import com.uwyn.rife.antlr4.runtime.Lexer;
import com.uwyn.rife.antlr4.runtime.Parser;
import com.uwyn.rife.antlr4.runtime.ParserRuleContext;
import com.uwyn.rife.antlr4.runtime.TokenStream;
import com.uwyn.rife.antlr4.runtime.atn.PredictionMode;

import javax.print.PrintException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** Run a lexer/parser combo, optionally printing tree string or generating
 *  postscript file. Optionally taking input file.
 *
 *  $ java com.uwyn.rife.antlr4.runtime.misc.TestRig GrammarName startRuleName
 *        [-tree]
 *        [-tokens] [-gui] [-ps file.ps]
 *        [-trace]
 *        [-diagnostics]
 *        [-SLL]
 *        [input-filename(s)]
 */
public class TestRig {
	public static final String LEXER_START_RULE_NAME = "tokens";

	protected String grammarName;
	protected String startRuleName;
	protected final List<String> inputFiles = new ArrayList<String>();
	protected boolean printTree = false;
	protected boolean showTokens = false;
	protected boolean trace = false;
	protected boolean diagnostics = false;
	protected String encoding = null;
	protected boolean SLL = false;

	public TestRig(String[] args) throws Exception {
		if ( args.length < 2 ) {
			System.err.println("java com.uwyn.rife.antlr4.runtime.misc.TestRig GrammarName startRuleName\n" +
							   "  [-tokens] [-tree] [-encoding encodingname]\n" +
							   "  [-trace] [-diagnostics] [-SLL]\n"+
							   "  [input-filename(s)]");
			System.err.println("Use startRuleName='tokens' if GrammarName is a lexer grammar.");
			System.err.println("Omitting input-filename makes rig read from stdin.");
			return;
		}
		int i=0;
		grammarName = args[i];
		i++;
		startRuleName = args[i];
		i++;
		while ( i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.charAt(0)!='-' ) { // input file name
				inputFiles.add(arg);
				continue;
			}
			if ( arg.equals("-tree") ) {
				printTree = true;
			}
			if ( arg.equals("-tokens") ) {
				showTokens = true;
			}
			else if ( arg.equals("-trace") ) {
				trace = true;
			}
			else if ( arg.equals("-SLL") ) {
				SLL = true;
			}
			else if ( arg.equals("-diagnostics") ) {
				diagnostics = true;
			}
			else if ( arg.equals("-encoding") ) {
				if ( i>=args.length ) {
					System.err.println("missing encoding on -encoding");
					return;
				}
				encoding = args[i];
				i++;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		TestRig testRig = new TestRig(args);
		testRig.process();
	}

	public void process() throws Exception {
//		System.out.println("exec "+grammarName+"."+startRuleName);
		String lexerName = grammarName+"Lexer";
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Class<? extends Lexer> lexerClass = null;
		try {
			lexerClass = cl.loadClass(lexerName).asSubclass(Lexer.class);
		}
		catch (java.lang.ClassNotFoundException cnfe) {
			// might be pure lexer grammar; no Lexer suffix then
			lexerName = grammarName;
			try {
				lexerClass = cl.loadClass(lexerName).asSubclass(Lexer.class);
			}
			catch (ClassNotFoundException cnfe2) {
				System.err.println("Can't load "+lexerName+" as lexer or parser");
				return;
			}
		}

		Constructor<? extends Lexer> lexerCtor = lexerClass.getConstructor(CharStream.class);
		Lexer lexer = lexerCtor.newInstance((CharStream)null);

		Class<? extends Parser> parserClass = null;
		Parser parser = null;
		if ( !startRuleName.equals(LEXER_START_RULE_NAME) ) {
			String parserName = grammarName+"Parser";
			parserClass = cl.loadClass(parserName).asSubclass(Parser.class);
			if ( parserClass==null ) {
				System.err.println("Can't load "+parserName);
			}
			Constructor<? extends Parser> parserCtor = parserClass.getConstructor(TokenStream.class);
			parser = parserCtor.newInstance((TokenStream)null);
		}

		if ( inputFiles.size()==0 ) {
			InputStream is = System.in;
			Reader r;
			if ( encoding!=null ) {
				r = new InputStreamReader(is, encoding);
			}
			else {
				r = new InputStreamReader(is);
			}

			process(lexer, parserClass, parser, is, r);
			return;
		}
		for (String inputFile : inputFiles) {
			InputStream is = System.in;
			if ( inputFile!=null ) {
				is = new FileInputStream(inputFile);
			}
			Reader r;
			if ( encoding!=null ) {
				r = new InputStreamReader(is, encoding);
			}
			else {
				r = new InputStreamReader(is);
			}

			if ( inputFiles.size()>1 ) {
				System.err.println(inputFile);
			}
			process(lexer, parserClass, parser, is, r);
		}
	}

	protected void process(Lexer lexer, Class<? extends Parser> parserClass, Parser parser, InputStream is, Reader r) throws IOException, IllegalAccessException, InvocationTargetException, PrintException {
		try {
			ANTLRInputStream input = new ANTLRInputStream(r);
			lexer.setInputStream(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			tokens.fill();

			if ( showTokens ) {
				for (Object tok : tokens.getTokens()) {
					System.out.println(tok);
				}
			}

			if ( startRuleName.equals(LEXER_START_RULE_NAME) ) return;

			if ( diagnostics ) {
				parser.addErrorListener(new DiagnosticErrorListener());
				parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
			}

			if ( printTree ) {
				parser.setBuildParseTree(true);
			}

			if ( SLL ) { // overrides diagnostics
				parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
			}

			parser.setTokenStream(tokens);
			parser.setTrace(trace);

			try {
				Method startRule = parserClass.getMethod(startRuleName);
				ParserRuleContext tree = (ParserRuleContext)startRule.invoke(parser, (Object[])null);

				if ( printTree ) {
					System.out.println(tree.toStringTree(parser));
				}
			}
			catch (NoSuchMethodException nsme) {
				System.err.println("No method for rule "+startRuleName+" or it has arguments");
			}
		}
		finally {
			if ( r!=null ) r.close();
			if ( is!=null ) is.close();
		}
	}
}
