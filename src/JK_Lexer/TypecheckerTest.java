package JK_Lexer;

import java.util.ArrayList; 
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TypecheckerTest {
	ArrayList<Statement> statements = new ArrayList<Statement>(0); 
	ArrayList<ClassDefExp> classdefs = new ArrayList<ClassDefExp>(0); 
	//THIS METHOD TESTS EXPRESSIONS WITH NO VARIABLES
	public void assertExpType(final Type expected, final Exp exp) {
		Program prog = new Program(statements, classdefs); 
		Typechecker typecheck = new Typechecker(prog);  
		try {
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
    @Test(expected = java.lang.AssertionError.class)   //using java.lang.assertionerror.class since the method assertExpType throws this when finding typeerrorexception
    public void failsTestVariableCheckWithoutClass() throws TypeErrorException{
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("foo")));
    	assertExpType(new IntType(), new VariableExp("oof"));
    }
    @Test
    public void	testPrintWithoutClass() {
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("oof")));
    	assertExpType(new IntType(), new PrintExp(new VariableExp("oof")));
    }
    @Test(expected = java.lang.AssertionError.class)
    public void failsTestPrintWithoutClass() throws TypeErrorException{
    	statements.add(new VariableDecExp(new IntType(), new VariableExp("foo")));
    	assertExpType(new IntType(), new PrintExp(new VariableExp("oof")));
    }
}