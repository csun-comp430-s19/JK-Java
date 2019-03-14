package JK_Lexer;

import java.util.ArrayList;
import java.util.Map; 
import java.util.HashMap; 
public class Typechecker {
	//begin instance variables
	
	//A program is taken in as the parameter for Typechecker
	private final Program program; 
	//List of any statements outside of classes in program
	private final ArrayList<Statement> statements;
	//Classes are mapped to their names
	private Map<String,ClassDefExp> classes; 
	//Instances and methods are mapped to their names and that map is mapped to the class name they are in 
	private Map<String, Map<String, InstanceDecExp>> instances; 
	private Map<String, Map<String, MethodDefExp>> methods; 
		
		
	//TEMPORARY CONSTRUCTOR ONLY FOR TESTING EXP WITH NO VARIABLES; DISREGARD FOR NOW
	public Typechecker(){
		this.program = new Program(); 
		this.statements = new ArrayList<>(); 
		this.classes = new HashMap<>(); 
		this.instances = new HashMap<>(); 
		this.methods = new HashMap<>(); 
	}
	//Constructor, takes in program
	public Typechecker(Program prog){
		this.program = prog; 
		this.statements = prog.statementList; 
		ArrayList<ClassDefExp> classList = prog.classDefList; 
		for(ClassDefExp c: classList) {
			this.classes.put(c.name, c);
			ArrayList<InstanceDecExp> instanceList = c.members; 
			for(InstanceDecExp i: instanceList) {
				Map<String, InstanceDecExp> temp = new HashMap<String,InstanceDecExp>(); 
				temp.put(i.var.var.name, i);
				this.instances.put(c.name, temp);
			}
			ArrayList<MethodDefExp> methodList = c.methods;
			for(MethodDefExp m: methodList) {
				Map<String, MethodDefExp> temp = new HashMap<String, MethodDefExp>();
				temp.put(m.name, m);
				this.methods.put(c.name, temp);
			}
		}
	}
	//begin functions
		
	//ensures type of expected is the same as type of actual
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