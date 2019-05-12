package JK_Lexer;

import java.util.ArrayList;

public class CClassStructandFuncs {
	public final CStructDec struct;
	public final ArrayList<CFunctionDec> constructors;
	public final ArrayList<CFunctionDec> functions;
	
	public CClassStructandFuncs(CStructDec struct, ArrayList<CFunctionDec> constructors, ArrayList<CFunctionDec> functions) {
		this.struct = struct;
		this.constructors = constructors;
		this.functions = functions;
	}
	
	public int hashcode() {
		return struct.hashCode() + constructors.hashCode() + functions.hashCode();
	}
	
	public boolean equals(Object other) {
		return (other instanceof CClassStructandFuncs) && 
				((CClassStructandFuncs)other).struct.equals(this.struct) && 
				((CClassStructandFuncs)other).constructors.equals(this.constructors) &&
				((CClassStructandFuncs)other).functions.equals(this.functions);
	}
	
	
	public String toString() {
		String str = struct.toString() + "\n";
		for(CFunctionDec cons : constructors) {
			str += cons.toString() + "\n";
		}
		for(CFunctionDec func : functions) {
			str += func.toString() + "\n";
		}
		return str;
	}
}
