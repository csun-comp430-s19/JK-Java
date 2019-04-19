package JK_Lexer;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;


// Basic expressions covered by this deadline are: var, string, int, print, and binop. Other expressions like objects,
// method calls, and new class object declarations are not implemented yet because we are still planning the scope of this
// code generation class, and it would be easier to implement once we have a better understanding of that when we work on later deadlines


public class CCodeGenerator {
	private final List<CInstruction> instructions;
	
	//CCodeGenerator object will output an arraylist of c instructions
	public CCodeGenerator() {
		instructions = new ArrayList<CInstruction>();
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
		if(v.type instanceof StringType) {
			add(new CVariableDec(new CChar(),new CVariableExp(v.var.name)));
		}
		if(v.type instanceof VoidType) {
			add(new CVariableDec(new CVoid(), new CVariableExp(v.var.name)));
		}
		
		//ADD NEW CLASS OBJECT HERE, INCOMPLETE
		
		else {
			throw new CCodeGeneratorException("Invalid type:"+v.type.toString());
		}
	}
	//Compile assignment statement
	public void compileAssignment(AssignmentStmt a) throws CCodeGeneratorException {
		CVariableExp left = new CVariableExp(a.v.name);
		CExp right = convertExp(a.e); 
		add(new CAssignment(left, right));
	}
	//Compile return statement
	public void compileReturn(ReturnStmt r) {
		add(new CReturn(r.e));
	}
	//Compile statement function
	public void compileStatement(final Statement s) throws CCodeGeneratorException{
		if(s instanceof VariableDecExp) {
			compileVariableDec((VariableDecExp)s);
		}
		if(s instanceof AssignmentStmt) {
			compileAssignment((AssignmentStmt)s);
		}
		if(s instanceof ReturnStmt) {
			compileReturn((ReturnStmt)s);
		}
		else {
			throw new CCodeGeneratorException("Statement not found: "+s.toString());
		}
	}
	//Iterates through list to write instructions into then closes file
	public void writeCompleteFile(final File file) throws IOException{
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
	//Method to write an expression to a file: compileExp to fill list of Cinstructions, then to writeCompleteFile (FOR TESTING EXPS)
	public void writeExpressiontoFile(final Exp exp, final File file) throws IOException, CCodeGeneratorException{
		final CCodeGenerator gen = new CCodeGenerator(); 
		gen.compileExp(exp); 
		gen.writeCompleteFile(file); 
	}
	//Method to test statements
	public void writeStatementToFile(final Statement s, final File file) throws IOException, CCodeGeneratorException{
		final CCodeGenerator gen = new CCodeGenerator(); 
		gen.compileStatement(s);
		gen.writeCompleteFile(file);
	}
}