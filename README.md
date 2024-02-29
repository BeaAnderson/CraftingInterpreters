# CraftingInterpreters
Implementation of book Crafting interpreters written by Robert Nystrom

Followed for the purpose of my professional development

To run this project you should compile it using javac *.java from the lox package

From the root directory then run java -classpath . com.beacodeart.lox.Lox

if you pass in a file location you can run lox code from a file, otherwise you can execute lines individually.

Right now the interpreter can evaluate aritmetic expressions and concatinate strings. In order to display the output of these evaluations, run the program and enter print expression;

For example:
print 5+2; // output 7
print "hello" + " world"; // output hello world
print 9+3*6; // output 27 following order of operations where division and multiplication have higher precedence than addition

See https://craftinginterpreters.com/the-lox-language.html for documentation on the language and https://github.com/munificent/craftinginterpreters for the source material.
