package com.beacodeart.lox;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	private static final Interpreter interpreter = new Interpreter();
	static boolean hadError = false;
	static boolean hadRuntimeError = false;

	/*  
	* main takes in one or zero arguments if main is run with 0 arguments 
	* the user can write code directly into the terminal that our interpreter will execute
	* otherwise the the interpreter will run code from a provided source location
	* if more than one arg is given we exit the program with code 64
	*/
	public static void main(String[] args) throws IOException {
		if (args.length > 1){
			System.out.println("Useage: jlox [script]");
			System.exit(64);
		} else if (args.length ==1){
			runFile(args[0]);
		} else{
			runPrompt();
		}

	}

	/*
	 * if given a file location will read the full file and convert it into a string 
	 * that will then be passed to our default run method
	 * 
	 */
	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));

		if (hadError) System.exit(65);
		if (hadRuntimeError) System.exit(70);
	}

	/*
	 * if main isn't given a source location, will read the users input then
	 * call our default run method for that string
	 */
	private static void runPrompt() throws IOException {	 
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		for (;;) {
			System.out.print("> ");
			String line = reader.readLine();
			
			if (line == null){
				break;
			}
			
			run(line);
			hadError = false;
		}
	}

	/*
	 * takes in a string proveded by either runPrompt or runFile
	 * which gets scanned and turned into tokens, tokens get turned into an
	 * expression which gets interpreted to produce output
	 */
	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		System.out.println(tokens);
				
		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();

		if (hadError) return;

		//System.out.println(new AstPrinter().print(statements));

		interpreter.interpret(statements);
	}

	//various error classes
	
	static void error(int line, String message){
		report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		System.err.println(
				"[Line " + line + "] Error " + where + ": " + message);
		hadError = true;
	}

	static void error(Token token, String message){
		if (token.type == TokenType.EOF) {
			report(token.line, "at end", message);
		} else {
			report(token.line, " at '" + token.lexeme + "'", message);
		}
	}

	static void runtimeError(RuntimeError error){
		System.err.println(error.getMessage() +
				"\n[line " + error.token.line + "]");
		hadRuntimeError = true;
	}
}
