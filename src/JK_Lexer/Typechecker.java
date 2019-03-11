package JK_Lexer;

import java.util.Map; 
import java.util.HashMap; 
public class Typechecker {
	//begin instance variables
	private final Map<String, MethodDefExp> methods; 
	private final Map<String, ClassDefExp> classes; 
	//begin methods
	
	//TEMPORARY CONSTRUCTOR ONLY FOR TESTING EXP WITH NO VARIABLES; DISREGARD
	public Typechecker(){
		this.methods = new HashMap<>(); 
		this.classes= new HashMap<>(); 
	}
	//constructor
	public Typechecker(final Map<String, MethodDefExp> methods,
					   final Map<String, ClassDefExp> classes) {
		this.methods=methods; 
		this.classes=classes; 
	}
	
	//Replace classdefexp with program here when changed
	public void typecheck(final ClassDefExp prog) throws TypeErrorException{	
	}
	public void ensureTypesSame(final Type expected, final Type actual) throws TypeErrorException{
		if(!expected.equals(actual)) {
			throw new TypeErrorException("Expected: " + expected.toString() +
										 " got: " + actual.toString()); 
		}
	}
	

	
	//typeofExp takes in map of strings (variable names) and types as well as an Exp e)
	public Type typeofExp(final Map<String,Type> env, final Exp e) throws TypeErrorException{
		if(e instanceof NumberExp) {
			return new IntType(); 
		}
		else if(e instanceof StringExp) {
			return new StringType(); 
		}
		else if(e instanceof VariableExp) {
			final VariableExp var = (VariableExp)e; 
			final String name = var.name; 
			final Type retType = env.get(name); 
			if(retType == null) {
				throw new TypeErrorException("Variable not defined: "+ var.toString());
			}
			else {
				return retType; 
			}
		}
		else if(e instanceof BinopExp) {
			final BinopExp asBinop = (BinopExp)e; 
			final Type leftType = typeofExp(env, asBinop.left);
			final Op op = asBinop.op; 
			final Type rightType= typeofExp(env, asBinop.right);
			ensureTypesSame(new IntType(), leftType); 
			ensureTypesSame(new IntType(), rightType); 
			return new IntType();
		}
		else if(e instanceof ThisExp){
			final VariableExp var = (VariableExp) ((ThisExp)e).variable; 
			final String name = var.name; 
			final Type retType = env.get(name); 
			if(retType == null) {
				throw new TypeErrorException("Variable in this. expression not defined: " +name);
			}
			else {
				return retType; 
			}
		}
		else if(e instanceof PrintExp) {
			final VariableExp var = (VariableExp)((PrintExp)e).expression; 
			final String name = var.name; 
			final Type retType = env.get(name); 
			if(retType == null) {
				throw new TypeErrorException("Variable in print expression not defined: " +name);
			}
			else {
				return retType; 
			}
		}
		//else if(e instanceof CallMethodExp)
		
		//else if(e instanceof NewExp) 
		
		else {
			throw new TypeErrorException("Not a valid exp"); 
		}
	}
}