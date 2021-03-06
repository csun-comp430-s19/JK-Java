package JK_Lexer;

import java.util.Map;

import JK_Lexer.Parser.ParseResult;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
	// begin static variables
	private static final Map<Token, Op> ADDITIVE_OP_MAP = new HashMap<Token, Op>() {
		{
			put(new PlusToken(), new PlusOp());
			put(new MinusToken(), new MinusOp());
		}
	};
	private static final Map<Token, Op> MULTIPLICATIVE_OP_MAP = new HashMap<Token, Op>() {
		{
			put(new MultToken(), new MultOp());
			put(new DivToken(), new DivOp());
		}
	};
	// end static variables

	// begin instance variables
	private final Token[] tokens;
	// end instance variables

	public Parser(final Token[] tokens) {
		this.tokens = tokens;
	}

	private Token getToken(final int pos) throws ParserException {
		assert (pos >= 0);
		if (pos < tokens.length) {
			return tokens[pos];
		} else {
			throw new ParserException("No token at position " + pos);
		}
	}

	public class ParseResult<A> {
		public final A result;
		public final int tokenPos;

		public ParseResult(final A result, final int tokenPos) {
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
					// we have an op. We MUST have a right.
					final ParseResult<Exp> right = parseSomething(finalResult.tokenPos + 1);
					finalResult = new ParseResult<Exp>(new BinopExp(finalResult.result, op, right.result),
							right.tokenPos);
				} else {
					// we don't have an op. return whatever we have
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

	private void assertTokenAtPos(final Token token, final int pos) throws ParserException {
		if (!getToken(pos).equals(token)) {
			throw new ParserException("Expected " + token.toString() + " at pos " + pos);
		}
	}

	private ParseResult<Exp> parsePrimary(final int startPos) throws ParserException {
		final Token current = getToken(startPos);
		Exp resultExp;
		int resultPos;

		if (current instanceof NumberToken) { // INT
			resultExp = new NumberExp(((NumberToken) current).number);
			resultPos = startPos + 1;
		} else if (current instanceof NameToken
				&& (tokens.length <= startPos + 1 || !(getToken(startPos + 1) instanceof PeriodToken))) { // VARIABLE
			resultExp = new VariableExp(((NameToken) current).name);
			resultPos = startPos + 1;
		} else if (current instanceof QuotedStringToken) { // STRING
			resultExp = new StringExp(((QuotedStringToken) current).string);
			resultPos = startPos + 1;
		} else if (current instanceof ThisToken) { // this.var
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			final ParseResult<Exp> variable = parseExp(startPos + 2);
			resultExp = new ThisExp(variable.result);
			resultPos = startPos + 3;
		} /*else if (current instanceof PrintToken) { // println(var);
			assertTokenAtPos(new LeftParenToken(), startPos + 1);
			final ParseResult<Exp> expression = parseExp(startPos + 2);
			assertTokenAtPos(new RightParenToken(), startPos + 3);
			resultExp = new PrintExp(expression.result);
			resultPos = startPos + 4;
		}*/ else if (current instanceof NameToken && getToken(startPos + 1) instanceof PeriodToken) { // call method
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			final ParseResult<Exp> methodname = parseExp(startPos + 2);
			assertTokenAtPos(new LeftParenToken(), startPos + 3);
			ArrayList<VariableExp> params = new ArrayList<VariableExp>();
			int i = 4;
			while(getToken(startPos + i) instanceof NameToken) {
				params.add((VariableExp)parseExp(startPos + i).result);
				if(getToken(startPos + i + 1) instanceof RightParenToken) {
					i+= 1;
					break;
				}else {
					assertTokenAtPos(new CommaToken(), startPos + i + 1);
					i += 2;
				}
			}
			assertTokenAtPos(new RightParenToken(), startPos + i);
			resultExp = new CallMethodExp(new VariableExp(((NameToken) current).name), (VariableExp)methodname.result,
					params);
			resultPos = startPos + i + 1;
		} else if (current instanceof NewToken && getToken(startPos+3) instanceof LeftParenToken) {    //NEW EXP FOR NON GENERIC CLASSES
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			final ParseResult<Exp> classname = parseExp(startPos + 2);
			assertTokenAtPos(new LeftParenToken(), startPos + 3);
			int currentPos = startPos+4; 
			ArrayList<VariableExp> varList = new ArrayList<VariableExp>(); 
			while(!(getToken(currentPos) instanceof RightParenToken)) {
				if(getToken(currentPos) instanceof NameToken) {
					varList.add((VariableExp)(parseExp(currentPos).result));
					currentPos++; 
				} else if(getToken(currentPos) instanceof CommaToken && getToken(currentPos-1) instanceof NameToken){
					currentPos++; 
				} else {
					throw new ParserException("Expected name or comma token at pos: "+ currentPos); 
				}
			}
			assertTokenAtPos(new RightParenToken(), currentPos);
			resultExp = new NewExp((VariableExp)(classname.result), varList);
			resultPos = currentPos+1;
		} else if(current instanceof NewToken && getToken(startPos+3) instanceof LessThanToken){	// NEW EXP FOR GENERIC CLASSES
			assertTokenAtPos(new PeriodToken(), startPos + 1);
			final ParseResult<Exp> classname = parseExp(startPos+2); 
			assertTokenAtPos(new LessThanToken(), startPos+3); 
			int newPos = startPos+4; 
			ArrayList<Type> typeList = new ArrayList<Type>(); 
			ArrayList<VariableExp> varList = new ArrayList<VariableExp>(); 
			while(!(getToken(newPos) instanceof GreaterThanToken)) {
				if(getToken(newPos) instanceof IntToken || getToken(newPos) instanceof StringToken || getToken(newPos) instanceof VoidToken || getToken(newPos) instanceof NameToken) {
					typeList.add(parseType(newPos).result);
					newPos++; 
				} else if(getToken(newPos) instanceof CommaToken && (getToken(newPos-1) instanceof IntToken || getToken(newPos-1) instanceof StringToken || getToken(newPos-1) instanceof VoidToken ||getToken(newPos-1) instanceof NameToken)) {
					newPos++; 
				} else {
					throw new ParserException("Expected CommaToken or any TypeToken at pos: "+ newPos); 
				}
			}
			assertTokenAtPos(new GreaterThanToken(), newPos); 
			assertTokenAtPos(new LeftParenToken(), newPos+1); 
			int newPos2 = newPos+2; 
			while(!(getToken(newPos2) instanceof RightParenToken)) {
				if(getToken(newPos2) instanceof NameToken) {
					varList.add((VariableExp)parseExp(newPos2).result);
					newPos2++; 
				} else if(getToken(newPos2) instanceof CommaToken && getToken(newPos2-1) instanceof NameToken) {
					newPos2++; 
				} else {
					throw new ParserException("Expected CommaToken or any NameToken at pos:"+ newPos2); 
				}
			}
			resultExp = new GenericNewExp(((VariableExp)classname.result), typeList, varList);
			resultPos = newPos2+1; 
		} else if (current instanceof LeftParenToken) { // (EXP)
			final ParseResult<Exp> nested = parseExp(startPos + 1);
			assertTokenAtPos(new RightParenToken(), nested.tokenPos);
			resultExp = nested.result;
			resultPos = nested.tokenPos + 1;
		} else { // ERROR
			throw new ParserException("Expected primary at " + startPos);
		}

		return new ParseResult<Exp>(resultExp, resultPos);
	} // parsePrimary

	public Statement parseSingleStatement() throws ParserException {
		return parseSingleStatement(0).result;
	}
	
	public ConstructorDef parseConstructorDef(String classname) throws ParserException {
		return parseConstructorDef(0, classname).result;
	}
	
	public ClassDefExp parseClassDef() throws ParserException {
		return parseClassDef(0).result;
	}
	public ClassDefExp parseGenericClassDef() throws ParserException {
		return parseGenericClassDef(0).result;
	}

	public Program parseProgram() throws ParserException {
		return parseProgram(0).result;
	}

	private ParseResult<ClassDefExp> parseClassDef(final int startPos) throws ParserException {
		final Token current = getToken(startPos);
		ArrayList<ConstructorDef> constructorlist = new ArrayList<ConstructorDef>();
		ArrayList<MethodDefExp> methodlist = new ArrayList<MethodDefExp>();
		ArrayList<InstanceDecExp> memberlist = new ArrayList<InstanceDecExp>();
		boolean extending = false;
		String extendingName = "";

		if ((current instanceof PublicToken) || (current instanceof PrivateToken)) { 
			ParseResult<Modifier> modifierResult = parseModifier(startPos);
			Modifier modifier = modifierResult.result;
			assertTokenAtPos(new ClassToken(), startPos + 1);
			if (!(getToken(startPos + 2) instanceof NameToken)) {
				throw new ParserException("Expected Valid Name of Class at: " + startPos + 2);
			}
			String classname = ((NameToken) getToken(startPos + 2)).name;
			Token currentToken;
			int currentPos;
			if(getToken(startPos + 3) instanceof ExtendsToken) {
				extending = true;
				if (!(getToken(startPos + 4) instanceof NameToken)) {
					throw new ParserException("Expected Valid Name of Class at: " + startPos + 4);
				}
				extendingName = ((NameToken) getToken(startPos + 4)).name;
				assertTokenAtPos(new LeftCurlyToken(), startPos + 5);
				currentToken = getToken(startPos + 6);
				currentPos = startPos + 6;
			}else {
			assertTokenAtPos(new LeftCurlyToken(), startPos + 3);
			currentToken = getToken(startPos + 4);
			currentPos = startPos + 4;
			}
			while (!(currentToken instanceof RightCurlyToken)) {
				ParseResult<Modifier> mod = parseModifier(currentPos);
				if (getToken(currentPos + 1) instanceof NameToken && getToken(currentPos + 2) instanceof LeftParenToken) {// constructor
					ParseResult<ConstructorDef> constructorDef = parseConstructorDef(currentPos, classname);
					currentPos = constructorDef.tokenPos;
					constructorlist.add(constructorDef.result);
				}else if (getToken(currentPos+2) instanceof NameToken) {
					ParseResult<Type> type = parseType(currentPos + 1);
					if (!(getToken(currentPos + 2) instanceof NameToken)) {
						throw new ParserException(
								"Expected Valid Name of Member variable or method at: " + currentPos + 2);
					}
					String name = getToken(currentPos + 2).toString();
					
					if (getToken(currentPos + 3) instanceof LeftParenToken) {
						// method dec
						ParseResult<MethodDefExp> methodDefExp = parseMethodDefExp(currentPos);
						currentPos = methodDefExp.tokenPos;
						methodlist.add(methodDefExp.result);
					} else {
						// member var dec
						assertTokenAtPos(new SemicolonToken(), currentPos + 3);
						memberlist.add(
								new InstanceDecExp(mod.result, new VariableDecExp(type.result, new VariableExp(name))));
						currentPos += 4;
					}
				} else {
					throw new ParserException("Unexpected " + getToken(currentPos + 1).toString() + " at: " + currentPos+1);
				}
				currentToken = getToken(currentPos);
			}

			return new ParseResult<ClassDefExp>(
					new ClassDefExp(modifier, classname, constructorlist, memberlist, methodlist, extending, extendingName), currentPos + 1);

		} else { // ERROR
			throw new ParserException("Expected Modifier for Class Declaration at: " + startPos);
		}
	}
	
	private ParseResult<GenericClassDefinition> parseGenericClassDef(final int startPos) throws ParserException{
		final Token current = getToken(startPos); 
		ArrayList<ConstructorDef> constructorlist = new ArrayList<ConstructorDef>();
		ArrayList<MethodDefExp> methodlist = new ArrayList<MethodDefExp>();
		ArrayList<InstanceDecExp> memberlist = new ArrayList<InstanceDecExp>();
		boolean extending = false;
		String extendingName = "";
		ArrayList<VariableExp> genericList = new ArrayList<VariableExp>(); 
		
		if((current instanceof PublicToken) || (current instanceof PrivateToken)) {
			ParseResult<Modifier> modifierResult = parseModifier(startPos); 
			Modifier modifier = modifierResult.result; 
			assertTokenAtPos(new ClassToken(), startPos+1); 
			if (!(getToken(startPos + 2) instanceof NameToken)) {
				throw new ParserException("Expected Valid Name of Class at: " + startPos + 2);
			}
			String classname = ((NameToken)getToken(startPos+2)).name; 
			assertTokenAtPos(new LessThanToken(), startPos+3);
			int tempPos = startPos+4; 
			while(!(getToken(tempPos) instanceof GreaterThanToken)) {
				if(getToken(tempPos) instanceof NameToken) {
					genericList.add(((VariableExp)parseExp(tempPos).result)); 
					tempPos++; 
				} else if(getToken(tempPos) instanceof CommaToken && getToken(tempPos-1) instanceof NameToken) {
					tempPos++; 
				} else {
					throw new ParserException("NameToken expected in generic class definition at pos: " +tempPos); 
				}
			}
			assertTokenAtPos(new GreaterThanToken(), tempPos); 
			Token currentToken; 
			int currentPos; 
			if(getToken(tempPos+1) instanceof ExtendsToken) {
				extending = true; 
				if(!(getToken(tempPos+2) instanceof NameToken)) {
					throw new ParserException("Expected valid name of class at pos: "+  (tempPos+2)); 
				}
				extendingName = ((NameToken) getToken(tempPos+2)).name; 
				assertTokenAtPos(new LeftCurlyToken(), tempPos+3);
				currentToken = getToken(tempPos+4); 
				currentPos = tempPos+4; 
			} else {
				assertTokenAtPos(new LeftCurlyToken(), tempPos+1); 
				currentToken= getToken(tempPos+2); 
				currentPos = tempPos+2; 
			}
			while(!(currentToken instanceof RightCurlyToken)) {
				ParseResult<Modifier> mod = parseModifier(currentPos);
				if(getToken(currentPos+1) instanceof NameToken && getToken(currentPos+2) instanceof LeftParenToken) {
					ParseResult<ConstructorDef> constructorDef = parseConstructorDef(currentPos, classname); 
					currentPos = constructorDef.tokenPos; 
					constructorlist.add(constructorDef.result);
				} else if(getToken(currentPos+2) instanceof NameToken) {
					ParseResult<Type> type = parseType(currentPos+1); 
					if(!(getToken(currentPos+2) instanceof NameToken)) {
						throw new ParserException("Expected Valid Name of Member Variable or method at: "+(currentPos+2));
					}
					String name = getToken(currentPos+2).toString();
					
					if(getToken(currentPos+3) instanceof LeftParenToken) {
						//method dec
						ParseResult<MethodDefExp> methodDefExp = parseMethodDefExp(currentPos); 
						currentPos = methodDefExp.tokenPos; 
						methodlist.add(methodDefExp.result);
					} else {
						//member var dec
						assertTokenAtPos(new SemicolonToken(), currentPos+3); 
						memberlist.add(new InstanceDecExp(mod.result, new VariableDecExp(type.result, new VariableExp(name))));
						currentPos+=4; 
					} 
				} else {
					throw new ParserException("Unexpected " + getToken(currentPos+1).toString()+ " at: "+ (currentPos+1)); 
				}
				currentToken = getToken(currentPos); 
			}
			return new ParseResult<GenericClassDefinition>(
						new GenericClassDefinition(modifier, classname, constructorlist, memberlist, methodlist, extending, extendingName, genericList), currentPos+1);
		} else {
			throw new ParserException("Expected Modifier for Class Declaration at: "+ startPos); 
		}
		
	}
	
	private ParseResult<ConstructorDef> parseConstructorDef(int startPos, String className) throws ParserException {
		int pos = startPos;
		ParseResult<Modifier> mod = parseModifier(pos);
		pos = mod.tokenPos;
		if (!(getToken(pos) instanceof NameToken)) {
			throw new ParserException("Expected Constructor Name at: " + pos);
		}
		String name = ((NameToken) getToken(pos)).name;
		if (!name.equals(className)) {
			throw new ParserException("Constructor needs to match class name: " + pos);
		}
		pos++;
		assertTokenAtPos(new LeftParenToken(), pos);
		pos++;
		ArrayList<VariableDecExp> paramlist = new ArrayList<VariableDecExp>();
		while (!(getToken(pos) instanceof RightParenToken)) {
			Token currentToken = getToken(pos);
			ParseResult<Type> paramType = parseType(pos);
			pos = paramType.tokenPos;
			currentToken = getToken(pos);
			if (!(currentToken instanceof NameToken)) {
				throw new ParserException("Expected Parameter Name at: " + pos);
			}
			VariableExp paramName = new VariableExp(currentToken.toString());
			pos++;
			paramlist.add(new VariableDecExp(paramType.result, paramName));
			if ((!(getToken(pos) instanceof RightParenToken))) {
				if ((!(getToken(pos) instanceof CommaToken))) {
					throw new ParserException("Expected comma at: " + pos);
				}
			}
			if (getToken(pos) instanceof CommaToken) {
				pos++;
			}
		}
		assertTokenAtPos(new LeftCurlyToken(), pos + 1);
		pos += 2;
		ParseResult<ArrayList<Statement>> block = parseConstructorStatements(pos);
		pos = block.tokenPos;
		assertTokenAtPos(new RightCurlyToken(), pos);
		ConstructorDef method = new ConstructorDef(mod.result, name, paramlist, block.result);
		return new ParseResult<ConstructorDef>(method, pos + 1);
	}
	
	private ParseResult<MethodDefExp> parseMethodDefExp(int startPos) throws ParserException {
		int pos = startPos;
		ParseResult<Modifier> mod = parseModifier(pos);
		ParseResult<Type> type = parseType(mod.tokenPos);
		pos = type.tokenPos;
		if (!(getToken(pos) instanceof NameToken)) {
			throw new ParserException("Expected Method Name at: " + pos);
		}
		String name = getToken(type.tokenPos).toString();
		pos++;
		assertTokenAtPos(new LeftParenToken(), pos);
		pos++;
		ArrayList<VariableDecExp> paramlist = new ArrayList<VariableDecExp>();
		while (!(getToken(pos) instanceof RightParenToken)) {
			Token currentToken = getToken(pos);
			ParseResult<Type> paramType = parseType(pos);
			pos = paramType.tokenPos;
			currentToken = getToken(pos);
			if (!(currentToken instanceof NameToken)) {
				throw new ParserException("Expected Parameter Name at: " + pos);
			}
			VariableExp paramName = new VariableExp(currentToken.toString());
			pos++;
			paramlist.add(new VariableDecExp(paramType.result, paramName));
			if ((!(getToken(pos) instanceof RightParenToken))) {
				if ((!(getToken(pos) instanceof CommaToken))) {
					throw new ParserException("Expected comma at: " + pos);
				}
			}
			if (getToken(pos) instanceof CommaToken) {
				pos++;
			}
		}
		assertTokenAtPos(new LeftCurlyToken(), pos + 1);
		pos += 2;
		ParseResult<ArrayList<Statement>> block = parseStatements(pos);
		pos = block.tokenPos;
		assertTokenAtPos(new RightCurlyToken(), pos);
		MethodDefExp method = new MethodDefExp(mod.result, type.result, name, paramlist, block.result);
		return new ParseResult<MethodDefExp>(method, pos + 1);
	}

	private ParseResult<Program> parseProgram(final int startPos) throws ParserException {
		Token currentToken = getToken(startPos);
		int pos = startPos;
		ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
		ArrayList<Statement> statementList = new ArrayList<Statement>();
		while (pos < tokens.length) {
			if ((currentToken instanceof PublicToken || currentToken instanceof PrivateToken)
					&& getToken(pos + 1) instanceof ClassToken) {
				// ClassDecExp
				if(getToken(pos+3) instanceof LessThanToken) {
					ParseResult<GenericClassDefinition> gClassDef = parseGenericClassDef(pos); 
					classDefList.add(gClassDef.result); 
					pos = gClassDef.tokenPos; 
				} else {
					ParseResult<ClassDefExp> classDef = parseClassDef(pos);
					classDefList.add(classDef.result);
					pos = classDef.tokenPos;
				}
			} else {
				// Statement
				ParseResult<Statement> statement = parseSingleStatement(pos);
				statementList.add(statement.result);
				pos = statement.tokenPos;
			}

		}
		return new ParseResult<Program>(new Program(statementList, classDefList), pos + 1);
	}

	private ParseResult<Modifier> parseModifier(int startPos) throws ParserException {
		Token m = getToken(startPos);
		// returns null if the token is not a modifier
		if (m instanceof PublicToken) {
			return new ParseResult<Modifier>(new PublicModifier(), startPos + 1);
		} else if (m instanceof PrivateToken) {
			return new ParseResult<Modifier>(new PrivateModifier(), startPos + 1);
		} else {
			throw new ParserException("Expected Modifier at " + startPos);
		}
	}

	private ParseResult<Type> parseType(int startPos) throws ParserException {
		// returns null if the token is not a modifier
		Token m = getToken(startPos);

		if (m instanceof IntToken) {
			return new ParseResult<Type>(new IntType(), startPos + 1);
		} else if (m instanceof StringToken) {
			return new ParseResult<Type>(new StringType(), startPos + 1);
		} else if (m instanceof VoidToken) {
			return new ParseResult<Type>(new VoidType(), startPos + 1);
		} else if(m instanceof NameToken && getToken(startPos+1) instanceof LessThanToken){
			ArrayList<Type> typeList = new ArrayList<Type>(); 
			int newPos= startPos+2; 
			while(!(getToken(newPos) instanceof GreaterThanToken)) {
				if(getToken(newPos) instanceof IntToken || getToken(newPos) instanceof StringToken ||getToken(newPos) instanceof VoidToken|| getToken(newPos) instanceof NameToken) {
					typeList.add(parseType(newPos).result);
					newPos++; 
				} else if(getToken(newPos) instanceof CommaToken && (getToken(newPos-1) instanceof IntToken) || getToken(newPos-1) instanceof StringToken || getToken(newPos-1) instanceof VoidToken || getToken(newPos-1) instanceof NameToken) {
					newPos++; 
				} else {
					throw new ParserException("Invalid Token, expected TypeToken or CommaToken at pos: " + newPos);
				}
			}
			return new ParseResult<Type>(new GenericObjectType(((NameToken)m).name, typeList), newPos+1);
		} else if (m instanceof NameToken) {
			return new ParseResult<Type>(new ObjectType(((NameToken) m).name), startPos + 1);
		} else {
			throw new ParserException("Expected Type at " + startPos);
		}
	}

	private ParseResult<ArrayList<Statement>> parseStatements(int startPos) throws ParserException {
		ArrayList<Statement> block = new ArrayList<Statement>();
		int i = startPos;
		while (!(getToken(i) instanceof RightCurlyToken)) {
			ParseResult<Statement> statementResult = parseSingleStatement(i);
			block.add(statementResult.result);
			i = statementResult.tokenPos;
		}
		return new ParseResult<ArrayList<Statement>>(block, i);
	}

	private ParseResult<ArrayList<Statement>> parseConstructorStatements(int startPos) throws ParserException {
		ArrayList<Statement> block = new ArrayList<Statement>();
		int i = startPos;
		while (!(getToken(i) instanceof RightCurlyToken)) {
			ParseResult<Statement> statementResult = parseSingleConstructorStatement(i);
			block.add(statementResult.result);
			i = statementResult.tokenPos;
		}
		return new ParseResult<ArrayList<Statement>>(block, i);
	}

	private ParseResult<Statement> parseSingleStatement(int startPos) throws ParserException {
		int pos = startPos;
		ParseResult<Statement> result = null;
		if (!(getToken(pos) instanceof SemicolonToken)) {
			if (getToken(pos) instanceof ReturnToken) {
				if (getToken(pos + 1) instanceof SemicolonToken)
					result = new ParseResult<Statement>(new ReturnStmt(), pos + 1);
				else {
					ParseResult<Exp> expResult = parseExp(pos + 1);
					result = new ParseResult<Statement>(new ReturnStmt((Exp) parseExp(pos + 1).result),
							expResult.tokenPos);
				}
			} else if ((getToken(pos) instanceof NameToken) && (getToken(pos + 1) instanceof AssignmentToken)) {
				ParseResult<AssignmentStmt> assignmentResult = parseAssignment(pos);
				result = new ParseResult<Statement>(assignmentResult.result, assignmentResult.tokenPos);
			} else if((getToken(pos) instanceof ThisToken)&& (getToken(pos+1) instanceof PeriodToken)&&
					   (getToken(pos+2) instanceof NameToken)&&(getToken(pos+3) instanceof AssignmentToken)) {
				ParseResult<AssignmentStmt> assignmentResult = parseAssignment(pos); 
				result = new ParseResult<Statement>(assignmentResult.result, assignmentResult.tokenPos);
			}else if ((getToken(pos) instanceof IntToken || getToken(pos) instanceof StringToken)
					&& (getToken(pos + 1) instanceof NameToken)) {
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
			} else if ((getToken(pos) instanceof NameToken) && (getToken(pos + 1) instanceof NameToken)
					&& (getToken(pos + 2) instanceof SemicolonToken)) {
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
			} else if((getToken(pos) instanceof NameToken) && (getToken(pos +1) instanceof PeriodToken) 
						&& (getToken(pos+2) instanceof NameToken) && (getToken(pos+3) instanceof LeftParenToken)) {
				ParseResult<IndependentMethodCallStmt> independentMethodCallStmtResult = parseIndependentMethodCallStmt(pos);
				result = new ParseResult<Statement>(independentMethodCallStmtResult.result, independentMethodCallStmtResult.tokenPos);
			} else if((getToken(pos) instanceof NameToken)&&(getToken(pos+1) instanceof LessThanToken)) {
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
			} else if ((getToken(pos) instanceof PrintToken)) { // println(var);
				assertTokenAtPos(new LeftParenToken(), pos + 1);
				final ParseResult<Exp> expression = parseExp(pos + 2);
				assertTokenAtPos(new RightParenToken(), pos + 3);
				result = new ParseResult<Statement>(new PrintExp(expression.result), startPos + 4);
			}
			if (result == null) {
				throw new ParserException("Expected valid statement at: " + pos);
			} else if (!(getToken(result.tokenPos) instanceof SemicolonToken)) {
				throw new ParserException("Expected semicolon at: " + result.tokenPos);
			} else {
				return new ParseResult<Statement>(result.result, result.tokenPos + 1);
			}
		} else {
			throw new ParserException("Empty Statement(double semicolon) at: " + pos);
		}
	}


	private ParseResult<Statement> parseSingleConstructorStatement(int startPos) throws ParserException {
		int pos = startPos;
		ParseResult<Statement> result = null;
		if (!(getToken(pos) instanceof SemicolonToken)) {
			if ((getToken(pos) instanceof NameToken) && (getToken(pos + 1) instanceof AssignmentToken)) {
				ParseResult<AssignmentStmt> assignmentResult = parseAssignment(pos);
				result = new ParseResult<Statement>(assignmentResult.result, assignmentResult.tokenPos);
			} else if((getToken(pos) instanceof ThisToken)&& (getToken(pos+1) instanceof PeriodToken)&&
					   (getToken(pos+2) instanceof NameToken)&&(getToken(pos+3) instanceof AssignmentToken)) {
				ParseResult<AssignmentStmt> assignmentResult = parseAssignment(pos); 
				result = result = new ParseResult<Statement>(assignmentResult.result, assignmentResult.tokenPos);
			}else if ((getToken(pos) instanceof IntToken || getToken(pos) instanceof StringToken)
					&& (getToken(pos + 1) instanceof NameToken)) {
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
			} else if ((getToken(pos) instanceof NameToken) && (getToken(pos + 1) instanceof NameToken)
					&& (getToken(pos + 2) instanceof SemicolonToken)) {
				ParseResult<VariableDecExp> variableDecExpResult = parseVariableDecExp(pos);
				result = new ParseResult<Statement>(variableDecExpResult.result, variableDecExpResult.tokenPos);
			} else if((getToken(pos) instanceof NameToken) && (getToken(pos +1) instanceof PeriodToken) 
					&& (getToken(pos+2) instanceof NameToken) && (getToken(pos+3) instanceof LeftParenToken)) {
			ParseResult<IndependentMethodCallStmt> independentMethodCallStmtResult = parseIndependentMethodCallStmt(pos);
			result = new ParseResult<Statement>(independentMethodCallStmtResult.result, independentMethodCallStmtResult.tokenPos);
			}

			if (result == null) {
				throw new ParserException("Expcted valid statement at: " + pos);
			} else if (!(getToken(result.tokenPos) instanceof SemicolonToken)) {
				throw new ParserException("Expected semicolon at: " + result.tokenPos);
			} else {
				return new ParseResult<Statement>(result.result, result.tokenPos + 1);
			}
		} else {
			throw new ParserException("Empty Statement(double semicolon) at: " + pos);
		}
	}
	
	private ParseResult<IndependentMethodCallStmt> parseIndependentMethodCallStmt(int startPos) throws ParserException{
		int pos = startPos;
		
		if((getToken(pos) instanceof NameToken) && (getToken(pos +1) instanceof PeriodToken) 
				&& (getToken(pos+2) instanceof NameToken) && (getToken(pos+3) instanceof LeftParenToken)) {
			VariableExp input = new VariableExp(((NameToken)getToken(pos)).name);
			VariableExp methodname = new VariableExp(((NameToken)getToken(pos+2)).name);
			pos = pos + 4;
			ArrayList<VariableExp> params = new ArrayList<VariableExp>();
			while(!(getToken(pos) instanceof RightParenToken)){
				if(!(getToken(pos) instanceof NameToken)){
					throw new ParserException("Expected Parameter Name at: " + pos);
				}
				params.add(new VariableExp(((NameToken) getToken(pos)).name));
				if(!(getToken(pos+1) instanceof RightParenToken)) {
					assertTokenAtPos(new CommaToken(), pos+1);
					pos += 2;
				}else {
					pos += 1;
				}
			}
			assertTokenAtPos(new RightParenToken(), pos);
			IndependentMethodCallStmt result = new IndependentMethodCallStmt(new CallMethodExp(input, methodname, params));
			return new ParseResult<IndependentMethodCallStmt>(result, pos+1);
			
		}else {
			throw new ParserException("Expected Method Name at: " + pos);
		}
			
	}

	private ParseResult<AssignmentStmt> parseAssignment(int startPos) throws ParserException {
		int pos = startPos;
		if ((getToken(pos) instanceof NameToken) && (getToken(pos + 1) instanceof AssignmentToken)) {
			ParseResult<Exp> expResult = parseExp(pos + 2);
			return new ParseResult<AssignmentStmt>(
					new AssignmentStmt(new VariableExp(getToken(pos).toString()), expResult.result, false),
					expResult.tokenPos);
		}else if((getToken(pos) instanceof ThisToken)){
			ParseResult<Exp> expResult = parseExp(pos+4); 
			return new ParseResult<AssignmentStmt>(
					new AssignmentStmt(new VariableExp(getToken(pos+2).toString()), expResult.result, true),
					expResult.tokenPos);
		}else {
			throw new ParserException("Expected variable and assignment at: " + pos);
		}
	}

	private ParseResult<VariableDecExp> parseVariableDecExp(int startPos) throws ParserException {
		int currentPos = startPos;
		ParseResult<Type> typeResult = parseType(currentPos);
		currentPos = typeResult.tokenPos;
		if (!(getToken(currentPos) instanceof NameToken)) {
			throw new ParserException("Expected Variable Name at " + currentPos);
		} else {
			return new ParseResult<VariableDecExp>(
					new VariableDecExp(typeResult.result, new VariableExp(getToken(currentPos).toString())),
					currentPos + 1);
		}
	}

} // Parser