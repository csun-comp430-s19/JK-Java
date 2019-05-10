package JK_Lexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
	
	public void assertMainMethodGeneration(String expected, Statement[] sArray) throws IOException{
		try {
			CCodeGenerator cg = new CCodeGenerator(); 
			final File file = File.createTempFile("testfile",".c");
			cg.writeMainToFile(sArray, file);
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
	@Test 
	public void testVariableExp() throws IOException{
		assertBasicExpGeneration("varname21", new VariableExp("varname21"));
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
	public void testPrintF() throws IOException{
		assertBasicExpGeneration("printf(var1)", new PrintExp(new VariableExp("var1")));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestPrintF() throws IOException{
		assertBasicExpGeneration("printf(var2)", new PrintExp(new VariableExp("var1")));
	}
	@Test
	public void testVariableDeclarationInt() throws IOException{
		assertStatementGeneration("int foo1;", new VariableDecExp(new IntType(), new VariableExp("foo1")));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestVariableDeclarationInt() throws IOException{
		assertStatementGeneration("int foo2;", new VariableDecExp(new IntType(), new VariableExp("foo1")));
	}
	@Test
	public void testVariableDeclarationCharArray() throws IOException{
		assertStatementGeneration("char foo1[];", new VariableDecExp(new StringType(), new VariableExp("foo1")));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestVariableDeclarationCharArray() throws IOException{
		assertStatementGeneration("char foo1;", new VariableDecExp(new StringType(), new VariableExp("foo1")));
	}
	@Test
	public void testReturnInteger() throws IOException{
		assertStatementGeneration("return 0;", new ReturnStmt(new NumberExp(0)));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestReturnInteger() throws IOException{
		assertStatementGeneration("return 1;", new ReturnStmt(new NumberExp(0)));
	}
	@Test
	public void testReturnVariable() throws IOException{
		assertStatementGeneration("return foo1;", new ReturnStmt(new VariableExp("foo1")));
	}
	@Test(expected = ComparisonFailure.class)
	public void failTestReturnVariable() throws IOException{
		assertStatementGeneration("return foo2;", new ReturnStmt(new VariableExp("foo1")));
	}
	@Test
	public void testReturnString() throws IOException{
		assertStatementGeneration("return \"hello world\";", new ReturnStmt(new StringExp("hello world")));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestReturnString() throws IOException{
		assertStatementGeneration("return \"goodbye world\";", new ReturnStmt(new StringExp("hello world")));
	}
	@Test 
	public void testAssignmentInt() throws IOException{
		assertStatementGeneration("foo1 = 1;", new AssignmentStmt(new VariableExp("foo1"), new NumberExp(1)));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestAssignmentInt() throws IOException{
		assertStatementGeneration("foo1 = 2;", new AssignmentStmt(new VariableExp("foo1"), new NumberExp(1)));
	}
	@Test 
	public void testAssignmentBinop() throws IOException{
		assertStatementGeneration("foo1 = (1 + 2);", new AssignmentStmt(new VariableExp("foo1"), new BinopExp(new NumberExp(1), new PlusOp(),new NumberExp(2))));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestAssignmentBinop() throws IOException{
		assertStatementGeneration("foo1 = (1 + 7);", new AssignmentStmt(new VariableExp("foo1"), new BinopExp(new NumberExp(1), new PlusOp(),new NumberExp(2))));
	}
	@Test 
	public void testAssignmentString() throws IOException{
		assertStatementGeneration("foo1 = \"hello\";", new AssignmentStmt(new VariableExp("foo1"), new StringExp("hello")));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestAssignmentString() throws IOException{
		assertStatementGeneration("foo1 = \"goodbye\";", new AssignmentStmt(new VariableExp("foo1"), new StringExp("hello")));
	}
	@Test 
	public void testAssignmentVarToVar() throws IOException{
		assertStatementGeneration("foo1 = foo2;", new AssignmentStmt(new VariableExp("foo1"), new VariableExp("foo2")));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestAssignmentVarToVar() throws IOException{
		assertStatementGeneration("foo1 = foo3;", new AssignmentStmt(new VariableExp("foo1"), new VariableExp("foo2")));
	}
	@Test
	public void testMainMethod() throws IOException{
		Statement[] sArray = new Statement[] { new VariableDecExp(new IntType(), new VariableExp("foo")),
											   new AssignmentStmt(new VariableExp("foo"), new NumberExp(100)),
											   new ReturnStmt(new NumberExp(0))};
		
		assertMainMethodGeneration("int main(){"
				                 + "int foo;"
				                 + "foo = 100;"
				                 + "return 0;"
				                 + "}", sArray);
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestMainMethod() throws IOException{
		Statement[] sArray = new Statement[] { new VariableDecExp(new IntType(), new VariableExp("foo")),
											   new AssignmentStmt(new VariableExp("foo"), new NumberExp(100)),
											   new ReturnStmt(new NumberExp(0))};
		
		assertMainMethodGeneration("int main(){"
				                 + "String foo;"
				                 + "foo = 500;"
				                 + "return 0;"
				                 + "}", sArray);
	}
	
	@Test
	public void testCallMethod() throws IOException{//FAILING DUE TO UNFINISHED CODEGEN
		VariableExp obj = new VariableExp("foo");
		VariableExp method = new VariableExp("bar");
		VariableExp param = new VariableExp("foobar");
		ArrayList<VariableExp> params = new ArrayList<VariableExp>();
		params.add(param);
		CallMethodExp exp = new CallMethodExp(obj,method,params);
		
		assertBasicExpGeneration("bar(foo, foobar);", exp);	
	}
	
	@Test(expected = ComparisonFailure.class)//FAILING DUE TO UNFINISHED CODEGEN
	public void failsTestCallMethod() throws IOException{
		VariableExp obj = new VariableExp("bar");
		VariableExp method = new VariableExp("foo");
		VariableExp param = new VariableExp("foobar");
		ArrayList<VariableExp> params = new ArrayList<VariableExp>();
		params.add(param);
		CallMethodExp exp = new CallMethodExp(obj,method,params);
		
		assertBasicExpGeneration("bar(foo, foobar);", exp);	
	}
	
	@Test
	public void testBasicProgramSingleStatement() throws IOException{
		VariableDecExp vdec = new VariableDecExp(new IntType(), new VariableExp("foo"));
		ArrayList<Statement> s = new ArrayList<Statement>();
		s.add(vdec);
		
		Program p = new Program(s, new ArrayList<ClassDefExp>());
		
		assertProgramGeneration("include <stdio.h>include <stdlib.h>int main(){int foo;return 0;}", p);
	}
	
	@Test
	public void testBasicProgramBasicClass() throws IOException{
		
		ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), new VariableExp("age"))));
    	ArrayList<MethodDefExp> methodList = new ArrayList<MethodDefExp>();
    	ArrayList<ConstructorDef> constructorList = new ArrayList<ConstructorDef>();
    	ArrayList<Statement> block = new ArrayList<Statement>();
    	ArrayList<Statement> setblock = new ArrayList<Statement>();
    	ArrayList<VariableDecExp> setparam = new ArrayList<VariableDecExp>();
    	ArrayList<VariableDecExp> constructorParam = new ArrayList<VariableDecExp>();
    	ArrayList<Statement> constructorblock = new ArrayList<Statement>();
    	constructorblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("a")));
    	constructorParam.add(new VariableDecExp(new IntType(), new VariableExp("a")));
    	setparam.add(new VariableDecExp(new IntType(), new VariableExp("n")));
    	block.add(new ReturnStmt(new VariableExp("age")));
    	setblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("n")));
    	methodList.add(new MethodDefExp(new PublicModifier(), new IntType(), "getAge", new ArrayList<VariableDecExp>(), block));
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "setAge", setparam, setblock));
    	constructorList.add(new ConstructorDef(new PublicModifier(), "Student", constructorParam, constructorblock));
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList, true, "Person");
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program p = new Program(statementList, classDefList);
		
    	//System.out.println(p.toString());
    	
		assertProgramGeneration("include <stdio.h>include <stdlib.h>int main(){int foo;return 0;}", p);
	}
	
}
