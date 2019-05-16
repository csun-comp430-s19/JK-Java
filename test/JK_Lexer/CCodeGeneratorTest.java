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
	
}
