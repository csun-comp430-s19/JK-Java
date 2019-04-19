package JK_Lexer;

import java.util.ArrayList;

public class CFunctionCall implements CExp{
	public final String name;
	public final ArrayList<CVariableExp> parameters;
	
	public CFunctionCall(String name, ArrayList<CVariableExp> parameters) {
		this.name = name;
		this.parameters = new ArrayList<CVariableExp>(parameters);
	}
	
	public int hashcode() {
		return name.hashCode() + parameters.hashCode();
	}
	
	public boolean equals(final Object other) {
		if(other instanceof CFunctionCall) {
			return ((CFunctionCall) other).name.equals(this.name) && ((CFunctionCall) other).parameters.equals(this.parameters);
		}else {
			return false;
		}
	}
	
	public String toString() {
		String s = name + "(";
		for(int i = 0 ; i < parameters.size(); i++) {
			if(i == parameters.size()-1) {
				s+= parameters.get(i).toString();
			}else {
				s+= parameters.get(i).toString() + ", ";
			}
		}
		s+= ");";
		
		return s;
	}
}
