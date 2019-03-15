package JK_Lexer;

import java.util.ArrayList;
import java.util.Map; 
import java.util.HashMap; 
public class Typechecker {
	//begin instance variables
	
	//List of any statements outside of classes in program
	private final ArrayList<Statement> statements;
	//Map for Variables declared in statements outside of classes
	private final Map<String,VariableDecExp> programVariables;
	//Classes are mapped to their names
	private Map<String,ClassDefExp> classes; 
	//Instances and methods are mapped to their names and that map is mapped to the class name they are in 
	private Map<String, Map<String, InstanceDecExp>> instances; 
	private Map<String, Map<String, MethodDefExp>> methods; 
	//Variable declarations in methods mapped to class name, method name, and their own names
	private Map<String, Map<String, Map<String, VariableDecExp>>> variables;
	
	//Placeholders to know where an exp is 
	private String currentClass;
	private String currentMethod; 
	
	//Constructor, takes in program and type-checks the statements (outside of classes) and the classes 
	public Typechecker(Program prog) throws TypeErrorException{ 
		
		this.statements = prog.statementList;  
		this.classes = new HashMap<String, ClassDefExp>(); 
		this.instances = new HashMap<String, Map<String, InstanceDecExp>>(); 
		this.methods = new HashMap<String, Map<String, MethodDefExp>>(); 
		this.variables = new HashMap<String, Map<String, Map<String, VariableDecExp>>>(); 
		this.programVariables = new HashMap<String, VariableDecExp>();
		
		ArrayList<ClassDefExp> classList = prog.classDefList;
		for(ClassDefExp c: classList) {
			if(classes.containsKey(c.name)) throw new TypeErrorException("Duplicate class declared: " + c.name);
			this.classes.put(c.name, c);
			ArrayList<InstanceDecExp> instanceList = c.members; 
			for(InstanceDecExp i: instanceList) {
				Map<String, InstanceDecExp> temp = new HashMap<String,InstanceDecExp>(); 
				if(temp.containsKey(i.var.var.name)) throw new TypeErrorException("Duplicate member declared in class " + c.name + " : " + i.var.var.name);
				temp.put(i.var.var.name, i);
				this.instances.put(c.name, temp);
			}
			ArrayList<MethodDefExp> methodList = c.methods;
			for(MethodDefExp m: methodList) {
				Map<String, MethodDefExp> temp = new HashMap<String, MethodDefExp>();
				if(temp.containsKey(m.name)) throw new TypeErrorException("Duplicate method declared in class " + c.name +" : "+ m.name);
				temp.put(m.name, m);
				this.methods.put(c.name, temp);
				ArrayList<VariableDecExp> variableList = m.parameters; 
				Map<String, VariableDecExp> temp2 = new HashMap<String, VariableDecExp>(); 
				for(VariableDecExp v: variableList) {
					if(temp2.containsKey(v.var.name)) throw new TypeErrorException("Duplicate method declared in class " + c.name + " method " + m.name + " : " + v.var.name);
					temp2.put(v.var.name, v);
				}
				Map<String, Map<String, VariableDecExp>> temp3 = new HashMap<String, Map<String, VariableDecExp>>();
				temp3.put(m.name, temp2); 
				this.variables.put(c.name, temp3);
			}
		}
		for(Statement s: this.statements) {
			if (s instanceof VariableDecExp) {
				if(programVariables.containsKey(((VariableDecExp)s).var.name)) throw new TypeErrorException("Duplicate variable declared in statements outside of class declaratons: " +((VariableDecExp)s).var.name);
				programVariables.put(((VariableDecExp)s).var.name, (VariableDecExp)s);
			}
		}
		
		//Typechecking all classes in Program 
		for(ClassDefExp c: this.classes.values()) {
			typecheckClass(c); 
		}
		//Typechecking all statements outside classes in Program  
		for(Statement s: this.statements) {
			typecheckStmt(s);
		}
	}
	//begin typechecking functions
	
	//Final typechecking function for program, creates new typechecker with Program prog
	public static void typecheckProgram(final Program prog) throws TypeErrorException{
		new Typechecker(prog); 
	}
	//Typechecking for classes
	public void typecheckClass(final ClassDefExp c) throws TypeErrorException {
		this.currentClass = c.name; 
		for(MethodDefExp m : c.methods) {
			typecheckMethod(m);
		}
	}
	//Typechecking for methods
	public void typecheckMethod(final MethodDefExp m) throws TypeErrorException {
		this.currentMethod = m.name; 
		for(Statement s : m.block) {
			typecheckStmt(s);
		}
	}
	//Typechecking for statements within classes
	public void typecheckStmt(final Statement s) throws TypeErrorException {
		if(s instanceof AssignmentStmt) {
			typecheckAssignment((AssignmentStmt)s);
		}else if(s instanceof ReturnStmt) {
			Type methodType = methods.get(this.currentClass).get(currentMethod).type;
			Type returnType = typeofExp(((ReturnStmt)s).e);
			
			if(!(methodType.equals(returnType))) throw new TypeErrorException("Method type of method " + this.currentMethod + " does not match type returned by: " + s.toString()); 
		}
	}
	
	public void typecheckAssignment(final AssignmentStmt as) throws TypeErrorException {
		Type left = lookupVariable(as.v.name);
		Type right = typeofExp(as.e);
		
		if(!left.equals(right)) throw new TypeErrorException("Assignment Statements must have matching sides: " + as.toString());
	}
	
	//typeofExp takes in map of strings (variable names) and types as well as an Exp e)
	public Type typeofExp(final Exp e) throws TypeErrorException{       // takes in another parameter too for in scope, but idk what yet
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
		else if(e instanceof VariableExp) {
			String name = ((VariableExp)e).name;
			return lookupVariable(name); 
		}
		else if(e instanceof ThisExp) {
			String name = ((VariableExp)((ThisExp)e).variable).name; 
			return lookupVariable(name); 
		}
		else if(e instanceof PrintExp) {
			String name = ((VariableExp)((PrintExp)e).expression).name; 
			return lookupVariable(name); 
		}
		//else if(e instanceof CallMethodExp) 
	
		
		else if(e instanceof NewExp) {
			String classname = ((VariableExp)((NewExp)e).classname).name; 
			String varname = ((VariableExp)((NewExp)e).variable).name; 
			lookupVariable(varname); 
			if(ensureClassExists(classname)) {
				return new CustomType(classname);
			}
			else {
				throw new TypeErrorException("Class does not exist: "+ classname); 
			}
		}
		
		else {
			throw new TypeErrorException("Not a valid exp"); 
		}
	}
	
	//begin helper functions
	
	//ensures type of expected is the same as type of actual
	public void ensureTypesSame(final Type expected, final Type actual) throws TypeErrorException{
		if(!expected.equals(actual)) {
			throw new TypeErrorException("Expected: " + expected.toString() +
										 " got: " + actual.toString()); 
		}
	}
	//looks up variable from the rest of the class
	public Type lookupVariable(final String name) throws TypeErrorException{
		//Checks if not in a class then checks statements outside classes for variable
		if(this.currentClass==null) {
			for(Statement s: this.statements){
				if(s instanceof VariableDecExp && ((VariableDecExp) s).var.name.equals(name)){
					return ((VariableDecExp)s).type; 
				}
			}
			throw new TypeErrorException("No variable found: "+name); 
		}
		//If in a class, then check variables within methods, instance variables, and variables within method parameters
		VariableDecExp temp = this.variables.get(this.currentClass).get(this.currentMethod).get(name);
		MethodDefExp temp2 = this.methods.get(this.currentClass).get(this.currentMethod); 
		InstanceDecExp temp3 = this.instances.get(this.currentClass).get(name);
		//Variables within methods check
		if(temp!=null) {
			return temp.type; 
		}
		//Instance variables check
		else if(temp3!=null) {
			return temp3.var.type; 
		}
		//Variables within method parameters check
		else if(temp2!=null) {
			for(VariableDecExp v: temp2.parameters) {
				if(v.var.name.equals(name)) {
					return v.type; 
				}
				else {
					break; 
				}
			}
			throw new TypeErrorException("Referring to unassigned variable "+name); 
		}
		else {
			throw new TypeErrorException("Referring to unassigned variable " + name); 
		}
	}
	//looks to see if class exists by string name 
	public boolean ensureClassExists(final String name) throws TypeErrorException{
		if(this.classes.get(name)==null) {
			return false;
		}
		return true; 
	}
}