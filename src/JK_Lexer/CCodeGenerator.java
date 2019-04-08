package JK_Lexer;

import java.util.ArrayList;

public class CCodeGenerator {
	private final ArrayList<CInstruction> instructions;
	
	public CCodeGenerator() {
		instructions = new ArrayList<CInstruction>();
	}
	
	public void add(final CInstruction i) {
		instructions.add(i);
	}
	
	public CBinOp compileBinOpExp(final BinopExp exp) {
		final CExp left = compileExp(exp.left);
		final CExp right = compileExp(exp.right);
		final CBinOp result = new CBinOp(left, exp.op, right);
		return result;
	}
	
	public CExp compileExp(final Exp exp) {
		if(exp instanceof BinopExp) {
			return compileBinOpExp((BinopExp)exp);
		}
//		else if(exp instanceof ) {
//		
//	}
		else {//Doesn't match anything
			return null;
		}
	}
	
	public CStringExp convertStringExp(final StringExp s) {
		return new CStringExp(s.fullstring);
	}
	
	public CNumberExp convertNumberExp(final NumberExp n) {
		return new CNumberExp(n.number);
	}
}
