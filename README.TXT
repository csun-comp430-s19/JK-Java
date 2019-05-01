Jason Dang, Kodi Winterer

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
	this.VAR |				My instance				
	println(VAR) |			Print statement
	exp op exp |			Arithmetic operation
	VAR.METHODNAME(VAR) |		Calls method	
	new.CLASSNAME(VAR)    |		Creates new instance of class
	new.CLASSNAME<type>(VAR)		Creates new instance of a generic class
vardec::=type VAR; 				Declares variable
stmt::=vardec; 	|			Declares variable
	VAR=exp; |			Assignment			
	return exp; |			return an expression
	return; 				return Void
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
program::= classdef* 



Basic example of how we will represent classes in C
Does not include constructors

#include <stdio.h>
#include <stdlib.h>

struct Foo{
    char* name;
    void* (*vtable[4])();
};

struct Bar{
	struct Foo parent; //parent class
	int age;
	void* (*vtable[4])();
};

char** foo_getChar(struct Foo* foo){
	//pass by reference (normally would not be done for a string but 
	//its a proof of concept for objects that will need to be passed by reference
    return &foo->name;
}

void foo_setChar(struct Foo* foo, char* str){
    foo->name = str;
}

int bar_getAge(struct Foo* foo){
	//this one is pass by value
	struct Bar* bar = (struct Foo*) foo;
	return bar->age;
}

void bar_setAge(struct Foo* foo, int num){
	struct Bar* bar = (struct Foo*) foo;
	bar->age = num;
}


int main(){
    struct Foo f;
	struct Bar b;
	
	f.name = "Hello World!";
	f.vtable[0] = foo_getChar;
	f.vtable[1] = foo_setChar;
	
	b.parent.name = "Foobar";
	b.vtable[0] = foo_getChar;
	b.vtable[1] = foo_setChar;
	b.vtable[2] = bar_getAge;
	b.vtable[3] = bar_setAge;
	b.age = 0;

    char** s = f.vtable[0](&f);
    printf("f.name:%s\n", f.name);
    printf("f.name through function pointer:%s\n", *s);
    
	char** bs = b.vtable[0](&b);
	printf("b.name:%s\n", *bs);
	printf("b.age:%d\n", b.vtable[2](&b));
	
	b.vtable[3](&b, 21);
	b.vtable[1](&b, "rabooF");
	
	printf("b.name:%s\n", *bs);
	printf("b.age:%d\n", b.vtable[2](&b));
	
    f.vtable[1](&f,"Bye world");
    printf("f.name after setChar:%s\n", f.name);
    printf("f.name through function pointer:%s\n", *s);
    return 0;
}
