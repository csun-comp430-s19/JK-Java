package JK_Lexer;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ParserTest {
    // specify null for expected if it's not supposed to parse
    public void assertParses(final Token[] tokens,
                             final Exp expected) {
        final Parser parser = new Parser(tokens);
        try {
            final Exp received = parser.parseExp();
            assertTrue("Expected parse failure; got: " + received,
                       expected != null);
            assertEquals(expected, received);
        } catch (final ParserException e) {
            assertTrue(("Unexpected parse failure for " +
                        Arrays.toString(tokens) +
                        ": " + e.getMessage()),
                       expected == null);
        }
    } // assertParses

    @Test
    public void testParsesInteger() {
        assertParses(new Token[]{ new NumberToken(123) },
                     new NumberExp(123));
    }

    @Test
    public void testParsesVariable() {
        assertParses(new Token[]{ new NameToken("foo") },
                     new VariableExp("foo"));
    }

    @Test
    public void testParsesParens() {
        final Token[] tokens = { new LeftParenToken(),
                                 new NumberToken(1),
                                 new RightParenToken() };
        final Exp expected = new NumberExp(1);
        assertParses(tokens, expected);
    }

    @Test
    public void testParsesPlus() {
        final Token[] tokens = { new NumberToken(1),
                                 new PlusToken(),
                                 new NumberToken(2) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new PlusOp(),
                                          new NumberExp(2));
        assertParses(tokens, expected);
    }

    @Test
    public void testParsesMinus() {
        final Token[] tokens = { new NumberToken(1),
                                 new MinusToken(),
                                 new NumberToken(2) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new MinusOp(),
                                          new NumberExp(2));
        assertParses(tokens, expected);
    }

    @Test
    public void testParsesMult() {
        final Token[] tokens = { new NumberToken(1),
                                 new MultToken(),
                                 new NumberToken(2) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new MultOp(),
                                          new NumberExp(2));
        assertParses(tokens, expected);
    }

    @Test
    public void testParsesDiv() {
        final Token[] tokens = { new NumberToken(1),
                                 new DivToken(),
                                 new NumberToken(2) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new DivOp(),
                                          new NumberExp(2));
        assertParses(tokens, expected);
    }

    @Test
    public void testArithmeticLeftAssociative() {
        final Token[] tokens = { new NumberToken(1),
                                 new PlusToken(),
                                 new NumberToken(2),
                                 new MinusToken(),
                                 new NumberToken(3) };
        final Exp expected = new BinopExp(new BinopExp(new NumberExp(1),
                                                       new PlusOp(),
                                                       new NumberExp(2)),
                                          new MinusOp(),
                                          new NumberExp(3));
        assertParses(tokens, expected);
    }

    @Test
    public void testArithmeticPrecedence() {
        final Token[] tokens = { new NumberToken(1),
                                 new MinusToken(),
                                 new NumberToken(2),
                                 new DivToken(),
                                 new NumberToken(3) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new MinusOp(),
                                          new BinopExp(new NumberExp(2),
                                                       new DivOp(),
                                                       new NumberExp(3)));
        assertParses(tokens, expected);
    }

    @Test
    public void testArithmeticPrecedenceWithParens() {
        final Token[] tokens = { new LeftParenToken(),
                                 new NumberToken(1),
                                 new MinusToken(),
                                 new NumberToken(2),
                                 new RightParenToken(),
                                 new DivToken(),
                                 new NumberToken(3) };
        final Exp expected = new BinopExp(new BinopExp(new NumberExp(1),
                                                       new MinusOp(),
                                                       new NumberExp(2)),
                                          new DivOp(),
                                          new NumberExp(3));
        assertParses(tokens, expected);
    }
    @Test
    public void testString() {
    	final Token[] tokens = { new QuoteToken(), 
    							 new QuotedStringToken("woo"),
    							 new QuoteToken() }; 
    	final Exp expected = new StringExp("woo");
    	assertParses(tokens, expected); 
    }
    @Test
    public void testThis() {
    	final Token[] tokens = { new ThisToken(), 
    							 new PeriodToken(), 
    							 new NameToken("boo") }; 
    	final Exp expected = new ThisExp("boo"); 
    	assertParses(tokens, expected); 
    }
    public void testPrint() {
    	final Token [] tokens = { new PrintToken(), 
    							  new LeftParenToken(), 
    							  new NameToken("Hello World"), 
    							  new RightParenToken() }; 
    	final Exp expected = new PrintExp("Hello World");
    	assertParses(tokens, expected); 
    }
}