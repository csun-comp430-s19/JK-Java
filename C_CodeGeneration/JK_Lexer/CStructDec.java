package JK_Lexer;

import java.util.ArrayList;

public class CStructDec implements CInstruction{
	final String name;
	final ArrayList<CVariableDec> vars;
	final int functionPointerSize;
	public CStructDec(String name, ArrayList<CVariableDec> vars, int functionPointerSize) {
		this.name = name;
		this.vars = new ArrayList<CVariableDec>(vars);
		this.functionPointerSize = functionPointerSize;
	}
	public int hashCode() {
		return name.hashCode() + vars.hashCode() + functionPointerSize;
	}
	public boolean equals(Object other) {
		if(other instanceof CStructDec) {
			CStructDec o = (CStructDec) other;
			return (o.name.equals(name) && o.vars.equals(vars) && functionPointerSize == o.functionPointerSize);
		}else {
			return false;
		}
	}
	public String toString() {
		String result = "struct " + name + " { ";
		for(CVariableDec var : vars) {
			result += var.toString() + "; ";
		}
		result += "void* (*vtable[" + functionPointerSize + "]) ();";
		result += "};";
		return result;
	}
}
