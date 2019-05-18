package JK_Lexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; 
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.ComparisonFailure;
import org.junit.Test;

public class CCodeGeneratorTest {
	//Testing method for basic exp generation: Create temp file and write an exp e to it, read file to a string, and compare it with expected string
	public void assertBasicExpGeneration(String expected, Exp e) throws IOException{
		try {
			CCodeGenerator cg = new CCodeGenerator(); 
			final File file = File.createTempFile("testfile",".c");
			cg.writeExpressiontoFile(e, file);
			final String received = convertFileToString(file); 
			assertTrue("Expected code generation error: "+received, expected!=null); 
			assertEquals(expected,received); 
			file.delete(); 
		}catch(final CCodeGeneratorException exc) {
			assertTrue("Unexpected code generation error: "+exc.getMessage(), expected==null);
		}
	}
	public void assertStatementGeneration(String expected, Statement s) throws IOException{
		try {
			CCodeGenerator cg = new CCodeGenerator(); 
			final File file = File.createTempFile("testfile",".c");
			cg.writeStatementToFile(s, file);
			final String received = convertFileToString(file); 
			assertTrue("Expected code generation error: "+received, expected!=null); 
			assertEquals(expected,received); 
			file.delete(); 
		}catch(final CCodeGeneratorException exc) {
			assertTrue("Unexpected code generation error: "+exc.getMessage(), expected==null);
		}
	}
	
	public void assertProgramGeneration(String expected, Program p) throws IOException{
		try {
			CCodeGenerator cg = new CCodeGenerator(p); 
			final File file = File.createTempFile("testfile",".c");
			cg.generate(file);
			final String received = convertFileToString(file); 
			assertTrue("Expected code generation error: "+received, expected!=null); 
			assertEquals(expected,received); 
			file.delete(); 
		}catch(final CCodeGeneratorException exc) {
			assertTrue("Unexpected code generation error: "+exc.getMessage(), expected==null);
		}
	}
	//Method for converting text from the c file created to a string, might have to change for later testing with more than one line/exp
	public String convertFileToString(File file) throws IOException{
		StringBuilder sb = new StringBuilder((int)file.length());
		Scanner scanner = new Scanner(file);
		while(scanner.hasNextLine()) {
			sb.append(scanner.nextLine());
		}
		scanner.close();
		return sb.toString(); 
	}
	@Test
	public void testNumberExp() throws IOException{
		assertBasicExpGeneration("1", new NumberExp(1));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestNumberExp() throws IOException{
		assertBasicExpGeneration("2", new NumberExp(1));
	}
	@Test 
	public void testStringExp() throws IOException{
		assertBasicExpGeneration("hello world", new StringExp("hello world"));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestStringExp() throws IOException{
		assertBasicExpGeneration("goodbye world", new StringExp("hello world"));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestVariableExp() throws IOException{
		assertBasicExpGeneration("varname2100", new VariableExp("varname21"));
	}
	@Test 
	public void testBinopPlus() throws IOException{
		assertBasicExpGeneration("1+2", new BinopExp(new NumberExp(1), new PlusOp(), new NumberExp(2)));
	}
	@Test 
	public void testBinopMinus() throws IOException{
		assertBasicExpGeneration("3-2", new BinopExp(new NumberExp(3), new MinusOp(), new NumberExp(2)));
	}
	@Test 
	public void testBinopMult() throws IOException{
		assertBasicExpGeneration("3*2", new BinopExp(new NumberExp(3), new MultOp(), new NumberExp(2)));
	}
	@Test 
	public void testBinopDiv() throws IOException{
		assertBasicExpGeneration("4/2", new BinopExp(new NumberExp(4), new DivOp(), new NumberExp(2)));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestBinopWrongOp() throws IOException{
		assertBasicExpGeneration("4+2", new BinopExp(new NumberExp(4), new DivOp(), new NumberExp(2)));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestBinopWrongNumber() throws IOException{
		assertBasicExpGeneration("4/4", new BinopExp(new NumberExp(4), new DivOp(), new NumberExp(2)));
	}
	@Test
	public void testVariableDeclarationInt() throws IOException{
		assertStatementGeneration("int user_foo1", new VariableDecExp(new IntType(), new VariableExp("foo1")));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestVariableDeclarationInt() throws IOException{
		assertStatementGeneration("int foo2;", new VariableDecExp(new IntType(), new VariableExp("foo1")));
	}
	@Test
	public void testVariableDeclarationCharArray() throws IOException{
		assertStatementGeneration("char* user_foo1", new VariableDecExp(new StringType(), new VariableExp("foo1")));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestVariableDeclarationCharArray() throws IOException{
		assertStatementGeneration("char foo1;", new VariableDecExp(new StringType(), new VariableExp("foo1")));
	}
	@Test
	public void testReturnInteger() throws IOException{
		assertStatementGeneration("return 0", new ReturnStmt(new NumberExp(0)));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestReturnInteger() throws IOException{
		assertStatementGeneration("return 1;", new ReturnStmt(new NumberExp(0)));
	}
	@Test
	public void testReturnVariable() throws IOException{
		assertStatementGeneration("return user_foo1", new ReturnStmt(new VariableExp("foo1")));
	}
	@Test(expected = ComparisonFailure.class)
	public void failTestReturnVariable() throws IOException{
		assertStatementGeneration("return foo2;", new ReturnStmt(new VariableExp("foo1")));
	}
	@Test
	public void testReturnString() throws IOException{
		assertStatementGeneration("return \"hello world\"", new ReturnStmt(new StringExp("hello world")));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestReturnString() throws IOException{
		assertStatementGeneration("return \"goodbye world\";", new ReturnStmt(new StringExp("hello world")));
	}
	@Test 
	public void testAssignmentInt() throws IOException{
		assertStatementGeneration("user_foo1 = 1", new AssignmentStmt(new VariableExp("foo1"), new NumberExp(1), false));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestAssignmentInt() throws IOException{
		assertStatementGeneration("user_foo1 = 2", new AssignmentStmt(new VariableExp("foo1"), new NumberExp(1), false));
	}
	@Test 
	public void testAssignmentBinop() throws IOException{
		assertStatementGeneration("user_foo1 = (1 + 2)", new AssignmentStmt(new VariableExp("foo1"), new BinopExp(new NumberExp(1), new PlusOp(),new NumberExp(2)), false));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestAssignmentBinop() throws IOException{
		assertStatementGeneration("user_foo1 = (1 + 7)", new AssignmentStmt(new VariableExp("foo1"), new BinopExp(new NumberExp(1), new PlusOp(),new NumberExp(2)), false));
	}
	@Test 
	public void testAssignmentString() throws IOException{
		assertStatementGeneration("user_foo1 = \"hello\"", new AssignmentStmt(new VariableExp("foo1"), new StringExp("hello"), false));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestAssignmentString() throws IOException{
		assertStatementGeneration("user_foo1 = \"goodbye\"", new AssignmentStmt(new VariableExp("foo1"), new StringExp("hello"), false));
	}
	@Test 
	public void testAssignmentVarToVar() throws IOException{
		assertStatementGeneration("user_foo1 = user_foo2", new AssignmentStmt(new VariableExp("foo1"), new VariableExp("foo2"), false));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestAssignmentVarToVar() throws IOException{
		assertStatementGeneration("foo1 = foo3;", new AssignmentStmt(new VariableExp("foo1"), new VariableExp("foo2"), false));
	}
	
	@Test
	public void testBasicProgramSingleStatement() throws IOException{
		VariableDecExp vdec = new VariableDecExp(new IntType(), new VariableExp("foo"));
		ArrayList<Statement> s = new ArrayList<Statement>();
		s.add(vdec);
		
		Program p = new Program(s, new ArrayList<ClassDefExp>());
		
		assertProgramGeneration("#include <stdio.h>#include <stdlib.h>int main(){int user_foo;return 0;}", p);
	}
	
	@Test
	public void testBasicProgramBasicClass() throws IOException, ParserException, TokenizerException, TypeErrorException{
		
		//Standard class declaration with variety of statements outside
    	final String input = "public class Student{"
    						+"private int age;"
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);"
    				  + "student.getAge();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog);
    	
		assertProgramGeneration("#include <stdio.h>" + 
				"#include <stdlib.h>" + 
				"struct Student { int* user_age; void* (*vtable[2]) ();};" + 
				"int Student_getAge(struct Student* structptr){return structptr->user_age; }" + 
				"void Student_setAge(struct Student* structptr, int user_n){structptr->user_age = user_n; }" + 
				"struct Student* Student_constructor0(struct Student* structptr, int user_a){structptr->user_age = malloc(sizeof(int)); structptr->vtable[0] = Student_getAge; structptr->vtable[1] = Student_setAge; structptr->user_age = user_a; return structptr; }" + 
				"int main(){" + 
				"int user_age;" + 
				"user_age = 21;" + 
				"struct Student* user_student;" + 
				"user_student = Student_constructor0(malloc(sizeof(struct Student)), user_age);" + 
				"user_student->vtable[0](user_student);" + 
				"return 0;" + 
				"}", prog);
	}
	
	@Test
	public void testBasicProgramExtendingClass() throws IOException, ParserException, TokenizerException, TypeErrorException{
		
		//Standard class declaration with variety of statements outside
    	final String input = "public class Person{"
    			+ "private String name;"
    			+ "public Person(String n){"
    			+ "this.name = n;"
    			+ "}"
    			+ "public void setName(String n){"
    			+ "this.name = n;"
    			+ "}"
    			+ "public String getName(){"
    			+ "return this.name;"
    			+ "}"
    			+ "}"
    			+ "public class Student extends Person{"
    						+"private int age;"
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);"
    				  + "student.getAge();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog);
    	
		assertProgramGeneration("#include <stdio.h>" + 
				"#include <stdlib.h>" + 
				"struct Person { char* user_name; void* (*vtable[2]) ();};" + 
				"struct Student { struct Person parent; int* user_age; void* (*vtable[4]) ();};" + 
				"void Person_setName(struct Person* structptr, char* user_n){structptr->user_name = user_n; }" + 
				"char* Person_getName(struct Person* structptr){return structptr->user_name; }" + 
				"int Student_getAge(struct Student* structptr){return structptr->user_age; }" + 
				"void Student_setAge(struct Student* structptr, int user_n){structptr->user_age = user_n; }" + 
				"struct Person* Person_constructor0(struct Person* structptr, char* user_n){structptr->vtable[0] = Person_setName; structptr->vtable[1] = Person_getName; structptr->user_name = user_n; return structptr; }" + 
				"struct Student* Student_constructor0(struct Student* structptr, int user_a){structptr->user_age = malloc(sizeof(int)); structptr->vtable[0] = Person_setName; structptr->vtable[1] = Person_getName; structptr->vtable[2] = Student_getAge; structptr->vtable[3] = Student_setAge; structptr->user_age = user_a; return structptr; }" + 
				"int main(){" + 
				"int user_age;" + 
				"user_age = 21;" + 
				"struct Student* user_student;" + 
				"user_student = Student_constructor0(malloc(sizeof(struct Student)), user_age);" + 
				"user_student->vtable[2](user_student);" + 
				"return 0;" + 
				"}", prog);
	}
	
	@Test
	public void testBasicProgramExtendingTwiceClass() throws IOException, ParserException, TokenizerException, TypeErrorException{
		
		//Standard class declaration with variety of statements outside
    	final String input ="public class Person{"
    							+ "private String name;"
    							+ "public Person(String n){"
    								+ "this.name = n;"
    							+ "}"
    							+ "public void setName(String n){"
    								+ "this.name = n;"
    							+ "}"
    							+ "public String getName(){"
    								+ "return this.name;"
    							+ "}"
    						+ "}"
    							
    						+ "public class Human extends Person{"
    							+ "private String gender;"
    							+ "public Human(String g){"
    								+ "this.gender = g;"
    							+ "}"
    							+ "public void setGender(String g){"
    								+ "this.gender = g;"
    							+ "}"
    							+ "public String getGender(){"
    								+ "return this.gender;"
    							+ "}"
    						+ "}"
    							
    						+ "public class Student extends Human{"
    							+"private int age;"
    							+"public Student(int a) {"
    								+"this.age = a; "
    							+"}"
    							+"public int getAge() {"
    								+ "String g;"
    								//+ "g = this.gender;"
    								+"return this.age; "
    							+"}"
    							+"public void setAge(int n) {"
    								+"this.age=n; "
    							+"}"
    						+ "}"
    						+ "int age; "
    						+ "age = 21;"
    						+ "Student student;"
    						+ "student = new.Student(age);"
    						+ "student.getAge();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog);
    	
		assertProgramGeneration("#include <stdio.h>" + 
				"#include <stdlib.h>" + 
				"struct Person { char* user_name; void* (*vtable[2]) ();};" + 
				"struct Human { struct Person parent; char* user_gender; void* (*vtable[4]) ();};" + 
				"struct Student { struct Human parent; int* user_age; void* (*vtable[6]) ();};" + 
				"void Person_setName(struct Person* structptr, char* user_n){structptr->user_name = user_n; }" + 
				"char* Person_getName(struct Person* structptr){return structptr->user_name; }" + 
				"void Human_setGender(struct Human* structptr, char* user_g){structptr->user_gender = user_g; }" + 
				"char* Human_getGender(struct Human* structptr){return structptr->user_gender; }" + 
				"int Student_getAge(struct Student* structptr){return structptr->user_age; }" + 
				"void Student_setAge(struct Student* structptr, int user_n){structptr->user_age = user_n; }" + 
				"struct Person* Person_constructor0(struct Person* structptr, char* user_n){structptr->vtable[0] = Person_setName; structptr->vtable[1] = Person_getName; structptr->user_name = user_n; return structptr; }" + 
				"struct Human* Human_constructor0(struct Human* structptr, char* user_g){structptr->vtable[0] = Person_setName; structptr->vtable[1] = Person_getName; structptr->vtable[2] = Human_setGender; structptr->vtable[3] = Human_getGender; structptr->user_gender = user_g; return structptr; }" + 
				"struct Student* Student_constructor0(struct Student* structptr, int user_a){structptr->user_age = malloc(sizeof(int)); structptr->vtable[0] = Person_setName; structptr->vtable[1] = Person_getName; structptr->vtable[2] = Human_setGender; structptr->vtable[3] = Human_getGender; structptr->vtable[4] = Student_getAge; structptr->vtable[5] = Student_setAge; structptr->user_age = user_a; return structptr; }" + 
				"int main(){" + 
				"int user_age;" + 
				"user_age = 21;" + 
				"struct Student* user_student;" + 
				"user_student = Student_constructor0(malloc(sizeof(struct Student)), user_age);" + 
				"user_student->vtable[4](user_student);" + 
				"return 0;" + 
				"}", prog);
	}
	
	@Test
	public void testBasicProgramGenericClass() throws IOException, ParserException, TokenizerException, TypeErrorException{
		
		//Standard class declaration with variety of statements outside
    	final String input = "public class Node<E>{"
    							+"private E element;"
    							+"public Node(E elem) {"
    								+"this.element = elem; "
    							+"}"
    							+"public E getElement() {"
    								+"return this.element; "
    							+"}"
    							+"public void setElement(E elem) {"
    								+"this.element=elem; "
    							+"}"
    						+ "}"
    						+ "String element;"
    						+ "element = \"Hello world!\";"
    						+ "Node<String> n;"
    						+ "n = new.Node<String>(element);"
    						+ "n.getElement();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog);
    	
		assertProgramGeneration("#include <stdio.h>" + 
				"#include <stdlib.h>" + 
				"struct Node { void* user_element; void* (*vtable[2]) ();};" + 
				"void* Node_getElement(struct Node* structptr){return structptr->user_element; }" + 
				"void Node_setElement(struct Node* structptr, void* user_elem){structptr->user_element = user_elem; }" + 
				"struct Node* Node_constructor0(struct Node* structptr, void* user_elem){structptr->vtable[0] = Node_getElement; structptr->vtable[1] = Node_setElement; structptr->user_element = user_elem; return structptr; }" + 
				"int main(){" + 
				"char* user_element;" + 
				"user_element = \"Hello world!\";" + 
				"struct Node* user_n;" + 
				"user_n = Node_constructor0(malloc(sizeof(struct Node)), user_element);" + 
				"user_n->vtable[0](user_n);" + 
				"return 0;" + 
				"}", prog);
	}
}
