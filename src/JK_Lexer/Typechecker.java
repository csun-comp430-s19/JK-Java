package JK_Lexer;

import java.util.Map; 
import java.util.HashMap; 
public class Typechecker {
	//begin instance variables
	private final Map<String, MethodDefExp> methods; 
	private final Map<String, InstanceDecExp> instances; 
	//begin methods
	
	//TEMPORARY CONSTRUCTOR ONLY FOR TESTING EXP WITH NO VARIABLES; DISREGARD FOR NOW
	public Typechecker(){
		this.methods = new HashMap<>(); 
		this.instances= new HashMap<>(); 
	}
	//constructor
	public Typechecker(final Map<String, MethodDefExp> methods,
					   final Map<String, InstanceDecExp> instances) {
		this.methods=methods; 
		this.instances=instances; 
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
	public Type typeofExp(final Exp e) throws TypeErrorException{
		if(e instanceof NumberExp) {
			return new IntType(); 
		}
		else if(e instanceof StringExp) {
			return new StringType(); 
		}
		else if(e instanceof BinopExp) {
			final BinopExp asBinop = (BinopExp)e; 
			final Type leftType = typeofExp(asBinop.left);
			final Op op = asBinop.op; 
			final Type rightType= typeofExp(asBinop.right);
			ensureTypesSame(new IntType(), leftType); 
			ensureTypesSame(new IntType(), rightType); 
			return new IntType();
		}
		//else if(e instanceof VariableExp) 
		
		//else if(e instanceof ThisExp)	
		
		//else if(e instanceof PrintExp) 
		
		//else if(e instanceof CallMethodExp) 
		
		//else if(e instanceof NewExp) 
		
		else {
			throw new TypeErrorException("Not a valid exp"); 
		}
	}
}