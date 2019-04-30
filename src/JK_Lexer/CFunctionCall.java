package JK_Lexer;

import java.util.ArrayList;

public class CFunctionCall implements CExp{
	public final String classname;
	public final String name;
	public final ArrayList<CVariableExp> parameters;
	
	public CFunctionCall(String classname, String name, ArrayList<CVariableExp> parameters) {
		this.classname = classname;
		this.name = name;
		this.parameters = new ArrayList<CVariableExp>(parameters);
	}
	
	public int hashcode() {
		return classname.hashCode() + name.hashCode() + parameters.hashCode();
	}
	
	public boolean equals(final Object other) {
		if(other instanceof CFunctionCall) {
			return ((CFunctionCall) other).classname.equals(this.classname) && ((CFunctionCall) other).name.equals(this.name) && ((CFunctionCall) other).parameters.equals(this.parameters);
		}else {
			return false;
		}
	}
	
	public String toString() {
		String s = classname + "_" + name + "(";
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
