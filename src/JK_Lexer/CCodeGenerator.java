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
	
	//Maps to use for tracking naming conventions of methods (e.g. getAge in Student will be named student_getAge)
	private Map<String, ClassDefExp> classes;
	private Map<String, Map<String, MethodDefExp>> methods;
	
	//CCodeGenerator object will output an arraylist of c instructions
	public CCodeGenerator() {
		this.instructions = new ArrayList<CInstruction>();
	}
	
	//For codegen with full programs, incomplete
	public CCodeGenerator(Program prog) {
		this.instructions = new ArrayList<CInstruction>(); 
		this.classes = new HashMap<String, ClassDefExp>();
		this.methods = new HashMap<String, Map<String, MethodDefExp>>();
		
		ArrayList<ClassDefExp> classList = prog.classDefList;
		for(ClassDefExp c: classList) {
			this.classes.put(c.name, c); 
			ArrayList<MethodDefExp> methodList = c.methods;
			Map<String, MethodDefExp> temp = new HashMap<String, MethodDefExp>();
			for(MethodDefExp m: methodList) {
				temp.put(m.name, m); 
				this.methods.put(c.name,temp); 
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
		VariableExp methodname = exp.methodname;
		ArrayList<VariableExp> parameters = exp.parameter;
		ArrayList<CVariableExp> cparams = new ArrayList<CVariableExp>();
		cparams.add((CVariableExp)convertExp(objname));
		for(VariableExp p: parameters) {
			cparams.add((CVariableExp)convertExp(p));
		}
		
		CFunctionCall result = new CFunctionCall(methodname.name, cparams);
		
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
}