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
	
		private Program inputprogram;
		
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
		this.inputprogram = null;
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
		this.inputprogram = prog;
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
		add(new CVariableExp("user_"+exp.name, true));
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
			return new CVariableExp(((VariableExp)exp).name, true);
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
		else if(exp instanceof NewExp) {
			return convertNew((NewExp)exp);
		}
		else if(exp instanceof ThisExp) {
			return new CVariableExp("structptr->user_"+((ThisExp)exp).variable.toString(), false);
		}
		//Add this, method call, and new class when planned out
		
		else {
			throw new CCodeGeneratorException("Basic expression not found: "+exp.toString());
		}
	}
	
	//Compile variable declaration
	public void compileVariableDec(VariableDecExp v) throws CCodeGeneratorException {
		if(v.type instanceof IntType) {
			add(new CVariableDec(new Cint(),new CVariableExp(v.var.name, true)));
		}
		else if(v.type instanceof StringType) {
			add(new CVariableDec(new CChar(),new CVariableExp(v.var.name, true)));
		}
		else if(v.type instanceof VoidType) {
			add(new CVariableDec(new CVoid(), new CVariableExp(v.var.name, true)));
		}
		
		//ADD NEW CLASS OBJECT HERE, INCOMPLETE
		
		else {
			throw new CCodeGeneratorException("Invalid type:"+v.type.toString());
		}
	}
	
	//convert Program to CProgram
	public CProgram convertProgram(Program p) throws CCodeGeneratorException {
		ArrayList<ClassDefExp> classdef = p.classDefList;
		ArrayList<Statement> statements = p.statementList;
		
		ArrayList<CClassStructandFuncs> structandfuncs = new ArrayList<CClassStructandFuncs>();
		ArrayList<CStatement> mainstmt = new ArrayList<CStatement>();
		
		for(ClassDefExp c : classdef) {
			structandfuncs.add(convertClassDef(c));
		}
		for(Statement s : statements) {
			mainstmt.add(convertStatement(s));
		}
		
		String[] includes = {"include <stdio.h>", "include <stdlib.h>"};
		
		CProgram program = new CProgram(includes, structandfuncs, mainstmt);
		return program;
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
	
	public CFunctionDec convertMethodDefExp(MethodDefExp exp, String parentClass) throws CCodeGeneratorException{
		Type jk_type = exp.type;
		String methodname = exp.name;
		currentMethod = methodname;
		
		ArrayList<VariableDecExp> params = exp.parameters;
		ArrayList<Statement> block = exp.block;
		
		ArrayList<CVariableDec> cparams = new ArrayList<CVariableDec>();
		ArrayList<CStatement> cblock = new ArrayList<CStatement>();
		
		CType c_type = convertType(jk_type);
		
		cparams.add(new CVariableDec(new CStructType("*"+parentClass), new CVariableExp("structptr", false)));
		
		for(VariableDecExp p: params) {
			cparams.add(convertVariableDec(p));
		}
		
		for(Statement s : block) {
			cblock.add(convertStatement(s));
		}
		
		CFunctionDec result = new CFunctionDec(c_type, parentClass + "_" + methodname, cparams, cblock);
		return result;
	}
	
	public CFunctionDec convertConstructorDef(ConstructorDef c, String parentClass, int constructornumber) throws CCodeGeneratorException {
		currentConstructor = constructornumber;
		inConstructor = true;
		String constructorname = parentClass + "_constructor"+constructornumber;
		ClassDefExp parentClassDef = classes.get(parentClass);
		
		ArrayList<VariableDecExp> params = c.parameters;
		ArrayList<Statement> block = c.block;
		
		ArrayList<CVariableDec> cparams = new ArrayList<CVariableDec>();
		ArrayList<CStatement> cblock = new ArrayList<CStatement>();
		
		cparams.add(new CVariableDec(new CStructType("*"+parentClass), new CVariableExp("structptr", false)));
		
		for(VariableDecExp p: params) {
			cparams.add(convertVariableDec(p));
		}
		
		for(Statement s : block) {
			cblock.add(convertStatement(s));
		}
		cblock.add(new CReturn((CExp)cparams.get(0).var));
		CFunctionDec result = new CFunctionDec(new CStructType(parentClass+"*"), constructorname, cparams, cblock);
		
		return result;
		
	}
	
	public CClassStructandFuncs convertClassDef(ClassDefExp c) throws CCodeGeneratorException{
		currentClass = c.name;
		String classname = c.name;
		ArrayList<ConstructorDef> constructorlist = c.constructors;
		ArrayList<MethodDefExp> methods = c.methods;
		ArrayList<InstanceDecExp> varmembers = c.members;
		
		ArrayList<CVariableDec> structmembers = new ArrayList<CVariableDec>();
		if(c.extending) {
			CVariableDec basestruct = new CVariableDec(new CStructType(c.extendingClass), new CVariableExp("parent", false));
			structmembers.add(basestruct);
		}
		for(InstanceDecExp i : varmembers) {
			structmembers.add(convertInstanceDec(i));
		}
		
		CStructDec cstruct = new CStructDec(c.name, structmembers);
		ArrayList<CFunctionDec> functions = new ArrayList<CFunctionDec>();
		ArrayList<CFunctionDec> structconstructors = new ArrayList<CFunctionDec>();
		ArrayList<ConstructorDef> constructorindexlist = constructors.get(c.name);
		for(ConstructorDef constructor : constructorlist) {
			int num = constructorindexlist.indexOf(constructor);
			structconstructors.add(convertConstructorDef(constructor, c.name,num));
			inConstructor = false;
			num++;
		}		
		for(MethodDefExp m : methods) {
			functions.add(convertMethodDefExp(m, c.name));
		}
		
		CClassStructandFuncs result = new CClassStructandFuncs(cstruct, structconstructors, functions);
		
		return result;
	}
	
	public CNewStruct convertNew(NewExp exp) throws CCodeGeneratorException{
		String classname = ((VariableExp)(exp.classname)).name;
		ArrayList<CExp> tl = new ArrayList<CExp>(); 
		ArrayList<Type> typeList = new ArrayList<Type>(); 
		for(VariableExp v: exp.variable) {
			CExp cvar = convertExp(v); 
			Type vartype = typeofExp(v); 
			tl.add(cvar); 
			typeList.add(vartype); 
		}
		
		/*Exp var = exp.variable;
		
		CExp cvar = convertExp(var);
		Type vartype = typeofExp(var);
		ArrayList<CExp> tl = new ArrayList<CExp>();
		tl.add(cvar);*/
		
		ClassDefExp classdef = classes.get(classname);
		
		/*ArrayList<ConstructorDef> constructorl = constructors.get(classdef.name);
		ConstructorDef c = null;
		int i = 0;
		for(i = 0; i < constructorl.size(); i++) {
			ConstructorDef temp = constructorl.get(i);
			if(temp.parameters.size() == 1 && temp.parameters.get(0).type.equals(vartype)) {//Check for size 1 since newexp only hold 1 param
				c = temp;
				break;
			}
		}
		if(c == null) throw new CCodeGeneratorException("Constructor not found: " + exp.toString());*/
		
		ArrayList<ConstructorDef> constructorl = constructors.get(classdef.name);
		ConstructorDef c = null; 
		int i=0; 
		for(i=0; i<constructorl.size();i++) {
			ConstructorDef temp = constructorl.get(i); 
			ArrayList<Type> tempTList = new ArrayList<Type>(); 
			for(VariableDecExp v: temp.parameters) {
				tempTList.add(v.type);
			}
			if(tempTList.equals(typeList)) {
				c = temp; 
				break; 
			}
		}
		
		//Get Indexed CConstructor
		String cConstructorCall = classname + "_constructor" + i;
		
		CNewStruct cstruct = new CNewStruct(classdef, cConstructorCall, tl);
		return cstruct;
		
	}
	
	public CType convertType(Type t) throws CCodeGeneratorException{
		if(t instanceof IntType) {
			return new Cint();
		}
		else if(t instanceof StringType) {
			return new CChar();
		}
		else if(t instanceof VoidType) {
			return new CVoid();
		}
		else if(t instanceof ObjectType) {
			return new CStructType(((ObjectType)t).className);
		}
		else {
			throw new CCodeGeneratorException("Invalid type:"+t.toString());
		}
	}
	
	public CVariableDec convertInstanceDec(InstanceDecExp i) throws CCodeGeneratorException{
		return new CVariableDec(convertType(i.var.type), new CVariableExp(i.var.var.name, true));
	}
	
	public CStatement convertStatement(Statement s) throws CCodeGeneratorException{
		if(s instanceof VariableDecExp) {
			return convertVariableDec((VariableDecExp)s);
		}
		else if(s instanceof AssignmentStmt) {
			return convertAssignment((AssignmentStmt)s);
		}
		else if(s instanceof ReturnStmt) {
			return convertReturn((ReturnStmt)s);
		}
		else if(s instanceof IndependentMethodCallStmt) {
			return convertIndependentMethodCall((IndependentMethodCallStmt) s);
		}
		else {
			throw new CCodeGeneratorException("Statement not found: "+s.toString());
		}
	}
	
	public CVariableDec convertVariableDec(VariableDecExp v) throws CCodeGeneratorException{
		if(v.type instanceof IntType) {
			return new CVariableDec(new Cint(),new CVariableExp(v.var.name, true));
		}
		else if(v.type instanceof StringType) {
			return new CVariableDec(new CChar(),new CVariableExp(v.var.name, true));
		}
		else if(v.type instanceof VoidType) {
			return new CVariableDec(new CVoid(), new CVariableExp(v.var.name, true));
		}
		else if(v.type instanceof ObjectType) {
			return new CVariableDec(new CStructType(((ObjectType)v.type).className), new CVariableExp(v.var.name, true));
		}
		else {
			throw new CCodeGeneratorException("Invalid type:"+v.type.toString());
		}
	}
	
	public CAssignment convertAssignment(AssignmentStmt a) throws CCodeGeneratorException{
		CVariableExp left;
		if(a.leftIsThis) left = new CVariableExp("structptr->user_"+a.v.name, false);
		else left = new CVariableExp(a.v.name, true);
		CExp right = convertExp(a.e); 
		return new CAssignment(left, right);
	}
	
	public CReturn convertReturn(ReturnStmt r) throws CCodeGeneratorException{
		if(r.e == null) {
			return new CReturn();
		}else {
			CExp c = convertExp(r.e);
			return new CReturn(c);
		}
	}
	
	public CIndependentFunctionCall convertIndependentMethodCall(IndependentMethodCallStmt m) throws CCodeGeneratorException {
		CallMethodExp method = m.methodcall;
		CFunctionCall func = convertCallMethod(method);
		return new CIndependentFunctionCall(func);
	}
	
	//Compile assignment statement
	public void compileAssignment(AssignmentStmt a) throws CCodeGeneratorException {
		CVariableExp left = new CVariableExp(a.v.name, true);
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
	
	//Writes entire CProgram object to file
		public void writeCProgramToFile(final File file) throws IOException{
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
	
	public void writeProgramToFile(final Program p, final File file) throws IOException, CCodeGeneratorException{
		final CCodeGenerator gen = new CCodeGenerator(p);
		gen.generate(file);
	}
	
	public void generate(File file) throws CCodeGeneratorException, IOException {
		CProgram c = convertProgram(this.inputprogram);
		System.out.println(c.toString());
		final PrintWriter output= new PrintWriter(new BufferedWriter(new FileWriter(file)));
		try {
			output.println(c.toString());
		}finally {
			output.close(); 
		}
		
		
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
		
		// typeofExp takes in map of strings (variable names) and types as well as an
		// Exp e)
		public Type typeofExp(final Exp e) throws CCodeGeneratorException{ 
																		
			if (e instanceof NumberExp) {
				return new IntType();
			} else if (e instanceof StringExp) {
				return new StringType();
			} else if (e instanceof BinopExp) {
				return new IntType();
			} else if (e instanceof VariableExp) {
				String name = ((VariableExp) e).name;
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
				//String parameterName = ((VariableExp) ((CallMethodExp) e).parameter).name;
				if(currentClass==null) {
					MethodDefExp m = retrieveMethod(methodName);
					return m.type; 
				}
				MethodDefExp temp = this.methods.get(this.currentClass).get(methodName);
					return temp.type;
			} else if (e instanceof NewExp) {
				String classname = ((VariableExp) ((NewExp) e).classname).name;
					return new CustomType(classname);
			}else {
				throw new CCodeGeneratorException("Type not found: " + e.toString());
			}
		}
		
		public MethodDefExp retrieveMethod(String m) throws CCodeGeneratorException{
			for(ClassDefExp c: classes.values()) {
				for(MethodDefExp me: c.methods) {
					if(me.name.contentEquals(m)) {
						return me; 
					}
				}
			}
			throw new CCodeGeneratorException("Method not found: " +m);
			
		}
}