package JK_Lexer;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Map; 
import java.util.HashMap; 

public class CCodeGenerator {
	private final List<CInstruction> instructions;
	

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
		
		
	//CCodeGenerator object will output an arraylist of c instructions
	public CCodeGenerator() {
		this.instructions = new ArrayList<CInstruction>(); 
		this.statements = new ArrayList<Statement>();
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
	}
	
	//For codegen with full programs, incomplete
	public CCodeGenerator(Program prog) {
		this.instructions = new ArrayList<CInstruction>(); 
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
			this.classes.put(c.name, c);
			ArrayList<InstanceDecExp> instanceList = c.members;
			for (InstanceDecExp i : instanceList) {
				Map<String, InstanceDecExp> temp = new HashMap<String, InstanceDecExp>();
				temp.put(i.var.var.name, i);
				this.instances.put(c.name, temp);
			}
			ArrayList<MethodDefExp> methodList = c.methods;
			Map<String, MethodDefExp> temp = new HashMap<String, MethodDefExp>();
			for (MethodDefExp m : methodList) {
				temp.put(m.name, m);
				this.methods.put(c.name, temp);
				ArrayList<VariableDecExp> variableList = m.parameters;
				Map<String, VariableDecExp> temp2 = new HashMap<String, VariableDecExp>();
				for (VariableDecExp v : variableList) {
					temp2.put(v.var.name, v);
				}
				Map<String, Map<String, VariableDecExp>> temp3 = new HashMap<String, Map<String, VariableDecExp>>();
				temp3.put(m.name, temp2);
				this.variables.put(c.name, temp3);
			}

			ArrayList<ConstructorDef> constructorList = c.constructors;
			ArrayList<ConstructorDef> tempConstructor = new ArrayList<ConstructorDef>();
			for (int i = 0; i < constructorList.size(); i++) {
				tempConstructor.add(i, constructorList.get(i));
				this.constructors.put(c.name, tempConstructor);
				ArrayList<VariableDecExp> variableList = constructorList.get(i).parameters;
				Map<String, VariableDecExp> temp2 = new HashMap<String, VariableDecExp>();
				for (VariableDecExp v : variableList) {
					temp2.put(v.var.name, v);
				}
				ArrayList<Map<String, VariableDecExp>> temp3 = new ArrayList<Map<String, VariableDecExp>>();
				temp3.add(i, temp2);
				this.constructorVariableDec.put(c.name, temp3);
			}
		}
	}
	
	//Method for adding instructions to the c instruction list
	public void add(final CInstruction i) {
		instructions.add(i);
	}
	
	//Compile NumberExp function
	public void compileNumberExp(final NumberExp exp) {
		add(new CNumberExp(exp.number));
	}
	
	//Compile StringExp function
	public void compileStringExp(final StringExp exp) {
		add(new CStringExp(exp.fullstring));
	}
	
	//Compile VariableExp function
	public void compileVariableExp(final VariableExp exp) {
		add(new CVariableExp(exp.name));
	}
	
	//Compile BinopExp function
	public void compileBinopExp(final BinopExp exp) throws CCodeGeneratorException {
		compileExp(exp.left); 
		add(new COp(exp.op));
		compileExp(exp.right);
	}
	
	//Compile PrintExp function
	public void compilePrintExp(final PrintExp exp) {
		add(new CPrintExp(exp.expression.toString())); 
	}
	
	//Compile CallMethod Function
	public void compileCallMethodExp(final CallMethodExp exp) throws CCodeGeneratorException {
		add(convertCallMethod(exp));
	}
	
	//Compile exp function
	public void compileExp(final Exp exp) throws CCodeGeneratorException {
		if(exp instanceof NumberExp){
			compileNumberExp((NumberExp)exp);
		}
		else if(exp instanceof StringExp) {
			compileStringExp((StringExp)exp); 
		}
		else if(exp instanceof VariableExp) {
			compileVariableExp((VariableExp)exp); 
		}
		else if(exp instanceof BinopExp) {
			compileBinopExp((BinopExp)exp);
		}
		else if(exp instanceof PrintExp) {
			compilePrintExp((PrintExp)exp); 
		}
		else if(exp instanceof CallMethodExp) {
			compileCallMethodExp((CallMethodExp) exp);
		}
		
		//Add this, method call, and new class when planned out
		
		else {
			throw new CCodeGeneratorException("Basic expression not found: "+exp.toString());
		}
	}
	
	//Convert Exp to CExp
	public CExp convertExp(final Exp exp) throws CCodeGeneratorException {
		if(exp instanceof NumberExp){
			return new CNumberExp(((NumberExp)exp).number);
		}
		else if(exp instanceof StringExp) {
			return new CStringExp(((StringExp)exp).fullstring);
		}
		else if(exp instanceof VariableExp) {
			return new CVariableExp(((VariableExp)exp).name);
		}
		else if(exp instanceof BinopExp) {
			CExp left = convertExp(((BinopExp)exp).left);
			CExp right = convertExp(((BinopExp)exp).right);
			return new CBinOp(left, ((BinopExp)exp).op, right);
		}
		else if(exp instanceof PrintExp) {
			return new CPrintExp(((PrintExp)exp).expression.toString());
		}
		else if(exp instanceof CallMethodExp) {
			return convertCallMethod((CallMethodExp)exp);
		}
		
		//Add this, method call, and new class when planned out
		
		else {
			throw new CCodeGeneratorException("Basic expression not found: "+exp.toString());
		}
	}
	
	//Compile variable declaration
	public void compileVariableDec(VariableDecExp v) throws CCodeGeneratorException {
		if(v.type instanceof IntType) {
			add(new CVariableDec(new Cint(),new CVariableExp(v.var.name)));
		}
		else if(v.type instanceof StringType) {
			add(new CVariableDec(new CChar(),new CVariableExp(v.var.name)));
		}
		else if(v.type instanceof VoidType) {
			add(new CVariableDec(new CVoid(), new CVariableExp(v.var.name)));
		}
		
		//ADD NEW CLASS OBJECT HERE, INCOMPLETE
		
		else {
			throw new CCodeGeneratorException("Invalid type:"+v.type.toString());
		}
	}
	
	//Compile Call Method Exp
	public CFunctionCall convertCallMethod(CallMethodExp exp) throws CCodeGeneratorException{
		VariableExp objname = exp.input;
		String classname = ((ObjectType) lookupVariable(objname.name)).className;
		VariableExp methodname = exp.methodname;
		ArrayList<VariableExp> parameters = exp.parameter;
		ArrayList<CVariableExp> cparams = new ArrayList<CVariableExp>();
		cparams.add((CVariableExp)convertExp(objname));
		for(VariableExp p: parameters) {
			cparams.add((CVariableExp)convertExp(p));
		}
		
		CFunctionCall result = new CFunctionCall(classname, methodname.name, cparams);
		
		return result;
	}
	
	//Compile assignment statement
	public void compileAssignment(AssignmentStmt a) throws CCodeGeneratorException {
		CVariableExp left = new CVariableExp(a.v.name);
		CExp right = convertExp(a.e); 
		add(new CAssignment(left, right));
	}
	
	//Compile return statement
	public void compileReturn(ReturnStmt r) throws CCodeGeneratorException {
		if(r.e == null) {
			add(new CReturn());
		}else {
			CExp c = convertExp(r.e);
			add(new CReturn(c));
		}
	}
	
	//Compile statement function
	public void compileStatement(final Statement s) throws CCodeGeneratorException{
		if(s instanceof VariableDecExp) {
			compileVariableDec((VariableDecExp)s);
		}
		else if(s instanceof AssignmentStmt) {
			compileAssignment((AssignmentStmt)s);
		}
		else if(s instanceof ReturnStmt) {
			compileReturn((ReturnStmt)s);
		}
		else {
			throw new CCodeGeneratorException("Statement not found: "+s.toString());
		}
	}
	
	//Writes individual instructions to file, for testing exp and statement 
	public void writeIndividualLinesToFile(final File file) throws IOException{
		final PrintWriter output= new PrintWriter(new BufferedWriter(new FileWriter(file))); 
		try {
			for(final CInstruction i: instructions) {
				String s = i.toString(); 
				output.println(s);
			}
		}finally {
			output.close(); 
		}
	}
	
	//Writes int main() to file with list of statements to file, for method testing since we havent implemented structs yet  
	public void writeMainToFile(final File file) throws IOException{
		final PrintWriter output= new PrintWriter(new BufferedWriter(new FileWriter(file))); 
		try {
			output.println("int main(){");
			for(final CInstruction i: instructions) {
				String s = i.toString(); 
				output.println(s);
			}
			output.println("}");
		}finally {
			output.close(); 
		}
	}
	
	//Method to write an expression to a file: compileExp to fill list of Cinstructions, then to writeCompleteFile (FOR TESTING EXPS)
	public void writeExpressiontoFile(final Exp exp, final File file) throws IOException, CCodeGeneratorException{
		final CCodeGenerator gen = new CCodeGenerator(); 
		gen.compileExp(exp); 
		gen.writeIndividualLinesToFile(file); 
	}
	
	//Method to test statements
	public void writeStatementToFile(final Statement s, final File file) throws IOException, CCodeGeneratorException{
		final CCodeGenerator gen = new CCodeGenerator(); 
		gen.compileStatement(s);
		gen.writeIndividualLinesToFile(file);
	}
	
	//Method to test int main() 
	public void writeMainToFile(final Statement[] sArray, final File file) throws IOException, CCodeGeneratorException{
		final CCodeGenerator gen = new CCodeGenerator(); 
		for(Statement s: sArray) {
			gen.compileStatement(s); 
		}
		gen.writeMainToFile(file); 
	}
	
	
	// looks up variable from the rest of the class
		public Type lookupVariable(final String name) throws CCodeGeneratorException{
			// Checks if not in a class then checks statements outside classes for variable
			if (this.currentClass == null) {
				for (Statement s : this.statements) {
					if (s instanceof VariableDecExp && ((VariableDecExp) s).var.name.equals(name)) {
						return ((VariableDecExp) s).type;
					}
				}
				throw new CCodeGeneratorException("No variable found: " + name);
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
					throw new CCodeGeneratorException("Referring to unassigned variable " + name);
				} else {
					throw new CCodeGeneratorException("Referring to unassigned variable " + name);
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
					throw new CCodeGeneratorException("Referring to unassigned variable " + name);
				} else {
					throw new CCodeGeneratorException("Referring to unassigned variable " + name);
				}
			}
		}
}