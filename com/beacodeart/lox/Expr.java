package com.beacodeart.lox;

// expression class
// because of nesting expressions can forn a tree
abstract class Expr {
	
	//visitor pattern
	interface Visitor<R> {
	R visitAssignExpr(Assign expr);
	R visitBinaryExpr(Binary expr);
	R visitGroupingExpr(Grouping expr);
	R visitLiteralExpr(Literal expr);
	R visitUnaryExpr(Unary expr);
	R visitVariableExpr(Variable expr);
	}
	
	// expr1 + expr2
	static class Binary extends Expr {
	Binary(Expr left, Token operator, Expr right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	@Override
	<R> R accept(Visitor<R> visitor) {
	return visitor.visitBinaryExpr(this);
	}

	final Expr left;
	final Token operator;
	final Expr right;
 }
 
	//(expr)
	static class Grouping extends Expr {
	Grouping(Expr expression) {
	this.expression = expression;
	}

	@Override
	<R> R accept(Visitor<R> visitor) {
	return visitor.visitGroupingExpr(this);
	}

	final Expr expression;
 }

	// value
	static class Literal extends Expr {
	Literal(Object value) {
	this.value = value;
	}

	@Override
	<R> R accept(Visitor<R> visitor) {
	return visitor.visitLiteralExpr(this);
	}

	final Object value;
 }
 
	// - expr or !expr
	// - expr can only be literal number
	static class Unary extends Expr {
	Unary(Token operator, Expr right) {
	this.operator = operator;
	this.right = right;
	}

	@Override
	<R> R accept(Visitor<R> visitor) {
	return visitor.visitUnaryExpr(this);
	}

	final Token operator;
	final Expr right;
 }

 static class Variable extends Expr {
    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    final Token name;
  }

  static class Assign extends Expr {
    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    final Token name;
    final Expr value;
  }


	abstract <R> R accept(Visitor<R> visitor);
}
