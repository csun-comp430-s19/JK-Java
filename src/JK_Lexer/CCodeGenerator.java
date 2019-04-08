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
	
	public void compileBinOpExp(final BinopExp exp) {
		
	}
	
	
}
