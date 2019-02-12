package JK_Lexer;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

public class TokenizerTest {
    // specify null for expected if it's not supposed to tokenize
    public void assertTokenizes(final String input,
                                final Token[] expected) {
        final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
        try {
            final List<Token> received = tokenizer.tokenize();
            assertTrue("Expected tokenization failure, got: " + received,
                       expected != null);
            assertArrayEquals(expected,
                              received.toArray(new Token[received.size()]));
        } catch (final TokenizerException e) {
            assertTrue(("Unexpected tokenization failure for \"" +
                        input + "\": " + e.getMessage()),
                       expected == null);
        }
    }

    @Test
    public void testTokenizeSingleDigitInteger() {
        assertTokenizes("0",
                        new Token[]{ new NumberToken(0) });
    }
    
    @Test
    public void testTokenizeInteger() {
        assertTokenizes("123",
                        new Token[]{ new NumberToken(123) });
    }

    @Test
    public void testTokenizeIntegerLeadingWhitespace() {
        assertTokenizes("  123",
                        new Token[]{ new NumberToken(123) });
    }

    @Test
    public void testTokenizeIntegerTrailingWhitespace() {
        assertTokenizes("123   ",
                        new Token[]{ new NumberToken(123) });
    }

    @Test
    public void testTokenizeIntegerLeadingAndTrailingWhitespace() {
        assertTokenizes("  123  ",
                        new Token[]{ new NumberToken(123) });
    }

    @Test
    public void testTokenizeVariableSingleLetter() {
        assertTokenizes("x",
                        new Token[]{ new NameToken("x") });
    }

    @Test
    public void testTokenizeVariableMultiLetter() {
        assertTokenizes("foo",
                        new Token[]{ new NameToken("foo") });
    }

    @Test
    public void testTokenizeVariableStartsWithIf() {
        assertTokenizes("ifx",
                        new Token[]{ new NameToken("ifx") });
    }

    @Test
    public void testTokenizeIf() {
        assertTokenizes("if",
                        new Token[]{ new IfToken() });
    }

    @Test
    public void testTokenizeSingleChars() {
        assertTokenizes("+-*/(){}",
                        new Token[]{ new PlusToken(),
                                     new MinusToken(),
                                     new MultToken(),
                                     new DivToken(),
                                     new LeftParenToken(),
                                     new RightParenToken(),
                                     new LeftCurlyToken(),
                                     new RightCurlyToken() });
    }

    @Test
    public void testTokenizeIntermixed() {
        assertTokenizes("*if+foo-",
                        new Token[]{ new MultToken(),
                                     new IfToken(),
                                     new PlusToken(),
                                     new NameToken("foo"),
                                     new MinusToken() });
    }

    @Test
    public void testTokenizeElse() {
        assertTokenizes("else",
                        new Token[]{ new ElseToken() });
    }

    @Test
    public void testTokenizeIfExpression() {
        assertTokenizes("if (1) { x } else { y }",
                        new Token[]{ new IfToken(),
                                     new LeftParenToken(),
                                     new NumberToken(1),
                                     new RightParenToken(),
                                     new LeftCurlyToken(),
                                     new NameToken("x"),
                                     new RightCurlyToken(),
                                     new ElseToken(),
                                     new LeftCurlyToken(),
                                     new NameToken("y"),
                                     new RightCurlyToken() });
    }
}
