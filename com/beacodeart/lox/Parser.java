package com.beacodeart.lox;

import java.util.ArrayList;
import java.util.List;

import static com.beacodeart.lox.TokenType.*;

/**
 * Parser
 * 
 * recursive decent parser
 * which means that the series of tokens is evaluated in order of presedence, with
 * each type of expression calling the next higher presedence operation first
 * then evaluating it's own state
 */
class Parser {
	//error handling
	private static class ParseError extends RuntimeException {}
	//input
	private final List<Token> tokens;
	//current list position
	private int current = 0;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	//entry point into our parser
	//with the new statement class we create a list of statements
	List<Stmt> parse(){
		List<Stmt> statements = new ArrayList<>();
		while (!isAtEnd()){
			statements.add((declaration()));
		}

		return statements;
	}

	//for now expressions just returns equality
	private Expr expression(){
		return assignment();
	}

	//declaration recognise var then return variable or return statement
	private Stmt declaration(){
		try {
			if (match(VAR)) return varDeclaration();

			return statement();
		} catch (ParseError error){
			synchronize();
			return null;
		}
	}

	//for now only 2 types of statement, print statements and expression statements
	private Stmt statement(){
		if (match(PRINT)) return printStatement();
		if (match(LEFT_BRACE)) return new Stmt.Block(block());

		return expressionStatement();
	}

	//both our statements take in an expression and the difference is how they are interpreted
	//statements are separated by semicolons
	private Stmt printStatement(){
		Expr value = expression();
		consume(SEMICOLON, "Expect ';' after value.");
		return new Stmt.Print(value);
	}

	//assign or initialise variable
	private Stmt varDeclaration(){
		//after var we need a name for that variable
		Token name = consume(IDENTIFIER, "Expect variable name");

		//then we need an assignment operator and we assign expression to that variable
		Expr initializer = null;
		if (match(EQUAL)){
			initializer = expression();
		}

		//otherwise we can declare but not initialise that variable
		consume(SEMICOLON, "Expect ';' after variable declaration");
		return new Stmt.Var(name, initializer);
	}

	private Stmt expressionStatement(){
		Expr value = expression();
		consume(SEMICOLON, "Expect ';' after value.");
		return new Stmt.Expression(value);
	}

	private List<Stmt> block(){
		List<Stmt> statements = new ArrayList<>();

		while (!check(RIGHT_BRACE) && !isAtEnd()){
			statements.add(declaration());
		}

		consume(RIGHT_BRACE, "Expect '}' after block.");
		return statements;
	}

	private Expr assignment(){
		Expr expr = equality();

		if (match(EQUAL)){
			Token equals = previous();
			Expr value = assignment();

			if (expr instanceof Expr.Variable){
				Token name = ((Expr.Variable)expr).name;
				return new Expr.Assign(name, value);
			}

			error(equals, "Invalid assigment target");
		}

		return expr;
	}
	
	//evaluates equality operations
	private Expr equality(){
		Expr expr = comparison();

		while (match(BANG_EQUAL, EQUAL_EQUAL)){
			Token operator = previous();
			Expr right = comparison ();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	// evaluates comparison operations
	private Expr comparison(){
		Expr expr = term();

		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
			Token operator = previous();
			Expr right = term();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	//evaluates addition and subtraction 
	private Expr term(){
		Expr expr = factor();

		while (match(MINUS, PLUS)){
			Token operator = previous();
			Expr right = factor();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	//evaluates division and multiplication
	private Expr factor(){
		Expr expr = unary();

		while (match(SLASH, STAR)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	//evaluates not eqaul or minus number
	private Expr unary(){
		if (match(BANG, MINUS)){
			Token operator = previous();
			Expr right = unary();
			return new Expr.Unary(operator, right);
		}

		return primary();
	}
	
	//evaluates key words and literal expression
	private Expr primary(){
		if (match(FALSE)) return new Expr.Literal(false);
		if (match(TRUE)) return new Expr.Literal(true);
		if (match(NIL)) return new Expr.Literal(null);

		if (match(NUMBER, STRING)){
			return new Expr.Literal(previous().literal);
		}

		if (match(IDENTIFIER)){
			return new Expr.Variable(previous());
		}

		if (match(LEFT_PAREN)){
			Expr expr = expression();
			consume(RIGHT_PAREN, "expect ')' after expression");
			return new Expr.Grouping(expr);
		}

		throw error(peek(), "Expect expression");
	}

	//Helper functions
	//
	//boolean current tokeb matches given token
	private boolean match(TokenType... types){
		for (TokenType type: types){
			if (check(type)) {
				advance();
				return true;
			}
		}

		return false;
	}

	// if current tolen matches current token advances
	private Token consume(TokenType type, String  message){
		if (check(type)) return advance();

		throw error(peek(), message);
	}
	
	//if current token equals passed in tooken true
	private boolean check(TokenType type){
		if (isAtEnd()) return false;
		return peek().type == type;
	}
	
	//advances tokeen then returns the previous token, which was the current token when the function was called
	private Token advance(){
		if (!isAtEnd()) current++;
		return previous();
	}
	
	//tells us if we are at the end of the file
	private boolean isAtEnd(){
		return peek().type == EOF;
	}

	//looks at teh current token
	private Token peek(){
		return tokens.get(current);
	}
	
	//gets the previous token
	private Token previous() {
		return tokens.get(current -1);
	}
	
	//throws error
	private ParseError error(Token token, String message){
		Lox.error(token, message);
		return new ParseError();
	}
	
	//not used yet
	private void synchronize(){
		advance();

		while(!isAtEnd()){
			if (previous().type == SEMICOLON) return;

			switch (peek().type){
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
			}

			advance();

			}
		}
	}


