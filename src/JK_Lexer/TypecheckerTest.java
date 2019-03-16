package JK_Lexer;
 
import java.util.ArrayList; 
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

//NOTES ON TESTS: We had a lot of trouble in fixing and adding more features to our parser,
//as well as fixing some of the types and classes we had in the syntax. As you can see, 
//our parser doesn't fully work for program as of right now because of these attempted fixes. 
//As a result, it came down to the wire timewise when our tests finally worked after debugging
//the typechecker, so our tests are fairly scarce as of the deadline
//A lot of things have gone unchecked as well because of this

//Since theres no submission for peer review on Canvas, 

//Jason: Edited my part of the parser (exp), started the typechecker and wrote most of the constructor,
//most of lookupVariable, and typeofExp

//Kodi:  Added in typechecking for everything else that is not exp
//       Added in and changed parser as it was missing some object orientation features
//       (parser and added oop stuff is still broken)




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
    
    
    
    
   
    // OUR PARSER IS BROKEN AS OF RIGHT NOW AFTER TRYING TO MODIFY IT SO PLEASE EXCUSE THE GIANT TESTS COMING UP 
    // I TRIED TO PUT WHAT EACH TEST IS TESTING IN COMMENTS BEFORE 
    
    
    
    
    
    @Test
    public void testEntireClassWithStatementsOutside() throws TypeErrorException{
    	/*public class Student{
    	 * 	 private int age; 
    	 * 	 public Student(int a){
    	 * 		age=a; 
    	 * 	  }
    	 *   public int getAge(){
    	 *  	return age; 
    	 *    }
    	 *   public void setAge(int n){
    	 *  	age=n;
    	 *    }
    	 *  }
    	 *  int age; 
    	 *  age=21;
    	 *  Student student;
    	 *  student = new Student(age);
    	 *  END PROGRAM */
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
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList);
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program prog = new Program(statementList, classDefList);
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsNoInstanceTestEntireClassWithStatementsOutside() throws TypeErrorException{
    	//Same prog as testEntireClassWithStatementsOutside but instance is named notage
    	
    	ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), new VariableExp("notage"))));
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
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList);
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program prog = new Program(statementList, classDefList);
    	Typechecker.typecheckProgram(prog); 
    }
    @Test(expected = TypeErrorException.class)
    public void failsWrongMethodTypeTestEntireClassWithStatementsOutside() throws TypeErrorException{
    	//Same as TestEntireClassWithStatementsOutside but getAge returns string instead of int
    	
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
    	methodList.add(new MethodDefExp(new PublicModifier(), new StringType(), "getAge", new ArrayList<VariableDecExp>(), block));
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "setAge", setparam, setblock));
    	constructorList.add(new ConstructorDef(new PublicModifier(), "Student", constructorParam, constructorblock));
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList);
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program prog = new Program(statementList, classDefList);
    	Typechecker.typecheckProgram(prog);
    }
    @Test(expected = TypeErrorException.class)
    public void failsWrongParameterEntireClassWithStatementsOutside() throws TypeErrorException{
    	//Same as TestEntireClassWithStatementsOutside but assignment in constructor uses b instead of a 
    	
    	ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), new VariableExp("age"))));
    	ArrayList<MethodDefExp> methodList = new ArrayList<MethodDefExp>();
    	ArrayList<ConstructorDef> constructorList = new ArrayList<ConstructorDef>();
    	ArrayList<Statement> block = new ArrayList<Statement>();
    	ArrayList<Statement> setblock = new ArrayList<Statement>();
    	ArrayList<VariableDecExp> setparam = new ArrayList<VariableDecExp>();
    	ArrayList<VariableDecExp> constructorParam = new ArrayList<VariableDecExp>();
    	ArrayList<Statement> constructorblock = new ArrayList<Statement>();
    	constructorblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("b")));
    	constructorParam.add(new VariableDecExp(new IntType(), new VariableExp("a")));
    	setparam.add(new VariableDecExp(new IntType(), new VariableExp("n")));
    	block.add(new ReturnStmt(new VariableExp("age")));
    	setblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("n")));
    	methodList.add(new MethodDefExp(new PublicModifier(), new IntType(), "getAge", new ArrayList<VariableDecExp>(), block));
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "setAge", setparam, setblock));
    	constructorList.add(new ConstructorDef(new PublicModifier(), "Student", constructorParam, constructorblock));
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList);
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program prog = new Program(statementList, classDefList);
    	Typechecker.typecheckProgram(prog);
    }
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateMethodEntireClassWithStatementsOutside() throws TypeErrorException{
    	//Same as TestEntireClassWithStatementsOutside but both methods have same name getAge
    	
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
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "getAge", setparam, setblock));
    	constructorList.add(new ConstructorDef(new PublicModifier(), "Student", constructorParam, constructorblock));
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList);
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program prog = new Program(statementList, classDefList);
    	Typechecker.typecheckProgram(prog);
    }
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateInstanceEntireClassWithStatementsOutside() throws TypeErrorException{
    	//Same as TestEntireClassWithStatementsOutside but with two instances of age (one string, one int)
    	
    	ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), new VariableExp("age"))));
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new StringType(), new VariableExp("age"))));
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
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "getAge", setparam, setblock));
    	constructorList.add(new ConstructorDef(new PublicModifier(), "Student", constructorParam, constructorblock));
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList);
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program prog = new Program(statementList, classDefList);
    	Typechecker.typecheckProgram(prog);
    }
    @Test(expected = TypeErrorException.class)
    public void failsDuplicateClassEntireClassWithStatementsOutside() throws TypeErrorException{
    	//Same as TestEntireClassWithStatementsOutside but has two classes named Student (added another class Student that is blank)
    	
    	ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), new VariableExp("age"))));
    	ArrayList<MethodDefExp> methodList = new ArrayList<MethodDefExp>();
    	ArrayList<ConstructorDef> constructorList = new ArrayList<ConstructorDef>();
    	ArrayList<Statement> block = new ArrayList<Statement>();
    	ArrayList<Statement> setblock = new ArrayList<Statement>();
    	ArrayList<VariableDecExp> setparam = new ArrayList<VariableDecExp>();
    	ArrayList<VariableDecExp> constructorParam = new ArrayList<VariableDecExp>();
    	ArrayList<Statement> constructorblock = new ArrayList<Statement>();
    	constructorblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("b")));
    	constructorParam.add(new VariableDecExp(new IntType(), new VariableExp("a")));
    	setparam.add(new VariableDecExp(new IntType(), new VariableExp("n")));
    	block.add(new ReturnStmt(new VariableExp("age")));
    	setblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("n")));
    	methodList.add(new MethodDefExp(new PublicModifier(), new IntType(), "getAge", new ArrayList<VariableDecExp>(), block));
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "setAge", setparam, setblock));
    	constructorList.add(new ConstructorDef(new PublicModifier(), "Student", constructorParam, constructorblock));
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList);
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	classDefList.add(new ClassDefExp(new PublicModifier(),"Student", new ArrayList<ConstructorDef>(), new ArrayList<InstanceDecExp>(), new ArrayList<MethodDefExp>()));
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program prog = new Program(statementList, classDefList);
    	Typechecker.typecheckProgram(prog);
    }
}