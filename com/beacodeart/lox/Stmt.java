package com.beacodeart.lox;

abstract class Stmt {
	
	public class Expression extends Stmt{
		Expression(Expr expression){
			this.expression = expression;
		}

		@Override
		<R> R accept(Visitor<R> visitor){
			return visitor.VisitExpressionStmt(this);
		}

		final Expr expression;
	}

	public class Print extends Stmt{
		Print(Expr expression){
			this.expression = expression;
		}

		@Override
		<R> R accept(Visitor<R> visitor){
			return visitor.VisitPrintStmt(this);
		}

		final Expr expression;	
	}

	abstract <R> R accept(Visitor<R> visitor);
}
