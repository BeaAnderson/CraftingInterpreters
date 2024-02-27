package com.beacodeart.lox;

// contains all the information to create and read tokens
class Token {
	
	//the what
	final TokenType type;
	//the identifier for variables for example
	final String lexeme;
	//the value if the tokeb
	final Object literal;
	// for error reporting	
	final int line;
	
	//ctor
	public Token(TokenType type, String lexeme, Object literal, int line) {	
		 this.type = type;
		 this.lexeme = lexeme;
		 this.literal = literal;
		 this.line = line;
	}
	
	//to string
	public String toString(){
		return type + " " + lexeme + " " + literal;
	}

}
