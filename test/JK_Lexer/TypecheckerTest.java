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
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
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
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
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
    						+"	age = a; "
    						+"}"
    						+"public String getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
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
    						+"	age = b; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
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
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void getAge(int n) {"
    							+"age=n; "
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
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
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
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
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
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
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
    	final String input = "public class Student{"
    						+"private int age; "
    						+"public Student(int a) {"
    						+"	age = a; "
    						+"}"
    						+"public int getAge() {"
    							+"return age; "
    						+"}"
    						+"public void setAge(int n) {"
    							+"age=n; "
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
}