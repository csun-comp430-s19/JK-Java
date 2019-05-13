package JK_Lexer;
 
import java.util.List; 
import java.util.ArrayList; 
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TypecheckerTest {
	ArrayList<Statement> statements = new ArrayList<Statement>(0); 
	ArrayList<ClassDefExp> classdefs = new ArrayList<ClassDefExp>(0); 
	//method tests some expressions
	public void assertExpType(final Type expected, final Exp exp) {
		Program prog = new Program(statements, classdefs); 
		try {
		Typechecker typecheck = new Typechecker(prog);  
			final Type received = typecheck.typeofExp(exp);
			assertTrue("Expected type error; got: "+ received.toString(), expected!=null); 
			assertEquals(expected,received); 
		}
		catch(final TypeErrorException e) {
			assertTrue("Unexpected type error: "+e.getMessage(), expected == null); 
		}
	}
	@Test
	public void testIntExp() {
        assertExpType(new IntType(),
                      new NumberExp(42));
	}
	@Test
	public void testStringExp() {
		assertExpType(new StringType(), 
					  new StringExp("hello")); 
	}
	@Test
	public void testBinopPlusInts() {
        assertExpType(new IntType(),
                      new BinopExp(new NumberExp(1),
                                   new PlusOp(),
                                   new NumberExp(2)));
	}
	@Test 
    public void testBinopPlusNonIntOrPointer() {
        assertExpType(null,
                      new BinopExp(new StringExp("oof"),
                                   new PlusOp(),
                                   new NumberExp(1)));
	}
    @Test
    public void testMinusInts() {
        assertExpType(new IntType(),
                      new BinopExp(new NumberExp(1),
                                   new MinusOp(),
                                   new NumberExp(2)));
    }
    @Test
    public void testMinusNonInts() {
        assertExpType(null,
                      new BinopExp(new NumberExp(1),
                                   new MinusOp(),
                                   new StringExp("oof")));
    }
    @Test
    public void testMultInts() {
        assertExpType(new IntType(),
                      new BinopExp(new NumberExp(1),
                                   new MultOp(),
                                   new NumberExp(2)));
    }

    @Test
    public void testMultNonInts() {
        assertExpType(null,
                      new BinopExp(new NumberExp(1),
                                   new MultOp(),
                                   new StringExp("oof")));
    }
    @Test
    public void testDivInts() {
        assertExpType(new IntType(),
                      new BinopExp(new NumberExp(1),
                                   new DivOp(),
                                   new NumberExp(2)));
    }

    @Test
    public void testDivNonInts() {
        assertExpType(null,
                      new BinopExp(new NumberExp(1),
                                   new DivOp(),
                                   new StringExp("oof")));
    }
    @Test 
    public void testVariableCheckWithoutClass() {
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("oof")));
    	assertExpType(new IntType(), new VariableExp("oof"));
    }
    @Test(expected = java.lang.AssertionError.class)   //using java.lang.AssertionError.class since the method assertExpType throws this when finding TypeErrorException
    public void failsTestVariableCheckWithoutClass() throws TypeErrorException{
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("foo")));
    	assertExpType(new IntType(), new VariableExp("oof"));
    }
    @Test
    public void	testPrintWithoutClass() {
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("oof")));
    	assertExpType(new IntType(), new PrintExp(new VariableExp("oof")));
    }
    @Test(expected = java.lang.AssertionError.class)   //using java.lang.AssertionError.class since the method assertExpType throws this when finding TypeErrorException
    public void failsTestPrintWithoutClass() throws TypeErrorException{
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("foo")));
    	assertExpType(new IntType(), new PrintExp(new VariableExp("oof")));
    }
    
    //Tests for full programs now fully functional with tokenizer, parser, and typechecker 

    @Test
    public void testEntireClassWithStatementsOutside() throws TypeErrorException, TokenizerException, ParserException{
    	//Standard class declaration with variety of statements outside
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsNoInstanceTestEntireClassWithStatementsOutside() throws TypeErrorException, TokenizerException, ParserException{
    	//Instance variable is named notage instead but age is used throughout
    	final String input = "public class Student{"
    						+"private int notage; "
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
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsWrongMethodTypeTestEntireClassWithStatementsOutside() throws TypeErrorException, TokenizerException, ParserException{
    	//getAge is supposed to return a string but returns an int 
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public String getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsWrongParameterEntireClassWithStatementsOutside() throws TypeErrorException, TokenizerException, ParserException{
    	//Assignment in constructor uses undeclared variable b 
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = b; "
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
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateMethodEntireClassWithStatementsOutside() throws TypeErrorException, TokenizerException, ParserException{
    	//Two methods named getAge
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void getAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateInstanceEntireClassWithStatementsOutside() throws TypeErrorException, TokenizerException, ParserException{
    	// Two instances of age, one string and one int
    	final String input = "public class Student{"
    						+"private int age; "
    						+"private String age;"
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
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateClassEntireClassWithStatementsOutside() throws TypeErrorException, TokenizerException, ParserException{
    	//Duplicate classes both named student, one blank for test
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "public class Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test
    public void testExtends() throws TypeErrorException, TokenizerException, ParserException{
    	//Testing extends keyword (inheritance)
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "public class Junior extends Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestExtends() throws TypeErrorException, TokenizerException, ParserException{
    	// testing extends failing when class extending does not exist (Person)
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "public class Junior extends Person{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void testNewClassVariableObjectThatDoesntExist() throws TypeErrorException, TokenizerException, ParserException{
    	// testing Person student = new Student() when Person does not exist
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "public class Junior extends Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Person student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test
    public void testNewClassInheritance() throws TypeErrorException, TokenizerException, ParserException{
    	// Testing new class object declaration with inheritance
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "public class Junior extends Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Junior junior;"
    				  + "junior = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestNewClassInheritance() throws TypeErrorException, TokenizerException, ParserException{
    	//Testing new clas inheritance failing when going backwards in inheritance
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "public class Junior extends Student{"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Junior(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void testPrivateMethodAccess() throws TypeErrorException, TokenizerException, ParserException{
    	//Testing fails when accessing private methods
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"private int getAge() {"
    							+"return this.age; "
    						+"}"
    				  + "}"
    				  + "int age; "
    			  	  + "Student student;"
    				  + "age = student.getAge();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test
    public void testFunctionCallOutsideClass() throws TypeErrorException, TokenizerException, ParserException{
    	//Testing function calls outside class
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    				  + "}"
    				  + "int age; "
    			  	  + "Student student;"
    				  + "age = student.getAge();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestFunctionCallOutsideClassWrongParameters() throws TypeErrorException, TokenizerException, ParserException{
    	//testing wrong parameters on function call 
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "int n; "
    			  	  + "Student student;"
    				  + "age = student.getAge(n);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestFunctionCallOutsideClassWrongTypeParameters() throws TypeErrorException, TokenizerException, ParserException{
    	// testing wrong type of parameters on function call 
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge(int n) {"
    							+"return this.age; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "String n; "
    			  	  + "Student student;"
    				  + "age = student.getAge(n);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test
    public void testIndependentMethodCall() throws TypeErrorException, TokenizerException, ParserException{
    	//testing independent method call 
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "student.setAge(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected=TypeErrorException.class)
    public void failsTestIndependentMethodCallPrivate() throws TypeErrorException, TokenizerException, ParserException{
    	//testing fail when calling private method with independent method call
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"private void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student.setAge(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected=TypeErrorException.class)
    public void failsTestIndependentMethodCallWrongMethodName() throws TypeErrorException, TokenizerException, ParserException{
    	//testing fail when calling invalid method name for independent method call
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "student.setAge1(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected=TypeErrorException.class)
    public void failsTestIndependentMethodCallWrongParameterNumber() throws TypeErrorException, TokenizerException, ParserException{
    	//testing fail when wrong number of parameters in independent method call
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "student.setAge();";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected=TypeErrorException.class)
    public void failsTestIndependentMethodCallWrongParameterType() throws TypeErrorException, TokenizerException, ParserException{
    	//testing fail when wrong parameter type in independent method call
    	final String input = "public class Student{"
    						+"private int age; "
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
    				  + "String age; "
    			  	  + "Student student;"
    				  + "student.setAge(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleSameParameterInConstructor() throws TypeErrorException, TokenizerException, ParserException{
    	// Multiple parameters with same name in constructor
    	final String input = "public class Student{"
    						+"private int age;"
    						+"public Student(int a, int a) {"
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
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleSameParameterInMethod() throws TypeErrorException, TokenizerException, ParserException{
    	// Multiple parameters with same name in method
    	final String input = "public class Student{"
    						+"private int age;"
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n, int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestEntireClassWithStatementsOutsideMoreCoverage() throws TypeErrorException, TokenizerException, ParserException{
    	//Failing class declaration with variety of statements outside (more coverage, some code is redundant/useless but is present solely for testing coverage)
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"int s;"
    							+"int n;"
    							+"s=2;"
    							+"this.age=n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test
    public void testEntireClassWithStatementsOutsideMoreCoverage() throws TypeErrorException, TokenizerException, ParserException{
    	//Failing class declaration with variety of statements outside (more coverage, some code is redundant/useless but is present solely for testing coverage)
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleVariablesOutsideClass() throws TypeErrorException, TokenizerException, ParserException{
    	//Fails because multiple variables same name outside class
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestNewClassDoesntExist() throws TypeErrorException, TokenizerException, ParserException{
    	//Fails because person doesnt exist
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Person(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestCallMethodThatDoesntExist() throws TypeErrorException, TokenizerException, ParserException{
    	//Fails because add doesnt exist for ints
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    							+"n=n.add(n);"
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test
    public void testCallMethodWithinClass() throws TypeErrorException, TokenizerException, ParserException{
    	//testing call method in a class method
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    							+"Student s;"
    							+"n=s.getAge();"
    						+"}"
    				  + "}"
    				  + "int age; "
    				  + "age = 21;"
    			  	  + "Student student;"
    				  + "student = new.Student(age);";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsTestMultipleSameInstance() throws TypeErrorException, TokenizerException, ParserException{
    	//Fails because same instance variable
    	final String input = "public class Student{"
    						+"private int age; "
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsDeclaredAlreadyInConstructor() throws TypeErrorException, TokenizerException, ParserException{
    	//Fails because a already declared in constructor
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"  int a; "
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age = n; "
    						+"}"
    				  + "}";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test
    public void testMultipleConstructors() throws TypeErrorException, TokenizerException, ParserException{
    	//Standard class declaration with multiple constructors
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public Student(int a, int b) {"
    						+"	this.age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return this.age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"this.age=n; "
    						+"}"
    				  + "}";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsCallingInstanceWithoutThis() throws TypeErrorException, TokenizerException, ParserException{
    	//fails since methods do  not call this.age for age
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	this.age = a; "
    						+"}"
    						+"public Student(int a, int b) {"
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
    						+"}"
    				  + "}";
    	final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
    	final List<Token> tokenList = tokenizer.tokenize(); 
    	Token[] tokenArray = new Token[tokenList.size()];
    	tokenArray = tokenList.toArray(tokenArray); 
    	final Parser parser = new Parser(tokenArray); 
    	final Program prog = parser.parseProgram(); 
    	Typechecker.typecheckProgram(prog); 
    }
}