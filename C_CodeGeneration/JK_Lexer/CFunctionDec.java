package JK_Lexer;

import java.util.ArrayList;

public class CFunctionDec {
	public final CType type;
	public final String name;
	public final ArrayList<CVariableDec> parameters;
	public final ArrayList<CStatement> block;
	
	public CFunctionDec(CType type, String name, ArrayList<CVariableDec> parameters, ArrayList<CStatement> block) {
		this.type = type;
		this.name = name;
		this.parameters = new ArrayList<CVariableDec>(parameters);
		this.block = new ArrayList<CStatement>(block);
	}
	
	public int hashCode() { return type.hashCode() + name.hashCode() + parameters.hashCode() + block.hashCode(); }
	public boolean equals(final Object other) {
		return (other instanceof CFunctionDec &&
				((CFunctionDec)other).type.equals(type) && ((CFunctionDec)other).name.equals(name)
				&&((CFunctionDec)other).parameters.equals(parameters) && ((CFunctionDec)other).block.equals(block)); 
	}
	 public String toString() {
	    String s = type + " " + name + "("; 
	    for(int i = 0; i < parameters.size(); i++) {
	    	if(i == parameters.size()-1) {
	    		s += parameters.get(i).toString();
	    	}
	    	else {
	    		s+= parameters.get(i).toString() + ", ";
	    	}
	    }
	    s+= "){";
	    for(CStatement stmt : block) {
	    	s+= stmt.toString() +"; ";
	    }
	    s+= "}";
	    return s;
	}
}
