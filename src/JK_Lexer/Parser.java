package JK_Lexer;

import java.util.Map;
import java.util.HashMap;

public class Parser {
    // begin static variables
    private static final Map<Token, Op> ADDITIVE_OP_MAP =
        new HashMap<Token, Op>() {{
            put(new PlusToken(), new PlusOp());
            put(new MinusToken(), new MinusOp());
        }};
    private static final Map<Token, Op> MULTIPLICATIVE_OP_MAP =
        new HashMap<Token, Op>() {{
            put(new MultToken(), new MultOp());
            put(new DivToken(), new DivOp());
        }};
    // end static variables
    
    // begin instance variables
    private final Token[] tokens;
    // end instance variables

    public Parser(final Token[] tokens) {
        this.tokens = tokens;
    }

    private Token getToken(final int pos) throws ParserException {
        assert(pos >= 0);
        if (pos < tokens.length) {
            return tokens[pos];
        } else {
            throw new ParserException("No token at position " + pos);
        }
    }
    
    private class ParseResult<A> {
        public final A result;
        public final int tokenPos;
        public ParseResult(final A result,
                           final int tokenPos) {
            this.result = result;
            this.tokenPos = tokenPos;
        }
    } // ParseResult

    // handles something (op something)*
    private abstract class ParseBinop {
        private final Map<Token, Op> opMap;
        public ParseBinop(final Map<Token, Op> opMap) {
            this.opMap = opMap;
        }

        public abstract ParseResult<Exp> parseSomething(final int startPos) throws ParserException;

        public ParseResult<Exp> parse(final int startPos) throws ParserException {
            int pos = startPos;
            ParseResult<Exp> finalResult = parseSomething(pos);
            if (finalResult == null) {
                return null;
            }

            ParseResult<Exp> currentResult = null;
            while (finalResult.tokenPos < tokens.length) {
                final Op op = opMap.get(getToken(finalResult.tokenPos));
                if (op != null) {
                    // we have an op.  We MUST have a right.
                    final ParseResult<Exp> right = parseSomething(finalResult.tokenPos + 1);
                    finalResult = new ParseResult<Exp>(new BinopExp(finalResult.result,
                                                                    op,
                                                                    right.result),
                                                       right.tokenPos);
                } else {
                    // we don't have an op.  return whatever we have
                    return finalResult;
                }
            }

            return finalResult;
        } // parse
    } // ParseBinop

    private class ParseAdditive extends ParseBinop {
        public ParseAdditive() {
            super(ADDITIVE_OP_MAP);
        }

        public ParseResult<Exp> parseSomething(final int startPos) throws ParserException {
            return parseMultiplicative(startPos);
        }
    }

    private class ParseMultiplicative extends ParseBinop {
        public ParseMultiplicative() {
            super(MULTIPLICATIVE_OP_MAP);
        }

        public ParseResult<Exp> parseSomething(final int startPos) throws ParserException {
            return parsePrimary(startPos);
        }
    }

    public Exp parseExp() throws ParserException {
        final ParseResult<Exp> result = parseExp(0);
        if (result.tokenPos == tokens.length) {
            return result.result;
        } else {
            throw new ParserException("Extra tokens starting at " + result.tokenPos);
        }
    }
    
    private ParseResult<Exp> parseExp(final int startPos) throws ParserException {
        return parseAdditive(startPos);
    }
    
    private ParseResult<Exp> parseAdditive(final int startPos) throws ParserException {
        return new ParseAdditive().parse(startPos);
    }

    private ParseResult<Exp> parseMultiplicative(final int startPos) throws ParserException {
        return new ParseMultiplicative().parse(startPos);
    }

    private ParseResult<Exp> parseNumber(final int startPos) throws ParserException {
        final Token current = getToken(startPos);
        if (current instanceof NumberToken) {
            return new ParseResult<Exp>(new NumberExp(((NumberToken)current).number),
                                        startPos + 1);
        } else {
            return null;
        }
    }

    private void assertTokenAtPos(final Token token, final int pos) throws ParserException {
        if (!getToken(pos).equals(token)) {
            throw new ParserException("Expected " + token.toString() + " at pos " + pos);
        }
    }
    
    private ParseResult<Exp> parsePrimary(final int startPos) throws ParserException {
        final Token current = getToken(startPos);
        Exp resultExp;
        int resultPos;

        if (current instanceof NumberToken) { //INT
            resultExp = new NumberExp(((NumberToken)current).number);
            resultPos = startPos + 1;
        } else if (current instanceof NameToken && (tokens.length <= startPos+1 || !(getToken(startPos+1) instanceof PeriodToken))) { //VARIABLE
            resultExp = new VariableExp(((NameToken)current).name);
            resultPos = startPos + 1;
        } else if (current instanceof QuoteToken) { //STRING
        	Token next = getToken(startPos+1); 
        	resultExp = new StringExp(((QuotedStringToken)next).string);
        	assertTokenAtPos(new QuoteToken(), startPos+2); 
        	resultPos = startPos+3; 
        } else if(current instanceof ThisToken) {    //this.var
        	assertTokenAtPos(new PeriodToken(), startPos+1); 
        	Token next=getToken(startPos+2); 
        	resultExp = new ThisExp(((NameToken)next).name);
        	resultPos = startPos+3; 
        } else if(current instanceof PrintToken) {    // println(var);
        	assertTokenAtPos(new LeftParenToken(), startPos+1); 
        	Token next = getToken(startPos+2); 
        	assertTokenAtPos(new RightParenToken(), startPos+3); 
        	assertTokenAtPos(new SemicolonToken(), startPos+4); 
        	resultExp = new PrintExp(((NameToken)next).name);
        	resultPos = startPos+5; 
        } else if(current instanceof NameToken && getToken(startPos+1) instanceof PeriodToken) {    //call method
        	String first = ((NameToken)current).name; 
        	assertTokenAtPos(new PeriodToken(), startPos+1); 
        	String methodname = ((NameToken)getToken(startPos+2)).name; 
        	assertTokenAtPos(new LeftParenToken(), startPos+3); 
        	String second = ((NameToken)getToken(startPos+4)).name; 
        	assertTokenAtPos(new RightParenToken(), startPos+5); 
        	resultExp= new CallMethodExp(first, methodname, second);
        	resultPos= startPos+6; 
        } else if(current instanceof NewToken) {
        	assertTokenAtPos(new PeriodToken(), startPos+1); 
        	String classname=((NameToken)getToken(startPos+2)).name;
        	assertTokenAtPos(new LeftParenToken(), startPos+3); 
        	String variable = ((NameToken)getToken(startPos+4)).name; 
        	assertTokenAtPos(new RightParenToken(), startPos+5); 
        	resultExp= new NewExp(classname, variable); 
        	resultPos= startPos+6; 
        }
        else if (current instanceof LeftParenToken) { //(EXP)
            final ParseResult<Exp> nested = parseExp(startPos + 1);
            assertTokenAtPos(new RightParenToken(), nested.tokenPos);
            resultExp = nested.result;
            resultPos = nested.tokenPos + 1;
        }  else { //ERROR
            throw new ParserException("Expected primary at " + startPos);
        }
        
        return new ParseResult<Exp>(resultExp, resultPos);
    } // parsePrimary
} // Parser