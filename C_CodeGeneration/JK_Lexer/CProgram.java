package JK_Lexer;

import java.util.ArrayList;

public class CProgram {
	public final String[] includes;
	public final ArrayList<CClassStructandFuncs> structandfuncs;
	public final ArrayList<CStatement> statement;
	
	public CProgram(String[] includes, ArrayList<CClassStructandFuncs> structsandfuncs, ArrayList<CStatement> statement) {
		this.includes = includes;
		this.structandfuncs = structsandfuncs;
		this.statement = statement;
	}
	
	public int hashcode() {
		return includes.hashCode() + structandfuncs.hashCode() + statement.hashCode();
	}
	
	public boolean equals(Object other) {
		return (other instanceof CProgram) && ((CProgram)other).includes.equals(includes) && ((CProgram)other).structandfuncs.equals(structandfuncs) && ((CProgram)other).statement.equals(statement);
	}
	
	public String toString() {
		String str = "";
		for (String include : includes) {
			str += include + "\n";
		}
		for(CClassStructandFuncs csf : structandfuncs) {
			str += csf.struct.toString() + "\n";
		}
		
		for(CClassStructandFuncs csf : structandfuncs) {
			for(CFunctionDec func : csf.functions) {
				str += func.toString() + "\n";
			}
		}
		
		for(CClassStructandFuncs csf : structandfuncs) {
			for(CFunctionDec cons : csf.constructors) {
				str += cons.toString() + "\n";
			}
		}
		
		
		str += "int main(){\n";
		for(CStatement s : statement) {
			str+= s.toString() + ";\n";
		}
		str += "return 0;\n}\n";
		
		return str;
	}
}
