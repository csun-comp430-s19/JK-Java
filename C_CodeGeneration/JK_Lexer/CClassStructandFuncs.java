package JK_Lexer;

import java.util.ArrayList;

public class CClassStructandFuncs {
	public final CStructDec struct;
	public final ArrayList<CFunctionDec> functions;
	
	public CClassStructandFuncs(CStructDec struct, ArrayList<CFunctionDec> functions) {
		this.struct = struct;
		this.functions = functions;
	}
	
	public int hashcode() {
		return struct.hashCode() + functions.hashCode();
	}
	
	public boolean equals(Object other) {
		return (other instanceof CClassStructandFuncs) && 
				((CClassStructandFuncs)other).struct.equals(this.struct) && 
				((CClassStructandFuncs)other).functions.equals(this.functions);
	}
	
	
	public String toString() {
		String str = struct.toString() + "\n";
		for(CFunctionDec func : functions) {
			str += func.toString() + "\n";
		}
		return str;
	}
}
