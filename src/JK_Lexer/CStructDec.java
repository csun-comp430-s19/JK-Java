package JK_Lexer;

import java.util.ArrayList;

public class CStructDec implements CInstruction{
	final String name;
	final ArrayList<CVariableDec> vars;
	public CStructDec(String name, ArrayList<CVariableDec> vars) {
		this.name = name;
		this.vars = new ArrayList<CVariableDec>(vars);
	}
	public int hashCode() {
		return name.hashCode() + vars.hashCode();
	}
	public boolean equals(Object other) {
		if(other instanceof CStructDec) {
			CStructDec o = (CStructDec) other;
			return (o.name.equals(name) && o.vars.equals(vars));
		}else {
			return false;
		}
	}
	public String toString() {
		String result = "struct " + name + " { ";
		for(CVariableDec var : vars) {
			result += var.toString() + "; ";
		}
		result += "};";
		return result;
	}
}
