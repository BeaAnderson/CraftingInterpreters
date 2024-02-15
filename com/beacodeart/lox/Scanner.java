package com.beacodeart.lox;

import java.util.*;

import static com.beacodeart.lox.TokenType.*;

/*
 * scanner class
 * 
 * takes in a string and produces a list of tokens
 * 
 */
class Scanner {
	//input
	private final String source;
	//output
	private final List<Token> tokens = new ArrayList<>();
	//pointers
	private int start = 0;
	private int current = 0;
	//tracking
	private int line = 1;
	
	//use a map so we can easily identify reserved keywords
	private static final Map<String, TokenType> keywords;

	static {
		keywords = new HashMap<>();
		keywords.put("and", AND);
		keywords.put("class", CLASS);
		keywords.put("else", ELSE);
		keywords.put("false", FALSE);
		keywords.put("for", FOR);
		keywords.put("fun", FUN);
		keywords.put("if", IF);
		keywords.put("nil", NIL);
		keywords.put("or", OR);
		keywords.put("print", PRINT);
		keywords.put("return", RETURN);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);
		keywords.put("true", TRUE);
		keywords.put("var", VAR);
		keywords.put("while", WHILE);
	}

	public Scanner (String source){
		this.source = source;
	}

	/*
	 * while or current position in the string is not greater than the length of the string
	 * call the scan token method, when we run out of string, we append an eof token
	 * this just makes other things easier for us.
	 */
	List<Token> scanTokens(){
		while (!isAtEnd()) {
			//set start pointer
			start = current;
			//scan the next token - may be variable length
			scanToken();
		}
		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}

	// big switch statement that checks what our current character is then acts bassed on that
	//advance returns current as part of it's action
	private void scanToken(){
		char c = advance();
		switch (c) {
			
			//simple cases
			case '(':addToken(LEFT_PAREN); break;
			case ')':addToken(RIGHT_PAREN); break;
			case '{':addToken(LEFT_BRACE); break;
			case '}':addToken(RIGHT_BRACE); break;
		 	case ',':addToken(COMMA); break;
			case '.':addToken(DOT); break;
			case '-':addToken(MINUS); break;
			case '+':addToken(PLUS); break;
			case ';':addToken(SEMICOLON); break;
			case '*':addToken(STAR); break;
			
			// these tokens may be one or two characters long, so need a ternary operator
			case '!':
				 addToken(match('=') ? BANG_EQUAL : BANG);
				 break;
			case '=':
				 addToken(match('=') ? EQUAL_EQUAL : EQUAL);
				 break;
			case '<':
				 addToken(match('=') ? LESS_EQUAL : LESS);
				 break;
			case '>':
				 addToken(match('=') ? GREATER_EQUAL : GREATER);
				 break;
			case '/':
				 if (match('/')){
					while (peek() != '\n' && !isAtEnd()) advance();
				 } else {
						addToken(SLASH);
					}
				 
				 break;
			
			// white space
			case ' ':
			case '\r':
			case '\t':
				break;
			// white space + new line
			case '\n':
				line++;
				break;
			
			//string	
			case '"': string(); break;


			//default handles a few different situations
			default:
				//if character is a digit our token has to be a nummber 
				if (isDigit(c)){
					number();
				 } else if (isAlpha(c)) { //if we have a letter this may be a variable identifyer or a reserved keyword. identifier handles both
					identifier();
				 } else {
					Lox.error(line, "unexpected character");
				 }
				 break;

		}
	}

	// while the next character is a letter or a number we are still in our identifier
	private void identifier(){
		while (isAlphaNumeric(peek())) advance();
		
		//pull the text of our identifier into a variable
		String text = source.substring(start, current);
		// if our text matches a reserved keyword we can add that keyword as a token
		TokenType type = keywords.get(text);
		if (type == null) type = IDENTIFIER;
		// if text doesn't match reserved keyword then we add a variable identifier 
		addToken(type);
	}

	//deals with case of number
	private void number() {
		// will deal with all digits up to any dp
		while(isDigit(peek())) advance();

		// if we have a period we may be dealing with a fp number, so we should scan this
		if (peek() == '.' && isDigit(peekNext())){
			advance();
			
			while(isDigit(peek())) advance();
		}

		//all numbers are stored as double
		addToken(NUMBER, Double.parseDouble(source.substring(start, current)));


	}

	// deals with strings, should advance until we get to  corresponding closing " or throw an error if we do not find one
	private void string(){
		while (peek() != '"' && !isAtEnd()){
			if (peek() == '\n') line++;
			advance();
		}

		if (isAtEnd()) {
			Lox.error(line, "unterminated string");
			return;
		}

		advance();

		// trim quotation marks
		String value = source.substring(start +1, current -1);
		addToken(STRING, value);
	}

	//evaluates whether our current character match the char passed in.
	private boolean match(char expected){
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;

		current++;
		return true;
	}

	//returns current, which is one adhead of the return from advance
	private char peek(){
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}

	// returns our next character without affecting our pointers
	private char peekNext(){
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}

	// returns whether ouir character is a letter
	private boolean isAlpha(char c){
		return (c >= 'a' && c <= 'z') ||
		       (c >= 'A' && c <= 'Z') ||
		        c == '_';
	}

	// returns true if our character is a letter or number
	private boolean isAlphaNumeric(char c){
		return isAlpha(c) || isDigit(c);
	}

	// returns true if our character is a number
	private boolean isDigit(char c){
		return c >= '0' && c <= '9';
	}

	// returns true if our current pointer is at the end of our string
	private boolean isAtEnd(){
		return current >= source.length();
	}

	//advances our current character then returns what the current character was before that advance
	private char advance() {
		current ++;
		return source.charAt(current -1);
	}

	// adds a token with null value
	private void addToken(TokenType type) {
		addToken(type, null);
	}

	// adds a token with value
	private void addToken(TokenType type, Object literal){
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}

}
