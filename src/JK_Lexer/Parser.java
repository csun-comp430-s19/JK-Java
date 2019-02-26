package JK_Lexer;

import java.util.Map;
import java.util.ArrayList;
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
    
    public ClassDefExp parseProgram() throws ParserException{
    	return parseClassDef(0);
    }
    
    private ClassDefExp parseClassDef(final int startPos) throws ParserException {
    	final Token current = getToken(startPos);
    	ArrayList <MethodDefExp> methodlist = new ArrayList<MethodDefExp>();
    	ArrayList <InstanceDecExp> memberlist = new ArrayList<InstanceDecExp>();
        Exp resultExp;
        int resultPos;
    	
        if ((current instanceof PublicToken) || (current instanceof PrivateToken)) { //(EXP)
        	Modifier modifier = parseModifier(current, startPos);
        	assertTokenAtPos(new ClassToken(), startPos+1);
        	String classname=((NameToken)getToken(startPos+2)).name;
        	assertTokenAtPos(new LeftCurlyToken(), startPos+3);
        	
        	Token currentToken = getToken(startPos+4);
        	int currentPos = startPos+4;
        	while(!(currentToken instanceof RightCurlyToken)) {
        		Modifier mod = parseModifier(getToken(currentPos), currentPos);
        		Type type = parseType(getToken(currentPos+1), currentPos+1);
        		String name = getToken(currentPos+2).toString();

        		if(getToken(currentPos+3) instanceof LeftParenToken) {
        			//method dec
        			int paramPos = currentPos+4;
        			ArrayList<VariableDecExp> paramlist = new ArrayList<VariableDecExp>();
        			while(!(getToken(paramPos) instanceof RightParenToken)) {
        				paramlist.add(new VariableDecExp(parseType(getToken(paramPos), paramPos), new VariableExp(getToken(paramPos+1).toString())));
        				paramPos += 2;
        			}
        			assertTokenAtPos(new LeftCurlyToken(),paramPos+1);
        			int stmtPos = paramPos+2;
        			int stmtStart = paramPos+2;
        			ArrayList<Token> stmtlist = new ArrayList<Token>();
        			while(!(getToken(stmtPos) instanceof RightCurlyToken)) {
        				stmtlist.add(getToken(stmtPos));
        				stmtPos++;
        			}
        			assertTokenAtPos(new RightCurlyToken(), stmtPos);
        			ArrayList<Statement> block = parseStatements(stmtlist, stmtStart);
        			methodlist.add(new MethodDefExp(mod, type, name, paramlist, block));
        			
        			currentPos = stmtPos+1;
        			currentToken = getToken(currentPos);
        		}
        		else {
        			//member var dec
        			memberlist.add(new InstanceDecExp(mod, new VariableDecExp(type, new VariableExp(name))));
        			assertTokenAtPos(new SemicolonToken(), currentPos+3);
        			currentPos += 4;
        			currentToken = getToken(currentPos);
        		}
        			
        	}

        	return new ClassDefExp(modifier, classname, memberlist, methodlist);
        	
        }  else { //ERROR
            throw new ParserException("Expected Class Declaration at " + startPos);
        }
    }
    
    private Modifier parseModifier(Token m, int startPos) throws ParserException{
    	//returns null if the token is not a modifier
    	if(m instanceof PublicToken) {
    		return new PublicModifier();
    	}
    	else if(m instanceof PrivateToken) {
    		return new PrivateModifier();
    	}
    	else {
    		throw new ParserException("Expected Modifier at " + startPos);
    	}
    }
    
    private Type parseType(Token m, int startPos) throws ParserException{
    	//returns null if the token is not a modifier
    	if(m instanceof IntToken) {
    		return new IntType();
    	}
    	else if(m instanceof StringToken) {
    		return new StringType();
    	}
    	else if(m instanceof VoidToken) {
    		return new VoidType();
    	}
    	else {
    		throw new ParserException("Expected Type at " + startPos);
    	}
    }

    private ArrayList<Statement> parseStatements(ArrayList<Token> tokenlist, int startPos) throws ParserException{
    	ArrayList<Statement> block = new ArrayList<Statement>();
    	for(int i = 0; i < tokenlist.size(); i++) {
    		int pos = startPos + i;
    		ArrayList<Token> statement = new ArrayList<Token>();
    		while(!(tokenlist.get(i) instanceof SemicolonToken)) {
    			statement.add(tokenlist.get(i));
    			i++;
    		}
    		i++;
    		if(statement.get(0) instanceof ReturnToken) {
     			if(statement.size()>1)
     				block.add(new ReturnStmt((Exp) parseExp(pos+1).result));
     			else {
     				block.add(new ReturnStmt());
     			}
    		 }
    		else if((statement.get(0) instanceof NameToken) && (statement.get(1) instanceof AssignmentToken)) {
    			block.add(new AssignmentStmt(new VariableExp(statement.get(0).toString()), (Exp) parseExp(pos+2).result));
    		}
    		else if((parseType(statement.get(0), startPos) instanceof Type) && (statement.get(1) instanceof NameToken)) {
				block.add(new VariableDecExp(parseType(statement.get(0),i), new VariableExp(statement.get(1).toString())));
    		}
    		
    	}
    	return block;
    }
    
    private VariableDecExp parseVariableDecExp(ArrayList<Token> tokenlist, int startPos) throws ParserException{
    	Type t = parseType(tokenlist.get(0), startPos);
    	if(!(tokenlist.get(1) instanceof NameToken)) {
    		throw new ParserException("Expected Variable Name at " + startPos+1);
    	}
    	else {
    		return new VariableDecExp(t, new VariableExp(tokenlist.get(1).toString()));
    	}
    }
    
    
} // Parser