package com.beacodeart.lox;

// custom lox exeception so user doesn't see java stack trace
class RuntimeError extends RuntimeException{
	final Token token;

	RuntimeError(Token token, String message) {
		super(message);
		this.token = token;
	}
}
