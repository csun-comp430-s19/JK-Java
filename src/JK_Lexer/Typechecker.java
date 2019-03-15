package JK_Lexer;

import java.util.ArrayList;
import java.util.Map; 
import java.util.HashMap; 
public class Typechecker {
	//begin instance variables
	

	//List of any statements outside of classes in program
	private final ArrayList<Statement> statements;
	//Classes are mapped to their names
	private Map<String,ClassDefExp> classes; 
	//Instances and methods are mapped to their names and that map is mapped to the class name they are in 
	private Map<String, Map<String, InstanceDecExp>> instances; 
	private Map<String, Map<String, MethodDefExp>> methods; 
	//Variable declarations in methods mapped to classname, methodname, and their own names
	private Map<String, Map<String, Map<String, VariableDecExp>>> variables;
		
	//TEMPORARY CONSTRUCTOR ONLY FOR TESTING EXP WITH NO VARIABLES; DISREGARD FOR NOW
	public Typechecker(){
		this.statements = new ArrayList<>(); 
		this.classes = new HashMap<>(); 
		this.instances = new HashMap<>(); 
		this.methods = new HashMap<>(); 
		this.variables = new HashMap<>(); 
	}
	//Constructor, takes in program and type-checks the statements (outside of classes) and the classes 
	public Typechecker(Program prog){ 
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
				ArrayList<VariableDecExp> variableList = m.parameters; 
				for(VariableDecExp v: variableList) {
					Map<String, VariableDecExp> temp2 = new HashMap<String, VariableDecExp>(); 
					temp2.put(v.var.name, v);
					Map<String, Map<String, VariableDecExp>> temp3 = new HashMap<String, Map<String, VariableDecExp>>();
					temp3.put(m.name, temp2); 
					this.variables.put(c.name, temp3);
				}
			}
		}
		//function that typechecks statements outside of classes goes here 
		
		//function that typechecks classes goes here		
		
	}
	//begin functions
	
	//Final typechecking function for program, creates new typechecker with prog
	public static void typecheckProgram(final Program prog) throws TypeErrorException{
		new Typechecker(prog); 
	}
	public void typecheckClass() {
		
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
	
	//ensures type of expected is the same as type of actual
	public void ensureTypesSame(final Type expected, final Type actual) throws TypeErrorException{
		if(!expected.equals(actual)) {
			throw new TypeErrorException("Expected: " + expected.toString() +
										 " got: " + actual.toString()); 
		}
	}
}