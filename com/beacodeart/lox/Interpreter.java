package com.beacodeart.lox;


//our interpreter uses the visitor pattern
class Interpreter implements Expr.Visitor<Object> {
	
	//calls the entry point into the interpreter
	void interpret (Expr expression){
		try {
			Object value = evaluate(expression);
			System.out.println(stringify(value));
		} catch (RuntimeError error){
			Lox.runtimeError(error);
		}
	}

	//from the expression the accept method calls the appropriate visit method of this Interpreter class
	private Object evaluate (Expr expr){
		return expr.accept(this);
	}
	
	//handles all cases of binary expression
	@Override
	public Object visitBinaryExpr(Expr.Binary expr){
		//because a binary expression may consist of expression of expression type for its
		//left and right operators these must be evaluated first
		//prescendence is handles by parser so these are executed left to right
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		//because a binary expression may be arithmetic or comparison based all these need to be handled
		//also because we can concatonate strings these need handled too
		switch (expr.operator.type) {
			case BANG_EQUAL: return !isEqual(left, right);
			case EQUAL_EQUAL: return isEqual(left, right);
			case GREATER:
				checkNumberOperands(expr.operator, left, right);
				return (double)left > (double)right;
			case GREATER_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left >= (double)right;
			case LESS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left < (double)right;
			case LESS_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left <= (double)right;
			case MINUS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left - (double)right;
			case PLUS:
				if (left instanceof Double && right instanceof Double){
					return (double)left + (double)right;
				}
 
				if (left instanceof String && right instanceof String){
					return (String)left + (String)right;
				}

				throw new RuntimeError(expr.operator, 
						"Operands must be two numbers or two strings.");
			case SLASH:
				checkNumberOperands(expr.operator, left, right);
				return (double)left / (double)right;
			case STAR:
				checkNumberOperands(expr.operator, left, right);
				return (double)left * (double)right;

		}

		return null;
	}
	
	//grouping just references evaluate
	@Override
	public Object visitGroupingExpr(Expr.Grouping expr){
		return evaluate(expr.expression);
	}

	//from a literal it's value can be returned
	@Override
	public Object visitLiteralExpr(Expr.Literal expr){
		return expr.value;
	}

	//two cases not and negate
	//handled with a case statement
	//cannot negate something that is not a number
	@Override
	public Object visitUnaryExpr(Expr.Unary expr){
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
			case BANG:
				return !isTruthy(right);
			case MINUS:
				checkNumberOperand(expr.operator, right);
				return -(double)right;
		}

		return null;
	}

	//helper methods
	//
	//if not a number errors out
	private void checkNumberOperand(Token operator, Object operand){
		if (operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a number.");
	}

	//if one of two not a number errors out
	private void checkNumberOperands(Token operator, Object left, Object right){
		if (left instanceof Double && right instanceof Double) return;

		throw new RuntimeError(operator, "Operands must be numbers.");
	} 
	
	//evaluates truth
	private boolean isTruthy( Object object){
		if (object == null) return false;
		if (object instanceof Boolean) return (boolean) object;
		return true;
	}

	//evaluates equality
	private boolean isEqual(Object a, Object b){
		if (a == null && b == null) return true;
		if (a==null) return false;

		return a.equals(b);

	}

	private String stringify(Object object){
		if (object == null) return "nil";

		if (object instanceof Double){
			String text = object.toString();
			if (text.endsWith(".0")){
				text = text.substring(0, text.length()-2);
			}
			return text;
		}

		return object.toString();
	}
	
}
