package JK_Lexer;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class Typechecker {
	// begin instance variables

	// List of any statements outside of classes in program
	private final ArrayList<Statement> statements;
	// Map for Variables declared in statements outside of classes
	private final Map<String, VariableDecExp> programVariables;
	// Classes are mapped to their names
	private Map<String, ClassDefExp> classes;
	// Instances and methods are mapped to their names and that map is mapped to the
	// class name they are in
	private Map<String, Map<String, InstanceDecExp>> instances;
	private Map<String, Map<String, MethodDefExp>> methods;
	private Map<String, ArrayList<ConstructorDef>> constructors;
	private Map<String, ArrayList<Map<String, VariableDecExp>>> constructorVariableDec;
	// Variable declarations in methods mapped to class name, method name, and their
	// own names
	private Map<String, Map<String, Map<String, VariableDecExp>>> variables;

	// Placeholders to know where an exp is
	private String currentClass;
	private String currentMethod;
	private int currentConstructor;
	private boolean inConstructor;

	// Constructor, takes in program and type-checks the statements (outside of
	// classes) and the classes
	public Typechecker(Program prog) throws TypeErrorException {

		this.statements = prog.statementList;
		this.classes = new HashMap<String, ClassDefExp>();
		this.instances = new HashMap<String, Map<String, InstanceDecExp>>();
		this.methods = new HashMap<String, Map<String, MethodDefExp>>();
		this.variables = new HashMap<String, Map<String, Map<String, VariableDecExp>>>();
		this.programVariables = new HashMap<String, VariableDecExp>();
		this.constructors = new HashMap<String, ArrayList<ConstructorDef>>();
		this.constructorVariableDec = new HashMap<String, ArrayList<Map<String, VariableDecExp>>>();
		inConstructor = true;
		currentClass = null;
		currentMethod = null;

		ArrayList<ClassDefExp> classList = prog.classDefList;
		for (ClassDefExp c : classList) {
			if (classes.containsKey(c.name))
				throw new TypeErrorException("Duplicate class declared: " + c.name);
			this.classes.put(c.name, c);
			ArrayList<InstanceDecExp> instanceList = c.members;
			Map<String, InstanceDecExp> tempp = new HashMap<String, InstanceDecExp>();
			for (InstanceDecExp i : instanceList) {
				
				if (tempp.containsKey(i.var.var.name)) 
					throw new TypeErrorException("Duplicate member declared in class " + c.name + " : " + i.var.var.name);
				tempp.put(i.var.var.name, i);
				this.instances.put(c.name, tempp);
			}
			ArrayList<MethodDefExp> methodList = c.methods;
			Map<String, MethodDefExp> temp = new HashMap<String, MethodDefExp>();
			for (MethodDefExp m : methodList) {
				if (temp.containsKey(m.name))
					throw new TypeErrorException("Duplicate method declared in class " + c.name + " : " + m.name);
				temp.put(m.name, m);
				this.methods.put(c.name, temp);
				ArrayList<VariableDecExp> variableList = m.parameters;
				Map<String, VariableDecExp> temp2 = new HashMap<String, VariableDecExp>();
				for (VariableDecExp v : variableList) {
					if (temp2.containsKey(v.var.name))
						throw new TypeErrorException("Duplicate parameter declared in class " + c.name + " method "
								+ m.name + " : " + v.var.name);
					temp2.put(v.var.name, v);
				}
				Map<String, Map<String, VariableDecExp>> temp3 = new HashMap<String, Map<String, VariableDecExp>>();
				temp3.put(m.name, temp2);
				this.variables.put(c.name, temp3);
			}

			ArrayList<ConstructorDef> constructorList = c.constructors;
			ArrayList<ConstructorDef> tempConstructor = new ArrayList<ConstructorDef>();
			for (int i = 0; i < constructorList.size(); i++) {
				for (int j = 0; i < tempConstructor.size(); j++) {
					if ((tempConstructor.get(j).parameters.equals(constructorList.get(i).parameters)))
						throw new TypeErrorException(
								"Duplicate declared constructor: Name: " + constructorList.get(i).name + "Parameters: "
										+ constructorList.get(i).parameters.toString());
				}
				tempConstructor.add(i, constructorList.get(i));
				this.constructors.put(c.name, tempConstructor);
				ArrayList<VariableDecExp> variableList = constructorList.get(i).parameters;
				Map<String, VariableDecExp> temp2 = new HashMap<String, VariableDecExp>();
				for (VariableDecExp v : variableList) {
					if (temp2.containsKey(v.var.name))
						throw new TypeErrorException("Duplicate parameter declared in class " + c.name
								+ " constructor: " + constructorList.get(i).name + "("
								+ constructorList.get(i).parameters.toString() + ")" + " : " + v.var.name);
					temp2.put(v.var.name, v);
				}
				ArrayList<Map<String, VariableDecExp>> temp3 = new ArrayList<Map<String, VariableDecExp>>();
				temp3.add(temp2);
				this.constructorVariableDec.put(c.name, temp3);
			}
		}

		// Typechecking all statements outside classes in Program
		for (Statement s : this.statements) {
			typecheckStmt(s);
		}
		// Typechecking all classes in Program
		for (ClassDefExp c : this.classes.values()) {
			typecheckClass(c);
		}
	}
	// begin typechecking functions

	// Final typechecking function for program, creates new typechecker with Program
	// prog
	public static void typecheckProgram(final Program prog) throws TypeErrorException {
		new Typechecker(prog);
	}

	// Typechecking for classes
	public void typecheckClass(final ClassDefExp c) throws TypeErrorException {
		// check if class extends another class and check to see if that extending class exists
		if(c.extending==true) {
			if(!ensureClassExists(c.extendingClass)) {
				throw new TypeErrorException("Extending class does not exist: "+c.extendingClass);
			}
		}
		this.currentClass = c.name;
		for (ConstructorDef cd : c.constructors) {
			typecheckConstructor(cd);
		}
		for (MethodDefExp m : c.methods) {
			typecheckMethod(m);
		}
	}

	// Typechecking for methods
	public void typecheckMethod(final MethodDefExp m) throws TypeErrorException {
		this.currentMethod = m.name;
		for (Statement s : m.block) {
			typecheckStmt(s);
		}
	}

	// Typechecking for constructors
	public void typecheckConstructor(final ConstructorDef cd) throws TypeErrorException {
		inConstructor = true;
		for (int i = 0; i < constructors.get(currentClass).size(); i++) {
			if (constructors.get(currentClass).get(i).equals(cd)) {
				this.currentConstructor = i;
				break;
			}
		}

		for (Statement s : cd.block) {
			if (s instanceof AssignmentStmt) {
				AssignmentStmt as = (AssignmentStmt) s;
				typecheckAssignment(as);
			} else if (s instanceof VariableDecExp) {
				if(((VariableDecExp) s).type instanceof CustomType){
					if(!ensureClassExists(((VariableDecExp) s).type.toString())) {
						throw new TypeErrorException("Class type not found: "+((VariableDecExp)s).type.toString());
					}
				}
				if (constructors.get(this.currentClass).get(currentConstructor).parameters.contains(s))
					throw new TypeErrorException(
							"Variable " + ((VariableDecExp) s).var.name + " already declared in parameters");
				else {
					constructorVariableDec.get(currentClass).get(currentConstructor).put(((VariableDecExp) s).var.name,
							((VariableDecExp) s));
				}
			}
		}
		inConstructor = false;
	}

	// Typechecking for statements
	public void typecheckStmt(final Statement s) throws TypeErrorException {
		if (currentClass != null) {
			if (s instanceof AssignmentStmt) {
				typecheckAssignment((AssignmentStmt) s);
			} else if (s instanceof ReturnStmt) {
				Type methodType = methods.get(this.currentClass).get(currentMethod).type;
				Type returnType = typeofExp(((ReturnStmt) s).e);

				if (!(methodType.equals(returnType)))
					throw new TypeErrorException("Method type of method " + this.currentMethod
							+ " does not match type returned by: " + s.toString());
			} else if (s instanceof VariableDecExp) {
				if (methods.get(this.currentClass).get(currentMethod).parameters.contains(s))
					throw new TypeErrorException(
							"Variable " + ((VariableDecExp) s).var.name + " already declared in parameters");
				else {
					variables.get(currentClass).get(currentMethod).put(((VariableDecExp) s).var.name,
							((VariableDecExp) s));
				}
			} else if(s instanceof IndependentMethodCallStmt) {
				typeofExp(((IndependentMethodCallStmt)s).methodcall);
			}
		} else {
			if (s instanceof AssignmentStmt) {
				typecheckAssignment((AssignmentStmt) s);
			} else if (s instanceof VariableDecExp) {
				if (programVariables.containsKey(((VariableDecExp) s).var.name))
					throw new TypeErrorException(
							"Variable " + ((VariableDecExp) s).var.name + " already declared in parameters");
				else {
					programVariables.put(((VariableDecExp) s).var.name, ((VariableDecExp) s));
				}
			} else if(s instanceof IndependentMethodCallStmt) {
				typeofExp(((IndependentMethodCallStmt)s).methodcall); 
			}
		}
	}

	public void typecheckAssignment(final AssignmentStmt as) throws TypeErrorException {
		Type left;
		Type right;
		//If left is this.exp, make sure it checks instance variables only for assigning type to left
		if(as.leftIsThis) {
			InstanceDecExp temp3 = null;
			try {
				temp3 = this.instances.get(this.currentClass).get(as.v.name);
			} catch (Exception e2) {
			}
			// Instance variables check
			if (temp3 != null) {
				left = temp3.var.type;
				right = typeofExp(as.e);
			}
			else {
				throw new TypeErrorException("Using this.var when there is no instance var declared: "+as.v.name);
			}
		}
		else {
			left = lookupVariable(as.v.name);
			right = typeofExp(as.e);
		}
		if(right instanceof CustomType && classes.get(left.toString())!=null){
			String s1 = classes.get(left.toString()).extendingClass;
			String s2 = classes.get(right.toString()).name; 
			if(s1.equals(s2)){
				return; 
			}
		}
		if (!left.toString().equals(right.toString()))
			throw new TypeErrorException("Assignment Statements must have matching sides: " + as.toString());
	}

	// typeofExp takes in map of strings (variable names) and types as well as an
	// Exp e)
	public Type typeofExp(final Exp e) throws TypeErrorException { 
																	
		if (e instanceof NumberExp) {
			return new IntType();
		} else if (e instanceof StringExp) {
			return new StringType();
		} else if (e instanceof BinopExp) {
			final BinopExp asBinop = (BinopExp) e;
			final Type leftType = typeofExp(asBinop.left);
			final Op op = asBinop.op;
			final Type rightType = typeofExp(asBinop.right);
			ensureTypesSame(new IntType(), leftType);
			ensureTypesSame(new IntType(), rightType);
			return new IntType();
		} else if (e instanceof VariableExp) {
			String name = ((VariableExp) e).name;
			InstanceDecExp temp3 = null;
			try {
				temp3 = this.instances.get(this.currentClass).get(name);
			} catch (Exception e2) {
			}
			if (temp3 != null) {
				throw new TypeErrorException("Trying to call instance variable without this.exp");
			}
			return lookupVariable(name);
		} else if (e instanceof ThisExp) {
			String name = ((VariableExp) ((ThisExp) e).variable).name;
			return lookupVariable(name);
		} else if (e instanceof PrintExp) {
			String name = ((VariableExp) ((PrintExp) e).expression).name;
			return lookupVariable(name);
		} else if (e instanceof CallMethodExp) {
			String varName = ((VariableExp) ((CallMethodExp) e).input).name;
			String methodName = ((VariableExp) ((CallMethodExp) e).methodname).name;
			ArrayList<VariableExp> params = new ArrayList<VariableExp>(((CallMethodExp) e).parameter);
			//Check if method is private, then check if it is being called in a different class
			ensureAccessModifier(methodName); 
			//String parameterName = ((VariableExp) ((CallMethodExp) e).parameter).name;
			lookupVariable(varName);
			for(VariableExp v : params) lookupVariable(v.name);
			if(currentClass==null) {
				MethodDefExp m = retrieveMethod(methodName); 
				ensureParametersSame(m, params);
				return m.type; 
			}
			MethodDefExp temp = this.methods.get(this.currentClass).get(methodName);
			if (temp == null) {
				throw new TypeErrorException(
						"Method does not exist: " + methodName + " in class: " + this.currentClass);
			} else {
				ensureParametersSame(temp,params); 
				return temp.type;
			}
		} else if (e instanceof NewExp) {
			String classname = ((VariableExp) ((NewExp) e).classname).name;
			String varname = ((VariableExp) ((NewExp) e).variable).name;
			lookupVariable(varname);
			if (ensureClassExists(classname)) {
				return new CustomType(classname);
			} else {
				throw new TypeErrorException("Class does not exist: " + classname);
			}
		}
		
		//Code should never get here
		else {
			throw new TypeErrorException("Not a valid exp");
		}
	}

	// begin helper functions

	// ensures type of expected is the same as type of actual
	public void ensureTypesSame(final Type expected, final Type actual) throws TypeErrorException {
		if (!expected.equals(actual)) {
			throw new TypeErrorException("Expected: " + expected.toString() + " got: " + actual.toString());
		}
	}

	// looks up variable from the rest of the class
	public Type lookupVariable(final String name) throws TypeErrorException {
		// Checks if not in a class then checks statements outside classes for variable
		if (this.currentClass == null) {
			for (Statement s : this.statements) {
				if (s instanceof VariableDecExp && ((VariableDecExp) s).var.name.equals(name)) {
					return ((VariableDecExp) s).type;
				}
			}
			throw new TypeErrorException("No variable found: " + name);
		}
		// If in a class, then check variables within methods, instance variables, and
		// variables within method parameters
		if (!inConstructor) {
			VariableDecExp temp = null;
			MethodDefExp temp2 = null;
			InstanceDecExp temp3 = null;
			try {
				temp = this.variables.get(this.currentClass).get(this.currentMethod).get(name);
			} catch (Exception e) {
			}
			try {
				temp2 = this.methods.get(this.currentClass).get(this.currentMethod);
			} catch (Exception e1) {
			}
			try {
				temp3 = this.instances.get(this.currentClass).get(name);
			} catch (Exception e2) {
			}
			// Variables within methods check
			if (temp != null) {
				return temp.type;
			}
			// Instance variables check
			else if (temp3 != null) {
				return temp3.var.type;
			}
			// Variables within method parameters check
			else if (temp2 != null) {
				for (VariableDecExp v : temp2.parameters) {
					if (v.var.name.equals(name)) {
						return v.type;
					} else {
						break;
					}
				}
				throw new TypeErrorException("Referring to unassigned variable " + name);
			} else {
				throw new TypeErrorException("Referring to unassigned variable " + name);
			}
		} else {
			VariableDecExp temp = null;
			ConstructorDef temp2 = null;
			InstanceDecExp temp3 = null;

			try {
				temp = constructorVariableDec.get(currentClass).get(currentConstructor).get(name);
			} catch (Exception e) {
			}
			try {
				temp2 = constructors.get(currentClass).get(currentConstructor);
			} catch (Exception e1) {
			}
			try {
				temp3 = instances.get(currentClass).get(name);
			} catch (Exception e2) {
			}
			if (temp != null) {
				return temp.type;
			}
			// Instance variables check
			else if (temp3 != null) {
				return temp3.var.type;
			}
			// Variables within constructor parameters check
			else if (temp2 != null) {
				for (VariableDecExp v : temp2.parameters) {
					if (v.var.name.equals(name)) {
						return v.type;
					} else {
						break;
					}
				}
				throw new TypeErrorException("Referring to unassigned variable " + name);
			} else {
				throw new TypeErrorException("Referring to unassigned variable " + name);
			}
		}
	}
	// looks to see if class exists by string name
	public boolean ensureClassExists(final String name) throws TypeErrorException {
		if (this.classes.get(name) == null) {
			return false;
		}
		return true;
	}
	//Sees if method is private or not, throw exception if private and outside class defined in 
	public void ensureAccessModifier(String methodname) throws TypeErrorException{
		for(ClassDefExp c: classes.values()) {
			for(MethodDefExp m: c.methods) {
				if(m.name.equals(methodname)) {
					checkPrivate(m,c); 
				}
			}
		}
	}
	public void checkPrivate(MethodDefExp m, ClassDefExp c) throws TypeErrorException {
		if(m.mod instanceof PrivateModifier) {
			if(currentClass==null|| !(currentClass.equals(c))) {
				throw new TypeErrorException("Private method used: "+ m.name);
			}
		}
	}
	public MethodDefExp retrieveMethod(String m) throws TypeErrorException {
		for(ClassDefExp c: classes.values()) {
			for(MethodDefExp me: c.methods) {
				if(me.name.contentEquals(m)) {
					return me; 
				}
			}
		}
		throw new TypeErrorException("Method not found: " +m);
	}
	public void ensureParametersSame(MethodDefExp m, ArrayList<VariableExp> params) throws TypeErrorException {
		int i=0; 
		if(params.size()!=m.parameters.size()) {
			throw new TypeErrorException("Invalid number of parameters for method: "+ m.name);
		}
		for(VariableExp v: params) {
			ensureTypesSame(lookupVariable(v.name), m.parameters.get(i).type);
			i++; 
		}
	}
}