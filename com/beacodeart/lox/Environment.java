package com.beacodeart.lox;

import java.util.HashMap;
import java.util.Map;

//environment is basically a map holding context
//each environment holds a reference to an enclosing environment.
//local variables go out of scope, global variables are available to local scpe although
//local variables with names that shadow global scope can be introduced
public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    final Environment enclosing;
   
    //default constructor
    Environment(){
        enclosing = null;
    }

    //constructor for local environment
    Environment(Environment enclosing){
        this.enclosing = enclosing;
    }
   
    //get the value of a token given the name
    Object get(Token name){
        if (values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);
    
        throw new RuntimeError(name, "undefined variable " + name.lexeme + ".");
    }

    //method to assign a value to the name of a variable
    void assign(Token name, Object value){
        if (values.containsKey(name.lexeme)){
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null){
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    //redefine variable
    void define(String name, Object value){
        values.put(name, value);
    }
}
