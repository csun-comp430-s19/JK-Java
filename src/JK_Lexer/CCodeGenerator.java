package JK_Lexer;

import java.util.ArrayList;

public class CCodeGenerator {
	private final ArrayList<CInstruction> instructions;
	
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
		else {
			throw new CCodeGeneratorException("Basic expression not found: "+exp.toString());
		}
	}
}