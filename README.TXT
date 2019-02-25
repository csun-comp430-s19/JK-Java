Jason Dang, Kodi Winterer

CHECK Parser_Dirty IF MASTER DOES NOT HAVE PARSE CLASSES/FUNCTIONS RIGHT NOW


VAR is variable
INT is an int
STRING is a string
CLASSNAME is the name of a class
METHODNAME is the name of a method
type::= INT | VOID | 				Built in types
            classname |				Class type including Object and String
            classname<type> 			Generic type represented by <>
op::= + | - | * | / 				Arithmetic operation
exp::= VAR | STRING | INT | <type> |		Variables, strings, integers, generics are expressions
	this |				My instance				
	println(exp) |			Print statement
	exp op exp |			Arithmetic operation
	exp.METHODNAME(exp*) |		Calls method	
	new.CLASSNAME(exp*) 		Creates new instance of class
new.CLASSNAME<type>(exp*)			Creates new instance of a generic class
vardec::=type VAR |				Declares variable
statement::=vardec; |				Declares variable
	         var=exp; |			Assignment			
	         return exp; |			return an expression
	         return; 			return Void
accessModifier::= public | private			Access modifiers
methoddef::=accessModifier type METHODNAME(vardec*) stmt		Creates method
instancedec::= accessModifier vardec;				Declares instance of variable
classdef::= class CLASSNAME{ |					Normal class declaration
                   class CLASSNAME<type>{ |				Generic class declaration
                   class CLASSNAME extends CLASSNAME{	Class with inheritance declaration
		instancedec*				
		constructor(vardec*) stmt
		methoddef*
	      }
program::= classdef* exp 			exp is entry point
